package com.example.leo.movie.model.generated;

import com.example.leo.movie.database.entities.Video;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Leo on 24/02/2018.
 */

public class VideoResult {

    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("results")
    @Expose
    public List<Video> results = null;

}