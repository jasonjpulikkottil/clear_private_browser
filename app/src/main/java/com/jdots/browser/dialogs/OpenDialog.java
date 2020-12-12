/*
 *   2019-2020
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.helpers.DownloadLocationHelper;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

public class OpenDialog extends DialogFragment {
    // Define the open listener.
    private OpenListener openListener;

    @Override
    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for the open listener from the launching context.
        openListener = (OpenListener) context;
    }

    // `@SuppressLint("InflateParams")` removes the warning about using null as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get a handle for the activity and the context.
        Activity activity = requireActivity();
        Context context = requireContext();

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the icon according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
            dialogBuilder.setIcon(R.drawable.proxy_enabled_night);
        } else {
            dialogBuilder.setIcon(R.drawable.proxy_enabled_day);
        }

        // Set the title.
        dialogBuilder.setTitle(R.string.open);

        // Set the view.  The parent view is null because it will be assigned by the alert dialog.
        dialogBuilder.setView(activity.getLayoutInflater().inflate(R.layout.open_dialog, null));

        // Set the cancel button listener.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        // Set the open button listener.
        dialogBuilder.setPositiveButton(R.string.open, (DialogInterface dialog, int which) -> {
            // Return the dialog fragment to the parent activity.
            openListener.onOpen(this);
        });

        // Create an alert dialog from the builder.
        AlertDialog alertDialog = dialogBuilder.create();

        // Remove the incorrect lint warning below that the window might be null.
        assert alertDialog.getWindow() != null;

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // The alert dialog must be shown before items in the layout can be modified.
        alertDialog.show();

        // Get handles for the layout items.
        EditText fileNameEditText = alertDialog.findViewById(R.id.file_name_edittext);
        Button browseButton = alertDialog.findViewById(R.id.browse_button);
        TextView fileDoesNotExistTextView = alertDialog.findViewById(R.id.file_does_not_exist_textview);
        TextView storagePermissionTextView = alertDialog.findViewById(R.id.storage_permission_textview);
        Button openButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Remove the incorrect lint warnings below that the views might be null.
        assert fileNameEditText != null;
        assert browseButton != null;
        assert fileDoesNotExistTextView != null;
        assert storagePermissionTextView != null;

        // Update the status of the open button when the file name changes.
        fileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Get the current file name.
                String fileNameString = fileNameEditText.getText().toString();

                // Convert the file name string to a file.
                File file = new File(fileNameString);

                // Check to see if the file exists.
                if (file.exists()) {  // The file exists.
                    // Hide the notification that the file does not exist.
                    fileDoesNotExistTextView.setVisibility(View.GONE);

                    // Enable the open button.
                    openButton.setEnabled(true);
                } else {  // The file does not exist.
                    // Show the notification that the file does not exist.
                    fileDoesNotExistTextView.setVisibility(View.VISIBLE);

                    // Disable the open button.
                    openButton.setEnabled(false);
                }
            }
        });

        // Instantiate the download location helper.
        DownloadLocationHelper downloadLocationHelper = new DownloadLocationHelper();

        // Get the default file path.
        String defaultFilePath = downloadLocationHelper.getDownloadLocation(context) + "/";

        // Display the default file path.
        fileNameEditText.setText(defaultFilePath);

        // Move the cursor to the end of the default file path.
        fileNameEditText.setSelection(defaultFilePath.length());

        // Hide the storage permission text view if the permission has already been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            storagePermissionTextView.setVisibility(View.GONE);
        }

        // Handle clicks on the browse button.
        browseButton.setOnClickListener((View view) -> {
            // Create the file picker intent.
            Intent browseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Set the intent MIME type to include all files so that everything is visible.
            browseIntent.setType("*/*");

            // Set the initial directory if the minimum API >= 26.
            if (Build.VERSION.SDK_INT >= 26) {
                browseIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory());
            }

            // Start the file picker.  This must be started under `activity` to that the request code is returned correctly.
            activity.startActivityForResult(browseIntent, MainWebViewActivity.BROWSE_OPEN_REQUEST_CODE);
        });

        // Return the alert dialog.
        return alertDialog;
    }

    // The public interface is used to send information back to the parent activity.
    public interface OpenListener {
        void onOpen(DialogFragment dialogFragment);
    }
}
