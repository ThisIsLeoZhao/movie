package com.example.leo.movie;

/**
 * Created by Leo on 16/12/2017.
 */

public interface IDownloadListener {
    public void onDone(String response);

    void onFailure(String reason);
}
