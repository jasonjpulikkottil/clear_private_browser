/*
 *   2016-2019
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
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdots.browser.R;
import com.jdots.browser.definitions.History;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class HistoryArrayAdapter extends ArrayAdapter<History> {

    // `currentPage` is used in `HistoryArrayAdapter` and `getView()`.
    private final int currentPage;

    public HistoryArrayAdapter(Context context, ArrayList<History> historyArrayList, int currentPageId) {
        // `super` must be called from the base `ArrayAdapter`.  `0` is the `textViewResourceId`, which is unused.
        super(context, 0, historyArrayList);

        // Store `currentPageId` in the class variable.
        currentPage = currentPageId;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Inflate the view if it is null.
        if (convertView == null) {
            // `false` does not attach `url_history_item_linearlayout` to `parent`.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.url_history_item_linearlayout, parent, false);
        }

        // Get handles for `favoriteIconImageView` and `urlTextView`.
        ImageView favoriteIconImageView = convertView.findViewById(R.id.history_favorite_icon_imageview);
        TextView urlTextView = convertView.findViewById(R.id.history_url_textview);

        // Get the URL history for this position.
        History history = getItem(position);

        // Remove the lint warning below that `history` might be `null`.
        assert history != null;

        // Set `favoriteIconImageView` and `urlTextView`.
        favoriteIconImageView.setImageBitmap(history.entryFavoriteIcon);
        urlTextView.setText(history.entryUrl);

        // Set the URL text for `currentPage` to be bold.
        if (position == currentPage) {
            urlTextView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {  // Set the default typeface for all the other entries.
            urlTextView.setTypeface(Typeface.DEFAULT);
        }

        // Return the modified `convertView`.
        return convertView;
    }
}