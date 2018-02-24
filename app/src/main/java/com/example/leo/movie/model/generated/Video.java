package com.example.leo.movie.model.generated;

/**
 * Created by Leo on 02/01/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Video {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;
    @SerializedName("iso_3166_1")
    @Expose
    public String iso31661;
    @SerializedName("key")
    @Expose
    public String key;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("site")
    @Expose
    public String site;
    @SerializedName("size")
    @Expose
    public long size;
    @SerializedName("type")
    @Expose
    public String type;

    public long movieId;
}