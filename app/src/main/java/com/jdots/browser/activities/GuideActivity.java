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

package com.jdots.browser.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.jdots.browser.R;
import com.jdots.browser.adapters.GuidePagerAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class GuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Set the theme.
        setTheme(R.style.ClearBrowser);

        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Set the content view.
        setContentView(R.layout.guide_coordinatorlayout);

        // The AndroidX toolbar must be used until the minimum API is >= 21.
        Toolbar toolbar = findViewById(R.id.guide_toolbar);
        setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Remove the incorrect lint warning that the action bar might be null.
        assert actionBar != null;

        // Display the home arrow on the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        //  Get a handle for the view pager and the tab layout.
        ViewPager aboutViewPager = findViewById(R.id.guide_viewpager);
        TabLayout aboutTabLayout = findViewById(R.id.guide_tablayout);

        // Remove the incorrect lint warnings that the views might be null
        assert aboutViewPager != null;
        assert aboutTabLayout != null;

        // Set the view pager adapter.
        aboutViewPager.setAdapter(new GuidePagerAdapter(getSupportFragmentManager(), getApplicationContext()));

        // Keep all the tabs in memory.
        aboutViewPager.setOffscreenPageLimit(10);

        // Link the tab layout to the view pager.
        aboutTabLayout.setupWithViewPager(aboutViewPager);
    }
}