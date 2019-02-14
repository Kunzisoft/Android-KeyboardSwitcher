package com.kunzisoft.keyboard.switcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableStringBuilder;

import com.kunzisoft.keyboard.switcher.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Custom Dialog that asks the user to download the pro version or make a donation.
 */
public class WarningFloatingButtonDialog extends DialogFragment {

    OnFloatingButtonListener onClickListener;

    public interface OnFloatingButtonListener {
        void onFloatingButtonDialogPositiveButtonClick();
        void onFloatingButtonDialogNegativeButtonClick();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            onClickListener = (OnFloatingButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + DialogInterface.OnClickListener.class.getName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        assert getActivity() != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append(getString(R.string.floating_button_warning));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(getActivity())) {
            stringBuilder.append("\n\n").append(Html.fromHtml(getString(R.string.floating_button_above_screen)));
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onFloatingButtonDialogPositiveButtonClick();
            }
        });
        builder.setMessage(stringBuilder);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onFloatingButtonDialogNegativeButtonClick();
                dismiss();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
