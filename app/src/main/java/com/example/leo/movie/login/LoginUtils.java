package com.example.leo.movie.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.movie.database.MovieDatabase;
import com.example.leo.movie.database.dao.FavoriteMovieDao;
import com.example.leo.movie.database.entities.FavoriteMovie;
import com.example.leo.movie.model.generated.FavoriteList;
import com.example.leo.movie.transport.UserClient;
import com.example.leo.movie.util.AppExecutors;

import java.util.stream.Collectors;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leo on 25/02/2018.
 */

public class LoginUtils {
    private static final String TOKEN_KEY = "token";
    private static final String LOGGED_IN_USER_KEY = "loggedInUser";
    private static FavoriteMovieDao sFavoriteMovieDao;

    public static void login(String username, String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(TOKEN_KEY, token).apply();
        prefsEditor.putString(LOGGED_IN_USER_KEY, username).apply();

        UserClient.obtain().getFavorites(username).enqueue(new Callback<FavoriteList>() {
            @Override
            public void onResponse(Call<FavoriteList> call, Response<FavoriteList> response) {
                if (response.isSuccessful()) {
                    sFavoriteMovieDao = MovieDatabase.getInstance(context).favoriteMovieDao();
                    AppExecutors.diskIO().execute(() ->
                            sFavoriteMovieDao.insertAll(response.body().favorites.stream().
                            map(FavoriteMovie::new).collect(Collectors.toList())));
                }
            }

            @Override
            public void onFailure(Call<FavoriteList> call, Throwable t) {
                // TODO: handle error
                Log.e(LoginUtils.class.getSimpleName(), t.getMessage());
            }
        });
    }

    public static boolean isLogin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey("weibobyleo".getBytes()).parse(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            Log.e(LoginUtils.class.getSimpleName(), e.toString());
            logout(context);
            return false;
        }

        return true;
    }

    public static void logout(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(TOKEN_KEY).apply();
        prefsEditor.remove(LOGGED_IN_USER_KEY).apply();

        sFavoriteMovieDao = MovieDatabase.getInstance(context).favoriteMovieDao();
        AppExecutors.diskIO().execute(() -> sFavoriteMovieDao.deleteAll());
    }

    public static String currentUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(LOGGED_IN_USER_KEY, null);
    }
}
