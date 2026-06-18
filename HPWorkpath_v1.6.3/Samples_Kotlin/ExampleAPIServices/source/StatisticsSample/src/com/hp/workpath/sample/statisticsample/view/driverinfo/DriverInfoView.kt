// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.driverinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.jobinfo.driverinfo.DriverInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class DriverInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_driver_info, rootView, false)
    private lateinit var layoutApplicationName: ViewGroup
    private lateinit var layoutClientHostName: ViewGroup
    private lateinit var layoutFileName: ViewGroup
    private lateinit var layoutJobAcct13: ViewGroup
    private lateinit var layoutJobAcct14: ViewGroup
    private lateinit var layoutJobAcct15: ViewGroup
    private lateinit var layoutJobAcct16: ViewGroup
    private lateinit var layoutJobId: ViewGroup
    private lateinit var layoutUserDomain: ViewGroup
    private lateinit var layoutUserName: ViewGroup

    fun setDriverInfo(driverInfo: DriverInfo?) {
        rootView.removeAllViews()
        if (driverInfo != null) {
            Utils.setSummary(layoutApplicationName, driverInfo.applicationName)
            Utils.setSummary(layoutClientHostName, driverInfo.clientHostName)
            Utils.setSummary(layoutFileName, driverInfo.fileName)
            Utils.setSummary(layoutJobAcct13, driverInfo.jobAcct13)
            Utils.setSummary(layoutJobAcct14, driverInfo.jobAcct14)
            Utils.setSummary(layoutJobAcct15, driverInfo.jobAcct15)
            Utils.setSummary(layoutJobAcct16, driverInfo.jobAcct16)
            Utils.setSummary(layoutJobId, driverInfo.jobId)
            Utils.setSummary(layoutUserDomain, driverInfo.userDomain)
            Utils.setSummary(layoutUserName, driverInfo.userName)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewDriverInfo() {
        layoutApplicationName =
            Utils.setTitle(view.findViewById(R.id.layoutApplicationName), R.string.applicationName)
        layoutClientHostName =
            Utils.setTitle(view.findViewById(R.id.layoutClientHostName), R.string.clientHostName)
        layoutFileName = Utils.setTitle(view.findViewById(R.id.layoutFileName), R.string.fileName)
        layoutJobAcct13 =
            Utils.setTitle(view.findViewById(R.id.layoutJobAcct13), R.string.jobAcct13)
        layoutJobAcct14 =
            Utils.setTitle(view.findViewById(R.id.layoutJobAcct14), R.string.jobAcct14)
        layoutJobAcct15 =
            Utils.setTitle(view.findViewById(R.id.layoutJobAcct15), R.string.jobAcct15)
        layoutJobAcct16 =
            Utils.setTitle(view.findViewById(R.id.layoutJobAcct16), R.string.jobAcct16)
        layoutJobId = Utils.setTitle(view.findViewById(R.id.layoutJobId), R.string.jobId)
        layoutUserDomain =
            Utils.setTitle(view.findViewById(R.id.layoutUserDomain), R.string.userDomain)
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName)
    }

    init {
        initViewDriverInfo()
    }
}