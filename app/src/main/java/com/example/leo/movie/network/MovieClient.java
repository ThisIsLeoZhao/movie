package com.example.leo.movie.network;

import com.example.leo.movie.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leo on 22/02/2018.
 */

public interface MovieClient {
    public static MovieClient getClient() {
        return RetrofitRequester.getMovieRequester().create(MovieClient.class);
    }
    @GET("movie/{sortOrder}?api_key=4e93ad4ab25cd6b40805b15c762698a2")
    Call<Result> listMovies(@Path("sortOrder") String sortOrder, @Query("page") int page);
}
