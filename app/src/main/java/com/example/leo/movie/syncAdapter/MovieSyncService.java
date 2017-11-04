package com.example.leo.movie.syncAdapter;

import android.app.Service;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Leo on 01/01/2017.
 */

public class MovieSyncService extends Service {
    private static MovieSyncAdapter sMovieSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
