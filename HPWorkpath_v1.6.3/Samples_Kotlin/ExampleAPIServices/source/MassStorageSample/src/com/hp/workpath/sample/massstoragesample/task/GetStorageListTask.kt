// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.api.massstorage.MassStorageService
import com.hp.workpath.sample.massstoragesample.Logger
import com.hp.workpath.sample.massstoragesample.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class GetStorageListTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val mResult: Result = Result()

    suspend fun execute() {
        var massStorageInfoList: MutableList<MassStorageInfo>? = null
        try {
            mContextRef.get()?.run {
                massStorageInfoList = MassStorageService.getStorageList(this, mResult)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(massStorageInfoList)
    }

    private suspend fun onPostExecute(massStorageInfoList: MutableList<MassStorageInfo>?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgress(View.GONE)
                Logger.showResult(this, "MassStorageService.getStorageList", mResult)
                if (massStorageInfoList != null && mResult.code == Result.RESULT_OK) {
                    loadStorageList(massStorageInfoList)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "MassStorageService.getStorageList ${mThrowable?.message}")
                }
            }
        }
    }
}