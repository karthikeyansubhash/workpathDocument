// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment;

import static com.hp.workpath.sample.statisticsample.StatisticActivity.INDEX;
import static com.hp.workpath.sample.statisticsample.StatisticActivity.LAST_JOB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.MainActivity;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.StatisticActivity;
import com.hp.workpath.sample.statisticsample.dialog.CommitDialogFragment;
import com.hp.workpath.sample.statisticsample.task.JobInfoTask;
import com.hp.workpath.sample.statisticsample.view.StatisticListAdapter;

import java.util.List;

public class StatisticListFragment extends Fragment implements View.OnClickListener {

    private static final int DIALOG_FRAGMENT = 1;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    private Button mCommitButton;
    private Button mGetLastJobInfoButton;
    private RecyclerView mListView;
    private StatisticListAdapter mListAdapter;
    private ProgressBar mProgressBar;

    private List<StatisticsJobData> mJobDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic_list, container, false);
        mCommitButton = view.findViewById(R.id.commitButton);
        mGetLastJobInfoButton = view.findViewById(R.id.getLastJobInfoButton);
        mListView = view.findViewById(R.id.jobInfoListView);
        mListAdapter = new StatisticListAdapter(listOnClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        mListView.addItemDecoration(dividerItemDecoration);
        mProgressBar = view.findViewById(R.id.progressBar);
        mCommitButton.setOnClickListener(this);
        mGetLastJobInfoButton.setOnClickListener(this);
        if (SCREEN_4_3_INCH.equals((String)view.findViewById(R.id.container).getTag())) {
            view.findViewById(R.id.fabMenu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(view.findViewById(R.id.layoutBottom).getVisibility() == View.VISIBLE){
                        view.findViewById(R.id.layoutBottom).setVisibility(View.GONE);
                    }else{
                        view.findViewById(R.id.layoutBottom).setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).refreshJobInfoList();
        if (mJobDataList != null) {
            mListAdapter.setItem(mJobDataList);
        }
    }

    View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = mListView.getChildLayoutPosition(v);
            View childView = mListView.getLayoutManager().findViewByPosition(itemPosition);
            TextView sequenceTextView = childView.findViewById(R.id.sequenceTextView);
            int jobSequence = Integer.parseInt(sequenceTextView.getText().toString());
            startJobInfoActivity(jobSequence);

        }
    };

    private void startLastJobInfoActivity() {
        if (mJobDataList != null && mJobDataList.size() > 0) {
            Intent intent = new Intent(getContext(), StatisticActivity.class);
            intent.putExtra(LAST_JOB, true);
            startActivity(intent);
        } else {
            if (getActivity() != null) {
                Logger.showResult(getActivity(), getString(R.string.no_job_info));
            }
        }
    }

    private void startJobInfoActivity(int index) {
        if (mJobDataList != null && index >= 0) {
            Intent intent = new Intent(getContext(), StatisticActivity.class);
            intent.putExtra(INDEX, index);
            startActivity(intent);
        } else {
            if (getActivity() != null) {
                Logger.showResult(getActivity(), getString(R.string.over_range));
            }
        }
    }

    public void getJobInfoList() {
        mProgressBar.setVisibility(View.VISIBLE);
        mListAdapter.clear();
        new JobInfoTask(getContext(), statisticsInterface).taskExecute();
    }

    @Override
    public void onClick(View v) {
        if (v == mCommitButton) {
            int totalTaskCount = ((MainActivity) getActivity()).getTotalTaskCount();
            CommitDialogFragment dialog = CommitDialogFragment.newInstance(totalTaskCount);
            dialog.setTargetFragment(this, DIALOG_FRAGMENT);
            dialog.show(getFragmentManager(), "dialog");
        } else if (v == mGetLastJobInfoButton) {
            startLastJobInfoActivity();
        }
    }

    ResponseInterface statisticsInterface = new ResponseInterface() {
        @Override
        public void success(List<StatisticsJobData> info) {
            mProgressBar.setVisibility(View.GONE);
            mJobDataList = info;
            if (mJobDataList != null && mJobDataList.size() > 0) {
                mListAdapter.setItem(mJobDataList);
            } else {
                if (getActivity() != null) {
                    Logger.showResult(getActivity(), getString(R.string.no_job_info));
                }
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_FRAGMENT:
                    ((MainActivity) getActivity()).refreshJobInfoList();
                    break;
            }
        }
    }
}
