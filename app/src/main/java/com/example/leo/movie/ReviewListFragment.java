package com.example.leo.movie;

import android.arch.lifecycle.LiveData;
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

import com.example.leo.movie.database.MovieDatabase;
import com.example.leo.movie.database.dao.ReviewDao;
import com.example.leo.movie.database.entities.Review;
import com.example.leo.movie.model.generated.ReviewResult;
import com.example.leo.movie.transport.MovieClient;
import com.example.leo.movie.util.AppExecutors;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leo on 21/11/2017.
 */

public class ReviewListFragment extends MyListFragment {
    private long mMovieId;
    private ReviewDao mReviewDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(Color.WHITE);
        }
        mReviewDao = MovieDatabase.getInstance(getContext()).reviewDao();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovieId = getArguments().getLong(MainFragment.MOVIE_ID_KEY, -1);
        downloadReviews();
    }

    private void downloadReviews() {
        MovieClient.obtain().getMovieReviews(mMovieId).enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                AppExecutors.diskIO().execute(() -> {
                    mReviewDao.insertAll(response.body().results.stream().map(
                            review -> {
                                review.movieId = mMovieId;
                                return review;
                            }
                    ).collect(Collectors.toList()));
                });

                LiveData<List<Review>> reviews = mReviewDao.getAllReviewsForMovie(mMovieId);
                reviews.observe(ReviewListFragment.this, reviews1 -> {
                    setListAdapter(new ReviewAdapter(getContext(), 0, reviews1));
                });

            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Log.e(ReviewListFragment.class.getSimpleName(), t.getMessage());
            }
        });
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
