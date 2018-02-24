package com.example.leo.movie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Leo on 21/12/2017.
 */

public class Movie {

    @SerializedName("vote_count")
    @Expose
    public long voteCount;
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("video")
    @Expose
    public boolean video;
    @SerializedName("vote_average")
    @Expose
    public double voteAverage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("popularity")
    @Expose
    public double popularity;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("genre_ids")
    @Expose
    public List<Long> genreIds = null;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("adult")
    @Expose
    public boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;

}