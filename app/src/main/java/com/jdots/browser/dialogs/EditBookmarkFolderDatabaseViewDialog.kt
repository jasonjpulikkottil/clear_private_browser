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

package com.jdots.browser.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.MatrixCursor
import android.database.MergeCursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener

import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.ResourceCursorAdapter
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

import com.jdots.browser.R
import com.jdots.browser.activities.BookmarksDatabaseViewActivity
import com.jdots.browser.helpers.BookmarksDatabaseHelper

import java.io.ByteArrayOutputStream

// Declare the class constants.
private const val DATABASE_ID = "database_id"
private const val FAVORITE_ICON_BYTE_ARRAY = "favorite_icon_byte_array"

class EditBookmarkFolderDatabaseViewDialog : DialogFragment() {
    // The public interface is used to send information back to the parent activity.
    interface EditBookmarkFolderDatabaseViewListener {
        fun onSaveBookmarkFolder(dialogFragment: DialogFragment, selectedFolderDatabaseId: Int, favoriteIconBitmap: Bitmap)
    }

    // Declare the class variables.
    private lateinit var editBookmarkFolderDatabaseViewListener: EditBookmarkFolderDatabaseViewListener

    // Declare the class views.
    private lateinit var nameEditText: EditText
    private lateinit var folderSpinner: Spinner
    private lateinit var displayOrderEditText: EditText
    private lateinit var currentIconRadioButton: RadioButton
    private lateinit var saveButton: Button

    override fun onAttach(context: Context) {
        // Run the default commands.
        super.onAttach(context)

        // Get a handle for edit bookmark database view listener from the launching context.
        editBookmarkFolderDatabaseViewListener = context as EditBookmarkFolderDatabaseViewListener
    }

    companion object {
        // `@JvmStatic` will no longer be required once all the code has transitioned to Kotlin.  Also, the function can then be moved out of a companion object and just become a package-level function.
        @JvmStatic
        fun folderDatabaseId(databaseId: Int, favoriteIconBitmap: Bitmap): EditBookmarkFolderDatabaseViewDialog {
            // Create a favorite icon byte array output stream.
            val favoriteIconByteArrayOutputStream = ByteArrayOutputStream()

            // Convert the favorite icon to a PNG and place it in the byte array output stream.  `0` is for lossless compression (the only option for a PNG).
            favoriteIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, favoriteIconByteArrayOutputStream)

            // Convert the byte array output stream to a byte array.
            val favoriteIconByteArray = favoriteIconByteArrayOutputStream.toByteArray()

            // Create an arguments bundle.
            val argumentsBundle = Bundle()

            // Store the variables in the bundle.
            argumentsBundle.putInt(DATABASE_ID, databaseId)
            argumentsBundle.putByteArray(FAVORITE_ICON_BYTE_ARRAY, favoriteIconByteArray)

            // Create a new instance of the dialog.
            val editBookmarkFolderDatabaseViewDialog = EditBookmarkFolderDatabaseViewDialog()

            // Add the arguments bundle to the dialog.
            editBookmarkFolderDatabaseViewDialog.arguments = argumentsBundle

            // Return the new dialog.
            return editBookmarkFolderDatabaseViewDialog
        }
    }

    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get a handle for the arguments.
        val arguments = requireArguments()

        // Get the variables from the arguments.
        val folderDatabaseId = arguments.getInt(DATABASE_ID)
        val favoriteIconByteArray = arguments.getByteArray(FAVORITE_ICON_BYTE_ARRAY)!!

        // Convert the favorite icon byte array to a bitmap.
        val favoriteIconBitmap = BitmapFactory.decodeByteArray(favoriteIconByteArray, 0, favoriteIconByteArray.size)

        // Initialize the bookmarks database helper.   The `0` specifies a database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
        val bookmarksDatabaseHelper = BookmarksDatabaseHelper(context, null, null, 0)

        // Get a cursor with the selected bookmark.
        val folderCursor = bookmarksDatabaseHelper.getBookmark(folderDatabaseId)

        // Move the cursor to the first position.
        folderCursor.moveToFirst()

        // Use an alert dialog builder to create the alert dialog.
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog)

        // Set the title.
        dialogBuilder.setTitle(R.string.edit_folder)

        // Set the view.  The parent view is `null` because it will be assigned by the alert dialog.
        dialogBuilder.setView(requireActivity().layoutInflater.inflate(R.layout.edit_bookmark_folder_databaseview_dialog, null))

        // Set the cancel button listener.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null)

        // Set the save button listener.
        dialogBuilder.setPositiveButton(R.string.save) { _: DialogInterface?, _: Int ->
            // Return the dialog fragment to the parent activity.
            editBookmarkFolderDatabaseViewListener.onSaveBookmarkFolder(this, folderDatabaseId, favoriteIconBitmap)
        }

        // Create an alert dialog from the alert dialog builder.
        val alertDialog = dialogBuilder.create()

        // Get a handle for the shared preferences.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // Get the screenshot preference.
        val allowScreenshots = sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false)

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            alertDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        // The alert dialog must be shown before items in the layout can be modified.
        alertDialog.show()

        // Get handles for the layout items.
        val databaseIdTextView = alertDialog.findViewById<TextView>(R.id.edit_folder_database_id_textview)!!
        val iconRadioGroup = alertDialog.findViewById<RadioGroup>(R.id.edit_folder_icon_radiogroup)!!
        val currentIconImageView = alertDialog.findViewById<ImageView>(R.id.edit_folder_current_icon_imageview)!!
        val newFavoriteIconImageView = alertDialog.findViewById<ImageView>(R.id.edit_folder_webpage_favorite_icon_imageview)!!
        currentIconRadioButton = alertDialog.findViewById(R.id.edit_folder_current_icon_radiobutton)!!
        nameEditText = alertDialog.findViewById(R.id.edit_folder_name_edittext)!!
        folderSpinner = alertDialog.findViewById(R.id.edit_folder_parent_folder_spinner)!!
        displayOrderEditText = alertDialog.findViewById(R.id.edit_folder_display_order_edittext)!!
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Store the current folder values.
        val currentFolderName = folderCursor.getString(folderCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME))
        val currentDisplayOrder = folderCursor.getInt(folderCursor.getColumnIndex(BookmarksDatabaseHelper.DISPLAY_ORDER))
        val parentFolder = folderCursor.getString(folderCursor.getColumnIndex(BookmarksDatabaseHelper.PARENT_FOLDER))

        // Populate the database ID text view.
        databaseIdTextView.text = folderCursor.getInt(folderCursor.getColumnIndex(BookmarksDatabaseHelper._ID)).toString()

        // Get the current favorite icon byte array from the cursor.
        val currentIconByteArray = folderCursor.getBlob(folderCursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON))

        // Convert the byte array to a bitmap beginning at the first byte and ending at the last.
        val currentIconBitmap = BitmapFactory.decodeByteArray(currentIconByteArray, 0, currentIconByteArray.size)

        // Populate the current icon image view.
        currentIconImageView.setImageBitmap(currentIconBitmap)

        // Populate the new favorite icon image view.
        newFavoriteIconImageView.setImageBitmap(favoriteIconBitmap)

        // Populate the folder name edit text.
        nameEditText.setText(currentFolderName)

        // Define an array of matrix cursor column names.
        val matrixCursorColumnNames = arrayOf(BookmarksDatabaseHelper._ID, BookmarksDatabaseHelper.BOOKMARK_NAME)

        // Create a matrix cursor.
        val matrixCursor = MatrixCursor(matrixCursorColumnNames)

        // Add `Home Folder` to the matrix cursor.
        matrixCursor.addRow(arrayOf(BookmarksDatabaseViewActivity.HOME_FOLDER_DATABASE_ID, getString(R.string.home_folder)))

        // Get a string of the current folder and all subfolders.
        val currentAndSubfolderString = getStringOfSubfolders(currentFolderName, bookmarksDatabaseHelper)

        // Get a cursor with the list of all the folders.
        val foldersCursor = bookmarksDatabaseHelper.getFoldersExcept(currentAndSubfolderString)

        // Combine the matrix cursor and the folders cursor.
        val combinedFoldersCursor = MergeCursor(arrayOf(matrixCursor, foldersCursor))

        // Create a resource cursor adapter for the spinner.
        val foldersCursorAdapter: ResourceCursorAdapter = object : ResourceCursorAdapter(context, R.layout.databaseview_spinner_item, combinedFoldersCursor, 0) {
            override fun bindView(view: View, context: Context, cursor: Cursor) {
                // Get handles for the spinner views.
                val spinnerItemImageView = view.findViewById<ImageView>(R.id.spinner_item_imageview)
                val spinnerItemTextView = view.findViewById<TextView>(R.id.spinner_item_textview)

                // Set the folder icon according to the type.
                if (combinedFoldersCursor.position == 0) {  // Set the `Home Folder` icon.
                    // Set the gray folder image.  `ContextCompat` must be used until the minimum API >= 21.
                    spinnerItemImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.folder_gray))
                } else {  // Set a user folder icon.
                    // Get the folder icon byte array.
                    val folderIconByteArray = cursor.getBlob(cursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON))

                    // Convert the byte array to a bitmap beginning at the first byte and ending at the last.
                    val folderIconBitmap = BitmapFactory.decodeByteArray(folderIconByteArray, 0, folderIconByteArray.size)

                    // Set the folder icon.
                    spinnerItemImageView.setImageBitmap(folderIconBitmap)
                }

                // Set the text view to display the folder name.
                spinnerItemTextView.text = cursor.getString(cursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME))
            }
        }

        // Set the folders cursor adapter drop drown view resource.
        foldersCursorAdapter.setDropDownViewResource(R.layout.databaseview_spinner_dropdown_items)

        // Set the adapter for the folder `Spinner`.
        folderSpinner.adapter = foldersCursorAdapter

        // Select the current folder in the spinner if the bookmark isn't in the "Home Folder".
        if (parentFolder != "") {
            // Get the database ID of the parent folder as a long.
            val parentFolderDatabaseId = bookmarksDatabaseHelper.getFolderDatabaseId(folderCursor.getString(folderCursor.getColumnIndex(BookmarksDatabaseHelper.PARENT_FOLDER))).toLong()

            // Initialize the parent folder position and the iteration variable.
            var parentFolderPosition = 0
            var i = 0

            // Find the parent folder position in the folders cursor adapter.
            do {
                if (foldersCursorAdapter.getItemId(i) == parentFolderDatabaseId) {
                    // Store the current position for the parent folder.
                    parentFolderPosition = i
                } else {
                    // Try the next entry.
                    i++
                }
                // Stop when the parent folder position is found or all the items in the folders cursor adapter have been checked.
            } while (parentFolderPosition == 0 && i < foldersCursorAdapter.count)

            // Select the parent folder in the spinner.
            folderSpinner.setSelection(parentFolderPosition)
        }

        // Store the current folder database ID.
        val currentParentFolderDatabaseId = folderSpinner.selectedItemId.toInt()

        // Populate the display order edit text.
        displayOrderEditText.setText(folderCursor.getInt(folderCursor.getColumnIndex(BookmarksDatabaseHelper.DISPLAY_ORDER)).toString())

        // Initially disable the edit button.
        saveButton.isEnabled = false

        // Update the save button if the icon selection changes.
        iconRadioGroup.setOnCheckedChangeListener { _: RadioGroup?, _: Int ->
            // Update the save button.
            updateSaveButton(bookmarksDatabaseHelper, currentFolderName, currentParentFolderDatabaseId, currentDisplayOrder)
        }

        // Update the save button if the bookmark name changes.
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                // Update the save button.
                updateSaveButton(bookmarksDatabaseHelper, currentFolderName, currentParentFolderDatabaseId, currentDisplayOrder)
            }
        })

        // Update the save button if the folder changes.
        folderSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                // Update the save button.
                updateSaveButton(bookmarksDatabaseHelper, currentFolderName, currentParentFolderDatabaseId, currentDisplayOrder)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing.
            }
        }

        // Update the save button if the display order changes.
        displayOrderEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                // Update the save button.
                updateSaveButton(bookmarksDatabaseHelper, currentFolderName, currentParentFolderDatabaseId, currentDisplayOrder)
            }
        })

        // Allow the enter key on the keyboard to save the bookmark from the bookmark name edit text.
        nameEditText.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            // Check the key code, event, and button status.
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && saveButton.isEnabled) {  // The enter key was pressed and the save button is enabled.
                // Trigger the listener and return the dialog fragment to the parent activity.
                editBookmarkFolderDatabaseViewListener.onSaveBookmarkFolder(this, folderDatabaseId, favoriteIconBitmap)

                // Manually dismiss the alert dialog.
                alertDialog.dismiss()

                // Consume the event.
                return@setOnKeyListener true
            } else {  // If any other key was pressed, or if the save button is currently disabled, do not consume the event.
                return@setOnKeyListener false
            }
        }

        // Allow the enter key on the keyboard to save the bookmark from the display order edit text.
        displayOrderEditText.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            // Check the key code, event, and button status.
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && saveButton.isEnabled) {  // The enter key was pressed and the save button is enabled.
                // Trigger the listener and return the dialog fragment to the parent activity.
                editBookmarkFolderDatabaseViewListener.onSaveBookmarkFolder(this, folderDatabaseId, favoriteIconBitmap)

                // Manually dismiss the alert dialog.
                alertDialog.dismiss()

                // Consume the event.
                return@setOnKeyListener true
            } else { // If any other key was pressed, or if the save button is currently disabled, do not consume the event.
                return@setOnKeyListener false
            }
        }

        // Return the alert dialog.
        return alertDialog
    }

    private fun updateSaveButton(bookmarksDatabaseHelper: BookmarksDatabaseHelper, currentFolderName: String, currentParentFolderDatabaseId: Int, currentDisplayOrder: Int) {
        // Get the values from the views.
        val newFolderName = nameEditText.text.toString()
        val newParentFolderDatabaseId = folderSpinner.selectedItemId.toInt()
        val newDisplayOrder = displayOrderEditText.text.toString()

        // Get a cursor for the new folder name if it exists.
        val folderExistsCursor = bookmarksDatabaseHelper.getFolder(newFolderName)

        // Is the new folder name empty?
        val folderNameNotEmpty = newFolderName.isNotEmpty()

        // Does the folder name already exist?
        val folderNameAlreadyExists = (newFolderName != currentFolderName) && folderExistsCursor.count > 0

        // Has the favorite icon changed?
        val iconChanged = !currentIconRadioButton.isChecked

        // Has the folder been renamed?
        val folderRenamed = (newFolderName != currentFolderName) && !folderNameAlreadyExists

        // Has the parent folder changed?
        val parentFolderChanged = newParentFolderDatabaseId != currentParentFolderDatabaseId

        // Has the display order changed?
        val displayOrderChanged = newDisplayOrder != currentDisplayOrder.toString()

        // Is the display order empty?
        val displayOrderNotEmpty = newDisplayOrder.isNotEmpty()

        // Update the enabled status of the edit button.
        saveButton.isEnabled = (iconChanged || folderRenamed || parentFolderChanged || displayOrderChanged) && folderNameNotEmpty && displayOrderNotEmpty
    }

    private fun getStringOfSubfolders(folderName: String, bookmarksDatabaseHelper: BookmarksDatabaseHelper): String {
        // Get a cursor will all the immediate subfolders.
        val subfoldersCursor = bookmarksDatabaseHelper.getSubfolders(folderName)

        // Initialize the subfolder string builder and populate it with the current folder.
        val currentAndSubfolderStringBuilder = StringBuilder(DatabaseUtils.sqlEscapeString(folderName))

        // Populate the subfolder string builder
        for (i in 0 until subfoldersCursor.count) {
            // Move the subfolder cursor to the current item.
            subfoldersCursor.moveToPosition(i)

            // Get the name of the subfolder.
            val subfolderName = subfoldersCursor.getString(subfoldersCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME))

            // Add a comma to the end of the existing string.
            currentAndSubfolderStringBuilder.append(",")

            // Get the folder name and run the task for any subfolders.
            val subfolderString = getStringOfSubfolders(subfolderName, bookmarksDatabaseHelper)

            // Add the folder name to the string builder.
            currentAndSubfolderStringBuilder.append(subfolderString)
        }

        // Return the string of folders.
        return currentAndSubfolderStringBuilder.toString()
    }
}