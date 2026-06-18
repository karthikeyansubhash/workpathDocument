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
import com.hp.workpath.api.printer.TrayInfo;
import com.hp.workpath.sample.printsample.Logger;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.R;

import java.util.List;

public class TrayInfoPreference extends Preference {

    private static final String TAG = MainActivity.TAG;

    Button mGetTrayInfoButton;
    TextView mTrayInfoTextView1;
    TextView mTrayInfoTextView2;

    String mTrayInfoStatus1;
    String mTrayInfoStatus2;

    public TrayInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TrayInfoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrayInfoPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        mGetTrayInfoButton = (Button) view.findViewById(R.id.getTrayInfoButton);
        mGetTrayInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrayInfoStatus();
            }
        });
        mTrayInfoTextView1 = (TextView) view.findViewById(R.id.trayTextView1);
        mTrayInfoTextView2 = (TextView) view.findViewById(R.id.trayTextView2);
        if (TextUtils.isEmpty(mTrayInfoStatus1)) {
            mTrayInfoStatus1 = getContext().getString(R.string.na);
        }
        mTrayInfoTextView1.setText(mTrayInfoStatus1);
    }

    private void getTrayInfoStatus() {
        if (PrinterStatus.isSupported(getContext())) {
            Result result = new Result();
            List<TrayInfo> trayInfoList = PrinterStatus.getTrays(getContext(), result);
            if (result.getCode() == Result.RESULT_OK) {
                mTrayInfoStatus1 = "";
                mTrayInfoStatus2 = "";
                int index = 0;
                for (TrayInfo trayInfo : trayInfoList) {
                    Log.i(TAG, "trayInfo=" + Logger.build(trayInfo));
                    String status;
                    if (trayInfo.getStatus() == TrayInfo.Status.AVAILABLE) {
                        status = trayInfo.getPaperSource() + " (" + trayInfo.getStatus() + "): "
                                + trayInfo.getLevel() + "% cap:" + trayInfo.getCapacity() + ", "
                                + trayInfo.getPaperSize() + ", " + trayInfo.getPaperType() + "\n";
                    } else {
                        status = trayInfo.getPaperSource() + " (" + trayInfo.getStatus() + ")\n";
                    }
                    if (index < 3) {
                        mTrayInfoStatus1 += status;
                    } else {
                        mTrayInfoStatus2 += status;
                    }
                    index++;
                }
                mTrayInfoTextView1.setText(mTrayInfoStatus1);
                mTrayInfoTextView2.setText(mTrayInfoStatus2);
            } else {
                Log.i(TAG, "PrinterStatus.getTrays " + Logger.build(result));
            }
        } else {
            Log.e(TAG, "PrinterStatus is not supported");
        }
    }
}
