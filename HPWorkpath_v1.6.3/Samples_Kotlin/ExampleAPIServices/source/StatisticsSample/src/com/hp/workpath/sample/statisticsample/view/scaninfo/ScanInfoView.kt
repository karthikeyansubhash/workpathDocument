// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.scaninfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.scan.ScanInfo
import com.hp.workpath.api.statistics.jobinfo.scan.ScannedSheetInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class ScanInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_scan_info, rootView, false)
    private lateinit var scannedSheetSetsView: ScannedSheetSetsView

    private lateinit var layoutA4EquivalentAdfDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentDuplexDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentFlatbedlDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentSimplexDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentTotalDeciSheets: ViewGroup
    private lateinit var layoutAdfSheets: ViewGroup
    private lateinit var layoutDuplexSheets: ViewGroup
    private lateinit var layoutFlatbedSheets: ViewGroup
    private lateinit var layoutSimplexSheets: ViewGroup
    private lateinit var layoutTotalSheets: ViewGroup
    private lateinit var layoutOtherPrintedSheets: ViewGroup
    private lateinit var layoutScannedSheetSets: LinearLayout

    fun setScanInfo(scanInfo: ScanInfo?) {
        rootView.removeAllViews()
        if (scanInfo != null) {
            Utils.setSummary(layoutA4EquivalentAdfDeciSheets, scanInfo.a4EquivalentAdfDeciSheets)
            Utils.setSummary(
                layoutA4EquivalentDuplexDeciSheets,
                scanInfo.a4EquivalentDuplexDeciSheets
            )
            Utils.setSummary(
                layoutA4EquivalentFlatbedlDeciSheets,
                scanInfo.a4EquivalentFlatbedlDeciSheets
            )
            Utils.setSummary(
                layoutA4EquivalentSimplexDeciSheets,
                scanInfo.a4EquivalentSimplexDeciSheets
            )
            Utils.setSummary(
                layoutA4EquivalentTotalDeciSheets,
                scanInfo.a4EquivalentTotalDeciSheets
            )
            Utils.setSummary(layoutAdfSheets, scanInfo.adfSheets)
            Utils.setSummary(layoutDuplexSheets, scanInfo.duplexSheets)
            Utils.setSummary(layoutFlatbedSheets, scanInfo.flatbedSheets)
            Utils.setSummary(layoutSimplexSheets, scanInfo.simplexSheets)
            Utils.setSummary(layoutTotalSheets, scanInfo.totalSheets)
            setScannedSheetInfo(scanInfo.scannedSheetInfo)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setScannedSheetInfo(scannedSheetInfo: ScannedSheetInfo?) {
        if (scannedSheetInfo != null) {
            Utils.setSummary(layoutOtherPrintedSheets, scannedSheetInfo.otherPrintedSheets)
            scannedSheetSetsView.setScannedSheetSets(scannedSheetInfo.scannedSheetSets)
        } else {
            val parent = layoutOtherPrintedSheets.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewScanInfo(view: View) {
        layoutA4EquivalentAdfDeciSheets = Utils.setTitle(
            view.findViewById(R.id.layoutA4EquivalentAdfDeciSheets),
            R.string.a4EquivalentAdfDeciSheets
        )
        layoutA4EquivalentDuplexDeciSheets = Utils.setTitle(
            view.findViewById(R.id.layoutA4EquivalentDuplexDeciSheets),
            R.string.a4EquivalentDuplexDeciSheets
        )
        layoutA4EquivalentFlatbedlDeciSheets = Utils.setTitle(
            view.findViewById(R.id.layoutA4EquivalentFlatbedlDeciSheets),
            R.string.a4EquivalentFlatbedlDeciSheets
        )
        layoutA4EquivalentSimplexDeciSheets = Utils.setTitle(
            view.findViewById(R.id.layoutA4EquivalentSimplexDeciSheets),
            R.string.a4EquivalentSimplexDeciSheets
        )
        layoutA4EquivalentTotalDeciSheets = Utils.setTitle(
            view.findViewById(R.id.layoutA4EquivalentTotalDeciSheets),
            R.string.a4EquivalentTotalDeciSheets
        )
        layoutAdfSheets =
            Utils.setTitle(view.findViewById(R.id.layoutAdfSheets), R.string.adfSheets)
        layoutDuplexSheets =
            Utils.setTitle(view.findViewById(R.id.layoutDuplexSheets), R.string.duplexSheets)
        layoutFlatbedSheets =
            Utils.setTitle(view.findViewById(R.id.layoutFlatbedSheets), R.string.flatbedSheets)
        layoutSimplexSheets =
            Utils.setTitle(view.findViewById(R.id.layoutSimplexSheets), R.string.simplexSheets)
        layoutTotalSheets =
            Utils.setTitle(view.findViewById(R.id.layoutTotalSheets), R.string.totalSheets)

        (view.findViewById<View>(R.id.titleScannedSheetInfoTextView) as TextView).setText(R.string.scannedSheetInfo)
        layoutOtherPrintedSheets = Utils.setTitle(
            view.findViewById(R.id.layoutOtherPrintedSheets),
            R.string.otherPrintedSheets
        )
        layoutScannedSheetSets = Utils.getLayout(
            view.findViewById(R.id.layoutScannedSheetSets),
            R.string.scannedSheetSets
        )
    }

    private fun initViewClass(inflater: LayoutInflater) {
        scannedSheetSetsView = ScannedSheetSetsView(inflater, layoutScannedSheetSets)
    }

    init {
        initViewScanInfo(view)
        initViewClass(inflater)
    }
}