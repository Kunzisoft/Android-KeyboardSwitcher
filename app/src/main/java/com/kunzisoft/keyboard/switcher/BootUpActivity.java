package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

/**
 * Utility class to show keyboard button at startup
 */
public class BootUpActivity extends AppCompatActivity{

    private Intent service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new Intent(this, OverlayShowingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        } else {
            startService();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /* Do nothing here if not permitted */
        if (Settings.canDrawOverlays(getApplicationContext())) {
            startService();
        } else {
            finish();
        }
    }

    private void startService() {
        startService(service);
        finish();
    }
}
