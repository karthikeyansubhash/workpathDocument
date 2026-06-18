// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrinterStatus;
import com.hp.workpath.api.scanner.ScannerStatus;
import com.hp.workpath.api.scanner.StatusInfo;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.R;

public class StatusPreference extends Preference {

    Button mGetStatusButton;
    TextView mPrinterStatusTextView;
    TextView mScannerStatusTextView;

    String mPrinterStatus;
    String mScannerStatus;

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
                getScannerStatus();
            }
        });
        mPrinterStatusTextView = (TextView) view.findViewById(R.id.printerStatusTextView);
        if (TextUtils.isEmpty(mPrinterStatus)) {
            mPrinterStatus = getContext().getString(R.string.na);
        }
        mPrinterStatusTextView.setText(mPrinterStatus);
        mScannerStatusTextView = (TextView) view.findViewById(R.id.scannerStatusTextView);
        mScannerStatusTextView.setText(mScannerStatus);
    }

    private void getPrinterStatus() {
        if (PrinterStatus.isSupported(getContext())) {
            Result result = new Result();
            com.hp.workpath.api.printer.StatusInfo statusInfo
                    = PrinterStatus.getStatus(getContext(), result);
            if (result.getCode() == Result.RESULT_OK) {
                mPrinterStatus = "Printer: " + Logger.build(statusInfo);
                mPrinterStatusTextView.setText(mPrinterStatus);
                showResult(mPrinterStatus, null);
            } else {
                showResult("PrinterStatus.getStatus(): ", result);
            }
        } else {
            showResult("PrinterStatus is not supported", null);
        }
    }

    private void getScannerStatus() {
        if (ScannerStatus.isSupported(getContext())) {
            Result result = new Result();
            StatusInfo statusInfo = ScannerStatus.getStatus(getContext(), result);
            if (result.getCode() == Result.RESULT_OK) {
                mScannerStatus = "Scanner: " + Logger.build(statusInfo);
                mScannerStatusTextView.setText(mScannerStatus);
                showResult(mScannerStatus, null);
            } else {
                showResult("ScannerStatus.getStatus(): ", result);
            }
        } else {
            showResult("ScannerStatus is not supported", null);
        }
    }

    private void showResult(final String msg, final Result result) {
        Toast.makeText(getContext(), (result!=null)?msg + Logger.build(result):msg, Toast.LENGTH_SHORT).show();
    }
}
