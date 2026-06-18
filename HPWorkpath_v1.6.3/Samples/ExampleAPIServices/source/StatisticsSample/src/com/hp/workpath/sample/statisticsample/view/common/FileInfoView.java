// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.FileInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class FileInfoView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutDataSize;
    ViewGroup layoutFileName;

    public FileInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setFileInfo(FileInfo fileInfo) {
        rootView.removeAllViews();
        if (fileInfo != null) {
            rootView.addView(setFileInfoInternal(1, fileInfo));
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setFileInfo(FileInfo[] fileInfos) {
        rootView.removeAllViews();
        if (fileInfos != null) {
            for (int index = 0; index < fileInfos.length; index++) {
                rootView.addView(setFileInfoInternal(index, fileInfos[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setFileInfoInternal(int index, FileInfo fileInfo) {
        View view = inflater.inflate(R.layout.layout_file_info, rootView, false);
        initViewFileInfo(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        } else {
            view.setBackgroundColor(view.getResources().getColor(android.R.color.white));
        }
        if (fileInfo != null) {
            Utils.setSummary(layoutDataSize, fileInfo.getDataSize());
            Utils.setSummary(layoutFileName, fileInfo.getFileName());
        }
        return view;
    }

    private void initViewFileInfo(View view) {
        layoutDataSize = Utils.setTitle(view.findViewById(R.id.layoutDataSize), R.string.dataSize);
        layoutFileName = Utils.setTitle(view.findViewById(R.id.layoutFileName), R.string.fileName);
    }
}
