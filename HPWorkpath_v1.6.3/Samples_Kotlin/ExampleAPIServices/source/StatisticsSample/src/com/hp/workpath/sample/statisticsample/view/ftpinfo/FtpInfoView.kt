// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.ftpinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.ftpinfo.FtpInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView

class FtpInfoView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var digitalSendInfoView: DigitalSendInfoView

    private lateinit var layoutDigitalSendInfo: LinearLayout
    private lateinit var layoutDirectoryPath: ViewGroup
    private lateinit var layoutHostName: ViewGroup
    private lateinit var layoutIpAddress: ViewGroup
    private lateinit var layoutPort: ViewGroup
    private lateinit var layoutUserName: ViewGroup

    fun setFtpInfo(ftpInfo: Array<FtpInfo?>?) {
        rootView.removeAllViews()
        if (ftpInfo != null) {
            for (index in ftpInfo.indices) {
                rootView.addView(setFtpInfoInternal(index, ftpInfo[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setFtpInfoInternal(index: Int, ftpInfo: FtpInfo?): View {
        val view = inflater.inflate(R.layout.layout_ftp_info, rootView, false)
        initViewFtpInfo(view)
        initViewClass(inflater)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (ftpInfo != null) {
            digitalSendInfoView.setDigitalSendInfo(ftpInfo.digitalSendInfo)
            Utils.setSummary(layoutDirectoryPath, ftpInfo.directoryPath)
            Utils.setSummary(layoutHostName, ftpInfo.hostName)
            Utils.setSummary(layoutIpAddress, ftpInfo.ipAddress)
            Utils.setSummary(layoutPort, ftpInfo.port)
            Utils.setSummary(layoutUserName, ftpInfo.userName)
        }
        return view
    }

    private fun initViewFtpInfo(view: View) {
        layoutDigitalSendInfo =
            Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo)
        layoutDirectoryPath =
            Utils.setTitle(view.findViewById(R.id.layoutDirectoryPath), R.string.directoryPath)
        layoutHostName = Utils.setTitle(view.findViewById(R.id.layoutHostName), R.string.hostName)
        layoutIpAddress =
            Utils.setTitle(view.findViewById(R.id.layoutIpAddress), R.string.ipAddress)
        layoutPort = Utils.setTitle(view.findViewById(R.id.layoutPort), R.string.port)
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        digitalSendInfoView = DigitalSendInfoView(inflater, layoutDigitalSendInfo)
    }
}