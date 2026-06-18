// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.sample.deviceeventsample.DeviceEventActivity;
import com.hp.workpath.sample.deviceeventsample.Logger;
import com.hp.workpath.sample.deviceeventsample.MainActivity;
import com.hp.workpath.sample.deviceeventsample.R;
import com.hp.workpath.sample.deviceeventsample.task.DeviceEventTask;
import com.hp.workpath.sample.deviceeventsample.view.DeviceEventListAdapter;

import java.util.List;

import static com.hp.workpath.sample.deviceeventsample.DeviceEventActivity.INDEX;

public class DeviceEventListFragment extends Fragment implements View.OnClickListener {

    private TextView mTotalTextView;
    private TextView mNoEventTextView;
    private TextView mEventTextView;
    private ImageButton mGetDeviceEventListButton;
    private RecyclerView mListView;
    private DeviceEventListAdapter mListAdapter;
    private ProgressBar mProgressBar;

    List<DeviceEvent> mDeviceEvents;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_event_list, container, false);
        if (SCREEN_4_3_INCH.equals(view.findViewById(R.id.layout).getTag())) {
            Toolbar toolBar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolBar);
        }
        mTotalTextView = view.findViewById(R.id.totalTextView);
        mNoEventTextView = view.findViewById(R.id.noEventTextView);
        mEventTextView = view.findViewById(R.id.eventTextView);
        mEventTextView.setMovementMethod(new ScrollingMovementMethod());
        mGetDeviceEventListButton = view.findViewById(R.id.getDeviceEventListButton);
        mGetDeviceEventListButton.setOnClickListener(this);
        mListView = view.findViewById(R.id.deviceEventListView);
        mListAdapter = new DeviceEventListAdapter(listOnClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL);
        mListView.addItemDecoration(dividerItemDecoration);
        mProgressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    public void getDeviceEventList() {
        mNoEventTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTotalTextView.setText("0");
        mListAdapter.clear();
        new DeviceEventTask(getContext(), deviceEventInterface).taskExecute();
    }

    public void setEvent(DeviceEvent deviceEvent) {
        mEventTextView.setText(Logger.build(deviceEvent));
    }

    View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = mListView.getChildLayoutPosition(v);
            startDeviceEventActivity(itemPosition + 1);
        }
    };

    private void startDeviceEventActivity(int index) {
        if (index > 0 && index <= mDeviceEvents.size()) {
            Intent intent = new Intent(getContext(), DeviceEventActivity.class);
            intent.putExtra(INDEX, index);
            startActivity(intent);
        } else {
            if (getContext() != null) {
                Logger.showResult(getActivity(), getString(R.string.range_over));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mGetDeviceEventListButton) {
            getDeviceEventList();
        }
    }

    ResponseInterface deviceEventInterface = new ResponseInterface() {
        @Override
        public void success(List<DeviceEvent> deviceEvents) {
            mProgressBar.setVisibility(View.GONE);
            mDeviceEvents = deviceEvents;
            if (mDeviceEvents != null && mDeviceEvents.size() > 0) {
                mTotalTextView.setText(String.valueOf(mDeviceEvents.size()));
                mListAdapter.setItem(mDeviceEvents);
            } else {
                mNoEventTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };
}
