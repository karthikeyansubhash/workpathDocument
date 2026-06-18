// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.driverinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.driverinfo.DriverInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class DriverInfoView {

    LinearLayout rootView;
    View view;

    ViewGroup layoutApplicationName;
    ViewGroup layoutClientHostName;
    ViewGroup layoutFileName;
    ViewGroup layoutJobAcct13;
    ViewGroup layoutJobAcct14;
    ViewGroup layoutJobAcct15;
    ViewGroup layoutJobAcct16;
    ViewGroup layoutJobId;
    ViewGroup layoutUserDomain;
    ViewGroup layoutUserName;

    public DriverInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_driver_info, rootView, false);
        initViewDriverInfo();
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        rootView.removeAllViews();
        if (driverInfo != null) {
            Utils.setSummary(layoutApplicationName, driverInfo.getApplicationName());
            Utils.setSummary(layoutClientHostName, driverInfo.getClientHostName());
            Utils.setSummary(layoutFileName, driverInfo.getFileName());
            Utils.setSummary(layoutJobAcct13, driverInfo.getJobAcct13());
            Utils.setSummary(layoutJobAcct14, driverInfo.getJobAcct14());
            Utils.setSummary(layoutJobAcct15, driverInfo.getJobAcct15());
            Utils.setSummary(layoutJobAcct16, driverInfo.getJobAcct16());
            Utils.setSummary(layoutJobId, driverInfo.getJobId());
            Utils.setSummary(layoutUserDomain, driverInfo.getUserDomain());
            Utils.setSummary(layoutUserName, driverInfo.getUserName());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewDriverInfo() {
        layoutApplicationName = Utils.setTitle(view.findViewById(R.id.layoutApplicationName), R.string.applicationName);
        layoutClientHostName = Utils.setTitle(view.findViewById(R.id.layoutClientHostName), R.string.clientHostName);
        layoutFileName = Utils.setTitle(view.findViewById(R.id.layoutFileName), R.string.fileName);
        layoutJobAcct13 = Utils.setTitle(view.findViewById(R.id.layoutJobAcct13), R.string.jobAcct13);
        layoutJobAcct14 = Utils.setTitle(view.findViewById(R.id.layoutJobAcct14), R.string.jobAcct14);
        layoutJobAcct15 = Utils.setTitle(view.findViewById(R.id.layoutJobAcct15), R.string.jobAcct15);
        layoutJobAcct16 = Utils.setTitle(view.findViewById(R.id.layoutJobAcct16), R.string.jobAcct16);
        layoutJobId = Utils.setTitle(view.findViewById(R.id.layoutJobId), R.string.jobId);
        layoutUserDomain = Utils.setTitle(view.findViewById(R.id.layoutUserDomain), R.string.userDomain);
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName);
    }
}
