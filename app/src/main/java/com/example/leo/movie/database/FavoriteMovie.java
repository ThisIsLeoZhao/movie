package com.example.leo.movie.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite_movie",
        indices = {@Index(value = "id", unique = true)},
        foreignKeys = @ForeignKey(entity = Movie.class,
                parentColumns = "id",
                childColumns = "id"))
public class FavoriteMovie {
    @PrimaryKey(autoGenerate = true)
    public int _id;

    @ColumnInfo(name = "id")
    public String id;

    public FavoriteMovie(String id) {
        this.id = id;
    }
}
