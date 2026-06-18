// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample.task

import android.content.Context
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.accessory.hid.AccessoryService
import com.hp.workpath.sample.accessoryagentsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InitializationTask(context: Context, private val initializeInterface: InitializeInterface?) {
    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val DEFAULT_RETRIES = 5

    interface InitializeInterface {
        fun handleComplete()
        fun handleException(t: Throwable?)
    }

    suspend fun execute(): InitStatus {
        var status = InitStatus.INIT_EXCEPTION
        var numberOfRetries = 0
        while (status == InitStatus.INIT_EXCEPTION && numberOfRetries < DEFAULT_RETRIES) {
            numberOfRetries++
            mContextRef.get()?.run {
                try {
                    // initialize Workpath SDK
                    Workpath.getInstance().initialize(this)
                    status = InitStatus.NO_ERROR
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

                // Check if AccessoryService or AccessService is supported
                if (status == InitStatus.NO_ERROR
                        && !AccessoryService.isSupported(this)
                        && !AccessService.isSupported(this)) {
                    // AccessoryService or AccessService is not supported on this device
                    status = InitStatus.NOT_SUPPORTED
                }
            }
            delay(1000 * 30.toLong())
        }

        if (initializeInterface != null) {
            onPostExecute(status)
        }
        return status
    }

    private suspend fun onPostExecute(status: InitStatus) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (status == InitStatus.NO_ERROR) {
                    initializeInterface?.handleComplete()
                } else {
                    when (status) {
                        InitStatus.INIT_EXCEPTION -> initializeInterface?.handleException(mThrowable)
                        InitStatus.NOT_SUPPORTED -> initializeInterface?.handleException(Exception(getString(R.string.service_not_supported)))
                        else -> initializeInterface?.handleException(Exception(getString(R.string.unknown_error)))
                    }
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }
}