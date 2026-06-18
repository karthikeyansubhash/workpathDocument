// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.MainActivity
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.StatisticActivity
import com.hp.workpath.sample.statisticsample.StatisticActivity.Companion.REQUEST_KEY
import com.hp.workpath.sample.statisticsample.databinding.FragmentStatisticListBinding
import com.hp.workpath.sample.statisticsample.dialog.CommitDialogFragment
import com.hp.workpath.sample.statisticsample.task.JobInfoTask
import com.hp.workpath.sample.statisticsample.view.StatisticListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StatisticListFragment : Fragment(), View.OnClickListener {

    private lateinit var mListView: RecyclerView
    private var mListAdapter: StatisticListAdapter? = null
    private lateinit var mJobDataList: List<StatisticsJobData>
    private var mBindingFragment: FragmentStatisticListBinding? = null
    private val mBindingStatisticListFragment get() = mBindingFragment!!
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBindingFragment =
            FragmentStatisticListBinding.inflate(inflater, container, false)
        return mBindingStatisticListFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListView = view.findViewById(R.id.jobInfoListView)
        mListAdapter = StatisticListAdapter(listOnClickListener)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        mListView.layoutManager = layoutManager
        mListView.adapter = mListAdapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        mListView.addItemDecoration(dividerItemDecoration)
        mBindingStatisticListFragment.commitButton.setOnClickListener(this)
        mBindingStatisticListFragment.getLastJobInfoButton.setOnClickListener(this)

        (activity as MainActivity?)?.refreshJobInfoList()
        if (::mJobDataList.isInitialized) {
            mListAdapter?.setItem(mJobDataList as MutableList<StatisticsJobData>)
        }
        if (SCREEN_4_3_INCH.equals(mBindingStatisticListFragment.container.tag)) {
            mBindingStatisticListFragment.fabMenu?.setOnClickListener {
                if (mBindingStatisticListFragment.layoutBottom.visibility == View.VISIBLE) {
                    mBindingStatisticListFragment.layoutBottom.visibility = View.GONE
                } else {
                    mBindingStatisticListFragment.layoutBottom.visibility = View.VISIBLE
                }
            }
        }
    }

    private var listOnClickListener = View.OnClickListener { v ->
        val itemPosition = mListView.getChildLayoutPosition(v)
        val childView = mListView.layoutManager?.findViewByPosition(itemPosition)
        val sequenceTextView = childView?.findViewById<TextView>(R.id.sequenceTextView)
        val jobSequence = sequenceTextView?.text.toString().toInt()
        startJobInfoActivity(jobSequence)
    }

    private fun startLastJobInfoActivity() {
        if (::mJobDataList.isInitialized) {
            val intent = Intent(context, StatisticActivity::class.java)
            intent.putExtra(StatisticActivity.LAST_JOB, true)
            startActivity(intent)
        } else {
            if (activity != null) {
                Logger.showResult(activity, getString(R.string.no_job_info))
            }
        }
    }

    private fun startJobInfoActivity(index: Int) {
        if (index >= 0) {
            val intent = Intent(context, StatisticActivity::class.java)
            intent.putExtra(StatisticActivity.INDEX, index)
            startActivity(intent)
        } else {
            if (activity != null) {
                Logger.showResult(activity, getString(R.string.over_range))
            }
        }
    }

    fun getJobInfoList() {
        mBindingStatisticListFragment.progressBar.visibility = View.VISIBLE
        mListAdapter?.clear()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            JobInfoTask(requireContext(), statisticsInterface).execute()
        }
    }

    override fun onClick(v: View) {
        if (v === mBindingStatisticListFragment.commitButton) {
            val dialog = CommitDialogFragment.newInstance()
            dialog.show(parentFragmentManager, "dialog")
            parentFragmentManager.setFragmentResultListener(
                REQUEST_KEY,
                this
            ) { requestKey, _ ->
                if (REQUEST_KEY == requestKey) {
                    (activity as MainActivity).refreshJobInfoList()
                }
            }
        } else if (v === mBindingStatisticListFragment.getLastJobInfoButton) {
            startLastJobInfoActivity()
        }
    }

    private var statisticsInterface: ResponseInterface = object : ResponseInterface {
        override fun success(info: List<StatisticsJobData?>) {
            mBindingStatisticListFragment.progressBar.visibility = View.GONE

            mJobDataList = info.filterNotNull()
            if (mJobDataList.isNotEmpty()) {
                mListAdapter?.setItem(mJobDataList.toMutableList())
            } else {
                if (activity != null) {
                    Logger.showResult(activity, getString(R.string.no_job_info))
                }
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingStatisticListFragment.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}