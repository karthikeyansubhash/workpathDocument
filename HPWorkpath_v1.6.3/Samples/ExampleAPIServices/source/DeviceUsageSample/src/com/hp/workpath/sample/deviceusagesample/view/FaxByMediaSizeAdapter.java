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

public class FaxByMediaSizeAdapter extends RecyclerView.Adapter<FaxByMediaSizeAdapter.ViewHolder> {

    List<PrinterInfo.FaxByMediaSize> mFaxByMediaSizes = new ArrayList<>();

    public PrinterInfo.FaxByMediaSize getItem(int position) {
        return mFaxByMediaSizes.get(position);
    }

    public void setItem(List<PrinterInfo.FaxByMediaSize> faxByMediaSizes) {
        mFaxByMediaSizes = faxByMediaSizes;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFaxByMediaSizes.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_job_category_and_media_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrinterInfo.FaxByMediaSize data = getItem(position);
        holder.indexTextView.setText(Integer.toString(position + 1));
        holder.mediaSizeTextView.setText(data.getMediaSize());
        if (data.getMediaSizeType() != null) {
            holder.mediaSizeTypeTextView.setText(data.getMediaSizeType().name());
        } else {
            holder.mediaSizeTypeTextView.setText("");
        }

        holder.colorImpressionsTextView.setText(String.valueOf(data.getColorImpressions()));
        holder.monoImpressionsTextView.setText(String.valueOf(data.getMonoImpressions()));
        holder.totalImpressionsTextView.setText(String.valueOf(data.getTotalImpressions()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexTextView;
        public TextView mediaSizeTextView;
        public TextView mediaSizeTypeTextView;
        public TextView colorImpressionsTextView;
        public TextView monoImpressionsTextView;
        public TextView totalImpressionsTextView;

        ViewHolder(View view) {
            super(view);
            indexTextView = view.findViewById(R.id.indexTextView);
            mediaSizeTextView = view.findViewById(R.id.jobCategoryTextView);
            mediaSizeTypeTextView = view.findViewById(R.id.jobCategoryTypeTextView);
            colorImpressionsTextView = view.findViewById(R.id.mediaSizeTextView);
            monoImpressionsTextView = view.findViewById(R.id.mediaSizeTypeTextView);
            totalImpressionsTextView = view.findViewById(R.id.impressionsTextView);
        }
    }
}
