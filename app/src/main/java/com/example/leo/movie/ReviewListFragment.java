package com.example.leo.movie;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.example.leo.movie.database.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leo on 21/11/2017.
 */

public class ReviewListFragment extends MyListFragment {
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

        long movieId = getArguments().getLong("movieId", -1);
        if (movieId != -1) {
            new DownloadReviewsTask().execute(movieId);
        }
    }

    private class DownloadReviewsTask extends AsyncTask<Long, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Long... longs) {
            final long movieId = longs[0];

            final String BASE = "https://api.themoviedb.org/3/";
            final String TYPE_PARAM = "movie";
            final String MOVIE_ID_PARAM = String.valueOf(movieId);
            final String REVIEW_PARAM = "reviews";
            final String API_KEY_PARAMS = "api_key";

            Uri uri = Uri.parse(BASE).buildUpon()
                    .appendEncodedPath(TYPE_PARAM)
                    .appendEncodedPath(MOVIE_ID_PARAM)
                    .appendEncodedPath(REVIEW_PARAM)
                    .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                    .build();

            String response;
            try {
                response = URLDownloader.downloadURL(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                Log.e(DownloadReviewsTask.class.getSimpleName(), e.getMessage());
                return null;
            }

            try {
                JSONArray results = new JSONObject(response).getJSONArray("results");
                ContentValues[] reviews = new ContentValues[results.length()];

                for (int i = 0; i < results.length(); i++) {
                    JSONObject reviewJSON = results.getJSONObject(i);
                    ContentValues review = new ContentValues();

                    review.put(MovieContract.ReviewEntry.MOVIE_ID_KEY_COLUMN, movieId);
                    review.put(MovieContract.ReviewEntry.REVIEW_ID_COLUMN, reviewJSON.getString(MovieContract.ReviewEntry.REVIEW_ID_COLUMN));
                    review.put(MovieContract.ReviewEntry.AUTHOR_COLUMN, reviewJSON.getString(MovieContract.ReviewEntry.AUTHOR_COLUMN));
                    review.put(MovieContract.ReviewEntry.CONTENT_COLUMN, reviewJSON.getString(MovieContract.ReviewEntry.CONTENT_COLUMN));
                    review.put(MovieContract.ReviewEntry.URL_COLUMN, reviewJSON.getString(MovieContract.ReviewEntry.URL_COLUMN));

                    reviews[i] = review;
                }

                getContext().getContentResolver()
                        .bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviews);

                return getContext().getContentResolver()
                        .query(MovieContract.ReviewEntry.buildReviewUriWithMovieId(movieId),
                                null, null, null, null);

            } catch (JSONException e) {
                Log.e(DownloadReviewsTask.class.getSimpleName(), e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            setListAdapter(new SimpleCursorAdapter(getContext(),
                    R.layout.review_listitem,
                    cursor,
                    new String[] {MovieContract.ReviewEntry.AUTHOR_COLUMN, MovieContract.ReviewEntry.CONTENT_COLUMN},
                    new int[] {R.id.review_author, R.id.review_content}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
        }
    }
}
