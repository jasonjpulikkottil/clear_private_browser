/*
 *   2019
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

import com.jdots.browser.helpers.CheckPinnedMismatchHelper;
import com.jdots.browser.views.NestedScrollWebView;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

import androidx.fragment.app.FragmentManager;

// This must run asynchronously because it involves a network request.  `String` declares the parameters.  `Void` does not declare progress units.  `String` contains the results.
public class GetHostIpAddresses extends AsyncTask<String, Void, String> {
    // The weak references are used to determine if the activity have disappeared while the AsyncTask is running.
    private final WeakReference<Activity> activityWeakReference;
    private final WeakReference<FragmentManager> fragmentManagerWeakReference;
    private final WeakReference<NestedScrollWebView> nestedScrollWebViewWeakReference;

    public GetHostIpAddresses(Activity activity, FragmentManager fragmentManager, NestedScrollWebView nestedScrollWebView) {
        // Populate the weak references.
        activityWeakReference = new WeakReference<>(activity);
        fragmentManagerWeakReference = new WeakReference<>(fragmentManager);
        nestedScrollWebViewWeakReference = new WeakReference<>(nestedScrollWebView);
    }

    @Override
    protected String doInBackground(String... domainName) {
        // Get a handles for the weak references.
        Activity activity = activityWeakReference.get();
        FragmentManager fragmentManager = fragmentManagerWeakReference.get();
        NestedScrollWebView nestedScrollWebView = nestedScrollWebViewWeakReference.get();

        // Abort if the activity or its components are gone.
        if ((activity == null) || activity.isFinishing() || fragmentManager == null || nestedScrollWebView == null) {
            // Return an empty spannable string builder.
            return "";
        }

        // Initialize an IP address string builder.
        StringBuilder ipAddresses = new StringBuilder();

        // Get an array with the IP addresses for the host.
        try {
            // Get an array with all the IP addresses for the domain.
            InetAddress[] inetAddressesArray = InetAddress.getAllByName(domainName[0]);

            // Add each IP address to the string builder.
            for (InetAddress inetAddress : inetAddressesArray) {
                // Add a line break to the string builder if this is not the first IP address.
                if (ipAddresses.length() > 0) {
                    ipAddresses.append("\n");
                }

                // Add the IP address to the string builder.
                ipAddresses.append(inetAddress.getHostAddress());
            }
        } catch (UnknownHostException exception) {
            // Do nothing.
        }

        // Return the string.
        return ipAddresses.toString();
    }

    // `onPostExecute()` operates on the UI thread.
    @Override
    protected void onPostExecute(String ipAddresses) {
        // Get a handle for the activity and the nested scroll WebView.
        Activity activity = activityWeakReference.get();
        FragmentManager fragmentManager = fragmentManagerWeakReference.get();
        NestedScrollWebView nestedScrollWebView = nestedScrollWebViewWeakReference.get();

        // Abort if the activity or its components are gone.
        if ((activity == null) || activity.isFinishing() || fragmentManager == null || nestedScrollWebView == null) {
            return;
        }

        // Store the IP addresses.
        nestedScrollWebView.setCurrentIpAddresses(ipAddresses);

        // Checked for pinned mismatches if there is pinned information and it is not ignored.
        if ((nestedScrollWebView.hasPinnedSslCertificate() || nestedScrollWebView.hasPinnedIpAddresses()) && !nestedScrollWebView.ignorePinnedDomainInformation()) {
            CheckPinnedMismatchHelper.checkPinnedMismatch(fragmentManager, nestedScrollWebView);
        }
    }
}