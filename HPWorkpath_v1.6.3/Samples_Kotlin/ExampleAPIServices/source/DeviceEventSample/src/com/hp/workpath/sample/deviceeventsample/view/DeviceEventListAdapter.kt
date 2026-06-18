// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.sample.deviceeventsample.R

class DeviceEventListAdapter(private var mListOnClickListener: View.OnClickListener) : RecyclerView.Adapter<DeviceEventListAdapter.ViewHolder>() {
    private var mDeviceEventDataList: MutableList<DeviceEvent> = ArrayList()

    private fun getItem(position: Int): DeviceEvent {
        return mDeviceEventDataList[position]
    }

    fun setItem(deviceEventList: MutableList<DeviceEvent>) {
        mDeviceEventDataList = deviceEventList
        notifyDataSetChanged()
    }

    fun clear() {
        val size = mDeviceEventDataList.size
        if (size > 0) {
            mDeviceEventDataList.clear()
            notifyItemRangeRemoved(0, size)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mDeviceEventDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_device_event_list, parent, false)
        view.setOnClickListener(mListOnClickListener)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventData = getItem(position)
        holder.sequenceTextView.text = (position + 1).toString()
        holder.titleTextView.text = eventData.title
        holder.severityTextView.text = eventData.severity
        holder.stateChangeTypeTextView.text = eventData.stateChangeType
        holder.categoryTextView.text = eventData.category
        holder.timestamp.text = eventData.timestamp.time
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var sequenceTextView: TextView = view.findViewById(R.id.sequenceTextView)
        var titleTextView: TextView = view.findViewById(R.id.titleTextView)
        var severityTextView: TextView = view.findViewById(R.id.severityTextView)
        var stateChangeTypeTextView: TextView = view.findViewById(R.id.stateChangeTypeTextView)
        var categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        var timestamp: TextView = view.findViewById(R.id.timestampTextView)
    }
}