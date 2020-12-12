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

package com.jdots.browser.asynctasks;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.jdots.browser.R;
import com.jdots.browser.views.NestedScrollWebView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import androidx.core.content.FileProvider;

public class SaveWebpageImage extends AsyncTask<Void, Void, String> {
    // Declare the weak references.
    private final WeakReference<Context> contextWeakReference;
    private final WeakReference<Activity> activityWeakReference;
    private final WeakReference<NestedScrollWebView> nestedScrollWebViewWeakReference;

    // Declare the class constants.
    private final String SUCCESS = "Success";
    private final String filePathString;
    // Declare the class variables.
    private Snackbar savingImageSnackbar;
    private Bitmap webpageBitmap;

    // The public constructor.
    public SaveWebpageImage(Context context, Activity activity, String filePathString, NestedScrollWebView nestedScrollWebView) {
        // Populate the weak references.
        contextWeakReference = new WeakReference<>(context);
        activityWeakReference = new WeakReference<>(activity);
        nestedScrollWebViewWeakReference = new WeakReference<>(nestedScrollWebView);

        // Populate the class variables.
        this.filePathString = filePathString;
    }

    // `onPreExecute()` operates on the UI thread.
    @Override
    protected void onPreExecute() {
        // Get handles for the activity and the nested scroll WebView.
        Activity activity = activityWeakReference.get();
        NestedScrollWebView nestedScrollWebView = nestedScrollWebViewWeakReference.get();

        // Abort if the activity or the nested scroll WebView is gone.
        if ((activity == null) || activity.isFinishing() || nestedScrollWebView == null) {
            return;
        }

        // Create a saving image snackbar.
        savingImageSnackbar = Snackbar.make(nestedScrollWebView, activity.getString(R.string.processing_image) + "  " + filePathString, Snackbar.LENGTH_INDEFINITE);

        // Display the saving image snackbar.
        savingImageSnackbar.show();

        // Create a webpage bitmap.  Once the Minimum API >= 26 Bitmap.Config.RBGA_F16 can be used instead of ARGB_8888.  The nested scroll WebView commands must be run on the UI thread.
        webpageBitmap = Bitmap.createBitmap(nestedScrollWebView.getHorizontalScrollRange(), nestedScrollWebView.getVerticalScrollRange(), Bitmap.Config.ARGB_8888);

        // Create a canvas.
        Canvas webpageCanvas = new Canvas(webpageBitmap);

        // Draw the current webpage onto the bitmap.  The nested scroll WebView commands must be run on the UI thread.
        nestedScrollWebView.draw(webpageCanvas);
    }

    @Override
    protected String doInBackground(Void... Void) {
        // Get a handle for the activity.
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return "";
        }

        // Create a webpage PNG byte array output stream.
        ByteArrayOutputStream webpageByteArrayOutputStream = new ByteArrayOutputStream();

        // Convert the bitmap to a PNG.  `0` is for lossless compression (the only option for a PNG).  This compression takes a long time.  Once the minimum API >= 30 this could be replaced with WEBP_LOSSLESS.
        webpageBitmap.compress(Bitmap.CompressFormat.PNG, 0, webpageByteArrayOutputStream);

        // Get a file for the image.
        File imageFile = new File(filePathString);

        // Delete the current file if it exists.
        if (imageFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            imageFile.delete();
        }

        // Create a file creation disposition string.
        String fileCreationDisposition = SUCCESS;

        try {
            // Create an image file output stream.
            FileOutputStream imageFileOutputStream = new FileOutputStream(imageFile);

            // Write the webpage image to the image file.
            webpageByteArrayOutputStream.writeTo(imageFileOutputStream);

            // Create a media scanner intent, which adds items like pictures to Android's recent file list.
            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            // Add the URI to the media scanner intent.
            mediaScannerIntent.setData(Uri.fromFile(imageFile));

            // Make it so.
            activity.sendBroadcast(mediaScannerIntent);
        } catch (Exception exception) {
            // Store the error in the file creation disposition string.
            fileCreationDisposition = exception.toString();
        }

        // Return the file creation disposition string.
        return fileCreationDisposition;
    }

    // `onPostExecute()` operates on the UI thread.
    @Override
    protected void onPostExecute(String fileCreationDisposition) {
        // Get handles for the weak references.
        Context context = contextWeakReference.get();
        Activity activity = activityWeakReference.get();
        NestedScrollWebView nestedScrollWebView = nestedScrollWebViewWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return;
        }

        // Dismiss the saving image snackbar.
        savingImageSnackbar.dismiss();

        // Display a file creation disposition snackbar.
        if (fileCreationDisposition.equals(SUCCESS)) {
            // Create a file saved snackbar.
            Snackbar imageSavedSnackbar = Snackbar.make(nestedScrollWebView, activity.getString(R.string.file_saved) + "  " + filePathString, Snackbar.LENGTH_SHORT);

            // Add an open action.
            imageSavedSnackbar.setAction(R.string.open, (View view) -> {
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

                // Autodetect the MIME type.
                openIntent.setDataAndType(fileUri, contentResolver.getType(fileUri));

                // Allow the app to read the file URI.
                openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Show the chooser.
                activity.startActivity(Intent.createChooser(openIntent, context.getString(R.string.open)));
            });

            // Show the image saved snackbar.
            imageSavedSnackbar.show();
        } else {
            // Display the file saving error.
            Snackbar.make(nestedScrollWebView, activity.getString(R.string.error_saving_file) + "  " + fileCreationDisposition, Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}