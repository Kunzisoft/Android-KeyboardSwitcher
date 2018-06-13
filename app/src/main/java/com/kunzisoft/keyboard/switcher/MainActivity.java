package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /* https://stackoverflow.com/questions/7569937/unable-to-add-window-android-view-viewrootw44da9bc0-permission-denied-for-t
        code to post/handler request for permission
     */
    public final static int REQUEST_CODE = 6517;

    private Intent service;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new Intent(this, OverlayShowingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        } else {
            startServiceAndFinishActivity();
        }

    }

    private void startServiceAndFinishActivity() {
        startService(service);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            /* if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /* request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            startServiceAndFinishActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /* check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)
                        && service != null) {
                    startServiceAndFinishActivity();
                }
            }
        }
    }
}
