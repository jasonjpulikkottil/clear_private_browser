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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebBackForwardList;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.activities.MainWebViewActivity;
import com.jdots.browser.adapters.HistoryArrayAdapter;
import com.jdots.browser.definitions.History;
import com.jdots.browser.fragments.WebViewTabFragment;
import com.jdots.browser.views.NestedScrollWebView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class UrlHistoryDialog extends DialogFragment {
    // The navigate history listener is used in `onAttach()` and `onCreateDialog()`.
    private NavigateHistoryListener navigateHistoryListener;

    public static UrlHistoryDialog loadBackForwardList(long webViewFragmentId) {
        // Create an arguments bundle.
        Bundle argumentsBundle = new Bundle();

        // Store the WebView fragment ID in the bundle.
        argumentsBundle.putLong("webview_fragment_id", webViewFragmentId);

        // Create a new instance of the URL history dialog.
        UrlHistoryDialog urlHistoryDialog = new UrlHistoryDialog();

        // Add the arguments bundle to this instance.
        urlHistoryDialog.setArguments(argumentsBundle);

        // Return the new URL history dialog.
        return urlHistoryDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        // Run the default commands.
        super.onAttach(context);

        // Get a handle for the listener from the launching context.
        navigateHistoryListener = (NavigateHistoryListener) context;
    }

    @Override
    @NonNull
    // `@SuppressLint("InflateParams")` removes the warning about using `null` as the parent view group when inflating the `AlertDialog`.
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the activity's layout inflater.
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        // Get the arguments.
        Bundle arguments = getArguments();

        // Remove the incorrect lint error that arguments might be null.
        assert arguments != null;

        // Get the WebView fragment ID from the arguments.
        long webViewFragmentId = arguments.getLong("webview_fragment_id");

        // Get the current position of this WebView fragment.
        int webViewPosition = MainWebViewActivity.webViewPagerAdapter.getPositionForId(webViewFragmentId);

        // Get the WebView tab fragment.
        WebViewTabFragment webViewTabFragment = MainWebViewActivity.webViewPagerAdapter.getPageFragment(webViewPosition);

        // Get the fragment view.
        View fragmentView = webViewTabFragment.getView();

        // Remove the incorrect lint warning below that the fragment view might be null.
        assert fragmentView != null;

        // Get a handle for the current WebView.
        NestedScrollWebView nestedScrollWebView = fragmentView.findViewById(R.id.nestedscroll_webview);

        // Get the web back forward list from the WebView.
        WebBackForwardList webBackForwardList = nestedScrollWebView.copyBackForwardList();

        // Store the current page index.
        int currentPageIndex = webBackForwardList.getCurrentIndex();

        // Remove the lint warning below that `getContext()` might be null.
        assert getContext() != null;

        // Get the default favorite icon drawable.  `ContextCompat` must be used until the minimum API >= 21.
        Drawable defaultFavoriteIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.world);

        // Convert the default favorite icon drawable to a `BitmapDrawable`.
        BitmapDrawable defaultFavoriteIconBitmapDrawable = (BitmapDrawable) defaultFavoriteIconDrawable;

        // Remove the incorrect lint error that `getBitmap()` might be null.
        assert defaultFavoriteIconBitmapDrawable != null;

        // Extract a bitmap from the default favorite icon bitmap drawable.
        Bitmap defaultFavoriteIcon = defaultFavoriteIconBitmapDrawable.getBitmap();

        // Create a history array list.
        ArrayList<History> historyArrayList = new ArrayList<>();

        // Populate the history array list, descending from the end of the list so that the newest entries are at the top.  `-1` is needed because the history array list is zero-based.
        for (int i = webBackForwardList.getSize() - 1; i >= 0; i--) {
            // Create a variable to store the favorite icon bitmap.
            Bitmap favoriteIconBitmap;

            // Determine the favorite icon bitmap
            if (webBackForwardList.getItemAtIndex(i).getFavicon() == null) {
                // If the web back forward list does not have a favorite icon, use Clear Browser's default world icon.
                favoriteIconBitmap = defaultFavoriteIcon;
            } else {  // Use the icon from the web back forward list.
                favoriteIconBitmap = webBackForwardList.getItemAtIndex(i).getFavicon();
            }

            // Store the favorite icon and the URL in history entry.
            History historyEntry = new History(favoriteIconBitmap, webBackForwardList.getItemAtIndex(i).getUrl());

            // Add this history entry to the history array list.
            historyArrayList.add(historyEntry);
        }

        // Subtract the original current page ID from the array size because the order of the array is reversed so that the newest entries are at the top.  `-1` is needed because the array is zero-based.
        int currentPageId = webBackForwardList.getSize() - 1 - currentPageIndex;

        // Use an alert dialog builder to create the alert dialog.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), R.style.ClearBrowserAlertDialog);

        // Set the title.
        dialogBuilder.setTitle(R.string.history);

        // Set the view.  The parent view is `null` because it will be assigned by `AlertDialog`.
        dialogBuilder.setView(layoutInflater.inflate(R.layout.url_history_dialog, null));

        // Setup the clear history button.
        dialogBuilder.setNegativeButton(R.string.clear_history, (DialogInterface dialog, int which) -> {
            // Clear the history.
            nestedScrollWebView.clearHistory();
        });

        // Set an `onClick()` listener on the positive button.
        dialogBuilder.setPositiveButton(R.string.close, (DialogInterface dialog, int which) -> {
            // Do nothing if `Close` is clicked.  The `Dialog` will automatically close.
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

        //The alert dialog must be shown before the contents can be modified.
        alertDialog.show();

        // Instantiate a history array adapter.
        HistoryArrayAdapter historyArrayAdapter = new HistoryArrayAdapter(getContext(), historyArrayList, currentPageId);

        // Get a handle for the list view.
        ListView listView = alertDialog.findViewById(R.id.history_listview);

        // Remove the incorrect lint warning below that the view might be null.
        assert listView != null;

        // Set the list view adapter.
        listView.setAdapter(historyArrayAdapter);

        // Listen for clicks on entries in the list view.
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Convert the long ID to an int.
            int itemId = (int) id;

            // Only consume the click if it is not on the `currentPageId`.
            if (itemId != currentPageId) {
                // Get a handle for the URL text view.
                TextView urlTextView = view.findViewById(R.id.history_url_textview);

                // Get the URL.
                String url = urlTextView.getText().toString();

                // Invoke the navigate history listener in the calling activity.  These commands cannot be run here because they need access to `applyDomainSettings()`.
                navigateHistoryListener.navigateHistory(url, currentPageId - itemId);

                // Dismiss the alert dialog.
                alertDialog.dismiss();
            }
        });

        // Return the alert dialog.
        return alertDialog;
    }

    // The public interface is used to send information back to the parent activity.
    public interface NavigateHistoryListener {
        void navigateHistory(String url, int steps);
    }
}