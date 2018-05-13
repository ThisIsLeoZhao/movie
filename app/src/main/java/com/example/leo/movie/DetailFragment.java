package com.example.leo.movie;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.movie.database.dao.FavoriteMovieDao;
import com.example.leo.movie.database.dao.MovieDao;
import com.example.leo.movie.database.MovieDatabase;
import com.example.leo.movie.database.dao.VideoDao;
import com.example.leo.movie.database.entities.Movie;
import com.example.leo.movie.database.entities.Video;
import com.example.leo.movie.login.LoginActivity;
import com.example.leo.movie.login.LoginUtils;
import com.example.leo.movie.model.generated.VideoResult;
import com.example.leo.movie.transport.MovieClient;
import com.example.leo.movie.transport.UserClient;
import com.example.leo.movie.util.AppExecutors;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailFragment extends MyFragment {
    private AppCompatActivity mActivity;
    private IDetailViewClickListener mDetailViewClickListener;
    private Long mMovieId;
    private MovieDao mMovieDao;
    private VideoDao mVideoDao;
    private FavoriteMovieDao mFavoriteMovieDao;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;

        mMovieDao = MovieDatabase.getInstance(getContext()).movieDao();
        mVideoDao = MovieDatabase.getInstance(getContext()).videoDao();
        mFavoriteMovieDao = MovieDatabase.getInstance(getContext()).favoriteMovieDao();

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

        AppExecutors.diskIO().execute(() -> {
            final Movie movie = mMovieDao.getMovie(movieId);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (movie != null) {
                    mMovieId = movie.id;

                    ((TextView) rootView.findViewById(R.id.titleTextView)).setText(movie.title);

                    loadPosterImage(movie.posterPath, rootView);

                    ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText(movie.releaseDate);

                    ((TextView) rootView.findViewById(R.id.ratingTextView)).setText(
                            String.format(getString(R.string.ratings), movie.voteAverage));
                    rootView.findViewById(R.id.ratingTextView).setOnClickListener(view ->
                            mDetailViewClickListener.onMovieRatingsViewClickListener(mMovieId));

//            setupMarkAsFavoriteButton(rootView.findViewById(R.id.markAsFavoriteButton));

                    ((TextView) rootView.findViewById(R.id.overviewTextView)).setText(movie.overview);

                    downloadVideos(mMovieId);
                }
            });
        });

        return rootView;
    }

//    private void setupMarkAsFavoriteButton(final Button markAsFavoriteButton) {
//        boolean isFavorite = mMovieDAO.isFavorite(mMovieId);
//
//        if (isFavorite) {
//            markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
//        } else {
//            markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
//        }
//
//        markAsFavoriteButton.setOnClickListener(view -> {
//            boolean isFavorite1 = mMovieDAO.isFavorite(mMovieId);
//
//            if (!LoginUtils.isLogin(getContext())) {
//                startActivity(new Intent(getContext(), LoginActivity.class));
//                markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
//                return;
//            }
//
//            if (!isFavorite1) {
//                markAsFavoriteButton.setText(getText(R.string.marked_as_favorite));
//
//                mMovieDAO.setFavorite(mMovieId);
//                UserClient.obtain().postFavorite(LoginUtils.currentUser(getContext()), mMovieId).enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//
//                    }
//                });
//            } else {
//                markAsFavoriteButton.setText(getText(R.string.mark_as_favorite));
//
//                mMovieDAO.removeFavorite(mMovieId);
//                UserClient.obtain().deleteFavorite(LoginUtils.currentUser(getContext()), mMovieId).enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//
//                    }
//                });
//                // TODO: sync layer
//            }
//        });
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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
        MovieClient.obtain().getMovieVideos(movieId).enqueue(new Callback<VideoResult>() {
            @Override
            public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                List<Video> videos = response.body().results;
                videos.forEach(video -> video.movieId = movieId);
                Log.e(DetailFragment.class.getSimpleName(), videos.size() + " Videos fetched");
                AppExecutors.diskIO().execute(() -> mVideoDao.insertAll(videos));

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
            public void onFailure(Call<VideoResult> call, Throwable t) {
                Log.e(DetailFragment.class.getSimpleName(), t.getMessage());
            }
        });
    }

    public interface IDetailViewClickListener {
        public void onMovieRatingsViewClickListener(long movieId);
    }
}
