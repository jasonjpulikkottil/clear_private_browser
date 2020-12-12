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

package com.jdots.browser.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jdots.browser.R;
import com.jdots.browser.adapters.AboutPagerAdapter;
import com.jdots.browser.asynctasks.SaveAboutVersionImage;
import com.jdots.browser.dialogs.SaveDialog;
import com.jdots.browser.dialogs.StoragePermissionDialog;
import com.jdots.browser.fragments.AboutVersionFragment;
import com.jdots.browser.helpers.FileNameHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

public class AboutActivity extends AppCompatActivity implements SaveDialog.SaveListener, StoragePermissionDialog.StoragePermissionDialogListener {
    // Declare the class variables.
    private String filePathString;
    private AboutPagerAdapter aboutPagerAdapter;

    // Declare the class views.
    private LinearLayout aboutVersionLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Set the theme.
        setTheme(R.style.ClearBrowser);

        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Get the intent that launched the activity.
        Intent launchingIntent = getIntent();

        // Store the blocklist versions.
        String[] blocklistVersions = launchingIntent.getStringArrayExtra("blocklist_versions");

        // Set the content view.
        setContentView(R.layout.about_coordinatorlayout);

        // Get handles for the views.
        Toolbar toolbar = findViewById(R.id.about_toolbar);
        TabLayout aboutTabLayout = findViewById(R.id.about_tablayout);
        ViewPager aboutViewPager = findViewById(R.id.about_viewpager);

        // Set the action bar.  `SupportActionBar` must be used until the minimum API is >= 21.
        setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Remove the incorrect lint warning that the action bar might be null.
        assert actionBar != null;  //

        // Display the home arrow on action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialize the about pager adapter.
        aboutPagerAdapter = new AboutPagerAdapter(getSupportFragmentManager(), getApplicationContext(), blocklistVersions);

        // Setup the ViewPager.
        aboutViewPager.setAdapter(aboutPagerAdapter);

        // Keep all the tabs in memory.  This prevents the memory usage updater from running multiple times.
        aboutViewPager.setOffscreenPageLimit(10);

        // Connect the tab layout to the view pager.
        aboutTabLayout.setupWithViewPager(aboutViewPager);
    }

    @Override
    public void onSave(int saveType, DialogFragment dialogFragment) {
        // Get a handle for the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the lint warning below that the dialog might be null.
        assert dialog != null;

        // Get a handle for the file name edit text.
        EditText fileNameEditText = dialog.findViewById(R.id.file_name_edittext);

        // Get the file path string.
        filePathString = fileNameEditText.getText().toString();

        // Get a handle for the about version linear layout.
        aboutVersionLinearLayout = findViewById(R.id.about_version_linearlayout);

        // check to see if the storage permission is needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
            // Save the file according to the type.
            switch (saveType) {
                case SaveDialog.SAVE_ABOUT_VERSION_TEXT:
                    // Save the about version text.
                    saveAsText(filePathString);
                    break;

                case SaveDialog.SAVE_ABOUT_VERSION_IMAGE:
                    // Save the about version image.
                    new SaveAboutVersionImage(this, this, filePathString, aboutVersionLinearLayout).execute();
                    break;
            }

            // Reset the file path string.
            filePathString = "";
        } else {  // The storage permission has not been granted.
            // Get the external private directory file.
            File externalPrivateDirectoryFile = getExternalFilesDir(null);

            // Remove the incorrect lint error below that the file might be null.
            assert externalPrivateDirectoryFile != null;

            // Get the external private directory string.
            String externalPrivateDirectory = externalPrivateDirectoryFile.toString();

            // Check to see if the file path is in the external private directory.
            if (filePathString.startsWith(externalPrivateDirectory)) {  // The file path is in the external private directory.
                // Save the webpage according to the type.
                switch (saveType) {
                    case SaveDialog.SAVE_ABOUT_VERSION_TEXT:
                        // Save the about version text.
                        saveAsText(filePathString);
                        break;

                    case SaveDialog.SAVE_ABOUT_VERSION_IMAGE:
                        // Save the about version image.
                        new SaveAboutVersionImage(this, this, filePathString, aboutVersionLinearLayout).execute();
                        break;
                }

                // Reset the file path string.
                filePathString = "";
            } else {  // The file path is in a public directory.
                // Check if the user has previously denied the storage permission.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {  // Show a dialog explaining the request first.
                    // Declare a storage permission dialog fragment.
                    DialogFragment storagePermissionDialogFragment;

                    // Instantiate the storage permission alert dialog according to the type.
                    if (saveType == SaveDialog.SAVE_ABOUT_VERSION_TEXT) {
                        storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(StoragePermissionDialog.SAVE_TEXT);
                    } else {
                        storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(StoragePermissionDialog.SAVE_IMAGE);
                    }

                    // Show the storage permission alert dialog.  The permission will be requested when the dialog is closed.
                    storagePermissionDialogFragment.show(getSupportFragmentManager(), getString(R.string.storage_permission));
                } else {  // Show the permission request directly.
                    switch (saveType) {
                        case SaveDialog.SAVE_ABOUT_VERSION_TEXT:
                            // Request the write external storage permission.  The text will be saved when it finishes.
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StoragePermissionDialog.SAVE_TEXT);
                            break;

                        case SaveDialog.SAVE_ABOUT_VERSION_IMAGE:
                            // Request the write external storage permission.  The image will be saved when it finishes.
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StoragePermissionDialog.SAVE_IMAGE);
                            break;
                    }

                }
            }
        }
    }

    @Override
    public void onCloseStoragePermissionDialog(int requestType) {
        // Request the write external storage permission according to the request type.  About version will be saved when it finishes.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestType);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Only process the results if they exist (this method is triggered when a dialog is presented the first time for an app, but no grant results are included).
        if (grantResults.length > 0) {
            // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // The storage permission was granted.
                switch (requestCode) {
                    case StoragePermissionDialog.SAVE_TEXT:
                        // Save the about version text.
                        saveAsText(filePathString);
                        break;

                    case StoragePermissionDialog.SAVE_IMAGE:
                        // Save the about version image.
                        new SaveAboutVersionImage(this, this, filePathString, aboutVersionLinearLayout).execute();
                        break;
                }
            } else {  // the storage permission was not granted.
                // Display an error snackbar.
                Snackbar.make(aboutVersionLinearLayout, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
            }

            // Reset the file path string.
            filePathString = "";
        }
    }

    // The activity result is called after browsing for a file in the save alert dialog.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Run the default commands.
        super.onActivityResult(requestCode, resultCode, data);

        // Only do something if the user didn't press back from the file picker.
        if (resultCode == Activity.RESULT_OK) {
            // Get a handle for the save dialog fragment.
            DialogFragment saveDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.save_dialog));

            // Only update the file name if the dialog still exists.
            if (saveDialogFragment != null) {
                // Get a handle for the save dialog.
                Dialog saveDialog = saveDialogFragment.getDialog();

                // Remove the lint warning below that the dialog might be null.
                assert saveDialog != null;

                // Get a handle for the dialog view.
                EditText fileNameEditText = saveDialog.findViewById(R.id.file_name_edittext);
                TextView fileExistsWarningTextView = saveDialog.findViewById(R.id.file_exists_warning_textview);

                // Get the file name URI from the intent.
                Uri fileNameUri = data.getData();

                // Process the file name URI if it is not null.
                if (fileNameUri != null) {
                    // Instantiate a file name helper.
                    FileNameHelper fileNameHelper = new FileNameHelper();

                    // Convert the file name URI to a file name path.
                    String fileNamePath = fileNameHelper.convertUriToFileNamePath(fileNameUri);

                    // Set the file name path as the text of the file nam edit text.
                    fileNameEditText.setText(fileNamePath);

                    // Move the cursor to the end of the file name edit text.
                    fileNameEditText.setSelection(fileNamePath.length());

                    // Hid ethe file exists warning.
                    fileExistsWarningTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void saveAsText(String fileNameString) {
        try {
            // Get a handle for the about about version fragment.
            AboutVersionFragment aboutVersionFragment = (AboutVersionFragment) aboutPagerAdapter.getTabFragment(0);

            // Get the about version text.
            String aboutVersionString = aboutVersionFragment.getAboutVersionString();

            // Create an input stream with the contents of about version.
            InputStream aboutVersionInputStream = new ByteArrayInputStream(aboutVersionString.getBytes(StandardCharsets.UTF_8));

            // Create an about version buffered reader.
            BufferedReader aboutVersionBufferedReader = new BufferedReader(new InputStreamReader(aboutVersionInputStream));

            // Create a file from the file name string.
            File saveFile = new File(fileNameString);

            // Delete the file if it already exists.
            if (saveFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                saveFile.delete();
            }

            // Create a file buffered writer.
            BufferedWriter fileBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile)));

            // Create a transfer string.
            String transferString;

            // Use the transfer string to copy the about version text from the buffered reader to the buffered writer.
            while ((transferString = aboutVersionBufferedReader.readLine()) != null) {
                // Append the line to the buffered writer.
                fileBufferedWriter.append(transferString);

                // Append a line break.
                fileBufferedWriter.append("\n");
            }

            // Close the buffered reader and writer.
            aboutVersionBufferedReader.close();
            fileBufferedWriter.close();

            // Add the file to the list of recent files.  This doesn't currently work, but maybe it will someday.
            MediaScannerConnection.scanFile(this, new String[]{fileNameString}, new String[]{"text/plain"}, null);

            // Create an about version saved snackbar.
            Snackbar aboutVersionSavedSnackbar = Snackbar.make(aboutVersionLinearLayout, getString(R.string.file_saved) + "  " + fileNameString, Snackbar.LENGTH_SHORT);

            // Add an open option to the snackbar.
            aboutVersionSavedSnackbar.setAction(R.string.open, (View view) -> {
                // Get a file for the file name string.
                File file = new File(fileNameString);

                // Declare a file URI variable.
                Uri fileUri;

                // Get the URI for the file according to the Android version.
                if (Build.VERSION.SDK_INT >= 24) {  // Use a file provider.
                    fileUri = FileProvider.getUriForFile(this, getString(R.string.file_provider), file);
                } else {  // Get the raw file path URI.
                    fileUri = Uri.fromFile(file);
                }

                // Get a handle for the content resolver.
                ContentResolver contentResolver = getContentResolver();

                // Create an open intent with `ACTION_VIEW`.
                Intent openIntent = new Intent(Intent.ACTION_VIEW);

                // Set the URI and the MIME type.
                openIntent.setDataAndType(fileUri, contentResolver.getType(fileUri));

                // Allow the app to read the file URI.
                openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Show the chooser.
                startActivity(Intent.createChooser(openIntent, getString(R.string.open)));
            });

            // Show the about version saved snackbar.
            aboutVersionSavedSnackbar.show();
        } catch (Exception exception) {
            // Display a snackbar with the error message.
            Snackbar.make(aboutVersionLinearLayout, getString(R.string.error_saving_file) + "  " + exception.toString(), Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}