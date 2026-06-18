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
import com.hp.workpath.sample.deviceusagesample.CopyByMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.PrintByMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.R;

public class CopyByMediaSizeView {

    LinearLayout rootView;
    LayoutInflater inflater;

    Button copyByMediaSizeButton;

    public CopyByMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setCopyByMediaSize(PrinterInfo.CopyByMediaSize[] copyByMediaSizes) {
        rootView.removeAllViews();
        if (copyByMediaSizes != null) {
            rootView.addView(setCopyByMediaSizeInternal(copyByMediaSizes));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setCopyByMediaSizeInternal(final PrinterInfo.CopyByMediaSize[] categories) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewCopyByMediaSize(view);
        if (categories != null) {
            copyByMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), CopyByMediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(categories));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewCopyByMediaSize(View view) {
        copyByMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
