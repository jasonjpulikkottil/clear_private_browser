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

package com.jdots.browser.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jdots.browser.BuildConfig;
import com.jdots.browser.R;
import com.jdots.browser.dialogs.StoragePermissionDialog;
import com.jdots.browser.helpers.DownloadLocationHelper;
import com.jdots.browser.helpers.FileNameHelper;
import com.jdots.browser.helpers.ImportExportDatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

public class ImportExportActivity extends AppCompatActivity implements StoragePermissionDialog.StoragePermissionDialogListener {
    // Define the encryption constants.
    private final int NO_ENCRYPTION = 0;
    private final int PASSWORD_ENCRYPTION = 1;
    private final int OPENPGP_ENCRYPTION = 2;

    // Define the activity result constants.
    private final int BROWSE_RESULT_CODE = 0;
    private final int OPENPGP_EXPORT_RESULT_CODE = 1;

    // Define the saved instance state constants.
    private final String PASSWORD_ENCRYPTED_TEXTINPUTLAYOUT_VISIBILITY = "password_encrypted_textinputlayout_visibility";
    private final String KITKAT_PASSWORD_ENCRYPTED_TEXTVIEW_VISIBILITY = "kitkat_password_encrypted_textview_visibility";
    private final String OPEN_KEYCHAIN_REQUIRED_TEXTVIEW_VISIBILITY = "open_keychain_required_textview_visibility";
    private final String FILE_LOCATION_CARD_VIEW = "file_location_card_view";
    private final String FILE_NAME_LINEARLAYOUT_VISIBILITY = "file_name_linearlayout_visibility";
    private final String FILE_DOES_NOT_EXIST_TEXTVIEW_VISIBILITY = "file_does_not_exist_textview_visibility";
    private final String FILE_EXISTS_WARNING_TEXTVIEW_VISIBILITY = "file_exists_warning_textview_visibility";
    private final String OPEN_KEYCHAIN_IMPORT_INSTRUCTIONS_TEXTVIEW_VISIBILITY = "open_keychain_import_instructions_textview_visibility";
    private final String IMPORT_EXPORT_BUTTON_VISIBILITY = "import_export_button_visibility";
    private final String FILE_NAME_TEXT = "file_name_text";
    private final String IMPORT_EXPORT_BUTTON_TEXT = "import_export_button_text";

    // Define the class views.
    TextInputLayout passwordEncryptionTextInputLayout;
    TextView kitKatPasswordEncryptionTextView;
    TextView openKeychainRequiredTextView;
    CardView fileLocationCardView;
    LinearLayout fileNameLinearLayout;
    EditText fileNameEditText;
    TextView fileDoesNotExistTextView;
    TextView fileExistsWarningTextView;
    TextView openKeychainImportInstructionsTextView;
    Button importExportButton;

    // Define the class variables.
    private boolean openKeychainInstalled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        // Set the content view.
        setContentView(R.layout.import_export_coordinatorlayout);

        // Set the support action bar.
        Toolbar toolbar = findViewById(R.id.import_export_toolbar);
        setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        ActionBar actionBar = getSupportActionBar();

        // Remove the incorrect lint warning that the action bar might be null.
        assert actionBar != null;

        // Display the home arrow on the support action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Find out if the system is running KitKat
        boolean runningKitKat = (Build.VERSION.SDK_INT == 19);

        // Find out if OpenKeychain is installed.
        try {
            openKeychainInstalled = !getPackageManager().getPackageInfo("org.sufficientlysecure.keychain", 0).versionName.isEmpty();
        } catch (PackageManager.NameNotFoundException exception) {
            openKeychainInstalled = false;
        }

        // Get handles for the views that need to be modified.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        passwordEncryptionTextInputLayout = findViewById(R.id.password_encryption_textinputlayout);
        EditText encryptionPasswordEditText = findViewById(R.id.password_encryption_edittext);
        kitKatPasswordEncryptionTextView = findViewById(R.id.kitkat_password_encryption_textview);
        openKeychainRequiredTextView = findViewById(R.id.openkeychain_required_textview);
        fileLocationCardView = findViewById(R.id.file_location_cardview);
        RadioButton importRadioButton = findViewById(R.id.import_radiobutton);
        RadioButton exportRadioButton = findViewById(R.id.export_radiobutton);
        fileNameLinearLayout = findViewById(R.id.file_name_linearlayout);
        fileNameEditText = findViewById(R.id.file_name_edittext);
        fileDoesNotExistTextView = findViewById(R.id.file_does_not_exist_textview);
        fileExistsWarningTextView = findViewById(R.id.file_exists_warning_textview);
        openKeychainImportInstructionsTextView = findViewById(R.id.openkeychain_import_instructions_textview);
        importExportButton = findViewById(R.id.import_export_button);
        TextView storagePermissionTextView = findViewById(R.id.import_export_storage_permission_textview);

        // Create an array adapter for the spinner.
        ArrayAdapter<CharSequence> encryptionArrayAdapter = ArrayAdapter.createFromResource(this, R.array.encryption_type, R.layout.spinner_item);

        // Set the drop down view resource on the spinner.
        encryptionArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        // Set the array adapter for the spinner.
        encryptionSpinner.setAdapter(encryptionArrayAdapter);

        // Instantiate the download location helper.
        DownloadLocationHelper downloadLocationHelper = new DownloadLocationHelper();

        // Get the default file path.
        String defaultFilePath = downloadLocationHelper.getDownloadLocation(this) + "/" + getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs";

        // Set the other default file paths.
        String defaultPasswordEncryptionFilePath = defaultFilePath + ".aes";
        String defaultPgpFilePath = defaultFilePath + ".pgp";

        // Set the default file path.
        fileNameEditText.setText(defaultFilePath);

        // Hide the storage permission text view if the permission has already been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            storagePermissionTextView.setVisibility(View.GONE);
        }

        // Update the UI when the spinner changes.
        encryptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case NO_ENCRYPTION:
                        // Hide the unneeded layout items.
                        passwordEncryptionTextInputLayout.setVisibility(View.GONE);
                        kitKatPasswordEncryptionTextView.setVisibility(View.GONE);
                        openKeychainRequiredTextView.setVisibility(View.GONE);
                        openKeychainImportInstructionsTextView.setVisibility(View.GONE);

                        // Show the file location card.
                        fileLocationCardView.setVisibility(View.VISIBLE);

                        // Show the file name linear layout if either import or export is checked.
                        if (importRadioButton.isChecked() || exportRadioButton.isChecked()) {
                            fileNameLinearLayout.setVisibility(View.VISIBLE);
                        }

                        // Reset the text of the import button, which may have been changed to `Decrypt`.
                        if (importRadioButton.isChecked()) {
                            importExportButton.setText(R.string.import_button);
                        }

                        // Reset the default file path.
                        fileNameEditText.setText(defaultFilePath);
                        break;

                    case PASSWORD_ENCRYPTION:
                        if (runningKitKat) {
                            // Show the KitKat password encryption message.
                            kitKatPasswordEncryptionTextView.setVisibility(View.VISIBLE);

                            // Hide the OpenPGP required text view and the file location card.
                            openKeychainRequiredTextView.setVisibility(View.GONE);
                            fileLocationCardView.setVisibility(View.GONE);
                        } else {
                            // Hide the OpenPGP layout items.
                            openKeychainRequiredTextView.setVisibility(View.GONE);
                            openKeychainImportInstructionsTextView.setVisibility(View.GONE);

                            // Show the password encryption layout items.
                            passwordEncryptionTextInputLayout.setVisibility(View.VISIBLE);

                            // Show the file location card.
                            fileLocationCardView.setVisibility(View.VISIBLE);

                            // Show the file name linear layout if either import or export is checked.
                            if (importRadioButton.isChecked() || exportRadioButton.isChecked()) {
                                fileNameLinearLayout.setVisibility(View.VISIBLE);
                            }

                            // Reset the text of the import button, which may have been changed to `Decrypt`.
                            if (importRadioButton.isChecked()) {
                                importExportButton.setText(R.string.import_button);
                            }

                            // Update the default file path.
                            fileNameEditText.setText(defaultPasswordEncryptionFilePath);
                        }
                        break;

                    case OPENPGP_ENCRYPTION:
                        // Hide the password encryption layout items.
                        passwordEncryptionTextInputLayout.setVisibility(View.GONE);
                        kitKatPasswordEncryptionTextView.setVisibility(View.GONE);

                        // Updated items based on the installation status of OpenKeychain.
                        if (openKeychainInstalled) {  // OpenKeychain is installed.
                            // Update the default file path.
                            fileNameEditText.setText(defaultPgpFilePath);

                            // Show the file location card.
                            fileLocationCardView.setVisibility(View.VISIBLE);

                            if (importRadioButton.isChecked()) {
                                // Show the file name linear layout and the OpenKeychain import instructions.
                                fileNameLinearLayout.setVisibility(View.VISIBLE);
                                openKeychainImportInstructionsTextView.setVisibility(View.VISIBLE);

                                // Set the text of the import button to be `Decrypt`.
                                importExportButton.setText(R.string.decrypt);
                            } else if (exportRadioButton.isChecked()) {
                                // Hide the file name linear layout and the OpenKeychain import instructions.
                                fileNameLinearLayout.setVisibility(View.GONE);
                                openKeychainImportInstructionsTextView.setVisibility(View.GONE);
                            }
                        } else {  // OpenKeychain is not installed.
                            // Show the OpenPGP required layout item.
                            openKeychainRequiredTextView.setVisibility(View.VISIBLE);

                            // Hide the file location card.
                            fileLocationCardView.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Update the status of the import/export button when the password changes.
        encryptionPasswordEditText.addTextChangedListener(new TextWatcher() {
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

                // Update the import/export button.
                if (importRadioButton.isChecked()) {  // The import radio button is checked.
                    // Enable the import button if the file and the password exists.
                    importExportButton.setEnabled(file.exists() && !encryptionPasswordEditText.getText().toString().isEmpty());
                } else if (exportRadioButton.isChecked()) {  // The export radio button is checked.
                    // Enable the export button if the file string and the password exists.
                    importExportButton.setEnabled(!fileNameString.isEmpty() && !encryptionPasswordEditText.getText().toString().isEmpty());
                }
            }
        });

        // Update the UI when the file name EditText changes.
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

                // Adjust the UI according to the encryption spinner position.
                switch (encryptionSpinner.getSelectedItemPosition()) {
                    case NO_ENCRYPTION:
                        // Determine if import or export is checked.
                        if (exportRadioButton.isChecked()) {  // The export radio button is checked.
                            // Hide the file does not exist text view.
                            fileDoesNotExistTextView.setVisibility(View.GONE);

                            // Display a warning if the file already exists.
                            if (file.exists()) {
                                fileExistsWarningTextView.setVisibility(View.VISIBLE);
                            } else {
                                fileExistsWarningTextView.setVisibility(View.GONE);
                            }

                            // Enable the export button if the file name is populated.
                            importExportButton.setEnabled(!fileNameString.isEmpty());
                        } else if (importRadioButton.isChecked()) {  // The import radio button is checked.
                            // Hide the file exists warning text view.
                            fileExistsWarningTextView.setVisibility(View.GONE);

                            // Check if the file exists.
                            if (file.exists()) {  // The file exists.
                                // Hide the notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.GONE);

                                // Enable the import button.
                                importExportButton.setEnabled(true);
                            } else {  // The file does not exist.
                                // Show a notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.VISIBLE);

                                // Disable the import button.
                                importExportButton.setEnabled(false);
                            }
                        } else {  // Neither radio button is checked.
                            // Hide the file notification text views.
                            fileExistsWarningTextView.setVisibility(View.GONE);
                            fileDoesNotExistTextView.setVisibility(View.GONE);
                        }
                        break;

                    case PASSWORD_ENCRYPTION:
                        // Determine if import or export is checked.
                        if (exportRadioButton.isChecked()) {  // The export radio button is checked.
                            // Hide the notification that the file does not exist.
                            fileDoesNotExistTextView.setVisibility(View.GONE);

                            // Display a warning if the file already exists.
                            if (file.exists()) {
                                fileExistsWarningTextView.setVisibility(View.VISIBLE);
                            } else {
                                fileExistsWarningTextView.setVisibility(View.GONE);
                            }

                            // Enable the export button if the file name and the password are populated.
                            importExportButton.setEnabled(!fileNameString.isEmpty() && !encryptionPasswordEditText.getText().toString().isEmpty());
                        } else if (importRadioButton.isChecked()) {  // The import radio button is checked.
                            // Hide the file exists warning text view.
                            fileExistsWarningTextView.setVisibility(View.GONE);

                            // Check if the file exists.
                            if (file.exists()) {  // The file exists.
                                // Hide the notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.GONE);

                                // Enable the import button if the password is populated.
                                importExportButton.setEnabled(!encryptionPasswordEditText.getText().toString().isEmpty());
                            } else {  // The file does not exist.
                                // Show a notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.VISIBLE);

                                // Disable the import button.
                                importExportButton.setEnabled(false);
                            }
                        } else {  // Neither radio button is checked.
                            // Hide the file notification text views.
                            fileExistsWarningTextView.setVisibility(View.GONE);
                            fileDoesNotExistTextView.setVisibility(View.GONE);
                        }
                        break;

                    case OPENPGP_ENCRYPTION:
                        // Hide the file exists warning text view.
                        fileExistsWarningTextView.setVisibility(View.GONE);

                        if (importRadioButton.isChecked()) {  // The import radio button is checked.
                            if (file.exists()) {  // The file exists.
                                // Hide the notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.GONE);

                                // Enable the import button if OpenKeychain is installed.
                                importExportButton.setEnabled(openKeychainInstalled);
                            } else {  // The file does not exist.
                                // Show the notification that the file does not exist.
                                fileDoesNotExistTextView.setVisibility(View.VISIBLE);

                                // Disable the import button.
                                importExportButton.setEnabled(false);
                            }
                        } else if (exportRadioButton.isChecked()) {  // The export radio button is checked.
                            // Hide the notification that the file does not exist.
                            fileDoesNotExistTextView.setVisibility(View.GONE);

                            // Enable the export button.
                            importExportButton.setEnabled(true);
                        } else {  // Neither radio button is checked.
                            // Hide the notification that the file does not exist.
                            fileDoesNotExistTextView.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });

        // Check to see if the activity has been restarted.
        if (savedInstanceState == null) {  // The app has not been restarted.
            // Initially hide the unneeded views.
            passwordEncryptionTextInputLayout.setVisibility(View.GONE);
            kitKatPasswordEncryptionTextView.setVisibility(View.GONE);
            openKeychainRequiredTextView.setVisibility(View.GONE);
            fileNameLinearLayout.setVisibility(View.GONE);
            fileDoesNotExistTextView.setVisibility(View.GONE);
            fileExistsWarningTextView.setVisibility(View.GONE);
            openKeychainImportInstructionsTextView.setVisibility(View.GONE);
            importExportButton.setVisibility(View.GONE);
        } else {  // The app has been restarted.
            // Restore the visibility of the views.
            passwordEncryptionTextInputLayout.setVisibility(savedInstanceState.getInt(PASSWORD_ENCRYPTED_TEXTINPUTLAYOUT_VISIBILITY));
            kitKatPasswordEncryptionTextView.setVisibility(savedInstanceState.getInt(KITKAT_PASSWORD_ENCRYPTED_TEXTVIEW_VISIBILITY));
            openKeychainRequiredTextView.setVisibility(savedInstanceState.getInt(OPEN_KEYCHAIN_REQUIRED_TEXTVIEW_VISIBILITY));
            fileLocationCardView.setVisibility(savedInstanceState.getInt(FILE_LOCATION_CARD_VIEW));
            fileNameLinearLayout.setVisibility(savedInstanceState.getInt(FILE_NAME_LINEARLAYOUT_VISIBILITY));
            fileDoesNotExistTextView.setVisibility(savedInstanceState.getInt(FILE_DOES_NOT_EXIST_TEXTVIEW_VISIBILITY));
            fileExistsWarningTextView.setVisibility(savedInstanceState.getInt(FILE_EXISTS_WARNING_TEXTVIEW_VISIBILITY));
            openKeychainImportInstructionsTextView.setVisibility(savedInstanceState.getInt(OPEN_KEYCHAIN_IMPORT_INSTRUCTIONS_TEXTVIEW_VISIBILITY));
            importExportButton.setVisibility(savedInstanceState.getInt(IMPORT_EXPORT_BUTTON_VISIBILITY));

            // Restore the text.
            fileNameEditText.post(() -> fileNameEditText.setText(savedInstanceState.getString(FILE_NAME_TEXT)));
            importExportButton.setText(savedInstanceState.getString(IMPORT_EXPORT_BUTTON_TEXT));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Save the visibility of the views.
        savedInstanceState.putInt(PASSWORD_ENCRYPTED_TEXTINPUTLAYOUT_VISIBILITY, passwordEncryptionTextInputLayout.getVisibility());
        savedInstanceState.putInt(KITKAT_PASSWORD_ENCRYPTED_TEXTVIEW_VISIBILITY, kitKatPasswordEncryptionTextView.getVisibility());
        savedInstanceState.putInt(OPEN_KEYCHAIN_REQUIRED_TEXTVIEW_VISIBILITY, openKeychainRequiredTextView.getVisibility());
        savedInstanceState.putInt(FILE_LOCATION_CARD_VIEW, fileLocationCardView.getVisibility());
        savedInstanceState.putInt(FILE_NAME_LINEARLAYOUT_VISIBILITY, fileNameLinearLayout.getVisibility());
        savedInstanceState.putInt(FILE_DOES_NOT_EXIST_TEXTVIEW_VISIBILITY, fileDoesNotExistTextView.getVisibility());
        savedInstanceState.putInt(FILE_EXISTS_WARNING_TEXTVIEW_VISIBILITY, fileExistsWarningTextView.getVisibility());
        savedInstanceState.putInt(OPEN_KEYCHAIN_IMPORT_INSTRUCTIONS_TEXTVIEW_VISIBILITY, openKeychainImportInstructionsTextView.getVisibility());
        savedInstanceState.putInt(IMPORT_EXPORT_BUTTON_VISIBILITY, importExportButton.getVisibility());

        // Save the text.
        savedInstanceState.putString(FILE_NAME_TEXT, fileNameEditText.getText().toString());
        savedInstanceState.putString(IMPORT_EXPORT_BUTTON_TEXT, importExportButton.getText().toString());
    }

    public void onClickRadioButton(View view) {
        // Get handles for the views.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        LinearLayout fileNameLinearLayout = findViewById(R.id.file_name_linearlayout);
        EditText passwordEncryptionEditText = findViewById(R.id.password_encryption_edittext);
        EditText fileNameEditText = findViewById(R.id.file_name_edittext);
        TextView fileDoesNotExistTextView = findViewById(R.id.file_does_not_exist_textview);
        TextView fileExistsWarningTextView = findViewById(R.id.file_exists_warning_textview);
        TextView openKeychainImportInstructionTextView = findViewById(R.id.openkeychain_import_instructions_textview);
        Button importExportButton = findViewById(R.id.import_export_button);

        // Get the current file name.
        String fileNameString = fileNameEditText.getText().toString();

        // Convert the file name string to a file.
        File file = new File(fileNameString);

        // Check to see if import or export was selected.
        switch (view.getId()) {
            case R.id.import_radiobutton:
                // Check to see if OpenPGP encryption is selected.
                if (encryptionSpinner.getSelectedItemPosition() == OPENPGP_ENCRYPTION) {  // OpenPGP encryption selected.
                    // Show the OpenKeychain import instructions.
                    openKeychainImportInstructionTextView.setVisibility(View.VISIBLE);

                    // Set the text on the import/export button to be `Decrypt`.
                    importExportButton.setText(R.string.decrypt);
                } else {  // OpenPGP encryption not selected.
                    // Hide the OpenKeychain import instructions.
                    openKeychainImportInstructionTextView.setVisibility(View.GONE);

                    // Set the text on the import/export button to be `Import`.
                    importExportButton.setText(R.string.import_button);
                }

                // Hide the file exists warning text view.
                fileExistsWarningTextView.setVisibility(View.GONE);

                // Display the file name views.
                fileNameLinearLayout.setVisibility(View.VISIBLE);
                importExportButton.setVisibility(View.VISIBLE);

                // Check to see if the file exists.
                if (file.exists()) {  // The file exists.
                    // Hide the notification that the file does not exist.
                    fileDoesNotExistTextView.setVisibility(View.GONE);

                    // Check to see if password encryption is selected.
                    if (encryptionSpinner.getSelectedItemPosition() == PASSWORD_ENCRYPTION) {  // Password encryption is selected.
                        // Enable the import button if the encryption password is populated.
                        importExportButton.setEnabled(!passwordEncryptionEditText.getText().toString().isEmpty());
                    } else {  // Password encryption is not selected.
                        // Enable the import/decrypt button.
                        importExportButton.setEnabled(true);
                    }
                } else {  // The file does not exist.
                    // Show the notification that the file does not exist.
                    fileDoesNotExistTextView.setVisibility(View.VISIBLE);

                    // Disable the import/decrypt button.
                    importExportButton.setEnabled(false);
                }
                break;

            case R.id.export_radiobutton:
                // Hide the OpenKeychain import instructions.
                openKeychainImportInstructionTextView.setVisibility(View.GONE);

                // Set the text on the import/export button to be `Export`.
                importExportButton.setText(R.string.export);

                // Show the import/export button.
                importExportButton.setVisibility(View.VISIBLE);

                // Check to see if OpenPGP encryption is selected.
                if (encryptionSpinner.getSelectedItemPosition() == OPENPGP_ENCRYPTION) {  // OpenPGP encryption is selected.
                    // Hide the file name views.
                    fileNameLinearLayout.setVisibility(View.GONE);
                    fileDoesNotExistTextView.setVisibility(View.GONE);
                    fileExistsWarningTextView.setVisibility(View.GONE);

                    // Enable the export button.
                    importExportButton.setEnabled(true);
                } else {  // OpenPGP encryption is not selected.
                    // Show the file name view.
                    fileNameLinearLayout.setVisibility(View.VISIBLE);

                    // Hide the notification that the file name does not exist.
                    fileDoesNotExistTextView.setVisibility(View.GONE);

                    // Display a warning if the file already exists.
                    if (file.exists()) {
                        fileExistsWarningTextView.setVisibility(View.VISIBLE);
                    } else {
                        fileExistsWarningTextView.setVisibility(View.GONE);
                    }

                    // Check the encryption type.
                    if (encryptionSpinner.getSelectedItemPosition() == NO_ENCRYPTION) {  // No encryption is selected.
                        // Enable the export button if the file name is populated.
                        importExportButton.setEnabled(!fileNameString.isEmpty());
                    } else {  // Password encryption is selected.
                        // Enable the export button if the file name and the password are populated.
                        importExportButton.setEnabled(!fileNameString.isEmpty() && !passwordEncryptionEditText.getText().toString().isEmpty());
                    }
                }
                break;
        }
    }

    public void browse(View view) {
        // Get a handle for the views.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        RadioButton importRadioButton = findViewById(R.id.import_radiobutton);

        // Check to see if import or export is selected.
        if (importRadioButton.isChecked()) {  // Import is selected.
            // Create the file picker intent.
            Intent importBrowseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Set the intent MIME type to include all files so that everything is visible.
            importBrowseIntent.setType("*/*");

            // Set the initial directory if the minimum API >= 26.
            if (Build.VERSION.SDK_INT >= 26) {
                importBrowseIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory());
            }

            // Request a file that can be opened.
            importBrowseIntent.addCategory(Intent.CATEGORY_OPENABLE);

            // Launch the file picker.
            startActivityForResult(importBrowseIntent, BROWSE_RESULT_CODE);
        } else {  // Export is selected
            // Create the file picker intent.
            Intent exportBrowseIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

            // Set the intent MIME type to include all files so that everything is visible.
            exportBrowseIntent.setType("*/*");

            // Set the initial export file name according to the encryption type.
            if (encryptionSpinner.getSelectedItemPosition() == NO_ENCRYPTION) {  // No encryption is selected.
                exportBrowseIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs");
            } else {  // Password encryption is selected.
                exportBrowseIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs.aes");
            }

            // Set the initial directory if the minimum API >= 26.
            if (Build.VERSION.SDK_INT >= 26) {
                exportBrowseIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStorageDirectory());
            }

            // Request a file that can be opened.
            exportBrowseIntent.addCategory(Intent.CATEGORY_OPENABLE);

            // Launch the file picker.
            startActivityForResult(exportBrowseIntent, BROWSE_RESULT_CODE);
        }
    }

    public void importExport(View view) {
        // Get a handle for the views.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        RadioButton importRadioButton = findViewById(R.id.import_radiobutton);
        RadioButton exportRadioButton = findViewById(R.id.export_radiobutton);

        // Check to see if the storage permission is needed.
        if ((encryptionSpinner.getSelectedItemPosition() == OPENPGP_ENCRYPTION) && exportRadioButton.isChecked()) {  // Permission not needed to export via OpenKeychain.
            // Export the settings.
            exportSettings();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
            // Check to see if import or export is selected.
            if (importRadioButton.isChecked()) {  // Import is selected.
                // Import the settings.
                importSettings();
            } else {  // Export is selected.
                // Export the settings.
                exportSettings();
            }
        } else {  // The storage permission has not been granted.
            // Get a handle for the file name EditText.
            EditText fileNameEditText = findViewById(R.id.file_name_edittext);

            // Get the file name string.
            String fileNameString = fileNameEditText.getText().toString();

            // Get the external private directory `File`.
            File externalPrivateDirectoryFile = getExternalFilesDir(null);

            // Remove the incorrect lint error below that the file might be null.
            assert externalPrivateDirectoryFile != null;

            // Get the external private directory string.
            String externalPrivateDirectory = externalPrivateDirectoryFile.toString();

            // Check to see if the file path is in the external private directory.
            if (fileNameString.startsWith(externalPrivateDirectory)) {  // The file path is in the external private directory.
                // Check to see if import or export is selected.
                if (importRadioButton.isChecked()) {  // Import is selected.
                    // Import the settings.
                    importSettings();
                } else {  // Export is selected.
                    // Export the settings.
                    exportSettings();
                }
            } else {  // The file path is in a public directory.
                // Check if the user has previously denied the storage permission.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {  // Show a dialog explaining the request first.
                    // Instantiate the storage permission alert dialog.
                    DialogFragment storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(0);

                    // Show the storage permission alert dialog.  The permission will be requested when the dialog is closed.
                    storagePermissionDialogFragment.show(getSupportFragmentManager(), getString(R.string.storage_permission));
                } else {  // Show the permission request directly.
                    // Request the storage permission.  The export will be run when it finishes.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        }
    }

    @Override
    public void onCloseStoragePermissionDialog(int type) {
        // Request the write external storage permission.  The import/export will be run when it finishes.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Get a handle for the import radiobutton.
        RadioButton importRadioButton = findViewById(R.id.import_radiobutton);

        // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {  // The storage permission was granted.
            // Run the import or export methods according to which radio button is selected.
            if (importRadioButton.isChecked()) {  // Import is selected.
                // Import the settings.
                importSettings();
            } else {  // Export is selected.
                // Export the settings.
                exportSettings();
            }
        } else {  // The storage permission was not granted.
            // Display an error snackbar.
            Snackbar.make(importRadioButton, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Run the default commands.
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case (BROWSE_RESULT_CODE):
                // Don't do anything if the user pressed back from the file picker.
                if (resultCode == Activity.RESULT_OK) {
                    // Get a handle for the views.
                    EditText fileNameEditText = findViewById(R.id.file_name_edittext);
                    TextView fileExistsWarningTextView = findViewById(R.id.file_exists_warning_textview);

                    // Instantiate the file name helper.
                    FileNameHelper fileNameHelper = new FileNameHelper();

                    // Get the file path URI from the intent.
                    Uri filePathUri = intent.getData();

                    // Use the file path from the intent if it exists.
                    if (filePathUri != null) {
                        // Convert the file name URI to a file name path.
                        String fileNamePath = fileNameHelper.convertUriToFileNamePath(filePathUri);

                        // Set the file name path as the text of the file name edit text.
                        fileNameEditText.setText(fileNamePath);

                        // Hide the file exists warning text view, because the file picker will have just created a file if export was selected.
                        fileExistsWarningTextView.setVisibility(View.GONE);
                    }
                }
                break;

            case OPENPGP_EXPORT_RESULT_CODE:
                // Get the temporary unencrypted export file.
                File temporaryUnencryptedExportFile = new File(getApplicationContext().getCacheDir() + "/" + getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs");

                // Delete the temporary unencrypted export file if it exists.
                if (temporaryUnencryptedExportFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    temporaryUnencryptedExportFile.delete();
                }
                break;
        }
    }

    private void exportSettings() {
        // Get a handle for the views.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        EditText fileNameEditText = findViewById(R.id.file_name_edittext);

        // Instantiate the import export database helper.
        ImportExportDatabaseHelper importExportDatabaseHelper = new ImportExportDatabaseHelper();

        // Get the export file string.
        String exportFileString = fileNameEditText.getText().toString();

        // Get the export and temporary unencrypted export files.
        File exportFile = new File(exportFileString);
        File temporaryUnencryptedExportFile = new File(getApplicationContext().getCacheDir() + "/" + getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs");

        // Create an export status string.
        String exportStatus;

        // Export according to the encryption type.
        switch (encryptionSpinner.getSelectedItemPosition()) {
            case NO_ENCRYPTION:
                // Export the unencrypted file.
                exportStatus = importExportDatabaseHelper.exportUnencrypted(exportFile, this);

                // Show a disposition snackbar.
                if (exportStatus.equals(ImportExportDatabaseHelper.EXPORT_SUCCESSFUL)) {
                    Snackbar.make(fileNameEditText, getString(R.string.export_successful), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(fileNameEditText, getString(R.string.export_failed) + "  " + exportStatus, Snackbar.LENGTH_INDEFINITE).show();
                }
                break;

            case PASSWORD_ENCRYPTION:
                // Create an unencrypted export in a private directory.
                exportStatus = importExportDatabaseHelper.exportUnencrypted(temporaryUnencryptedExportFile, this);

                try {
                    // Create an unencrypted export file input stream.
                    FileInputStream unencryptedExportFileInputStream = new FileInputStream(temporaryUnencryptedExportFile);

                    // Delete the encrypted export file if it exists.
                    if (exportFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        exportFile.delete();
                    }

                    // Create an encrypted export file output stream.
                    FileOutputStream encryptedExportFileOutputStream = new FileOutputStream(exportFile);

                    // Get a handle for the encryption password EditText.
                    EditText encryptionPasswordEditText = findViewById(R.id.password_encryption_edittext);

                    // Get the encryption password.
                    String encryptionPasswordString = encryptionPasswordEditText.getText().toString();

                    // Initialize a secure random number generator.
                    SecureRandom secureRandom = new SecureRandom();

                    // Get a 256 bit (32 byte) random salt.
                    byte[] saltByteArray = new byte[32];
                    secureRandom.nextBytes(saltByteArray);

                    // Convert the encryption password to a byte array.
                    byte[] encryptionPasswordByteArray = encryptionPasswordString.getBytes(StandardCharsets.UTF_8);

                    // Append the salt to the encryption password byte array.  This protects against rainbow table attacks.
                    byte[] encryptionPasswordWithSaltByteArray = new byte[encryptionPasswordByteArray.length + saltByteArray.length];
                    System.arraycopy(encryptionPasswordByteArray, 0, encryptionPasswordWithSaltByteArray, 0, encryptionPasswordByteArray.length);
                    System.arraycopy(saltByteArray, 0, encryptionPasswordWithSaltByteArray, encryptionPasswordByteArray.length, saltByteArray.length);

                    // Get a SHA-512 message digest.
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");

                    // Hash the salted encryption password.  Otherwise, any characters after the 32nd character in the password are ignored.
                    byte[] hashedEncryptionPasswordWithSaltByteArray = messageDigest.digest(encryptionPasswordWithSaltByteArray);

                    // Truncate the encryption password byte array to 256 bits (32 bytes).
                    byte[] truncatedHashedEncryptionPasswordWithSaltByteArray = Arrays.copyOf(hashedEncryptionPasswordWithSaltByteArray, 32);

                    // Create an AES secret key from the encryption password byte array.
                    SecretKeySpec secretKey = new SecretKeySpec(truncatedHashedEncryptionPasswordWithSaltByteArray, "AES");

                    // Generate a random 12 byte initialization vector.  According to NIST, a 12 byte initialization vector is more secure than a 16 byte one.
                    byte[] initializationVector = new byte[12];
                    secureRandom.nextBytes(initializationVector);

                    // Get a Advanced Encryption Standard, Galois/Counter Mode, No Padding cipher instance. Galois/Counter mode protects against modification of the ciphertext.  It doesn't use padding.
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

                    // Set the GCM tag length to be 128 bits (the maximum) and apply the initialization vector.
                    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, initializationVector);

                    // Initialize the cipher.
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

                    // Add the salt and the initialization vector to the export file.
                    encryptedExportFileOutputStream.write(saltByteArray);
                    encryptedExportFileOutputStream.write(initializationVector);

                    // Create a cipher output stream.
                    CipherOutputStream cipherOutputStream = new CipherOutputStream(encryptedExportFileOutputStream, cipher);

                    // Initialize variables to store data as it is moved from the unencrypted export file input stream to the cipher output stream.  Move 128 bits (16 bytes) at a time.
                    int numberOfBytesRead;
                    byte[] encryptedBytes = new byte[16];

                    // Read up to 128 bits (16 bytes) of data from the unencrypted export file stream.  `-1` will be returned when the end of the file is reached.
                    while ((numberOfBytesRead = unencryptedExportFileInputStream.read(encryptedBytes)) != -1) {
                        // Write the data to the cipher output stream.
                        cipherOutputStream.write(encryptedBytes, 0, numberOfBytesRead);
                    }

                    // Close the streams.
                    cipherOutputStream.flush();
                    cipherOutputStream.close();
                    encryptedExportFileOutputStream.close();
                    unencryptedExportFileInputStream.close();

                    // Wipe the encryption data from memory.
                    //noinspection UnusedAssignment
                    encryptionPasswordString = "";
                    Arrays.fill(saltByteArray, (byte) 0);
                    Arrays.fill(encryptionPasswordByteArray, (byte) 0);
                    Arrays.fill(encryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(hashedEncryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(truncatedHashedEncryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(initializationVector, (byte) 0);
                    Arrays.fill(encryptedBytes, (byte) 0);

                    // Delete the temporary unencrypted export file.
                    //noinspection ResultOfMethodCallIgnored
                    temporaryUnencryptedExportFile.delete();
                } catch (Exception exception) {
                    exportStatus = exception.toString();
                }

                // Show a disposition snackbar.
                if (exportStatus.equals(ImportExportDatabaseHelper.EXPORT_SUCCESSFUL)) {
                    Snackbar.make(fileNameEditText, getString(R.string.export_successful), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(fileNameEditText, getString(R.string.export_failed) + "  " + exportStatus, Snackbar.LENGTH_INDEFINITE).show();
                }
                break;

            case OPENPGP_ENCRYPTION:
                // Create an unencrypted export in the private location.
                importExportDatabaseHelper.exportUnencrypted(temporaryUnencryptedExportFile, this);

                // Create an encryption intent for OpenKeychain.
                Intent openKeychainEncryptIntent = new Intent("org.sufficientlysecure.keychain.action.ENCRYPT_DATA");

                // Include the temporary unencrypted export file URI.
                openKeychainEncryptIntent.setData(FileProvider.getUriForFile(this, getString(R.string.file_provider), temporaryUnencryptedExportFile));

                // Allow OpenKeychain to read the file URI.
                openKeychainEncryptIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Send the intent to the OpenKeychain package.
                openKeychainEncryptIntent.setPackage("org.sufficientlysecure.keychain");

                // Make it so.
                startActivityForResult(openKeychainEncryptIntent, OPENPGP_EXPORT_RESULT_CODE);
                break;
        }

        // Add the file to the list of recent files.  This doesn't currently work, but maybe it will someday.
        MediaScannerConnection.scanFile(this, new String[]{exportFileString}, new String[]{"application/x-sqlite3"}, null);
    }

    private void importSettings() {
        // Get a handle for the views.
        Spinner encryptionSpinner = findViewById(R.id.encryption_spinner);
        EditText fileNameEditText = findViewById(R.id.file_name_edittext);

        // Instantiate the import export database helper.
        ImportExportDatabaseHelper importExportDatabaseHelper = new ImportExportDatabaseHelper();

        // Get the import file.
        File importFile = new File(fileNameEditText.getText().toString());

        // Initialize the import status string
        String importStatus = "";

        // Import according to the encryption type.
        switch (encryptionSpinner.getSelectedItemPosition()) {
            case NO_ENCRYPTION:
                // Import the unencrypted file.
                importStatus = importExportDatabaseHelper.importUnencrypted(importFile, this);
                break;

            case PASSWORD_ENCRYPTION:
                // Use a private temporary import location.
                File temporaryUnencryptedImportFile = new File(getApplicationContext().getCacheDir() + "/" + getString(R.string.settings) + " " + BuildConfig.VERSION_NAME + ".pbs");

                try {
                    // Create an encrypted import file input stream.
                    FileInputStream encryptedImportFileInputStream = new FileInputStream(importFile);

                    // Delete the temporary import file if it exists.
                    if (temporaryUnencryptedImportFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        temporaryUnencryptedImportFile.delete();
                    }

                    // Create an unencrypted import file output stream.
                    FileOutputStream unencryptedImportFileOutputStream = new FileOutputStream(temporaryUnencryptedImportFile);

                    // Get a handle for the encryption password EditText.
                    EditText encryptionPasswordEditText = findViewById(R.id.password_encryption_edittext);

                    // Get the encryption password.
                    String encryptionPasswordString = encryptionPasswordEditText.getText().toString();

                    // Get the salt from the beginning of the import file.
                    byte[] saltByteArray = new byte[32];
                    //noinspection ResultOfMethodCallIgnored
                    encryptedImportFileInputStream.read(saltByteArray);

                    // Get the initialization vector from the import file.
                    byte[] initializationVector = new byte[12];
                    //noinspection ResultOfMethodCallIgnored
                    encryptedImportFileInputStream.read(initializationVector);

                    // Convert the encryption password to a byte array.
                    byte[] encryptionPasswordByteArray = encryptionPasswordString.getBytes(StandardCharsets.UTF_8);

                    // Append the salt to the encryption password byte array.  This protects against rainbow table attacks.
                    byte[] encryptionPasswordWithSaltByteArray = new byte[encryptionPasswordByteArray.length + saltByteArray.length];
                    System.arraycopy(encryptionPasswordByteArray, 0, encryptionPasswordWithSaltByteArray, 0, encryptionPasswordByteArray.length);
                    System.arraycopy(saltByteArray, 0, encryptionPasswordWithSaltByteArray, encryptionPasswordByteArray.length, saltByteArray.length);

                    // Get a SHA-512 message digest.
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");

                    // Hash the salted encryption password.  Otherwise, any characters after the 32nd character in the password are ignored.
                    byte[] hashedEncryptionPasswordWithSaltByteArray = messageDigest.digest(encryptionPasswordWithSaltByteArray);

                    // Truncate the encryption password byte array to 256 bits (32 bytes).
                    byte[] truncatedHashedEncryptionPasswordWithSaltByteArray = Arrays.copyOf(hashedEncryptionPasswordWithSaltByteArray, 32);

                    // Create an AES secret key from the encryption password byte array.
                    SecretKeySpec secretKey = new SecretKeySpec(truncatedHashedEncryptionPasswordWithSaltByteArray, "AES");

                    // Get a Advanced Encryption Standard, Galois/Counter Mode, No Padding cipher instance. Galois/Counter mode protects against modification of the ciphertext.  It doesn't use padding.
                    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

                    // Set the GCM tag length to be 128 bits (the maximum) and apply the initialization vector.
                    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, initializationVector);

                    // Initialize the cipher.
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

                    // Create a cipher input stream.
                    CipherInputStream cipherInputStream = new CipherInputStream(encryptedImportFileInputStream, cipher);

                    // Initialize variables to store data as it is moved from the cipher input stream to the unencrypted import file output stream.  Move 128 bits (16 bytes) at a time.
                    int numberOfBytesRead;
                    byte[] decryptedBytes = new byte[16];

                    // Read up to 128 bits (16 bytes) of data from the cipher input stream.  `-1` will be returned when the end fo the file is reached.
                    while ((numberOfBytesRead = cipherInputStream.read(decryptedBytes)) != -1) {
                        // Write the data to the unencrypted import file output stream.
                        unencryptedImportFileOutputStream.write(decryptedBytes, 0, numberOfBytesRead);
                    }

                    // Close the streams.
                    unencryptedImportFileOutputStream.flush();
                    unencryptedImportFileOutputStream.close();
                    cipherInputStream.close();
                    encryptedImportFileInputStream.close();

                    // Wipe the encryption data from memory.
                    //noinspection UnusedAssignment
                    encryptionPasswordString = "";
                    Arrays.fill(saltByteArray, (byte) 0);
                    Arrays.fill(initializationVector, (byte) 0);
                    Arrays.fill(encryptionPasswordByteArray, (byte) 0);
                    Arrays.fill(encryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(hashedEncryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(truncatedHashedEncryptionPasswordWithSaltByteArray, (byte) 0);
                    Arrays.fill(decryptedBytes, (byte) 0);

                    // Import the unencrypted database from the private location.
                    importStatus = importExportDatabaseHelper.importUnencrypted(temporaryUnencryptedImportFile, this);

                    // Delete the temporary unencrypted import file.
                    //noinspection ResultOfMethodCallIgnored
                    temporaryUnencryptedImportFile.delete();
                } catch (Exception exception) {
                    importStatus = exception.toString();
                }
                break;

            case OPENPGP_ENCRYPTION:
                try {
                    // Create an decryption intent for OpenKeychain.
                    Intent openKeychainDecryptIntent = new Intent("org.sufficientlysecure.keychain.action.DECRYPT_DATA");

                    // Include the URI to be decrypted.
                    openKeychainDecryptIntent.setData(FileProvider.getUriForFile(this, getString(R.string.file_provider), importFile));

                    // Allow OpenKeychain to read the file URI.
                    openKeychainDecryptIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Send the intent to the OpenKeychain package.
                    openKeychainDecryptIntent.setPackage("org.sufficientlysecure.keychain");

                    // Make it so.
                    startActivity(openKeychainDecryptIntent);
                } catch (IllegalArgumentException exception) {  // The file import location is not valid.
                    // Display a snack bar with the import error.
                    Snackbar.make(fileNameEditText, getString(R.string.import_failed) + "  " + exception.toString(), Snackbar.LENGTH_INDEFINITE).show();
                }
                break;
        }

        // Respond to the import disposition.
        if (importStatus.equals(ImportExportDatabaseHelper.IMPORT_SUCCESSFUL)) {  // The import was successful.
            // Create an intent to restart Clear Browser.
            Intent restartIntent = getParentActivityIntent();

            // Assert that the intent is not null to remove the lint error below.
            assert restartIntent != null;

            // `Intent.FLAG_ACTIVITY_CLEAR_TASK` removes all activities from the stack.  It requires `Intent.FLAG_ACTIVITY_NEW_TASK`.
            restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Create a restart handler.
            Handler restartHandler = new Handler();

            // Create a restart runnable.
            Runnable restartRunnable = () -> {
                // Restart Clear Browser.
                startActivity(restartIntent);

                // Kill this instance of Clear Browser.  Otherwise, the app exhibits sporadic behavior after the restart.
                System.exit(0);
            };

            // Restart Clear Browser after 150 milliseconds to allow enough time for the preferences to be saved.
            restartHandler.postDelayed(restartRunnable, 150);

        } else if (!(encryptionSpinner.getSelectedItemPosition() == OPENPGP_ENCRYPTION)) {  // The import was not successful.
            // Display a snack bar with the import error.
            Snackbar.make(fileNameEditText, getString(R.string.import_failed) + "  " + importStatus, Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}