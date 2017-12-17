package com.example.leo.movie.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.leo.movie.IDownloadListener;
import com.example.leo.movie.MovieDownloader;
import com.example.leo.movie.MovieStore;
import com.example.leo.movie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by Leo on 15/01/2017.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private MovieStore mMovieStore;

    public MovieSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mMovieStore = new MovieStore(context);
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

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        MovieDownloader.fetchExistedMovie(getContext(), new IDownloadListener() {
            @Override
            public void onDone(String response) {
                try {
                    JSONArray movies = new JSONObject(response).getJSONArray("results");
                    mMovieStore.insertMovies(movies);
                } catch (JSONException e) {
                    Log.e(MovieSyncAdapter.class.getSimpleName(), e.getMessage());
                }
            }

            @Override
            public void onFailure(String reason) {
                Log.e(MovieSyncAdapter.class.getSimpleName(), reason);
            }
        });
    }

}
