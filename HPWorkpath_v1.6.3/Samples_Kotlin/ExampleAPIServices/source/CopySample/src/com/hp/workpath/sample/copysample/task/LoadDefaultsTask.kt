// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.copier.CopierService
import com.hp.workpath.api.copier.CopyAttributes
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.Logger.build
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * Async task to request device defaults for Copy
 */
class LoadDefaultsTask(context: MainActivity, fragment: CopyConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<CopyConfigureFragment> = WeakReference(fragment)
    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var defaults: CopyAttributes? = null
        try {
            mContextRef.get()?.run {
                defaults = CopierService.getDefaults(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(defaults)
    }

    private suspend fun onPostExecute(defaults: CopyAttributes?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (defaults != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, this.getString(R.string.succeed) + build(defaults))
                    mFragment.get()?.setDefaultCopyAttributes(defaults)
                } else if (mThrowable != null) {
                    Logger.showResult(this,"CopierService.getDefaults ${mThrowable?.message}")
                } else {
                    Logger.showResult(this,"CopierService.getDefaults", result)
                }
            }
        }
    }
}