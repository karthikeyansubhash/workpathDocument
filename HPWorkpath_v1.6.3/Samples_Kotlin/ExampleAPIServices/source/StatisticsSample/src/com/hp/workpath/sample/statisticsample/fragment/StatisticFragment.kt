// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment

import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.StatisticActivity.Companion.INDEX
import com.hp.workpath.sample.statisticsample.StatisticActivity.Companion.LAST_JOB
import com.hp.workpath.sample.statisticsample.databinding.FragmentStatisticBinding
import com.hp.workpath.sample.statisticsample.task.JobInfoTask
import com.hp.workpath.sample.statisticsample.task.LastJobInfoTask
import com.hp.workpath.sample.statisticsample.view.StatisticJobInfoView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StatisticFragment : Fragment() {

    private lateinit var statisticView: StatisticJobInfoView
    private var mStatisticJobData: StatisticsJobData? = null

    private var mBindingFragment: FragmentStatisticBinding? = null
    private val mBindingStatisticFragment get() = mBindingFragment!!
    private var mIndex = 0
    private var isLastJob = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val view = inflater.inflate(R.layout.fragment_statistic, container, false)
        mBindingFragment = FragmentStatisticBinding.inflate(inflater, container, false)
        statisticView = StatisticJobInfoView(inflater, mBindingStatisticFragment.root)
        return mBindingStatisticFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBindingStatisticFragment.statisticRawDataTextView.movementMethod =
            ScrollingMovementMethod()
        arguments?.run {
            isLastJob = getBoolean(LAST_JOB)
            if (isLastJob) {
                getLastJobInfo()
            } else {
                mIndex = getInt(INDEX)
                getJobInfoList()
            }
        }
    }

    private fun setRawData(statisticsJobData: StatisticsJobData) {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        mBindingStatisticFragment.statisticRawDataTextView.text = gson.toJson(statisticsJobData)
    }

    private fun getJobInfoList() {
        mBindingStatisticFragment.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            context?.let { JobInfoTask(it, statisticsInterface).execute(mIndex) }
        }
    }

    private fun getLastJobInfo() {
        mBindingStatisticFragment.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            context?.let { LastJobInfoTask(it, statisticsInterface).execute() }
        }
    }

    private fun setStatisticsJobData(statisticsJobData: StatisticsJobData) {
        statisticView.setStatisticJobData(statisticsJobData)
    }

    private fun showStatisticJobData() {
        mStatisticJobData?.run {
            setRawData(this)
            setStatisticsJobData(this)
        } ?: run {
            mBindingStatisticFragment.statisticRawDataTextView.text =
                "There is no statistic information"
        }
    }

    private var statisticsInterface: ResponseInterface = object : ResponseInterface {
        override fun success(info: List<StatisticsJobData?>) {
            mBindingStatisticFragment.progressBar.visibility = View.GONE
            if (info.isNotEmpty()) {
                mStatisticJobData = info[0]
                if (TextUtils.isEmpty(mStatisticJobData?.jobId)) {
                    mBindingStatisticFragment.statisticRawDataTextView.text =
                        "There is no statistic information"
                } else {
                    showStatisticJobData()
                }
            } else {
                mBindingStatisticFragment.statisticRawDataTextView.text =
                    "There is no statistic information"
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingStatisticFragment.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}