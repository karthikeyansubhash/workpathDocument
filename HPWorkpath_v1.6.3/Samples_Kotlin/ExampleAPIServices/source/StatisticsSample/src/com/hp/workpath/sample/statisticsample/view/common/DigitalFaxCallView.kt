// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.emailinfo.EmailInfo
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class DigitalFaxCallView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutBillingCode: ViewGroup
    private lateinit var layoutFaxNumber: ViewGroup

    fun setDigitalFaxCall(digitalFaxCall: Array<FaxAttributes.DigitalFaxCall?>?) {
        rootView.removeAllViews()
        if (digitalFaxCall != null) {
            for (index in digitalFaxCall.indices) {
                rootView.addView(setDigitalFaxCallInternal(index, digitalFaxCall[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    fun setDigitalFaxCall(digitalFaxCall: Array<EmailInfo.DigitalFaxCall?>?) {
        rootView.removeAllViews()
        if (digitalFaxCall != null) {
            for (index in digitalFaxCall.indices) {
                rootView.addView(setDigitalFaxCallInternal(index, digitalFaxCall[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setDigitalFaxCallInternal(
        index: Int,
        digitalFaxCall: FaxAttributes.DigitalFaxCall?
    ): View {
        val view = inflater.inflate(R.layout.layout_digital_fax_call, rootView, false)
        initViewDigitalFaxCall(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (digitalFaxCall != null) {
            Utils.setSummary(layoutBillingCode, digitalFaxCall.billingCode)
            Utils.setSummary(layoutFaxNumber, digitalFaxCall.faxNumber)
        }
        return view
    }

    private fun setDigitalFaxCallInternal(
        index: Int,
        digitalFaxCall: EmailInfo.DigitalFaxCall?
    ): View {
        val view = inflater.inflate(R.layout.layout_digital_fax_call, rootView, false)
        initViewDigitalFaxCall(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (digitalFaxCall != null) {
            Utils.setSummary(layoutBillingCode, digitalFaxCall.billingCode)
            Utils.setSummary(layoutFaxNumber, digitalFaxCall.faxNumber)
        }
        return view
    }

    private fun initViewDigitalFaxCall(view: View) {
        layoutBillingCode =
            Utils.setTitle(view.findViewById(R.id.layoutBillingCode), R.string.billingCode)
        layoutFaxNumber =
            Utils.setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber)
    }
}