package com.kunzisoft.keyboard.switcher.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kunzisoft.keyboard.switcher.R;
import com.kunzisoft.keyboard.switcher.boot.BootUpActivity;
import com.kunzisoft.keyboard.switcher.dialogs.AppDialog;
import com.kunzisoft.keyboard.switcher.dialogs.WarningFloatingButtonDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class PreferenceActivity extends AppCompatActivity implements WarningFloatingButtonDialog.OnFloatingButtonListener{

    private static final String TAG_PREFERENCE_FRAGMENT = "TAG_PREFERENCE_FRAGMENT";
    private PreferenceFragment preferenceFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_activity);
        // Manage fragment who contains list of preferences
        preferenceFragment =
                (PreferenceFragment) getSupportFragmentManager().findFragmentByTag(TAG_PREFERENCE_FRAGMENT);

        if(preferenceFragment == null)
            preferenceFragment = new PreferenceFragment();

        startActivity(new Intent(this, BootUpActivity.class));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, preferenceFragment, TAG_PREFERENCE_FRAGMENT)
                .commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.app_warning_key), true)) {
            AppDialog dialogFragment = new AppDialog();
            if (getSupportFragmentManager() != null)
                dialogFragment.show(getSupportFragmentManager(), "application_dialog");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contribution, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_contribute) {
            String url = "https://www.kunzisoft.com/donation";
            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
            intentUrl.setData(Uri.parse(url));
            startActivity(intentUrl);
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
}
