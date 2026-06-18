// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.httpinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.httpinfo.HttpInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView;

public class HttpInfoView {

    LinearLayout rootView;
    LayoutInflater inflater;
    DigitalSendInfoView digitalSendInfoView;

    LinearLayout layoutDigitalSendInfo;
    ViewGroup layoutUri;
    ViewGroup layoutUserName;

    public HttpInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setHttpInfo(HttpInfo[] httpInfo) {
        rootView.removeAllViews();
        if (httpInfo != null) {
            for (int index = 0; index < httpInfo.length; index++) {
                rootView.addView(setHttpInfoInternal(index, httpInfo[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setHttpInfoInternal(int index, HttpInfo httpInfo) {
        View view = inflater.inflate(R.layout.layout_http_info, rootView, false);
        initViewHttpInfo(view);
        initViewClass(inflater);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (httpInfo != null) {
            digitalSendInfoView.setDigitalSendInfo(httpInfo.getDigitalSendInfo());
            Utils.setSummary(layoutUri, httpInfo.getUri());
            Utils.setSummary(layoutUserName, httpInfo.getUserName());
        }
        return view;
    }

    private void initViewHttpInfo(View view) {
        layoutDigitalSendInfo = Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo);
        layoutUri = Utils.setTitle(view.findViewById(R.id.layoutUri), R.string.uri);
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName);
    }

    private void initViewClass(LayoutInflater inflater) {
        digitalSendInfoView = new DigitalSendInfoView(inflater, layoutDigitalSendInfo);
    }
}
