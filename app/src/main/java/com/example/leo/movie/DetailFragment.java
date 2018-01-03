package com.example.leo.movie;


import android.content.Context;
import android.content.Intent;
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

import com.example.leo.movie.model.Movie;
import com.example.leo.movie.model.MovieDAO;
import com.example.leo.movie.model.Video;
import com.example.leo.movie.model.VideoDAO;
import com.example.leo.movie.network.URLDownloader;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailFragment extends MyFragment {
    private AppCompatActivity mActivity;
    private IDetailViewClickListener mDetailViewClickListener;
    private Long mMovieId;
    private VideoDAO mVideoDAO;
    private MovieDAO mMovieDAO;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
        mVideoDAO = new VideoDAO(mActivity);
        mMovieDAO = new MovieDAO(mActivity);

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

        long movieId = mActivity.getIntent().getExtras().getLong(MainFragment.MOVIE_ID_KEY, -1);

        Movie movie = mMovieDAO.getMovie(movieId);

        if (movie != null) {
            mMovieId = movie.id;

            ((TextView) rootView.findViewById(R.id.titleTextView)).setText(movie.title);

            loadPosterImage(movie.poster_path, rootView);

            ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(
                    SimpleDateFormat.getDateInstance().format(movie.release_date));

            ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(
                    String.format(getString(R.string.ratings), movie.vote_average));
            rootView.findViewById(R.id.ratingTextView).setOnClickListener(view ->
                    mDetailViewClickListener.onMovieRatingsViewClickListener(mMovieId));

            setupMarkAsFavoriteButton(rootView.findViewById(R.id.markAsFavoriteButton));

            ((TextView) rootView.findViewById(R.id.overviewTextView)).setText(movie.overview);
        }

        return rootView;
    }

    private void setupMarkAsFavoriteButton(final Button markAsFavoriteButton) {
        boolean isFavorite = mMovieDAO.isFavorite(mMovieId);

        if (isFavorite) {
            markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
        } else {
            markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
        }

        markAsFavoriteButton.setOnClickListener(view -> {
            boolean isFavorite1 = mMovieDAO.isFavorite(mMovieId);

            if (!isFavorite1) {
                mMovieDAO.setFavorite(mMovieId);
                markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
            } else {
                mMovieDAO.removeFavorite(mMovieId);
                markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
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
                    Log.e(DetailFragment.class.getSimpleName(), videos.length() + " Videos fetched");
                    saveVideos(videos, movieId);
                } catch (JSONException e) {
                    Log.e(DetailFragment.class.getSimpleName(), e.getMessage());
                }

                List<Video> videos = mVideoDAO.getVideos(movieId);

                ViewGroup videoList = mActivity.findViewById(R.id.movie_videos_list);
                LayoutInflater layoutInflater = (LayoutInflater) mActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null) {
                    for (Video video : videos) {
                        View view = layoutInflater.inflate(R.layout.movie_videos_listitem, null);
                        ((TextView) view.findViewById(R.id.videoTitleTextView)).setText(video.name);
                        videoList.addView(view);

                        final Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + video.key);

                        view.setOnClickListener(view1 -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            startActivity(intent);
                        });
                    }
                }
            }

            @Override
            public void onFailure(String reason) {
                Log.e(DetailFragment.class.getSimpleName(), reason);
            }
        });
    }

    private void saveVideos(JSONArray videos, long movieId) {
        List<Video> videoList = Arrays.asList(new Gson().fromJson(videos.toString(), Video[].class));
        mVideoDAO.saveVideos(videoList, movieId);
    }

    public interface IDetailViewClickListener {
        public void onMovieRatingsViewClickListener(long movieId);
    }
}
