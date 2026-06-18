// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import com.google.gson.Gson
import com.hp.workpath.sample.googlesigninsample.MainActivity
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class RequestTokenTask(private val mClientInfo: ClientInfo, private val mCode: String, private val mTaskInterface: TaskInterface) {
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var accountInfo: AccountInfo? = null
        val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("code", mCode)
                .addFormDataPart("client_id", mClientInfo.clientId)
                .addFormDataPart("redirect_uri", mClientInfo.redirectUri)
                .addFormDataPart("grant_type", mClientInfo.grantType)
                .addFormDataPart("code_verifier", mClientInfo.codeVerifier)
                .addFormDataPart("client_secret", mClientInfo.clientSecret)
                .build()
        val request = Request.Builder()
                .url(mClientInfo.tokenUri)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", RequestBody.create(null, ByteArray(0)))
                .post(requestBody)
                .build()
        try {
            val response = client.newCall(request).execute()
            if (response != null) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!.string()
                    val gson = Gson()
                    accountInfo = gson.fromJson(responseBody, AccountInfo::class.java)
                } else {
                    Throwable(response.body()?.string())
                }
            } else {
                Throwable("Request token response is null")
            }
        } catch (e: IOException) {
            mThrowable = e
        }
        onPostExecute(accountInfo)
    }

    private suspend fun onPostExecute(accountInfo: AccountInfo?) {
        if (accountInfo == null) {
            mTaskInterface.onFailure(mThrowable)
        } else {
            mTaskInterface.receivedToken(accountInfo)
        }
    }

    companion object {
        private val TAG: String = MainActivity.TAG
    }
}