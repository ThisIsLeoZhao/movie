package com.example.leo.movie.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PopularMovieDao {
    @Query("SELECT * FROM popular_movie " +
            "INNER JOIN movie ON popular_movie.id = movie.id " +
            "ORDER BY movie.popularity DESC")
    LiveData<List<Movie>> getAllPopularMoviesDesc();

    @Insert(onConflict = REPLACE)
    void insertAll(List<PopularMovie> movies);
}
