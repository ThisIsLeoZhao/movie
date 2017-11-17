package com.example.leo.movie;

import android.app.Activity;
import android.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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

public class DetailFragment extends Fragment {
    private Activity mActivity;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        int movieEntryId = mActivity.getIntent().getExtras().getInt(MainFragment.MOVIE_ENTRY_ID, -1);

        Cursor cursor = mActivity.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                MovieContract.MovieEntry._ID + " = ?", new String[]{String.valueOf(movieEntryId)},
                null
        );

        if (cursor != null && cursor.moveToNext()) {
            ((TextView) rootView.findViewById(R.id.titleTextView)).setText(
                    cursor.getString(COL_MOVIE_TITLE));

            final String poster_path = cursor.getString(COL_MOVIE_POSTER_PATH);
            loadPosterImage(poster_path, rootView);

            ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(
                    cursor.getString(COL_MOVIE_RELEASE_DATE));

            double rating = cursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
            ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(
                    String.format(getString(R.string.ratings), rating));

            ((TextView) rootView.findViewById(R.id.overviewTextView)).setText(
                    cursor.getString(COL_MOVIE_OVERVIEW));

            new DownloadVideosTask().execute(cursor.getLong(COL_MOVIE_ID));
            cursor.close();
        }

        return rootView;
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

    private class DownloadVideosTask extends AsyncTask<Long, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Long... movieIds) {
            final long movieId = movieIds[0];

            final String BASE = "https://api.themoviedb.org/3/";
            final String TYPE_PARAM = "movie";
            final String MOVIE_ID_PARAM = String.valueOf(movieId);
            final String VIDEO_PARAM = "videos";
            final String API_KEY_PARAMS = "api_key";

            Uri uri = Uri.parse(BASE).buildUpon()
                    .appendEncodedPath(TYPE_PARAM)
                    .appendEncodedPath(MOVIE_ID_PARAM)
                    .appendEncodedPath(VIDEO_PARAM)
                    .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                    .build();

            String movieVideos = null;
            try {
                movieVideos = URLDownloader.downloadURL(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                Log.e(DownloadVideosTask.class.getSimpleName(), e.getMessage());
            }

            if (movieVideos != null) {
                try {
                    JSONArray videos = new JSONObject(movieVideos).getJSONArray("results");

                    ContentValues[] values = new ContentValues[videos.length()];
                    for (int i = 0; i < videos.length(); i++) {
                        JSONObject video = videos.getJSONObject(i);

                        ContentValues value = new ContentValues();
                        value.put(MovieContract.VideoEntry.MOVIE_ID_KEY_COLUMN, movieId);
                        value.put(MovieContract.VideoEntry.VIDEO_ID_COLUMN, video.getString(MovieContract.VideoEntry.VIDEO_ID_COLUMN));
                        value.put(MovieContract.VideoEntry.KEY_COLUMN, video.getString(MovieContract.VideoEntry.KEY_COLUMN));
                        value.put(MovieContract.VideoEntry.NAME_COLUMN, video.getString(MovieContract.VideoEntry.NAME_COLUMN));
                        value.put(MovieContract.VideoEntry.TYPE_COLUMN, video.getString(MovieContract.VideoEntry.TYPE_COLUMN));

                        values[i] = value;
                    }

                    mActivity.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, values);

                    return mActivity.getContentResolver().query(MovieContract.VideoEntry.buildVideoUriWithMovieId(movieId),
                            null, null, null, null);
                } catch (JSONException e) {
                    Log.e(DownloadVideosTask.class.getSimpleName(), e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Cursor cursor) {
            final int keyColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.KEY_COLUMN);
            final int nameColumnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.NAME_COLUMN);

            ViewGroup videoList = mActivity.findViewById(R.id.movie_videos_list);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    View view = getLayoutInflater().inflate(R.layout.movie_videos_listitem, null);
                    ((TextView) view.findViewById(R.id.videoTitleTextView)).setText(cursor.getString(nameColumnIndex));
                    videoList.addView(view);

                    String videoKey = cursor.getString(keyColumnIndex);
                    final Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + videoKey);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            startActivity(intent);
                        }
                    });
                }
                cursor.close();
            }
        }
    }
}
