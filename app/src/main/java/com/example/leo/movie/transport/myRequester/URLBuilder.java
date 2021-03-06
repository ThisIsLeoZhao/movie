package com.example.leo.movie.transport.myRequester;

import android.net.Uri;

import com.example.leo.movie.BuildConfig;
import com.example.leo.movie.transport.SortOrder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leo on 06/01/2018.
 */

public class URLBuilder {
    private static final String BASE = "https://api.themoviedb.org/3/";
    private static final String PATH_MOVIE = "movie";
    private static final String PATH_API_KEY = "api_key";
    private static final String PATH_PAGE = "page";
    private static final String PATH_VIDEO = "videos";
    private static final String PATH_REVIEW = "reviews";

    private static final String LOGIN_BASE = "http://192.168.0.10:3000";
    private static final String PATH_LOGIN = "login";

    public static URL loginURL() {
        Uri uri = Uri.parse(LOGIN_BASE).buildUpon()
                .appendEncodedPath(PATH_LOGIN)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL movieFetchURL(SortOrder sortOrder, int page) throws MalformedURLException {
        Uri uri = Uri.parse(BASE).buildUpon()
                .appendEncodedPath(PATH_MOVIE)
                .appendEncodedPath(sortOrder.toString())
                .appendQueryParameter(PATH_PAGE, String.valueOf(page))
                .appendQueryParameter(PATH_API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build();

        return new URL(uri.toString());
    }

    public static URL videoFetchURL(long movieId) throws MalformedURLException {
        return new URL(Uri.parse(BASE).buildUpon()
                .appendEncodedPath(PATH_MOVIE)
                .appendEncodedPath(String.valueOf(movieId))
                .appendEncodedPath(PATH_VIDEO)
                .appendQueryParameter(PATH_API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build().toString());
    }

    public static URL reviewFetchURL(long movieId) throws MalformedURLException {
        return new URL(Uri.parse(BASE).buildUpon()
                .appendEncodedPath(PATH_MOVIE)
                .appendEncodedPath(String.valueOf(movieId))
                .appendEncodedPath(PATH_REVIEW)
                .appendQueryParameter(PATH_API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build().toString());
    }
}
