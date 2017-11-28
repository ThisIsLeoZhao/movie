package com.example.leo.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailActivity extends MyActivity implements DetailFragment.IDetailViewClickListener {
    public static String MOVIE_ID_KEY = "movieId";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public void onMovieRatingsViewClickListener(long movieId) {
        Bundle args = new Bundle();
        args.putLong(MOVIE_ID_KEY, movieId);
        finish();
        Fragment fragment = new ReviewListFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
