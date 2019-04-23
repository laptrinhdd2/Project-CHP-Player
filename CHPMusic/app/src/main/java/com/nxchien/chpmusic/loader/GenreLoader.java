package com.nxchien.chpmusic.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Genres;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nxchien.chpmusic.model.Genre;
import com.nxchien.chpmusic.model.Song;
import com.nxchien.chpmusic.util.SortOrder;

import java.util.ArrayList;

public class GenreLoader {

    @NonNull
    public static ArrayList<Song> getSongs(@NonNull final Context context, final int genreId) {
        return SongLoader.getSongsForCursor(makeGenreSongCursor(context, genreId));
    }

    public  static ArrayList<Genre> getGenreForArtist(Context context, @NonNull long artistID) {
        Cursor cursor = GenreLoader.doSomething(context, artistID);
        return GenreLoader.getGenresFromCursor(context,cursor);
    }

    @NonNull
    public static ArrayList<Genre> getGenresFromCursor(@NonNull final Context context, @Nullable final Cursor cursor) {
        final ArrayList<Genre> genres = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Genre genre = getGenreFromCursor(context, cursor);
                    if (genre.songCount > 0) {
                        genres.add(genre);
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            context.getContentResolver().delete(Genres.EXTERNAL_CONTENT_URI, Genres._ID + " == " + genre.id, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // nothing we can do then
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return genres;
    }

    @NonNull
    private static Genre getGenreFromCursor(@NonNull final Context context, @NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        final int songs = getSongs(context, id).size();
        return new Genre(id, name, songs);
    }



    public static Cursor doSomething(final  Context context, long artistId) {
        String query = " _id in (select genre_id from audio_genres_map where audio_id in (select _id from audio_meta where "+SongLoader.BASE_SELECTION+" AND artist_id = "+artistId+"))" ;
        final String[] projection = new String[]{
                Genres._ID,
                Genres.NAME
        };
        try {
            return context.getContentResolver().query(
                    Genres.EXTERNAL_CONTENT_URI,
                    projection, query, null,SortOrder.GenreSortOrder.GENRE_A_Z/*PreferenceUtil.getInstance(context).getGenreSortOrder()*/);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    private static Cursor makeGenreSongCursor(@NonNull final Context context, int genreId) {
        try {
            return context.getContentResolver().query(
                    Genres.Members.getContentUri("external", genreId),
                    SongLoader.BASE_PROJECTION, SongLoader.BASE_SELECTION, null, SortOrder.SongSortOrder.SONG_A_Z/* PreferenceUtil.getInstance(context).getSongSortOrder()*/);
        } catch (SecurityException e) {
            return null;
        }
    }
}
