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

package com.jdots.browser.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.jdots.browser.BuildConfig;
import com.jdots.browser.R;
import com.jdots.browser.dialogs.SaveDialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewCompat;

public class AboutVersionFragment extends Fragment {
    // Declare the class constants.
    final static String BLOCKLIST_VERSIONS = "blocklist_versions";
    final long MEBIBYTE = 1048576;

    // Declare the class variables.
    private boolean updateMemoryUsageBoolean = true;
    private String[] blocklistVersions;
    private View aboutVersionLayout;
    private String appConsumedMemoryLabel;
    private String appAvailableMemoryLabel;
    private String appTotalMemoryLabel;
    private String appMaximumMemoryLabel;
    private String systemConsumedMemoryLabel;
    private String systemAvailableMemoryLabel;
    private String systemTotalMemoryLabel;
    private Runtime runtime;
    private ActivityManager activityManager;
    private ActivityManager.MemoryInfo memoryInfo;
    private NumberFormat numberFormat;
    private ForegroundColorSpan blueColorSpan;

    // Declare the class views.
    private TextView ClearBrowserTextView;
    private TextView versionTextView;
    private TextView hardwareTextView;
    private TextView brandTextView;
    private TextView manufacturerTextView;
    private TextView modelTextView;
    private TextView deviceTextView;
    private TextView bootloaderTextView;
    private TextView radioTextView;
    private TextView softwareTextView;
    private TextView androidTextView;
    private TextView securityPatchTextView;
    private TextView buildTextView;
    private TextView webViewProviderTextView;
    private TextView webViewVersionTextView;
    private TextView orbotTextView;
    private TextView i2pTextView;
    private TextView openKeychainTextView;
    private TextView memoryUsageTextView;
    private TextView appConsumedMemoryTextView;
    private TextView appAvailableMemoryTextView;
    private TextView appTotalMemoryTextView;
    private TextView appMaximumMemoryTextView;
    private TextView systemConsumedMemoryTextView;
    private TextView systemAvailableMemoryTextView;
    private TextView systemTotalMemoryTextView;
    private TextView blocklistsTextView;
    private TextView easyListTextView;
    private TextView easyPrivacyTextView;
    private TextView fanboyAnnoyanceTextView;
    private TextView fanboySocialTextView;
    private TextView ultraListTextView;
    private TextView ultraPrivacyTextView;
    private TextView packageSignatureTextView;
    private TextView certificateIssuerDnTextView;
    private TextView certificateSubjectDnTextView;
    private TextView certificateStartDateTextView;
    private TextView certificateEndDateTextView;
    private TextView certificateVersionTextView;
    private TextView certificateSerialNumberTextView;
    private TextView certificateSignatureAlgorithmTextView;

    public static AboutVersionFragment createTab(String[] blocklistVersions) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the arguments in the bundle.
        argumentsBundle.putStringArray(BLOCKLIST_VERSIONS, blocklistVersions);

        // Create a new instance of the tab fragment.
        AboutVersionFragment aboutVersionFragment = new AboutVersionFragment();

        // Add the arguments bundle to the fragment.
        aboutVersionFragment.setArguments(argumentsBundle);

        // Return the new fragment.
        return aboutVersionFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint warning below that the arguments might be null.
        assert arguments != null;

        // Store the arguments in class variables.
        blocklistVersions = arguments.getStringArray(BLOCKLIST_VERSIONS);

        // Enable the options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        // Get a handle for the context.
        Context context = getContext();

        // Remove the incorrect lint warning below that the context might be null.
        assert context != null;

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Inflate the layout.  Setting false at the end of inflater.inflate does not attach the inflated layout as a child of container.  The fragment will take care of attaching the root automatically.
        aboutVersionLayout = layoutInflater.inflate(R.layout.about_version, container, false);

        // Get handles for the views.
        ClearBrowserTextView = aboutVersionLayout.findViewById(R.id.application_name_textview);
        versionTextView = aboutVersionLayout.findViewById(R.id.version);
        hardwareTextView = aboutVersionLayout.findViewById(R.id.hardware);
        brandTextView = aboutVersionLayout.findViewById(R.id.brand);
        manufacturerTextView = aboutVersionLayout.findViewById(R.id.manufacturer);
        modelTextView = aboutVersionLayout.findViewById(R.id.model);
        deviceTextView = aboutVersionLayout.findViewById(R.id.device);
        bootloaderTextView = aboutVersionLayout.findViewById(R.id.bootloader);
        radioTextView = aboutVersionLayout.findViewById(R.id.radio);
        softwareTextView = aboutVersionLayout.findViewById(R.id.software);
        androidTextView = aboutVersionLayout.findViewById(R.id.android);
        securityPatchTextView = aboutVersionLayout.findViewById(R.id.security_patch);
        buildTextView = aboutVersionLayout.findViewById(R.id.build);
        webViewProviderTextView = aboutVersionLayout.findViewById(R.id.webview_provider);
        webViewVersionTextView = aboutVersionLayout.findViewById(R.id.webview_version);
        orbotTextView = aboutVersionLayout.findViewById(R.id.orbot);
        i2pTextView = aboutVersionLayout.findViewById(R.id.i2p);
        openKeychainTextView = aboutVersionLayout.findViewById(R.id.open_keychain);
        memoryUsageTextView = aboutVersionLayout.findViewById(R.id.memory_usage);
        appConsumedMemoryTextView = aboutVersionLayout.findViewById(R.id.app_consumed_memory);
        appAvailableMemoryTextView = aboutVersionLayout.findViewById(R.id.app_available_memory);
        appTotalMemoryTextView = aboutVersionLayout.findViewById(R.id.app_total_memory);
        appMaximumMemoryTextView = aboutVersionLayout.findViewById(R.id.app_maximum_memory);
        systemConsumedMemoryTextView = aboutVersionLayout.findViewById(R.id.system_consumed_memory);
        systemAvailableMemoryTextView = aboutVersionLayout.findViewById(R.id.system_available_memory);
        systemTotalMemoryTextView = aboutVersionLayout.findViewById(R.id.system_total_memory);
        blocklistsTextView = aboutVersionLayout.findViewById(R.id.blocklists);
        easyListTextView = aboutVersionLayout.findViewById(R.id.easylist);
        easyPrivacyTextView = aboutVersionLayout.findViewById(R.id.easyprivacy);
        fanboyAnnoyanceTextView = aboutVersionLayout.findViewById(R.id.fanboy_annoyance);
        fanboySocialTextView = aboutVersionLayout.findViewById(R.id.fanboy_social);
        ultraListTextView = aboutVersionLayout.findViewById(R.id.ultralist);
        ultraPrivacyTextView = aboutVersionLayout.findViewById(R.id.ultraprivacy);
        packageSignatureTextView = aboutVersionLayout.findViewById(R.id.package_signature);
        certificateIssuerDnTextView = aboutVersionLayout.findViewById(R.id.certificate_issuer_dn);
        certificateSubjectDnTextView = aboutVersionLayout.findViewById(R.id.certificate_subject_dn);
        certificateStartDateTextView = aboutVersionLayout.findViewById(R.id.certificate_start_date);
        certificateEndDateTextView = aboutVersionLayout.findViewById(R.id.certificate_end_date);
        certificateVersionTextView = aboutVersionLayout.findViewById(R.id.certificate_version);
        certificateSerialNumberTextView = aboutVersionLayout.findViewById(R.id.certificate_serial_number);
        certificateSignatureAlgorithmTextView = aboutVersionLayout.findViewById(R.id.certificate_signature_algorithm);

        // Setup the labels.
        String version = getString(R.string.version) + " " + BuildConfig.VERSION_NAME + " (" + getString(R.string.version_code) + " " + BuildConfig.VERSION_CODE + ")";
        String brandLabel = getString(R.string.brand) + "  ";
        String manufacturerLabel = getString(R.string.manufacturer) + "  ";
        String modelLabel = getString(R.string.model) + "  ";
        String deviceLabel = getString(R.string.device) + "  ";
        String bootloaderLabel = getString(R.string.bootloader) + "  ";
        String androidLabel = getString(R.string.android) + "  ";
        String buildLabel = getString(R.string.build) + "  ";
        String webViewVersionLabel = getString(R.string.webview_version) + "  ";
        appConsumedMemoryLabel = getString(R.string.app_consumed_memory) + "  ";
        appAvailableMemoryLabel = getString(R.string.app_available_memory) + "  ";
        appTotalMemoryLabel = getString(R.string.app_total_memory) + "  ";
        appMaximumMemoryLabel = getString(R.string.app_maximum_memory) + "  ";
        systemConsumedMemoryLabel = getString(R.string.system_consumed_memory) + "  ";
        systemAvailableMemoryLabel = getString(R.string.system_available_memory) + "  ";
        systemTotalMemoryLabel = getString(R.string.system_total_memory) + "  ";
        String easyListLabel = getString(R.string.easylist_label) + "  ";
        String easyPrivacyLabel = getString(R.string.easyprivacy_label) + "  ";
        String fanboyAnnoyanceLabel = getString(R.string.fanboy_annoyance_label) + "  ";
        String fanboySocialLabel = getString(R.string.fanboy_social_label) + "  ";
        String ultraListLabel = getString(R.string.ultralist_label) + "  ";
        String ultraPrivacyLabel = getString(R.string.ultraprivacy_label) + "  ";
        String issuerDNLabel = getString(R.string.issuer_dn) + "  ";
        String subjectDNLabel = getString(R.string.subject_dn) + "  ";
        String startDateLabel = getString(R.string.start_date) + "  ";
        String endDateLabel = getString(R.string.end_date) + "  ";
        String certificateVersionLabel = getString(R.string.certificate_version) + "  ";
        String serialNumberLabel = getString(R.string.serial_number) + "  ";
        String signatureAlgorithmLabel = getString(R.string.signature_algorithm) + "  ";

        // The WebView layout is only used to get the default user agent from `bare_webview`.  It is not used to render content on the screen.
        // Once the minimum API >= 26 this can be accomplished with the WebView package info.
        View webViewLayout = layoutInflater.inflate(R.layout.bare_webview, container, false);
        WebView tabLayoutWebView = webViewLayout.findViewById(R.id.bare_webview);
        String userAgentString = tabLayoutWebView.getSettings().getUserAgentString();

        // Get the device's information and store it in strings.
        String brand = Build.BRAND;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String device = Build.DEVICE;
        String bootloader = Build.BOOTLOADER;
        String radio = Build.getRadioVersion();
        String android = Build.VERSION.RELEASE + " (" + getString(R.string.api) + " " + Build.VERSION.SDK_INT + ")";
        String build = Build.DISPLAY;
        // Select the substring that begins after `Chrome/` and goes until the next ` `.
        String webView = userAgentString.substring(userAgentString.indexOf("Chrome/") + 7, userAgentString.indexOf(" ", userAgentString.indexOf("Chrome/")));

        // Get the Orbot version name if Orbot is installed.
        String orbot;
        try {
            // Store the version name.
            orbot = context.getPackageManager().getPackageInfo("org.torproject.android", 0).versionName;
        } catch (PackageManager.NameNotFoundException exception) {  // Orbot is not installed.
            orbot = "";
        }

        // Get the I2P version name if I2P is installed.
        String i2p;
        try {
            // Store the version name.
            i2p = context.getPackageManager().getPackageInfo("net.i2p.android.router", 0).versionName;
        } catch (PackageManager.NameNotFoundException exception) {  // I2P is not installed.
            i2p = "";
        }

        // Get the OpenKeychain version name if it is installed.
        String openKeychain;
        try {
            // Store the version name.
            openKeychain = context.getPackageManager().getPackageInfo("org.sufficientlysecure.keychain", 0).versionName;
        } catch (PackageManager.NameNotFoundException exception) {  // OpenKeychain is not installed.
            openKeychain = "";
        }

        // Create a spannable string builder for the hardware and software text views that needs multiple colors of text.
        SpannableStringBuilder brandStringBuilder = new SpannableStringBuilder(brandLabel + brand);
        SpannableStringBuilder manufacturerStringBuilder = new SpannableStringBuilder(manufacturerLabel + manufacturer);
        SpannableStringBuilder modelStringBuilder = new SpannableStringBuilder(modelLabel + model);
        SpannableStringBuilder deviceStringBuilder = new SpannableStringBuilder(deviceLabel + device);
        SpannableStringBuilder bootloaderStringBuilder = new SpannableStringBuilder(bootloaderLabel + bootloader);
        SpannableStringBuilder androidStringBuilder = new SpannableStringBuilder(androidLabel + android);
        SpannableStringBuilder buildStringBuilder = new SpannableStringBuilder(buildLabel + build);
        SpannableStringBuilder webViewVersionStringBuilder = new SpannableStringBuilder(webViewVersionLabel + webView);
        SpannableStringBuilder easyListStringBuilder = new SpannableStringBuilder(easyListLabel + blocklistVersions[0]);
        SpannableStringBuilder easyPrivacyStringBuilder = new SpannableStringBuilder(easyPrivacyLabel + blocklistVersions[1]);
        SpannableStringBuilder fanboyAnnoyanceStringBuilder = new SpannableStringBuilder(fanboyAnnoyanceLabel + blocklistVersions[2]);
        SpannableStringBuilder fanboySocialStringBuilder = new SpannableStringBuilder(fanboySocialLabel + blocklistVersions[3]);
        SpannableStringBuilder ultraListStringBuilder = new SpannableStringBuilder(ultraListLabel + blocklistVersions[4]);
        SpannableStringBuilder ultraPrivacyStringBuilder = new SpannableStringBuilder(ultraPrivacyLabel + blocklistVersions[5]);

        // Set the blue color span according to the theme.  The deprecated `getResources()` must be used until the minimum API >= 23.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            blueColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.blue_700));
        } else {
            blueColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.violet_700));
        }

        // Setup the spans to display the device information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        brandStringBuilder.setSpan(blueColorSpan, brandLabel.length(), brandStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        manufacturerStringBuilder.setSpan(blueColorSpan, manufacturerLabel.length(), manufacturerStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        modelStringBuilder.setSpan(blueColorSpan, modelLabel.length(), modelStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        deviceStringBuilder.setSpan(blueColorSpan, deviceLabel.length(), deviceStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        bootloaderStringBuilder.setSpan(blueColorSpan, bootloaderLabel.length(), bootloaderStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        androidStringBuilder.setSpan(blueColorSpan, androidLabel.length(), androidStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        buildStringBuilder.setSpan(blueColorSpan, buildLabel.length(), buildStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        webViewVersionStringBuilder.setSpan(blueColorSpan, webViewVersionLabel.length(), webViewVersionStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        easyListStringBuilder.setSpan(blueColorSpan, easyListLabel.length(), easyListStringBuilder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        easyPrivacyStringBuilder.setSpan(blueColorSpan, easyPrivacyLabel.length(), easyPrivacyStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        fanboyAnnoyanceStringBuilder.setSpan(blueColorSpan, fanboyAnnoyanceLabel.length(), fanboyAnnoyanceStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        fanboySocialStringBuilder.setSpan(blueColorSpan, fanboySocialLabel.length(), fanboySocialStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ultraListStringBuilder.setSpan(blueColorSpan, ultraListLabel.length(), ultraListStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ultraPrivacyStringBuilder.setSpan(blueColorSpan, ultraPrivacyLabel.length(), ultraPrivacyStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // Display the strings in the text boxes.
        versionTextView.setText(version);
        brandTextView.setText(brandStringBuilder);
        manufacturerTextView.setText(manufacturerStringBuilder);
        modelTextView.setText(modelStringBuilder);
        deviceTextView.setText(deviceStringBuilder);
        bootloaderTextView.setText(bootloaderStringBuilder);
        androidTextView.setText(androidStringBuilder);
        buildTextView.setText(buildStringBuilder);
        webViewVersionTextView.setText(webViewVersionStringBuilder);
        easyListTextView.setText(easyListStringBuilder);
        easyPrivacyTextView.setText(easyPrivacyStringBuilder);
        fanboyAnnoyanceTextView.setText(fanboyAnnoyanceStringBuilder);
        fanboySocialTextView.setText(fanboySocialStringBuilder);
        ultraListTextView.setText(ultraListStringBuilder);
        ultraPrivacyTextView.setText(ultraPrivacyStringBuilder);

        // Only populate the radio text view if there is a radio in the device.
        if (!radio.isEmpty()) {
            String radioLabel = getString(R.string.radio) + "  ";
            SpannableStringBuilder radioStringBuilder = new SpannableStringBuilder(radioLabel + radio);
            radioStringBuilder.setSpan(blueColorSpan, radioLabel.length(), radioStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            radioTextView.setText(radioStringBuilder);
        } else {  // This device does not have a radio.
            radioTextView.setVisibility(View.GONE);
        }

        // Build.VERSION.SECURITY_PATCH is only available for SDK_INT >= 23.
        if (Build.VERSION.SDK_INT >= 23) {
            String securityPatchLabel = getString(R.string.security_patch) + "  ";
            String securityPatch = Build.VERSION.SECURITY_PATCH;
            SpannableStringBuilder securityPatchStringBuilder = new SpannableStringBuilder(securityPatchLabel + securityPatch);
            securityPatchStringBuilder.setSpan(blueColorSpan, securityPatchLabel.length(), securityPatchStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            securityPatchTextView.setText(securityPatchStringBuilder);
        } else {  // The API < 23.
            // Hide the security patch text view.
            securityPatchTextView.setVisibility(View.GONE);
        }

        // Only populate the WebView provider if the SDK >= 21.
        if (Build.VERSION.SDK_INT >= 21) {
            // Create the WebView provider label.
            String webViewProviderLabel = getString(R.string.webview_provider) + "  ";

            // Get the current WebView package info.
            PackageInfo webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(context);

            // Remove the warning below that the package info might be null.
            assert webViewPackageInfo != null;

            // Get the WebView provider name.
            String webViewPackageName = webViewPackageInfo.packageName;

            // Create the spannable string builder.
            SpannableStringBuilder webViewProviderStringBuilder = new SpannableStringBuilder(webViewProviderLabel + webViewPackageName);

            // Apply the coloration.
            webViewProviderStringBuilder.setSpan(blueColorSpan, webViewProviderLabel.length(), webViewProviderStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            // Display the WebView provider.
            webViewProviderTextView.setText(webViewProviderStringBuilder);
        } else {  // The API < 21.
            // Hide the WebView provider text view.
            webViewProviderTextView.setVisibility(View.GONE);
        }

        // Only populate the Orbot text view if it is installed.
        if (!orbot.isEmpty()) {
            String orbotLabel = getString(R.string.orbot) + "  ";
            SpannableStringBuilder orbotStringBuilder = new SpannableStringBuilder(orbotLabel + orbot);
            orbotStringBuilder.setSpan(blueColorSpan, orbotLabel.length(), orbotStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            orbotTextView.setText(orbotStringBuilder);
        } else {  // Orbot is not installed.
            orbotTextView.setVisibility(View.GONE);
        }

        // Only populate the I2P text view if it is installed.
        if (!i2p.isEmpty()) {
            String i2pLabel = getString(R.string.i2p) + "  ";
            SpannableStringBuilder i2pStringBuilder = new SpannableStringBuilder(i2pLabel + i2p);
            i2pStringBuilder.setSpan(blueColorSpan, i2pLabel.length(), i2pStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            i2pTextView.setText(i2pStringBuilder);
        } else {  // I2P is not installed.
            i2pTextView.setVisibility(View.GONE);
        }

        // Only populate the OpenKeychain text view if it is installed.
        if (!openKeychain.isEmpty()) {
            String openKeychainLabel = getString(R.string.openkeychain) + "  ";
            SpannableStringBuilder openKeychainStringBuilder = new SpannableStringBuilder(openKeychainLabel + openKeychain);
            openKeychainStringBuilder.setSpan(blueColorSpan, openKeychainLabel.length(), openKeychainStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            openKeychainTextView.setText(openKeychainStringBuilder);
        } else {  //OpenKeychain is not installed.
            openKeychainTextView.setVisibility(View.GONE);
        }

        // Display the package signature.
        try {
            // Get the first package signature.  Suppress the lint warning about the need to be careful in implementing comparison of certificates for security purposes.
            @SuppressLint("PackageManagerGetSignatures") Signature packageSignature = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES).signatures[0];

            // Convert the signature to a byte array input stream.
            InputStream certificateByteArrayInputStream = new ByteArrayInputStream(packageSignature.toByteArray());

            // Display the certificate information on the screen.
            try {
                // Instantiate a `CertificateFactory`.
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");

                // Generate an `X509Certificate`.
                X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(certificateByteArrayInputStream);

                // Store the individual sections of the certificate that we are interested in.
                Principal issuerDNPrincipal = x509Certificate.getIssuerDN();
                Principal subjectDNPrincipal = x509Certificate.getSubjectDN();
                Date startDate = x509Certificate.getNotBefore();
                Date endDate = x509Certificate.getNotAfter();
                int certificateVersion = x509Certificate.getVersion();
                BigInteger serialNumberBigInteger = x509Certificate.getSerialNumber();
                String signatureAlgorithmNameString = x509Certificate.getSigAlgName();

                // Create a `SpannableStringBuilder` for each `TextView` that needs multiple colors of text.
                SpannableStringBuilder issuerDNStringBuilder = new SpannableStringBuilder(issuerDNLabel + issuerDNPrincipal.toString());
                SpannableStringBuilder subjectDNStringBuilder = new SpannableStringBuilder(subjectDNLabel + subjectDNPrincipal.toString());
                SpannableStringBuilder startDateStringBuilder = new SpannableStringBuilder(startDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(startDate));
                SpannableStringBuilder endDataStringBuilder = new SpannableStringBuilder(endDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(endDate));
                SpannableStringBuilder certificateVersionStringBuilder = new SpannableStringBuilder(certificateVersionLabel + certificateVersion);
                SpannableStringBuilder serialNumberStringBuilder = new SpannableStringBuilder(serialNumberLabel + serialNumberBigInteger);
                SpannableStringBuilder signatureAlgorithmStringBuilder = new SpannableStringBuilder(signatureAlgorithmLabel + signatureAlgorithmNameString);

                // Setup the spans to display the device information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
                issuerDNStringBuilder.setSpan(blueColorSpan, issuerDNLabel.length(), issuerDNStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                subjectDNStringBuilder.setSpan(blueColorSpan, subjectDNLabel.length(), subjectDNStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                startDateStringBuilder.setSpan(blueColorSpan, startDateLabel.length(), startDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                endDataStringBuilder.setSpan(blueColorSpan, endDateLabel.length(), endDataStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                certificateVersionStringBuilder.setSpan(blueColorSpan, certificateVersionLabel.length(), certificateVersionStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                serialNumberStringBuilder.setSpan(blueColorSpan, serialNumberLabel.length(), serialNumberStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                signatureAlgorithmStringBuilder.setSpan(blueColorSpan, signatureAlgorithmLabel.length(), signatureAlgorithmStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Display the strings in the text boxes.
                certificateIssuerDnTextView.setText(issuerDNStringBuilder);
                certificateSubjectDnTextView.setText(subjectDNStringBuilder);
                certificateStartDateTextView.setText(startDateStringBuilder);
                certificateEndDateTextView.setText(endDataStringBuilder);
                certificateVersionTextView.setText(certificateVersionStringBuilder);
                certificateSerialNumberTextView.setText(serialNumberStringBuilder);
                certificateSignatureAlgorithmTextView.setText(signatureAlgorithmStringBuilder);
            } catch (CertificateException e) {
                // Do nothing if there is a certificate error.
            }

            // Get a handle for the runtime.
            runtime = Runtime.getRuntime();

            // Get a handle for the activity.
            Activity activity = getActivity();

            // Remove the incorrect lint warning below that the activity might be null.
            assert activity != null;

            // Get a handle for the activity manager.
            activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

            // Remove the incorrect lint warning below that the activity manager might be null.
            assert activityManager != null;

            // Instantiate a memory info variable.
            memoryInfo = new ActivityManager.MemoryInfo();

            // Define a number format.
            numberFormat = NumberFormat.getInstance();

            // Set the minimum and maximum number of fraction digits.
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);

            // Update the memory usage.
            updateMemoryUsage(getActivity());
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing if `PackageManager` says Clear Browser isn't installed.
        }

        // Scroll the tab if the saved instance state is not null.
        if (savedInstanceState != null) {
            aboutVersionLayout.post(() -> {
                aboutVersionLayout.setScrollX(savedInstanceState.getInt("scroll_x"));
                aboutVersionLayout.setScrollY(savedInstanceState.getInt("scroll_y"));
            });
        }

        // Return the tab layout.
        return aboutVersionLayout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // Inflate the about version menu.
        menuInflater.inflate(R.menu.about_version_options_menu, menu);

        // Run the default commands.
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        // Remove the incorrect lint warning below that the activity might be null.
        assert getActivity() != null;

        // Get the ID of the menu item that was selected.
        int menuItemId = menuItem.getItemId();

        // Run the appropriate commands.
        if (menuItemId == R.id.copy) {  // Copy.
            // Get the about version string.
            String aboutVersionString = getAboutVersionString();

            // Get a handle for the clipboard manager.
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

            // Remove the incorrect lint error below that the clipboard manager might be null.
            assert clipboardManager != null;

            // Save the about version string in a clip data.
            ClipData aboutVersionClipData = ClipData.newPlainText(getString(R.string.about), aboutVersionString);

            // Place the clip data on the clipboard.
            clipboardManager.setPrimaryClip(aboutVersionClipData);

            // Display a snackbar.
            Snackbar.make(aboutVersionLayout, R.string.version_info_copied, Snackbar.LENGTH_SHORT).show();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.share) {  // Share.
            // Get the about version string.
            String aboutString = getAboutVersionString();

            // Create an email intent.
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            // Add the about version string to the intent.
            emailIntent.putExtra(Intent.EXTRA_TEXT, aboutString);

            // Set the MIME type.
            emailIntent.setType("text/plain");

            // Set the intent to open in a new task.
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Make it so.
            startActivity(Intent.createChooser(emailIntent, getString(R.string.share)));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save_text) {  // Save text.
            // Instantiate the save alert dialog.
            DialogFragment saveTextDialogFragment = SaveDialog.save(SaveDialog.SAVE_ABOUT_VERSION_TEXT);

            // Show the save alert dialog.
            saveTextDialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.save_dialog));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save_image) {  // Save image.
            // Instantiate the save alert dialog.
            DialogFragment saveImageDialogFragment = SaveDialog.save(SaveDialog.SAVE_ABOUT_VERSION_IMAGE);

            // Show the save alert dialog.
            saveImageDialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.save_dialog));

            // Consume the event.
            return true;
        } else {  // The home button was selected.
            // Return the parent class.
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Save the scroll positions if the layout is not null, which can happen if a tab is not currently selected.
        if (aboutVersionLayout != null) {
            savedInstanceState.putInt("scroll_x", aboutVersionLayout.getScrollX());
            savedInstanceState.putInt("scroll_y", aboutVersionLayout.getScrollY());
        }
    }

    @Override
    public void onPause() {
        // Run the default commands.
        super.onPause();

        // Pause the updating of the memory usage.
        updateMemoryUsageBoolean = false;
    }

    @Override
    public void onResume() {
        // Run the default commands.
        super.onResume();

        // Resume the updating of the memory usage.
        updateMemoryUsageBoolean = true;
    }

    public void updateMemoryUsage(Activity activity) {
        try {
            // Update the memory usage if enabled.
            if (updateMemoryUsageBoolean) {
                // Populate the memory info variable.
                activityManager.getMemoryInfo(memoryInfo);

                // Get the app memory information.
                long appAvailableMemoryLong = runtime.freeMemory();
                long appTotalMemoryLong = runtime.totalMemory();
                long appMaximumMemoryLong = runtime.maxMemory();

                // Calculate the app consumed memory.
                long appConsumedMemoryLong = appTotalMemoryLong - appAvailableMemoryLong;

                // Get the system memory information.
                long systemTotalMemoryLong = memoryInfo.totalMem;
                long systemAvailableMemoryLong = memoryInfo.availMem;

                // Calculate the system consumed memory.
                long systemConsumedMemoryLong = systemTotalMemoryLong - systemAvailableMemoryLong;

                // Convert the memory information into mebibytes.
                float appConsumedMemoryFloat = (float) appConsumedMemoryLong / MEBIBYTE;
                float appAvailableMemoryFloat = (float) appAvailableMemoryLong / MEBIBYTE;
                float appTotalMemoryFloat = (float) appTotalMemoryLong / MEBIBYTE;
                float appMaximumMemoryFloat = (float) appMaximumMemoryLong / MEBIBYTE;
                float systemConsumedMemoryFloat = (float) systemConsumedMemoryLong / MEBIBYTE;
                float systemAvailableMemoryFloat = (float) systemAvailableMemoryLong / MEBIBYTE;
                float systemTotalMemoryFloat = (float) systemTotalMemoryLong / MEBIBYTE;

                // Get the mebibyte string.
                String mebibyte = getString(R.string.mebibyte);

                // Calculate the mebibyte length.
                int mebibyteLength = mebibyte.length();

                // Create spannable string builders.
                SpannableStringBuilder appConsumedMemoryStringBuilder = new SpannableStringBuilder(appConsumedMemoryLabel + numberFormat.format(appConsumedMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder appAvailableMemoryStringBuilder = new SpannableStringBuilder(appAvailableMemoryLabel + numberFormat.format(appAvailableMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder appTotalMemoryStringBuilder = new SpannableStringBuilder(appTotalMemoryLabel + numberFormat.format(appTotalMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder appMaximumMemoryStringBuilder = new SpannableStringBuilder(appMaximumMemoryLabel + numberFormat.format(appMaximumMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder systemConsumedMemoryStringBuilder = new SpannableStringBuilder(systemConsumedMemoryLabel + numberFormat.format(systemConsumedMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder systemAvailableMemoryStringBuilder = new SpannableStringBuilder(systemAvailableMemoryLabel + numberFormat.format(systemAvailableMemoryFloat) + " " + mebibyte);
                SpannableStringBuilder systemTotalMemoryStringBuilder = new SpannableStringBuilder(systemTotalMemoryLabel + numberFormat.format(systemTotalMemoryFloat) + " " + mebibyte);

                // Setup the spans to display the memory information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
                appConsumedMemoryStringBuilder.setSpan(blueColorSpan, appConsumedMemoryLabel.length(), appConsumedMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                appAvailableMemoryStringBuilder.setSpan(blueColorSpan, appAvailableMemoryLabel.length(), appAvailableMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                appTotalMemoryStringBuilder.setSpan(blueColorSpan, appTotalMemoryLabel.length(), appTotalMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                appMaximumMemoryStringBuilder.setSpan(blueColorSpan, appMaximumMemoryLabel.length(), appMaximumMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                systemConsumedMemoryStringBuilder.setSpan(blueColorSpan, systemConsumedMemoryLabel.length(), systemConsumedMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                systemAvailableMemoryStringBuilder.setSpan(blueColorSpan, systemAvailableMemoryLabel.length(), systemAvailableMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                systemTotalMemoryStringBuilder.setSpan(blueColorSpan, systemTotalMemoryLabel.length(), systemTotalMemoryStringBuilder.length() - mebibyteLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // Display the string in the text boxes.
                appConsumedMemoryTextView.setText(appConsumedMemoryStringBuilder);
                appAvailableMemoryTextView.setText(appAvailableMemoryStringBuilder);
                appTotalMemoryTextView.setText(appTotalMemoryStringBuilder);
                appMaximumMemoryTextView.setText(appMaximumMemoryStringBuilder);
                systemConsumedMemoryTextView.setText(systemConsumedMemoryStringBuilder);
                systemAvailableMemoryTextView.setText(systemAvailableMemoryStringBuilder);
                systemTotalMemoryTextView.setText(systemTotalMemoryStringBuilder);
            }

            // Schedule another memory update if the activity has not been destroyed.
            if (!activity.isDestroyed()) {
                // Create a handler to update the memory usage.
                Handler updateMemoryUsageHandler = new Handler();

                // Create a runnable to update the memory usage.
                Runnable updateMemoryUsageRunnable = () -> updateMemoryUsage(activity);

                // Update the memory usage after 1000 milliseconds
                updateMemoryUsageHandler.postDelayed(updateMemoryUsageRunnable, 1000);
            }
        } catch (Exception exception) {
            // Do nothing.
        }
    }

    public String getAboutVersionString() {
        // Initialize an about version string builder.
        StringBuilder aboutVersionStringBuilder = new StringBuilder();

        // Populate the about version string builder.
        aboutVersionStringBuilder.append(ClearBrowserTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(versionTextView.getText());
        aboutVersionStringBuilder.append("\n\n");
        aboutVersionStringBuilder.append(hardwareTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(brandTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(manufacturerTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(modelTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(deviceTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(bootloaderTextView.getText());
        aboutVersionStringBuilder.append("\n");
        if (radioTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(radioTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(softwareTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(androidTextView.getText());
        aboutVersionStringBuilder.append("\n");
        if (securityPatchTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(securityPatchTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        aboutVersionStringBuilder.append(buildTextView.getText());
        aboutVersionStringBuilder.append("\n");
        if (webViewProviderTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(webViewProviderTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        aboutVersionStringBuilder.append(webViewVersionTextView.getText());
        aboutVersionStringBuilder.append("\n");
        if (orbotTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(orbotTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        if (i2pTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(i2pTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        if (openKeychainTextView.getVisibility() == View.VISIBLE) {
            aboutVersionStringBuilder.append(openKeychainTextView.getText());
            aboutVersionStringBuilder.append("\n");
        }
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(memoryUsageTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(appConsumedMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(appAvailableMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(appTotalMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(appMaximumMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(systemConsumedMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(systemAvailableMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(systemTotalMemoryTextView.getText());
        aboutVersionStringBuilder.append("\n\n");
        aboutVersionStringBuilder.append(blocklistsTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(easyListTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(easyPrivacyTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(fanboyAnnoyanceTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(fanboySocialTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(ultraListTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(ultraPrivacyTextView.getText());
        aboutVersionStringBuilder.append("\n\n");
        aboutVersionStringBuilder.append(packageSignatureTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateIssuerDnTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateSubjectDnTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateStartDateTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateEndDateTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateVersionTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateSerialNumberTextView.getText());
        aboutVersionStringBuilder.append("\n");
        aboutVersionStringBuilder.append(certificateSignatureAlgorithmTextView.getText());

        // Return the string.
        return aboutVersionStringBuilder.toString();
    }
}