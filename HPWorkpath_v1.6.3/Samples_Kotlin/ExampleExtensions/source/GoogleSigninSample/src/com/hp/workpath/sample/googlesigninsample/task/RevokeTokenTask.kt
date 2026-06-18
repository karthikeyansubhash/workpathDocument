// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import android.util.Log
import android.webkit.CookieManager
import com.hp.workpath.sample.googlesigninsample.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class RevokeTokenTask(private val mAccessToken: String, private val mTaskInterface: TaskInterface) {
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var result = false
        try {
            val client = OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            Log.i(TAG, "Revoke accessToken: $mAccessToken")
            val request = Request.Builder()
                    .url("https://accounts.google.com/o/oauth2/revoke?token=$mAccessToken")
                    .method("POST", RequestBody.create(null, ByteArray(0)))
                    .build()
            val response = client.newCall(request).execute()
            if (response != null) {
                if (response.isSuccessful) {
                    result = true
                } else {
                    Throwable(response.body()?.string())
                }
            } else {
                Throwable("Revoke token response is null")
            }
        } catch (e: IOException) {
            mThrowable = e
        }
        onPostExecute(result)
    }

    private suspend fun onPostExecute(result: Boolean) {
        if (!result) {
            mTaskInterface.onFailure(mThrowable)
        } else {
            mTaskInterface.revokedToken()
        }
        CookieManager.getInstance().removeAllCookies(null)
    }

    companion object {
        private val TAG: String = MainActivity.TAG
    }
}