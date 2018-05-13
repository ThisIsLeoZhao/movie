package com.example.leo.movie.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite_movie",
        indices = {@Index(value = "id", unique = true)})
public class FavoriteMovie {
    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "id")
    public long id;

    public FavoriteMovie(long id) {
        this.id = id;
    }
}
