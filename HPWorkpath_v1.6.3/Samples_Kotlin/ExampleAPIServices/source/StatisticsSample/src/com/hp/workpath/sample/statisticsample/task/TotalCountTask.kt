// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsService
import com.hp.workpath.sample.statisticsample.Logger
import java.lang.ref.WeakReference

class TotalCountTask(context: Context) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val mResult: Result = Result()

    suspend fun execute(): Int? {
        try {
            mContextRef.get()?.run {
                val count = StatisticsService.getTotalCount(this, mResult)
                onPostExecute(count)
                return count
            }
        } catch (t: Throwable) {
            mThrowable = t
        }

        return null
    }

    private suspend fun onPostExecute(count: Int?) {
        mThrowable?.run {
            Logger.showResult(null, "StatisticsService.getTotalCount " + this.message)
        } ?: run {
            Logger.showResult(null, "StatisticsService.getTotalCount $count", mResult)
        }
    }
}