// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.deviceusage.DeviceUsageInfo
import com.hp.workpath.api.deviceusage.DeviceUsageService
import com.hp.workpath.sample.deviceusagesample.fragment.ResponseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class DeviceUsageTask(context: Context, responseInterface: ResponseInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mResponseInterface: ResponseInterface = responseInterface
    private val mResult: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var deviceUsageInfo: DeviceUsageInfo? = null
        try {
            mContextRef.get()?.run {
                deviceUsageInfo = DeviceUsageService.getDeviceUsageInfo(this, mResult)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(deviceUsageInfo)
    }

    private suspend fun onPostExecute(deviceUsageInfo: DeviceUsageInfo?) {
        withContext(Dispatchers.Main) {
            if (mResult.code == Result.RESULT_OK && deviceUsageInfo != null) {
                mResponseInterface.success(deviceUsageInfo)
            } else {
                mThrowable?.run{
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents " + this.message, null)
                } ?: run {
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents", mResult)
                }
            }
        }
    }

}