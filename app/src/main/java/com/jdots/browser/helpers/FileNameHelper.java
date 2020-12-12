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

package com.jdots.browser.helpers;

import android.net.Uri;
import android.os.Environment;

public class FileNameHelper {
    public String convertUriToFileNamePath(Uri Uri) {
        // Initialize a file name path string.
        String fileNamePath = "";

        // Convert the URI to a raw file name path.
        String rawFileNamePath = Uri.getPath();

        // Only process the raw file name path if it is not null.
        if (rawFileNamePath != null) {
            // Check to see if the file name Path includes a valid storage location.
            if (rawFileNamePath.contains(":")) {  // The path contains a `:`.
                // Split the path into the initial content uri and the final path information.
                String fileNameContentPath = rawFileNamePath.substring(0, rawFileNamePath.indexOf(":"));
                String fileNameFinalPath = rawFileNamePath.substring(rawFileNamePath.indexOf(":") + 1);

                // Check to see if the current file name final patch is a complete, valid path.
                if (fileNameFinalPath.startsWith("/storage/emulated/")) {  // The existing file name final path is a complete, valid path.
                    // Use the provided file name path as is.
                    fileNamePath = fileNameFinalPath;
                } else { // The existing file name final path is not a complete, valid path.
                    // Construct the file name path.
                    switch (fileNameContentPath) {
                        // The documents home has a special content path.
                        case "/document/home":
                            fileNamePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + fileNameFinalPath;
                            break;

                        // Everything else for the primary user should be in `/document/primary`.
                        case "/document/primary":
                            fileNamePath = Environment.getExternalStorageDirectory() + "/" + fileNameFinalPath;
                            break;

                        // Just in case, catch everything else and place it in the external storage directory.
                        default:
                            fileNamePath = Environment.getExternalStorageDirectory() + "/" + fileNameFinalPath;
                            break;
                    }
                }
            } else {  // The path does not contain a `:`.
                // Use the raw file name path.
                fileNamePath = rawFileNamePath;
            }
        }

        // Return the file name path string.
        return fileNamePath;
    }
}