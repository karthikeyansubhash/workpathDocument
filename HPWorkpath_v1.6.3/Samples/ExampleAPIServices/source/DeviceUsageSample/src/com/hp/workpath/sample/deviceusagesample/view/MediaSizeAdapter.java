// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.deviceusage.scanner.ScannerInfo;
import com.hp.workpath.sample.deviceusagesample.R;

import java.util.ArrayList;
import java.util.List;

public class MediaSizeAdapter extends RecyclerView.Adapter<MediaSizeAdapter.ViewHolder> {

    List<ScannerInfo.ByMediaSize> mByMediaSizeList = new ArrayList<>();

    public ScannerInfo.ByMediaSize getItem(int position) {
        return mByMediaSizeList.get(position);
    }

    public void setItem(List<ScannerInfo.ByMediaSize> byMediaSizeList) {
        mByMediaSizeList = byMediaSizeList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mByMediaSizeList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_by_media_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannerInfo.ByMediaSize data = getItem(position);
        holder.indexTextView.setText(Integer.toString(position + 1));
        holder.mediaSizeTextView.setText(data.getMediaSize());
        if (data.getMediaSizeType() != null) {
            holder.mediaSizeTypeTextView.setText(data.getMediaSizeType().name());
        }
        holder.imagesTextView.setText(Integer.toString(data.getImages()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexTextView;
        public TextView mediaSizeTextView;
        public TextView mediaSizeTypeTextView;
        public TextView imagesTextView;

        ViewHolder(View view) {
            super(view);
            indexTextView = view.findViewById(R.id.indexTextView);
            mediaSizeTextView = view.findViewById(R.id.mediaSizeTextView);
            mediaSizeTypeTextView = view.findViewById(R.id.mediaSizeTypeTextView);
            imagesTextView = view.findViewById(R.id.imagesTextView);
        }
    }
}
