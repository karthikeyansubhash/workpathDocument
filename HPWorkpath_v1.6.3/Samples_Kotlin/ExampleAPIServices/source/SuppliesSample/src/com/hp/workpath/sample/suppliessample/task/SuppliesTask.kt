// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.supplies.SuppliesService
import com.hp.workpath.api.supplies.supplyinfo.Supply
import com.hp.workpath.sample.suppliessample.fragment.ResponseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class SuppliesTask(context: Context, responseInterface: ResponseInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mResponseInterface: ResponseInterface = responseInterface
    private var mThrowable: Throwable? = null
    private var mResult: Result = Result()

    suspend fun execute() {
        var supplies: List<Supply>? = null
        mContextRef.get()?.run {
            try {
                supplies = SuppliesService.getSuppliesInfo(this, mResult)
            } catch (t: Throwable) {
                mThrowable = t
            }
        }
        onPostExecute(supplies)
    }

    private suspend fun onPostExecute(supplyList: List<Supply>?) {
        withContext(Dispatchers.Main) {
            if (mResult.code == Result.RESULT_OK && supplyList != null) {
                mResponseInterface.success(supplyList)
            } else {
                mThrowable?.run {
                    mResponseInterface.failed("SuppliesService.getSuppliesInfo $message", null)
                } ?: run {
                    mResponseInterface.failed("SuppliesService.getSuppliesInfo", mResult)
                }
            }
        }
    }
}