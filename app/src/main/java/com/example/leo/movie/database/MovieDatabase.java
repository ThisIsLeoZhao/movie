package com.example.leo.movie.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Movie.class, PopularMovie.class, RatingMovie.class, FavoriteMovie.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {
    private static final Object lock = new Object();
    private static volatile MovieDatabase INSTANCE;

    public abstract MovieDao movieDao();
    public abstract PopularMovieDao popularMovieDao();
    public abstract FavoriteMovieDao favoriteMovieDao();
    public abstract RatingMovieDao ratingMovieDao();

    public static MovieDatabase getInstance(Context context) {
        MovieDatabase result = INSTANCE; // Makes sure INSTANCE is accessed only once to improve performance
        if (INSTANCE == null) {
            synchronized (lock) {
                result = INSTANCE;
                if (result == null) {
                    result = Room.databaseBuilder(context.getApplicationContext(),
                            MovieDatabase.class, "movie.db").fallbackToDestructiveMigration().build();
                    INSTANCE = result;
                }
            }
        }

        return result;
    }
}
