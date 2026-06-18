// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import android.view.View
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.sample.massstoragesample.Logger
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R
import java.lang.ref.WeakReference

class DeleteFileTask(context: MainActivity, fileList: List<CustomerDataFile>, baseCustomerDataFile: CustomerDataFile){

    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFilesToBeDeleted: List<CustomerDataFile> = fileList
    private val mBaseCustomerDataFile: CustomerDataFile = baseCustomerDataFile
    private var mThrowable: Throwable? = null
    private var failCount = 0
    private var successCount = 0

    suspend fun execute() {
        var result = false
        try {
            for (customerDataFile in mFilesToBeDeleted) {
                val isDeleted = customerDataFile.delete()
                if (isDeleted) {
                    ++successCount
                } else {
                    ++failCount
                }
            }
            result = true
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(result)
    }

    private suspend fun onPostExecute(result: Boolean) {
        mContextRef.get()?.run {
            runOnUiThread {
                enableButton(true)
                showProgress(View.GONE)
                Logger.showResult(this, getString(R.string.sucess_and_fail, successCount, failCount))
                if (result) {
                    displayFileList(mBaseCustomerDataFile)
                } else {
                    if (mThrowable != null) {
                        Logger.showResult(mContextRef.get(), "CustomerDataFile.delete ${mThrowable?.message}")
                    }
                }
            }
        }
    }
}