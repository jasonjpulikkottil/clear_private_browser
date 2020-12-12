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

package com.jdots.browser.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.activities.BookmarksActivity;
import com.jdots.browser.helpers.BookmarksDatabaseHelper;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class MoveToFolderDialog extends DialogFragment {
    // Instantiate the class variables.
    private MoveToFolderListener moveToFolderListener;
    private BookmarksDatabaseHelper bookmarksDatabaseHelper;
    private StringBuilder exceptFolders;

    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for `MoveToFolderListener` from the launching context.
        moveToFolderListener = (MoveToFolderListener) context;
    }

    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize the database helper.  The `0` specifies a database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
        bookmarksDatabaseHelper = new BookmarksDatabaseHelper(getContext(), null, null, 0);

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog);

        // Get the current theme status.
        int currentThemeStatus = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the icon according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            dialogBuilder.setIcon(R.drawable.move_to_folder_blue_day);
        } else {
            dialogBuilder.setIcon(R.drawable.move_to_folder_blue_night);
        }

        // Set the title.
        dialogBuilder.setTitle(R.string.move_to_folder);

        // Remove the incorrect lint warning that `getActivity()` might be null.
        assert getActivity() != null;

        // Set the view.  The parent view is `null` because it will be assigned by `AlertDialog`.
        dialogBuilder.setView(getActivity().getLayoutInflater().inflate(R.layout.move_to_folder_dialog, null));

        // Set the listener for the negative button.
        dialogBuilder.setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
            // Do nothing.  The `AlertDialog` will close automatically.
        });

        // Set the listener fo the positive button.
        dialogBuilder.setPositiveButton(R.string.move, (DialogInterface dialog, int which) -> {
            // Return the `DialogFragment` to the parent activity on save.
            moveToFolderListener.onMoveToFolder(MoveToFolderDialog.this);
        });

        // Create an alert dialog from the alert dialog builder.
        final AlertDialog alertDialog = dialogBuilder.create();

        // Get a handle for the shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get the screenshot preference.
        boolean allowScreenshots = sharedPreferences.getBoolean("allow_screenshots", false);

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            // Remove the warning below that `getWindow()` might be null.
            assert alertDialog.getWindow() != null;

            // Disable screenshots.
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        // Show the alert dialog so the items in the layout can be modified.
        alertDialog.show();

        // Get a handle for the positive button.
        final Button moveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

        // Initially disable the positive button.
        moveButton.setEnabled(false);

        // Initialize the variables.
        Cursor foldersCursor;
        CursorAdapter foldersCursorAdapter;
        exceptFolders = new StringBuilder();

        // Check to see if we are in the `Home Folder`.
        if (BookmarksActivity.currentFolder.isEmpty()) {  // Don't display `Home Folder` at the top of the `ListView`.
            // If a folder is selected, add it and all children to the list of folders not to display.
            long[] selectedBookmarksLongArray = BookmarksActivity.checkedItemIds;
            for (long databaseIdLong : selectedBookmarksLongArray) {
                // Get `databaseIdInt` for each selected bookmark.
                int databaseIdInt = (int) databaseIdLong;

                // If `databaseIdInt` is a folder.
                if (bookmarksDatabaseHelper.isFolder(databaseIdInt)) {
                    // Get the name of the selected folder.
                    String folderName = bookmarksDatabaseHelper.getFolderName(databaseIdInt);

                    // Populate the list of folders not to get.
                    if (exceptFolders.toString().isEmpty()) {
                        // Add the selected folder to the list of folders not to display.
                        exceptFolders.append(DatabaseUtils.sqlEscapeString(folderName));
                    } else {
                        // Add the selected folder to the end of the list of folders not to display.
                        exceptFolders.append(",");
                        exceptFolders.append(DatabaseUtils.sqlEscapeString(folderName));
                    }

                    // Add the selected folder's subfolders to the list of folders not to display.
                    addSubfoldersToExceptFolders(folderName);
                }
            }

            // Get a cursor containing the folders to display.
            foldersCursor = bookmarksDatabaseHelper.getFoldersExcept(exceptFolders.toString());

            // Setup `foldersCursorAdaptor` with `this` context.  `false` disables autoRequery.
            foldersCursorAdapter = new CursorAdapter(alertDialog.getContext(), foldersCursor, false) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    // Remove the incorrect lint warning that `.getLayoutInflater()` might be false.
                    assert getActivity() != null;

                    // Inflate the individual item layout.  `false` does not attach it to the root.
                    return getActivity().getLayoutInflater().inflate(R.layout.move_to_folder_item_linearlayout, parent, false);
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    // Get the folder icon from `cursor`.
                    byte[] folderIconByteArray = cursor.getBlob(cursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON));
                    // Convert the byte array to a `Bitmap` beginning at the first byte and ending at the last.
                    Bitmap folderIconBitmap = BitmapFactory.decodeByteArray(folderIconByteArray, 0, folderIconByteArray.length);
                    // Display `folderIconBitmap` in `move_to_folder_icon`.
                    ImageView folderIconImageView = view.findViewById(R.id.move_to_folder_icon);
                    folderIconImageView.setImageBitmap(folderIconBitmap);

                    // Get the folder name from `cursor` and display it in `move_to_folder_name_textview`.
                    String folderName = cursor.getString(cursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));
                    TextView folderNameTextView = view.findViewById(R.id.move_to_folder_name_textview);
                    folderNameTextView.setText(folderName);
                }
            };
        } else {  // Display `Home Folder` at the top of the `ListView`.
            // Get the home folder icon drawable and convert it to a `Bitmap`.
            Drawable homeFolderIconDrawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.folder_gray_bitmap);
            BitmapDrawable homeFolderIconBitmapDrawable = (BitmapDrawable) homeFolderIconDrawable;
            assert homeFolderIconDrawable != null;
            Bitmap homeFolderIconBitmap = homeFolderIconBitmapDrawable.getBitmap();

            // Convert the folder `Bitmap` to a byte array.  `0` is for lossless compression (the only option for a PNG).
            ByteArrayOutputStream homeFolderIconByteArrayOutputStream = new ByteArrayOutputStream();
            homeFolderIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, homeFolderIconByteArrayOutputStream);
            byte[] homeFolderIconByteArray = homeFolderIconByteArrayOutputStream.toByteArray();

            // Setup a `MatrixCursor` for the `Home Folder`.
            String[] homeFolderMatrixCursorColumnNames = {BookmarksDatabaseHelper._ID, BookmarksDatabaseHelper.BOOKMARK_NAME, BookmarksDatabaseHelper.FAVORITE_ICON};
            MatrixCursor homeFolderMatrixCursor = new MatrixCursor(homeFolderMatrixCursorColumnNames);
            homeFolderMatrixCursor.addRow(new Object[]{0, getString(R.string.home_folder), homeFolderIconByteArray});

            // Add the parent folder to the list of folders not to display.
            exceptFolders.append(DatabaseUtils.sqlEscapeString(BookmarksActivity.currentFolder));

            // If a folder is selected, add it and all children to the list of folders not to display.
            long[] selectedBookmarksLongArray = BookmarksActivity.checkedItemIds;
            for (long databaseIdLong : selectedBookmarksLongArray) {
                // Get `databaseIdInt` for each selected bookmark.
                int databaseIdInt = (int) databaseIdLong;

                // If `databaseIdInt` is a folder.
                if (bookmarksDatabaseHelper.isFolder(databaseIdInt)) {
                    // Get the name of the selected folder.
                    String folderName = bookmarksDatabaseHelper.getFolderName(databaseIdInt);

                    // Add the selected folder to the end of the list of folders not to display.
                    exceptFolders.append(",");
                    exceptFolders.append(DatabaseUtils.sqlEscapeString(folderName));

                    // Add the selected folder's subfolders to the list of folders not to display.
                    addSubfoldersToExceptFolders(folderName);
                }
            }

            // Get a `Cursor` containing the folders to display.
            foldersCursor = bookmarksDatabaseHelper.getFoldersExcept(exceptFolders.toString());

            // Combine `homeFolderMatrixCursor` and `foldersCursor`.
            MergeCursor foldersMergeCursor = new MergeCursor(new Cursor[]{homeFolderMatrixCursor, foldersCursor});

            // Setup `foldersCursorAdaptor`.  `false` disables autoRequery.
            foldersCursorAdapter = new CursorAdapter(alertDialog.getContext(), foldersMergeCursor, false) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    // Remove the incorrect lint warning that `.getLayoutInflater()` might be false.
                    assert getActivity() != null;

                    // Inflate the individual item layout.  `false` does not attach it to the root.
                    return getActivity().getLayoutInflater().inflate(R.layout.move_to_folder_item_linearlayout, parent, false);
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    // Get the folder icon from `cursor`.
                    byte[] folderIconByteArray = cursor.getBlob(cursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON));
                    // Convert the byte array to a `Bitmap` beginning at the first byte and ending at the last.
                    Bitmap folderIconBitmap = BitmapFactory.decodeByteArray(folderIconByteArray, 0, folderIconByteArray.length);
                    // Display `folderIconBitmap` in `move_to_folder_icon`.
                    ImageView folderIconImageView = view.findViewById(R.id.move_to_folder_icon);
                    folderIconImageView.setImageBitmap(folderIconBitmap);

                    // Get the folder name from `cursor` and display it in `move_to_folder_name_textview`.
                    String folderName = cursor.getString(cursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));
                    TextView folderNameTextView = view.findViewById(R.id.move_to_folder_name_textview);
                    folderNameTextView.setText(folderName);
                }
            };
        }

        // Get a handle for the folders list view.
        ListView foldersListView = alertDialog.findViewById(R.id.move_to_folder_listview);

        // Remove the incorrect lint warning below that the view might be null.
        assert foldersListView != null;

        // Set the folder list view adapter.
        foldersListView.setAdapter(foldersCursorAdapter);

        // Enable the move button when a folder is selected.
        foldersListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Enable the move button.
            moveButton.setEnabled(true);
        });

        // `onCreateDialog` requires the return of an `AlertDialog`.
        return alertDialog;
    }

    private void addSubfoldersToExceptFolders(String folderName) {
        // Get a `Cursor` will all the immediate subfolders.
        Cursor subfoldersCursor = bookmarksDatabaseHelper.getSubfolders(folderName);

        for (int i = 0; i < subfoldersCursor.getCount(); i++) {
            // Move `subfolderCursor` to the current item.
            subfoldersCursor.moveToPosition(i);

            // Get the name of the subfolder.
            String subfolderName = subfoldersCursor.getString(subfoldersCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME));

            // Add the subfolder to `exceptFolders`.
            exceptFolders.append(",");
            exceptFolders.append(DatabaseUtils.sqlEscapeString(subfolderName));

            // Run the same tasks for any subfolders of the subfolder.
            addSubfoldersToExceptFolders(subfolderName);
        }
    }

    // The public interface is used to send information back to the parent activity.
    public interface MoveToFolderListener {
        void onMoveToFolder(DialogFragment dialogFragment);
    }
}