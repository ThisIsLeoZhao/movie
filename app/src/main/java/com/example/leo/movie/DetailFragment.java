package com.example.leo.movie;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.leo.movie.database.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailFragment extends Fragment {
    private Activity mActivity;

    private static final String[] MOVIE_COLUMNS = {
        MovieContract.MovieEntry._ID,
        MovieContract.MovieEntry.MOVIE_TITLE_COLUMN,
        MovieContract.MovieEntry.POSTER_PATH_COLUMN,
        MovieContract.MovieEntry.RELEASE_DATE_COLUMN,
        MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN,
        MovieContract.MovieEntry.OVERVIEW_COLUMN
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER_PATH = 2;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_VOTE_AVERAGE = 4;
    private static final int COL_MOVIE_OVERVIEW = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mActivity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int movieId = mActivity.getIntent().getExtras().getInt("movieId", -1) + 1;

        Cursor cursor = mActivity.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                "_ID = ?", new String[] {String.valueOf(movieId)},
                null
        );

        if (cursor != null) {
            if (cursor.moveToNext()) {
                ((TextView) rootView.findViewById(R.id.titleTextView)).setText(
                        cursor.getString(COL_MOVIE_TITLE));

                final String BASE = "https://image.tmdb.org/t/p/";
                final String IMAGE_SIZE = "w500";

                final String poster_path = cursor.getString(COL_MOVIE_POSTER_PATH);
                final Uri uri = Uri.parse(BASE).buildUpon()
                        .appendEncodedPath(IMAGE_SIZE)
                        .appendEncodedPath(poster_path)
                        .build();

                Picasso.with(getActivity()).load(uri)
                        .into((ImageView) rootView.findViewById(R.id.thumbnailImageView));

                ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(
                        cursor.getString(COL_MOVIE_RELEASE_DATE));

                double rating = cursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
                ((RatingBar) rootView.findViewById(R.id.ratingBar)).setNumStars(10);
                ((RatingBar) rootView.findViewById(R.id.ratingBar)).setRating((float) rating);
                ((RatingBar) rootView.findViewById(R.id.ratingBar)).setStepSize((float) 0.5);
                ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(String.valueOf(rating));

                ((TextView) rootView.findViewById(R.id.overviewTextView)).setText(
                        cursor.getString(COL_MOVIE_OVERVIEW));
            }

            cursor.close();
        }

        return rootView;
    }
}
