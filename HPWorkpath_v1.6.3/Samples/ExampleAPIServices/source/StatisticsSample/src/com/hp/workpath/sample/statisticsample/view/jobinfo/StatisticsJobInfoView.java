// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.jobinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.StatisticsAttributes;
import com.hp.workpath.api.statistics.jobinfo.StatisticsJobInfo;
import com.hp.workpath.api.statistics.jobinfo.Timestamp;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class StatisticsJobInfoView {

    LinearLayout rootView;
    View view;

    ViewGroup layoutJobInfoJobId;
    ViewGroup layoutJobInfoApplicationName;
    ViewGroup layoutJobInfoDeviceJobName;
    ViewGroup layoutJobInfoJobCategory;
    ViewGroup layoutJobInfoJobDataSource;
    ViewGroup layoutJobInfoJobDestinations;
    ViewGroup layoutJobInfoJobDoneStatus;
    ViewGroup layoutJobPaused;
    ViewGroup layoutJobInfoParentJobId;
    ViewGroup layoutJobInfoProcessedBy;

    ViewGroup layoutJobDoneTimeStampOffset;
    ViewGroup layoutJobDoneTimeStampTime;
    ViewGroup layoutJobStartedTimeStampOffset;
    ViewGroup layoutJobStartedTimeStampTime;

    public StatisticsJobInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_statistic_job_info, rootView, false);
        initViewJobInfo(view);
    }

    public void setStatisticsJobInfo(StatisticsJobInfo statisticsJobInfo) {
        rootView.removeAllViews();
        if (statisticsJobInfo != null) {
            Utils.setSummary(layoutJobInfoJobId, statisticsJobInfo.getJobId());
            Utils.setSummary(layoutJobInfoApplicationName, statisticsJobInfo.getApplicationName());
            Utils.setSummary(layoutJobInfoDeviceJobName, statisticsJobInfo.getDeviceJobName());
            Utils.setSummary(layoutJobInfoJobCategory, statisticsJobInfo.getJobCategory());
            Utils.setSummary(layoutJobInfoJobDataSource, statisticsJobInfo.getJobDataSource());
            String destination = "";
            if (statisticsJobInfo.getJobDestinations() != null) {
                for (StatisticsAttributes.JobDestination jobDestination : statisticsJobInfo.getJobDestinations()) {
                    destination += jobDestination.name() + " ";
                }
            }
            Utils.setSummary(layoutJobInfoJobDestinations, destination);
            Utils.setSummary(layoutJobInfoJobDoneStatus, statisticsJobInfo.getJobDoneStatus());
            setJobDoneTimeStamp(statisticsJobInfo.getJobDoneTimestamp());
            Utils.setSummary(layoutJobPaused, statisticsJobInfo.getJobPaused());
            setJobStartedTimeStamp(statisticsJobInfo.getJobStartedTimestamp());
            Utils.setSummary(layoutJobInfoParentJobId, statisticsJobInfo.getParentJobId());
            Utils.setSummary(layoutJobInfoProcessedBy, statisticsJobInfo.getProcessedBy());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setJobDoneTimeStamp(Timestamp timeStamp) {
        if (timeStamp != null) {
            Utils.setSummary(layoutJobDoneTimeStampOffset, timeStamp.getOffset());
            Utils.setSummary(layoutJobDoneTimeStampTime, timeStamp.getTime());
        } else {
            LinearLayout parent = (LinearLayout) layoutJobDoneTimeStampOffset.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setJobStartedTimeStamp(Timestamp timeStamp) {
        if (timeStamp != null) {
            Utils.setSummary(layoutJobStartedTimeStampOffset, timeStamp.getOffset());
            Utils.setSummary(layoutJobStartedTimeStampTime, timeStamp.getTime());
        } else {
            LinearLayout parent = (LinearLayout) layoutJobStartedTimeStampOffset.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewJobInfo(View view) {
        layoutJobInfoJobId = Utils.setTitle(view.findViewById(R.id.layoutJobInfoJobId), R.string.jobInfoJobId);
        layoutJobInfoApplicationName = Utils.setTitle(view.findViewById(R.id.layoutJobInfoApplicationName), R.string.jobInfoApplicationName);
        layoutJobInfoDeviceJobName = Utils.setTitle(view.findViewById(R.id.layoutJobInfoDeviceJobName), R.string.jobInfoDeviceJobName);
        layoutJobInfoJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobInfoJobCategory), R.string.jobInfoJobCategory);
        layoutJobInfoJobDataSource = Utils.setTitle(view.findViewById(R.id.layoutJobInfoJobDataSource), R.string.jobInfoJobDataSource);
        layoutJobInfoJobDestinations = Utils.setTitle(view.findViewById(R.id.layoutJobInfoJobDestinations), R.string.jobInfoJobDestinations);
        layoutJobInfoJobDoneStatus = Utils.setTitle(view.findViewById(R.id.layoutJobInfoJobDoneStatus), R.string.jobInfoJobDoneStatus);
        layoutJobPaused = Utils.setTitle(view.findViewById(R.id.layoutJobPaused), R.string.jobPaused);

        layoutJobInfoParentJobId = Utils.setTitle(view.findViewById(R.id.layoutJobInfoParentJobId), R.string.jobInfoParentJobId);
        layoutJobInfoProcessedBy = Utils.setTitle(view.findViewById(R.id.layoutJobInfoProcessedBy), R.string.jobInfoProcessedBy);

        ((TextView) view.findViewById(R.id.titleJobDoneTimestampTextView)).setText(R.string.jobDoneTimestamp);
        ((TextView) view.findViewById(R.id.titleJobStartedTimestampTextView)).setText(R.string.jobStartedTimestamp);

        layoutJobDoneTimeStampOffset = Utils.setTitle(view.findViewById(R.id.layoutJobDoneTimestampOffset), R.string.offset);
        layoutJobDoneTimeStampTime = Utils.setTitle(view.findViewById(R.id.layoutJobDoneTimestampTime), R.string.time);
        layoutJobStartedTimeStampOffset = Utils.setTitle(view.findViewById(R.id.layoutJobStartedTimestampOffset), R.string.offset);
        layoutJobStartedTimeStampTime = Utils.setTitle(view.findViewById(R.id.layoutJobStartedTimestampTime), R.string.time);
    }
}
