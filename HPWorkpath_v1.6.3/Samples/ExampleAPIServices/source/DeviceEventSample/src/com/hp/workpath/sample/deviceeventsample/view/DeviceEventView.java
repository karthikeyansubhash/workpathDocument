// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.sample.deviceeventsample.R;
import com.hp.workpath.sample.deviceeventsample.view.deviceevent.TimeStampView;

public class DeviceEventView {

    View view;

    ViewGroup layoutInstanceId;
    ViewGroup layoutSeverity;
    ViewGroup layoutStateChangeType;
    ViewGroup layoutTitle;
    LinearLayout layoutTimestamp;
    ViewGroup layoutSupportInformationLink;
    ViewGroup layoutCategory;
    ViewGroup layoutDetails;
    ViewGroup layoutEventCode;

    TimeStampView timeStampView;

    public DeviceEventView(LayoutInflater inflater, View view) {
        this.view = view;
        initView();
        initViewClass(inflater);
    }

    public void setDeviceEventData(DeviceEvent deviceEvent) {
        if (deviceEvent != null) {
            Utils.setSummary(layoutInstanceId, deviceEvent.getInstanceId());
            Utils.setSummary(layoutSeverity, deviceEvent.getSeverity());
            Utils.setSummary(layoutStateChangeType, deviceEvent.getStateChangeType());
            Utils.setSummary(layoutTitle, deviceEvent.getTitle());
            timeStampView.setTimeStamp(deviceEvent.getTimestamp());
            Utils.setSummary(layoutSupportInformationLink, deviceEvent.getSupportInformationLink());
            Utils.setSummary(layoutCategory, deviceEvent.getCategory());
            Utils.setSummary(layoutDetails, deviceEvent.getDetails());
            Utils.setSummary(layoutEventCode, deviceEvent.getEventCode());
        }
    }

    private void initViewClass(LayoutInflater inflater) {
        timeStampView = new TimeStampView(inflater, layoutTimestamp);
    }

    private void initView() {
        layoutInstanceId = Utils.setTitle(view.findViewById(R.id.layoutInstanceId), R.string.instanceId);
        layoutSeverity = Utils.setTitle(view.findViewById(R.id.layoutSeverity), R.string.serverity);
        layoutStateChangeType = Utils.setTitle(view.findViewById(R.id.layoutStateChangeType), R.string.stateChangeType);
        layoutTitle = Utils.setTitle(view.findViewById(R.id.layoutTitle), R.string.title);
        layoutTimestamp = Utils.getLayout(view.findViewById(R.id.layoutTimestamp), R.string.timestamp);
        layoutSupportInformationLink = Utils.setTitle(view.findViewById(R.id.layoutSupportInformationLink), R.string.supportInformationLink);
        layoutCategory = Utils.setTitle(view.findViewById(R.id.layoutCategory), R.string.category);
        layoutDetails = Utils.setTitle(view.findViewById(R.id.layoutDetails), R.string.details);
        layoutEventCode = Utils.setTitle(view.findViewById(R.id.layoutEventCode), R.string.eventCode);
    }
}
