package com.example.leo.movie;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.movie.database.FavoriteMovieDao;
import com.example.leo.movie.database.Movie;
import com.example.leo.movie.database.MovieDao;
import com.example.leo.movie.database.MovieDatabase;
import com.example.leo.movie.database.PopularMovie;
import com.example.leo.movie.database.PopularMovieDao;
import com.example.leo.movie.database.RatingMovie;
import com.example.leo.movie.database.RatingMovieDao;
import com.example.leo.movie.syncAdapter.MovieSyncService;
import com.example.leo.movie.transport.MovieDownloader;
import com.example.leo.movie.util.AppExecutors;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Leo on 30/12/2016.
 */

public class MainFragment extends MyFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static String MOVIE_ID_KEY = "movieId";
    private static String TAG = MainFragment.class.getSimpleName();
    private static String KEY_PREF_SORT_ORDER;
    private static String KEY_PREF_SHOW_FAVORITE;
    private Activity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mPosterView;
    private TextView mPullToLoadMoreTextView;
    private MyAdapter mPosterAdapter;
    private RecyclerView.LayoutManager mPosterLayoutManager;
    private boolean mShowFavorites;
    private boolean mSortByRatings;
    private MovieDao mMovieDao;
    private RatingMovieDao mRatingMovieDao;
    private PopularMovieDao mPopularMovieDao;
    private FavoriteMovieDao mFavoriteMovieDao;
    private LiveData<List<Movie>> mMovies;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        KEY_PREF_SORT_ORDER = getString(R.string.key_pref_sort_order);
        KEY_PREF_SHOW_FAVORITE = getString(R.string.key_pref_show_favorite);

        mPosterView = rootView.findViewById(R.id.poster_view);
        mPosterLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        mPosterView.setLayoutManager(mPosterLayoutManager);

        mPosterAdapter = new MyAdapter();
        mPosterView.setAdapter(mPosterAdapter);

        mMovieDao = MovieDatabase.getInstance(getContext()).movieDao();
        mRatingMovieDao = MovieDatabase.getInstance(getContext()).ratingMovieDao();
        mPopularMovieDao = MovieDatabase.getInstance(getContext()).popularMovieDao();
        mFavoriteMovieDao = MovieDatabase.getInstance(getContext()).favoriteMovieDao();

        updateSortOrder();

        Intent intent = new Intent(mActivity, MovieSyncService.class);
        mActivity.startService(intent);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPullToLoadMoreTextView = view.findViewById(R.id.pullToLoadMoreTextView);

        mPosterView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mPosterLayoutManager) {
            @Override
            public void onLoadMore() {
                MovieDownloader.fetchMoreMovie(getActivity(), new IFetchMovieListener() {
                    @Override
                    public void onDone(List<Movie> movies) {
                        mPullToLoadMoreTextView.setVisibility(View.GONE);
                        AppExecutors.diskIO().execute(() -> mMovieDao.insertAll(movies));
                        if (mSortByRatings) {
                            AppExecutors.diskIO().execute(() -> mRatingMovieDao.insertAll(
                                    movies.stream().map(value -> new RatingMovie(value.id)).collect(Collectors.toList())));
                        } else {
                            AppExecutors.diskIO().execute(() -> mPopularMovieDao.insertAll(
                                    movies.stream().map(value -> new PopularMovie(value.id)).collect(Collectors.toList())));
                        }

                        setLoading(false);
                    }

                    @Override
                    public void onFailure(String reason) {
                        Log.e(TAG, reason);

                        mPullToLoadMoreTextView.setVisibility(View.GONE);
                        setLoading(false);
                    }
                });
            }
        });

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (mSwipeRefreshLayout == null) {
                return;
            }

            Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
            MovieDownloader.fetchExistedMovie(getActivity(), new IFetchMovieListener() {
                @Override
                public void onDone(List<Movie> movies) {
                    AppExecutors.diskIO().execute(() -> mMovieDao.insertAll(movies));
                    if (mSortByRatings) {
                        AppExecutors.diskIO().execute(() -> mRatingMovieDao.insertAll(
                                movies.stream().map(value -> new RatingMovie(value.id)).collect(Collectors.toList())));
                    } else {
                        AppExecutors.diskIO().execute(() -> mPopularMovieDao.insertAll(
                                movies.stream().map(value -> new PopularMovie(value.id)).collect(Collectors.toList())));
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(String reason) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, reason);
                }
            });
        });
        mSwipeRefreshLayout.setEnabled(!prefs.getBoolean(KEY_PREF_SHOW_FAVORITE, false));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mShowFavorites = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(KEY_PREF_SHOW_FAVORITE, false);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateSortOrder() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSortByRatings = prefs.getString(KEY_PREF_SORT_ORDER, getString(R.string.pref_sort_by_popularity))
                .equals(getContext().getString(R.string.pref_sort_by_ratings));

        if (mMovies != null) {
            mMovies.removeObservers(this);
        }
        mMovies = mSortByRatings ? mRatingMovieDao.getAllRatingMoviesDesc() :
                mPopularMovieDao.getAllPopularMoviesDesc();
        mMovies.observe(this, movies1 -> mPosterAdapter.swapItems(movies1));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (s.equals(KEY_PREF_SORT_ORDER)) {
            updateSortOrder();
        } else if (s.equals(KEY_PREF_SHOW_FAVORITE)) {
            mShowFavorites = prefs.getBoolean(KEY_PREF_SHOW_FAVORITE, false);
            mSwipeRefreshLayout.setEnabled(!mShowFavorites);
            if (mMovies != null) {
                mMovies.removeObservers(this);
            }
            mMovies = mFavoriteMovieDao.getAllFavoriteMoviesDesc();
            mMovies.observe(this, movies1 -> mPosterAdapter.swapItems(movies1));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Movie> mPosters = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("ImageAdapter", "newView");

            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_image_view, parent, false);

            return new ViewHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.i("ImageAdapter", "bindView " + mPosters.get(position).title);
            holder.bindPoster(mPosters.get(position));
        }

        @Override
        public int getItemCount() {
            return mPosters.size();
        }

        public void swapItems(List<Movie> movies) {
            mPosters = movies;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mImageView = view.findViewById(R.id.poster_grid_image_view);
            }

            public void bindPoster(Movie movie) {
                final String BASE = "https://image.tmdb.org/t/p/";
                final String IMAGE_SIZE = "w342";

                final Uri uri = Uri.parse(BASE).buildUpon()
                        .appendEncodedPath(IMAGE_SIZE)
                        .appendEncodedPath(movie.posterPath)
                        .build();

                Picasso.with(mImageView.getContext()).load(uri).into(mImageView);

                mImageView.setOnClickListener(view1 -> {
                    Intent intent = new Intent(mActivity, DetailActivity.class);
                    intent.putExtra(MOVIE_ID_KEY, movie.id);

                    startActivity(intent);
                });
            }
        }
    }
}
