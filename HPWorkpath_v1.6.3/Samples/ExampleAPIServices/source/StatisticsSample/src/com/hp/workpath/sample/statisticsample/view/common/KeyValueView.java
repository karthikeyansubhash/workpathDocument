// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.userinfo.ExtendedUserInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class KeyValueView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutKey;
    ViewGroup layoutValueString;

    public KeyValueView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setKeyValue(ExtendedUserInfo.KeyValue[] keyValues) {
        rootView.removeAllViews();
        if (keyValues != null) {
            for (int index = 0; index < keyValues.length; index++) {
                rootView.addView(setKeyValueInternal(index, keyValues[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setKeyValueInternal(int index, ExtendedUserInfo.KeyValue keyValue) {
        View view = inflater.inflate(R.layout.layout_key_value, rootView, false);
        initViewKeyValue(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (keyValue != null) {
            Utils.setSummary(layoutKey, keyValue.getKey());
            Utils.setSummary(layoutValueString, keyValue.getValueString());
        }
        return view;
    }

    private void initViewKeyValue(View view) {
        layoutKey = Utils.setTitle(view.findViewById(R.id.layoutKey), R.string.key);
        layoutValueString = Utils.setTitle(view.findViewById(R.id.layoutValueString), R.string.valueString);
    }
}
