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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.syncAdapter.MovieSyncService;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Set;

import static com.example.leo.movie.SettingsActivity.KEY_PREF_SHOW_FAVORITE;

/**
 * Created by Leo on 30/12/2016.
 */

public class MainFragment extends MyFragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private Activity mActivity;

    private CursorAdapter mPosterAdapter;
    private boolean mShowFavorites;

    private static final int LOADER_ID = 1;

    public static String MOVIE_ID_KEY = "movieId";
    public static String FAVORITE_MOVIE_KEY = "favoriteMovies";

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

        mPosterAdapter = new PosterImageAdapter(mActivity);

        GridView posterView = rootView.findViewById(R.id.poster_grid);
        posterView.setAdapter(mPosterAdapter);

        posterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra(MOVIE_ID_KEY, (int) view.getTag());

                startActivity(intent);
            }
        });

        Intent intent = new Intent(mActivity, MovieSyncService.class);
        mActivity.startService(intent);

        return rootView;
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
            return new CursorLoader(mActivity,
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null, null,
                    null);
        }

        Set<String> allFavorites = mActivity.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                .getStringSet(FAVORITE_MOVIE_KEY, Collections.<String>emptySet());
        StringBuilder selection = new StringBuilder(MovieContract.MovieEntry.MOVIE_ID_COLUMN + " IN (");
        for (String favoriteId : allFavorites) {
            selection.append(favoriteId);
            selection.append(",");
        }
        if (selection.charAt(selection.length() - 1) == ',') {
            selection.deleteCharAt(selection.length() - 1);
        }

        selection.append(")");

        return new CursorLoader(mActivity, MovieContract.MovieEntry.CONTENT_URI,
                null, selection.toString(), null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPosterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (s.equals(KEY_PREF_SHOW_FAVORITE)) {
            mShowFavorites = prefs.getBoolean(KEY_PREF_SHOW_FAVORITE, false);
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

            Picasso.with(mActivity).load(uri).into(imageView);

            view.setTag(cursor.getInt(
                    cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID_COLUMN)));
        }
    }
}
