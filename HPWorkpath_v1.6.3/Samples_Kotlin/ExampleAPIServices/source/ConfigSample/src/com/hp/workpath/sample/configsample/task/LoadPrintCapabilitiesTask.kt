// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample.task

import android.util.Log
import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.sample.configsample.Logger.showResult
import com.hp.workpath.sample.configsample.MainActivity
import com.hp.workpath.sample.configsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class LoadPrintCapabilitiesTask(context: MainActivity) {

    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute() {
        var caps: PrintAttributesCaps? = null
        try {
            mContextRef.get()?.run {
                if (!PrinterService.isSupported(this)) {
                    Log.d(MainActivity.TAG, getString(R.string.print_service_not_supported));
                } else {
                    caps = PrinterService.getCapabilities(this, result)
                }
            }
        } catch (t: Throwable) {
            mThrowable = t;
        }
        onPostExecute(caps)
    }

    private suspend fun onPostExecute(caps: PrintAttributesCaps?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                if (caps != null && result.code == Result.RESULT_OK) {
                    setPrintCapabilities(caps)
                    showResult(this, "Success")
                } else if (mThrowable != null) {
                    showResult(this, "PrinterService.getDefaults " + mThrowable?.message)
                } else {
                    showResult(this, "PrinterService.getDefaults", result)
                }
            }
        }
    }
}