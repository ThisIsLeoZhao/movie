package com.example.leo.movie.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leo on 02/01/2017.
 */

public class MovieContract {
    private MovieContract() {
    }

    public static final String DATABASE_NAME = "my_movie.db";

    public static final String CONTENT_AUTHORITY = "com.example.leo.movie.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";

    public static class VideoEntry implements BaseColumns {
        public static final String TABLE_NAME = "video";
        public static final String MOVIE_ID_KEY_COLUMN = "movie_id";
        public static final String VIDEO_ID_COLUMN = "id";
        public static final String KEY_COLUMN = "key";
        public static final String NAME_COLUMN = "name";
        public static final String TYPE_COLUMN = "type";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String CREATE_VIDEOS =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        VIDEO_ID_COLUMN + " INTEGER," +
                        KEY_COLUMN + " TEXT," +
                        NAME_COLUMN + " TEXT," +
                        TYPE_COLUMN + " TEXT, " +
                        MOVIE_ID_KEY_COLUMN + " INTEGER, " +
                        " FOREIGN KEY (" + MOVIE_ID_KEY_COLUMN + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +
                        "UNIQUE(" + VIDEO_ID_COLUMN + ", " + KEY_COLUMN + ", " + NAME_COLUMN + "))";

        public static final String DELETE_VIDEOS =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideoUriWithMovieId(long id) {
            return MovieEntry.CONTENT_URI.buildUpon()
                    .appendEncodedPath(String.valueOf(id))
                    .appendEncodedPath(PATH_VIDEO).build();
        }
    }

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String POSTER_PATH_COLUMN = "poster_path";
        public static final String MOVIE_TITLE_COLUMN = "title";
        public static final String RELEASE_DATE_COLUMN = "release_date";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String VOTE_AVERAGE_COLUMN = "vote_average";
        public static final String MOVIE_ID_COLUMN = "id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CREATE_MOVIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        MOVIE_ID_COLUMN + " INTEGER," +
                        MOVIE_TITLE_COLUMN + " TEXT," +
                        POSTER_PATH_COLUMN + " TEXT," +
                        RELEASE_DATE_COLUMN + " TEXT," +
                        OVERVIEW_COLUMN + " TEXT," +
                        VOTE_AVERAGE_COLUMN + " REAL)";

        public static final String DELETE_MOVIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
