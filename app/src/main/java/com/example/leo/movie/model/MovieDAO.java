package com.example.leo.movie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.leo.movie.R;
import com.example.leo.movie.database.MovieContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 16/12/2017.
 */

public class MovieDAO extends DAO {
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_ID_COLUMN,
            MovieContract.MovieEntry.MOVIE_TITLE_COLUMN,
            MovieContract.MovieEntry.POSTER_PATH_COLUMN,
            MovieContract.MovieEntry.RELEASE_DATE_COLUMN,
            MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN,
            MovieContract.MovieEntry.OVERVIEW_COLUMN,
            MovieContract.MovieEntry.POPULARITY_COLUMN
    };

    private static final int COL_MOVIE_ENTRY_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_POSTER_PATH = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_VOTE_AVERAGE = 5;
    private static final int COL_MOVIE_OVERVIEW = 6;
    private static final int COL_MOVIE_POPULARITY = 7;

    private static final String[] FAVORITE_MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN
    };

    private static final int COL_FAVORITE_MOVIE_ENTRY_ID = 0;
    private static final int COL_FAVORITE_MOVIE_ID = 1;

    public MovieDAO(Context context) {
        super(context);
    }

    public static List<Movie> getMovies(Cursor movieCursor) {
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
                value.put(MOVIE_COLUMNS[COL_MOVIE_ID], movie.id);
                value.put(MOVIE_COLUMNS[COL_MOVIE_TITLE], movie.title);
                value.put(MOVIE_COLUMNS[COL_MOVIE_POSTER_PATH], movie.poster_path);
                value.put(MOVIE_COLUMNS[COL_MOVIE_RELEASE_DATE], SimpleDateFormat.getDateInstance().format(movie.release_date));
                value.put(MOVIE_COLUMNS[COL_MOVIE_VOTE_AVERAGE], movie.vote_average);
                value.put(MOVIE_COLUMNS[COL_MOVIE_OVERVIEW], movie.overview);
                value.put(MOVIE_COLUMNS[COL_MOVIE_POPULARITY], movie.popularity);

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

    public Movie getMovie(long movieId) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                MovieContract.MovieEntry.MOVIE_ID_COLUMN + " = ?",
                new String[]{String.valueOf(movieId)},
                null
        );

        List<Movie> movies = getMovies(cursor);

        if (cursor != null) {
            cursor.close();
        }

        if (movies.size() == 0) {
            return null;
        } else {
            return movies.get(0);
        }
    }

    public boolean isFavorite(long movieId) {
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                FAVORITE_MOVIE_COLUMNS[COL_FAVORITE_MOVIE_ID] + " = ?",
                new String[]{String.valueOf(movieId)}, null);

        boolean isFavorite = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        return isFavorite;
    }

    public void setFavorite(long movieId) {
        ContentValues value = new ContentValues();
        value.put(FAVORITE_MOVIE_COLUMNS[COL_FAVORITE_MOVIE_ID], movieId);

        mContext.getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, value);
    }

    public void removeFavorite(long movieId) {
        mContext.getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                FAVORITE_MOVIE_COLUMNS[COL_FAVORITE_MOVIE_ID] + " = ?",
                new String[]{String.valueOf(movieId)});
    }
}
