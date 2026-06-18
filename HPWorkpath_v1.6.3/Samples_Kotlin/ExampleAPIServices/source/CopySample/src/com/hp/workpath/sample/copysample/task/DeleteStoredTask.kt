// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.copier.CopierService
import com.hp.workpath.api.copier.JobCredentialsAttributes
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class DeleteStoredTask(context: MainActivity, jobCredentials: JobCredentialsAttributes) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val jobCredentials: JobCredentialsAttributes = jobCredentials
    private lateinit var mErrorMsg: String
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute(vararg params: String) {
        try {
            val storedJobId = params[0]
            mContextRef.get()?.run {
                CopierService.deleteStoredJob(this, storedJobId, jobCredentials, result)
            }
        } catch (iae: IllegalArgumentException) {
            mErrorMsg = "IllegalArgumentException"
            mThrowable = iae
        } catch (t: Throwable) {
            mErrorMsg = "Unknown exception";
            mThrowable = t;
        }
        onPostExecute()
    }

    private suspend fun onPostExecute() {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (::mErrorMsg.isInitialized) {
                    Logger.showResult(this, "$mErrorMsg, ${mThrowable?.message}")
                } else if (result.code != Result.RESULT_OK) {
                    Logger.showResult(this, "CopierService.deleteStoredJob", result)
                } else {
                    Logger.showResult(this, this.getString(R.string.job_deleted))
                }
            }
        }
    }
}