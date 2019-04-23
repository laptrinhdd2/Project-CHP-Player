package com.nxchien.chpmusic.glide.artistimage;

public class ArtistImage {
    public final String mArtistName;

    public String getArtistName() {
        return mArtistName;
    }

    public final boolean mSkipOkHttpCache;

    public ArtistImage(String artistName, boolean skipOkHttpCache) {
        this.mArtistName = artistName;
        this.mSkipOkHttpCache = skipOkHttpCache;
    }
}
