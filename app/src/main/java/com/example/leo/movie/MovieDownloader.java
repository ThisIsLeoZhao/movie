package com.example.leo.movie;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leo on 16/12/2017.
 */

public class MovieDownloader {
    private static final String BASE = "https://api.themoviedb.org/3/";
    private static final String MOVIE_TYPE_PATH = "movie";
    private static final String API_KEY_PARAMS = "api_key";
    private static final String PAGE_PARAMS = "page";
    private MovieDownloader() {
    }

    public static void fetchMoreMovie(Context context, IDownloadListener downloadListener) {
        SortOrder sortOrder = getSortOrderPref(context);
        int latestPage = getLatestPage(context, sortOrder);

        fetchMovieList(context, sortOrder, latestPage + 1, downloadListener);
    }

    public static void fetchExistedMovie(Context context, IDownloadListener downloadListener) {
        SortOrder sortOrder = getSortOrderPref(context);
        int latestPage = getLatestPage(context, sortOrder);

        // TODO: Fetch all existed page
        fetchMovieList(context, sortOrder, 1, downloadListener);
    }

    private static int getLatestPage(Context context, SortOrder sortOrder) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String latestPageKey = context.getString(R.string.key_pref_popular_latest_page);

        if (sortOrder.equals(SortOrder.TOP_RATED)) {
            latestPageKey = context.getString(R.string.key_pref_top_rated_latest_page);
        }

        return prefs.getInt(latestPageKey, 0);
    }

    private static void updateLatestPage(Context context, SortOrder sortOrder, int page) {
        if (page > getLatestPage(context, sortOrder)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = prefs.edit();

            String latestPageKey = context.getString(R.string.key_pref_popular_latest_page);

            if (sortOrder.equals(SortOrder.TOP_RATED)) {
                latestPageKey = context.getString(R.string.key_pref_top_rated_latest_page);
            }

            prefsEditor.putInt(latestPageKey, page).apply();
        }
    }

    private static SortOrder getSortOrderPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SortOrder sortOrder = SortOrder.POPULAR;

        if (prefs.getString(context.getString(R.string.key_pref_sort_order), context.getString(R.string.pref_sort_by_popularity))
                .equals(context.getString(R.string.pref_sort_by_ratings))) {
            sortOrder = SortOrder.TOP_RATED;
        }

        return sortOrder;
    }

    private static void fetchMovieList(final Context context, final SortOrder sortOrder, final int page, final IDownloadListener downloadListener) {
        Uri uri = Uri.parse(BASE).buildUpon()
                .appendEncodedPath(MOVIE_TYPE_PATH)
                .appendEncodedPath(sortOrder.toString())
                .appendQueryParameter(PAGE_PARAMS, String.valueOf(page))
                .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build();

        try {
            URL url = new URL(uri.toString());
            URLDownloader.downloadURL(url, new IDownloadListener() {
                @Override
                public void onDone(String response) {
                    if (response == null) {
                        downloadListener.onFailure("Failed to download movies");
                        return;
                    }
                    updateLatestPage(context, sortOrder, page);
                    downloadListener.onDone(response);
                }

                @Override
                public void onFailure(String reason) {
                    downloadListener.onFailure(reason);
                }
            });
        } catch (MalformedURLException e) {
            Log.e(MovieDownloader.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
            downloadListener.onFailure("Invalid URL");
        }
    }

    public enum SortOrder {
        POPULAR("popular"),
        TOP_RATED("top_rated");

        private String mSortOrder;

        SortOrder(String sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public String toString() {
            return mSortOrder;
        }
    }
}
