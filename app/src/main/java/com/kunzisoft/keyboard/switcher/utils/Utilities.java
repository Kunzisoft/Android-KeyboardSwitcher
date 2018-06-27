package com.kunzisoft.keyboard.switcher.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Utilities {

    public static void openAvailableKeyboards(@Nullable Context context) {
        if (context != null) {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
}
