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

public class AboutWebViewFragment extends Fragment {
    // Declare the class constants.
    final static String TAB_NUMBER = "tab_number";

    // Declare the class variables.
    private int tabNumber;

    // Declare the class views.
    private View webViewLayout;

    public static AboutWebViewFragment createTab(int tabNumber) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the arguments in the bundle.
        argumentsBundle.putInt(TAB_NUMBER, tabNumber);

        // Create a new instance of the tab fragment.
        AboutWebViewFragment aboutWebViewFragment = new AboutWebViewFragment();

        // Add the arguments bundle to the fragment.
        aboutWebViewFragment.setArguments(argumentsBundle);

        // Return the new fragment.
        return aboutWebViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Get a handle for the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint warning below that arguments might be null.
        assert arguments != null;

        // Store the tab number in a class variable.
        tabNumber = arguments.getInt(TAB_NUMBER);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout.  Setting false at the end of inflater.inflate does not attach the inflated layout as a child of container.  The fragment will take care of attaching the root automatically.
        webViewLayout = layoutInflater.inflate(R.layout.bare_webview, container, false);

        // Get a handle for tab WebView.
        WebView tabWebView = (WebView) webViewLayout;

        // Get a handle for the context.
        Context context = getContext();

        // Remove the incorrect lint warning below that the context might be null.
        assert context != null;

        // Create a WebView asset loader.
        final WebViewAssetLoader webViewAssetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context)).build();

        // Set a WebView client.
        tabWebView.setWebViewClient(new WebViewClient() {
            // `shouldOverrideUrlLoading` allows the sending of external links back to the main Clear Browser WebView.  The deprecated `shouldOverrideUrlLoading` must be used until API >= 24.
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
                // Have the WebView asset loader process the request.  This allows the loading of SVG files, which otherwise is prevented by the CORS policy.
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

        // Load the indicated tab.  The tab numbers start at 0, with the WebView tabs starting at 1.
        switch (tabNumber) {
            case 1:
                // Load the Permissions tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_permissions.html");
                break;

            case 2:
                // Load the Privacy Policy tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_privacy_policy.html");
                break;

            case 3:
                // Load the Changelog tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_changelog.html");
                break;

            case 4:
                // Load the Licenses tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_licenses.html");
                break;

            case 5:
                // Load the Contributors tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_contributors.html");
                break;

            case 6:
                // Load the Links tab.
                tabWebView.loadUrl("https://appassets.androidplatform.net/assets/" + getString(R.string.android_asset_path) + "/about_links.html");
                break;
        }

        // Scroll the tab if the saved instance state is not null.
        if (savedInstanceState != null) {
            tabWebView.post(() -> {
                tabWebView.setScrollX(savedInstanceState.getInt("scroll_x"));
                tabWebView.setScrollY(savedInstanceState.getInt("scroll_y"));
            });
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

        // Save the scroll positions if the layout is not null, which can happen if a tab is not currently selected.
        if (tabWebView != null) {
            savedInstanceState.putInt("scroll_x", tabWebView.getScrollX());
            savedInstanceState.putInt("scroll_y", tabWebView.getScrollY());
        }
    }
}