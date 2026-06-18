// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.sample.printsample.Logger
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.R
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LoadCapabilitiesTask(context: MainActivity, fragment: PrintConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<PrintConfigureFragment> = WeakReference(fragment)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute() {
        var caps: PrintAttributesCaps? = null
        try {
            mContextRef.get()?.run {
                caps = requestCaps(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(caps)
    }

    private suspend fun onPostExecute(caps: PrintAttributesCaps?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (caps != null && result.code == Result.RESULT_OK) {
                    mFragment.get()?.loadCapabilities(caps)
                    Logger.showResult(this, getString(R.string.loaded_caps))
                } else if (mThrowable != null) {
                    Logger.showResult(this, "PrinterService.getCapabilities ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "PrinterService.getCapabilities", result)
                }
            }
        }
    }
}