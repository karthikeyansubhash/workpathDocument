// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.helper.email.Email
import com.hp.workpath.api.helper.email.EmailAttributes
import com.hp.workpath.sample.emailsample.Logger
import com.hp.workpath.sample.emailsample.Logger.build
import com.hp.workpath.sample.emailsample.MainActivity
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference

class LoadEmailDefaultsTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mResult: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var defaults: EmailAttributes? = null
        try {
            mContextRef.get()?.run {
                defaults = Email.getDefaults(this, mResult)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(defaults)
    }

    private suspend fun onPostExecute(defaults: EmailAttributes?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                if (defaults != null && mResult.code == Result.RESULT_OK) {
                    Logger.showResult(this, "EmailAttributes=" + build(defaults))
                    setDefaultEmailAttributes(defaults)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "Email.getDefaults ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "Email.getDefaults", mResult)
                }
            }
        }
    }

}