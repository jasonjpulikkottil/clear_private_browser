/*
 *   2019
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

package com.jdots.browser.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NoSwipeViewPager extends ViewPager {
    // The basic constructor
    public NoSwipeViewPager(@NonNull Context context) {
        // Roll up to the full constructor.
        this(context, null);
    }

    // The full constructor.
    public NoSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        // Run the default commands.
        super(context, attributeSet);
    }

    // It is necessary to override `performClick()` when overriding `onTouchEvent()`
    @Override
    public boolean performClick() {
        // Run the default commands.
        super.performClick();

        // Do not consume the events.
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // `onTouchEvent()` requires calling `performClick()`.
        performClick();

        // Do not allow swiping.
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Do not allow swiping.
        return false;
    }
}