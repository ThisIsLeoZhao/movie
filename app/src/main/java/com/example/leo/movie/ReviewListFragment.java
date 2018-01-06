package com.example.leo.movie;

import android.content.Context;
import android.graphics.Color;
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
import com.example.leo.movie.network.Requester;
import com.example.leo.movie.network.ResponseHandler;
import com.example.leo.movie.network.URLBuilder;
import com.example.leo.movie.schema.ListResult;

import java.net.MalformedURLException;
import java.net.URL;
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
        URL url = null;
        try {
            url = URLBuilder.reviewFetchURL(mMovieId);
        } catch (MalformedURLException e) {
            Log.e(ReviewListFragment.class.getSimpleName(), e.getMessage());
        }

        Requester.makeRequest(url, new ResponseHandler<>(ReviewListResult.class, new IResponseCallback<ReviewListResult>() {
            @Override
            public void success(ReviewListResult response) {
                if (response == null) {
                    return;
                }

                new ReviewDAO(getActivity()).saveReviews(response.results, mMovieId);

                List<Review> reviews = new ReviewDAO(getActivity()).getReviews(mMovieId);

                setListAdapter(new ReviewAdapter(getContext(), 0, reviews));
            }

            @Override
            public void fail(String reason) {
                Log.e(ReviewListFragment.class.getSimpleName(), reason);
            }
        }));
    }

    private class ReviewListResult extends ListResult<Review> {
        public ReviewListResult(Class<Review> type) {
            super(type);
        }
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
