/*
 *   2020
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

package com.jdots.browser.viewmodels

import android.text.SpannableStringBuilder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.jdots.browser.backgroundtasks.GetSourceBackgroundTask

import java.net.Proxy
import java.util.concurrent.ExecutorService

class WebViewSource(private val urlString: String, private val userAgent: String, private val doNotTrack: Boolean, private val localeString: String, private val proxy: Proxy,
                    private val executorService: ExecutorService) : ViewModel() {
    // Initialize the mutable live data variables.
    private val mutableLiveDataSourceStringArray = MutableLiveData<Array<SpannableStringBuilder>>()
    private val mutableLiveDataErrorString = MutableLiveData<String>()

    // Initialize the view model.
    init {
        // Instantiate the get source background task class.
        val getSourceBackgroundTask = GetSourceBackgroundTask()

        // Get the source.
        executorService.execute { mutableLiveDataSourceStringArray.postValue(getSourceBackgroundTask.acquire(urlString, userAgent, doNotTrack, localeString, proxy, this)) }
    }

    // The source observer.
    fun observeSource(): LiveData<Array<SpannableStringBuilder>> {
        // Return the source to the activity.
        return mutableLiveDataSourceStringArray
    }

    // The error observer.
    fun observeErrors(): LiveData<String> {
        // Return any errors to the activity.
        return mutableLiveDataErrorString
    }

    // The interface for returning the error from the background task
    fun returnError(errorString: String) {
        // Update the mutable live data error string.
        mutableLiveDataErrorString.postValue(errorString)
    }

    // The workhorse that gets the source.
    fun updateSource(urlString: String) {
        // Reset the mutable live data error string.  This prevents the snackbar from displaying later if the activity restarts.
        mutableLiveDataErrorString.postValue("")

        // Instantiate the get source background task class.
        val getSourceBackgroundTask = GetSourceBackgroundTask()

        // Get the source.
        executorService.execute { mutableLiveDataSourceStringArray.postValue(getSourceBackgroundTask.acquire(urlString, userAgent, doNotTrack, localeString, proxy, this)) }
    }
}