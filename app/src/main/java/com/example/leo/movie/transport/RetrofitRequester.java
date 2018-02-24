package com.example.leo.movie.transport;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leo on 22/02/2018.
 */

public class RetrofitRequester {
    private static Retrofit sMovieRequester;
    private static Retrofit sAuthRequester;

    public static Retrofit getMovieRequester() {
        if (sMovieRequester == null) {
            sMovieRequester = getRequester("https://api.themoviedb.org/3/");
        }
        return sMovieRequester;
    }

    public static Retrofit getAuthRequester() {
        if (sAuthRequester == null) {
            sAuthRequester = getRequester("http://192.168.0.10:3000/");
        }
        return sAuthRequester;
    }


    private static Retrofit getRequester(String host) {
        return new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
