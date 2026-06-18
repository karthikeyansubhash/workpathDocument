// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import android.view.View
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.sample.massstoragesample.Logger
import com.hp.workpath.sample.massstoragesample.MainActivity
import java.lang.ref.WeakReference

class CreateNewFileTask(context: MainActivity, file: CustomerDataFile) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFile: CustomerDataFile = file
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var isFileCreated = false
        try {
            isFileCreated = mFile.createNewFile()
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(isFileCreated)
    }

    private suspend fun onPostExecute(result: Boolean) {
        mContextRef.get()?.run {
            runOnUiThread {
                enableButton(true)
                showProgress(View.GONE)
                Logger.showResult(mContextRef.get(), "CustomerDataFile.createNewFile $result")
                if (result) {
                    displayFileList(mFile.parentFile)
                } else {
                    if (mThrowable != null) {
                        Logger.showResult(mContextRef.get(), "CustomerDataFile.createNewFile ${mThrowable?.message}")
                    }
                }
            }
        }
    }
}