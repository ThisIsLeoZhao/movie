package com.example.leo.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.model.Movie;
import com.example.leo.movie.model.MovieDAO;
import com.example.leo.movie.network.MovieDownloader;
import com.example.leo.movie.syncAdapter.MovieSyncService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 30/12/2016.
 */

public class MainFragment extends MyFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = MainFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;
    public static String MOVIE_ID_KEY = "movieId";
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
    private MovieDAO mMovieDAO;

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

        mMovieDAO = new MovieDAO(getContext());

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

                        mMovieDAO.insertMovies(movies);
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
                    mMovieDAO.insertMovies(movies);

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

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!mShowFavorites) {
            final Uri contentUri = mSortByRatings ? MovieContract.RatingMovieEntry.CONTENT_URI :
                    MovieContract.PopularMovieEntry.CONTENT_URI;
            final String sortOrder = mSortByRatings ? MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN :
                    MovieContract.MovieEntry.POPULARITY_COLUMN;

            return new CursorLoader(mActivity,
                    contentUri,
                    null,
                    null, null,
                    sortOrder + " DESC");
        }

        return new CursorLoader(mActivity,
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPosterAdapter.swapItems(MovieDAO.getMovies(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapItems(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (s.equals(KEY_PREF_SORT_ORDER)) {
            mSortByRatings = prefs.getString(KEY_PREF_SORT_ORDER, getString(R.string.pref_sort_by_popularity))
                    .equals(getContext().getString(R.string.pref_sort_by_ratings));
            getLoaderManager().restartLoader(LOADER_ID, null, MainFragment.this);
        } else if (s.equals(KEY_PREF_SHOW_FAVORITE)) {
            mShowFavorites = prefs.getBoolean(KEY_PREF_SHOW_FAVORITE, false);
            mSwipeRefreshLayout.setEnabled(!mShowFavorites);
            getLoaderManager().restartLoader(LOADER_ID, null, MainFragment.this);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Movie> mPosters = new ArrayList<>();

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
                        .appendEncodedPath(movie.poster_path)
                        .build();

                Picasso.with(mImageView.getContext()).load(uri).into(mImageView);

                mImageView.setOnClickListener(view1 -> {
                    Intent intent = new Intent(mActivity, DetailActivity.class);
                    intent.putExtra(MOVIE_ID_KEY, movie.id);

                    startActivity(intent);
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("ImageAdapter", "newView");

            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_image_view, parent, false);

            ViewHolder viewHolder = new ViewHolder(inflatedView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
    }
}
