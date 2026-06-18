// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hp.workpath.api.deviceusage.printer.PrinterInfo
import com.hp.workpath.sample.deviceusagesample.MainActivity.Companion.TAG
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.Utils
import com.hp.workpath.sample.deviceusagesample.view.Utils.getLayout
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.*

class PrinterInfoView(inflater: LayoutInflater, private var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_printer_info, rootView, false)
    private lateinit var plexView: PlexView
    private lateinit var byJobCategoryView: ByJobCategoryView
    private lateinit var byJobCategoryAndMediaSizeView: ByJobCategoryAndMediaSizeView
    private lateinit var byColorModeView: ByColorModeView
    private lateinit var a4EquivalentByJobCategoryView: A4EquivalentByJobCategoryView
    private lateinit var printByMediaSizeView: PrintByMediaSizeView
    private lateinit var copyByMediaSizeView: CopyByMediaSizeView
    private lateinit var faxByMediaSizeView: FaxByMediaSizeView
    private lateinit var plexByMediaSizeView: PlexByMediaSizeView
    private lateinit var layoutSheets: ViewGroup
    private lateinit var layoutEngineCycles: ViewGroup
    private lateinit var layoutPlex: LinearLayout
    private lateinit var layoutByJobCategory: LinearLayout
    private lateinit var layoutByJobCategoryAndMediaSize: LinearLayout
    private lateinit var layoutByColorMode: LinearLayout
    private lateinit var layoutA4EquivalentByJobCategory: LinearLayout
    private lateinit var layoutPrintByMediaSize: LinearLayout
    private lateinit var layoutCopyByMediaSize: LinearLayout
    private lateinit var layoutFaxByMediaSize: LinearLayout
    private lateinit var layoutPlexByMediaSize: LinearLayout

    fun setPrinterInfo(printerInfo: PrinterInfo?) {
        rootView.removeAllViews()
        if (printerInfo != null) {
            val a4EquivalentByJobCategory = printerInfo.a4EquivalentByJobCategory
            for (index in a4EquivalentByJobCategory.indices) {
                Log.d(TAG, "a4EquivalentByJobCategory JobCategory : " + a4EquivalentByJobCategory[index].jobCategory)
                Log.d(TAG, "a4EquivalentByJobCategory ColorDeciImpressions : " + a4EquivalentByJobCategory[index].colorDeciImpressions)
                Log.d(TAG, "a4EquivalentByJobCategory MonoDeciImpressions : " + a4EquivalentByJobCategory[index].monoDeciImpressions)
                Log.d(TAG, "a4EquivalentByJobCategory TotalDeciImpressions : " + a4EquivalentByJobCategory[index].totalDeciImpressions)
            }


            val printByMediaSize = printerInfo.printByMediaSize
            for (index in printByMediaSize.indices) {
                Log.d(TAG, "printByMediaSize JobCategory : " + printByMediaSize[index].mediaSize)
                Log.d(TAG, "printByMediaSize ColorImpressions : " + printByMediaSize[index].colorImpressions)
                Log.d(TAG, "printByMediaSize MonoImpressions : " + printByMediaSize[index].monoImpressions)
                Log.d(TAG, "printByMediaSize TotalImpressions : " + printByMediaSize[index].totalImpressions)
            }

            val copyByMediaSize = printerInfo.copyByMediaSize
            for (index in copyByMediaSize.indices) {
                Log.d(TAG, "copyByMediaSize JobCategory : " + copyByMediaSize[index].mediaSize)
                Log.d(TAG, "copyByMediaSize ColorImpressions : " + copyByMediaSize[index].colorImpressions)
                Log.d(TAG, "copyByMediaSize MonoImpressions : " + copyByMediaSize[index].monoImpressions)
                Log.d(TAG, "copyByMediaSize TotalImpressions : " + copyByMediaSize[index].totalImpressions)
            }

            val faxByMediaSize = printerInfo.faxByMediaSize
            for (index in faxByMediaSize.indices) {
                Log.d(TAG, "faxByMediaSize JobCategory : " + faxByMediaSize[index].mediaSize)
                Log.d(TAG, "faxByMediaSize ColorImpressions : " + faxByMediaSize[index].colorImpressions)
                Log.d(TAG, "faxByMediaSize MonoImpressions : " + faxByMediaSize[index].monoImpressions)
                Log.d(TAG, "faxByMediaSize TotalImpressions : " + faxByMediaSize[index].totalImpressions)
            }

            val plexByMediaSize = printerInfo.plexByMediaSize
            for (index in plexByMediaSize.indices) {
                Log.d(TAG, "plexByMediaSize JobCategory : " + plexByMediaSize[index].mediaSize)
                Log.d(TAG, "plexByMediaSize SimplexSheets : " + plexByMediaSize[index].simplexSheets)
                Log.d(TAG, "plexByMediaSize DuplexSheets : " + plexByMediaSize[index].duplexSheets)
                Log.d(TAG, "plexByMediaSize TotalSheets : " + plexByMediaSize[index].totalSheets)
            }

            Utils.setSummary(layoutSheets, printerInfo.sheets)
            Utils.setSummary(layoutEngineCycles, printerInfo.engineCycles)
            plexView.setPlex(printerInfo.byPrintPlex)
            byJobCategoryView.setByJobCategory(printerInfo.byJobCategory)
            byJobCategoryAndMediaSizeView.setByJobCategoryAndMediaSize(printerInfo.byJobCategoryAndMediaSize)
            byColorModeView.setByColorMode(printerInfo.byColorMode)
            a4EquivalentByJobCategoryView.setA4EquivalentByJobCategory(printerInfo.a4EquivalentByJobCategory)
            printByMediaSizeView.setPrintByMediaSize(printerInfo.printByMediaSize)
            copyByMediaSizeView.setCopyByMediaSize(printerInfo.copyByMediaSize)
            faxByMediaSizeView.setFaxByMediaSize(printerInfo.faxByMediaSize)
            plexByMediaSizeView.setPlexByMediaSize(printerInfo.plexByMediaSize)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewPrinterInfo() {
        layoutSheets = Utils.setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets)
        layoutEngineCycles = Utils.setTitle(view.findViewById(R.id.layoutEngineCycles), R.string.engineCycles)
        layoutPlex = Utils.getLayout(view.findViewById(R.id.layoutByPrintPlex), R.string.byPrintPlex)
        layoutByJobCategory = Utils.getLayout(view.findViewById(R.id.layoutByJobCategory), R.string.byJobCategory)
        layoutByJobCategoryAndMediaSize = Utils.getLayout(view.findViewById(R.id.layoutByJobCategoryAndMediaSize), R.string.byJobCategoryAndMediaSize)
        layoutByColorMode = Utils.getLayout(view.findViewById(R.id.layoutByColorMode), R.string.byColorMode)
        layoutA4EquivalentByJobCategory = getLayout(view.findViewById(R.id.layoutA4EquivalentByJobCategory), R.string.a4EquivalentByJobCategory)
        layoutPrintByMediaSize = getLayout(view.findViewById(R.id.layoutPrintByMediaSize), R.string.printByMediaSize)
        layoutCopyByMediaSize = getLayout(view.findViewById(R.id.layoutCopyByMediaSize), R.string.copyByMediaSize)
        layoutFaxByMediaSize = getLayout(view.findViewById(R.id.layoutFaxByMediaSize), R.string.faxByMediaSize)
        layoutPlexByMediaSize = getLayout(view.findViewById(R.id.layoutPlexByMediaSiz), R.string.plexByMediaSize)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        plexView = PlexView(inflater, layoutPlex)
        byJobCategoryView = ByJobCategoryView(inflater, layoutByJobCategory)
        byJobCategoryAndMediaSizeView = ByJobCategoryAndMediaSizeView(inflater, layoutByJobCategoryAndMediaSize)
        byColorModeView = ByColorModeView(inflater, layoutByColorMode)
        a4EquivalentByJobCategoryView =
            A4EquivalentByJobCategoryView(inflater, layoutA4EquivalentByJobCategory)
        printByMediaSizeView = PrintByMediaSizeView(inflater, layoutPrintByMediaSize)
        copyByMediaSizeView = CopyByMediaSizeView(inflater, layoutCopyByMediaSize)
        faxByMediaSizeView = FaxByMediaSizeView(inflater, layoutFaxByMediaSize)
        plexByMediaSizeView = PlexByMediaSizeView(inflater, layoutPlexByMediaSize)
    }

    init {
        initViewPrinterInfo()
        initViewClass(inflater)
    }
}