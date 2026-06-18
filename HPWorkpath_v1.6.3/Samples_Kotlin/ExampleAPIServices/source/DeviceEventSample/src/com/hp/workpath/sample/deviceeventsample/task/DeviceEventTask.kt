// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.api.device.events.DeviceEventsService
import com.hp.workpath.sample.deviceeventsample.fragment.ResponseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class DeviceEventTask(context: Context, responseInterface: ResponseInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mResponseInterface: ResponseInterface = responseInterface
    private val mResult: Result = Result()
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var deviceEventList: List<DeviceEvent>? = null
        try {
            mContextRef.get()?.run {
                deviceEventList = DeviceEventsService.getDeviceEvents(this, mResult)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(deviceEventList)
    }

    private suspend fun onPostExecute(deviceEventList: List<DeviceEvent>?) {
        withContext(Dispatchers.Main) {
            if (mResult.code == Result.RESULT_OK && deviceEventList != null) {
                mResponseInterface.success(deviceEventList)
            } else {
                mThrowable?.run {
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents $message", null)
                } ?: run {
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents", mResult)
                }
            }
        }
    }
}