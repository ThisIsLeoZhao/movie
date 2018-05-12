package com.example.leo.movie.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RatingMovieDao {
    @Query("SELECT * FROM rating_movie " +
            "INNER JOIN movie ON rating_movie.id = movie.id " +
            "ORDER BY movie.vote_average DESC")
    LiveData<List<Movie>> getAllRatingMoviesDesc();

    @Insert(onConflict = REPLACE)
    void insertAll(List<RatingMovie> movies);
}
