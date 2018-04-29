package com.example.leo.movie.transport;

import com.example.leo.movie.BuildConfig;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leo on 22/02/2018.
 */

public class RetrofitRequester {
    private static GsonConverterFactory sGsonConverterFactory = GsonConverterFactory.create();
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final OkHttpClient MOVIE_OK_HTTP_CLIENT = OK_HTTP_CLIENT.newBuilder().addInterceptor(chain -> {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.MY_MOVIE_DB_API_KEY)
                .build();

        return chain.proceed(original.newBuilder().url(url).build());
    }).build();

    private static final Retrofit.Builder BUILDER = new Retrofit.Builder();

    private static Retrofit sMovieRequester = getRequester("https://api.themoviedb.org/3/", MOVIE_OK_HTTP_CLIENT);;
    private static Retrofit sAuthRequester = getRequester("http://192.168.0.10:3000/", OK_HTTP_CLIENT);

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
