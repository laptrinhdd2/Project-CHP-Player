package com.nxchien.chpmusic.model;

import com.nxchien.chpmusic.util.MusicUtil;

public class Artist {
    public static final String UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown Artist";

    public final int albumCount;
    public final long id;

    public String getName() {
            if (MusicUtil.isArtistNameUnknown(name)) {
                return UNKNOWN_ARTIST_DISPLAY_NAME;
            }
            return name;
    }

    private final String name;
    public final int songCount;

    public Artist() {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        this.albumCount = -1;
    }

    public Artist(long _id, String _name, int _albumCount, int _songCount) {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
        this.albumCount = _albumCount;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Artist && ((Artist) obj).getName().equals(this.getName());
    }
}
