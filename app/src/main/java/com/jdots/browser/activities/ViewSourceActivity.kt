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

package com.jdots.browser.activities

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.jdots.browser.R
import com.jdots.browser.dialogs.AboutViewSourceDialog
import com.jdots.browser.helpers.ProxyHelper
import com.jdots.browser.viewmodelfactories.WebViewSourceFactory
import com.jdots.browser.viewmodels.WebViewSource
import java.util.*

// Declare the public constants.
const val CURRENT_URL = "current_url"
const val USER_AGENT = "user_agent"

class ViewSourceActivity : AppCompatActivity() {
    // Declare the class variables.
    private lateinit var initialGrayColorSpan: ForegroundColorSpan
    private lateinit var finalGrayColorSpan: ForegroundColorSpan
    private lateinit var redColorSpan: ForegroundColorSpan

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get a handle for the shared preferences.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        // Get the screenshot preference.
        val allowScreenshots = sharedPreferences.getBoolean(getString(R.string.allow_screenshots_key), false)

        // Disable screenshots if not allowed.
        if (!allowScreenshots) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        // Set the theme.
        setTheme(R.style.ClearBrowser)

        // Run the default commands.
        super.onCreate(savedInstanceState)

        // Get the launching intent
        val intent = intent

        // Get the information from the intent.
        val currentUrl = intent.getStringExtra(CURRENT_URL)
        val userAgent = intent.getStringExtra(USER_AGENT)

        // Set the content view.
        setContentView(R.layout.view_source_coordinatorlayout)

        // Get a handle for the toolbar.
        val toolbar = findViewById<Toolbar>(R.id.view_source_toolbar)

        // Set the support action bar.
        setSupportActionBar(toolbar)

        // Get a handle for the action bar.
        val actionBar = supportActionBar!!

        // Add the custom layout to the action bar.
        actionBar.setCustomView(R.layout.view_source_app_bar)

        // Instruct the action bar to display a custom layout.
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        // Get handles for the views.
        val urlEditText = findViewById<EditText>(R.id.url_edittext)
        val requestHeadersTextView = findViewById<TextView>(R.id.request_headers)
        val responseMessageTextView = findViewById<TextView>(R.id.response_message)
        val responseHeadersTextView = findViewById<TextView>(R.id.response_headers)
        val responseBodyTextView = findViewById<TextView>(R.id.response_body)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.view_source_swiperefreshlayout)

        // Populate the URL text box.
        urlEditText.setText(currentUrl)

        // Initialize the gray foreground color spans for highlighting the URLs.  The deprecated `getColor()` must be used until the minimum API >= 23.
        @Suppress("DEPRECATION")
        initialGrayColorSpan = ForegroundColorSpan(resources.getColor(R.color.gray_500))
        @Suppress("DEPRECATION")
        finalGrayColorSpan = ForegroundColorSpan(resources.getColor(R.color.gray_500))

        // Get the current theme status.
        val currentThemeStatus = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // Set the red color span according to the theme.  The deprecated `getColor()` must be used until the minimum API >= 23.
        redColorSpan = if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            @Suppress("DEPRECATION")
            ForegroundColorSpan(resources.getColor(R.color.red_a700))
        } else {
            @Suppress("DEPRECATION")
            ForegroundColorSpan(resources.getColor(R.color.red_900))
        }

        // Apply text highlighting to the URL.
        highlightUrlText()

        // Get a handle for the input method manager, which is used to hide the keyboard.
        val inputMethodManager = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)

        // Remove the formatting from the URL when the user is editing the text.
        urlEditText.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {  // The user is editing the URL text box.
                // Remove the highlighting.
                urlEditText.text.removeSpan(redColorSpan)
                urlEditText.text.removeSpan(initialGrayColorSpan)
                urlEditText.text.removeSpan(finalGrayColorSpan)
            } else {  // The user has stopped editing the URL text box.
                // Hide the soft keyboard.
                inputMethodManager.hideSoftInputFromWindow(urlEditText.windowToken, 0)

                // Move to the beginning of the string.
                urlEditText.setSelection(0)

                // Reapply the highlighting.
                highlightUrlText()
            }
        }

        // Set the refresh color scheme according to the theme.
        if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
            swipeRefreshLayout.setColorSchemeResources(R.color.blue_700)
        } else {
            swipeRefreshLayout.setColorSchemeResources(R.color.violet_500)
        }

        // Initialize a color background typed value.
        val colorBackgroundTypedValue = TypedValue()

        // Get the color background from the theme.
        theme.resolveAttribute(android.R.attr.colorBackground, colorBackgroundTypedValue, true)

        // Get the color background int from the typed value.
        val colorBackgroundInt = colorBackgroundTypedValue.data

        // Set the swipe refresh background color.
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorBackgroundInt)

        // Get the Do Not Track status.
        val doNotTrack = sharedPreferences.getBoolean(getString(R.string.do_not_track_key), false)

        // Populate the locale string.
        val localeString = if (Build.VERSION.SDK_INT >= 24) {  // SDK >= 24 has a list of locales.
            // Get the list of locales.
            val localeList = resources.configuration.locales

            // Initialize a string builder to extract the locales from the list.
            val localesStringBuilder = StringBuilder()

            // Initialize a `q` value, which is used by `WebView` to indicate the order of importance of the languages.
            var q = 10

            // Populate the string builder with the contents of the locales list.
            for (i in 0 until localeList.size()) {
                // Append a comma if there is already an item in the string builder.
                if (i > 0) {
                    localesStringBuilder.append(",")
                }

                // Get the locale from the list.
                val locale = localeList[i]

                // Add the locale to the string.  `locale` by default displays as `en_US`, but WebView uses the `en-US` format.
                localesStringBuilder.append(locale.language)
                localesStringBuilder.append("-")
                localesStringBuilder.append(locale.country)

                // If not the first locale, append `;q=0.x`, which drops by .1 for each removal from the main locale until q=0.1.
                if (q < 10) {
                    localesStringBuilder.append(";q=0.")
                    localesStringBuilder.append(q)
                }

                // Decrement `q` if it is greater than 1.
                if (q > 1) {
                    q--
                }

                // Add a second entry for the language only portion of the locale.
                localesStringBuilder.append(",")
                localesStringBuilder.append(locale.language)

                // Append `1;q=0.x`, which drops by .1 for each removal form the main locale until q=0.1.
                localesStringBuilder.append(";q=0.")
                localesStringBuilder.append(q)

                // Decrement `q` if it is greater than 1.
                if (q > 1) {
                    q--
                }
            }

            // Store the populated string builder in the locale string.
            localesStringBuilder.toString()
        } else {  // SDK < 24 only has a primary locale.
            // Store the locale in the locale string.
            Locale.getDefault().toString()
        }

        // Instantiate the proxy helper.
        val proxyHelper = ProxyHelper()

        // Get the current proxy.
        val proxy = proxyHelper.getCurrentProxy(this)

        // Make the progress bar visible.
        progressBar.visibility = View.VISIBLE

        // Set the progress bar to be indeterminate.
        progressBar.isIndeterminate = true

        // Instantiate the WebView source factory.
        val webViewSourceFactory: ViewModelProvider.Factory = WebViewSourceFactory(currentUrl!!, userAgent!!, doNotTrack, localeString, proxy, MainWebViewActivity.executorService)

        // Instantiate the WebView source view model class.
        val webViewSource = ViewModelProvider(this, webViewSourceFactory).get(WebViewSource::class.java)

        // Create a source observer.
        webViewSource.observeSource().observe(this, { sourceStringArray: Array<SpannableStringBuilder> ->
            // Populate the text views.  This can take a long time, and freezes the user interface, if the response body is particularly large.
            requestHeadersTextView.text = sourceStringArray[0]
            responseMessageTextView.text = sourceStringArray[1]
            responseHeadersTextView.text = sourceStringArray[2]
            responseBodyTextView.text = sourceStringArray[3]

            // Hide the progress bar.
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE

            //Stop the swipe to refresh indicator if it is running
            swipeRefreshLayout.isRefreshing = false
        })

        // Create an error observer.
        webViewSource.observeErrors().observe(this, { errorString: String ->
            // Display an error snackbar if the string is not `""`.
            if (errorString != "") {
                Snackbar.make(swipeRefreshLayout, errorString, Snackbar.LENGTH_LONG).show()
            }
        })

        // Implement swipe to refresh.
        swipeRefreshLayout.setOnRefreshListener {
            // Make the progress bar visible.
            progressBar.visibility = View.VISIBLE

            // Set the progress bar to be indeterminate.
            progressBar.isIndeterminate = true

            // Get the URL.
            val urlString = urlEditText.text.toString()

            // Get the updated source.
            webViewSource.updateSource(urlString)
        }

        // Set the go button on the keyboard to request new source data.
        urlEditText.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            // Request new source data if the enter key was pressed.
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Hide the soft keyboard.
                inputMethodManager.hideSoftInputFromWindow(urlEditText.windowToken, 0)

                // Remove the focus from the URL box.
                urlEditText.clearFocus()

                // Make the progress bar visible.
                progressBar.visibility = View.VISIBLE

                // Set the progress bar to be indeterminate.
                progressBar.isIndeterminate = true

                // Get the URL.
                val urlString = urlEditText.text.toString()

                // Get the updated source.
                webViewSource.updateSource(urlString)

                // Consume the key press.
                return@setOnKeyListener true
            } else {
                // Do not consume the key press.
                return@setOnKeyListener false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu.  This adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.view_source_options_menu, menu)

        // Display the menu.
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        // Get a handle for the about alert dialog.
        val aboutDialogFragment: DialogFragment = AboutViewSourceDialog()

        // Show the about alert dialog.
        aboutDialogFragment.show(supportFragmentManager, getString(R.string.about))

        // Consume the event.
        return true
    }

    // This method must be named `goBack()` and must have a View argument to match the default back arrow in the app bar.
    fun goBack(@Suppress("UNUSED_PARAMETER") view: View) {
        // Go home.
        NavUtils.navigateUpFromSameTask(this)
    }

    private fun highlightUrlText() {
        // Get a handle for the URL edit text.
        val urlEditText = findViewById<EditText>(R.id.url_edittext)

        // Get the URL string.
        val urlString = urlEditText.text.toString()

        // Highlight the URL according to the protocol.
        if (urlString.startsWith("file://")) {  // This is a file URL.
            // De-emphasize only the protocol.
            urlEditText.text.setSpan(initialGrayColorSpan, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        } else if (urlString.startsWith("content://")) {
            // De-emphasize only the protocol.
            urlEditText.text.setSpan(initialGrayColorSpan, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        } else {  // This is a web URL.
            // Get the index of the `/` immediately after the domain name.
            val endOfDomainName = urlString.indexOf("/", urlString.indexOf("//") + 2)

            // Create a base URL string.
            val baseUrl: String

            // Get the base URL.
            baseUrl = if (endOfDomainName > 0) {  // There is at least one character after the base URL.
                // Get the base URL.
                urlString.substring(0, endOfDomainName)
            } else {  // There are no characters after the base URL.
                // Set the base URL to be the entire URL string.
                urlString
            }

            // Get the index of the last `.` in the domain.
            val lastDotIndex = baseUrl.lastIndexOf(".")

            // Get the index of the penultimate `.` in the domain.
            val penultimateDotIndex = baseUrl.lastIndexOf(".", lastDotIndex - 1)

            // Markup the beginning of the URL.
            if (urlString.startsWith("http://")) {  // Highlight the protocol of connections that are not encrypted.
                urlEditText.text.setSpan(redColorSpan, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

                // De-emphasize subdomains.
                if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                    urlEditText.text.setSpan(initialGrayColorSpan, 7, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            } else if (urlString.startsWith("https://")) {  // De-emphasize the protocol of connections that are encrypted.
                if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                    // De-emphasize the protocol and the additional subdomains.
                    urlEditText.text.setSpan(initialGrayColorSpan, 0, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                } else {  // There is only one subdomain in the domain name.
                    // De-emphasize only the protocol.
                    urlEditText.text.setSpan(initialGrayColorSpan, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }

            // De-emphasize the text after the domain name.
            if (endOfDomainName > 0) {
                urlEditText.text.setSpan(finalGrayColorSpan, endOfDomainName, urlString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }
}