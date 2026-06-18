// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class CallView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_call, rootView, false)
    private lateinit var layoutBillingCode: ViewGroup
    private lateinit var layoutDuration: ViewGroup
    private lateinit var layoutFaxNumber: ViewGroup
    private lateinit var layoutFaxResult: ViewGroup

    fun setCall(call: FaxAttributes.Call?) {
        rootView.removeAllViews()
        if (call != null) {
            Utils.setSummary(layoutBillingCode, call.billingCode)
            Utils.setSummary(layoutDuration, call.duration)
            Utils.setSummary(layoutFaxNumber, call.faxNumber)
            Utils.setSummary(layoutFaxResult, call.faxResult)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewCall() {
        layoutBillingCode =
            Utils.setTitle(view.findViewById(R.id.layoutBillingCode), R.string.applicationName)
        layoutDuration = Utils.setTitle(view.findViewById(R.id.layoutDuration), R.string.duration)
        layoutFaxNumber =
            Utils.setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber)
        layoutFaxResult =
            Utils.setTitle(view.findViewById(R.id.layoutFaxResult), R.string.faxResult)
    }

    init {
        initViewCall()
    }
}