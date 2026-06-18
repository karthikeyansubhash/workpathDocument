// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.deviceusage.printer.PrinterInfo
import com.hp.workpath.sample.deviceusagesample.R

class CopyByMediaSizeAdapter : RecyclerView.Adapter<CopyByMediaSizeAdapter.ViewHolder>() {
    private var mCopyByMediaSizeList: List<PrinterInfo.CopyByMediaSize> = ArrayList()

    private fun getItem(position: Int): PrinterInfo.CopyByMediaSize {
        return mCopyByMediaSizeList[position]
    }

    fun setItem(byJobCategoryAndMediaSizes: List<PrinterInfo.CopyByMediaSize>) {
        mCopyByMediaSizeList = byJobCategoryAndMediaSizes
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mCopyByMediaSizeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_job_category_and_media_size, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.indexTextView.text = (position + 1).toString()
        holder.mediaSizeTextView.text = data.mediaSize
        if (data.mediaSizeType != null) {
            holder.mediaSizeTypeTextView.text = data.mediaSizeType.name
        } else {
            holder.mediaSizeTypeTextView.text = ""
        }

        holder.colorImpressionsTextView.setText(data.colorImpressions.toString())
        holder.monoImpressionsTextView.setText(data.monoImpressions.toString())
        holder.totalImpressionsTextView.setText(data.totalImpressions.toString())
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var indexTextView: TextView = view.findViewById(R.id.indexTextView)
        var mediaSizeTextView: TextView = view.findViewById(R.id.jobCategoryTextView)
        var mediaSizeTypeTextView: TextView = view.findViewById(R.id.jobCategoryTypeTextView)
        var colorImpressionsTextView: TextView = view.findViewById(R.id.mediaSizeTextView)
        var monoImpressionsTextView: TextView = view.findViewById(R.id.mediaSizeTypeTextView)
        var totalImpressionsTextView: TextView = view.findViewById(R.id.impressionsTextView)
    }
}