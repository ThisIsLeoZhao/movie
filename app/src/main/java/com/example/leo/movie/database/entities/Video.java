package com.example.leo.movie.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "video",
        indices = {@Index(value = "id", unique = true)},
        foreignKeys = @ForeignKey(entity = Movie.class,
                parentColumns = "id",
                childColumns = "movie_id"))
public class Video {
    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "iso_639_1")
    @SerializedName("iso_639_1")
    public String iso6391;

    @ColumnInfo(name = "iso_3166_1")
    @SerializedName("iso_3166_1")
    public String iso31661;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "site")
    public String site;

    @ColumnInfo(name = "size")
    public long size;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "movie_id")
    public long movieId;
}
