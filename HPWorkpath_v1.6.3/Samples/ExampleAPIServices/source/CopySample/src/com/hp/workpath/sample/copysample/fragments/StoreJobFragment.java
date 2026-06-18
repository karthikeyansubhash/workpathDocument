// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.copier.JobCredentialsAttributes;
import com.hp.workpath.api.copier.StoredJobInfo;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;
import com.hp.workpath.sample.copysample.task.EnumerateStoredJobTask;

import java.util.ArrayList;
import java.util.List;

import static com.hp.workpath.sample.copysample.fragments.ListDialogFragment.CURRENT_SELECTED;

public final class StoreJobFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;

    RelativeLayout mEnumerateLayout;
    Button mEnumerateButton;
    Button mDetailButton;
    TextView mEnumerateTextView;
    EditText mPasswordEditText;
    ProgressBar mProgressBar;

    List<StoredJobInfo> mStoredJobInfoList = null;
    StoredJobInfo mCurrentStoredJob = null;
    SharedPreferences mPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mStoredJobInfoList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_job, container, false);
        mEnumerateLayout = view.findViewById(R.id.enumerateLayout);
        mEnumerateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnumerateListDialog();
            }
        });
        mEnumerateButton = view.findViewById(R.id.enumerateButton);
        mEnumerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(View.VISIBLE);
                new EnumerateStoredJobTask(StoreJobFragment.this).taskExecute();
            }
        });
        mEnumerateTextView = view.findViewById(R.id.enumerateTextView);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        mProgressBar = view.findViewById(R.id.progressBar);
        mDetailButton = view.findViewById(R.id.detailButton);
        mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStoredJob != null) {
                    DetailDialogFragment dialogFragment = DetailDialogFragment.newInstance(mCurrentStoredJob);
                    if (dialogFragment != null) {
                        dialogFragment.setTargetFragment(StoreJobFragment.this, DIALOG_FRAGMENT);
                        dialogFragment.show(getFragmentManager(), "dialog");
                    }
                } else {
                    if(getContext() != null) {
                        Logger.showResult(getActivity(), getString(R.string.stored_job_not_loaded));
                    }
                }
            }
        });
        initStoredJobList();
        return view;
    }

    private void initStoredJobList() {
        if (mStoredJobInfoList != null && mStoredJobInfoList.size() > 0) {
            String jobId = getCurrentStoredJobId();
            if (jobId != null) {
                selectedStoredJob(jobId);
            } else {
                selectedStoredJob(mStoredJobInfoList.get(0));
            }
        } else {
            setCurrentStoredJobId(null);
        }
    }

    public void showProgressBar(int visibility) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(visibility);
        }
    }

    public void enumerateStoredJob(List<StoredJobInfo> storedJobInfoList) {
        mStoredJobInfoList = storedJobInfoList;
        if (storedJobInfoList != null && storedJobInfoList.size() > 0) {
            String jobId = getCurrentStoredJobId();
            if (jobId != null) {
                selectedStoredJob(jobId);
            } else {
                selectedStoredJob(storedJobInfoList.get(0));
            }
            if (getContext() != null) {
                Logger.showResult(getActivity(), getString(R.string.succeed));
            }
        } else {
            if (getContext() != null) {
                Logger.showResult(getActivity(), getString(R.string.stored_job_empty));
                mEnumerateTextView.setText("");
                mPasswordEditText.setText("");
                mPasswordEditText.setVisibility(View.GONE);
            }
        }
    }

    private void selectedStoredJob(String storedJobId) {
        boolean found = false;
        for (StoredJobInfo storedJobInfo : mStoredJobInfoList) {
            if (storedJobId.equals(storedJobInfo.getStoredJobId())) {
                selectedStoredJob(storedJobInfo);
                found = true;
                break;
            }
        }
        if (!found) {
            selectedStoredJob(mStoredJobInfoList.get(0));
        }
    }

    private void selectedStoredJob(StoredJobInfo storedJobInfo) {
        if (storedJobInfo != null) {
            mCurrentStoredJob = storedJobInfo;
            setCurrentStoredJobId(storedJobInfo.getStoredJobId());
            setCopiesInPreference(storedJobInfo.getCopies());
            StringBuilder emulateStringBuilder = new StringBuilder();
            emulateStringBuilder.append("(").append(storedJobInfo.getStoredJobId()).append(")\n")
                    .append("UserName: ").append(storedJobInfo.getStoredJobUserName()).append(" / ")
                    .append("JobName: ").append(storedJobInfo.getStoredJobName());
            mEnumerateTextView.setText(emulateStringBuilder.toString());
            mPasswordEditText.setText("");
            if (JobCredentialsAttributes.PasswordType.NUMERIC.equals(storedJobInfo.getStoredJobPasswordType())) {
                mPasswordEditText.setVisibility(View.VISIBLE);
            } else {
                mPasswordEditText.setVisibility(View.GONE);
            }
        }
    }

    private void setCopiesInPreference(int copies) {
        mPrefs.edit().putString(CopyConfigureFragment.PREF_COPIES, Integer.toString(copies)).apply();
    }

    private void showEnumerateListDialog() {
        String jobId = getCurrentStoredJobId();
        if (jobId != null) {
            ListDialogFragment dialogFragment = ListDialogFragment.newInstance(mStoredJobInfoList, jobId);
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, DIALOG_FRAGMENT);
                dialogFragment.show(getFragmentManager(), "dialog");
            }
        } else {
            if(getContext() != null) {
                Logger.showResult(getActivity(), getString(R.string.stored_job_not_loaded));
            }
        }
    }

    public JobCredentialsAttributes getJobCredentials() throws CapabilitiesExceededException {
        JobCredentialsAttributes.Builder builder = new JobCredentialsAttributes.Builder();
        if (mCurrentStoredJob != null) {
            builder.setPasswordType(mCurrentStoredJob.getStoredJobPasswordType());
            builder.setPassword(mPasswordEditText.getText().toString());
        }
        return builder.build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_FRAGMENT:
                    if (data != null && data.hasExtra(CURRENT_SELECTED)) {
                        String storedJobId = data.getStringExtra(CURRENT_SELECTED);
                        selectedStoredJob(storedJobId);
                    }
                    break;
            }
        }
    }

    private String getCurrentStoredJobId() {
        if (getActivity() != null) {
            return ((MainActivity) getActivity()).getStoredJobId();
        }
        return null;
    }

    private void setCurrentStoredJobId(String storedJobId) {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setStoredJobId(storedJobId);
        }
    }
}