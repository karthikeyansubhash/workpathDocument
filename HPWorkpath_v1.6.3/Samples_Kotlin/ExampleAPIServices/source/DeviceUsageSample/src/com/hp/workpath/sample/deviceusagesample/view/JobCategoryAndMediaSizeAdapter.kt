// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.deviceusage.printer.PrinterInfo.ByJobCategoryAndMediaSize
import com.hp.workpath.sample.deviceusagesample.R

class JobCategoryAndMediaSizeAdapter : RecyclerView.Adapter<JobCategoryAndMediaSizeAdapter.ViewHolder>() {
    private var mByJobCategoryAndMediaSizeList: List<ByJobCategoryAndMediaSize> = ArrayList()

    private fun getItem(position: Int): ByJobCategoryAndMediaSize {
        return mByJobCategoryAndMediaSizeList[position]
    }

    fun setItem(byJobCategoryAndMediaSizes: List<ByJobCategoryAndMediaSize>) {
        mByJobCategoryAndMediaSizeList = byJobCategoryAndMediaSizes
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mByJobCategoryAndMediaSizeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_job_category_and_media_size, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.indexTextView.text = (position + 1).toString()
        holder.jobCategoryTextView.text = data.jobCategory
        if (data.jobCategoryType != null) {
            holder.jobCategoryTypeTextView.text = data.jobCategoryType.name
        } else {
            holder.jobCategoryTypeTextView.text = ""
        }
        holder.mediaSizeTextView.text = data.mediaSize
        if (data.mediaSizeType != null) {
            holder.mediaSizeTypeTextView.text = data.mediaSizeType.name
        } else {
            holder.mediaSizeTypeTextView.text = ""
        }
        holder.impressionsTextView.text = data.impressions.toString()
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var indexTextView: TextView = view.findViewById(R.id.indexTextView)
        var jobCategoryTextView: TextView = view.findViewById(R.id.jobCategoryTextView)
        var jobCategoryTypeTextView: TextView = view.findViewById(R.id.jobCategoryTypeTextView)
        var mediaSizeTextView: TextView = view.findViewById(R.id.mediaSizeTextView)
        var mediaSizeTypeTextView: TextView = view.findViewById(R.id.mediaSizeTypeTextView)
        var impressionsTextView: TextView = view.findViewById(R.id.impressionsTextView)
    }
}