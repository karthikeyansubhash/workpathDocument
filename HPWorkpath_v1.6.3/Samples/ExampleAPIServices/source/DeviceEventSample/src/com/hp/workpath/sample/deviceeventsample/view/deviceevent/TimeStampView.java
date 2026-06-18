// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view.deviceevent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.device.events.Timestamp;
import com.hp.workpath.sample.deviceeventsample.R;
import com.hp.workpath.sample.deviceeventsample.view.Utils;

public class TimeStampView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutOffset;
    ViewGroup layoutTime;

    public TimeStampView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        rootView.removeAllViews();
        if (timeStamp != null) {
            rootView.addView(setTimeStampInternal(timeStamp));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setTimeStampInternal(Timestamp timeStamp) {
        View view = inflater.inflate(R.layout.layout_time_stamp, rootView, false);
        initViewTimeStamp(view);
        if (timeStamp != null) {
            Utils.setSummary(layoutOffset, timeStamp.getOffset());
            Utils.setSummary(layoutTime, timeStamp.getTime());
        }
        return view;
    }

    private void initViewTimeStamp(View view) {
        layoutOffset = Utils.setTitle(view.findViewById(R.id.layoutOffset), R.string.offset);
        layoutTime = Utils.setTitle(view.findViewById(R.id.layoutTime), R.string.time);
    }
}
