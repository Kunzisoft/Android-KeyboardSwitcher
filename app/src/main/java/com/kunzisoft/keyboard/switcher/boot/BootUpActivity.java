package com.kunzisoft.keyboard.switcher.boot;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.kunzisoft.keyboard.switcher.NotificationBuilder;
import com.kunzisoft.keyboard.switcher.OverlayShowingService;
import com.kunzisoft.keyboard.switcher.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Utility class to show keyboard button at startup
 */
public class BootUpActivity extends AppCompatActivity{

	private void stopFloatingButtonService() {
		stopService(new Intent(this, OverlayShowingService.class));
	}

    private void startFloatingButtonService() {
		startService(new Intent(this, OverlayShowingService.class));
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(getString(R.string.settings_notification_key), false)) {
			NotificationBuilder notificationBuilder =
					new NotificationBuilder((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
			notificationBuilder.createKeyboardNotification(this);
        }

		stopFloatingButtonService();
        if (preferences.getBoolean(getString(R.string.settings_floating_button_key), false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (Settings.canDrawOverlays(getApplicationContext())) {
					startFloatingButtonService();
				}
            } else {
                startFloatingButtonService();
            }
        }

        finish();
    }
}
