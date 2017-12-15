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
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_RATING = "rating";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String POSTER_PATH_COLUMN = "poster_path";
        public static final String MOVIE_TITLE_COLUMN = "title";
        public static final String RELEASE_DATE_COLUMN = "release_date";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String VOTE_AVERAGE_COLUMN = "vote_average";
        public static final String POPULARITY_COLUMN = "popularity";
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
                        MOVIE_ID_COLUMN + " INTEGER UNIQUE ON CONFLICT REPLACE," +
                        MOVIE_TITLE_COLUMN + " TEXT," +
                        POSTER_PATH_COLUMN + " TEXT," +
                        RELEASE_DATE_COLUMN + " TEXT," +
                        OVERVIEW_COLUMN + " TEXT," +
                        VOTE_AVERAGE_COLUMN + " REAL" +
                        POPULARITY_COLUMN + " REAL)";

        public static final String DELETE_MOVIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class RatingMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "rating_movie";
        public static final String MOVIE_ID_KEY_COLUMN = "movie_id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATING;

        public static final String CREATE_RATING =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        MOVIE_ID_KEY_COLUMN + " INTEGER UNIQUE ON CONFLICT REPLACE," +
                        " FOREIGN KEY (" + MOVIE_ID_KEY_COLUMN + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "))";

        public static final String DELETE_RATING =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildRatingMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class PopularMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "popular_movie";
        public static final String MOVIE_ID_KEY_COLUMN = "movie_id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String CREATE_POPULAR =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        MOVIE_ID_KEY_COLUMN + " INTEGER UNIQUE ON CONFLICT REPLACE," +
                        " FOREIGN KEY (" + MOVIE_ID_KEY_COLUMN + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "))";

        public static final String DELETE_POPULAR =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildPopularMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movie";
        public static final String MOVIE_ID_KEY_COLUMN = "movie_id";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String CREATE_FAVORITES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        MOVIE_ID_KEY_COLUMN + " INTEGER UNIQUE ON CONFLICT REPLACE," +
                        " FOREIGN KEY (" + MOVIE_ID_KEY_COLUMN + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "))";

        public static final String DELETE_FAVORITES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildFavoriteMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

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
                        "UNIQUE(" + VIDEO_ID_COLUMN + ", " + KEY_COLUMN + ", " + NAME_COLUMN + ") " +
                        "ON CONFLICT REPLACE)";

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

    public static class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";
        public static final String MOVIE_ID_KEY_COLUMN = "movie_id";
        public static final String REVIEW_ID_COLUMN = "id";
        public static final String AUTHOR_COLUMN = "author";
        public static final String CONTENT_COLUMN = "content";
        public static final String URL_COLUMN = "url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CREATE_REVIEWS =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY," +
                        REVIEW_ID_COLUMN + " INTEGER UNIQUE ON CONFLICT REPLACE," +
                        AUTHOR_COLUMN + " TEXT," +
                        CONTENT_COLUMN + " TEXT," +
                        URL_COLUMN + " TEXT, " +
                        MOVIE_ID_KEY_COLUMN + " INTEGER, " +
                        " FOREIGN KEY (" + MOVIE_ID_KEY_COLUMN + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "))";

        public static final String DELETE_REVIEWS =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewUriWithMovieId(long id) {
            return MovieEntry.CONTENT_URI.buildUpon()
                    .appendEncodedPath(String.valueOf(id))
                    .appendEncodedPath(PATH_REVIEW).build();
        }

    }
}
