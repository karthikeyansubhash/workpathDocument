// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.CustomerDataFileUtils
import com.hp.workpath.sample.massstoragesample.Logger
import com.hp.workpath.sample.massstoragesample.MainActivity
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

class CreateFileTask(context: MainActivity, customerDataFile: CustomerDataFile, content: String) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mCustomerDataFile: CustomerDataFile = customerDataFile
    private val mContent: String = content
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var isFileCreated = false
        try {
            mCustomerDataFile.createNewFile()

            var fos: OutputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                fos = CustomerDataFileUtils.openOutputStream(mContextRef.get(), mCustomerDataFile)
                bos = BufferedOutputStream(fos)
                bos.write(mContent.toByteArray())
                bos.flush()
                isFileCreated = true
            } catch (t: Throwable) {
                mThrowable = t
            } finally {
                if (bos != null) {
                    try {
                        bos.close()
                    } catch (e: IOException) { // ignore
                    }
                }
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) { // ignore
                    }
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(isFileCreated)
    }

    private suspend fun onPostExecute(result: Boolean) {
        mContextRef.get()?.run {
            runOnUiThread {
                enableButton(true);
                if (result) {
                    Logger.showResult(this, "File created")
                    displayFileList(mCustomerDataFile.parentFile)
                } else {
                    Logger.showResult(this, "File could not be created. ${mThrowable?.message}")
                }
            }
        }
    }
}