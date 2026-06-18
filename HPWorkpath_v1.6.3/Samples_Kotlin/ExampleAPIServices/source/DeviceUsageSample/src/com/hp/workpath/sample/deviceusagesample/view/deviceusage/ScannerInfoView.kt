// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.deviceusage.scanner.ScannerInfo
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.Utils.getLayout
import com.hp.workpath.sample.deviceusagesample.view.Utils.setSummary
import com.hp.workpath.sample.deviceusagesample.view.Utils.setTitle
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.ByJobCategoryView
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.ByMediaSizeView
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.PlexView

class ScannerInfoView(inflater: LayoutInflater, private var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_scanner_info, rootView, false)
    private lateinit var plexView: PlexView
    private lateinit var byJobCategoryViewView: ByJobCategoryView
    private lateinit var byMediaSizeView: ByMediaSizeView
    private lateinit var layoutSheets: ViewGroup
    private lateinit var layoutEngineCycles: ViewGroup
    private lateinit var layoutPlex: LinearLayout
    private lateinit var layoutByJobCategory: LinearLayout
    private lateinit var layoutByMediaSize: LinearLayout

    fun setScannerInfo(scannerInfo: ScannerInfo?) {
        rootView.removeAllViews()
        if (scannerInfo != null) {
            setSummary(layoutSheets, scannerInfo.sheets)
            setSummary(layoutEngineCycles, scannerInfo.engineCycles)
            plexView.setPlex(scannerInfo.byScanPlex)
            byJobCategoryViewView.setByJobCategory(scannerInfo.byJobCategory)
            byMediaSizeView.setByMediaSize(scannerInfo.byMediaSize)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewScannerInfo() {
        layoutSheets = setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets)
        layoutEngineCycles = setTitle(view.findViewById(R.id.layoutEngineCycles), R.string.engineCycles)
        layoutPlex = getLayout(view.findViewById(R.id.layoutByScanPlex), R.string.byPrintPlex)
        layoutByJobCategory = getLayout(view.findViewById(R.id.layoutByJobCategory), R.string.byJobCategory)
        layoutByMediaSize = getLayout(view.findViewById(R.id.layoutByMediaSize), R.string.byMediaSize)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        plexView = PlexView(inflater, layoutPlex)
        byJobCategoryViewView = ByJobCategoryView(inflater, layoutByJobCategory)
        byMediaSizeView = ByMediaSizeView(inflater, layoutByMediaSize)
    }

    init {
        initViewScannerInfo()
        initViewClass(inflater)
    }
}