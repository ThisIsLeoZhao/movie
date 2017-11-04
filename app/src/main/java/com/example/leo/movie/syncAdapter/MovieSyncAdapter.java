package com.example.leo.movie.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.leo.movie.R;
import com.example.leo.movie.database.MovieContract;
import com.example.leo.movie.database.MovieProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by Leo on 15/01/2017.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public MovieSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public static void initialiseSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final String BASE = "https://api.themoviedb.org/3/";
        String PATH = "movie/popular";
        final String API_KEY_PARAMS = "api_key";

        Uri uri = Uri.parse(BASE).buildUpon()
                .appendEncodedPath(PATH)
                .appendQueryParameter(API_KEY_PARAMS, "4e93ad4ab25cd6b40805b15c762698a2")
                .build();

        Log.d(MovieSyncAdapter.class.getName(), "onPerformSync");
        try {
            URL url = new URL(uri.toString());
            String result = downloadURL(url);

            if (result == null) {
                return;
            }

            getContext().getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null, null);
            JSONArray movies = new JSONObject(result).getJSONArray("results");

            ContentValues[] values = new ContentValues[movies.length()];
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);

                ContentValues value = new ContentValues();
                value.put(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN, movie.getString("title"));
                value.put(MovieContract.MovieEntry.POSTER_PATH_COLUMN, movie.getString("poster_path"));
                value.put(MovieContract.MovieEntry.RELEASE_DATE_COLUMN, movie.getString("release_date"));
                value.put(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN, movie.getString("vote_average"));
                value.put(MovieContract.MovieEntry.OVERVIEW_COLUMN, movie.getString("overview"));

                values[i] = value;
            }

            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
            Log.d(MovieSyncAdapter.class.getName(), "syncCompleted");

        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        }
    }


    public static Account getSyncAccount(Context context) {
        Account account = new Account("dummyAccount", context.getString(R.string.account_type));

        AccountManager manager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        if (manager.addAccountExplicitly(account, "", null)) {
            onAccountCreated(context, account);
            return account;
        }
        return null;
    }

    private static void onAccountCreated(Context context, Account account) {
        ContentResolver.setIsSyncable(account, context.getString(R.string.content_authority), 1);
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(90, 1).
                    setSyncAdapter(account, context.getString(R.string.content_authority)).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, context.getString(R.string.content_authority), new Bundle(), 90);
        }
    }

    private String downloadURL(URL url) {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    private String readStream(InputStream stream) {
        StringBuilder result = new StringBuilder();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try {
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
