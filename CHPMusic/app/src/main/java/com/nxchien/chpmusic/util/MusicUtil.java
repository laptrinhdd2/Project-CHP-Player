package com.nxchien.chpmusic.util;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.nxchien.chpmusic.model.Artist;

public class MusicUtil {

    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static boolean isArtistNameUnknown(@Nullable String artistName) {
        if (TextUtils.isEmpty(artistName)) return false;
        if (artistName.equals(Artist.UNKNOWN_ARTIST_DISPLAY_NAME)) return true;
        artistName = artistName.trim().toLowerCase();
        return artistName.equals("unknown") || artistName.equals("<unknown>");
    }
}
