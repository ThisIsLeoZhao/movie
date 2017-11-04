package com.example.leo.movie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Leo on 31/12/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_detail, new DetailFragment())
                    .commit();
        }

    }
}
