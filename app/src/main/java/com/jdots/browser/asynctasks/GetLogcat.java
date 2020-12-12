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
import android.os.AsyncTask;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jdots.browser.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

// `Void` does not declare any parameters.  `Void` does not declare progress units.  `String` contains the results.
public class GetLogcat extends AsyncTask<Void, Void, String> {
    // Define the class variables.
    private final WeakReference<Activity> activityWeakReference;
    private final int scrollViewYPositionInt;

    // The public constructor.
    public GetLogcat(Activity activity, int scrollViewYPositionInt) {
        // Populate the weak reference to the calling activity.
        activityWeakReference = new WeakReference<>(activity);

        // Store the scrollview Y position.
        this.scrollViewYPositionInt = scrollViewYPositionInt;
    }

    @Override
    protected String doInBackground(Void... parameters) {
        // Get a handle for the activity.
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return "";
        }

        // Create a log string builder.
        StringBuilder logStringBuilder = new StringBuilder();

        try {
            // Get the logcat.  `-b all` gets all the buffers (instead of just crash, main, and system).  `-v long` produces more complete information.  `-d` dumps the logcat and exits.
            Process process = Runtime.getRuntime().exec("logcat -b all -v long -d");

            // Wrap the logcat in a buffered reader.
            BufferedReader logBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Create a log transfer string.
            String logTransferString;

            // Use the log transfer string to copy the logcat from the buffered reader to the string builder.
            while ((logTransferString = logBufferedReader.readLine()) != null) {
                // Append a line.
                logStringBuilder.append(logTransferString);

                // Append a line break.
                logStringBuilder.append("\n");
            }

            // Close the buffered reader.
            logBufferedReader.close();
        } catch (IOException exception) {
            // Do nothing.
        }

        // Return the logcat.
        return logStringBuilder.toString();
    }

    // `onPostExecute()` operates on the UI thread.
    @Override
    protected void onPostExecute(String logcatString) {
        // Get a handle for the activity.
        Activity activity = activityWeakReference.get();

        // Abort if the activity is gone.
        if ((activity == null) || activity.isFinishing()) {
            return;
        }

        // Get handles for the views.
        TextView logcatTextView = activity.findViewById(R.id.logcat_textview);
        SwipeRefreshLayout swipeRefreshLayout = activity.findViewById(R.id.logcat_swiperefreshlayout);
        ScrollView scrollView = activity.findViewById(R.id.logcat_scrollview);

        // Display the logcat.
        logcatTextView.setText(logcatString);

        // Update the scroll position after the text is populated.
        logcatTextView.post(() -> {
            // Set the scroll position.
            scrollView.setScrollY(scrollViewYPositionInt);
        });

        // Stop the swipe to refresh animation if it is displayed.
        swipeRefreshLayout.setRefreshing(false);
    }
}