package com.kunzisoft.keyboard.switcher;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;

import com.kunzisoft.androidclearchroma.ChromaPreferenceFragmentCompat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PreferenceFragment extends ChromaPreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // add listeners for non-default actions
        Preference preference = findPreference(getString(R.string.settings_ime_available_key));
        preference.setOnPreferenceClickListener(this);

        preference = findPreference(getString(R.string.settings_position_button_key));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(getString(R.string.settings_ime_available_key))) {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return false;
    }

    @Override
    /*
     * To manage color selection
     */
    public void onPositiveButtonClick(@ColorInt int color) {
        super.onPositiveButtonClick(color);
        restartService();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.settings_position_button_key))) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setChecked((Boolean) newValue);
            restartService();
        }
        return false;
    }

    private void restartService() {
        // Restart service
        if (getActivity() != null) {
            getActivity().stopService(new Intent(getActivity(), OverlayShowingService.class));
            getActivity().startService(new Intent(getActivity(), OverlayShowingService.class));
        }
    }
}