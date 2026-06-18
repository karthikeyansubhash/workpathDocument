// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.emailinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.jobinfo.emailinfo.EmailInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.getLayout
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle
import com.hp.workpath.sample.statisticsample.view.common.DigitalFaxCallView
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView

class EmailInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_email_info, rootView, false)
    private lateinit var digitalFaxCallView: DigitalFaxCallView
    private lateinit var digitalSendInfoView: DigitalSendInfoView

    private lateinit var layoutBccAddresses: ViewGroup
    private lateinit var layoutCcAddresses: ViewGroup
    private lateinit var layoutDigitalFaxCalls: LinearLayout
    private lateinit var layoutDigitalSendInfo: LinearLayout
    private lateinit var layoutEmailSubject: ViewGroup
    private lateinit var layoutFailedRecipientsList: ViewGroup
    private lateinit var layoutFromAddress: ViewGroup
    private lateinit var layoutHostName: ViewGroup
    private lateinit var layoutIpAddress: ViewGroup
    private lateinit var layoutPort: ViewGroup
    private lateinit var layoutToAddresses: ViewGroup

    fun setEmailInfo(emailInfo: EmailInfo?) {
        rootView.removeAllViews()
        if (emailInfo != null) {
            if (emailInfo.bccAddresses != null) {
                val bccAddress = StringBuilder()
                for (bcc in emailInfo.bccAddresses) {
                    bccAddress.append(bcc).append(" ")
                }
                setSummary(layoutBccAddresses, bccAddress.toString())
            }

            if (emailInfo.ccAddresses != null) {
                val ccAddress = StringBuilder()
                for (cc in emailInfo.ccAddresses) {
                    ccAddress.append(cc).append(" ")
                }
                setSummary(layoutCcAddresses, ccAddress.toString())
            }
            digitalFaxCallView.setDigitalFaxCall(emailInfo.digitalFaxCalls)
            digitalSendInfoView.setDigitalSendInfo(emailInfo.digitalSendInfo)
            setSummary(layoutEmailSubject, emailInfo.emailSubject)

            if (emailInfo.failedRecipientsList != null) {
                val failedRecipient = StringBuilder()
                for (recipient in emailInfo.failedRecipientsList) {
                    failedRecipient.append(recipient).append(" ")
                }
                setSummary(layoutFailedRecipientsList, failedRecipient.toString())
            }
            setSummary(layoutFromAddress, emailInfo.fromAddress)
            setSummary(layoutHostName, emailInfo.hostName)
            setSummary(layoutIpAddress, emailInfo.ipAddress)
            setSummary(layoutPort, emailInfo.port)

            if (emailInfo.toAddresses != null) {
                val toAddress = StringBuilder()
                for (to in emailInfo.toAddresses) {
                    toAddress.append(to).append(" ")
                }
                setSummary(layoutToAddresses, toAddress.toString())
            }
            rootView.addView(view)
        } else {
            val parent = rootView.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewEmailInfo(view: View) {
        layoutBccAddresses =
            setTitle(view.findViewById(R.id.layoutBccAddresses), R.string.bccAddresses)
        layoutCcAddresses =
            setTitle(view.findViewById(R.id.layoutCcAddresses), R.string.ccAddresses)
        layoutDigitalFaxCalls =
            getLayout(view.findViewById(R.id.layoutDigitalFaxCalls), R.string.digitalFaxCalls)
        layoutDigitalSendInfo =
            getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo)

        layoutEmailSubject =
            setTitle(view.findViewById(R.id.layoutEmailSubject), R.string.emailSubject)
        layoutFailedRecipientsList = setTitle(
            view.findViewById(R.id.layoutFailedRecipientsList),
            R.string.failedRecipientsList
        )
        layoutFromAddress =
            setTitle(view.findViewById(R.id.layoutFromAddress), R.string.fromAddress)
        layoutHostName = setTitle(view.findViewById(R.id.layoutHostName), R.string.hostName)
        layoutIpAddress = setTitle(view.findViewById(R.id.layoutIpAddress), R.string.ipAddress)
        layoutPort = setTitle(view.findViewById(R.id.layoutPort), R.string.port)
        layoutToAddresses =
            setTitle(view.findViewById(R.id.layoutToAddresses), R.string.toAddresses)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        digitalFaxCallView = DigitalFaxCallView(inflater, layoutDigitalFaxCalls)
        digitalSendInfoView = DigitalSendInfoView(inflater, layoutDigitalSendInfo)
    }

    init {
        initViewEmailInfo(view)
        initViewClass(inflater)
    }
}