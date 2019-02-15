package com.kunzisoft.keyboard.switcher.utils;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import com.kunzisoft.keyboard.switcher.KeyboardManagerActivity;
import com.kunzisoft.keyboard.switcher.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Utilities {

    public static void openAvailableKeyboards(@Nullable Context context) {
        if (context != null) {
            try {
                Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                new AlertDialog.Builder(context)
                        .setMessage(R.string.error_unavailable_keyboard_feature)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        }).create().show();
            }
        }
    }

    public static void chooseAKeyboard(@Nullable Context context) {
        if (context != null) {
            InputMethodManager imeManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imeManager != null) {
                imeManager.showInputMethodPicker();
            }
        }
    }

	public static PendingIntent getPendingIntent(Context context) {
		return getPendingIntent(context, null);
	}

    public static PendingIntent getPendingIntent(Context context, @Nullable Long delay) {
        Intent chooserIntent = new Intent(context, KeyboardManagerActivity.class);
        chooserIntent.setAction(Intent.ACTION_MAIN);
        chooserIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (delay != null)
        	chooserIntent.putExtra(KeyboardManagerActivity.DELAY_SHOW_KEY, delay);
        return PendingIntent.getActivity(
                context, 0, chooserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
