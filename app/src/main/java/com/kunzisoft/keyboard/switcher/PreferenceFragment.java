package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;

import com.kunzisoft.androidclearchroma.ChromaPreferenceFragmentCompat;
import com.kunzisoft.keyboard.switcher.dialogs.WarningFloatingButtonDialog;
import com.kunzisoft.keyboard.switcher.utils.Utilities;

public class PreferenceFragment extends ChromaPreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    /* https://stackoverflow.com/questions/7569937/unable-to-add-window-android-view-viewrootw44da9bc0-permission-denied-for-t
    code to post/handler request for permission
    */
    public final static int REQUEST_CODE = 6517;

    private Intent service;

    private SwitchPreference preferenceFloatingButton;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        service = new Intent(getActivity(), OverlayShowingService.class);

        // add listeners for non-default actions
        Preference preference = findPreference(getString(R.string.settings_ime_available_key));
        preference.setOnPreferenceClickListener(this);

        preference = findPreference(getString(R.string.settings_ime_change_key));
        preference.setOnPreferenceClickListener(this);

        preferenceFloatingButton = (SwitchPreference) findPreference(getString(R.string.settings_floating_button_key));
        preferenceFloatingButton.setOnPreferenceChangeListener(this);

        preference = findPreference(getString(R.string.settings_position_button_key));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(getString(R.string.settings_ime_available_key))) {
            Utilities.openAvailableKeyboards(getContext());
        }

        if (preference.getKey().equals(getString(R.string.settings_ime_change_key))) {
            Utilities.chooseAKeyboard(getContext());
        }

        return false;
    }

    @Override
    /*
     * To manage color selection
     */
    public void onPositiveButtonClick(@ColorInt int color) {
        super.onPositiveButtonClick(color);
        restartFloatingButtonService();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.settings_floating_button_key))) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean floatingButtonEnabled = (Boolean) newValue;
            switchPreference.setChecked(floatingButtonEnabled);

            if (floatingButtonEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    WarningFloatingButtonDialog dialogFragment = new WarningFloatingButtonDialog();
                    if (getFragmentManager() != null)
                        dialogFragment.show(getFragmentManager(), "pro_feature_dialog");
                }
            } else {
                stopFloatingButtonService();
            }
        }

        if (preference.getKey().equals(getString(R.string.settings_position_button_key))) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setChecked((Boolean) newValue);
            restartFloatingButtonService();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (getActivity() != null
                && !Settings.canDrawOverlays(getActivity())) {
            /* if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            /* request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            startFloatingButtonService();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getActivity())
                        && service != null) {
                    startFloatingButtonService();
                }
            }
        }
    }

    public void startFloatingButtonService() {
        if (getActivity() != null) {
            getActivity().startService(service);
        }
        if (preferenceFloatingButton != null)
            preferenceFloatingButton.setChecked(true);
    }

    public void stopFloatingButtonService() {
        if (getActivity() != null) {
            getActivity().stopService(service);
        }
        if (preferenceFloatingButton != null)
            preferenceFloatingButton.setChecked(false);
    }

    public void restartFloatingButtonService() {
        // Restart service
        if (getActivity() != null) {
            getActivity().stopService(new Intent(getActivity(), OverlayShowingService.class));
            getActivity().startService(new Intent(getActivity(), OverlayShowingService.class));
        }
        if (preferenceFloatingButton != null)
            preferenceFloatingButton.setChecked(true);
    }
}