package com.example.leo.movie.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leo on 22/02/2018.
 */

public class RetrofitRequester {
    public static Retrofit getMovieRequester() {
        return new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
