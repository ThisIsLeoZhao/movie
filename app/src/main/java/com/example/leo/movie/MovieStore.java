package com.example.leo.movie;

import android.content.ContentValues;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.movie.database.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leo on 16/12/2017.
 */

public class MovieStore {
    private Context mContext;

    public MovieStore(Context context) {
        mContext = context;
    }

    public void insertMovies(JSONArray movies) {
        final boolean sortByRatings = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(mContext.getString(R.string.key_pref_sort_order),
                        mContext.getString(R.string.pref_sort_by_popularity))
                .equals(mContext.getString(R.string.pref_sort_by_ratings));

        try {
            ContentValues[] values = new ContentValues[movies.length()];
            ContentValues[] movieIds = new ContentValues[movies.length()];
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);

                ContentValues value = new ContentValues();
                value.put(MovieContract.MovieEntry.MOVIE_ID_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                value.put(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN));
                value.put(MovieContract.MovieEntry.POSTER_PATH_COLUMN, movie.getString(MovieContract.MovieEntry.POSTER_PATH_COLUMN));
                value.put(MovieContract.MovieEntry.RELEASE_DATE_COLUMN, movie.getString(MovieContract.MovieEntry.RELEASE_DATE_COLUMN));
                value.put(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN, movie.getString(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN));
                value.put(MovieContract.MovieEntry.OVERVIEW_COLUMN, movie.getString(MovieContract.MovieEntry.OVERVIEW_COLUMN));
                value.put(MovieContract.MovieEntry.POPULARITY_COLUMN, movie.getString(MovieContract.MovieEntry.POPULARITY_COLUMN));

                values[i] = value;

                value = new ContentValues();
                if (sortByRatings) {
                    value.put(MovieContract.RatingMovieEntry.MOVIE_ID_KEY_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                } else {
                    value.put(MovieContract.PopularMovieEntry.MOVIE_ID_KEY_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                }
                movieIds[i] = value;
            }

            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
            if (sortByRatings) {
                mContext.getContentResolver().bulkInsert(MovieContract.RatingMovieEntry.CONTENT_URI, movieIds);
            } else {
                mContext.getContentResolver().bulkInsert(MovieContract.PopularMovieEntry.CONTENT_URI, movieIds);
            }

        } catch (JSONException e) {
            Log.e(MovieStore.class.getSimpleName(), e.getMessage());
        }
    }

    public void insertFavorites() {

    }
}
