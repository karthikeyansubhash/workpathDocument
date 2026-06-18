// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.deviceusage.DeviceUsageInfo;
import com.hp.workpath.sample.deviceusagesample.Logger;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.task.DeviceUsageTask;
import com.hp.workpath.sample.deviceusagesample.view.DeviceUsageView;

public class DeviceUsageFragment extends Fragment {

    TextView mDeviceUsageRawDataTextView;
    Button mGetDeviceUsageButton;
    ProgressBar mProgressBar;

    DeviceUsageView mDeviceUsageView;
    DeviceUsageInfo mDeviceUsageInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_usage, container, false);
        mDeviceUsageRawDataTextView = view.findViewById(R.id.deviceUsageRawDataTextView);
        mDeviceUsageRawDataTextView.setMovementMethod(new ScrollingMovementMethod());
        mGetDeviceUsageButton = view.findViewById(R.id.getDeviceUsageButton);
        mGetDeviceUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceUsage();
            }
        });
        mProgressBar = view.findViewById(R.id.progressBar);
        mDeviceUsageView = new DeviceUsageView(inflater, view);
        getDeviceUsage();
        return view;
    }

    public void getDeviceUsage() {
        mProgressBar.setVisibility(View.VISIBLE);
        new DeviceUsageTask(getContext(), deviceUsageInterface).taskExecute();
    }

    private void setDeviceUsage(DeviceUsageInfo deviceUsageInfo) {
        mDeviceUsageView.setDeviceUsageInfo(deviceUsageInfo);
    }

    ResponseInterface deviceUsageInterface = new ResponseInterface() {
        @Override
        public void success(DeviceUsageInfo deviceUsageInfo) {
            mProgressBar.setVisibility(View.GONE);
            mDeviceUsageInfo = deviceUsageInfo;
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            if (mDeviceUsageInfo != null) {
                mDeviceUsageRawDataTextView.setText(gson.toJson(mDeviceUsageInfo));
                setDeviceUsage(mDeviceUsageInfo);
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };
}
