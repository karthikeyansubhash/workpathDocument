// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;

import java.util.ArrayList;
import java.util.List;

public class JobCategoryAndMediaSizeAdapter extends RecyclerView.Adapter<JobCategoryAndMediaSizeAdapter.ViewHolder> {

    List<PrinterInfo.ByJobCategoryAndMediaSize> mByJobCategoryAndMediaSizeList = new ArrayList<>();

    public PrinterInfo.ByJobCategoryAndMediaSize getItem(int position) {
        return mByJobCategoryAndMediaSizeList.get(position);
    }

    public void setItem(List<PrinterInfo.ByJobCategoryAndMediaSize> byJobCategoryAndMediaSizes) {
        mByJobCategoryAndMediaSizeList = byJobCategoryAndMediaSizes;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mByJobCategoryAndMediaSizeList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_job_category_and_media_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrinterInfo.ByJobCategoryAndMediaSize data = getItem(position);
        holder.indexTextView.setText(Integer.toString(position + 1));
        holder.jobCategoryTextView.setText(data.getJobCategory());
        if (data.getJobCategoryType() != null) {
            holder.jobCategoryTypeTextView.setText(data.getJobCategoryType().name());
        } else {
            holder.jobCategoryTypeTextView.setText("");
        }
        holder.mediaSizeTextView.setText(data.getMediaSize());
        if (data.getMediaSizeType() != null) {
            holder.mediaSizeTypeTextView.setText(data.getMediaSizeType().name());
        } else {
            holder.mediaSizeTypeTextView.setText("");
        }
        holder.impressionsTextView.setText(Integer.toString(data.getImpressions()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexTextView;
        public TextView jobCategoryTextView;
        public TextView jobCategoryTypeTextView;
        public TextView mediaSizeTextView;
        public TextView mediaSizeTypeTextView;
        public TextView impressionsTextView;

        ViewHolder(View view) {
            super(view);
            indexTextView = view.findViewById(R.id.indexTextView);
            jobCategoryTextView = view.findViewById(R.id.jobCategoryTextView);
            jobCategoryTypeTextView = view.findViewById(R.id.jobCategoryTypeTextView);
            mediaSizeTextView = view.findViewById(R.id.mediaSizeTextView);
            mediaSizeTypeTextView = view.findViewById(R.id.mediaSizeTypeTextView);
            impressionsTextView = view.findViewById(R.id.impressionsTextView);
        }
    }
}
