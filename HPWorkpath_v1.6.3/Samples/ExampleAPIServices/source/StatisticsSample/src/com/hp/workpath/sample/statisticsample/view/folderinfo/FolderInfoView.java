// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.folderinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.folderinfo.FolderInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;
import com.hp.workpath.sample.statisticsample.view.common.DigitalFaxCallView;
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView;

public class FolderInfoView {

    LinearLayout rootView;
    LayoutInflater inflater;

    DigitalFaxCallView digitalFaxCallView;
    DigitalSendInfoView digitalSendInfoView;

    LinearLayout layoutDigitalFaxCalls;
    LinearLayout layoutDigitalSendInfo;
    ViewGroup layoutUncPath;
    ViewGroup layoutUserName;

    public FolderInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setFolderInfo(FolderInfo[] folderInfo) {
        rootView.removeAllViews();
        if (folderInfo != null) {
            for (int index = 0; index < folderInfo.length; index++) {
                rootView.addView(setFolderInfoInternal(index, folderInfo[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setFolderInfoInternal(int index, FolderInfo folderInfo) {
        View view = inflater.inflate(R.layout.layout_folder_info, rootView, false);
        initViewFolderInfo(view);
        initViewClass(inflater);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (folderInfo != null) {
            digitalFaxCallView.setDigitalFaxCall(folderInfo.getDigitalFaxCalls());
            digitalSendInfoView.setDigitalSendInfo(folderInfo.getDigitalSendInfo());
            Utils.setSummary(layoutUncPath, folderInfo.getUncPath());
            Utils.setSummary(layoutUserName, folderInfo.getUserName());
        }
        return view;
    }

    private void initViewFolderInfo(View view) {
        layoutDigitalFaxCalls = Utils.getLayout(view.findViewById(R.id.layoutDigitalFaxCalls), R.string.digitalFaxCalls);
        layoutDigitalSendInfo = Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo);
        layoutUncPath = Utils.setTitle(view.findViewById(R.id.layoutUncPath), R.string.uncPath);
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName);
    }

    private void initViewClass(LayoutInflater inflater) {
        digitalFaxCallView = new DigitalFaxCallView(inflater, layoutDigitalFaxCalls);
        digitalSendInfoView = new DigitalSendInfoView(inflater, layoutDigitalSendInfo);
    }
}
