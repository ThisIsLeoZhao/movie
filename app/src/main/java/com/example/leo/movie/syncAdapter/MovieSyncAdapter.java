package com.example.leo.movie.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.movie.BuildConfig;
import com.example.leo.movie.R;
import com.example.leo.movie.URLDownloader;
import com.example.leo.movie.database.MovieContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.ACCOUNT_SERVICE;
import static com.example.leo.movie.SettingsActivity.KEY_PREF_SORT_ORDER;

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

    public static void syncImmediately(Context context) {
        Bundle settings = new Bundle();
        settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority),
                settings);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final String BASE = "https://api.themoviedb.org/3/";
        final String TYPE_PARAM = "movie";
        String SORT_PARAM = "popular";
        final String API_KEY_PARAMS = "api_key";

        final boolean sortByRatings = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(KEY_PREF_SORT_ORDER, getContext().getString(R.string.pref_sort_by_popularity)).equals(getContext().getString(R.string.pref_sort_by_ratings));
        if (sortByRatings) {
            SORT_PARAM = "top_rated";
        }

        Uri uri = Uri.parse(BASE).buildUpon()
                .appendEncodedPath(TYPE_PARAM)
                .appendEncodedPath(SORT_PARAM)
                .appendQueryParameter(API_KEY_PARAMS, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build();

        try {
            URL url = new URL(uri.toString());
            String result = URLDownloader.downloadURL(url);

            if (result == null) {
                return;
            }

            JSONArray movies = new JSONObject(result).getJSONArray("results");

            ContentValues[] values = new ContentValues[movies.length()];
            ContentValues[] movieIds = new ContentValues[movies.length()];
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);

                ContentValues value = new ContentValues();
                value.put(MovieContract.MovieEntry.MOVIE_ID_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                value.put(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_TITLE_COLUMN));
                value.put(MovieContract.MovieEntry.POSTER_PATH_COLUMN, movie.getString(MovieContract.MovieEntry.POSTER_PATH_COLUMN));
                value.put(MovieContract.MovieEntry.RELEASE_DATE_COLUMN, movie.getString(MovieContract.MovieEntry.RELEASE_DATE_COLUMN));
                value.put(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN, movie.getString(MovieContract.MovieEntry.VOTE_AVERAGE_COLUMN));
                value.put(MovieContract.MovieEntry.OVERVIEW_COLUMN, movie.getString(MovieContract.MovieEntry.OVERVIEW_COLUMN));

                values[i] = value;

                value = new ContentValues();
                if (sortByRatings) {
                    value.put(MovieContract.RatingMovieEntry.MOVIE_ID_KEY_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                } else {
                    value.put(MovieContract.PopularMovieEntry.MOVIE_ID_KEY_COLUMN, movie.getString(MovieContract.MovieEntry.MOVIE_ID_COLUMN));
                }
                movieIds[i] = value;
            }

            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
            if (sortByRatings) {
                getContext().getContentResolver().bulkInsert(MovieContract.RatingMovieEntry.CONTENT_URI, movieIds);
            } else {
                getContext().getContentResolver().bulkInsert(MovieContract.PopularMovieEntry.CONTENT_URI, movieIds);
            }

        } catch (MalformedURLException | JSONException e) {
            Log.e(MovieSyncAdapter.class.getSimpleName(), e.getMessage());
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

        // we can enable inexact timers in our periodic sync
        SyncRequest request = new SyncRequest.Builder().
                syncPeriodic(90, 1).
                setSyncAdapter(account, context.getString(R.string.content_authority)).
                setExtras(new Bundle()).build();
        ContentResolver.requestSync(request);
    }

}
