// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hp.workpath.api.deviceusage.scanner.ScannerInfo;
import com.hp.workpath.sample.deviceusagesample.MediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.R;

import static com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity.DATA;

public class ByMediaSizeView {

    LinearLayout rootView;
    LayoutInflater inflater;

    Button byMediaSizeButton;

    public ByMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setByMediaSize(ScannerInfo.ByMediaSize[] byMediaSizes) {
        rootView.removeAllViews();
        if (byMediaSizes != null) {
            rootView.addView(setByMediaSizeInternal(byMediaSizes));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setByMediaSizeInternal(final ScannerInfo.ByMediaSize[] byMediaSizes) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewByMediaSize(view);
        if (byMediaSizes != null) {
            byMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), MediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(byMediaSizes));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewByMediaSize(View view) {
        byMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
