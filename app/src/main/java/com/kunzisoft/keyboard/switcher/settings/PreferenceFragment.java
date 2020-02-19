package com.kunzisoft.keyboard.switcher.settings;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.kunzisoft.androidclearchroma.ChromaPreferenceFragmentCompat;
import com.kunzisoft.keyboard.switcher.NotificationBuilder;
import com.kunzisoft.keyboard.switcher.OverlayShowingService;
import com.kunzisoft.keyboard.switcher.R;
import com.kunzisoft.keyboard.switcher.dialogs.WarningFloatingButtonDialog;
import com.kunzisoft.keyboard.switcher.utils.Utilities;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PreferenceFragment extends ChromaPreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    /* https://stackoverflow.com/questions/7569937/unable-to-add-window-android-view-viewrootw44da9bc0-permission-denied-for-t
    code to post/handler request for permission
    */
    private final static int REQUEST_CODE = 6517;

    private SwitchPreference preferenceNotification;
    private SwitchPreference preferenceFloatingButton;

    private NotificationBuilder mNotificationBuilder;

    private boolean tryToOpenExternalDialog;

	@Override
	public void onResume() {
		super.onResume();

		tryToOpenExternalDialog = false;
		// To unchecked the preference floating button if not allowed by the system
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!Settings.canDrawOverlays(getActivity())) {
				if (preferenceFloatingButton != null)
					preferenceFloatingButton.setChecked(false);
			}
		}
	}

	@Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // add listeners for non-default actions
        findPreference(getString(R.string.settings_ime_available_key))
                .setOnPreferenceClickListener(this);
        findPreference(getString(R.string.settings_ime_change_key))
                .setOnPreferenceClickListener(this);

        preferenceNotification = (SwitchPreference) findPreference(getString(R.string.settings_notification_key));
        preferenceNotification.setOnPreferenceChangeListener(this);
        if (getContext() != null) {
			mNotificationBuilder =
					new NotificationBuilder((NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE));
		}

        preferenceFloatingButton = (SwitchPreference) findPreference(getString(R.string.settings_floating_button_key));
        preferenceFloatingButton.setOnPreferenceChangeListener(this);

        findPreference(getString(R.string.settings_floating_button_lock_key))
                .setOnPreferenceChangeListener(this);
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
        restartFloatingButtonAndCheckedButton();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals(getString(R.string.settings_notification_key))) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean notificationEnabled = (Boolean) newValue;
            switchPreference.setChecked(notificationEnabled);

            if (notificationEnabled) {
                startNotificationAndCheckButton();
            } else {
                stopNotificationAndUncheckButton();
            }
        }

		if (preference.getKey().equals(getString(R.string.settings_floating_button_key))) {
			SwitchPreference switchPreference = (SwitchPreference) preference;
			boolean floatingButtonEnabled = (Boolean) newValue;
			switchPreference.setChecked(floatingButtonEnabled);

			if (floatingButtonEnabled) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					WarningFloatingButtonDialog dialogFragment = new WarningFloatingButtonDialog();
					if (getFragmentManager() != null)
						dialogFragment.show(getFragmentManager(), "warning_floating_button_dialog");
				} else {
					startFloatingButtonAndCheckButton();
				}
			} else {
				stopFloatingButtonAndUncheckedButton();
			}
		}

        if (preference.getKey().equals(getString(R.string.settings_floating_button_lock_key))) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setChecked((Boolean) newValue);
            restartFloatingButtonAndCheckedButton();
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean drawOverlayPermissionAllowed() {
    	if (getActivity() != null) {
			/* check if we already  have permission to draw over other apps */
			if (Settings.canDrawOverlays(getActivity())) {
				return true;
			} else {
				try {
					/* if not construct intent to request permission */
					tryToOpenExternalDialog = true;
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
							Uri.parse("package:" + getActivity().getPackageName()));
					/* request permission via start activity for result */
					startActivityForResult(intent, REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					if (getContext() != null)
						new AlertDialog.Builder(getContext())
								.setMessage(R.string.error_overlay_permission_request)
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {}
								}).create().show();
				}
			}
		}
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getActivity())) {
                    startFloatingButtonAndCheckButton();
                }
            }
        }
    }

	/**
	 * Method used to not destroy the main activity when an external dialog is requested
	 * @return 'true' if an external dialog is requested
	 */
	public boolean isTryingToOpenExternalDialog() {
    	return tryToOpenExternalDialog;
	}

    /*
    ------ Notification Service ------
     */

	private void startNotificationAndCheckButton() {
		if(mNotificationBuilder != null) {
			mNotificationBuilder.createKeyboardNotification(getContext());
		}
        if (preferenceNotification != null)
            preferenceNotification.setChecked(true);
    }

	private void stopNotificationAndUncheckButton() {
		if(mNotificationBuilder != null) {
			mNotificationBuilder.cancelKeyboardNotification();
		}
        if (preferenceNotification != null)
            preferenceNotification.setChecked(false);
    }

    /*
    ------ Floating Button Service ------
    */

	private void stopFloatingButtonService() {
		if (getActivity() != null) {
			getActivity().stopService(new Intent(getActivity(), OverlayShowingService.class));
		}
	}

    private void startFloatingButtonService() {
		if (getActivity() != null) {
			Intent floatingButtonService = new Intent(getActivity(), OverlayShowingService.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				getActivity().startForegroundService(floatingButtonService);
			} else {
				getActivity().startService(floatingButtonService);
			}
		}
	}

    void startFloatingButtonAndCheckButton() {
		stopFloatingButtonService();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (drawOverlayPermissionAllowed()) {
				startFloatingButtonService();
			} else {
				if (preferenceFloatingButton != null)
					preferenceFloatingButton.setChecked(false);
			}
		} else {
			startFloatingButtonService();
		}
        if (preferenceFloatingButton != null)
            preferenceFloatingButton.setChecked(true);
    }

    void stopFloatingButtonAndUncheckedButton() {
        stopFloatingButtonService();
        if (preferenceFloatingButton != null)
            preferenceFloatingButton.setChecked(false);
    }

    private void restartFloatingButtonAndCheckedButton() {
        // Restart service
        if (getActivity() != null) {
            getActivity().stopService(new Intent(getActivity(), OverlayShowingService.class));
        }
		startFloatingButtonAndCheckButton();
    }
}