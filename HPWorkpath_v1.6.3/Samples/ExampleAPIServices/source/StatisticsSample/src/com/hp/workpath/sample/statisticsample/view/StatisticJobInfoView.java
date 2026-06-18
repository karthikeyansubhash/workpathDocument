// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.driverinfo.DriverInfoView;
import com.hp.workpath.sample.statisticsample.view.emailinfo.EmailInfoView;
import com.hp.workpath.sample.statisticsample.view.extendeduserinfo.ExtendedUserInfoView;
import com.hp.workpath.sample.statisticsample.view.faxinfo.FaxInInfoView;
import com.hp.workpath.sample.statisticsample.view.faxinfo.FaxOutInfoView;
import com.hp.workpath.sample.statisticsample.view.folderinfo.FolderInfoView;
import com.hp.workpath.sample.statisticsample.view.ftpinfo.FtpInfoView;
import com.hp.workpath.sample.statisticsample.view.httpinfo.HttpInfoView;
import com.hp.workpath.sample.statisticsample.view.faxinfo.IpFaxOutInfoView;
import com.hp.workpath.sample.statisticsample.view.printinfo.PrintInfoView;
import com.hp.workpath.sample.statisticsample.view.scaninfo.ScanInfoView;
import com.hp.workpath.sample.statisticsample.view.jobinfo.StatisticsJobInfoView;

public class StatisticJobInfoView {

    View view;
    StatisticsJobInfoView statisticsJobInfoView;
    ScanInfoView scanInfoView;
    PrintInfoView printInfoView;
    ExtendedUserInfoView extendedUserInfoView;
    EmailInfoView emailInfoView;
    DriverInfoView driverInfoView;
    FaxInInfoView faxInInfoView;
    FaxOutInfoView faxOutInfoView;
    FaxInInfoView ipFaxInInfoView;
    IpFaxOutInfoView ipFaxOutInfoView;
    FolderInfoView folderInfoView;
    FtpInfoView ftpInfoView;
    HttpInfoView httpInfoView;

    public StatisticJobInfoView(LayoutInflater inflater, View view) {
        this.view = view;
        initView();
        initViewClass(inflater);
    }

    public void setStatisticJobData(StatisticsJobData statisticJobData) {
        Utils.setSummary(layoutJobSequence, statisticJobData.getJobSequence());
        Utils.setSummary(layoutJobId, statisticJobData.getJobId());
        Utils.setSummary(layoutResourceId, statisticJobData.getResourceId());
        statisticsJobInfoView.setStatisticsJobInfo(statisticJobData.getJobInfo());
        scanInfoView.setScanInfo(statisticJobData.getScanInfo());
        printInfoView.setPrintInfo(statisticJobData.getPrintInfo());
        extendedUserInfoView.setExtendedUserInfo(statisticJobData.getExtendedUserInfo());
        emailInfoView.setEmailInfo(statisticJobData.getEmailInfo());
        driverInfoView.setDriverInfo(statisticJobData.getDriverInfo());
        faxInInfoView.setFaxInInfo(statisticJobData.getFaxInInfo());
        faxOutInfoView.setFaxOutInfo(statisticJobData.getFaxOutInfo());
        ipFaxInInfoView.setFaxInInfo(statisticJobData.getIpFaxInInfo());
        ipFaxOutInfoView.setFaxOutInfo(statisticJobData.getIpFaxOutInfo());
        folderInfoView.setFolderInfo(statisticJobData.getFolderInfo());
        ftpInfoView.setFtpInfo(statisticJobData.getFtpInfo());
        httpInfoView.setHttpInfo(statisticJobData.getHttpInfo());
    }

    private void initViewClass(LayoutInflater inflater) {
        statisticsJobInfoView = new StatisticsJobInfoView(inflater, layoutJobInfo);
        scanInfoView = new ScanInfoView(inflater, layoutScanInfo);
        printInfoView = new PrintInfoView(inflater, layoutPrintInfo);
        extendedUserInfoView = new ExtendedUserInfoView(inflater, layoutExtendedUserInfo);
        emailInfoView = new EmailInfoView(inflater, layoutEmailInfo);
        driverInfoView = new DriverInfoView(inflater, layoutDriverInfo);
        faxInInfoView = new FaxInInfoView(inflater, layoutFaxInInfo);
        faxOutInfoView = new FaxOutInfoView(inflater, layoutFaxOutInfo);
        ipFaxInInfoView = new FaxInInfoView(inflater, layoutIpFaxInInfo);
        ipFaxOutInfoView = new IpFaxOutInfoView(inflater, layoutIpFaxOutInfo);
        folderInfoView = new FolderInfoView(inflater, layoutFolderInfo);
        ftpInfoView = new FtpInfoView(inflater, layoutFtpInfo);
        httpInfoView = new HttpInfoView(inflater, layoutHttpInfo);
    }

    ViewGroup layoutJobSequence;
    ViewGroup layoutJobId;
    ViewGroup layoutResourceId;
    LinearLayout layoutJobInfo;
    LinearLayout layoutScanInfo;
    LinearLayout layoutPrintInfo;
    LinearLayout layoutExtendedUserInfo;
    LinearLayout layoutEmailInfo;
    LinearLayout layoutDriverInfo;
    LinearLayout layoutFaxInInfo;
    LinearLayout layoutFaxOutInfo;
    LinearLayout layoutIpFaxInInfo;
    LinearLayout layoutIpFaxOutInfo;
    LinearLayout layoutFolderInfo;
    LinearLayout layoutFtpInfo;
    LinearLayout layoutHttpInfo;

    private void initView() {
        layoutJobSequence = Utils.setTitle(view.findViewById(R.id.layoutJobSequence), R.string.jobSequence);
        layoutJobId = Utils.setTitle(view.findViewById(R.id.layoutJobId), R.string.jobId);
        layoutResourceId = Utils.setTitle(view.findViewById(R.id.layoutResourceId), R.string.resourceId);
        layoutJobInfo= Utils.getLayout(view.findViewById(R.id.layoutJobInfo), R.string.jobInfo);
        layoutScanInfo = Utils.getLayout(view.findViewById(R.id.layoutScanInfo), R.string.scanInfo);
        layoutPrintInfo = Utils.getLayout(view.findViewById(R.id.layoutPrintInfo), R.string.printInfo);
        layoutExtendedUserInfo = Utils.getLayout(view.findViewById(R.id.layoutExtendedUserInfo), R.string.extendedUserInfo);
        layoutEmailInfo = Utils.getLayout(view.findViewById(R.id.layoutEmailInfo), R.string.emailInfo);
        layoutDriverInfo = Utils.getLayout(view.findViewById(R.id.layoutDriverInfo), R.string.driverInfo);
        layoutFaxInInfo = Utils.getLayout(view.findViewById(R.id.layoutAFaxInInfo), R.string.faxInInfo);
        layoutFaxOutInfo = Utils.getLayout(view.findViewById(R.id.layoutAFaxOutInfo), R.string.faxOutInfo);
        layoutIpFaxInInfo = Utils.getLayout(view.findViewById(R.id.layoutIpFaxInInfo), R.string.ipFaxInInfo);
        layoutIpFaxOutInfo = Utils.getLayout(view.findViewById(R.id.layoutIpFaxOutInfo), R.string.ipFaxOutInfo);
        layoutFolderInfo = Utils.getLayout(view.findViewById(R.id.layoutFolderInfo), R.string.folderInfo);
        layoutFtpInfo = Utils.getLayout(view.findViewById(R.id.layoutFtpInfo), R.string.ftpInfo);
        layoutHttpInfo = Utils.getLayout(view.findViewById(R.id.layoutHttpInfo), R.string.httpInfo);
    }
}
