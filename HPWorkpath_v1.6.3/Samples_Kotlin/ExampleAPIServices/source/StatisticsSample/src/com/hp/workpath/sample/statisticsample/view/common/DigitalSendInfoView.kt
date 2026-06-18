// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.StatisticsAttributes.DigitalSendInfo
import com.hp.workpath.api.statistics.jobinfo.StatisticsAttributes.DigitalSendInfo.DeliveredFile
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils
import com.hp.workpath.sample.statisticsample.view.Utils.getLayout
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class DigitalSendInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_digital_send_info, rootView, false)
    private lateinit var fileInfoView: FileInfoView
    private lateinit var fileInfoMetadataView: FileInfoView

    private lateinit var layoutResult: ViewGroup
    private lateinit var layoutTotalDataSize: ViewGroup
    private lateinit var layoutFiles: LinearLayout
    private lateinit var layoutMetadataFile: LinearLayout

    fun setDigitalSendInfo(digitalSendInfo: DigitalSendInfo?) {
        rootView.removeAllViews()
        if (digitalSendInfo != null) {
            setDeliveredFile(digitalSendInfo.deliveredFiles)
            Utils.setSummary(layoutResult, digitalSendInfo.result)
            Utils.setSummary(layoutTotalDataSize, digitalSendInfo.totalDataSize)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setDeliveredFile(deliveredFile: DeliveredFile?) {
        if (deliveredFile != null) {
            fileInfoView.setFileInfo(deliveredFile.files)
            fileInfoMetadataView.setFileInfo(deliveredFile.metdataFile)
        } else {
            val parent =
                view.findViewById<View>(R.id.titleDeliveredFilesTextView).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewDigitalSendInfo() {
        (view.findViewById<View>(R.id.titleDeliveredFilesTextView) as TextView).setText(R.string.deliveredFiles)
        layoutResult = setTitle(view.findViewById(R.id.layoutResult), R.string.result)
        layoutTotalDataSize =
            setTitle(view.findViewById(R.id.layoutTotalDataSize), R.string.totalDataSize)
        layoutFiles = getLayout(view.findViewById(R.id.layoutFiles), R.string.files)
        layoutMetadataFile =
            getLayout(view.findViewById(R.id.layoutMetadataFile), R.string.metadataFile)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        fileInfoView = FileInfoView(inflater, layoutFiles)
        fileInfoMetadataView = FileInfoView(inflater, layoutMetadataFile)
    }

    init {
        initViewDigitalSendInfo()
        initViewClass(inflater)
    }
}