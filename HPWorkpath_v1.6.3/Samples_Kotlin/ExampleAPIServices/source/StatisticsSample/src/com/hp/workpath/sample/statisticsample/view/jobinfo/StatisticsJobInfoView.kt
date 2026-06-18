// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.jobinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.StatisticsJobInfo
import com.hp.workpath.api.statistics.jobinfo.Timestamp
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class StatisticsJobInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_statistic_job_info, rootView, false)
    private lateinit var layoutJobInfoJobId: ViewGroup
    private lateinit var layoutJobInfoApplicationName: ViewGroup
    private lateinit var layoutJobInfoDeviceJobName: ViewGroup
    private lateinit var layoutJobInfoJobCategory: ViewGroup
    private lateinit var layoutJobInfoJobDataSource: ViewGroup
    private lateinit var layoutJobInfoJobDestinations: ViewGroup
    private lateinit var layoutJobInfoJobDoneStatus: ViewGroup
    private lateinit var layoutJobPaused: ViewGroup
    private lateinit var layoutJobInfoParentJobId: ViewGroup
    private lateinit var layoutJobInfoProcessedBy: ViewGroup

    private lateinit var layoutJobDoneTimeStampOffset: ViewGroup
    private lateinit var layoutJobDoneTimeStampTime: ViewGroup
    private lateinit var layoutJobStartedTimeStampOffset: ViewGroup
    private lateinit var layoutJobStartedTimeStampTime: ViewGroup

    fun setStatisticsJobInfo(statisticsJobInfo: StatisticsJobInfo?) {
        rootView.removeAllViews()
        if (statisticsJobInfo != null) {
            setSummary(layoutJobInfoJobId, statisticsJobInfo.jobId)
            setSummary(layoutJobInfoApplicationName, statisticsJobInfo.applicationName)
            setSummary(layoutJobInfoDeviceJobName, statisticsJobInfo.deviceJobName)
            setSummary(layoutJobInfoJobCategory, statisticsJobInfo.jobCategory)
            setSummary(layoutJobInfoJobDataSource, statisticsJobInfo.jobDataSource)
            var destination = ""
            if (statisticsJobInfo.jobDestinations != null) {
                for (jobDestination in statisticsJobInfo.jobDestinations) {
                    destination += jobDestination.name + " "
                }
            }
            setSummary(layoutJobInfoJobDestinations, destination)
            setSummary(layoutJobInfoJobDoneStatus, statisticsJobInfo.jobDoneStatus)
            setJobDoneTimeStamp(statisticsJobInfo.jobDoneTimestamp)
            setSummary(layoutJobPaused, statisticsJobInfo.jobPaused)
            setJobStartedTimeStamp(statisticsJobInfo.jobStartedTimestamp)
            setSummary(layoutJobInfoParentJobId, statisticsJobInfo.parentJobId)
            setSummary(layoutJobInfoProcessedBy, statisticsJobInfo.processedBy)
            rootView.addView(view)
        } else {
            val parent = rootView.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setJobDoneTimeStamp(timeStamp: Timestamp?) {
        if (timeStamp != null) {
            setSummary(layoutJobDoneTimeStampOffset, timeStamp.offset)
            setSummary(layoutJobDoneTimeStampTime, timeStamp.time)
        } else {
            val parent = layoutJobDoneTimeStampOffset.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setJobStartedTimeStamp(timeStamp: Timestamp?) {
        if (timeStamp != null) {
            setSummary(layoutJobStartedTimeStampOffset, timeStamp.offset)
            setSummary(layoutJobStartedTimeStampTime, timeStamp.time)
        } else {
            val parent = layoutJobStartedTimeStampOffset.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewJobInfo(view: View) {
        layoutJobInfoJobId =
            setTitle(view.findViewById(R.id.layoutJobInfoJobId), R.string.jobInfoJobId)
        layoutJobInfoApplicationName = setTitle(
            view.findViewById(R.id.layoutJobInfoApplicationName),
            R.string.jobInfoApplicationName
        )
        layoutJobInfoDeviceJobName = setTitle(
            view.findViewById(R.id.layoutJobInfoDeviceJobName),
            R.string.jobInfoDeviceJobName
        )
        layoutJobInfoJobCategory =
            setTitle(view.findViewById(R.id.layoutJobInfoJobCategory), R.string.jobInfoJobCategory)
        layoutJobInfoJobDataSource = setTitle(
            view.findViewById(R.id.layoutJobInfoJobDataSource),
            R.string.jobInfoJobDataSource
        )
        layoutJobInfoJobDestinations = setTitle(
            view.findViewById(R.id.layoutJobInfoJobDestinations),
            R.string.jobInfoJobDestinations
        )
        layoutJobInfoJobDoneStatus = setTitle(
            view.findViewById(R.id.layoutJobInfoJobDoneStatus),
            R.string.jobInfoJobDoneStatus
        )
        layoutJobPaused = setTitle(view.findViewById(R.id.layoutJobPaused), R.string.jobPaused)

        layoutJobInfoParentJobId =
            setTitle(view.findViewById(R.id.layoutJobInfoParentJobId), R.string.jobInfoParentJobId)
        layoutJobInfoProcessedBy =
            setTitle(view.findViewById(R.id.layoutJobInfoProcessedBy), R.string.jobInfoProcessedBy)

        (view.findViewById<View>(R.id.titleJobDoneTimestampTextView) as TextView).setText(R.string.jobDoneTimestamp)
        (view.findViewById<View>(R.id.titleJobStartedTimestampTextView) as TextView).setText(R.string.jobStartedTimestamp)

        layoutJobDoneTimeStampOffset =
            setTitle(view.findViewById(R.id.layoutJobDoneTimestampOffset), R.string.offset)
        layoutJobDoneTimeStampTime =
            setTitle(view.findViewById(R.id.layoutJobDoneTimestampTime), R.string.time)
        layoutJobStartedTimeStampOffset =
            setTitle(view.findViewById(R.id.layoutJobStartedTimestampOffset), R.string.offset)
        layoutJobStartedTimeStampTime =
            setTitle(view.findViewById(R.id.layoutJobStartedTimestampTime), R.string.time)
    }

    init {
        initViewJobInfo(view)
    }
}