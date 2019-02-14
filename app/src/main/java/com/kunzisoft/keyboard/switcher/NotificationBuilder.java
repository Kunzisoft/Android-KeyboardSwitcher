package com.kunzisoft.keyboard.switcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.kunzisoft.keyboard.switcher.utils.Utilities;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationBuilder {

	private static final String CHANNEL_ID_KEYBOARD = "com.kunzisoft.keyboard.notification.channel";
	private static final String CHANNEL_NAME_KEYBOARD = "Keyboard switcher notification";

	private NotificationManager mNotificationManager;
	private int notificationId = 45;

	public NotificationBuilder(NotificationManager notificationManager) {
		this.mNotificationManager = notificationManager;

		// Create notification channel for Oreo+
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID_KEYBOARD,
					CHANNEL_NAME_KEYBOARD,
					NotificationManager.IMPORTANCE_LOW);
			notificationManager.createNotificationChannel(channel);
		}
	}

	public void createKeyboardNotification(Context context) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_KEYBOARD)
				.setSmallIcon(R.drawable.ic_notification_white_24dp)
				.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
				.setContentTitle(context.getString(R.string.notification_title))
				.setAutoCancel(false)
				.setOngoing(true)
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.setVisibility(NotificationCompat.VISIBILITY_SECRET)
				.setContentText(context.getString(R.string.notification_content_text))
				.setContentIntent(Utilities.getPendingIntent(context, 500L)); // Trick 500ms delay to show th dialog

		mNotificationManager.cancel(notificationId);
		mNotificationManager.notify(notificationId, builder.build());
	}

	public void cancelKeyboardNotification() {
		mNotificationManager.cancel(notificationId);
	}
}
