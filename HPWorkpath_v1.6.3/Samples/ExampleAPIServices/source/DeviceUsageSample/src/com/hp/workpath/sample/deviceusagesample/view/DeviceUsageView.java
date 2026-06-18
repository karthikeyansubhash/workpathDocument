// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.DeviceUsageInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.PrinterInfoView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.ScannerInfoView;

public class DeviceUsageView {

    View view;
    PrinterInfoView printerInfoView;
    ScannerInfoView scannerInfoView;

    LinearLayout layoutPrinterInfo;
    LinearLayout layoutScannerInfo;

    public DeviceUsageView(LayoutInflater inflater, View view) {
        this.view = view;
        initView();
        initViewClass(inflater);
    }

    public void setDeviceUsageInfo(DeviceUsageInfo deviceUsageInfo) {
        printerInfoView.setPrinterInfo(deviceUsageInfo.getPrinter());
        scannerInfoView.setScannerInfo(deviceUsageInfo.getScanner());
    }

    private void initViewClass(LayoutInflater inflater) {
        printerInfoView = new PrinterInfoView(inflater, layoutPrinterInfo);
        scannerInfoView = new ScannerInfoView(inflater, layoutScannerInfo);
    }


    private void initView() {
        layoutPrinterInfo = view.findViewById(R.id.layoutPrinterInfo);
        layoutScannerInfo = view.findViewById(R.id.layoutScannerInfo);
    }

}
