package com.example.leo.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

/**
 * Created by Leo on 30/12/2016.
 */

public class MainFragment extends MyFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Activity mActivity;

    private CursorAdapter mImageAdapter;

    private static final int LOADER_ID = 1;
    public static final String MOVIE_ENTRY_ID = "movieEntryId";

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

        mImageAdapter = new PosterImageAdapter(mActivity);

        GridView posterView = rootView.findViewById(R.id.poster_grid);
        posterView.setAdapter(mImageAdapter);

        posterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra(MOVIE_ENTRY_ID, position + 1);
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

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);
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
        }
    }
}
