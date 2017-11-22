package com.example.leo.movie;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailActivity extends AppCompatActivity implements IDetailViewClickListener {
    public static String MOVIE_ID_KEY = "movieId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new DetailFragment())
                    .commit();
        }

    }

    @Override
    public void onMovieRatingsViewClickListener(long movieId) {
        Bundle args = new Bundle();
        args.putLong(MOVIE_ID_KEY, movieId);

        Fragment fragment = new ReviewListFragment();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
