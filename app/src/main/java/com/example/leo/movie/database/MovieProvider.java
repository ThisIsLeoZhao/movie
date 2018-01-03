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
    private static final int MOVIE = 100;
    private static final int MOVIE_VIDEOS = 101;
    private static final int MOVIE_REVIEWS = 102;
    private static final int FAVORITE_MOVIE = 110;
    private static final int POPULAR_MOVIE = 120;
    private static final int RATING_MOVIE = 130;
    private static final int VIDEOS = 200;
    private static final int REVIEWS = 300;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieHelper mDBHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_RATING, RATING_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_VIDEO, MOVIE_VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_REVIEW, MOVIE_REVIEWS);

        return matcher;
    }

    private Cursor getVideosByMovieId(Uri uri, String[] projection, String sortOrder) {
        final String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN
                + " = ?";
        String[] selectionArgs = new String[]{movieId};

        final SQLiteQueryBuilder videoByMovieQueryBuilder = new SQLiteQueryBuilder();

        //movie INNER JOIN videos ON movie.id = videos.movie_id
        videoByMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.VideoEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN +
                        " = " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.MOVIE_ID_KEY_COLUMN);

        return videoByMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {
        final String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN
                + " = ?";
        String[] selectionArgs = new String[]{movieId};

        final SQLiteQueryBuilder reviewByMovieQueryBuilder = new SQLiteQueryBuilder();

        //movie INNER JOIN reviews ON movie.id = reviews.movie_id
        reviewByMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.MOVIE_ID_KEY_COLUMN);

        return reviewByMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor queryFavoriteMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder favoriteMovieQueryBuilder = new SQLiteQueryBuilder();

        favoriteMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteMovieEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN +
                        " = " + MovieContract.FavoriteMovieEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN);

        return favoriteMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor queryPopularMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder popularMovieQueryBuilder = new SQLiteQueryBuilder();

        popularMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.PopularMovieEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN +
                        " = " + MovieContract.PopularMovieEntry.TABLE_NAME +
                        "." + MovieContract.PopularMovieEntry.MOVIE_ID_KEY_COLUMN);

        return popularMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor queryRatingMovies(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder ratingMovieQueryBuilder = new SQLiteQueryBuilder();

        ratingMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.RatingMovieEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.MOVIE_ID_COLUMN +
                        " = " + MovieContract.RatingMovieEntry.TABLE_NAME +
                        "." + MovieContract.RatingMovieEntry.MOVIE_ID_KEY_COLUMN);

        return ratingMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
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
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VIDEOS:
                cursor = mDBHelper.getReadableDatabase().query(MovieContract.VideoEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case REVIEWS:
                cursor = mDBHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_MOVIE:
                cursor = queryFavoriteMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case POPULAR_MOVIE:
                cursor = queryPopularMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case RATING_MOVIE:
                cursor = queryRatingMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_VIDEOS:
                cursor = getVideosByMovieId(uri, projection, sortOrder);
                break;
            case MOVIE_REVIEWS:
                cursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            default:
                return null;
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
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE:
                return MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            case POPULAR_MOVIE:
                return MovieContract.PopularMovieEntry.CONTENT_TYPE;
            case RATING_MOVIE:
                return MovieContract.RatingMovieEntry.CONTENT_TYPE;
            case MOVIE_VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
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
            case FAVORITE_MOVIE:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.FavoriteMovieEntry.buildFavoriteMovieUri(id);
                }

                break;
            case POPULAR_MOVIE:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.PopularMovieEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.PopularMovieEntry.buildPopularMovieUri(id);
                }

                break;
            case RATING_MOVIE:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.RatingMovieEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.RatingMovieEntry.buildRatingMovieUri(id);
                }

                break;
            case VIDEOS:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.VideoEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.VideoEntry.buildVideoUri(id);
                }

                break;
            case REVIEWS:
                id = mDBHelper.getWritableDatabase().insert(MovieContract.ReviewEntry.TABLE_NAME,
                        null, values);
                if (id != -1) {
                    insertedUri = MovieContract.ReviewEntry.buildReviewUri(id);
                }
                break;
            default:
                insertedUri = null;
                break;
        }
        if (insertedUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return insertedUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numOfAffectedRows;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                numOfAffectedRows = bulkInsertInfo(MovieContract.MovieEntry.TABLE_NAME, values);
                break;
            case FAVORITE_MOVIE:
                numOfAffectedRows = bulkInsertInfo(MovieContract.FavoriteMovieEntry.TABLE_NAME, values);
                break;
            case POPULAR_MOVIE:
                numOfAffectedRows = bulkInsertInfo(MovieContract.PopularMovieEntry.TABLE_NAME, values);
                break;
            case RATING_MOVIE:
                numOfAffectedRows = bulkInsertInfo(MovieContract.RatingMovieEntry.TABLE_NAME, values);
                break;
            case VIDEOS:
                numOfAffectedRows = bulkInsertInfo(MovieContract.VideoEntry.TABLE_NAME, values);
                break;
            case REVIEWS:
                numOfAffectedRows = bulkInsertInfo(MovieContract.ReviewEntry.TABLE_NAME, values);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        if (numOfAffectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
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
            case FAVORITE_MOVIE:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR_MOVIE:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.PopularMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RATING_MOVIE:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.RatingMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                numOfAffectedRows = mDBHelper.getWritableDatabase()
                        .delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        if (numOfAffectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

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
            case REVIEWS:
                numOfAffectedRows = mDBHelper.getWritableDatabase().update(MovieContract.ReviewEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                numOfAffectedRows = 0;
                break;
        }

        if (numOfAffectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

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
}
