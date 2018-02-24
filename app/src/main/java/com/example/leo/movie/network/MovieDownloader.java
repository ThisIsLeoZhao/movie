package com.example.leo.movie.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.leo.movie.IFetchMovieListener;
import com.example.leo.movie.R;
import com.example.leo.movie.model.MovieResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.leo.movie.network.URLBuilder.SortOrder;

/**
 * Created by Leo on 16/12/2017.
 */

public class MovieDownloader {
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
        Call<MovieResult> call = MovieClient.obtain().getMovies(sortOrder.toString(), page);
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieResult> call, @NonNull Response<MovieResult> response) {
                updateLatestPage(context, sortOrder, page);
                fetchMovieListener.onDone(response.body().results);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResult> call, @NonNull Throwable t) {
                fetchMovieListener.onFailure(t.getMessage());

            }
        });

    }
}
