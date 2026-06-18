// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import static com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity.DATA;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.PrintByMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.R;

public class PrintByMediaSizeView {

    LinearLayout rootView;
    LayoutInflater inflater;

    Button printByMediaSizeButton;

    public PrintByMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setPrintByMediaSize(PrinterInfo.PrintByMediaSize[] printByMediaSizes) {
        rootView.removeAllViews();
        if (printByMediaSizes != null) {
            rootView.addView(setPrintByMediaSizeInternal(printByMediaSizes));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setPrintByMediaSizeInternal(final PrinterInfo.PrintByMediaSize[] categories) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewPrintByMediaSize(view);
        if (categories != null) {
            printByMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), PrintByMediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(categories));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewPrintByMediaSize(View view) {
        printByMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
