package com.example.leo.movie.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.movie.BuildConfig;
import com.example.leo.movie.IDownloadListener;
import com.example.leo.movie.IFetchMovieListener;
import com.example.leo.movie.R;
import com.example.leo.movie.model.Movie;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public static void fetchMoreMovie(Context context, IFetchMovieListener fetchMovieListener) {
        SortOrder sortOrder = getSortOrderPref(context);
        int latestPage = getLatestPage(context, sortOrder);

        fetchMovieList(context, sortOrder, latestPage + 1, fetchMovieListener);
    }

    public static void fetchExistedMovie(Context context, IFetchMovieListener fetchMovieListener) {
        SortOrder sortOrder = getSortOrderPref(context);
        int latestPage = getLatestPage(context, sortOrder);

        // TODO: Fetch all existed page
        fetchMovieList(context, sortOrder, 1, fetchMovieListener);
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

    private static void fetchMovieList(final Context context, final SortOrder sortOrder, final int page, IFetchMovieListener fetchMovieListener) {
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
                        fetchMovieListener.onFailure("Failed to download movies");
                        return;
                    }
                    updateLatestPage(context, sortOrder, page);
                    fetchMovieListener.onDone(parseMovies(response));
                }

                @Override
                public void onFailure(String reason) {
                    fetchMovieListener.onFailure(reason);
                }
            });
        } catch (MalformedURLException e) {
            Log.e(MovieDownloader.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
            fetchMovieListener.onFailure("Invalid URL");
        }
    }

    public static List<Movie> parseMovies(String movies) {
        Gson gson = new Gson();

        try {
            return Arrays.asList(gson.fromJson(new JSONObject(movies).getJSONArray("results").toString(),
                    Movie[].class));
        } catch (JSONException e) {
            Log.e(URLDownloader.class.getSimpleName(), e.getMessage());
            return Collections.emptyList();
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
