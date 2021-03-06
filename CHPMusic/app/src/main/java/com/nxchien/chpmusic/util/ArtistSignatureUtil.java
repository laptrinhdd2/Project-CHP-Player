package com.nxchien.chpmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.bumptech.glide.signature.ObjectKey;

public class ArtistSignatureUtil {
    private static final String ARTIST_SIGNATURE_PREFS = "artist_signatures";

    private static ArtistSignatureUtil sInstance;

    private final SharedPreferences mPreferences;

    private ArtistSignatureUtil(@NonNull final Context context) {
        mPreferences = context.getSharedPreferences(ARTIST_SIGNATURE_PREFS, Context.MODE_PRIVATE);
    }

    public static ArtistSignatureUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new ArtistSignatureUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public long getArtistSignatureRaw(String artistName) {
        return mPreferences.getLong(artistName, 0);
    }

    public ObjectKey getArtistSignature(String artistName) {
        return new ObjectKey(String.valueOf(getArtistSignatureRaw(artistName)));
    }
}
