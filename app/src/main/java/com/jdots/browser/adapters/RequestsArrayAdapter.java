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

package com.jdots.browser.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.helpers.BlocklistHelper;

import java.util.List;

import androidx.annotation.NonNull;

public class RequestsArrayAdapter extends ArrayAdapter<String[]> {
    public RequestsArrayAdapter(Context context, List<String[]> resourceRequestsList) {
        // `super` must be called form the base ArrayAdapter.  `0` is the `textViewResourceId`, which is unused.
        super(context, 0, resourceRequestsList);
    }

    @Override
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // Get a handle for the context.
        Context context = getContext();

        // Inflate the view if it is null.
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.requests_item_linearlayout, parent, false);
        }

        // Get handles for the views.
        LinearLayout linearLayout = view.findViewById(R.id.request_item_linearlayout);
        TextView dispositionTextView = view.findViewById(R.id.request_item_disposition);
        TextView urlTextView = view.findViewById(R.id.request_item_url);

        // Get the string array for this entry.
        String[] entryStringArray = getItem(position);

        // Remove the lint warning below that `entryStringArray` might be null.
        assert entryStringArray != null;

        // The ID is one greater than the position because it is 0 based.
        int id = position + 1;

        // Get the current theme status.
        int currentThemeStatus = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the action text and the background color.
        switch (entryStringArray[0]) {
            case BlocklistHelper.REQUEST_DEFAULT:
                // Create the disposition string.
                String requestDefault = id + ". " + context.getResources().getString(R.string.allowed);

                // Set the disposition text.
                dispositionTextView.setText(requestDefault);

                // Set the background color.
                linearLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                break;

            case BlocklistHelper.REQUEST_ALLOWED:
                // Create the disposition string.
                String requestAllowed = id + ". " + context.getResources().getString(R.string.allowed);

                // Set the disposition text.
                dispositionTextView.setText(requestAllowed);

                // Set the background color.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_100));
                } else {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.blue_700_50));
                }
                break;

            case BlocklistHelper.REQUEST_THIRD_PARTY:
                // Create the disposition string.
                String requestThirdParty = id + ". " + context.getResources().getString(R.string.blocked);

                // Set the disposition text.
                dispositionTextView.setText(requestThirdParty);

                // Set the background color.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow_100));
                } else {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow_700_50));
                }
                break;


            case BlocklistHelper.REQUEST_BLOCKED:
                // Create the disposition string.
                String requestBlocked = id + ". " + context.getResources().getString(R.string.blocked);

                // Set the disposition text.
                dispositionTextView.setText(requestBlocked);

                // Set the background color.
                if (currentThemeStatus == Configuration.UI_MODE_NIGHT_NO) {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.red_100));
                } else {
                    linearLayout.setBackgroundColor(context.getResources().getColor(R.color.red_700_40));
                }
                break;
        }

        // Set the URL text.
        urlTextView.setText(entryStringArray[1]);

        // Return the modified view.
        return view;
    }
}