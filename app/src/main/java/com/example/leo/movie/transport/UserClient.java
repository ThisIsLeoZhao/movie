package com.example.leo.movie.transport;

import com.example.leo.movie.model.generated.FavoriteList;
import com.example.leo.movie.model.generated.LoginResult;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Leo on 24/02/2018.
 */

public interface UserClient {
    public static UserClient obtain() {
        return RetrofitRequester.getUserRequester().create(UserClient.class);
    }

    @POST("login")
    @FormUrlEncoded
    Call<LoginResult> login(@Field("username") String username, @Field("password") String password);

    @POST("favorite")
    @FormUrlEncoded
    Call<Void> postFavorite(@Field("username") String username, @Field("movieId") long movieId);

    @DELETE("favorite/{username}/{movieId}")
    Call<Void> deleteFavorite(@Path("username") String username, @Path("movieId") long movieId);

    @GET("favorite/{username}")
    Call<FavoriteList> getFavorites(@Path("username") String username);
}
