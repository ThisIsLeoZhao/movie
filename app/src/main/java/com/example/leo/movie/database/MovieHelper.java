package com.example.leo.movie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leo on 02/01/2017.
 */

public class MovieHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;

    public MovieHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContract.MovieEntry.CREATE_MOVIES);
        db.execSQL(MovieContract.VideoEntry.CREATE_VIDEOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cache upgrade just need to discard the old data and start over
        db.execSQL(MovieContract.VideoEntry.DELETE_VIDEOS);
        db.execSQL(MovieContract.MovieEntry.DELETE_MOVIES);
        onCreate(db);
    }
}
