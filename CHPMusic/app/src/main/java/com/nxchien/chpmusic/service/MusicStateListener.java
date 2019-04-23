package com.nxchien.chpmusic.service;

public interface MusicStateListener {

    void restartLoader();

    void onPlaylistChanged();

    void onMetaChanged();
}
