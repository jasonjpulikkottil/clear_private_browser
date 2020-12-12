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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;

import com.google.android.material.snackbar.Snackbar;
import com.jdots.browser.R;
import com.jdots.browser.helpers.ProxyHelper;
import com.jdots.browser.views.NoSwipeViewPager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.NumberFormat;

import androidx.core.content.FileProvider;

public class SaveUrl extends AsyncTask<String, Long, String> {
    // Define a weak references.
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<Activity> activityWeakReference;

    // Define a success string constant.
    private final String SUCCESS = "Success";

    // Define the class variables.
    private final String filePathString;
    private final String userAgent;
    private final boolean cookiesEnabled;
    private Snackbar savingFileSnackbar;

    // The public constructor.
    public SaveUrl(Context context, Activity activity, String filePathString, String userAgent, boolean cookiesEnabled) {
        // Populate weak references to the calling context and activity.
        contextWeakReference = new WeakReference<>(context);
        activityWeakReference = new WeakReference<>(activity);

        // Store the class variables.
        this.filePathString = filePathString;
        this.userAgent = userAgent;
        this.cookiesEnabled = cookiesEnabled;
    }

    // `onPreExecute()` operates on the UI thread.
    @Override
    protected void onPreExecute() {
        // Get a handle for the activity.
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return;
        }

        // Get a handle for the no swipe view pager.
        NoSwipeViewPager noSwipeViewPager = activity.findViewById(R.id.webviewpager);

        // Create a saving file snackbar.
        savingFileSnackbar = Snackbar.make(noSwipeViewPager, activity.getString(R.string.saving_file) + "  0% - " + filePathString, Snackbar.LENGTH_INDEFINITE);

        // Display the saving file snackbar.
        savingFileSnackbar.show();
    }

    @Override
    protected String doInBackground(String... urlToSave) {
        // Get handles for the context and activity.
        Context context = contextWeakReference.get();
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return null;
        }

        // Define a save disposition string.
        String saveDisposition = SUCCESS;

        try {
            // Get the file.
            File file = new File(filePathString);

            // Delete the file if it exists.
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

            // Create a new file.
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            // Create an output file stream.
            OutputStream fileOutputStream = new FileOutputStream(file);

            // Save the URL.
            if (urlToSave[0].startsWith("data:")) {  // The URL contains the entire data of an image.
                // Get the Base64 data, which begins after a `,`.
                String base64DataString = urlToSave[0].substring(urlToSave[0].indexOf(",") + 1);

                // Decode the Base64 string to a byte array.
                byte[] base64DecodedDataByteArray = Base64.decode(base64DataString, Base64.DEFAULT);

                // Write the Base64 byte array to the file output stream.
                fileOutputStream.write(base64DecodedDataByteArray);

                // Close the file output stream.
                fileOutputStream.close();
            } else {  // The URL points to the data location on the internet.
                // Get the URL from the calling activity.
                URL url = new URL(urlToSave[0]);

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

                    // Only add the cookies if they are not null.
                    if (cookiesString != null) {
                        // Add the cookies to the header property.
                        httpUrlConnection.setRequestProperty("Cookie", cookiesString);
                    }
                }

                // The actual network request is in a `try` bracket so that `disconnect()` is run in the `finally` section even if an error is encountered in the main block.
                try {
                    // Get the content length header, which causes the connection to the server to be made.
                    String contentLengthString = httpUrlConnection.getHeaderField("Content-Length");

                    // Define the file size long.
                    long fileSize;

                    // Make sure the content length isn't null.
                    if (contentLengthString != null) {  // The content length isn't null.
                        // Convert the content length to an long.
                        fileSize = Long.parseLong(contentLengthString);
                    } else {  // The content length is null.
                        // Set the file size to be `-1`.
                        fileSize = -1;
                    }

                    // Get the response body stream.
                    InputStream inputStream = new BufferedInputStream(httpUrlConnection.getInputStream());

                    // Initialize the conversion buffer byte array.
                    byte[] conversionBufferByteArray = new byte[1024];

                    // Initialize the downloaded kilobytes counter.
                    long downloadedKilobytesCounter = 0;

                    // Define the buffer length variable.
                    int bufferLength;

                    // Attempt to read data from the input stream and store it in the output stream.  Also store the amount of data read in the buffer length variable.
                    while ((bufferLength = inputStream.read(conversionBufferByteArray)) > 0) {  // Proceed while the amount of data stored in the buffer in > 0.
                        // Write the contents of the conversion buffer to the file output stream.
                        fileOutputStream.write(conversionBufferByteArray, 0, bufferLength);

                        // Update the file download progress snackbar.
                        if (fileSize == -1) {  // The file size is unknown.
                            // Negatively update the downloaded kilobytes counter.
                            downloadedKilobytesCounter = downloadedKilobytesCounter - bufferLength;

                            publishProgress(downloadedKilobytesCounter);
                        } else {  // The file size is known.
                            // Update the downloaded kilobytes counter.
                            downloadedKilobytesCounter = downloadedKilobytesCounter + bufferLength;

                            // Calculate the download percentage.
                            long downloadPercentage = (downloadedKilobytesCounter * 100) / fileSize;

                            // Update the download percentage.
                            publishProgress(downloadPercentage);
                        }
                    }

                    // Close the input stream.
                    inputStream.close();

                    // Close the file output stream.
                    fileOutputStream.close();
                } finally {
                    // Disconnect the HTTP URL connection.
                    httpUrlConnection.disconnect();
                }
            }

            // Create a media scanner intent, which adds items like pictures to Android's recent file list.
            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            // Add the URI to the media scanner intent.
            mediaScannerIntent.setData(Uri.fromFile(file));

            // Make it so.
            activity.sendBroadcast(mediaScannerIntent);
        } catch (Exception exception) {
            // Store the error in the save disposition string.
            saveDisposition = exception.toString();
        }

        // Return the save disposition string.
        return saveDisposition;
    }

    // `onProgressUpdate()` operates on the UI thread.
    @Override
    protected void onProgressUpdate(Long... downloadPercentage) {
        // Get a handle for the activity.
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return;
        }

        // Check to see if a download percentage has been calculated.
        if (downloadPercentage[0] < 0) {  // There is no download percentage.  The negative number represents the raw downloaded kilobytes.
            // Calculate the number of bytes downloaded.  When the `downloadPercentage` is negative, it is actually the raw number of kilobytes downloaded.
            long numberOfBytesDownloaded = -downloadPercentage[0];

            // Format the number of bytes downloaded.
            String formattedNumberOfBytesDownloaded = NumberFormat.getInstance().format(numberOfBytesDownloaded);

            // Update the snackbar.
            savingFileSnackbar.setText(activity.getString(R.string.saving_file) + "  " + formattedNumberOfBytesDownloaded + " " + activity.getString(R.string.bytes) + " - " + filePathString);
        } else {  // There is a download percentage.
            // Update the snackbar.
            savingFileSnackbar.setText(activity.getString(R.string.saving_file) + "  " + downloadPercentage[0] + "% - " + filePathString);
        }
    }

    // `onPostExecute()` operates on the UI thread.
    @Override
    protected void onPostExecute(String saveDisposition) {
        // Get handles for the context and activity.
        Context context = contextWeakReference.get();
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return;
        }

        // Get a handle for the no swipe view pager.
        NoSwipeViewPager noSwipeViewPager = activity.findViewById(R.id.webviewpager);

        // Dismiss the saving file snackbar.
        savingFileSnackbar.dismiss();

        // Display a save disposition snackbar.
        if (saveDisposition.equals(SUCCESS)) {
            // Create a file saved snackbar.
            Snackbar fileSavedSnackbar = Snackbar.make(noSwipeViewPager, activity.getString(R.string.file_saved) + "  " + filePathString, Snackbar.LENGTH_LONG);

            // Add an open action if the file is not an APK on API >= 26 (that scenario requires the REQUEST_INSTALL_PACKAGES permission).
            if (!(Build.VERSION.SDK_INT >= 26 && filePathString.endsWith(".apk"))) {
                fileSavedSnackbar.setAction(R.string.open, (View view) -> {
                    // Get a file for the file path string.
                    File file = new File(filePathString);

                    // Declare a file URI variable.
                    Uri fileUri;

                    // Get the URI for the file according to the Android version.
                    if (Build.VERSION.SDK_INT >= 24) {  // Use a file provider.
                        fileUri = FileProvider.getUriForFile(context, activity.getString(R.string.file_provider), file);
                    } else {  // Get the raw file path URI.
                        fileUri = Uri.fromFile(file);
                    }

                    // Get a handle for the content resolver.
                    ContentResolver contentResolver = context.getContentResolver();

                    // Create an open intent with `ACTION_VIEW`.
                    Intent openIntent = new Intent(Intent.ACTION_VIEW);

                    // Set the URI and the MIME type.
                    if (filePathString.endsWith("apk") || filePathString.endsWith("APK")) {  // Force detection of APKs.
                        openIntent.setDataAndType(fileUri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
                    } else {  // Autodetect the MIME type.
                        openIntent.setDataAndType(fileUri, contentResolver.getType(fileUri));
                    }

                    // Allow the app to read the file URI.
                    openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Show the chooser.
                    activity.startActivity(Intent.createChooser(openIntent, context.getString(R.string.open)));
                });
            }

            // Show the file saved snackbar.
            fileSavedSnackbar.show();
        } else {
            // Display the file saving error.
            Snackbar.make(noSwipeViewPager, activity.getString(R.string.error_saving_file) + "  " + saveDisposition, Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}