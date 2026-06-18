// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.api.statistics.StatisticsService
import com.hp.workpath.sample.statisticsample.fragment.ResponseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class JobInfoTask(context: Context, responseInterface: ResponseInterface) {
    private val mContextRef: WeakReference<Context> = WeakReference(context)
    private val mResponseInterface: ResponseInterface = responseInterface
    private var mThrowable: Throwable? = null
    private val mResult: Result = Result()

    suspend fun execute(jobSequence: Int? = null) {
        try {
            mContextRef.get()?.run {
                return if (jobSequence == null) {
                    onPostExecute(StatisticsService.getAllJobsList(this, mResult))
                } else {
                    val jobDataList: MutableList<StatisticsJobData> = ArrayList()
                    jobDataList.add(StatisticsService.getJobInfoByJobSequence(this, jobSequence, mResult))
                    onPostExecute(jobDataList)
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(null)
    }

    private suspend fun onPostExecute(jobDataList: List<StatisticsJobData>?) {
        withContext(Dispatchers.Main) {
            if (mResult.code == Result.RESULT_OK && jobDataList != null) {
                mResponseInterface.success(jobDataList)
            } else {
                mThrowable?.run {
                    mResponseInterface.failure("StatisticsService.getJobInfoByJobSequence $message", null)
                } ?: run {
                    mResponseInterface.failure("StatisticsService.getJobInfoByJobSequence", mResult)
                }
            }
        }
    }
}