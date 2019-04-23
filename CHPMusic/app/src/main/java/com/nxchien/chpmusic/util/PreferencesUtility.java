package com.nxchien.chpmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";

    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector";
    public static final String LAST_ADDED_CUTOFF = "last_added_cutoff";

    private static final String SHOW_LOCKSCREEN_ALBUMART = "show_albumart_lockscreen";
    private static final String ARTIST_IMAGE = "artist_image";
    private static final String ARTIST_IMAGE_MOBILE = "artist_image_mobile";
    private static final String SONG_CHILD_SORT_ORDER = "song_child_sort_order";

    private static PreferencesUtility sInstance;

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    private SharedPreferences mPreferences;

    public SharedPreferences getSharePreferences() {
        return mPreferences;
    }

    private static Context context;

    public PreferencesUtility(final Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public final int getSongChildSortOrder() {
        return mPreferences.getInt(SONG_CHILD_SORT_ORDER, 0);
    }

    public final void setSongChildSortOrder(int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SONG_CHILD_SORT_ORDER, value);
        editor.apply();
    }

    public boolean getXPosedTrackselectorEnabled() {
        return mPreferences.getBoolean(TOGGLE_XPOSED_TRACKSELECTOR, false);
    }

    public long getLastAddedCutoff() {
        return mPreferences.getLong(LAST_ADDED_CUTOFF, 0L);
    }

    public boolean getSetAlbumartLockscreen() {
        return mPreferences.getBoolean(SHOW_LOCKSCREEN_ALBUMART, true);
    }

    public boolean loadArtistImages() {
        if (mPreferences.getBoolean(ARTIST_IMAGE, true)) {
            return true;
        }
        return false;
    }

}

