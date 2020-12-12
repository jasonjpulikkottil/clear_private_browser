/*
 *   2016-2017
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

package com.jdots.browser.definitions;

import android.graphics.Bitmap;

// Create a `History` object.
public class History {
    // Create the `History` package-local variables.
    public final Bitmap entryFavoriteIcon;
    public final String entryUrl;

    public History(Bitmap entryFavoriteIcon, String entryUrl) {
        // Populate the package-local variables.
        this.entryFavoriteIcon = entryFavoriteIcon;
        this.entryUrl = entryUrl;
    }
}