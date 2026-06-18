// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.sample.deviceeventsample.R;
import java.util.ArrayList;
import java.util.List;

public class DeviceEventListAdapter extends RecyclerView.Adapter<DeviceEventListAdapter.ViewHolder> {

    List<DeviceEvent> mDeviceEventDataList = new ArrayList<>();
    View.OnClickListener mListOnClickListener;

    public DeviceEventListAdapter(View.OnClickListener listener) {
        mListOnClickListener = listener;
    }

    public DeviceEvent getItem(int position) {
        return mDeviceEventDataList.get(position);
    }

    public void setItem(List<DeviceEvent> deviceEventList) {
        mDeviceEventDataList = deviceEventList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = mDeviceEventDataList.size();
        if (size > 0) {
            mDeviceEventDataList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDeviceEventDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_device_event_list, parent, false);
        view.setOnClickListener(mListOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceEvent eventData = getItem(position);
        holder.sequenceTextView.setText(String.valueOf(position + 1));
        holder.titleTextView.setText(eventData.getTitle());
        holder.severityTextView.setText(eventData.getSeverity());
        holder.stateChangeTypeTextView.setText(eventData.getStateChangeType());
        holder.categoryTextView.setText(eventData.getCategory());
        holder.timestampTextView.setText(eventData.getTimestamp().getTime());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sequenceTextView;
        public TextView titleTextView;
        public TextView severityTextView;
        public TextView stateChangeTypeTextView;
        public TextView categoryTextView;
        public TextView timestampTextView;

        ViewHolder(View view) {
            super(view);
            sequenceTextView = view.findViewById(R.id.sequenceTextView);
            titleTextView = view.findViewById(R.id.titleTextView);
            severityTextView = view.findViewById(R.id.severityTextView);
            stateChangeTypeTextView = view.findViewById(R.id.stateChangeTypeTextView);
            categoryTextView = view.findViewById(R.id.categoryTextView);
            timestampTextView = view.findViewById(R.id.timestampTextView);
        }
    }
}
