package com.example.leo.movie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Leo on 28/11/2017.
 */

public class MyActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONCREATE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONSAVEINSTANCESTATE");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONRESOTREINSTANCESTATE");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONSTART");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONRESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONPAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONSTOP");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " ONDESTROY");
    }
}
