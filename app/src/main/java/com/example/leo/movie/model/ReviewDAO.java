package com.example.leo.movie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.leo.movie.database.MovieContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 03/01/2018.
 */

public class ReviewDAO extends DAO {
    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.REVIEW_ID_COLUMN,
            MovieContract.ReviewEntry.AUTHOR_COLUMN,
            MovieContract.ReviewEntry.CONTENT_COLUMN,
            MovieContract.ReviewEntry.URL_COLUMN,
            MovieContract.ReviewEntry.MOVIE_ID_KEY_COLUMN
    };

    private static final int COL_REVIEW_ID = 1;
    private static final int COL_REVIEW_AUTHOR = 2;
    private static final int COL_REVIEW_CONTENT = 3;
    private static final int COL_REVIEW_URL = 4;
    private static final int COL_VIDEO_MOVIE_ID = 5;

    public ReviewDAO(Context context) {
        super(context);
    }

    public void saveReviews(List<Review> reviews, long movieId) {
        ContentValues[] values = new ContentValues[reviews.size()];

        for (int i = 0; i < reviews.size(); i++) {

            Review review = reviews.get(i);
            ContentValues value = new ContentValues();

            value.put(REVIEW_COLUMNS[COL_VIDEO_MOVIE_ID], movieId);
            value.put(REVIEW_COLUMNS[COL_REVIEW_ID], review.id);
            value.put(REVIEW_COLUMNS[COL_REVIEW_AUTHOR], review.author);
            value.put(REVIEW_COLUMNS[COL_REVIEW_CONTENT], review.content);
            value.put(REVIEW_COLUMNS[COL_REVIEW_URL], review.url.toString());

            values[i] = value;

        }

        mContext.getContentResolver()
                .bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, values);
    }

    public List<Review> getReviews(long movieId) {
        List<Review> reviews = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver()
                .query(MovieContract.ReviewEntry.buildReviewUriWithMovieId(movieId),
                        null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Review review = new Review();
                review.id = cursor.getString(cursor.getColumnIndex(REVIEW_COLUMNS[COL_VIDEO_MOVIE_ID]));
                review.author = cursor.getString(cursor.getColumnIndex(REVIEW_COLUMNS[COL_REVIEW_AUTHOR]));
                review.content = cursor.getString(cursor.getColumnIndex(REVIEW_COLUMNS[COL_REVIEW_CONTENT]));
                try {
                    review.url = new URL(cursor.getString(cursor.getColumnIndex(REVIEW_COLUMNS[COL_REVIEW_URL])));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                review.movieId = movieId;

                reviews.add(review);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return reviews;
    }
}
