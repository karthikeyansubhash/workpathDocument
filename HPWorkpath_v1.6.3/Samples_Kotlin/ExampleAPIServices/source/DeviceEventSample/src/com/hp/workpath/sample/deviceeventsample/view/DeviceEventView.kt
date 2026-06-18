// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.sample.deviceeventsample.R
import com.hp.workpath.sample.deviceeventsample.view.Utils.getLayout
import com.hp.workpath.sample.deviceeventsample.view.Utils.setSummary
import com.hp.workpath.sample.deviceeventsample.view.Utils.setTitle
import com.hp.workpath.sample.deviceeventsample.view.deviceevent.TimeStampView

class DeviceEventView(inflater: LayoutInflater, private var view: View) {
    private lateinit var layoutInstanceId: ViewGroup
    private lateinit var layoutSeverity: ViewGroup
    private lateinit var layoutStateChangeType: ViewGroup
    private lateinit var layoutTitle: ViewGroup
    private lateinit var layoutTimestamp: LinearLayout
    private lateinit var layoutSupportInformationLink: ViewGroup
    private lateinit var layoutCategory: ViewGroup
    private lateinit var layoutDetails: ViewGroup
    private lateinit var layoutEventCode: ViewGroup
    private lateinit var timeStampView: TimeStampView

    fun setDeviceEventData(deviceEvent: DeviceEvent?) {
        if (deviceEvent != null) {
            setSummary(layoutInstanceId, deviceEvent.instanceId)
            setSummary(layoutSeverity, deviceEvent.severity)
            setSummary(layoutStateChangeType, deviceEvent.stateChangeType)
            setSummary(layoutTitle, deviceEvent.title)
            timeStampView.setTimeStamp(deviceEvent.timestamp)
            setSummary(layoutSupportInformationLink, deviceEvent.supportInformationLink)
            setSummary(layoutCategory, deviceEvent.category)
            setSummary(layoutDetails, deviceEvent.details)
            setSummary(layoutEventCode, deviceEvent.eventCode)
        }
    }

    private fun initViewClass(inflater: LayoutInflater) {
        timeStampView = TimeStampView(inflater, layoutTimestamp)
    }

    private fun initView() {
        layoutInstanceId = setTitle(view.findViewById(R.id.layoutInstanceId), R.string.instanceId)
        layoutSeverity = setTitle(view.findViewById(R.id.layoutSeverity), R.string.serverity)
        layoutStateChangeType = setTitle(view.findViewById(R.id.layoutStateChangeType), R.string.stateChangeType)
        layoutTitle = setTitle(view.findViewById(R.id.layoutTitle), R.string.title)
        layoutTimestamp = getLayout(view.findViewById(R.id.layoutTimestamp), R.string.timestamp)
        layoutSupportInformationLink = setTitle(view.findViewById(R.id.layoutSupportInformationLink), R.string.supportInformationLink)
        layoutCategory = setTitle(view.findViewById(R.id.layoutCategory), R.string.category)
        layoutDetails = setTitle(view.findViewById(R.id.layoutDetails), R.string.details)
        layoutEventCode = setTitle(view.findViewById(R.id.layoutEventCode), R.string.eventCode)
    }

    init {
        initView()
        initViewClass(inflater)
    }
}