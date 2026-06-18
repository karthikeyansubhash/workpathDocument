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

public class PlexByMediaSizeAdapter extends RecyclerView.Adapter<PlexByMediaSizeAdapter.ViewHolder> {

    List<PrinterInfo.PlexByMediaSize> mPlexByMediaSizes = new ArrayList<>();

    public PrinterInfo.PlexByMediaSize getItem(int position) {
        return mPlexByMediaSizes.get(position);
    }

    public void setItem(List<PrinterInfo.PlexByMediaSize> plexByMediaSizes) {
        mPlexByMediaSizes = plexByMediaSizes;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPlexByMediaSizes.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_job_category_and_media_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrinterInfo.PlexByMediaSize data = getItem(position);
        holder.indexTextView.setText(Integer.toString(position + 1));
        holder.mediaSizeTextView.setText(data.getMediaSize());
        if (data.getMediaSizeType() != null) {
            holder.mediaSizeTypeTextView.setText(data.getMediaSizeType().name());
        } else {
            holder.mediaSizeTypeTextView.setText("");
        }

        holder.simplexSheetsTextView.setText(String.valueOf(data.getSimplexSheets()));
        holder.duplexSheetsTextView.setText(String.valueOf(data.getDuplexSheets()));
        holder.totalSheetsTextView.setText(String.valueOf(data.getTotalSheets()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexTextView;
        public TextView mediaSizeTextView;
        public TextView mediaSizeTypeTextView;
        public TextView simplexSheetsTextView;
        public TextView duplexSheetsTextView;
        public TextView totalSheetsTextView;

        ViewHolder(View view) {
            super(view);
            indexTextView = view.findViewById(R.id.indexTextView);
            mediaSizeTextView = view.findViewById(R.id.jobCategoryTextView);
            mediaSizeTypeTextView = view.findViewById(R.id.jobCategoryTypeTextView);
            simplexSheetsTextView = view.findViewById(R.id.mediaSizeTextView);
            duplexSheetsTextView = view.findViewById(R.id.mediaSizeTypeTextView);
            totalSheetsTextView = view.findViewById(R.id.impressionsTextView);
        }
    }
}
