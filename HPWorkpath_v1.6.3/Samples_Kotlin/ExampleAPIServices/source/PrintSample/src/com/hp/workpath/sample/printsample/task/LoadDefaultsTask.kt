// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.sample.printsample.Logger
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LoadDefaultsTask(context: MainActivity, fragment: PrintConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<PrintConfigureFragment> = WeakReference(fragment)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute() {
        var defaults: PrintAttributes? = null
        try {
            mContextRef.get()?.run {
                defaults = PrinterService.getDefaults(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(defaults)
    }

    private suspend fun onPostExecute(defaults: PrintAttributes?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (defaults != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, "Defaults=" + Logger.build(defaults));
                    mFragment.get()?.setDefaultPrintAttributes(defaults)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "PrinterService.getDefaults ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "PrinterService.getDefaults", result)
                }
            }
        }
    }
}