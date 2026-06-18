// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.statistics.jobinfo.emailinfo.EmailInfo;
import com.hp.workpath.api.statistics.jobinfo.faxinfo.FaxAttributes;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class DigitalFaxCallView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutBillingCode;
    ViewGroup layoutFaxNumber;

    public DigitalFaxCallView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setDigitalFaxCall(FaxAttributes.DigitalFaxCall[] digitalFaxCall) {
        rootView.removeAllViews();
        if (digitalFaxCall != null) {
            for (int index = 0; index < digitalFaxCall.length; index++) {
                rootView.addView(setDigitalFaxCallInternal(index, digitalFaxCall[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setDigitalFaxCall(EmailInfo.DigitalFaxCall[] digitalFaxCall) {
        rootView.removeAllViews();
        if (digitalFaxCall != null) {
            for (int index = 0; index < digitalFaxCall.length; index++) {
                rootView.addView(setDigitalFaxCallInternal(index, digitalFaxCall[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setDigitalFaxCallInternal(int index, FaxAttributes.DigitalFaxCall digitalFaxCall) {
        View view = inflater.inflate(R.layout.layout_digital_fax_call, rootView, false);
        initViewDigitalFaxCall(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (digitalFaxCall != null) {
            Utils.setSummary(layoutBillingCode, digitalFaxCall.getBillingCode());
            Utils.setSummary(layoutFaxNumber, digitalFaxCall.getFaxNumber());
        }
        return view;
    }

    private View setDigitalFaxCallInternal(int index, EmailInfo.DigitalFaxCall digitalFaxCall) {
        View view = inflater.inflate(R.layout.layout_digital_fax_call, rootView, false);
        initViewDigitalFaxCall(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (digitalFaxCall != null) {
            Utils.setSummary(layoutBillingCode, digitalFaxCall.getBillingCode());
            Utils.setSummary(layoutFaxNumber, digitalFaxCall.getFaxNumber());
        }
        return view;
    }

    private void initViewDigitalFaxCall(View view) {
        layoutBillingCode = Utils.setTitle(view.findViewById(R.id.layoutBillingCode), R.string.billingCode);
        layoutFaxNumber = Utils.setTitle(view.findViewById(R.id.layoutFaxNumber), R.string.faxNumber);
    }
}
