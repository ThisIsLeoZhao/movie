package com.example.leo.movie;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Leo on 23/12/2017.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mIsLoading;

    public EndlessRecyclerOnScrollListener(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy < 0) {
            return;
        }

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

        Log.i(EndlessRecyclerOnScrollListener.class.getSimpleName(), visibleItemCount + " " +
                totalItemCount + " " + lastVisibleItem);

        if (!mIsLoading && lastVisibleItem + visibleItemCount >= totalItemCount) {
            onLoadMore();
            mIsLoading = true;
        }
    }

    public void setLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    public abstract void onLoadMore();
}
