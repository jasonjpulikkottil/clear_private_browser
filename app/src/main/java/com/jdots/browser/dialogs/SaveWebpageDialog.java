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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.jdots.browser.R;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.asynctasks.GetUrlSize;
import com.jdots.browser.helpers.DownloadLocationHelper;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

public class SaveWebpageDialog extends DialogFragment {
    // Define the save webpage listener.
    private SaveWebpageListener saveWebpageListener;
    // Define the get URL size AsyncTask.  This allows previous instances of the task to be cancelled if a new one is run.
    private AsyncTask getUrlSize;

    public static SaveWebpageDialog saveWebpage(int saveType, String urlString, String fileSizeString, String contentDispositionFileNameString, String userAgentString, boolean cookiesEnabled) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the arguments in the bundle.
        argumentsBundle.putInt("save_type", saveType);
        argumentsBundle.putString("url_string", urlString);
        argumentsBundle.putString("file_size_string", fileSizeString);
        argumentsBundle.putString("content_disposition_file_name_string", contentDispositionFileNameString);
        argumentsBundle.putString("user_agent_string", userAgentString);
        argumentsBundle.putBoolean("cookies_enabled", cookiesEnabled);

        // Create a new instance of the save webpage dialog.
        SaveWebpageDialog saveWebpageDialog = new SaveWebpageDialog();

        // Add the arguments bundle to the new dialog.
        saveWebpageDialog.setArguments(argumentsBundle);

        // Return the new dialog.
        return saveWebpageDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for the save webpage listener from the launching context.
        saveWebpageListener = (SaveWebpageListener) context;
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
        int saveType = arguments.getInt("save_type");
        String urlString = arguments.getString("url_string");
        String fileSizeString = arguments.getString("file_size_string");
        String contentDispositionFileNameString = arguments.getString("content_disposition_file_name_string");
        String userAgentString = arguments.getString("user_agent_string");
        boolean cookiesEnabled = arguments.getBoolean("cookies_enabled");

        // Get a handle for the activity and the context.
        Activity activity = requireActivity();
        Context context = requireContext();

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the icon according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {  // The night theme is enabled.
            // Set the icon according to the save type.
            switch (saveType) {
                case StoragePermissionDialog.SAVE_URL:
                    dialogBuilder.setIcon(R.drawable.copy_enabled_night);
                    break;

                case StoragePermissionDialog.SAVE_ARCHIVE:
                    dialogBuilder.setIcon(R.drawable.dom_storage_cleared_night);
                    break;

                case StoragePermissionDialog.SAVE_IMAGE:
                    dialogBuilder.setIcon(R.drawable.images_enabled_night);
                    break;
            }
        } else {  // The day theme is enabled.
            // Set the icon according to the save type.
            switch (saveType) {
                case StoragePermissionDialog.SAVE_URL:
                    dialogBuilder.setIcon(R.drawable.copy_enabled_day);
                    break;

                case StoragePermissionDialog.SAVE_ARCHIVE:
                    dialogBuilder.setIcon(R.drawable.dom_storage_cleared_day);
                    break;

                case StoragePermissionDialog.SAVE_IMAGE:
                    dialogBuilder.setIcon(R.drawable.images_enabled_day);
                    break;
            }
        }

        // Set the title according to the type.
        switch (saveType) {
            case StoragePermissionDialog.SAVE_URL:
                dialogBuilder.setTitle(R.string.save);
                break;

            case StoragePermissionDialog.SAVE_ARCHIVE:
                dialogBuilder.setTitle(R.string.save_archive);
                break;

            case StoragePermissionDialog.SAVE_IMAGE:
                dialogBuilder.setTitle(R.string.save_image);
                break;
        }

        // Set the view.  The parent view is null because it will be assigned by the alert dialog.
        dialogBuilder.setView(activity.getLayoutInflater().inflate(R.layout.save_url_dialog, null));

        // Set the cancel button listener.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        // Set the save button listener.
        dialogBuilder.setPositiveButton(R.string.save, (DialogInterface dialog, int which) -> {
            // Return the dialog fragment to the parent activity.
            saveWebpageListener.onSaveWebpage(saveType, urlString, this);
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
        TextInputLayout urlTextInputLayout = alertDialog.findViewById(R.id.url_textinputlayout);
        EditText urlEditText = alertDialog.findViewById(R.id.url_edittext);
        EditText fileNameEditText = alertDialog.findViewById(R.id.file_name_edittext);
        Button browseButton = alertDialog.findViewById(R.id.browse_button);
        TextView fileSizeTextView = alertDialog.findViewById(R.id.file_size_textview);
        TextView fileExistsWarningTextView = alertDialog.findViewById(R.id.file_exists_warning_textview);
        TextView storagePermissionTextView = alertDialog.findViewById(R.id.storage_permission_textview);
        Button saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Remove the incorrect warnings that the views might be null.
        assert urlTextInputLayout != null;
        assert urlEditText != null;
        assert fileNameEditText != null;
        assert browseButton != null;
        assert fileSizeTextView != null;
        assert fileExistsWarningTextView != null;
        assert storagePermissionTextView != null;

        // Set the file size text view.
        fileSizeTextView.setText(fileSizeString);

        // Modify the layout based on the save type.
        if (saveType == StoragePermissionDialog.SAVE_URL) {  // A URL is being saved.
            // Remove the incorrect lint error below that the URL string might be null.
            assert urlString != null;

            // Populate the URL edit text according to the type.  This must be done before the text change listener is created below so that the file size isn't requested again.
            if (urlString.startsWith("data:")) {  // The URL contains the entire data of an image.
                // Get a substring of the data URL with the first 100 characters.  Otherwise, the user interface will freeze while trying to layout the edit text.
                String urlSubstring = urlString.substring(0, 100) + "…";

                // Populate the URL edit text with the truncated URL.
                urlEditText.setText(urlSubstring);

                // Disable the editing of the URL edit text.
                urlEditText.setInputType(InputType.TYPE_NULL);
            } else {  // The URL contains a reference to the location of the data.
                // Populate the URL edit text with the full URL.
                urlEditText.setText(urlString);
            }

            // Update the file size and the status of the save button when the URL changes.
            urlEditText.addTextChangedListener(new TextWatcher() {
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
                    // Cancel the get URL size AsyncTask if it is running.
                    if ((getUrlSize != null)) {
                        getUrlSize.cancel(true);
                    }

                    // Get the current URL to save.
                    String urlToSave = urlEditText.getText().toString();

                    // Wipe the file size text view.
                    fileSizeTextView.setText("");

                    // Get the file size for the current URL.
                    getUrlSize = new GetUrlSize(context, alertDialog, userAgentString, cookiesEnabled).execute(urlToSave);

                    // Enable the save button if the URL and file name are populated.
                    saveButton.setEnabled(!urlToSave.isEmpty() && !fileNameEditText.getText().toString().isEmpty());
                }
            });
        } else {  // An archive or an image is being saved.
            // Hide the URL edit text and the file size text view.
            urlTextInputLayout.setVisibility(View.GONE);
            fileSizeTextView.setVisibility(View.GONE);
        }

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

                // Enable the save button based on the save type.
                if (saveType == StoragePermissionDialog.SAVE_URL) {  // A URL is being saved.
                    // Enable the save button if the file name and the URL is populated.
                    saveButton.setEnabled(!fileNameString.isEmpty() && !urlEditText.getText().toString().isEmpty());
                } else {  // An archive or an image is being saved.
                    // Enable the save button if the file name is populated.
                    saveButton.setEnabled(!fileNameString.isEmpty());
                }
            }
        });

        // Create a file name string.
        String fileName = "";

        // Set the file name according to the type.
        switch (saveType) {
            case StoragePermissionDialog.SAVE_URL:
                // Use the file name from the content disposition.
                fileName = contentDispositionFileNameString;
                break;

            case StoragePermissionDialog.SAVE_ARCHIVE:
                // Use an archive name ending in `.mht`.
                fileName = getString(R.string.webpage_mht);
                break;

            case StoragePermissionDialog.SAVE_IMAGE:
                // Use a file name ending in `.png`.
                fileName = getString(R.string.webpage_png);
                break;
        }

        // Save the file name as the default file name.  This must be final to be used in the lambda below.
        final String defaultFileName = fileName;

        // Instantiate the download location helper.
        DownloadLocationHelper downloadLocationHelper = new DownloadLocationHelper();

        // Get the default file path.
        String defaultFilePath = downloadLocationHelper.getDownloadLocation(context) + "/" + defaultFileName;

        // Populate the file name edit text.
        fileNameEditText.setText(defaultFilePath);

        // Move the cursor to the end of the default file path.
        fileNameEditText.setSelection(defaultFilePath.length());

        // Handle clicks on the browse button.
        browseButton.setOnClickListener((View view) -> {
            // Create the file picker intent.
            Intent browseIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

            // Set the intent MIME type to include all files so that everything is visible.
            browseIntent.setType("*/*");

            // Set the initial file name according to the type.
            browseIntent.putExtra(Intent.EXTRA_TITLE, defaultFileName);

            // Set the initial directory if the minimum API >= 26.
            if (Build.VERSION.SDK_INT >= 26) {
                browseIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory());
            }

            // Request a file that can be opened.
            browseIntent.addCategory(Intent.CATEGORY_OPENABLE);

            // Start the file picker.  This must be started under `activity` so that the request code is returned correctly.
            activity.startActivityForResult(browseIntent, MainWebViewActivity.BROWSE_SAVE_WEBPAGE_REQUEST_CODE);
        });

        // Hide the storage permission text view if the permission has already been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            storagePermissionTextView.setVisibility(View.GONE);
        }

        // Return the alert dialog.
        return alertDialog;
    }

    // The public interface is used to send information back to the parent activity.
    public interface SaveWebpageListener {
        void onSaveWebpage(int saveType, String originalUrlString, DialogFragment dialogFragment);
    }
}