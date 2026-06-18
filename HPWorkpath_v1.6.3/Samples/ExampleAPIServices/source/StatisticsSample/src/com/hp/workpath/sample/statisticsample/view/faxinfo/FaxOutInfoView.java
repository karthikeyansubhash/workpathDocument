// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes;
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxOutInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class FaxOutInfoView {

    LinearLayout rootView;
    View view;
    CallView callView;

    LinearLayout layoutFaxCalls;

    public FaxOutInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_fax_out_info, rootView, false);
        initViewFaxOutInfo();
        initViewClass(inflater);
    }

    public void setFaxOutInfo(FaxOutInfo faxOutInfo) {
        rootView.removeAllViews();
        if (faxOutInfo != null) {
            for (FaxAttributes.Call call : faxOutInfo.getFaxCalls()) {
                callView.setCall(call);
            }
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewFaxOutInfo() {
        layoutFaxCalls = Utils.getLayout(view.findViewById(R.id.layoutFaxCalls), R.string.faxCalls);
    }

    private void initViewClass(LayoutInflater inflater) {
        callView = new CallView(inflater, layoutFaxCalls);
    }
}
