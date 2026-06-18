// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.scanner.ScannerInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;

public class ByJobCategoryView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutJobCategory;
    ViewGroup layoutJobCategoryType;
    ViewGroup layoutImages;

    public ByJobCategoryView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setByJobCategory(ScannerInfo.ByJobCategory[] byJobCategories) {
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

    private View setByJobCategoryInternal(int index, ScannerInfo.ByJobCategory category) {
        View view = inflater.inflate(R.layout.layout_by_job_category_scan, rootView, false);
        initViewByJobCategory(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (category != null) {
            Utils.setSummary(layoutJobCategory, category.getJobCategory());
            Utils.setSummary(layoutJobCategoryType, category.getJobCategoryType());
            Utils.setSummary(layoutImages, category.getImages());
        }
        return view;
    }

    private void initViewByJobCategory(View view) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory);
        layoutJobCategoryType = Utils.setTitle(view.findViewById(R.id.layoutJobCategoryType), R.string.jobCategoryType);
        layoutImages = Utils.setTitle(view.findViewById(R.id.layoutImages), R.string.images);
    }
}
