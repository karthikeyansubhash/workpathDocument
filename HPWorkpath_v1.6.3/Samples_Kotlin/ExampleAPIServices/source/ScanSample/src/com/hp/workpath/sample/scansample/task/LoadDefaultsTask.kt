// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task

import android.util.Log

import com.hp.workpath.api.Result
import com.hp.workpath.api.scanner.ScanAttributes
import com.hp.workpath.api.scanner.ScannerService
import com.hp.workpath.sample.scansample.Logger
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment

import java.lang.ref.WeakReference

import com.hp.workpath.sample.scansample.MainActivity.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadDefaultsTask(context: MainActivity, fragment: ScanConfigureFragment) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mFragment: WeakReference<ScanConfigureFragment> = WeakReference(fragment)

    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var defaults: ScanAttributes? = null
        try {
            mContextRef.get()?.run {
                defaults = ScannerService.getDefaults(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(defaults)
    }

    private suspend fun onPostExecute(defaults: ScanAttributes?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (defaults != null && result.code == Result.RESULT_OK) {
                    Log.i(TAG, "Defaults=" + Logger.build(defaults))
                    Logger.showResult(this, getString(R.string.succeed))
                    mFragment.get()?.setDefaultScanAttributes(defaults)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "ScannerService.getDefaults ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "ScannerService.getDefaults", result)
                }
            }
        }
    }
}
