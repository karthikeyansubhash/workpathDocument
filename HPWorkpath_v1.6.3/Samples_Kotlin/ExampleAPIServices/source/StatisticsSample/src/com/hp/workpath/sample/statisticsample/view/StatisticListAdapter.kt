// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.sample.statisticsample.R

class StatisticListAdapter(private var mListOnClickListener: View.OnClickListener) :
    RecyclerView.Adapter<StatisticListAdapter.ViewHolder>() {
    private var mJobDataList: MutableList<StatisticsJobData> = ArrayList()

    private fun getItem(position: Int): StatisticsJobData {
        return mJobDataList[position]
    }

    fun setItem(jobDataList: MutableList<StatisticsJobData>) {
        mJobDataList = jobDataList
        notifyDataSetChanged()
    }

    fun clear() {
        val size = mJobDataList.size
        if (size > 0) {
            mJobDataList.clear()
            notifyItemRangeRemoved(0, size)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mJobDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_jobinfo_list, parent, false)
        view.setOnClickListener(mListOnClickListener)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jobData = getItem(position)
        holder.sequenceTextView.text = jobData.jobSequence.toString()
        holder.jobIdTextView.text = jobData.jobId
        if (jobData.jobInfo != null) {
            if (jobData.jobInfo.jobCategory != null) {
                holder.jobCategoryTextView.text = jobData.jobInfo.jobCategory.name
            }
            holder.jobNameTextView.text = jobData.jobInfo.deviceJobName
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var sequenceTextView: TextView = view.findViewById(R.id.sequenceTextView)
        var jobIdTextView: TextView = view.findViewById(R.id.jobIdTextView)
        var jobCategoryTextView: TextView = view.findViewById(R.id.jobCategoryTextView)
        var jobNameTextView: TextView = view.findViewById(R.id.jobNameTextView)
    }
}