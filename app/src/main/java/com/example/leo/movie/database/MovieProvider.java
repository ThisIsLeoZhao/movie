package com.example.leo.movie.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leo on 07/01/2017.
 */

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int MOVIE = 100;
    private static final int MOVIE_VIDEOS = 101;

    private static final int VIDEOS = 200;

    private static final SQLiteQueryBuilder sVideoByMovieQueryBuilder;

    static {
        sVideoByMovieQueryBuilder = new SQLiteQueryBuilder();

        //movie INNER JOIN videos ON movie._id = videos.movie_id
        sVideoByMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.VideoEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID +
                        " = " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.MOVIE_ID_KEY_COLUMN);
    }

    private MovieHelper mDBHelper;

    private Cursor getVideosByMovieId(Uri uri, String[] projection, String sortOrder) {
        final String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID
                + " = ? ";
        String[] selectionArgs = new String[] { movieId };

        return sVideoByMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new MovieHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = mDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case VIDEOS:
                cursor = mDBHelper.getReadableDatabase().query(MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case MOVIE_VIDEOS:
                cursor = getVideosByMovieId(uri, projection, sortOrder);
                break;
            default:
                cursor = null;
                break;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                long id = mDBHelper.getWritableDatabase().insert(MovieContract.MovieEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.MovieEntry.buildMovieUri(id);
                }

                break;
            case VIDEOS:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.VideoEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.VideoEntry.buildVideoUri(id);
                }

                break;
            default:
                insertedUri = null;
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numOfAffectedRows;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numOfAffectedRows = bulkInsertInfo(MovieContract.MovieEntry.TABLE_NAME, values);
                break;
            case VIDEOS:
                numOfAffectedRows = bulkInsertInfo(MovieContract.VideoEntry.TABLE_NAME, values);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numOfAffectedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numOfAffectedRows;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numOfAffectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int numOfAffectedRows;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numOfAffectedRows = mDBHelper.getWritableDatabase().update(MovieContract.MovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case VIDEOS:
                numOfAffectedRows = mDBHelper.getWritableDatabase().update(MovieContract.VideoEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numOfAffectedRows;
    }

    private int bulkInsertInfo(String tableName, ContentValues[] values) {
        int numOfLinesAffected = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(tableName, null, value);
                if (id != -1) {
                    numOfLinesAffected++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return numOfLinesAffected;

    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#" + MovieContract.PATH_VIDEO, MOVIE_VIDEOS);

        return matcher;
    }
}
