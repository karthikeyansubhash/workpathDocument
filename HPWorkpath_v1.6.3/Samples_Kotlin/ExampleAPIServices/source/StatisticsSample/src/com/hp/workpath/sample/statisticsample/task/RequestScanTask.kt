// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task

import android.text.TextUtils
import android.util.Log
import com.hp.workpath.api.scanner.ScanAttributes
import com.hp.workpath.api.scanner.ScannerService
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.MainActivity
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.fragment.TestJobFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class RequestScanTask(context: TestJobFragment, private val mScanAttributes: ScanAttributes) {
    private val mContextRef: WeakReference<TestJobFragment> = WeakReference(context)
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var rid: String? = null
        try {
            mContextRef.get()?.run {
                rid = ScannerService.submit(requireContext(), mScanAttributes, null)
                Log.i(TAG, "Job submitted with rid = $rid")
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(rid)
    }

    private suspend fun onPostExecute(rid: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (!TextUtils.isEmpty(rid)) {
                    setRid(rid)
                } else if (mThrowable != null) {
                    Logger.showResult(
                        activity,
                        getString(R.string.job_request_failed) + " " + mThrowable?.message
                    )
                } else {
                    Logger.showResult(activity, getString(R.string.job_request_failed))
                }
            }
        }
    }

    companion object {
        private const val TAG = MainActivity.TAG
    }
}