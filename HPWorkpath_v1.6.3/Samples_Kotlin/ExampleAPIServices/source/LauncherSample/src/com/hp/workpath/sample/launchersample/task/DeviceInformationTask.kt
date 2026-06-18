// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample.task

import com.hp.workpath.api.Result
import com.hp.workpath.api.device.DeviceAttribute
import com.hp.workpath.api.device.DeviceService
import com.hp.workpath.sample.launchersample.Logger
import com.hp.workpath.sample.launchersample.MainActivity
import com.hp.workpath.sample.launchersample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class DeviceInformationTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private var mResult = Result()

    suspend fun execute() {
        var hostname: String? = null
        try {
            mContextRef.get()?.run {
                hostname = DeviceService.getString(this, DeviceAttribute.DA_NETWORK_HOSTNAME, mResult)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(hostname)
    }

    private suspend fun onPostExecute(hostname: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (mResult.code == Result.RESULT_OK && hostname != null) {
                    handleDeviceInfo(hostname)
                } else if (mThrowable != null) {
                    Logger.showResult(this, getString(R.string.error) + " " + mThrowable?.message)
                } else {
                    Logger.showResult(this, getString(R.string.error), mResult)
                }
            }
        }

    }
}