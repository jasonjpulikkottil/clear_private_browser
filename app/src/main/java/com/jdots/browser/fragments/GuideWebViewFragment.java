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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jdots.browser.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewFeature;

public class GuideWebViewFragment extends Fragment {
    // Declare the class constants.
    private final static String TAB_NUMBER = "tab_number";

    // Declare the class variables.
    private int tabNumber;

    // Declare the class views.
    private View webViewLayout;

    // Store the tab number in the arguments bundle.
    public static GuideWebViewFragment createTab(int tabNumber) {
        // Create a bundle.
        Bundle bundle = new Bundle();

        // Store the tab number in the bundle.
        bundle.putInt(TAB_NUMBER, tabNumber);

        // Create a new guide tab fragment.
        GuideWebViewFragment guideWebViewFragment = new GuideWebViewFragment();

        // Add the bundle to the fragment.
        guideWebViewFragment.setArguments(bundle);

        // Return the new fragment.
        return guideWebViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the lint warning below that arguments might be null.
        assert arguments != null;

        // Store the tab number in a class variable.
        tabNumber = arguments.getInt(TAB_NUMBER);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout.  The fragment will take care of attaching the root automatically.
        webViewLayout = inflater.inflate(R.layout.bare_webview, container, false);

        // Get a handle for the tab WebView.
        WebView tabWebView = (WebView) webViewLayout;

        // Get a handle for the context.
        Context context = getContext();

        // Remove the incorrect lint warning below that the context might be null.
        assert context != null;

        // Create a WebView asset loader.
        final WebViewAssetLoader webViewAssetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context)).build();

        // Set a WebView client.
        tabWebView.setWebViewClient(new WebViewClient() {
            // `shouldOverrideUrlLoading` allows sending of external links back to the main Clear Browser WebView.  The deprecated `shouldOverrideUrlLoading` must be used until API >= 24.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Create an intent to view the URL.
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);

                // Add the URL to the intent.
                urlIntent.setData(Uri.parse(url));

                // Make it so.
                startActivity(urlIntent);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                // Have the WebView asset loader process the request.  This allows loading of SVG files, which otherwise is prevented by the CORS policy.
                return webViewAssetLoader.shouldInterceptRequest(Uri.parse(url));
            }
        });

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Check to see if the app is in night mode.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES && WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {  // The app is in night mode.
            // Apply the dark WebView theme.
            WebSettingsCompat.setForceDark(tabWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }

        // Load the indicated tab.  The tab numbers start at 0.
        switch (tabNumber) {
            case 0:
                // Load the Overview tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_overview.html");
                break;

            case 1:
                // Load the JavaScript tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_javascript.html");
                break;

            case 2:
                // Load the Local Storage tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_local_storage.html");
                break;

            case 3:
                // Load the User Agent tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_user_agent.html");
                break;

            case 4:
                // Load the Requests tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_requests.html");
                break;

            case 5:
                // Load the Domain Settings tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_domain_settings.html");
                break;

            case 6:
                // Load the SSL Certificates tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_ssl_certificates.html");
                break;

            case 7:
                // Load the Proxies tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_proxies.html");
                break;

            case 8:
                // Load the Tracking IDs tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/guide_tracking_ids.html");
                break;
        }

        // Scroll the WebView if the saved instance state is not null.
        if (savedInstanceState != null) {
            tabWebView.post(() -> tabWebView.setScrollY(savedInstanceState.getInt("scroll_y")));
        }

        // Return the formatted WebView layout.
        return webViewLayout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Get a handle for the tab WebView.  A class variable cannot be used because it gets out of sync when restarting.
        WebView tabWebView = (WebView) webViewLayout;

        // Save the scroll Y position if the tab WebView is not null, which can happen if a tab is not currently selected.
        if (tabWebView != null) {
            savedInstanceState.putInt("scroll_y", tabWebView.getScrollY());
        }
    }
}