// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.jobinfo.faxinfo.IpFaxOutInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class IpFaxOutInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_ip_fax_out_info, rootView, false)
    private lateinit var faxCallView: FaxCallView
    private lateinit var layoutIpFaxCalls: LinearLayout

    fun setFaxOutInfo(faxOutInfo: IpFaxOutInfo?) {
        rootView.removeAllViews()
        if (faxOutInfo != null) {
            faxCallView.setFaxCall(faxOutInfo.ipFaxCalls)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewIpFaxOutInfo() {
        layoutIpFaxCalls =
            Utils.getLayout(view.findViewById(R.id.layoutIpFaxCalls), R.string.ipFaxCalls)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        faxCallView = FaxCallView(inflater, layoutIpFaxCalls)
    }

    init {
        initViewIpFaxOutInfo()
        initViewClass(inflater)
    }
}