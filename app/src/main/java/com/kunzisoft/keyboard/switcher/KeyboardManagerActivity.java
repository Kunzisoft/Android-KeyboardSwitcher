package com.kunzisoft.keyboard.switcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Activity to show manager on Marshmallow
 */
public class KeyboardManagerActivity extends AppCompatActivity {

    private View rootView;
    private AppCompatDialog dialogUtility;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);
        rootView = findViewById(R.id.root_view);

        // Only to show input method picker
        dialogUtility = new AppCompatDialog(this, android.R.style.Theme_Panel);
        if (dialogUtility.getWindow() != null) {
            dialogUtility.getWindow().setTitle("");
        }
        dialogUtility.setCanceledOnTouchOutside(true);
        dialogUtility.setCancelable(true);
        dialogUtility.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();
                }
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        if (dialogUtility != null)
            dialogUtility.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Close the back activity
        finish();
    }
}
