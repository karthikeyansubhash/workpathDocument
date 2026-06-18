// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.statistics.StatisticsJobData
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.getLayout
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle
import com.hp.workpath.sample.statisticsample.view.driverinfo.DriverInfoView
import com.hp.workpath.sample.statisticsample.view.emailinfo.EmailInfoView
import com.hp.workpath.sample.statisticsample.view.extendeduserinfo.ExtendedUserInfoView
import com.hp.workpath.sample.statisticsample.view.faxinfo.FaxInInfoView
import com.hp.workpath.sample.statisticsample.view.faxinfo.FaxOutInfoView
import com.hp.workpath.sample.statisticsample.view.faxinfo.IpFaxOutInfoView
import com.hp.workpath.sample.statisticsample.view.folderinfo.FolderInfoView
import com.hp.workpath.sample.statisticsample.view.ftpinfo.FtpInfoView
import com.hp.workpath.sample.statisticsample.view.httpinfo.HttpInfoView
import com.hp.workpath.sample.statisticsample.view.jobinfo.StatisticsJobInfoView
import com.hp.workpath.sample.statisticsample.view.printinfo.PrintInfoView
import com.hp.workpath.sample.statisticsample.view.scaninfo.ScanInfoView

class StatisticJobInfoView(inflater: LayoutInflater, var rootView: View) {
    var view: View = rootView

    private lateinit var statisticsJobInfoView: StatisticsJobInfoView
    private lateinit var scanInfoView: ScanInfoView
    private lateinit var printInfoView: PrintInfoView
    private lateinit var extendedUserInfoView: ExtendedUserInfoView
    private lateinit var emailInfoView: EmailInfoView
    private lateinit var driverInfoView: DriverInfoView
    private lateinit var faxInInfoView: FaxInInfoView
    private lateinit var faxOutInfoView: FaxOutInfoView
    private lateinit var ipFaxInInfoView: FaxInInfoView
    private lateinit var ipFaxOutInfoView: IpFaxOutInfoView
    private lateinit var folderInfoView: FolderInfoView
    private lateinit var ftpInfoView: FtpInfoView
    private lateinit var httpInfoView: HttpInfoView

    fun setStatisticJobData(statisticJobData: StatisticsJobData) {
        setSummary(layoutJobSequence, statisticJobData.getJobSequence())
        setSummary(layoutJobId, statisticJobData.jobId)
        setSummary(layoutResourceId, statisticJobData.resourceId)
        statisticsJobInfoView.setStatisticsJobInfo(statisticJobData.jobInfo)
        scanInfoView.setScanInfo(statisticJobData.scanInfo)
        printInfoView.setPrintInfo(statisticJobData.printInfo)
        extendedUserInfoView.setExtendedUserInfo(statisticJobData.extendedUserInfo)
        emailInfoView.setEmailInfo(statisticJobData.emailInfo)
        driverInfoView.setDriverInfo(statisticJobData.driverInfo)
        faxInInfoView.setFaxInInfo(statisticJobData.faxInInfo)
        faxOutInfoView.setFaxOutInfo(statisticJobData.faxOutInfo)
        ipFaxInInfoView.setFaxInInfo(statisticJobData.ipFaxInInfo)
        ipFaxOutInfoView.setFaxOutInfo(statisticJobData.ipFaxOutInfo)
        folderInfoView.setFolderInfo(statisticJobData.folderInfo)
        ftpInfoView.setFtpInfo(statisticJobData.ftpInfo)
        httpInfoView.setHttpInfo(statisticJobData.httpInfo)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        statisticsJobInfoView = StatisticsJobInfoView(inflater, layoutJobInfo)
        scanInfoView = ScanInfoView(inflater, layoutScanInfo)
        printInfoView = PrintInfoView(inflater, layoutPrintInfo)
        extendedUserInfoView = ExtendedUserInfoView(inflater, layoutExtendedUserInfo)
        emailInfoView = EmailInfoView(inflater, layoutEmailInfo)
        driverInfoView = DriverInfoView(inflater, layoutDriverInfo)
        faxInInfoView = FaxInInfoView(inflater, layoutFaxInInfo)
        faxOutInfoView = FaxOutInfoView(inflater, layoutFaxOutInfo)
        ipFaxInInfoView = FaxInInfoView(inflater, layoutIpFaxInInfo)
        ipFaxOutInfoView = IpFaxOutInfoView(inflater, layoutIpFaxOutInfo)
        folderInfoView = FolderInfoView(inflater, layoutFolderInfo)
        ftpInfoView = FtpInfoView(inflater, layoutFtpInfo)
        httpInfoView = HttpInfoView(inflater, layoutHttpInfo)
    }

    private lateinit var layoutJobSequence: ViewGroup
    private lateinit var layoutJobId: ViewGroup
    private lateinit var layoutResourceId: ViewGroup
    private lateinit var layoutJobInfo: LinearLayout
    private lateinit var layoutScanInfo: LinearLayout
    private lateinit var layoutPrintInfo: LinearLayout
    private lateinit var layoutExtendedUserInfo: LinearLayout
    private lateinit var layoutEmailInfo: LinearLayout
    private lateinit var layoutDriverInfo: LinearLayout
    private lateinit var layoutFaxInInfo: LinearLayout
    private lateinit var layoutFaxOutInfo: LinearLayout
    private lateinit var layoutIpFaxInInfo: LinearLayout
    private lateinit var layoutIpFaxOutInfo: LinearLayout
    private lateinit var layoutFolderInfo: LinearLayout
    private lateinit var layoutFtpInfo: LinearLayout
    private lateinit var layoutHttpInfo: LinearLayout

    private fun initView() {
        layoutJobSequence = setTitle(view.findViewById(R.id.layoutJobSequence), R.string.jobSequence)
        layoutJobId = setTitle(view.findViewById(R.id.layoutJobId), R.string.jobId)
        layoutResourceId = setTitle(view.findViewById(R.id.layoutResourceId), R.string.resourceId)
        layoutJobInfo = getLayout(view.findViewById(R.id.layoutJobInfo), R.string.jobInfo)
        layoutScanInfo = getLayout(view.findViewById(R.id.layoutScanInfo), R.string.scanInfo)
        layoutPrintInfo = getLayout(view.findViewById(R.id.layoutPrintInfo), R.string.printInfo)
        layoutExtendedUserInfo =
            getLayout(view.findViewById(R.id.layoutExtendedUserInfo), R.string.extendedUserInfo)
        layoutEmailInfo = getLayout(view.findViewById(R.id.layoutEmailInfo), R.string.emailInfo)
        layoutDriverInfo = getLayout(view.findViewById(R.id.layoutDriverInfo), R.string.driverInfo)
        layoutFaxInInfo = getLayout(view.findViewById(R.id.layoutAFaxInInfo), R.string.faxInInfo)
        layoutFaxOutInfo = getLayout(view.findViewById(R.id.layoutAFaxOutInfo), R.string.faxOutInfo)
        layoutIpFaxInInfo =
            getLayout(view.findViewById(R.id.layoutIpFaxInInfo), R.string.ipFaxInInfo)
        layoutIpFaxOutInfo =
            getLayout(view.findViewById(R.id.layoutIpFaxOutInfo), R.string.ipFaxOutInfo)
        layoutFolderInfo = getLayout(view.findViewById(R.id.layoutFolderInfo), R.string.folderInfo)
        layoutFtpInfo = getLayout(view.findViewById(R.id.layoutFtpInfo), R.string.ftpInfo)
        layoutHttpInfo = getLayout(view.findViewById(R.id.layoutHttpInfo), R.string.httpInfo)
    }

    init {
        initView()
        initViewClass(inflater)
    }
}