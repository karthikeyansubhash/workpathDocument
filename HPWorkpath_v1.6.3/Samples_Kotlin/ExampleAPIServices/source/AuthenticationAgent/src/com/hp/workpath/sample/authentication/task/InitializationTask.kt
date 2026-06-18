// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authentication.task

import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.sample.authentication.AuthenticationActivity
import java.lang.ref.WeakReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InitializationTask(context: AuthenticationActivity) {
    private var mThrowable: Throwable? = null
    private val mContextRef: WeakReference<AuthenticationActivity> = WeakReference(context)

    suspend fun execute() {
        var status = InitStatus.NO_ERROR

        mContextRef.get()?.run {
            try {
                // initialize Workpath SDK
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

            onPostExecute(status)
        }
    }

    private suspend fun onPostExecute(status: InitStatus) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (status == InitStatus.NO_ERROR) {
                    handleComplete()
                } else {
                    handleException(mThrowable)
                }
            }
        }
    }

    enum class InitStatus {
        INIT_EXCEPTION, NOT_SUPPORTED, NO_ERROR
    }
}