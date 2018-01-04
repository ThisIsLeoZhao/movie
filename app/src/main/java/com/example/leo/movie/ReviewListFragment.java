package com.example.leo.movie;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.leo.movie.model.Review;
import com.example.leo.movie.model.ReviewDAO;
import com.example.leo.movie.network.URLDownloader;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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

                List<Review> reviews = new ReviewDAO(getActivity()).getReviews(mMovieId);

                setListAdapter(new ReviewAdapter(getContext(), 0, reviews));
            }

            @Override
            public void onFailure(String reason) {
                Log.e(ReviewListFragment.class.getSimpleName(), reason);
            }
        });
    }

    private void saveReviews(JSONArray reviews) {
        List<Review> reviewList = Arrays.asList(new Gson().fromJson(reviews.toString(), Review[].class));
        new ReviewDAO(getActivity()).saveReviews(reviewList, mMovieId);
    }

    private class ReviewAdapter extends ArrayAdapter<Review> {
        public ReviewAdapter(@NonNull Context context, int resource, @NonNull List<Review> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Review review = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_listitem, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.review_author)).setText(review.author);
            ((TextView) convertView.findViewById(R.id.review_content)).setText(review.content);

            return convertView;
        }
    }
}
