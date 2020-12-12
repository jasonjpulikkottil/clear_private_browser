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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import com.jdots.browser.R;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.helpers.DownloadLocationHelper;
import com.jdots.browser.helpers.ProxyHelper;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    // Define the class variables.
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    private SharedPreferences savedPreferences;
    private int currentThemeStatus;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from the XML file.
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Get a handle for the activity.
        Activity activity = getActivity();

        // Remove the lint warning below that `getApplicationContext()` might produce a null pointer exception.
        assert activity != null;

        // Get a handle for the context and the resources.
        Context context = activity.getApplicationContext();
        Resources resources = getResources();

        // Get the current theme status.
        currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Initialize savedPreferences.
        savedPreferences = getPreferenceScreen().getSharedPreferences();

        // Get handles for the preferences.
        Preference javaScriptPreference = findPreference("javascript");
        Preference firstPartyCookiesPreference = findPreference("first_party_cookies");
        Preference thirdPartyCookiesPreference = findPreference("third_party_cookies");
        Preference domStoragePreference = findPreference("dom_storage");
        Preference formDataPreference = findPreference("save_form_data");  // The form data preference can be removed once the minimum API >= 26.
        Preference userAgentPreference = findPreference("user_agent");
        Preference customUserAgentPreference = findPreference("custom_user_agent");
        Preference incognitoModePreference = findPreference("incognito_mode");
        Preference doNotTrackPreference = findPreference("do_not_track");
        Preference allowScreenshotsPreference = findPreference(getString(R.string.allow_screenshots_key));
        Preference easyListPreference = findPreference("easylist");
        Preference easyPrivacyPreference = findPreference("easyprivacy");
        Preference fanboyAnnoyanceListPreference = findPreference("fanboys_annoyance_list");
        Preference fanboySocialBlockingListPreference = findPreference("fanboys_social_blocking_list");
        Preference ultraListPreference = findPreference("ultralist");
        Preference ultraPrivacyPreference = findPreference("ultraprivacy");
        Preference blockAllThirdPartyRequestsPreference = findPreference("block_all_third_party_requests");
        Preference googleAnalyticsPreference = findPreference("google_analytics");
        Preference facebookClickIdsPreference = findPreference("facebook_click_ids");
        Preference twitterAmpRedirectsPreference = findPreference("twitter_amp_redirects");
        Preference searchPreference = findPreference("search");
        Preference searchCustomURLPreference = findPreference("search_custom_url");
        Preference proxyPreference = findPreference("proxy");
        Preference proxyCustomUrlPreference = findPreference("proxy_custom_url");
        Preference fullScreenBrowsingModePreference = findPreference("full_screen_browsing_mode");
        Preference hideAppBarPreference = findPreference("hide_app_bar");
        Preference clearEverythingPreference = findPreference("clear_everything");
        Preference clearCookiesPreference = findPreference("clear_cookies");
        Preference clearDomStoragePreference = findPreference("clear_dom_storage");
        Preference clearFormDataPreference = findPreference("clear_form_data");  // The clear form data preference can be removed once the minimum API >= 26.
        Preference clearLogcatPreference = findPreference(getString(R.string.clear_logcat_key));
        Preference clearCachePreference = findPreference("clear_cache");
        Preference homepagePreference = findPreference("homepage");
        Preference downloadLocationPreference = findPreference("download_location");
        Preference downloadCustomLocationPreference = findPreference("download_custom_location");
        Preference fontSizePreference = findPreference("font_size");
        Preference openIntentsInNewTabPreference = findPreference("open_intents_in_new_tab");
        Preference swipeToRefreshPreference = findPreference("swipe_to_refresh");
        Preference scrollAppBarPreference = findPreference("scroll_app_bar");
        Preference displayAdditionalAppBarIconsPreference = findPreference("display_additional_app_bar_icons");
        Preference appThemePreference = findPreference("app_theme");
        Preference webViewThemePreference = findPreference("webview_theme");
        Preference wideViewportPreference = findPreference("wide_viewport");
        Preference displayWebpageImagesPreference = findPreference("display_webpage_images");

        // Remove the lint warnings below that the preferences might be null.
        assert javaScriptPreference != null;
        assert firstPartyCookiesPreference != null;
        assert thirdPartyCookiesPreference != null;
        assert domStoragePreference != null;
        assert formDataPreference != null;
        assert userAgentPreference != null;
        assert customUserAgentPreference != null;
        assert incognitoModePreference != null;
        assert doNotTrackPreference != null;
        assert allowScreenshotsPreference != null;
        assert easyListPreference != null;
        assert easyPrivacyPreference != null;
        assert fanboyAnnoyanceListPreference != null;
        assert fanboySocialBlockingListPreference != null;
        assert ultraListPreference != null;
        assert ultraPrivacyPreference != null;
        assert blockAllThirdPartyRequestsPreference != null;
        assert googleAnalyticsPreference != null;
        assert facebookClickIdsPreference != null;
        assert twitterAmpRedirectsPreference != null;
        assert searchPreference != null;
        assert searchCustomURLPreference != null;
        assert proxyPreference != null;
        assert proxyCustomUrlPreference != null;
        assert fullScreenBrowsingModePreference != null;
        assert hideAppBarPreference != null;
        assert clearEverythingPreference != null;
        assert clearCookiesPreference != null;
        assert clearDomStoragePreference != null;
        assert clearFormDataPreference != null;
        assert clearLogcatPreference != null;
        assert clearCachePreference != null;
        assert homepagePreference != null;
        assert downloadLocationPreference != null;
        assert downloadCustomLocationPreference != null;
        assert fontSizePreference != null;
        assert openIntentsInNewTabPreference != null;
        assert swipeToRefreshPreference != null;
        assert scrollAppBarPreference != null;
        assert displayAdditionalAppBarIconsPreference != null;
        assert appThemePreference != null;
        assert webViewThemePreference != null;
        assert wideViewportPreference != null;
        assert displayWebpageImagesPreference != null;

        // Set the preference dependencies.
        hideAppBarPreference.setDependency("full_screen_browsing_mode");
        domStoragePreference.setDependency("javascript");

        // Get strings from the preferences.
        String userAgentName = savedPreferences.getString("user_agent", getString(R.string.user_agent_default_value));
        String searchString = savedPreferences.getString("search", getString(R.string.search_default_value));
        String proxyString = savedPreferences.getString("proxy", getString(R.string.proxy_default_value));
        String downloadLocationString = savedPreferences.getString("download_location", getString(R.string.download_location_default_value));

        // Get booleans that are used in multiple places from the preferences.
        boolean javaScriptEnabled = savedPreferences.getBoolean("javascript", false);
        boolean firstPartyCookiesEnabled = savedPreferences.getBoolean("first_party_cookies", false);
        boolean fanboyAnnoyanceListEnabled = savedPreferences.getBoolean("fanboys_annoyance_list", true);
        boolean fanboySocialBlockingEnabled = savedPreferences.getBoolean("fanboys_social_blocking_list", true);
        boolean fullScreenBrowsingMode = savedPreferences.getBoolean("full_screen_browsing_mode", false);
        boolean clearEverything = savedPreferences.getBoolean("clear_everything", true);

        // Only enable the third-party cookies preference if first-party cookies are enabled and API >= 21.
        thirdPartyCookiesPreference.setEnabled(firstPartyCookiesEnabled && (Build.VERSION.SDK_INT >= 21));

        // Remove the form data preferences if the API is >= 26 as they no longer do anything.
        if (Build.VERSION.SDK_INT >= 26) {
            // Get handles for the categories.
            PreferenceCategory privacyCategory = findPreference("privacy");
            PreferenceCategory clearAndExitCategory = findPreference("clear_and_exit");

            // Remove the incorrect lint warnings below that the preference categories might be null.
            assert privacyCategory != null;
            assert clearAndExitCategory != null;

            // Remove the form data preferences.
            privacyCategory.removePreference(formDataPreference);
            clearAndExitCategory.removePreference(clearFormDataPreference);
        }

        // Only enable Fanboy's social blocking list preference if Fanboy's annoyance list is disabled.
        fanboySocialBlockingListPreference.setEnabled(!fanboyAnnoyanceListEnabled);


        // Inflate a WebView to get the default user agent.
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // `@SuppressLint("InflateParams")` removes the warning about using `null` as the `ViewGroup`, which in this case makes sense because the `bare_webview` will not be displayed.
        @SuppressLint("InflateParams") View bareWebViewLayout = inflater.inflate(R.layout.bare_webview, null, false);

        // Get a handle for a bare WebView.
        WebView bareWebView = bareWebViewLayout.findViewById(R.id.bare_webview);

        // Get the user agent arrays.
        ArrayAdapter<CharSequence> userAgentNamesArray = ArrayAdapter.createFromResource(context, R.array.user_agent_names, R.layout.spinner_item);
        String[] translatedUserAgentNamesArray = resources.getStringArray(R.array.translated_user_agent_names);
        String[] userAgentDataArray = resources.getStringArray(R.array.user_agent_data);

        // Get the array position of the user agent name.
        int userAgentArrayPosition = userAgentNamesArray.getPosition(userAgentName);

        // Populate the user agent summary.
        switch (userAgentArrayPosition) {
            case MainWebViewActivity.UNRECOGNIZED_USER_AGENT:  // The user agent name is not on the canonical list.
                // This is probably because it was set in an older version of Clear Browser before the switch to persistent user agent names.  Use the current user agent entry name as the summary.
                userAgentPreference.setSummary(userAgentName);
                break;

            case MainWebViewActivity.SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                // Get the user agent text from the webview (which changes based on the version of Android and WebView installed).
                userAgentPreference.setSummary(translatedUserAgentNamesArray[userAgentArrayPosition] + ":\n" + bareWebView.getSettings().getUserAgentString());
                break;

            case MainWebViewActivity.SETTINGS_CUSTOM_USER_AGENT:
                // Set the summary text.
                userAgentPreference.setSummary(R.string.custom_user_agent);
                break;

            default:
                // Get the user agent summary from the user agent data array.
                userAgentPreference.setSummary(translatedUserAgentNamesArray[userAgentArrayPosition] + ":\n" + userAgentDataArray[userAgentArrayPosition]);
        }

        // Set the summary text for the custom user agent preference.
        customUserAgentPreference.setSummary(savedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value)));

        // Only enable the custom user agent preference if the user agent is set to `Custom`.
        customUserAgentPreference.setEnabled(userAgentPreference.getSummary().equals(getString(R.string.custom_user_agent)));


        // Set the search URL as the summary text for the search preference when the preference screen is loaded.
        if (searchString.equals("Custom URL")) {
            // Use R.string.custom_url, which will be translated, instead of the array value, which will not.
            searchPreference.setSummary(R.string.custom_url);
        } else {
            // Set the array value as the summary text.
            searchPreference.setSummary(searchString);
        }

        // Set the summary text for the search custom URL (the default is `""`).
        searchCustomURLPreference.setSummary(savedPreferences.getString("search_custom_url", getString(R.string.search_custom_url_default_value)));

        // Only enable the search custom URL preference if the search is set to `Custom URL`.
        searchCustomURLPreference.setEnabled(searchString.equals("Custom URL"));


        // Set the summary text for the proxy preference when the preference screen is loaded.
        switch (proxyString) {
            case ProxyHelper.NONE:
                proxyPreference.setSummary(getString(R.string.no_proxy_enabled));
                break;

            case ProxyHelper.TOR:
                if (Build.VERSION.SDK_INT == 19) {  // Proxying through SOCKS doesn't work on Android KitKat.
                    proxyPreference.setSummary(getString(R.string.tor_enabled_kitkat));
                } else {
                    proxyPreference.setSummary(getString(R.string.tor_enabled));
                }
                break;

            case ProxyHelper.I2P:
                proxyPreference.setSummary(getString(R.string.i2p_enabled));
                break;

            case ProxyHelper.CUSTOM:
                proxyPreference.setSummary(getString(R.string.custom_proxy));
                break;
        }

        // Set the summary text for the custom proxy URL.
        proxyCustomUrlPreference.setSummary(savedPreferences.getString("proxy_custom_url", getString(R.string.proxy_custom_url_default_value)));

        // Only enable the custom proxy URL if a custom proxy is selected.
        proxyCustomUrlPreference.setEnabled(proxyString.equals("Custom"));


        // Set the status of the clear and exit preferences.
        clearCookiesPreference.setEnabled(!clearEverything);
        clearDomStoragePreference.setEnabled(!clearEverything);
        clearFormDataPreference.setEnabled(!clearEverything);  // The form data line can be removed once the minimum API is >= 26.
        clearLogcatPreference.setEnabled(!clearEverything);
        clearCachePreference.setEnabled(!clearEverything);


        // Set the homepage URL as the summary text for the homepage preference.
        homepagePreference.setSummary(savedPreferences.getString("homepage", getString(R.string.homepage_default_value)));


        // Get the download location string arrays.
        String[] downloadLocationEntriesStringArray = resources.getStringArray(R.array.download_location_entries);
        String[] downloadLocationEntryValuesStringArray = resources.getStringArray(R.array.download_location_entry_values);

        // Instantiate the download location helper.
        DownloadLocationHelper downloadLocationHelper = new DownloadLocationHelper();

        // Check to see if a download custom location is selected.
        if (downloadLocationString.equals(downloadLocationEntryValuesStringArray[3])) {  // A download custom location is selected.
            // Set the download location summary text to be `Custom`.
            downloadLocationPreference.setSummary(downloadLocationEntriesStringArray[3]);
        } else {  // A custom download location is not selected.
            // Set the download location summary text to be the download location.
            downloadLocationPreference.setSummary(downloadLocationHelper.getDownloadLocation(context));

            // Disable the download custom location preference.
            downloadCustomLocationPreference.setEnabled(false);
        }

        // Set the summary text for the download custom location (the default is `"`).
        downloadCustomLocationPreference.setSummary(savedPreferences.getString("download_custom_location", getString(R.string.download_custom_location_default_value)));


        // Set the font size as the summary text for the preference.
        fontSizePreference.setSummary(savedPreferences.getString("font_size", getString(R.string.font_size_default_value)) + "%");


        // Get the app theme string arrays.
        String[] appThemeEntriesStringArray = resources.getStringArray(R.array.app_theme_entries);
        String[] appThemeEntryValuesStringArray = resources.getStringArray(R.array.app_theme_entry_values);

        // Get the current app theme.
        String currentAppTheme = savedPreferences.getString("app_theme", getString(R.string.app_theme_default_value));

        // Define an app theme entry number.
        int appThemeEntryNumber;

        // Get the app theme entry number that matches the current app theme.  A switch statement cannot be used because the theme entry values string array is not a compile time constant.
        if (currentAppTheme.equals(appThemeEntryValuesStringArray[1])) {  // The light theme is selected.
            // Store the app theme entry number.
            appThemeEntryNumber = 1;
        } else if (currentAppTheme.equals(appThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
            // Store the app theme entry number.
            appThemeEntryNumber = 2;
        } else {  // The system default theme is selected.
            // Store the app theme entry number.
            appThemeEntryNumber = 0;
        }

        // Set the current theme as the summary text for the preference.
        appThemePreference.setSummary(appThemeEntriesStringArray[appThemeEntryNumber]);


        // Get the WebView theme string arrays.
        String[] webViewThemeEntriesStringArray = resources.getStringArray(R.array.webview_theme_entries);
        String[] webViewThemeEntryValuesStringArray = resources.getStringArray(R.array.webview_theme_entry_values);

        // Get the current WebView theme.
        String currentWebViewTheme = savedPreferences.getString("webview_theme", getString(R.string.webview_theme_default_value));

        // Define a WebView theme entry number.
        int webViewThemeEntryNumber;

        // Get the WebView theme entry number that matches the current WebView theme.  A switch statement cannot be used because the WebView theme entry values string array is not a compile time constant.
        if (currentWebViewTheme.equals(webViewThemeEntryValuesStringArray[1])) {  // The light theme is selected.
            // Store the WebView theme entry number.
            webViewThemeEntryNumber = 1;
        } else if (currentWebViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
            // Store the WebView theme entry number.
            webViewThemeEntryNumber = 2;
        } else {  // The system default theme is selected.
            // Store the WebView theme entry number.
            webViewThemeEntryNumber = 0;
        }

        // Set the visibility of the WebView theme preference.
        if (Build.VERSION.SDK_INT < 21) {  // The device is running API 19.
            // Get a handle for the general category.
            PreferenceCategory generalCategory = findPreference("general");

            // Remove the incorrect lint warning below that the general preference category might be null.
            assert generalCategory != null;

            // Remove the WebView theme preference.
            generalCategory.removePreference(webViewThemePreference);
        } else {  // The device is running API >= 21
            // Set the current theme as the summary text for the preference.
            webViewThemePreference.setSummary(webViewThemeEntriesStringArray[webViewThemeEntryNumber]);
        }


        // Set the JavaScript icon.
        if (javaScriptEnabled) {
            javaScriptPreference.setIcon(R.drawable.javascript_enabled);
        } else {
            javaScriptPreference.setIcon(R.drawable.privacy_mode);
        }

        // Set the first-party cookies icon.
        if (firstPartyCookiesEnabled) {
            firstPartyCookiesPreference.setIcon(R.drawable.cookies_enabled);
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                firstPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_night);
            } else {
                firstPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_day);
            }
        }

        // Set the third party cookies icon.
        if (firstPartyCookiesEnabled && Build.VERSION.SDK_INT >= 21) {
            if (savedPreferences.getBoolean("third_party_cookies", false)) {
                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_warning);
            } else {
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_night);
                } else {
                    thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_day);
                }
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_night);
            } else {
                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_day);
            }
        }

        // Set the DOM storage icon.
        if (javaScriptEnabled) {  // The preference is enabled.
            if (savedPreferences.getBoolean("dom_storage", false)) {  // DOM storage is enabled.
                domStoragePreference.setIcon(R.drawable.dom_storage_enabled);
            } else {  // DOM storage is disabled.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    domStoragePreference.setIcon(R.drawable.dom_storage_disabled_night);
                } else {
                    domStoragePreference.setIcon(R.drawable.dom_storage_disabled_day);
                }
            }
        } else {  // The preference is disabled.  The icon should be ghosted.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                domStoragePreference.setIcon(R.drawable.dom_storage_ghosted_night);
            } else {
                domStoragePreference.setIcon(R.drawable.dom_storage_ghosted_day);
            }
        }

        // Set the save form data icon if API < 26.  Save form data has no effect on API >= 26.
        if (Build.VERSION.SDK_INT < 26) {
            if (savedPreferences.getBoolean("save_form_data", false)) {
                formDataPreference.setIcon(R.drawable.form_data_enabled);
            } else {
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    formDataPreference.setIcon(R.drawable.form_data_disabled_night);
                } else {
                    formDataPreference.setIcon(R.drawable.form_data_disabled_day);
                }
            }
        }

        // Set the custom user agent icon.
        if (customUserAgentPreference.isEnabled()) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_enabled_night);
            } else {
                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_night);
            } else {
                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_day);
            }
        }

        // Set the incognito mode icon.
        if (savedPreferences.getBoolean("incognito_mode", false)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                incognitoModePreference.setIcon(R.drawable.incognito_mode_enabled_night);
            } else {
                incognitoModePreference.setIcon(R.drawable.incognito_mode_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                incognitoModePreference.setIcon(R.drawable.incognito_mode_disabled_night);
            } else {
                incognitoModePreference.setIcon(R.drawable.incognito_mode_disabled_day);
            }
        }

        // Set the Do Not Track icon.
        if (savedPreferences.getBoolean("do_not_track", false)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                doNotTrackPreference.setIcon(R.drawable.block_tracking_enabled_night);
            } else {
                doNotTrackPreference.setIcon(R.drawable.block_tracking_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                doNotTrackPreference.setIcon(R.drawable.block_tracking_disabled_night);
            } else {
                doNotTrackPreference.setIcon(R.drawable.block_tracking_disabled_day);
            }
        }

        // Set the allow screenshots icon.
        if (savedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_enabled_day);
            } else {
                allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_enabled_night);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_disabled_day);
            } else {
                allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_disabled_night);
            }
        }

        // Set the EasyList icon.
        if (savedPreferences.getBoolean("easylist", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyListPreference.setIcon(R.drawable.block_ads_enabled_night);
            } else {
                easyListPreference.setIcon(R.drawable.block_ads_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyListPreference.setIcon(R.drawable.block_ads_disabled_night);
            } else {
                easyListPreference.setIcon(R.drawable.block_ads_disabled_day);
            }
        }

        // Set the EasyPrivacy icon.
        if (savedPreferences.getBoolean("easyprivacy", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_night);
            } else {
                easyPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                easyPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_night);
            } else {
                easyPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_day);
            }
        }

        // Set the Fanboy lists icons.
        if (fanboyAnnoyanceListEnabled) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                // Set the Fanboy annoyance list icon.
                fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_enabled_night);

                // Set the Fanboy social blocking list icon.
                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_ghosted_night);
            } else {
                // Set the Fanboy annoyance list icon.
                fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_enabled_day);

                // Set the Fanboy social blocking list icon.
                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_ghosted_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                // Set the Fanboy annoyance list icon.
                fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_disabled_night);

                // Set the Fanboy social blocking list icon.
                if (fanboySocialBlockingEnabled) {
                    fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_night);
                } else {
                    fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_night);
                }
            } else {
                // Set the Fanboy annoyance list icon.
                fanboyAnnoyanceListPreference.setIcon(R.drawable.block_ads_disabled_day);

                // Set the Fanboy social blocking list icon.
                if (fanboySocialBlockingEnabled) {
                    fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_day);
                } else {
                    fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_day);
                }
            }
        }

        // Set the UltraList icon.
        if (savedPreferences.getBoolean("ultralist", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraListPreference.setIcon(R.drawable.block_ads_enabled_night);
            } else {
                ultraListPreference.setIcon(R.drawable.block_ads_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraListPreference.setIcon(R.drawable.block_ads_disabled_night);
            } else {
                ultraListPreference.setIcon(R.drawable.block_ads_disabled_day);
            }
        }

        // Set the UltraPrivacy icon.
        if (savedPreferences.getBoolean("ultraprivacy", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_night);
            } else {
                ultraPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                ultraPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_night);
            } else {
                ultraPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_day);
            }
        }

        // Set the block all third-party requests icon.
        if (savedPreferences.getBoolean("block_all_third_party_requests", false)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_enabled_night);
            } else {
                blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_disabled_night);
            } else {
                blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_disabled_day);
            }
        }

        // Set the Google Analytics icon according to the theme.
        if (savedPreferences.getBoolean("google_analytics", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                googleAnalyticsPreference.setIcon(R.drawable.modify_url_enabled_night);
            } else {
                googleAnalyticsPreference.setIcon(R.drawable.modify_url_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                googleAnalyticsPreference.setIcon(R.drawable.modify_url_disabled_night);
            } else {
                googleAnalyticsPreference.setIcon(R.drawable.modify_url_disabled_day);
            }
        }

        // Set the Facebook Click IDs icon according to the theme.
        if (savedPreferences.getBoolean("facebook_click_ids", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                facebookClickIdsPreference.setIcon(R.drawable.modify_url_enabled_night);
            } else {
                facebookClickIdsPreference.setIcon(R.drawable.modify_url_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                facebookClickIdsPreference.setIcon(R.drawable.modify_url_disabled_night);
            } else {
                facebookClickIdsPreference.setIcon(R.drawable.modify_url_disabled_day);
            }
        }

        // Set the Twitter AMP redirects icon according to the theme.
        if (savedPreferences.getBoolean("twitter_amp_redirects", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_enabled_night);
            } else {
                twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_disabled_night);
            } else {
                twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_disabled_day);
            }
        }

        // Set the search custom URL icon.
        if (searchCustomURLPreference.isEnabled()) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                searchCustomURLPreference.setIcon(R.drawable.search_custom_url_enabled_night);
            } else {
                searchCustomURLPreference.setIcon(R.drawable.search_custom_url_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                searchCustomURLPreference.setIcon(R.drawable.search_custom_url_ghosted_night);
            } else {
                searchCustomURLPreference.setIcon(R.drawable.search_custom_url_ghosted_day);
            }
        }

        // Set the Proxy icons according to the theme and status.
        if (proxyString.equals("None")) {  // Proxying is disabled.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {  // Dark theme.
                // Set the main proxy icon to be disabled.
                proxyPreference.setIcon(R.drawable.proxy_disabled_night);

                // Set the custom proxy URL icon to be ghosted.
                proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_night);
            } else {  // Light theme.
                // Set the main proxy icon to be disabled.
                proxyPreference.setIcon(R.drawable.proxy_disabled_day);

                // Set the custom proxy URL icon to be ghosted.
                proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_day);
            }
        } else {  // Proxying is enabled.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {  // Dark theme.
                // Set the main proxy icon to be enabled.
                proxyPreference.setIcon(R.drawable.proxy_enabled_night);

                // Set the custom proxy URL icon according to its status.
                if (proxyCustomUrlPreference.isEnabled()) {  // Custom proxy is enabled.
                    proxyCustomUrlPreference.setIcon(R.drawable.proxy_enabled_night);
                } else {  // Custom proxy is disabled.
                    proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_night);
                }
            } else {  // Light theme.
                // Set the main proxy icon to be enabled.
                proxyPreference.setIcon(R.drawable.proxy_enabled_day);

                // Set the custom proxy URL icon according to its status.
                if (proxyCustomUrlPreference.isEnabled()) {  // Custom proxy is enabled.
                    proxyCustomUrlPreference.setIcon(R.drawable.proxy_enabled_day);
                } else {  // Custom proxy is disabled.
                    proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_day);
                }
            }
        }

        // Set the full screen browsing mode icons.
        if (fullScreenBrowsingMode) {  // Full screen browsing mode is enabled.
            // Set the `fullScreenBrowsingModePreference` icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_enabled_night);
            } else {
                fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_enabled_day);
            }

            // Set the hide app bar icon.
            if (savedPreferences.getBoolean("hide_app_bar", true)) {  // Hide app bar is enabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_night);
                } else {
                    hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_day);
                }
            } else {  // Hide app bar is disabled.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_night);
                } else {
                    hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_day);
                }
            }
        } else {  // Full screen browsing mode is disabled.
            // Set the icons according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_disabled_night);
                hideAppBarPreference.setIcon(R.drawable.app_bar_ghosted_night);
            } else {
                fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_disabled_day);
                hideAppBarPreference.setIcon(R.drawable.app_bar_ghosted_day);
            }
        }

        // Set the clear everything preference icon.
        if (clearEverything) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                clearEverythingPreference.setIcon(R.drawable.clear_everything_enabled_night);
            } else {
                clearEverythingPreference.setIcon(R.drawable.clear_everything_enabled_day);
            }
        } else {
            clearEverythingPreference.setIcon(R.drawable.clear_everything_disabled);
        }

        // Set the clear cookies preference icon.
        if (clearEverything || savedPreferences.getBoolean("clear_cookies", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                clearCookiesPreference.setIcon(R.drawable.cookies_cleared_night);
            } else {
                clearCookiesPreference.setIcon(R.drawable.cookies_cleared_day);
            }
        } else {
            clearCookiesPreference.setIcon(R.drawable.cookies_warning);
        }

        // Set the clear DOM storage preference icon.
        if (clearEverything || savedPreferences.getBoolean("clear_dom_storage", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_night);
            } else {
                clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_day);
            }
        } else {
            clearDomStoragePreference.setIcon(R.drawable.dom_storage_warning);
        }

        // Set the clear form data preference icon if the API < 26.  It has no effect on newer versions of Android.
        if (Build.VERSION.SDK_INT < 26) {
            if (clearEverything || savedPreferences.getBoolean("clear_form_data", true)) {
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                    clearFormDataPreference.setIcon(R.drawable.form_data_cleared_night);
                } else {
                    clearFormDataPreference.setIcon(R.drawable.form_data_cleared_day);
                }
            } else {
                clearFormDataPreference.setIcon(R.drawable.form_data_warning);
            }
        }

        // Set the clear logcat preference icon.
        if (clearEverything || savedPreferences.getBoolean(getString(R.string.clear_logcat_key), true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                clearLogcatPreference.setIcon(R.drawable.bug_cleared_day);
            } else {
                clearLogcatPreference.setIcon(R.drawable.bug_cleared_night);
            }
        } else {
            clearLogcatPreference.setIcon(R.drawable.bug_warning);
        }

        // Set the clear cache preference icon.
        if (clearEverything || savedPreferences.getBoolean("clear_cache", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                clearCachePreference.setIcon(R.drawable.cache_cleared_night);
            } else {
                clearCachePreference.setIcon(R.drawable.cache_cleared_day);
            }
        } else {
            clearCachePreference.setIcon(R.drawable.cache_warning);
        }

        // Set the download custom location icon.
        if (downloadCustomLocationPreference.isEnabled()) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                downloadCustomLocationPreference.setIcon(R.drawable.downloads_enabled_night);
            } else {
                downloadCustomLocationPreference.setIcon(R.drawable.downloads_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                downloadCustomLocationPreference.setIcon(R.drawable.downloads_ghosted_night);
            } else {
                downloadCustomLocationPreference.setIcon(R.drawable.downloads_ghosted_day);
            }
        }

        // Set the open intents in new tab preference icon.
        if (savedPreferences.getBoolean("open_intents_in_new_tab", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                openIntentsInNewTabPreference.setIcon(R.drawable.tab_enabled_night);
            } else {
                openIntentsInNewTabPreference.setIcon(R.drawable.tab_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                openIntentsInNewTabPreference.setIcon(R.drawable.tab_disabled_night);
            } else {
                openIntentsInNewTabPreference.setIcon(R.drawable.tab_disabled_day);
            }
        }

        // Set the swipe to refresh preference icon.
        if (savedPreferences.getBoolean("swipe_to_refresh", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                swipeToRefreshPreference.setIcon(R.drawable.refresh_enabled_night);
            } else {
                swipeToRefreshPreference.setIcon(R.drawable.refresh_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                swipeToRefreshPreference.setIcon(R.drawable.refresh_disabled_night);
            } else {
                swipeToRefreshPreference.setIcon(R.drawable.refresh_disabled_day);
            }
        }

        // Set the scroll app bar preference icon.
        if (savedPreferences.getBoolean("scroll_app_bar", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                scrollAppBarPreference.setIcon(R.drawable.app_bar_enabled_night);
            } else {
                scrollAppBarPreference.setIcon(R.drawable.app_bar_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                scrollAppBarPreference.setIcon(R.drawable.app_bar_disabled_night);
            } else {
                scrollAppBarPreference.setIcon(R.drawable.app_bar_disabled_day);
            }
        }

        // Set the display additional app bar icons preference icon.
        if (savedPreferences.getBoolean("display_additional_app_bar_icons", false)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_enabled_night);
            } else {
                displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_disabled_night);
            } else {
                displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_disabled_day);
            }
        }

        // Set the WebView theme preference icon.
        switch (webViewThemeEntryNumber) {
            case 0:  // The system default WebView theme is selected.
                // Set the icon according to the app theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    webViewThemePreference.setIcon(R.drawable.webview_light_theme_day);
                } else {
                    webViewThemePreference.setIcon(R.drawable.webview_dark_theme_night);
                }
                break;

            case 1:  // The light WebView theme is selected.
                // Set the icon according to the app theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    webViewThemePreference.setIcon(R.drawable.webview_light_theme_day);
                } else {
                    webViewThemePreference.setIcon(R.drawable.webview_light_theme_night);
                }
                break;

            case 2:  // The dark WebView theme is selected.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    webViewThemePreference.setIcon(R.drawable.webview_dark_theme_day);
                } else {
                    webViewThemePreference.setIcon(R.drawable.webview_dark_theme_night);
                }
                break;
        }

        // Set the wide viewport preference icon.
        if (savedPreferences.getBoolean("wide_viewport", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                wideViewportPreference.setIcon(R.drawable.wide_viewport_enabled_night);
            } else {
                wideViewportPreference.setIcon(R.drawable.wide_viewport_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                wideViewportPreference.setIcon(R.drawable.wide_viewport_disabled_night);
            } else {
                wideViewportPreference.setIcon(R.drawable.wide_viewport_disabled_day);
            }
        }

        // Set the display webpage images preference icon.
        if (savedPreferences.getBoolean("display_webpage_images", true)) {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                displayWebpageImagesPreference.setIcon(R.drawable.images_enabled_night);
            } else {
                displayWebpageImagesPreference.setIcon(R.drawable.images_enabled_day);
            }
        } else {
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                displayWebpageImagesPreference.setIcon(R.drawable.images_disabled_night);
            } else {
                displayWebpageImagesPreference.setIcon(R.drawable.images_disabled_day);
            }
        }


        // Listen for preference changes.
        preferencesListener = (SharedPreferences sharedPreferences, String key) -> {
            switch (key) {
                case "javascript":
                    // Update the icons and the DOM storage preference status.
                    if (sharedPreferences.getBoolean("javascript", false)) {  // The JavaScript preference is enabled.
                        // Update the icon for the JavaScript preference.
                        javaScriptPreference.setIcon(R.drawable.javascript_enabled);

                        // Update the status of the DOM storage preference.
                        domStoragePreference.setEnabled(true);

                        // Update the icon for the DOM storage preference.
                        if (sharedPreferences.getBoolean("dom_storage", false)) {
                            domStoragePreference.setIcon(R.drawable.dom_storage_enabled);
                        } else {
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                domStoragePreference.setIcon(R.drawable.dom_storage_disabled_night);
                            } else {
                                domStoragePreference.setIcon(R.drawable.dom_storage_disabled_day);
                            }
                        }
                    } else {  // The JavaScript preference is disabled.
                        // Update the icon for the JavaScript preference.
                        javaScriptPreference.setIcon(R.drawable.privacy_mode);

                        // Update the status of the DOM storage preference.
                        domStoragePreference.setEnabled(false);

                        // Set the icon for DOM storage preference to be ghosted.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            domStoragePreference.setIcon(R.drawable.dom_storage_ghosted_night);
                        } else {
                            domStoragePreference.setIcon(R.drawable.dom_storage_ghosted_day);
                        }
                    }
                    break;

                case "first_party_cookies":
                    // Update the icons for `first_party_cookies` and `third_party_cookies`.
                    if (sharedPreferences.getBoolean("first_party_cookies", false)) {
                        // Set the icon for `first_party_cookies`.
                        firstPartyCookiesPreference.setIcon(R.drawable.cookies_enabled);

                        // Update the icon for `third_party_cookies`.
                        if (Build.VERSION.SDK_INT >= 21) {
                            if (sharedPreferences.getBoolean("third_party_cookies", false)) {
                                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_warning);
                            } else {
                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                    thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_night);
                                } else {
                                    thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_day);
                                }
                            }
                        } else {
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_night);
                            } else {
                                thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_day);
                            }
                        }
                    } else {  // `first_party_cookies` is `false`.
                        // Update the icon for `first_party_cookies`.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            firstPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_night);
                        } else {
                            firstPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_day);
                        }

                        // Set the icon for `third_party_cookies` to be ghosted.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_night);
                        } else {
                            thirdPartyCookiesPreference.setIcon(R.drawable.cookies_ghosted_day);
                        }
                    }

                    // Enable `third_party_cookies` if `first_party_cookies` is `true` and API >= 21.
                    thirdPartyCookiesPreference.setEnabled(sharedPreferences.getBoolean("first_party_cookies", false) && (Build.VERSION.SDK_INT >= 21));
                    break;

                case "third_party_cookies":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("third_party_cookies", false)) {
                        thirdPartyCookiesPreference.setIcon(R.drawable.cookies_warning);
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_night);
                        } else {
                            thirdPartyCookiesPreference.setIcon(R.drawable.cookies_disabled_day);
                        }
                    }
                    break;

                case "dom_storage":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("dom_storage", false)) {
                        domStoragePreference.setIcon(R.drawable.dom_storage_enabled);
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            domStoragePreference.setIcon(R.drawable.dom_storage_disabled_night);
                        } else {
                            domStoragePreference.setIcon(R.drawable.dom_storage_disabled_day);
                        }
                    }
                    break;

                // Save form data can be removed once the minimum API >= 26.
                case "save_form_data":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("save_form_data", false)) {
                        formDataPreference.setIcon(R.drawable.form_data_enabled);
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            formDataPreference.setIcon(R.drawable.form_data_disabled_night);
                        } else {
                            formDataPreference.setIcon(R.drawable.form_data_disabled_day);
                        }
                    }
                    break;

                case "user_agent":
                    // Get the new user agent name.
                    String newUserAgentName = sharedPreferences.getString("user_agent", getString(R.string.user_agent_default_value));

                    // Get the array position for the new user agent name.
                    int newUserAgentArrayPosition = userAgentNamesArray.getPosition(newUserAgentName);

                    // Get the translated new user agent name.
                    String translatedNewUserAgentName = translatedUserAgentNamesArray[newUserAgentArrayPosition];

                    // Populate the user agent summary.
                    switch (newUserAgentArrayPosition) {
                        case MainWebViewActivity.SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                            // Get the user agent text from the webview (which changes based on the version of Android and WebView installed).
                            userAgentPreference.setSummary(translatedNewUserAgentName + ":\n" + bareWebView.getSettings().getUserAgentString());

                            // Disable the custom user agent preference.
                            customUserAgentPreference.setEnabled(false);

                            // Set the custom user agent preference icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_night);
                            } else {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_day);
                            }
                            break;

                        case MainWebViewActivity.SETTINGS_CUSTOM_USER_AGENT:
                            // Set the summary text.
                            userAgentPreference.setSummary(R.string.custom_user_agent);

                            // Enable the custom user agent preference.
                            customUserAgentPreference.setEnabled(true);

                            // Set the custom user agent preference icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_enabled_night);
                            } else {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_enabled_day);
                            }
                            break;

                        default:
                            // Get the user agent summary from the user agent data array.
                            userAgentPreference.setSummary(translatedNewUserAgentName + ":\n" + userAgentDataArray[newUserAgentArrayPosition]);

                            // Disable the custom user agent preference.
                            customUserAgentPreference.setEnabled(false);

                            // Set the custom user agent preference icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_night);
                            } else {
                                customUserAgentPreference.setIcon(R.drawable.custom_user_agent_ghosted_day);
                            }
                    }
                    break;

                case "custom_user_agent":
                    // Set the new custom user agent as the summary text for the preference.
                    customUserAgentPreference.setSummary(sharedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value)));
                    break;

                case "incognito_mode":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("incognito_mode", false)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            incognitoModePreference.setIcon(R.drawable.incognito_mode_enabled_night);
                        } else {
                            incognitoModePreference.setIcon(R.drawable.incognito_mode_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            incognitoModePreference.setIcon(R.drawable.incognito_mode_disabled_night);
                        } else {
                            incognitoModePreference.setIcon(R.drawable.incognito_mode_disabled_day);
                        }
                    }
                    break;

                case "do_not_track":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("do_not_track", false)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            doNotTrackPreference.setIcon(R.drawable.block_tracking_enabled_night);
                        } else {
                            doNotTrackPreference.setIcon(R.drawable.block_tracking_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            doNotTrackPreference.setIcon(R.drawable.block_tracking_disabled_night);
                        } else {
                            doNotTrackPreference.setIcon(R.drawable.block_tracking_disabled_day);
                        }
                    }

                    break;

                case "allow_screenshots":
                    // Update the icon.
                    if (sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_enabled_day);
                        } else {
                            allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_enabled_night);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_disabled_day);
                        } else {
                            allowScreenshotsPreference.setIcon(R.drawable.allow_screenshots_disabled_night);
                        }
                    }

                    // Create an intent to restart Clear Browser.
                    Intent allowScreenshotsRestartIntent = getActivity().getParentActivityIntent();

                    // Assert that the intent is not null to remove the lint error below.
                    assert allowScreenshotsRestartIntent != null;

                    // `Intent.FLAG_ACTIVITY_CLEAR_TASK` removes all activities from the stack.  It requires `Intent.FLAG_ACTIVITY_NEW_TASK`.
                    allowScreenshotsRestartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Create a handler to restart the activity.
                    Handler allowScreenshotsRestartHandler = new Handler();

                    // Create a runnable to restart the activity.
                    Runnable allowScreenshotsRestartRunnable = () -> {
                        // Restart the activity.
                        startActivity(allowScreenshotsRestartIntent);

                        // Kill this instance of Clear Browser.  Otherwise, the app exhibits sporadic behavior after the restart.
                        System.exit(0);
                    };

                    // Restart the activity after 150 milliseconds, so that the app has enough time to save the change to the preference.
                    allowScreenshotsRestartHandler.postDelayed(allowScreenshotsRestartRunnable, 150);
                    break;

                case "easylist":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("easylist", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            easyListPreference.setIcon(R.drawable.block_ads_enabled_night);
                        } else {
                            easyListPreference.setIcon(R.drawable.block_ads_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            easyListPreference.setIcon(R.drawable.block_ads_disabled_night);
                        } else {
                            easyListPreference.setIcon(R.drawable.block_ads_disabled_day);
                        }
                    }
                    break;

                case "easyprivacy":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("easyprivacy", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            easyPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_night);
                        } else {
                            easyPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            easyPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_night);
                        } else {
                            easyPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_day);
                        }
                    }
                    break;

                case "fanboys_annoyance_list":
                    boolean currentFanboyAnnoyanceList = sharedPreferences.getBoolean("fanboys_annoyance_list", true);
                    boolean currentFanboySocialBlockingList = sharedPreferences.getBoolean("fanboys_social_blocking_list", true);

                    // Update the Fanboy icons.
                    if (currentFanboyAnnoyanceList) {  // Fanboy's annoyance list is enabled.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            // Update the Fanboy's annoyance list icon.
                            fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_enabled_night);

                            // Update the Fanboy's social blocking list icon.
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_ghosted_night);
                        } else {
                            // Update the Fanboy's annoyance list icon.
                            fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_enabled_day);

                            // Update the Fanboy's social blocking list icon.
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_ghosted_day);
                        }
                    } else {  // Fanboy's annoyance list is disabled.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            // Update the Fanboy's annoyance list icon.
                            fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_disabled_night);

                            // Update the Fanboy's social blocking list icon.
                            if (currentFanboySocialBlockingList) {
                                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_night);
                            } else {
                                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_night);
                            }
                        } else {
                            // Update the Fanboy's annoyance list icon.
                            fanboyAnnoyanceListPreference.setIcon(R.drawable.social_media_disabled_day);

                            // Update the Fanboy's social blocking list icon.
                            if (currentFanboySocialBlockingList) {
                                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_day);
                            } else {
                                fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_day);
                            }
                        }
                    }

                    // Only enable Fanboy's social blocking list preference if Fanboy's annoyance list preference is disabled.
                    fanboySocialBlockingListPreference.setEnabled(!currentFanboyAnnoyanceList);
                    break;

                case "fanboys_social_blocking_list":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("fanboys_social_blocking_list", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_night);
                        } else {
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_night);
                        } else {
                            fanboySocialBlockingListPreference.setIcon(R.drawable.social_media_disabled_day);
                        }
                    }
                    break;

                case "ultralist":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("ultralist", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            ultraListPreference.setIcon(R.drawable.block_ads_enabled_night);
                        } else {
                            ultraListPreference.setIcon(R.drawable.block_ads_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            ultraListPreference.setIcon(R.drawable.block_ads_disabled_night);
                        } else {
                            ultraListPreference.setIcon(R.drawable.block_ads_disabled_day);
                        }
                    }
                    break;

                case "ultraprivacy":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("ultraprivacy", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            ultraPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_night);
                        } else {
                            ultraPrivacyPreference.setIcon(R.drawable.block_tracking_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            ultraPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_night);
                        } else {
                            ultraPrivacyPreference.setIcon(R.drawable.block_tracking_disabled_day);
                        }
                    }
                    break;

                case "block_all_third_party_requests":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("block_all_third_party_requests", false)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_enabled_night);
                        } else {
                            blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_disabled_night);
                        } else {
                            blockAllThirdPartyRequestsPreference.setIcon(R.drawable.block_all_third_party_requests_disabled_day);
                        }
                    }
                    break;

                case "google_analytics":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("google_analytics", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            googleAnalyticsPreference.setIcon(R.drawable.modify_url_enabled_night);
                        } else {
                            googleAnalyticsPreference.setIcon(R.drawable.modify_url_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            googleAnalyticsPreference.setIcon(R.drawable.modify_url_disabled_night);
                        } else {
                            googleAnalyticsPreference.setIcon(R.drawable.modify_url_disabled_day);
                        }
                    }
                    break;

                case "facebook_click_ids":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("facebook_click_ids", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            facebookClickIdsPreference.setIcon(R.drawable.modify_url_enabled_night);
                        } else {
                            facebookClickIdsPreference.setIcon(R.drawable.modify_url_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            facebookClickIdsPreference.setIcon(R.drawable.modify_url_disabled_night);
                        } else {
                            facebookClickIdsPreference.setIcon(R.drawable.modify_url_disabled_day);
                        }
                    }
                    break;

                case "twitter_amp_redirects":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("twitter_amp_redirects", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_enabled_night);
                        } else {
                            twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_disabled_night);
                        } else {
                            twitterAmpRedirectsPreference.setIcon(R.drawable.modify_url_disabled_day);
                        }
                    }
                    break;

                case "search":
                    // Store the new search string.
                    String newSearchString = sharedPreferences.getString("search", getString(R.string.search_default_value));

                    // Update the search and search custom URL preferences.
                    if (newSearchString.equals("Custom URL")) {  // `Custom URL` is selected.
                        // Set the summary text to `R.string.custom_url`, which is translated.
                        searchPreference.setSummary(R.string.custom_url);

                        // Enable `searchCustomURLPreference`.
                        searchCustomURLPreference.setEnabled(true);

                        // Set the `searchCustomURLPreference` according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            searchCustomURLPreference.setIcon(R.drawable.search_custom_url_enabled_night);
                        } else {
                            searchCustomURLPreference.setIcon(R.drawable.search_custom_url_enabled_day);
                        }
                    } else {  // `Custom URL` is not selected.
                        // Set the summary text to `newSearchString`.
                        searchPreference.setSummary(newSearchString);

                        // Disable `searchCustomURLPreference`.
                        searchCustomURLPreference.setEnabled(false);

                        // Set the `searchCustomURLPreference` according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            searchCustomURLPreference.setIcon(R.drawable.search_custom_url_ghosted_night);
                        } else {
                            searchCustomURLPreference.setIcon(R.drawable.search_custom_url_ghosted_day);
                        }
                    }
                    break;

                case "search_custom_url":
                    // Set the new search custom URL as the summary text for the preference.
                    searchCustomURLPreference.setSummary(sharedPreferences.getString("search_custom_url", getString(R.string.search_custom_url_default_value)));
                    break;

                case "proxy":
                    // Get current proxy string.
                    String currentProxyString = sharedPreferences.getString("proxy", getString(R.string.proxy_default_value));

                    // Update the summary text for the proxy preference.
                    switch (currentProxyString) {
                        case ProxyHelper.NONE:
                            proxyPreference.setSummary(getString(R.string.no_proxy_enabled));
                            break;

                        case ProxyHelper.TOR:
                            if (Build.VERSION.SDK_INT == 19) {  // Proxying through SOCKS doesn't work on Android KitKat.
                                proxyPreference.setSummary(getString(R.string.tor_enabled_kitkat));
                            } else {
                                proxyPreference.setSummary(getString(R.string.tor_enabled));
                            }
                            break;

                        case ProxyHelper.I2P:
                            proxyPreference.setSummary(getString(R.string.i2p_enabled));
                            break;

                        case ProxyHelper.CUSTOM:
                            proxyPreference.setSummary(getString(R.string.custom_proxy));
                            break;
                    }

                    // Update the status of the custom URL preference.
                    proxyCustomUrlPreference.setEnabled(currentProxyString.equals("Custom"));

                    // Update the icons.
                    if (currentProxyString.equals("None")) {  // Proxying is disabled.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {  // Dark theme.
                            // Set the main proxy icon to be disabled
                            proxyPreference.setIcon(R.drawable.proxy_disabled_night);

                            // Set the custom proxy URL icon to be ghosted.
                            proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_night);
                        } else {  // Light theme.
                            // Set the main proxy icon to be disabled.
                            proxyPreference.setIcon(R.drawable.proxy_disabled_day);

                            // Set the custom proxy URL icon to be ghosted.
                            proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_day);
                        }
                    } else {  // Proxying is enabled.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {  // Dark theme.
                            // Set the main proxy icon to be enabled.
                            proxyPreference.setIcon(R.drawable.proxy_enabled_night);

                            /// Set the custom proxy URL icon according to its status.
                            if (proxyCustomUrlPreference.isEnabled()) {  // Custom proxy is enabled.
                                proxyCustomUrlPreference.setIcon(R.drawable.proxy_enabled_night);
                            } else {  // Custom proxy is disabled.
                                proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_night);
                            }
                        } else {  // Light theme.
                            // Set the main proxy icon to be enabled.
                            proxyPreference.setIcon(R.drawable.proxy_enabled_day);

                            // Set the custom proxy URL icon according to its status.
                            if (proxyCustomUrlPreference.isEnabled()) {  // Custom proxy is enabled.
                                proxyCustomUrlPreference.setIcon(R.drawable.proxy_enabled_day);
                            } else {  // Custom proxy is disabled.
                                proxyCustomUrlPreference.setIcon(R.drawable.proxy_ghosted_day);
                            }
                        }
                    }
                    break;

                case "proxy_custom_url":
                    // Set the summary text for the proxy custom URL.
                    proxyCustomUrlPreference.setSummary(sharedPreferences.getString("proxy_custom_url", getString(R.string.proxy_custom_url_default_value)));
                    break;

                case "full_screen_browsing_mode":
                    if (sharedPreferences.getBoolean("full_screen_browsing_mode", false)) {  // Full screen browsing is enabled.
                        // Set the full screen browsing mode preference icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_enabled_night);
                        } else {
                            fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_enabled_day);
                        }

                        // Set the hide app bar preference icon.
                        if (sharedPreferences.getBoolean("hide_app_bar", true)) {  //  Hide app bar is enabled.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_night);
                            } else {
                                hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_day);
                            }
                        } else {  // Hide app bar is disabled.
                            // Set the icon according to the theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_night);
                            } else {
                                hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_day);
                            }
                        }
                    } else {  // Full screen browsing is disabled.
                        // Update the icons according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_disabled_night);
                            hideAppBarPreference.setIcon(R.drawable.app_bar_ghosted_night);
                        } else {
                            fullScreenBrowsingModePreference.setIcon(R.drawable.full_screen_disabled_day);
                            hideAppBarPreference.setIcon(R.drawable.app_bar_ghosted_day);
                        }
                    }
                    break;

                case "hide_app_bar":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("hide_app_bar", true)) {  // Hide app bar is enabled.
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_night);
                        } else {
                            hideAppBarPreference.setIcon(R.drawable.app_bar_enabled_day);
                        }
                    } else {  // Hide app bar is disabled.
                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_night);
                        } else {
                            hideAppBarPreference.setIcon(R.drawable.app_bar_disabled_day);
                        }
                    }
                    break;

                case "clear_everything":
                    // Store the new clear everything status
                    boolean newClearEverythingBoolean = sharedPreferences.getBoolean("clear_everything", true);

                    // Update the status of the clear and exit preferences.
                    clearCookiesPreference.setEnabled(!newClearEverythingBoolean);
                    clearDomStoragePreference.setEnabled(!newClearEverythingBoolean);
                    clearFormDataPreference.setEnabled(!newClearEverythingBoolean);  // This line can be removed once the minimum API >= 26.
                    clearLogcatPreference.setEnabled(!newClearEverythingBoolean);
                    clearCachePreference.setEnabled(!newClearEverythingBoolean);

                    // Update the clear everything preference icon.
                    if (newClearEverythingBoolean) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearEverythingPreference.setIcon(R.drawable.clear_everything_enabled_night);
                        } else {
                            clearEverythingPreference.setIcon(R.drawable.clear_everything_enabled_day);
                        }
                    } else {
                        clearEverythingPreference.setIcon(R.drawable.clear_everything_disabled);
                    }

                    // Update the clear cookies preference icon.
                    if (newClearEverythingBoolean || sharedPreferences.getBoolean("clear_cookies", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearCookiesPreference.setIcon(R.drawable.cookies_cleared_night);
                        } else {
                            clearCookiesPreference.setIcon(R.drawable.cookies_cleared_day);
                        }
                    } else {
                        clearCookiesPreference.setIcon(R.drawable.cookies_warning);
                    }

                    // Update the clear dom storage preference icon.
                    if (newClearEverythingBoolean || sharedPreferences.getBoolean("clear_dom_storage", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_night);
                        } else {
                            clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_day);
                        }
                    } else {
                        clearDomStoragePreference.setIcon(R.drawable.dom_storage_warning);
                    }

                    // Update the clear form data preference icon if the API < 26.
                    if (Build.VERSION.SDK_INT < 26) {
                        if (newClearEverythingBoolean || sharedPreferences.getBoolean("clear_form_data", true)) {
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                clearFormDataPreference.setIcon(R.drawable.form_data_cleared_night);
                            } else {
                                clearFormDataPreference.setIcon(R.drawable.form_data_cleared_day);
                            }
                        } else {
                            clearFormDataPreference.setIcon(R.drawable.form_data_warning);
                        }
                    }

                    // Update the clear logcat preference icon.
                    if (newClearEverythingBoolean || sharedPreferences.getBoolean(getString(R.string.clear_logcat_key), true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            clearLogcatPreference.setIcon(R.drawable.bug_cleared_day);
                        } else {
                            clearLogcatPreference.setIcon(R.drawable.bug_cleared_night);
                        }
                    } else {
                        clearLogcatPreference.setIcon(R.drawable.cache_warning);
                    }

                    // Update the clear cache preference icon.
                    if (newClearEverythingBoolean || sharedPreferences.getBoolean("clear_cache", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearCachePreference.setIcon(R.drawable.cache_cleared_night);
                        } else {
                            clearCachePreference.setIcon(R.drawable.cache_cleared_day);
                        }
                    } else {
                        clearCachePreference.setIcon(R.drawable.cache_warning);
                    }
                    break;

                case "clear_cookies":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("clear_cookies", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearCookiesPreference.setIcon(R.drawable.cookies_cleared_night);
                        } else {
                            clearCookiesPreference.setIcon(R.drawable.cookies_cleared_day);
                        }
                    } else {
                        clearCookiesPreference.setIcon(R.drawable.cookies_warning);
                    }
                    break;

                case "clear_dom_storage":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("clear_dom_storage", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_night);
                        } else {
                            clearDomStoragePreference.setIcon(R.drawable.dom_storage_cleared_day);
                        }
                    } else {
                        clearDomStoragePreference.setIcon(R.drawable.dom_storage_warning);
                    }
                    break;

                // This section can be removed once the minimum API >= 26.
                case "clear_form_data":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("clear_form_data", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearFormDataPreference.setIcon(R.drawable.form_data_cleared_night);
                        } else {
                            clearFormDataPreference.setIcon(R.drawable.form_data_cleared_day);
                        }
                    } else {
                        clearFormDataPreference.setIcon(R.drawable.form_data_warning);
                    }
                    break;

                case "clear_logcat":
                    // Update the icon.
                    if (sharedPreferences.getBoolean(getString(R.string.clear_logcat_key), true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            clearLogcatPreference.setIcon(R.drawable.bug_cleared_day);
                        } else {
                            clearLogcatPreference.setIcon(R.drawable.bug_cleared_night);
                        }
                    } else {
                        clearLogcatPreference.setIcon(R.drawable.bug_warning);
                    }
                    break;

                case "clear_cache":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("clear_cache", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            clearCachePreference.setIcon(R.drawable.cache_cleared_night);
                        } else {
                            clearCachePreference.setIcon(R.drawable.cache_cleared_day);
                        }
                    } else {
                        clearCachePreference.setIcon(R.drawable.cache_warning);
                    }
                    break;

                case "homepage":
                    // Set the new homepage URL as the summary text for the Homepage preference.
                    homepagePreference.setSummary(sharedPreferences.getString("homepage", getString(R.string.homepage_default_value)));
                    break;

                case "download_location":
                    // Get the new download location.
                    String newDownloadLocationString = sharedPreferences.getString("download_location", getString(R.string.download_location_default_value));

                    // Check to see if a download custom location is selected.
                    if (newDownloadLocationString.equals(downloadLocationEntryValuesStringArray[3])) {  // A download custom location is selected.
                        // Set the download location summary text to be `Custom`.
                        downloadLocationPreference.setSummary(downloadLocationEntriesStringArray[3]);

                        // Enable the download custom location preference.
                        downloadCustomLocationPreference.setEnabled(true);
                    } else {  // A download custom location is not selected.
                        // Set the download location summary text to be the download location.
                        downloadLocationPreference.setSummary(downloadLocationHelper.getDownloadLocation(context));

                        // Disable the download custom location.
                        downloadCustomLocationPreference.setEnabled(newDownloadLocationString.equals(downloadLocationEntryValuesStringArray[3]));
                    }

                    // Update the download custom location icon.
                    if (downloadCustomLocationPreference.isEnabled()) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            downloadCustomLocationPreference.setIcon(R.drawable.downloads_enabled_night);
                        } else {
                            downloadCustomLocationPreference.setIcon(R.drawable.downloads_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            downloadCustomLocationPreference.setIcon(R.drawable.downloads_ghosted_night);
                        } else {
                            downloadCustomLocationPreference.setIcon(R.drawable.downloads_ghosted_day);
                        }
                    }
                    break;

                case "download_custom_location":
                    // Set the new download custom location as the summary text for the preference.
                    downloadCustomLocationPreference.setSummary(sharedPreferences.getString("download_custom_location", getString(R.string.download_custom_location_default_value)));
                    break;

                case "font_size":
                    // Update the font size summary text.
                    fontSizePreference.setSummary(sharedPreferences.getString("font_size", getString(R.string.font_size_default_value)) + "%");
                    break;

                case "open_intents_in_new_tab":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("open_intents_in_new_tab", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            openIntentsInNewTabPreference.setIcon(R.drawable.tab_enabled_night);
                        } else {
                            openIntentsInNewTabPreference.setIcon(R.drawable.tab_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            openIntentsInNewTabPreference.setIcon(R.drawable.tab_disabled_night);
                        } else {
                            openIntentsInNewTabPreference.setIcon(R.drawable.tab_disabled_day);
                        }
                    }
                    break;

                case "swipe_to_refresh":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("swipe_to_refresh", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            swipeToRefreshPreference.setIcon(R.drawable.refresh_enabled_night);
                        } else {
                            swipeToRefreshPreference.setIcon(R.drawable.refresh_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            swipeToRefreshPreference.setIcon(R.drawable.refresh_disabled_night);
                        } else {
                            swipeToRefreshPreference.setIcon(R.drawable.refresh_disabled_day);
                        }
                    }
                    break;

                case "scroll_app_bar":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("scroll_app_bar", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            scrollAppBarPreference.setIcon(R.drawable.app_bar_enabled_night);
                        } else {
                            scrollAppBarPreference.setIcon(R.drawable.app_bar_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            scrollAppBarPreference.setIcon(R.drawable.app_bar_disabled_night);
                        } else {
                            scrollAppBarPreference.setIcon(R.drawable.app_bar_disabled_day);
                        }
                    }
                    break;

                case "display_additional_app_bar_icons":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("display_additional_app_bar_icons", false)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_enabled_night);
                        } else {
                            displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_disabled_night);
                        } else {
                            displayAdditionalAppBarIconsPreference.setIcon(R.drawable.more_disabled_day);
                        }
                    }
                    break;

                case "app_theme":
                    // Get the new theme.
                    String newAppTheme = sharedPreferences.getString("app_theme", getString(R.string.app_theme_default_value));

                    // Update the system according to the new theme.  A switch statement cannot be used because the theme entry values string array is not a compile time constant.
                    if (newAppTheme.equals(appThemeEntryValuesStringArray[1])) {  // The light theme is selected.
                        // Update the theme preference summary text.
                        appThemePreference.setSummary(appThemeEntriesStringArray[1]);

                        // Apply the new theme.
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else if (newAppTheme.equals(appThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
                        // Update the theme preference summary text.
                        appThemePreference.setSummary(appThemeEntriesStringArray[2]);

                        // Apply the new theme.
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {  // The system default theme is selected.
                        // Update the theme preference summary text.
                        appThemePreference.setSummary(appThemeEntriesStringArray[0]);

                        // Apply the new theme.
                        if (Build.VERSION.SDK_INT >= 28) {  // The system default theme is supported.
                            // Follow the system default theme.
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        } else {// The system default theme is not supported.
                            // Follow the battery saver mode.
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        }
                    }

                    // Update the current theme status.
                    currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    break;

                case "webview_theme":
                    // Get the new WebView theme.
                    String newWebViewTheme = sharedPreferences.getString("webview_theme", getString(R.string.webview_theme_default_value));

                    // Define a new WebView theme entry number.
                    int newWebViewThemeEntryNumber;

                    // Get the webView theme entry number that matches the new WebView theme.  A switch statement cannot be used because the theme entry values string array is not a compile time constant.
                    if (newWebViewTheme.equals(webViewThemeEntriesStringArray[1])) {  // The light theme is selected.
                        // Store the new WebView theme entry number.
                        newWebViewThemeEntryNumber = 1;
                    } else if (newWebViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
                        // Store the WebView theme entry number.
                        newWebViewThemeEntryNumber = 2;
                    } else {  // The system default theme is selected.
                        // Store the WebView theme entry number.
                        newWebViewThemeEntryNumber = 0;
                    }

                    // Update the icon.
                    switch (newWebViewThemeEntryNumber) {
                        case 0:  // The system default WebView theme is selected.
                            // Set the icon according to the app theme.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                webViewThemePreference.setIcon(R.drawable.webview_light_theme_day);
                            } else {
                                webViewThemePreference.setIcon(R.drawable.webview_dark_theme_night);
                            }
                            break;

                        case 1:  // The system default WebView theme is selected.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                webViewThemePreference.setIcon(R.drawable.webview_light_theme_day);
                            } else {
                                webViewThemePreference.setIcon(R.drawable.webview_light_theme_night);
                            }
                            break;

                        case 2:  // The system default WebView theme is selected.
                            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                                webViewThemePreference.setIcon(R.drawable.webview_dark_theme_day);
                            } else {
                                webViewThemePreference.setIcon(R.drawable.webview_dark_theme_night);
                            }
                            break;
                    }

                    // Set the current theme as the summary text for the preference.
                    webViewThemePreference.setSummary(webViewThemeEntriesStringArray[newWebViewThemeEntryNumber]);
                    break;

                case "wide_viewport":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("wide_viewport", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            wideViewportPreference.setIcon(R.drawable.wide_viewport_enabled_night);
                        } else {
                            wideViewportPreference.setIcon(R.drawable.wide_viewport_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            wideViewportPreference.setIcon(R.drawable.wide_viewport_disabled_night);
                        } else {
                            wideViewportPreference.setIcon(R.drawable.wide_viewport_disabled_day);
                        }
                    }
                    break;

                case "display_webpage_images":
                    // Update the icon.
                    if (sharedPreferences.getBoolean("display_webpage_images", true)) {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayWebpageImagesPreference.setIcon(R.drawable.images_enabled_night);
                        } else {
                            displayWebpageImagesPreference.setIcon(R.drawable.images_enabled_day);
                        }
                    } else {
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                            displayWebpageImagesPreference.setIcon(R.drawable.images_disabled_night);
                        } else {
                            displayWebpageImagesPreference.setIcon(R.drawable.images_disabled_day);
                        }
                    }
                    break;
            }
        };

        // Register the listener.
        savedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener);
    }

    // It is necessary to re-register the listener on every resume or it will randomly stop working because apps can be paused and resumed at any time, even while running in the foreground.
    @Override
    public void onPause() {
        super.onPause();
        savedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        savedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener);
    }
}