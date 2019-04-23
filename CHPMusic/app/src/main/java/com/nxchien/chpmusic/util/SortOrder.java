package com.nxchien.chpmusic.util;

import android.provider.MediaStore;

public final class SortOrder {

    public SortOrder() {
    }

    public interface ArtistSortOrder {

        String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
    }
    /**
     * Song sort order entries.
     */
    public interface SongSortOrder {
        /* Song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Song sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* Song sort order artist */
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
        String SONG_ARTIST_DESC = MediaStore.Audio.Media.ARTIST +" DESC";

        /* Song sort order date */
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        String SONG_DATE_DESC = MediaStore.Audio.Media.DATE_ADDED;
    }


    /**
     * Genre sort order entries.
     */
    public interface GenreSortOrder {
        /* Genre sort order A-Z */
        String GENRE_A_Z = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER;

    }

}
