// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.scanner.ScannerInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.ByJobCategoryView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.ByMediaSizeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner.PlexView;

public class ScannerInfoView {

    LinearLayout rootView;
    View view;

    PlexView plexView;
    ByJobCategoryView byJobCategoryViewView;
    ByMediaSizeView byMediaSizeView;

    ViewGroup layoutSheets;
    ViewGroup layoutEngineCycles;

    LinearLayout layoutPlex;
    LinearLayout layoutByJobCategory;
    LinearLayout layoutByMediaSize;

    public ScannerInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_scanner_info, rootView, false);

        initViewScannerInfo();
        initViewClass(inflater);
    }

    public void setScannerInfo(ScannerInfo scannerInfo) {
        rootView.removeAllViews();
        if (scannerInfo != null) {
            Utils.setSummary(layoutSheets, scannerInfo.getSheets());
            Utils.setSummary(layoutEngineCycles, scannerInfo.getEngineCycles());
            plexView.setPlex(scannerInfo.getByScanPlex());
            byJobCategoryViewView.setByJobCategory(scannerInfo.getByJobCategory());
            byMediaSizeView.setByMediaSize(scannerInfo.getByMediaSize());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewScannerInfo() {
        layoutSheets = Utils.setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets);
        layoutEngineCycles = Utils.setTitle(view.findViewById(R.id.layoutEngineCycles), R.string.engineCycles);
        layoutPlex = Utils.getLayout(view.findViewById(R.id.layoutByScanPlex), R.string.byPrintPlex);
        layoutByJobCategory = Utils.getLayout(view.findViewById(R.id.layoutByJobCategory), R.string.byJobCategory);
        layoutByMediaSize = Utils.getLayout(view.findViewById(R.id.layoutByMediaSize), R.string.byMediaSize);
    }

    private void initViewClass(LayoutInflater inflater) {
        plexView = new PlexView(inflater, layoutPlex);
        byJobCategoryViewView = new ByJobCategoryView(inflater, layoutByJobCategory);
        byMediaSizeView = new ByMediaSizeView(inflater, layoutByMediaSize);
    }
}
