/*
 *   2016-2019
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

package com.jdots.browser.helpers;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jdots.browser.R;
import com.jdots.browser.dialogs.AdConsentDialog;

public class AdHelper {
    private static boolean initialized;

    public static void initializeAds(View view, Context applicationContext, FragmentManager fragmentManager, String googleAppId, String adUnitId) {
        if (!initialized) {  // This is the first run.
            // Initialize mobile ads.
            MobileAds.initialize(applicationContext, googleAppId);

            // Initialize the bookmarks database helper.  The `0` specifies a database version, but that is ignored and set instead using a constant in `AdConsentDatabaseHelper`.
            AdConsentDatabaseHelper adConsentDatabaseHelper = new AdConsentDatabaseHelper(applicationContext, null, null, 0);

            // Check to see if consent has been granted.
            boolean adConsentGranted = adConsentDatabaseHelper.isGranted();

            // Display the ad consent dialog if needed.
            if (!adConsentGranted) {  // Ad consent has not been granted.
                // Display the ad consent dialog.
                DialogFragment adConsentDialogFragment = new AdConsentDialog();
                adConsentDialogFragment.show(fragmentManager, "Ad Consent");
            } else {  // Ad consent has been granted.
                // Load an ad.
                loadAd(view, applicationContext, adUnitId);
            }

            // Set the initialized variable to true so this section doesn't run again.
            initialized = true;
        } else {  // Ads have previously been initialized.
            // Load an ad.
            loadAd(view, applicationContext, adUnitId);
        }
    }

    public static void loadAd(View view, Context applicationContext, String adUnitId) {
        // Cast the generic view to an AdView.
        AdView adView = (AdView) view;

        // Save the layout parameters.  They are used when programatically recreating the ad below.
        RelativeLayout.LayoutParams adViewLayoutParameters = (RelativeLayout.LayoutParams) adView.getLayoutParams();

        // Remove the AdView.
        RelativeLayout adViewParentLayout = (RelativeLayout) adView.getParent();
        adViewParentLayout.removeView(adView);

        // Setup the new AdView.  This is necessary because the size of the banner ad can change on rotate.
        adView = new AdView(applicationContext);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(adUnitId);
        adView.setId(R.id.adview);
        adView.setLayoutParams(adViewLayoutParameters);

        // Display the new AdView.
        adViewParentLayout.addView(adView);

        // Only request non-personalized ads.  https://developers.google.com/ad-manager/mobile-ads-sdk/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
        Bundle adSettingsBundle = new Bundle();
        adSettingsBundle.putString("npa", "1");

        // Build the ad request.
        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, adSettingsBundle).build();

        // Make it so.
        adView.loadAd(adRequest);
    }

    public static void hideAd(View view) {
        // Cast the generic view to an AdView.
        AdView adView = (AdView) view;

        // Hide the ad.
        adView.setVisibility(View.GONE);
    }

    public static void pauseAd(View view) {
        // Cast The generic view to an AdView.
        AdView adView = (AdView) view;

        // Pause the AdView.
        adView.pause();
    }

    public static void resumeAd(View view) {
        // Cast the generic view to an AdView.
        AdView adView = (AdView) view;

        // Resume the AdView.
        adView.resume();
    }
}