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

package com.jdots.browser.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager

import com.jdots.browser.R

class AboutViewSourceDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use a builder to create the alert dialog.
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog)

        // Set the icon according to the theme.
        dialogBuilder.setIconAttribute(R.attr.aboutBlueIcon)

        // Set the title.
        dialogBuilder.setTitle(R.string.about_view_source)

        // Set the text.
        dialogBuilder.setMessage(R.string.about_view_source_message)

        // Set a listener on the close button.  Using `null` as the listener closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.close, null)

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

        // Return the alert dialog.
        return alertDialog
    }
}