// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;

public class ByColorModeView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutJobCategory;
    ViewGroup layoutColorMode;
    ViewGroup layoutColorModeType;
    ViewGroup layoutImpressions;

    public ByColorModeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setByColorMode(PrinterInfo.ByColorMode[] byColorModes) {
        rootView.removeAllViews();
        if (byColorModes != null) {
            for (int index = 0; index < byColorModes.length; index++) {
                rootView.addView(setByColorModeInternal(index, byColorModes[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setByColorModeInternal(int index, PrinterInfo.ByColorMode colorMode) {
        View view = inflater.inflate(R.layout.layout_by_color_mode, rootView, false);
        initViewByJobCategory(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (colorMode != null) {
            Utils.setSummary(layoutJobCategory, colorMode.getJobCategory());
            Utils.setSummary(layoutColorMode, colorMode.getColorMode());
            Utils.setSummary(layoutColorModeType, colorMode.getColorModeType());
            Utils.setSummary(layoutImpressions, colorMode.getImpressions());
        }
        return view;
    }

    private void initViewByJobCategory(View view) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory);
        layoutColorMode = Utils.setTitle(view.findViewById(R.id.layoutColorMode), R.string.colorMode);
        layoutColorModeType = Utils.setTitle(view.findViewById(R.id.layoutColorModeType), R.string.colorModeType);
        layoutImpressions = Utils.setTitle(view.findViewById(R.id.layoutImpressions), R.string.impressions);
    }
}
