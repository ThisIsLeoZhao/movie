package com.example.leo.movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.model.Movie;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Leo on 16/12/2017.
 */

public class MovieStore {
    private Context mContext;

    private static final int COL_MOVIE_ID = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_POSTER_PATH = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_VOTE_AVERAGE = 5;
    private static final int COL_MOVIE_OVERVIEW = 6;
    private static final int COL_MOVIE_POPULARITY = 7;

    public MovieStore(Context context) {
        mContext = context;
    }

    public void insertMovies(List<Movie> movies) {
        new Thread(() -> {
            final boolean sortByRatings = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getString(mContext.getString(R.string.key_pref_sort_order),
                            mContext.getString(R.string.pref_sort_by_popularity))
                    .equals(mContext.getString(R.string.pref_sort_by_ratings));

            ContentValues[] values = new ContentValues[movies.size()];
            ContentValues[] movieIds = new ContentValues[movies.size()];
            for (int i = 0; i < movies.size(); i++) {
                Movie movie = movies.get(i);

                ContentValues value = new ContentValues();
                value.put(MovieContract.MovieEntry.MOVIE_ID_COLUMN, movie.id);
                value.put(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN, movie.title);
                value.put(MovieContract.MovieEntry.POSTER_PATH_COLUMN, movie.poster_path);
                value.put(MovieContract.MovieEntry.RELEASE_DATE_COLUMN, SimpleDateFormat.getDateInstance().format(movie.release_date));
                value.put(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN, movie.vote_average);
                value.put(MovieContract.MovieEntry.OVERVIEW_COLUMN, movie.overview);
                value.put(MovieContract.MovieEntry.POPULARITY_COLUMN, movie.popularity);

                values[i] = value;

                value = new ContentValues();
                if (sortByRatings) {
                    value.put(MovieContract.RatingMovieEntry.MOVIE_ID_KEY_COLUMN, movie.id);
                } else {
                    value.put(MovieContract.PopularMovieEntry.MOVIE_ID_KEY_COLUMN, movie.id);
                }
                movieIds[i] = value;
            }

            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
            if (sortByRatings) {
                mContext.getContentResolver().bulkInsert(MovieContract.RatingMovieEntry.CONTENT_URI, movieIds);
            } else {
                mContext.getContentResolver().bulkInsert(MovieContract.PopularMovieEntry.CONTENT_URI, movieIds);
            }

        }).start();
    }

    public void insertFavorites() {

    }

    public static List<Movie> getMoviesFromCursor(Cursor movieCursor) {
        List<Movie> movies = new ArrayList<>();

        if (movieCursor != null && movieCursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.id = movieCursor.getLong(COL_MOVIE_ID);
                movie.title = movieCursor.getString(COL_MOVIE_TITLE);
                movie.poster_path = movieCursor.getString(COL_MOVIE_POSTER_PATH);
                movie.vote_average = movieCursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
                movie.overview = movieCursor.getString(COL_MOVIE_OVERVIEW);
                movie.popularity = movieCursor.getDouble(COL_MOVIE_POPULARITY);

                try {
                    movie.release_date = SimpleDateFormat.getDateInstance()
                            .parse(movieCursor.getString(COL_MOVIE_RELEASE_DATE));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                movies.add(movie);
            } while (movieCursor.moveToNext());
        }

        return movies;
    }
}
