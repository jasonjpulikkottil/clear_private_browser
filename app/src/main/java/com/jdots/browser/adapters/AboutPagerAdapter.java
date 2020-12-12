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
import com.jdots.browser.fragments.AboutVersionFragment;
import com.jdots.browser.fragments.AboutWebViewFragment;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AboutPagerAdapter extends FragmentPagerAdapter {
    // Define the class variables.
    private final Context context;
    private final String[] blocklistVersions;
    private final LinkedList<Fragment> aboutFragmentList = new LinkedList<>();

    public AboutPagerAdapter(FragmentManager fragmentManager, Context context, String[] blocklistVersions) {
        // Run the default commands.
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // Store the class variables.
        this.context = context;
        this.blocklistVersions = blocklistVersions;
    }

    @Override
    // Get the count of the number of tabs.
    public int getCount() {
        return 7;
    }

    @Override
    // Get the name of each tab.  Tab numbers start at 0.
    public CharSequence getPageTitle(int tab) {
        switch (tab) {
            case 0:
                return context.getString(R.string.version);

            case 1:
                return context.getString(R.string.permissions);

            case 2:
                return context.getString(R.string.privacy_policy);

            case 3:
                return context.getString(R.string.changelog);

            case 4:
                return context.getString(R.string.licenses);

            case 5:
                return context.getString(R.string.contributors);

            case 6:
                return context.getString(R.string.links);

            default:
                return "";
        }
    }

    @Override
    @NonNull
    // Setup each tab.
    public Fragment getItem(int tabNumber) {
        // Create the tab fragment and add it to the list.
        if (tabNumber == 0) {
            // Add the version tab to the list.
            aboutFragmentList.add(AboutVersionFragment.createTab(blocklistVersions));
        } else {
            // Add the WebView tab to the list.
            aboutFragmentList.add(AboutWebViewFragment.createTab(tabNumber));
        }

        // Return the tab number fragment.
        return aboutFragmentList.get(tabNumber);
    }

    public Fragment getTabFragment(int tabNumber) {
        // Return the tab fragment.
        return aboutFragmentList.get(tabNumber);
    }
}