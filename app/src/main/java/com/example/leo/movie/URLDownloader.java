package com.example.leo.movie;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.leo.movie.model.Movie;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Leo on 13/11/2017.
 */

public class URLDownloader {
    private URLDownloader() {
    }

    public static void downloadURL(final URL url, final IDownloadListener downloadListener) {
        if (url == null) {
            downloadListener.onFailure("Invalid url: null");
            return;
        }

        final Handler uiHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            Log.i(URLDownloader.class.getSimpleName(), "onDownloading: " + url.toString());

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                try (InputStream stream = connection.getInputStream()) {
                    Log.i(URLDownloader.class.getSimpleName(), "download finished: " + url.toString());

                    final String result = readStream(stream);
                    uiHandler.post(() -> downloadListener.onDone(result));
                }

            } catch (final IOException e) {
                uiHandler.post(() -> downloadListener.onFailure(e.getMessage()));
                Log.e(URLDownloader.class.getSimpleName(), e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();

    }

    private static String readStream(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try {
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(URLDownloader.class.getSimpleName(), e.getMessage());
            throw e;
        }

        return result.toString();
    }
}
