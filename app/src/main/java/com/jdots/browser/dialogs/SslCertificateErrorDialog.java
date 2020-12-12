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

package com.jdots.browser.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.fragments.WebViewTabFragment;
import com.jdots.browser.views.NestedScrollWebView;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SslCertificateErrorDialog extends DialogFragment {
    public static SslCertificateErrorDialog displayDialog(SslError error, long webViewFragmentId) {
        // Get the various components of the SSL error message.
        int primaryErrorIntForBundle = error.getPrimaryError();
        String urlWithErrorForBundle = error.getUrl();
        SslCertificate sslCertificate = error.getCertificate();
        String issuedToCNameForBundle = sslCertificate.getIssuedTo().getCName();
        String issuedToONameForBundle = sslCertificate.getIssuedTo().getOName();
        String issuedToUNameForBundle = sslCertificate.getIssuedTo().getUName();
        String issuedByCNameForBundle = sslCertificate.getIssuedBy().getCName();
        String issuedByONameForBundle = sslCertificate.getIssuedBy().getOName();
        String issuedByUNameForBundle = sslCertificate.getIssuedBy().getUName();
        Date startDateForBundle = sslCertificate.getValidNotBeforeDate();
        Date endDateForBundle = sslCertificate.getValidNotAfterDate();

        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the SSL error message components in a `Bundle`.
        argumentsBundle.putInt("primary_error_int", primaryErrorIntForBundle);
        argumentsBundle.putString("url_with_error", urlWithErrorForBundle);
        argumentsBundle.putString("issued_to_cname", issuedToCNameForBundle);
        argumentsBundle.putString("issued_to_oname", issuedToONameForBundle);
        argumentsBundle.putString("issued_to_uname", issuedToUNameForBundle);
        argumentsBundle.putString("issued_by_cname", issuedByCNameForBundle);
        argumentsBundle.putString("issued_by_oname", issuedByONameForBundle);
        argumentsBundle.putString("issued_by_uname", issuedByUNameForBundle);
        argumentsBundle.putString("start_date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(startDateForBundle));
        argumentsBundle.putString("end_date", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(endDateForBundle));
        argumentsBundle.putLong("webview_fragment_id", webViewFragmentId);

        // Create a new instance of the SSL certificate error dialog.
        SslCertificateErrorDialog thisSslCertificateErrorDialog = new SslCertificateErrorDialog();

        // Add the arguments bundle to the new dialog.
        thisSslCertificateErrorDialog.setArguments(argumentsBundle);

        // Return the new dialog.
        return thisSslCertificateErrorDialog;
    }

    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the `AlertDialog`.
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint warning that the arguments might be null.
        assert arguments != null;

        // Get the variables from the bundle.
        int primaryErrorInt = arguments.getInt("primary_error_int");
        String urlWithErrors = arguments.getString("url_with_error");
        String issuedToCName = arguments.getString("issued_to_cname");
        String issuedToOName = arguments.getString("issued_to_oname");
        String issuedToUName = arguments.getString("issued_to_uname");
        String issuedByCName = arguments.getString("issued_by_cname");
        String issuedByOName = arguments.getString("issued_by_oname");
        String issuedByUName = arguments.getString("issued_by_uname");
        String startDate = arguments.getString("start_date");
        String endDate = arguments.getString("end_date");
        long webViewFragmentId = arguments.getLong("webview_fragment_id");

        // Get the current position of this WebView fragment.
        int webViewPosition = MainWebViewActivity.webViewPagerAdapter.getPositionForId(webViewFragmentId);

        // Get the WebView tab fragment.
        WebViewTabFragment webViewTabFragment = MainWebViewActivity.webViewPagerAdapter.getPageFragment(webViewPosition);

        // Get the fragment view.
        View fragmentView = webViewTabFragment.getView();

        // Remove the incorrect lint warning below that the fragment view might be null.
        assert fragmentView != null;

        // Get a handle for the current WebView.
        NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

        // Get a handle for the SSL error handler.
        SslErrorHandler sslErrorHandler = nestedScrollWebView.getSslErrorHandler();

        // Get the activity's layout inflater.
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the icon according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
            dialogBuilder.setIcon(R.drawable.ssl_certificate_enabled_night);
        } else {
            dialogBuilder.setIcon(R.drawable.ssl_certificate_enabled_day);
        }

        // Set the title.
        dialogBuilder.setTitle(R.string.ssl_certificate_error);

        // Set the view.  The parent view is `null` because it will be assigned by `AlertDialog`.
        dialogBuilder.setView(layoutInflater.inflate(R.layout.ssl_certificate_error, null));

        // Set a listener on the cancel button.
        dialogBuilder.setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
            // Check to make sure the SSL error handler is not null.  This might happen if multiple dialogs are displayed at once.
            if (sslErrorHandler != null) {
                // Cancel the request.
                sslErrorHandler.cancel();

                // Reset the SSL error handler.
                nestedScrollWebView.resetSslErrorHandler();
            }
        });

        // Set a listener on the proceed button.
        dialogBuilder.setPositiveButton(R.string.proceed, (DialogInterface dialog, int which) -> {
            // Check to make sure the SSL error handler is not null.  This might happen if multiple dialogs are displayed at once.
            if (sslErrorHandler != null) {
                // Cancel the request.
                sslErrorHandler.proceed();

                // Reset the SSL error handler.
                nestedScrollWebView.resetSslErrorHandler();
            }
        });


        // Create an alert dialog from the alert dialog builder.
        AlertDialog alertDialog = dialogBuilder.create();

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            // Remove the warning below that `getWindow()` might be null.
            assert alertDialog.getWindow() != null;

            // Disable screenshots.
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Get a URI for the URL with errors.
        Uri uriWithErrors = Uri.parse(urlWithErrors);

        // Get the IP addresses for the URI.
        new GetIpAddresses(getActivity(), alertDialog).execute(uriWithErrors.getHost());

        // The alert dialog must be shown before the contents can be modified.
        alertDialog.show();

        // Get handles for the `TextViews`
        TextView primaryErrorTextView = alertDialog.findViewById(R.id.primary_error);
        TextView urlTextView = alertDialog.findViewById(R.id.url);
        TextView issuedToCNameTextView = alertDialog.findViewById(R.id.issued_to_cname);
        TextView issuedToONameTextView = alertDialog.findViewById(R.id.issued_to_oname);
        TextView issuedToUNameTextView = alertDialog.findViewById(R.id.issued_to_uname);
        TextView issuedByTextView = alertDialog.findViewById(R.id.issued_by_textview);
        TextView issuedByCNameTextView = alertDialog.findViewById(R.id.issued_by_cname);
        TextView issuedByONameTextView = alertDialog.findViewById(R.id.issued_by_oname);
        TextView issuedByUNameTextView = alertDialog.findViewById(R.id.issued_by_uname);
        TextView validDatesTextView = alertDialog.findViewById(R.id.valid_dates_textview);
        TextView startDateTextView = alertDialog.findViewById(R.id.start_date);
        TextView endDateTextView = alertDialog.findViewById(R.id.end_date);

        // Remove the incorrect lint warnings below that the views might be null.
        assert primaryErrorTextView != null;
        assert urlTextView != null;
        assert issuedToCNameTextView != null;
        assert issuedToONameTextView != null;
        assert issuedToUNameTextView != null;
        assert issuedByTextView != null;
        assert issuedByCNameTextView != null;
        assert issuedByONameTextView != null;
        assert issuedByUNameTextView != null;
        assert validDatesTextView != null;
        assert startDateTextView != null;
        assert endDateTextView != null;

        // Setup the common strings.
        String urlLabel = getString(R.string.url_label) + "  ";
        String cNameLabel = getString(R.string.common_name) + "  ";
        String oNameLabel = getString(R.string.organization) + "  ";
        String uNameLabel = getString(R.string.organizational_unit) + "  ";
        String startDateLabel = getString(R.string.start_date) + "  ";
        String endDateLabel = getString(R.string.end_date) + "  ";

        // Create a spannable string builder for each text view that needs multiple colors of text.
        SpannableStringBuilder urlStringBuilder = new SpannableStringBuilder(urlLabel + urlWithErrors);
        SpannableStringBuilder issuedToCNameStringBuilder = new SpannableStringBuilder(cNameLabel + issuedToCName);
        SpannableStringBuilder issuedToONameStringBuilder = new SpannableStringBuilder(oNameLabel + issuedToOName);
        SpannableStringBuilder issuedToUNameStringBuilder = new SpannableStringBuilder(uNameLabel + issuedToUName);
        SpannableStringBuilder issuedByCNameStringBuilder = new SpannableStringBuilder(cNameLabel + issuedByCName);
        SpannableStringBuilder issuedByONameStringBuilder = new SpannableStringBuilder(oNameLabel + issuedByOName);
        SpannableStringBuilder issuedByUNameStringBuilder = new SpannableStringBuilder(uNameLabel + issuedByUName);
        SpannableStringBuilder startDateStringBuilder = new SpannableStringBuilder(startDateLabel + startDate);
        SpannableStringBuilder endDateStringBuilder = new SpannableStringBuilder((endDateLabel + endDate));

        // Define the color spans.
        ForegroundColorSpan blueColorSpan;
        ForegroundColorSpan redColorSpan;

        // Set the color spans according to the theme.  The deprecated `getResources()` must be used until the minimum API >= 23.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            blueColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.blue_700));
            redColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red_a700));
        } else {
            blueColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.violet_700));
            redColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red_900));
        }

        // Setup the spans to display the certificate information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        urlStringBuilder.setSpan(blueColorSpan, urlLabel.length(), urlStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedToCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), issuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedToONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), issuedToONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedToUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), issuedToUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedByCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), issuedByCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedByONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), issuedByONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        issuedByUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), issuedByUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        startDateStringBuilder.setSpan(blueColorSpan, startDateLabel.length(), startDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        endDateStringBuilder.setSpan(blueColorSpan, endDateLabel.length(), endDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // Initialize `primaryErrorString`.
        String primaryErrorString = "";

        // Highlight the primary error in red and store the primary error string in `primaryErrorString`.
        switch (primaryErrorInt) {
            case SslError.SSL_IDMISMATCH:
                // Change the URL span colors to red.
                urlStringBuilder.setSpan(redColorSpan, urlLabel.length(), urlStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                issuedToCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), issuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Store the primary error string.
                primaryErrorString = getString(R.string.cn_mismatch);
                break;

            case SslError.SSL_UNTRUSTED:
                // Change the issued by text view text to red.  The deprecated `getResources().getColor` must be used until the minimum API >= 23.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    issuedByTextView.setTextColor(getResources().getColor(R.color.red_900));
                } else {
                    issuedByTextView.setTextColor(getResources().getColor(R.color.red_a700));
                }

                // Change the issued by span color to red.
                issuedByCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), issuedByCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                issuedByONameStringBuilder.setSpan(redColorSpan, oNameLabel.length(), issuedByONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                issuedByUNameStringBuilder.setSpan(redColorSpan, uNameLabel.length(), issuedByUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Store the primary error string.
                primaryErrorString = getString(R.string.untrusted);
                break;

            case SslError.SSL_DATE_INVALID:
                // Change the valid dates text view text to red.  The deprecated `getResources().getColor` must be used until the minimum API >= 23.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    validDatesTextView.setTextColor(getResources().getColor(R.color.red_900));
                } else {
                    validDatesTextView.setTextColor(getResources().getColor(R.color.red_a700));
                }

                // Change the date span colors to red.
                startDateStringBuilder.setSpan(redColorSpan, startDateLabel.length(), startDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                endDateStringBuilder.setSpan(redColorSpan, endDateLabel.length(), endDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Store the primary error string.
                primaryErrorString = getString(R.string.invalid_date);
                break;

            case SslError.SSL_NOTYETVALID:
                // Change the start date span color to red.
                startDateStringBuilder.setSpan(redColorSpan, startDateLabel.length(), startDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Store the primary error string.
                primaryErrorString = getString(R.string.future_certificate);
                break;

            case SslError.SSL_EXPIRED:
                // Change the end date span color to red.
                endDateStringBuilder.setSpan(redColorSpan, endDateLabel.length(), endDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Store the primary error string.
                primaryErrorString = getString(R.string.expired_certificate);
                break;

            case SslError.SSL_INVALID:
                // Store the primary error string.
                primaryErrorString = getString(R.string.invalid_certificate);
                break;
        }


        // Display the strings.
        primaryErrorTextView.setText(primaryErrorString);
        urlTextView.setText(urlStringBuilder);
        issuedToCNameTextView.setText(issuedToCNameStringBuilder);
        issuedToONameTextView.setText(issuedToONameStringBuilder);
        issuedToUNameTextView.setText(issuedToUNameStringBuilder);
        issuedByCNameTextView.setText(issuedByCNameStringBuilder);
        issuedByONameTextView.setText(issuedByONameStringBuilder);
        issuedByUNameTextView.setText(issuedByUNameStringBuilder);
        startDateTextView.setText(startDateStringBuilder);
        endDateTextView.setText(endDateStringBuilder);

        // `onCreateDialog` requires the return of an alert dialog.
        return alertDialog;
    }


    // This must run asynchronously because it involves a network request.  `String` declares the parameters.  `Void` does not declare progress units.  `SpannableStringBuilder` contains the results.
    private static class GetIpAddresses extends AsyncTask<String, Void, SpannableStringBuilder> {
        // The weak references are used to determine if the activity or the alert dialog have disappeared while the AsyncTask is running.
        private final WeakReference<Activity> activityWeakReference;
        private final WeakReference<AlertDialog> alertDialogWeakReference;

        GetIpAddresses(Activity activity, AlertDialog alertDialog) {
            // Populate the weak references.
            activityWeakReference = new WeakReference<>(activity);
            alertDialogWeakReference = new WeakReference<>(alertDialog);
        }

        @Override
        protected SpannableStringBuilder doInBackground(String... domainName) {
            // Get handles for the activity and the alert dialog.
            Activity activity = activityWeakReference.get();
            AlertDialog alertDialog = alertDialogWeakReference.get();

            // Abort if the activity or the dialog is gone.
            if ((activity == null) || (activity.isFinishing()) || (alertDialog == null)) {
                return new SpannableStringBuilder();
            }

            // Initialize an IP address string builder.
            StringBuilder ipAddresses = new StringBuilder();

            // Get an array with the IP addresses for the host.
            try {
                // Get an array with all the IP addresses for the domain.
                InetAddress[] inetAddressesArray = InetAddress.getAllByName(domainName[0]);

                // Add each IP address to the string builder.
                for (InetAddress inetAddress : inetAddressesArray) {
                    // Check to see if this is not the first IP address.
                    if (ipAddresses.length() > 0) {
                        // Add a line break to the string builder first.
                        ipAddresses.append("\n");
                    }

                    // Add the IP Address to the string builder.
                    ipAddresses.append(inetAddress.getHostAddress());
                }
            } catch (UnknownHostException exception) {
                // Do nothing.
            }

            // Set the label.
            String ipAddressesLabel = activity.getString(R.string.ip_addresses) + "  ";

            // Create a spannable string builder.
            SpannableStringBuilder ipAddressesStringBuilder = new SpannableStringBuilder(ipAddressesLabel + ipAddresses);

            // Create a blue foreground color span.
            ForegroundColorSpan blueColorSpan;

            // Get the current theme status.
            int currentThemeStatus = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            // Set the blue color span according to the theme.  The deprecated `getColor()` must be used until the minimum API >= 23.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                blueColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.violet_500));
            } else {
                blueColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.blue_700));
            }

            // Set the string builder to display the certificate information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            ipAddressesStringBuilder.setSpan(blueColorSpan, ipAddressesLabel.length(), ipAddressesStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            // Return the formatted string.
            return ipAddressesStringBuilder;
        }

        // `onPostExecute()` operates on the UI thread.
        @Override
        protected void onPostExecute(SpannableStringBuilder ipAddresses) {
            // Get handles for the activity and the alert dialog.
            Activity activity = activityWeakReference.get();
            AlertDialog alertDialog = alertDialogWeakReference.get();

            // Abort if the activity or the alert dialog is gone.
            if ((activity == null) || (activity.isFinishing()) || (alertDialog == null)) {
                return;
            }

            // Get a handle for the IP addresses text view.
            TextView ipAddressesTextView = alertDialog.findViewById(R.id.ip_addresses);

            // Remove the incorrect lint warning below that the view might be null.
            assert ipAddressesTextView != null;

            // Populate the IP addresses text view.
            ipAddressesTextView.setText(ipAddresses);
        }
    }
}
