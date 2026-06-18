// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view.deviceevent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.device.events.Timestamp
import com.hp.workpath.sample.deviceeventsample.R
import com.hp.workpath.sample.deviceeventsample.view.Utils

class TimeStampView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    lateinit var layoutOffset: ViewGroup
    lateinit var layoutTime: ViewGroup

    fun setTimeStamp(timeStamp: Timestamp?) {
        rootView.removeAllViews()
        if (timeStamp != null) {
            rootView.addView(setTimeStampInternal(timeStamp))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setTimeStampInternal(timeStamp: Timestamp?): View {
        val view = inflater.inflate(R.layout.layout_time_stamp, rootView, false)
        initViewTimeStamp(view)
        if (timeStamp != null) {
            Utils.setSummary(layoutOffset, timeStamp.offset)
            Utils.setSummary(layoutTime, timeStamp.time)
        }
        return view
    }

    private fun initViewTimeStamp(view: View) {
        layoutOffset = Utils.setTitle(view.findViewById(R.id.layoutOffset), R.string.offset)
        layoutTime = Utils.setTitle(view.findViewById(R.id.layoutTime), R.string.time)
    }
}