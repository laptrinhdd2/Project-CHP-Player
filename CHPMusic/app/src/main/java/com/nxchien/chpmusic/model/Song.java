package com.nxchien.chpmusic.model;

public class Song {

    public final long id;
    public final String title;
    public final int trackNumber;
    public final int year;
    public final int duration;
    public final String data;
    public final long dateModified;
    public final long albumId;
    public final String albumName;
    public final long artistId;
    public final String artistName;

    public Song() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.year = -1;
        this.dateModified = -1;
        this.trackNumber = -1;
        this.data ="";
    }

    public Song(long id, String title, int trackNumber, int year, int duration, String data, long dateModified, long albumId, String albumName, long artistId, String artistName) {
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.data = data;
        this.dateModified = dateModified;
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    @Override
    public boolean equals( Object obj) {
        if(obj instanceof Song) {
           return ((Song)obj).id == this.id;
        } else if(obj instanceof Integer) {
            return ((Integer)obj) == this.id;
        } else if(obj instanceof Long) {
            return ((Long)obj) == this.id;
        }
        return false;
    }
}
