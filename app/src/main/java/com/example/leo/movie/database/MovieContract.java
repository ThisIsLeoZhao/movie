package com.example.leo.movie.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leo on 02/01/2017.
 */

public class MovieContract {
    private MovieContract() {}

    public static final String DATABASE_NAME = "my_movie.db";

    public static final String CONTENT_AUTHORITY = "com.example.leo.movie.provider.movieprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String POSTER_PATH_COLUMN = "poster_path";
        public static final String MOVIE_TITLE_COLUMN = "title";
        public static final String RELEASE_DATE_COLUMN = "release_date";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String VOTE_AVERAGE_COLUMN = "vote_average";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CREATE_MOVIES =
                "CREATE TABLE " + TABLE_NAME +
                " (" + _ID + " INTEGER PRIMARY KEY," +
                MOVIE_TITLE_COLUMN + " TEXT," +
                POSTER_PATH_COLUMN + " TEXT," +
                RELEASE_DATE_COLUMN + " TEXT," +
                OVERVIEW_COLUMN + " TEXT," +
                VOTE_AVERAGE_COLUMN + " REAL)";

        public static final String DELETE_MOVIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

    }
}
