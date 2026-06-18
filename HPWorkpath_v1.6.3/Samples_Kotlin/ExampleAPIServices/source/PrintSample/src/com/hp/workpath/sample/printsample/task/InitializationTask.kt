// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task

import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.job.JobService
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InitializationTask(context: MainActivity) {
    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var status = InitStatus.NO_ERROR

    suspend fun execute() {
        mContextRef.get()?.run {
            try { // initialize Workpath SDK
                Workpath.getInstance().initialize(this)

                if (!PrinterService.isSupported(this)
                        || !JobService.isSupported(this)) {
                    // PrinterService is not supported on this device
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