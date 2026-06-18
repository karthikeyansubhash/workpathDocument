// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import android.content.Context
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.ref.WeakReference

class DriveFileListTask(context: Context, clientSecret: String, taskInterface: DriveTaskInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mCredential: String = clientSecret
    private val mTaskInterface: DriveTaskInterface = taskInterface
    private var mThrowable: Throwable? = null

    interface DriveTaskInterface {
        fun onFailure(t: Throwable?)
        fun onResponse(files: List<File>?)
    }

    suspend fun execute() {
        var files: List<File>? = null
        try {
            val service = getDriveService(mContextRef.get(), mCredential)
            val result = service.files().list().execute()
            files = result.files
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(files)
    }

    private suspend fun onPostExecute(files: List<File>?) {
        withContext(Dispatchers.Main) {
            if (mThrowable != null) {
                mTaskInterface.onFailure(mThrowable)
            } else {
                mTaskInterface.onResponse(files)
            }
        }
    }

    /**
     * Build and return an authorized Drive client service.
     *
     * @return an authorized Drive client service
     * @throws IOException
     */
    @Throws(Throwable::class)
    private fun getDriveService(context: Context?, credentialJson: String): Drive {
        val credential: Credential = authorize(credentialJson)
        val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
        val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
        return Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, null)
                .setHttpRequestInitializer(credential)
                .setApplicationName(context!!.applicationInfo.name)
                .build()
    }

    @Throws(IOException::class)
    private fun authorize(credentialJson: String): GoogleCredential {
        val gson = Gson()
        val accountInfo = gson.fromJson(credentialJson, AccountInfo::class.java)
        val credential = GoogleCredential()
        credential.accessToken = accountInfo.accessToken
        return credential
    }

}