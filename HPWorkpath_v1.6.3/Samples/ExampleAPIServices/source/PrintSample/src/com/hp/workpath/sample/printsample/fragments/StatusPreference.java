// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrinterStatus;
import com.hp.workpath.api.printer.StatusInfo;
import com.hp.workpath.sample.printsample.Logger;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.R;

public class StatusPreference extends Preference {

    private static final String TAG = MainActivity.TAG;

    Button mGetStatusButton;
    TextView mStatusTextView;

    String mPrinterStatus;

    public StatusPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        mGetStatusButton = (Button) view.findViewById(R.id.getStatusButton);
        mGetStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrinterStatus();
            }
        });
        mStatusTextView = (TextView) view.findViewById(R.id.statusTextView);
        if (TextUtils.isEmpty(mPrinterStatus)) {
            mPrinterStatus = getContext().getString(R.string.na);
        }
        mStatusTextView.setText(mPrinterStatus);
    }

    private void getPrinterStatus() {
        if (PrinterStatus.isSupported(getContext())) {
            Result result = new Result();
            StatusInfo statusInfo = PrinterStatus.getStatus(getContext(), result);
            if (result.getCode() == Result.RESULT_OK) {
                Log.i(TAG, "StatusInfo=" + Logger.build(statusInfo));
                mPrinterStatus = Logger.build(statusInfo);
                mStatusTextView.setText(mPrinterStatus);
            } else {
                Log.i(TAG,"PrinterStatus.getStatus" + Logger.build(result));
            }
        } else {
            Log.e(TAG,"PrinterStatus is not supported");
        }
    }
}
