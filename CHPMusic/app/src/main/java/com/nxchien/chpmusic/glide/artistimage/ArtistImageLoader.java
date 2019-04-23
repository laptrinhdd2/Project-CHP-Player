package com.nxchien.chpmusic.glide.artistimage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ArtistImageLoader implements ModelLoader<ArtistImage, InputStream> {
    private static final int TIMEOUT = 500;

    private ModelLoader<GlideUrl, InputStream> urlLoader;

    public ArtistImageLoader( ModelLoader<GlideUrl, InputStream> urlLoader) {
        this.urlLoader = urlLoader;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull ArtistImage artistImage, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(String.valueOf(artistImage.getArtistName())),new ArtistImageFetcher(artistImage,urlLoader,width,height,options));
    }

    @Override
    public boolean handles(@NonNull ArtistImage artistImage) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ArtistImage, InputStream> {

        private OkHttpUrlLoader.Factory okHttpFactory;

        public Factory() {
            okHttpFactory = new OkHttpUrlLoader.Factory(new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .build());
                    }

        @NonNull
        @Override
        public ModelLoader<ArtistImage, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ArtistImageLoader(okHttpFactory.build(multiFactory));
        }

        @Override
        public void teardown() {
            okHttpFactory.teardown();
        }
    }
}

