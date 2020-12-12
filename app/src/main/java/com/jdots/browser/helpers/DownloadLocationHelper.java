/*
 *   2020
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

package com.jdots.browser.helpers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.jdots.browser.R;

import java.io.File;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class DownloadLocationHelper {
    public String getDownloadLocation(Context context) {
        // Get the download location entry values string array.
        String[] downloadLocationEntryValuesStringArray = context.getResources().getStringArray(R.array.download_location_entry_values);

        // Get the two standard download directories.
        File publicDownloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File publicAppFilesDirectory = context.getExternalFilesDir(null);

        // Remove the incorrect lint warning below that the public app files directory might be null.
        assert publicAppFilesDirectory != null;

        // Convert the download directories to strings.
        String publicDownloadDirectoryString = publicDownloadDirectory.toString();
        String publicAppFilesDirectoryString = publicAppFilesDirectory.toString();

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the download location strings from the preferences.
        String downloadLocationString = sharedPreferences.getString("download_location", context.getString(R.string.download_location_default_value));
        String downloadCustomLocationString = sharedPreferences.getString("download_custom_location", context.getString(R.string.download_custom_location_default_value));

        // Define a string for the default file path.
        String defaultFilePath;

        // Set the default file path according to the download location.
        if (downloadLocationString.equals(downloadLocationEntryValuesStringArray[0])) {  // the download location is set to auto.
            // Set the download location summary text according to the storage permission status.
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
                // Use the public download directory.
                defaultFilePath = publicDownloadDirectoryString;
            } else {  // The storage permission has not been granted.
                // Use the public app files directory.
                defaultFilePath = publicAppFilesDirectoryString;
            }
        } else if (downloadLocationString.equals(downloadLocationEntryValuesStringArray[1])) {  // The download location is set to the app directory.
            // Use the public app files directory.
            defaultFilePath = publicAppFilesDirectoryString;
        } else if (downloadLocationString.equals(downloadLocationEntryValuesStringArray[2])) {  // The download location is set to the public directory.
            // Use the public download directory.
            defaultFilePath = publicDownloadDirectoryString;
        } else {  // The download location is set to custom.
            // Use the download custom location.
            defaultFilePath = downloadCustomLocationString;
        }

        // Return the default file path.
        return defaultFilePath;
    }
}