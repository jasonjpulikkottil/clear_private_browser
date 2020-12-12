/*
 *   2019-2020
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

import android.os.Bundle;

import com.jdots.browser.fragments.WebViewTabFragment;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WebViewPagerAdapter extends FragmentPagerAdapter {
    // The WebView fragments list contains all the WebViews.
    private final LinkedList<WebViewTabFragment> webViewFragmentsList = new LinkedList<>();

    // Define the constructor.
    public WebViewPagerAdapter(FragmentManager fragmentManager) {
        // Run the default commands.
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public int getCount() {
        // Return the number of pages.
        return webViewFragmentsList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        //noinspection SuspiciousMethodCalls
        if (webViewFragmentsList.contains(object)) {
            // Return the current page position.
            //noinspection SuspiciousMethodCalls
            return webViewFragmentsList.indexOf(object);
        } else {
            // The tab has been deleted.
            return POSITION_NONE;
        }
    }

    @Override
    @NonNull
    public Fragment getItem(int pageNumber) {
        // Get the fragment for a particular page.  Page numbers are 0 indexed.
        return webViewFragmentsList.get(pageNumber);
    }

    @Override
    public long getItemId(int position) {
        // Return the unique ID for this page.
        return webViewFragmentsList.get(position).fragmentId;
    }

    public int getPositionForId(long fragmentId) {
        // Initialize the position variable.
        int position = -1;

        // Initialize the while counter.
        int i = 0;

        // Find the current position of the WebView fragment with the given ID.
        while (position < 0 && i < webViewFragmentsList.size()) {
            // Check to see if the tab ID of this WebView matches the page ID.
            if (webViewFragmentsList.get(i).fragmentId == fragmentId) {
                // Store the position if they are a match.
                position = i;
            }

            // Increment the counter.
            i++;
        }

        // Set the position to be the last tab if it is not found.
        // Sometimes there is a race condition in populating the webView fragments list when resuming Clear Browser and displaying an SSL certificate error while loading a new intent.
        // In that case, the last tab should be the one it is looking for.
        if (position == -1) {
            position = webViewFragmentsList.size() - 1;
        }

        // Return the position.
        return position;
    }

    public void addPage(int pageNumber, ViewPager webViewPager, String url, boolean moveToNewPage) {
        // Add a new page.
        webViewFragmentsList.add(WebViewTabFragment.createPage(pageNumber, url));

        // Update the view pager.
        notifyDataSetChanged();

        // Move to the new page if indicated.
        if (moveToNewPage) {
            webViewPager.setCurrentItem(pageNumber);
        }
    }

    public void restorePage(Bundle savedState, Bundle savedNestedScrollWebViewState) {
        // Restore the page.
        webViewFragmentsList.add(WebViewTabFragment.restorePage(savedState, savedNestedScrollWebViewState));

        // Update the view pager.
        notifyDataSetChanged();
    }

    public boolean deletePage(int pageNumber, ViewPager webViewPager) {
        // Delete the page.
        webViewFragmentsList.remove(pageNumber);

        // Update the view pager.
        notifyDataSetChanged();

        // Return true if the selected page number did not change after the delete.  This will cause the calling method to reset the current WebView to the new contents of this page number.
        return (webViewPager.getCurrentItem() == pageNumber);
    }

    public WebViewTabFragment getPageFragment(int pageNumber) {
        // Return the page fragment.
        return webViewFragmentsList.get(pageNumber);
    }
}