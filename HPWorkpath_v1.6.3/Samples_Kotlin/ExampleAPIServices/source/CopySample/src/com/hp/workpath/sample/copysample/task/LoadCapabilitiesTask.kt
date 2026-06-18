// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.copier.CopyAttributesCaps
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Async task to retrieve copy capabilities from printer.
 */
class LoadCapabilitiesTask(context: MainActivity, fragment: CopyConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<CopyConfigureFragment> = WeakReference(fragment)
    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var caps: CopyAttributesCaps? = null
        try {
            mContextRef.get()?.run {
                caps = requestCaps(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(caps)
    }

    private suspend fun onPostExecute(caps: CopyAttributesCaps?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (caps != null && result.code == Result.RESULT_OK) {
                    mFragment.get()?.loadCapabilities(caps)
                    Logger.showResult(this, getString(R.string.loaded_caps))
                } else if (mThrowable != null) {
                    Logger.showResult(this, "CopierService.getCapabilities ${mThrowable?.message}");
                } else {
                    Logger.showResult(this, "CopierService.getCapabilities", result);
                }
            }
        }
    }
}