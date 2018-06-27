package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

/**
 * Utility class to show keyboard button at startup
 */
public class BootUpActivity extends AppCompatActivity{

    private Intent floatingButtonService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(getString(R.string.settings_floating_button_key), false)) {
            floatingButtonService = new Intent(this, OverlayShowingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkDrawOverlayPermission();
            } else {
                startService(floatingButtonService);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /* Do nothing here if not permitted */
        if (Settings.canDrawOverlays(getApplicationContext())) {
            startService(floatingButtonService);
        }
        finish();
    }
}
