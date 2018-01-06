package com.example.leo.movie.network;

import android.net.Uri;

import com.example.leo.movie.BuildConfig;

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

    public enum SortOrder {
        POPULAR("popular"),
        TOP_RATED("top_rated");

        private String mSortOrder;

        SortOrder(String sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public String toString() {
            return mSortOrder;
        }
    }
}
