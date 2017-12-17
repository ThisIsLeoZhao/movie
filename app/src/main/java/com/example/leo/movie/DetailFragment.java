package com.example.leo.movie;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.movie.database.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailFragment extends MyFragment {
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_ID_COLUMN,
            MovieContract.MovieEntry.MOVIE_TITLE_COLUMN,
            MovieContract.MovieEntry.POSTER_PATH_COLUMN,
            MovieContract.MovieEntry.RELEASE_DATE_COLUMN,
            MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN,
            MovieContract.MovieEntry.OVERVIEW_COLUMN
    };
    private static final int COL_MOVIE_ENTRY_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_POSTER_PATH = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_VOTE_AVERAGE = 5;
    private static final int COL_MOVIE_OVERVIEW = 6;
    private AppCompatActivity mActivity;
    private IDetailViewClickListener mDetailViewClickListener;
    private Long mMovieId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;

        if (mActivity instanceof IDetailViewClickListener) {
            mDetailViewClickListener = (IDetailViewClickListener) mActivity;
        } else {
            throw new RuntimeException(mActivity.getClass().getSimpleName()
                    + " needs to implement " + IDetailViewClickListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int movieId = mActivity.getIntent().getExtras().getInt(MainFragment.MOVIE_ID_KEY, -1);

        Cursor cursor = mActivity.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                MovieContract.MovieEntry.MOVIE_ID_COLUMN + " = ?",
                new String[]{String.valueOf(movieId)},
                null
        );

        if (cursor != null && cursor.moveToNext()) {
            mMovieId = cursor.getLong(COL_MOVIE_ID);

            ((TextView) rootView.findViewById(R.id.titleTextView)).setText(
                    cursor.getString(COL_MOVIE_TITLE));

            final String poster_path = cursor.getString(COL_MOVIE_POSTER_PATH);
            loadPosterImage(poster_path, rootView);

            ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(
                    cursor.getString(COL_MOVIE_RELEASE_DATE));

            double rating = cursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
            ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(
                    String.format(getString(R.string.ratings), rating));
            rootView.findViewById(R.id.ratingTextView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDetailViewClickListener.onMovieRatingsViewClickListener(mMovieId);
                }
            });

            setupMarkAsFavoriteButton(rootView.findViewById(R.id.markAsFavoriteButton));

            ((TextView) rootView.findViewById(R.id.overviewTextView)).setText(
                    cursor.getString(COL_MOVIE_OVERVIEW));

            cursor.close();
        }

        return rootView;
    }

    private void setupMarkAsFavoriteButton(final Button markAsFavoriteButton) {
        Cursor cursor = mActivity.getContentResolver().query(
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN + " = ?",
                new String[]{String.valueOf(mMovieId)}, null);

        boolean isFavorite = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        if (isFavorite) {
            markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
        } else {
            markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
        }

        markAsFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = mActivity.getContentResolver().query(
                        MovieContract.FavoriteMovieEntry.CONTENT_URI,
                        null,
                        MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN + " = ?",
                        new String[]{String.valueOf(mMovieId)}, null);

                boolean isFavorite = cursor != null && cursor.moveToFirst();

                if (cursor != null) {
                    cursor.close();
                }

                if (!isFavorite) {
                    ContentValues value = new ContentValues();
                    value.put(MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN, mMovieId);

                    mActivity.getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, value);
                    markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
                } else {
                    mActivity.getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                            MovieContract.FavoriteMovieEntry.MOVIE_ID_KEY_COLUMN + " = ?",
                            new String[]{String.valueOf(mMovieId)});
                    markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        downloadVideos(mMovieId);
    }

    private void loadPosterImage(String poster_path, View rootView) {
        final String BASE = "https://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w500";

        final Uri uri = Uri.parse(BASE).buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
                .appendEncodedPath(poster_path)
                .build();

        Picasso.with(mActivity).load(uri)
                .into((ImageView) rootView.findViewById(R.id.thumbnailImageView));
    }

    private void downloadVideos(long movieId) {
        final String BASE = "https://api.themoviedb.org/3/";
        final String TYPE_PARAM = "movie";
        final String MOVIE_ID_PARAM = String.valueOf(movieId);
        final String VIDEO_PARAM = "videos";
        final String API_KEY_PARAMS = "api_key";

        URL url = null;
        try {
            url = new URL(Uri.parse(BASE).buildUpon()
                    .appendEncodedPath(TYPE_PARAM)
                    .appendEncodedPath(MOVIE_ID_PARAM)
                    .appendEncodedPath(VIDEO_PARAM)
                    .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                    .build().toString());
        } catch (MalformedURLException e) {
            Log.e(DetailFragment.class.getSimpleName(), e.getMessage());
        }

        URLDownloader.downloadURL(url, new IDownloadListener() {
            @Override
            public void onDone(String response) {
                if (response == null) {
                    return;
                }

                try {
                    JSONArray videos = new JSONObject(response).getJSONArray("results");
                    saveVideos(videos);
                } catch (JSONException e) {
                    Log.e(DetailFragment.class.getSimpleName(), e.getMessage());
                }

                Cursor cursor = mActivity.getContentResolver().query(MovieContract.VideoEntry.buildVideoUriWithMovieId(movieId),
                        null, null, null, null);

                if (cursor == null) {
                    Log.e(DetailFragment.class.getSimpleName(), 0 + " Videos fetched");
                    return;
                }
                Log.e(DetailFragment.class.getSimpleName(), cursor.getCount() + " Videos fetched");

                final int keyColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.KEY_COLUMN);
                final int nameColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.NAME_COLUMN);

                ViewGroup videoList = mActivity.findViewById(R.id.movie_videos_list);
                LayoutInflater layoutInflater = (LayoutInflater) mActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null) {
                    while (cursor.moveToNext()) {
                        View view = layoutInflater.inflate(R.layout.movie_videos_listitem, null);
                        ((TextView) view.findViewById(R.id.videoTitleTextView)).setText(cursor.getString(nameColumnIndex));
                        videoList.addView(view);

                        String videoKey = cursor.getString(keyColumnIndex);
                        final Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + videoKey);

                        view.setOnClickListener(view1 -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            startActivity(intent);
                        });
                    }
                    cursor.close();
                }
            }

            @Override
            public void onFailure(String reason) {
                Log.e(DetailFragment.class.getSimpleName(), reason);
            }
        });
    }

    private void saveVideos(JSONArray videos) {
        try {
            ContentValues[] values = new ContentValues[videos.length()];
            for (int i = 0; i < videos.length(); i++) {
                JSONObject video = videos.getJSONObject(i);

                ContentValues value = new ContentValues();
                value.put(MovieContract.VideoEntry.MOVIE_ID_KEY_COLUMN, mMovieId);
                value.put(MovieContract.VideoEntry.VIDEO_ID_COLUMN, video.getString(MovieContract.VideoEntry.VIDEO_ID_COLUMN));
                value.put(MovieContract.VideoEntry.KEY_COLUMN, video.getString(MovieContract.VideoEntry.KEY_COLUMN));
                value.put(MovieContract.VideoEntry.NAME_COLUMN, video.getString(MovieContract.VideoEntry.NAME_COLUMN));
                value.put(MovieContract.VideoEntry.TYPE_COLUMN, video.getString(MovieContract.VideoEntry.TYPE_COLUMN));

                values[i] = value;
            }

            mActivity.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, values);
        } catch (JSONException e) {
            Log.e(DetailFragment.class.getSimpleName(), e.getMessage());
        }
    }

    public interface IDetailViewClickListener {
        public void onMovieRatingsViewClickListener(long movieId);
    }
}
