

package com.jdots.browser.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jdots.browser.BuildConfig;
import com.jdots.browser.R;
import com.jdots.browser.adapters.WebViewPagerAdapter;
import com.jdots.browser.asynctasks.GetHostIpAddresses;
import com.jdots.browser.asynctasks.PopulateBlocklists;
import com.jdots.browser.asynctasks.PrepareSaveDialog;
import com.jdots.browser.asynctasks.SaveUrl;
import com.jdots.browser.asynctasks.SaveWebpageImage;
import com.jdots.browser.dialogs.AdConsentDialog;
import com.jdots.browser.dialogs.CreateBookmarkDialog;
import com.jdots.browser.dialogs.CreateBookmarkFolderDialog;
import com.jdots.browser.dialogs.CreateHomeScreenShortcutDialog;
import com.jdots.browser.dialogs.EditBookmarkFolderDialog;
import com.jdots.browser.dialogs.FontSizeDialog;
import com.jdots.browser.dialogs.HttpAuthenticationDialog;
import com.jdots.browser.dialogs.OpenDialog;
import com.jdots.browser.dialogs.PinnedMismatchDialog;
import com.jdots.browser.dialogs.ProxyNotInstalledDialog;
import com.jdots.browser.dialogs.SaveWebpageDialog;
import com.jdots.browser.dialogs.SslCertificateErrorDialog;
import com.jdots.browser.dialogs.StoragePermissionDialog;
import com.jdots.browser.dialogs.UrlHistoryDialog;
import com.jdots.browser.dialogs.ViewSslCertificateDialog;
import com.jdots.browser.dialogs.WaitingForProxyDialog;
import com.jdots.browser.fragments.WebViewTabFragment;
import com.jdots.browser.helpers.AdHelper;
import com.jdots.browser.helpers.BlocklistHelper;
import com.jdots.browser.helpers.BookmarksDatabaseHelper;
import com.jdots.browser.helpers.DomainsDatabaseHelper;
import com.jdots.browser.helpers.FileNameHelper;
import com.jdots.browser.helpers.ProxyHelper;
import com.jdots.browser.views.NestedScrollWebView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

public class MainWebViewActivity extends AppCompatActivity implements CreateBookmarkDialog.CreateBookmarkListener, CreateBookmarkFolderDialog.CreateBookmarkFolderListener,
        EditBookmarkFolderDialog.EditBookmarkFolderListener, FontSizeDialog.UpdateFontSizeListener, NavigationView.OnNavigationItemSelectedListener, OpenDialog.OpenListener,
        PinnedMismatchDialog.PinnedMismatchListener, PopulateBlocklists.PopulateBlocklistsListener, SaveWebpageDialog.SaveWebpageListener, StoragePermissionDialog.StoragePermissionDialogListener,
        UrlHistoryDialog.NavigateHistoryListener, WebViewTabFragment.NewTabListener {

    // The user agent constants are public static so they can be accessed from `SettingsFragment`, `DomainsActivity`, and `DomainSettingsFragment`.
    public final static int UNRECOGNIZED_USER_AGENT = -1;
    public final static int SETTINGS_WEBVIEW_DEFAULT_USER_AGENT = 1;
    public final static int SETTINGS_CUSTOM_USER_AGENT = 12;
    public final static int DOMAINS_SYSTEM_DEFAULT_USER_AGENT = 0;
    public final static int DOMAINS_WEBVIEW_DEFAULT_USER_AGENT = 2;
    public final static int DOMAINS_CUSTOM_USER_AGENT = 13;
    // Start activity for result request codes.  The public static entries are accessed from `OpenDialog()` and `SaveWebpageDialog()`.
    public final static int BROWSE_OPEN_REQUEST_CODE = 0;
    public final static int BROWSE_SAVE_WEBPAGE_REQUEST_CODE = 1;
    // The executor service handles background tasks.  It is accessed from `ViewSourceActivity`.
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);
    // `orbotStatus` is public static so it can be accessed from `OrbotProxyHelper`.  It is also used in `onCreate()`, `onResume()`, and `applyProxy()`.
    public static String orbotStatus = "unknown";
    // The WebView pager adapter is accessed from `HttpAuthenticationDialog`, `PinnedMismatchDialog`, and `SslCertificateErrorDialog`.  It is also used in `onCreate()`, `onResume()`, and `addTab()`.
    public static WebViewPagerAdapter webViewPagerAdapter;
    // `restartFromBookmarksActivity` is public static so it can be accessed from `BookmarksActivity`.  It is also used in `onRestart()`.
    public static boolean restartFromBookmarksActivity;
    // `currentBookmarksFolder` is public static so it can be accessed from `BookmarksActivity`.  It is also used in `onCreate()`, `onBackPressed()`, `onCreateBookmark()`, `onCreateBookmarkFolder()`,
    // `onSaveEditBookmark()`, `onSaveEditBookmarkFolder()`, and `loadBookmarksFolder()`.
    public static String currentBookmarksFolder;
    // The proxy mode is public static so it can be accessed from `ProxyHelper()`.
    // It is also used in `onRestart()`, `onPrepareOptionsMenu()`, `onOptionsItemSelected()`, `applyAppSettings()`, and `applyProxy()`.
    // It will be updated in `applyAppSettings()`, but it needs to be initialized here or the first run of `onPrepareOptionsMenu()` crashes.
    public static String proxyMode = ProxyHelper.NONE;
    private final int BROWSE_FILE_UPLOAD_REQUEST_CODE = 2;
    // Define the saved instance state constants.
    private final String SAVED_STATE_ARRAY_LIST = "saved_state_array_list";
    private final String SAVED_NESTED_SCROLL_WEBVIEW_STATE_ARRAY_LIST = "saved_nested_scroll_webview_state_array_list";
    private final String SAVED_TAB_POSITION = "saved_tab_position";
    private final String PROXY_MODE = "proxy_mode";
    // `customHeader` is used in `onCreate()`, `onOptionsItemSelected()`, `onCreateContextMenu()`, and `loadUrl()`.
    private final Map<String, String> customHeaders = new HashMap<>();
    // Define the class variables.
    @SuppressWarnings("rawtypes")
    AsyncTask populateBlocklists;
    // Define the saved instance state variables.
    private ArrayList<Bundle> savedStateArrayList;
    private ArrayList<Bundle> savedNestedScrollWebViewStateArrayList;
    private int savedTabPosition;
    private String savedProxyMode;
    // The current WebView is used in `onCreate()`, `onPrepareOptionsMenu()`, `onOptionsItemSelected()`, `onNavigationItemSelected()`, `onRestart()`, `onCreateContextMenu()`, `findPreviousOnPage()`,
    // `findNextOnPage()`, `closeFindOnPage()`, `loadUrlFromTextBox()`, `onSslMismatchBack()`, `applyProxy()`, and `applyDomainSettings()`.
    private NestedScrollWebView currentWebView;
    // The search URL is set in `applyAppSettings()` and used in `onNewIntent()`, `loadUrlFromTextBox()`, `initializeApp()`, and `initializeWebView()`.
    private String searchURL;

    // The options menu is set in `onCreateOptionsMenu()` and used in `onOptionsItemSelected()`, `updatePrivacyIcons()`, and `initializeWebView()`.
    private Menu optionsMenu;

    // The blocklists are populated in `finishedPopulatingBlocklists()` and accessed from `initializeWebView()`.
    private ArrayList<List<String[]>> easyList;
    private ArrayList<List<String[]>> easyPrivacy;
    private ArrayList<List<String[]>> fanboysAnnoyanceList;
    private ArrayList<List<String[]>> fanboysSocialList;
    private ArrayList<List<String[]>> ultraList;
    private ArrayList<List<String[]>> ultraPrivacy;

    // `webViewDefaultUserAgent` is used in `onCreate()` and `onPrepareOptionsMenu()`.
    private String webViewDefaultUserAgent;

    // The incognito mode is set in `applyAppSettings()` and used in `initializeWebView()`.
    private boolean incognitoModeEnabled;

    // The full screen browsing mode tracker is set it `applyAppSettings()` and used in `initializeWebView()`.
    private boolean fullScreenBrowsingModeEnabled;

    // `inFullScreenBrowsingMode` is used in `onCreate()`, `onConfigurationChanged()`, and `applyAppSettings()`.
    private boolean inFullScreenBrowsingMode;

    // The app bar trackers are set in `applyAppSettings()` and used in `initializeWebView()`.
    private boolean hideAppBar;
    private boolean scrollAppBar;

    // The loading new intent tracker is set in `onNewIntent()` and used in `setCurrentWebView()`.
    private boolean loadingNewIntent;

    // `reapplyDomainSettingsOnRestart` is used in `onCreate()`, `onOptionsItemSelected()`, `onNavigationItemSelected()`, `onRestart()`, and `onAddDomain()`, .
    private boolean reapplyDomainSettingsOnRestart;

    // `reapplyAppSettingsOnRestart` is used in `onNavigationItemSelected()` and `onRestart()`.
    private boolean reapplyAppSettingsOnRestart;

    // `displayingFullScreenVideo` is used in `onCreate()` and `onResume()`.
    private boolean displayingFullScreenVideo;

    // `orbotStatusBroadcastReceiver` is used in `onCreate()` and `onDestroy()`.
    private BroadcastReceiver orbotStatusBroadcastReceiver;

    // The waiting for proxy boolean is used in `onResume()`, `initializeApp()` and `applyProxy()`.
    private boolean waitingForProxy = false;

    // The action bar drawer toggle is initialized in `onCreate()` and used in `onResume()`.
    private ActionBarDrawerToggle actionBarDrawerToggle;

    // The color spans are used in `onCreate()` and `highlightUrlText()`.
    private ForegroundColorSpan redColorSpan;
    private ForegroundColorSpan initialGrayColorSpan;
    private ForegroundColorSpan finalGrayColorSpan;

    // `bookmarksDatabaseHelper` is used in `onCreate()`, `onDestroy`, `onOptionsItemSelected()`, `onCreateBookmark()`, `onCreateBookmarkFolder()`, `onSaveEditBookmark()`, `onSaveEditBookmarkFolder()`,
    // and `loadBookmarksFolder()`.
    private BookmarksDatabaseHelper bookmarksDatabaseHelper;

    // `bookmarksCursor` is used in `onDestroy()`, `onOptionsItemSelected()`, `onCreateBookmark()`, `onCreateBookmarkFolder()`, `onSaveEditBookmark()`, `onSaveEditBookmarkFolder()`, and `loadBookmarksFolder()`.
    private Cursor bookmarksCursor;

    // `bookmarksCursorAdapter` is used in `onCreateBookmark()`, `onCreateBookmarkFolder()` `onSaveEditBookmark()`, `onSaveEditBookmarkFolder()`, and `loadBookmarksFolder()`.
    private CursorAdapter bookmarksCursorAdapter;

    // `oldFolderNameString` is used in `onCreate()` and `onSaveEditBookmarkFolder()`.
    private String oldFolderNameString;

    // `fileChooserCallback` is used in `onCreate()` and `onActivityResult()`.
    private ValueCallback<Uri[]> fileChooserCallback;

    // The default progress view offsets are set in `onCreate()` and used in `initializeWebView()`.
    private int appBarHeight;
    private int defaultProgressViewStartOffset;
    private int defaultProgressViewEndOffset;

    // The URL sanitizers are set in `applyAppSettings()` and used in `sanitizeUrl()`.
    private boolean sanitizeGoogleAnalytics;
    private boolean sanitizeFacebookClickIds;
    private boolean sanitizeTwitterAmpRedirects;

    // The file path strings are used in `onSaveWebpage()` and `onRequestPermissionResult()`
    private String openFilePath;
    private String saveWebpageUrl;
    private String saveWebpageFilePath;

    // Declare the class views.
    private DrawerLayout drawerLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private LinearLayout findOnPageLinearLayout;
    private LinearLayout tabsLinearLayout;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager webViewPager;
    private NavigationView navigationView;

    // Declare the class menus.
    private Menu navigationMenu;

    // Declare the class menu items.
    private MenuItem navigationRequestsMenuItem;

    @Override
    // Remove the warning about needing to override `performClick()` when using an `OnTouchListener` with `WebView`.
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onCreate(savedInstanceState);

        // Check to see if the activity has been restarted.
        if (savedInstanceState != null) {
            // Store the saved instance state variables.
            savedStateArrayList = savedInstanceState.getParcelableArrayList(SAVED_STATE_ARRAY_LIST);
            savedNestedScrollWebViewStateArrayList = savedInstanceState.getParcelableArrayList(SAVED_NESTED_SCROLL_WEBVIEW_STATE_ARRAY_LIST);
            savedTabPosition = savedInstanceState.getInt(SAVED_TAB_POSITION);
            savedProxyMode = savedInstanceState.getString(PROXY_MODE);
        }

        // Initialize the default preference values the first time the program is run.  `false` keeps this command from resetting any current preferences back to default.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the screenshot preference.
        String appTheme = sharedPreferences.getString("app_theme", getString(R.string.app_theme_default_value));
        boolean allowScreenshots = sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false);

        // Get the theme entry values string array.
        String[] appThemeEntryValuesStringArray = getResources().getStringArray(R.array.app_theme_entry_values);

        // Set the app theme according to the preference.  A switch statement cannot be used because the theme entry values string array is not a compile time constant.
        if (appTheme.equals(appThemeEntryValuesStringArray[1])) {  // The light theme is selected.
            // Apply the light theme.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (appTheme.equals(appThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
            // Apply the dark theme.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {  // The system default theme is selected.
            if (Build.VERSION.SDK_INT >= 28) {  // The system default theme is supported.
                // Follow the system default theme.
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {  // The system default theme is not supported.
                // Follow the battery saver mode.
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            }
        }

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Enable the drawing of the entire webpage.  This makes it possible to save a website image.  This must be done before anything else happens with the WebView.
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
        }

        // Set the theme.
        setTheme(R.style.ClearBrowser);

        // Set the content view.
        setContentView(R.layout.main_framelayout);

        // Get handles for the views.
        drawerLayout = findViewById(R.id.drawerlayout);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.toolbar);
        findOnPageLinearLayout = findViewById(R.id.find_on_page_linearlayout);
        tabsLinearLayout = findViewById(R.id.tabs_linearlayout);
        tabLayout = findViewById(R.id.tablayout);
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        webViewPager = findViewById(R.id.webviewpager);
        navigationView = findViewById(R.id.navigationview);

        // Get handles for the class menus.
        navigationMenu = navigationView.getMenu();

        // Get a handle for the navigation requests menu item.
        navigationRequestsMenuItem = navigationMenu.findItem(R.id.requests);

        // Get a handle for the app compat delegate.
        AppCompatDelegate appCompatDelegate = getDelegate();

        // Set the support action bar.
        appCompatDelegate.setSupportActionBar(toolbar);

        // Get a handle for the action bar.
        ActionBar actionBar = appCompatDelegate.getSupportActionBar();

        // This is needed to get rid of the Android Studio warning that the action bar might be null.
        assert actionBar != null;

        // Add the custom layout, which shows the URL text bar.
        actionBar.setCustomView(R.layout.url_app_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Create the hamburger icon at the start of the AppBar.
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);

        // Initially disable the sliding drawers.  They will be enabled once the blocklists are loaded.
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // Initialize the web view pager adapter.
        webViewPagerAdapter = new WebViewPagerAdapter(getSupportFragmentManager());

        // Set the pager adapter on the web view pager.
        webViewPager.setAdapter(webViewPagerAdapter);

        // Store up to 100 tabs in memory.
        webViewPager.setOffscreenPageLimit(100);

        // Initialize the app.
        initializeApp();

        // Apply the app settings from the shared preferences.
        applyAppSettings();

        // Populate the blocklists.
        populateBlocklists = new PopulateBlocklists(this, this).execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Run the default commands.
        super.onNewIntent(intent);

        // Replace the intent that started the app with this one.
        setIntent(intent);

        // Check to see if the app is being restarted from a saved state.
        if (savedStateArrayList == null || savedStateArrayList.size() == 0) {  // The activity is not being restarted from a saved state.
            // Get the information from the intent.
            String intentAction = intent.getAction();
            Uri intentUriData = intent.getData();
            String intentStringExtra = intent.getStringExtra(Intent.EXTRA_TEXT);

            // Determine if this is a web search.
            boolean isWebSearch = ((intentAction != null) && intentAction.equals(Intent.ACTION_WEB_SEARCH));

            // Only process the URI if it contains data or it is a web search.  If the user pressed the desktop icon after the app was already running the URI will be null.
            if (intentUriData != null || intentStringExtra != null || isWebSearch) {
                // Get the shared preferences.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                // Create a URL string.
                String url;

                // If the intent action is a web search, perform the search.
                if (isWebSearch) {  // The intent is a web search.
                    // Create an encoded URL string.
                    String encodedUrlString;

                    // Sanitize the search input and convert it to a search.
                    try {
                        encodedUrlString = URLEncoder.encode(intent.getStringExtra(SearchManager.QUERY), "UTF-8");
                    } catch (UnsupportedEncodingException exception) {
                        encodedUrlString = "";
                    }

                    // Add the base search URL.
                    url = searchURL + encodedUrlString;
                } else if (intentUriData != null) {  // The intent contains a URL formatted as a URI.
                    // Set the intent data as the URL.
                    url = intentUriData.toString();
                } else {  // The intent contains a string, which might be a URL.
                    // Set the intent string as the URL.
                    url = intentStringExtra;
                }

                // Add a new tab if specified in the preferences.
                if (sharedPreferences.getBoolean("open_intents_in_new_tab", true)) {  // Load the URL in a new tab.
                    // Set the loading new intent flag.
                    loadingNewIntent = true;

                    // Add a new tab.
                    addNewTab(url, true);
                } else {  // Load the URL in the current tab.
                    // Make it so.
                    loadUrl(currentWebView, url);
                }

                // Close the navigation drawer if it is open.
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                // Close the bookmarks drawer if it is open.
                if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        }
    }

    @Override
    public void onRestart() {
        // Run the default commands.
        super.onRestart();

        // Apply the app settings if returning from the Settings activity.
        if (reapplyAppSettingsOnRestart) {
            // Reset the reapply app settings on restart tracker.
            reapplyAppSettingsOnRestart = false;

            // Apply the app settings.
            applyAppSettings();
        }

        // Apply the domain settings if returning from the settings or domains activity.
        if (reapplyDomainSettingsOnRestart) {
            // Reset the reapply domain settings on restart tracker.
            reapplyDomainSettingsOnRestart = false;

            // Reapply the domain settings for each tab.
            for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
                // Get the WebView tab fragment.
                WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

                // Get the fragment view.
                View fragmentView = webViewTabFragment.getView();

                // Only reload the WebViews if they exist.
                if (fragmentView != null) {
                    // Get the nested scroll WebView from the tab fragment.
                    NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                    // Reset the current domain name so the domain settings will be reapplied.
                    nestedScrollWebView.resetCurrentDomainName();

                    // Reapply the domain settings if the URL is not null, which can happen if an empty tab is active when returning from settings.
                    if (nestedScrollWebView.getUrl() != null) {
                        applyDomainSettings(nestedScrollWebView, nestedScrollWebView.getUrl(), false, true, false);
                    }
                }
            }
        }

        // Update the bookmarks drawer if returning from the Bookmarks activity.
        if (restartFromBookmarksActivity) {
            // Close the bookmarks drawer.
            drawerLayout.closeDrawer(GravityCompat.END);

            // Reload the bookmarks drawer.
            loadBookmarksFolder();

            // Reset `restartFromBookmarksActivity`.
            restartFromBookmarksActivity = false;
        }

        // Update the privacy icon.  `true` runs `invalidateOptionsMenu` as the last step.  This can be important if the screen was rotated.
        updatePrivacyIcons(true);
    }

    // `onResume()` runs after `onStart()`, which runs after `onCreate()` and `onRestart()`.
    @Override
    public void onResume() {
        // Run the default commands.
        super.onResume();

        // Resume any WebViews.
        for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
            // Get the WebView tab fragment.
            WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

            // Get the fragment view.
            View fragmentView = webViewTabFragment.getView();

            // Only resume the WebViews if they exist (they won't when the app is first created).
            if (fragmentView != null) {
                // Get the nested scroll WebView from the tab fragment.
                NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                // Resume the nested scroll WebView.
                nestedScrollWebView.onResume();
            }
        }

        // Resume the nested scroll WebView JavaScript timers.  This is a global command that resumes JavaScript timers on all WebViews.
        if (currentWebView != null) {
            currentWebView.resumeTimers();
        }

        // Reapply the proxy settings if the system is using a proxy.  This redisplays the appropriate alert dialog.
        if (!proxyMode.equals(ProxyHelper.NONE)) {
            applyProxy(false);
        }

        // Reapply any system UI flags and the ad in the free flavor.
        if (displayingFullScreenVideo || inFullScreenBrowsingMode) {  // The system is displaying a website or a video in full screen mode.
            // Get a handle for the root frame layouts.
            FrameLayout rootFrameLayout = findViewById(R.id.root_framelayout);

            /* Hide the system bars.
             * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
             * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
             * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
             * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
             */
            rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (BuildConfig.FLAVOR.contentEquals("free")) {  // The system in not in full screen mode.
            // Resume the ad.
            AdHelper.resumeAd(findViewById(R.id.adview));
        }
    }

    @Override
    public void onPause() {
        // Run the default commands.
        super.onPause();

        for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
            // Get the WebView tab fragment.
            WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

            // Get the fragment view.
            View fragmentView = webViewTabFragment.getView();

            // Only pause the WebViews if they exist (they won't when the app is first created).
            if (fragmentView != null) {
                // Get the nested scroll WebView from the tab fragment.
                NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                // Pause the nested scroll WebView.
                nestedScrollWebView.onPause();
            }
        }

        // Pause the WebView JavaScript timers.  This is a global command that pauses JavaScript on all WebViews.
        if (currentWebView != null) {
            currentWebView.pauseTimers();
        }

        // Pause the ad or it will continue to consume resources in the background on the free flavor.
        if (BuildConfig.FLAVOR.contentEquals("free")) {
            // Pause the ad.
            AdHelper.pauseAd(findViewById(R.id.adview));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Run the default commands.
        super.onSaveInstanceState(savedInstanceState);

        // Create the saved state array lists.
        ArrayList<Bundle> savedStateArrayList = new ArrayList<>();
        ArrayList<Bundle> savedNestedScrollWebViewStateArrayList = new ArrayList<>();

        // Get the URLs from each tab.
        for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
            // Get the WebView tab fragment.
            WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

            // Get the fragment view.
            View fragmentView = webViewTabFragment.getView();

            if (fragmentView != null) {
                // Get the nested scroll WebView from the tab fragment.
                NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                // Create saved state bundle.
                Bundle savedStateBundle = new Bundle();

                // Get the current states.
                nestedScrollWebView.saveState(savedStateBundle);
                Bundle savedNestedScrollWebViewStateBundle = nestedScrollWebView.saveNestedScrollWebViewState();

                // Store the saved states in the array lists.
                savedStateArrayList.add(savedStateBundle);
                savedNestedScrollWebViewStateArrayList.add(savedNestedScrollWebViewStateBundle);
            }
        }

        // Get the current tab position.
        int currentTabPosition = tabLayout.getSelectedTabPosition();

        // Store the saved states in the bundle.
        savedInstanceState.putParcelableArrayList(SAVED_STATE_ARRAY_LIST, savedStateArrayList);
        savedInstanceState.putParcelableArrayList(SAVED_NESTED_SCROLL_WEBVIEW_STATE_ARRAY_LIST, savedNestedScrollWebViewStateArrayList);
        savedInstanceState.putInt(SAVED_TAB_POSITION, currentTabPosition);
        savedInstanceState.putString(PROXY_MODE, proxyMode);
    }

    @Override
    public void onDestroy() {
        // Unregister the orbot status broadcast receiver if it exists.
        if (orbotStatusBroadcastReceiver != null) {
            this.unregisterReceiver(orbotStatusBroadcastReceiver);
        }

        // Close the bookmarks cursor if it exists.
        if (bookmarksCursor != null) {
            bookmarksCursor.close();
        }

        // Close the bookmarks database if it exists.
        if (bookmarksDatabaseHelper != null) {
            bookmarksDatabaseHelper.close();
        }

        // Stop populating the blocklists if the AsyncTask is running in the background.
        if (populateBlocklists != null) {
            populateBlocklists.cancel(true);
        }

        // Run the default commands.
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.  This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.webview_options_menu, menu);

        // Store a handle for the options menu so it can be used by `onOptionsItemSelected()` and `updatePrivacyIcons()`.
        optionsMenu = menu;

        // Set the initial status of the privacy icons.  `false` does not call `invalidateOptionsMenu` as the last step.
        updatePrivacyIcons(false);

        // Get handles for the menu items.
        MenuItem bookmarksMenuItem = menu.findItem(R.id.bookmarks);
        //  MenuItem toggleFirstPartyCookiesMenuItem = menu.findItem(R.id.toggle_first_party_cookies);
        //  MenuItem toggleThirdPartyCookiesMenuItem = menu.findItem(R.id.toggle_third_party_cookies);
        //  MenuItem toggleSaveFormDataMenuItem = menu.findItem(R.id.toggle_save_form_data);  // Form data can be removed once the minimum API >= 26.
        MenuItem clearFormDataMenuItem = menu.findItem(R.id.clear_form_data);  // Form data can be removed once the minimum API >= 26.
        MenuItem refreshMenuItem = menu.findItem(R.id.refresh);
        MenuItem darkWebViewMenuItem = menu.findItem(R.id.dark_webview);
        MenuItem adConsentMenuItem = menu.findItem(R.id.ad_consent);

        // Only display third-party cookies if API >= 21
        //  toggleThirdPartyCookiesMenuItem.setVisible(Build.VERSION.SDK_INT >= 21);

        // Only display the form data menu items if the API < 26.
        //   toggleSaveFormDataMenuItem.setVisible(Build.VERSION.SDK_INT < 26);
        clearFormDataMenuItem.setVisible(Build.VERSION.SDK_INT < 26);

        // Disable the clear form data menu item if the API >= 26 so that the status of the main Clear Data is calculated correctly.
        clearFormDataMenuItem.setEnabled(Build.VERSION.SDK_INT < 26);

        // Only display the dark WebView menu item if API >= 21.
        darkWebViewMenuItem.setVisible(Build.VERSION.SDK_INT >= 21);

        // Only show Ad Consent if this is the free flavor.
        adConsentMenuItem.setVisible(BuildConfig.FLAVOR.contentEquals("free"));

        // Get the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the dark theme and app bar preferences.
        boolean displayAdditionalAppBarIcons = sharedPreferences.getBoolean(getString(R.string.display_additional_app_bar_icons_key), false);

        // Set the status of the additional app bar icons.  Setting the refresh menu item to `SHOW_AS_ACTION_ALWAYS` makes it appear even on small devices like phones.
        if (displayAdditionalAppBarIcons) {
            refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            bookmarksMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            // toggleFirstPartyCookiesMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else { //Do not display the additional icons.
            refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            bookmarksMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            //   toggleFirstPartyCookiesMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        // Replace Refresh with Stop if a URL is already loading.
        if (currentWebView != null && currentWebView.getProgress() != 100) {
            // Set the title.
            refreshMenuItem.setTitle(R.string.stop);

            // Set the icon if it is displayed in the app bar.
            if (displayAdditionalAppBarIcons) {
                // Get the current theme status.
                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                // Set the icon according to the current theme status.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    refreshMenuItem.setIcon(R.drawable.close_blue_day);
                } else {
                    refreshMenuItem.setIcon(R.drawable.close_blue_night);
                }
            }
        }

        // Done.
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get handles for the menu items.
        MenuItem addOrEditDomain = menu.findItem(R.id.add_or_edit_domain);
        //  MenuItem firstPartyCookiesMenuItem = menu.findItem(R.id.toggle_first_party_cookies);
        // MenuItem thirdPartyCookiesMenuItem = menu.findItem(R.id.toggle_third_party_cookies);
        //  MenuItem domStorageMenuItem = menu.findItem(R.id.toggle_dom_storage);
        //  MenuItem saveFormDataMenuItem = menu.findItem(R.id.toggle_save_form_data);  // Form data can be removed once the minimum API >= 26.
        //    MenuItem clearDataMenuItem = menu.findItem(R.id.clear_data);
        //   MenuItem clearCookiesMenuItem = menu.findItem(R.id.clear_cookies);
        //     MenuItem clearDOMStorageMenuItem = menu.findItem(R.id.clear_dom_storage);
        //    MenuItem clearFormDataMenuItem = menu.findItem(R.id.clear_form_data);  // Form data can be removed once the minimum API >= 26.
        //   MenuItem blocklistsMenuItem = menu.findItem(R.id.blocklists);
        //   MenuItem easyListMenuItem = menu.findItem(R.id.easylist);
        //   MenuItem easyPrivacyMenuItem = menu.findItem(R.id.easyprivacy);
        //  MenuItem fanboysAnnoyanceListMenuItem = menu.findItem(R.id.fanboys_annoyance_list);
        // MenuItem fanboysSocialBlockingListMenuItem = menu.findItem(R.id.fanboys_social_blocking_list);
        //  MenuItem ultraListMenuItem = menu.findItem(R.id.ultralist);
        //  MenuItem ultraPrivacyMenuItem = menu.findItem(R.id.ultraprivacy);
        // MenuItem blockAllThirdPartyRequestsMenuItem = menu.findItem(R.id.block_all_third_party_requests);
        MenuItem proxyMenuItem = menu.findItem(R.id.proxy);
        MenuItem userAgentMenuItem = menu.findItem(R.id.user_agent);
        MenuItem fontSizeMenuItem = menu.findItem(R.id.font_size);
        MenuItem swipeToRefreshMenuItem = menu.findItem(R.id.swipe_to_refresh);
        MenuItem wideViewportMenuItem = menu.findItem(R.id.wide_viewport);
        MenuItem displayImagesMenuItem = menu.findItem(R.id.display_images);
        MenuItem darkWebViewMenuItem = menu.findItem(R.id.dark_webview);

        // Get a handle for the cookie manager.
        CookieManager cookieManager = CookieManager.getInstance();

        // Initialize the current user agent string and the font size.
        String currentUserAgent = getString(R.string.user_agent_application_name);
        int fontSize = 100;

        // Set items that require the current web view to be populated.  It will be null when the program is first opened, as `onPrepareOptionsMenu()` is called before the first WebView is initialized.
        if (currentWebView != null) {
            // Set the add or edit domain text.
            if (currentWebView.getDomainSettingsApplied()) {
                addOrEditDomain.setTitle(R.string.edit_domain_settings);
            } else {
                addOrEditDomain.setTitle(R.string.add_domain_settings);
            }

            // Get the current user agent from the WebView.
            currentUserAgent = currentWebView.getSettings().getUserAgentString();

            // Get the current font size from the
            fontSize = currentWebView.getSettings().getTextZoom();

            // Set the status of the menu item checkboxes.
            //  domStorageMenuItem.setChecked(currentWebView.getSettings().getDomStorageEnabled());
            //  saveFormDataMenuItem.setChecked(currentWebView.getSettings().getSaveFormData());  // Form data can be removed once the minimum API >= 26.
//            easyListMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYLIST));
            //           easyPrivacyMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYPRIVACY));
            // fanboysAnnoyanceListMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST));
            //  fanboysSocialBlockingListMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST));
            //          ultraListMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRALIST));
            //         ultraPrivacyMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRAPRIVACY));
            //  blockAllThirdPartyRequestsMenuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.THIRD_PARTY_REQUESTS));
            swipeToRefreshMenuItem.setChecked(currentWebView.getSwipeToRefresh());
            wideViewportMenuItem.setChecked(currentWebView.getSettings().getUseWideViewPort());
            displayImagesMenuItem.setChecked(currentWebView.getSettings().getLoadsImagesAutomatically());

            // Initialize the display names for the blocklists with the number of blocked requests.
            //  blocklistsMenuItem.setTitle(getString(R.string.blocklists) + " - " + currentWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
            //    easyListMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.EASYLIST) + " - " + getString(R.string.easylist));
            //   easyPrivacyMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.EASYPRIVACY) + " - " + getString(R.string.easyprivacy));
            //  fanboysAnnoyanceListMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST) + " - " + getString(R.string.fanboys_annoyance_list));
            //  fanboysSocialBlockingListMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST) + " - " + getString(R.string.fanboys_social_blocking_list));
            //   ultraListMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.ULTRALIST) + " - " + getString(R.string.ultralist));
            //  ultraPrivacyMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.ULTRAPRIVACY) + " - " + getString(R.string.ultraprivacy));
            //   blockAllThirdPartyRequestsMenuItem.setTitle(currentWebView.getRequestsCount(NestedScrollWebView.THIRD_PARTY_REQUESTS) + " - " + getString(R.string.block_all_third_party_requests));

            // Only modify third-party cookies if the API >= 21.
            if (Build.VERSION.SDK_INT >= 21) {
                // Set the status of the third-party cookies checkbox.
                //    thirdPartyCookiesMenuItem.setChecked(cookieManager.acceptThirdPartyCookies(currentWebView));

                // Enable third-party cookies if first-party cookies are enabled.
                //    thirdPartyCookiesMenuItem.setEnabled(cookieManager.acceptCookie());
            }

            // Enable DOM Storage if JavaScript is enabled.
            //  domStorageMenuItem.setEnabled(currentWebView.getSettings().getJavaScriptEnabled());

            // Set the checkbox status for dark WebView if the WebView supports it.
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                darkWebViewMenuItem.setChecked(WebSettingsCompat.getForceDark(currentWebView.getSettings()) == WebSettingsCompat.FORCE_DARK_ON);
            }
        }

        // Set the checked status of the first party cookies menu item.
        // firstPartyCookiesMenuItem.setChecked(cookieManager.acceptCookie());

        // Enable Clear Cookies if there are any.
        // clearCookiesMenuItem.setEnabled(cookieManager.hasCookies());

        // Get the application's private data directory, which will be something like `/data/user/0/com.jdots.browser.standard`, which links to `/data/data/com.jdots.browser.standard`.
        String privateDataDirectoryString = getApplicationInfo().dataDir;

        // Get a count of the number of files in the Local Storage directory.
        File localStorageDirectory = new File(privateDataDirectoryString + "/app_webview/Local Storage/");
        int localStorageDirectoryNumberOfFiles = 0;
        if (localStorageDirectory.exists()) {
            // `Objects.requireNonNull` removes a lint warning that `localStorageDirectory.list` might produce a null pointed exception if it is dereferenced.
            localStorageDirectoryNumberOfFiles = Objects.requireNonNull(localStorageDirectory.list()).length;
        }

        // Get a count of the number of files in the IndexedDB directory.
        File indexedDBDirectory = new File(privateDataDirectoryString + "/app_webview/IndexedDB");
        int indexedDBDirectoryNumberOfFiles = 0;
        if (indexedDBDirectory.exists()) {
            // `Objects.requireNonNull` removes a lint warning that `indexedDBDirectory.list` might produce a null pointed exception if it is dereferenced.
            indexedDBDirectoryNumberOfFiles = Objects.requireNonNull(indexedDBDirectory.list()).length;
        }

        // Enable Clear DOM Storage if there is any.
        //clearDOMStorageMenuItem.setEnabled(localStorageDirectoryNumberOfFiles > 0 || indexedDBDirectoryNumberOfFiles > 0);

        // Enable Clear Form Data is there is any.  This can be removed once the minimum API >= 26.
        if (Build.VERSION.SDK_INT < 26) {
            // Get the WebView database.
            WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(this);

            // Enable the clear form data menu item if there is anything to clear.
            //   clearFormDataMenuItem.setEnabled(webViewDatabase.hasFormData());
        }

        // Enable Clear Data if any of the submenu items are enabled.
        // clearDataMenuItem.setEnabled(clearCookiesMenuItem.isEnabled() || clearDOMStorageMenuItem.isEnabled() || clearFormDataMenuItem.isEnabled());

        // Disable Fanboy's Social Blocking List menu item if Fanboy's Annoyance List is checked.
        // fanboysSocialBlockingListMenuItem.setEnabled(!fanboysAnnoyanceListMenuItem.isChecked());

        // Set the proxy title and check the applied proxy.
        switch (proxyMode) {
            case ProxyHelper.NONE:
                // Set the proxy title.
                proxyMenuItem.setTitle(getString(R.string.proxy) + " - " + getString(R.string.proxy_none));

                // Check the proxy None radio button.
                menu.findItem(R.id.proxy_none).setChecked(true);
                break;

            case ProxyHelper.TOR:
                // Set the proxy title.
                proxyMenuItem.setTitle(getString(R.string.proxy) + " - " + getString(R.string.proxy_tor));

                // Check the proxy Tor radio button.
                menu.findItem(R.id.proxy_tor).setChecked(true);
                break;

            case ProxyHelper.I2P:
                // Set the proxy title.
                proxyMenuItem.setTitle(getString(R.string.proxy) + " - " + getString(R.string.proxy_i2p));

                // Check the proxy I2P radio button.
                menu.findItem(R.id.proxy_i2p).setChecked(true);
                break;

            case ProxyHelper.CUSTOM:
                // Set the proxy title.
                proxyMenuItem.setTitle(getString(R.string.proxy) + " - " + getString(R.string.proxy_custom));

                // Check the proxy Custom radio button.
                menu.findItem(R.id.proxy_custom).setChecked(true);
                break;
        }

        // Select the current user agent menu item.  A switch statement cannot be used because the user agents are not compile time constants.
        if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[0])) {  // Clear Browser.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_application_name));

            // Select the Clear Browser radio box.
            menu.findItem(R.id.user_agent_application_name).setChecked(true);
        } else if (currentUserAgent.equals(webViewDefaultUserAgent)) {  // WebView Default.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_webview_default));

            // Select the WebView Default radio box.
            menu.findItem(R.id.user_agent_webview_default).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[2])) {  // Firefox on Android.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_firefox_on_android));

            // Select the Firefox on Android radio box.
            menu.findItem(R.id.user_agent_firefox_on_android).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[3])) {  // Chrome on Android.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_chrome_on_android));

            // Select the Chrome on Android radio box.
            menu.findItem(R.id.user_agent_chrome_on_android).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[4])) {  // Safari on iOS.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_safari_on_ios));

            // Select the Safari on iOS radio box.
            menu.findItem(R.id.user_agent_safari_on_ios).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[5])) {  // Firefox on Linux.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_firefox_on_linux));

            // Select the Firefox on Linux radio box.
            menu.findItem(R.id.user_agent_firefox_on_linux).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[6])) {  // Chromium on Linux.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_chromium_on_linux));

            // Select the Chromium on Linux radio box.
            menu.findItem(R.id.user_agent_chromium_on_linux).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[7])) {  // Firefox on Windows.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_firefox_on_windows));

            // Select the Firefox on Windows radio box.
            menu.findItem(R.id.user_agent_firefox_on_windows).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[8])) {  // Chrome on Windows.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_chrome_on_windows));

            // Select the Chrome on Windows radio box.
            menu.findItem(R.id.user_agent_chrome_on_windows).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[9])) {  // Edge on Windows.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_edge_on_windows));

            // Select the Edge on Windows radio box.
            menu.findItem(R.id.user_agent_edge_on_windows).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[10])) {  // Internet Explorer on Windows.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_internet_explorer_on_windows));

            // Select the Internet on Windows radio box.
            menu.findItem(R.id.user_agent_internet_explorer_on_windows).setChecked(true);
        } else if (currentUserAgent.equals(getResources().getStringArray(R.array.user_agent_data)[11])) {  // Safari on macOS.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_safari_on_macos));

            // Select the Safari on macOS radio box.
            menu.findItem(R.id.user_agent_safari_on_macos).setChecked(true);
        } else {  // Custom user agent.
            // Update the user agent menu item title.
            userAgentMenuItem.setTitle(getString(R.string.options_user_agent) + " - " + getString(R.string.user_agent_custom));

            // Select the Custom radio box.
            menu.findItem(R.id.user_agent_custom).setChecked(true);
        }

        // Set the font size title.
        fontSizeMenuItem.setTitle(getString(R.string.font_size) + " - " + fontSize + "%");

        // Run all the other default commands.
        super.onPrepareOptionsMenu(menu);

        // Display the menu.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get a handle for the cookie manager.
        CookieManager cookieManager = CookieManager.getInstance();

        // Get the selected menu item ID.
        int menuItemId = menuItem.getItemId();

        // Run the commands that correlate to the selected menu item.
        if (menuItemId == R.id.toggle_javascript) {  // JavaScript.
            // Toggle the JavaScript status.
            currentWebView.getSettings().setJavaScriptEnabled(!currentWebView.getSettings().getJavaScriptEnabled());

            // Update the privacy icon.
            updatePrivacyIcons(true);

            // Display a `Snackbar`.
            if (currentWebView.getSettings().getJavaScriptEnabled()) {  // JavaScrip is enabled.
                Snackbar.make(webViewPager, R.string.javascript_enabled, Snackbar.LENGTH_SHORT).show();
            } else if (cookieManager.acceptCookie()) {  // JavaScript is disabled, but first-party cookies are enabled.
                Snackbar.make(webViewPager, R.string.javascript_disabled, Snackbar.LENGTH_SHORT).show();
            } else {  // Privacy mode.
                Snackbar.make(webViewPager, R.string.privacy_mode, Snackbar.LENGTH_SHORT).show();
            }

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.refresh) {  // Refresh.
            // Run the command that correlates to the current status of the menu item.
            if (menuItem.getTitle().equals(getString(R.string.refresh))) {  // The refresh button was pushed.
                // Reload the current WebView.
                currentWebView.reload();
            } else {  // The stop button was pushed.
                // Stop the loading of the WebView.
                currentWebView.stopLoading();
            }

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.bookmarks) {  // Bookmarks.
            // Open the bookmarks drawer.
            drawerLayout.openDrawer(GravityCompat.END);

            // Consume the event.
            return true;
        }

        /*

        else if (menuItemId == R.id.toggle_first_party_cookies) {  // First-party cookies.
            // Switch the first-party cookie status.
            cookieManager.setAcceptCookie(!cookieManager.acceptCookie());

            // Store the first-party cookie status.
            currentWebView.setAcceptFirstPartyCookies(cookieManager.acceptCookie());

            // Update the menu checkbox.
            menuItem.setChecked(cookieManager.acceptCookie());

            // Update the privacy icon.
            updatePrivacyIcons(true);

            // Display a snackbar.
            if (cookieManager.acceptCookie()) {  // First-party cookies are enabled.
                Snackbar.make(webViewPager, R.string.first_party_cookies_enabled, Snackbar.LENGTH_SHORT).show();
            } else if (currentWebView.getSettings().getJavaScriptEnabled()) {  // JavaScript is still enabled.
                Snackbar.make(webViewPager, R.string.first_party_cookies_disabled, Snackbar.LENGTH_SHORT).show();
            } else {  // Privacy mode.
                Snackbar.make(webViewPager, R.string.privacy_mode, Snackbar.LENGTH_SHORT).show();
            }

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        }



        else if (menuItemId == R.id.toggle_third_party_cookies) {  // Third-party cookies.
            // Only act if the API >= 21.  Otherwise, there are no third-party cookie controls.
            if (Build.VERSION.SDK_INT >= 21) {
                // Toggle the status of thirdPartyCookiesEnabled.
                cookieManager.setAcceptThirdPartyCookies(currentWebView, !cookieManager.acceptThirdPartyCookies(currentWebView));

                // Update the menu checkbox.
                menuItem.setChecked(cookieManager.acceptThirdPartyCookies(currentWebView));

                // Display a snackbar.
                if (cookieManager.acceptThirdPartyCookies(currentWebView)) {
                    Snackbar.make(webViewPager, R.string.third_party_cookies_enabled, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(webViewPager, R.string.third_party_cookies_disabled, Snackbar.LENGTH_SHORT).show();
                }

                // Reload the current WebView.
                currentWebView.reload();
            }

            // Consume the event.
            return true;
        }




         else if (menuItemId == R.id.toggle_dom_storage) {  // DOM storage.

            // Toggle the status of domStorageEnabled.
            currentWebView.getSettings().setDomStorageEnabled(!currentWebView.getSettings().getDomStorageEnabled());

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.getSettings().getDomStorageEnabled());

            // Update the privacy icon.
            updatePrivacyIcons(true);

            // Display a snackbar.
            if (currentWebView.getSettings().getDomStorageEnabled()) {
                Snackbar.make(webViewPager, R.string.dom_storage_enabled, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(webViewPager, R.string.dom_storage_disabled, Snackbar.LENGTH_SHORT).show();
            }

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        }
        /*
        else if (menuItemId == R.id.toggle_save_form_data) {  // Form data.  This can be removed once the minimum API >= 26.
            // Switch the status of saveFormDataEnabled.
            currentWebView.getSettings().setSaveFormData(!currentWebView.getSettings().getSaveFormData());

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.getSettings().getSaveFormData());

            // Display a snackbar.
            if (currentWebView.getSettings().getSaveFormData()) {
                Snackbar.make(webViewPager, R.string.form_data_enabled, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(webViewPager, R.string.form_data_disabled, Snackbar.LENGTH_SHORT).show();
            }

            // Update the privacy icon.
            updatePrivacyIcons(true);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        }

        */

        else if (menuItemId == R.id.clear_cookies) {  // Clear cookies.
            // Create a snackbar.
            Snackbar.make(webViewPager, R.string.cookies_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        // Do nothing because everything will be handled by `onDismissed()` below.
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {  // The snackbar was dismissed without the undo button being pushed.
                                // Delete the cookies, which command varies by SDK.
                                if (Build.VERSION.SDK_INT < 21) {
                                    cookieManager.removeAllCookie();
                                } else {
                                    cookieManager.removeAllCookies(null);
                                }
                            }
                        }
                    })
                    .show();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.clear_dom_storage) {  // Clear DOM storage.
            // Create a snackbar.
            Snackbar.make(webViewPager, R.string.dom_storage_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        // Do nothing because everything will be handled by `onDismissed()` below.
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {  // The snackbar was dismissed without the undo button being pushed.
                                // Delete the DOM Storage.
                                WebStorage webStorage = WebStorage.getInstance();
                                webStorage.deleteAllData();

                                // Initialize a handler to manually delete the DOM storage files and directories.
                                Handler deleteDomStorageHandler = new Handler();

                                // Setup a runnable to manually delete the DOM storage files and directories.
                                Runnable deleteDomStorageRunnable = () -> {
                                    try {
                                        // Get a handle for the runtime.
                                        Runtime runtime = Runtime.getRuntime();

                                        // Get the application's private data directory, which will be something like `/data/user/0/com.jdots.browser.standard`,
                                        // which links to `/data/data/com.jdots.browser.standard`.
                                        String privateDataDirectoryString = getApplicationInfo().dataDir;

                                        // A string array must be used because the directory contains a space and `Runtime.exec` will otherwise not escape the string correctly.
                                        Process deleteLocalStorageProcess = runtime.exec(new String[]{"rm", "-rf", privateDataDirectoryString + "/app_webview/Local Storage/"});

                                        // Multiple commands must be used because `Runtime.exec()` does not like `*`.
                                        Process deleteIndexProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/app_webview/IndexedDB");
                                        Process deleteQuotaManagerProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/QuotaManager");
                                        Process deleteQuotaManagerJournalProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/QuotaManager-journal");
                                        Process deleteDatabasesProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/app_webview/databases");

                                        // Wait for the processes to finish.
                                        deleteLocalStorageProcess.waitFor();
                                        deleteIndexProcess.waitFor();
                                        deleteQuotaManagerProcess.waitFor();
                                        deleteQuotaManagerJournalProcess.waitFor();
                                        deleteDatabasesProcess.waitFor();
                                    } catch (Exception exception) {
                                        // Do nothing if an error is thrown.
                                    }
                                };

                                // Manually delete the DOM storage files after 200 milliseconds.
                                deleteDomStorageHandler.postDelayed(deleteDomStorageRunnable, 200);
                            }
                        }
                    })
                    .show();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.clear_form_data) {  // Clear form data.  This can be remove once the minimum API >= 26.
            // Create a snackbar.
            Snackbar.make(webViewPager, R.string.form_data_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        // Do nothing because everything will be handled by `onDismissed()` below.
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {  // The snackbar was dismissed without the undo button being pushed.
                                // Get a handle for the webView database.
                                WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(getApplicationContext());

                                // Delete the form data.
                                webViewDatabase.clearFormData();
                            }
                        }
                    })
                    .show();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.easylist) {  // EasyList.
            // Toggle the EasyList status.
            currentWebView.enableBlocklist(NestedScrollWebView.EASYLIST, !currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYLIST));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYLIST));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.easyprivacy) {  // EasyPrivacy.
            // Toggle the EasyPrivacy status.
            currentWebView.enableBlocklist(NestedScrollWebView.EASYPRIVACY, !currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYPRIVACY));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.EASYPRIVACY));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        }/*

        else if (menuItemId == R.id.fanboys_annoyance_list) {  // Fanboy's Annoyance List.
            // Toggle Fanboy's Annoyance List status.
            currentWebView.enableBlocklist(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST, !currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST));

            // Get a handle for the Fanboy's Social Block List menu item.
            MenuItem fanboysSocialBlockingListMenuItem = optionsMenu.findItem(R.id.fanboys_social_blocking_list);

            // Update the staus of Fanboy's Social Blocking List.
            fanboysSocialBlockingListMenuItem.setEnabled(!currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } /*

        else if (menuItemId == R.id.fanboys_social_blocking_list) {  // Fanboy's Social Blocking List.
            // Toggle Fanboy's Social Blocking List status.
            currentWebView.enableBlocklist(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST, !currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } */ else if (menuItemId == R.id.ultralist) {  // UltraList.
            // Toggle the UltraList status.
            currentWebView.enableBlocklist(NestedScrollWebView.ULTRALIST, !currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRALIST));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRALIST));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.ultraprivacy) {  // UltraPrivacy.
            // Toggle the UltraPrivacy status.
            currentWebView.enableBlocklist(NestedScrollWebView.ULTRAPRIVACY, !currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRAPRIVACY));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.ULTRAPRIVACY));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        }

        /*else if (menuItemId == R.id.block_all_third_party_requests) {  // Block all third-party requests.
            //Toggle the third-party requests blocker status.
            currentWebView.enableBlocklist(NestedScrollWebView.THIRD_PARTY_REQUESTS, !currentWebView.isBlocklistEnabled(NestedScrollWebView.THIRD_PARTY_REQUESTS));

            // Update the menu checkbox.
            menuItem.setChecked(currentWebView.isBlocklistEnabled(NestedScrollWebView.THIRD_PARTY_REQUESTS));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } */

        else if (menuItemId == R.id.proxy_none) {  // Proxy - None.
            // Update the proxy mode.
            proxyMode = ProxyHelper.NONE;

            // Apply the proxy mode.
            applyProxy(true);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.proxy_tor) {  // Proxy - Tor.
            // Update the proxy mode.
            proxyMode = ProxyHelper.TOR;

            // Apply the proxy mode.
            applyProxy(true);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.proxy_i2p) {  // Proxy - I2P.
            // Update the proxy mode.
            proxyMode = ProxyHelper.I2P;

            // Apply the proxy mode.
            applyProxy(true);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.proxy_custom) {  // Proxy - Custom.
            // Update the proxy mode.
            proxyMode = ProxyHelper.CUSTOM;

            // Apply the proxy mode.
            applyProxy(true);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_application_name) {  // User Agent - Clear Browser.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[0]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_webview_default) {  // User Agent - WebView Default.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString("");

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_firefox_on_android) {  // User Agent - Firefox on Android.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[2]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_chrome_on_android) {  // User Agent - Chrome on Android.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[3]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_safari_on_ios) {  // User Agent - Safari on iOS.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[4]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_firefox_on_linux) {  // User Agent - Firefox on Linux.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[5]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_chromium_on_linux) {  // User Agent - Chromium on Linux.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[6]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_firefox_on_windows) {  // User Agent - Firefox on Windows.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[7]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_chrome_on_windows) {  // User Agent - Chrome on Windows.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[8]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_edge_on_windows) {  // User Agent - Edge on Windows.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[9]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_internet_explorer_on_windows) {  // User Agent - Internet Explorer on Windows.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[10]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_safari_on_macos) {  // User Agent - Safari on macOS.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(getResources().getStringArray(R.array.user_agent_data)[11]);

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.user_agent_custom) {  // User Agent - Custom.
            // Update the user agent.
            currentWebView.getSettings().setUserAgentString(sharedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value)));

            // Reload the current WebView.
            currentWebView.reload();

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.font_size) {  // Font size.
            // Instantiate the font size dialog.
            DialogFragment fontSizeDialogFragment = FontSizeDialog.displayDialog(currentWebView.getSettings().getTextZoom());

            // Show the font size dialog.
            fontSizeDialogFragment.show(getSupportFragmentManager(), getString(R.string.font_size));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.swipe_to_refresh) {  // Swipe to refresh.
            // Toggle the stored status of swipe to refresh.
            currentWebView.setSwipeToRefresh(!currentWebView.getSwipeToRefresh());

            // Get a handle for the swipe refresh layout.
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);

            // Update the swipe refresh layout.
            if (currentWebView.getSwipeToRefresh()) {  // Swipe to refresh is enabled.
                // Only enable the swipe refresh layout if the WebView is scrolled to the top.  It is updated every time the scroll changes.
                swipeRefreshLayout.setEnabled(currentWebView.getY() == 0);
            } else {  // Swipe to refresh is disabled.
                // Disable the swipe refresh layout.
                swipeRefreshLayout.setEnabled(false);
            }

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.wide_viewport) {  // Wide viewport.
            // Toggle the viewport.
            currentWebView.getSettings().setUseWideViewPort(!currentWebView.getSettings().getUseWideViewPort());

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.display_images) {  // Display images.
            // Toggle the displaying of images.
            if (currentWebView.getSettings().getLoadsImagesAutomatically()) {  // Images are currently loaded automatically.
                // Disable loading of images.
                currentWebView.getSettings().setLoadsImagesAutomatically(false);

                // Reload the website to remove existing images.
                currentWebView.reload();
            } else {  // Images are not currently loaded automatically.
                // Enable loading of images.  Missing images will be loaded without the need for a reload.
                currentWebView.getSettings().setLoadsImagesAutomatically(true);
            }

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.dark_webview) {  // Dark WebView.
            // Check to see if dark WebView is supported by this WebView.
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                // Toggle the dark WebView setting.
                if (WebSettingsCompat.getForceDark(currentWebView.getSettings()) == WebSettingsCompat.FORCE_DARK_ON) {  // Dark WebView is currently enabled.
                    // Turn off dark WebView.
                    WebSettingsCompat.setForceDark(currentWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                } else {  // Dark WebView is currently disabled.
                    // Turn on dark WebView.
                    WebSettingsCompat.setForceDark(currentWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                }
            }

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.find_on_page) {  // Find on page.
            // Get a handle for the views.
            Toolbar toolbar = findViewById(R.id.toolbar);
            LinearLayout findOnPageLinearLayout = findViewById(R.id.find_on_page_linearlayout);
            EditText findOnPageEditText = findViewById(R.id.find_on_page_edittext);

            // Set the minimum height of the find on page linear layout to match the toolbar.
            findOnPageLinearLayout.setMinimumHeight(toolbar.getHeight());

            // Hide the toolbar.
            toolbar.setVisibility(View.GONE);

            // Show the find on page linear layout.
            findOnPageLinearLayout.setVisibility(View.VISIBLE);

            // Display the keyboard.  The app must wait 200 ms before running the command to work around a bug in Android.
            // http://stackoverflow.com/questions/5520085/android-show-softkeyboard-with-showsoftinput-is-not-working
            findOnPageEditText.postDelayed(() -> {
                // Set the focus on the find on page edit text.
                findOnPageEditText.requestFocus();

                // Get a handle for the input method manager.
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                // Remove the lint warning below that the input method manager might be null.
                assert inputMethodManager != null;

                // Display the keyboard.  `0` sets no input flags.
                inputMethodManager.showSoftInput(findOnPageEditText, 0);
            }, 200);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.print) {  // Print.
            // Get a print manager instance.
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

            // Remove the lint error below that print manager might be null.
            assert printManager != null;

            // Create a print document adapter from the current WebView.
            PrintDocumentAdapter printDocumentAdapter = currentWebView.createPrintDocumentAdapter();

            // Print the document.
            printManager.print(getString(R.string.application_name_web_page), printDocumentAdapter, null);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save_url) {  // Save URL.
            // Prepare the save dialog.  The dialog will be displayed once the file size and the content disposition have been acquired.
            new PrepareSaveDialog(this, this, getSupportFragmentManager(), StoragePermissionDialog.SAVE_URL, currentWebView.getSettings().getUserAgentString(),
                    currentWebView.getAcceptFirstPartyCookies()).execute(currentWebView.getCurrentUrl());

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save_archive) {  // Save archive.
            // Instantiate the save dialog.
            DialogFragment saveArchiveFragment = SaveWebpageDialog.saveWebpage(StoragePermissionDialog.SAVE_ARCHIVE, null, null, getString(R.string.webpage_mht), null,
                    false);

            // Show the save dialog.  It must be named `save_dialog` so that the file picker can update the file name.
            saveArchiveFragment.show(getSupportFragmentManager(), getString(R.string.save_dialog));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.save_image) {  // Save image.
            // Instantiate the save dialog.
            DialogFragment saveImageFragment = SaveWebpageDialog.saveWebpage(StoragePermissionDialog.SAVE_IMAGE, null, null, getString(R.string.webpage_png), null,
                    false);

            // Show the save dialog.  It must be named `save_dialog` so that the file picker can update the file name.
            saveImageFragment.show(getSupportFragmentManager(), getString(R.string.save_dialog));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.add_to_homescreen) {  // Add to homescreen.
            // Instantiate the create home screen shortcut dialog.
            DialogFragment createHomeScreenShortcutDialogFragment = CreateHomeScreenShortcutDialog.createDialog(currentWebView.getTitle(), currentWebView.getUrl(),
                    currentWebView.getFavoriteOrDefaultIcon());

            // Show the create home screen shortcut dialog.
            createHomeScreenShortcutDialogFragment.show(getSupportFragmentManager(), getString(R.string.create_shortcut));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.view_source) {  // View source.
            // Create an intent to launch the view source activity.
            Intent viewSourceIntent = new Intent(this, ViewSourceActivity.class);

            // Add the variables to the intent.
            viewSourceIntent.putExtra(ViewSourceActivityKt.CURRENT_URL, currentWebView.getUrl());
            viewSourceIntent.putExtra(ViewSourceActivityKt.USER_AGENT, currentWebView.getSettings().getUserAgentString());

            // Make it so.
            startActivity(viewSourceIntent);

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.share_url) {  // Share URL.
            // Setup the share string.
            String shareString = currentWebView.getTitle() + " – " + currentWebView.getUrl();

            // Create the share intent.
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            // Add the share string to the intent.
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);

            // Set the MIME type.
            shareIntent.setType("text/plain");

            // Set the intent to open in a new task.
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Make it so.
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_url)));

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.open_with_app) {  // Open with app.
            // Open the URL with an outside app.
            openWithApp(currentWebView.getUrl());

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.open_with_browser) {  // Open with browser.
            // Open the URL with an outside browser.
            openWithBrowser(currentWebView.getUrl());

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.add_or_edit_domain) {  // Add or edit domain.
            // Check if domain settings currently exist.
            if (currentWebView.getDomainSettingsApplied()) {  // Edit the current domain settings.
                // Reapply the domain settings on returning to `MainWebViewActivity`.
                reapplyDomainSettingsOnRestart = true;

                // Create an intent to launch the domains activity.
                Intent domainsIntent = new Intent(this, DomainsActivity.class);

                // Add the extra information to the intent.
                domainsIntent.putExtra("load_domain", currentWebView.getDomainSettingsDatabaseId());
                domainsIntent.putExtra("close_on_back", true);
                domainsIntent.putExtra("current_url", currentWebView.getUrl());

                // Get the current certificate.
                SslCertificate sslCertificate = currentWebView.getCertificate();

                // Check to see if the SSL certificate is populated.
                if (sslCertificate != null) {
                    // Extract the certificate to strings.
                    String issuedToCName = sslCertificate.getIssuedTo().getCName();
                    String issuedToOName = sslCertificate.getIssuedTo().getOName();
                    String issuedToUName = sslCertificate.getIssuedTo().getUName();
                    String issuedByCName = sslCertificate.getIssuedBy().getCName();
                    String issuedByOName = sslCertificate.getIssuedBy().getOName();
                    String issuedByUName = sslCertificate.getIssuedBy().getUName();
                    long startDateLong = sslCertificate.getValidNotBeforeDate().getTime();
                    long endDateLong = sslCertificate.getValidNotAfterDate().getTime();

                    // Add the certificate to the intent.
                    domainsIntent.putExtra("ssl_issued_to_cname", issuedToCName);
                    domainsIntent.putExtra("ssl_issued_to_oname", issuedToOName);
                    domainsIntent.putExtra("ssl_issued_to_uname", issuedToUName);
                    domainsIntent.putExtra("ssl_issued_by_cname", issuedByCName);
                    domainsIntent.putExtra("ssl_issued_by_oname", issuedByOName);
                    domainsIntent.putExtra("ssl_issued_by_uname", issuedByUName);
                    domainsIntent.putExtra("ssl_start_date", startDateLong);
                    domainsIntent.putExtra("ssl_end_date", endDateLong);
                }

                // Check to see if the current IP addresses have been received.
                if (currentWebView.hasCurrentIpAddresses()) {
                    // Add the current IP addresses to the intent.
                    domainsIntent.putExtra("current_ip_addresses", currentWebView.getCurrentIpAddresses());
                }

                // Make it so.
                startActivity(domainsIntent);
            } else {  // Add a new domain.
                // Apply the new domain settings on returning to `MainWebViewActivity`.
                reapplyDomainSettingsOnRestart = true;

                // Get the current domain
                Uri currentUri = Uri.parse(currentWebView.getUrl());
                String currentDomain = currentUri.getHost();

                // Initialize the database handler.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
                DomainsDatabaseHelper domainsDatabaseHelper = new DomainsDatabaseHelper(this, null, null, 0);

                // Create the domain and store the database ID.
                int newDomainDatabaseId = domainsDatabaseHelper.addDomain(currentDomain);

                // Create an intent to launch the domains activity.
                Intent domainsIntent = new Intent(this, DomainsActivity.class);

                // Add the extra information to the intent.
                domainsIntent.putExtra("load_domain", newDomainDatabaseId);
                domainsIntent.putExtra("close_on_back", true);
                domainsIntent.putExtra("current_url", currentWebView.getUrl());

                // Get the current certificate.
                SslCertificate sslCertificate = currentWebView.getCertificate();

                // Check to see if the SSL certificate is populated.
                if (sslCertificate != null) {
                    // Extract the certificate to strings.
                    String issuedToCName = sslCertificate.getIssuedTo().getCName();
                    String issuedToOName = sslCertificate.getIssuedTo().getOName();
                    String issuedToUName = sslCertificate.getIssuedTo().getUName();
                    String issuedByCName = sslCertificate.getIssuedBy().getCName();
                    String issuedByOName = sslCertificate.getIssuedBy().getOName();
                    String issuedByUName = sslCertificate.getIssuedBy().getUName();
                    long startDateLong = sslCertificate.getValidNotBeforeDate().getTime();
                    long endDateLong = sslCertificate.getValidNotAfterDate().getTime();

                    // Add the certificate to the intent.
                    domainsIntent.putExtra("ssl_issued_to_cname", issuedToCName);
                    domainsIntent.putExtra("ssl_issued_to_oname", issuedToOName);
                    domainsIntent.putExtra("ssl_issued_to_uname", issuedToUName);
                    domainsIntent.putExtra("ssl_issued_by_cname", issuedByCName);
                    domainsIntent.putExtra("ssl_issued_by_oname", issuedByOName);
                    domainsIntent.putExtra("ssl_issued_by_uname", issuedByUName);
                    domainsIntent.putExtra("ssl_start_date", startDateLong);
                    domainsIntent.putExtra("ssl_end_date", endDateLong);
                }

                // Check to see if the current IP addresses have been received.
                if (currentWebView.hasCurrentIpAddresses()) {
                    // Add the current IP addresses to the intent.
                    domainsIntent.putExtra("current_ip_addresses", currentWebView.getCurrentIpAddresses());
                }

                // Make it so.
                startActivity(domainsIntent);
            }

            // Consume the event.
            return true;
        } else if (menuItemId == R.id.ad_consent) {  // Ad consent.
            // Instantiate the ad consent dialog.
            DialogFragment adConsentDialogFragment = new AdConsentDialog();

            // Display the ad consent dialog.
            adConsentDialogFragment.show(getSupportFragmentManager(), getString(R.string.ad_consent));

            // Consume the event.
            return true;
        } else {  // There is no match with the options menu.  Pass the event up to the parent method.
            // Don't consume the event.
            return super.onOptionsItemSelected(menuItem);
        }
    }

    // removeAllCookies is deprecated, but it is required for API < 21.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the menu item ID.
        int menuItemId = menuItem.getItemId();

        // Run the commands that correspond to the selected menu item.
        if (menuItemId == R.id.clear_and_exit) {  // Clear and exit.
            // Clear and exit Clear Browser.
            clearAndExit();
        } else if (menuItemId == R.id.home) {  // Home.
            // Load the homepage.
            loadUrl(currentWebView, sharedPreferences.getString("homepage", getString(R.string.homepage_default_value)));
        } else if (menuItemId == R.id.back) {  // Back.
            // Check if the WebView can go back.
            if (currentWebView.canGoBack()) {
                // Get the current web back forward list.
                WebBackForwardList webBackForwardList = currentWebView.copyBackForwardList();

                // Get the previous entry URL.
                String previousUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();

                // Apply the domain settings.
                applyDomainSettings(currentWebView, previousUrl, false, false, false);

                // Load the previous website in the history.
                currentWebView.goBack();
            }
        } else if (menuItemId == R.id.forward) {  // Forward.
            // Check if the WebView can go forward.
            if (currentWebView.canGoForward()) {
                // Get the current web back forward list.
                WebBackForwardList webBackForwardList = currentWebView.copyBackForwardList();

                // Get the next entry URL.
                String nextUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() + 1).getUrl();

                // Apply the domain settings.
                applyDomainSettings(currentWebView, nextUrl, false, false, false);

                // Load the next website in the history.
                currentWebView.goForward();
            }
        } else if (menuItemId == R.id.history) {  // History.
            // Instantiate the URL history dialog.
            DialogFragment urlHistoryDialogFragment = UrlHistoryDialog.loadBackForwardList(currentWebView.getWebViewFragmentId());

            // Show the URL history dialog.
            urlHistoryDialogFragment.show(getSupportFragmentManager(), getString(R.string.history));
        } else if (menuItemId == R.id.open) {  // Open.
            // Instantiate the open file dialog.
            DialogFragment openDialogFragment = new OpenDialog();

            // Show the open file dialog.
            openDialogFragment.show(getSupportFragmentManager(), getString(R.string.open));
        } else if (menuItemId == R.id.requests) {  // Requests.
            // Populate the resource requests.
            RequestsActivity.resourceRequests = currentWebView.getResourceRequests();

            // Create an intent to launch the Requests activity.
            Intent requestsIntent = new Intent(this, RequestsActivity.class);

            // Add the block third-party requests status to the intent.
            requestsIntent.putExtra("block_all_third_party_requests", currentWebView.isBlocklistEnabled(NestedScrollWebView.THIRD_PARTY_REQUESTS));

            // Make it so.
            startActivity(requestsIntent);
        } else if (menuItemId == R.id.downloads) {  // Downloads.
            // Launch the system Download Manager.
            Intent downloadManagerIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

            // Launch as a new task so that Download Manager and Clear Browser show as separate windows in the recent tasks list.
            downloadManagerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Make it so.
            startActivity(downloadManagerIntent);
        } else if (menuItemId == R.id.domains) {  // Domains.
            // Set the flag to reapply the domain settings on restart when returning from Domain Settings.
            reapplyDomainSettingsOnRestart = true;

            // Launch the domains activity.
            Intent domainsIntent = new Intent(this, DomainsActivity.class);

            // Add the extra information to the intent.
            domainsIntent.putExtra("current_url", currentWebView.getUrl());

            // Get the current certificate.
            SslCertificate sslCertificate = currentWebView.getCertificate();

            // Check to see if the SSL certificate is populated.
            if (sslCertificate != null) {
                // Extract the certificate to strings.
                String issuedToCName = sslCertificate.getIssuedTo().getCName();
                String issuedToOName = sslCertificate.getIssuedTo().getOName();
                String issuedToUName = sslCertificate.getIssuedTo().getUName();
                String issuedByCName = sslCertificate.getIssuedBy().getCName();
                String issuedByOName = sslCertificate.getIssuedBy().getOName();
                String issuedByUName = sslCertificate.getIssuedBy().getUName();
                long startDateLong = sslCertificate.getValidNotBeforeDate().getTime();
                long endDateLong = sslCertificate.getValidNotAfterDate().getTime();

                // Add the certificate to the intent.
                domainsIntent.putExtra("ssl_issued_to_cname", issuedToCName);
                domainsIntent.putExtra("ssl_issued_to_oname", issuedToOName);
                domainsIntent.putExtra("ssl_issued_to_uname", issuedToUName);
                domainsIntent.putExtra("ssl_issued_by_cname", issuedByCName);
                domainsIntent.putExtra("ssl_issued_by_oname", issuedByOName);
                domainsIntent.putExtra("ssl_issued_by_uname", issuedByUName);
                domainsIntent.putExtra("ssl_start_date", startDateLong);
                domainsIntent.putExtra("ssl_end_date", endDateLong);
            }

            // Check to see if the current IP addresses have been received.
            if (currentWebView.hasCurrentIpAddresses()) {
                // Add the current IP addresses to the intent.
                domainsIntent.putExtra("current_ip_addresses", currentWebView.getCurrentIpAddresses());
            }

            // Make it so.
            startActivity(domainsIntent);
        } else if (menuItemId == R.id.settings) {  // Settings.
            // Set the flag to reapply app settings on restart when returning from Settings.
            reapplyAppSettingsOnRestart = true;

            // Set the flag to reapply the domain settings on restart when returning from Settings.
            reapplyDomainSettingsOnRestart = true;

            // Launch the settings activity.
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (menuItemId == R.id.import_export) { // Import/Export.
            // Create an intent to launch the import/export activity.
            Intent importExportIntent = new Intent(this, ImportExportActivity.class);

            // Make it so.
            startActivity(importExportIntent);
        }
        /*
        else if (menuItemId == R.id.logcat) {  // Logcat.
            // Create an intent to launch the logcat activity.
            Intent logcatIntent = new Intent(this, LogcatActivity.class);

            // Make it so.
            startActivity(logcatIntent);
        } else if (menuItemId == R.id.guide) {  // Guide.
            // Create an intent to launch the guide activity.
            Intent guideIntent = new Intent(this, GuideActivity.class);

            // Make it so.
            startActivity(guideIntent);
        }

        /*
        else if (menuItemId == R.id.about) {  // About
            // Create an intent to launch the about activity.
            Intent aboutIntent = new Intent(this, AboutActivity.class);

            // Create a string array for the blocklist versions.
            String[] blocklistVersions = new String[]{easyList.get(0).get(0)[0], easyPrivacy.get(0).get(0)[0], fanboysAnnoyanceList.get(0).get(0)[0], fanboysSocialList.get(0).get(0)[0],
                    ultraList.get(0).get(0)[0], ultraPrivacy.get(0).get(0)[0]};

            // Add the blocklist versions to the intent.
            aboutIntent.putExtra("blocklist_versions", blocklistVersions);

            // Make it so.
            startActivity(aboutIntent);
        }
        */


        // Close the navigation drawer.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        // Run the default commands.
        super.onPostCreate(savedInstanceState);

        // Sync the state of the DrawerToggle after the default `onRestoreInstanceState()` has finished.  This creates the navigation drawer icon.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // Run the default commands.
        super.onConfigurationChanged(newConfig);

        // Reload the ad for the free flavor if not in full screen mode.
        if (BuildConfig.FLAVOR.contentEquals("free") && !inFullScreenBrowsingMode) {
            // Reload the ad.  The AdView is destroyed and recreated, which changes the ID, every time it is reloaded to handle possible rotations.
            AdHelper.loadAd(findViewById(R.id.adview), getApplicationContext(), getString(R.string.ad_unit_id));
        }

        // `invalidateOptionsMenu` should recalculate the number of action buttons from the menu to display on the app bar, but it doesn't because of the this bug:
        // https://code.google.com/p/android/issues/detail?id=20493#c8
        // ActivityCompat.invalidateOptionsMenu(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        // Get the hit test result.
        final WebView.HitTestResult hitTestResult = currentWebView.getHitTestResult();

        // Define the URL strings.
        final String imageUrl;
        final String linkUrl;

        // Get handles for the system managers.
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        // Remove the lint errors below that the clipboard manager might be null.
        assert clipboardManager != null;

        // Process the link according to the type.
        switch (hitTestResult.getType()) {
            // `SRC_ANCHOR_TYPE` is a link.
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                // Get the target URL.
                linkUrl = hitTestResult.getExtra();

                // Set the target URL as the title of the `ContextMenu`.
                menu.setHeaderTitle(linkUrl);

                // Add an Open in New Tab entry.
                menu.add(R.string.open_in_new_tab).setOnMenuItemClickListener((MenuItem item) -> {
                    // Load the link URL in a new tab and move to it.
                    addNewTab(linkUrl, true);

                    // Consume the event.
                    return true;
                });

                // Add an Open in Background entry.
                menu.add(R.string.open_in_background).setOnMenuItemClickListener((MenuItem item) -> {
                    // Load the link URL in a new tab but do not move to it.
                    addNewTab(linkUrl, false);

                    // Consume the event.
                    return true;
                });

                // Add an Open with App entry.
                menu.add(R.string.open_with_app).setOnMenuItemClickListener((MenuItem item) -> {
                    openWithApp(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add an Open with Browser entry.
                menu.add(R.string.open_with_browser).setOnMenuItemClickListener((MenuItem item) -> {
                    openWithBrowser(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add a Copy URL entry.
                menu.add(R.string.copy_url).setOnMenuItemClickListener((MenuItem item) -> {
                    // Save the link URL in a `ClipData`.
                    ClipData srcAnchorTypeClipData = ClipData.newPlainText(getString(R.string.url), linkUrl);

                    // Set the `ClipData` as the clipboard's primary clip.
                    clipboardManager.setPrimaryClip(srcAnchorTypeClipData);

                    // Consume the event.
                    return true;
                });

                // Add a Save URL entry.
                menu.add(R.string.save_url).setOnMenuItemClickListener((MenuItem item) -> {
                    // Prepare the save dialog.  The dialog will be displayed once the file size and the content disposition have been acquired.
                    new PrepareSaveDialog(this, this, getSupportFragmentManager(), StoragePermissionDialog.SAVE_URL, currentWebView.getSettings().getUserAgentString(),
                            currentWebView.getAcceptFirstPartyCookies()).execute(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add an empty Cancel entry, which by default closes the context menu.
                menu.add(R.string.cancel);
                break;

            // `IMAGE_TYPE` is an image.
            case WebView.HitTestResult.IMAGE_TYPE:
                // Get the image URL.
                imageUrl = hitTestResult.getExtra();

                // Remove the incorrect lint warning below that the image URL might be null.
                assert imageUrl != null;

                // Set the context menu title.
                if (imageUrl.startsWith("data:")) {  // The image data is contained in within the URL, making it exceedingly long.
                    // Truncate the image URL before making it the title.
                    menu.setHeaderTitle(imageUrl.substring(0, 100));
                } else {  // The image URL does not contain the full image data.
                    // Set the image URL as the title of the context menu.
                    menu.setHeaderTitle(imageUrl);
                }

                // Add an Open in New Tab entry.
                menu.add(R.string.open_image_in_new_tab).setOnMenuItemClickListener((MenuItem item) -> {
                    // Load the image in a new tab.
                    addNewTab(imageUrl, true);

                    // Consume the event.
                    return true;
                });

                // Add an Open with App entry.
                menu.add(R.string.open_with_app).setOnMenuItemClickListener((MenuItem item) -> {
                    // Open the image URL with an external app.
                    openWithApp(imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add an Open with Browser entry.
                menu.add(R.string.open_with_browser).setOnMenuItemClickListener((MenuItem item) -> {
                    // Open the image URL with an external browser.
                    openWithBrowser(imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add a View Image entry.
                menu.add(R.string.view_image).setOnMenuItemClickListener(item -> {
                    // Load the image in the current tab.
                    loadUrl(currentWebView, imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add a Save Image entry.
                menu.add(R.string.save_image).setOnMenuItemClickListener((MenuItem item) -> {
                    // Prepare the save dialog.  The dialog will be displayed once the file size and the content disposition have been acquired.
                    new PrepareSaveDialog(this, this, getSupportFragmentManager(), StoragePermissionDialog.SAVE_URL, currentWebView.getSettings().getUserAgentString(),
                            currentWebView.getAcceptFirstPartyCookies()).execute(imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add a Copy URL entry.
                menu.add(R.string.copy_url).setOnMenuItemClickListener((MenuItem item) -> {
                    // Save the image URL in a clip data.
                    ClipData imageTypeClipData = ClipData.newPlainText(getString(R.string.url), imageUrl);

                    // Set the clip data as the clipboard's primary clip.
                    clipboardManager.setPrimaryClip(imageTypeClipData);

                    // Consume the event.
                    return true;
                });

                // Add an empty Cancel entry, which by default closes the context menu.
                menu.add(R.string.cancel);
                break;

            // `SRC_IMAGE_ANCHOR_TYPE` is an image that is also a link.
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                // Get the image URL.
                imageUrl = hitTestResult.getExtra();

                // Instantiate a handler.
                Handler handler = new Handler();

                // Get a message from the handler.
                Message message = handler.obtainMessage();

                // Request the image details from the last touched node be returned in the message.
                currentWebView.requestFocusNodeHref(message);

                // Get the link URL from the message data.
                linkUrl = message.getData().getString("url");

                // Set the link URL as the title of the context menu.
                menu.setHeaderTitle(linkUrl);

                // Add an Open in New Tab entry.
                menu.add(R.string.open_in_new_tab).setOnMenuItemClickListener((MenuItem item) -> {
                    // Load the link URL in a new tab and move to it.
                    addNewTab(linkUrl, true);

                    // Consume the event.
                    return true;
                });

                // Add an Open in Background entry.
                menu.add(R.string.open_in_background).setOnMenuItemClickListener((MenuItem item) -> {
                    // Lod the link URL in a new tab but do not move to it.
                    addNewTab(linkUrl, false);

                    // Consume the event.
                    return true;
                });

                // Add an Open Image in New Tab entry.
                menu.add(R.string.open_image_in_new_tab).setOnMenuItemClickListener((MenuItem item) -> {
                    // Load the image in a new tab and move to it.
                    addNewTab(imageUrl, true);

                    // Consume the event.
                    return true;
                });

                // Add an Open with App entry.
                menu.add(R.string.open_with_app).setOnMenuItemClickListener((MenuItem item) -> {
                    // Open the link URL with an external app.
                    openWithApp(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add an Open with Browser entry.
                menu.add(R.string.open_with_browser).setOnMenuItemClickListener((MenuItem item) -> {
                    // Open the link URL with an external browser.
                    openWithBrowser(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add a View Image entry.
                menu.add(R.string.view_image).setOnMenuItemClickListener((MenuItem item) -> {
                    // View the image in the current tab.
                    loadUrl(currentWebView, imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add a Save Image entry.
                menu.add(R.string.save_image).setOnMenuItemClickListener((MenuItem item) -> {
                    // Prepare the save dialog.  The dialog will be displayed once the file size and the content disposition have been acquired.
                    new PrepareSaveDialog(this, this, getSupportFragmentManager(), StoragePermissionDialog.SAVE_URL, currentWebView.getSettings().getUserAgentString(),
                            currentWebView.getAcceptFirstPartyCookies()).execute(imageUrl);

                    // Consume the event.
                    return true;
                });

                // Add a Copy URL entry.
                menu.add(R.string.copy_url).setOnMenuItemClickListener((MenuItem item) -> {
                    // Save the link URL in a clip data.
                    ClipData srcImageAnchorTypeClipData = ClipData.newPlainText(getString(R.string.url), linkUrl);

                    // Set the clip data as the clipboard's primary clip.
                    clipboardManager.setPrimaryClip(srcImageAnchorTypeClipData);

                    // Consume the event.
                    return true;
                });

                // Add a Save URL entry.
                menu.add(R.string.save_url).setOnMenuItemClickListener((MenuItem item) -> {
                    // Prepare the save dialog.  The dialog will be displayed once the file size and the content disposition have been acquired.
                    new PrepareSaveDialog(this, this, getSupportFragmentManager(), StoragePermissionDialog.SAVE_URL, currentWebView.getSettings().getUserAgentString(),
                            currentWebView.getAcceptFirstPartyCookies()).execute(linkUrl);

                    // Consume the event.
                    return true;
                });

                // Add an empty Cancel entry, which by default closes the context menu.
                menu.add(R.string.cancel);
                break;

            case WebView.HitTestResult.EMAIL_TYPE:
                // Get the target URL.
                linkUrl = hitTestResult.getExtra();

                // Set the target URL as the title of the `ContextMenu`.
                menu.setHeaderTitle(linkUrl);

                // Add a Write Email entry.
                menu.add(R.string.write_email).setOnMenuItemClickListener(item -> {
                    // Use `ACTION_SENDTO` instead of `ACTION_SEND` so that only email programs are launched.
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

                    // Parse the url and set it as the data for the `Intent`.
                    emailIntent.setData(Uri.parse("mailto:" + linkUrl));

                    // `FLAG_ACTIVITY_NEW_TASK` opens the email program in a new task instead as part of Clear Browser.
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        // Make it so.
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException exception) {
                        // Display a snackbar.
                        Snackbar.make(currentWebView, getString(R.string.error) + "  " + exception, Snackbar.LENGTH_INDEFINITE).show();
                    }

                    // Consume the event.
                    return true;
                });

                // Add a Copy Email Address entry.
                menu.add(R.string.copy_email_address).setOnMenuItemClickListener(item -> {
                    // Save the email address in a `ClipData`.
                    ClipData srcEmailTypeClipData = ClipData.newPlainText(getString(R.string.email_address), linkUrl);

                    // Set the `ClipData` as the clipboard's primary clip.
                    clipboardManager.setPrimaryClip(srcEmailTypeClipData);

                    // Consume the event.
                    return true;
                });

                // Add an empty Cancel entry, which by default closes the context menu.
                menu.add(R.string.cancel);
                break;
        }
    }

    @Override
    public void onCreateBookmark(DialogFragment dialogFragment, Bitmap favoriteIconBitmap) {
        // Get a handle for the bookmarks list view.
        ListView bookmarksListView = findViewById(R.id.bookmarks_drawer_listview);

        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialog != null;

        // Get the views from the dialog fragment.
        EditText createBookmarkNameEditText = dialog.findViewById(R.id.create_bookmark_name_edittext);
        EditText createBookmarkUrlEditText = dialog.findViewById(R.id.create_bookmark_url_edittext);

        // Extract the strings from the edit texts.
        String bookmarkNameString = createBookmarkNameEditText.getText().toString();
        String bookmarkUrlString = createBookmarkUrlEditText.getText().toString();

        // Create a favorite icon byte array output stream.
        ByteArrayOutputStream favoriteIconByteArrayOutputStream = new ByteArrayOutputStream();

        // Convert the favorite icon bitmap to a byte array.  `0` is for lossless compression (the only option for a PNG).
        favoriteIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, favoriteIconByteArrayOutputStream);

        // Convert the favorite icon byte array stream to a byte array.
        byte[] favoriteIconByteArray = favoriteIconByteArrayOutputStream.toByteArray();

        // Display the new bookmark below the current items in the (0 indexed) list.
        int newBookmarkDisplayOrder = bookmarksListView.getCount();

        // Create the bookmark.
        bookmarksDatabaseHelper.createBookmark(bookmarkNameString, bookmarkUrlString, currentBookmarksFolder, newBookmarkDisplayOrder, favoriteIconByteArray);

        // Update the bookmarks cursor with the current contents of this folder.
        bookmarksCursor = bookmarksDatabaseHelper.getBookmarksByDisplayOrder(currentBookmarksFolder);

        // Update the list view.
        bookmarksCursorAdapter.changeCursor(bookmarksCursor);

        // Scroll to the new bookmark.
        bookmarksListView.setSelection(newBookmarkDisplayOrder);
    }

    @Override
    public void onCreateBookmarkFolder(DialogFragment dialogFragment, @NonNull Bitmap favoriteIconBitmap) {
        // Get a handle for the bookmarks list view.
        ListView bookmarksListView = findViewById(R.id.bookmarks_drawer_listview);

        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialog != null;

        // Get handles for the views in the dialog fragment.
        EditText createFolderNameEditText = dialog.findViewById(R.id.create_folder_name_edittext);
        RadioButton defaultFolderIconRadioButton = dialog.findViewById(R.id.create_folder_default_icon_radiobutton);
        ImageView folderIconImageView = dialog.findViewById(R.id.create_folder_default_icon);

        // Get new folder name string.
        String folderNameString = createFolderNameEditText.getText().toString();

        // Create a folder icon bitmap.
        Bitmap folderIconBitmap;

        // Set the folder icon bitmap according to the dialog.
        if (defaultFolderIconRadioButton.isChecked()) {  // Use the default folder icon.
            // Get the default folder icon drawable.
            Drawable folderIconDrawable = folderIconImageView.getDrawable();

            // Convert the folder icon drawable to a bitmap drawable.
            BitmapDrawable folderIconBitmapDrawable = (BitmapDrawable) folderIconDrawable;

            // Convert the folder icon bitmap drawable to a bitmap.
            folderIconBitmap = folderIconBitmapDrawable.getBitmap();
        } else {  // Use the WebView favorite icon.
            // Copy the favorite icon bitmap to the folder icon bitmap.
            folderIconBitmap = favoriteIconBitmap;
        }

        // Create a folder icon byte array output stream.
        ByteArrayOutputStream folderIconByteArrayOutputStream = new ByteArrayOutputStream();

        // Convert the folder icon bitmap to a byte array.  `0` is for lossless compression (the only option for a PNG).
        folderIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, folderIconByteArrayOutputStream);

        // Convert the folder icon byte array stream to a byte array.
        byte[] folderIconByteArray = folderIconByteArrayOutputStream.toByteArray();

        // Move all the bookmarks down one in the display order.
        for (int i = 0; i < bookmarksListView.getCount(); i++) {
            int databaseId = (int) bookmarksListView.getItemIdAtPosition(i);
            bookmarksDatabaseHelper.updateDisplayOrder(databaseId, i + 1);
        }

        // Create the folder, which will be placed at the top of the `ListView`.
        bookmarksDatabaseHelper.createFolder(folderNameString, currentBookmarksFolder, folderIconByteArray);

        // Update the bookmarks cursor with the current contents of this folder.
        bookmarksCursor = bookmarksDatabaseHelper.getBookmarksByDisplayOrder(currentBookmarksFolder);

        // Update the `ListView`.
        bookmarksCursorAdapter.changeCursor(bookmarksCursor);

        // Scroll to the new folder.
        bookmarksListView.setSelection(0);
    }

    @Override
    public void onSaveBookmarkFolder(DialogFragment dialogFragment, int selectedFolderDatabaseId, Bitmap favoriteIconBitmap) {
        // Remove the incorrect lint warning below that the dialog fragment might be null.
        assert dialogFragment != null;

        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialog != null;

        // Get handles for the views from the dialog.
        EditText editFolderNameEditText = dialog.findViewById(R.id.edit_folder_name_edittext);
        RadioButton currentFolderIconRadioButton = dialog.findViewById(R.id.edit_folder_current_icon_radiobutton);
        RadioButton defaultFolderIconRadioButton = dialog.findViewById(R.id.edit_folder_default_icon_radiobutton);
        ImageView defaultFolderIconImageView = dialog.findViewById(R.id.edit_folder_default_icon_imageview);

        // Get the new folder name.
        String newFolderNameString = editFolderNameEditText.getText().toString();

        // Check if the favorite icon has changed.
        if (currentFolderIconRadioButton.isChecked()) {  // Only the name has changed.
            // Update the name in the database.
            bookmarksDatabaseHelper.updateFolder(selectedFolderDatabaseId, oldFolderNameString, newFolderNameString);
        } else if (!currentFolderIconRadioButton.isChecked() && newFolderNameString.equals(oldFolderNameString)) {  // Only the icon has changed.
            // Create the new folder icon Bitmap.
            Bitmap folderIconBitmap;

            // Populate the new folder icon bitmap.
            if (defaultFolderIconRadioButton.isChecked()) {
                // Get the default folder icon drawable.
                Drawable folderIconDrawable = defaultFolderIconImageView.getDrawable();

                // Convert the folder icon drawable to a bitmap drawable.
                BitmapDrawable folderIconBitmapDrawable = (BitmapDrawable) folderIconDrawable;

                // Convert the folder icon bitmap drawable to a bitmap.
                folderIconBitmap = folderIconBitmapDrawable.getBitmap();
            } else {  // Use the `WebView` favorite icon.
                // Copy the favorite icon bitmap to the folder icon bitmap.
                folderIconBitmap = favoriteIconBitmap;
            }

            // Create a folder icon byte array output stream.
            ByteArrayOutputStream newFolderIconByteArrayOutputStream = new ByteArrayOutputStream();

            // Convert the folder icon bitmap to a byte array.  `0` is for lossless compression (the only option for a PNG).
            folderIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, newFolderIconByteArrayOutputStream);

            // Convert the folder icon byte array stream to a byte array.
            byte[] newFolderIconByteArray = newFolderIconByteArrayOutputStream.toByteArray();

            // Update the folder icon in the database.
            bookmarksDatabaseHelper.updateFolder(selectedFolderDatabaseId, newFolderIconByteArray);
        } else {  // The folder icon and the name have changed.
            // Get the new folder icon `Bitmap`.
            Bitmap folderIconBitmap;
            if (defaultFolderIconRadioButton.isChecked()) {
                // Get the default folder icon drawable.
                Drawable folderIconDrawable = defaultFolderIconImageView.getDrawable();

                // Convert the folder icon drawable to a bitmap drawable.
                BitmapDrawable folderIconBitmapDrawable = (BitmapDrawable) folderIconDrawable;

                // Convert the folder icon bitmap drawable to a bitmap.
                folderIconBitmap = folderIconBitmapDrawable.getBitmap();
            } else {  // Use the `WebView` favorite icon.
                // Copy the favorite icon bitmap to the folder icon bitmap.
                folderIconBitmap = favoriteIconBitmap;
            }

            // Create a folder icon byte array output stream.
            ByteArrayOutputStream newFolderIconByteArrayOutputStream = new ByteArrayOutputStream();

            // Convert the folder icon bitmap to a byte array.  `0` is for lossless compression (the only option for a PNG).
            folderIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, newFolderIconByteArrayOutputStream);

            // Convert the folder icon byte array stream to a byte array.
            byte[] newFolderIconByteArray = newFolderIconByteArrayOutputStream.toByteArray();

            // Update the folder name and icon in the database.
            bookmarksDatabaseHelper.updateFolder(selectedFolderDatabaseId, oldFolderNameString, newFolderNameString, newFolderIconByteArray);
        }

        // Update the bookmarks cursor with the current contents of this folder.
        bookmarksCursor = bookmarksDatabaseHelper.getBookmarksByDisplayOrder(currentBookmarksFolder);

        // Update the `ListView`.
        bookmarksCursorAdapter.changeCursor(bookmarksCursor);
    }

    // Override `onBackPressed()` to handle the navigation drawer and and the WebViews.
    @Override
    public void onBackPressed() {
        // Check the different options for processing `back`.
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {  // The navigation drawer is open.
            // Close the navigation drawer.
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLayout.isDrawerVisible(GravityCompat.END)) {  // The bookmarks drawer is open.
            // close the bookmarks drawer.
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (displayingFullScreenVideo) {  // A full screen video is shown.
            // Get a handle for the layouts.
            FrameLayout rootFrameLayout = findViewById(R.id.root_framelayout);
            RelativeLayout mainContentRelativeLayout = findViewById(R.id.main_content_relativelayout);
            FrameLayout fullScreenVideoFrameLayout = findViewById(R.id.full_screen_video_framelayout);

            // Re-enable the screen timeout.
            fullScreenVideoFrameLayout.setKeepScreenOn(false);

            // Unset the full screen video flag.
            displayingFullScreenVideo = false;

            // Remove all the views from the full screen video frame layout.
            fullScreenVideoFrameLayout.removeAllViews();

            // Hide the full screen video frame layout.
            fullScreenVideoFrameLayout.setVisibility(View.GONE);

            // Enable the sliding drawers.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            // Show the main content relative layout.
            mainContentRelativeLayout.setVisibility(View.VISIBLE);

            // Apply the appropriate full screen mode flags.
            if (fullScreenBrowsingModeEnabled && inFullScreenBrowsingMode) {  // Clear Browser is currently in full screen browsing mode.
                // Hide the banner ad in the free flavor.
                if (BuildConfig.FLAVOR.contentEquals("free")) {
                    AdHelper.hideAd(findViewById(R.id.adview));
                }

                /* Hide the system bars.
                 * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                 * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                 * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                 * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                 */
                rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                // Reload the website if the app bar is hidden.  Otherwise, there is some bug in Android that causes the WebView to be entirely black.
                if (hideAppBar) {
                    // Reload the WebView.
                    currentWebView.reload();
                }
            } else {  // Switch to normal viewing mode.
                // Remove the `SYSTEM_UI` flags from the root frame layout.
                rootFrameLayout.setSystemUiVisibility(0);
            }

            // Reload the ad for the free flavor if not in full screen mode.
            if (BuildConfig.FLAVOR.contentEquals("free") && !inFullScreenBrowsingMode) {
                // Reload the ad.
                AdHelper.loadAd(findViewById(R.id.adview), getApplicationContext(), getString(R.string.ad_unit_id));
            }
        } else if (currentWebView.canGoBack()) {  // There is at least one item in the current WebView history.
            // Get the current web back forward list.
            WebBackForwardList webBackForwardList = currentWebView.copyBackForwardList();

            // Get the previous entry URL.
            String previousUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();

            // Apply the domain settings.
            applyDomainSettings(currentWebView, previousUrl, false, false, false);

            // Go back.
            currentWebView.goBack();
        } else if (tabLayout.getTabCount() > 1) {  // There are at least two tabs.
            // Close the current tab.
            closeCurrentTab();
        } else {  // There isn't anything to do in Clear Browser.
            // Close Clear Browser.  `finishAndRemoveTask()` also removes Clear Browser from the recent app list.
            if (Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }

            // Manually kill Clear Browser.  Otherwise, it is glitchy when restarted.
            System.exit(0);
        }
    }

    // Process the results of a file browse.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        // Run the default commands.
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        // Run the commands that correlate to the specified request code.
        switch (requestCode) {
            case BROWSE_FILE_UPLOAD_REQUEST_CODE:
                // File uploads only work on API >= 21.
                if (Build.VERSION.SDK_INT >= 21) {
                    // Pass the file to the WebView.
                    fileChooserCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, returnedIntent));
                }
                break;

            case BROWSE_SAVE_WEBPAGE_REQUEST_CODE:
                // Don't do anything if the user pressed back from the file picker.
                if (resultCode == Activity.RESULT_OK) {
                    // Get a handle for the save dialog fragment.
                    DialogFragment saveWebpageDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.save_dialog));

                    // Only update the file name if the dialog still exists.
                    if (saveWebpageDialogFragment != null) {
                        // Get a handle for the save webpage dialog.
                        Dialog saveWebpageDialog = saveWebpageDialogFragment.getDialog();

                        // Remove the incorrect lint warning below that the dialog might be null.
                        assert saveWebpageDialog != null;

                        // Get a handle for the file name edit text.
                        EditText fileNameEditText = saveWebpageDialog.findViewById(R.id.file_name_edittext);
                        TextView fileExistsWarningTextView = saveWebpageDialog.findViewById(R.id.file_exists_warning_textview);

                        // Instantiate the file name helper.
                        FileNameHelper fileNameHelper = new FileNameHelper();

                        // Get the file path if it isn't null.
                        if (returnedIntent.getData() != null) {
                            // Convert the file name URI to a file name path.
                            String fileNamePath = fileNameHelper.convertUriToFileNamePath(returnedIntent.getData());

                            // Set the file name path as the text of the file name edit text.
                            fileNameEditText.setText(fileNamePath);

                            // Move the cursor to the end of the file name edit text.
                            fileNameEditText.setSelection(fileNamePath.length());

                            // Hide the file exists warning.
                            fileExistsWarningTextView.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case BROWSE_OPEN_REQUEST_CODE:
                // Don't do anything if the user pressed back from the file picker.
                if (resultCode == Activity.RESULT_OK) {
                    // Get a handle for the open dialog fragment.
                    DialogFragment openDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.open));

                    // Only update the file name if the dialog still exists.
                    if (openDialogFragment != null) {
                        // Get a handle for the open dialog.
                        Dialog openDialog = openDialogFragment.getDialog();

                        // Remove the incorrect lint warning below that the dialog might be null.
                        assert openDialog != null;

                        // Get a handle for the file name edit text.
                        EditText fileNameEditText = openDialog.findViewById(R.id.file_name_edittext);

                        // Instantiate the file name helper.
                        FileNameHelper fileNameHelper = new FileNameHelper();

                        // Get the file path if it isn't null.
                        if (returnedIntent.getData() != null) {
                            // Convert the file name URI to a file name path.
                            String fileNamePath = fileNameHelper.convertUriToFileNamePath(returnedIntent.getData());

                            // Set the file name path as the text of the file name edit text.
                            fileNameEditText.setText(fileNamePath);

                            // Move the cursor to the end of the file name edit text.
                            fileNameEditText.setSelection(fileNamePath.length());
                        }
                    }
                }
                break;
        }
    }

    private void loadUrlFromTextBox() {
        // Get a handle for the URL edit text.
        EditText urlEditText = findViewById(R.id.url_edittext);

        // Get the text from urlTextBox and convert it to a string.  trim() removes white spaces from the beginning and end of the string.
        String unformattedUrlString = urlEditText.getText().toString().trim();

        // Initialize the formatted URL string.
        String url = "";

        // Check to see if `unformattedUrlString` is a valid URL.  Otherwise, convert it into a search.
        if (unformattedUrlString.startsWith("content://")) {  // This is a Content URL.
            // Load the entire content URL.
            url = unformattedUrlString;
        } else if (Patterns.WEB_URL.matcher(unformattedUrlString).matches() || unformattedUrlString.startsWith("http://") || unformattedUrlString.startsWith("https://") ||
                unformattedUrlString.startsWith("file://")) {  // This is a standard URL.
            // Add `https://` at the beginning if there is no protocol.  Otherwise the app will segfault.
            if (!unformattedUrlString.startsWith("http") && !unformattedUrlString.startsWith("file://") && !unformattedUrlString.startsWith("content://")) {
                unformattedUrlString = "https://" + unformattedUrlString;
            }

            // Initialize `unformattedUrl`.
            URL unformattedUrl = null;

            // Convert `unformattedUrlString` to a `URL`, then to a `URI`, and then back to a `String`, which sanitizes the input and adds in any missing components.
            try {
                unformattedUrl = new URL(unformattedUrlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // The ternary operator (? :) makes sure that a null pointer exception is not thrown, which would happen if `.get` was called on a `null` value.
            String scheme = unformattedUrl != null ? unformattedUrl.getProtocol() : null;
            String authority = unformattedUrl != null ? unformattedUrl.getAuthority() : null;
            String path = unformattedUrl != null ? unformattedUrl.getPath() : null;
            String query = unformattedUrl != null ? unformattedUrl.getQuery() : null;
            String fragment = unformattedUrl != null ? unformattedUrl.getRef() : null;

            // Build the URI.
            Uri.Builder uri = new Uri.Builder();
            uri.scheme(scheme).authority(authority).path(path).query(query).fragment(fragment);

            // Decode the URI as a UTF-8 string in.
            try {
                url = URLDecoder.decode(uri.build().toString(), "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                // Do nothing.  The formatted URL string will remain blank.
            }
        } else if (!unformattedUrlString.isEmpty()) {  // This is not a URL, but rather a search string.
            // Create an encoded URL String.
            String encodedUrlString;

            // Sanitize the search input.
            try {
                encodedUrlString = URLEncoder.encode(unformattedUrlString, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                encodedUrlString = "";
            }

            // Add the base search URL.
            url = searchURL + encodedUrlString;
        }

        // Clear the focus from the URL edit text.  Otherwise, proximate typing in the box will retain the colorized formatting instead of being reset during refocus.
        urlEditText.clearFocus();

        // Make it so.
        loadUrl(currentWebView, url);
    }

    private void loadUrl(NestedScrollWebView nestedScrollWebView, String url) {
        // Sanitize the URL.
        url = sanitizeUrl(url);

        // Apply the domain settings and load the URL.
        applyDomainSettings(nestedScrollWebView, url, true, false, true);
    }

    public void findPreviousOnPage(View view) {
        // Go to the previous highlighted phrase on the page.  `false` goes backwards instead of forwards.
        currentWebView.findNext(false);
    }

    public void findNextOnPage(View view) {
        // Go to the next highlighted phrase on the page. `true` goes forwards instead of backwards.
        currentWebView.findNext(true);
    }

    public void closeFindOnPage(View view) {
        // Get a handle for the views.
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout findOnPageLinearLayout = findViewById(R.id.find_on_page_linearlayout);
        EditText findOnPageEditText = findViewById(R.id.find_on_page_edittext);

        // Delete the contents of `find_on_page_edittext`.
        findOnPageEditText.setText(null);

        // Clear the highlighted phrases if the WebView is not null.
        if (currentWebView != null) {
            currentWebView.clearMatches();
        }

        // Hide the find on page linear layout.
        findOnPageLinearLayout.setVisibility(View.GONE);

        // Show the toolbar.
        toolbar.setVisibility(View.VISIBLE);

        // Get a handle for the input method manager.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Remove the lint warning below that the input method manager might be null.
        assert inputMethodManager != null;

        // Hide the keyboard.
        inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
    }

    @Override
    public void onApplyNewFontSize(DialogFragment dialogFragment) {
        // Remove the incorrect lint warning below that the dialog fragment might be null.
        assert dialogFragment != null;

        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below tha the dialog might be null.
        assert dialog != null;

        // Get a handle for the font size edit text.
        EditText fontSizeEditText = dialog.findViewById(R.id.font_size_edittext);

        // Initialize the new font size variable with the current font size.
        int newFontSize = currentWebView.getSettings().getTextZoom();

        // Get the font size from the edit text.
        try {
            newFontSize = Integer.parseInt(fontSizeEditText.getText().toString());
        } catch (Exception exception) {
            // If the edit text does not contain a valid font size do nothing.
        }

        // Apply the new font size.
        currentWebView.getSettings().setTextZoom(newFontSize);
    }

    @Override
    public void onOpen(DialogFragment dialogFragment) {
        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialog != null;

        // Get a handle for the file name edit text.
        EditText fileNameEditText = dialog.findViewById(R.id.file_name_edittext);

        // Get the file path string.
        openFilePath = fileNameEditText.getText().toString();

        // Apply the domain settings.  This resets the favorite icon and removes any domain settings.
        applyDomainSettings(currentWebView, "file://" + openFilePath, true, false, false);

        // Check to see if the storage permission is needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
            // Open the file.
            currentWebView.loadUrl("file://" + openFilePath);
        } else {  // The storage permission has not been granted.
            // Get the external private directory file.
            File externalPrivateDirectoryFile = getExternalFilesDir(null);

            // Remove the incorrect lint error below that the file might be null.
            assert externalPrivateDirectoryFile != null;

            // Get the external private directory string.
            String externalPrivateDirectory = externalPrivateDirectoryFile.toString();

            // Check to see if the file path is in the external private directory.
            if (openFilePath.startsWith(externalPrivateDirectory)) {  // the file path is in the external private directory.
                // Open the file.
                currentWebView.loadUrl("file://" + openFilePath);
            } else {  // The file path is in a public directory.
                // Check if the user has previously denied the storage permission.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {  // Show a dialog explaining the request first.
                    // Instantiate the storage permission alert dialog.
                    DialogFragment storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(StoragePermissionDialog.OPEN);

                    // Show the storage permission alert dialog.  The permission will be requested the the dialog is closed.
                    storagePermissionDialogFragment.show(getSupportFragmentManager(), getString(R.string.storage_permission));
                } else {  // Show the permission request directly.
                    // Request the write external storage permission.  The file will be opened when it finishes.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StoragePermissionDialog.OPEN);
                }
            }
        }
    }

    @Override
    public void onSaveWebpage(int saveType, String originalUrlString, DialogFragment dialogFragment) {
        // Get the dialog.
        Dialog dialog = dialogFragment.getDialog();

        // Remove the incorrect lint warning below that the dialog might be null.
        assert dialog != null;

        // Get a handle for the edit texts.
        EditText urlEditText = dialog.findViewById(R.id.url_edittext);
        EditText fileNameEditText = dialog.findViewById(R.id.file_name_edittext);

        // Store the URL.
        if ((originalUrlString != null) && originalUrlString.startsWith("data:")) {
            // Save the original URL.
            saveWebpageUrl = originalUrlString;
        } else {
            // Get the URL from the edit text, which may have been modified.
            saveWebpageUrl = urlEditText.getText().toString();
        }

        // Get the file path from the edit text.
        saveWebpageFilePath = fileNameEditText.getText().toString();

        // Check to see if the storage permission is needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {  // The storage permission has been granted.
            //Save the webpage according to the save type.
            switch (saveType) {
                case StoragePermissionDialog.SAVE_URL:
                    // Save the URL.
                    new SaveUrl(this, this, saveWebpageFilePath, currentWebView.getSettings().getUserAgentString(), currentWebView.getAcceptFirstPartyCookies()).execute(saveWebpageUrl);
                    break;

                case StoragePermissionDialog.SAVE_ARCHIVE:
                    // Save the webpage archive.
                    saveWebpageArchive(saveWebpageFilePath);
                    break;

                case StoragePermissionDialog.SAVE_IMAGE:
                    // Save the webpage image.
                    new SaveWebpageImage(this, this, saveWebpageFilePath, currentWebView).execute();
                    break;
            }

            // Reset the strings.
            saveWebpageUrl = "";
            saveWebpageFilePath = "";
        } else {  // The storage permission has not been granted.
            // Get the external private directory file.
            File externalPrivateDirectoryFile = getExternalFilesDir(null);

            // Remove the incorrect lint error below that the file might be null.
            assert externalPrivateDirectoryFile != null;

            // Get the external private directory string.
            String externalPrivateDirectory = externalPrivateDirectoryFile.toString();

            // Check to see if the file path is in the external private directory.
            if (saveWebpageFilePath.startsWith(externalPrivateDirectory)) {  // The file path is in the external private directory.
                // Save the webpage according to the save type.
                switch (saveType) {
                    case StoragePermissionDialog.SAVE_URL:
                        // Save the URL.
                        new SaveUrl(this, this, saveWebpageFilePath, currentWebView.getSettings().getUserAgentString(), currentWebView.getAcceptFirstPartyCookies()).execute(saveWebpageUrl);
                        break;

                    case StoragePermissionDialog.SAVE_ARCHIVE:
                        // Save the webpage archive.
                        saveWebpageArchive(saveWebpageFilePath);
                        break;

                    case StoragePermissionDialog.SAVE_IMAGE:
                        // Save the webpage image.
                        new SaveWebpageImage(this, this, saveWebpageFilePath, currentWebView).execute();
                        break;
                }

                // Reset the strings.
                saveWebpageUrl = "";
                saveWebpageFilePath = "";
            } else {  // The file path is in a public directory.
                // Check if the user has previously denied the storage permission.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {  // Show a dialog explaining the request first.
                    // Instantiate the storage permission alert dialog.
                    DialogFragment storagePermissionDialogFragment = StoragePermissionDialog.displayDialog(saveType);

                    // Show the storage permission alert dialog.  The permission will be requested when the dialog is closed.
                    storagePermissionDialogFragment.show(getSupportFragmentManager(), getString(R.string.storage_permission));
                } else {  // Show the permission request directly.
                    // Request the write external storage permission according to the save type.  The URL will be saved when it finishes.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, saveType);
                }
            }
        }
    }

    @Override
    public void onCloseStoragePermissionDialog(int requestType) {
        // Request the write external storage permission according to the request type.  The file will be opened when it finishes.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestType);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Only process the results if they exist (this method is triggered when a dialog is presented the first time for an app, but no grant results are included).
        if (grantResults.length > 0) {
            switch (requestCode) {
                case StoragePermissionDialog.OPEN:
                    // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // The storage permission was granted.
                        // Load the file.
                        currentWebView.loadUrl("file://" + openFilePath);
                    } else {  // The storage permission was not granted.
                        // Display an error snackbar.
                        Snackbar.make(currentWebView, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
                    }
                    break;

                case StoragePermissionDialog.SAVE_URL:
                    // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // The storage permission was granted.
                        // Save the raw URL.
                        new SaveUrl(this, this, saveWebpageFilePath, currentWebView.getSettings().getUserAgentString(), currentWebView.getAcceptFirstPartyCookies()).execute(saveWebpageUrl);
                    } else {  // The storage permission was not granted.
                        // Display an error snackbar.
                        Snackbar.make(currentWebView, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
                    }
                    break;

                case StoragePermissionDialog.SAVE_ARCHIVE:
                    // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // The storage permission was granted.
                        // Save the webpage archive.
                        saveWebpageArchive(saveWebpageFilePath);
                    } else {  // The storage permission was not granted.
                        // Display an error snackbar.
                        Snackbar.make(currentWebView, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
                    }
                    break;

                case StoragePermissionDialog.SAVE_IMAGE:
                    // Check to see if the storage permission was granted.  If the dialog was canceled the grant results will be empty.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // The storage permission was granted.
                        // Save the webpage image.
                        new SaveWebpageImage(this, this, saveWebpageFilePath, currentWebView).execute();
                    } else {  // The storage permission was not granted.
                        // Display an error snackbar.
                        Snackbar.make(currentWebView, getString(R.string.cannot_use_location), Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }

            // Reset the strings.
            openFilePath = "";
            saveWebpageUrl = "";
            saveWebpageFilePath = "";
        }
    }

    private void initializeApp() {
        // Get a handle for the input method.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Remove the lint warning below that the input method manager might be null.
        assert inputMethodManager != null;

        // Initialize the gray foreground color spans for highlighting the URLs.  The deprecated `getResources()` must be used until API >= 23.
        initialGrayColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.gray_500));
        finalGrayColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.gray_500));

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the red color span according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            redColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red_a700));
        } else {
            redColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red_900));
        }

        // Get handles for the URL views.
        EditText urlEditText = findViewById(R.id.url_edittext);

        // Remove the formatting from the URL edit text when the user is editing the text.
        urlEditText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {  // The user is editing the URL text box.
                // Remove the highlighting.
                urlEditText.getText().removeSpan(redColorSpan);
                urlEditText.getText().removeSpan(initialGrayColorSpan);
                urlEditText.getText().removeSpan(finalGrayColorSpan);
            } else {  // The user has stopped editing the URL text box.
                // Move to the beginning of the string.
                urlEditText.setSelection(0);

                // Reapply the highlighting.
                highlightUrlText();
            }
        });

        // Set the go button on the keyboard to load the URL in `urlTextBox`.
        urlEditText.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            // If the event is a key-down event on the `enter` button, load the URL.
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Load the URL into the mainWebView and consume the event.
                loadUrlFromTextBox();

                // If the enter key was pressed, consume the event.
                return true;
            } else {
                // If any other key was pressed, do not consume the event.
                return false;
            }
        });

        // Create an Orbot status broadcast receiver.
        orbotStatusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Store the content of the status message in `orbotStatus`.
                orbotStatus = intent.getStringExtra("org.torproject.android.intent.extra.STATUS");

                // If Clear Browser is waiting on the proxy, load the website now that Orbot is connected.
                if ((orbotStatus != null) && orbotStatus.equals("ON") && waitingForProxy) {
                    // Reset the waiting for proxy status.
                    waitingForProxy = false;

                    // Get a handle for the waiting for proxy dialog.
                    DialogFragment waitingForProxyDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.waiting_for_proxy_dialog));

                    // Dismiss the waiting for proxy dialog if it is displayed.
                    if (waitingForProxyDialogFragment != null) {
                        waitingForProxyDialogFragment.dismiss();
                    }

                    // Reload existing URLs and load any URLs that are waiting for the proxy.
                    for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
                        // Get the WebView tab fragment.
                        WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

                        // Get the fragment view.
                        View fragmentView = webViewTabFragment.getView();

                        // Only process the WebViews if they exist.
                        if (fragmentView != null) {
                            // Get the nested scroll WebView from the tab fragment.
                            NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                            // Get the waiting for proxy URL string.
                            String waitingForProxyUrlString = nestedScrollWebView.getWaitingForProxyUrlString();

                            // Load the pending URL if it exists.
                            if (!waitingForProxyUrlString.isEmpty()) {  // A URL is waiting to be loaded.
                                // Load the URL.
                                loadUrl(nestedScrollWebView, waitingForProxyUrlString);

                                // Reset the waiting for proxy URL string.
                                nestedScrollWebView.resetWaitingForProxyUrlString();
                            } else {  // No URL is waiting to be loaded.
                                // Reload the existing URL.
                                nestedScrollWebView.reload();
                            }
                        }
                    }
                }
            }
        };

        // Register the Orbot status broadcast receiver on `this` context.
        this.registerReceiver(orbotStatusBroadcastReceiver, new IntentFilter("org.torproject.android.intent.action.STATUS"));

        // Get handles for views that need to be modified.
        ListView bookmarksListView = findViewById(R.id.bookmarks_drawer_listview);
        FloatingActionButton launchBookmarksActivityFab = findViewById(R.id.launch_bookmarks_activity_fab);
        FloatingActionButton createBookmarkFolderFab = findViewById(R.id.create_bookmark_folder_fab);
        FloatingActionButton createBookmarkFab = findViewById(R.id.create_bookmark_fab);
        EditText findOnPageEditText = findViewById(R.id.find_on_page_edittext);

        // Listen for touches on the navigation menu.
        navigationView.setNavigationItemSelectedListener(this);

        // Get handles for the navigation menu items.
        MenuItem navigationBackMenuItem = navigationMenu.findItem(R.id.back);
        MenuItem navigationForwardMenuItem = navigationMenu.findItem(R.id.forward);
        MenuItem navigationHistoryMenuItem = navigationMenu.findItem(R.id.history);

        // Update the web view pager every time a tab is modified.
        webViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing.
            }

            @Override
            public void onPageSelected(int position) {
                // Close the find on page bar if it is open.
                closeFindOnPage(null);

                // Set the current WebView.
                setCurrentWebView(position);

                // Select the corresponding tab if it does not match the currently selected page.  This will happen if the page was scrolled by creating a new tab.
                if (tabLayout.getSelectedTabPosition() != position) {
                    // Wait until the new tab has been created.
                    tabLayout.post(() -> {
                        // Get a handle for the tab.
                        TabLayout.Tab tab = tabLayout.getTabAt(position);

                        // Assert that the tab is not null.
                        assert tab != null;

                        // Select the tab.
                        tab.select();
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing.
            }
        });

        // Display the View SSL Certificate dialog when the currently selected tab is reselected.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Select the same page in the view pager.
                webViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Instantiate the View SSL Certificate dialog.
                DialogFragment viewSslCertificateDialogFragment = ViewSslCertificateDialog.displayDialog(currentWebView.getWebViewFragmentId());

                // Display the View SSL Certificate dialog.
                viewSslCertificateDialogFragment.show(getSupportFragmentManager(), getString(R.string.view_ssl_certificate));
            }
        });

        // Set the launch bookmarks activity FAB to launch the bookmarks activity.
        launchBookmarksActivityFab.setOnClickListener(v -> {
            // Get a copy of the favorite icon bitmap.
            Bitmap favoriteIconBitmap = currentWebView.getFavoriteOrDefaultIcon();

            // Create a favorite icon byte array output stream.
            ByteArrayOutputStream favoriteIconByteArrayOutputStream = new ByteArrayOutputStream();

            // Convert the favorite icon bitmap to a byte array.  `0` is for lossless compression (the only option for a PNG).
            favoriteIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, favoriteIconByteArrayOutputStream);

            // Convert the favorite icon byte array stream to a byte array.
            byte[] favoriteIconByteArray = favoriteIconByteArrayOutputStream.toByteArray();

            // Create an intent to launch the bookmarks activity.
            Intent bookmarksIntent = new Intent(getApplicationContext(), BookmarksActivity.class);

            // Add the extra information to the intent.
            bookmarksIntent.putExtra("current_url", currentWebView.getUrl());
            bookmarksIntent.putExtra("current_title", currentWebView.getTitle());
            bookmarksIntent.putExtra("current_folder", currentBookmarksFolder);
            bookmarksIntent.putExtra("favorite_icon_byte_array", favoriteIconByteArray);

            // Make it so.
            startActivity(bookmarksIntent);
        });

        // Set the create new bookmark folder FAB to display an alert dialog.
        createBookmarkFolderFab.setOnClickListener(v -> {
            // Create a create bookmark folder dialog.
            DialogFragment createBookmarkFolderDialog = CreateBookmarkFolderDialog.createBookmarkFolder(currentWebView.getFavoriteOrDefaultIcon());

            // Show the create bookmark folder dialog.
            createBookmarkFolderDialog.show(getSupportFragmentManager(), getString(R.string.create_folder));
        });

        // Set the create new bookmark FAB to display an alert dialog.
        createBookmarkFab.setOnClickListener(view -> {
            // Instantiate the create bookmark dialog.
            DialogFragment createBookmarkDialog = CreateBookmarkDialog.createBookmark(currentWebView.getUrl(), currentWebView.getTitle(), currentWebView.getFavoriteOrDefaultIcon());

            // Display the create bookmark dialog.
            createBookmarkDialog.show(getSupportFragmentManager(), getString(R.string.create_bookmark));
        });

        // Search for the string on the page whenever a character changes in the `findOnPageEditText`.
        findOnPageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Search for the text in the WebView if it is not null.  Sometimes on resume after a period of non-use the WebView will be null.
                if (currentWebView != null) {
                    currentWebView.findAllAsync(findOnPageEditText.getText().toString());
                }
            }
        });

        // Set the `check mark` button for the `findOnPageEditText` keyboard to close the soft keyboard.
        findOnPageEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {  // The `enter` key was pressed.
                // Hide the soft keyboard.
                inputMethodManager.hideSoftInputFromWindow(currentWebView.getWindowToken(), 0);

                // Consume the event.
                return true;
            } else {  // A different key was pressed.
                // Do not consume the event.
                return false;
            }
        });

        // Implement swipe to refresh.
        swipeRefreshLayout.setOnRefreshListener(() -> currentWebView.reload());

        // Store the default progress view offsets for use later in `initializeWebView()`.
        defaultProgressViewStartOffset = swipeRefreshLayout.getProgressViewStartOffset();
        defaultProgressViewEndOffset = swipeRefreshLayout.getProgressViewEndOffset();

        // Set the refresh color scheme according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            swipeRefreshLayout.setColorSchemeResources(R.color.blue_700);
        } else {
            swipeRefreshLayout.setColorSchemeResources(R.color.violet_500);
        }

        // Initialize a color background typed value.
        TypedValue colorBackgroundTypedValue = new TypedValue();

        // Get the color background from the theme.
        getTheme().resolveAttribute(android.R.attr.colorBackground, colorBackgroundTypedValue, true);

        // Get the color background int from the typed value.
        int colorBackgroundInt = colorBackgroundTypedValue.data;

        // Set the swipe refresh background color.
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorBackgroundInt);

        // The drawer titles identify the drawer layouts in accessibility mode.
        drawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.navigation_drawer));
        drawerLayout.setDrawerTitle(GravityCompat.END, getString(R.string.bookmarks));

        // Initialize the bookmarks database helper.  The `0` specifies a database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
        bookmarksDatabaseHelper = new BookmarksDatabaseHelper(this, null, null, 0);

        // Initialize `currentBookmarksFolder`.  `""` is the home folder in the database.
        currentBookmarksFolder = "";

        // Load the home folder, which is `""` in the database.
        loadBookmarksFolder();

        bookmarksListView.setOnItemClickListener((parent, view, position, id) -> {
            // Convert the id from long to int to match the format of the bookmarks database.
            int databaseId = (int) id;

            // Get the bookmark cursor for this ID.
            Cursor bookmarkCursor = bookmarksDatabaseHelper.getBookmark(databaseId);

            // Move the bookmark cursor to the first row.
            bookmarkCursor.moveToFirst();

            // Act upon the bookmark according to the type.
            if (bookmarkCursor.getInt(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.IS_FOLDER)) == 1) {  // The selected bookmark is a folder.
                // Store the new folder name in `currentBookmarksFolder`.
                currentBookmarksFolder = bookmarkCursor.getString(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));

                // Load the new folder.
                loadBookmarksFolder();
            } else {  // The selected bookmark is not a folder.
                // Load the bookmark URL.
                loadUrl(currentWebView, bookmarkCursor.getString(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_URL)));

                // Close the bookmarks drawer.
                drawerLayout.closeDrawer(GravityCompat.END);
            }

            // Close the `Cursor`.
            bookmarkCursor.close();
        });

        bookmarksListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Convert the database ID from `long` to `int`.
            int databaseId = (int) id;

            // Find out if the selected bookmark is a folder.
            boolean isFolder = bookmarksDatabaseHelper.isFolder(databaseId);

            if (isFolder) {
                // Save the current folder name, which is used in `onSaveEditBookmarkFolder()`.
                oldFolderNameString = bookmarksCursor.getString(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));

                // Instantiate the edit folder bookmark dialog.
                DialogFragment editBookmarkFolderDialog = EditBookmarkFolderDialog.folderDatabaseId(databaseId, currentWebView.getFavoriteOrDefaultIcon());

                // Show the edit folder bookmark dialog.
                editBookmarkFolderDialog.show(getSupportFragmentManager(), getString(R.string.edit_folder));
            } else {
                // Get the bookmark cursor for this ID.
                Cursor bookmarkCursor = bookmarksDatabaseHelper.getBookmark(databaseId);

                // Move the bookmark cursor to the first row.
                bookmarkCursor.moveToFirst();

                // Load the bookmark in a new tab but do not switch to the tab or close the drawer.
                addNewTab(bookmarkCursor.getString(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_URL)), false);
            }

            // Consume the event.
            return true;
        });

        // The drawer listener is used to update the navigation menu.
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if ((newState == DrawerLayout.STATE_SETTLING) || (newState == DrawerLayout.STATE_DRAGGING)) {  // A drawer is opening or closing.
                    // Update the navigation menu items if the WebView is not null.
                    if (currentWebView != null) {
                        navigationBackMenuItem.setEnabled(currentWebView.canGoBack());
                        navigationForwardMenuItem.setEnabled(currentWebView.canGoForward());
                        navigationHistoryMenuItem.setEnabled((currentWebView.canGoBack() || currentWebView.canGoForward()));
                        navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + currentWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                        // Hide the keyboard (if displayed).
                        inputMethodManager.hideSoftInputFromWindow(currentWebView.getWindowToken(), 0);
                    }

                    // Clear the focus from from the URL text box and the WebView.  This removes any text selection markers and context menus, which otherwise draw above the open drawers.
                    urlEditText.clearFocus();
                    currentWebView.clearFocus();
                }
            }
        });

        // Replace the header that `WebView` creates for `X-Requested-With` with a null value.  The default value is the application ID (com.jdots.browser.standard).
        customHeaders.put("X-Requested-With", "");

        // Inflate a bare WebView to get the default user agent.  It is not used to render content on the screen.
        @SuppressLint("InflateParams") View webViewLayout = getLayoutInflater().inflate(R.layout.bare_webview, null, false);

        // Get a handle for the WebView.
        WebView bareWebView = webViewLayout.findViewById(R.id.bare_webview);

        // Store the default user agent.
        webViewDefaultUserAgent = bareWebView.getSettings().getUserAgentString();

        // Destroy the bare WebView.
        bareWebView.destroy();
    }

    private void applyAppSettings() {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Store the values from the shared preferences in variables.
        incognitoModeEnabled = sharedPreferences.getBoolean("incognito_mode", false);
        boolean doNotTrackEnabled = sharedPreferences.getBoolean("do_not_track", false);
        sanitizeGoogleAnalytics = sharedPreferences.getBoolean("google_analytics", true);
        sanitizeFacebookClickIds = sharedPreferences.getBoolean("facebook_click_ids", true);
        sanitizeTwitterAmpRedirects = sharedPreferences.getBoolean("twitter_amp_redirects", true);
        proxyMode = sharedPreferences.getString("proxy", getString(R.string.proxy_default_value));
        fullScreenBrowsingModeEnabled = sharedPreferences.getBoolean("full_screen_browsing_mode", false);
        hideAppBar = sharedPreferences.getBoolean("hide_app_bar", true);
        scrollAppBar = sharedPreferences.getBoolean("scroll_app_bar", true);

        // Apply the saved proxy mode if the app has been restarted.
        if (savedProxyMode != null) {
            // Apply the saved proxy mode.
            proxyMode = savedProxyMode;

            // Reset the saved proxy mode.
            savedProxyMode = null;
        }

        // Get the search string.
        String searchString = sharedPreferences.getString("search", getString(R.string.search_default_value));

        // Set the search string.
        if (searchString.equals("Custom URL")) {  // A custom search string is used.
            searchURL = sharedPreferences.getString("search_custom_url", getString(R.string.search_custom_url_default_value));
        } else {  // A custom search string is not used.
            searchURL = searchString;
        }

        // Get a handle for the app compat delegate.
        AppCompatDelegate appCompatDelegate = getDelegate();

        // Get handles for the views that need to be modified.
        FrameLayout rootFrameLayout = findViewById(R.id.root_framelayout);
        ActionBar actionBar = appCompatDelegate.getSupportActionBar();

        // Remove the incorrect lint warning below that the action bar might be null.
        assert actionBar != null;

        // Apply the proxy.
        applyProxy(false);

        // Set Do Not Track status.
        if (doNotTrackEnabled) {
            customHeaders.put("DNT", "1");
        } else {
            customHeaders.remove("DNT");
        }

        // Get the current layout parameters.  Using coordinator layout parameters allows the `setBehavior()` command and using app bar layout parameters allows the `setScrollFlags()` command.
        CoordinatorLayout.LayoutParams swipeRefreshLayoutParams = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        AppBarLayout.LayoutParams findOnPageLayoutParams = (AppBarLayout.LayoutParams) findOnPageLinearLayout.getLayoutParams();
        AppBarLayout.LayoutParams tabsLayoutParams = (AppBarLayout.LayoutParams) tabsLinearLayout.getLayoutParams();

        // Add the scrolling behavior to the layout parameters.
        if (scrollAppBar) {
            // Enable scrolling of the app bar.
            swipeRefreshLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
            toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            findOnPageLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            tabsLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        } else {
            // Disable scrolling of the app bar.
            swipeRefreshLayoutParams.setBehavior(null);
            toolbarLayoutParams.setScrollFlags(0);
            findOnPageLayoutParams.setScrollFlags(0);
            tabsLayoutParams.setScrollFlags(0);

            // Expand the app bar if it is currently collapsed.
            appBarLayout.setExpanded(true);
        }

        // Apply the modified layout parameters.
        swipeRefreshLayout.setLayoutParams(swipeRefreshLayoutParams);
        toolbar.setLayoutParams(toolbarLayoutParams);
        findOnPageLinearLayout.setLayoutParams(findOnPageLayoutParams);
        tabsLinearLayout.setLayoutParams(tabsLayoutParams);

        // Set the app bar scrolling for each WebView.
        for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
            // Get the WebView tab fragment.
            WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

            // Get the fragment view.
            View fragmentView = webViewTabFragment.getView();

            // Only modify the WebViews if they exist.
            if (fragmentView != null) {
                // Get the nested scroll WebView from the tab fragment.
                NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                // Set the app bar scrolling.
                nestedScrollWebView.setNestedScrollingEnabled(scrollAppBar);
            }
        }

        // Update the full screen browsing mode settings.
        if (fullScreenBrowsingModeEnabled && inFullScreenBrowsingMode) {  // Clear Browser is currently in full screen browsing mode.
            // Update the visibility of the app bar, which might have changed in the settings.
            if (hideAppBar) {
                // Hide the tab linear layout.
                tabsLinearLayout.setVisibility(View.GONE);

                // Hide the action bar.
                actionBar.hide();
            } else {
                // Show the tab linear layout.
                tabsLinearLayout.setVisibility(View.VISIBLE);

                // Show the action bar.
                actionBar.show();
            }

            // Hide the banner ad in the free flavor.
            if (BuildConfig.FLAVOR.contentEquals("free")) {
                AdHelper.hideAd(findViewById(R.id.adview));
            }

            /* Hide the system bars.
             * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
             * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
             * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
             * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
             */
            rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {  // Clear Browser is not in full screen browsing mode.
            // Reset the full screen tracker, which could be true if Clear Browser was in full screen mode before entering settings and full screen browsing was disabled.
            inFullScreenBrowsingMode = false;

            // Show the tab linear layout.
            tabsLinearLayout.setVisibility(View.VISIBLE);

            // Show the action bar.
            actionBar.show();

            // Show the banner ad in the free flavor.
            if (BuildConfig.FLAVOR.contentEquals("free")) {
                // Initialize the ads.  If this isn't the first run, `loadAd()` will be automatically called instead.
                AdHelper.initializeAds(findViewById(R.id.adview), getApplicationContext(), getSupportFragmentManager(), getString(R.string.google_app_id), getString(R.string.ad_unit_id));
            }

            // Remove the `SYSTEM_UI` flags from the root frame layout.
            rootFrameLayout.setSystemUiVisibility(0);
        }
    }

    @Override
    public void navigateHistory(String url, int steps) {
        // Apply the domain settings.
        applyDomainSettings(currentWebView, url, false, false, false);

        // Load the history entry.
        currentWebView.goBackOrForward(steps);
    }

    @Override
    public void pinnedErrorGoBack() {
        // Get the current web back forward list.
        WebBackForwardList webBackForwardList = currentWebView.copyBackForwardList();

        // Get the previous entry URL.
        String previousUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();

        // Apply the domain settings.
        applyDomainSettings(currentWebView, previousUrl, false, false, false);

        // Go back.
        currentWebView.goBack();
    }

    // `reloadWebsite` is used if returning from the Domains activity.  Otherwise JavaScript might not function correctly if it is newly enabled.
    @SuppressLint("SetJavaScriptEnabled")
    private void applyDomainSettings(NestedScrollWebView nestedScrollWebView, String url, boolean resetTab, boolean reloadWebsite, boolean loadUrl) {
        // Store the current URL.
        nestedScrollWebView.setCurrentUrl(url);

        // Parse the URL into a URI.
        Uri uri = Uri.parse(url);

        // Extract the domain from `uri`.
        String newHostName = uri.getHost();

        // Strings don't like to be null.
        if (newHostName == null) {
            newHostName = "";
        }

        // Apply the domain settings if a new domain is being loaded or if the new domain is blank.  This allows the user to set temporary settings for JavaScript, cookies, DOM storage, etc.
        if (!nestedScrollWebView.getCurrentDomainName().equals(newHostName) || newHostName.equals("")) {
            // Set the new host name as the current domain name.
            nestedScrollWebView.setCurrentDomainName(newHostName);

            // Reset the ignoring of pinned domain information.
            nestedScrollWebView.setIgnorePinnedDomainInformation(false);

            // Clear any pinned SSL certificate or IP addresses.
            nestedScrollWebView.clearPinnedSslCertificate();
            nestedScrollWebView.clearPinnedIpAddresses();

            // Reset the favorite icon if specified.
            if (resetTab) {
                // Initialize the favorite icon.
                nestedScrollWebView.initializeFavoriteIcon();

                // Get the current page position.
                int currentPagePosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                // Get the corresponding tab.
                TabLayout.Tab tab = tabLayout.getTabAt(currentPagePosition);

                // Update the tab if it isn't null, which sometimes happens when restarting from the background.
                if (tab != null) {
                    // Get the tab custom view.
                    View tabCustomView = tab.getCustomView();

                    // Remove the warning below that the tab custom view might be null.
                    assert tabCustomView != null;

                    // Get the tab views.
                    ImageView tabFavoriteIconImageView = tabCustomView.findViewById(R.id.favorite_icon_imageview);
                    TextView tabTitleTextView = tabCustomView.findViewById(R.id.title_textview);

                    // Set the default favorite icon as the favorite icon for this tab.
                    tabFavoriteIconImageView.setImageBitmap(Bitmap.createScaledBitmap(nestedScrollWebView.getFavoriteOrDefaultIcon(), 64, 64, true));

                    // Set the loading title text.
                    tabTitleTextView.setText(R.string.loading);
                }
            }

            // Initialize the database handler.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
            DomainsDatabaseHelper domainsDatabaseHelper = new DomainsDatabaseHelper(this, null, null, 0);

            // Get a full cursor from `domainsDatabaseHelper`.
            Cursor domainNameCursor = domainsDatabaseHelper.getDomainNameCursorOrderedByDomain();

            // Initialize `domainSettingsSet`.
            Set<String> domainSettingsSet = new HashSet<>();

            // Get the domain name column index.
            int domainNameColumnIndex = domainNameCursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME);

            // Populate `domainSettingsSet`.
            for (int i = 0; i < domainNameCursor.getCount(); i++) {
                // Move `domainsCursor` to the current row.
                domainNameCursor.moveToPosition(i);

                // Store the domain name in `domainSettingsSet`.
                domainSettingsSet.add(domainNameCursor.getString(domainNameColumnIndex));
            }

            // Close `domainNameCursor.
            domainNameCursor.close();

            // Initialize the domain name in database variable.
            String domainNameInDatabase = null;

            // Check the hostname against the domain settings set.
            if (domainSettingsSet.contains(newHostName)) {  // The hostname is contained in the domain settings set.
                // Record the domain name in the database.
                domainNameInDatabase = newHostName;

                // Set the domain settings applied tracker to true.
                nestedScrollWebView.setDomainSettingsApplied(true);
            } else {  // The hostname is not contained in the domain settings set.
                // Set the domain settings applied tracker to false.
                nestedScrollWebView.setDomainSettingsApplied(false);
            }

            // Check all the subdomains of the host name against wildcard domains in the domain cursor.
            while (!nestedScrollWebView.getDomainSettingsApplied() && newHostName.contains(".")) {  // Stop checking if domain settings are already applied or there are no more `.` in the host name.
                if (domainSettingsSet.contains("*." + newHostName)) {  // Check the host name prepended by `*.`.
                    // Set the domain settings applied tracker to true.
                    nestedScrollWebView.setDomainSettingsApplied(true);

                    // Store the applied domain names as it appears in the database.
                    domainNameInDatabase = "*." + newHostName;
                }

                // Strip out the lowest subdomain of of the host name.
                newHostName = newHostName.substring(newHostName.indexOf(".") + 1);
            }


            // Get a handle for the shared preferences.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            // Store the general preference information.
            String defaultFontSizeString = sharedPreferences.getString("font_size", getString(R.string.font_size_default_value));
            String defaultUserAgentName = sharedPreferences.getString("user_agent", getString(R.string.user_agent_default_value));
            boolean defaultSwipeToRefresh = sharedPreferences.getBoolean("swipe_to_refresh", true);
            String webViewTheme = sharedPreferences.getString("webview_theme", getString(R.string.webview_theme_default_value));
            boolean wideViewport = sharedPreferences.getBoolean("wide_viewport", true);
            boolean displayWebpageImages = sharedPreferences.getBoolean("display_webpage_images", true);

            // Get the WebView theme entry values string array.
            String[] webViewThemeEntryValuesStringArray = getResources().getStringArray(R.array.webview_theme_entry_values);

            // Get a handle for the cookie manager.
            CookieManager cookieManager = CookieManager.getInstance();

            // Get handles for the views.
            RelativeLayout urlRelativeLayout = findViewById(R.id.url_relativelayout);
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);

            // Initialize the user agent array adapter and string array.
            ArrayAdapter<CharSequence> userAgentNamesArray = ArrayAdapter.createFromResource(this, R.array.user_agent_names, R.layout.spinner_item);
            String[] userAgentDataArray = getResources().getStringArray(R.array.user_agent_data);

            if (nestedScrollWebView.getDomainSettingsApplied()) {  // The url has custom domain settings.
                // Get a cursor for the current host and move it to the first position.
                Cursor currentDomainSettingsCursor = domainsDatabaseHelper.getCursorForDomainName(domainNameInDatabase);
                currentDomainSettingsCursor.moveToFirst();

                // Get the settings from the cursor.
                nestedScrollWebView.setDomainSettingsDatabaseId(currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper._ID)));
                nestedScrollWebView.getSettings().setJavaScriptEnabled(currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_JAVASCRIPT)) == 1);
                nestedScrollWebView.setAcceptFirstPartyCookies(currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES)) == 1);
                boolean domainThirdPartyCookiesEnabled = (currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES)) == 1);
                nestedScrollWebView.getSettings().setDomStorageEnabled(currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_DOM_STORAGE)) == 1);
                // Form data can be removed once the minimum API >= 26.
                boolean saveFormData = (currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FORM_DATA)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.EASYLIST,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYLIST)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.EASYPRIVACY,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYPRIVACY)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.ULTRALIST, currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ULTRALIST)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.ULTRAPRIVACY,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY)) == 1);
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.THIRD_PARTY_REQUESTS,
                        currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS)) == 1);
                String userAgentName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.USER_AGENT));
                int fontSize = currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.FONT_SIZE));
                int swipeToRefreshInt = currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SWIPE_TO_REFRESH));
                int webViewThemeInt = currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.WEBVIEW_THEME));
                int wideViewportInt = currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.WIDE_VIEWPORT));
                int displayWebpageImagesInt = currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.DISPLAY_IMAGES));
                boolean pinnedSslCertificate = (currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE)) == 1);
                String pinnedSslIssuedToCName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME));
                String pinnedSslIssuedToOName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION));
                String pinnedSslIssuedToUName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT));
                String pinnedSslIssuedByCName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME));
                String pinnedSslIssuedByOName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION));
                String pinnedSslIssuedByUName = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT));
                boolean pinnedIpAddresses = (currentDomainSettingsCursor.getInt(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_IP_ADDRESSES)) == 1);
                String pinnedHostIpAddresses = currentDomainSettingsCursor.getString(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.IP_ADDRESSES));

                // Get the pinned SSL date longs.
                long pinnedSslStartDateLong = currentDomainSettingsCursor.getLong(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_START_DATE));
                long pinnedSslEndDateLong = currentDomainSettingsCursor.getLong(currentDomainSettingsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_END_DATE));

                // Define the pinned SSL date variables.
                Date pinnedSslStartDate;
                Date pinnedSslEndDate;

                // Set the pinned SSL certificate start date to `null` if the saved date long is 0 because creating a new date results in an error if the input is 0.
                if (pinnedSslStartDateLong == 0) {
                    pinnedSslStartDate = null;
                } else {
                    pinnedSslStartDate = new Date(pinnedSslStartDateLong);
                }

                // Set the pinned SSL certificate end date to `null` if the saved date long is 0 because creating a new date results in an error if the input is 0.
                if (pinnedSslEndDateLong == 0) {
                    pinnedSslEndDate = null;
                } else {
                    pinnedSslEndDate = new Date(pinnedSslEndDateLong);
                }

                // Close the current host domain settings cursor.
                currentDomainSettingsCursor.close();

                // If there is a pinned SSL certificate, store it in the WebView.
                if (pinnedSslCertificate) {
                    nestedScrollWebView.setPinnedSslCertificate(pinnedSslIssuedToCName, pinnedSslIssuedToOName, pinnedSslIssuedToUName, pinnedSslIssuedByCName, pinnedSslIssuedByOName, pinnedSslIssuedByUName,
                            pinnedSslStartDate, pinnedSslEndDate);
                }

                // If there is a pinned IP address, store it in the WebView.
                if (pinnedIpAddresses) {
                    nestedScrollWebView.setPinnedIpAddresses(pinnedHostIpAddresses);
                }

                // Apply the cookie domain settings.
                cookieManager.setAcceptCookie(nestedScrollWebView.getAcceptFirstPartyCookies());

                // Set third-party cookies status if API >= 21.
                if (Build.VERSION.SDK_INT >= 21) {
                    cookieManager.setAcceptThirdPartyCookies(nestedScrollWebView, domainThirdPartyCookiesEnabled);
                }

                // Apply the form data setting if the API < 26.
                if (Build.VERSION.SDK_INT < 26) {
                    nestedScrollWebView.getSettings().setSaveFormData(saveFormData);
                }

                // Apply the font size.
                try {  // Try the specified font size to see if it is valid.
                    if (fontSize == 0) {  // Apply the default font size.
                        // Try to set the font size from the value in the app settings.
                        nestedScrollWebView.getSettings().setTextZoom(Integer.parseInt(defaultFontSizeString));
                    } else {  // Apply the font size from domain settings.
                        nestedScrollWebView.getSettings().setTextZoom(fontSize);
                    }
                } catch (Exception exception) {  // The specified font size is invalid
                    // Set the font size to be 100%
                    nestedScrollWebView.getSettings().setTextZoom(100);
                }

                // Set the user agent.
                if (userAgentName.equals(getString(R.string.system_default_user_agent))) {  // Use the system default user agent.
                    // Get the array position of the default user agent name.
                    int defaultUserAgentArrayPosition = userAgentNamesArray.getPosition(defaultUserAgentName);

                    // Set the user agent according to the system default.
                    switch (defaultUserAgentArrayPosition) {
                        case UNRECOGNIZED_USER_AGENT:  // The default user agent name is not on the canonical list.
                            // This is probably because it was set in an older version of Clear Browser before the switch to persistent user agent names.
                            nestedScrollWebView.getSettings().setUserAgentString(defaultUserAgentName);
                            break;

                        case SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                            // Set the user agent to `""`, which uses the default value.
                            nestedScrollWebView.getSettings().setUserAgentString("");
                            break;

                        case SETTINGS_CUSTOM_USER_AGENT:
                            // Set the default custom user agent.
                            nestedScrollWebView.getSettings().setUserAgentString(sharedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value)));
                            break;

                        default:
                            // Get the user agent string from the user agent data array
                            nestedScrollWebView.getSettings().setUserAgentString(userAgentDataArray[defaultUserAgentArrayPosition]);
                    }
                } else {  // Set the user agent according to the stored name.
                    // Get the array position of the user agent name.
                    int userAgentArrayPosition = userAgentNamesArray.getPosition(userAgentName);

                    switch (userAgentArrayPosition) {
                        case UNRECOGNIZED_USER_AGENT:  // The user agent name contains a custom user agent.
                            nestedScrollWebView.getSettings().setUserAgentString(userAgentName);
                            break;

                        case SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                            // Set the user agent to `""`, which uses the default value.
                            nestedScrollWebView.getSettings().setUserAgentString("");
                            break;

                        default:
                            // Get the user agent string from the user agent data array.
                            nestedScrollWebView.getSettings().setUserAgentString(userAgentDataArray[userAgentArrayPosition]);
                    }
                }

                // Set swipe to refresh.
                switch (swipeToRefreshInt) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        // Store the swipe to refresh status in the nested scroll WebView.
                        nestedScrollWebView.setSwipeToRefresh(defaultSwipeToRefresh);

                        // Update the swipe refresh layout.
                        if (defaultSwipeToRefresh) {  // Swipe to refresh is enabled.
                            // Only enable the swipe refresh layout if the WebView is scrolled to the top.  It is updated every time the scroll changes.
                            swipeRefreshLayout.setEnabled(currentWebView.getY() == 0);
                        } else {  // Swipe to refresh is disabled.
                            // Disable the swipe refresh layout.
                            swipeRefreshLayout.setEnabled(false);
                        }
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        // Store the swipe to refresh status in the nested scroll WebView.
                        nestedScrollWebView.setSwipeToRefresh(true);

                        // Only enable the swipe refresh layout if the WebView is scrolled to the top.  It is updated every time the scroll changes.
                        swipeRefreshLayout.setEnabled(currentWebView.getY() == 0);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        // Store the swipe to refresh status in the nested scroll WebView.
                        nestedScrollWebView.setSwipeToRefresh(false);

                        // Disable swipe to refresh.
                        swipeRefreshLayout.setEnabled(false);
                }

                // Check to see if WebView themes are supported.
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    // Set the WebView theme.
                    switch (webViewThemeInt) {
                        case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                            // Set the WebView theme.  A switch statement cannot be used because the WebView theme entry values string array is not a compile time constant.
                            if (webViewTheme.equals(webViewThemeEntryValuesStringArray[1])) {  // The light theme is selected.
                                // Turn off the WebView dark mode.
                                WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                            } else if (webViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
                                // Turn on the WebView dark mode.
                                WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                            } else {  // The system default theme is selected.
                                // Get the current system theme status.
                                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                                // Set the WebView theme according to the current system theme status.
                                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {  // The system is in day mode.
                                    // Turn off the WebView dark mode.
                                    WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                                } else {  // The system is in night mode.
                                    // Turn on the WebView dark mode.
                                    WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                                }
                            }
                            break;

                        case DomainsDatabaseHelper.LIGHT_THEME:
                            // Turn off the WebView dark mode.
                            WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                            break;

                        case DomainsDatabaseHelper.DARK_THEME:
                            // Turn on the WebView dark mode.
                            WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                            break;
                    }
                }

                // Set the viewport.
                switch (wideViewportInt) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        nestedScrollWebView.getSettings().setUseWideViewPort(wideViewport);
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        nestedScrollWebView.getSettings().setUseWideViewPort(true);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        nestedScrollWebView.getSettings().setUseWideViewPort(false);
                        break;
                }

                // Set the loading of webpage images.
                switch (displayWebpageImagesInt) {
                    case DomainsDatabaseHelper.SYSTEM_DEFAULT:
                        nestedScrollWebView.getSettings().setLoadsImagesAutomatically(displayWebpageImages);
                        break;

                    case DomainsDatabaseHelper.ENABLED:
                        nestedScrollWebView.getSettings().setLoadsImagesAutomatically(true);
                        break;

                    case DomainsDatabaseHelper.DISABLED:
                        nestedScrollWebView.getSettings().setLoadsImagesAutomatically(false);
                        break;
                }

                // Get the current theme status.
                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                // Set a background on the URL relative layout to indicate that custom domain settings are being used.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.url_bar_background_light_green, null));
                } else {
                    urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.url_bar_background_dark_blue, null));
                }
            } else {  // The new URL does not have custom domain settings.  Load the defaults.
                // Store the values from the shared preferences.
                nestedScrollWebView.getSettings().setJavaScriptEnabled(sharedPreferences.getBoolean("javascript", false));
                nestedScrollWebView.setAcceptFirstPartyCookies(sharedPreferences.getBoolean("first_party_cookies", false));
                boolean defaultThirdPartyCookiesEnabled = sharedPreferences.getBoolean("third_party_cookies", false);
                nestedScrollWebView.getSettings().setDomStorageEnabled(sharedPreferences.getBoolean("dom_storage", false));
                boolean saveFormData = sharedPreferences.getBoolean("save_form_data", false);  // Form data can be removed once the minimum API >= 26.
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.EASYLIST, sharedPreferences.getBoolean("easylist", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.EASYPRIVACY, sharedPreferences.getBoolean("easyprivacy", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST, sharedPreferences.getBoolean("fanboys_annoyance_list", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST, sharedPreferences.getBoolean("fanboys_social_blocking_list", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.ULTRALIST, sharedPreferences.getBoolean("ultralist", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.ULTRAPRIVACY, sharedPreferences.getBoolean("ultraprivacy", true));
                nestedScrollWebView.enableBlocklist(NestedScrollWebView.THIRD_PARTY_REQUESTS, sharedPreferences.getBoolean("block_all_third_party_requests", false));

                // Apply the default first-party cookie setting.
                cookieManager.setAcceptCookie(nestedScrollWebView.getAcceptFirstPartyCookies());

                // Apply the default font size setting.
                try {
                    // Try to set the font size from the value in the app settings.
                    nestedScrollWebView.getSettings().setTextZoom(Integer.parseInt(defaultFontSizeString));
                } catch (Exception exception) {
                    // If the app settings value is invalid, set the font size to 100%.
                    nestedScrollWebView.getSettings().setTextZoom(100);
                }

                // Apply the form data setting if the API < 26.
                if (Build.VERSION.SDK_INT < 26) {
                    nestedScrollWebView.getSettings().setSaveFormData(saveFormData);
                }

                // Store the swipe to refresh status in the nested scroll WebView.
                nestedScrollWebView.setSwipeToRefresh(defaultSwipeToRefresh);

                // Update the swipe refresh layout.
                if (defaultSwipeToRefresh) {  // Swipe to refresh is enabled.
                    // Only enable the swipe refresh layout if the WebView is scrolled to the top.  It is updated every time the scroll changes.
                    swipeRefreshLayout.setEnabled(currentWebView.getY() == 0);
                } else {  // Swipe to refresh is disabled.
                    // Disable the swipe refresh layout.
                    swipeRefreshLayout.setEnabled(false);
                }

                // Reset the pinned variables.
                nestedScrollWebView.setDomainSettingsDatabaseId(-1);

                // Set third-party cookies status if API >= 21.
                if (Build.VERSION.SDK_INT >= 21) {
                    cookieManager.setAcceptThirdPartyCookies(nestedScrollWebView, defaultThirdPartyCookiesEnabled);
                }

                // Get the array position of the user agent name.
                int userAgentArrayPosition = userAgentNamesArray.getPosition(defaultUserAgentName);

                // Set the user agent.
                switch (userAgentArrayPosition) {
                    case UNRECOGNIZED_USER_AGENT:  // The default user agent name is not on the canonical list.
                        // This is probably because it was set in an older version of Clear Browser before the switch to persistent user agent names.
                        nestedScrollWebView.getSettings().setUserAgentString(defaultUserAgentName);
                        break;

                    case SETTINGS_WEBVIEW_DEFAULT_USER_AGENT:
                        // Set the user agent to `""`, which uses the default value.
                        nestedScrollWebView.getSettings().setUserAgentString("");
                        break;

                    case SETTINGS_CUSTOM_USER_AGENT:
                        // Set the default custom user agent.
                        nestedScrollWebView.getSettings().setUserAgentString(sharedPreferences.getString("custom_user_agent", getString(R.string.custom_user_agent_default_value)));
                        break;

                    default:
                        // Get the user agent string from the user agent data array
                        nestedScrollWebView.getSettings().setUserAgentString(userAgentDataArray[userAgentArrayPosition]);
                }

                // Apply the WebView theme if supported by the installed WebView.
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    // Set the WebView theme.  A switch statement cannot be used because the WebView theme entry values string array is not a compile time constant.
                    if (webViewTheme.equals(webViewThemeEntryValuesStringArray[1])) {  // The light theme is selected.
                        // Turn off the WebView dark mode.
                        WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    } else if (webViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
                        // Turn on the WebView dark mode.
                        WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    } else {  // The system default theme is selected.
                        // Get the current system theme status.
                        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                        // Set the WebView theme according to the current system theme status.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {  // The system is in day mode.
                            // Turn off the WebView dark mode.
                            WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                        } else {  // The system is in night mode.
                            // Turn on the WebView dark mode.
                            WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                        }
                    }
                }

                // Set the viewport.
                nestedScrollWebView.getSettings().setUseWideViewPort(wideViewport);

                // Set the loading of webpage images.
                nestedScrollWebView.getSettings().setLoadsImagesAutomatically(displayWebpageImages);

                // Set a transparent background on URL edit text.
                urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.transparent, null));
            }

            // Close the domains database helper.
            domainsDatabaseHelper.close();

            // Update the privacy icons.
            updatePrivacyIcons(true);
        }

        // Reload the website if returning from the Domains activity.
        if (reloadWebsite) {
            nestedScrollWebView.reload();
        }

        // Load the URL if directed.  This makes sure that the domain settings are properly loaded before the URL.  By using `loadUrl()`, instead of `loadUrlFromBase()`, the Referer header will never be sent.
        if (loadUrl) {
            nestedScrollWebView.loadUrl(url, customHeaders);
        }
    }

    private void applyProxy(boolean reloadWebViews) {
        // Set the proxy according to the mode.  `this` refers to the current activity where an alert dialog might be displayed.
        ProxyHelper.setProxy(getApplicationContext(), appBarLayout, proxyMode);

        // Reset the waiting for proxy tracker.
        waitingForProxy = false;

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Update the user interface and reload the WebViews if requested.
        switch (proxyMode) {
            case ProxyHelper.NONE:
                // Initialize a color background typed value.
                TypedValue colorBackgroundTypedValue = new TypedValue();

                // Get the color background from the theme.
                getTheme().resolveAttribute(android.R.attr.colorBackground, colorBackgroundTypedValue, true);

                // Get the color background int from the typed value.
                int colorBackgroundInt = colorBackgroundTypedValue.data;

                // Set the default app bar layout background.
                appBarLayout.setBackgroundColor(colorBackgroundInt);
                break;

            case ProxyHelper.TOR:
                // Set the app bar background to indicate proxying through Orbot is enabled.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    appBarLayout.setBackgroundResource(R.color.blue_50);
                } else {
                    appBarLayout.setBackgroundResource(R.color.dark_blue_30);
                }

                // Check to see if Orbot is installed.
                try {
                    // Get the package manager.
                    PackageManager packageManager = getPackageManager();

                    // Check to see if Orbot is in the list.  This will throw an error and drop to the catch section if it isn't installed.
                    packageManager.getPackageInfo("org.torproject.android", 0);

                    // Check to see if the proxy is ready.
                    if (!orbotStatus.equals("ON")) {  // Orbot is not ready.
                        // Set the waiting for proxy status.
                        waitingForProxy = true;

                        // Show the waiting for proxy dialog if it isn't already displayed.
                        if (getSupportFragmentManager().findFragmentByTag(getString(R.string.waiting_for_proxy_dialog)) == null) {
                            // Get a handle for the waiting for proxy alert dialog.
                            DialogFragment waitingForProxyDialogFragment = new WaitingForProxyDialog();

                            // Display the waiting for proxy alert dialog.
                            waitingForProxyDialogFragment.show(getSupportFragmentManager(), getString(R.string.waiting_for_proxy_dialog));
                        }
                    }
                } catch (PackageManager.NameNotFoundException exception) {  // Orbot is not installed.
                    // Show the Orbot not installed dialog if it is not already displayed.
                    if (getSupportFragmentManager().findFragmentByTag(getString(R.string.proxy_not_installed_dialog)) == null) {
                        // Get a handle for the Orbot not installed alert dialog.
                        DialogFragment orbotNotInstalledDialogFragment = ProxyNotInstalledDialog.displayDialog(proxyMode);

                        // Display the Orbot not installed alert dialog.
                        orbotNotInstalledDialogFragment.show(getSupportFragmentManager(), getString(R.string.proxy_not_installed_dialog));
                    }
                }
                break;

            case ProxyHelper.I2P:
                // Set the app bar background to indicate proxying through Orbot is enabled.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    appBarLayout.setBackgroundResource(R.color.blue_50);
                } else {
                    appBarLayout.setBackgroundResource(R.color.dark_blue_30);
                }

                // Check to see if I2P is installed.
                try {
                    // Get the package manager.
                    PackageManager packageManager = getPackageManager();

                    // Check to see if I2P is in the list.  This will throw an error and drop to the catch section if it isn't installed.
                    packageManager.getPackageInfo("org.torproject.android", 0);
                } catch (PackageManager.NameNotFoundException exception) {  // I2P is not installed.
                    // Sow the I2P not installed dialog if it is not already displayed.
                    if (getSupportFragmentManager().findFragmentByTag(getString(R.string.proxy_not_installed_dialog)) == null) {
                        // Get a handle for the waiting for proxy alert dialog.
                        DialogFragment i2pNotInstalledDialogFragment = ProxyNotInstalledDialog.displayDialog(proxyMode);

                        // Display the I2P not installed alert dialog.
                        i2pNotInstalledDialogFragment.show(getSupportFragmentManager(), getString(R.string.proxy_not_installed_dialog));
                    }
                }
                break;

            case ProxyHelper.CUSTOM:
                // Set the app bar background to indicate proxying through Orbot is enabled.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    appBarLayout.setBackgroundResource(R.color.blue_50);
                } else {
                    appBarLayout.setBackgroundResource(R.color.dark_blue_30);
                }
                break;
        }

        // Reload the WebViews if requested and not waiting for the proxy.
        if (reloadWebViews && !waitingForProxy) {
            // Reload the WebViews.
            for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
                // Get the WebView tab fragment.
                WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

                // Get the fragment view.
                View fragmentView = webViewTabFragment.getView();

                // Only reload the WebViews if they exist.
                if (fragmentView != null) {
                    // Get the nested scroll WebView from the tab fragment.
                    NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                    // Reload the WebView.
                    nestedScrollWebView.reload();
                }
            }
        }
    }

    private void updatePrivacyIcons(boolean runInvalidateOptionsMenu) {
        // Only update the privacy icons if the options menu and the current WebView have already been populated.
        if ((optionsMenu != null) && (currentWebView != null)) {
            // Get handles for the menu items.
            MenuItem privacyMenuItem = optionsMenu.findItem(R.id.toggle_javascript);
            //  MenuItem firstPartyCookiesMenuItem = optionsMenu.findItem(R.id.toggle_first_party_cookies);
            MenuItem refreshMenuItem = optionsMenu.findItem(R.id.refresh);

            // Update the privacy icon.
            if (currentWebView.getSettings().getJavaScriptEnabled()) {  // JavaScript is enabled.
                privacyMenuItem.setIcon(R.drawable.javascript_enabled);
            } else if (currentWebView.getAcceptFirstPartyCookies()) {  // JavaScript is disabled but cookies are enabled.
                privacyMenuItem.setIcon(R.drawable.warning);
            } else {  // All the dangerous features are disabled.
                privacyMenuItem.setIcon(R.drawable.privacy_mode);
            }

            // Get the current theme status.
            int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            // Update the first-party cookies icon.
            if (currentWebView.getAcceptFirstPartyCookies()) {  // First-party cookies are enabled.
                //firstPartyCookiesMenuItem.setIcon(R.drawable.cookies_enabled);
            } else {  // First-party cookies are disabled.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    //  firstPartyCookiesMenuItem.setIcon(R.drawable.cookies_disabled_day);
                } else {
                    //  firstPartyCookiesMenuItem.setIcon(R.drawable.cookies_disabled_night);
                }
            }

            // Update the refresh icon.
            if (refreshMenuItem.getTitle() == getString(R.string.refresh)) {  // The refresh icon is displayed.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    refreshMenuItem.setIcon(R.drawable.refresh_enabled_day);
                } else {
                    refreshMenuItem.setIcon(R.drawable.refresh_enabled_night);
                }
            } else {  // The stop icon is displayed.
                // Set the icon according to the theme.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    refreshMenuItem.setIcon(R.drawable.close_blue_day);
                } else {
                    refreshMenuItem.setIcon(R.drawable.close_blue_night);
                }
            }

            // `invalidateOptionsMenu()` calls `onPrepareOptionsMenu()` and redraws the icons in the app bar.
            if (runInvalidateOptionsMenu) {
                invalidateOptionsMenu();
            }
        }
    }

    private void highlightUrlText() {
        // Get a handle for the URL edit text.
        EditText urlEditText = findViewById(R.id.url_edittext);

        // Only highlight the URL text if the box is not currently selected.
        if (!urlEditText.hasFocus()) {
            // Get the URL string.
            String urlString = urlEditText.getText().toString();

            // Highlight the URL according to the protocol.
            if (urlString.startsWith("file://") || urlString.startsWith("content://")) {  // This is a file or content URL.
                // De-emphasize everything before the file name.
                urlEditText.getText().setSpan(initialGrayColorSpan, 0, urlString.lastIndexOf("/") + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {  // This is a web URL.
                // Get the index of the `/` immediately after the domain name.
                int endOfDomainName = urlString.indexOf("/", (urlString.indexOf("//") + 2));

                // Create a base URL string.
                String baseUrl;

                // Get the base URL.
                if (endOfDomainName > 0) {  // There is at least one character after the base URL.
                    // Get the base URL.
                    baseUrl = urlString.substring(0, endOfDomainName);
                } else {  // There are no characters after the base URL.
                    // Set the base URL to be the entire URL string.
                    baseUrl = urlString;
                }

                // Get the index of the last `.` in the domain.
                int lastDotIndex = baseUrl.lastIndexOf(".");

                // Get the index of the penultimate `.` in the domain.
                int penultimateDotIndex = baseUrl.lastIndexOf(".", lastDotIndex - 1);

                // Markup the beginning of the URL.
                if (urlString.startsWith("http://")) {  // Highlight the protocol of connections that are not encrypted.
                    urlEditText.getText().setSpan(redColorSpan, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    // De-emphasize subdomains.
                    if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                        urlEditText.getText().setSpan(initialGrayColorSpan, 7, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                } else if (urlString.startsWith("https://")) {  // De-emphasize the protocol of connections that are encrypted.
                    if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                        // De-emphasize the protocol and the additional subdomains.
                        urlEditText.getText().setSpan(initialGrayColorSpan, 0, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    } else {  // There is only one subdomain in the domain name.
                        // De-emphasize only the protocol.
                        urlEditText.getText().setSpan(initialGrayColorSpan, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }

                // De-emphasize the text after the domain name.
                if (endOfDomainName > 0) {
                    urlEditText.getText().setSpan(finalGrayColorSpan, endOfDomainName, urlString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    private void loadBookmarksFolder() {
        // Update the bookmarks cursor with the contents of the bookmarks database for the current folder.
        bookmarksCursor = bookmarksDatabaseHelper.getBookmarksByDisplayOrder(currentBookmarksFolder);

        // Populate the bookmarks cursor adapter.  `this` specifies the `Context`.  `false` disables `autoRequery`.
        bookmarksCursorAdapter = new CursorAdapter(this, bookmarksCursor, false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                // Inflate the individual item layout.  `false` does not attach it to the root.
                return getLayoutInflater().inflate(R.layout.bookmarks_drawer_item_linearlayout, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Get handles for the views.
                ImageView bookmarkFavoriteIcon = view.findViewById(R.id.bookmark_favorite_icon);
                TextView bookmarkNameTextView = view.findViewById(R.id.bookmark_name);

                // Get the favorite icon byte array from the cursor.
                byte[] favoriteIconByteArray = cursor.getBlob(cursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON));

                // Convert the byte array to a `Bitmap` beginning at the first byte and ending at the last.
                Bitmap favoriteIconBitmap = BitmapFactory.decodeByteArray(favoriteIconByteArray, 0, favoriteIconByteArray.length);

                // Display the bitmap in `bookmarkFavoriteIcon`.
                bookmarkFavoriteIcon.setImageBitmap(favoriteIconBitmap);

                // Get the bookmark name from the cursor and display it in `bookmarkNameTextView`.
                String bookmarkNameString = cursor.getString(cursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));
                bookmarkNameTextView.setText(bookmarkNameString);

                // Make the font bold for folders.
                if (cursor.getInt(cursor.getColumnIndex(BookmarksDatabaseHelper.IS_FOLDER)) == 1) {
                    bookmarkNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {  // Reset the font to default for normal bookmarks.
                    bookmarkNameTextView.setTypeface(Typeface.DEFAULT);
                }
            }
        };

        // Get a handle for the bookmarks list view.
        ListView bookmarksListView = findViewById(R.id.bookmarks_drawer_listview);

        // Populate the list view with the adapter.
        bookmarksListView.setAdapter(bookmarksCursorAdapter);

        // Get a handle for the bookmarks title text view.
        TextView bookmarksTitleTextView = findViewById(R.id.bookmarks_title_textview);

        // Set the bookmarks drawer title.
        if (currentBookmarksFolder.isEmpty()) {
            bookmarksTitleTextView.setText(R.string.bookmarks);
        } else {
            bookmarksTitleTextView.setText(currentBookmarksFolder);
        }
    }

    private void openWithApp(String url) {
        // Create an open with app intent with `ACTION_VIEW`.
        Intent openWithAppIntent = new Intent(Intent.ACTION_VIEW);

        // Set the URI but not the MIME type.  This should open all available apps.
        openWithAppIntent.setData(Uri.parse(url));

        // Flag the intent to open in a new task.
        openWithAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Try the intent.
        try {
            // Show the chooser.
            startActivity(openWithAppIntent);
        } catch (ActivityNotFoundException exception) {  // There are no apps available to open the URL.
            // Show a snackbar with the error.
            Snackbar.make(currentWebView, getString(R.string.error) + "  " + exception, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void openWithBrowser(String url) {
        // Create an open with browser intent with `ACTION_VIEW`.
        Intent openWithBrowserIntent = new Intent(Intent.ACTION_VIEW);

        // Set the URI and the MIME type.  `"text/html"` should load browser options.
        openWithBrowserIntent.setDataAndType(Uri.parse(url), "text/html");

        // Flag the intent to open in a new task.
        openWithBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Try the intent.
        try {
            // Show the chooser.
            startActivity(openWithBrowserIntent);
        } catch (ActivityNotFoundException exception) {  // There are no browsers available to open the URL.
            // Show a snackbar with the error.
            Snackbar.make(currentWebView, getString(R.string.error) + "  " + exception, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private String sanitizeUrl(String url) {
        // Sanitize Google Analytics.
        if (sanitizeGoogleAnalytics) {
            // Remove `?utm_`.
            if (url.contains("?utm_")) {
                url = url.substring(0, url.indexOf("?utm_"));
            }

            // Remove `&utm_`.
            if (url.contains("&utm_")) {
                url = url.substring(0, url.indexOf("&utm_"));
            }
        }

        // Sanitize Facebook Click IDs.
        if (sanitizeFacebookClickIds) {
            // Remove `?fbclid=`.
            if (url.contains("?fbclid=")) {
                url = url.substring(0, url.indexOf("?fbclid="));
            }

            // Remove `&fbclid=`.
            if (url.contains("&fbclid=")) {
                url = url.substring(0, url.indexOf("&fbclid="));
            }

            // Remove `?fbadid=`.
            if (url.contains("?fbadid=")) {
                url = url.substring(0, url.indexOf("?fbadid="));
            }

            // Remove `&fbadid=`.
            if (url.contains("&fbadid=")) {
                url = url.substring(0, url.indexOf("&fbadid="));
            }
        }

        // Sanitize Twitter AMP redirects.
        if (sanitizeTwitterAmpRedirects) {
            // Remove `?amp=1`.
            if (url.contains("?amp=1")) {
                url = url.substring(0, url.indexOf("?amp=1"));
            }
        }

        // Return the sanitized URL.
        return url;
    }

    public void finishedPopulatingBlocklists(ArrayList<ArrayList<List<String[]>>> combinedBlocklists) {
        // Store the blocklists.
        easyList = combinedBlocklists.get(0);
        easyPrivacy = combinedBlocklists.get(1);
        fanboysAnnoyanceList = combinedBlocklists.get(2);
        fanboysSocialList = combinedBlocklists.get(3);
        ultraList = combinedBlocklists.get(4);
        ultraPrivacy = combinedBlocklists.get(5);

        // Check to see if the activity has been restarted with a saved state.
        if ((savedStateArrayList == null) || (savedStateArrayList.size() == 0)) {  // The activity has not been restarted or it was restarted on start to force the night theme.
            // Add the first tab.
            addNewTab("", true);
        } else {  // The activity has been restarted.
            // Restore each tab.  Once the minimum API >= 24, a `forEach()` command can be used.
            for (int i = 0; i < savedStateArrayList.size(); i++) {
                // Add a new tab.
                tabLayout.addTab(tabLayout.newTab());

                // Get the new tab.
                TabLayout.Tab newTab = tabLayout.getTabAt(i);

                // Remove the lint warning below that the current tab might be null.
                assert newTab != null;

                // Set a custom view on the new tab.
                newTab.setCustomView(R.layout.tab_custom_view);

                // Add the new page.
                webViewPagerAdapter.restorePage(savedStateArrayList.get(i), savedNestedScrollWebViewStateArrayList.get(i));
            }

            // Reset the saved state variables.
            savedStateArrayList = null;
            savedNestedScrollWebViewStateArrayList = null;

            // Restore the selected tab position.
            if (savedTabPosition == 0) {  // The first tab is selected.
                // Set the first page as the current WebView.
                setCurrentWebView(0);
            } else {  // the first tab is not selected.
                // Move to the selected tab.
                webViewPager.setCurrentItem(savedTabPosition);
            }

            // Get the intent that started the app.
            Intent intent = getIntent();

            // Reset the intent.  This prevents a duplicate tab from being created on restart.
            setIntent(new Intent());

            // Get the information from the intent.
            String intentAction = intent.getAction();
            Uri intentUriData = intent.getData();
            String intentStringExtra = intent.getStringExtra(Intent.EXTRA_TEXT);

            // Determine if this is a web search.
            boolean isWebSearch = ((intentAction != null) && intentAction.equals(Intent.ACTION_WEB_SEARCH));

            // Only process the URI if it contains data or it is a web search.  If the user pressed the desktop icon after the app was already running the URI will be null.
            if (intentUriData != null || intentStringExtra != null || isWebSearch) {
                // Get the shared preferences.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                // Create a URL string.
                String url;

                // If the intent action is a web search, perform the search.
                if (isWebSearch) {  // The intent is a web search.
                    // Create an encoded URL string.
                    String encodedUrlString;

                    // Sanitize the search input and convert it to a search.
                    try {
                        encodedUrlString = URLEncoder.encode(intent.getStringExtra(SearchManager.QUERY), "UTF-8");
                    } catch (UnsupportedEncodingException exception) {
                        encodedUrlString = "";
                    }

                    // Add the base search URL.
                    url = searchURL + encodedUrlString;
                } else if (intentUriData != null) {  // The intent contains a URL formatted as a URI.
                    // Set the intent data as the URL.
                    url = intentUriData.toString();
                } else {  // The intent contains a string, which might be a URL.
                    // Set the intent string as the URL.
                    url = intentStringExtra;
                }

                // Add a new tab if specified in the preferences.
                if (sharedPreferences.getBoolean("open_intents_in_new_tab", true)) {  // Load the URL in a new tab.
                    // Set the loading new intent flag.
                    loadingNewIntent = true;

                    // Add a new tab.
                    addNewTab(url, true);
                } else {  // Load the URL in the current tab.
                    // Make it so.
                    loadUrl(currentWebView, url);
                }
            }
        }
    }

    public void addTab(View view) {
        // Add a new tab with a blank URL.
        addNewTab("", true);
    }

    private void addNewTab(String url, boolean moveToTab) {
        // Get the new page number.  The page numbers are 0 indexed, so the new page number will match the current count.
        int newTabNumber = tabLayout.getTabCount();

        // Add a new tab.
        tabLayout.addTab(tabLayout.newTab());

        // Get the new tab.
        TabLayout.Tab newTab = tabLayout.getTabAt(newTabNumber);

        // Remove the lint warning below that the current tab might be null.
        assert newTab != null;

        // Set a custom view on the new tab.
        newTab.setCustomView(R.layout.tab_custom_view);

        // Add the new WebView page.
        webViewPagerAdapter.addPage(newTabNumber, webViewPager, url, moveToTab);
    }

    public void closeTab(View view) {
        // Run the command according to the number of tabs.
        if (tabLayout.getTabCount() > 1) {  // There is more than one tab open.
            // Close the current tab.
            closeCurrentTab();
        } else {  // There is only one tab open.
            clearAndExit();
        }
    }

    private void closeCurrentTab() {
        // Pause the current WebView.  This prevents buffered audio from playing after the tab is closed.
        currentWebView.onPause();

        // Get the current tab number.
        int currentTabNumber = tabLayout.getSelectedTabPosition();

        // Delete the current tab.
        tabLayout.removeTabAt(currentTabNumber);

        // Delete the current page.  If the selected page number did not change during the delete, it will return true, meaning that the current WebView must be reset.
        if (webViewPagerAdapter.deletePage(currentTabNumber, webViewPager)) {
            setCurrentWebView(currentTabNumber);
        }

        // Expand the app bar if it is currently collapsed.
        appBarLayout.setExpanded(true);
    }

    private void saveWebpageArchive(String filePath) {
        // Save the webpage archive.
        currentWebView.saveWebArchive(filePath);

        // Display a snackbar.
        Snackbar saveWebpageArchiveSnackbar = Snackbar.make(currentWebView, getString(R.string.file_saved) + "  " + filePath, Snackbar.LENGTH_SHORT);

        // Add an open option to the snackbar.
        saveWebpageArchiveSnackbar.setAction(R.string.open, (View view) -> {
            // Get a file for the file name string.
            File file = new File(filePath);

            // Declare a file URI variable.
            Uri fileUri;

            // Get the URI for the file according to the Android version.
            if (Build.VERSION.SDK_INT >= 24) {  // Use a file provider.
                fileUri = FileProvider.getUriForFile(this, getString(R.string.file_provider), file);
            } else {  // Get the raw file path URI.
                fileUri = Uri.fromFile(file);
            }

            // Get a handle for the content resolver.
            ContentResolver contentResolver = getContentResolver();

            // Create an open intent with `ACTION_VIEW`.
            Intent openIntent = new Intent(Intent.ACTION_VIEW);

            // Set the URI and the MIME type.
            openIntent.setDataAndType(fileUri, contentResolver.getType(fileUri));

            // Allow the app to read the file URI.
            openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Show the chooser.
            startActivity(Intent.createChooser(openIntent, getString(R.string.open)));
        });

        // Show the snackbar.
        saveWebpageArchiveSnackbar.show();
    }

    private void clearAndExit() {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Close the bookmarks cursor and database.
        bookmarksCursor.close();
        bookmarksDatabaseHelper.close();

        // Get the status of the clear everything preference.
        boolean clearEverything = sharedPreferences.getBoolean("clear_everything", true);

        // Get a handle for the runtime.
        Runtime runtime = Runtime.getRuntime();

        // Get the application's private data directory, which will be something like `/data/user/0/com.jdots.browser.standard`,
        // which links to `/data/data/com.jdots.browser.standard`.
        String privateDataDirectoryString = getApplicationInfo().dataDir;

        // Clear cookies.
        if (clearEverything || sharedPreferences.getBoolean("clear_cookies", true)) {
            // The command to remove cookies changed slightly in API 21.
            if (Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().removeAllCookies(null);
            } else {
                CookieManager.getInstance().removeAllCookie();
            }

            // Manually delete the cookies database, as `CookieManager` sometimes will not flush its changes to disk before `System.exit(0)` is run.
            try {
                // Two commands must be used because `Runtime.exec()` does not like `*`.
                Process deleteCookiesProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/Cookies");
                Process deleteCookiesJournalProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/Cookies-journal");

                // Wait until the processes have finished.
                deleteCookiesProcess.waitFor();
                deleteCookiesJournalProcess.waitFor();
            } catch (Exception exception) {
                // Do nothing if an error is thrown.
            }
        }

        // Clear DOM storage.
        if (clearEverything || sharedPreferences.getBoolean("clear_dom_storage", true)) {
            // Ask `WebStorage` to clear the DOM storage.
            WebStorage webStorage = WebStorage.getInstance();
            webStorage.deleteAllData();

            // Manually delete the DOM storage files and directories, as `WebStorage` sometimes will not flush its changes to disk before `System.exit(0)` is run.
            try {
                // A `String[]` must be used because the directory contains a space and `Runtime.exec` will otherwise not escape the string correctly.
                Process deleteLocalStorageProcess = runtime.exec(new String[]{"rm", "-rf", privateDataDirectoryString + "/app_webview/Local Storage/"});

                // Multiple commands must be used because `Runtime.exec()` does not like `*`.
                Process deleteIndexProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/app_webview/IndexedDB");
                Process deleteQuotaManagerProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/QuotaManager");
                Process deleteQuotaManagerJournalProcess = runtime.exec("rm -f " + privateDataDirectoryString + "/app_webview/QuotaManager-journal");
                Process deleteDatabaseProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/app_webview/databases");

                // Wait until the processes have finished.
                deleteLocalStorageProcess.waitFor();
                deleteIndexProcess.waitFor();
                deleteQuotaManagerProcess.waitFor();
                deleteQuotaManagerJournalProcess.waitFor();
                deleteDatabaseProcess.waitFor();
            } catch (Exception exception) {
                // Do nothing if an error is thrown.
            }
        }

        // Clear form data if the API < 26.
        if ((Build.VERSION.SDK_INT < 26) && (clearEverything || sharedPreferences.getBoolean("clear_form_data", true))) {
            WebViewDatabase webViewDatabase = WebViewDatabase.getInstance(this);
            webViewDatabase.clearFormData();

            // Manually delete the form data database, as `WebViewDatabase` sometimes will not flush its changes to disk before `System.exit(0)` is run.
            try {
                // A string array must be used because the database contains a space and `Runtime.exec` will not otherwise escape the string correctly.
                Process deleteWebDataProcess = runtime.exec(new String[]{"rm", "-f", privateDataDirectoryString + "/app_webview/Web Data"});
                Process deleteWebDataJournalProcess = runtime.exec(new String[]{"rm", "-f", privateDataDirectoryString + "/app_webview/Web Data-journal"});

                // Wait until the processes have finished.
                deleteWebDataProcess.waitFor();
                deleteWebDataJournalProcess.waitFor();
            } catch (Exception exception) {
                // Do nothing if an error is thrown.
            }
        }

        // Clear the logcat.
        if (clearEverything || sharedPreferences.getBoolean(getString(R.string.clear_logcat_key), true)) {
            try {
                // Clear the logcat.  `-c` clears the logcat.  `-b all` clears all the buffers (instead of just crash, main, and system).
                Process process = Runtime.getRuntime().exec("logcat -b all -c");

                // Wait for the process to finish.
                process.waitFor();
            } catch (IOException | InterruptedException exception) {
                // Do nothing.
            }
        }

        // Clear the cache.
        if (clearEverything || sharedPreferences.getBoolean("clear_cache", true)) {
            // Clear the cache from each WebView.
            for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
                // Get the WebView tab fragment.
                WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

                // Get the fragment view.
                View fragmentView = webViewTabFragment.getView();

                // Only clear the cache if the WebView exists.
                if (fragmentView != null) {
                    // Get the nested scroll WebView from the tab fragment.
                    NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                    // Clear the cache for this WebView.
                    nestedScrollWebView.clearCache(true);
                }
            }

            // Manually delete the cache directories.
            try {
                // Delete the main cache directory.
                Process deleteCacheProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/cache");

                // Delete the secondary `Service Worker` cache directory.
                // A string array must be used because the directory contains a space and `Runtime.exec` will otherwise not escape the string correctly.
                Process deleteServiceWorkerProcess = runtime.exec(new String[]{"rm", "-rf", privateDataDirectoryString + "/app_webview/Service Worker/"});

                // Wait until the processes have finished.
                deleteCacheProcess.waitFor();
                deleteServiceWorkerProcess.waitFor();
            } catch (Exception exception) {
                // Do nothing if an error is thrown.
            }
        }

        // Wipe out each WebView.
        for (int i = 0; i < webViewPagerAdapter.getCount(); i++) {
            // Get the WebView tab fragment.
            WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(i);

            // Get the fragment view.
            View fragmentView = webViewTabFragment.getView();

            // Only wipe out the WebView if it exists.
            if (fragmentView != null) {
                // Get the nested scroll WebView from the tab fragment.
                NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

                // Clear SSL certificate preferences for this WebView.
                nestedScrollWebView.clearSslPreferences();

                // Clear the back/forward history for this WebView.
                nestedScrollWebView.clearHistory();

                // Destroy the internal state of the WebView.
                nestedScrollWebView.destroy();
            }
        }

        // Clear the custom headers.
        customHeaders.clear();

        // Manually delete the `app_webview` folder, which contains the cookies, DOM storage, form data, and `Service Worker` cache.
        // See `https://code.google.com/p/android/issues/detail?id=233826&thanks=233826&ts=1486670530`.
        if (clearEverything) {
            try {
                // Delete the folder.
                Process deleteAppWebviewProcess = runtime.exec("rm -rf " + privateDataDirectoryString + "/app_webview");

                // Wait until the process has finished.
                deleteAppWebviewProcess.waitFor();
            } catch (Exception exception) {
                // Do nothing if an error is thrown.
            }
        }

        // Close Clear Browser.  `finishAndRemoveTask` also removes Clear Browser from the recent app list.
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // Remove the terminated program from RAM.  The status code is `0`.
        System.exit(0);
    }

    public void bookmarksBack(View view) {
        if (currentBookmarksFolder.isEmpty()) {  // The home folder is displayed.
            // close the bookmarks drawer.
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {  // A subfolder is displayed.
            // Place the former parent folder in `currentFolder`.
            currentBookmarksFolder = bookmarksDatabaseHelper.getParentFolderName(currentBookmarksFolder);

            // Load the new folder.
            loadBookmarksFolder();
        }
    }

    private void setCurrentWebView(int pageNumber) {
        // Get handles for the URL views.
        RelativeLayout urlRelativeLayout = findViewById(R.id.url_relativelayout);
        EditText urlEditText = findViewById(R.id.url_edittext);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);

        // Stop the swipe to refresh indicator if it is running
        swipeRefreshLayout.setRefreshing(false);

        // Get the WebView tab fragment.
        WebViewTabFragment webViewTabFragment = webViewPagerAdapter.getPageFragment(pageNumber);

        // Get the fragment view.
        View fragmentView = webViewTabFragment.getView();

        // Set the current WebView if the fragment view is not null.
        if (fragmentView != null) {  // The fragment has been populated.
            // Store the current WebView.
            currentWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

            // Update the status of swipe to refresh.
            if (currentWebView.getSwipeToRefresh()) {  // Swipe to refresh is enabled.
                // Enable the swipe refresh layout if the WebView is scrolled all the way to the top.  It is updated every time the scroll changes.
                swipeRefreshLayout.setEnabled(currentWebView.getY() == 0);
            } else {  // Swipe to refresh is disabled.
                // Disable the swipe refresh layout.
                swipeRefreshLayout.setEnabled(false);
            }

            // Get a handle for the cookie manager.
            CookieManager cookieManager = CookieManager.getInstance();

            // Set the first-party cookie status.
            cookieManager.setAcceptCookie(currentWebView.getAcceptFirstPartyCookies());

            // Update the privacy icons.  `true` redraws the icons in the app bar.
            updatePrivacyIcons(true);

            // Get a handle for the input method manager.
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            // Remove the lint warning below that the input method manager might be null.
            assert inputMethodManager != null;

            // Get the current URL.
            String url = currentWebView.getUrl();

            // Update the URL edit text if not loading a new intent.  Otherwise, this will be handled by `onPageStarted()` (if called) and `onPageFinished()`.
            if (!loadingNewIntent) {  // A new intent is not being loaded.
                if ((url == null) || url.equals("about:blank")) {  // The WebView is blank.
                    // Display the hint in the URL edit text.
                    urlEditText.setText("");

                    // Request focus for the URL text box.
                    urlEditText.requestFocus();

                    // Display the keyboard.
                    inputMethodManager.showSoftInput(urlEditText, 0);
                } else {  // The WebView has a loaded URL.
                    // Clear the focus from the URL text box.
                    urlEditText.clearFocus();

                    // Hide the soft keyboard.
                    inputMethodManager.hideSoftInputFromWindow(currentWebView.getWindowToken(), 0);

                    // Display the current URL in the URL text box.
                    urlEditText.setText(url);

                    // Highlight the URL text.
                    highlightUrlText();
                }
            } else {  // A new intent is being loaded.
                // Reset the loading new intent tracker.
                loadingNewIntent = false;
            }

            // Set the background to indicate the domain settings status.
            if (currentWebView.getDomainSettingsApplied()) {
                // Get the current theme status.
                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                // Set a green background on the URL relative layout to indicate that custom domain settings are being used.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.url_bar_background_light_green, null));
                } else {
                    urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.url_bar_background_dark_blue, null));
                }
            } else {
                urlRelativeLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.transparent, null));
            }
        } else {  // The fragment has not been populated.  Try again in 100 milliseconds.
            // Create a handler to set the current WebView.
            Handler setCurrentWebViewHandler = new Handler();

            // Create a runnable to set the current WebView.
            Runnable setCurrentWebWebRunnable = () -> {
                // Set the current WebView.
                setCurrentWebView(pageNumber);
            };

            // Try setting the current WebView again after 100 milliseconds.
            setCurrentWebViewHandler.postDelayed(setCurrentWebWebRunnable, 100);
        }
    }

    @Override
    public void initializeWebView(NestedScrollWebView nestedScrollWebView, int pageNumber, ProgressBar progressBar, String url, Boolean restoringState) {
        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the WebView theme.
        String webViewTheme = sharedPreferences.getString("webview_theme", getString(R.string.webview_theme_default_value));

        // Get the WebView theme entry values string array.
        String[] webViewThemeEntryValuesStringArray = getResources().getStringArray(R.array.webview_theme_entry_values);

        // Apply the WebView theme if supported by the installed WebView.
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            // Set the WebView theme.  A switch statement cannot be used because the WebView theme entry values string array is not a compile time constant.
            if (webViewTheme.equals(webViewThemeEntryValuesStringArray[1])) {  // The light theme is selected.
                // Turn off the WebView dark mode.
                WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);

                // Make the WebView visible. The WebView was created invisible in `webview_framelayout` to prevent a white background splash in night mode.
                // If the system is currently in night mode, showing the WebView will be handled in `onProgressChanged()`.
                nestedScrollWebView.setVisibility(View.VISIBLE);
            } else if (webViewTheme.equals(webViewThemeEntryValuesStringArray[2])) {  // The dark theme is selected.
                // Turn on the WebView dark mode.
                WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            } else {  // The system default theme is selected.
                // Get the current system theme status.
                int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                // Set the WebView theme according to the current system theme status.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {  // The system is in day mode.
                    // Turn off the WebView dark mode.
                    WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);

                    // Make the WebView visible. The WebView was created invisible in `webview_framelayout` to prevent a white background splash in night mode.
                    // If the system is currently in night mode, showing the WebView will be handled in `onProgressChanged()`.
                    nestedScrollWebView.setVisibility(View.VISIBLE);
                } else {  // The system is in night mode.
                    // Turn on the WebView dark mode.
                    WebSettingsCompat.setForceDark(nestedScrollWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                }
            }
        }

        // Get a handle for the app compat delegate.
        AppCompatDelegate appCompatDelegate = getDelegate();

        // Get handles for the activity views.
        FrameLayout rootFrameLayout = findViewById(R.id.root_framelayout);
        RelativeLayout mainContentRelativeLayout = findViewById(R.id.main_content_relativelayout);
        ActionBar actionBar = appCompatDelegate.getSupportActionBar();
        LinearLayout tabsLinearLayout = findViewById(R.id.tabs_linearlayout);
        EditText urlEditText = findViewById(R.id.url_edittext);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);

        // Remove the incorrect lint warning below that the action bar might be null.
        assert actionBar != null;

        // Get a handle for the activity
        Activity activity = this;

        // Get a handle for the input method manager.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Instantiate the blocklist helper.
        BlocklistHelper blocklistHelper = new BlocklistHelper();

        // Remove the lint warning below that the input method manager might be null.
        assert inputMethodManager != null;

        // Initialize the favorite icon.
        nestedScrollWebView.initializeFavoriteIcon();

        // Set the app bar scrolling.
        nestedScrollWebView.setNestedScrollingEnabled(sharedPreferences.getBoolean("scroll_app_bar", true));

        // Allow pinch to zoom.
        nestedScrollWebView.getSettings().setBuiltInZoomControls(true);

        // Hide zoom controls.
        nestedScrollWebView.getSettings().setDisplayZoomControls(false);

        // Don't allow mixed content (HTTP and HTTPS) on the same website.
        if (Build.VERSION.SDK_INT >= 21) {
            nestedScrollWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        // Set the WebView to load in overview mode (zoomed out to the maximum width).
        nestedScrollWebView.getSettings().setLoadWithOverviewMode(true);

        // Explicitly disable geolocation.
        nestedScrollWebView.getSettings().setGeolocationEnabled(false);

        // Create a double-tap gesture detector to toggle full-screen mode.
        GestureDetector doubleTapGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            // Override `onDoubleTap()`.  All other events are handled using the default settings.
            @Override
            public boolean onDoubleTap(MotionEvent event) {
                if (fullScreenBrowsingModeEnabled) {  // Only process the double-tap if full screen browsing mode is enabled.
                    // Toggle the full screen browsing mode tracker.
                    inFullScreenBrowsingMode = !inFullScreenBrowsingMode;

                    // Toggle the full screen browsing mode.
                    if (inFullScreenBrowsingMode) {  // Switch to full screen mode.
                        // Hide the app bar if specified.
                        if (hideAppBar) {
                            // Close the find on page bar if it is visible.
                            closeFindOnPage(null);

                            // Hide the tab linear layout.
                            tabsLinearLayout.setVisibility(View.GONE);

                            // Hide the action bar.
                            actionBar.hide();

                            // Check to see if the app bar is normally scrolled.
                            if (scrollAppBar) {  // The app bar is scrolled when it is displayed.
                                // Get the swipe refresh layout parameters.
                                CoordinatorLayout.LayoutParams swipeRefreshLayoutParams = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();

                                // Remove the off-screen scrolling layout.
                                swipeRefreshLayoutParams.setBehavior(null);
                            } else {  // The app bar is not scrolled when it is displayed.
                                // Remove the padding from the top of the swipe refresh layout.
                                swipeRefreshLayout.setPadding(0, 0, 0, 0);

                                // The swipe refresh circle must be moved above the now removed status bar location.
                                swipeRefreshLayout.setProgressViewOffset(false, -200, defaultProgressViewEndOffset);
                            }
                        }

                        // Hide the banner ad in the free flavor.
                        if (BuildConfig.FLAVOR.contentEquals("free")) {
                            AdHelper.hideAd(findViewById(R.id.adview));
                        }

                        /* Hide the system bars.
                         * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                         * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                         * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                         * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                         */
                        rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    } else {  // Switch to normal viewing mode.
                        // Show the app bar if it was hidden.
                        if (hideAppBar) {
                            // Show the tab linear layout.
                            tabsLinearLayout.setVisibility(View.VISIBLE);

                            // Show the action bar.
                            actionBar.show();

                            // Check to see if the app bar is normally scrolled.
                            if (scrollAppBar) {  // The app bar is scrolled when it is displayed.
                                // Get the swipe refresh layout parameters.
                                CoordinatorLayout.LayoutParams swipeRefreshLayoutParams = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();

                                // Add the off-screen scrolling layout.
                                swipeRefreshLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
                            } else {  // The app bar is not scrolled when it is displayed.
                                // The swipe refresh layout must be manually moved below the app bar layout.
                                swipeRefreshLayout.setPadding(0, appBarHeight, 0, 0);

                                // The swipe to refresh circle doesn't always hide itself completely unless it is moved up 10 pixels.
                                swipeRefreshLayout.setProgressViewOffset(false, defaultProgressViewStartOffset - 10 + appBarHeight, defaultProgressViewEndOffset + appBarHeight);
                            }
                        }

                        // Show the banner ad in the free flavor.
                        if (BuildConfig.FLAVOR.contentEquals("free")) {
                            // Reload the ad.
                            AdHelper.loadAd(findViewById(R.id.adview), getApplicationContext(), getString(R.string.ad_unit_id));
                        }

                        // Remove the `SYSTEM_UI` flags from the root frame layout.
                        rootFrameLayout.setSystemUiVisibility(0);
                    }

                    // Consume the double-tap.
                    return true;
                } else { // Do not consume the double-tap because full screen browsing mode is disabled.
                    return false;
                }
            }
        });

        // Pass all touch events on the WebView through the double-tap gesture detector.
        nestedScrollWebView.setOnTouchListener((View view, MotionEvent event) -> {
            // Call `performClick()` on the view, which is required for accessibility.
            view.performClick();

            // Send the event to the gesture detector.
            return doubleTapGestureDetector.onTouchEvent(event);
        });

        // Register the WebView for a context menu.  This is used to see link targets and download images.
        registerForContextMenu(nestedScrollWebView);

        // Allow the downloading of files.
        nestedScrollWebView.setDownloadListener((String downloadUrl, String userAgent, String contentDisposition, String mimetype, long contentLength) -> {
            // Define a formatted file size string.
            String formattedFileSizeString;

            // Process the content length if it contains data.
            if (contentLength > 0) {  // The content length is greater than 0.
                // Format the content length as a string.
                formattedFileSizeString = NumberFormat.getInstance().format(contentLength) + " " + getString(R.string.bytes);
            } else {  // The content length is not greater than 0.
                // Set the formatted file size string to be `unknown size`.
                formattedFileSizeString = getString(R.string.unknown_size);
            }

            // Get the file name from the content disposition.
            String fileNameString = PrepareSaveDialog.getFileNameFromHeaders(this, contentDisposition, mimetype, downloadUrl);

            // Instantiate the save dialog.
            DialogFragment saveDialogFragment = SaveWebpageDialog.saveWebpage(StoragePermissionDialog.SAVE_URL, downloadUrl, formattedFileSizeString, fileNameString, userAgent,
                    nestedScrollWebView.getAcceptFirstPartyCookies());

            // Show the save dialog.  It must be named `save_dialog` so that the file picker can update the file name.
            saveDialogFragment.show(getSupportFragmentManager(), getString(R.string.save_dialog));
        });

        // Update the find on page count.
        nestedScrollWebView.setFindListener(new WebView.FindListener() {
            // Get a handle for `findOnPageCountTextView`.
            final TextView findOnPageCountTextView = findViewById(R.id.find_on_page_count_textview);

            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                if ((isDoneCounting) && (numberOfMatches == 0)) {  // There are no matches.
                    // Set `findOnPageCountTextView` to `0/0`.
                    findOnPageCountTextView.setText(R.string.zero_of_zero);
                } else if (isDoneCounting) {  // There are matches.
                    // `activeMatchOrdinal` is zero-based.
                    int activeMatch = activeMatchOrdinal + 1;

                    // Build the match string.
                    String matchString = activeMatch + "/" + numberOfMatches;

                    // Set `findOnPageCountTextView`.
                    findOnPageCountTextView.setText(matchString);
                }
            }
        });

        // Update the status of swipe to refresh based on the scroll position of the nested scroll WebView.  Also reinforce full screen browsing mode.
        // On API < 23, `getViewTreeObserver().addOnScrollChangedListener()` must be used, but it is a little bit buggy and appears to get garbage collected from time to time.
        if (Build.VERSION.SDK_INT >= 23) {
            nestedScrollWebView.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
                if (nestedScrollWebView.getSwipeToRefresh()) {
                    // Only enable swipe to refresh if the WebView is scrolled to the top.
                    swipeRefreshLayout.setEnabled(nestedScrollWebView.getScrollY() == 0);
                } else {
                    // Disable swipe to refresh.
                    swipeRefreshLayout.setEnabled(false);
                }

                // Reinforce the system UI visibility flags if in full screen browsing mode.
                // This hides the status and navigation bars, which are displayed if other elements are shown, like dialog boxes, the options menu, or the keyboard.
                if (inFullScreenBrowsingMode) {
                    /* Hide the system bars.
                     * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                     * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                     */
                    rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            });
        } else {
            nestedScrollWebView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                if (nestedScrollWebView.getSwipeToRefresh()) {
                    // Only enable swipe to refresh if the WebView is scrolled to the top.
                    swipeRefreshLayout.setEnabled(nestedScrollWebView.getScrollY() == 0);
                } else {
                    // Disable swipe to refresh.
                    swipeRefreshLayout.setEnabled(false);
                }


                // Reinforce the system UI visibility flags if in full screen browsing mode.
                // This hides the status and navigation bars, which are displayed if other elements are shown, like dialog boxes, the options menu, or the keyboard.
                if (inFullScreenBrowsingMode) {
                    /* Hide the system bars.
                     * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                     * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                     */
                    rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            });
        }

        // Set the web chrome client.
        nestedScrollWebView.setWebChromeClient(new WebChromeClient() {
            // Update the progress bar when a page is loading.
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Update the progress bar.
                progressBar.setProgress(progress);

                // Set the visibility of the progress bar.
                if (progress < 100) {
                    // Show the progress bar.
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    // Hide the progress bar.
                    progressBar.setVisibility(View.GONE);

                    //Stop the swipe to refresh indicator if it is running
                    swipeRefreshLayout.setRefreshing(false);

                    // Make the current WebView visible.  If this is a new tab, the current WebView would have been created invisible in `webview_framelayout` to prevent a white background splash in night mode.
                    nestedScrollWebView.setVisibility(View.VISIBLE);
                }
            }

            // Set the favorite icon when it changes.
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                // Only update the favorite icon if the website has finished loading.
                if (progressBar.getVisibility() == View.GONE) {
                    // Store the new favorite icon.
                    nestedScrollWebView.setFavoriteOrDefaultIcon(icon);

                    // Get the current page position.
                    int currentPosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                    // Get the current tab.
                    TabLayout.Tab tab = tabLayout.getTabAt(currentPosition);

                    // Check to see if the tab has been populated.
                    if (tab != null) {
                        // Get the custom view from the tab.
                        View tabView = tab.getCustomView();

                        // Check to see if the custom tab view has been populated.
                        if (tabView != null) {
                            // Get the favorite icon image view from the tab.
                            ImageView tabFavoriteIconImageView = tabView.findViewById(R.id.favorite_icon_imageview);

                            // Display the favorite icon in the tab.
                            tabFavoriteIconImageView.setImageBitmap(Bitmap.createScaledBitmap(icon, 64, 64, true));
                        }
                    }
                }
            }

            // Save a copy of the title when it changes.
            @Override
            public void onReceivedTitle(WebView view, String title) {
                // Get the current page position.
                int currentPosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                // Get the current tab.
                TabLayout.Tab tab = tabLayout.getTabAt(currentPosition);

                // Only populate the title text view if the tab has been fully created.
                if (tab != null) {
                    // Get the custom view from the tab.
                    View tabView = tab.getCustomView();

                    // Only populate the title text view if the tab view has been fully populated.
                    if (tabView != null) {
                        // Get the title text view from the tab.
                        TextView tabTitleTextView = tabView.findViewById(R.id.title_textview);

                        // Set the title according to the URL.
                        if (title.equals("about:blank")) {
                            // Set the title to indicate a new tab.
                            tabTitleTextView.setText(R.string.new_tab);
                        } else {
                            // Set the title as the tab text.
                            tabTitleTextView.setText(title);
                        }
                    }
                }
            }

            // Enter full screen video.
            @Override
            public void onShowCustomView(View video, CustomViewCallback callback) {
                // Get a handle for the full screen video frame layout.
                FrameLayout fullScreenVideoFrameLayout = findViewById(R.id.full_screen_video_framelayout);

                // Set the full screen video flag.
                displayingFullScreenVideo = true;

                // Pause the ad if this is the free flavor.
                if (BuildConfig.FLAVOR.contentEquals("free")) {
                    // The AdView is destroyed and recreated, which changes the ID, every time it is reloaded to handle possible rotations.
                    AdHelper.pauseAd(findViewById(R.id.adview));
                }

                // Hide the keyboard.
                inputMethodManager.hideSoftInputFromWindow(nestedScrollWebView.getWindowToken(), 0);

                // Hide the main content relative layout.
                mainContentRelativeLayout.setVisibility(View.GONE);

                /* Hide the system bars.
                 * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                 * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                 * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                 * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                 */
                rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                // Disable the sliding drawers.
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                // Add the video view to the full screen video frame layout.
                fullScreenVideoFrameLayout.addView(video);

                // Show the full screen video frame layout.
                fullScreenVideoFrameLayout.setVisibility(View.VISIBLE);

                // Disable the screen timeout while the video is playing.  YouTube does this automatically, but not all other videos do.
                fullScreenVideoFrameLayout.setKeepScreenOn(true);
            }

            // Exit full screen video.
            @Override
            public void onHideCustomView() {
                // Get a handle for the full screen video frame layout.
                FrameLayout fullScreenVideoFrameLayout = findViewById(R.id.full_screen_video_framelayout);

                // Re-enable the screen timeout.
                fullScreenVideoFrameLayout.setKeepScreenOn(false);

                // Unset the full screen video flag.
                displayingFullScreenVideo = false;

                // Remove all the views from the full screen video frame layout.
                fullScreenVideoFrameLayout.removeAllViews();

                // Hide the full screen video frame layout.
                fullScreenVideoFrameLayout.setVisibility(View.GONE);

                // Enable the sliding drawers.
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                // Show the main content relative layout.
                mainContentRelativeLayout.setVisibility(View.VISIBLE);

                // Apply the appropriate full screen mode flags.
                if (fullScreenBrowsingModeEnabled && inFullScreenBrowsingMode) {  // Clear Browser is currently in full screen browsing mode.
                    // Hide the app bar if specified.
                    if (hideAppBar) {
                        // Hide the tab linear layout.
                        tabsLinearLayout.setVisibility(View.GONE);

                        // Hide the action bar.
                        actionBar.hide();
                    }

                    // Hide the banner ad in the free flavor.
                    if (BuildConfig.FLAVOR.contentEquals("free")) {
                        AdHelper.hideAd(findViewById(R.id.adview));
                    }

                    /* Hide the system bars.
                     * SYSTEM_UI_FLAG_FULLSCREEN hides the status bar at the top of the screen.
                     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN makes the root frame layout fill the area that is normally reserved for the status bar.
                     * SYSTEM_UI_FLAG_HIDE_NAVIGATION hides the navigation bar on the bottom or right of the screen.
                     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY makes the status and navigation bars translucent and automatically re-hides them after they are shown.
                     */
                    rootFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {  // Switch to normal viewing mode.
                    // Remove the `SYSTEM_UI` flags from the root frame layout.
                    rootFrameLayout.setSystemUiVisibility(0);
                }

                // Reload the ad for the free flavor if not in full screen mode.
                if (BuildConfig.FLAVOR.contentEquals("free") && !inFullScreenBrowsingMode) {
                    // Reload the ad.
                    AdHelper.loadAd(findViewById(R.id.adview), getApplicationContext(), getString(R.string.ad_unit_id));
                }
            }

            // Upload files.
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // Show the file chooser if the device is running API >= 21.
                if (Build.VERSION.SDK_INT >= 21) {
                    // Store the file path callback.
                    fileChooserCallback = filePathCallback;

                    // Create an intent to open a chooser based on the file chooser parameters.
                    Intent fileChooserIntent = fileChooserParams.createIntent();

                    // Get a handle for the package manager.
                    PackageManager packageManager = getPackageManager();

                    // Check to see if the file chooser intent resolves to an installed package.
                    if (fileChooserIntent.resolveActivity(packageManager) != null) {  // The file chooser intent is fine.
                        // Start the file chooser intent.
                        startActivityForResult(fileChooserIntent, BROWSE_FILE_UPLOAD_REQUEST_CODE);
                    } else {  // The file chooser intent will cause a crash.
                        // Create a generic intent to open a chooser.
                        Intent genericFileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);

                        // Request an openable file.
                        genericFileChooserIntent.addCategory(Intent.CATEGORY_OPENABLE);

                        // Set the file type to everything.
                        genericFileChooserIntent.setType("*/*");

                        // Start the generic file chooser intent.
                        startActivityForResult(genericFileChooserIntent, BROWSE_FILE_UPLOAD_REQUEST_CODE);
                    }
                }
                return true;
            }
        });

        nestedScrollWebView.setWebViewClient(new WebViewClient() {
            // `shouldOverrideUrlLoading` makes this WebView the default handler for URLs inside the app, so that links are not kicked out to other apps.
            // The deprecated `shouldOverrideUrlLoading` must be used until API >= 24.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Sanitize the url.
                url = sanitizeUrl(url);

                // Handle the URL according to the type.
                if (url.startsWith("http")) {  // Load the URL in Clear Browser.
                    // Load the URL.  By using `loadUrl()`, instead of `loadUrlFromBase()`, the Referer header will never be sent.
                    loadUrl(nestedScrollWebView, url);

                    // Returning true indicates that Clear Browser is manually handling the loading of the URL.
                    // Custom headers cannot be added if false is returned and the WebView handles the loading of the URL.
                    return true;
                } else if (url.startsWith("mailto:")) {  // Load the email address in an external email program.
                    // Use `ACTION_SENDTO` instead of `ACTION_SEND` so that only email programs are launched.
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

                    // Parse the url and set it as the data for the intent.
                    emailIntent.setData(Uri.parse(url));

                    // Open the email program in a new task instead of as part of Clear Browser.
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        // Make it so.
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException exception) {
                        // Display a snackbar.
                        Snackbar.make(currentWebView, getString(R.string.error) + "  " + exception, Snackbar.LENGTH_INDEFINITE).show();
                    }


                    // Returning true indicates Clear Browser is handling the URL by creating an intent.
                    return true;
                } else if (url.startsWith("tel:")) {  // Load the phone number in the dialer.
                    // Open the dialer and load the phone number, but wait for the user to place the call.
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);

                    // Add the phone number to the intent.
                    dialIntent.setData(Uri.parse(url));

                    // Open the dialer in a new task instead of as part of Clear Browser.
                    dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        // Make it so.
                        startActivity(dialIntent);
                    } catch (ActivityNotFoundException exception) {
                        // Display a snackbar.
                        Snackbar.make(currentWebView, getString(R.string.error) + "  " + exception, Snackbar.LENGTH_INDEFINITE).show();
                    }

                    // Returning true indicates Clear Browser is handling the URL by creating an intent.
                    return true;
                } else {  // Load a system chooser to select an app that can handle the URL.
                    // Open an app that can handle the URL.
                    Intent genericIntent = new Intent(Intent.ACTION_VIEW);

                    // Add the URL to the intent.
                    genericIntent.setData(Uri.parse(url));

                    // List all apps that can handle the URL instead of just opening the first one.
                    genericIntent.addCategory(Intent.CATEGORY_BROWSABLE);

                    // Open the app in a new task instead of as part of Clear Browser.
                    genericIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Start the app or display a snackbar if no app is available to handle the URL.
                    try {
                        startActivity(genericIntent);
                    } catch (ActivityNotFoundException exception) {
                        Snackbar.make(nestedScrollWebView, getString(R.string.unrecognized_url) + "  " + url, Snackbar.LENGTH_SHORT).show();
                    }

                    // Returning true indicates Clear Browser is handling the URL by creating an intent.
                    return true;
                }
            }

            // Check requests against the block lists.  The deprecated `shouldInterceptRequest()` must be used until minimum API >= 21.
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // Check to see if the resource request is for the main URL.
                if (url.equals(nestedScrollWebView.getCurrentUrl())) {
                    // `return null` loads the resource request, which should never be blocked if it is the main URL.
                    return null;
                }

                // Wait until the blocklists have been populated.  When Clear Browser is being resumed after having the process killed in the background it will try to load the URLs immediately.
                while (ultraPrivacy == null) {
                    // The wait must be synchronized, which only lets one thread run on it at a time, or `java.lang.IllegalMonitorStateException` is thrown.
                    synchronized (this) {
                        try {
                            // Check to see if the blocklists have been populated after 100 ms.
                            wait(100);
                        } catch (InterruptedException exception) {
                            // Do nothing.
                        }
                    }
                }

                // Sanitize the URL.
                url = sanitizeUrl(url);

                // Create an empty web resource response to be used if the resource request is blocked.
                WebResourceResponse emptyWebResourceResponse = new WebResourceResponse("text/plain", "utf8", new ByteArrayInputStream("".getBytes()));

                // Reset the whitelist results tracker.
                String[] whitelistResultStringArray = null;

                // Initialize the third party request tracker.
                boolean isThirdPartyRequest = false;

                // Get the current URL.  `.getUrl()` throws an error because operations on the WebView cannot be made from this thread.
                String currentBaseDomain = nestedScrollWebView.getCurrentDomainName();

                // Store a copy of the current domain for use in later requests.
                String currentDomain = currentBaseDomain;

                // Nobody is happy when comparing null strings.
                if ((currentBaseDomain != null) && (url != null)) {
                    // Convert the request URL to a URI.
                    Uri requestUri = Uri.parse(url);

                    // Get the request host name.
                    String requestBaseDomain = requestUri.getHost();

                    // Only check for third-party requests if the current base domain is not empty and the request domain is not null.
                    if (!currentBaseDomain.isEmpty() && (requestBaseDomain != null)) {
                        // Determine the current base domain.
                        while (currentBaseDomain.indexOf(".", currentBaseDomain.indexOf(".") + 1) > 0) {  // There is at least one subdomain.
                            // Remove the first subdomain.
                            currentBaseDomain = currentBaseDomain.substring(currentBaseDomain.indexOf(".") + 1);
                        }

                        // Determine the request base domain.
                        while (requestBaseDomain.indexOf(".", requestBaseDomain.indexOf(".") + 1) > 0) {  // There is at least one subdomain.
                            // Remove the first subdomain.
                            requestBaseDomain = requestBaseDomain.substring(requestBaseDomain.indexOf(".") + 1);
                        }

                        // Update the third party request tracker.
                        isThirdPartyRequest = !currentBaseDomain.equals(requestBaseDomain);
                    }
                }

                // Get the current WebView page position.
                int webViewPagePosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                // Determine if the WebView is currently displayed.
                boolean webViewDisplayed = (webViewPagePosition == tabLayout.getSelectedTabPosition());

                // Block third-party requests if enabled.
                if (isThirdPartyRequest && nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.THIRD_PARTY_REQUESTS)) {
                    // Add the result to the resource requests.
                    nestedScrollWebView.addResourceRequest(new String[]{BlocklistHelper.REQUEST_THIRD_PARTY, url});

                    // Increment the blocked requests counters.
                    nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                    nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.THIRD_PARTY_REQUESTS);

                    // Update the titles of the blocklist menu items if the WebView is currently displayed.
                    if (webViewDisplayed) {
                        // Updating the UI must be run from the UI thread.
                        activity.runOnUiThread(() -> {
                            // Update the menu item titles.
                            navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                            // Update the options menu if it has been populated.
                            if (optionsMenu != null) {
                                //    optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                // optionsMenu.findItem(R.id.block_all_third_party_requests).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.THIRD_PARTY_REQUESTS) + " - " +
                                //        getString(R.string.block_all_third_party_requests));
                            }
                        });
                    }

                    // Return an empty web resource response.
                    return emptyWebResourceResponse;
                }

                // Check UltraList if it is enabled.
                if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.ULTRALIST)) {
                    // Check the URL against UltraList.
                    String[] ultraListResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, ultraList);

                    // Process the UltraList results.
                    if (ultraListResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched UltraLists's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{ultraListResults[0], ultraListResults[1], ultraListResults[2], ultraListResults[3], ultraListResults[4], ultraListResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.ULTRALIST);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //   optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    //   optionsMenu.findItem(R.id.ultralist).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.ULTRALIST) + " - " + getString(R.string.ultralist));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (ultraListResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched UltraList's whitelist.
                        // Add a whitelist entry to the resource requests array.
                        nestedScrollWebView.addResourceRequest(new String[]{ultraListResults[0], ultraListResults[1], ultraListResults[2], ultraListResults[3], ultraListResults[4], ultraListResults[5]});

                        // The resource request has been allowed by UltraPrivacy.  `return null` loads the requested resource.
                        return null;
                    }
                }

                // Check UltraPrivacy if it is enabled.
                if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.ULTRAPRIVACY)) {
                    // Check the URL against UltraPrivacy.
                    String[] ultraPrivacyResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, ultraPrivacy);

                    // Process the UltraPrivacy results.
                    if (ultraPrivacyResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched UltraPrivacy's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{ultraPrivacyResults[0], ultraPrivacyResults[1], ultraPrivacyResults[2], ultraPrivacyResults[3], ultraPrivacyResults[4],
                                ultraPrivacyResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.ULTRAPRIVACY);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //  optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    //  optionsMenu.findItem(R.id.ultraprivacy).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.ULTRAPRIVACY) + " - " + getString(R.string.ultraprivacy));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (ultraPrivacyResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched UltraPrivacy's whitelist.
                        // Add a whitelist entry to the resource requests array.
                        nestedScrollWebView.addResourceRequest(new String[]{ultraPrivacyResults[0], ultraPrivacyResults[1], ultraPrivacyResults[2], ultraPrivacyResults[3], ultraPrivacyResults[4],
                                ultraPrivacyResults[5]});

                        // The resource request has been allowed by UltraPrivacy.  `return null` loads the requested resource.
                        return null;
                    }
                }

                // Check EasyList if it is enabled.
                if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.EASYLIST)) {
                    // Check the URL against EasyList.
                    String[] easyListResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, easyList);

                    // Process the EasyList results.
                    if (easyListResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched EasyList's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{easyListResults[0], easyListResults[1], easyListResults[2], easyListResults[3], easyListResults[4], easyListResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.EASYLIST);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //   optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    //   optionsMenu.findItem(R.id.easylist).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.EASYLIST) + " - " + getString(R.string.easylist));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (easyListResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched EasyList's whitelist.
                        // Update the whitelist result string array tracker.
                        whitelistResultStringArray = new String[]{easyListResults[0], easyListResults[1], easyListResults[2], easyListResults[3], easyListResults[4], easyListResults[5]};
                    }
                }

                // Check EasyPrivacy if it is enabled.
                if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.EASYPRIVACY)) {
                    // Check the URL against EasyPrivacy.
                    String[] easyPrivacyResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, easyPrivacy);

                    // Process the EasyPrivacy results.
                    if (easyPrivacyResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched EasyPrivacy's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{easyPrivacyResults[0], easyPrivacyResults[1], easyPrivacyResults[2], easyPrivacyResults[3], easyPrivacyResults[4],
                                easyPrivacyResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.EASYPRIVACY);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //   optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    //  optionsMenu.findItem(R.id.easyprivacy).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.EASYPRIVACY) + " - " + getString(R.string.easyprivacy));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (easyPrivacyResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched EasyPrivacy's whitelist.
                        // Update the whitelist result string array tracker.
                        whitelistResultStringArray = new String[]{easyPrivacyResults[0], easyPrivacyResults[1], easyPrivacyResults[2], easyPrivacyResults[3], easyPrivacyResults[4], easyPrivacyResults[5]};
                    }
                }

                // Check Fanboy’s Annoyance List if it is enabled.
                if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST)) {
                    // Check the URL against Fanboy's Annoyance List.
                    String[] fanboysAnnoyanceListResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, fanboysAnnoyanceList);

                    // Process the Fanboy's Annoyance List results.
                    if (fanboysAnnoyanceListResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched Fanboy's Annoyance List's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{fanboysAnnoyanceListResults[0], fanboysAnnoyanceListResults[1], fanboysAnnoyanceListResults[2], fanboysAnnoyanceListResults[3],
                                fanboysAnnoyanceListResults[4], fanboysAnnoyanceListResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //  optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    // optionsMenu.findItem(R.id.fanboys_annoyance_list).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.FANBOYS_ANNOYANCE_LIST) + " - " +
                                    //         getString(R.string.fanboys_annoyance_list));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (fanboysAnnoyanceListResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched Fanboy's Annoyance List's whitelist.
                        // Update the whitelist result string array tracker.
                        whitelistResultStringArray = new String[]{fanboysAnnoyanceListResults[0], fanboysAnnoyanceListResults[1], fanboysAnnoyanceListResults[2], fanboysAnnoyanceListResults[3],
                                fanboysAnnoyanceListResults[4], fanboysAnnoyanceListResults[5]};
                    }
                } else if (nestedScrollWebView.isBlocklistEnabled(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST)) {  // Only check Fanboy’s Social Blocking List if Fanboy’s Annoyance List is disabled.
                    // Check the URL against Fanboy's Annoyance List.
                    String[] fanboysSocialListResults = blocklistHelper.checkBlocklist(currentDomain, url, isThirdPartyRequest, fanboysSocialList);

                    // Process the Fanboy's Social Blocking List results.
                    if (fanboysSocialListResults[0].equals(BlocklistHelper.REQUEST_BLOCKED)) {  // The resource request matched Fanboy's Social Blocking List's blacklist.
                        // Add the result to the resource requests.
                        nestedScrollWebView.addResourceRequest(new String[]{fanboysSocialListResults[0], fanboysSocialListResults[1], fanboysSocialListResults[2], fanboysSocialListResults[3],
                                fanboysSocialListResults[4], fanboysSocialListResults[5]});

                        // Increment the blocked requests counters.
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS);
                        nestedScrollWebView.incrementRequestsCount(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST);

                        // Update the titles of the blocklist menu items if the WebView is currently displayed.
                        if (webViewDisplayed) {
                            // Updating the UI must be run from the UI thread.
                            activity.runOnUiThread(() -> {
                                // Update the menu item titles.
                                navigationRequestsMenuItem.setTitle(getString(R.string.requests) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));

                                // Update the options menu if it has been populated.
                                if (optionsMenu != null) {
                                    //   optionsMenu.findItem(R.id.blocklists).setTitle(getString(R.string.blocklists) + " - " + nestedScrollWebView.getRequestsCount(NestedScrollWebView.BLOCKED_REQUESTS));
                                    // optionsMenu.findItem(R.id.fanboys_social_blocking_list).setTitle(nestedScrollWebView.getRequestsCount(NestedScrollWebView.FANBOYS_SOCIAL_BLOCKING_LIST) + " - " +
                                    //         getString(R.string.fanboys_social_blocking_list));
                                }
                            });
                        }

                        // The resource request was blocked.  Return an empty web resource response.
                        return emptyWebResourceResponse;
                    } else if (fanboysSocialListResults[0].equals(BlocklistHelper.REQUEST_ALLOWED)) {  // The resource request matched Fanboy's Social Blocking List's whitelist.
                        // Update the whitelist result string array tracker.
                        whitelistResultStringArray = new String[]{fanboysSocialListResults[0], fanboysSocialListResults[1], fanboysSocialListResults[2], fanboysSocialListResults[3],
                                fanboysSocialListResults[4], fanboysSocialListResults[5]};
                    }
                }

                // Add the request to the log because it hasn't been processed by any of the previous checks.
                if (whitelistResultStringArray != null) {  // The request was processed by a whitelist.
                    nestedScrollWebView.addResourceRequest(whitelistResultStringArray);
                } else {  // The request didn't match any blocklist entry.  Log it as a default request.
                    nestedScrollWebView.addResourceRequest(new String[]{BlocklistHelper.REQUEST_DEFAULT, url});
                }

                // The resource request has not been blocked.  `return null` loads the requested resource.
                return null;
            }

            // Handle HTTP authentication requests.
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                // Store the handler.
                nestedScrollWebView.setHttpAuthHandler(handler);

                // Instantiate an HTTP authentication dialog.
                DialogFragment httpAuthenticationDialogFragment = HttpAuthenticationDialog.displayDialog(host, realm, nestedScrollWebView.getWebViewFragmentId());

                // Show the HTTP authentication dialog.
                httpAuthenticationDialogFragment.show(getSupportFragmentManager(), getString(R.string.http_authentication));
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Get the preferences.
                boolean scrollAppBar = sharedPreferences.getBoolean("scroll_app_bar", true);

                // Set the top padding of the swipe refresh layout according to the app bar scrolling preference.  This can't be done in `appAppSettings()` because the app bar is not yet populated there.
                if (scrollAppBar || (inFullScreenBrowsingMode && hideAppBar)) {
                    // No padding is needed because it will automatically be placed below the app bar layout due to the scrolling layout behavior.
                    swipeRefreshLayout.setPadding(0, 0, 0, 0);

                    // The swipe to refresh circle doesn't always hide itself completely unless it is moved up 10 pixels.
                    swipeRefreshLayout.setProgressViewOffset(false, defaultProgressViewStartOffset - 10, defaultProgressViewEndOffset);
                } else {
                    // Get the app bar layout height.  This can't be done in `applyAppSettings()` because the app bar is not yet populated there.
                    appBarHeight = appBarLayout.getHeight();

                    // The swipe refresh layout must be manually moved below the app bar layout.
                    swipeRefreshLayout.setPadding(0, appBarHeight, 0, 0);

                    // The swipe to refresh circle doesn't always hide itself completely unless it is moved up 10 pixels.
                    swipeRefreshLayout.setProgressViewOffset(false, defaultProgressViewStartOffset - 10 + appBarHeight, defaultProgressViewEndOffset + appBarHeight);
                }

                // Reset the list of resource requests.
                nestedScrollWebView.clearResourceRequests();

                // Reset the requests counters.
                nestedScrollWebView.resetRequestsCounters();

                // Get the current page position.
                int currentPagePosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                // Update the URL text bar if the page is currently selected and the URL edit text is not currently being edited.
                if ((tabLayout.getSelectedTabPosition() == currentPagePosition) && !urlEditText.hasFocus()) {
                    // Display the formatted URL text.
                    urlEditText.setText(url);

                    // Apply text highlighting to `urlTextBox`.
                    highlightUrlText();

                    // Hide the keyboard.
                    inputMethodManager.hideSoftInputFromWindow(nestedScrollWebView.getWindowToken(), 0);
                }

                // Reset the list of host IP addresses.
                nestedScrollWebView.clearCurrentIpAddresses();

                // Get a URI for the current URL.
                Uri currentUri = Uri.parse(url);

                // Get the IP addresses for the host.
                new GetHostIpAddresses(activity, getSupportFragmentManager(), nestedScrollWebView).execute(currentUri.getHost());

                // Replace Refresh with Stop if the options menu has been created.  (The first WebView typically begins loading before the menu items are instantiated.)
                if (optionsMenu != null) {
                    // Get a handle for the refresh menu item.
                    MenuItem refreshMenuItem = optionsMenu.findItem(R.id.refresh);

                    // Set the title.
                    refreshMenuItem.setTitle(R.string.stop);

                    // Get the app bar and theme preferences.
                    boolean displayAdditionalAppBarIcons = sharedPreferences.getBoolean(getString(R.string.display_additional_app_bar_icons_key), false);

                    // If the icon is displayed in the AppBar, set it according to the theme.
                    if (displayAdditionalAppBarIcons) {
                        // Get the current theme status.
                        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                        // Set the stop icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            refreshMenuItem.setIcon(R.drawable.close_blue_day);
                        } else {
                            refreshMenuItem.setIcon(R.drawable.close_blue_night);
                        }
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Flush any cookies to persistent storage.  The cookie manager has become very lazy about flushing cookies in recent versions.
                if (nestedScrollWebView.getAcceptFirstPartyCookies() && Build.VERSION.SDK_INT >= 21) {
                    CookieManager.getInstance().flush();
                }

                // Update the Refresh menu item if the options menu has been created.
                if (optionsMenu != null) {
                    // Get a handle for the refresh menu item.
                    MenuItem refreshMenuItem = optionsMenu.findItem(R.id.refresh);

                    // Reset the Refresh title.
                    refreshMenuItem.setTitle(R.string.refresh);

                    // Get the app bar and theme preferences.
                    boolean displayAdditionalAppBarIcons = sharedPreferences.getBoolean(getString(R.string.display_additional_app_bar_icons_key), false);

                    // If the icon is displayed in the app bar, reset it according to the theme.
                    if (displayAdditionalAppBarIcons) {
                        // Get the current theme status.
                        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                        // Set the icon according to the theme.
                        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                            refreshMenuItem.setIcon(R.drawable.refresh_enabled_day);
                        } else {
                            refreshMenuItem.setIcon(R.drawable.refresh_enabled_night);
                        }
                    }
                }

                // Clear the cache, history, and logcat if Incognito Mode is enabled.
                if (incognitoModeEnabled) {
                    // Clear the cache.  `true` includes disk files.
                    nestedScrollWebView.clearCache(true);

                    // Clear the back/forward history.
                    nestedScrollWebView.clearHistory();

                    // Manually delete cache folders.
                    try {
                        // Get the application's private data directory, which will be something like `/data/user/0/com.jdots.browser.standard`,
                        // which links to `/data/data/com.jdots.browser.standard`.
                        String privateDataDirectoryString = getApplicationInfo().dataDir;

                        // Delete the main cache directory.
                        Runtime.getRuntime().exec("rm -rf " + privateDataDirectoryString + "/cache");

                        // Delete the secondary `Service Worker` cache directory.
                        // A `String[]` must be used because the directory contains a space and `Runtime.exec` will not escape the string correctly otherwise.
                        Runtime.getRuntime().exec(new String[]{"rm", "-rf", privateDataDirectoryString + "/app_webview/Service Worker/"});
                    } catch (IOException exception) {
                        // Do nothing if an error is thrown.
                    }

                    // Clear the logcat.
                    try {
                        // Clear the logcat.  `-c` clears the logcat.  `-b all` clears all the buffers (instead of just crash, main, and system).
                        Runtime.getRuntime().exec("logcat -b all -c");
                    } catch (IOException exception) {
                        // Do nothing.
                    }
                }

                // Get the current page position.
                int currentPagePosition = webViewPagerAdapter.getPositionForId(nestedScrollWebView.getWebViewFragmentId());

                // Get the current URL from the nested scroll WebView.  This is more accurate than using the URL passed into the method, which is sometimes not the final one.
                String currentUrl = nestedScrollWebView.getUrl();

                // Get the current tab.
                TabLayout.Tab tab = tabLayout.getTabAt(currentPagePosition);

                // Update the URL text bar if the page is currently selected and the user is not currently typing in the URL edit text.
                // Crash records show that, in some crazy way, it is possible for the current URL to be blank at this point.
                // Probably some sort of race condition when Clear Browser is being resumed.
                if ((tabLayout.getSelectedTabPosition() == currentPagePosition) && !urlEditText.hasFocus() && (currentUrl != null)) {
                    // Check to see if the URL is `about:blank`.
                    if (currentUrl.equals("about:blank")) {  // The WebView is blank.
                        // Display the hint in the URL edit text.
                        urlEditText.setText("");

                        // Request focus for the URL text box.
                        urlEditText.requestFocus();

                        // Display the keyboard.
                        inputMethodManager.showSoftInput(urlEditText, 0);

                        // Apply the domain settings.  This clears any settings from the previous domain.
                        applyDomainSettings(nestedScrollWebView, "", true, false, false);

                        // Only populate the title text view if the tab has been fully created.
                        if (tab != null) {
                            // Get the custom view from the tab.
                            View tabView = tab.getCustomView();

                            // Remove the incorrect warning below that the current tab view might be null.
                            assert tabView != null;

                            // Get the title text view from the tab.
                            TextView tabTitleTextView = tabView.findViewById(R.id.title_textview);

                            // Set the title as the tab text.
                            tabTitleTextView.setText(R.string.new_tab);
                        }
                    } else {  // The WebView has loaded a webpage.
                        // Update the URL edit text if it is not currently being edited.
                        if (!urlEditText.hasFocus()) {
                            // Sanitize the current URL.  This removes unwanted URL elements that were added by redirects, so that they won't be included if the URL is shared.
                            String sanitizedUrl = sanitizeUrl(currentUrl);

                            // Display the final URL.  Getting the URL from the WebView instead of using the one provided by `onPageFinished()` makes websites like YouTube function correctly.
                            urlEditText.setText(sanitizedUrl);

                            // Apply text highlighting to the URL.
                            highlightUrlText();
                        }

                        // Only populate the title text view if the tab has been fully created.
                        if (tab != null) {
                            // Get the custom view from the tab.
                            View tabView = tab.getCustomView();

                            // Remove the incorrect warning below that the current tab view might be null.
                            assert tabView != null;

                            // Get the title text view from the tab.
                            TextView tabTitleTextView = tabView.findViewById(R.id.title_textview);

                            // Set the title as the tab text.  Sometimes `onReceivedTitle()` is not called, especially when navigating history.
                            tabTitleTextView.setText(nestedScrollWebView.getTitle());
                        }
                    }
                }
            }

            // Handle SSL Certificate errors.
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Get the current website SSL certificate.
                SslCertificate currentWebsiteSslCertificate = error.getCertificate();

                // Extract the individual pieces of information from the current website SSL certificate.
                String currentWebsiteIssuedToCName = currentWebsiteSslCertificate.getIssuedTo().getCName();
                String currentWebsiteIssuedToOName = currentWebsiteSslCertificate.getIssuedTo().getOName();
                String currentWebsiteIssuedToUName = currentWebsiteSslCertificate.getIssuedTo().getUName();
                String currentWebsiteIssuedByCName = currentWebsiteSslCertificate.getIssuedBy().getCName();
                String currentWebsiteIssuedByOName = currentWebsiteSslCertificate.getIssuedBy().getOName();
                String currentWebsiteIssuedByUName = currentWebsiteSslCertificate.getIssuedBy().getUName();
                Date currentWebsiteSslStartDate = currentWebsiteSslCertificate.getValidNotBeforeDate();
                Date currentWebsiteSslEndDate = currentWebsiteSslCertificate.getValidNotAfterDate();

                // Proceed to the website if the current SSL website certificate matches the pinned domain certificate.
                if (nestedScrollWebView.hasPinnedSslCertificate()) {
                    // Get the pinned SSL certificate.
                    ArrayList<Object> pinnedSslCertificateArrayList = nestedScrollWebView.getPinnedSslCertificate();

                    // Extract the arrays from the array list.
                    String[] pinnedSslCertificateStringArray = (String[]) pinnedSslCertificateArrayList.get(0);
                    Date[] pinnedSslCertificateDateArray = (Date[]) pinnedSslCertificateArrayList.get(1);

                    // Check if the current SSL certificate matches the pinned certificate.
                    if (currentWebsiteIssuedToCName.equals(pinnedSslCertificateStringArray[0]) && currentWebsiteIssuedToOName.equals(pinnedSslCertificateStringArray[1]) &&
                            currentWebsiteIssuedToUName.equals(pinnedSslCertificateStringArray[2]) && currentWebsiteIssuedByCName.equals(pinnedSslCertificateStringArray[3]) &&
                            currentWebsiteIssuedByOName.equals(pinnedSslCertificateStringArray[4]) && currentWebsiteIssuedByUName.equals(pinnedSslCertificateStringArray[5]) &&
                            currentWebsiteSslStartDate.equals(pinnedSslCertificateDateArray[0]) && currentWebsiteSslEndDate.equals(pinnedSslCertificateDateArray[1])) {

                        // An SSL certificate is pinned and matches the current domain certificate.  Proceed to the website without displaying an error.
                        handler.proceed();
                    }
                } else {  // Either there isn't a pinned SSL certificate or it doesn't match the current website certificate.
                    // Store the SSL error handler.
                    nestedScrollWebView.setSslErrorHandler(handler);

                    // Instantiate an SSL certificate error alert dialog.
                    DialogFragment sslCertificateErrorDialogFragment = SslCertificateErrorDialog.displayDialog(error, nestedScrollWebView.getWebViewFragmentId());

                    // Show the SSL certificate error dialog.
                    sslCertificateErrorDialogFragment.show(getSupportFragmentManager(), getString(R.string.ssl_certificate_error));
                }
            }
        });

        // Check to see if the state is being restored.
        if (restoringState) {  // The state is being restored.
            // Resume the nested scroll WebView JavaScript timers.
            nestedScrollWebView.resumeTimers();
        } else if (pageNumber == 0) {  // The first page is being loaded.
            // Set this nested scroll WebView as the current WebView.
            currentWebView = nestedScrollWebView;

            // Initialize the URL to load string.
            String urlToLoadString;

            // Get the intent that started the app.
            Intent launchingIntent = getIntent();

            // Reset the intent.  This prevents a duplicate tab from being created on restart.
            setIntent(new Intent());

            // Get the information from the intent.
            String launchingIntentAction = launchingIntent.getAction();
            Uri launchingIntentUriData = launchingIntent.getData();
            String launchingIntentStringExtra = launchingIntent.getStringExtra(Intent.EXTRA_TEXT);

            // Parse the launching intent URL.
            if ((launchingIntentAction != null) && launchingIntentAction.equals(Intent.ACTION_WEB_SEARCH)) {  // The intent contains a search string.
                // Create an encoded URL string.
                String encodedUrlString;

                // Sanitize the search input and convert it to a search.
                try {
                    encodedUrlString = URLEncoder.encode(launchingIntent.getStringExtra(SearchManager.QUERY), "UTF-8");
                } catch (UnsupportedEncodingException exception) {
                    encodedUrlString = "";
                }

                // Store the web search as the URL to load.
                urlToLoadString = searchURL + encodedUrlString;
            } else if (launchingIntentUriData != null) {  // The launching intent contains a URL formatted as a URI.
                // Store the URI as a URL.
                urlToLoadString = launchingIntentUriData.toString();
            } else if (launchingIntentStringExtra != null) {  // The launching intent contains text that might be a URL.
                // Store the URL.
                urlToLoadString = launchingIntentStringExtra;
            } else if (!url.equals("")) {  // The activity has been restarted.
                // Load the saved URL.
                urlToLoadString = url;
            } else {  // The is no URL in the intent.
                // Store the homepage to be loaded.
                urlToLoadString = sharedPreferences.getString("homepage", getString(R.string.homepage_default_value));
            }

            // Load the website if not waiting for the proxy.
            if (waitingForProxy) {  // Store the URL to be loaded in the Nested Scroll WebView.
                nestedScrollWebView.setWaitingForProxyUrlString(urlToLoadString);
            } else {  // Load the URL.
                loadUrl(nestedScrollWebView, urlToLoadString);
            }

            // Reset the intent.  This prevents a duplicate tab from being created on a subsequent restart if loading an link from a new intent on restart.
            // For example, this prevents a duplicate tab if a link is loaded from the Guide after changing the theme in the guide and then changing the theme again in the main activity.
            setIntent(new Intent());
        } else {  // This is not the first tab.
            // Load the URL.
            loadUrl(nestedScrollWebView, url);

            // Set the focus and display the keyboard if the URL is blank.
            if (url.equals("")) {
                // Request focus for the URL text box.
                urlEditText.requestFocus();

                // Create a display keyboard handler.
                Handler displayKeyboardHandler = new Handler();

                // Create a display keyboard runnable.
                Runnable displayKeyboardRunnable = () -> {
                    // Display the keyboard.
                    inputMethodManager.showSoftInput(urlEditText, 0);
                };

                // Display the keyboard after 100 milliseconds, which leaves enough time for the tab to transition.
                displayKeyboardHandler.postDelayed(displayKeyboardRunnable, 100);
            }
        }
    }
}