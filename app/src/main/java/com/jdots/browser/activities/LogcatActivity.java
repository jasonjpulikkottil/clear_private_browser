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

package com.jdots.browser.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.jdots.browser.R;
import com.jdots.browser.asynctasks.GetLogcat;
import com.jdots.browser.dialogs.SaveDialog;
import com.jdots.browser.dialogs.StoragePermissionDialog;
import com.jdots.browser.helpers.FileNameHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LogcatActivity extends AppCompatActivity implements SaveDialog.SaveListener, StoragePermissionDialog.StoragePermissionDialogListener {
    // Declare the class constants.
    private final String SCROLLVIEW_POSITION = "scrollview_position";

    // Declare the class variables.
    private String filePathString;

    // Define the class views.
    private TextView logcatTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Set the theme.
        setTheme(R.style.ClearBrowser);

        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Set the content view.
        setContentView(R.layout.logcat_coordinatorlayout);

        // Set the toolbar as the action bar.
        Toolbar toolbar = findViewById(R.id.logcat_toolbar);
        setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        ActionBar actionBar = getSupportActionBar();

        // Remove the incorrect lint warning that the action bar might be null.
        assert actionBar != null;

        // Display the the back arrow in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Populate the class views.
        logcatTextView = findViewById(R.id.logcat_textview);

        // Implement swipe to refresh.
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.logcat_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Get the current logcat.
            new GetLogcat(this, 0).execute();
        });

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the refresh color scheme according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            swipeRefreshLayout.setColorSchemeResources(R.color.blue_700);
        } else {
            swipeRefreshLayout.setColorSchemeResources(R.color.blue_500);
        }

        // Initialize a color background typed value.
        TypedValue colorBackgroundTypedValue = new TypedValue();

        // Get the color background from the theme.
        getTheme().resolveAttribute(android.R.attr.colorBackground, colorBackgroundTypedValue, true);

        // Get the color background int from the typed value.
        int colorBackgroundInt = colorBackgroundTypedValue.data;

        // Set the swipe refresh background color.
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorBackgroundInt);

        // Initialize the scrollview Y position int.
        int scrollViewYPositionInt = 0;

        // Check to see if the activity has been restarted.
        if (savedInstanceState != null) {
            // Get the saved scrollview position.
            scrollViewYPositionInt = savedInstanceState.getInt(SCROLLVIEW_POSITION);
        }

        // Get the logcat.
        new GetLogcat(this, scrollViewYPositionInt).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.  This adds items to the action bar.
        getMenuInflater().inflate(R.menu.logcat_options_menu, menu);

        // Display the menu.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Get the selected menu item ID.
        int menuItemId = menuItem.getItemId();

        // Run the commands that correlate to the selected menu item.
        if (menuItemId == R.id.copy) {  // Copy was selected.
            // Get a handle for the clipboard manager.
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            // Remove the incorrect lint error below that the clipboard manager might be null.
            assert clipboardManager != null;

            // Save the logcat in a clip data.
            ClipData logcatClipData = ClipData.newPlainText(getString(R.string.logcat), logcatTextView.getText());

            // Place the clip data on the clipboard.
            clipboardManager.setPrimaryClip(logcatClipData);

            // Display a snackbar.
            Snackbar.make(logcatTextView, R.string.logcat_copied, Snackbar.LENGTH_SHORT).show();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save) {  // Save was selected.
            // Instantiate the save alert dialog.
            DialogFragment saveDialogFragment = SaveDialog.save(SaveDialog.SAVE_LOGCAT);

            // Show the save alert dialog.
            saveDialogFragment.show(getSupportFragmentManager(), getString(R.string.save_logcat));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.clear) {  // Clear was selected.
            try {
                // Clear the logcat.  `-c` clears the logcat.  `-b all` clears all the buffers (instead of just crash, main, and system).
                Process process = Runtime.getRuntime().exec("logcat -b all -c");

                // Wait for the process to finish.
                process.waitFor();

                // Reload the logcat.
                new GetLogcat(this, 0).execute();
            } catch (IOException | InterruptedException exception) {
                // Do nothing.
            }

            // Consume the event.
            return true;
        } else {  // The home button was pushed.
            // Do not consume the event.  The system will process the home command.
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Get a handle for the logcat scrollview.
        ScrollView logcatScrollView = findViewById(R.id.logcat_scrollview);

        // Get the scrollview Y position.
        int scrollViewYPositionInt = logcatScrollView.getScrollY();

        // Store the scrollview Y position in the bundle.
        savedInstanceState.putInt(SCROLLVIEW_POSITION, scrollViewYPositionInt);
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

        // Check to see if the storage permission is needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
            // Save the logcat.
            saveLogcat(filePathString);
        } else {  // The storage permission has not been granted.
            // Get the external private directory file.
            File externalPrivateDirectoryFile = getExternalFilesDir(null);

            // Remove the incorrect lint error below that the file might be null.
            assert externalPrivateDirectoryFile != null;

            // Get the external private directory string.
            String externalPrivateDirectory = externalPrivateDirectoryFile.toString();

            // Check to see if the file path is in the external private directory.
            if (filePathString.startsWith(externalPrivateDirectory)) {  // The file path is in the external private directory.
                // Save the logcat.
                saveLogcat(filePathString);
            } else {  // The file path is in a public directory.
                // Check if the user has previously denied the storage permission.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {  // Show a dialog explaining the request first.
                    // Instantiate the storage permission alert dialog.  The type is specified as `0` because it currently isn't used for this activity.
                    DialogFragment storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(0);

                    // Show the storage permission alert dialog.  The permission will be requested when the dialog is closed.
                    storagePermissionDialogFragment.show(getSupportFragmentManager(), getString(R.string.storage_permission));
                } else {  // Show the permission request directly.
                    // Request the write external storage permission.  The logcat will be saved when it finishes.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                }
            }
        }
    }

    @Override
    public void onCloseStoragePermissionDialog(int requestType) {
        // Request the write external storage permission.  The logcat will be saved when it finishes.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check to see if the storage permission was granted.  If the dialog was canceled the grant result will be empty.
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {  // The storage permission was granted.
            // Save the logcat.
            saveLogcat(filePathString);
        } else {  // The storage permission was not granted.
            // Display an error snackbar.
            Snackbar.make(logcatTextView, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
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
            DialogFragment saveDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.save_logcat));

            // Only update the file name if the dialog still exists.
            if (saveDialogFragment != null) {
                // Get a handle for the save dialog.
                Dialog saveDialog = saveDialogFragment.getDialog();

                // Remove the lint warning below that the save dialog might be null.
                assert saveDialog != null;

                // Get a handle for the dialog views.
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

                    // Set the file name path as the text of the file name edit text.
                    fileNameEditText.setText(fileNamePath);

                    // Move the cursor to the end of the file name edit text.
                    fileNameEditText.setSelection(fileNamePath.length());

                    // Hide the file exists warning.
                    fileExistsWarningTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void saveLogcat(String fileNameString) {
        try {
            // Get the logcat as a string.
            String logcatString = logcatTextView.getText().toString();

            // Create an input stream with the contents of the logcat.
            InputStream logcatInputStream = new ByteArrayInputStream(logcatString.getBytes(StandardCharsets.UTF_8));

            // Create a logcat buffered reader.
            BufferedReader logcatBufferedReader = new BufferedReader(new InputStreamReader(logcatInputStream));

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

            // Use the transfer string to copy the logcat from the buffered reader to the buffered writer.
            while ((transferString = logcatBufferedReader.readLine()) != null) {
                // Append the line to the buffered writer.
                fileBufferedWriter.append(transferString);

                // Append a line break.
                fileBufferedWriter.append("\n");
            }

            // Close the buffered reader and writer.
            logcatBufferedReader.close();
            fileBufferedWriter.close();

            // Add the file to the list of recent files.  This doesn't currently work, but maybe it will someday.
            MediaScannerConnection.scanFile(this, new String[]{fileNameString}, new String[]{"text/plain"}, null);

            // Create a logcat saved snackbar.
            Snackbar logcatSavedSnackbar = Snackbar.make(logcatTextView, getString(R.string.file_saved) + "  " + fileNameString, Snackbar.LENGTH_SHORT);

            // Add an open action to the snackbar.
            logcatSavedSnackbar.setAction(R.string.open, (View view) -> {
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

            // Show the logcat saved snackbar.
            logcatSavedSnackbar.show();
        } catch (Exception exception) {
            // Display a snackbar with the error message.
            Snackbar.make(logcatTextView, getString(R.string.error_saving_file) + "  " + exception.toString(), Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}