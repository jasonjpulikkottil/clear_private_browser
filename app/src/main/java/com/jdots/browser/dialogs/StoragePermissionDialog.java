/*
 *   2018-2020
 *
 * //
 *

 *
 * Clear Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *
 *
 */

package com.jdots.browser.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.jdots.browser.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class StoragePermissionDialog extends DialogFragment {
    // Define the save type constants.
    public static final int OPEN = 0;
    public static final int SAVE_URL = 1;
    public static final int SAVE_ARCHIVE = 2;
    public static final int SAVE_TEXT = 3;
    public static final int SAVE_IMAGE = 4;

    // The listener is used in `onAttach()` and `onCreateDialog()`.
    private StoragePermissionDialogListener storagePermissionDialogListener;

    public static StoragePermissionDialog displayDialog(int requestType) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the save type in the bundle.
        argumentsBundle.putInt("request_type", requestType);

        // Create a new instance of the storage permission dialog.
        StoragePermissionDialog storagePermissionDialog = new StoragePermissionDialog();

        // Add the arguments bundle to the new dialog.
        storagePermissionDialog.setArguments(argumentsBundle);

        // Return the new dialog.
        return storagePermissionDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for the listener from the launching context.
        storagePermissionDialogListener = (StoragePermissionDialogListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint warning that the arguments might be null.
        assert arguments != null;

        // Get the save type.
        int requestType = arguments.getInt("request_type");

        // Use a builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the icon according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
            dialogBuilder.setIcon(R.drawable.import_export_night);
        } else {
            dialogBuilder.setIcon(R.drawable.import_export_day);
        }

        // Set the title.
        dialogBuilder.setTitle(R.string.storage_permission);

        // Set the text.
        dialogBuilder.setMessage(R.string.storage_permission_message);

        // Set an listener on the OK button.
        dialogBuilder.setNegativeButton(R.string.ok, (DialogInterface dialog, int which) -> {
            // Inform the parent activity that the dialog was closed.
            storagePermissionDialogListener.onCloseStoragePermissionDialog(requestType);
        });

        // Create an alert dialog from the builder.
        final AlertDialog alertDialog = dialogBuilder.create();

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            // Remove the warning below that `getWindow()` might be null.
            assert alertDialog.getWindow() != null;

            // Disable screenshots.
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Return the alert dialog.
        return alertDialog;
    }

    // The public interface is used to send information back to the parent activity.
    public interface StoragePermissionDialogListener {
        void onCloseStoragePermissionDialog(int requestType);
    }
}