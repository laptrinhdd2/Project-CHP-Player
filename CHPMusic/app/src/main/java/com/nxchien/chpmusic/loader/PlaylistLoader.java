package com.nxchien.chpmusic.loader;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.PlaylistsColumns;


import com.nxchien.chpmusic.model.Playlist;
import com.nxchien.chpmusic.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {

    static ArrayList<Playlist> mPlaylistList;
    private static Cursor mCursor;

    public static List<Playlist> getPlaylists(Context context, boolean defaultIncluded) {

        mPlaylistList = new ArrayList<>();

        if (defaultIncluded)
            makeDefaultPlaylists(context);

        mCursor = makePlaylistCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(0);

                final String name = mCursor.getString(1);

                final int songCount = Utils.getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                mPlaylistList.add(playlist);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mPlaylistList;
    }

    private static void makeDefaultPlaylists(Context context) {
        final Resources resources = context.getResources();

        final Playlist lastAdded = new Playlist(Utils.PlaylistType.LastAdded.mId,
                resources.getString(Utils.PlaylistType.LastAdded.mTitleId), -1);
        mPlaylistList.add(lastAdded);

        final Playlist recentlyPlayed = new Playlist(Utils.PlaylistType.RecentlyPlayed.mId,
                resources.getString(Utils.PlaylistType.RecentlyPlayed.mTitleId), -1);
        mPlaylistList.add(recentlyPlayed);

        final Playlist topTracks = new Playlist(Utils.PlaylistType.TopTracks.mId,
                resources.getString(Utils.PlaylistType.TopTracks.mTitleId), -1);
        mPlaylistList.add(topTracks);
    }


    public static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }
}

