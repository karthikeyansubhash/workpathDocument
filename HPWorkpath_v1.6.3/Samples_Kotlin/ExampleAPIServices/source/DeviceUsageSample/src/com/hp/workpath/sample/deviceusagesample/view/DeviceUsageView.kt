// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.hp.workpath.api.deviceusage.DeviceUsageInfo
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.PrinterInfoView
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.ScannerInfoView

class DeviceUsageView(inflater: LayoutInflater, var view: View) {
    private lateinit var printerInfoView: PrinterInfoView
    private lateinit var scannerInfoView: ScannerInfoView
    private lateinit var layoutPrinterInfo: LinearLayout
    private lateinit var layoutScannerInfo: LinearLayout

    fun setDeviceUsageInfo(deviceUsageInfo: DeviceUsageInfo) {
        printerInfoView.setPrinterInfo(deviceUsageInfo.printer)
        scannerInfoView.setScannerInfo(deviceUsageInfo.scanner)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        printerInfoView = PrinterInfoView(inflater, layoutPrinterInfo)
        scannerInfoView = ScannerInfoView(inflater, layoutScannerInfo)
    }

    private fun initView() {
        layoutPrinterInfo = view.findViewById(R.id.layoutPrinterInfo)
        layoutScannerInfo = view.findViewById(R.id.layoutScannerInfo)
    }

    init {
        initView()
        initViewClass(inflater)
    }
}