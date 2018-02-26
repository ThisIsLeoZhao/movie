package com.example.leo.movie.model.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Leo on 24/02/2018.
 */

public class FavoriteList {
    @SerializedName("favorites")
    @Expose
    public List<Long> favorites = null;
}
