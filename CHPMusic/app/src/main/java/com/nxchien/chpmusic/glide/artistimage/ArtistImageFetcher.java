package com.nxchien.chpmusic.glide.artistimage;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import com.nxchien.chpmusic.util.MusicUtil;

import java.io.InputStream;

public class ArtistImageFetcher implements DataFetcher<InputStream> {
    private final ArtistImage model;
    private ModelLoader<GlideUrl, InputStream> urlLoader;
    int width;
    int height;
    boolean isCancelled;
    DataFetcher<InputStream> urlFetcher;
    Options mOption;

    public ArtistImageFetcher(ArtistImage model, ModelLoader<GlideUrl, InputStream> urlLoader, int width, int height, Options options) {
        this.model = model;
        this.urlLoader = urlLoader;
        this.width = width;
        this.height = height;
        mOption = options;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        if (!MusicUtil.isArtistNameUnknown(model.mArtistName)) {
            String artistNames = model.getArtistName();
            artistNames = artistNames
                    .replace(" ft ", " & ")
                    .replace(";", " & ")
                    .replace(",", " & ")
                    .replaceAll("( +)", " ").trim();
            Exception e = null;
            String[] artists = artistNames.split("&");

            if (artists.length == 0) {
                callback.onLoadFailed(new NullPointerException("Artist's empty"));
            }
            for (String artistName : artists) {
                if (artistName.isEmpty()) {
                    e = new Exception("Empty Artist");
                    continue;
                }
                //e = loadThisArtist(artistName.trim(), priority, callback);
                if (e == null) break;
            }
            if (e != null) callback.onLoadFailed(e);
        } else callback.onLoadFailed(new Exception("Unknown Artist"));
    }

    @Override
    public void cleanup() {
        if (urlFetcher != null) {
            urlFetcher.cleanup();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
        if (urlFetcher != null) {
            urlFetcher.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
