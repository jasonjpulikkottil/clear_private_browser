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

package com.jdots.browser.backgroundtasks;

import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.webkit.CookieManager;

import com.jdots.browser.viewmodels.WebViewSource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class GetSourceBackgroundTask {
    public SpannableStringBuilder[] acquire(String urlString, String userAgent, boolean doNotTrack, String localeString, Proxy proxy, WebViewSource webViewSource) {
        // Initialize the spannable string builders.
        SpannableStringBuilder requestHeadersBuilder = new SpannableStringBuilder();
        SpannableStringBuilder responseMessageBuilder = new SpannableStringBuilder();
        SpannableStringBuilder responseHeadersBuilder = new SpannableStringBuilder();
        SpannableStringBuilder responseBodyBuilder = new SpannableStringBuilder();

        // Because everything relating to requesting data from a webserver can throw errors, the entire section must catch `IOExceptions`.
        try {
            // Get the current URL from the main activity.
            URL url = new URL(urlString);

            // Open a connection to the URL.  No data is actually sent at this point.
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection(proxy);

            // Define the variables necessary to build the request headers.
            requestHeadersBuilder = new SpannableStringBuilder();
            int oldRequestHeadersBuilderLength;
            int newRequestHeadersBuilderLength;


            // Set the `Host` header property.
            httpUrlConnection.setRequestProperty("Host", url.getHost());

            // Add the `Host` header to the string builder and format the text.
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Host", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Host");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ");
            requestHeadersBuilder.append(url.getHost());


            // Set the `Connection` header property.
            httpUrlConnection.setRequestProperty("Connection", "keep-alive");

            // Add the `Connection` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Connection", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Connection");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  keep-alive");


            // Set the `Upgrade-Insecure-Requests` header property.
            httpUrlConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");

            // Add the `Upgrade-Insecure-Requests` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Upgrade-Insecure-Requests", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Upgrade-Insecure_Requests");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  1");


            // Set the `User-Agent` header property.
            httpUrlConnection.setRequestProperty("User-Agent", userAgent);

            // Add the `User-Agent` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("User-Agent", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("User-Agent");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ");
            requestHeadersBuilder.append(userAgent);


            // Set the `x-requested-with` header property.
            httpUrlConnection.setRequestProperty("x-requested-with", "");

            // Add the `x-requested-with` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("x-requested-with", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("x-requested-with");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ");


            // Set the `Sec-Fetch-Site` header property.
            httpUrlConnection.setRequestProperty("Sec-Fetch-Site", "none");

            // Add the `Sec-Fetch-Site` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Sec-Fetch-Site", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Sec-Fetch-Site");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  none");


            // Set the `Sec-Fetch-Mode` header property.
            httpUrlConnection.setRequestProperty("Sec-Fetch-Mode", "navigate");

            // Add the `Sec-Fetch-Mode` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Sec-Fetch-Mode", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Sec-Fetch-Mode");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  navigate");


            // Set the `Sec-Fetch-User` header property.
            httpUrlConnection.setRequestProperty("Sec-Fetch-User", "?1");

            // Add the `Sec-Fetch-User` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Sec-Fetch-User", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Sec-Fetch-User");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ?1");


            // Only populate `Do Not Track` if it is enabled.
            if (doNotTrack) {
                // Set the `dnt` header property.
                httpUrlConnection.setRequestProperty("dnt", "1");

                // Add the `dnt` header to the string builder and format the text.
                requestHeadersBuilder.append(System.getProperty("line.separator"));
                if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                    requestHeadersBuilder.append("dnt", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {  // Older versions not so much.
                    oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                    requestHeadersBuilder.append("dnt");
                    newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                    requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                requestHeadersBuilder.append(":  1");
            }


            // Set the `Accept` header property.
            httpUrlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");

            // Add the `Accept` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Accept", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Accept");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ");
            requestHeadersBuilder.append("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");


            // Set the `Accept-Language` header property.
            httpUrlConnection.setRequestProperty("Accept-Language", localeString);

            // Add the `Accept-Language` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Accept-Language", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Accept-Language");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  ");
            requestHeadersBuilder.append(localeString);


            // Get the cookies for the current domain.
            String cookiesString = CookieManager.getInstance().getCookie(url.toString());

            // Only process the cookies if they are not null.
            if (cookiesString != null) {
                // Add the cookies to the header property.
                httpUrlConnection.setRequestProperty("Cookie", cookiesString);

                // Add the cookie header to the string builder and format the text.
                requestHeadersBuilder.append(System.getProperty("line.separator"));
                if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                    requestHeadersBuilder.append("Cookie", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {  // Older versions not so much.
                    oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                    requestHeadersBuilder.append("Cookie");
                    newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                    requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                requestHeadersBuilder.append(":  ");
                requestHeadersBuilder.append(cookiesString);
            }


            // `HttpUrlConnection` sets `Accept-Encoding` to be `gzip` by default.  If the property is manually set, than `HttpUrlConnection` does not process the decoding.
            // Add the `Accept-Encoding` header to the string builder and format the text.
            requestHeadersBuilder.append(System.getProperty("line.separator"));
            if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                requestHeadersBuilder.append("Accept-Encoding", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {  // Older versions not so much.
                oldRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.append("Accept-Encoding");
                newRequestHeadersBuilderLength = requestHeadersBuilder.length();
                requestHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldRequestHeadersBuilderLength, newRequestHeadersBuilderLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            requestHeadersBuilder.append(":  gzip");


            // The actual network request is in a `try` bracket so that `disconnect()` is run in the `finally` section even if an error is encountered in the main block.
            try {
                // Initialize the string builders.
                responseMessageBuilder = new SpannableStringBuilder();
                responseHeadersBuilder = new SpannableStringBuilder();

                // Get the response code, which causes the connection to the server to be made.
                int responseCode = httpUrlConnection.getResponseCode();

                // Populate the response message string builder.
                if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                    responseMessageBuilder.append(String.valueOf(responseCode), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {  // Older versions not so much.
                    responseMessageBuilder.append(String.valueOf(responseCode));
                    int newLength = responseMessageBuilder.length();
                    responseMessageBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, newLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                responseMessageBuilder.append(":  ");
                responseMessageBuilder.append(httpUrlConnection.getResponseMessage());

                // Initialize the iteration variable.
                int i = 0;

                // Iterate through the received header fields.
                while (httpUrlConnection.getHeaderField(i) != null) {
                    // Add a new line if there is already information in the string builder.
                    if (i > 0) {
                        responseHeadersBuilder.append(System.getProperty("line.separator"));
                    }

                    // Add the header to the string builder and format the text.
                    if (Build.VERSION.SDK_INT >= 21) {  // Newer versions of Android are so smart.
                        responseHeadersBuilder.append(httpUrlConnection.getHeaderFieldKey(i), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {  // Older versions not so much.
                        int oldLength = responseHeadersBuilder.length();
                        responseHeadersBuilder.append(httpUrlConnection.getHeaderFieldKey(i));
                        int newLength = responseHeadersBuilder.length();
                        responseHeadersBuilder.setSpan(new StyleSpan(Typeface.BOLD), oldLength, newLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    responseHeadersBuilder.append(":  ");
                    responseHeadersBuilder.append(httpUrlConnection.getHeaderField(i));

                    // Increment the iteration variable.
                    i++;
                }

                // Instantiate an input stream for the response body.
                InputStream inputStream;

                // Get the correct input stream based on the response code.
                if (responseCode == 404) {  // Get the error stream.
                    inputStream = new BufferedInputStream(httpUrlConnection.getErrorStream());
                } else {  // Get the response body stream.
                    inputStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                }

                // Initialize the byte array output stream and the conversion buffer byte array.
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] conversionBufferByteArray = new byte[1024];

                // Define the buffer length variable.
                int bufferLength;

                try {
                    // Attempt to read data from the input stream and store it in the conversion buffer byte array.  Also store the amount of data read in the buffer length variable.
                    while ((bufferLength = inputStream.read(conversionBufferByteArray)) > 0) {  // Proceed while the amount of data stored in the buffer is > 0.
                        // Write the contents of the conversion buffer to the byte array output stream.
                        byteArrayOutputStream.write(conversionBufferByteArray, 0, bufferLength);
                    }
                } catch (IOException exception) {
                    // Return the error message.
                    webViewSource.returnError(exception.toString());
                }

                // Close the input stream.
                inputStream.close();

                // Populate the response body string with the contents of the byte array output stream.
                responseBodyBuilder.append(byteArrayOutputStream.toString());
            } finally {
                // Disconnect HTTP URL connection.
                httpUrlConnection.disconnect();
            }
        } catch (Exception exception) {
            // Return the error message.
            webViewSource.returnError(exception.toString());
        }

        // Return the response body string as the result.
        return new SpannableStringBuilder[]{requestHeadersBuilder, responseMessageBuilder, responseHeadersBuilder, responseBodyBuilder};
    }
}