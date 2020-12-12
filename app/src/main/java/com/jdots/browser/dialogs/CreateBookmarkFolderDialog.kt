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
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

import com.jdots.browser.R
import com.jdots.browser.helpers.BookmarksDatabaseHelper

import java.io.ByteArrayOutputStream

// Declare the class constants.
private const val FAVORITE_ICON_BYTE_ARRAY = "favorite_icon_byte_array"

class CreateBookmarkFolderDialog : DialogFragment() {
    // The public interface is used to send information back to the parent activity.
    interface CreateBookmarkFolderListener {
        fun onCreateBookmarkFolder(dialogFragment: DialogFragment, favoriteIconBitmap: Bitmap)
    }

    // Declare the class variables.
    private lateinit var createBookmarkFolderListener: CreateBookmarkFolderListener

    override fun onAttach(context: Context) {
        // Run the default commands.
        super.onAttach(context)

        // Get a handle for the create bookmark folder listener from the launching context.
        createBookmarkFolderListener = context as CreateBookmarkFolderListener
    }

    companion object {
        // `@JvmStatic` will no longer be required once all the code has transitioned to Kotlin.  Also, the function can then be moved out of a companion object and just become a package-level function.
        @JvmStatic
        fun createBookmarkFolder(favoriteIconBitmap: Bitmap): CreateBookmarkFolderDialog {
            // Create a favorite icon byte array output stream.
            val favoriteIconByteArrayOutputStream = ByteArrayOutputStream()

            // Convert the favorite icon to a PNG and place it in the byte array output stream.  `0` is for lossless compression (the only option for a PNG).
            favoriteIconBitmap.compress(Bitmap.CompressFormat.PNG, 0, favoriteIconByteArrayOutputStream)

            // Convert the byte array output stream to a byte array.
            val favoriteIconByteArray = favoriteIconByteArrayOutputStream.toByteArray()

            // Create an arguments bundle.
            val argumentsBundle = Bundle()

            // Store the favorite icon in the bundle.
            argumentsBundle.putByteArray(FAVORITE_ICON_BYTE_ARRAY, favoriteIconByteArray)

            // Create a new instance of the dialog.
            val createBookmarkFolderDialog = CreateBookmarkFolderDialog()

            // Add the bundle to the dialog.
            createBookmarkFolderDialog.arguments = argumentsBundle

            // Return the new dialog.
            return createBookmarkFolderDialog
        }
    }

    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the alert dialog.
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the arguments.
        val arguments = requireArguments()

        // Get the favorite icon byte array.
        val favoriteIconByteArray = arguments.getByteArray(FAVORITE_ICON_BYTE_ARRAY)!!

        // Convert the favorite icon byte array to a bitmap.
        val favoriteIconBitmap = BitmapFactory.decodeByteArray(favoriteIconByteArray, 0, favoriteIconByteArray.size)

        // Use an alert dialog builder to create the dialog.
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog)

        // Set the title.
        dialogBuilder.setTitle(R.string.create_folder)

        // Set the view.  The parent view is null because it will be assigned by the alert dialog.
        dialogBuilder.setView(requireActivity().layoutInflater.inflate(R.layout.create_bookmark_folder_dialog, null))

        // Set a listener on the cancel button.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null)

        // Set a listener on the create button.
        dialogBuilder.setPositiveButton(R.string.create) { _: DialogInterface, _: Int ->
            // Return the dialog fragment to the parent activity on create.
            createBookmarkFolderListener.onCreateBookmarkFolder(this, favoriteIconBitmap)
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

        // Display the keyboard.
        alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // The alert dialog must be shown before the content can be modified.
        alertDialog.show()

        // Get handles for the views in the dialog.
        val webPageIconImageView = alertDialog.findViewById<ImageView>(R.id.create_folder_web_page_icon)!!
        val folderNameEditText = alertDialog.findViewById<EditText>(R.id.create_folder_name_edittext)!!
        val createButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Display the current favorite icon.
        webPageIconImageView.setImageBitmap(favoriteIconBitmap)

        // Initially disable the create button.
        createButton.isEnabled = false

        // Initialize the database helper.  The `0` specifies a database version, but that is ignored and set instead using a constant in `BookmarksDatabaseHelper`.
        val bookmarksDatabaseHelper = BookmarksDatabaseHelper(context, null, null, 0)

        // Enable the create button if the new folder name is unique.
        folderNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(editable: Editable) {
                // Convert the current text to a string.
                val folderName = editable.toString()

                // Check if a folder with the name already exists.
                val folderExistsCursor = bookmarksDatabaseHelper.getFolder(folderName)

                // Enable the create button if the new folder name is not empty and doesn't already exist.
                createButton.isEnabled = folderName.isNotEmpty() && (folderExistsCursor.count == 0)
            }
        })

        // Set the enter key on the keyboard to create the folder from the edit text.
        folderNameEditText.setOnKeyListener { _: View?, keyCode: Int, keyEvent: KeyEvent ->
            // Check the key code, event, and button status.
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && createButton.isEnabled) {  // The event is a key-down on the enter key and the create button is enabled.
                // Trigger the create bookmark folder listener and return the dialog fragment to the parent activity.
                createBookmarkFolderListener.onCreateBookmarkFolder(this, favoriteIconBitmap)

                // Manually dismiss the alert dialog.
                alertDialog.dismiss()

                // Consume the event.
                return@setOnKeyListener true
            } else {  // Some other key was pressed or the create button is disabled.
                return@setOnKeyListener false
            }
        }

        // Return the alert dialog.
        return alertDialog
    }
}