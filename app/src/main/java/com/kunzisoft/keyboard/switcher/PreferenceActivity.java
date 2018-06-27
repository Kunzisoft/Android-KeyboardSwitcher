package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kunzisoft.keyboard.switcher.dialog.WarningFloatingButtonDialog;

public class PreferenceActivity extends AppCompatActivity implements WarningFloatingButtonDialog.OnFloatingButtonListener{

    private PreferenceFragment preferenceFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preference_activity);

        preferenceFragment = new PreferenceFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, preferenceFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contribution, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_contribute:
                String url = "https://www.kunzisoft.com/donation";
                Intent intentUrl = new Intent(Intent.ACTION_VIEW);
                intentUrl.setData(Uri.parse(url));
                startActivity(intentUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPositiveButtonClick() {
        if (preferenceFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                preferenceFragment.checkDrawOverlayPermission();
            } else {
                preferenceFragment.startFloatingButtonService();
            }
        }
    }

    @Override
    public void onNegativeButtonClick() {
        preferenceFragment.stopFloatingButtonService();
    }
}
