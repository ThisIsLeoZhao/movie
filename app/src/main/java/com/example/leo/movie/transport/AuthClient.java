package com.example.leo.movie.transport;

import com.example.leo.movie.model.generated.LoginResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Leo on 24/02/2018.
 */

public interface AuthClient {
    public static AuthClient obtain() {
        return RetrofitRequester.getAuthRequester().create(AuthClient.class);
    }

    @POST("login")
    @FormUrlEncoded
    Call<LoginResult> login(@Field("username") String username, @Field("password") String password);
}
