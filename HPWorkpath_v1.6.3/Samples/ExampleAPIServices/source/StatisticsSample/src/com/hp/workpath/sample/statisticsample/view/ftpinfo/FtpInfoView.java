// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.ftpinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.ftpinfo.FtpInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView;

public class FtpInfoView {

    LinearLayout rootView;
    LayoutInflater inflater;
    DigitalSendInfoView digitalSendInfoView;

    LinearLayout layoutDigitalSendInfo;
    ViewGroup layoutDirectoryPath;
    ViewGroup layoutHostName;
    ViewGroup layoutIpAddress;
    ViewGroup layoutPort;
    ViewGroup layoutUserName;

    public FtpInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setFtpInfo(FtpInfo[] ftpInfo) {
        rootView.removeAllViews();
        if (ftpInfo != null) {
            for (int index = 0; index < ftpInfo.length; index++) {
                rootView.addView(setFtpInfoInternal(index, ftpInfo[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setFtpInfoInternal(int index, FtpInfo ftpInfo) {
        View view = inflater.inflate(R.layout.layout_ftp_info, rootView, false);
        initViewFtpInfo(view);
        initViewClass(inflater);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (ftpInfo != null) {
            digitalSendInfoView.setDigitalSendInfo(ftpInfo.getDigitalSendInfo());
            Utils.setSummary(layoutDirectoryPath, ftpInfo.getDirectoryPath());
            Utils.setSummary(layoutHostName, ftpInfo.getHostName());
            Utils.setSummary(layoutIpAddress, ftpInfo.getIpAddress());
            Utils.setSummary(layoutPort, ftpInfo.getPort());
            Utils.setSummary(layoutUserName, ftpInfo.getUserName());
        }
        return view;
    }

    private void initViewFtpInfo(View view) {
        layoutDigitalSendInfo = Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo);
        layoutDirectoryPath = Utils.setTitle(view.findViewById(R.id.layoutDirectoryPath), R.string.directoryPath);
        layoutHostName = Utils.setTitle(view.findViewById(R.id.layoutHostName), R.string.hostName);
        layoutIpAddress = Utils.setTitle(view.findViewById(R.id.layoutIpAddress), R.string.ipAddress);
        layoutPort = Utils.setTitle(view.findViewById(R.id.layoutPort), R.string.port);
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName);
    }

    private void initViewClass(LayoutInflater inflater) {
        digitalSendInfoView = new DigitalSendInfoView(inflater, layoutDigitalSendInfo);
    }
}
