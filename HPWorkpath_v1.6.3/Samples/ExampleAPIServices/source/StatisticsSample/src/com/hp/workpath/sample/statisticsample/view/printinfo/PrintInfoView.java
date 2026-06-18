// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.print.PrintInfo;
import com.hp.workpath.api.statistics.jobinfo.print.PrintSettings;
import com.hp.workpath.api.statistics.jobinfo.print.PrintedSheetInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class PrintInfoView {

    LinearLayout rootView;
    View view;
    PrintAgentInfoView printAgentInfoView;
    PrintedSheetSetsView printedSheetSetsView;

    ViewGroup layoutA4EquivalentBlankDeciSides;
    ViewGroup layoutA4EquivalentColorDeciImpressions;
    ViewGroup layoutA4EquivalentDuplexDeciSheets;
    ViewGroup layoutA4EquivalentMonoChromeDeciImpressions;
    ViewGroup layoutA4EquivalentSimplexDeciSheets;
    ViewGroup layoutA4EquivalentTotalDeciImpressions;
    ViewGroup layoutA4EquivalentTotalDeciSheets;
    ViewGroup layoutBlankSides;
    ViewGroup layoutColorImpressions;
    ViewGroup layoutDuplexSheets;
    ViewGroup layoutMonochromeImpressions;
    ViewGroup layoutSimplexSheets;
    ViewGroup layoutTotalImpressions;
    ViewGroup layoutTotalSheets;

    LinearLayout layoutAgents;

    ViewGroup layoutEconoMode;
    ViewGroup layoutOtherPrintedSheets;
    LinearLayout layoutPrintedSheetSets;

    public PrintInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_print_info, rootView, false);
        initViewPrintInfo();
        initViewClass(inflater);
    }

    public void setPrintInfo(PrintInfo printInfo) {
        rootView.removeAllViews();
        if (printInfo != null) {
            Utils.setSummary(layoutA4EquivalentBlankDeciSides, printInfo.getA4EquivalentBlankDeciSides());
            Utils.setSummary(layoutA4EquivalentColorDeciImpressions, printInfo.getA4EquivalentColorDeciImpressions());
            Utils.setSummary(layoutA4EquivalentDuplexDeciSheets, printInfo.getA4EquivalentDuplexDeciSheets());
            Utils.setSummary(layoutA4EquivalentMonoChromeDeciImpressions, printInfo.getA4EquivalentMonoChromeDeciImpressions());
            Utils.setSummary(layoutA4EquivalentSimplexDeciSheets, printInfo.getA4EquivalentSimplexDeciSheets());
            Utils.setSummary(layoutA4EquivalentTotalDeciImpressions, printInfo.getA4EquivalentTotalDeciImpressions());
            Utils.setSummary(layoutA4EquivalentTotalDeciSheets, printInfo.getA4EquivalentTotalDeciSheets());
            printAgentInfoView.setPrintAgentInfo(printInfo.getAgents());
            Utils.setSummary(layoutBlankSides, printInfo.getBlankSides());
            Utils.setSummary(layoutColorImpressions, printInfo.getColorImpressions());
            Utils.setSummary(layoutDuplexSheets, printInfo.getDuplexSheets());
            Utils.setSummary(layoutMonochromeImpressions, printInfo.getMonochromeImpressions());
            Utils.setSummary(layoutSimplexSheets, printInfo.getSimplexSheets());
            Utils.setSummary(layoutTotalImpressions, printInfo.getTotalImpressions());
            Utils.setSummary(layoutTotalSheets, printInfo.getTotalSheets());
            setPrintSettings(printInfo.getPrintSettings());
            setPrintedSheetInfo(printInfo.getPrintedSheetInfo());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setPrintSettings(PrintSettings printSettings) {
        if (printSettings != null) {
            Utils.setSummary(layoutEconoMode, printSettings.isEconoMode());
        } else {
            LinearLayout parent = (LinearLayout) layoutEconoMode.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setPrintedSheetInfo(PrintedSheetInfo printedSheetInfo) {
        if (printedSheetInfo != null) {
            Utils.setSummary(layoutOtherPrintedSheets, printedSheetInfo.getOtherPrintedSheets());
            printedSheetSetsView.setPrintedSheetSets(printedSheetInfo.getPrintedSheetSets());
        } else {
            LinearLayout parent = (LinearLayout) layoutOtherPrintedSheets.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewPrintInfo() {
        layoutA4EquivalentBlankDeciSides = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentBlankDeciSides), R.string.a4EquivalentBlankDeciSides);
        layoutA4EquivalentColorDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentColorDeciImpressions), R.string.a4EquivalentColorDeciImpressions);
        layoutA4EquivalentDuplexDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentDuplexDeciSheets), R.string.a4EquivalentDuplexDeciSheets);
        layoutA4EquivalentMonoChromeDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentMonoChromeDeciImpressions), R.string.a4EquivalentMonoChromeDeciImpressions);
        layoutA4EquivalentSimplexDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentSimplexDeciSheets), R.string.a4EquivalentSimplexDeciSheets);
        layoutA4EquivalentTotalDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentTotalDeciImpressions), R.string.a4EquivalentTotalDeciImpressions);
        layoutA4EquivalentTotalDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentTotalDeciSheets), R.string.a4EquivalentTotalDeciSheets);
        layoutBlankSides = Utils.setTitle(view.findViewById(R.id.layoutBlankSides), R.string.blankSides);
        layoutColorImpressions = Utils.setTitle(view.findViewById(R.id.layoutColorImpressions), R.string.colorImpressions);
        layoutDuplexSheets = Utils.setTitle(view.findViewById(R.id.layoutDuplexSheets), R.string.duplexSheets);
        layoutMonochromeImpressions = Utils.setTitle(view.findViewById(R.id.layoutMonochromeImpressions), R.string.monochromeImpressions);
        layoutSimplexSheets = Utils.setTitle(view.findViewById(R.id.layoutSimplexSheets), R.string.simplexSheets);
        layoutTotalImpressions = Utils.setTitle(view.findViewById(R.id.layoutTotalImpressions), R.string.totalImpressions);
        layoutTotalSheets = Utils.setTitle(view.findViewById(R.id.layoutTotalSheets), R.string.totalSheets);
        layoutAgents = Utils.getLayout(view.findViewById(R.id.layoutAgents), R.string.agents);
        layoutEconoMode = Utils.setTitle(view.findViewById(R.id.layoutEconoMode), R.string.econoMode);
        layoutOtherPrintedSheets = Utils.setTitle(view.findViewById(R.id.layoutOtherPrintedSheets), R.string.otherPrintedSheets);
        layoutPrintedSheetSets = Utils.getLayout(view.findViewById(R.id.layoutPrintedSheetSets), R.string.printedSheetSets);

        ((TextView) view.findViewById(R.id.titlePrintSettingsTextView)).setText(R.string.printSettings);
        ((TextView) view.findViewById(R.id.titlePrintedSheetInfoTextView)).setText(R.string.printedSheetInfo);
    }

    private void initViewClass(LayoutInflater inflater) {
        printAgentInfoView = new PrintAgentInfoView(inflater, layoutAgents);
        printedSheetSetsView = new PrintedSheetSetsView(inflater, layoutPrintedSheetSets);
    }
}
