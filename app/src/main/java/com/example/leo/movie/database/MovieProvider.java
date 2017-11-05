package com.example.leo.movie.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leo on 07/01/2017.
 */

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MOVIE = 1;

    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
    }

    private MovieHelper mDBHelper;

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
                cursor = queryMovieInfo(projection, selection, selectionArgs, sortOrder);
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
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                insertedUri = insertMovieInfo(values);
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
                numOfAffectedRows = bulkInsertMovieInfo(values);
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
                numOfAffectedRows = deleteMovieInfo(selection, selectionArgs);
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
                numOfAffectedRows = updateMovieInfo(values, selection, selectionArgs);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numOfAffectedRows;
    }

    private Cursor queryMovieInfo(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    private Uri insertMovieInfo(ContentValues value) {
        long id = mDBHelper.getWritableDatabase().insert(MovieContract.MovieEntry.TABLE_NAME,
                null, value);

        return MovieContract.MovieEntry.buildMovieUri(id);
    }

    private int bulkInsertMovieInfo(ContentValues[] values) {
        int numOfLinesAffected = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                if (id != -1) {
                    numOfLinesAffected++;
                }
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        return numOfLinesAffected;

    }

    private int deleteMovieInfo(String selection, String[] selectionArgs) {
        return mDBHelper.getWritableDatabase().delete(MovieContract.MovieEntry.TABLE_NAME,
                selection, selectionArgs);
    }

    private int updateMovieInfo(ContentValues values, String selection, String[] selectionArgs) {
        return mDBHelper.getWritableDatabase().update(
                MovieContract.MovieEntry.TABLE_NAME,
                values,
                selection, selectionArgs);
    }
}
