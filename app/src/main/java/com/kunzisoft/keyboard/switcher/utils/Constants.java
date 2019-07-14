package com.kunzisoft.keyboard.switcher.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Constants {

    public static final String ORGANIZATION = "Kunzisoft";
    public static final String URL_WEB_SITE = "http://kunzisoft.com/";
    public static final String URL_CONTRIBUTION = "https://gitlab.com/kunzisoft/Android-KeyboardSwitcher";

    /**
     * Get the current package version.
     *
     * @return The current version.
     */
    public static String getVersion(Context context) {
        String result;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            result = String.format("%s", info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Constants.class.getSimpleName(), "Unable to get application version", e);
            result = "Unable to get application version.";
        }

        return result;
    }
}
