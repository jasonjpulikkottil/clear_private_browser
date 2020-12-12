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

package com.jdots.browser.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.jdots.browser.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

// NestedScrollWebView extends WebView to handle nested scrolls (scrolling the app bar off the screen).
public class NestedScrollWebView extends WebView implements NestedScrollingChild2 {
    // Define the blocklists constants.
    public final static int BLOCKED_REQUESTS = 0;
    public final static int EASYLIST = 1;
    public final static int EASYPRIVACY = 2;
    public final static int FANBOYS_ANNOYANCE_LIST = 3;
    public final static int FANBOYS_SOCIAL_BLOCKING_LIST = 4;
    public final static int ULTRALIST = 5;
    public final static int ULTRAPRIVACY = 6;
    public final static int THIRD_PARTY_REQUESTS = 7;

    // Define the saved state constants.
    private final String DOMAIN_SETTINGS_APPLIED = "domain_settings_applied";
    private final String DOMAIN_SETTINGS_DATABASE_ID = "domain_settings_database_id";
    private final String CURRENT_URl = "current_url";
    private final String CURRENT_DOMAIN_NAME = "current_domain_name";
    private final String ACCEPT_FIRST_PARTY_COOKIES = "accept_first_party_cookies";
    private final String EASYLIST_ENABLED = "easylist_enabled";
    private final String EASYPRIVACY_ENABLED = "easyprivacy_enabled";
    private final String FANBOYS_ANNOYANCE_LIST_ENABLED = "fanboys_annoyance_list_enabled";
    private final String FANBOYS_SOCIAL_BLOCKING_LIST_ENABLED = "fanboys_social_blocking_list_enabled";
    private final String ULTRALIST_ENABLED = "ultralist_enabled";
    private final String ULTRAPRIVACY_ENABLED = "ultraprivacy_enabled";
    private final String BLOCK_ALL_THIRD_PARTY_REQUESTS = "block_all_third_party_requests";
    private final String HAS_PINNED_SSL_CERTIFICATE = "has_pinned_ssl_certificate";
    private final String PINNED_SSL_ISSUED_TO_CNAME = "pinned_ssl_issued_to_cname";
    private final String PINNED_SSL_ISSUED_TO_ONAME = "pinned_ssl_issued_to_oname";
    private final String PINNED_SSL_ISSUED_TO_UNAME = "pinned_ssl_issued_to_uname";
    private final String PINNED_SSL_ISSUED_BY_CNAME = "pinned_ssl_issued_by_cname";
    private final String PINNED_SSL_ISSUED_BY_ONAME = "pinned_ssl_issued_by_oname";
    private final String PINNED_SSL_ISSUED_BY_UNAME = "pinned_ssl_issued_by_uname";
    private final String PINNED_SSL_START_DATE = "pinned_ssl_start_date";
    private final String PINNED_SSL_END_DATE = "pinned_ssl_end_date";
    private final String HAS_PINNED_IP_ADDRESSES = "has_pinned_ip_addresses";
    private final String PINNED_IP_ADDRESSES = "pinned_ip_addresses";
    private final String IGNORE_PINNED_DOMAIN_INFORMATION = "ignore_pinned_domain_information";
    private final String SWIPE_TO_REFRESH = "swipe_to_refresh";
    private final String JAVASCRIPT_ENABLED = "javascript_enabled";
    private final String DOM_STORAGE_ENABLED = "dom_storage_enabled";
    private final String USER_AGENT = "user_agent";
    private final String WIDE_VIEWPORT = "wide_viewport";
    private final String FONT_SIZE = "font_size";
    // Track the resource requests.
    private final List<String[]> resourceRequests = Collections.synchronizedList(new ArrayList<>());  // Using a synchronized list makes adding resource requests thread safe.
    // The nested scrolling child helper is used throughout the class.
    private final NestedScrollingChildHelper nestedScrollingChildHelper;
    // Keep a copy of the WebView fragment ID.
    private long webViewFragmentId;
    // Store the handlers.
    private SslErrorHandler sslErrorHandler;
    private HttpAuthHandler httpAuthHandler;
    // Track if domain settings are applied to this nested scroll WebView and, if so, the database ID.
    private boolean domainSettingsApplied;
    private int domainSettingsDatabaseId;
    // Keep track of the current URL.  This is used to not block resource requests to the main URL.
    private String currentUrl;
    // Keep track of when the domain name changes so that domain settings can be reapplied.  This should never be null.
    private String currentDomainName = "";
    // Track the status of first-party cookies.  This is necessary because first-party cookie status is app wide instead of WebView specific.
    private boolean acceptFirstPartyCookies;
    private boolean easyListEnabled;
    private boolean easyPrivacyEnabled;
    private boolean fanboysAnnoyanceListEnabled;
    private boolean fanboysSocialBlockingListEnabled;
    private boolean ultraListEnabled;
    private boolean ultraPrivacyEnabled;
    private boolean blockAllThirdPartyRequests;
    private int blockedRequests;
    private int easyListBlockedRequests;
    private int easyPrivacyBlockedRequests;
    private int fanboysAnnoyanceListBlockedRequests;
    private int fanboysSocialBlockingListBlockedRequests;
    private int ultraListBlockedRequests;
    private int ultraPrivacyBlockedRequests;
    private int thirdPartyBlockedRequests;
    // The pinned SSL certificate variables.
    private boolean hasPinnedSslCertificate;
    private String pinnedSslIssuedToCName;
    private String pinnedSslIssuedToOName;
    private String pinnedSslIssuedToUName;
    private String pinnedSslIssuedByCName;
    private String pinnedSslIssuedByOName;
    private String pinnedSslIssuedByUName;
    private Date pinnedSslStartDate;
    private Date pinnedSslEndDate;
    // The current IP addresses variables.
    private boolean hasCurrentIpAddresses;
    private String currentIpAddresses;
    // The pinned IP addresses variables.
    private boolean hasPinnedIpAddresses;
    private String pinnedIpAddresses;
    // The ignore pinned domain information tracker.  This is set when a user proceeds past a pinned mismatch dialog to prevent the dialog from showing again until after the domain changes.
    private boolean ignorePinnedDomainInformation;
    // The default or favorite icon.
    private Bitmap favoriteOrDefaultIcon;
    // Track swipe to refresh.
    private boolean swipeToRefresh;
    // Track a URL waiting for a proxy.
    private String waitingForProxyUrlString = "";
    // The previous Y position needs to be tracked between motion events.
    private int previousYPosition;


    // The basic constructor.
    public NestedScrollWebView(Context context) {
        // Roll up to the next constructor.
        this(context, null);
    }

    // The intermediate constructor.
    public NestedScrollWebView(Context context, AttributeSet attributeSet) {
        // Roll up to the next constructor.
        this(context, attributeSet, android.R.attr.webViewStyle);
    }

    // The full constructor.
    public NestedScrollWebView(Context context, AttributeSet attributeSet, int defaultStyle) {
        // Run the default commands.
        super(context, attributeSet, defaultStyle);

        // Initialize the nested scrolling child helper.
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        // Enable nested scrolling by default.
        nestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    public long getWebViewFragmentId() {
        // Return the WebView fragment ID.
        return webViewFragmentId;
    }

    // WebView Fragment ID.
    public void setWebViewFragmentId(long webViewFragmentId) {
        // Store the WebView fragment ID.
        this.webViewFragmentId = webViewFragmentId;
    }

    public SslErrorHandler getSslErrorHandler() {
        // Return the current SSL error handler.
        return sslErrorHandler;
    }

    // SSL error handler.
    public void setSslErrorHandler(SslErrorHandler sslErrorHandler) {
        // Store the current SSL error handler.
        this.sslErrorHandler = sslErrorHandler;
    }

    public void resetSslErrorHandler() {
        // Reset the current SSL error handler.
        sslErrorHandler = null;
    }

    public HttpAuthHandler getHttpAuthHandler() {
        // Return the current HTTP authentication handler.
        return httpAuthHandler;
    }

    // HTTP authentication handler.
    public void setHttpAuthHandler(HttpAuthHandler httpAuthHandler) {
        // Store the current HTTP authentication handler.
        this.httpAuthHandler = httpAuthHandler;
    }

    public void resetHttpAuthHandler() {
        // Reset the current HTTP authentication handler.
        httpAuthHandler = null;
    }

    public boolean getDomainSettingsApplied() {
        // Return the domain settings applied status.
        return domainSettingsApplied;
    }

    // Domain settings.
    public void setDomainSettingsApplied(boolean applied) {
        // Store the domain settings applied status.
        domainSettingsApplied = applied;
    }

    public int getDomainSettingsDatabaseId() {
        // Return the domain settings database ID.
        return domainSettingsDatabaseId;
    }

    // Domain settings database ID.
    public void setDomainSettingsDatabaseId(int databaseId) {
        // Store the domain settings database ID.
        domainSettingsDatabaseId = databaseId;
    }

    public String getCurrentUrl() {
        // Return the current URL.
        return currentUrl;
    }

    // Current URL.
    public void setCurrentUrl(String url) {
        // Store the current URL.
        currentUrl = url;
    }

    public void resetCurrentDomainName() {
        // Reset the current domain name.
        currentDomainName = "";
    }

    public String getCurrentDomainName() {
        // Return the current domain name.
        return currentDomainName;
    }

    // Current domain name.  To function well when called, the domain name should never be allowed to be null.
    public void setCurrentDomainName(@NonNull String domainName) {
        // Store the current domain name.
        currentDomainName = domainName;
    }

    public boolean getAcceptFirstPartyCookies() {
        // Return the accept first-party cookies status.
        return acceptFirstPartyCookies;
    }

    // First-party cookies.
    public void setAcceptFirstPartyCookies(boolean status) {
        // Store the accept first-party cookies status.
        acceptFirstPartyCookies = status;
    }

    // Resource requests.
    public void addResourceRequest(String[] resourceRequest) {
        // Add the resource request to the list.
        resourceRequests.add(resourceRequest);
    }

    public List<String[]> getResourceRequests() {
        // Return the list of resource requests as an array list.
        return resourceRequests;
    }

    public void clearResourceRequests() {
        // Clear the resource requests.
        resourceRequests.clear();
    }


    // Blocklists.
    public void enableBlocklist(int blocklist, boolean status) {
        // Update the status of the indicated blocklist.
        switch (blocklist) {
            case EASYLIST:
                // Update the status of the blocklist.
                easyListEnabled = status;
                break;

            case EASYPRIVACY:
                // Update the status of the blocklist.
                easyPrivacyEnabled = status;
                break;

            case FANBOYS_ANNOYANCE_LIST:
                // Update the status of the blocklist.
                fanboysAnnoyanceListEnabled = status;
                break;

            case FANBOYS_SOCIAL_BLOCKING_LIST:
                // Update the status of the blocklist.
                fanboysSocialBlockingListEnabled = status;
                break;

            case ULTRALIST:
                // Update the status of the blocklist.
                ultraListEnabled = status;
                break;

            case ULTRAPRIVACY:
                // Update the status of the blocklist.
                ultraPrivacyEnabled = status;
                break;

            case THIRD_PARTY_REQUESTS:
                // Update the status of the blocklist.
                blockAllThirdPartyRequests = status;
                break;
        }
    }

    public boolean isBlocklistEnabled(int blocklist) {
        // Get the status of the indicated blocklist.
        switch (blocklist) {
            case EASYLIST:
                // Return the status of the blocklist.
                return easyListEnabled;

            case EASYPRIVACY:
                // Return the status of the blocklist.
                return easyPrivacyEnabled;

            case FANBOYS_ANNOYANCE_LIST:
                // Return the status of the blocklist.
                return fanboysAnnoyanceListEnabled;

            case FANBOYS_SOCIAL_BLOCKING_LIST:
                // Return the status of the blocklist.
                return fanboysSocialBlockingListEnabled;

            case ULTRALIST:
                // Return the status of the blocklist.
                return ultraListEnabled;

            case ULTRAPRIVACY:
                // Return the status of the blocklist.
                return ultraPrivacyEnabled;

            case THIRD_PARTY_REQUESTS:
                // Return the status of the blocklist.
                return blockAllThirdPartyRequests;

            default:
                // The default value is required but should never be used.
                return false;
        }
    }


    // Resource request counters.
    public void resetRequestsCounters() {
        // Reset all the resource request counters.
        blockedRequests = 0;
        easyListBlockedRequests = 0;
        easyPrivacyBlockedRequests = 0;
        fanboysAnnoyanceListBlockedRequests = 0;
        fanboysSocialBlockingListBlockedRequests = 0;
        ultraListBlockedRequests = 0;
        ultraPrivacyBlockedRequests = 0;
        thirdPartyBlockedRequests = 0;
    }

    public void incrementRequestsCount(int blocklist) {
        // Increment the count of the indicated blocklist.
        switch (blocklist) {
            case BLOCKED_REQUESTS:
                // Increment the blocked requests count.
                blockedRequests++;
                break;

            case EASYLIST:
                // Increment the EasyList blocked requests count.
                easyListBlockedRequests++;
                break;

            case EASYPRIVACY:
                // Increment the EasyPrivacy blocked requests count.
                easyPrivacyBlockedRequests++;
                break;

            case FANBOYS_ANNOYANCE_LIST:
                // Increment the Fanboy's Annoyance List blocked requests count.
                fanboysAnnoyanceListBlockedRequests++;
                break;

            case FANBOYS_SOCIAL_BLOCKING_LIST:
                // Increment the Fanboy's Social Blocking List blocked requests count.
                fanboysSocialBlockingListBlockedRequests++;
                break;

            case ULTRALIST:
                // Increment the UltraList blocked requests count.
                ultraListBlockedRequests++;
                break;

            case ULTRAPRIVACY:
                // Increment the UltraPrivacy blocked requests count.
                ultraPrivacyBlockedRequests++;
                break;

            case THIRD_PARTY_REQUESTS:
                // Increment the Third Party blocked requests count.
                thirdPartyBlockedRequests++;
                break;
        }
    }

    public int getRequestsCount(int blocklist) {
        // Get the count of the indicated blocklist.
        switch (blocklist) {
            case BLOCKED_REQUESTS:
                // Return the blocked requests count.
                return blockedRequests;

            case EASYLIST:
                // Return the EasyList blocked requests count.
                return easyListBlockedRequests;

            case EASYPRIVACY:
                // Return the EasyPrivacy blocked requests count.
                return easyPrivacyBlockedRequests;

            case FANBOYS_ANNOYANCE_LIST:
                // Return the Fanboy's Annoyance List blocked requests count.
                return fanboysAnnoyanceListBlockedRequests;

            case FANBOYS_SOCIAL_BLOCKING_LIST:
                // Return the Fanboy's Social Blocking List blocked requests count.
                return fanboysSocialBlockingListBlockedRequests;

            case ULTRALIST:
                // Return the UltraList blocked requests count.
                return ultraListBlockedRequests;

            case ULTRAPRIVACY:
                // Return the UltraPrivacy blocked requests count.
                return ultraPrivacyBlockedRequests;

            case THIRD_PARTY_REQUESTS:
                // Return the Third Party blocked requests count.
                return thirdPartyBlockedRequests;

            default:
                // Return 0.  This should never end up being called.
                return 0;
        }
    }


    // Pinned SSL certificates.
    public boolean hasPinnedSslCertificate() {
        // Return the status of the pinned SSL certificate.
        return hasPinnedSslCertificate;
    }

    public void setPinnedSslCertificate(String issuedToCName, String issuedToOName, String issuedToUName, String issuedByCName, String issuedByOName, String issuedByUName, Date startDate, Date endDate) {
        // Store the pinned SSL certificate information.
        pinnedSslIssuedToCName = issuedToCName;
        pinnedSslIssuedToOName = issuedToOName;
        pinnedSslIssuedToUName = issuedToUName;
        pinnedSslIssuedByCName = issuedByCName;
        pinnedSslIssuedByOName = issuedByOName;
        pinnedSslIssuedByUName = issuedByUName;
        pinnedSslStartDate = startDate;
        pinnedSslEndDate = endDate;

        // Set the pinned SSL certificate tracker.
        hasPinnedSslCertificate = true;
    }

    public ArrayList<Object> getPinnedSslCertificate() {
        // Initialize an array list.
        ArrayList<Object> arrayList = new ArrayList<>();

        // Create the SSL certificate string array.
        String[] sslCertificateStringArray = new String[]{pinnedSslIssuedToCName, pinnedSslIssuedToOName, pinnedSslIssuedToUName, pinnedSslIssuedByCName, pinnedSslIssuedByOName, pinnedSslIssuedByUName};

        // Create the SSL certificate date array.
        Date[] sslCertificateDateArray = new Date[]{pinnedSslStartDate, pinnedSslEndDate};

        // Add the arrays to the array list.
        arrayList.add(sslCertificateStringArray);
        arrayList.add(sslCertificateDateArray);

        // Return the pinned SSL certificate array list.
        return arrayList;
    }

    public void clearPinnedSslCertificate() {
        // Clear the pinned SSL certificate.
        pinnedSslIssuedToCName = null;
        pinnedSslIssuedToOName = null;
        pinnedSslIssuedToUName = null;
        pinnedSslIssuedByCName = null;
        pinnedSslIssuedByOName = null;
        pinnedSslIssuedByUName = null;
        pinnedSslStartDate = null;
        pinnedSslEndDate = null;

        // Clear the pinned SSL certificate tracker.
        hasPinnedSslCertificate = false;
    }


    // Current IP addresses.
    public boolean hasCurrentIpAddresses() {
        // Return the status of the current IP addresses.
        return hasCurrentIpAddresses;
    }

    public String getCurrentIpAddresses() {
        // Return the current IP addresses.
        return currentIpAddresses;
    }

    public void setCurrentIpAddresses(String ipAddresses) {
        // Store the current IP addresses.
        currentIpAddresses = ipAddresses;

        // Set the current IP addresses tracker.
        hasCurrentIpAddresses = true;
    }

    public void clearCurrentIpAddresses() {
        // Clear the current IP addresses.
        currentIpAddresses = null;

        // Clear the current IP addresses tracker.
        hasCurrentIpAddresses = false;
    }


    // Pinned IP addresses.
    public boolean hasPinnedIpAddresses() {
        // Return the status of the pinned IP addresses.
        return hasPinnedIpAddresses;
    }

    public String getPinnedIpAddresses() {
        // Return the pinned IP addresses.
        return pinnedIpAddresses;
    }

    public void setPinnedIpAddresses(String ipAddresses) {
        // Store the pinned IP addresses.
        pinnedIpAddresses = ipAddresses;

        // Set the pinned IP addresses tracker.
        hasPinnedIpAddresses = true;
    }

    public void clearPinnedIpAddresses() {
        // Clear the pinned IP addresses.
        pinnedIpAddresses = null;

        // Clear the pinned IP addresses tracker.
        hasPinnedIpAddresses = false;
    }


    // Ignore pinned information.
    public void setIgnorePinnedDomainInformation(boolean status) {
        // Set the status of the ignore pinned domain information tracker.
        ignorePinnedDomainInformation = status;
    }

    public boolean ignorePinnedDomainInformation() {
        // Return the status of the ignore pinned domain information tracker.
        return ignorePinnedDomainInformation;
    }


    // Favorite or default icon.
    public void initializeFavoriteIcon() {
        // Get the default favorite icon drawable.  `ContextCompat` must be used until API >= 21.
        Drawable favoriteIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.world);

        // Cast the favorite icon drawable to a bitmap drawable.
        BitmapDrawable favoriteIconBitmapDrawable = (BitmapDrawable) favoriteIconDrawable;

        // Remove the incorrect warning below that the favorite icon bitmap drawable might be null.
        assert favoriteIconBitmapDrawable != null;

        // Store the default icon bitmap.
        favoriteOrDefaultIcon = favoriteIconBitmapDrawable.getBitmap();
    }

    public Bitmap getFavoriteOrDefaultIcon() {
        // Return the favorite or default icon.
        return favoriteOrDefaultIcon;
    }

    public void setFavoriteOrDefaultIcon(Bitmap icon) {
        // Scale the favorite icon bitmap down if it is larger than 256 x 256.  Filtering uses bilinear interpolation.
        if ((icon.getHeight() > 256) || (icon.getWidth() > 256)) {
            favoriteOrDefaultIcon = Bitmap.createScaledBitmap(icon, 256, 256, true);
        } else {
            // Store the icon as presented.
            favoriteOrDefaultIcon = icon;
        }
    }

    public boolean getSwipeToRefresh() {
        // Return the swipe to refresh status.
        return swipeToRefresh;
    }

    // Swipe to refresh.
    public void setSwipeToRefresh(boolean status) {
        // Store the swipe to refresh status.
        swipeToRefresh = status;
    }

    public String getWaitingForProxyUrlString() {
        // Return the waiting for proxy URL string.
        return waitingForProxyUrlString;
    }

    // Waiting for proxy.
    public void setWaitingForProxyUrlString(String urlString) {
        // Store the waiting for proxy URL string.
        waitingForProxyUrlString = urlString;
    }

    public void resetWaitingForProxyUrlString() {
        // Clear the waiting for proxy URL string.
        waitingForProxyUrlString = "";
    }

    // Scroll range.
    public int getHorizontalScrollRange() {
        // Return the horizontal scroll range.
        return computeHorizontalScrollRange();
    }

    public int getVerticalScrollRange() {
        // Return the vertical scroll range.
        return computeVerticalScrollRange();
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // Initialize a tracker to return if this motion event is handled.
        boolean motionEventHandled;

        // Run the commands for the given motion event action.
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start nested scrolling along the vertical axis.  `ViewCompat` must be used until the minimum API >= 21.
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);

                // Save the current Y position.  Action down will not be called again until a new motion starts.
                previousYPosition = (int) motionEvent.getY();

                // Run the default commands.
                motionEventHandled = super.onTouchEvent(motionEvent);
                break;

            case MotionEvent.ACTION_MOVE:
                // Get the current Y position.
                int currentYMotionPosition = (int) motionEvent.getY();

                // Calculate the pre-scroll delta Y.
                int preScrollDeltaY = previousYPosition - currentYMotionPosition;

                // Initialize a variable to track how much of the scroll is consumed.
                int[] consumedScroll = new int[2];

                // Initialize a variable to track the offset in the window.
                int[] offsetInWindow = new int[2];

                // Get the WebView Y position.
                int webViewYPosition = getScrollY();

                // Set the scroll delta Y to initially be the same as the pre-scroll delta Y.
                int scrollDeltaY = preScrollDeltaY;

                // Dispatch the nested pre-school.  This scrolls the app bar if it needs it.  `offsetInWindow` will be returned with an updated value.
                if (dispatchNestedPreScroll(0, preScrollDeltaY, consumedScroll, offsetInWindow)) {
                    // Update the scroll delta Y if some of it was consumed.
                    // There is currently a bug in Android where if scrolling up at a certain slow speed the input can lock the pre scroll and continue to consume it after the app bar is fully displayed.
                    scrollDeltaY = preScrollDeltaY - consumedScroll[1];
                }

                // Check to see if the WebView is at the top and and the scroll action is downward.
                if ((webViewYPosition == 0) && (scrollDeltaY < 0)) {  // Swipe to refresh is being engaged.
                    // Stop the nested scroll so that swipe to refresh has complete control.  This way releasing the scroll to refresh circle doesn't scroll the WebView at the same time.
                    stopNestedScroll();
                } else {  // Swipe to refresh is not being engaged.
                    // Start the nested scroll so that the app bar can scroll off the screen.
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);

                    // Dispatch the nested scroll.  This scrolls the WebView.  The delta Y unconsumed normally controls the swipe refresh layout, but that is handled with the `if` statement above.
                    dispatchNestedScroll(0, scrollDeltaY, 0, 0, offsetInWindow);

                    // Store the current Y position for use in the next action move.
                    previousYPosition = previousYPosition - scrollDeltaY;
                }

                // Run the default commands.
                motionEventHandled = super.onTouchEvent(motionEvent);
                break;


            default:
                // Stop nested scrolling.
                stopNestedScroll();

                // Run the default commands.
                motionEventHandled = super.onTouchEvent(motionEvent);
        }

        // Perform a click.  This is required by the Android accessibility guidelines.
        performClick();

        // Return the status of the motion event.
        return motionEventHandled;
    }

    public Bundle saveNestedScrollWebViewState() {
        // Create a saved state bundle.
        Bundle savedState = new Bundle();

        // Initialize the long date variables.
        long pinnedSslStartDateLong = 0;
        long pinnedSslEndDateLong = 0;

        // Convert the dates to longs.
        if (pinnedSslStartDate != null) {
            pinnedSslStartDateLong = pinnedSslStartDate.getTime();
        }

        if (pinnedSslEndDate != null) {
            pinnedSslEndDateLong = pinnedSslEndDate.getTime();
        }

        // Populate the saved state bundle.
        savedState.putBoolean(DOMAIN_SETTINGS_APPLIED, domainSettingsApplied);
        savedState.putInt(DOMAIN_SETTINGS_DATABASE_ID, domainSettingsDatabaseId);
        savedState.putString(CURRENT_URl, currentUrl);
        savedState.putString(CURRENT_DOMAIN_NAME, currentDomainName);
        savedState.putBoolean(ACCEPT_FIRST_PARTY_COOKIES, acceptFirstPartyCookies);
        savedState.putBoolean(EASYLIST_ENABLED, easyListEnabled);
        savedState.putBoolean(EASYPRIVACY_ENABLED, easyPrivacyEnabled);
        savedState.putBoolean(FANBOYS_ANNOYANCE_LIST_ENABLED, fanboysAnnoyanceListEnabled);
        savedState.putBoolean(FANBOYS_SOCIAL_BLOCKING_LIST_ENABLED, fanboysSocialBlockingListEnabled);
        savedState.putBoolean(ULTRALIST_ENABLED, ultraListEnabled);
        savedState.putBoolean(ULTRAPRIVACY_ENABLED, ultraPrivacyEnabled);
        savedState.putBoolean(BLOCK_ALL_THIRD_PARTY_REQUESTS, blockAllThirdPartyRequests);
        savedState.putBoolean(HAS_PINNED_SSL_CERTIFICATE, hasPinnedSslCertificate);
        savedState.putString(PINNED_SSL_ISSUED_TO_CNAME, pinnedSslIssuedToCName);
        savedState.putString(PINNED_SSL_ISSUED_TO_ONAME, pinnedSslIssuedToOName);
        savedState.putString(PINNED_SSL_ISSUED_TO_UNAME, pinnedSslIssuedToUName);
        savedState.putString(PINNED_SSL_ISSUED_BY_CNAME, pinnedSslIssuedByCName);
        savedState.putString(PINNED_SSL_ISSUED_BY_ONAME, pinnedSslIssuedByOName);
        savedState.putString(PINNED_SSL_ISSUED_BY_UNAME, pinnedSslIssuedByUName);
        savedState.putLong(PINNED_SSL_START_DATE, pinnedSslStartDateLong);
        savedState.putLong(PINNED_SSL_END_DATE, pinnedSslEndDateLong);
        savedState.putBoolean(HAS_PINNED_IP_ADDRESSES, hasPinnedIpAddresses);
        savedState.putString(PINNED_IP_ADDRESSES, pinnedIpAddresses);
        savedState.putBoolean(IGNORE_PINNED_DOMAIN_INFORMATION, ignorePinnedDomainInformation);
        savedState.putBoolean(SWIPE_TO_REFRESH, swipeToRefresh);
        savedState.putBoolean(JAVASCRIPT_ENABLED, this.getSettings().getJavaScriptEnabled());
        savedState.putBoolean(DOM_STORAGE_ENABLED, this.getSettings().getDomStorageEnabled());
        savedState.putString(USER_AGENT, this.getSettings().getUserAgentString());
        savedState.putBoolean(WIDE_VIEWPORT, this.getSettings().getUseWideViewPort());
        savedState.putInt(FONT_SIZE, this.getSettings().getTextZoom());

        // Return the saved state bundle.
        return savedState;
    }

    public void restoreNestedScrollWebViewState(Bundle savedState) {
        // Restore the class variables.
        domainSettingsApplied = savedState.getBoolean(DOMAIN_SETTINGS_APPLIED);
        domainSettingsDatabaseId = savedState.getInt(DOMAIN_SETTINGS_DATABASE_ID);
        currentUrl = savedState.getString(CURRENT_URl);
        currentDomainName = savedState.getString(CURRENT_DOMAIN_NAME);
        acceptFirstPartyCookies = savedState.getBoolean(ACCEPT_FIRST_PARTY_COOKIES);
        easyListEnabled = savedState.getBoolean(EASYLIST_ENABLED);
        easyPrivacyEnabled = savedState.getBoolean(EASYPRIVACY_ENABLED);
        fanboysAnnoyanceListEnabled = savedState.getBoolean(FANBOYS_ANNOYANCE_LIST_ENABLED);
        fanboysSocialBlockingListEnabled = savedState.getBoolean(FANBOYS_SOCIAL_BLOCKING_LIST_ENABLED);
        ultraListEnabled = savedState.getBoolean(ULTRALIST_ENABLED);
        ultraPrivacyEnabled = savedState.getBoolean(ULTRAPRIVACY_ENABLED);
        blockAllThirdPartyRequests = savedState.getBoolean(BLOCK_ALL_THIRD_PARTY_REQUESTS);
        hasPinnedSslCertificate = savedState.getBoolean(HAS_PINNED_SSL_CERTIFICATE);
        pinnedSslIssuedToCName = savedState.getString(PINNED_SSL_ISSUED_TO_CNAME);
        pinnedSslIssuedToOName = savedState.getString(PINNED_SSL_ISSUED_TO_ONAME);
        pinnedSslIssuedToUName = savedState.getString(PINNED_SSL_ISSUED_TO_UNAME);
        pinnedSslIssuedByCName = savedState.getString(PINNED_SSL_ISSUED_BY_CNAME);
        pinnedSslIssuedByOName = savedState.getString(PINNED_SSL_ISSUED_BY_ONAME);
        pinnedSslIssuedByUName = savedState.getString(PINNED_SSL_ISSUED_BY_UNAME);
        hasPinnedIpAddresses = savedState.getBoolean(HAS_PINNED_IP_ADDRESSES);
        pinnedIpAddresses = savedState.getString(PINNED_IP_ADDRESSES);
        ignorePinnedDomainInformation = savedState.getBoolean(IGNORE_PINNED_DOMAIN_INFORMATION);
        swipeToRefresh = savedState.getBoolean(SWIPE_TO_REFRESH);
        this.getSettings().setJavaScriptEnabled(savedState.getBoolean(JAVASCRIPT_ENABLED));
        this.getSettings().setDomStorageEnabled(savedState.getBoolean(DOM_STORAGE_ENABLED));
        this.getSettings().setUserAgentString(savedState.getString(USER_AGENT));
        this.getSettings().setUseWideViewPort(savedState.getBoolean(WIDE_VIEWPORT));
        this.getSettings().setTextZoom(savedState.getInt(FONT_SIZE));

        // Get the date longs.
        long pinnedSslStartDateLong = savedState.getLong(PINNED_SSL_START_DATE);
        long pinnedSslEndDateLong = savedState.getLong(PINNED_SSL_END_DATE);

        // Set the pinned SSL start date to `null` if the saved date long is 0 because creating a new date results in an error if the input is 0.
        if (pinnedSslStartDateLong == 0) {
            pinnedSslStartDate = null;
        } else {
            pinnedSslStartDate = new Date(pinnedSslStartDateLong);
        }

        // Set the Pinned SSL end date to `null` if the saved date long is 0 because creating a new date results in an error if the input is 0.
        if (pinnedSslEndDateLong == 0) {
            pinnedSslEndDate = null;
        } else {
            pinnedSslEndDate = new Date(pinnedSslEndDateLong);
        }
    }

    // The Android accessibility guidelines require overriding `performClick()` and calling it from `onTouchEvent()`.
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // Method from NestedScrollingChild.
    @Override
    public boolean isNestedScrollingEnabled() {
        // Return the status of nested scrolling.
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    // Method from NestedScrollingChild.
    @Override
    public void setNestedScrollingEnabled(boolean status) {
        // Set the status of the nested scrolling.
        nestedScrollingChildHelper.setNestedScrollingEnabled(status);
    }

    // Method from NestedScrollingChild.
    @Override
    public boolean startNestedScroll(int axes) {
        // Start a nested scroll along the indicated axes.
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    // Method from NestedScrollingChild2.
    @Override
    public boolean startNestedScroll(int axes, int type) {
        // Start a nested scroll along the indicated axes for the given type of input which caused the scroll event.
        return nestedScrollingChildHelper.startNestedScroll(axes, type);
    }


    // Method from NestedScrollingChild.
    @Override
    public void stopNestedScroll() {
        // Stop the nested scroll.
        nestedScrollingChildHelper.stopNestedScroll();
    }

    // Method from NestedScrollingChild2.
    @Override
    public void stopNestedScroll(int type) {
        // Stop the nested scroll of the given type of input which caused the scroll event.
        nestedScrollingChildHelper.stopNestedScroll(type);
    }


    // Method from NestedScrollingChild.
    @Override
    public boolean hasNestedScrollingParent() {
        // Return the status of the nested scrolling parent.
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    // Method from NestedScrollingChild2.
    @Override
    public boolean hasNestedScrollingParent(int type) {
        // return the status of the nested scrolling parent for the given type of input which caused the scroll event.
        return nestedScrollingChildHelper.hasNestedScrollingParent(type);
    }


    // Method from NestedScrollingChild.
    @Override
    public boolean dispatchNestedPreScroll(int deltaX, int deltaY, int[] consumed, int[] offsetInWindow) {
        // Dispatch a nested pre-scroll with the specified deltas, which lets a parent to consume some of the scroll if desired.
        return nestedScrollingChildHelper.dispatchNestedPreScroll(deltaX, deltaY, consumed, offsetInWindow);
    }

    // Method from NestedScrollingChild2.
    @Override
    public boolean dispatchNestedPreScroll(int deltaX, int deltaY, int[] consumed, int[] offsetInWindow, int type) {
        // Dispatch a nested pre-scroll with the specified deltas for the given type of input which caused the scroll event, which lets a parent to consume some of the scroll if desired.
        return nestedScrollingChildHelper.dispatchNestedPreScroll(deltaX, deltaY, consumed, offsetInWindow, type);
    }


    // Method from NestedScrollingChild.
    @Override
    public boolean dispatchNestedScroll(int deltaXConsumed, int deltaYConsumed, int deltaXUnconsumed, int deltaYUnconsumed, int[] offsetInWindow) {
        // Dispatch a nested scroll with the specified deltas.
        return nestedScrollingChildHelper.dispatchNestedScroll(deltaXConsumed, deltaYConsumed, deltaXUnconsumed, deltaYUnconsumed, offsetInWindow);
    }

    // Method from NestedScrollingChild2.
    @Override
    public boolean dispatchNestedScroll(int deltaXConsumed, int deltaYConsumed, int deltaXUnconsumed, int deltaYUnconsumed, int[] offsetInWindow, int type) {
        // Dispatch a nested scroll with the specified deltas for the given type of input which caused the scroll event.
        return nestedScrollingChildHelper.dispatchNestedScroll(deltaXConsumed, deltaYConsumed, deltaXUnconsumed, deltaYUnconsumed, offsetInWindow, type);
    }


    // Method from NestedScrollingChild.
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        // Dispatch a nested pre-fling with the specified velocity, which lets a parent consume the fling if desired.
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // Method from NestedScrollingChild.
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        // Dispatch a nested fling with the specified velocity.
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }
}