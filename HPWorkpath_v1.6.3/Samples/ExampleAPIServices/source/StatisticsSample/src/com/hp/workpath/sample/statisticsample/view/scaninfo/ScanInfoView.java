// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.scaninfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.scan.ScanInfo;
import com.hp.workpath.api.statistics.jobinfo.scan.ScannedSheetInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class ScanInfoView {

    LinearLayout rootView;
    View view;
    ScannedSheetSetsView scannedSheetSetsView;


    ViewGroup layoutA4EquivalentAdfDeciSheets;
    ViewGroup layoutA4EquivalentDuplexDeciSheets;
    ViewGroup layoutA4EquivalentFlatbedlDeciSheets;
    ViewGroup layoutA4EquivalentSimplexDeciSheets;
    ViewGroup layoutA4EquivalentTotalDeciSheets;
    ViewGroup layoutAdfSheets;
    ViewGroup layoutDuplexSheets;
    ViewGroup layoutFlatbedSheets;
    ViewGroup layoutSimplexSheets;
    ViewGroup layoutTotalSheets;
    ViewGroup layoutOtherPrintedSheets;
    LinearLayout layoutScannedSheetSets;

    public ScanInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_scan_info, rootView, false);
        initViewScanInfo(view);
        initViewClass(inflater);
    }

    public void setScanInfo(ScanInfo scanInfo) {
        rootView.removeAllViews();
        if (scanInfo != null) {
            Utils.setSummary(layoutA4EquivalentAdfDeciSheets, scanInfo.getA4EquivalentAdfDeciSheets());
            Utils.setSummary(layoutA4EquivalentDuplexDeciSheets, scanInfo.getA4EquivalentDuplexDeciSheets());
            Utils.setSummary(layoutA4EquivalentFlatbedlDeciSheets, scanInfo.getA4EquivalentFlatbedlDeciSheets());
            Utils.setSummary(layoutA4EquivalentSimplexDeciSheets, scanInfo.getA4EquivalentSimplexDeciSheets());
            Utils.setSummary(layoutA4EquivalentTotalDeciSheets, scanInfo.getA4EquivalentTotalDeciSheets());
            Utils.setSummary(layoutAdfSheets, scanInfo.getAdfSheets());
            Utils.setSummary(layoutDuplexSheets, scanInfo.getDuplexSheets());
            Utils.setSummary(layoutFlatbedSheets, scanInfo.getFlatbedSheets());
            Utils.setSummary(layoutSimplexSheets, scanInfo.getSimplexSheets());
            Utils.setSummary(layoutTotalSheets, scanInfo.getTotalSheets());
            setScannedSheetInfo(scanInfo.getScannedSheetInfo());

            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setScannedSheetInfo(ScannedSheetInfo scannedSheetInfo) {
        if (scannedSheetInfo != null) {
            Utils.setSummary(layoutOtherPrintedSheets, scannedSheetInfo.getOtherPrintedSheets());
            scannedSheetSetsView.setScannedSheetSets(scannedSheetInfo.getScannedSheetSets());
        } else {
            LinearLayout parent = (LinearLayout) layoutOtherPrintedSheets.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewScanInfo(View view) {
        layoutA4EquivalentAdfDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentAdfDeciSheets), R.string.a4EquivalentAdfDeciSheets);
        layoutA4EquivalentDuplexDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentDuplexDeciSheets), R.string.a4EquivalentDuplexDeciSheets);
        layoutA4EquivalentFlatbedlDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentFlatbedlDeciSheets), R.string.a4EquivalentFlatbedlDeciSheets);
        layoutA4EquivalentSimplexDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentSimplexDeciSheets), R.string.a4EquivalentSimplexDeciSheets);
        layoutA4EquivalentTotalDeciSheets = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentTotalDeciSheets), R.string.a4EquivalentTotalDeciSheets);
        layoutAdfSheets = Utils.setTitle(view.findViewById(R.id.layoutAdfSheets), R.string.adfSheets);
        layoutDuplexSheets = Utils.setTitle(view.findViewById(R.id.layoutDuplexSheets), R.string.duplexSheets);
        layoutFlatbedSheets = Utils.setTitle(view.findViewById(R.id.layoutFlatbedSheets), R.string.flatbedSheets);
        layoutSimplexSheets = Utils.setTitle(view.findViewById(R.id.layoutSimplexSheets), R.string.simplexSheets);
        layoutTotalSheets = Utils.setTitle(view.findViewById(R.id.layoutTotalSheets), R.string.totalSheets);

        ((TextView) view.findViewById(R.id.titleScannedSheetInfoTextView)).setText(R.string.scannedSheetInfo);
        layoutOtherPrintedSheets = Utils.setTitle(view.findViewById(R.id.layoutOtherPrintedSheets), R.string.otherPrintedSheets);
        layoutScannedSheetSets = Utils.getLayout(view.findViewById(R.id.layoutScannedSheetSets), R.string.scannedSheetSets);
    }

    private void initViewClass(LayoutInflater inflater) {
        scannedSheetSetsView = new ScannedSheetSetsView(inflater, layoutScannedSheetSets);
    }
}
