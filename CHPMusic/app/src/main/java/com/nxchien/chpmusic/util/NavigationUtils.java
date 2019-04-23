package com.nxchien.chpmusic.util;

import android.content.Context;
import android.content.Intent;

import com.nxchien.chpmusic.ui.MainActivity;

public class NavigationUtils {

    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("navigate_nowplaying");
        return intent;
    }
}
