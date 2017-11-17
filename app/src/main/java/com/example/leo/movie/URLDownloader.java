package com.example.leo.movie;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leo on 13/11/2017.
 */

public class URLDownloader {
    public static String downloadURL(URL url) {
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            try (InputStream stream = connection.getInputStream()) {
                result = readStream(stream);
            }

        } catch (IOException e) {
            Log.e(URLDownloader.class.getSimpleName(), e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    private static String readStream(InputStream stream) {
        StringBuilder result = new StringBuilder();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try {
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(URLDownloader.class.getSimpleName(), e.getMessage());
        }

        return result.toString();
    }
}
