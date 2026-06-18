// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.scanner.ScanAttributesCaps
import com.hp.workpath.sample.scansample.Logger
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LoadCapabilitiesTask(context: MainActivity, fragment: ScanConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<ScanConfigureFragment> = WeakReference(fragment)

    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var caps: ScanAttributesCaps? = null
        try {
            mContextRef.get()?.run {
                caps = requestCaps(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(caps)
    }

    private suspend fun onPostExecute(caps: ScanAttributesCaps?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (caps != null && result.code == Result.RESULT_OK) {
                    mFragment.get()?.loadCapabilities(caps)
                    Logger.showResult(this, getString(R.string.loaded_caps))
                } else if (mThrowable != null) {
                    Logger.showResult(this, "ScannerService.getCapabilities ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "ScannerService.getCapabilities", result)
                }
            }
        }
    }
}
