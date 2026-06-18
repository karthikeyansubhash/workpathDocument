package com.hp.workpath.sample.eventnotificationsample.task

import android.content.Context
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.eventnotificationsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

abstract class InitializationTask(context: Context, handler: ResultHandler) {
    private var mThrowable: Throwable? = null

    private var mContextRef: WeakReference<Context> = WeakReference(context)

    private var mResultHandlerRef: WeakReference<ResultHandler> = WeakReference(handler)

    var status = InitStatus.NO_ERROR

    suspend fun taskExecute() {
        mContextRef.get()?.run {
            try {
                // initialize Workpath SDK
                Workpath.getInstance().initialize(this)

                // Check if AccessService is supported
                if (!isSupported(this)) {
                    // AccessService is not supported on this device
                    status = InitStatus.NOT_SUPPORTED
                }
            } catch (sue: SsdkUnsupportedException) {
                mThrowable= sue
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
            mContextRef?.get()?.run {
                if (status == InitStatus.NO_ERROR) {
                    mResultHandlerRef?.get()?.handleComplete()
                } else {
                    when (status) {
                        InitStatus.INIT_EXCEPTION -> mResultHandlerRef.get()?.handleException(mThrowable)
                        InitStatus.NOT_SUPPORTED -> mResultHandlerRef.get()?.handleException(Exception(
                            mContextRef.get()?.getString(getExceptionMessage())))
                        else -> mResultHandlerRef.get()?.handleException(Exception(this.getString(R.string.unknown_error)))
                    }
                }
            }
        }
    }


    enum class InitStatus {
        INIT_EXCEPTION,
        NOT_SUPPORTED,
        NO_ERROR
    }


    abstract fun isSupported(context: Context?): Boolean

    abstract fun getExceptionMessage(): Int
}