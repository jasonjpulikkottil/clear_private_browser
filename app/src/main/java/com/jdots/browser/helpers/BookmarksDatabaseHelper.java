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

package com.jdots.browser.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarksDatabaseHelper extends SQLiteOpenHelper {
    public static final String _ID = "_id";
    public static final String BOOKMARK_NAME = "bookmarkname";
    public static final String BOOKMARK_URL = "bookmarkurl";
    public static final String PARENT_FOLDER = "parentfolder";
    public static final String DISPLAY_ORDER = "displayorder";
    public static final String IS_FOLDER = "isfolder";
    public static final String FAVORITE_ICON = "favoriteicon";
    static final String BOOKMARKS_DATABASE = "bookmarks.db";
    static final String BOOKMARKS_TABLE = "bookmarks";
    static final String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + BOOKMARKS_TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            BOOKMARK_NAME + " TEXT, " +
            BOOKMARK_URL + " TEXT, " +
            PARENT_FOLDER + " TEXT, " +
            DISPLAY_ORDER + " INTEGER, " +
            IS_FOLDER + " BOOLEAN, " +
            FAVORITE_ICON + " BLOB)";
    private static final int SCHEMA_VERSION = 1;

    // Initialize the database.  The lint warnings for the unused parameters are suppressed.
    public BookmarksDatabaseHelper(Context context, @SuppressWarnings("UnusedParameters") String name, SQLiteDatabase.CursorFactory cursorFactory, @SuppressWarnings("UnusedParameters") int version) {
        super(context, BOOKMARKS_DATABASE, cursorFactory, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase bookmarksDatabase) {
        // Create the bookmarks table.
        bookmarksDatabase.execSQL(CREATE_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bookmarksDatabase, int oldVersion, int newVersion) {
        // Code for upgrading the database will be added here when the schema version > 1.
    }

    // Create a bookmark.
    public void createBookmark(String bookmarkName, String bookmarkURL, String parentFolder, int displayOrder, byte[] favoriteIcon) {
        // Store the bookmark data in a `ContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();

        // ID is created automatically.
        bookmarkContentValues.put(BOOKMARK_NAME, bookmarkName);
        bookmarkContentValues.put(BOOKMARK_URL, bookmarkURL);
        bookmarkContentValues.put(PARENT_FOLDER, parentFolder);
        bookmarkContentValues.put(DISPLAY_ORDER, displayOrder);
        bookmarkContentValues.put(IS_FOLDER, false);
        bookmarkContentValues.put(FAVORITE_ICON, favoriteIcon);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Insert a new row.  The second argument is `null`, which makes it so that a completely null row cannot be created.
        bookmarksDatabase.insert(BOOKMARKS_TABLE, null, bookmarkContentValues);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Create a bookmark from content values.
    void createBookmark(ContentValues contentValues) {
        // Get a writable database.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Insert a new row.  The second argument is `null`, which makes it so that a completely null row cannot be created.
        bookmarksDatabase.insert(BOOKMARKS_TABLE, null, contentValues);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Create a folder.
    public void createFolder(String folderName, String parentFolder, byte[] favoriteIcon) {
        ContentValues bookmarkContentValues = new ContentValues();

        // ID is created automatically.  Folders are always created at the top of the list.
        bookmarkContentValues.put(BOOKMARK_NAME, folderName);
        bookmarkContentValues.put(PARENT_FOLDER, parentFolder);
        bookmarkContentValues.put(DISPLAY_ORDER, 0);
        bookmarkContentValues.put(IS_FOLDER, true);
        bookmarkContentValues.put(FAVORITE_ICON, favoriteIcon);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // The second argument is `null`, which makes it so that completely null rows cannot be created.  Not a problem in our case.
        bookmarksDatabase.insert(BOOKMARKS_TABLE, null, bookmarkContentValues);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Get a `Cursor` for the bookmark with the specified database ID.
    public Cursor getBookmark(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the cursor for the database ID.
        String GET_ONE_BOOKMARK = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " = " + databaseId;

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.  We can't close the `Cursor` because we need to use it in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ONE_BOOKMARK, null);
    }

    // Get the folder name for the specified database ID.
    public String getFolderName(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the cursor for the folder.
        String GET_FOLDER = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " = " + databaseId;

        // Get a folder cursor.
        Cursor folderCursor = bookmarksDatabase.rawQuery(GET_FOLDER, null);

        // Get the folder name.
        folderCursor.moveToFirst();
        String folderName = folderCursor.getString(folderCursor.getColumnIndex(BOOKMARK_NAME));

        // Close the cursor and the database handle.
        folderCursor.close();
        bookmarksDatabase.close();

        // Return the folder name.
        return folderName;
    }

    // Get the database ID for the specified folder name.
    public int getFolderDatabaseId(String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape `folderName`.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Prepare the SQL statement to get the `Cursor` for the folder.
        String GET_FOLDER = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + BOOKMARK_NAME + " = " + folderName +
                " AND " + IS_FOLDER + " = " + 1;

        // Get `folderCursor`.  The second argument is `null` because there are no `selectionArgs`.
        Cursor folderCursor = bookmarksDatabase.rawQuery(GET_FOLDER, null);

        // Get the database ID.
        folderCursor.moveToFirst();
        int databaseId = folderCursor.getInt(folderCursor.getColumnIndex(_ID));

        // Close the cursor and the database handle.
        folderCursor.close();
        bookmarksDatabase.close();

        // Return the database ID.
        return databaseId;
    }

    // Get a cursor for the specified folder name.
    public Cursor getFolder(String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape `folderName`.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Prepare the SQL statement to get the `Cursor` for the folder.
        String GET_FOLDER = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + BOOKMARK_NAME + " = " + folderName +
                " AND " + IS_FOLDER + " = " + 1;

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.
        // We can't close the `Cursor` because we need to use it in the parent activity.
        return bookmarksDatabase.rawQuery(GET_FOLDER, null);
    }

    // Get a cursor of all the folders except those specified.
    public Cursor getFoldersExcept(String exceptFolders) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the `Cursor` for the folders.
        String GET_FOLDERS_EXCEPT = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + IS_FOLDER + " = " + 1 +
                " AND " + BOOKMARK_NAME + " NOT IN (" + exceptFolders +
                ") ORDER BY " + BOOKMARK_NAME + " ASC";

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.
        // We can't close the `Cursor` because we need to use it in the parent activity.
        return bookmarksDatabase.rawQuery(GET_FOLDERS_EXCEPT, null);
    }

    // Get a cursor with all the subfolders of the specified folder.
    public Cursor getSubfolders(String currentFolder) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape `currentFolder.
        currentFolder = DatabaseUtils.sqlEscapeString(currentFolder);

        // Prepare the SQL statement to get the `Cursor` for the subfolders.
        String GET_SUBFOLDERS = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + currentFolder +
                " AND " + IS_FOLDER + " = " + 1;

        // Return the results as a `Cursor`.  The second argument is `null` because there are no `selectionArgs`.
        // We can't close the `Cursor` because we need to use it in the parent activity.
        return bookmarksDatabase.rawQuery(GET_SUBFOLDERS, null);
    }

    // Get the name of the parent folder.
    public String getParentFolderName(String currentFolder) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape `currentFolder`.
        currentFolder = DatabaseUtils.sqlEscapeString(currentFolder);

        // Prepare the SQL statement to get the current folder.
        String GET_CURRENT_FOLDER = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + IS_FOLDER + " = " + 1 +
                " AND " + BOOKMARK_NAME + " = " + currentFolder;

        // Get the bookmark cursor and move to the first entry.
        Cursor bookmarkCursor = bookmarksDatabase.rawQuery(GET_CURRENT_FOLDER, null);
        bookmarkCursor.moveToFirst();

        // Store the name of the parent folder.
        String parentFolder = bookmarkCursor.getString(bookmarkCursor.getColumnIndex(PARENT_FOLDER));

        // Close the cursor.
        bookmarkCursor.close();

        return parentFolder;
    }

    // Get the name of the parent folder.
    public String getParentFolderName(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the current bookmark.
        String GET_BOOKMARK = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " = " + databaseId;

        // Get the bookmark cursor and move to the first entry.
        Cursor bookmarkCursor = bookmarksDatabase.rawQuery(GET_BOOKMARK, null);
        bookmarkCursor.moveToFirst();

        // Store the name of the parent folder.
        String parentFolder = bookmarkCursor.getString(bookmarkCursor.getColumnIndex(PARENT_FOLDER));

        // Close the cursor.
        bookmarkCursor.close();

        return parentFolder;
    }

    // Get a cursor of all the folders.
    public Cursor getAllFolders() {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to get the `Cursor` for all the folders.
        String GET_ALL_FOLDERS = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + IS_FOLDER + " = " + 1 +
                " ORDER BY " + BOOKMARK_NAME + " ASC";

        // Return the results as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ALL_FOLDERS, null);
    }

    // Get a cursor for all bookmarks and folders.
    public Cursor getAllBookmarks() {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Get everything in the bookmarks table.
        String GET_ALL_BOOKMARKS = "SELECT * FROM " + BOOKMARKS_TABLE;

        // Return the result as a Cursor.  The Cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ALL_BOOKMARKS, null);
    }

    // Get a cursor for all bookmarks and folders ordered by display order.
    public Cursor getAllBookmarksByDisplayOrder() {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Get everything in the bookmarks table ordered by display order.
        String GET_ALL_BOOKMARKS = "SELECT * FROM " + BOOKMARKS_TABLE +
                " ORDER BY " + DISPLAY_ORDER + " ASC";

        // Return the result as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ALL_BOOKMARKS, null);
    }

    // Get a cursor for all bookmarks and folders except those with the specified IDs.
    public Cursor getAllBookmarksExcept(long[] exceptIdLongArray) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare a string builder to contain the comma-separated list of IDs not to get.
        StringBuilder idsNotToGetStringBuilder = new StringBuilder();

        // Extract the array of IDs not to get to the string builder.
        for (long databaseIdLong : exceptIdLongArray) {
            // Check to see if there is already a number in the builder.
            if (idsNotToGetStringBuilder.length() > 0) {
                // This is not the first number, so place a `,` before the new number.
                idsNotToGetStringBuilder.append(",");
            }

            // Add the new number to the builder.
            idsNotToGetStringBuilder.append(databaseIdLong);
        }

        // Prepare the SQL statement to select all items except those with the specified IDs.
        String GET_ALL_BOOKMARKS_EXCEPT_SPECIFIED = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " NOT IN (" + idsNotToGetStringBuilder.toString() + ")";

        // Return the results as a cursor.  The cursor cannot be closed because it will be used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ALL_BOOKMARKS_EXCEPT_SPECIFIED, null);
    }

    // Get a cursor for all bookmarks and folders by display order except for a specific of IDs.
    public Cursor getAllBookmarksByDisplayOrderExcept(long[] exceptIdLongArray) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare a string builder to contain the comma-separated list of IDs not to get.
        StringBuilder idsNotToGetStringBuilder = new StringBuilder();

        // Extract the array of IDs not to get to the string builder.
        for (long databaseIdLong : exceptIdLongArray) {
            // Check to see if there is already a number in the builder.
            if (idsNotToGetStringBuilder.length() > 0) {
                // This is not the first number, so place a `,` before the new number.
                idsNotToGetStringBuilder.append(",");
            }

            // Add the new number to the builder.
            idsNotToGetStringBuilder.append(databaseIdLong);
        }

        // Prepare the SQL statement to select all items except those with the specified IDs.
        String GET_ALL_BOOKMARKS_EXCEPT_SPECIFIED = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " NOT IN (" + idsNotToGetStringBuilder.toString() +
                ") ORDER BY " + DISPLAY_ORDER + " ASC";

        // Return the results as a cursor.  The cursor cannot be closed because it will be used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_ALL_BOOKMARKS_EXCEPT_SPECIFIED, null);
    }

    // Get a cursor for bookmarks and folders in the specified folder.
    public Cursor getBookmarks(String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape the folder name.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Get everything in the bookmarks table with `folderName` as the `PARENT_FOLDER`.
        String GET_BOOKMARKS = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + folderName;

        // Return the result as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_BOOKMARKS, null);
    }

    // Get a cursor for bookmarks and folders in the specified folder ordered by display order.
    public Cursor getBookmarksByDisplayOrder(String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape `folderName`.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Get everything in the bookmarks table with `folderName` as the `PARENT_FOLDER`.
        String GET_BOOKMARKS = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + folderName +
                " ORDER BY " + DISPLAY_ORDER + " ASC";

        // Return the result as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_BOOKMARKS, null);
    }

    // Get a cursor with just database ID of bookmarks and folders in the specified folder.  This is useful for deleting folders with bookmarks that have favorite icons too large to fit in a cursor.
    public Cursor getBookmarkIDs(String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // SQL escape the folder name.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Get everything in the bookmarks table with `folderName` as the `PARENT_FOLDER`.
        String GET_BOOKMARKS = "SELECT " + _ID + " FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + folderName;

        // Return the result as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_BOOKMARKS, null);
    }

    // Get a cursor for bookmarks and folders in the specified folder except for ta specific list of IDs.
    public Cursor getBookmarksExcept(long[] exceptIdLongArray, String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare a string builder to contain the comma-separated list of IDs not to get.
        StringBuilder idsNotToGetStringBuilder = new StringBuilder();

        // Extract the array of IDs not to get to the string builder.
        for (long databaseIdLong : exceptIdLongArray) {
            // Check to see if there is already a number in the builder.
            if (idsNotToGetStringBuilder.length() > 0) {
                // This is not the first number, so place a `,` before the new number.
                idsNotToGetStringBuilder.append(",");
            }

            // Add the new number to the builder.
            idsNotToGetStringBuilder.append(databaseIdLong);
        }

        // SQL escape the folder name.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Get everything in the bookmarks table with `folderName` as the `PARENT_FOLDER` except those with the specified IDs.
        String GET_BOOKMARKS_EXCEPT_SPECIFIED = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + folderName +
                " AND " + _ID + " NOT IN (" + idsNotToGetStringBuilder.toString() + ")";

        // Return the result as a cursor.  The cursor cannot be closed because it is used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_BOOKMARKS_EXCEPT_SPECIFIED, null);
    }

    // Get a cursor for bookmarks and folders in the specified folder by display order except for a specific list of IDs.
    public Cursor getBookmarksByDisplayOrderExcept(long[] exceptIdLongArray, String folderName) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare a string builder to contain the comma-separated list of IDs not to get.
        StringBuilder idsNotToGetStringBuilder = new StringBuilder();

        // Extract the array of IDs not to get to the string builder.
        for (long databaseIdLong : exceptIdLongArray) {
            // Check to see if there is already a number in the builder.
            if (idsNotToGetStringBuilder.length() > 0) {
                // This is not the first number, so place a `,` before the new number.
                idsNotToGetStringBuilder.append(",");
            }

            // Add the new number to the builder.
            idsNotToGetStringBuilder.append(databaseIdLong);
        }

        // SQL escape `folderName`.
        folderName = DatabaseUtils.sqlEscapeString(folderName);

        // Prepare the SQL statement to select all items except those with the specified IDs.
        String GET_BOOKMARKS_EXCEPT_SPECIFIED = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + folderName +
                " AND " + _ID + " NOT IN (" + idsNotToGetStringBuilder.toString() +
                ") ORDER BY " + DISPLAY_ORDER + " ASC";

        // Return the results as a cursor.  The cursor cannot be closed because it will be used in the parent activity.
        return bookmarksDatabase.rawQuery(GET_BOOKMARKS_EXCEPT_SPECIFIED, null);
    }

    // Check if a database ID is a folder.
    public boolean isFolder(int databaseId) {
        // Get a readable database handle.
        SQLiteDatabase bookmarksDatabase = this.getReadableDatabase();

        // Prepare the SQL statement to determine if `databaseId` is a folder.
        String CHECK_IF_FOLDER = "SELECT " + IS_FOLDER + " FROM " + BOOKMARKS_TABLE +
                " WHERE " + _ID + " = " + databaseId;

        // Populate the folder cursor.
        Cursor folderCursor = bookmarksDatabase.rawQuery(CHECK_IF_FOLDER, null);

        // Ascertain if this database ID is a folder.
        folderCursor.moveToFirst();
        boolean isFolder = (folderCursor.getInt(folderCursor.getColumnIndex(IS_FOLDER)) == 1);

        // Close the cursor and the database handle.
        folderCursor.close();
        bookmarksDatabase.close();

        return isFolder;
    }

    // Update the bookmark name and URL.
    public void updateBookmark(int databaseId, String bookmarkName, String bookmarkUrl) {
        // Initialize a ContentValues.
        ContentValues bookmarkContentValues = new ContentValues();

        // Store the updated values.
        bookmarkContentValues.put(BOOKMARK_NAME, bookmarkName);
        bookmarkContentValues.put(BOOKMARK_URL, bookmarkUrl);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the bookmark.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the bookmark name, URL, parent folder, and display order.
    public void updateBookmark(int databaseId, String bookmarkName, String bookmarkUrl, String parentFolder, int displayOrder) {
        // Initialize a `ContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();

        // Store the updated values.
        bookmarkContentValues.put(BOOKMARK_NAME, bookmarkName);
        bookmarkContentValues.put(BOOKMARK_URL, bookmarkUrl);
        bookmarkContentValues.put(PARENT_FOLDER, parentFolder);
        bookmarkContentValues.put(DISPLAY_ORDER, displayOrder);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the bookmark.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the bookmark name, URL, and favorite icon.
    public void updateBookmark(int databaseId, String bookmarkName, String bookmarkUrl, byte[] favoriteIcon) {
        // Initialize a `ContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();

        // Store the updated values.
        bookmarkContentValues.put(BOOKMARK_NAME, bookmarkName);
        bookmarkContentValues.put(BOOKMARK_URL, bookmarkUrl);
        bookmarkContentValues.put(FAVORITE_ICON, favoriteIcon);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the bookmark.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the bookmark name, URL, parent folder, display order, and favorite icon.
    public void updateBookmark(int databaseId, String bookmarkName, String bookmarkUrl, String parentFolder, int displayOrder, byte[] favoriteIcon) {
        // Initialize a `ContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();

        // Store the updated values.
        bookmarkContentValues.put(BOOKMARK_NAME, bookmarkName);
        bookmarkContentValues.put(BOOKMARK_URL, bookmarkUrl);
        bookmarkContentValues.put(PARENT_FOLDER, parentFolder);
        bookmarkContentValues.put(DISPLAY_ORDER, displayOrder);
        bookmarkContentValues.put(FAVORITE_ICON, favoriteIcon);

        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the bookmark.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the folder name.
    public void updateFolder(int databaseId, String oldFolderName, String newFolderName) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the folder first.  Store the new folder name in `folderContentValues`.
        ContentValues folderContentValues = new ContentValues();
        folderContentValues.put(BOOKMARK_NAME, newFolderName);

        // Run the update on the folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, folderContentValues, _ID + " = " + databaseId, null);

        // Update the bookmarks inside the folder.  Store the new parent folder name in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(PARENT_FOLDER, newFolderName);

        // SQL escape `oldFolderName`.
        oldFolderName = DatabaseUtils.sqlEscapeString(oldFolderName);

        // Run the update on all the bookmarks that currently list `oldFolderName` as their parent folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, PARENT_FOLDER + " = " + oldFolderName, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the folder icon.
    public void updateFolder(int databaseId, byte[] folderIcon) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Store the updated icon in `folderContentValues`.
        ContentValues folderContentValues = new ContentValues();
        folderContentValues.put(FAVORITE_ICON, folderIcon);

        // Run the update on the folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, folderContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the folder name, parent folder, and display order.
    public void updateFolder(int databaseId, String oldFolderName, String newFolderName, String parentFolder, int displayOrder) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the folder first.  Store the new folder name in `folderContentValues`.
        ContentValues folderContentValues = new ContentValues();
        folderContentValues.put(BOOKMARK_NAME, newFolderName);
        folderContentValues.put(PARENT_FOLDER, parentFolder);
        folderContentValues.put(DISPLAY_ORDER, displayOrder);

        // Run the update on the folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, folderContentValues, _ID + " = " + databaseId, null);

        // Update the bookmarks inside the folder.  Store the new parent folder name in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(PARENT_FOLDER, newFolderName);

        // SQL escape `oldFolderName`.
        oldFolderName = DatabaseUtils.sqlEscapeString(oldFolderName);

        // Run the update on all the bookmarks that currently list `oldFolderName` as their parent folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, PARENT_FOLDER + " = " + oldFolderName, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the folder name and icon.
    public void updateFolder(int databaseId, String oldFolderName, String newFolderName, byte[] folderIcon) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the folder first.  Store the updated values in `folderContentValues`.
        ContentValues folderContentValues = new ContentValues();
        folderContentValues.put(BOOKMARK_NAME, newFolderName);
        folderContentValues.put(FAVORITE_ICON, folderIcon);

        // Run the update on the folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, folderContentValues, _ID + " = " + databaseId, null);

        // Update the bookmarks inside the folder.  Store the new parent folder name in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(PARENT_FOLDER, newFolderName);

        // SQL escape `oldFolderName`.
        oldFolderName = DatabaseUtils.sqlEscapeString(oldFolderName);

        // Run the update on all the bookmarks that currently list `oldFolderName` as their parent folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, PARENT_FOLDER + " = " + oldFolderName, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the folder name and icon.
    public void updateFolder(int databaseId, String oldFolderName, String newFolderName, String parentFolder, int displayOrder, byte[] folderIcon) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Update the folder first.  Store the updated values in `folderContentValues`.
        ContentValues folderContentValues = new ContentValues();
        folderContentValues.put(BOOKMARK_NAME, newFolderName);
        folderContentValues.put(PARENT_FOLDER, parentFolder);
        folderContentValues.put(DISPLAY_ORDER, displayOrder);
        folderContentValues.put(FAVORITE_ICON, folderIcon);

        // Run the update on the folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, folderContentValues, _ID + " = " + databaseId, null);

        // Update the bookmarks inside the folder.  Store the new parent folder name in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(PARENT_FOLDER, newFolderName);

        // SQL escape `oldFolderName`.
        oldFolderName = DatabaseUtils.sqlEscapeString(oldFolderName);

        // Run the update on all the bookmarks that currently list `oldFolderName` as their parent folder.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, PARENT_FOLDER + " = " + oldFolderName, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Update the display order for one bookmark or folder.
    public void updateDisplayOrder(int databaseId, int displayOrder) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Store the new display order in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(DISPLAY_ORDER, displayOrder);

        // Update the database.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Move one bookmark or folder to a new folder.
    public void moveToFolder(int databaseId, String newFolder) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // SQL escape the new folder name.
        String newFolderSqlEscaped = DatabaseUtils.sqlEscapeString(newFolder);

        // Prepare a SQL query to select all the bookmarks in the new folder.
        String NEW_FOLDER = "SELECT * FROM " + BOOKMARKS_TABLE +
                " WHERE " + PARENT_FOLDER + " = " + newFolderSqlEscaped +
                " ORDER BY " + DISPLAY_ORDER + " ASC";

        // Get a cursor for all the bookmarks in the new folder.  The second argument is `null` because there are no `selectionArgs`.
        Cursor newFolderCursor = bookmarksDatabase.rawQuery(NEW_FOLDER, null);

        // Instantiate a variable to store the display order after the move.
        int displayOrder;

        // Set the new display order.
        if (newFolderCursor.getCount() > 0) {  // There are already bookmarks in the folder.
            // Move to the last bookmark.
            newFolderCursor.moveToLast();

            // Set the display order to be one greater that the last bookmark.
            displayOrder = newFolderCursor.getInt(newFolderCursor.getColumnIndex(DISPLAY_ORDER)) + 1;
        } else {  // There are no bookmarks in the new folder.
            // Set the display order to be `0`.
            displayOrder = 0;
        }

        // Close the new folder `Cursor`.
        newFolderCursor.close();

        // Store the new values in `bookmarkContentValues`.
        ContentValues bookmarkContentValues = new ContentValues();
        bookmarkContentValues.put(DISPLAY_ORDER, displayOrder);
        bookmarkContentValues.put(PARENT_FOLDER, newFolder);

        // Update the database.  The last argument is `null` because there are no `whereArgs`.
        bookmarksDatabase.update(BOOKMARKS_TABLE, bookmarkContentValues, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }

    // Delete one bookmark.
    public void deleteBookmark(int databaseId) {
        // Get a writable database handle.
        SQLiteDatabase bookmarksDatabase = this.getWritableDatabase();

        // Deletes the row with the given `databaseId`.  The last argument is `null` because we don't need additional parameters.
        bookmarksDatabase.delete(BOOKMARKS_TABLE, _ID + " = " + databaseId, null);

        // Close the database handle.
        bookmarksDatabase.close();
    }
}