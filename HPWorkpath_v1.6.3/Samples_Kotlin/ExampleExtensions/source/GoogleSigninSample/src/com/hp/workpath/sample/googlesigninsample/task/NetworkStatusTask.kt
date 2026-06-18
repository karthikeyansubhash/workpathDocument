// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hp.workpath.sample.googlesigninsample.MainActivity
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class NetworkStatusTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)

    suspend fun execute() {
        var result = false
        mContextRef.get()?.run {
            if (!isNetworkAvailable(this)) result = false
            if (!checkInternetConnection()) result = false else result = true
            this.setNetworkAvailable(result)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun checkInternetConnection(): Boolean {
        try {
            val url = URL("https://www.google.com")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connectTimeout = TIMEOUT
            httpURLConnection.readTimeout = TIMEOUT
            httpURLConnection.connect()
            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                return true
            }
        } catch (ignore: Exception) {
        }
        return false
    }

    companion object {
        private const val TIMEOUT = 3000
    }

}