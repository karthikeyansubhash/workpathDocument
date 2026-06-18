// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task

import android.content.Context
import android.util.Log
import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.api.scanner.ScanAttributesCaps
import com.hp.workpath.api.scanner.ScannerService
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.MainActivity
import com.hp.workpath.sample.statisticsample.MainActivity.Companion.TAG
import com.hp.workpath.sample.statisticsample.view.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LoadCapabilitiesTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val mResult: Result = Result()

    suspend fun execute() {
        var printCaps: PrintAttributesCaps? = null
        var scanCaps: ScanAttributesCaps? = null
        try {
            mContextRef.get()?.run {
                Utils.copyAssets(this)
                if (ScannerService.isSupported(this)) {
                    Log.i(TAG, "ScannerService is supported")
                    scanCaps = requestScanCaps(this, mResult)
                }

                if (PrinterService.isSupported(this)) {
                    Log.i(TAG, "PrinterService is supported")
                    printCaps = requestPrintCaps(this, mResult)
                }
                onPostExecute(printCaps, scanCaps)
            }
        } catch (t: Throwable) {
            mThrowable = t
            onPostExecute(printCaps, scanCaps)
        }
    }

    private suspend fun onPostExecute(
        printCaps: PrintAttributesCaps?,
        scanCaps: ScanAttributesCaps?
    ) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                Logger.showResult(this, "getCapabilities", mResult)
                loadCapabilities(scanCaps, printCaps)
            }
        }
    }

    private fun requestPrintCaps(context: Context, result: Result): PrintAttributesCaps {
        return PrinterService.getCapabilities(context, result)
    }

    private fun requestScanCaps(context: Context, result: Result): ScanAttributesCaps {
        return ScannerService.getCapabilities(context, result)
    }
}