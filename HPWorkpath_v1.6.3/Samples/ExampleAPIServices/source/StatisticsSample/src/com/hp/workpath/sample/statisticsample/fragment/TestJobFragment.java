// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.JobService;
import com.hp.workpath.api.job.JobletAttributes;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.MainActivity;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.task.LastJobInfoTask;
import com.hp.workpath.sample.statisticsample.task.RequestPrintTask;
import com.hp.workpath.sample.statisticsample.task.RequestScanTask;

import java.io.File;
import java.util.List;

public class TestJobFragment extends Fragment implements View.OnClickListener {

    Button mGetLastJobInfoButton;
    Button mPrintButton;
    Button mScanButton;
    TextView mJobInfoTextView;
    TextView mStatisticInfoTextView;
    ProgressBar mProgressBar;

    private JobObserver mJobObserver = null;
    private String mRid = null;
    private String mJobId = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobObserver = new JobObserver(new Handler());
        mJobObserver.register(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_job, container, false);
        mGetLastJobInfoButton = view.findViewById(R.id.getLastJobInfoButton);
        mGetLastJobInfoButton.setOnClickListener(this);
        mPrintButton = view.findViewById(R.id.printButton);
        mPrintButton.setOnClickListener(this);
        mScanButton = view.findViewById(R.id.scanButton);
        mScanButton.setOnClickListener(this);
        mJobInfoTextView = view.findViewById(R.id.jobInfoTextView);
        mJobInfoTextView.setMovementMethod(new ScrollingMovementMethod());
        mStatisticInfoTextView = view.findViewById(R.id.statisticInfoTextView);
        mStatisticInfoTextView.setMovementMethod(new ScrollingMovementMethod());
        mProgressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mJobObserver.unregister(getContext());
    }

    @Override
    public void onClick(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        if (v == mGetLastJobInfoButton) {
            new LastJobInfoTask(getContext(), statisticsInterface).taskExecute();
        } else if (v == mPrintButton) {
            PrintAttributesCaps printCaps = ((MainActivity) getActivity()).getPrintCaps();
            if (printCaps == null) {
                Logger.showResult(getActivity(), "Print is not supported");
                mProgressBar.setVisibility(View.GONE);
                return;
            }
            enableButton(false);
            cleanJobInfo();
            try {
                String path = getActivity().getFilesDir().getPath() + "/test_page.pdf";
                PrintAttributes attributes = new PrintAttributes.PrintFromStorageBuilder(Uri.fromFile(new File(path)))
                        .build(printCaps);
                new RequestPrintTask(this, attributes).taskExecute();
            } catch (CapabilitiesExceededException e) {
                enableButton(true);
                mProgressBar.setVisibility(View.GONE);
                Logger.showResult(getActivity(), "PrintAttributes.PrintFromStorageBuilder" + e.getMessage());
            }
        } else if (v == mScanButton) {
            ScanAttributesCaps scanCaps = ((MainActivity) getActivity()).getScanCaps();
            if (scanCaps == null) {
                Logger.showResult(getActivity(), "Scan is not supported");
                mProgressBar.setVisibility(View.GONE);
                return;
            }
            enableButton(false);
            cleanJobInfo();
            try {
                ScanAttributes.MeBuilder attributesBuilder = new ScanAttributes.MeBuilder();
                attributesBuilder.setDocumentFormat(ScanAttributes.DocumentFormat.PDF);
                attributesBuilder.setJobAssemblyMode(ScanAttributes.JobAssemblyMode.OFF);
                new RequestScanTask(this, attributesBuilder.build(scanCaps)).taskExecute();
            } catch (CapabilitiesExceededException e) {
                enableButton(true);
                mProgressBar.setVisibility(View.GONE);
                Logger.showResult(getActivity(), "ScanAttributes.MeBuilder" + e.getMessage());
            }
        }
    }

    private void cleanJobInfo() {
        mRid = null;
        mJobId = null;
    }

    private void enableButton(boolean enable) {
        if (mPrintButton != null) {
            mPrintButton.setEnabled(enable);
        }
        if (mScanButton != null) {
            mScanButton.setEnabled(enable);
        }
    }

    public void setRid(String rid) {
        mRid = rid;
    }

    ResponseInterface statisticsInterface = new ResponseInterface() {
        @Override
        public void success(List<StatisticsJobData> info) {
            mProgressBar.setVisibility(View.GONE);
            if (info != null && info.size() > 0) {
                StatisticsJobData data = info.get(0);
                if (TextUtils.isEmpty(data.getJobId())) {
                    mStatisticInfoTextView.setText("There is no statistic information");
                } else {
                    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                    mStatisticInfoTextView.setText(gson.toJson(data));
                }
            } else {
                mStatisticInfoTextView.setText("There is no statistic information");
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };

    private class JobObserver extends JobService.AbstractJobletObserver {

        public JobObserver(final Handler handler) {
            super(handler);
        }

        @Override
        public void onComplete(String rid, JobInfo jobInfo) {
            mProgressBar.setVisibility(View.GONE);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            mJobInfoTextView.setText(gson.toJson(jobInfo));
            enableButton(true);
        }

        @Override
        public void onProgress(String rid, JobInfo jobInfo) {
            Log.d(MainActivity.TAG, "Received onProgress for rid " + rid);
            Log.d(MainActivity.TAG, "Received onProgress jobInfo " + jobInfo);
            if (rid.equals(mRid)) {
                if (mJobId == null) {
                    if (jobInfo.getJobId() != null) {
                        mJobId = jobInfo.getJobId();
                        Log.d(MainActivity.TAG, "Received jobID as " + mJobId);
                        final JobletAttributes taskAttributes =
                                new JobletAttributes.Builder().setShowUi(true).build();

                        final String jrid = JobService.monitorJobInForeground(getActivity(), mJobId,
                                taskAttributes, null);
                        Log.d(MainActivity.TAG, "MonitorJob request: " + jrid);
                    }
                }
            }
        }

        @Override
        public void onFail(String rid, Result result) {
            Log.e(MainActivity.TAG, "Received onFail for rid " + rid + ", " + result);
            mProgressBar.setVisibility(View.GONE);
            mJobInfoTextView.setText("Received onFail for rid " + rid + ", " + Logger.build(result));
            enableButton(true);
        }

        @Override
        public void onCancel(String rid) {
            Log.d(MainActivity.TAG, "Received onCancel for rid " + rid);
            mProgressBar.setVisibility(View.GONE);
            mJobInfoTextView.setText("Received onCancel for rid " + rid);
            enableButton(true);
        }
    }
}
