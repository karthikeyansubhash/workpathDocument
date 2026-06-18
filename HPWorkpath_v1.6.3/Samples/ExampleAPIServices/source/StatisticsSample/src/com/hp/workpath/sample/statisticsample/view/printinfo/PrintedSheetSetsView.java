// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.print.PrintedSheetInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class PrintedSheetSetsView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutBackImpressionClassification;
    ViewGroup layoutCount;
    ViewGroup layoutFrontImpressionClassification;
    ViewGroup layoutLogicalMediaOutputId;
    ViewGroup layoutMediaInputId;
    ViewGroup layoutMediaSizeId;
    ViewGroup layoutMediaTypeId;
    ViewGroup layoutPhysicalMediaOutputId;
    ViewGroup layoutPlex;

    public PrintedSheetSetsView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setPrintedSheetSets(PrintedSheetInfo.PrintedSheetSets[] printedSheetSets) {
        rootView.removeAllViews();
        if (printedSheetSets != null) {
            for (int index = 0; index < printedSheetSets.length; index++) {
                rootView.addView(setPrintedSheetSetsInternal(index, printedSheetSets[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setPrintedSheetSetsInternal(int index, PrintedSheetInfo.PrintedSheetSets printedSheetSets) {
        View view = inflater.inflate(R.layout.layout_printed_sheet_sets, rootView, false);
        initViewPrintedSheetSets(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (printedSheetSets != null) {
            Utils.setSummary(layoutBackImpressionClassification, printedSheetSets.getBackImpressionClassification());
            Utils.setSummary(layoutCount, printedSheetSets.getCount());
            Utils.setSummary(layoutFrontImpressionClassification, printedSheetSets.getFrontImpressionClassification());
            Utils.setSummary(layoutLogicalMediaOutputId, printedSheetSets.getLogicalMediaOutputId());
            Utils.setSummary(layoutMediaInputId, printedSheetSets.getMediaInputId());
            Utils.setSummary(layoutMediaSizeId, printedSheetSets.getMediaSizeId());
            Utils.setSummary(layoutMediaTypeId, printedSheetSets.getMediaTypeId());
            Utils.setSummary(layoutPhysicalMediaOutputId, printedSheetSets.getPhysicalMediaOutputId());
            Utils.setSummary(layoutPlex, printedSheetSets.getPlex());
        }
        return view;
    }

    private void initViewPrintedSheetSets(View view) {
        layoutBackImpressionClassification = Utils.setTitle(view.findViewById(R.id.layoutBackImpressionClassification), R.string.backImpressionClassification);
        layoutCount = Utils.setTitle(view.findViewById(R.id.layoutCount), R.string.count);
        layoutFrontImpressionClassification = Utils.setTitle(view.findViewById(R.id.layoutFrontImpressionClassification), R.string.frontImpressionClassification);
        layoutLogicalMediaOutputId = Utils.setTitle(view.findViewById(R.id.layoutLogicalMediaOutputId), R.string.logicalMediaOutputId);
        layoutMediaInputId = Utils.setTitle(view.findViewById(R.id.layoutMediaInputId), R.string.mediaInputId);
        layoutMediaSizeId = Utils.setTitle(view.findViewById(R.id.layoutMediaSizeId), R.string.mediaSizeId);
        layoutMediaTypeId = Utils.setTitle(view.findViewById(R.id.layoutMediaTypeId), R.string.mediaTypeId);
        layoutPhysicalMediaOutputId = Utils.setTitle(view.findViewById(R.id.layoutPhysicalMediaOutputId), R.string.physicalMediaOutputId);
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex);
    }
}
