package com.example.leo.movie.model.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Leo on 16/01/2018.
 */

public class LoginResult {

    @SerializedName("auth")
    @Expose
    public boolean auth;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("message")
    @Expose
    public String message;

}