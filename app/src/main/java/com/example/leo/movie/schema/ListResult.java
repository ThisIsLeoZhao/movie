package com.example.leo.movie.schema;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Leo on 04/01/2018.
 */

public class ListResult<T> {
    private final Class<T> type;
    public List<T> results;

    public ListResult(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public void fromJson(String json) {
        T result = new Gson().fromJson(json, type);
    }
}
