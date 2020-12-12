/*
 *   2018-2020
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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.jdots.browser.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImportExportDatabaseHelper {
    // Declare the public constants.
    public static final String EXPORT_SUCCESSFUL = "Export Successful";
    public static final String IMPORT_SUCCESSFUL = "Import Successful";

    // Declare the class constants.
    private static final int SCHEMA_VERSION = 12;
    private static final String PREFERENCES_TABLE = "preferences";

    // Declare the preferences constants.
    private static final String _ID = "_id";
    private static final String JAVASCRIPT = "javascript";
    private static final String FIRST_PARTY_COOKIES = "first_party_cookies";
    private static final String THIRD_PARTY_COOKIES = "third_party_cookies";
    private static final String DOM_STORAGE = "dom_storage";
    private static final String SAVE_FORM_DATA = "save_form_data";
    private static final String USER_AGENT = "user_agent";
    private static final String CUSTOM_USER_AGENT = "custom_user_agent";
    private static final String INCOGNITO_MODE = "incognito_mode";
    private static final String DO_NOT_TRACK = "do_not_track";
    private static final String ALLOW_SCREENSHOTS = "allow_screenshots";
    private static final String EASYLIST = "easylist";
    private static final String EASYPRIVACY = "easyprivacy";
    private static final String FANBOYS_ANNOYANCE_LIST = "fanboys_annoyance_list";
    private static final String FANBOYS_SOCIAL_BLOCKING_LIST = "fanboys_social_blocking_list";
    private static final String ULTRALIST = "ultralist";
    private static final String ULTRAPRIVACY = "ultraprivacy";
    private static final String BLOCK_ALL_THIRD_PARTY_REQUESTS = "block_all_third_party_requests";
    private static final String GOOGLE_ANALYTICS = "google_analytics";
    private static final String FACEBOOK_CLICK_IDS = "facebook_click_ids";
    private static final String TWITTER_AMP_REDIRECTS = "twitter_amp_redirects";
    private static final String SEARCH = "search";
    private static final String SEARCH_CUSTOM_URL = "search_custom_url";
    private static final String PROXY = "proxy";
    private static final String PROXY_CUSTOM_URL = "proxy_custom_url";
    private static final String FULL_SCREEN_BROWSING_MODE = "full_screen_browsing_mode";
    private static final String HIDE_APP_BAR = "hide_app_bar";
    private static final String CLEAR_EVERYTHING = "clear_everything";
    private static final String CLEAR_COOKIES = "clear_cookies";
    private static final String CLEAR_DOM_STORAGE = "clear_dom_storage";
    private static final String CLEAR_FORM_DATA = "clear_form_data";
    private static final String CLEAR_LOGCAT = "clear_logcat";
    private static final String CLEAR_CACHE = "clear_cache";
    private static final String HOMEPAGE = "homepage";
    private static final String DOWNLOAD_LOCATION = "download_location";
    private static final String DOWNLOAD_CUSTOM_LOCATION = "download_custom_location";
    private static final String FONT_SIZE = "font_size";
    private static final String OPEN_INTENTS_IN_NEW_TAB = "open_intents_in_new_tab";
    private static final String SWIPE_TO_REFRESH = "swipe_to_refresh";
    private static final String SCROLL_APP_BAR = "scroll_app_bar";
    private static final String DISPLAY_ADDITIONAL_APP_BAR_ICONS = "display_additional_app_bar_icons";
    private static final String APP_THEME = "app_theme";
    private static final String WEBVIEW_THEME = "webview_theme";
    private static final String WIDE_VIEWPORT = "wide_viewport";
    private static final String DISPLAY_WEBPAGE_IMAGES = "display_webpage_images";

    public String exportUnencrypted(File exportFile, Context context) {
        try {
            // Delete the current file if it exists.
            if (exportFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                exportFile.delete();
            }

            // Create the export database.
            SQLiteDatabase exportDatabase = SQLiteDatabase.openOrCreateDatabase(exportFile, null);

            // Set the export database version number.
            exportDatabase.setVersion(SCHEMA_VERSION);

            // Create the export database domains table.
            exportDatabase.execSQL(DomainsDatabaseHelper.CREATE_DOMAINS_TABLE);


            // Create the export database bookmarks table.
            exportDatabase.execSQL(BookmarksDatabaseHelper.CREATE_BOOKMARKS_TABLE);

            // Open the bookmarks database.  The `0` specifies the database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
            BookmarksDatabaseHelper bookmarksDatabaseHelper = new BookmarksDatabaseHelper(context, null, null, 0);

            // Get a full bookmarks cursor.
            Cursor bookmarksCursor = bookmarksDatabaseHelper.getAllBookmarks();

            // Move to the first bookmark.
            bookmarksCursor.moveToFirst();

            // Copy the data from the bookmarks cursor into the export database.
            for (int i = 0; i < bookmarksCursor.getCount(); i++) {
                // Extract the record from the cursor and store the data in a ContentValues.
                ContentValues bookmarksContentValues = new ContentValues();
                bookmarksContentValues.put(BookmarksDatabaseHelper.BOOKMARK_NAME, bookmarksCursor.getString(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.BOOKMARK_URL, bookmarksCursor.getString(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_URL)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.PARENT_FOLDER, bookmarksCursor.getString(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.PARENT_FOLDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.DISPLAY_ORDER, bookmarksCursor.getInt(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.DISPLAY_ORDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.IS_FOLDER, bookmarksCursor.getInt(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.IS_FOLDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.FAVORITE_ICON, bookmarksCursor.getBlob(bookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON)));

                // Insert the record into the export database.
                exportDatabase.insert(BookmarksDatabaseHelper.BOOKMARKS_TABLE, null, bookmarksContentValues);

                // Advance to the next record.
                bookmarksCursor.moveToNext();
            }

            // Close the bookmarks database.
            bookmarksCursor.close();
            bookmarksDatabaseHelper.close();


            // Open the domains database.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
            DomainsDatabaseHelper domainsDatabaseHelper = new DomainsDatabaseHelper(context, null, null, 0);

            // Get a full domains database cursor.
            Cursor domainsCursor = domainsDatabaseHelper.getCompleteCursorOrderedByDomain();

            // Move to the first domain.
            domainsCursor.moveToFirst();

            // Copy the data from the domains cursor into the export database.
            for (int i = 0; i < domainsCursor.getCount(); i++) {
                // Extract the record from the cursor and store the data in a ContentValues.
                ContentValues domainsContentValues = new ContentValues();
                domainsContentValues.put(DomainsDatabaseHelper.DOMAIN_NAME, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_JAVASCRIPT, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_JAVASCRIPT)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_DOM_STORAGE, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_DOM_STORAGE)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FORM_DATA, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FORM_DATA)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_EASYLIST, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYLIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_EASYPRIVACY, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYPRIVACY)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ULTRALIST, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ULTRALIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY)));
                domainsContentValues.put(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS)));
                domainsContentValues.put(DomainsDatabaseHelper.USER_AGENT, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.USER_AGENT)));
                domainsContentValues.put(DomainsDatabaseHelper.FONT_SIZE, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.FONT_SIZE)));
                domainsContentValues.put(DomainsDatabaseHelper.SWIPE_TO_REFRESH, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SWIPE_TO_REFRESH)));
                domainsContentValues.put(DomainsDatabaseHelper.WEBVIEW_THEME, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.WEBVIEW_THEME)));
                domainsContentValues.put(DomainsDatabaseHelper.WIDE_VIEWPORT, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.WIDE_VIEWPORT)));
                domainsContentValues.put(DomainsDatabaseHelper.DISPLAY_IMAGES, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.DISPLAY_IMAGES)));
                domainsContentValues.put(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_START_DATE, domainsCursor.getLong(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_START_DATE)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_END_DATE, domainsCursor.getLong(domainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_END_DATE)));
                domainsContentValues.put(DomainsDatabaseHelper.PINNED_IP_ADDRESSES, domainsCursor.getInt(domainsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_IP_ADDRESSES)));
                domainsContentValues.put(DomainsDatabaseHelper.IP_ADDRESSES, domainsCursor.getString(domainsCursor.getColumnIndex(DomainsDatabaseHelper.IP_ADDRESSES)));

                // Insert the record into the export database.
                exportDatabase.insert(DomainsDatabaseHelper.DOMAINS_TABLE, null, domainsContentValues);

                // Advance to the next record.
                domainsCursor.moveToNext();
            }

            // Close the domains database.
            domainsCursor.close();
            domainsDatabaseHelper.close();


            // Prepare the preferences table SQL creation string.
            String CREATE_PREFERENCES_TABLE = "CREATE TABLE " + PREFERENCES_TABLE + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    JAVASCRIPT + " BOOLEAN, " +
                    FIRST_PARTY_COOKIES + " BOOLEAN, " +
                    THIRD_PARTY_COOKIES + " BOOLEAN, " +
                    DOM_STORAGE + " BOOLEAN, " +
                    SAVE_FORM_DATA + " BOOLEAN, " +
                    USER_AGENT + " TEXT, " +
                    CUSTOM_USER_AGENT + " TEXT, " +
                    INCOGNITO_MODE + " BOOLEAN, " +
                    DO_NOT_TRACK + " BOOLEAN, " +
                    ALLOW_SCREENSHOTS + " BOOLEAN, " +
                    EASYLIST + " BOOLEAN, " +
                    EASYPRIVACY + " BOOLEAN, " +
                    FANBOYS_ANNOYANCE_LIST + " BOOLEAN, " +
                    FANBOYS_SOCIAL_BLOCKING_LIST + " BOOLEAN, " +
                    ULTRALIST + " BOOLEAN, " +
                    ULTRAPRIVACY + " BOOLEAN, " +
                    BLOCK_ALL_THIRD_PARTY_REQUESTS + " BOOLEAN, " +
                    GOOGLE_ANALYTICS + " BOOLEAN, " +
                    FACEBOOK_CLICK_IDS + " BOOLEAN, " +
                    TWITTER_AMP_REDIRECTS + " BOOLEAN, " +
                    SEARCH + " TEXT, " +
                    SEARCH_CUSTOM_URL + " TEXT, " +
                    PROXY + " TEXT, " +
                    PROXY_CUSTOM_URL + " TEXT, " +
                    FULL_SCREEN_BROWSING_MODE + " BOOLEAN, " +
                    HIDE_APP_BAR + " BOOLEAN, " +
                    CLEAR_EVERYTHING + " BOOLEAN, " +
                    CLEAR_COOKIES + " BOOLEAN, " +
                    CLEAR_DOM_STORAGE + " BOOLEAN, " +
                    CLEAR_FORM_DATA + " BOOLEAN, " +
                    CLEAR_LOGCAT + " BOOLEAN, " +
                    CLEAR_CACHE + " BOOLEAN, " +
                    HOMEPAGE + " TEXT, " +
                    DOWNLOAD_LOCATION + " TEXT, " +
                    DOWNLOAD_CUSTOM_LOCATION + " TEXT, " +
                    FONT_SIZE + " TEXT, " +
                    OPEN_INTENTS_IN_NEW_TAB + " BOOLEAN, " +
                    SWIPE_TO_REFRESH + " BOOLEAN, " +
                    SCROLL_APP_BAR + " BOOLEAN, " +
                    DISPLAY_ADDITIONAL_APP_BAR_ICONS + " BOOLEAN, " +
                    APP_THEME + " TEXT, " +
                    WEBVIEW_THEME + " TEXT, " +
                    WIDE_VIEWPORT + " BOOLEAN, " +
                    DISPLAY_WEBPAGE_IMAGES + " BOOLEAN)";

            // Create the export database preferences table.
            exportDatabase.execSQL(CREATE_PREFERENCES_TABLE);

            // Get a handle for the shared preference.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            // Create a ContentValues with the preferences information.
            ContentValues preferencesContentValues = new ContentValues();
            preferencesContentValues.put(JAVASCRIPT, sharedPreferences.getBoolean(JAVASCRIPT, false));
            preferencesContentValues.put(FIRST_PARTY_COOKIES, sharedPreferences.getBoolean(FIRST_PARTY_COOKIES, false));
            preferencesContentValues.put(THIRD_PARTY_COOKIES, sharedPreferences.getBoolean(THIRD_PARTY_COOKIES, false));
            preferencesContentValues.put(DOM_STORAGE, sharedPreferences.getBoolean(DOM_STORAGE, false));
            preferencesContentValues.put(SAVE_FORM_DATA, sharedPreferences.getBoolean(SAVE_FORM_DATA, false));  // Save form data can be removed once the minimum API >= 26.
            preferencesContentValues.put(USER_AGENT, sharedPreferences.getString(USER_AGENT, context.getString(R.string.user_agent_default_value)));
            preferencesContentValues.put(CUSTOM_USER_AGENT, sharedPreferences.getString(CUSTOM_USER_AGENT, context.getString(R.string.custom_user_agent_default_value)));
            preferencesContentValues.put(INCOGNITO_MODE, sharedPreferences.getBoolean(INCOGNITO_MODE, false));
            preferencesContentValues.put(DO_NOT_TRACK, sharedPreferences.getBoolean(DO_NOT_TRACK, false));
            preferencesContentValues.put(ALLOW_SCREENSHOTS, sharedPreferences.getBoolean(ALLOW_SCREENSHOTS, false));
            preferencesContentValues.put(EASYLIST, sharedPreferences.getBoolean(EASYLIST, true));
            preferencesContentValues.put(EASYPRIVACY, sharedPreferences.getBoolean(EASYPRIVACY, true));
            preferencesContentValues.put(FANBOYS_ANNOYANCE_LIST, sharedPreferences.getBoolean(FANBOYS_ANNOYANCE_LIST, true));
            preferencesContentValues.put(FANBOYS_SOCIAL_BLOCKING_LIST, sharedPreferences.getBoolean(FANBOYS_SOCIAL_BLOCKING_LIST, true));
            preferencesContentValues.put(ULTRALIST, sharedPreferences.getBoolean(ULTRALIST, true));
            preferencesContentValues.put(ULTRAPRIVACY, sharedPreferences.getBoolean(ULTRAPRIVACY, true));
            preferencesContentValues.put(BLOCK_ALL_THIRD_PARTY_REQUESTS, sharedPreferences.getBoolean(BLOCK_ALL_THIRD_PARTY_REQUESTS, false));
            preferencesContentValues.put(GOOGLE_ANALYTICS, sharedPreferences.getBoolean(GOOGLE_ANALYTICS, true));
            preferencesContentValues.put(FACEBOOK_CLICK_IDS, sharedPreferences.getBoolean(FACEBOOK_CLICK_IDS, true));
            preferencesContentValues.put(TWITTER_AMP_REDIRECTS, sharedPreferences.getBoolean(TWITTER_AMP_REDIRECTS, true));
            preferencesContentValues.put(SEARCH, sharedPreferences.getString(SEARCH, context.getString(R.string.search_default_value)));
            preferencesContentValues.put(SEARCH_CUSTOM_URL, sharedPreferences.getString(SEARCH_CUSTOM_URL, context.getString(R.string.search_custom_url_default_value)));
            preferencesContentValues.put(PROXY, sharedPreferences.getString(PROXY, context.getString(R.string.proxy_default_value)));
            preferencesContentValues.put(PROXY_CUSTOM_URL, sharedPreferences.getString(PROXY_CUSTOM_URL, context.getString(R.string.proxy_custom_url_default_value)));
            preferencesContentValues.put(FULL_SCREEN_BROWSING_MODE, sharedPreferences.getBoolean(FULL_SCREEN_BROWSING_MODE, false));
            preferencesContentValues.put(HIDE_APP_BAR, sharedPreferences.getBoolean(HIDE_APP_BAR, true));
            preferencesContentValues.put(CLEAR_EVERYTHING, sharedPreferences.getBoolean(CLEAR_EVERYTHING, true));
            preferencesContentValues.put(CLEAR_COOKIES, sharedPreferences.getBoolean(CLEAR_COOKIES, true));
            preferencesContentValues.put(CLEAR_DOM_STORAGE, sharedPreferences.getBoolean(CLEAR_DOM_STORAGE, true));
            preferencesContentValues.put(CLEAR_FORM_DATA, sharedPreferences.getBoolean(CLEAR_FORM_DATA, true));  // Clear form data can be removed once the minimum API >= 26.
            preferencesContentValues.put(CLEAR_LOGCAT, sharedPreferences.getBoolean(CLEAR_LOGCAT, true));
            preferencesContentValues.put(CLEAR_CACHE, sharedPreferences.getBoolean(CLEAR_CACHE, true));
            preferencesContentValues.put(HOMEPAGE, sharedPreferences.getString(HOMEPAGE, context.getString(R.string.homepage_default_value)));
            preferencesContentValues.put(DOWNLOAD_LOCATION, sharedPreferences.getString(DOWNLOAD_LOCATION, context.getString(R.string.download_location_default_value)));
            preferencesContentValues.put(DOWNLOAD_CUSTOM_LOCATION, sharedPreferences.getString(DOWNLOAD_CUSTOM_LOCATION, context.getString(R.string.download_custom_location_default_value)));
            preferencesContentValues.put(FONT_SIZE, sharedPreferences.getString(FONT_SIZE, context.getString(R.string.font_size_default_value)));
            preferencesContentValues.put(OPEN_INTENTS_IN_NEW_TAB, sharedPreferences.getBoolean(OPEN_INTENTS_IN_NEW_TAB, true));
            preferencesContentValues.put(SWIPE_TO_REFRESH, sharedPreferences.getBoolean(SWIPE_TO_REFRESH, true));
            preferencesContentValues.put(SCROLL_APP_BAR, sharedPreferences.getBoolean(SCROLL_APP_BAR, true));
            preferencesContentValues.put(DISPLAY_ADDITIONAL_APP_BAR_ICONS, sharedPreferences.getBoolean(DISPLAY_ADDITIONAL_APP_BAR_ICONS, false));
            preferencesContentValues.put(APP_THEME, sharedPreferences.getString(APP_THEME, context.getString(R.string.app_theme_default_value)));
            preferencesContentValues.put(WEBVIEW_THEME, sharedPreferences.getString(WEBVIEW_THEME, context.getString(R.string.webview_theme_default_value)));
            preferencesContentValues.put(WIDE_VIEWPORT, sharedPreferences.getBoolean(WIDE_VIEWPORT, true));
            preferencesContentValues.put(DISPLAY_WEBPAGE_IMAGES, sharedPreferences.getBoolean(DISPLAY_WEBPAGE_IMAGES, true));

            // Insert the preferences into the export database.
            exportDatabase.insert(PREFERENCES_TABLE, null, preferencesContentValues);

            // Close the export database.
            exportDatabase.close();

            // Convert the database file to a string.
            String exportFileString = exportFile.toString();

            // Create strings for the temporary database files.
            String journalFileString = exportFileString + "-journal";

            // Get `Files` for the temporary database files.
            File journalFile = new File(journalFileString);

            // Delete the Journal file if it exists.
            if (journalFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                journalFile.delete();
            }

            // Export successful.
            return EXPORT_SUCCESSFUL;
        } catch (Exception exception) {
            // Return the export error.
            return exception.toString();
        }
    }

    public String importUnencrypted(File importFile, Context context) {
        try {
            // Create a temporary import file string.
            String temporaryImportFileString = context.getCacheDir() + "/" + "temporary_import_file";

            // Get a handle for a temporary import file.
            File temporaryImportFile = new File(temporaryImportFileString);

            // Delete the temporary import file if it already exists.
            if (temporaryImportFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                temporaryImportFile.delete();
            }

            // Create input and output streams.
            InputStream importFileInputStream = new FileInputStream(importFile);
            OutputStream temporaryImportFileOutputStream = new FileOutputStream(temporaryImportFile);

            // Create a byte array.
            byte[] transferByteArray = new byte[1024];

            // Create an integer to track the number of bytes read.
            int bytesRead;

            // Copy the import file to the temporary import file.  Once the minimum API >= 26 `Files.copy` can be used instead.
            while ((bytesRead = importFileInputStream.read(transferByteArray)) > 0) {
                temporaryImportFileOutputStream.write(transferByteArray, 0, bytesRead);
            }

            // Close the file streams.
            importFileInputStream.close();
            temporaryImportFileOutputStream.close();


            // Get a handle for the shared preference.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            // Open the import database.  Once the minimum API >= 27 the file can be opened directly without using the string.
            SQLiteDatabase importDatabase = SQLiteDatabase.openDatabase(temporaryImportFileString, null, SQLiteDatabase.OPEN_READWRITE);

            // Get the database version.
            int importDatabaseVersion = importDatabase.getVersion();

            // Upgrade the database if needed.
            if (importDatabaseVersion < SCHEMA_VERSION) {
                switch (importDatabaseVersion) {
                    // Upgrade from schema version 1, Clear Browser 2.13.
                    case 1:
                        // Previously this upgrade added `download_with_external_app` to the Preferences table.  But that is now removed in schema version 10.

                        // Upgrade from schema version 2, Clear Browser 2.14.
                    case 2:
                        // Once the SQLite version is >= 3.25.0 `ALTER TABLE RENAME COLUMN` can be used.  https://www.sqlite.org/lang_altertable.html  https://www.sqlite.org/changes.html
                        // https://developer.android.com/reference/android/database/sqlite/package-summary
                        // In the meantime, a new column must be created with the new name.  There is no need to delete the old column on the temporary import database.

                        // Get a cursor with the current `default_font_size` value.
                        Cursor importDatabasePreferenceCursor = importDatabase.rawQuery("SELECT default_font_size FROM " + PREFERENCES_TABLE, null);

                        // Move to the beginning fo the cursor.
                        importDatabasePreferenceCursor.moveToFirst();

                        // Get the current value in `default_font_size`.
                        String fontSize = importDatabasePreferenceCursor.getString(importDatabasePreferenceCursor.getColumnIndex("default_font_size"));

                        // SQL escape the font size.
                        fontSize = DatabaseUtils.sqlEscapeString(fontSize);

                        // Close the cursor.
                        importDatabasePreferenceCursor.close();

                        // Create the new font size column.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + FONT_SIZE + " TEXT");

                        // Populate the preferences table with the current font size value.
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + FONT_SIZE + " = '" + fontSize + "'");

                        // Upgrade from schema version 3, Clear Browser 2.15.
                    case 3:
                        // Add the Pinned IP Addresses columns to the domains table.
                        importDatabase.execSQL("ALTER TABLE " + DomainsDatabaseHelper.DOMAINS_TABLE + " ADD COLUMN " + DomainsDatabaseHelper.PINNED_IP_ADDRESSES + " BOOLEAN");
                        importDatabase.execSQL("ALTER TABLE " + DomainsDatabaseHelper.DOMAINS_TABLE + " ADD COLUMN " + DomainsDatabaseHelper.IP_ADDRESSES + " TEXT");

                        // Upgrade from schema version 4, Clear Browser 2.16.
                    case 4:
                        // Add the hide and scroll app bar columns to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + HIDE_APP_BAR + " BOOLEAN");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + SCROLL_APP_BAR + " BOOLEAN");

                        // Get the current hide and scroll app bar settings.
                        boolean hideAppBar = sharedPreferences.getBoolean(HIDE_APP_BAR, true);
                        boolean scrollAppBar = sharedPreferences.getBoolean(SCROLL_APP_BAR, true);

                        // Populate the preferences table with the current app bar values.
                        if (hideAppBar) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + HIDE_APP_BAR + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + HIDE_APP_BAR + " = " + 0);
                        }

                        if (scrollAppBar) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + SCROLL_APP_BAR + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + SCROLL_APP_BAR + " = " + 0);
                        }

                        // Upgrade from schema version 5, Clear Browser 2.17.
                    case 5:
                        // Add the open intents in new tab column to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + OPEN_INTENTS_IN_NEW_TAB + " BOOLEAN");

                        // Get the current open intents in new tab preference.
                        boolean openIntentsInNewTab = sharedPreferences.getBoolean(OPEN_INTENTS_IN_NEW_TAB, true);

                        // Populate the preferences table with the current open intents value.
                        if (openIntentsInNewTab) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + OPEN_INTENTS_IN_NEW_TAB + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + OPEN_INTENTS_IN_NEW_TAB + " = " + 0);
                        }

                        // Upgrade from schema version 6, Clear Browser 3.0.
                    case 6:
                        // Add the wide viewport column to the domains table.
                        importDatabase.execSQL("ALTER TABLE " + DomainsDatabaseHelper.DOMAINS_TABLE + " ADD COLUMN " + DomainsDatabaseHelper.WIDE_VIEWPORT + " INTEGER");

                        // Add the Google Analytics, Facebook Click IDs, Twitter AMP redirects, and wide viewport columns to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + GOOGLE_ANALYTICS + " BOOLEAN");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + FACEBOOK_CLICK_IDS + " BOOLEAN");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + TWITTER_AMP_REDIRECTS + " BOOLEAN");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + WIDE_VIEWPORT + " BOOLEAN");

                        // Get the current preference values.
                        boolean googleAnalytics = sharedPreferences.getBoolean(GOOGLE_ANALYTICS, true);
                        boolean facebookClickIds = sharedPreferences.getBoolean(FACEBOOK_CLICK_IDS, true);
                        boolean twitterAmpRedirects = sharedPreferences.getBoolean(TWITTER_AMP_REDIRECTS, true);
                        boolean wideViewport = sharedPreferences.getBoolean(WIDE_VIEWPORT, true);

                        // Populate the preferences with the current Google Analytics value.
                        if (googleAnalytics) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + GOOGLE_ANALYTICS + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + GOOGLE_ANALYTICS + " = " + 0);
                        }

                        // Populate the preferences with the current Facebook Click IDs value.
                        if (facebookClickIds) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + FACEBOOK_CLICK_IDS + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + FACEBOOK_CLICK_IDS + " = " + 0);
                        }

                        // Populate the preferences table with the current Twitter AMP redirects value.
                        if (twitterAmpRedirects) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + TWITTER_AMP_REDIRECTS + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + TWITTER_AMP_REDIRECTS + " = " + 0);
                        }

                        // Populate the preferences table with the current wide viewport value.
                        if (wideViewport) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + WIDE_VIEWPORT + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + WIDE_VIEWPORT + " = " + 0);
                        }

                        // Upgrade from schema version 7, Clear Browser 3.1.
                    case 7:
                        // Add the UltraList column to the domains table.
                        importDatabase.execSQL("ALTER TABLE " + DomainsDatabaseHelper.DOMAINS_TABLE + " ADD COLUMN " + DomainsDatabaseHelper.ULTRALIST + " BOOLEAN");

                        // Add the UltraList column to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + ULTRALIST + " BOOLEAN");

                        // Get the current preference values.
                        boolean ultraList = sharedPreferences.getBoolean(ULTRALIST, true);

                        // Populate the preferences tables with the current UltraList value.
                        if (ultraList) {
                            importDatabase.execSQL("UPDATE " + DomainsDatabaseHelper.DOMAINS_TABLE + " SET " + DomainsDatabaseHelper.ULTRALIST + " = " + 1);
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + ULTRALIST + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + DomainsDatabaseHelper.DOMAINS_TABLE + " SET " + DomainsDatabaseHelper.ULTRALIST + " = " + 0);
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + ULTRALIST + " = " + 0);
                        }

                        // Upgrade from schema version 8, Clear Browser 3.2.
                    case 8:
                        // Add the new proxy columns to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + PROXY + " TEXT");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + PROXY_CUSTOM_URL + " TEXT");

                        // Get the current proxy values.
                        String proxy = sharedPreferences.getString(PROXY, context.getString(R.string.proxy_default_value));
                        String proxyCustomUrl = sharedPreferences.getString(PROXY_CUSTOM_URL, context.getString(R.string.proxy_custom_url_default_value));

                        // SQL escape the proxy custom URL string.
                        proxyCustomUrl = DatabaseUtils.sqlEscapeString(proxyCustomUrl);

                        // Populate the preferences table with the current proxy values. The proxy custom URL does not need to be surrounded by `'` because it was SLQ escaped above.
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + PROXY + " = '" + proxy + "'");
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + PROXY_CUSTOM_URL + " = " + proxyCustomUrl);

                        // Upgrade from schema version 9, Clear Browser 3.3.
                    case 9:
                        // Add the download location columns to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + DOWNLOAD_LOCATION + " TEXT");
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + DOWNLOAD_CUSTOM_LOCATION + " TEXT");

                        // Get the current download location values.
                        String downloadLocation = sharedPreferences.getString(DOWNLOAD_LOCATION, context.getString(R.string.download_location_default_value));
                        String downloadCustomLocation = sharedPreferences.getString(DOWNLOAD_CUSTOM_LOCATION, context.getString(R.string.download_custom_location_default_value));

                        // Populate the preferences table with the current download location values.
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + DOWNLOAD_LOCATION + " = '" + downloadLocation + "'");
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + DOWNLOAD_CUSTOM_LOCATION + " = '" + downloadCustomLocation + "'");

                        // Upgrade from schema version 10, Clear Browser 3.4.
                    case 10:
                        // Add the app theme column to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + APP_THEME + " TEXT");

                        // Get a cursor for the dark theme preference.
                        Cursor darkThemePreferencesCursor = importDatabase.rawQuery("SELECT dark_theme FROM " + PREFERENCES_TABLE, null);

                        // Move to the first preference.
                        darkThemePreferencesCursor.moveToFirst();

                        // Get the old dark theme value, which is in column 0.
                        int darkTheme = darkThemePreferencesCursor.getInt(0);

                        // Close the dark theme preference cursor.
                        darkThemePreferencesCursor.close();

                        // Populate the app theme according to the old dark theme preference.
                        if (darkTheme == 0) {  // A light theme was selected.
                            // Set the app theme to be the system default.
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + APP_THEME + " = 'System default'");
                        } else {  // A dark theme was selected.
                            // Set the app theme to be dark.
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + APP_THEME + " = 'Dark'");
                        }

                        // Add the WebView theme to the domains table.  This defaults to 0, which is `System default`, so a separate step isn't needed to populate the database.
                        importDatabase.execSQL("ALTER TABLE " + DomainsDatabaseHelper.DOMAINS_TABLE + " ADD COLUMN " + DomainsDatabaseHelper.WEBVIEW_THEME + " INTEGER");

                        // Add the WebView theme to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + WEBVIEW_THEME + " TEXT");

                        // Get the WebView theme default value string.
                        String webViewThemeDefaultValue = context.getString(R.string.webview_theme_default_value);

                        // Set the WebView theme in the preferences table to be the default.
                        importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + WEBVIEW_THEME + " = " + "'" + webViewThemeDefaultValue + "'");

                        // Upgrade from schema version 11, Clear Browser 3.5.
                    case 11:
                        // Add the clear logcat column to the preferences table.
                        importDatabase.execSQL("ALTER TABLE " + PREFERENCES_TABLE + " ADD COLUMN " + CLEAR_LOGCAT + " BOOLEAN");

                        // Get the current clear logcat value.
                        boolean clearLogcat = sharedPreferences.getBoolean(CLEAR_LOGCAT, true);

                        // Populate the preference table with the current clear logcat value.
                        if (clearLogcat) {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + CLEAR_LOGCAT + " = " + 1);
                        } else {
                            importDatabase.execSQL("UPDATE " + PREFERENCES_TABLE + " SET " + CLEAR_LOGCAT + " = " + 0);
                        }
                }
            }


            // Get a cursor for the bookmarks table.
            Cursor importBookmarksCursor = importDatabase.rawQuery("SELECT * FROM " + BookmarksDatabaseHelper.BOOKMARKS_TABLE, null);

            // Delete the current bookmarks database.
            context.deleteDatabase(BookmarksDatabaseHelper.BOOKMARKS_DATABASE);

            // Create a new bookmarks database.  The `0` specifies the database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
            BookmarksDatabaseHelper bookmarksDatabaseHelper = new BookmarksDatabaseHelper(context, null, null, 0);

            // Move to the first bookmark.
            importBookmarksCursor.moveToFirst();

            // Copy the data from the import bookmarks cursor into the bookmarks database.
            for (int i = 0; i < importBookmarksCursor.getCount(); i++) {
                // Extract the record from the cursor and store the data in a ContentValues.
                ContentValues bookmarksContentValues = new ContentValues();
                bookmarksContentValues.put(BookmarksDatabaseHelper.BOOKMARK_NAME, importBookmarksCursor.getString(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.BOOKMARK_URL, importBookmarksCursor.getString(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_URL)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.PARENT_FOLDER, importBookmarksCursor.getString(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.PARENT_FOLDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.DISPLAY_ORDER, importBookmarksCursor.getInt(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.DISPLAY_ORDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.IS_FOLDER, importBookmarksCursor.getInt(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.IS_FOLDER)));
                bookmarksContentValues.put(BookmarksDatabaseHelper.FAVORITE_ICON, importBookmarksCursor.getBlob(importBookmarksCursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON)));

                // Insert the record into the export database.
                bookmarksDatabaseHelper.createBookmark(bookmarksContentValues);

                // Advance to the next record.
                importBookmarksCursor.moveToNext();
            }

            // Close the bookmarks cursor.
            importBookmarksCursor.close();

            // Close the bookmarks database.
            bookmarksDatabaseHelper.close();


            // Get a cursor for the domains table.
            Cursor importDomainsCursor = importDatabase.rawQuery("SELECT * FROM " + DomainsDatabaseHelper.DOMAINS_TABLE + " ORDER BY " + DomainsDatabaseHelper.DOMAIN_NAME + " ASC", null);

            // Delete the current domains database.
            context.deleteDatabase(DomainsDatabaseHelper.DOMAINS_DATABASE);

            // Create a new domains database.  The `0` specifies the database version, but that is ignored and set instead using a constant in `DomainsDatabaseHelper`.
            DomainsDatabaseHelper domainsDatabaseHelper = new DomainsDatabaseHelper(context, null, null, 0);

            // Move to the first domain.
            importDomainsCursor.moveToFirst();

            // Copy the data from the import domains cursor into the domains database.
            for (int i = 0; i < importDomainsCursor.getCount(); i++) {
                // Extract the record from the cursor and store the data in a ContentValues.
                ContentValues domainsContentValues = new ContentValues();
                domainsContentValues.put(DomainsDatabaseHelper.DOMAIN_NAME, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.DOMAIN_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_JAVASCRIPT, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_JAVASCRIPT)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FIRST_PARTY_COOKIES)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_THIRD_PARTY_COOKIES)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_DOM_STORAGE, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_DOM_STORAGE)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FORM_DATA, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FORM_DATA)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_EASYLIST, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYLIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_EASYPRIVACY, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_EASYPRIVACY)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST,
                        importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_ANNOYANCE_LIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST,
                        importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_FANBOYS_SOCIAL_BLOCKING_LIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ULTRALIST, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ULTRALIST)));
                domainsContentValues.put(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.ENABLE_ULTRAPRIVACY)));
                domainsContentValues.put(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS,
                        importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.BLOCK_ALL_THIRD_PARTY_REQUESTS)));
                domainsContentValues.put(DomainsDatabaseHelper.USER_AGENT, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.USER_AGENT)));
                domainsContentValues.put(DomainsDatabaseHelper.FONT_SIZE, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.FONT_SIZE)));
                domainsContentValues.put(DomainsDatabaseHelper.SWIPE_TO_REFRESH, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SWIPE_TO_REFRESH)));
                domainsContentValues.put(DomainsDatabaseHelper.WEBVIEW_THEME, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.WEBVIEW_THEME)));
                domainsContentValues.put(DomainsDatabaseHelper.WIDE_VIEWPORT, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.WIDE_VIEWPORT)));
                domainsContentValues.put(DomainsDatabaseHelper.DISPLAY_IMAGES, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.DISPLAY_IMAGES)));
                domainsContentValues.put(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_SSL_CERTIFICATE)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_COMMON_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATION)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT,
                        importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_TO_ORGANIZATIONAL_UNIT)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_COMMON_NAME)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATION)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT,
                        importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_ISSUED_BY_ORGANIZATIONAL_UNIT)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_START_DATE, importDomainsCursor.getLong(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_START_DATE)));
                domainsContentValues.put(DomainsDatabaseHelper.SSL_END_DATE, importDomainsCursor.getLong(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.SSL_END_DATE)));
                domainsContentValues.put(DomainsDatabaseHelper.PINNED_IP_ADDRESSES, importDomainsCursor.getInt(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.PINNED_IP_ADDRESSES)));
                domainsContentValues.put(DomainsDatabaseHelper.IP_ADDRESSES, importDomainsCursor.getString(importDomainsCursor.getColumnIndex(DomainsDatabaseHelper.IP_ADDRESSES)));

                // Insert the record into the export database.
                domainsDatabaseHelper.addDomain(domainsContentValues);

                // Advance to the next record.
                importDomainsCursor.moveToNext();
            }

            // Close the domains cursor.
            importDomainsCursor.close();

            // Close the domains database.
            domainsDatabaseHelper.close();


            // Get a cursor for the preferences table.
            Cursor importPreferencesCursor = importDatabase.rawQuery("SELECT * FROM " + PREFERENCES_TABLE, null);

            // Move to the first preference.
            importPreferencesCursor.moveToFirst();

            // Import the preference data.
            sharedPreferences.edit()
                    .putBoolean(JAVASCRIPT, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(JAVASCRIPT)) == 1)
                    .putBoolean(FIRST_PARTY_COOKIES, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(FIRST_PARTY_COOKIES)) == 1)
                    .putBoolean(THIRD_PARTY_COOKIES, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(THIRD_PARTY_COOKIES)) == 1)
                    .putBoolean(DOM_STORAGE, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(DOM_STORAGE)) == 1)
                    // Save form data can be removed once the minimum API >= 26.
                    .putBoolean(SAVE_FORM_DATA, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(SAVE_FORM_DATA)) == 1)
                    .putString(USER_AGENT, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(USER_AGENT)))
                    .putString(CUSTOM_USER_AGENT, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(CUSTOM_USER_AGENT)))
                    .putBoolean(INCOGNITO_MODE, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(INCOGNITO_MODE)) == 1)
                    .putBoolean(DO_NOT_TRACK, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(DO_NOT_TRACK)) == 1)
                    .putBoolean(ALLOW_SCREENSHOTS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(ALLOW_SCREENSHOTS)) == 1)
                    .putBoolean(EASYLIST, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(EASYLIST)) == 1)
                    .putBoolean(EASYPRIVACY, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(EASYPRIVACY)) == 1)
                    .putBoolean(FANBOYS_ANNOYANCE_LIST, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(FANBOYS_ANNOYANCE_LIST)) == 1)
                    .putBoolean(FANBOYS_SOCIAL_BLOCKING_LIST, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(FANBOYS_SOCIAL_BLOCKING_LIST)) == 1)
                    .putBoolean(ULTRALIST, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(ULTRALIST)) == 1)
                    .putBoolean(ULTRAPRIVACY, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(ULTRAPRIVACY)) == 1)
                    .putBoolean(BLOCK_ALL_THIRD_PARTY_REQUESTS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(BLOCK_ALL_THIRD_PARTY_REQUESTS)) == 1)
                    .putBoolean(GOOGLE_ANALYTICS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(GOOGLE_ANALYTICS)) == 1)
                    .putBoolean(FACEBOOK_CLICK_IDS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(FACEBOOK_CLICK_IDS)) == 1)
                    .putBoolean(TWITTER_AMP_REDIRECTS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(TWITTER_AMP_REDIRECTS)) == 1)
                    .putString(SEARCH, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(SEARCH)))
                    .putString(SEARCH_CUSTOM_URL, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(SEARCH_CUSTOM_URL)))
                    .putString(PROXY, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(PROXY)))
                    .putString(PROXY_CUSTOM_URL, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(PROXY_CUSTOM_URL)))
                    .putBoolean(FULL_SCREEN_BROWSING_MODE, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(FULL_SCREEN_BROWSING_MODE)) == 1)
                    .putBoolean(HIDE_APP_BAR, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(HIDE_APP_BAR)) == 1)
                    .putBoolean(CLEAR_EVERYTHING, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_EVERYTHING)) == 1)
                    .putBoolean(CLEAR_COOKIES, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_COOKIES)) == 1)
                    .putBoolean(CLEAR_DOM_STORAGE, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_DOM_STORAGE)) == 1)
                    // Clear form data can be removed once the minimum API >= 26.
                    .putBoolean(CLEAR_FORM_DATA, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_FORM_DATA)) == 1)
                    .putBoolean(CLEAR_LOGCAT, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_LOGCAT)) == 1)
                    .putBoolean(CLEAR_CACHE, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(CLEAR_CACHE)) == 1)
                    .putString(HOMEPAGE, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(HOMEPAGE)))
                    .putString(DOWNLOAD_LOCATION, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(DOWNLOAD_LOCATION)))
                    .putString(DOWNLOAD_CUSTOM_LOCATION, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(DOWNLOAD_CUSTOM_LOCATION)))
                    .putString(FONT_SIZE, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(FONT_SIZE)))
                    .putBoolean(OPEN_INTENTS_IN_NEW_TAB, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(OPEN_INTENTS_IN_NEW_TAB)) == 1)
                    .putBoolean(SWIPE_TO_REFRESH, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(SWIPE_TO_REFRESH)) == 1)
                    .putBoolean(SCROLL_APP_BAR, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(SCROLL_APP_BAR)) == 1)
                    .putBoolean(DISPLAY_ADDITIONAL_APP_BAR_ICONS, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(DISPLAY_ADDITIONAL_APP_BAR_ICONS)) == 1)
                    .putString(APP_THEME, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(APP_THEME)))
                    .putString(WEBVIEW_THEME, importPreferencesCursor.getString(importPreferencesCursor.getColumnIndex(WEBVIEW_THEME)))
                    .putBoolean(WIDE_VIEWPORT, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(WIDE_VIEWPORT)) == 1)
                    .putBoolean(DISPLAY_WEBPAGE_IMAGES, importPreferencesCursor.getInt(importPreferencesCursor.getColumnIndex(DISPLAY_WEBPAGE_IMAGES)) == 1)
                    .apply();

            // Close the preferences cursor.
            importPreferencesCursor.close();


            // Close the import database.
            importDatabase.close();

            // Create strings for the temporary database files.
            String shmFileString = temporaryImportFileString + "-shm";
            String walFileString = temporaryImportFileString + "-wal";
            String journalFileString = temporaryImportFileString + "-journal";

            // Get `Files` for the temporary database files.
            File shmFile = new File(shmFileString);
            File walFile = new File(walFileString);
            File journalFile = new File(journalFileString);

            // Delete the Shared Memory file if it exists.
            if (shmFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                shmFile.delete();
            }

            // Delete the Write Ahead Log file if it exists.
            if (walFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                walFile.delete();
            }

            // Delete the Journal file if it exists.
            if (journalFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                journalFile.delete();
            }

            // Delete the temporary import file.
            //noinspection ResultOfMethodCallIgnored
            temporaryImportFile.delete();

            // Import successful.
            return IMPORT_SUCCESSFUL;
        } catch (Exception exception) {
            // Return the import error.
            return exception.toString();
        }
    }
}