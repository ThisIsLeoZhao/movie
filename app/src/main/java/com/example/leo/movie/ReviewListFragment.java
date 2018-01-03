package com.example.leo.movie;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.network.URLDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leo on 21/11/2017.
 */

public class ReviewListFragment extends MyListFragment {
    private long mMovieId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovieId = getArguments().getLong(MainFragment.MOVIE_ID_KEY, -1);
        downloadReviews();
    }

    private void downloadReviews() {
        final String BASE = "https://api.themoviedb.org/3/";
        final String TYPE_PARAM = "movie";
        final String MOVIE_ID_PARAM = String.valueOf(mMovieId);
        final String REVIEW_PARAM = "reviews";
        final String API_KEY_PARAMS = "api_key";

        URL url = null;
        try {
            url = new URL(Uri.parse(BASE).buildUpon()
                    .appendEncodedPath(TYPE_PARAM)
                    .appendEncodedPath(MOVIE_ID_PARAM)
                    .appendEncodedPath(REVIEW_PARAM)
                    .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                    .build().toString());
        } catch (MalformedURLException e) {
            Log.e(ReviewListFragment.class.getSimpleName(), e.getMessage());
        }

        URLDownloader.downloadURL(url, new IDownloadListener() {
            @Override
            public void onDone(String response) {
                if (response == null) {
                    return;
                }

                try {
                    JSONArray results = new JSONObject(response).getJSONArray("results");
                    saveReviews(results);
                } catch (JSONException e) {
                    Log.e(ReviewListFragment.class.getSimpleName(), e.getMessage());
                }

                Cursor cursor = getContext().getContentResolver()
                        .query(MovieContract.ReviewEntry.buildReviewUriWithMovieId(mMovieId),
                                null, null, null, null);

                setListAdapter(new SimpleCursorAdapter(getContext(),
                        R.layout.review_listitem,
                        cursor,
                        new String[]{MovieContract.ReviewEntry.AUTHOR_COLUMN, MovieContract.ReviewEntry.CONTENT_COLUMN},
                        new int[]{R.id.review_author, R.id.review_content}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
            }

            @Override
            public void onFailure(String reason) {
                Log.e(ReviewListFragment.class.getSimpleName(), reason);
            }
        });
    }

    private void saveReviews(JSONArray reviews) {
        try {
            ContentValues[] values = new ContentValues[reviews.length()];

            for (int i = 0; i < reviews.length(); i++) {

                JSONObject review = reviews.getJSONObject(i);
                ContentValues value = new ContentValues();

                value.put(MovieContract.ReviewEntry.MOVIE_ID_KEY_COLUMN, mMovieId);
                value.put(MovieContract.ReviewEntry.REVIEW_ID_COLUMN, review.getString(MovieContract.ReviewEntry.REVIEW_ID_COLUMN));
                value.put(MovieContract.ReviewEntry.AUTHOR_COLUMN, review.getString(MovieContract.ReviewEntry.AUTHOR_COLUMN));
                value.put(MovieContract.ReviewEntry.CONTENT_COLUMN, review.getString(MovieContract.ReviewEntry.CONTENT_COLUMN));
                value.put(MovieContract.ReviewEntry.URL_COLUMN, review.getString(MovieContract.ReviewEntry.URL_COLUMN));

                values[i] = value;

            }
            getContext().getContentResolver()
                    .bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, values);
        } catch (JSONException e) {
            Log.e(ReviewListFragment.class.getSimpleName(), e.getMessage());
        }
    }
}
