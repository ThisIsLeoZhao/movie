package com.example.leo.movie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leo on 02/01/2017.
 */

public class MovieHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 3;

    public MovieHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContract.MovieEntry.CREATE_MOVIES);
        db.execSQL(MovieContract.FavoriteMovieEntry.CREATE_FAVORITES);
        db.execSQL(MovieContract.PopularMovieEntry.CREATE_POPULAR);
        db.execSQL(MovieContract.RatingMovieEntry.CREATE_RATING);
        db.execSQL(MovieContract.VideoEntry.CREATE_VIDEOS);
        db.execSQL(MovieContract.ReviewEntry.CREATE_REVIEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cache upgrade just need to discard the old data and start over
        db.execSQL(MovieContract.MovieEntry.DELETE_MOVIES);
        db.execSQL(MovieContract.FavoriteMovieEntry.DELETE_FAVORITES);
        db.execSQL(MovieContract.PopularMovieEntry.DELETE_POPULAR);
        db.execSQL(MovieContract.RatingMovieEntry.DELETE_RATING);
        db.execSQL(MovieContract.VideoEntry.DELETE_VIDEOS);
        db.execSQL(MovieContract.ReviewEntry.DELETE_REVIEWS);
        onCreate(db);
    }
}
