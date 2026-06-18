package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;

public class A4EquivalentByJobCategoryView {
    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutJobCategory;
    ViewGroup layoutColorDeciImpressions;
    ViewGroup layoutMonoDeciImpressions;
    ViewGroup layoutTotalDeciImpressions;

    public A4EquivalentByJobCategoryView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setA4EquivalentByJobCategory(PrinterInfo.A4EquivalentByJobCategory[] A4EquivalentByJobCategories) {
        rootView.removeAllViews();
        if (A4EquivalentByJobCategories != null) {
            for (int index = 0; index < A4EquivalentByJobCategories.length; index++) {
                rootView.addView(setA4EquivalentByJobCategoryInternal(index, A4EquivalentByJobCategories[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setA4EquivalentByJobCategoryInternal(int index, PrinterInfo.A4EquivalentByJobCategory a4EquivalentByJobCategory) {
        View view = inflater.inflate(R.layout.layout_a4_equivalent_by_job_category, rootView, false);
        initViewA4EquivalentByJobCategoryView(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (a4EquivalentByJobCategory != null) {
            Utils.setSummary(layoutJobCategory, a4EquivalentByJobCategory.getJobCategory());
            Utils.setSummary(layoutColorDeciImpressions, a4EquivalentByJobCategory.getColorDeciImpressions());
            Utils.setSummary(layoutMonoDeciImpressions, a4EquivalentByJobCategory.getMonoDeciImpressions());
            Utils.setSummary(layoutTotalDeciImpressions, a4EquivalentByJobCategory.getTotalDeciImpressions());
        }
        return view;
    }

    private void initViewA4EquivalentByJobCategoryView(View view) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory);
        layoutColorDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutColorDeciImpressions), R.string.colorDeciImpressions);
        layoutMonoDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutMonoDeciImpressions), R.string.monoDeciImpressions);
        layoutTotalDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutTotalDeciImpressions), R.string.totalDeciImpressions);
    }
}
