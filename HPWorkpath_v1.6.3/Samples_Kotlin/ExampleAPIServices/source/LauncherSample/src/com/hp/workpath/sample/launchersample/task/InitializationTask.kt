// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample.task

import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.launcher.LauncherService
import com.hp.workpath.sample.launchersample.MainActivity
import com.hp.workpath.sample.launchersample.R
import java.lang.ref.WeakReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InitializationTask(context: MainActivity) {
    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)

    suspend fun execute() {
        var status = InitStatus.NO_ERROR
        mContextRef.get()?.run {
            try { // initialize Workpath SDK
                Workpath.getInstance().initialize(this)

                // Check if LauncherService is supported
                if (!LauncherService.isSupported(this)) {
                    // LauncherService is not supported on this device
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

    private suspend fun onPostExecute(status: InitStatus?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (status == InitStatus.NO_ERROR || status == null) {
                    this.handleComplete()
                } else {
                    when (status) {
                        InitStatus.INIT_EXCEPTION -> mThrowable?.let { this.handleException(it) }
                        InitStatus.NOT_SUPPORTED -> this.handleException(Exception(this.getString(R.string.service_not_supported)))
                        else -> this.handleException(Exception(this.getString(R.string.unknown_error)))
                    }
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }

    companion object {
        private const val TAG = MainActivity.TAG
    }

}