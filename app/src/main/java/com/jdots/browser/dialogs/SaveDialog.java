/*
 * //
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
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.helpers.DownloadLocationHelper;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class SaveDialog extends DialogFragment {
    // Declare the class constants.
    public static final int SAVE_LOGCAT = 0;
    public static final int SAVE_ABOUT_VERSION_TEXT = 1;
    public static final int SAVE_ABOUT_VERSION_IMAGE = 2;
    private static final String SAVE_TYPE = "save_type";
    // Declare the class variables.
    String fileName;
    // Declare the save listener.
    private SaveListener saveListener;

    public static SaveDialog save(int saveType) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the arguments in the bundle.
        argumentsBundle.putInt(SAVE_TYPE, saveType);

        // Create a new instance of the save dialog.
        SaveDialog saveDialog = new SaveDialog();

        // Add the arguments bundle to the dialog.
        saveDialog.setArguments(argumentsBundle);

        // Return the new dialog.
        return saveDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for save listener from the launching context.
        saveListener = (SaveListener) context;
    }

    // `@SuppressLint("InflateParams")` removes the warning about using null as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint warning that the arguments might be null.
        assert arguments != null;

        // Get the arguments from the bundle.
        int saveType = arguments.getInt(SAVE_TYPE);

        // Get a handle for the activity and the context.
        Activity activity = requireActivity();
        Context context = requireContext();

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the title and icon according to the type.
        switch (saveType) {
            case SAVE_LOGCAT:
                // Set the title.
                dialogBuilder.setTitle(R.string.save_logcat);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    dialogBuilder.setIcon(R.drawable.save_dialog_day);
                } else {
                    dialogBuilder.setIcon(R.drawable.save_dialog_night);
                }
                break;

            case SAVE_ABOUT_VERSION_TEXT:
                // Set the title.
                dialogBuilder.setTitle(R.string.save_text);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    dialogBuilder.setIcon(R.drawable.save_text_blue_day);
                } else {
                    dialogBuilder.setIcon(R.drawable.save_text_blue_night);
                }
                break;

            case SAVE_ABOUT_VERSION_IMAGE:
                // Set the title.
                dialogBuilder.setTitle(R.string.save_image);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    dialogBuilder.setIcon(R.drawable.images_enabled_day);
                } else {
                    dialogBuilder.setIcon(R.drawable.images_enabled_night);
                }
                break;
        }

        // Set the view.  The parent view is null because it will be assigned by the alert dialog.
        dialogBuilder.setView(activity.getLayoutInflater().inflate(R.layout.save_dialog, null));

        // Set the cancel button listener.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        // Set the save button listener.
        dialogBuilder.setPositiveButton(R.string.save, (DialogInterface dialog, int which) -> {
            // Return the dialog fragment to the parent activity.
            saveListener.onSave(saveType, this);
        });

        // Create an alert dialog from the builder.
        AlertDialog alertDialog = dialogBuilder.create();

        // Remove the incorrect lint warning below that `getWindow()` might be null.
        assert alertDialog.getWindow() != null;

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // The alert dialog must be shown before items in the layout can be modified.
        alertDialog.show();

        // Get handles for the layout items.
        EditText fileNameEditText = alertDialog.findViewById(R.id.file_name_edittext);
        Button browseButton = alertDialog.findViewById(R.id.browse_button);
        TextView fileExistsWarningTextView = alertDialog.findViewById(R.id.file_exists_warning_textview);
        TextView storagePermissionTextView = alertDialog.findViewById(R.id.storage_permission_textview);
        Button saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Remove the incorrect lint warnings below that the views might be null.
        assert fileNameEditText != null;
        assert browseButton != null;
        assert fileExistsWarningTextView != null;
        assert storagePermissionTextView != null;

        // Update the status of the save button when the file name changes.
        fileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Get the current file name.
                String fileNameString = fileNameEditText.getText().toString();

                // Convert the file name string to a file.
                File file = new File(fileNameString);

                // Check to see if the file exists.
                if (file.exists()) {
                    // Show the file exists warning.
                    fileExistsWarningTextView.setVisibility(View.VISIBLE);
                } else {
                    // Hide the file exists warning.
                    fileExistsWarningTextView.setVisibility(View.GONE);
                }

                // Enable the save button if the file name is populated.
                saveButton.setEnabled(!fileNameString.isEmpty());
            }
        });

        // Set the file name according to the type.
        switch (saveType) {
            case SAVE_LOGCAT:
                // Use a file name ending in `.txt`.
                fileName = getString(R.string.application_name_logcat_txt);
                break;

            case SAVE_ABOUT_VERSION_TEXT:
                // Use a file name ending in `.txt`.
                fileName = getString(R.string.application_name_version_txt);
                break;

            case SAVE_ABOUT_VERSION_IMAGE:
                // Use a file name ending in `.png`.
                fileName = getString(R.string.application_name_version_png);
                break;
        }

        // Instantiate the download location helper.
        DownloadLocationHelper downloadLocationHelper = new DownloadLocationHelper();

        // Get the default file path.
        String defaultFilePath = downloadLocationHelper.getDownloadLocation(context) + "/" + fileName;

        // Display the default file path.
        fileNameEditText.setText(defaultFilePath);

        // Hide the storage permission text view if the permission has already been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            storagePermissionTextView.setVisibility(View.GONE);
        }

        // Handle clicks on the browse button.
        browseButton.setOnClickListener((View view) -> {
            // Create the file picker intent.
            Intent browseIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

            // Set the intent MIME type to include all files so that everything is visible.
            browseIntent.setType("*/*");

            // Set the initial file name.
            browseIntent.putExtra(Intent.EXTRA_TITLE, fileName);

            // Set the initial directory if the minimum API >= 26.
            if (Build.VERSION.SDK_INT >= 26) {
                browseIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory());
            }

            // Request a file that can be opened.
            browseIntent.addCategory(Intent.CATEGORY_OPENABLE);

            // Launch the file picker.  There is only one `startActivityForResult()`, so the request code is simply set to 0, but it must be run under `activity` so the request code is correct.
            activity.startActivityForResult(browseIntent, 0);
        });

        // Return the alert dialog.
        return alertDialog;
    }

    // The public interface is used to send information back to the parent activity.
    public interface SaveListener {
        void onSave(int saveType, DialogFragment dialogFragment);
    }
}