// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.R;

import static com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity.DATA;

public class ByJobCategoryAndMediaSizeView {

    LinearLayout rootView;
    LayoutInflater inflater;

    Button byJobCategoryAndMediaSizeButton;

    public ByJobCategoryAndMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setByJobCategoryAndMediaSize(PrinterInfo.ByJobCategoryAndMediaSize[] byJobCategoryAndMediaSizes) {
        rootView.removeAllViews();
        if (byJobCategoryAndMediaSizes != null) {
            rootView.addView(setByJobCategoryAndMediaSizeInternal(byJobCategoryAndMediaSizes));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setByJobCategoryAndMediaSizeInternal(final PrinterInfo.ByJobCategoryAndMediaSize[] categories) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewByJobCategory(view);
        if (categories != null) {
            byJobCategoryAndMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), JobCategoryAndMediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(categories));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewByJobCategory(View view) {
        byJobCategoryAndMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
