package com.example.leo.movie.network;

import com.example.leo.movie.model.MovieResult;
import com.example.leo.movie.model.ReviewResult;
import com.example.leo.movie.model.VideoResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leo on 22/02/2018.
 */

public interface MovieClient {
    public static MovieClient obtain() {
        return RetrofitRequester.getMovieRequester().create(MovieClient.class);
    }

    @GET("movie/{sortOrder}?api_key=4e93ad4ab25cd6b40805b15c762698a2")
    Call<MovieResult> getMovies(@Path("sortOrder") String sortOrder, @Query("page") int page);

    @GET("movie/{movieId}/videos?api_key=4e93ad4ab25cd6b40805b15c762698a2")
    Call<VideoResult> getMovieVideos(@Path("movieId") long movieId);

    @GET("movie/{movieId}/reviews?api_key=4e93ad4ab25cd6b40805b15c762698a2")
    Call<ReviewResult> getMovieReviews(@Path("movieId") long movieId);
}
