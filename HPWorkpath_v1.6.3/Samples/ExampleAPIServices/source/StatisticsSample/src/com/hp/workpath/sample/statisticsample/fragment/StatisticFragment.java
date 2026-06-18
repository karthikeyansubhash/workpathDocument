// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment;

import static com.hp.workpath.sample.statisticsample.StatisticActivity.INDEX;
import static com.hp.workpath.sample.statisticsample.StatisticActivity.LAST_JOB;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.task.JobInfoTask;
import com.hp.workpath.sample.statisticsample.task.LastJobInfoTask;
import com.hp.workpath.sample.statisticsample.view.StatisticJobInfoView;

import java.util.List;

public class StatisticFragment extends Fragment {

    TextView mStatisticRawDataTextView;
    ProgressBar mProgressBar;

    StatisticsJobData mStatisticJobData;
    StatisticJobInfoView statisticView;

    int mIndex = 0;
    boolean isLastJob = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        mStatisticRawDataTextView = view.findViewById(R.id.statisticRawDataTextView);
        mStatisticRawDataTextView.setMovementMethod(new ScrollingMovementMethod());
        mProgressBar = view.findViewById(R.id.progressBar);

        statisticView = new StatisticJobInfoView(inflater, view);
        isLastJob = getArguments().getBoolean(LAST_JOB);
        if (isLastJob) {
            getLastJobInfo();
        } else {
            mIndex = getArguments().getInt(INDEX);
            getJobInfoList();
        }
        return view;
    }

    private void setRawData(StatisticsJobData statisticsJobData) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        mStatisticRawDataTextView.setText(gson.toJson(statisticsJobData));
    }

    private void getJobInfoList() {
        mProgressBar.setVisibility(View.VISIBLE);
        new JobInfoTask(getContext(), statisticsInterface).taskExecute(mIndex);
    }

    private void getLastJobInfo() {
        mProgressBar.setVisibility(View.VISIBLE);
        new LastJobInfoTask(getContext(), statisticsInterface).taskExecute();
    }

    private void setStatisticsJobData(StatisticsJobData statisticsJobData) {
        statisticView.setStatisticJobData(statisticsJobData);
    }

    private void showStatisticJobData() {
        if (mStatisticJobData != null) {
            setRawData(mStatisticJobData);
            setStatisticsJobData(mStatisticJobData);
        } else {
            mStatisticRawDataTextView.setText("There is no statistic information");
        }
    }

    ResponseInterface statisticsInterface = new ResponseInterface() {
        @Override
        public void success(List<StatisticsJobData> info) {
            mProgressBar.setVisibility(View.GONE);
            if (info != null && info.size() > 0) {
                mStatisticJobData = info.get(0);
                if (TextUtils.isEmpty(mStatisticJobData.getJobId())) {
                    mStatisticRawDataTextView.setText("There is no statistic information");
                } else {
                    showStatisticJobData();
                }
            } else {
                mStatisticRawDataTextView.setText("There is no statistic information");
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };
}
