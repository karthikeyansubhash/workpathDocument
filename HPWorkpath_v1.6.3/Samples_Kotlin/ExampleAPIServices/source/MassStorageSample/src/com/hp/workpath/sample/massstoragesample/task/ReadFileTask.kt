// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import android.content.Context
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.CustomerDataFileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

class ReadFileTask(context: Context, customerDataFile: CustomerDataFile, private val readFileTaskInterface: ReadFileTaskInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mCustomerDataFile: CustomerDataFile = customerDataFile
    private var mThrowable: Throwable? = null

    interface ReadFileTaskInterface {
        fun fileContent(content: String?)
    }

    suspend fun execute() {
        var content = ""
        var fis: InputStream? = null
        var bis: BufferedInputStream? = null
        val fileLength = mCustomerDataFile.length()
        try {
            var bufferSize = 64
            if (fileLength < bufferSize) {
                bufferSize = fileLength.toInt()
            }
            val buffer = ByteArray(bufferSize)
            fis = CustomerDataFileUtils.openInputStream(mContextRef.get(), mCustomerDataFile)
            bis = BufferedInputStream(fis)
            bis.read(buffer)
            content = String(buffer)
        } catch (t: Throwable) {
            mThrowable = t
        } finally {
            if (bis != null) {
                try {
                    bis.close()
                } catch (e: IOException) {
                }
            }
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                }
            }
        }
        onPostExecute(content)
    }

    private suspend fun onPostExecute(content: String) {
        withContext(Dispatchers.Main) {
            if (mThrowable != null) {
                readFileTaskInterface.fileContent("CustomerDataFileUtils.openInputStream ${mThrowable?.message}")
            } else {
                readFileTaskInterface.fileContent(content)
            }
        }
    }
}