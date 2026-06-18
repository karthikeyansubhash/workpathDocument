// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxInInfo
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxInInfo.Id
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class FaxInInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_fax_in_info, rootView, false)

    private lateinit var layoutStationId: ViewGroup
    private lateinit var layoutTotalImagesReceived: ViewGroup

    private lateinit var layoutName: ViewGroup
    private lateinit var layoutNumber: ViewGroup

    fun setFaxInInfo(faxInInfo: FaxInInfo?) {
        rootView.removeAllViews()
        if (faxInInfo != null) {
            setId(faxInInfo.callerId)
            setSummary(layoutStationId, faxInInfo.stationId)
            setSummary(layoutTotalImagesReceived, faxInInfo.totalImagesReceived)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setId(id: Id?) {
        if (id != null) {
            setSummary(layoutName, id.name)
            setSummary(layoutNumber, id.number)
        } else {
            val parent = layoutName.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewFaxInInfo() {
        (view.findViewById<View>(R.id.titleCallerIdTextView) as TextView).setText(R.string.callerId)
        layoutStationId = setTitle(view.findViewById(R.id.layoutStationId), R.string.stationId)
        layoutTotalImagesReceived = setTitle(
            view.findViewById(R.id.layoutTotalImagesReceived),
            R.string.totalImagesReceived
        )
        layoutName = setTitle(view.findViewById(R.id.layoutName), R.string.name)
        layoutNumber = setTitle(view.findViewById(R.id.layoutNumber), R.string.number)
    }

    init {
        initViewFaxInInfo()
    }
}