// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.sample.statisticsample.R;

import java.util.ArrayList;
import java.util.List;

public class StatisticListAdapter extends RecyclerView.Adapter<StatisticListAdapter.ViewHolder> {

    List<StatisticsJobData> mJobDataList = new ArrayList<>();
    View.OnClickListener mListOnClickListener;

    public StatisticListAdapter(View.OnClickListener listener) {
        mListOnClickListener = listener;
    }

    public StatisticsJobData getItem(int position) {
        return mJobDataList.get(position);
    }

    public void setItem(List<StatisticsJobData> jobDataList) {
        mJobDataList = jobDataList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = mJobDataList.size();
        if (size > 0) {
            mJobDataList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mJobDataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_jobinfo_list, parent, false);
        view.setOnClickListener(mListOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatisticsJobData jobData = getItem(position);
        holder.sequenceTextView.setText(String.valueOf(jobData.getJobSequence()));
        holder.jobIdTextView.setText(jobData.getJobId());
        if (jobData.getJobInfo() != null) {
            if (jobData.getJobInfo().getJobCategory() != null) {
                holder.jobCategoryTextView.setText(jobData.getJobInfo().getJobCategory().name());
            }
            holder.jobNameTextView.setText(jobData.getJobInfo().getDeviceJobName());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sequenceTextView;
        public TextView jobIdTextView;
        public TextView jobCategoryTextView;
        public TextView jobNameTextView;

        ViewHolder(View view) {
            super(view);
            sequenceTextView = view.findViewById(R.id.sequenceTextView);
            jobIdTextView = view.findViewById(R.id.jobIdTextView);
            jobCategoryTextView = view.findViewById(R.id.jobCategoryTextView);
            jobNameTextView = view.findViewById(R.id.jobNameTextView);
        }
    }
}
