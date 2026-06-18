// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class CallView {

    LinearLayout rootView;
    View view;

    ViewGroup layoutBillingCode;
    ViewGroup layoutDuration;
    ViewGroup layoutFaxNumber;
    ViewGroup layoutFaxResult;

    public CallView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_call, rootView, false);
        initViewCall();
    }

    public void setCall(FaxAttributes.Call call) {
        rootView.removeAllViews();
        if (call != null) {
            Utils.setSummary(layoutBillingCode, call.getBillingCode());
            Utils.setSummary(layoutDuration, call.getDuration());
            Utils.setSummary(layoutFaxNumber, call.getFaxNumber());
            Utils.setSummary(layoutFaxResult, call.getFaxResult());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewCall() {
        layoutBillingCode = Utils.setTitle(view.findViewById(R.id.layoutBillingCode), R.string.applicationName);
        layoutDuration = Utils.setTitle(view.findViewById(R.id.layoutDuration), R.string.duration);
        layoutFaxNumber = Utils.setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber);
        layoutFaxResult = Utils.setTitle(view.findViewById(R.id.layoutFaxResult), R.string.faxResult);
    }
}
