// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxInInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class FaxInInfoView {

    LinearLayout rootView;
    View view;

    ViewGroup layoutStationId;
    ViewGroup layoutTotalImagesReceived;
    ViewGroup layoutName;
    ViewGroup layoutNumber;

    public FaxInInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_fax_in_info, rootView, false);
        initViewFaxInInfo();
    }

    public void setFaxInInfo(FaxInInfo faxInInfo) {
        rootView.removeAllViews();
        if (faxInInfo != null) {
            setId(faxInInfo.getCallerId());
            Utils.setSummary(layoutStationId, faxInInfo.getStationId());
            Utils.setSummary(layoutTotalImagesReceived, faxInInfo.getTotalImagesReceived());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setId(FaxInInfo.Id id) {
        if (id != null) {
            Utils.setSummary(layoutName, id.getName());
            Utils.setSummary(layoutNumber, id.getNumber());
        } else {
            LinearLayout parent = (LinearLayout) layoutName.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewFaxInInfo() {
        ((TextView) view.findViewById(R.id.titleCallerIdTextView)).setText(R.string.callerId);
        layoutStationId = Utils.setTitle(view.findViewById(R.id.layoutStationId), R.string.stationId);
        layoutTotalImagesReceived = Utils.setTitle(view.findViewById(R.id.layoutTotalImagesReceived), R.string.totalImagesReceived);
        layoutName = Utils.setTitle(view.findViewById(R.id.layoutName), R.string.name);
        layoutNumber = Utils.setTitle(view.findViewById(R.id.layoutNumber), R.string.number);
    }
}
