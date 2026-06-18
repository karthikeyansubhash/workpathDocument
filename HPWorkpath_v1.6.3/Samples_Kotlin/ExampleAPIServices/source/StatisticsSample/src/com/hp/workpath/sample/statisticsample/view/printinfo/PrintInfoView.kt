// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.print.PrintInfo
import com.hp.workpath.api.statistics.jobinfo.print.PrintSettings
import com.hp.workpath.api.statistics.jobinfo.print.PrintedSheetInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.getLayout
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class PrintInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_print_info, rootView, false)

    private lateinit var printAgentInfoView: PrintAgentInfoView
    private lateinit var printedSheetSetsView: PrintedSheetSetsView

    private lateinit var layoutA4EquivalentBlankDeciSides: ViewGroup
    private lateinit var layoutA4EquivalentColorDeciImpressions: ViewGroup
    private lateinit var layoutA4EquivalentDuplexDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentMonoChromeDeciImpressions: ViewGroup
    private lateinit var layoutA4EquivalentSimplexDeciSheets: ViewGroup
    private lateinit var layoutA4EquivalentTotalDeciImpressions: ViewGroup
    private lateinit var layoutA4EquivalentTotalDeciSheets: ViewGroup
    private lateinit var layoutBlankSides: ViewGroup
    private lateinit var layoutColorImpressions: ViewGroup
    private lateinit var layoutDuplexSheets: ViewGroup
    private lateinit var layoutMonochromeImpressions: ViewGroup
    private lateinit var layoutSimplexSheets: ViewGroup
    private lateinit var layoutTotalImpressions: ViewGroup
    private lateinit var layoutTotalSheets: ViewGroup

    private lateinit var layoutAgents: LinearLayout

    private lateinit var layoutEconoMode: ViewGroup
    private lateinit var layoutOtherPrintedSheets: ViewGroup
    private lateinit var layoutPrintedSheetSets: LinearLayout

    fun setPrintInfo(printInfo: PrintInfo?) {
        rootView.removeAllViews()
        if (printInfo != null) {
            setSummary(layoutA4EquivalentBlankDeciSides, printInfo.a4EquivalentBlankDeciSides)
            setSummary(
                layoutA4EquivalentColorDeciImpressions,
                printInfo.a4EquivalentColorDeciImpressions
            )
            setSummary(layoutA4EquivalentDuplexDeciSheets, printInfo.a4EquivalentDuplexDeciSheets)
            setSummary(
                layoutA4EquivalentMonoChromeDeciImpressions,
                printInfo.a4EquivalentMonoChromeDeciImpressions
            )
            setSummary(layoutA4EquivalentSimplexDeciSheets, printInfo.a4EquivalentSimplexDeciSheets)
            setSummary(
                layoutA4EquivalentTotalDeciImpressions,
                printInfo.a4EquivalentTotalDeciImpressions
            )
            setSummary(layoutA4EquivalentTotalDeciSheets, printInfo.a4EquivalentTotalDeciSheets)
            printAgentInfoView.setPrintAgentInfo(printInfo.agents)
            setSummary(layoutBlankSides, printInfo.blankSides)
            setSummary(layoutColorImpressions, printInfo.colorImpressions)
            setSummary(layoutDuplexSheets, printInfo.duplexSheets)
            setSummary(layoutMonochromeImpressions, printInfo.monochromeImpressions)
            setSummary(layoutSimplexSheets, printInfo.simplexSheets)
            setSummary(layoutTotalImpressions, printInfo.totalImpressions)
            setSummary(layoutTotalSheets, printInfo.totalSheets)
            setPrintSettings(printInfo.printSettings)
            setPrintedSheetInfo(printInfo.printedSheetInfo)
            rootView.addView(view)
        } else {
            val parent = rootView.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPrintSettings(printSettings: PrintSettings?) {
        if (printSettings != null) {
            setSummary(layoutEconoMode, printSettings.isEconoMode)
        } else {
            val parent = layoutEconoMode.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPrintedSheetInfo(printedSheetInfo: PrintedSheetInfo?) {
        if (printedSheetInfo != null) {
            setSummary(layoutOtherPrintedSheets, printedSheetInfo.otherPrintedSheets)
            printedSheetSetsView.setPrintedSheetSets(printedSheetInfo.printedSheetSets)
        } else {
            val parent = layoutOtherPrintedSheets.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewPrintInfo() {
        layoutA4EquivalentBlankDeciSides = setTitle(
            view.findViewById(R.id.layoutA4EquivalentBlankDeciSides),
            R.string.a4EquivalentBlankDeciSides
        )
        layoutA4EquivalentColorDeciImpressions = setTitle(
            view.findViewById(R.id.layoutA4EquivalentColorDeciImpressions),
            R.string.a4EquivalentColorDeciImpressions
        )
        layoutA4EquivalentDuplexDeciSheets = setTitle(
            view.findViewById(R.id.layoutA4EquivalentDuplexDeciSheets),
            R.string.a4EquivalentDuplexDeciSheets
        )
        layoutA4EquivalentMonoChromeDeciImpressions = setTitle(
            view.findViewById(R.id.layoutA4EquivalentMonoChromeDeciImpressions),
            R.string.a4EquivalentMonoChromeDeciImpressions
        )
        layoutA4EquivalentSimplexDeciSheets = setTitle(
            view.findViewById(R.id.layoutA4EquivalentSimplexDeciSheets),
            R.string.a4EquivalentSimplexDeciSheets
        )
        layoutA4EquivalentTotalDeciImpressions = setTitle(
            view.findViewById(R.id.layoutA4EquivalentTotalDeciImpressions),
            R.string.a4EquivalentTotalDeciImpressions
        )
        layoutA4EquivalentTotalDeciSheets = setTitle(
            view.findViewById(R.id.layoutA4EquivalentTotalDeciSheets),
            R.string.a4EquivalentTotalDeciSheets
        )
        layoutBlankSides = setTitle(view.findViewById(R.id.layoutBlankSides), R.string.blankSides)
        layoutColorImpressions =
            setTitle(view.findViewById(R.id.layoutColorImpressions), R.string.colorImpressions)
        layoutDuplexSheets =
            setTitle(view.findViewById(R.id.layoutDuplexSheets), R.string.duplexSheets)
        layoutMonochromeImpressions = setTitle(
            view.findViewById(R.id.layoutMonochromeImpressions),
            R.string.monochromeImpressions
        )
        layoutSimplexSheets =
            setTitle(view.findViewById(R.id.layoutSimplexSheets), R.string.simplexSheets)
        layoutTotalImpressions =
            setTitle(view.findViewById(R.id.layoutTotalImpressions), R.string.totalImpressions)
        layoutTotalSheets =
            setTitle(view.findViewById(R.id.layoutTotalSheets), R.string.totalSheets)
        layoutAgents = getLayout(view.findViewById(R.id.layoutAgents), R.string.agents)
        layoutEconoMode = setTitle(view.findViewById(R.id.layoutEconoMode), R.string.econoMode)
        layoutOtherPrintedSheets =
            setTitle(view.findViewById(R.id.layoutOtherPrintedSheets), R.string.otherPrintedSheets)
        layoutPrintedSheetSets =
            getLayout(view.findViewById(R.id.layoutPrintedSheetSets), R.string.printedSheetSets)

        (view.findViewById<View>(R.id.titlePrintSettingsTextView) as TextView).setText(R.string.printSettings)
        (view.findViewById<View>(R.id.titlePrintedSheetInfoTextView) as TextView).setText(R.string.printedSheetInfo)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        printAgentInfoView = PrintAgentInfoView(inflater, layoutAgents)
        printedSheetSetsView = PrintedSheetSetsView(inflater, layoutPrintedSheetSets)
    }

    init {
        initViewPrintInfo()
        initViewClass(inflater)
    }
}