package com.example.leo.movie.transport;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.leo.movie.transport.OkHttpClientStore.AUTH_HTTP_CLIENT;
import static com.example.leo.movie.transport.OkHttpClientStore.MOVIE_HTTP_CLIENT;

/**
 * Created by Leo on 22/02/2018.
 */

public class RetrofitRequester {
    private static GsonConverterFactory sGsonConverterFactory = GsonConverterFactory.create();
    private static final Retrofit.Builder BUILDER = new Retrofit.Builder();

    private static Retrofit sMovieRequester = getRequester("https://api.themoviedb.org/3/", MOVIE_HTTP_CLIENT);
    private static Retrofit sAuthRequester = getRequester("https://192.168.0.10:3000/", AUTH_HTTP_CLIENT);

    public static Retrofit getMovieRequester() {
        return sMovieRequester;
    }

    public static Retrofit getUserRequester() {
        return sAuthRequester;
    }

    private static Retrofit getRequester(String host, OkHttpClient okHttpClient) {
        return BUILDER
                .baseUrl(host)
                .client(okHttpClient)
                .addConverterFactory(sGsonConverterFactory)
                .build();
    }


}
