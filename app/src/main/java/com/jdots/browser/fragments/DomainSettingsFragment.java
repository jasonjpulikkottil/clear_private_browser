/*
 *   2017-2020
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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.activities.DomainsActivity;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.helpers.DomainsDatabaseHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

public class DomainSettingsFragment extends Fragment {
    // Initialize the public class constants.  These are used by activities calling this fragment.
    public static final String DATABASE_ID = "database_id";
    public static final String SCROLL_Y = "scroll_y";

    // Define the public variables.  `databaseId` is public static so it can be accessed from `DomainsActivity`. It is also used in `onCreate()` and `onCreateView()`.
    public static int databaseId;

    // Define the class variables.
    private int scrollY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Remove the lint warning that `getArguments` might be null.
        assert getArguments() != null;

        // Store the database id in `databaseId`.
        databaseId = getArguments().getInt(DATABASE_ID);
        scrollY = getArguments().getInt(SCROLL_Y);
    }

    // The deprecated `getDrawable()` must be used until the minimum API >= 21.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate `domain_settings_fragment`.  `false` does not attach it to the root `container`.
        View domainSettingsView = inflater.inflate(R.layout.domain_settings_fragment, container, false);

        // Get handles for the context and the resources.
        Context context = getContext();
        Resources resources = getResources();

        // Remove the error below that the context might be null.
        assert context != null;

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Get a handle for the shared preference.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Store the default settings.
        String defaultUserAgentName = sharedPreferences.getString("user_agent", getString(R.string.user_agent_default_value));
        String defaultCustomUserAgentString = sharedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value));
        String defaultFontSizeString = sharedPreferences.getString("font_size", getString(R.string.font_size_default_value));
        boolean defaultSwipeToRefresh = sharedPreferences.getBoolean("swipe_to_refresh", true);
        String defaultWebViewTheme = sharedPreferences.getString("webview_theme", getString(R.string.webview_theme_default_value));
        boolean defaultWideViewport = sharedPreferences.getBoolean("wide_viewport", true);
        boolean defaultDisplayWebpageImages = sharedPreferences.getBoolean("display_webpage_images", true);

        // Get handles for the views.
        ScrollView domainSettingsScrollView = domainSettingsView.findViewById(R.id.domain_settings_scrollview);
        EditText domainNameEditText = domainSettingsView.findViewById(R.id.domain_settings_name_edittext);
        ImageView javaScriptImageView = domainSettingsView.findViewById(R.id.javascript_imageview);
        SwitchCompat javaScriptSwitch = domainSettingsView.findViewById(R.id.javascript_switch);
        ImageView firstPartyCookiesImageView = domainSettingsView.findViewById(R.id.first_party_cookies_imageview);
        SwitchCompat firstPartyCookiesSwitch = domainSettingsView.findViewById(R.id.first_party_cookies_switch);
        LinearLayout thirdPartyCookiesLinearLayout = domainSettingsView.findViewById(R.id.third_party_cookies_linearlayout);
        ImageView thirdPartyCookiesImageView = domainSettingsView.findViewById(R.id.third_party_cookies_imageview);
        SwitchCompat thirdPartyCookiesSwitch = domainSettingsView.findViewById(R.id.third_party_cookies_switch);
        ImageView domStorageImageView = domainSettingsView.findViewById(R.id.dom_storage_imageview);
        SwitchCompat domStorageSwitch = domainSettingsView.findViewById(R.id.dom_storage_switch);
        ImageView formDataImageView = domainSettingsView.findViewById(R.id.form_data_imageview);  // The form data views can be remove once the minimum API >= 26.
        SwitchCompat formDataSwitch = domainSettingsView.findViewById(R.id.form_data_switch);  // The form data views can be remove once the minimum API >= 26.
        ImageView easyListImageView = domainSettingsView.findViewById(R.id.easylist_imageview);
        SwitchCompat easyListSwitch = domainSettingsView.findViewById(R.id.easylist_switch);
        ImageView easyPrivacyImageView = domainSettingsView.findViewById(R.id.easyprivacy_imageview);
        SwitchCompat easyPrivacySwitch = domainSettingsView.findViewById(R.id.easyprivacy_switch);
        ImageView fanboysAnnoyanceListImageView = domainSettingsView.findViewById(R.id.fanboys_annoyance_list_imageview);
        SwitchCompat fanboysAnnoyanceListSwitch = domainSettingsView.findViewById(R.id.fanboys_annoyance_list_switch);
        ImageView fanboysSocialBlockingListImageView = domainSettingsView.findViewById(R.id.fanboys_social_blocking_list_imageview);
        SwitchCompat fanboysSocialBlockingListSwitch = domainSettingsView.findViewById(R.id.fanboys_social_blocking_list_switch);
        ImageView ultraListImageView = domainSettingsView.findViewById(R.id.ultralist_imageview);
        SwitchCompat ultraListSwitch = domainSettingsView.findViewById(R.id.ultralist_switch);
        ImageView ultraPrivacyImageView = domainSettingsView.findViewById(R.id.ultraprivacy_imageview);
        SwitchCompat ultraPrivacySwitch = domainSettingsView.findViewById(R.id.ultraprivacy_switch);
        ImageView blockAllThirdPartyRequestsImageView = domainSettingsView.findViewById(R.id.block_all_third_party_requests_imageview);
        SwitchCompat blockAllThirdPartyRequestsSwitch = domainSettingsView.findViewById(R.id.block_all_third_party_requests_switch);
        Spinner userAgentSpinner = domainSettingsView.findViewById(R.id.user_agent_spinner);
        TextView userAgentTextView = domainSettingsView.findViewById(R.id.user_agent_textview);
        EditText customUserAgentEditText = domainSettingsView.findViewById(R.id.custom_user_agent_edittext);
        Spinner fontSizeSpinner = domainSettingsView.findViewById(R.id.font_size_spinner);
        TextView defaultFontSizeTextView = domainSettingsView.findViewById(R.id.default_font_size_textview);
        EditText customFontSizeEditText = domainSettingsView.findViewById(R.id.custom_font_size_edittext);
        ImageView swipeToRefreshImageView = domainSettingsView.findViewById(R.id.swipe_to_refresh_imageview);
        Spinner swipeToRefreshSpinner = domainSettingsView.findViewById(R.id.swipe_to_refresh_spinner);
        TextView swipeToRefreshTextView = domainSettingsView.findViewById(R.id.swipe_to_refresh_textview);
        ImageView webViewThemeImageView = domainSettingsView.findViewById(R.id.webview_theme_imageview);
        Spinner webViewThemeSpinner = domainSettingsView.findViewById(R.id.webview_theme_spinner);
        TextView webViewThemeTextView = domainSettingsView.findViewById(R.id.webview_theme_textview);
        ImageView wideViewportImageView = domainSettingsView.findViewById(R.id.wide_viewport_imageview);
        Spinner wideViewportSpinner = domainSettingsView.findViewById(R.id.wide_viewport_spinner);
        TextView wideViewportTextView = domainSettingsView.findViewById(R.id.wide_viewport_textview);
        ImageView displayWebpageImagesImageView = domainSettingsView.findViewById(R.id.display_webpage_images_imageview);
        Spinner displayWebpageImagesSpinner = domainSettingsView.findViewById(R.id.display_webpage_images_spinner);
        TextView displayImagesTextView = domainSettingsView.findViewById(R.id.display_webpage_images_textview);
        ImageView pinnedSslCertificateImageView = domainSettingsView.findViewById(R.id.pinned_ssl_certificate_imageview);
        SwitchCompat pinnedSslCertificateSwitch = domainSettingsView.findViewById(R.id.pinned_ssl_certificate_switch);
        CardView savedSslCardView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_cardview);
        LinearLayout savedSslCertificateLinearLayout = domainSettingsView.findViewById(R.id.saved_ssl_certificate_linearlayout);
        RadioButton savedSslCertificateRadioButton = domainSettingsView.findViewById(R.id.saved_ssl_certificate_radiobutton);
        TextView savedSslIssuedToCNameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_to_cname);
        TextView savedSslIssuedToONameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_to_oname);
        TextView savedSslIssuedToUNameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_to_uname);
        TextView savedSslIssuedByCNameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_by_cname);
        TextView savedSslIssuedByONameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_by_oname);
        TextView savedSslIssuedByUNameTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_issued_by_uname);
        TextView savedSslStartDateTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_start_date);
        TextView savedSslEndDateTextView = domainSettingsView.findViewById(R.id.saved_ssl_certificate_end_date);
        CardView currentSslCardView = domainSettingsView.findViewById(R.id.current_website_certificate_cardview);
        LinearLayout currentWebsiteCertificateLinearLayout = domainSettingsView.findViewById(R.id.current_website_certificate_linearlayout);
        RadioButton currentWebsiteCertificateRadioButton = domainSettingsView.findViewById(R.id.current_website_certificate_radiobutton);
        TextView currentSslIssuedToCNameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_to_cname);
        TextView currentSslIssuedToONameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_to_oname);
        TextView currentSslIssuedToUNameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_to_uname);
        TextView currentSslIssuedByCNameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_by_cname);
        TextView currentSslIssuedByONameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_by_oname);
        TextView currentSslIssuedByUNameTextView = domainSettingsView.findViewById(R.id.current_website_certificate_issued_by_uname);
        TextView currentSslStartDateTextView = domainSettingsView.findViewById(R.id.current_website_certificate_start_date);
        TextView currentSslEndDateTextView = domainSettingsView.findViewById(R.id.current_website_certificate_end_date);
        TextView noCurrentWebsiteCertificateTextView = domainSettingsView.findViewById(R.id.no_current_website_certificate);
        ImageView pinnedIpAddressesImageView = domainSettingsView.findViewById(R.id.pinned_ip_addresses_imageview);
        SwitchCompat pinnedIpAddressesSwitch = domainSettingsView.findViewById(R.id.pinned_ip_addresses_switch);
        CardView savedIpAddressesCardView = domainSettingsView.findViewById(R.id.saved_ip_addresses_cardview);
        LinearLayout savedIpAddressesLinearLayout = domainSettingsView.findViewById(R.id.saved_ip_addresses_linearlayout);
        RadioButton savedIpAddressesRadioButton = domainSettingsView.findViewById(R.id.saved_ip_addresses_radiobutton);
        TextView savedIpAddressesTextView = domainSettingsView.findViewById(R.id.saved_ip_addresses_textview);
        CardView currentIpAddressesCardView = domainSettingsView.findViewById(R.id.current_ip_addresses_cardview);
        LinearLayout currentIpAddressesLinearLayout = domainSettingsView.findViewById(R.id.current_ip_addresses_linearlayout);
        RadioButton currentIpAddressesRadioButton = domainSettingsView.findViewById(R.id.current_ip_addresses_radiobutton);
        TextView currentIpAddressesTextView = domainSettingsView.findViewById(R.id.current_ip_addresses_textview);

        // Setup the pinned labels.
        String cNameLabel = getString(R.string.common_name) + "  ";
        String oNameLabel = getString(R.string.organization) + "  ";
        String uNameLabel = getString(R.string.organizational_unit) + "  ";
        String startDateLabel = getString(R.string.start_date) + "  ";
        String endDateLabel = getString(R.string.end_date) + "  ";

        // Initialize the database handler.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
        DomainsDatabaseHelper domainsDatabaseHelper = new DomainsDatabaseHelper(context, null, null, 0);

        // Get the database cursor for this ID and move it to the first row.
        Cursor domainCursor = domainsDatabaseHelper.getCursorForId(databaseId);
        domainCursor.moveToFirst();

        // Save the cursor entries as variables.
        String domainNameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME));
        int javaScriptInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_JAVASCRIPT));
        int firstPartyCookiesInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES));
        int thirdPartyCookiesInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES));
        int domStorageInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_DOM_STORAGE));
        int formDataInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FORM_DATA));  // Form data can be remove once the minimum API >= 26.
        int easyListInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYLIST));
        int easyPrivacyInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYPRIVACY));
        int fanboysAnnoyanceListInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST));
        int fanboysSocialBlockingListInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST));
        int ultraListInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ULTRALIST));
        int ultraPrivacyInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY));
        int blockAllThirdPartyRequestsInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS));
        String currentUserAgentName = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.USER_AGENT));
        int fontSizeInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.FONT_SIZE));
        int swipeToRefreshInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.SWIPE_TO_REFRESH));
        int webViewThemeInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.WEBVIEW_THEME));
        int wideViewportInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.WIDE_VIEWPORT));
        int displayImagesInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.DISPLAY_IMAGES));
        int pinnedSslCertificateInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE));
        String savedSslIssuedToCNameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME));
        String savedSslIssuedToONameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION));
        String savedSslIssuedToUNameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT));
        String savedSslIssuedByCNameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME));
        String savedSslIssuedByONameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION));
        String savedSslIssuedByUNameString = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT));
        int pinnedIpAddressesInt = domainCursor.getInt(domainCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_IP_ADDRESSES));
        String savedIpAddresses = domainCursor.getString(domainCursor.getColumnIndex(DomainsDatabaseHelper.IP_ADDRESSES));

        // Initialize the saved SSL certificate date variables.
        Date savedSslStartDate = null;
        Date savedSslEndDate = null;

        // Only get the saved SSL certificate dates from the cursor if they are not set to `0`.
        if (domainCursor.getLong(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_START_DATE)) != 0) {
            savedSslStartDate = new Date(domainCursor.getLong(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_START_DATE)));
        }

        if (domainCursor.getLong(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_END_DATE)) != 0) {
            savedSslEndDate = new Date(domainCursor.getLong(domainCursor.getColumnIndex(DomainsDatabaseHelper.SSL_END_DATE)));
        }

        // Create array adapters for the spinners.
        ArrayAdapter<CharSequence> translatedUserAgentArrayAdapter = ArrayAdapter.createFromResource(context, R.array.translated_domain_settings_user_agent_names, R.layout.spinner_item);
        ArrayAdapter<CharSequence> fontSizeArrayAdapter = ArrayAdapter.createFromResource(context, R.array.font_size_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> swipeToRefreshArrayAdapter = ArrayAdapter.createFromResource(context, R.array.swipe_to_refresh_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> webViewThemeArrayAdapter = ArrayAdapter.createFromResource(context, R.array.webview_theme_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> wideViewportArrayAdapter = ArrayAdapter.createFromResource(context, R.array.wide_viewport_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> displayImagesArrayAdapter = ArrayAdapter.createFromResource(context, R.array.display_webpage_images_array, R.layout.spinner_item);

        // Set the drop down view resource on the spinners.
        translatedUserAgentArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);
        fontSizeArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);
        swipeToRefreshArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);
        webViewThemeArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);
        wideViewportArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);
        displayImagesArrayAdapter.setDropDownViewResource(R.layout.domain_settings_spinner_dropdown_items);

        // Set the array adapters for the spinners.
        userAgentSpinner.setAdapter(translatedUserAgentArrayAdapter);
        fontSizeSpinner.setAdapter(fontSizeArrayAdapter);
        swipeToRefreshSpinner.setAdapter(swipeToRefreshArrayAdapter);
        webViewThemeSpinner.setAdapter(webViewThemeArrayAdapter);
        wideViewportSpinner.setAdapter(wideViewportArrayAdapter);
        displayWebpageImagesSpinner.setAdapter(displayImagesArrayAdapter);

        // Create a spannable string builder for each TextView that needs multiple colors of text.
        SpannableStringBuilder savedSslIssuedToCNameStringBuilder = new SpannableStringBuilder(cNameLabel + savedSslIssuedToCNameString);
        SpannableStringBuilder savedSslIssuedToONameStringBuilder = new SpannableStringBuilder(oNameLabel + savedSslIssuedToONameString);
        SpannableStringBuilder savedSslIssuedToUNameStringBuilder = new SpannableStringBuilder(uNameLabel + savedSslIssuedToUNameString);
        SpannableStringBuilder savedSslIssuedByCNameStringBuilder = new SpannableStringBuilder(cNameLabel + savedSslIssuedByCNameString);
        SpannableStringBuilder savedSslIssuedByONameStringBuilder = new SpannableStringBuilder(oNameLabel + savedSslIssuedByONameString);
        SpannableStringBuilder savedSslIssuedByUNameStringBuilder = new SpannableStringBuilder(uNameLabel + savedSslIssuedByUNameString);

        // Initialize the spannable string builders for the saved SSL certificate dates.
        SpannableStringBuilder savedSslStartDateStringBuilder;
        SpannableStringBuilder savedSslEndDateStringBuilder;

        // Leave the SSL certificate dates empty if they are `null`.
        if (savedSslStartDate == null) {
            savedSslStartDateStringBuilder = new SpannableStringBuilder(startDateLabel);
        } else {
            savedSslStartDateStringBuilder = new SpannableStringBuilder(startDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(savedSslStartDate));
        }

        if (savedSslEndDate == null) {
            savedSslEndDateStringBuilder = new SpannableStringBuilder(endDateLabel);
        } else {
            savedSslEndDateStringBuilder = new SpannableStringBuilder(endDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(savedSslEndDate));
        }

        // Create the foreground color spans.
        final ForegroundColorSpan blueColorSpan;
        final ForegroundColorSpan redColorSpan;

        // Set the color spans according to the theme.  The deprecated `getColor()` must be used until the minimum API >= 23.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            blueColorSpan = new ForegroundColorSpan(resources.getColor(R.color.blue_700));
            redColorSpan = new ForegroundColorSpan(resources.getColor(R.color.red_a700));
        } else {
            blueColorSpan = new ForegroundColorSpan(resources.getColor(R.color.violet_700));
            redColorSpan = new ForegroundColorSpan(resources.getColor(R.color.red_900));
        }

        // Set the domain name from the the database cursor.
        domainNameEditText.setText(domainNameString);

        // Update the certificates' `Common Name` color when the domain name text changes.
        domainNameEditText.addTextChangedListener(new TextWatcher() {
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
                // Get the new domain name.
                String newDomainName = domainNameEditText.getText().toString();

                // Check the saved SSL certificate against the new domain name.
                boolean savedSslMatchesNewDomainName = checkDomainNameAgainstCertificate(newDomainName, savedSslIssuedToCNameString);

                // Create a `SpannableStringBuilder` for the saved certificate `Common Name`.
                SpannableStringBuilder savedSslCNameStringBuilder = new SpannableStringBuilder(cNameLabel + savedSslIssuedToCNameString);

                // Format the saved certificate `Common Name` color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
                if (savedSslMatchesNewDomainName) {
                    savedSslCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), savedSslCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else {
                    savedSslCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), savedSslCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }

                // Update the saved SSL issued to CName text view.
                savedSslIssuedToCNameTextView.setText(savedSslCNameStringBuilder);

                // Update the current website certificate if it exists.
                if (DomainsActivity.sslIssuedToCName != null) {
                    // Check the current website certificate against the new domain name.
                    boolean currentSslMatchesNewDomainName = checkDomainNameAgainstCertificate(newDomainName, DomainsActivity.sslIssuedToCName);

                    // Create a `SpannableStringBuilder` for the current website certificate `Common Name`.
                    SpannableStringBuilder currentSslCNameStringBuilder = new SpannableStringBuilder(cNameLabel + DomainsActivity.sslIssuedToCName);

                    // Format the current certificate `Common Name` color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
                    if (currentSslMatchesNewDomainName) {
                        currentSslCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), currentSslCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    } else {
                        currentSslCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), currentSslCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    // Update the current SSL issued to CName text view.
                    currentSslIssuedToCNameTextView.setText(currentSslCNameStringBuilder);
                }
            }
        });

        // Set the JavaScript switch status.
        if (javaScriptInt == 1) {  // JavaScript is enabled.
            javaScriptSwitch.setChecked(true);
            javaScriptImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.javascript_enabled, null));
        } else {  // JavaScript is disabled.
            javaScriptSwitch.setChecked(false);
            javaScriptImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.privacy_mode, null));
        }

        // Set the first-party cookies status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (firstPartyCookiesInt == 1) {  // First-party cookies are enabled.
            firstPartyCookiesSwitch.setChecked(true);
            firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_enabled, null));
        } else {  // First-party cookies are disabled.
            firstPartyCookiesSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_night, null));
            } else {
                firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_day, null));
            }
        }

        // Only display third-party cookies if SDK_INT >= 21.
        if (Build.VERSION.SDK_INT >= 21) {  // Third-party cookies can be configured for API >= 21.
            // Only enable third-party-cookies if first-party cookies are enabled.
            if (firstPartyCookiesInt == 1) {  // First-party cookies are enabled.
                // Set the third-party cookies status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
                if (thirdPartyCookiesInt == 1) {  // Both first-party and third-party cookies are enabled.
                    // Set the third-party cookies switch to be checked.
                    thirdPartyCookiesSwitch.setChecked(true);

                    // Set the icon to be red.
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_warning, null));
                } else {  // First party cookies are enabled but third-party cookies are disabled.
                    // Set the third-party cookies switch to be checked.
                    thirdPartyCookiesSwitch.setChecked(false);

                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_night, null));
                    } else {
                        thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_day, null));
                    }
                }
            } else {  // First-party cookies are disabled.
                // Set the status of third-party cookies.
                thirdPartyCookiesSwitch.setChecked(thirdPartyCookiesInt == 1);

                // Disable the third-party cookies switch.
                thirdPartyCookiesSwitch.setEnabled(false);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_ghosted_night, null));
                } else {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_ghosted_day, null));
                }
            }
        } else {  // Third-party cookies cannot be configured for API <= 21.
            // Hide the LinearLayout for third-party cookies.
            thirdPartyCookiesLinearLayout.setVisibility(View.GONE);
        }

        // Only enable DOM storage if JavaScript is enabled.
        if (javaScriptInt == 1) {  // JavaScript is enabled.
            // Enable the DOM storage `Switch`.
            domStorageSwitch.setEnabled(true);

            // Set the DOM storage status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
            if (domStorageInt == 1) {  // Both JavaScript and DOM storage are enabled.
                domStorageSwitch.setChecked(true);
                domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_enabled, null));
            } else {  // JavaScript is enabled but DOM storage is disabled.
                // Set the DOM storage switch to off.
                domStorageSwitch.setChecked(false);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_night, null));
                } else {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_day, null));
                }
            }
        } else {  // JavaScript is disabled.
            // Disable the DOM storage `Switch`.
            domStorageSwitch.setEnabled(false);

            // Set the checked status of DOM storage.
            domStorageSwitch.setChecked(domStorageInt == 1);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_ghosted_night, null));
            } else {
                domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_ghosted_day, null));
            }
        }

        // Set the form data visibility.  Form data can be removed once the minimum API >= 26.
        if (Build.VERSION.SDK_INT >= 26) {  // Form data no longer applies to newer versions of Android.
            // Hide the form data image view and switch.
            formDataImageView.setVisibility(View.GONE);
            formDataSwitch.setVisibility(View.GONE);
        } else {  // Form data should be displayed because this is an older version of Android.
            if (formDataInt == 1) {  // Form data is on.
                // Turn the form data switch on.
                formDataSwitch.setChecked(true);

                // Set the form data icon.
                formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_enabled, null));
            } else {  // Form data is off.
                // Turn the form data switch to off.
                formDataSwitch.setChecked(false);

                // Set the icon according to the theme.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_disabled_night, null));
                } else {
                    formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_disabled_day, null));
                }
            }
        }

        // Set the EasyList status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (easyListInt == 1) {  // EasyList is on.
            // Turn the switch on.
            easyListSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_night, null));
            } else {
                easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_day, null));
            }
        } else {  // EasyList is off.
            // Turn the switch off.
            easyListSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_night, null));
            } else {
                easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_day, null));
            }
        }

        // Set the EasyPrivacy status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (easyPrivacyInt == 1) {  // EasyPrivacy is on.
            // Turn the switch on.
            easyPrivacySwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_night, null));
            } else {
                easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_day, null));
            }
        } else {  // EasyPrivacy is off.
            // Turn the switch off.
            easyPrivacySwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_night, null));
            } else {
                easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_day, null));
            }
        }

        // Set the Fanboy's Annoyance List status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (fanboysAnnoyanceListInt == 1) {  // Fanboy's Annoyance List is on.
            // Turn the switch on.
            fanboysAnnoyanceListSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_night, null));
            } else {
                fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_day, null));
            }
        } else {  // Fanboy's Annoyance List is off.
            // Turn the switch off.
            fanboysAnnoyanceListSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_night, null));
            } else {
                fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_day, null));
            }
        }

        // Only enable Fanboy's Social Blocking List if Fanboy's Annoyance List is off.
        if (fanboysAnnoyanceListInt == 0) {  // Fanboy's Annoyance List is on.
            // Enable Fanboy's Social Blocking List switch.
            fanboysSocialBlockingListSwitch.setEnabled(true);

            // Enable Fanboy's Social Blocking List.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
            if (fanboysSocialBlockingListInt == 1) {  // Fanboy's Social Blocking List is on.
                // Turn on Fanboy's Social Blocking List switch.
                fanboysSocialBlockingListSwitch.setChecked(true);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_night, null));
                } else {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_day, null));
                }
            } else {  // Fanboy's Social Blocking List is off.
                // Turn off Fanboy's Social Blocking List switch.
                fanboysSocialBlockingListSwitch.setChecked(false);

                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_night, null));
                } else {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_day, null));
                }
            }
        } else {  // Fanboy's Annoyance List is on.
            // Disable Fanboy's Social Blocking List switch.
            fanboysSocialBlockingListSwitch.setEnabled(false);

            // Set the status of Fanboy's Social Blocking List.
            fanboysSocialBlockingListSwitch.setChecked(fanboysSocialBlockingListInt == 1);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_ghosted_night, null));
            } else {
                fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_ghosted_day, null));
            }
        }

        // Set the UltraList status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (ultraListInt == 1) {  // UltraList is on.
            // Turn the switch on.
            ultraListSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_night, null));
            } else {
                ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_day, null));
            }
        } else {  // UltraList is off.
            // Turn the switch off.
            ultraListSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_night, null));
            } else {
                ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_day, null));
            }
        }

        // Set the UltraPrivacy status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (ultraPrivacyInt == 1) {  // UltraPrivacy is on.
            // Turn the switch on.
            ultraPrivacySwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_night, null));
            } else {
                ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_day, null));
            }
        } else {  // EasyPrivacy is off.
            // Turn the switch off.
            ultraPrivacySwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_night, null));
            } else {
                ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_day, null));
            }
        }

        // Set the third-party resource blocking status.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        if (blockAllThirdPartyRequestsInt == 1) {  // Blocking all third-party requests is on.
            // Turn the switch on.
            blockAllThirdPartyRequestsSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_enabled_night, null));
            } else {
                blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_enabled_day, null));
            }
        } else {  // Blocking all third-party requests is off.
            // Turn the switch off.
            blockAllThirdPartyRequestsSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_disabled_night, null));
            } else {
                blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_disabled_day, null));
            }
        }

        // Inflated a WebView to get the default user agent.
        // `@SuppressLint("InflateParams")` removes the warning about using `null` as the `ViewGroup`, which in this case makes sense because the bare WebView should not be displayed on the screen.
        @SuppressLint("InflateParams") View bareWebViewLayout = inflater.inflate(R.layout.bare_webview, null, false);
        WebView bareWebView = bareWebViewLayout.findViewById(R.id.bare_webview);
        final String webViewDefaultUserAgentString = bareWebView.getSettings().getUserAgentString();

        // Get a handle for the user agent array adapter.  This array does not contain the `System default` entry.
        ArrayAdapter<CharSequence> userAgentNamesArray = ArrayAdapter.createFromResource(context, R.array.user_agent_names, R.layout.spinner_item);

        // Get the positions of the user agent and the default user agent.
        int userAgentArrayPosition = userAgentNamesArray.getPosition(currentUserAgentName);
        int defaultUserAgentArrayPosition = userAgentNamesArray.getPosition(defaultUserAgentName);

        // Get a handle for the user agent data array.  This array does not contain the `System default` entry.
        String[] userAgentDataArray = resources.getStringArray(R.array.user_agent_data);

        // Set the user agent text.
        if (currentUserAgentName.equals(getString(R.string.system_default_user_agent))) {  // Use the system default user agent.
            // Set the user agent according to the system default.
            switch (defaultUserAgentArrayPosition) {
                case MainWebViewActivity.UNRECOGNIZED_USER_AGENT:  // The default user agent name is not on the canonical list.
                    // This is probably because it was set in an older version of Clear Browser before the switch to persistent user agent names.
                    userAgentTextView.setText(defaultUserAgentName);
                    break;

                case MainWebViewActivity.SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                    // Display the `WebView` default user agent.
                    userAgentTextView.setText(webViewDefaultUserAgentString);
                    break;

                case MainWebViewActivity.SETTINGS_CUSTOM_USER_AGENT:
                    // Display the custom user agent.
                    userAgentTextView.setText(defaultCustomUserAgentString);
                    break;

                default:
                    // Get the user agent string from the user agent data array.
                    userAgentTextView.setText(userAgentDataArray[defaultUserAgentArrayPosition]);
            }
        } else if (userAgentArrayPosition == MainWebViewActivity.UNRECOGNIZED_USER_AGENT) {  // A custom user agent is stored in the current user agent name.
            // Set the user agent spinner to `Custom user agent`.
            userAgentSpinner.setSelection(MainWebViewActivity.DOMAINS_CUSTOM_USER_AGENT);

            // Hide the user agent TextView.
            userAgentTextView.setVisibility(View.GONE);

            // Show the custom user agent EditText and set the current user agent name as the text.
            customUserAgentEditText.setVisibility(View.VISIBLE);
            customUserAgentEditText.setText(currentUserAgentName);
        } else {  // The user agent name contains one of the canonical user agents.
            // Set the user agent spinner selection.  The spinner has one more entry at the beginning than the user agent data array, so the position must be incremented.
            userAgentSpinner.setSelection(userAgentArrayPosition + 1);

            // Show the user agent TextView.
            userAgentTextView.setVisibility(View.VISIBLE);

            // Hide the custom user agent EditText.
            customUserAgentEditText.setVisibility(View.GONE);

            // Set the user agent text.
            if (userAgentArrayPosition == MainWebViewActivity.DOMAINS_WEBVIEW_DEFAULT_USER_AGENT) {  // The WebView default user agent is selected.
                // Display the WebView default user agent.
                userAgentTextView.setText(webViewDefaultUserAgentString);
            } else {  // A user agent besides the default is selected.
                // Get the user agent string from the user agent data array.  The spinner has one more entry at the beginning than the user agent data array, so the position must be incremented.
                userAgentTextView.setText(userAgentDataArray[userAgentArrayPosition + 1]);
            }
        }

        // Open the user agent spinner when the text view is clicked.
        userAgentTextView.setOnClickListener((View v) -> {
            // Open the user agent spinner.
            userAgentSpinner.performClick();
        });

        // Display the font size settings.
        if (fontSizeInt == 0) {  // `0` is the code for system default font size.
            // Set the font size to the system default
            fontSizeSpinner.setSelection(0);

            // Show the default font size text view.
            defaultFontSizeTextView.setVisibility(View.VISIBLE);

            // Hide the custom font size edit text.
            customFontSizeEditText.setVisibility(View.GONE);

            // Set the default font size as the text of the custom font size edit text.  This way, if the user switches to custom it will already be populated.
            customFontSizeEditText.setText(defaultFontSizeString);
        } else {  // A custom font size is selected.
            // Set the spinner to the custom font size.
            fontSizeSpinner.setSelection(1);

            // Hide the default font size text view.
            defaultFontSizeTextView.setVisibility(View.GONE);

            // Show the custom font size edit text.
            customFontSizeEditText.setVisibility(View.GONE);

            // Set the custom font size.
            customFontSizeEditText.setText(String.valueOf(fontSizeInt));
        }

        // Initialize the default font size percentage string.
        String defaultFontSizePercentageString = defaultFontSizeString + "%";

        // Set the default font size text in the text view.
        defaultFontSizeTextView.setText(defaultFontSizePercentageString);

        // Open the font size spinner when the text view is clicked.
        defaultFontSizeTextView.setOnClickListener((View v) -> {
            // Open the user agent spinner.
            fontSizeSpinner.performClick();
        });

        // Select the swipe to refresh selection in the spinner.
        swipeToRefreshSpinner.setSelection(swipeToRefreshInt);

        // Set the swipe to refresh text.
        if (defaultSwipeToRefresh) {
            swipeToRefreshTextView.setText(swipeToRefreshArrayAdapter.getItem(DomainsDatabaseHelper.ENABLED));
        } else {
            swipeToRefreshTextView.setText(swipeToRefreshArrayAdapter.getItem(DomainsDatabaseHelper.DISABLED));
        }

        // Set the swipe to refresh icon and TextView settings.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        switch (swipeToRefreshInt) {
            case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                if (defaultSwipeToRefresh) {  // Swipe to refresh is enabled by default.
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_night, null));
                    } else {
                        swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_day, null));
                    }
                } else {  // Swipe to refresh is disabled by default
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_night, null));
                    } else {
                        swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_day, null));
                    }
                }

                // Show the swipe to refresh TextView.
                swipeToRefreshTextView.setVisibility(View.VISIBLE);
                break;

            case DomainsDatabaseHelper.ENABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_night, null));
                } else {
                    swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_day, null));
                }

                // Hide the swipe to refresh TextView.`
                swipeToRefreshTextView.setVisibility(View.GONE);
                break;

            case DomainsDatabaseHelper.DISABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_night, null));
                } else {
                    swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_day, null));
                }

                // Hide the swipe to refresh TextView.
                swipeToRefreshTextView.setVisibility(View.GONE);
                break;
        }

        // Open the swipe to refresh spinner when the TextView is clicked.
        swipeToRefreshTextView.setOnClickListener((View v) -> {
            // Open the swipe to refresh spinner.
            swipeToRefreshSpinner.performClick();
        });

        // Get the WebView theme string arrays.
        String[] webViewThemeStringArray = resources.getStringArray(R.array.webview_theme_array);
        String[] webViewThemeEntryValuesStringArray = resources.getStringArray(R.array.webview_theme_entry_values);

        // Define an app WebView theme entry number.
        int appWebViewThemeEntryNumber;

        // Get the WebView theme entry number that matches the current WebView theme.  A switch statement cannot be used because the WebView theme entry values string array is not a compile time constant.
        if (defaultWebViewTheme.equals(webViewThemeEntryValuesStringArray[1])) {  // The light theme is selected.
            // Store the default WebView theme entry number.
            appWebViewThemeEntryNumber = 1;
        } else if (defaultWebViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
            // Store the default WebView theme entry number.
            appWebViewThemeEntryNumber = 2;
        } else {  // The system default theme is selected.
            // Store the default WebView theme entry number.
            appWebViewThemeEntryNumber = 0;
        }

        // Set the WebView theme visibility.
        if (Build.VERSION.SDK_INT < 21) {  // The WebView theme cannot be set on API 19.
            // Get a handle for the webView theme linear layout.
            LinearLayout webViewThemeLinearLayout = domainSettingsView.findViewById(R.id.webview_theme_linearlayout);

            // Hide the WebView theme linear layout.
            webViewThemeLinearLayout.setVisibility(View.GONE);
        } else {  // The WebView theme can be set on API >= 21.
            // Select the WebView theme in the spinner.
            webViewThemeSpinner.setSelection(webViewThemeInt);

            // Set the WebView theme text.
            if (appWebViewThemeEntryNumber == DomainsDatabaseHelper.SYSTEM_DEFAULT) {  // The app WebView theme is system default.
                // Set the text according to the current UI theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    webViewThemeTextView.setText(webViewThemeStringArray[DomainsDatabaseHelper.LIGHT_THEME]);
                } else {
                    webViewThemeTextView.setText(webViewThemeStringArray[DomainsDatabaseHelper.DARK_THEME]);
                }
            } else {  // The app WebView theme is not system default.
                // Set the text according to the app WebView theme.
                webViewThemeTextView.setText(webViewThemeStringArray[appWebViewThemeEntryNumber]);
            }

            // Set the WebView theme icon and text visibility.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
            switch (webViewThemeInt) {
                case DomainsDatabaseHelper.SYSTEM_DEFAULT:  // The domain WebView theme is system default.
                    // Set the icon according to the app WebView theme.
                    switch (appWebViewThemeEntryNumber) {
                        case DomainsDatabaseHelper.SYSTEM_DEFAULT:  // The default WebView theme is system default.
                            // Set the icon according to the app theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                // Set the light mode icon.
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                            } else {
                                // Set the dark theme icon.
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                            }
                            break;

                        case DomainsDatabaseHelper.LIGHT_THEME:  // the default WebView theme is light.
                            // Set the icon according to the app theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                            } else {
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_night, null));
                            }
                            break;

                        case DomainsDatabaseHelper.DARK_THEME:  // the default WebView theme is dark.
                            // Set the icon according to the app theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_day, null));
                            } else {
                                webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                            }
                            break;
                    }

                    // Show the WebView theme text view.
                    webViewThemeTextView.setVisibility(View.VISIBLE);
                    break;

                case DomainsDatabaseHelper.LIGHT_THEME:  // The domain WebView theme is light.
                    // Set the icon according to the app theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                        webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                    } else {
                        webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_night, null));
                    }

                    // Hide the WebView theme text view.
                    webViewThemeTextView.setVisibility(View.GONE);
                    break;

                case DomainsDatabaseHelper.DARK_THEME:  // The domain WebView theme is dark.
                    // Set the icon according to the app theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                        webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_day, null));
                    } else {
                        webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                    }

                    // Hide the WebView theme text view.
                    webViewThemeTextView.setVisibility(View.GONE);
                    break;
            }

            // Open the WebView theme spinner when the text view is clicked.
            webViewThemeTextView.setOnClickListener((View v) -> {
                // Open the WebView theme spinner.
                webViewThemeSpinner.performClick();
            });
        }

        // Select the wide viewport in the spinner.
        wideViewportSpinner.setSelection(wideViewportInt);

        // Set the default wide viewport text.
        if (defaultWideViewport) {
            wideViewportTextView.setText(wideViewportArrayAdapter.getItem(DomainsDatabaseHelper.ENABLED));
        } else {
            wideViewportTextView.setText(wideViewportArrayAdapter.getItem(DomainsDatabaseHelper.DISABLED));
        }

        // Set the wide viewport icon and text view settings.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        switch (wideViewportInt) {
            case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                if (defaultWideViewport) {  // Wide viewport enabled by default.
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_night, null));
                    } else {
                        wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_day, null));
                    }
                } else {  // Wide viewport disabled by default.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_night, null));
                    } else {
                        wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_day, null));
                    }
                }

                // Show the wide viewport text view.
                wideViewportTextView.setVisibility(View.VISIBLE);
                break;

            case DomainsDatabaseHelper.ENABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_night, null));
                } else {
                    wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_day, null));
                }

                // Hide the wide viewport text view.
                wideViewportTextView.setVisibility(View.GONE);
                break;

            case DomainsDatabaseHelper.DISABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_night, null));
                } else {
                    wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_day, null));
                }

                // Hide the wide viewport text view.
                wideViewportTextView.setVisibility(View.GONE);
                break;
        }

        // Open the wide viewport spinner when the text view is clicked.
        wideViewportTextView.setOnClickListener((View view) -> {
            // Open the wide viewport spinner.
            wideViewportSpinner.performClick();
        });

        // Display the website images mode in the spinner.
        displayWebpageImagesSpinner.setSelection(displayImagesInt);

        // Set the default display images text.
        if (defaultDisplayWebpageImages) {
            displayImagesTextView.setText(displayImagesArrayAdapter.getItem(DomainsDatabaseHelper.ENABLED));
        } else {
            displayImagesTextView.setText(displayImagesArrayAdapter.getItem(DomainsDatabaseHelper.DISABLED));
        }

        // Set the display website images icon and text view settings.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
        switch (displayImagesInt) {
            case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                if (defaultDisplayWebpageImages) {  // Display webpage images enabled by default.
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_night, null));
                    } else {
                        displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_day, null));
                    }
                } else {  // Display webpage images disabled by default.
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_night, null));
                    } else {
                        displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_day, null));
                    }
                }

                // Show the display images text view.
                displayImagesTextView.setVisibility(View.VISIBLE);
                break;

            case DomainsDatabaseHelper.ENABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_night, null));
                } else {
                    displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_day, null));
                }

                // Hide the display images text view.
                displayImagesTextView.setVisibility(View.GONE);
                break;

            case DomainsDatabaseHelper.DISABLED:
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_night, null));
                } else {
                    displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_day, null));
                }

                // Hide the display images text view.
                displayImagesTextView.setVisibility(View.GONE);
                break;
        }

        // Open the display images spinner when the text view is clicked.
        displayImagesTextView.setOnClickListener((View view) -> {
            // Open the user agent spinner.
            displayWebpageImagesSpinner.performClick();
        });

        // Set the pinned SSL certificate icon.
        if (pinnedSslCertificateInt == 1) {  // Pinned SSL certificate is enabled.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
            // Check the switch.
            pinnedSslCertificateSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_night, null));
            } else {
                pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_day, null));
            }
        } else {  // Pinned SSL certificate is disabled.
            // Uncheck the switch.
            pinnedSslCertificateSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_night, null));
            } else {
                pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_day, null));
            }
        }

        // Store the current date.
        Date currentDate = Calendar.getInstance().getTime();

        // Setup the string builders to display the general certificate information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        savedSslIssuedToONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), savedSslIssuedToONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        savedSslIssuedToUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), savedSslIssuedToUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        savedSslIssuedByCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), savedSslIssuedByCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        savedSslIssuedByONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), savedSslIssuedByONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        savedSslIssuedByUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), savedSslIssuedByUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        // Check the certificate Common Name against the domain name.
        boolean savedSslCommonNameMatchesDomainName = checkDomainNameAgainstCertificate(domainNameString, savedSslIssuedToCNameString);

        // Format the issued to Common Name color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        if (savedSslCommonNameMatchesDomainName) {
            savedSslIssuedToCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), savedSslIssuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            savedSslIssuedToCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), savedSslIssuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        //  Format the start date color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        if ((savedSslStartDate != null) && savedSslStartDate.after(currentDate)) {  // The certificate start date is in the future.
            savedSslStartDateStringBuilder.setSpan(redColorSpan, startDateLabel.length(), savedSslStartDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {  // The certificate start date is in the past.
            savedSslStartDateStringBuilder.setSpan(blueColorSpan, startDateLabel.length(), savedSslStartDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // Format the end date color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
        if ((savedSslEndDate != null) && savedSslEndDate.before(currentDate)) {  // The certificate end date is in the past.
            savedSslEndDateStringBuilder.setSpan(redColorSpan, endDateLabel.length(), savedSslEndDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {  // The certificate end date is in the future.
            savedSslEndDateStringBuilder.setSpan(blueColorSpan, endDateLabel.length(), savedSslEndDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        // Display the saved website SSL certificate strings.
        savedSslIssuedToCNameTextView.setText(savedSslIssuedToCNameStringBuilder);
        savedSslIssuedToONameTextView.setText(savedSslIssuedToONameStringBuilder);
        savedSslIssuedToUNameTextView.setText(savedSslIssuedToUNameStringBuilder);
        savedSslIssuedByCNameTextView.setText(savedSslIssuedByCNameStringBuilder);
        savedSslIssuedByONameTextView.setText(savedSslIssuedByONameStringBuilder);
        savedSslIssuedByUNameTextView.setText(savedSslIssuedByUNameStringBuilder);
        savedSslStartDateTextView.setText(savedSslStartDateStringBuilder);
        savedSslEndDateTextView.setText(savedSslEndDateStringBuilder);

        // Populate the current website SSL certificate if there is one.
        if (DomainsActivity.sslIssuedToCName != null) {
            // Get dates from the raw long values.
            Date currentSslStartDate = new Date(DomainsActivity.sslStartDateLong);
            Date currentSslEndDate = new Date(DomainsActivity.sslEndDateLong);

            // Create a spannable string builder for each text view that needs multiple colors of text.
            SpannableStringBuilder currentSslIssuedToCNameStringBuilder = new SpannableStringBuilder(cNameLabel + DomainsActivity.sslIssuedToCName);
            SpannableStringBuilder currentSslIssuedToONameStringBuilder = new SpannableStringBuilder(oNameLabel + DomainsActivity.sslIssuedToOName);
            SpannableStringBuilder currentSslIssuedToUNameStringBuilder = new SpannableStringBuilder(uNameLabel + DomainsActivity.sslIssuedToUName);
            SpannableStringBuilder currentSslIssuedByCNameStringBuilder = new SpannableStringBuilder(cNameLabel + DomainsActivity.sslIssuedByCName);
            SpannableStringBuilder currentSslIssuedByONameStringBuilder = new SpannableStringBuilder(oNameLabel + DomainsActivity.sslIssuedByOName);
            SpannableStringBuilder currentSslIssuedByUNameStringBuilder = new SpannableStringBuilder(uNameLabel + DomainsActivity.sslIssuedByUName);
            SpannableStringBuilder currentSslStartDateStringBuilder = new SpannableStringBuilder(startDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG)
                    .format(currentSslStartDate));
            SpannableStringBuilder currentSslEndDateStringBuilder = new SpannableStringBuilder(endDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG)
                    .format(currentSslEndDate));

            // Setup the string builders to display the general certificate information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            currentSslIssuedToONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), currentSslIssuedToONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            currentSslIssuedToUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), currentSslIssuedToUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            currentSslIssuedByCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), currentSslIssuedByCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            currentSslIssuedByONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), currentSslIssuedByONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            currentSslIssuedByUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), currentSslIssuedByUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            // Check the certificate Common Name against the domain name.
            boolean currentSslCommonNameMatchesDomainName = checkDomainNameAgainstCertificate(domainNameString, DomainsActivity.sslIssuedToCName);

            // Format the issued to Common Name color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            if (currentSslCommonNameMatchesDomainName) {
                currentSslIssuedToCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), currentSslIssuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                currentSslIssuedToCNameStringBuilder.setSpan(redColorSpan, cNameLabel.length(), currentSslIssuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            //  Format the start date color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            if (currentSslStartDate.after(currentDate)) {  // The certificate start date is in the future.
                currentSslStartDateStringBuilder.setSpan(redColorSpan, startDateLabel.length(), currentSslStartDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {  // The certificate start date is in the past.
                currentSslStartDateStringBuilder.setSpan(blueColorSpan, startDateLabel.length(), currentSslStartDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // Format the end date color.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            if (currentSslEndDate.before(currentDate)) {  // The certificate end date is in the past.
                currentSslEndDateStringBuilder.setSpan(redColorSpan, endDateLabel.length(), currentSslEndDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {  // The certificate end date is in the future.
                currentSslEndDateStringBuilder.setSpan(blueColorSpan, endDateLabel.length(), currentSslEndDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // Display the current website SSL certificate strings.
            currentSslIssuedToCNameTextView.setText(currentSslIssuedToCNameStringBuilder);
            currentSslIssuedToONameTextView.setText(currentSslIssuedToONameStringBuilder);
            currentSslIssuedToUNameTextView.setText(currentSslIssuedToUNameStringBuilder);
            currentSslIssuedByCNameTextView.setText(currentSslIssuedByCNameStringBuilder);
            currentSslIssuedByONameTextView.setText(currentSslIssuedByONameStringBuilder);
            currentSslIssuedByUNameTextView.setText(currentSslIssuedByUNameStringBuilder);
            currentSslStartDateTextView.setText(currentSslStartDateStringBuilder);
            currentSslEndDateTextView.setText(currentSslEndDateStringBuilder);
        }

        // Set the initial display status of the SSL certificates card views.
        if (pinnedSslCertificateSwitch.isChecked()) {  // An SSL certificate is pinned.
            // Set the visibility of the saved SSL certificate.
            if (savedSslIssuedToCNameString == null) {
                savedSslCardView.setVisibility(View.GONE);
            } else {
                savedSslCardView.setVisibility(View.VISIBLE);
            }

            // Set the visibility of the current website SSL certificate.
            if (DomainsActivity.sslIssuedToCName == null) {  // There is no current SSL certificate.
                // Hide the SSL certificate.
                currentSslCardView.setVisibility(View.GONE);

                // Show the instruction.
                noCurrentWebsiteCertificateTextView.setVisibility(View.VISIBLE);
            } else {  // There is a current SSL certificate.
                // Show the SSL certificate.
                currentSslCardView.setVisibility(View.VISIBLE);

                // Hide the instruction.
                noCurrentWebsiteCertificateTextView.setVisibility(View.GONE);
            }

            // Set the status of the radio buttons and the card view backgrounds.
            if (savedSslCardView.getVisibility() == View.VISIBLE) {  // The saved SSL certificate is displayed.
                // Check the saved SSL certificate radio button.
                savedSslCertificateRadioButton.setChecked(true);

                // Uncheck the current website SSL certificate radio button.
                currentWebsiteCertificateRadioButton.setChecked(false);

                // Darken the background of the current website SSL certificate linear layout according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                } else {
                    currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                }
            } else if (currentSslCardView.getVisibility() == View.VISIBLE) {  // The saved SSL certificate is hidden but the current website SSL certificate is visible.
                // Check the current website SSL certificate radio button.
                currentWebsiteCertificateRadioButton.setChecked(true);

                // Uncheck the saved SSL certificate radio button.
                savedSslCertificateRadioButton.setChecked(false);
            } else {  // Neither SSL certificate is visible.
                // Uncheck both radio buttons.
                savedSslCertificateRadioButton.setChecked(false);
                currentWebsiteCertificateRadioButton.setChecked(false);
            }
        } else {  // An SSL certificate is not pinned.
            // Hide the SSl certificates and instructions.
            savedSslCardView.setVisibility(View.GONE);
            currentSslCardView.setVisibility(View.GONE);
            noCurrentWebsiteCertificateTextView.setVisibility(View.GONE);

            // Uncheck the radio buttons.
            savedSslCertificateRadioButton.setChecked(false);
            currentWebsiteCertificateRadioButton.setChecked(false);
        }

        // Set the pinned IP addresses icon.
        if (pinnedIpAddressesInt == 1) {  // Pinned IP addresses is enabled.  Once the minimum API >= 21 a selector can be sued as the tint mode instead of specifying different icons.
            // Check the switch.
            pinnedIpAddressesSwitch.setChecked(true);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_night, null));
            } else {
                pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_day, null));
            }
        } else {  // Pinned IP Addresses is disabled.
            // Uncheck the switch.
            pinnedIpAddressesSwitch.setChecked(false);

            // Set the icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_night, null));
            } else {
                pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_day, null));
            }
        }

        // Populate the saved and current IP addresses.
        savedIpAddressesTextView.setText(savedIpAddresses);
        currentIpAddressesTextView.setText(DomainsActivity.currentIpAddresses);

        // Set the initial display status of the IP addresses card views.
        if (pinnedIpAddressesSwitch.isChecked()) {  // IP addresses are pinned.
            // Set the visibility of the saved IP addresses.
            if (savedIpAddresses == null) {  // There are no saved IP addresses.
                savedIpAddressesCardView.setVisibility(View.GONE);
            } else {  // There are saved IP addresses.
                savedIpAddressesCardView.setVisibility(View.VISIBLE);
            }

            // Set the visibility of the current IP addresses.
            currentIpAddressesCardView.setVisibility(View.VISIBLE);

            // Set the status of the radio buttons and the card view backgrounds.
            if (savedIpAddressesCardView.getVisibility() == View.VISIBLE) {  // The saved IP addresses are displayed.
                // Check the saved IP addresses radio button.
                savedIpAddressesRadioButton.setChecked(true);

                // Uncheck the current IP addresses radio button.
                currentIpAddressesRadioButton.setChecked(false);

                // Darken the background of the current IP addresses linear layout according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                } else {
                    currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                }
            } else {  // The saved IP addresses are hidden.
                // Check the current IP addresses radio button.
                currentIpAddressesRadioButton.setChecked(true);

                // Uncheck the saved IP addresses radio button.
                savedIpAddressesRadioButton.setChecked(false);
            }
        } else {  // IP addresses are not pinned.
            // Hide the IP addresses card views.
            savedIpAddressesCardView.setVisibility(View.GONE);
            currentIpAddressesCardView.setVisibility(View.GONE);

            // Uncheck the radio buttons.
            savedIpAddressesRadioButton.setChecked(false);
            currentIpAddressesRadioButton.setChecked(false);
        }


        // Set the JavaScript switch listener.
        javaScriptSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {  // JavaScript is enabled.
                // Update the JavaScript icon.
                javaScriptImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.javascript_enabled, null));

                // Enable the DOM storage `Switch`.
                domStorageSwitch.setEnabled(true);

                // Update the DOM storage icon.
                if (domStorageSwitch.isChecked()) {  // DOM storage is enabled.
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_enabled, null));
                } else {  // DOM storage is disabled.
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_night, null));
                    } else {
                        domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_day, null));
                    }
                }
            } else {  // JavaScript is disabled.
                // Update the JavaScript icon.
                javaScriptImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.privacy_mode, null));

                // Disable the DOM storage `Switch`.
                domStorageSwitch.setEnabled(false);

                // Set the DOM storage icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_ghosted_night, null));
                } else {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_ghosted_day, null));
                }
            }
        });

        // Set the first-party cookies switch listener.
        firstPartyCookiesSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {  // First-party cookies are enabled.
                // Update the first-party cookies icon.
                firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_enabled, null));

                // Enable the third-party cookies switch.
                thirdPartyCookiesSwitch.setEnabled(true);

                // Update the third-party cookies icon.
                if (thirdPartyCookiesSwitch.isChecked()) {  // Third-party cookies are enabled.
                    // Set the third-party cookies icon to be red.
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_warning, null));
                } else {  // Third-party cookies are disabled.
                    // Set the third-party cookies icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_night, null));
                    } else {
                        thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_day, null));
                    }
                }
            } else {  // First-party cookies are disabled.
                // Update the first-party cookies icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_night, null));
                } else {
                    firstPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_day, null));
                }

                // Disable the third-party cookies switch.
                thirdPartyCookiesSwitch.setEnabled(false);

                // Set the third-party cookies icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_ghosted_night, null));
                } else {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_ghosted_day, null));
                }
            }
        });

        // Set the third-party cookies switch listener.
        thirdPartyCookiesSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {
                // Set the third-party cookies icon to be red.
                thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_warning, null));
            } else {
                // Update the third-party cookies icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_night, null));
                } else {
                    thirdPartyCookiesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.cookies_disabled_day, null));
                }
            }
        });

        // Set the DOM Storage switch listener.
        domStorageSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {
                domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_enabled, null));
            } else {
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_night, null));
                } else {
                    domStorageImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.dom_storage_disabled_day, null));
                }
            }
        });

        // Set the form data switch listener.  It can be removed once the minimum API >= 26.
        if (Build.VERSION.SDK_INT < 26) {
            formDataSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                // Update the icon.
                if (isChecked) {
                    formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_enabled, null));
                } else {
                    // Set the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_disabled_night, null));
                    } else {
                        formDataImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.form_data_disabled_day, null));
                    }
                }
            });
        }

        // Set the EasyList switch listener.
        easyListSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // EasyList is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_night, null));
                } else {
                    easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_day, null));
                }
            } else {  // EasyList is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_night, null));
                } else {
                    easyListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_day, null));
                }
            }
        });

        // Set the EasyPrivacy switch listener.
        easyPrivacySwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // EasyPrivacy is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_night, null));
                } else {
                    easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_day, null));
                }
            } else {  // EasyPrivacy is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_night, null));
                } else {
                    easyPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_day, null));
                }
            }
        });

        // Set the Fanboy's Annoyance List switch listener.
        fanboysAnnoyanceListSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon and Fanboy's Social Blocking List.
            if (isChecked) {  // Fanboy's Annoyance List is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_night, null));
                } else {
                    fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_day, null));
                }

                // Disable the Fanboy's Social Blocking List switch.
                fanboysSocialBlockingListSwitch.setEnabled(false);

                // Update the Fanboy's Social Blocking List icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_ghosted_night, null));
                } else {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_ghosted_day, null));
                }
            } else {  // Fanboy's Annoyance List is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_night, null));
                } else {
                    fanboysAnnoyanceListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_day, null));
                }

                // Enable the Fanboy's Social Blocking List switch.
                fanboysSocialBlockingListSwitch.setEnabled(true);

                // Update the Fanboy's Social Blocking List icon.
                if (fanboysSocialBlockingListSwitch.isChecked()) {  // Fanboy's Social Blocking List is on.
                    // Update the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_night, null));
                    } else {
                        fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_day, null));
                    }
                } else {  // Fanboy's Social Blocking List is off.
                    // Update the icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_night, null));
                    } else {
                        fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_day, null));
                    }
                }
            }

        });

        // Set the Fanboy's Social Blocking List switch listener.
        fanboysSocialBlockingListSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // Fanboy's Social Blocking List is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_night, null));
                } else {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_enabled_day, null));
                }
            } else {  // Fanboy's Social Blocking List is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_night, null));
                } else {
                    fanboysSocialBlockingListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.social_media_disabled_day, null));
                }
            }
        });

        // Set the UltraList switch listener.
        ultraListSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // UltraList is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_night, null));
                } else {
                    ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_enabled_day, null));
                }
            } else {  // UltraList is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_night, null));
                } else {
                    ultraListImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_ads_disabled_day, null));
                }
            }
        });

        // Set the UltraPrivacy switch listener.
        ultraPrivacySwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // UltraPrivacy is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_night, null));
                } else {
                    ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_enabled_day, null));
                }
            } else {  // UltraPrivacy is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_night, null));
                } else {
                    ultraPrivacyImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_tracking_disabled_day, null));
                }
            }
        });

        // Set the block all third-party requests switch listener.
        blockAllThirdPartyRequestsSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // Blocking all third-party requests is on.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_enabled_night, null));
                } else {
                    blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_enabled_day, null));
                }
            } else {  // Blocking all third-party requests is off.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_disabled_night, null));
                } else {
                    blockAllThirdPartyRequestsImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.block_all_third_party_requests_disabled_day, null));
                }
            }
        });

        // Set the user agent spinner listener.
        userAgentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set the new user agent.
                switch (position) {
                    case MainWebViewActivity.DOMAINS_SYSTEM_DEFAULT_USER_AGENT:
                        // Show the user agent TextView.
                        userAgentTextView.setVisibility(View.VISIBLE);

                        // Hide the custom user agent EditText.
                        customUserAgentEditText.setVisibility(View.GONE);

                        // Set the user text.
                        switch (defaultUserAgentArrayPosition) {
                            case MainWebViewActivity.UNRECOGNIZED_USER_AGENT:  // The default user agent name is not on the canonical list.
                                // This is probably because it was set in an older version of Clear Browser before the switch to persistent user agent names.
                                userAgentTextView.setText(defaultUserAgentName);
                                break;

                            case MainWebViewActivity.SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                                // Display the `WebView` default user agent.
                                userAgentTextView.setText(webViewDefaultUserAgentString);
                                break;

                            case MainWebViewActivity.SETTINGS_CUSTOM_USER_AGENT:
                                // Display the custom user agent.
                                userAgentTextView.setText(defaultCustomUserAgentString);
                                break;

                            default:
                                // Get the user agent string from the user agent data array.
                                userAgentTextView.setText(userAgentDataArray[defaultUserAgentArrayPosition]);
                        }
                        break;

                    case MainWebViewActivity.DOMAINS_WEBVIEW_DEFAULT_USER_AGENT:
                        // Show the user agent TextView and set the text.
                        userAgentTextView.setVisibility(View.VISIBLE);
                        userAgentTextView.setText(webViewDefaultUserAgentString);

                        // Hide the custom user agent EditTex.
                        customUserAgentEditText.setVisibility(View.GONE);
                        break;

                    case MainWebViewActivity.DOMAINS_CUSTOM_USER_AGENT:
                        // Hide the user agent TextView.
                        userAgentTextView.setVisibility(View.GONE);

                        // Show the custom user agent EditText and set the current user agent name as the text.
                        customUserAgentEditText.setVisibility(View.VISIBLE);
                        customUserAgentEditText.setText(currentUserAgentName);
                        break;

                    default:
                        // Show the user agent TextView and set the text from the user agent data array, which has one less entry than the spinner, so the position must be decremented.
                        userAgentTextView.setVisibility(View.VISIBLE);
                        userAgentTextView.setText(userAgentDataArray[position - 1]);

                        // Hide `customUserAgentEditText`.
                        customUserAgentEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the font size spinner listener.
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the font size display options.
                if (position == 0) {  // The system default font size has been selected.
                    // Show the default font size text view.
                    defaultFontSizeTextView.setVisibility(View.VISIBLE);

                    // Hide the custom font size edit text.
                    customFontSizeEditText.setVisibility(View.GONE);
                } else {  // A custom font size has been selected.
                    // Hide the default font size text view.
                    defaultFontSizeTextView.setVisibility(View.GONE);

                    // Show the custom font size edit text.
                    customFontSizeEditText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the swipe to refresh spinner listener.
        swipeToRefreshSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the icon and the visibility of `nightModeTextView`.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
                switch (position) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        if (defaultSwipeToRefresh) {  // Swipe to refresh enabled by default.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_night, null));
                            } else {
                                swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_day, null));
                            }
                        } else {  // Swipe to refresh disabled by default.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_night, null));
                            } else {
                                swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_day, null));
                            }
                        }

                        // Show the swipe to refresh TextView.
                        swipeToRefreshTextView.setVisibility(View.VISIBLE);
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_night, null));
                        } else {
                            swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_enabled_day, null));
                        }

                        // Hide the swipe to refresh TextView.
                        swipeToRefreshTextView.setVisibility(View.GONE);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_night, null));
                        } else {
                            swipeToRefreshImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.refresh_disabled_day, null));
                        }

                        // Hide the swipe to refresh TextView.
                        swipeToRefreshTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the WebView theme spinner listener.
        webViewThemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the icon and the visibility of the WebView theme text view.  Once the minimum API >= 21 a selector can be used as the tint mode instead of specifying different icons.
                switch (position) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:  // the domain WebView theme is system default.
                        // Set the icon according to the app WebView theme.
                        switch (appWebViewThemeEntryNumber) {
                            case DomainsDatabaseHelper.SYSTEM_DEFAULT:  // The default WebView theme is system default.
                                // Set the icon according to the app theme.
                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                    // Set the light mode icon.
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                                } else {
                                    // Set the dark theme icon.
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                                }
                                break;

                            case DomainsDatabaseHelper.LIGHT_THEME:  // The default WebView theme is light.
                                // Set the icon according to the app theme.
                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                                } else {
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_night, null));
                                }
                                break;

                            case DomainsDatabaseHelper.DARK_THEME:  // The default WebView theme is dark.
                                // Set the icon according to the app theme.
                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_day, null));
                                } else {
                                    webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                                }
                                break;
                        }

                        // Show the WebView theme text view.
                        webViewThemeTextView.setVisibility(View.VISIBLE);
                        break;

                    case DomainsDatabaseHelper.LIGHT_THEME:  // The domain WebView theme is light.
                        // Set the icon according to the app theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_day, null));
                        } else {
                            webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_light_theme_night, null));
                        }

                        // Hide the WebView theme text view.
                        webViewThemeTextView.setVisibility(View.GONE);
                        break;

                    case DomainsDatabaseHelper.DARK_THEME:  // The domain WebView theme is dark.
                        // Set the icon according to the app theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_day, null));
                        } else {
                            webViewThemeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.webview_dark_theme_night, null));
                        }

                        // Hide the WebView theme text view.
                        webViewThemeTextView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the wide viewport spinner listener.
        wideViewportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the icon and the visibility of the wide viewport text view.
                switch (position) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        if (defaultWideViewport) {  // Wide viewport is enabled by default.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_night, null));
                            } else {
                                wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_day, null));
                            }
                        } else {  // Wide viewport is disabled by default.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_night, null));
                            } else {
                                wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_day, null));
                            }
                        }

                        // Show the wide viewport text view.
                        wideViewportTextView.setVisibility(View.VISIBLE);
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_night, null));
                        } else {
                            wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_enabled_day, null));
                        }

                        // Hide the wide viewport text view.
                        wideViewportTextView.setVisibility(View.GONE);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_night, null));
                        } else {
                            wideViewportImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.wide_viewport_disabled_day, null));
                        }

                        // Hid ethe wide viewport text view.
                        wideViewportTextView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the display webpage images spinner listener.
        displayWebpageImagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the icon and the visibility of the display images text view.
                switch (position) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        if (defaultDisplayWebpageImages) {  // Display webpage images is enabled by default.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_night, null));
                            } else {
                                displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_day, null));
                            }
                        } else {  // Display webpage images is disabled by default.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_night, null));
                            } else {
                                displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_day, null));
                            }
                        }

                        // Show the display images text view.
                        displayImagesTextView.setVisibility(View.VISIBLE);
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_night, null));
                        } else {
                            displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_enabled_day, null));
                        }

                        // Hide the display images text view.
                        displayImagesTextView.setVisibility(View.GONE);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_night, null));
                        } else {
                            displayWebpageImagesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.images_disabled_day, null));
                        }

                        // Hide the display images text view.
                        displayImagesTextView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Set the pinned SSL certificate switch listener.
        pinnedSslCertificateSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // SSL certificate pinning is enabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_night, null));
                } else {
                    pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_day, null));
                }

                // Update the visibility of the saved SSL certificate.
                if (savedSslIssuedToCNameString == null) {
                    savedSslCardView.setVisibility(View.GONE);
                } else {
                    savedSslCardView.setVisibility(View.VISIBLE);
                }

                // Update the visibility of the current website SSL certificate.
                if (DomainsActivity.sslIssuedToCName == null) {
                    // Hide the SSL certificate.
                    currentSslCardView.setVisibility(View.GONE);

                    // Show the instruction.
                    noCurrentWebsiteCertificateTextView.setVisibility(View.VISIBLE);
                } else {
                    // Show the SSL certificate.
                    currentSslCardView.setVisibility(View.VISIBLE);

                    // Hide the instruction.
                    noCurrentWebsiteCertificateTextView.setVisibility(View.GONE);
                }

                // Set the status of the radio buttons.
                if (savedSslCardView.getVisibility() == View.VISIBLE) {  // The saved SSL certificate is displayed.
                    // Check the saved SSL certificate radio button.
                    savedSslCertificateRadioButton.setChecked(true);

                    // Uncheck the current website SSL certificate radio button.
                    currentWebsiteCertificateRadioButton.setChecked(false);

                    // Set the background of the saved SSL certificate linear layout to be transparent.
                    savedSslCertificateLinearLayout.setBackgroundResource(R.color.transparent);

                    // Darken the background of the current website SSL certificate linear layout according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                    } else {
                        currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                    }

                    // Scroll to the current website SSL certificate card.
                    savedSslCardView.getParent().requestChildFocus(savedSslCardView, savedSslCardView);
                } else if (currentSslCardView.getVisibility() == View.VISIBLE) {  // The saved SSL certificate is hidden but the current website SSL certificate is visible.
                    // Check the current website SSL certificate radio button.
                    currentWebsiteCertificateRadioButton.setChecked(true);

                    // Uncheck the saved SSL certificate radio button.
                    savedSslCertificateRadioButton.setChecked(false);

                    // Set the background of the current website SSL certificate linear layout to be transparent.
                    currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.transparent);

                    // Darken the background of the saved SSL certificate linear layout according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                    } else {
                        savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                    }

                    // Scroll to the current website SSL certificate card.
                    currentSslCardView.getParent().requestChildFocus(currentSslCardView, currentSslCardView);
                } else {  // Neither SSL certificate is visible.
                    // Uncheck both radio buttons.
                    savedSslCertificateRadioButton.setChecked(false);
                    currentWebsiteCertificateRadioButton.setChecked(false);

                    // Scroll to the current website SSL certificate card.
                    noCurrentWebsiteCertificateTextView.getParent().requestChildFocus(noCurrentWebsiteCertificateTextView, noCurrentWebsiteCertificateTextView);
                }
            } else {  // SSL certificate pinning is disabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_night, null));
                } else {
                    pinnedSslCertificateImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_day, null));
                }

                // Hide the SSl certificates and instructions.
                savedSslCardView.setVisibility(View.GONE);
                currentSslCardView.setVisibility(View.GONE);
                noCurrentWebsiteCertificateTextView.setVisibility(View.GONE);

                // Uncheck the radio buttons.
                savedSslCertificateRadioButton.setChecked(false);
                currentWebsiteCertificateRadioButton.setChecked(false);
            }
        });

        savedSslCardView.setOnClickListener((View view) -> {
            // Check the saved SSL certificate radio button.
            savedSslCertificateRadioButton.setChecked(true);

            // Uncheck the current website SSL certificate radio button.
            currentWebsiteCertificateRadioButton.setChecked(false);

            // Set the background of the saved SSL certificate linear layout to be transparent.
            savedSslCertificateLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the current website SSL certificate linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        savedSslCertificateRadioButton.setOnClickListener((View view) -> {
            // Check the saved SSL certificate radio button.
            savedSslCertificateRadioButton.setChecked(true);

            // Uncheck the current website SSL certificate radio button.
            currentWebsiteCertificateRadioButton.setChecked(false);

            // Set the background of the saved SSL certificate linear layout to be transparent.
            savedSslCertificateLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the current website SSL certificate linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        currentSslCardView.setOnClickListener((View view) -> {
            // Check the current website SSL certificate radio button.
            currentWebsiteCertificateRadioButton.setChecked(true);

            // Uncheck the saved SSL certificate radio button.
            savedSslCertificateRadioButton.setChecked(false);

            // Set the background of the current website SSL certificate linear layout to be transparent.
            currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the saved SSL certificate linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        currentWebsiteCertificateRadioButton.setOnClickListener((View view) -> {
            // Check the current website SSL certificate radio button.
            currentWebsiteCertificateRadioButton.setChecked(true);

            // Uncheck the saved SSL certificate radio button.
            savedSslCertificateRadioButton.setChecked(false);

            // Set the background of the current website SSL certificate linear layout to be transparent.
            currentWebsiteCertificateLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the saved SSL certificate linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                savedSslCertificateLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        // Set the pinned IP addresses switch listener.
        pinnedIpAddressesSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            // Update the icon.
            if (isChecked) {  // IP addresses pinning is enabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_night, null));
                } else {
                    pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_enabled_day, null));
                }

                // Update the visibility of the saved IP addresses card view.
                if (savedIpAddresses == null) {  // There are no saved IP addresses.
                    savedIpAddressesCardView.setVisibility(View.GONE);
                } else {  // There are saved IP addresses.
                    savedIpAddressesCardView.setVisibility(View.VISIBLE);
                }

                // Show the current IP addresses card view.
                currentIpAddressesCardView.setVisibility(View.VISIBLE);

                // Set the status of the radio buttons.
                if (savedIpAddressesCardView.getVisibility() == View.VISIBLE) {  // The saved IP addresses are visible.
                    // Check the saved IP addresses radio button.
                    savedIpAddressesRadioButton.setChecked(true);

                    // Uncheck the current IP addresses radio button.
                    currentIpAddressesRadioButton.setChecked(false);

                    // Set the background of the saved IP addresses linear layout to be transparent.
                    savedSslCertificateLinearLayout.setBackgroundResource(R.color.transparent);

                    // Darken the background of the current IP addresses linear layout according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                    } else {
                        currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                    }
                } else {  // The saved IP addresses are not visible.
                    // Check the current IP addresses radio button.
                    currentIpAddressesRadioButton.setChecked(true);

                    // Uncheck the saved IP addresses radio button.
                    savedIpAddressesRadioButton.setChecked(false);

                    // Set the background of the current IP addresses linear layout to be transparent.
                    currentIpAddressesLinearLayout.setBackgroundResource(R.color.transparent);

                    // Darken the background of the saved IP addresses linear layout according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
                    } else {
                        savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
                    }
                }

                // Scroll to the bottom of the card views.
                currentIpAddressesCardView.getParent().requestChildFocus(currentIpAddressesCardView, currentIpAddressesCardView);
            } else {  // IP addresses pinning is disabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_night, null));
                } else {
                    pinnedIpAddressesImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ssl_certificate_disabled_day, null));
                }

                // Hide the IP addresses card views.
                savedIpAddressesCardView.setVisibility(View.GONE);
                currentIpAddressesCardView.setVisibility(View.GONE);

                // Uncheck the radio buttons.
                savedIpAddressesRadioButton.setChecked(false);
                currentIpAddressesRadioButton.setChecked(false);
            }
        });

        savedIpAddressesCardView.setOnClickListener((View view) -> {
            // Check the saved IP addresses radio button.
            savedIpAddressesRadioButton.setChecked(true);

            // Uncheck the current website IP addresses radio button.
            currentIpAddressesRadioButton.setChecked(false);

            // Set the background of the saved IP addresses linear layout to be transparent.
            savedIpAddressesLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the current IP addresses linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        savedIpAddressesRadioButton.setOnClickListener((View view) -> {
            // Check the saved IP addresses radio button.
            savedIpAddressesRadioButton.setChecked(true);

            // Uncheck the current website IP addresses radio button.
            currentIpAddressesRadioButton.setChecked(false);

            // Set the background of the saved IP addresses linear layout to be transparent.
            savedIpAddressesLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the current IP addresses linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                currentIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        currentIpAddressesCardView.setOnClickListener((View view) -> {
            // Check the current IP addresses radio button.
            currentIpAddressesRadioButton.setChecked(true);

            // Uncheck the saved IP addresses radio button.
            savedIpAddressesRadioButton.setChecked(false);

            // Set the background of the current IP addresses linear layout to be transparent.
            currentIpAddressesLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the saved IP addresses linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        currentIpAddressesRadioButton.setOnClickListener((View view) -> {
            // Check the current IP addresses radio button.
            currentIpAddressesRadioButton.setChecked(true);

            // Uncheck the saved IP addresses radio button.
            savedIpAddressesRadioButton.setChecked(false);

            // Set the background of the current IP addresses linear layout to be transparent.
            currentIpAddressesLinearLayout.setBackgroundResource(R.color.transparent);

            // Darken the background of the saved IP addresses linear layout according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_33);
            } else {
                savedIpAddressesLinearLayout.setBackgroundResource(R.color.black_translucent_11);
            }
        });

        // Set the scroll Y.
        domainSettingsScrollView.post(() -> domainSettingsScrollView.setScrollY(scrollY));

        // Return the domain settings view.
        return domainSettingsView;
    }

    private boolean checkDomainNameAgainstCertificate(String domainName, String certificateCommonName) {
        // Initialize `domainNamesMatch`.
        boolean domainNamesMatch = false;

        // Check various wildcard permutations if `domainName` and `certificateCommonName` are not empty.
        // `noinspection ConstantCondition` removes Android Studio's incorrect lint warning that `domainName` can never be `null`.
        if ((domainName != null) && (certificateCommonName != null)) {
            // Check if the domains match.
            if (domainName.equals(certificateCommonName)) {
                domainNamesMatch = true;
            }

            // If `domainName` starts with a wildcard, check the base domain against all the subdomains of `certificateCommonName`.
            if (!domainNamesMatch && domainName.startsWith("*.") && (domainName.length() > 2)) {
                // Remove the initial `*.`.
                String baseDomainName = domainName.substring(2);

                // Setup a copy of `certificateCommonName` to test subdomains.
                String certificateCommonNameSubdomain = certificateCommonName;

                // Check all the subdomains in `certificateCommonNameSubdomain` against `baseDomainName`.
                while (!domainNamesMatch && certificateCommonNameSubdomain.contains(".")) {  // Stop checking if we know that `domainNamesMatch` is `true` or if we run out of  `.`.
                    // Test the `certificateCommonNameSubdomain` against `baseDomainName`.
                    if (certificateCommonNameSubdomain.equals(baseDomainName)) {
                        domainNamesMatch = true;
                    }

                    // Strip out the lowest subdomain of `certificateCommonNameSubdomain`.
                    try {
                        certificateCommonNameSubdomain = certificateCommonNameSubdomain.substring(certificateCommonNameSubdomain.indexOf(".") + 1);
                    } catch (IndexOutOfBoundsException e) {  // `certificateCommonNameSubdomain` ends with `.`.
                        certificateCommonNameSubdomain = "";
                    }
                }
            }

            // If `certificateCommonName` starts with a wildcard, check the base common name against all the subdomains of `domainName`.
            if (!domainNamesMatch && certificateCommonName.startsWith("*.") && (certificateCommonName.length() > 2)) {
                // Remove the initial `*.`.
                String baseCertificateCommonName = certificateCommonName.substring(2);

                // Setup a copy of `domainName` to test subdomains.
                String domainNameSubdomain = domainName;

                // Check all the subdomains in `domainNameSubdomain` against `baseCertificateCommonName`.
                while (!domainNamesMatch && domainNameSubdomain.contains(".") && (domainNameSubdomain.length() > 2)) {
                    // Test the `domainNameSubdomain` against `baseCertificateCommonName`.
                    if (domainNameSubdomain.equals(baseCertificateCommonName)) {
                        domainNamesMatch = true;
                    }

                    // Strip out the lowest subdomain of `domainNameSubdomain`.
                    try {
                        domainNameSubdomain = domainNameSubdomain.substring(domainNameSubdomain.indexOf(".") + 1);
                    } catch (IndexOutOfBoundsException e) { // `domainNameSubdomain` ends with `.`.
                        domainNameSubdomain = "";
                    }
                }
            }

            // If both names start with a wildcard, check if the root of one contains the root of the other.
            if (!domainNamesMatch && domainName.startsWith("*.") && (domainName.length() > 2) && certificateCommonName.startsWith("*.") && (certificateCommonName.length() > 2)) {
                // Remove the wildcards.
                String rootDomainName = domainName.substring(2);
                String rootCertificateCommonName = certificateCommonName.substring(2);

                // Check if one name ends with the contents of the other.  If so, there will be overlap in the their wildcard subdomains.
                if (rootDomainName.endsWith(rootCertificateCommonName) || rootCertificateCommonName.endsWith(rootDomainName)) {
                    domainNamesMatch = true;
                }
            }
        }

        return domainNamesMatch;
    }
}