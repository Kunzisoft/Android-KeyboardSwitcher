package com.kunzisoft.keyboard.switcher.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.kunzisoft.keyboard.switcher.R;
import com.kunzisoft.keyboard.switcher.boot.BootUpActivity;
import com.kunzisoft.keyboard.switcher.dialogs.AppDialog;
import com.kunzisoft.keyboard.switcher.dialogs.WarningFloatingButtonDialog;

public class PreferenceActivity extends AppCompatActivity implements WarningFloatingButtonDialog.OnFloatingButtonListener{

    private static final String TAG_PREFERENCE_FRAGMENT = "TAG_PREFERENCE_FRAGMENT";
    private static final String TAG_ABOUT_FRAGMENT = "TAG_ABOUT_FRAGMENT";

    private static final String KEY_ABOUT_ACTIVE = "KEY_ABOUT_ACTIVE";

    private View testZoneView;
    private PreferenceFragment preferenceFragment;
    private Fragment aboutFragment;
    private boolean aboutFragmentActive = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_activity);

        testZoneView = findViewById(R.id.test_zone_container);

        // Manage fragment who contains list of preferences
        preferenceFragment = (PreferenceFragment) getSupportFragmentManager().findFragmentByTag(TAG_PREFERENCE_FRAGMENT);
        if(preferenceFragment == null)
            preferenceFragment = new PreferenceFragment();

        aboutFragment = getSupportFragmentManager().findFragmentByTag(TAG_ABOUT_FRAGMENT);
        if(aboutFragment == null)
            aboutFragment =  new AboutFragment();

        if (savedInstanceState != null) {
            aboutFragmentActive = savedInstanceState.getBoolean(KEY_ABOUT_ACTIVE, aboutFragmentActive);
        }

        startActivity(new Intent(this, BootUpActivity.class));

        Fragment fragmentToShow;
        String tagToSave;
        if (aboutFragmentActive) {
            fragmentToShow = aboutFragment;
            tagToSave = TAG_ABOUT_FRAGMENT;
        } else {
            fragmentToShow = preferenceFragment;
            tagToSave = TAG_PREFERENCE_FRAGMENT;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragmentToShow, tagToSave)
                .commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.app_warning_key), true)) {
            AppDialog dialogFragment = new AppDialog();
            if (getSupportFragmentManager() != null)
                dialogFragment.show(getSupportFragmentManager(), "application_dialog");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_ABOUT_ACTIVE, aboutFragmentActive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contribution, menu);

        return true;
    }

    private void switchToPreferenceScreen() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        getSupportFragmentManager().popBackStack();
        aboutFragmentActive = false;
        testZoneView.setVisibility(View.VISIBLE);
    }

    private void switchToAboutScreen() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.about_title);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, aboutFragment, TAG_ABOUT_FRAGMENT)
                .addToBackStack(null)
                .commit();
        aboutFragmentActive = true;
        testZoneView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contribute:
                if (aboutFragmentActive) {
                    switchToPreferenceScreen();
                } else {
                    switchToAboutScreen();
                }
            return true;
            case android.R.id.home:
                switchToPreferenceScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFloatingButtonDialogPositiveButtonClick() {
        if (preferenceFragment != null)
        	preferenceFragment.startFloatingButtonAndCheckButton();
    }

    @Override
    public void onFloatingButtonDialogNegativeButtonClick() {
		if (preferenceFragment != null)
        	preferenceFragment.stopFloatingButtonAndUncheckedButton();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // To avoid flickering and open time
        if (preferenceFragment != null && !preferenceFragment.isTryingToOpenExternalDialog())
            finish();
    }

    @Override
    public void onBackPressed() {
        if (aboutFragmentActive) {
            switchToPreferenceScreen();
        } else {
            super.onBackPressed();
        }
    }
}
