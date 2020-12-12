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

package com.jdots.browser.adapters;

import android.content.Context;

import com.jdots.browser.R;
import com.jdots.browser.fragments.GuideWebViewFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class GuidePagerAdapter extends FragmentPagerAdapter {
    // Define the class variables.
    private final Context context;

    // The default constructor.
    public GuidePagerAdapter(FragmentManager fragmentManager, Context context) {
        // Run the default commands.
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // Store the class variables.
        this.context = context;
    }

    @Override
    // Get the count of the number of tabs.
    public int getCount() {
        return 9;
    }

    @Override
    // Get the name of each tab.  Tab numbers start at 0.
    public CharSequence getPageTitle(int tab) {
        switch (tab) {
            case 0:
                return context.getString(R.string.overview);

            case 1:
                return context.getString(R.string.javascript);

            case 2:
                return context.getString(R.string.local_storage);

            case 3:
                return context.getString(R.string.user_agent);

            case 4:
                return context.getString(R.string.requests);

            case 5:
                return context.getString(R.string.domain_settings);

            case 6:
                return context.getString(R.string.ssl_certificates);

            case 7:
                return context.getString(R.string.proxies);

            case 8:
                return context.getString(R.string.tracking_ids);

            default:
                return "";
        }
    }

    @Override
    @NonNull
    // Setup each tab.
    public Fragment getItem(int tabNumber) {
        return GuideWebViewFragment.createTab(tabNumber);
    }
}