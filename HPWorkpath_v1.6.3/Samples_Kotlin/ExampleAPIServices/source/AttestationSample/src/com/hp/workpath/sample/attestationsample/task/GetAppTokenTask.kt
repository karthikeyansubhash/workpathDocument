// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.attestationsample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.attestation.AppToken
import com.hp.workpath.api.attestation.AttestationService
import com.hp.workpath.sample.attestationsample.Logger
import com.hp.workpath.sample.attestationsample.MainActivity
import com.hp.workpath.sample.attestationsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class GetAppTokenTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var appToken: AppToken? = null
        try {
            mContextRef.get()?.run {
                appToken = AttestationService.getAppToken(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(appToken)
    }


    private suspend fun onPostExecute(appToken: AppToken?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                if (mThrowable != null) {
                    handleException(mThrowable)
                } else if (appToken != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, getString(R.string.success))
                    getAppTokenComplete(appToken, result)
                } else {
                    Logger.showResult(this,"AttestationService.getAppToken", result)
                    getAppTokenComplete(appToken, result)
                }
            }
        }
    }
}