// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.copier.CopierService
import com.hp.workpath.api.copier.StoredJobInfo
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.Logger.build
import com.hp.workpath.sample.copysample.fragments.StoreJobFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class EnumerateStoredJobTask(fragment: StoreJobFragment) {
    private val mContextRef: WeakReference<StoreJobFragment> = WeakReference(fragment)
    private val result: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var jobInfoList: List<StoredJobInfo>? = null
        try {
            mContextRef.get()?.run {
                jobInfoList = CopierService.enumerateStoredJob(requireContext(), result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(jobInfoList)
    }

    private suspend fun onPostExecute(storedJobInfoList: List<StoredJobInfo>?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                this.showProgressBar(View.GONE)
                if (result.code == Result.RESULT_OK && storedJobInfoList != null) {
                    for (storedJobInfo in storedJobInfoList) {
                        Logger.showResult(activity, "StoredJobList=" + build(storedJobInfo))
                    }
                    this.enumerateStoredJob(storedJobInfoList)
                } else if (mThrowable != null) {
                    Logger.showResult(activity, "CopierService.enumerateStoredJob ${mThrowable?.message}")
                } else {
                    Logger.showResult(activity, "CopierService.enumerateStoredJob", result)
                }
            }
        }
    }
}