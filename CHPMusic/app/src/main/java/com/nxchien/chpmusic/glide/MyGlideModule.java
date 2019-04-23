package com.nxchien.chpmusic.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.nxchien.chpmusic.glide.artistimage.ArtistImage;
import com.nxchien.chpmusic.glide.artistimage.ArtistImageLoader;
import com.nxchien.chpmusic.glide.audiocover.AudioFileCover;
import com.nxchien.chpmusic.glide.audiocover.AudioFileCoverLoader;


import java.io.InputStream;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(AudioFileCover.class,InputStream.class,new AudioFileCoverLoader.Factory());
        registry.append(ArtistImage.class,InputStream.class, new ArtistImageLoader.Factory());

    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
