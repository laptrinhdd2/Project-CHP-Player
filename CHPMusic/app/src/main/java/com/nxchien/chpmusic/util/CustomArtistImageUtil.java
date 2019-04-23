package com.nxchien.chpmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.nxchien.chpmusic.App;
import com.nxchien.chpmusic.model.Artist;

import java.io.File;
import java.util.Locale;

public class CustomArtistImageUtil {
    private static final String CUSTOM_ARTIST_IMAGE_PREFS = "custom_artist_image";
    private static final String FOLDER_NAME = "/custom_artist_images/";

    private static CustomArtistImageUtil sInstance;

    private final SharedPreferences mPreferences;

    private CustomArtistImageUtil(@NonNull final Context context) {
        mPreferences = context.getApplicationContext().getSharedPreferences(CUSTOM_ARTIST_IMAGE_PREFS, Context.MODE_PRIVATE);
    }

    public static CustomArtistImageUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new CustomArtistImageUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean hasCustomArtistImage(Artist artist) {
        return mPreferences.getBoolean(getFileName(artist), false);
    }

    private static String getFileName(Artist artist) {
        String artistName = artist.getName();
        if (artistName == null)
            artistName = "";
        // replace everything that is not a letter or a number with _
        artistName = artistName.replaceAll("[^a-zA-Z0-9]", "_");
        return String.format(Locale.US, "#%d#%s.jpeg", artist.id, artistName);
    }

    public static File getFile(Artist artist) {
        File dir = new File(App.getInstance().getFilesDir(), FOLDER_NAME);
        return new File(dir, getFileName(artist));
    }
}
