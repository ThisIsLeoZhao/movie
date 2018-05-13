package com.example.leo.movie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.leo.movie.R;
import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.model.generated.Movie;

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
                movie.posterPath = movieCursor.getString(COL_MOVIE_POSTER_PATH);
                movie.voteAverage = movieCursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
                movie.overview = movieCursor.getString(COL_MOVIE_OVERVIEW);
                movie.popularity = movieCursor.getDouble(COL_MOVIE_POPULARITY);
                movie.releaseDate = movieCursor.getString(COL_MOVIE_RELEASE_DATE);

                movies.add(movie);
            } while (movieCursor.moveToNext());
        }

        return movies;
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

    public void setFavorites(List<Long> movieIds) {
        ContentValues[] values = new ContentValues[movieIds.size()];

        for (int i = 0; i < movieIds.size(); i++) {
            ContentValues value = new ContentValues();
            value.put(FAVORITE_MOVIE_COLUMNS[COL_FAVORITE_MOVIE_ID], movieIds.get(i));

            values[i] = value;
        }

        mContext.getContentResolver().bulkInsert(MovieContract.FavoriteMovieEntry.CONTENT_URI, values);
    }

    public void removeFavorite(long movieId) {
        mContext.getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                FAVORITE_MOVIE_COLUMNS[COL_FAVORITE_MOVIE_ID] + " = ?",
                new String[]{String.valueOf(movieId)});
    }

    public void clearFavorite() {
        mContext.getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null, null);
    }
}
