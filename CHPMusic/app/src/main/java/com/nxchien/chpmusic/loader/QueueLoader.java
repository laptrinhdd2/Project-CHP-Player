package com.nxchien.chpmusic.loader;

import android.content.Context;


import com.nxchien.chpmusic.model.Song;

import java.util.ArrayList;
import java.util.List;


public class QueueLoader {


    private static NowPlayingCursor mCursor;

    public static List<Song> getQueueSongs(Context context) {

        mCursor = new NowPlayingCursor(context);

        final ArrayList<Song> mSongList = new ArrayList<>(SongLoader.getSongsForCursor(mCursor));
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

}
