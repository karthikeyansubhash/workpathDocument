// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxOutInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class FaxOutInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_fax_out_info, rootView, false)
    private lateinit var callView: CallView
    private lateinit var layoutFaxCalls: LinearLayout

    fun setFaxOutInfo(faxOutInfo: FaxOutInfo?) {
        rootView.removeAllViews()
        if (faxOutInfo != null) {
            for (call in faxOutInfo.faxCalls) {
                callView.setCall(call)
            }
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewFaxOutInfo() {
        layoutFaxCalls = Utils.getLayout(view.findViewById(R.id.layoutFaxCalls), R.string.faxCalls)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        callView = CallView(inflater, layoutFaxCalls)
    }

    init {
        initViewFaxOutInfo()
        initViewClass(inflater)
    }
}