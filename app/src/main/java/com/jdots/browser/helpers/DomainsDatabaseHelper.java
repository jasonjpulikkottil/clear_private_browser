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

package com.jdots.browser.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class DomainsDatabaseHelper extends SQLiteOpenHelper {
    public static final String _ID = "_id";
    public static final String DOMAIN_NAME = "domainname";
    public static final String ENABLE_JAVASCRIPT = "enablejavascript";
    public static final String ENABLE_FIRST_PARTY_COOKIES = "enablefirstpartycookies";
    public static final String ENABLE_THIRD_PARTY_COOKIES = "enablethirdpartycookies";
    public static final String ENABLE_DOM_STORAGE = "enabledomstorage";
    public static final String ENABLE_FORM_DATA = "enableformdata";  // Form data can be removed once the minimum API >= 26.
    public static final String ENABLE_EASYLIST = "enableeasylist";
    public static final String ENABLE_EASYPRIVACY = "enableeasyprivacy";
    public static final String ENABLE_FANBOYS_ANNOYANCE_LIST = "enablefanboysannoyancelist";
    public static final String ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST = "enablefanboyssocialblockinglist";
    public static final String ULTRALIST = "ultralist";
    public static final String ENABLE_ULTRAPRIVACY = "enableultraprivacy";
    public static final String BLOCK_ALL_THIRD_PARTY_REQUESTS = "blockallthirdpartyrequests";
    public static final String USER_AGENT = "useragent";
    public static final String FONT_SIZE = "fontsize";
    public static final String SWIPE_TO_REFRESH = "swipetorefresh";
    public static final String WEBVIEW_THEME = "webview_theme";
    public static final String WIDE_VIEWPORT = "wide_viewport";
    public static final String DISPLAY_IMAGES = "displayimages";
    public static final String PINNED_SSL_CERTIFICATE = "pinnedsslcertificate";
    public static final String SSL_ISSUED_TO_COMMON_NAME = "sslissuedtocommonname";
    public static final String SSL_ISSUED_TO_ORGANIZATION = "sslissuedtoorganization";
    public static final String SSL_ISSUED_TO_ORGANIZATIONAL_UNIT = "sslissuedtoorganizationalunit";
    public static final String SSL_ISSUED_BY_COMMON_NAME = "sslissuedbycommonname";
    public static final String SSL_ISSUED_BY_ORGANIZATION = "sslissuedbyorganization";
    public static final String SSL_ISSUED_BY_ORGANIZATIONAL_UNIT = "sslissuedbyorganizationalunit";
    public static final String SSL_START_DATE = "sslstartdate";
    public static final String SSL_END_DATE = "sslenddate";
    public static final String PINNED_IP_ADDRESSES = "pinned_ip_addresses";
    public static final String IP_ADDRESSES = "ip_addresses";
    // Spinner constants.
    public static final int SYSTEM_DEFAULT = 0;
    public static final int ENABLED = 1;
    public static final int DISABLED = 2;
    public static final int LIGHT_THEME = 1;
    public static final int DARK_THEME = 2;
    static final String DOMAINS_DATABASE = "domains.db";
    static final String DOMAINS_TABLE = "domains";
    static final String CREATE_DOMAINS_TABLE = "CREATE TABLE " + DOMAINS_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            DOMAIN_NAME + " TEXT, " +
            ENABLE_JAVASCRIPT + " BOOLEAN, " +
            ENABLE_FIRST_PARTY_COOKIES + " BOOLEAN, " +
            ENABLE_THIRD_PARTY_COOKIES + " BOOLEAN, " +
            ENABLE_DOM_STORAGE + " BOOLEAN, " +
            ENABLE_FORM_DATA + " BOOLEAN, " +
            ENABLE_EASYLIST + " BOOLEAN, " +
            ENABLE_EASYPRIVACY + " BOOLEAN, " +
            ENABLE_FANBOYS_ANNOYANCE_LIST + " BOOLEAN, " +
            ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST + " BOOLEAN, " +
            ULTRALIST + " BOOLEAN, " +
            ENABLE_ULTRAPRIVACY + " BOOLEAN, " +
            BLOCK_ALL_THIRD_PARTY_REQUESTS + " BOOLEAN, " +
            USER_AGENT + " TEXT, " +
            FONT_SIZE + " INTEGER, " +
            SWIPE_TO_REFRESH + " INTEGER, " +
            WEBVIEW_THEME + " INTEGER, " +
            WIDE_VIEWPORT + " INTEGER, " +
            DISPLAY_IMAGES + " INTEGER, " +
            PINNED_SSL_CERTIFICATE + " BOOLEAN, " +
            SSL_ISSUED_TO_COMMON_NAME + " TEXT, " +
            SSL_ISSUED_TO_ORGANIZATION + " TEXT, " +
            SSL_ISSUED_TO_ORGANIZATIONAL_UNIT + " TEXT, " +
            SSL_ISSUED_BY_COMMON_NAME + " TEXT, " +
            SSL_ISSUED_BY_ORGANIZATION + " TEXT, " +
            SSL_ISSUED_BY_ORGANIZATIONAL_UNIT + " TEXT, " +
            SSL_START_DATE + " INTEGER, " +
            SSL_END_DATE + " INTEGER, " +
            PINNED_IP_ADDRESSES + " BOOLEAN, " +
            IP_ADDRESSES + " TEXT)";
    private static final int SCHEMA_VERSION = 12;
    private final Context appContext;

    // Initialize the database.  The lint warnings for the unused parameters are suppressed.
    public DomainsDatabaseHelper(Context context, @SuppressWarnings("UnusedParameters") String name, SQLiteDatabase.CursorFactory cursorFactory, @SuppressWarnings("UnusedParameters") int version) {
        super(context, DOMAINS_DATABASE, cursorFactory, SCHEMA_VERSION);

        // Store a handle for the context.
        appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase domainsDatabase) {
        // Create the domains table.
        domainsDatabase.execSQL(CREATE_DOMAINS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase domainsDatabase, int oldVersion, int newVersion) {
        // Upgrade the database table.
        switch (oldVersion) {
            // Upgrade from schema version 1.
            case 1:
                // Add the display images column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + DISPLAY_IMAGES + " INTEGER");

                // Upgrade from schema version 2.
            case 2:
                //  Add the SSL certificate columns.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + PINNED_SSL_CERTIFICATE + " BOOLEAN");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_TO_COMMON_NAME + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_TO_ORGANIZATION + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_TO_ORGANIZATIONAL_UNIT + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_BY_COMMON_NAME + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_BY_ORGANIZATION + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_ISSUED_BY_ORGANIZATIONAL_UNIT + " TEXT");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_START_DATE + " INTEGER");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SSL_END_DATE + " INTEGER");

                // Upgrade from schema version 3.
            case 3:
                // Add the night mode column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN nightmode INTEGER");

                // Upgrade from schema version 4.
            case 4:
                // Add the block lists columns.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ENABLE_EASYLIST + " BOOLEAN");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ENABLE_EASYPRIVACY + " BOOLEAN");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ENABLE_FANBOYS_ANNOYANCE_LIST + " BOOLEAN");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST + " BOOLEAN");

                // Get a handle for the shared preference.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

                // Get the default block list settings.
                boolean easyListEnabled = sharedPreferences.getBoolean("easylist", true);
                boolean easyPrivacyEnabled = sharedPreferences.getBoolean("easyprivacy", true);
                boolean fanboyAnnoyanceListEnabled = sharedPreferences.getBoolean("fanboys_annoyance_list", true);
                boolean fanboySocialBlockingListEnabled = sharedPreferences.getBoolean("fanboys_social_blocking_list", true);

                // Set EasyList for existing rows according to the current system-wide default.
                if (easyListEnabled) {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_EASYLIST + " = " + 1);
                } else {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_EASYLIST + " = " + 0);
                }

                // Set EasyPrivacy for existing rows according to the current system-wide default.
                if (easyPrivacyEnabled) {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_EASYPRIVACY + " = " + 1);
                } else {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_EASYPRIVACY + " = " + 0);
                }

                // Set Fanboy's Annoyance List for existing rows according to the current system-wide default.
                if (fanboyAnnoyanceListEnabled) {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_FANBOYS_ANNOYANCE_LIST + " = " + 1);
                } else {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_FANBOYS_ANNOYANCE_LIST + " = " + 0);
                }

                // Set Fanboy's Social Blocking List for existing rows according to the current system-wide default.
                if (fanboySocialBlockingListEnabled) {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST + " = " + 1);
                } else {
                    domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST + " = " + 0);
                }

                // Upgrade from schema version 5.
            case 5:
                // Add the swipe to refresh column.  This defaults to `0`, which is `System default`, so a separate step isn't needed to populate the column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + SWIPE_TO_REFRESH + " INTEGER");

                // Upgrade from schema version 6.
            case 6:
                // Add the block all third-party requests column.  This defaults to `0`, which is off, so a separate step isn't needed to populate the column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + BLOCK_ALL_THIRD_PARTY_REQUESTS + " BOOLEAN");

                // Upgrade from schema version 7.
            case 7:
                // Add the UltraPrivacy column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ENABLE_ULTRAPRIVACY + " BOOLEAN");

                // Enable it for all existing rows.
                domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ENABLE_ULTRAPRIVACY + " = " + 1);

                // Upgrade from schema version 8.
            case 8:
                // Add the pinned IP addresses columns.  These default to `0` and `""`, so a separate step isn't needed to populate the columns.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + PINNED_IP_ADDRESSES + " BOOLEAN");
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + IP_ADDRESSES + " TEXT");

                // Upgrade from schema version 9.
            case 9:
                // Add the wide viewport column.  This defaults to `0`, which is `System default`, so a separate step isn't needed to populate the column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + WIDE_VIEWPORT + " INTEGER");

                // Upgrade from schema version 10.
            case 10:
                // Add the UltraList column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + ULTRALIST + " BOOLEAN");

                // Enable it for all existing rows.
                domainsDatabase.execSQL("UPDATE " + DOMAINS_TABLE + " SET " + ULTRALIST + " = " + 1);

                // Upgrade from schema version 11.
            case 11:
                // Add the WebView theme column.  This defaults to `0`, which is `System default`, so a separate step isn't needed to populate the column.
                domainsDatabase.execSQL("ALTER TABLE " + DOMAINS_TABLE + " ADD COLUMN " + WEBVIEW_THEME + " INTEGER");

                // SQLite amazingly doesn't include an easy command to delete a column.  <https://www.sqlite.org/lang_altertable.html>
                // Although a new table could be created and all the data copied to it, I think I will just leave the old night mode column.  It will be wiped out the next time an import is run.
        }
    }

    Cursor getCompleteCursorOrderedByDomain() {
        // Get a readable database handle.
        SQLiteDatabase domainsDatabase = this.getReadableDatabase();

        // Return everything in the domains table ordered by the domain name.  The second argument is `null` because there are no `selectionArgs`.
        return domainsDatabase.rawQuery("SELECT * FROM " + DOMAINS_TABLE + " ORDER BY " + DOMAIN_NAME + " ASC", null);
    }

    public Cursor getDomainNameCursorOrderedByDomain() {
        // Get a readable database handle.
        SQLiteDatabase domainsDatabase = this.getReadableDatabase();

        // Get everything in the domains table ordered by the domain name.
        String GET_CURSOR_ORDERED_BY_DOMAIN = "SELECT " + _ID + ", " + DOMAIN_NAME +
                " FROM " + DOMAINS_TABLE +
                " ORDER BY " + DOMAIN_NAME + " ASC";

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.  The cursor can't be closed because it is needed in the calling activity.
        return domainsDatabase.rawQuery(GET_CURSOR_ORDERED_BY_DOMAIN, null);
    }

    public Cursor getDomainNameCursorOrderedByDomainExcept(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase domainsDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to select all rows except that with `databaseId`.
        String GET_CURSOR_ORDERED_BY_DOMAIN_EXCEPT = "SELECT " + _ID + ", " + DOMAIN_NAME +
                " FROM " + DOMAINS_TABLE +
                " WHERE " + _ID + " IS NOT " + databaseId +
                " ORDER BY " + DOMAIN_NAME + " ASC";

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.  The cursor can't be closed because it is needed in the calling activity.
        return domainsDatabase.rawQuery(GET_CURSOR_ORDERED_BY_DOMAIN_EXCEPT, null);
    }

    public Cursor getCursorForId(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase domainsDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the `Cursor` for `databaseId`.
        String GET_CURSOR_FOR_ID = "SELECT * FROM " + DOMAINS_TABLE +
                " WHERE " + _ID + " = " + databaseId;

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.  The cursor can't be closed because it is needed in the calling activity.
        return domainsDatabase.rawQuery(GET_CURSOR_FOR_ID, null);
    }

    public Cursor getCursorForDomainName(String domainName) {
        // Get a readable database handle.
        SQLiteDatabase domainsDatabase = this.getReadableDatabase();

        // Return a cursor for the requested domain name.
        return domainsDatabase.query(DOMAINS_TABLE, null, DOMAIN_NAME + " = " + "\"" + domainName + "\"", null, null, null, null);

    }

    public int addDomain(String domainName) {
        // Store the domain data in a `ContentValues`.
        ContentValues domainContentValues = new ContentValues();

        // Get a handle for the shared preference.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

        // Get the default settings.
        boolean javaScript = sharedPreferences.getBoolean("javascript", false);
        boolean firstPartyCookies = sharedPreferences.getBoolean("first_party_cookies", false);
        boolean thirdPartyCookies = sharedPreferences.getBoolean("third_party_cookies", false);
        boolean domStorage = sharedPreferences.getBoolean("dom_storage", false);
        boolean saveFormData = sharedPreferences.getBoolean("save_form_data", false);  // Form data can be removed once the minimum API >= 26.
        boolean easyList = sharedPreferences.getBoolean("easylist", true);
        boolean easyPrivacy = sharedPreferences.getBoolean("easyprivacy", true);
        boolean fanboyAnnoyanceList = sharedPreferences.getBoolean("fanboys_annoyance_list", true);
        boolean fanboySocialBlockingList = sharedPreferences.getBoolean("fanboys_social_blocking_list", true);
        boolean ultraList = sharedPreferences.getBoolean("ultralist", true);
        boolean ultraPrivacy = sharedPreferences.getBoolean("ultraprivacy", true);
        boolean blockAllThirdPartyRequests = sharedPreferences.getBoolean("block_all_third_party_requests", false);

        // Create entries for the database fields.  The ID is created automatically.  The pinned SSL certificate information is not created unless added by the user.
        domainContentValues.put(DOMAIN_NAME, domainName);
        domainContentValues.put(ENABLE_JAVASCRIPT, javaScript);
        domainContentValues.put(ENABLE_FIRST_PARTY_COOKIES, firstPartyCookies);
        domainContentValues.put(ENABLE_THIRD_PARTY_COOKIES, thirdPartyCookies);
        domainContentValues.put(ENABLE_DOM_STORAGE, domStorage);
        domainContentValues.put(ENABLE_FORM_DATA, saveFormData);  // Form data can be removed once the minimum API >= 26.
        domainContentValues.put(ENABLE_EASYLIST, easyList);
        domainContentValues.put(ENABLE_EASYPRIVACY, easyPrivacy);
        domainContentValues.put(ENABLE_FANBOYS_ANNOYANCE_LIST, fanboyAnnoyanceList);
        domainContentValues.put(ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST, fanboySocialBlockingList);
        domainContentValues.put(ULTRALIST, ultraList);
        domainContentValues.put(ENABLE_ULTRAPRIVACY, ultraPrivacy);
        domainContentValues.put(BLOCK_ALL_THIRD_PARTY_REQUESTS, blockAllThirdPartyRequests);
        domainContentValues.put(USER_AGENT, "System default user agent");
        domainContentValues.put(FONT_SIZE, 0);
        domainContentValues.put(SWIPE_TO_REFRESH, 0);
        domainContentValues.put(WEBVIEW_THEME, 0);
        domainContentValues.put(WIDE_VIEWPORT, 0);
        domainContentValues.put(DISPLAY_IMAGES, 0);

        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Insert a new row and store the resulting database ID.  The second argument is `null`, which makes it so that a completely null row cannot be created.
        int newDomainDatabaseId = (int) domainsDatabase.insert(DOMAINS_TABLE, null, domainContentValues);

        // Close the database handle.
        domainsDatabase.close();

        // Return the new domain database ID.
        return newDomainDatabaseId;
    }

    void addDomain(ContentValues contentValues) {
        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Add the new domain.
        domainsDatabase.insert(DOMAINS_TABLE, null, contentValues);

        // Close the database handle.
        domainsDatabase.close();
    }

    public void updateDomain(int databaseId, String domainName, boolean javaScript, boolean firstPartyCookies, boolean thirdPartyCookies, boolean domStorage, boolean formData, boolean easyList,
                             boolean easyPrivacy, boolean fanboysAnnoyance, boolean fanboysSocialBlocking, boolean ultraList, boolean ultraPrivacy, boolean blockAllThirdPartyRequests, String userAgent,
                             int fontSize, int swipeToRefresh, int webViewTheme, int wideViewport, int displayImages, boolean pinnedSslCertificate, boolean pinnedIpAddresses) {

        // Store the domain data in a `ContentValues`.
        ContentValues domainContentValues = new ContentValues();

        // Add entries for each field in the database.
        domainContentValues.put(DOMAIN_NAME, domainName);
        domainContentValues.put(ENABLE_JAVASCRIPT, javaScript);
        domainContentValues.put(ENABLE_FIRST_PARTY_COOKIES, firstPartyCookies);
        domainContentValues.put(ENABLE_THIRD_PARTY_COOKIES, thirdPartyCookies);
        domainContentValues.put(ENABLE_DOM_STORAGE, domStorage);
        domainContentValues.put(ENABLE_FORM_DATA, formData);  // Form data can be removed once the minimum API >= 26.
        domainContentValues.put(ENABLE_EASYLIST, easyList);
        domainContentValues.put(ENABLE_EASYPRIVACY, easyPrivacy);
        domainContentValues.put(ENABLE_FANBOYS_ANNOYANCE_LIST, fanboysAnnoyance);
        domainContentValues.put(ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST, fanboysSocialBlocking);
        domainContentValues.put(ULTRALIST, ultraList);
        domainContentValues.put(ENABLE_ULTRAPRIVACY, ultraPrivacy);
        domainContentValues.put(BLOCK_ALL_THIRD_PARTY_REQUESTS, blockAllThirdPartyRequests);
        domainContentValues.put(USER_AGENT, userAgent);
        domainContentValues.put(FONT_SIZE, fontSize);
        domainContentValues.put(SWIPE_TO_REFRESH, swipeToRefresh);
        domainContentValues.put(WEBVIEW_THEME, webViewTheme);
        domainContentValues.put(WIDE_VIEWPORT, wideViewport);
        domainContentValues.put(DISPLAY_IMAGES, displayImages);
        domainContentValues.put(PINNED_SSL_CERTIFICATE, pinnedSslCertificate);
        domainContentValues.put(PINNED_IP_ADDRESSES, pinnedIpAddresses);

        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Update the row for `databaseId`.  The last argument is `null` because there are no `whereArgs`.
        domainsDatabase.update(DOMAINS_TABLE, domainContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        domainsDatabase.close();
    }

    public void updatePinnedSslCertificate(int databaseId, String sslIssuedToCommonName, String sslIssuedToOrganization, String sslIssuedToOrganizationalUnit, String sslIssuedByCommonName,
                                           String sslIssuedByOrganization, String sslIssuedByOrganizationalUnit, long sslStartDate, long sslEndDate) {

        // Store the pinned SSL certificate in a content values.
        ContentValues pinnedSslCertificateContentValues = new ContentValues();

        // Add entries for each field in the certificate.
        pinnedSslCertificateContentValues.put(SSL_ISSUED_TO_COMMON_NAME, sslIssuedToCommonName);
        pinnedSslCertificateContentValues.put(SSL_ISSUED_TO_ORGANIZATION, sslIssuedToOrganization);
        pinnedSslCertificateContentValues.put(SSL_ISSUED_TO_ORGANIZATIONAL_UNIT, sslIssuedToOrganizationalUnit);
        pinnedSslCertificateContentValues.put(SSL_ISSUED_BY_COMMON_NAME, sslIssuedByCommonName);
        pinnedSslCertificateContentValues.put(SSL_ISSUED_BY_ORGANIZATION, sslIssuedByOrganization);
        pinnedSslCertificateContentValues.put(SSL_ISSUED_BY_ORGANIZATIONAL_UNIT, sslIssuedByOrganizationalUnit);
        pinnedSslCertificateContentValues.put(SSL_START_DATE, sslStartDate);
        pinnedSslCertificateContentValues.put(SSL_END_DATE, sslEndDate);

        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Update the row for database ID.
        domainsDatabase.update(DOMAINS_TABLE, pinnedSslCertificateContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        domainsDatabase.close();
    }

    public void updatePinnedIpAddresses(int databaseId, String ipAddresses) {
        // Store the pinned IP addresses in a content values.
        ContentValues pinnedIpAddressesContentValues = new ContentValues();

        // Add the IP addresses to the content values.
        pinnedIpAddressesContentValues.put(IP_ADDRESSES, ipAddresses);

        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Update the row for the database ID.
        domainsDatabase.update(DOMAINS_TABLE, pinnedIpAddressesContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        domainsDatabase.close();
    }

    public void deleteDomain(int databaseId) {
        // Get a writable database handle.
        SQLiteDatabase domainsDatabase = this.getWritableDatabase();

        // Delete the row for `databaseId`.  The last argument is `null` because we don't need additional parameters.
        domainsDatabase.delete(DOMAINS_TABLE, _ID + " = " + databaseId, null);

        // Close the database handle.
        domainsDatabase.close();
    }
}