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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.syncAdapter.MovieSyncAdapter;
import com.example.leo.movie.syncAdapter.MovieSyncService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Leo on 30/12/2016.
 */

public class MainFragment extends MyFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int LOADER_ID = 1;
    public static String MOVIE_ID_KEY = "movieId";
    private static String KEY_PREF_SORT_ORDER;
    private static String KEY_PREF_SHOW_FAVORITE;
    private Activity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mPullToLoadMoreTextView;
    private CursorAdapter mPosterAdapter;
    private boolean mShowFavorites;
    private boolean mSortByRatings;
    private MovieStore mMovieStore;
    private boolean isLoadingNewData = false;

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

        GridView posterView = rootView.findViewById(R.id.poster_grid);
        mPosterAdapter = new PosterImageAdapter(mActivity);
        posterView.setAdapter(mPosterAdapter);

        mMovieStore = new MovieStore(getContext());

        Intent intent = new Intent(mActivity, MovieSyncService.class);
        mActivity.startService(intent);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView posterView = view.findViewById(R.id.poster_grid);
        mPullToLoadMoreTextView = view.findViewById(R.id.pullToLoadMoreTextView);

        posterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra(MOVIE_ID_KEY, (int) view.getTag());

                startActivity(intent);
            }
        });

        posterView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean userScrolled = false;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (!isLoadingNewData && userScrolled && i + i1 == i2) {
                    mPullToLoadMoreTextView.setVisibility(View.VISIBLE);
                    userScrolled = false;
                    getMoreData();
                }
            }
        });

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(MainFragment.class.getSimpleName(), "onRefresh called from SwipeRefreshLayout");

            MovieSyncAdapter.syncImmediately(mActivity);
        });
        mSwipeRefreshLayout.setEnabled(!prefs.getBoolean(KEY_PREF_SHOW_FAVORITE, false));
    }

    private void getMoreData() {
        MovieDownloader.fetchMoreMovie(getActivity(), new IDownloadListener() {
            @Override
            public void onDone(String response) {
                try {
                    JSONArray movies = new JSONObject(response).getJSONArray("results");
                    mMovieStore.insertMovies(movies);
                } catch (JSONException e) {
                    Log.e(MainFragment.class.getSimpleName(), e.getMessage());
                }

                mPullToLoadMoreTextView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String reason) {
                Log.e(MainFragment.class.getSimpleName(), reason);

                mPullToLoadMoreTextView.setVisibility(View.GONE);
            }
        });
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
        isLoadingNewData = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mPosterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
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

    private class PosterImageAdapter extends CursorAdapter {
        public PosterImageAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.grid_image_view, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (cursor == null) {
                return;
            }

            ImageView imageView = view.findViewById(R.id.poster_grid_image_view);

            String poster_path = cursor.getString(
                    cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.POSTER_PATH_COLUMN));

            final String BASE = "https://image.tmdb.org/t/p/";
            final String IMAGE_SIZE = "w500";

            final Uri uri = Uri.parse(BASE).buildUpon()
                    .appendEncodedPath(IMAGE_SIZE)
                    .appendEncodedPath(poster_path)
                    .build();

            Picasso.with(getActivity()).load(uri).into(imageView);

            view.setTag(cursor.getInt(
                    cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID_COLUMN)));
        }
    }
}
