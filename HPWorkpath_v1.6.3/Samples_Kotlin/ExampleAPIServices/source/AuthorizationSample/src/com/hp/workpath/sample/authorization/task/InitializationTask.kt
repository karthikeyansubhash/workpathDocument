// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.os.Handler
import android.os.Looper
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.sample.authorization.MainActivity
import com.hp.workpath.sample.authorization.R
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class InitializationTask(context: MainActivity) {

    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler: Handler = Handler(Looper.getMainLooper())

    fun taskExecute() {
        try {
            executor.execute {
                var status = InitStatus.NO_ERROR

                try {
                    // Initialize Workpath SDK
                    mContextRef.get()?.let { Workpath.getInstance().initialize(it) }
                    // Check if Authorization is supported
                    if (mContextRef.get()?.let { AuthorizationService.isSupported(it) } == false) {
                        // Authorization is not supported on this device
                        status = InitStatus.NOT_SUPPORTED
                    }
                } catch (t: Throwable) {
                    mThrowable = t
                    status = InitStatus.INIT_EXCEPTION
                }

                onPostExecute(status)
            }
        } catch (e: Exception) {
            mThrowable = e
            onPostExecute(InitStatus.INIT_EXCEPTION)
            executor.shutdown()
        }
    }

    private fun onPostExecute(status: InitStatus) {
        handler.post {
            mContextRef.get()?.let { context ->
                when (status) {
                    InitStatus.NO_ERROR -> context.handleComplete()
                    InitStatus.INIT_EXCEPTION -> context.handleException(mThrowable)
                    InitStatus.NOT_SUPPORTED -> context.handleException(
                        Throwable(context.getString(R.string.service_not_supported))
                    )

                    else -> context.handleException(
                        Throwable(context.getString(R.string.unknown_error))
                    )
                }
            }
        }
    }

    fun cancel() {
        executor.shutdown()
    }

    enum class InitStatus {
        INIT_EXCEPTION,
        NOT_SUPPORTED,
        NO_ERROR
    }
}