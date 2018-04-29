package com.example.leo.movie.transport;

import com.example.leo.movie.model.generated.MovieResult;
import com.example.leo.movie.model.generated.ReviewResult;
import com.example.leo.movie.model.generated.VideoResult;

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

    @GET("movie/{sortOrder}")
    Call<MovieResult> getMovies(@Path("sortOrder") String sortOrder, @Query("page") int page);

    @GET("movie/{movieId}/videos")
    Call<VideoResult> getMovieVideos(@Path("movieId") long movieId);

    @GET("movie/{movieId}/reviews")
    Call<ReviewResult> getMovieReviews(@Path("movieId") long movieId);
}
