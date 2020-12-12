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

package com.jdots.browser.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jdots.browser.R;
import com.jdots.browser.dialogs.AddDomainDialog;
import com.jdots.browser.fragments.DomainSettingsFragment;
import com.jdots.browser.fragments.DomainsListFragment;
import com.jdots.browser.helpers.DomainsDatabaseHelper;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class DomainsActivity extends AppCompatActivity implements AddDomainDialog.AddDomainListener, DomainsListFragment.DismissSnackbarInterface {
    // Define the public static variables.
    public static int domainsListViewPosition;
    public static boolean twoPanedMode;
    public static int currentDomainDatabaseId;
    public static MenuItem deleteMenuItem;
    public static boolean dismissingSnackbar;

    // The SSL certificate and IP address information are accessed from `DomainSettingsFragment` and `saveDomainSettings()`.
    public static String sslIssuedToCName;
    public static String sslIssuedToOName;
    public static String sslIssuedToUName;
    public static String sslIssuedByCName;
    public static String sslIssuedByOName;
    public static String sslIssuedByUName;
    public static long sslStartDateLong;
    public static long sslEndDateLong;
    public static String currentIpAddresses;
    // `domainsDatabaseHelper` is used in `onCreate()`, `saveDomainSettings()`, and `onDestroy()`.
    private static DomainsDatabaseHelper domainsDatabaseHelper;
    // Initialize the class constants.
    private final String LISTVIEW_POSITION = "listview_position";
    private final String DOMAIN_SETTINGS_DISPLAYED = "domain_settings_displayed";
    private final String DOMAIN_SETTINGS_DATABASE_ID = "domain_settings_database_is";
    private final String DOMAIN_SETTINGS_SCROLL_Y = "domain_settings_scroll_y";
    // Initialize the class variables.
    private boolean restartAfterRotate;
    private boolean domainSettingsDisplayedBeforeRotate;
    private int domainSettingsDatabaseIdBeforeRotate;
    private int domainSettingsScrollY = 0;
    // Defile the class views.
    private ListView domainsListView;
    // `closeActivityAfterDismissingSnackbar` is used in `onOptionsItemSelected()`, and `onBackPressed()`.
    private boolean closeActivityAfterDismissingSnackbar;
    // The undelete snackbar is used in `onOptionsItemSelected()` and `onBackPressed()`.
    private Snackbar undoDeleteSnackbar;
    // `addDomainFAB` is used in `onCreate()`, `onCreateOptionsMenu()`, `onOptionsItemSelected()`, and `onBackPressed()`.
    private FloatingActionButton addDomainFAB;

    // `deletedDomainPosition` is used in an inner and outer class in `onOptionsItemSelected()`.
    private int deletedDomainPosition;

    // `goDirectlyToDatabaseId` is used in `onCreate()` and `onCreateOptionsMenu()`.
    private int goDirectlyToDatabaseId;

    // `closeOnBack` is used in `onCreate()`, `onOptionsItemSelected()` and `onBackPressed()`.
    private boolean closeOnBack;

    // `coordinatorLayout` is use in `onCreate()`, `onOptionsItemSelected()`, and `onSaveInstanceState()`.
    private View coordinatorLayout;

    // `resources` is used in `onCreate()`, `onOptionsItemSelected()`, and `onSaveInstanceState()`.
    private Resources resources;

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

        // Initialize the domains listview position.
        domainsListViewPosition = 0;

        // Extract the values from the saved instance state if it is not null.
        if (savedInstanceState != null) {
            domainsListViewPosition = savedInstanceState.getInt(LISTVIEW_POSITION);
            restartAfterRotate = true;
            domainSettingsDisplayedBeforeRotate = savedInstanceState.getBoolean(DOMAIN_SETTINGS_DISPLAYED);
            domainSettingsDatabaseIdBeforeRotate = savedInstanceState.getInt(DOMAIN_SETTINGS_DATABASE_ID);
            domainSettingsScrollY = savedInstanceState.getInt(DOMAIN_SETTINGS_SCROLL_Y);
        }

        // Get the launching intent
        Intent intent = getIntent();

        // Extract the domain to load if there is one.  `-1` is the default value.
        goDirectlyToDatabaseId = intent.getIntExtra("load_domain", -1);

        // Get the status of close-on-back, which is true when the domains activity is called from the options menu.
        closeOnBack = intent.getBooleanExtra("close_on_back", false);

        // Get the current URL.
        String currentUrl = intent.getStringExtra("current_url");

        // Store the current SSL certificate information in class variables.
        sslIssuedToCName = intent.getStringExtra("ssl_issued_to_cname");
        sslIssuedToOName = intent.getStringExtra("ssl_issued_to_oname");
        sslIssuedToUName = intent.getStringExtra("ssl_issued_to_uname");
        sslIssuedByCName = intent.getStringExtra("ssl_issued_by_cname");
        sslIssuedByOName = intent.getStringExtra("ssl_issued_by_oname");
        sslIssuedByUName = intent.getStringExtra("ssl_issued_by_uname");
        sslStartDateLong = intent.getLongExtra("ssl_start_date", 0);
        sslEndDateLong = intent.getLongExtra("ssl_end_date", 0);
        currentIpAddresses = intent.getStringExtra("current_ip_addresses");

        // Set the content view.
        setContentView(R.layout.domains_coordinatorlayout);

        // Populate the class variables.
        coordinatorLayout = findViewById(R.id.domains_coordinatorlayout);
        resources = getResources();

        // `SupportActionBar` from `android.support.v7.app.ActionBar` must be used until the minimum API is >= 21.
        final Toolbar toolbar = findViewById(R.id.domains_toolbar);
        setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        ActionBar actionBar = getSupportActionBar();

        // Remove the incorrect lint warning that the action bar might be null.
        assert actionBar != null;

        // Set the back arrow on the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialize the database handler.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
        domainsDatabaseHelper = new DomainsDatabaseHelper(this, null, null, 0);

        // Determine if we are in two pane mode.  `domain_settings_fragment_container` does not exist on devices with a width less than 900dp.
        twoPanedMode = (findViewById(R.id.domain_settings_fragment_container) != null);

        // Get a handle for the add domain floating action button.
        addDomainFAB = findViewById(R.id.add_domain_fab);

        // Configure the add domain floating action button.
        addDomainFAB.setOnClickListener((View view) -> {
            // Remove the incorrect warning below that the current URL might be null.
            assert currentUrl != null;

            // Create an add domain dialog.
            DialogFragment addDomainDialog = AddDomainDialog.addDomain(currentUrl);

            // Show the add domain dialog.
            addDomainDialog.show(getSupportFragmentManager(), resources.getString(R.string.add_domain));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.domains_options_menu, menu);

        // Store `deleteMenuItem` for future use.
        deleteMenuItem = menu.findItem(R.id.delete_domain);

        // Only display `deleteMenuItem` (initially) in two-paned mode.
        deleteMenuItem.setVisible(twoPanedMode);

        // Get a handle for the fragment manager.
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Display the fragments.  This must be done from `onCreateOptionsMenu()` instead of `onCreate()` because `populateDomainsListView()` needs `deleteMenuItem` to be inflated.
        if (restartAfterRotate && domainSettingsDisplayedBeforeRotate) {  // The device was rotated and domain settings were displayed previously.
            if (twoPanedMode) {  // The device is in two-paned mode.
                // Reset `restartAfterRotate`.
                restartAfterRotate = false;

                // Display `DomainsListFragment`.
                DomainsListFragment domainsListFragment = new DomainsListFragment();
                fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
                fragmentManager.executePendingTransactions();

                // Populate the list of domains.  `domainSettingsDatabaseId` highlights the domain that was highlighted before the rotation.
                populateDomainsListView(domainSettingsDatabaseIdBeforeRotate, domainsListViewPosition);
            } else {  // The device is in single-paned mode.
                // Reset `restartAfterRotate`.
                restartAfterRotate = false;

                // Store the current domain database ID.
                currentDomainDatabaseId = domainSettingsDatabaseIdBeforeRotate;

                // Create an arguments bundle.
                Bundle argumentsBundle = new Bundle();

                // Add the domain settings arguments.
                argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, currentDomainDatabaseId);
                argumentsBundle.putInt(DomainSettingsFragment.SCROLL_Y, domainSettingsScrollY);

                // Instantiate a new domain settings fragment.
                DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();

                // Add the arguments bundle to the domain settings fragment.
                domainSettingsFragment.setArguments(argumentsBundle);

                // Show the delete menu item.
                deleteMenuItem.setVisible(true);

                // Hide the add domain floating action button.
                addDomainFAB.hide();

                // Display the domain settings fragment.
                fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();
            }
        } else {  // The device was not rotated or, if it was, domain settings were not displayed previously.
            if (goDirectlyToDatabaseId >= 0) {  // Load the indicated domain settings.
                // Store the current domain database ID.
                currentDomainDatabaseId = goDirectlyToDatabaseId;

                if (twoPanedMode) {  // The device is in two-paned mode.
                    // Display `DomainsListFragment`.
                    DomainsListFragment domainsListFragment = new DomainsListFragment();
                    fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
                    fragmentManager.executePendingTransactions();

                    // Populate the list of domains.  `domainSettingsDatabaseId` highlights the domain that was highlighted before the rotation.
                    populateDomainsListView(goDirectlyToDatabaseId, domainsListViewPosition);
                } else {  // The device is in single-paned mode.
                    // Create an arguments bundle.
                    Bundle argumentsBundle = new Bundle();

                    // Add the domain settings to arguments bundle.
                    argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, goDirectlyToDatabaseId);
                    argumentsBundle.putInt(DomainSettingsFragment.SCROLL_Y, domainSettingsScrollY);

                    // Instantiate a new domain settings fragment.
                    DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();

                    // Add the arguments bundle to the domain settings fragment`.
                    domainSettingsFragment.setArguments(argumentsBundle);

                    // Show the delete menu item.
                    deleteMenuItem.setVisible(true);

                    // Hide the add domain floating action button.
                    addDomainFAB.hide();

                    // Display the domain settings fragment.
                    fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();
                }
            } else {  // Highlight the first domain.
                // Display `DomainsListFragment`.
                DomainsListFragment domainsListFragment = new DomainsListFragment();
                fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
                fragmentManager.executePendingTransactions();

                // Populate the list of domains.  `-1` highlights the first domain.
                populateDomainsListView(-1, domainsListViewPosition);
            }
        }

        // Success!
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Get the ID of the menu item that was selected.
        int menuItemId = menuItem.getItemId();

        // Get a handle for the fragment manager.
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Run the command according to the selected menu item.
        if (menuItemId == android.R.id.home) {  // The home arrow is identified as `android.R.id.home`, not just `R.id.home`.
            // Check if the device is in two-paned mode.
            if (twoPanedMode) {  // The device is in two-paned mode.
                // Save the current domain settings if the domain settings fragment is displayed.
                if (findViewById(R.id.domain_settings_scrollview) != null) {
                    saveDomainSettings(coordinatorLayout, resources);
                }

                // Dismiss the undo delete snackbar if it is shown.
                if (undoDeleteSnackbar != null && undoDeleteSnackbar.isShown()) {
                    // Set the close flag.
                    closeActivityAfterDismissingSnackbar = true;

                    // Dismiss the snackbar.
                    undoDeleteSnackbar.dismiss();
                } else {
                    // Go home.
                    NavUtils.navigateUpFromSameTask(this);
                }
            } else if (closeOnBack) {  // Go directly back to the main WebView activity because the domains activity was launched from the options menu.
                // Save the current domain settings.
                saveDomainSettings(coordinatorLayout, resources);

                // Go home.
                NavUtils.navigateUpFromSameTask(this);
            } else if (findViewById(R.id.domain_settings_scrollview) != null) {  // The device is in single-paned mode and the domain settings fragment is displayed.
                // Save the current domain settings.
                saveDomainSettings(coordinatorLayout, resources);

                // Display the domains list fragment.
                DomainsListFragment domainsListFragment = new DomainsListFragment();
                fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
                fragmentManager.executePendingTransactions();

                // Populate the list of domains.  `-1` highlights the first domain if in two-paned mode.  It has no effect in single-paned mode.
                populateDomainsListView(-1, domainsListViewPosition);

                // Show the add domain floating action button.
                addDomainFAB.show();

                // Hide the delete menu item.
                deleteMenuItem.setVisible(false);
            } else {  // The device is in single-paned mode and domains list fragment is displayed.
                // Dismiss the undo delete `SnackBar` if it is shown.
                if (undoDeleteSnackbar != null && undoDeleteSnackbar.isShown()) {
                    // Set the close flag.
                    closeActivityAfterDismissingSnackbar = true;

                    // Dismiss the snackbar.
                    undoDeleteSnackbar.dismiss();
                } else {
                    // Go home.
                    NavUtils.navigateUpFromSameTask(this);
                }
            }
        } else if (menuItemId == R.id.delete_domain) {  // Delete.
            // Get a handle for the activity.
            Activity activity = this;

            // Check to see if the domain settings were loaded directly for editing of this app in single-paned mode.
            if (closeOnBack && !twoPanedMode) {  // The activity should delete the domain settings and exit straight to the the main WebView activity.
                // Delete the selected domain.
                domainsDatabaseHelper.deleteDomain(currentDomainDatabaseId);

                // Go home.
                NavUtils.navigateUpFromSameTask(activity);
            } else {  // A snackbar should be shown before deleting the domain settings.
                // Reset close-on-back, which otherwise can cause errors if the system attempts to save settings for a domain that no longer exists.
                closeOnBack = false;

                // Store a copy of the current domain database ID because it could change while the snackbar is displayed.
                final int databaseIdToDelete = currentDomainDatabaseId;

                // Update the fragments and menu items.
                if (twoPanedMode) {  // Two-paned mode.
                    // Store the deleted domain position, which is needed if undo is selected in the snackbar.
                    deletedDomainPosition = domainsListView.getCheckedItemPosition();

                    // Disable the options menu items.
                    deleteMenuItem.setEnabled(false);
                    deleteMenuItem.setIcon(R.drawable.delete_disabled);

                    // Remove the domain settings fragment.
                    fragmentManager.beginTransaction().remove(Objects.requireNonNull(fragmentManager.findFragmentById(R.id.domain_settings_fragment_container))).commit();
                } else {  // Single-paned mode.
                    // Display the domains list fragment.
                    DomainsListFragment domainsListFragment = new DomainsListFragment();
                    fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
                    fragmentManager.executePendingTransactions();

                    // Show the add domain floating action button.
                    addDomainFAB.show();

                    // Hide `deleteMenuItem`.
                    deleteMenuItem.setVisible(false);
                }

                // Get a cursor that does not show the domain to be deleted.
                Cursor domainsPendingDeleteCursor = domainsDatabaseHelper.getDomainNameCursorOrderedByDomainExcept(databaseIdToDelete);

                // Setup the domains pending delete cursor adapter.
                CursorAdapter domainsPendingDeleteCursorAdapter = new CursorAdapter(this, domainsPendingDeleteCursor, false) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        // Inflate the individual item layout.
                        return getLayoutInflater().inflate(R.layout.domain_name_linearlayout, parent, false);
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        // Get the domain name string.
                        String domainNameString = cursor.getString(cursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME));

                        // Get a handle for the domain name text view.
                        TextView domainNameTextView = view.findViewById(R.id.domain_name_textview);

                        // Display the domain name.
                        domainNameTextView.setText(domainNameString);
                    }
                };

                // Update the handle for the current domains list view.
                domainsListView = findViewById(R.id.domains_listview);

                // Update the list view.
                domainsListView.setAdapter(domainsPendingDeleteCursorAdapter);

                // Display a snackbar.
                undoDeleteSnackbar = Snackbar.make(domainsListView, R.string.domain_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, (View v) -> {
                            // Do nothing because everything will be handled by `onDismissed()` below.
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Run commands based on the event.
                                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {  // The user pushed the `Undo` button.
                                    // Create an arguments bundle.
                                    Bundle argumentsBundle = new Bundle();

                                    // Store the domains settings in the arguments bundle.
                                    argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, databaseIdToDelete);
                                    argumentsBundle.putInt(DomainSettingsFragment.SCROLL_Y, domainSettingsScrollY);

                                    // Instantiate a new domain settings fragment.
                                    DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();

                                    // Add the arguments bundle to the domain settings fragment.
                                    domainSettingsFragment.setArguments(argumentsBundle);

                                    // Display the correct fragments.
                                    if (twoPanedMode) {  // The device in in two-paned mode.
                                        // Get a cursor with the current contents of the domains database.
                                        Cursor undoDeleteDomainsCursor = domainsDatabaseHelper.getDomainNameCursorOrderedByDomain();

                                        // Setup the domains cursor adapter.
                                        CursorAdapter undoDeleteDomainsCursorAdapter = new CursorAdapter(getApplicationContext(), undoDeleteDomainsCursor, false) {
                                            @Override
                                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                                // Inflate the individual item layout.
                                                return getLayoutInflater().inflate(R.layout.domain_name_linearlayout, parent, false);
                                            }

                                            @Override
                                            public void bindView(View view, Context context, Cursor cursor) {
                                                /// Get the domain name string.
                                                String domainNameString = cursor.getString(cursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME));

                                                // Get a handle for the domain name text view.
                                                TextView domainNameTextView = view.findViewById(R.id.domain_name_textview);

                                                // Display the domain name.
                                                domainNameTextView.setText(domainNameString);
                                            }
                                        };

                                        // Update the domains list view.
                                        domainsListView.setAdapter(undoDeleteDomainsCursorAdapter);

                                        // Select the previously deleted domain in the list view.
                                        domainsListView.setItemChecked(deletedDomainPosition, true);

                                        // Display the domain settings fragment.
                                        fragmentManager.beginTransaction().replace(R.id.domain_settings_fragment_container, domainSettingsFragment).commit();

                                        // Enable the options delete menu item.
                                        deleteMenuItem.setEnabled(true);

                                        // Get the current theme status.
                                        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                                        // Set the delete menu item icon according to the theme.
                                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                            deleteMenuItem.setIcon(R.drawable.delete_night);
                                        } else {
                                            deleteMenuItem.setIcon(R.drawable.delete_day);
                                        }
                                    } else {  // The device in in one-paned mode.
                                        // Display the domain settings fragment.
                                        fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();

                                        // Hide the add domain floating action button.
                                        addDomainFAB.hide();

                                        // Show and enable the delete menu item.
                                        deleteMenuItem.setVisible(true);

                                        // Display the domain settings fragment.
                                        fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();
                                    }
                                } else {  // The snackbar was dismissed without the undo button being pushed.
                                    // Delete the selected domain.
                                    domainsDatabaseHelper.deleteDomain(databaseIdToDelete);

                                    // Enable the delete menu item if the system was waiting for a snackbar to be dismissed.
                                    if (dismissingSnackbar) {
                                        // Create a `Runnable` to enable the delete menu item.
                                        Runnable enableDeleteMenuItemRunnable = () -> {
                                            // Enable the delete menu item according to the display mode.
                                            if (twoPanedMode) {  // Two-paned mode.
                                                // Enable the delete menu item.
                                                deleteMenuItem.setEnabled(true);

                                                // Get the current theme status.
                                                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                                                // Set the delete menu item icon according to the theme.
                                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                                                    deleteMenuItem.setIcon(R.drawable.delete_night);
                                                } else {
                                                    deleteMenuItem.setIcon(R.drawable.delete_day);
                                                }
                                            } else {  // Single-paned mode.
                                                // Show the delete menu item.
                                                deleteMenuItem.setVisible(true);
                                            }

                                            // Reset the dismissing snackbar tracker.
                                            dismissingSnackbar = false;
                                        };

                                        // Enable the delete menu icon after 100 milliseconds to make sure that the previous domain has been deleted from the database.
                                        Handler handler = new Handler();
                                        handler.postDelayed(enableDeleteMenuItemRunnable, 100);
                                    }

                                    // Close the activity if back was pressed.
                                    if (closeActivityAfterDismissingSnackbar) {
                                        // Go home.
                                        NavUtils.navigateUpFromSameTask(activity);
                                    }
                                }
                            }
                        });

                // Show the Snackbar.
                undoDeleteSnackbar.show();
            }
        }

        // Consume the event.
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Get a handle for the domain settings scrollview.
        ScrollView domainSettingsScrollView = findViewById(R.id.domain_settings_scrollview);

        // Check to see if the domain settings scrollview exists.
        if (domainSettingsScrollView == null) {  // The domain settings are not displayed.
            // Store the domain settings status in the bundle.
            savedInstanceState.putBoolean(DOMAIN_SETTINGS_DISPLAYED, false);
            savedInstanceState.putInt(DOMAIN_SETTINGS_DATABASE_ID, -1);
            savedInstanceState.putInt(DOMAIN_SETTINGS_SCROLL_Y, 0);
        } else {  // The domain settings are displayed.
            // Save any changes that have been made to the domain settings.
            saveDomainSettings(coordinatorLayout, resources);

            // Get the domain settings scroll Y.
            int domainSettingsScrollY = domainSettingsScrollView.getScrollY();

            // Store the domain settings status in the bundle.
            savedInstanceState.putBoolean(DOMAIN_SETTINGS_DISPLAYED, true);
            savedInstanceState.putInt(DOMAIN_SETTINGS_DATABASE_ID, DomainSettingsFragment.databaseId);
            savedInstanceState.putInt(DOMAIN_SETTINGS_SCROLL_Y, domainSettingsScrollY);
        }

        // Check to see if the domains listview exists.
        if (domainsListView != null) {
            // Get the domains listview position.
            int domainsListViewPosition = domainsListView.getFirstVisiblePosition();

            // Store the listview position in the bundle.
            savedInstanceState.putInt(LISTVIEW_POSITION, domainsListViewPosition);
        }
    }

    // Control what the navigation bar back button does.
    @Override
    public void onBackPressed() {
        // Get a handle for the fragment manager.
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (twoPanedMode) {  // The device is in two-paned mode.
            // Save the current domain settings if the domain settings fragment is displayed.
            if (findViewById(R.id.domain_settings_scrollview) != null) {
                saveDomainSettings(coordinatorLayout, resources);
            }

            // Dismiss the undo delete SnackBar if it is shown.
            if ((undoDeleteSnackbar != null) && undoDeleteSnackbar.isShown()) {
                // Set the close flag.
                closeActivityAfterDismissingSnackbar = true;

                // Dismiss the snackbar.
                undoDeleteSnackbar.dismiss();
            } else {
                // Pass `onBackPressed()` to the system.
                super.onBackPressed();
            }
        } else if (closeOnBack) {  // Go directly back to the main WebView activity because the domains activity was launched from the options menu.
            // Save the current domain settings.
            saveDomainSettings(coordinatorLayout, resources);

            // Pass `onBackPressed()` to the system.
            super.onBackPressed();
        } else if (findViewById(R.id.domain_settings_scrollview) != null) {  // The device is in single-paned mode and domain settings fragment is displayed.
            // Save the current domain settings.
            saveDomainSettings(coordinatorLayout, resources);

            // Display the domains list fragment.
            DomainsListFragment domainsListFragment = new DomainsListFragment();
            fragmentManager.beginTransaction().replace(R.id.domains_listview_fragment_container, domainsListFragment).commit();
            fragmentManager.executePendingTransactions();

            // Populate the list of domains.  `-1` highlights the first domain if in two-paned mode.  It has no effect in single-paned mode.
            populateDomainsListView(-1, domainsListViewPosition);

            // Show the add domain floating action button.
            addDomainFAB.show();

            // Hide the delete menu item.
            deleteMenuItem.setVisible(false);
        } else {  // The device is in single-paned mode and the domain list fragment is displayed.
            // Dismiss the undo delete SnackBar if it is shown.
            if ((undoDeleteSnackbar != null) && undoDeleteSnackbar.isShown()) {
                // Set the close flag.
                closeActivityAfterDismissingSnackbar = true;

                // Dismiss the snackbar.
                undoDeleteSnackbar.dismiss();
            } else {
                // Pass `onBackPressed()` to the system.
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onAddDomain(@NonNull DialogFragment dialogFragment) {
        // Dismiss the undo delete snackbar if it is currently displayed.
        if ((undoDeleteSnackbar != null) && undoDeleteSnackbar.isShown()) {
            undoDeleteSnackbar.dismiss();
        }

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialogFragment.getDialog() != null;

        // Get a handle for the domain name edit text.
        EditText domainNameEditText = dialogFragment.getDialog().findViewById(R.id.domain_name_edittext);

        // Get the domain name string.
        String domainNameString = domainNameEditText.getText().toString();

        // Create the domain and store the database ID in `currentDomainDatabaseId`.
        currentDomainDatabaseId = domainsDatabaseHelper.addDomain(domainNameString);

        // Display the newly created domain.
        if (twoPanedMode) {  // The device in in two-paned mode.
            populateDomainsListView(currentDomainDatabaseId, 0);
        } else {  // The device is in single-paned mode.
            // Hide the add domain floating action button.
            addDomainFAB.hide();

            // Show and enable the delete menu item.
            DomainsActivity.deleteMenuItem.setVisible(true);

            // Create an arguments bundle.
            Bundle argumentsBundle = new Bundle();

            // Add the domain settings to the arguments bundle.  The scroll Y should always be `0` on a new domain.
            argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, currentDomainDatabaseId);
            argumentsBundle.putInt(DomainSettingsFragment.SCROLL_Y, 0);

            // Instantiate a new domain settings fragment.
            DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();

            // Add the arguments bundle to the domain setting fragment.
            domainSettingsFragment.setArguments(argumentsBundle);

            // Display the domain settings fragment.
            getSupportFragmentManager().beginTransaction().replace(R.id.domains_listview_fragment_container, domainSettingsFragment).commit();
        }
    }

    public void saveDomainSettings(View view, Resources resources) {
        // Get handles for the domain settings.
        EditText domainNameEditText = view.findViewById(R.id.domain_settings_name_edittext);
        SwitchCompat javaScriptSwitch = view.findViewById(R.id.javascript_switch);
        SwitchCompat firstPartyCookiesSwitch = view.findViewById(R.id.first_party_cookies_switch);
        SwitchCompat thirdPartyCookiesSwitch = view.findViewById(R.id.third_party_cookies_switch);
        SwitchCompat domStorageSwitch = view.findViewById(R.id.dom_storage_switch);
        SwitchCompat formDataSwitch = view.findViewById(R.id.form_data_switch);  // Form data can be removed once the minimum API >= 26.
        SwitchCompat easyListSwitch = view.findViewById(R.id.easylist_switch);
        SwitchCompat easyPrivacySwitch = view.findViewById(R.id.easyprivacy_switch);
        SwitchCompat fanboysAnnoyanceSwitch = view.findViewById(R.id.fanboys_annoyance_list_switch);
        SwitchCompat fanboysSocialBlockingSwitch = view.findViewById(R.id.fanboys_social_blocking_list_switch);
        SwitchCompat ultraListSwitch = view.findViewById(R.id.ultralist_switch);
        SwitchCompat ultraPrivacySwitch = view.findViewById(R.id.ultraprivacy_switch);
        SwitchCompat blockAllThirdPartyRequestsSwitch = view.findViewById(R.id.block_all_third_party_requests_switch);
        Spinner userAgentSpinner = view.findViewById(R.id.user_agent_spinner);
        EditText customUserAgentEditText = view.findViewById(R.id.custom_user_agent_edittext);
        Spinner fontSizeSpinner = view.findViewById(R.id.font_size_spinner);
        EditText customFontSizeEditText = view.findViewById(R.id.custom_font_size_edittext);
        Spinner swipeToRefreshSpinner = view.findViewById(R.id.swipe_to_refresh_spinner);
        Spinner webViewThemeSpinner = view.findViewById(R.id.webview_theme_spinner);
        Spinner wideViewportSpinner = view.findViewById(R.id.wide_viewport_spinner);
        Spinner displayWebpageImagesSpinner = view.findViewById(R.id.display_webpage_images_spinner);
        SwitchCompat pinnedSslCertificateSwitch = view.findViewById(R.id.pinned_ssl_certificate_switch);
        RadioButton currentWebsiteCertificateRadioButton = view.findViewById(R.id.current_website_certificate_radiobutton);
        SwitchCompat pinnedIpAddressesSwitch = view.findViewById(R.id.pinned_ip_addresses_switch);
        RadioButton currentIpAddressesRadioButton = view.findViewById(R.id.current_ip_addresses_radiobutton);

        // Extract the data for the domain settings.
        String domainNameString = domainNameEditText.getText().toString();
        boolean javaScript = javaScriptSwitch.isChecked();
        boolean firstPartyCookies = firstPartyCookiesSwitch.isChecked();
        boolean thirdPartyCookies = thirdPartyCookiesSwitch.isChecked();
        boolean domStorage = domStorageSwitch.isChecked();
        boolean formData = formDataSwitch.isChecked();  // Form data can be removed once the minimum API >= 26.
        boolean easyList = easyListSwitch.isChecked();
        boolean easyPrivacy = easyPrivacySwitch.isChecked();
        boolean fanboysAnnoyance = fanboysAnnoyanceSwitch.isChecked();
        boolean fanboysSocialBlocking = fanboysSocialBlockingSwitch.isChecked();
        boolean ultraList = ultraListSwitch.isChecked();
        boolean ultraPrivacy = ultraPrivacySwitch.isChecked();
        boolean blockAllThirdPartyRequests = blockAllThirdPartyRequestsSwitch.isChecked();
        int userAgentSwitchPosition = userAgentSpinner.getSelectedItemPosition();
        int fontSizeSwitchPosition = fontSizeSpinner.getSelectedItemPosition();
        int swipeToRefreshInt = swipeToRefreshSpinner.getSelectedItemPosition();
        int webViewThemeInt = webViewThemeSpinner.getSelectedItemPosition();
        int wideViewportInt = wideViewportSpinner.getSelectedItemPosition();
        int displayWebpageImagesInt = displayWebpageImagesSpinner.getSelectedItemPosition();
        boolean pinnedSslCertificate = pinnedSslCertificateSwitch.isChecked();
        boolean pinnedIpAddress = pinnedIpAddressesSwitch.isChecked();

        // Initialize the user agent name string.
        String userAgentName;

        // Set the user agent name.
        switch (userAgentSwitchPosition) {
            case MainWebViewActivity.DOMAINS_SYSTEM_DEFAULT_USER_AGENT:
                // Set the user agent name to be `System default user agent`.
                userAgentName = resources.getString(R.string.system_default_user_agent);
                break;

            case MainWebViewActivity.DOMAINS_CUSTOM_USER_AGENT:
                // Set the user agent name to be the custom user agent.
                userAgentName = customUserAgentEditText.getText().toString();
                break;

            default:
                // Get the array of user agent names.
                String[] userAgentNameArray = resources.getStringArray(R.array.user_agent_names);

                // Set the user agent name from the array.  The domain spinner has one more entry than the name array, so the position must be decremented.
                userAgentName = userAgentNameArray[userAgentSwitchPosition - 1];
        }

        // Initialize the font size integer.  `0` indicates the system default font size.
        int fontSizeInt = 0;

        // Use a custom font size if it is selected.
        if (fontSizeSwitchPosition == 1) {  // A custom font size is specified.
            // Get the custom font size from the edit text.
            fontSizeInt = Integer.parseInt(customFontSizeEditText.getText().toString());
        }

        // Save the domain settings.
        domainsDatabaseHelper.updateDomain(DomainsActivity.currentDomainDatabaseId, domainNameString, javaScript, firstPartyCookies, thirdPartyCookies, domStorage, formData, easyList, easyPrivacy,
                fanboysAnnoyance, fanboysSocialBlocking, ultraList, ultraPrivacy, blockAllThirdPartyRequests, userAgentName, fontSizeInt, swipeToRefreshInt, webViewThemeInt, wideViewportInt,
                displayWebpageImagesInt, pinnedSslCertificate, pinnedIpAddress);

        // Update the pinned SSL certificate if a new one is checked.
        if (currentWebsiteCertificateRadioButton.isChecked()) {
            // Update the database.
            domainsDatabaseHelper.updatePinnedSslCertificate(currentDomainDatabaseId, sslIssuedToCName, sslIssuedToOName, sslIssuedToUName, sslIssuedByCName, sslIssuedByOName, sslIssuedByUName,
                    sslStartDateLong, sslEndDateLong);
        }

        // Update the pinned IP addresses if new ones are checked.
        if (currentIpAddressesRadioButton.isChecked()) {
            // Update the database.
            domainsDatabaseHelper.updatePinnedIpAddresses(currentDomainDatabaseId, currentIpAddresses);
        }
    }

    private void populateDomainsListView(final int highlightedDomainDatabaseId, int domainsListViewPosition) {
        // get a handle for the current `domains_listview`.
        domainsListView = findViewById(R.id.domains_listview);

        // Get a `Cursor` with the current contents of the domains database.
        Cursor domainsCursor = domainsDatabaseHelper.getDomainNameCursorOrderedByDomain();

        // Setup `domainsCursorAdapter` with `this` context.  `false` disables `autoRequery`.
        CursorAdapter domainsCursorAdapter = new CursorAdapter(getApplicationContext(), domainsCursor, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                // Inflate the individual item layout.  `false` does not attach it to the root.
                return getLayoutInflater().inflate(R.layout.domain_name_linearlayout, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Set the domain name.
                String domainNameString = cursor.getString(cursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME));
                TextView domainNameTextView = view.findViewById(R.id.domain_name_textview);
                domainNameTextView.setText(domainNameString);
            }
        };

        // Update the list view.
        domainsListView.setAdapter(domainsCursorAdapter);

        // Restore the scroll position.
        domainsListView.setSelection(domainsListViewPosition);

        // Display the domain settings in the second pane if operating in two pane mode and the database contains at least one domain.
        if (DomainsActivity.twoPanedMode && (domainsCursor.getCount() > 0)) {  // Two-paned mode is enabled and there is at least one domain.
            // Initialize `highlightedDomainPosition`.
            int highlightedDomainPosition = 0;

            // Get the cursor position for the highlighted domain.
            for (int i = 0; i < domainsCursor.getCount(); i++) {
                // Move to position `i` in the cursor.
                domainsCursor.moveToPosition(i);

                // Get the database ID for this position.
                int currentDatabaseId = domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper._ID));

                // Set `highlightedDomainPosition` if the database ID for this matches `highlightedDomainDatabaseId`.
                if (highlightedDomainDatabaseId == currentDatabaseId) {
                    highlightedDomainPosition = i;
                }
            }

            // Select the highlighted domain.
            domainsListView.setItemChecked(highlightedDomainPosition, true);

            // Get the database ID for the highlighted domain.
            domainsCursor.moveToPosition(highlightedDomainPosition);
            currentDomainDatabaseId = domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper._ID));

            // Create an arguments bundle.
            Bundle argumentsBundle = new Bundle();

            // Store the domain settings in the arguments bundle.
            argumentsBundle.putInt(DomainSettingsFragment.DATABASE_ID, currentDomainDatabaseId);
            argumentsBundle.putInt(DomainSettingsFragment.SCROLL_Y, domainSettingsScrollY);

            // Instantiate a new domain settings fragment.
            DomainSettingsFragment domainSettingsFragment = new DomainSettingsFragment();

            // Add the arguments bundle to the domain settings fragment.
            domainSettingsFragment.setArguments(argumentsBundle);

            // Display the domain settings fragment.
            getSupportFragmentManager().beginTransaction().replace(R.id.domain_settings_fragment_container, domainSettingsFragment).commit();

            // Enable the delete options menu items.
            deleteMenuItem.setEnabled(true);

            // Get the current theme status.
            int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            // Set the delete icon according to the theme.
            if (currentThemeStatus == Configuration.UI_MODE_NIGHT_YES) {
                deleteMenuItem.setIcon(R.drawable.delete_night);
            } else {
                deleteMenuItem.setIcon(R.drawable.delete_day);
            }
        } else if (twoPanedMode) {  // Two-paned mode is enabled but there are no domains.
            // Disable the options `MenuItems`.
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.setIcon(R.drawable.delete_disabled);
        }
    }

    @Override
    public void dismissSnackbar() {
        // Dismiss the undo delete snackbar if it is shown.
        if (undoDeleteSnackbar != null && undoDeleteSnackbar.isShown()) {
            // Dismiss the snackbar.
            undoDeleteSnackbar.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        // Close the domains database helper.
        domainsDatabaseHelper.close();

        // Run the default commands.
        super.onDestroy();
    }
}