package com.kunzisoft.keyboard.switcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Broadcast receiver for "action boot completed"
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getAction();
        if (preferences.getBoolean(context.getString(R.string.settings_launch_startup_key), true)
                && action != null
                && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // To show the button, else bug in new android version
            Intent intentSetting = new Intent(context, BootUpActivity.class);
            intentSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentSetting);
        }
    }
}