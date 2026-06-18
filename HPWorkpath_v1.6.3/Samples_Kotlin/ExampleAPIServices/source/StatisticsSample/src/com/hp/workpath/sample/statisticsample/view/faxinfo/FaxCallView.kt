// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes.FaxConfiguration
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes.IpServer
import com.hp.workpath.api.statistics.jobinfo.faxinfo.IpFaxOutInfo.FaxCall
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class FaxCallView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var callView: CallView

    private lateinit var layoutsIpTransport: ViewGroup
    private lateinit var layoutFaxNumber: ViewGroup
    private lateinit var layoutPortNumber: ViewGroup
    private lateinit var layoutProxyPortNumber: ViewGroup
    private lateinit var layoutProxyServer: ViewGroup
    private lateinit var layoutServer: ViewGroup

    private lateinit var layoutFaxCall: LinearLayout

    fun setFaxCall(faxCalls: Array<FaxCall?>?) {
        rootView.removeAllViews()
        if (faxCalls != null) {
            for (index in faxCalls.indices) {
                rootView.addView(setFaxCallInternal(index, faxCalls[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setFaxCallInternal(index: Int, faxCall: FaxCall?): View {
        val view = inflater.inflate(R.layout.layout_fax_call, rootView, false)
        initViewPrintAgentInfo(view)
        initViewClass(inflater)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (faxCall != null) {
            callView.setCall(faxCall.faxCall)
            setFaxConfiguration(faxCall.ipFaxConfiguration)
        }
        return view
    }

    private fun setFaxConfiguration(faxConfiguration: FaxConfiguration?) {
        if (faxConfiguration != null) {
            setIpServer(faxConfiguration.sipServer)
            setSummary(layoutsIpTransport, faxConfiguration.sipTransport)
            setSummary(layoutFaxNumber, faxConfiguration.faxNumber)
        } else {
            val parent = layoutsIpTransport.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setIpServer(ipServer: IpServer?) {
        if (ipServer != null) {
            setSummary(layoutPortNumber, ipServer.portNumber)
            setSummary(layoutProxyPortNumber, ipServer.proxyPortNumber)
            setSummary(layoutProxyServer, ipServer.proxyServer)
            setSummary(layoutServer, ipServer.server)
        } else {
            val parent = layoutPortNumber.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewPrintAgentInfo(view: View) {
        layoutFaxCall = Utils.getLayout(view.findViewById(R.id.layoutFaxCall), R.string.faxCall)
        (view.findViewById<View>(R.id.titleIpFaxConfigurationTextView) as TextView).setText(R.string.ipFaxConfiguration)
        (view.findViewById<View>(R.id.titleIpServerTextView) as TextView).setText(R.string.sIpServer)
        layoutsIpTransport =
            setTitle(view.findViewById(R.id.layoutsIpTransport), R.string.sIpTransport)
        layoutFaxNumber = setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber)
        layoutPortNumber = setTitle(view.findViewById(R.id.layoutPortNumber), R.string.portNumber)
        layoutProxyPortNumber =
            setTitle(view.findViewById(R.id.layoutProxyPortNumber), R.string.proxyPortNumber)
        layoutProxyServer =
            setTitle(view.findViewById(R.id.layoutProxyServer), R.string.proxyServer)
        layoutServer = setTitle(view.findViewById(R.id.layoutServer), R.string.server)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        callView = CallView(inflater, layoutFaxCall)
    }
}