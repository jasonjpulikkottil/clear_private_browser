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

package com.jdots.browser.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;

import com.jdots.browser.R;
import com.jdots.browser.dialogs.SaveWebpageDialog;
import com.jdots.browser.helpers.ProxyHelper;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.NumberFormat;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class PrepareSaveDialog extends AsyncTask<String, Void, String[]> {
    // Define weak references.
    private final WeakReference<Activity> activityWeakReference;
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<FragmentManager> fragmentManagerWeakReference;

    // Define the class variables.
    private final int saveType;
    private final String userAgent;
    private final boolean cookiesEnabled;
    private String urlString;

    // The public constructor.
    public PrepareSaveDialog(Activity activity, Context context, FragmentManager fragmentManager, int saveType, String userAgent, boolean cookiesEnabled) {
        // Populate the weak references.
        activityWeakReference = new WeakReference<>(activity);
        contextWeakReference = new WeakReference<>(context);
        fragmentManagerWeakReference = new WeakReference<>(fragmentManager);

        // Store the class variables.
        this.saveType = saveType;
        this.userAgent = userAgent;
        this.cookiesEnabled = cookiesEnabled;
    }

    // Content dispositions can contain other text besides the file name, and they can be in any order.
    // Elements are separated by semicolons.  Sometimes the file names are contained in quotes.
    public static String getFileNameFromHeaders(Context context, String contentDispositionString, String contentTypeString, String urlString) {
        // Define a file name string.
        String fileNameString;

        // Only process the content disposition string if it isn't null.
        if (contentDispositionString != null) {  // The content disposition is not null.
            // Check to see if the content disposition contains a file name.
            if (contentDispositionString.contains("filename=")) {  // The content disposition contains a filename.
                // Get the part of the content disposition after `filename=`.
                fileNameString = contentDispositionString.substring(contentDispositionString.indexOf("filename=") + 9);

                // Remove any `;` and anything after it.  This removes any entries after the filename.
                if (fileNameString.contains(";")) {
                    // Remove the first `;` and everything after it.
                    fileNameString = fileNameString.substring(0, fileNameString.indexOf(";") - 1);
                }

                // Remove any `"` at the beginning of the string.
                if (fileNameString.startsWith("\"")) {
                    // Remove the first character.
                    fileNameString = fileNameString.substring(1);
                }

                // Remove any `"` at the end of the string.
                if (fileNameString.endsWith("\"")) {
                    // Remove the last character.
                    fileNameString = fileNameString.substring(0, fileNameString.length() - 1);
                }
            } else {  // The headers contain no useful information.
                // Get the file name string from the URL.
                fileNameString = getFileNameFromUrl(context, urlString, contentTypeString);
            }
        } else {  // The content disposition is null.
            // Get the file name string from the URL.
            fileNameString = getFileNameFromUrl(context, urlString, contentTypeString);
        }

        // Return the file name string.
        return fileNameString;
    }

    private static String getFileNameFromUrl(Context context, String urlString, String contentTypeString) {
        // Convert the URL string to a URI.
        Uri uri = Uri.parse(urlString);

        // Get the last path segment.
        String lastPathSegment = uri.getLastPathSegment();

        // Use a default file name if the last path segment is null.
        if (lastPathSegment == null) {
            lastPathSegment = context.getString(R.string.file);

            if (MimeTypeMap.getSingleton().hasMimeType(contentTypeString)) {  // The content type contains a MIME type.
                // Add the file extension that matches the MIME type.
                lastPathSegment = lastPathSegment + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentTypeString);
            }
        }

        // Return the last path segment as the file name.
        return lastPathSegment;
    }

    @Override
    protected String[] doInBackground(String... urlToSave) {
        // Get a handle for the activity and context.
        Activity activity = activityWeakReference.get();
        Context context = contextWeakReference.get();

        // Abort if the activity is gone.
        if (activity == null || activity.isFinishing()) {
            // Return a null string array.
            return null;
        }

        // Get the URL string.
        urlString = urlToSave[0];

        // Define the strings.
        String formattedFileSize;
        String fileNameString;

        // Populate the file size and name strings.
        if (urlString.startsWith("data:")) {  // The URL contains the entire data of an image.
            // Remove `data:` from the beginning of the URL.
            String urlWithoutData = urlString.substring(5);

            // Get the URL MIME type, which end with a `;`.
            String urlMimeType = urlWithoutData.substring(0, urlWithoutData.indexOf(";"));

            // Get the Base64 data, which begins after a `,`.
            String base64DataString = urlWithoutData.substring(urlWithoutData.indexOf(",") + 1);

            // Calculate the file size of the data URL.  Each Base64 character represents 6 bits.
            formattedFileSize = NumberFormat.getInstance().format(base64DataString.length() * 3 / 4) + " " + context.getString(R.string.bytes);

            // Set the file name according to the MIME type.
            fileNameString = context.getString(R.string.file) + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(urlMimeType);
        } else {  // The URL refers to the location of the data.
            // Initialize the formatted file size string.
            formattedFileSize = context.getString(R.string.unknown_size);

            // Because everything relating to requesting data from a webserver can throw errors, the entire section must catch exceptions.
            try {
                // Convert the URL string to a URL.
                URL url = new URL(urlString);

                // Instantiate the proxy helper.
                ProxyHelper proxyHelper = new ProxyHelper();

                // Get the current proxy.
                Proxy proxy = proxyHelper.getCurrentProxy(context);

                // Open a connection to the URL.  No data is actually sent at this point.
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection(proxy);

                // Add the user agent to the header property.
                httpUrlConnection.setRequestProperty("User-Agent", userAgent);

                // Add the cookies if they are enabled.
                if (cookiesEnabled) {
                    // Get the cookies for the current domain.
                    String cookiesString = CookieManager.getInstance().getCookie(url.toString());

                    // only add the cookies if they are not null.
                    if (cookiesString != null) {
                        // Add the cookies to the header property.
                        httpUrlConnection.setRequestProperty("Cookie", cookiesString);
                    }
                }

                // The actual network request is in a `try` bracket so that `disconnect()` is run in the `finally` section even if an error is encountered in the main block.
                try {
                    // Get the status code.  This initiates a network connection.
                    int responseCode = httpUrlConnection.getResponseCode();

                    // Check the response code.
                    if (responseCode >= 400) {  // The response code is an error message.
                        // Set the formatted file size to indicate a bad URL.
                        formattedFileSize = context.getString(R.string.invalid_url);

                        // Set the file name according to the URL.
                        fileNameString = getFileNameFromUrl(context, urlString, null);
                    } else {  // The response code is not an error message.
                        // Get the headers.
                        String contentLengthString = httpUrlConnection.getHeaderField("Content-Length");
                        String contentDispositionString = httpUrlConnection.getHeaderField("Content-Disposition");
                        String contentTypeString = httpUrlConnection.getContentType();

                        // Remove anything after the MIME type in the content type string.
                        if (contentTypeString.contains(";")) {
                            // Remove everything beginning with the `;`.
                            contentTypeString = contentTypeString.substring(0, contentTypeString.indexOf(";"));
                        }

                        // Only process the content length string if it isn't null.
                        if (contentLengthString != null) {
                            // Convert the content length string to a long.
                            long fileSize = Long.parseLong(contentLengthString);

                            // Format the file size.
                            formattedFileSize = NumberFormat.getInstance().format(fileSize) + " " + context.getString(R.string.bytes);
                        }

                        // Get the file name string from the content disposition.
                        fileNameString = getFileNameFromHeaders(context, contentDispositionString, contentTypeString, urlString);
                    }
                } finally {
                    // Disconnect the HTTP URL connection.
                    httpUrlConnection.disconnect();
                }
            } catch (Exception exception) {
                // Set the formatted file size to indicate a bad URL.
                formattedFileSize = context.getString(R.string.invalid_url);

                // Set the file name according to the URL.
                fileNameString = getFileNameFromUrl(context, urlString, null);
            }
        }

        // Return the formatted file size and name as a string array.
        return new String[]{formattedFileSize, fileNameString};
    }

    // `onPostExecute()` operates on the UI thread.
    @Override
    protected void onPostExecute(String[] fileStringArray) {
        // Get a handle for the activity and the fragment manager.
        Activity activity = activityWeakReference.get();
        FragmentManager fragmentManager = fragmentManagerWeakReference.get();

        // Abort if the activity is gone.
        if (activity == null || activity.isFinishing()) {
            // Exit.
            return;
        }

        // Instantiate the save dialog.
        DialogFragment saveDialogFragment = SaveWebpageDialog.saveWebpage(saveType, urlString, fileStringArray[0], fileStringArray[1], userAgent, cookiesEnabled);

        // Show the save dialog.  It must be named `save_dialog` so that the file picker can update the file name.
        saveDialogFragment.show(fragmentManager, activity.getString(R.string.save_dialog));
    }
}