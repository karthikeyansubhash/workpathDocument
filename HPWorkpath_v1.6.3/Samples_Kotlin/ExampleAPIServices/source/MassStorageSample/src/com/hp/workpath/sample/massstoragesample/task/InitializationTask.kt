// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task

import android.util.Log
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.massstorage.MassStorageService
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InitializationTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        var status = InitStatus.NO_ERROR
        mContextRef.get()?.run {
            try {
                // initialize Workpath SDK
                Workpath.getInstance().initialize(this)

                // Check if MassStorageService is supported
                if (!MassStorageService.isSupported(this)) {
                    // MassStorageService is not supported on this device
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
        }
        onPostExecute(status)
    }

    private suspend fun onPostExecute(status: InitStatus) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (status == InitStatus.NO_ERROR) {
                    handleComplete()
                } else {
                    when (status) {
                        InitStatus.INIT_EXCEPTION -> handleException(mThrowable)
                        InitStatus.NOT_SUPPORTED -> handleException(Exception(getString(R.string.service_not_supported)))
                        else -> handleException(Exception(getString(R.string.unknown_error)))
                    }
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }
}