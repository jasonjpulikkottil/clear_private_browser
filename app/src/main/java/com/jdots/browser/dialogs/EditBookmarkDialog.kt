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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.jdots.browser.R
import com.jdots.browser.helpers.BookmarksDatabaseHelper
import java.io.ByteArrayOutputStream

// Declare the class constants.
private const val DATABASE_ID = "database_id"
private const val FAVORITE_ICON_BYTE_ARRAY = "favorite_icon_byte_array"

class EditBookmarkDialog : DialogFragment() {
    // The public interface is used to send information back to the parent activity.
    interface EditBookmarkListener {
        fun onSaveBookmark(dialogFragment: DialogFragment, selectedBookmarkDatabaseId: Int, favoriteIconBitmap: Bitmap)
    }

    // Declare the class variables.
    private lateinit var editBookmarkListener: EditBookmarkListener

    // Declare the class views.
    private lateinit var nameEditText: EditText
    private lateinit var urlEditText: EditText
    private lateinit var newIconRadioButton: RadioButton
    private lateinit var saveButton: Button

    override fun onAttach(context: Context) {
        // Run the default commands.
        super.onAttach(context)

        // Get a handle for the edit bookmark listener from the launching context.
        editBookmarkListener = context as EditBookmarkListener
    }

    companion object {
        // `@JvmStatic` will no longer be required once all the code has transitioned to Kotlin.  Also, the function can then be moved out of a companion object and just become a package-level function.
        @JvmStatic
        fun bookmarkDatabaseId(databaseId: Int, favoriteIconBitmap: Bitmap): EditBookmarkDialog {
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
            val editBookmarkDialog = EditBookmarkDialog()

            // Add the arguments bundle to the dialog.
            editBookmarkDialog.arguments = argumentsBundle

            // Return the new dialog.
            return editBookmarkDialog
        }
    }

    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the arguments.
        val arguments = requireArguments()

        // Get the variables from the arguments.
        val selectedBookmarkDatabaseId = arguments.getInt(DATABASE_ID)
        val favoriteIconByteArray = arguments.getByteArray(FAVORITE_ICON_BYTE_ARRAY)!!

        // Convert the favorite icon byte array to a bitmap.
        val favoriteIconBitmap = BitmapFactory.decodeByteArray(favoriteIconByteArray, 0, favoriteIconByteArray.size)

        // Initialize the bookmarks database helper.  The `0` specifies a database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
        val bookmarksDatabaseHelper = BookmarksDatabaseHelper(context, null, null, 0)

        // Get a cursor with the selected bookmark.
        val bookmarkCursor = bookmarksDatabaseHelper.getBookmark(selectedBookmarkDatabaseId)

        // Move the cursor to the first position.
        bookmarkCursor.moveToFirst()

        // Use an alert dialog builder to create the alert dialog.
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog)

        // Set the title.
        dialogBuilder.setTitle(R.string.edit_bookmark)

        // Set the view.  The parent view is null because it will be assigned by the alert dialog.
        dialogBuilder.setView(requireActivity().layoutInflater.inflate(R.layout.edit_bookmark_dialog, null))

        // Set the cancel button listener.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null)

        // Set the save button listener.
        dialogBuilder.setPositiveButton(R.string.save) { _: DialogInterface?, _: Int ->
            // Return the dialog fragment to the parent activity.
            editBookmarkListener.onSaveBookmark(this, selectedBookmarkDatabaseId, favoriteIconBitmap)
        }

        // Create an alert dialog from the builder.
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
        val iconRadioGroup = alertDialog.findViewById<RadioGroup>(R.id.edit_bookmark_icon_radiogroup)!!
        val currentIconImageView = alertDialog.findViewById<ImageView>(R.id.edit_bookmark_current_icon)!!
        val newFavoriteIconImageView = alertDialog.findViewById<ImageView>(R.id.edit_bookmark_webpage_favorite_icon)!!
        newIconRadioButton = alertDialog.findViewById(R.id.edit_bookmark_webpage_favorite_icon_radiobutton)!!
        nameEditText = alertDialog.findViewById(R.id.edit_bookmark_name_edittext)!!
        urlEditText = alertDialog.findViewById(R.id.edit_bookmark_url_edittext)!!
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Get the current favorite icon byte array from the cursor.
        val currentIconByteArray = bookmarkCursor.getBlob(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.FAVORITE_ICON))

        // Convert the byte array to a bitmap beginning at the first byte and ending at the last.
        val currentIconBitmap = BitmapFactory.decodeByteArray(currentIconByteArray, 0, currentIconByteArray.size)

        // Display the current icon bitmap.
        currentIconImageView.setImageBitmap(currentIconBitmap)

        // Set the new favorite icon bitmap.
        newFavoriteIconImageView.setImageBitmap(favoriteIconBitmap)

        // Store the current bookmark name and URL.
        val currentName = bookmarkCursor.getString(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_NAME))
        val currentUrl = bookmarkCursor.getString(bookmarkCursor.getColumnIndex(BookmarksDatabaseHelper.BOOKMARK_URL))

        // Populate the edit texts.
        nameEditText.setText(currentName)
        urlEditText.setText(currentUrl)

        // Initially disable the save button.
        saveButton.isEnabled = false

        // Update the save button if the icon selection changes.
        iconRadioGroup.setOnCheckedChangeListener { _: RadioGroup?, _: Int ->
            // Update the save button.
            updateSaveButton(currentName, currentUrl)
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
                updateSaveButton(currentName, currentUrl)
            }
        })

        // Update the save button if the URL changes.
        urlEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                // Update the edit button.
                updateSaveButton(currentName, currentUrl)
            }
        })

        // Allow the enter key on the keyboard to save the bookmark from the bookmark name edit text.
        nameEditText.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            // Check the key code, event, and button status.
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && saveButton.isEnabled) {  // The enter key was pressed and the save button is enabled.
                // Trigger the listener and return the dialog fragment to the parent activity.
                editBookmarkListener.onSaveBookmark(this, selectedBookmarkDatabaseId, favoriteIconBitmap)

                // Manually dismiss the alert dialog.
                alertDialog.dismiss()

                // Consume the event.
                return@setOnKeyListener true
            } else {  // If any other key was pressed, or if the save button is currently disabled, do not consume the event.
                return@setOnKeyListener false
            }
        }

        // Allow the enter key on the keyboard to save the bookmark from the URL edit text.
        urlEditText.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            // Check the key code, event, and button status.
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && saveButton.isEnabled) {  // The enter key was pressed and the save button is enabled.
                // Trigger the listener and return the dialog fragment to the parent activity.
                editBookmarkListener.onSaveBookmark(this, selectedBookmarkDatabaseId, favoriteIconBitmap)

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

    private fun updateSaveButton(currentName: String, currentUrl: String) {
        // Get the text from the edit texts.
        val newName = nameEditText.text.toString()
        val newUrl = urlEditText.text.toString()

        // Has the favorite icon changed?
        val iconChanged = newIconRadioButton.isChecked

        // Has the name changed?
        val nameChanged = newName != currentName

        // Has the URL changed?
        val urlChanged = newUrl != currentUrl

        // Update the enabled status of the save button.
        saveButton.isEnabled = iconChanged || nameChanged || urlChanged
    }
}