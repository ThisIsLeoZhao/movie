package com.example.leo.movie.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "review",
        indices = {@Index(value = "id", unique = true),
                @Index(value = "movie_id")},
        foreignKeys = @ForeignKey(entity = Movie.class,
                parentColumns = "id",
                childColumns = "movie_id"))
public class Review {
    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "movie_id")
    public long movieId;
}
