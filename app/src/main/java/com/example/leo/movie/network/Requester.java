package com.example.leo.movie.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.leo.movie.model.Movie;
import com.example.leo.movie.schema.ListResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leo on 13/11/2017.
 */

public class Requester {
    private Requester() {
    }

    public static void makeRequest(final URL url, final ResponseHandler responseCallback) {
        if (url == null) {
            responseCallback.fail("Invalid request url: null");
            return;
        }

        final Handler uiHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            Log.i(Requester.class.getSimpleName(), "onDownloading: " + url.toString());

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                try (InputStream stream = connection.getInputStream()) {
                    Log.i(Requester.class.getSimpleName(), "download finished: " + url.toString());

                    final String result = readStream(stream);
                    uiHandler.post(() -> responseCallback.success(result));
                }

            } catch (final IOException e) {
                uiHandler.post(() -> responseCallback.fail(e.getMessage()));
                Log.e(Requester.class.getSimpleName(), e.getMessage());
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
            Log.e(Requester.class.getSimpleName(), e.getMessage());
            throw e;
        }

        return result.toString();
    }
}
