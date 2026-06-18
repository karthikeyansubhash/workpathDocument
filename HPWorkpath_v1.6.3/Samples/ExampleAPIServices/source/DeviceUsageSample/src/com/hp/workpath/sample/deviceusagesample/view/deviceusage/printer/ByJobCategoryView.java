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

public class ByJobCategoryView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutJobCategory;
    ViewGroup layoutJobCategoryType;
    ViewGroup layoutA4EquivalentDeciImpressions;

    public ByJobCategoryView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setByJobCategory(PrinterInfo.ByJobCategory[] byJobCategories) {
        rootView.removeAllViews();
        if (byJobCategories != null) {
            for (int index = 0; index < byJobCategories.length; index++) {
                rootView.addView(setByJobCategoryInternal(index, byJobCategories[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setByJobCategoryInternal(int index, PrinterInfo.ByJobCategory category) {
        View view = inflater.inflate(R.layout.layout_by_job_category, rootView, false);
        initViewByJobCategory(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (category != null) {
            Utils.setSummary(layoutJobCategory, category.getJobCategory());
            Utils.setSummary(layoutJobCategoryType, category.getJobCategoryType());
            Utils.setSummary(layoutA4EquivalentDeciImpressions, category.getA4EquivalentDeciImpressions());
        }
        return view;
    }

    private void initViewByJobCategory(View view) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory);
        layoutJobCategoryType = Utils.setTitle(view.findViewById(R.id.layoutJobCategoryType), R.string.jobCategoryType);
        layoutA4EquivalentDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentDeciImpressions), R.string.a4EquivalentDeciImpressions);
    }
}
