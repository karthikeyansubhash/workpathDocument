// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authenticationpreprompt.task

import android.content.Context
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.AccessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InitializationTask(context: Context, private val initializeInterface: InitializeInterface?) {
    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<Context> = WeakReference(context)

    interface InitializeInterface {
        fun handleComplete()
        fun handleException(t: Throwable?)
    }

    suspend fun execute(): InitStatus {
        var status = InitStatus.NO_ERROR
        mContextRef.get()?.run {
            try {
                // initialize the Workpath SDK
                Workpath.getInstance().initialize(this)

                // Check if AccessService is supported
                if (!AccessService.isSupported(this)) {
                    // AccessService is not supported on this device
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
        return status
    }

    private suspend fun onPostExecute(status: InitStatus) {
        if (initializeInterface != null) {
            withContext(Dispatchers.Main) {
                if (status == InitStatus.NO_ERROR) {
                    initializeInterface.handleComplete()
                } else {
                    initializeInterface.handleException(mThrowable)
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }
}