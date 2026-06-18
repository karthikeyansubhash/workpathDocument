// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.faxinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.faxinfo.IpFaxOutInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class IpFaxOutInfoView {

    LinearLayout rootView;
    View view;
    FaxCallView faxCallView;

    LinearLayout layoutIpFaxCalls;

    public IpFaxOutInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_ip_fax_out_info, rootView, false);
        initViewIpFaxOutInfo();
        initViewClass(inflater);
    }

    public void setFaxOutInfo(IpFaxOutInfo faxOutInfo) {
        rootView.removeAllViews();
        if (faxOutInfo != null) {
            faxCallView.setFaxCall(faxOutInfo.getIpFaxCalls());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewIpFaxOutInfo() {
        layoutIpFaxCalls = Utils.getLayout(view.findViewById(R.id.layoutIpFaxCalls), R.string.ipFaxCalls);
    }

    private void initViewClass(LayoutInflater inflater) {
        faxCallView = new FaxCallView(inflater, layoutIpFaxCalls);
    }
}
