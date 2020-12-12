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

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jdots.browser.R;
import com.jdots.browser.activities.DomainsActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DomainsListFragment extends Fragment {
    // Instantiate the dismiss snackbar interface.
    private DismissSnackbarInterface dismissSnackbarInterface;

    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Populate the dismiss snackbar interface.
        dismissSnackbarInterface = (DismissSnackbarInterface) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate `domains_list_fragment`.  `false` does not attach it to the root `container`.
        View domainsListFragmentView = inflater.inflate(R.layout.domains_list_fragment, container, false);

        // Get a handle for the domains listview.
        ListView domainsListView = domainsListFragmentView.findViewById(R.id.domains_listview);

        // Remove the incorrect lint error below that `.getSupportFragmentManager()` might be null.
        assert getActivity() != null;

        // Get a handle for the support fragment manager.
        final FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        domainsListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Dismiss the snackbar if it is visible.
            dismissSnackbarInterface.dismissSnackbar();

            // Save the current domain settings if operating in two-paned mode and a domain is currently selected.
            if (DomainsActivity.twoPanedMode && DomainsActivity.deleteMenuItem.isEnabled()) {
                // Get a handle for the domain settings fragment.
                Fragment domainSettingsFragment = supportFragmentManager.findFragmentById(R.id.domain_settings_fragment_container);

                // Remove the incorrect lint warning below that the domain settings fragment might be null.
                assert domainSettingsFragment != null;

                // Get a handle for the domain settings fragment view.
                View domainSettingsFragmentView = domainSettingsFragment.getView();

                // Remove the incorrect lint warning below that the domain settings fragment view might be null.
                assert domainSettingsFragmentView != null;

                // Get a handle for the domains activity.
                DomainsActivity domainsActivity = new DomainsActivity();

                // Save the domain settings.
                domainsActivity.saveDomainSettings(domainSettingsFragmentView, getResources());
            }

            // Store the new `currentDomainDatabaseId`, converting it from `long` to `int` to match the format of the domains database.
            DomainsActivity.currentDomainDatabaseId = (int) id;

            // Add `currentDomainDatabaseId` to `argumentsBundle`.
            Bundle argumentsBundle = new Bundle();
            argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, DomainsActivity.currentDomainDatabaseId);

            // Add the arguments bundle to the domain settings fragment.
            DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();
            domainSettingsFragment.setArguments(argumentsBundle);

            // Check to see if the device is in two paned mode.
            if (DomainsActivity.twoPanedMode) {  // The device in in two-paned mode.
                // enable `deleteMenuItem` if the system is not waiting for a `Snackbar` to be dismissed.
                if (!DomainsActivity.dismissingSnackbar) {
                    // Enable the delete menu item.
                    DomainsActivity.deleteMenuItem.setEnabled(true);

                    // Get the current theme status.
                    int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                    // Set the delete icon according to the theme.
                    if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                        DomainsActivity.deleteMenuItem.setIcon(R.drawable.delete_night);
                    } else {
                        DomainsActivity.deleteMenuItem.setIcon(R.drawable.delete_day);
                    }
                }

                // Display the domain settings fragment.
                supportFragmentManager.beginTransaction().replace(R.id.domain_settings_fragment_container, domainSettingsFragment).commit();
            } else { // The device in in single-paned mode
                // Save the domains listview position.
                DomainsActivity.domainsListViewPosition = domainsListView.getFirstVisiblePosition();

                // Show `deleteMenuItem` if the system is not waiting for a `Snackbar` to be dismissed.
                if (!DomainsActivity.dismissingSnackbar) {
                    DomainsActivity.deleteMenuItem.setVisible(true);
                }

                // Hide the add domain FAB.
                FloatingActionButton addDomainFAB = getActivity().findViewById(R.id.add_domain_fab);
                addDomainFAB.hide();

                // Display the domain settings fragment.
                supportFragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();
            }
        });

        // Return the domains list fragment.
        return domainsListFragmentView;
    }

    // Define the public dismiss snackbar interface.
    public interface DismissSnackbarInterface {
        void dismissSnackbar();
    }
}