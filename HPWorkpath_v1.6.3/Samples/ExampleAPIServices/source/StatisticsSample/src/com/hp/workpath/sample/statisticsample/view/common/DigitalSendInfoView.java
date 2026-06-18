// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.StatisticsAttributes;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class DigitalSendInfoView {

    LinearLayout rootView;
    View view;
    FileInfoView fileInfoView;
    FileInfoView fileInfoMetadataView;

    ViewGroup layoutResult;
    ViewGroup layoutTotalDataSize;

    LinearLayout layoutFiles;
    LinearLayout layoutMetadataFile;

    public DigitalSendInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_digital_send_info, rootView, false);
        initViewDigitalSendInfo();
        initViewClass(inflater);
    }

    public void setDigitalSendInfo(StatisticsAttributes.DigitalSendInfo digitalSendInfo) {
        rootView.removeAllViews();
        if (digitalSendInfo != null) {
            setDeliveredFile(digitalSendInfo.getDeliveredFiles());
            Utils.setSummary(layoutResult, digitalSendInfo.getResult());
            Utils.setSummary(layoutTotalDataSize, digitalSendInfo.getTotalDataSize());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setDeliveredFile(StatisticsAttributes.DigitalSendInfo.DeliveredFile deliveredFile) {
        if (deliveredFile != null) {
            fileInfoView.setFileInfo(deliveredFile.getFiles());
            fileInfoMetadataView.setFileInfo(deliveredFile.getMetdataFile());
        } else {
            LinearLayout parent = (LinearLayout) view.findViewById(R.id.titleDeliveredFilesTextView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewDigitalSendInfo() {
        ((TextView) view.findViewById(R.id.titleDeliveredFilesTextView)).setText(R.string.deliveredFiles);
        layoutResult = Utils.setTitle(view.findViewById(R.id.layoutResult), R.string.result);
        layoutTotalDataSize = Utils.setTitle(view.findViewById(R.id.layoutTotalDataSize), R.string.totalDataSize);
        layoutFiles = Utils.getLayout(view.findViewById(R.id.layoutFiles), R.string.files);
        layoutMetadataFile = Utils.getLayout(view.findViewById(R.id.layoutMetadataFile), R.string.metadataFile);
    }

    private void initViewClass(LayoutInflater inflater) {
        fileInfoView = new FileInfoView(inflater, layoutFiles);
        fileInfoMetadataView = new FileInfoView(inflater, layoutMetadataFile);
    }
}
