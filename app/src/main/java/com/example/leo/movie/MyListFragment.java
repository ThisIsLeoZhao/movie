package com.example.leo.movie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Leo on 29/11/2017.
 */

public class MyListFragment extends ListFragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onActivityCreated");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("LifecycleLogger", this.getClass().getSimpleName() + " onDestroy");
    }
}
