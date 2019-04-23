package com.nxchien.chpmusic.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;


import com.nxchien.chpmusic.model.Song;
import com.nxchien.chpmusic.util.PreferencesUtility;
import com.nxchien.chpmusic.util.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class LastAddedLoader {

    private static Cursor mCursor;
    public static List<Song> getLastAddedSongs(Context context) {
        return getLastAddedSongs(context, SortOrder.SongSortOrder.SONG_DATE);
    }

    public static List<Song> getLastAddedSongs(Context context, String sortOrder) {

        mCursor = makeLastAddedCursor(context, sortOrder);
        ArrayList<Song> mSongList = SongLoader.getSongsForCursor(mCursor);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    public static final Cursor makeLastAddedCursor(final Context context, String sortOrder) {
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        long cutoff = PreferencesUtility.getInstance(context).getLastAddedCutoff();
        // use the most recent of the two timestamps
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        final StringBuilder selection = new StringBuilder();
        selection.append(AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + ">");
        selection.append(cutoff);

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                SongLoader.BASE_PROJECTION, selection.toString(), null, sortOrder);
    }
}
