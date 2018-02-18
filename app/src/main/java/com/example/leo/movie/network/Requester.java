package com.example.leo.movie.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

    public static void get(final URL url, final ResponseHandler responseCallback) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                String response = getUrl(url);
                uiHandler.post(() -> responseCallback.success(response));
            } catch (Exception e) {
                uiHandler.post(() -> responseCallback.fail(e.getMessage()));
            }
        }).start();
    }

    public static void post(final URL url, final String post, final ResponseHandler responseCallback) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                String response = postUrl(url, post);
                uiHandler.post(() -> responseCallback.success(response));
            } catch (Exception e) {
                uiHandler.post(() -> responseCallback.fail(e.toString()));
            }
        }).start();
    }

    private static String getUrl(final URL url) throws Exception {
        if (url == null) {
            throw new Exception("Invalid url: null");
        }

        Log.i(Requester.class.getSimpleName(), "onDownloading: " + url.toString());

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (InputStream stream = connection.getInputStream()) {
                Log.i(Requester.class.getSimpleName(), "download finished: " + url.toString());

                return readStream(stream);
            }

        } catch (final IOException e) {
            Log.e(Requester.class.getSimpleName(), e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String postUrl(final URL url, final String post) throws Exception {
        if (url == null) {
            throw new Exception("Invalid url: null");
        }

        Log.i(Requester.class.getSimpleName(), "onPosting: " + url.toString());

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(post);
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    try (InputStream stream = connection.getInputStream()) {
                        Log.i(Requester.class.getSimpleName(), "post finished: " + url.toString());

                        return readStream(stream);
                    }
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    try (InputStream stream = connection.getErrorStream()) {
                        throw new Exception(readStream(stream));
                    }
                default:
                    throw new Exception("");
            }
        } catch (final IOException e) {
            Log.e(Requester.class.getSimpleName(), e.toString());
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
