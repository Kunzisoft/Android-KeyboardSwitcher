package com.kunzisoft.keyboard.switcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.kunzisoft.keyboard.switcher.utils.Utilities;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

public class KeyboardNotificationService extends Service {

    private static final String CHANNEL_ID_KEYBOARD = "com.kunzisoft.keyboard.notification.channel";
    private static final String CHANNEL_NAME_KEYBOARD = "Keyboard switcher notification";

    private NotificationManager notificationManager;
    private int notificationId = 1;

    public KeyboardNotificationService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create notification channel for Oreo+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_KEYBOARD,
                    CHANNEL_NAME_KEYBOARD,
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.w(TAG, "null intent");

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_KEYBOARD)
                    .setSmallIcon(R.drawable.ic_notification_white_24dp)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(getString(R.string.notification_title))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setContentText(getString(R.string.notification_content_text))
                    .setContentIntent(Utilities.getPendingIntent(this));

            notificationManager.cancel(notificationId);
            notificationManager.notify(notificationId, builder.build());

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(notificationId);
    }
}
