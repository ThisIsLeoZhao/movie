package com.example.leo.movie.model.generated;

/**
 * Created by Leo on 24/02/2018.
 */

import com.example.leo.movie.database.entities.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResult {

    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("page")
    @Expose
    public long page;
    @SerializedName("results")
    @Expose
    public List<Review> results = null;
    @SerializedName("total_pages")
    @Expose
    public long totalPages;
    @SerializedName("total_results")
    @Expose
    public long totalResults;
}
