// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.Result
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.job.JobService
import com.hp.workpath.api.job.JobService.AbstractJobletObserver
import com.hp.workpath.api.job.JobletAttributes
import com.hp.workpath.api.printer.PrintAttributes.PrintFromStorageBuilder
import com.hp.workpath.api.scanner.ScanAttributes
import com.hp.workpath.api.scanner.ScanAttributes.MeBuilder
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.Logger.build
import com.hp.workpath.sample.statisticsample.MainActivity
import com.hp.workpath.sample.statisticsample.databinding.FragmentTestJobBinding
import com.hp.workpath.sample.statisticsample.task.LastJobInfoTask
import com.hp.workpath.sample.statisticsample.task.RequestPrintTask
import com.hp.workpath.sample.statisticsample.task.RequestScanTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class TestJobFragment : Fragment(), View.OnClickListener {

    private lateinit var mJobObserver: JobObserver
    private var mRid: String? = null
    private var mJobId: String? = null
    private var mBindingFragment: FragmentTestJobBinding? = null
    private val mBindingTestJobFragment get() = mBindingFragment!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mJobObserver = JobObserver(Handler(Looper.getMainLooper()))
        context?.let { mJobObserver.register(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentTestJobBinding.inflate(inflater, container, false)
        return mBindingTestJobFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingTestJobFragment.statisticInfoTextView.movementMethod = ScrollingMovementMethod()
        mBindingTestJobFragment.jobInfoTextView.movementMethod = ScrollingMovementMethod()
        mBindingTestJobFragment.getLastJobInfoButton.setOnClickListener(this)
        mBindingTestJobFragment.printButton.setOnClickListener(this)
        mBindingTestJobFragment.scanButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let { mJobObserver.unregister(it) }
    }

    override fun onClick(v: View) {
        mBindingTestJobFragment.progressBar.visibility = View.VISIBLE
        when {
            v === mBindingTestJobFragment.getLastJobInfoButton -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    context?.let { LastJobInfoTask(it, statisticsInterface).execute() }
                }
            }
            v === mBindingTestJobFragment.printButton -> {
                val printCaps = (activity as MainActivity).getPrintCaps()
                if (printCaps == null) {
                    Logger.showResult(activity, "Print is not supported")
                    mBindingTestJobFragment.progressBar.visibility = View.GONE
                    return
                }
                enableButton(false)
                cleanJobInfo()
                try {
                    val path = activity?.filesDir?.path + "/test_page.pdf"
                    val attributes = PrintFromStorageBuilder(Uri.fromFile(File(path))).build(printCaps)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                        RequestPrintTask(this@TestJobFragment, attributes).execute()
                    }
                } catch (e: CapabilitiesExceededException) {
                    mBindingTestJobFragment.progressBar.visibility = View.GONE
                    enableButton(true)
                    Logger.showResult(
                        activity,
                        "PrintAttributes.PrintFromStorageBuilder" + e.message
                    )
                }
            }
            v === mBindingTestJobFragment.scanButton -> {
                val scanCaps = (activity as MainActivity).getScanCaps()
                if (scanCaps == null) {
                    Logger.showResult(activity, "Scan is not supported")
                    mBindingTestJobFragment.progressBar.visibility = View.GONE
                    return
                }
                enableButton(false)
                cleanJobInfo()
                try {
                    val attributesBuilder = MeBuilder()
                    attributesBuilder.setDocumentFormat(ScanAttributes.DocumentFormat.PDF)
                    attributesBuilder.setJobAssemblyMode(ScanAttributes.JobAssemblyMode.OFF)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                        RequestScanTask(this@TestJobFragment, attributesBuilder.build(scanCaps)).execute()
                    }
                } catch (e: CapabilitiesExceededException) {
                    mBindingTestJobFragment.progressBar.visibility = View.GONE
                    enableButton(true)
                    Logger.showResult(activity, "ScanAttributes.MeBuilder" + e.message)
                }
            }
        }
    }

    private fun cleanJobInfo() {
        mRid = null
        mJobId = null
    }

    private fun enableButton(enable: Boolean) {
        mBindingTestJobFragment.printButton.isEnabled = enable
        mBindingTestJobFragment.scanButton.isEnabled = enable
    }

    fun setRid(rid: String?) {
        mRid = rid
    }

    private var statisticsInterface: ResponseInterface = object : ResponseInterface {
        override fun success(info: List<StatisticsJobData?>) {
            mBindingTestJobFragment.progressBar.visibility = View.GONE
            if (info.isNotEmpty()) {
                val data = info[0]
                if (TextUtils.isEmpty(data?.jobId)) {
                    mBindingTestJobFragment.statisticInfoTextView.text =
                        "There is no statistic information"
                } else {
                    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                    mBindingTestJobFragment.statisticInfoTextView.text = gson.toJson(data)
                }
            } else {
                mBindingTestJobFragment.statisticInfoTextView.text =
                    "There is no statistic information"
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingTestJobFragment.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }

    private inner class JobObserver(handler: Handler?) : AbstractJobletObserver(handler) {
        override fun onComplete(rid: String, jobInfo: JobInfo) {
            mBindingTestJobFragment.progressBar.visibility = View.GONE
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            mBindingTestJobFragment.jobInfoTextView.text = gson.toJson(jobInfo)
            enableButton(true)
        }

        override fun onProgress(rid: String, jobInfo: JobInfo) {
            Log.d(MainActivity.TAG, "Received onProgress for rid $rid")
            Log.d(MainActivity.TAG, "Received onProgress jobInfo $jobInfo")
            if (rid == mRid) {
                if (mJobId == null) {
                    if (jobInfo.jobId != null) {
                        mJobId = jobInfo.jobId
                        Log.d(MainActivity.TAG, "Received jobID as $mJobId")
                        val taskAttributes = JobletAttributes.Builder().setShowUi(true).build()
                        val jrid = activity?.let {
                            JobService.monitorJobInForeground(
                                it,
                                jobInfo.jobId,
                                taskAttributes,
                                null
                            )
                        }
                        Log.d(MainActivity.TAG, "MonitorJob request: $jrid")
                    }
                }
            }
        }

        override fun onFail(rid: String, result: Result) {
            Log.e(MainActivity.TAG, "Received onFail for rid $rid, $result")
            mBindingTestJobFragment.progressBar.visibility = View.GONE
            mBindingTestJobFragment.jobInfoTextView.text =
                "Received onFail for rid " + rid + ", " + build(result)
            enableButton(true)
        }

        override fun onCancel(rid: String) {
            Log.d(MainActivity.TAG, "Received onCancel for rid $rid")
            mBindingTestJobFragment.progressBar.visibility = View.GONE
            mBindingTestJobFragment.jobInfoTextView.text = "Received onCancel for rid $rid"
            enableButton(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}