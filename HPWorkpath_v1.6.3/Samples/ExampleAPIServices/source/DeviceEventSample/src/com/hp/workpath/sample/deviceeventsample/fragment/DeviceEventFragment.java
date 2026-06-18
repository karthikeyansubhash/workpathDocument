// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.sample.deviceeventsample.Logger;
import com.hp.workpath.sample.deviceeventsample.R;
import com.hp.workpath.sample.deviceeventsample.task.DeviceEventTask;
import com.hp.workpath.sample.deviceeventsample.view.DeviceEventView;

import java.util.List;

import static com.hp.workpath.sample.deviceeventsample.DeviceEventActivity.INDEX;

public class DeviceEventFragment extends Fragment {

    LinearLayout layoutSummary;
    TextView mDeviceEventRawDataTextView;
    TextView mTitleTextView;
    ProgressBar mProgressBar;

    List<DeviceEvent> mDeviceEvents;
    DeviceEventView mDeviceEventView;

    int mIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_event, container, false);
        layoutSummary = view.findViewById(R.id.layoutSummary);
        mDeviceEventRawDataTextView = view.findViewById(R.id.deviceEventRawDataTextView);
        mDeviceEventRawDataTextView.setMovementMethod(new ScrollingMovementMethod());
        mTitleTextView = view.findViewById(R.id.titleTextView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mDeviceEventView = new DeviceEventView(inflater, view);
        mIndex = getArguments().getInt(INDEX);
        getDeviceEvent();
        return view;
    }

    private void setRawData(DeviceEvent deviceEvent) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        mDeviceEventRawDataTextView.setText(gson.toJson(deviceEvent));
    }

    public void getDeviceEvent() {
        mProgressBar.setVisibility(View.VISIBLE);
        new DeviceEventTask(getContext(), deviceEventInterface).taskExecute();
    }

    private void setDeviceEvent(DeviceEvent deviceEvent) {
        mTitleTextView.setText(deviceEvent.getTitle());
        setRawData(deviceEvent);
        mDeviceEventView.setDeviceEventData(deviceEvent);
    }

    ResponseInterface deviceEventInterface = new ResponseInterface() {
        @Override
        public void success(List<DeviceEvent> deviceEvents) {
            mProgressBar.setVisibility(View.GONE);
            mDeviceEvents = deviceEvents;
            if (mDeviceEvents != null && mDeviceEvents.size() > 0) {
                layoutSummary.setVisibility(View.VISIBLE);
                setDeviceEvent(mDeviceEvents.get(mIndex - 1));
            } else {
                mDeviceEventRawDataTextView.setText(getString(R.string.no_event));
                layoutSummary.setVisibility(View.GONE);
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };
}
