// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes;
import com.hp.workpath.api.statistics.jobinfo.faxinfo.IpFaxOutInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class FaxCallView {

    LinearLayout rootView;
    LayoutInflater inflater;

    CallView callView;

    ViewGroup layoutsIpTransport;
    ViewGroup layoutFaxNumber;
    ViewGroup layoutPortNumber;
    ViewGroup layoutProxyPortNumber;
    ViewGroup layoutProxyServer;
    ViewGroup layoutServer;

    LinearLayout layoutFaxCall;

    public FaxCallView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setFaxCall(IpFaxOutInfo.FaxCall[] faxCalls) {
        rootView.removeAllViews();
        if (faxCalls != null) {
            for (int index = 0; index < faxCalls.length; index++) {
                rootView.addView(setFaxCallInternal(index, faxCalls[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setFaxCallInternal(int index, IpFaxOutInfo.FaxCall faxCall) {
        View view = inflater.inflate(R.layout.layout_fax_call, rootView, false);
        initViewPrintAgentInfo(view);
        initViewClass(inflater);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (faxCall != null) {
            callView.setCall(faxCall.getFaxCall());
            setFaxConfiguration(faxCall.getIpFaxConfiguration());
        }
        return view;
    }

    public void setFaxConfiguration(FaxAttributes.FaxConfiguration faxConfiguration) {
        if (faxConfiguration != null) {
            setIpServer(faxConfiguration.getSipServer());
            Utils.setSummary(layoutsIpTransport, faxConfiguration.getSipTransport());
            Utils.setSummary(layoutFaxNumber, faxConfiguration.getFaxNumber());
        } else {
            LinearLayout parent = (LinearLayout) layoutsIpTransport.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setIpServer(FaxAttributes.IpServer ipServer) {
        if (ipServer != null) {
            Utils.setSummary(layoutPortNumber, ipServer.getPortNumber());
            Utils.setSummary(layoutProxyPortNumber, ipServer.getProxyPortNumber());
            Utils.setSummary(layoutProxyServer, ipServer.getProxyServer());
            Utils.setSummary(layoutServer, ipServer.getServer());
        } else {
            LinearLayout parent = (LinearLayout) layoutPortNumber.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewPrintAgentInfo(View view) {
        layoutFaxCall = Utils.getLayout(view.findViewById(R.id.layoutFaxCall), R.string.faxCall);
        ((TextView) view.findViewById(R.id.titleIpFaxConfigurationTextView)).setText(R.string.ipFaxConfiguration);
        ((TextView) view.findViewById(R.id.titleIpServerTextView)).setText(R.string.sIpServer);
        layoutsIpTransport = Utils.setTitle(view.findViewById(R.id.layoutsIpTransport), R.string.sIpTransport);
        layoutFaxNumber = Utils.setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber);
        layoutPortNumber = Utils.setTitle(view.findViewById(R.id.layoutPortNumber), R.string.portNumber);
        layoutProxyPortNumber = Utils.setTitle(view.findViewById(R.id.layoutProxyPortNumber), R.string.proxyPortNumber);
        layoutProxyServer = Utils.setTitle(view.findViewById(R.id.layoutProxyServer), R.string.proxyServer);
        layoutServer = Utils.setTitle(view.findViewById(R.id.layoutServer), R.string.server);
    }

    private void initViewClass(LayoutInflater inflater) {
        callView = new CallView(inflater, layoutFaxCall);
    }
}
