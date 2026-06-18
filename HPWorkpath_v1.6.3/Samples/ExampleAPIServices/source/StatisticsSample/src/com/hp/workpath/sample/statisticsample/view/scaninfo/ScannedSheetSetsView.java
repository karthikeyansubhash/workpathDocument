// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.scaninfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.scan.ScannedSheetInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class ScannedSheetSetsView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutCount;
    ViewGroup layoutMediaInputId;
    ViewGroup layoutMediaSizeId;
    ViewGroup layoutPlex;

    public ScannedSheetSetsView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setScannedSheetSets(ScannedSheetInfo.ScannedSheetSets[] scannedSheetSet) {
        rootView.removeAllViews();
        if (scannedSheetSet != null) {
            for (ScannedSheetInfo.ScannedSheetSets set : scannedSheetSet) {
                rootView.addView(setScannedSheetSets(set));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setScannedSheetSets(ScannedSheetInfo.ScannedSheetSets scannedSheetSet) {
        View view = inflater.inflate(R.layout.layout_scanned_sheet_sets, rootView, false);
        initViewScannedSheetSets(view);

        if (scannedSheetSet != null) {
            Utils.setSummary(layoutCount, scannedSheetSet.getCount());
            Utils.setSummary(layoutMediaInputId, scannedSheetSet.getMediaInputId());
            Utils.setSummary(layoutMediaSizeId, scannedSheetSet.getMediaSizeId());
            Utils.setSummary(layoutPlex, scannedSheetSet.getPlex());
        }
        return view;
    }

    private void initViewScannedSheetSets(View view) {
        layoutCount = Utils.setTitle(view.findViewById(R.id.layoutCount), R.string.count);
        layoutMediaInputId = Utils.setTitle(view.findViewById(R.id.layoutMediaInputId), R.string.mediaInputId);
        layoutMediaSizeId = Utils.setTitle(view.findViewById(R.id.layoutMediaSizeId), R.string.mediaSizeId);
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex);
    }
}
