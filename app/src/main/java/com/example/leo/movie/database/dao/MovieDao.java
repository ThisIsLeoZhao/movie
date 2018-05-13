package com.example.leo.movie.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.leo.movie.database.entities.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAllMovies();

    @Query(("SELECT * FROM movie WHERE id = :movieId"))
    Movie getMovie(long movieId);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Movie> movies);
}
