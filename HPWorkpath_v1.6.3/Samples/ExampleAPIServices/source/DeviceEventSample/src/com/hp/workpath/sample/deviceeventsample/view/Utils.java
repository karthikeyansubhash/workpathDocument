// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.sample.deviceeventsample.R;

public class Utils {

    public static ViewGroup setTitle(ViewGroup viewGroup, int id) {
        ((TextView) viewGroup.findViewById(R.id.titleTextView)).setText(id);
        return viewGroup;
    }

    public static <T> void setSummary(ViewGroup viewGroup, T value) {
        try {
            if (value != null) {
                String valueString = "";
                if (value instanceof Enum) {
                    valueString = ((Enum) value).name();
                } else if (value instanceof Integer) {
                    valueString = String.valueOf(value);
                } else if (value instanceof String[]) {
                    for (String str: (String[]) value) {
                        valueString += str + "\n";
                    }
                } else {
                    valueString = (String) value;
                }
                ((TextView) viewGroup.findViewById(R.id.summaryTextView)).setText(valueString);
                return;
            }
        } catch (Throwable ignore) { }
        viewGroup.setVisibility(View.GONE);
    }

    public static LinearLayout getLayout(ViewGroup viewGroup, int id) {
        ((TextView) viewGroup.findViewById(R.id.titleTextView)).setText(id);
        return ((LinearLayout) viewGroup.findViewById(R.id.layoutChild));
    }
}
