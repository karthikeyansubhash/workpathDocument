// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsService
import com.hp.workpath.sample.statisticsample.Logger
import java.lang.ref.WeakReference

class CommitTask(context: Context, commit: Int) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mCommit: Int = commit
    private var mThrowable: Throwable? = null
    private val mResult: Result = Result()

    suspend fun execute(): Boolean {
        var result = false;
        try {
            mContextRef.get()?.run {
                result = StatisticsService.commit(this, mCommit, mResult)
                onPostExecute(result)
                return result
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        return result
    }

    private suspend fun onPostExecute(result: Boolean) {
        mThrowable?.run {
            Logger.showResult(null, "StatisticsService.commit " + this.message)
        } ?: run {
            Logger.showResult(null, "StatisticsService.commit $result", mResult)
        }
    }
}