package com.example.leo.movie.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "movie",
        indices = {@Index(value = "id", unique = true)})
public class Movie {
    @PrimaryKey(autoGenerate = true)
    public long _id;

    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    public String posterPath;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    public String releaseDate;

    @ColumnInfo(name = "overview")
    public String overview;

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    public double voteAverage;

    @ColumnInfo(name = "popularity")
    public double popularity;

    @SerializedName("vote_count")
    @Ignore
    public long voteCount;
    @SerializedName("video")
    @Ignore
    public boolean video;
    @SerializedName("original_language")
    @Ignore
    public String originalLanguage;
    @SerializedName("original_title")
    @Ignore
    public String originalTitle;
    @SerializedName("genre_ids")
    @Ignore
    public List<Long> genreIds = null;
    @SerializedName("backdrop_path")
    @Ignore
    public String backdropPath;
    @SerializedName("adult")
    @Ignore
    public boolean adult;
}
