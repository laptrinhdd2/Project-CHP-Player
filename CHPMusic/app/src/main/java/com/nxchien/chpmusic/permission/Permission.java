package com.nxchien.chpmusic.permission;

import android.content.Context;
import android.content.pm.PackageManager;

public class Permission {

    private static Context context;

    public static void init(Context context) {
        Permission.context = context;
    }

    public static boolean checkPermission(String permissionName) {
        if (context == null) {
            throw new RuntimeException("Call Permission.init(context) first");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }
}