package com.example.leo.movie;

import org.json.JSONArray;

/**
 * Created by Leo on 16/12/2017.
 */

public interface IDownloadListener {
    public void onDone(String result);

    void onFailure(String reason);
}
