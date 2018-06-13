package com.kunzisoft.keyboard.switcher;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class OverlayShowingService extends Service implements OnTouchListener, OnClickListener {

    private boolean right;

    private View topLeftView;

    private ImageView overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        right = true;

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new ImageView(this);
        if (right)
            overlayedButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_right_36dp));
        else
            overlayedButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_left_36dp));
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setOnClickListener(this);

        int typeFilter = LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeFilter = LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        typeFilter,
                        LayoutParams.FLAG_NOT_FOCUSABLE
                                | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT);
        if (right)
            params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        else
            params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;

        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);

        topLeftView = new View(this);
        LayoutParams topLeftParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        typeFilter,
                        LayoutParams.FLAG_NOT_FOCUSABLE
                                | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT);
        if (right)
            topLeftParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        else
            topLeftParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(topLeftView, topLeftParams);
    }

    @Override
    public void onDestroy() {
	    super.onDestroy();

        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            wm.removeView(topLeftView);
            overlayedButton = null;
            topLeftView = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return moving;
        }

        return false;
    }

    @Override
    public void onClick(final View view) {
        startActivity(new Intent(this, KeyboardManagerActivity.class));

        /*
        InputMethodManager imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
        //*/

        /*
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //*/
    }
}
