// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import android.util.Log
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.hp.workpath.sample.googlesigninsample.MainActivity
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo
import java.io.IOException

class RefreshTokenTask(private val mClientInfo: ClientInfo, private val mRefreshToken: String, private val mTaskInterface: TaskInterface) {
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var accessToken: String? = null
        try {
            val response: TokenResponse = GoogleRefreshTokenRequest(NetHttpTransport(), JacksonFactory(),
                    mRefreshToken, mClientInfo.clientId, mClientInfo.clientSecret).execute()
            Log.i(TAG, "Access token: " + response.accessToken)
            accessToken = response.accessToken
        } catch (e: IOException) {
            mThrowable = e
        }
        onPostExecute(accessToken)
    }

    private suspend fun onPostExecute(accessToken: String?) {
        accessToken?.let {
            mTaskInterface.refreshedToken(it)
        } ?: run {
            mTaskInterface.onFailure(mThrowable)
        }
    }

    companion object {
        private val TAG: String = MainActivity.TAG
    }
}