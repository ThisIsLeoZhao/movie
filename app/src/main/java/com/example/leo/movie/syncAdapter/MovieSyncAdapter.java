package com.example.leo.movie.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.movie.IFetchMovieListener;
import com.example.leo.movie.R;
import com.example.leo.movie.database.entities.Movie;
import com.example.leo.movie.database.dao.MovieDao;
import com.example.leo.movie.database.MovieDatabase;
import com.example.leo.movie.database.entities.PopularMovie;
import com.example.leo.movie.database.dao.PopularMovieDao;
import com.example.leo.movie.database.entities.RatingMovie;
import com.example.leo.movie.database.dao.RatingMovieDao;
import com.example.leo.movie.transport.MovieDownloader;
import com.example.leo.movie.util.AppExecutors;

import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by Leo on 15/01/2017.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private MovieDao mMovieDao;
    private RatingMovieDao mRatingMovieDao;
    private PopularMovieDao mPopularMovieDao;
    private boolean mSortByRatings;

    public MovieSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mMovieDao = MovieDatabase.getInstance(getContext()).movieDao();
        mRatingMovieDao = MovieDatabase.getInstance(getContext()).ratingMovieDao();
        mPopularMovieDao = MovieDatabase.getInstance(getContext()).popularMovieDao();
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
                syncPeriodic(9000, 1).
                setSyncAdapter(account, context.getString(R.string.content_authority)).
                setExtras(new Bundle()).build();
        ContentResolver.requestSync(request);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        MovieDownloader.fetchExistedMovie(getContext(), new IFetchMovieListener() {
            @Override
            public void onDone(List<Movie> movies) {
                // TODO: Extract to common utils class
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                mSortByRatings = prefs.getString("pref_sortOrder", "Popularity")
                        .equals(getContext().getString(R.string.pref_sort_by_ratings));


                AppExecutors.diskIO().execute(() -> mMovieDao.insertAll(movies));
                if (mSortByRatings) {
                    AppExecutors.diskIO().execute(() -> mRatingMovieDao.insertAll(
                            movies.stream().map(value -> new RatingMovie(value.id)).collect(Collectors.toList())));
                } else {
                    AppExecutors.diskIO().execute(() -> mPopularMovieDao.insertAll(
                            movies.stream().map(value -> new PopularMovie(value.id)).collect(Collectors.toList())));
                }
            }

            @Override
            public void onFailure(String reason) {
                Log.e(MovieSyncAdapter.class.getSimpleName(), reason);
            }
        });
    }

}
