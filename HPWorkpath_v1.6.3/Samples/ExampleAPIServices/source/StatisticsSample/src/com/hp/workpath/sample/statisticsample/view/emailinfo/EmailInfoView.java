// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.emailinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.emailinfo.EmailInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;
import com.hp.workpath.sample.statisticsample.view.common.DigitalFaxCallView;
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView;

public class EmailInfoView {

    LinearLayout rootView;
    View view;
    DigitalFaxCallView digitalFaxCallView;
    DigitalSendInfoView digitalSendInfoView;

    ViewGroup layoutBccAddresses;
    ViewGroup layoutCcAddresses;
    LinearLayout layoutDigitalFaxCalls;
    LinearLayout layoutDigitalSendInfo;
    ViewGroup layoutEmailSubject;
    ViewGroup layoutFailedRecipientsList;
    ViewGroup layoutFromAddress;
    ViewGroup layoutHostName;
    ViewGroup layoutIpAddress;
    ViewGroup layoutPort;
    ViewGroup layoutToAddresses;

    public EmailInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_email_info, rootView, false);
        initViewEmailInfo(view);
        initViewClass(inflater);
    }

    public void setEmailInfo(EmailInfo emailInfo) {
        rootView.removeAllViews();
        if (emailInfo != null) {
            if (emailInfo.getBccAddresses() != null) {
                StringBuilder bccAddress = new StringBuilder();
                for (String bcc : emailInfo.getBccAddresses()) {
                    bccAddress.append(bcc).append(" ");
                }
                Utils.setSummary(layoutBccAddresses, bccAddress.toString());
            }

            if (emailInfo.getCcAddresses() != null) {
                StringBuilder ccAddress = new StringBuilder();
                for (String cc : emailInfo.getCcAddresses()) {
                    ccAddress.append(cc).append(" ");
                }
                Utils.setSummary(layoutCcAddresses, ccAddress.toString());
            }
            digitalFaxCallView.setDigitalFaxCall(emailInfo.getDigitalFaxCalls());
            digitalSendInfoView.setDigitalSendInfo(emailInfo.getDigitalSendInfo());
            Utils.setSummary(layoutEmailSubject, emailInfo.getEmailSubject());

            if (emailInfo.getFailedRecipientsList() != null) {
                StringBuilder failedRecipient = new StringBuilder();
                for (String recipient : emailInfo.getFailedRecipientsList()) {
                    failedRecipient.append(recipient).append(" ");
                }
                Utils.setSummary(layoutFailedRecipientsList, failedRecipient.toString());
            }
            Utils.setSummary(layoutFromAddress, emailInfo.getFromAddress());
            Utils.setSummary(layoutHostName, emailInfo.getHostName());
            Utils.setSummary(layoutIpAddress, emailInfo.getIpAddress());
            Utils.setSummary(layoutPort, emailInfo.getPort());

            if (emailInfo.getToAddresses() != null) {
                StringBuilder toAddress = new StringBuilder();
                for (String to : emailInfo.getToAddresses()) {
                    toAddress.append(to).append(" ");
                }
                Utils.setSummary(layoutToAddresses, toAddress.toString());
            }
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewEmailInfo(View view) {
        layoutBccAddresses = Utils.setTitle(view.findViewById(R.id.layoutBccAddresses), R.string.bccAddresses);
        layoutCcAddresses = Utils.setTitle(view.findViewById(R.id.layoutCcAddresses), R.string.ccAddresses);
        layoutDigitalFaxCalls = Utils.getLayout(view.findViewById(R.id.layoutDigitalFaxCalls), R.string.digitalFaxCalls);
        layoutDigitalSendInfo = Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo);

        layoutEmailSubject = Utils.setTitle(view.findViewById(R.id.layoutEmailSubject), R.string.emailSubject);
        layoutFailedRecipientsList = Utils.setTitle(view.findViewById(R.id.layoutFailedRecipientsList), R.string.failedRecipientsList);
        layoutFromAddress = Utils.setTitle(view.findViewById(R.id.layoutFromAddress), R.string.fromAddress);
        layoutHostName = Utils.setTitle(view.findViewById(R.id.layoutHostName), R.string.hostName);
        layoutIpAddress = Utils.setTitle(view.findViewById(R.id.layoutIpAddress), R.string.ipAddress);
        layoutPort = Utils.setTitle(view.findViewById(R.id.layoutPort), R.string.port);
        layoutToAddresses = Utils.setTitle(view.findViewById(R.id.layoutToAddresses), R.string.toAddresses);
    }

    private void initViewClass(LayoutInflater inflater) {
        digitalFaxCallView = new DigitalFaxCallView(inflater, layoutDigitalFaxCalls);
        digitalSendInfoView = new DigitalSendInfoView(inflater, layoutDigitalSendInfo);
    }
}
