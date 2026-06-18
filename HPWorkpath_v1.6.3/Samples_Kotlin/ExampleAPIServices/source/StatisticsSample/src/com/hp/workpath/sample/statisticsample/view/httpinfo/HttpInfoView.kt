// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.httpinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.httpinfo.HttpInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView

class HttpInfoView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var digitalSendInfoView: DigitalSendInfoView
    private lateinit var layoutDigitalSendInfo: LinearLayout
    private lateinit var layoutUri: ViewGroup
    private lateinit var layoutUserName: ViewGroup

    fun setHttpInfo(httpInfo: Array<HttpInfo?>?) {
        rootView.removeAllViews()
        if (httpInfo != null) {
            for (index in httpInfo.indices) {
                rootView.addView(setHttpInfoInternal(index, httpInfo[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setHttpInfoInternal(index: Int, httpInfo: HttpInfo?): View {
        val view = inflater.inflate(R.layout.layout_http_info, rootView, false)
        initViewHttpInfo(view)
        initViewClass(inflater)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (httpInfo != null) {
            digitalSendInfoView.setDigitalSendInfo(httpInfo.digitalSendInfo)
            Utils.setSummary(layoutUri, httpInfo.uri)
            Utils.setSummary(layoutUserName, httpInfo.userName)
        }
        return view
    }

    private fun initViewHttpInfo(view: View) {
        layoutDigitalSendInfo =
            Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo)
        layoutUri = Utils.setTitle(view.findViewById(R.id.layoutUri), R.string.uri)
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        digitalSendInfoView = DigitalSendInfoView(inflater, layoutDigitalSendInfo)
    }
}