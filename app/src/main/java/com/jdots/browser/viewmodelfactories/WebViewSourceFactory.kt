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

package com.jdots.browser.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import java.net.Proxy
import java.util.concurrent.ExecutorService

class WebViewSourceFactory(private val urlString: String, private val userAgent: String, private val doNotTrack: Boolean, private val localeString: String, private val proxy: Proxy,
                           private val executorService: ExecutorService) : ViewModelProvider.Factory {
    // Override the create function in order to add the provided arguments.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // Return a new instance of the model class with the provided arguments.
        return modelClass.getConstructor(String::class.java, String::class.java, Boolean::class.java, String::class.java, Proxy::class.java, ExecutorService::class.java)
                .newInstance(urlString, userAgent, doNotTrack, localeString, proxy, executorService)
    }
}