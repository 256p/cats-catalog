package com.bidstack.cat_gallery.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

public class PreferencesHelper {

    private static final String PREF_VOTES_COUNT = "PREF_VOTES_COUNT";

    private SharedPreferences prefs;

    @Inject
    public PreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setVotesCount(int votesCount) {
        prefs.edit().putInt(PREF_VOTES_COUNT, votesCount).apply();
    }

    public int getVotesCount() {
        return prefs.getInt(PREF_VOTES_COUNT, 3);
    }
}
