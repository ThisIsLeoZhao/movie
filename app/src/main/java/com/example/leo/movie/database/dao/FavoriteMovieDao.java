package com.example.leo.movie.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.leo.movie.database.entities.FavoriteMovie;
import com.example.leo.movie.database.entities.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movie " +
            "INNER JOIN movie ON favorite_movie.id = movie.id " +
            "ORDER BY movie.vote_average DESC")
    LiveData<List<Movie>> getAllFavoriteMoviesDesc();

    @Query("SELECT * FROM favorite_movie INNER JOIN movie " +
            "ON favorite_movie.id = :movieId AND favorite_movie.id = movie.id")
    Movie getFavoriteMovie(long movieId);

    @Insert(onConflict = REPLACE)
    void insertAll(List<FavoriteMovie> movies);

    @Insert(onConflict = REPLACE)
    void insert(FavoriteMovie movie);

    @Query("DELETE FROM favorite_movie WHERE id = :movieId")
    void delete(long movieId);

    @Query("DELETE FROM favorite_movie")
    void deleteAll();
}
