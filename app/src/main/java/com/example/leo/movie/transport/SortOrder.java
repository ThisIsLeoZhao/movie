package com.example.leo.movie.transport;

/**
 * Created by Leo on 24/02/2018.
 */

public enum SortOrder {
    POPULAR("popular"),
    TOP_RATED("top_rated");

    private String mSortOrder;

    SortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return mSortOrder;
    }
}
