package com.example.leo.movie.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.leo.movie.database.entities.Review;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ReviewDao {
    @Query("SELECT * FROM review WHERE movie_id = :movieId")
    LiveData<List<Review>> getAllReviewsForMovie(long movieId);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Review> reviews);
}
