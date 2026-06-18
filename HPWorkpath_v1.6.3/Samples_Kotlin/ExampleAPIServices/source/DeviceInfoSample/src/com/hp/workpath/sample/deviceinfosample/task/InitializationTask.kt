// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample.task

import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.device.DeviceService
import com.hp.workpath.sample.deviceinfosample.MainActivity
import com.hp.workpath.sample.deviceinfosample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InitializationTask(context: MainActivity) {
    private lateinit var mThrowable: Throwable
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)

    suspend fun execute() {
        var status = InitStatus.NO_ERROR

        mContextRef.get()?.run {
            try { // initialize Workpath SDK
                Workpath.getInstance().initialize(this)

                // Check if DeviceService is supported
                if (!DeviceService.isSupported(this)) { // DeviceService is not supported on this device
                    status = InitStatus.NOT_SUPPORTED
                }
            } catch (sue: SsdkUnsupportedException) {
                mThrowable = sue
                status = InitStatus.INIT_EXCEPTION
            } catch (se: SecurityException) {
                mThrowable = se
                status = InitStatus.INIT_EXCEPTION
            } catch (t: Throwable) {
                mThrowable = t
                status = InitStatus.INIT_EXCEPTION
            }
            onPostExecute(status)
        }
    }

    private suspend fun onPostExecute(status: InitStatus) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (status == InitStatus.NO_ERROR) {
                    this.handleComplete()
                } else {
                    when (status) {
                        InitStatus.INIT_EXCEPTION -> this.handleException(mThrowable)
                        InitStatus.NOT_SUPPORTED -> this.handleException(Exception(mContextRef.get()!!.getString(R.string.service_not_supported)))
                        else -> this.handleException(Exception(mContextRef.get()!!.getString(R.string.unknown_error)))
                    }
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }
}