package com.example.leo.movie;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.example.leo.movie.syncAdapter.MovieSyncAdapter;

/**
 * Created by Leo on 05/11/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_SORT_ORDER = "pref_sortOrder";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            if (findPreference(KEY_PREF_SORT_ORDER) != null) {
                Preference sortOrderPref = findPreference(KEY_PREF_SORT_ORDER);
                sortOrderPref.setSummary(getPreferenceManager().getSharedPreferences()
                        .getString(KEY_PREF_SORT_ORDER, getString(R.string.pref_sort_by_popularity)));
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(KEY_PREF_SORT_ORDER)) {
                if (findPreference(KEY_PREF_SORT_ORDER) != null) {
                    findPreference(KEY_PREF_SORT_ORDER).setSummary(
                            sharedPreferences.getString(KEY_PREF_SORT_ORDER,
                                    getString(R.string.pref_sort_by_popularity)));

                    MovieSyncAdapter.syncImmediately(getActivity());
                }
            }
        }
    }
}
