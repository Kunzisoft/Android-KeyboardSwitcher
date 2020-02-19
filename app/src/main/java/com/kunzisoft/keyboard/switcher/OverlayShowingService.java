package com.kunzisoft.keyboard.switcher;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.kunzisoft.keyboard.switcher.utils.Utilities;

import androidx.annotation.ColorRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.kunzisoft.keyboard.switcher.NotificationBuilder.CHANNEL_ID_KEYBOARD;

public class OverlayShowingService extends Service implements OnTouchListener, OnClickListener {

    private SharedPreferences preferences;
    private static final String Y_POSITION_PREFERENCE_KEY = "Y_POSITION_PREFERENCE_KEY";
    private static final String X_POSITION_PREFERENCE_KEY = "X_POSITION_PREFERENCE_KEY";
    private int xPositionToSave;
    private int yPositionToSave;

    private View topLeftView;

    private ImageView overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager windowManager;

    private boolean lockedButton;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // To keep the service on top
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_KEYBOARD)
                    .setSmallIcon(R.drawable.ic_notification_button_white_24dp)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(getString(R.string.notification_floating_button_title))
                    .setContentText(getString(R.string.notification_floating_button_content_text))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET);
            startForeground(56, builder.build());
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(getString(R.string.settings_floating_button_key), false)) {

            // check Button Position
            lockedButton = preferences.getBoolean(getString(R.string.settings_floating_button_lock_key), false);

            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            overlayedButton = new ImageView(this);
            @ColorRes int color = preferences.getInt(getString(R.string.settings_colors_key), ContextCompat.getColor(this, R.color.colorPrimary));
            overlayedButton.setImageResource(R.drawable.ic_keyboard_white_32dp);
            overlayedButton.setColorFilter(color);
            overlayedButton.setAlpha((color >> 24) & 0xff);
            overlayedButton.setOnTouchListener(this);
            overlayedButton.setOnClickListener(this);

            int typeFilter = LayoutParams.TYPE_SYSTEM_ALERT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeFilter = LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            LayoutParams overlayedButtonParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            typeFilter,
                            LayoutParams.FLAG_NOT_FOCUSABLE
                                    | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                            PixelFormat.TRANSLUCENT);

            overlayedButtonParams.x = 0;
            overlayedButtonParams.y = 0;
            if (preferences.contains(X_POSITION_PREFERENCE_KEY)) {
                xPositionToSave = preferences.getInt(X_POSITION_PREFERENCE_KEY, overlayedButtonParams.y);
                overlayedButtonParams.x = xPositionToSave;
            }
            if (preferences.contains(Y_POSITION_PREFERENCE_KEY)) {
                yPositionToSave = preferences.getInt(Y_POSITION_PREFERENCE_KEY, overlayedButtonParams.x);
                overlayedButtonParams.y = yPositionToSave;
            }
            windowManager.addView(overlayedButton, overlayedButtonParams);

            topLeftView = new View(this);
            LayoutParams topLeftParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            typeFilter,
                            LayoutParams.FLAG_NOT_FOCUSABLE
                                    | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                            PixelFormat.TRANSLUCENT);
            topLeftParams.x = 0;
            topLeftParams.y = 0;
            topLeftParams.width = 0;
            topLeftParams.height = 0;
            windowManager.addView(topLeftView, topLeftParams);
        }
    }

    private void getPositionOnScreen(MotionEvent event) {
        int[] location = new int[2];
        if (overlayedButton != null)
            overlayedButton.getLocationOnScreen(location);

        originalXPos = (int) (location[0] + event.getX());
        originalYPos = (int) (location[1] + event.getY());
    }

    private void savePreferencePosition() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(X_POSITION_PREFERENCE_KEY, xPositionToSave);
        editor.putInt(Y_POSITION_PREFERENCE_KEY, yPositionToSave);
        editor.apply();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {

    	// Consume the touch and click if the button is locked
        if (lockedButton) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
                view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                onClick(view);
            }
			return true;
		}

		float x = event.getRawX();
		float y = event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moving = false;

            getPositionOnScreen(event);

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            WindowManager.LayoutParams params = (LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            int deltaMoveX = view.getMeasuredWidth() * 2/3;
            int deltaMoveY = view.getMeasuredHeight() * 2/3;

            if (Math.abs(newX - originalXPos) < deltaMoveX
                    && Math.abs(newY - originalYPos) < deltaMoveY
                    && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);
            xPositionToSave = params.x;
            yPositionToSave = params.y;

            windowManager.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            savePreferencePosition();
            return moving;
        }

        return false;
    }

    @Override
    public void onClick(final View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(this, KeyboardManagerActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Utilities.chooseAKeyboard(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (overlayedButton != null) {
            savePreferencePosition();
            windowManager.removeView(overlayedButton);
            windowManager.removeView(topLeftView);
            overlayedButton = null;
            topLeftView = null;
        }
    }
}
