// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.copier.StoredJobInfo;
import com.hp.workpath.sample.copysample.R;

import java.util.ArrayList;
import java.util.List;

public class ListDialogFragment extends DialogFragment {

    public static final String CURRENT_SELECTED = "currentSelected";
    public static final String STORED_JOB_INFO_LIST = "storedJobInfoList";

    private TextView mEmptyTextView;
    private RadioGroup mRadioGroup;

    private ArrayList<StoredJobInfo> storedJobInfoList;
    private String currentStoredJobId;

    public static ListDialogFragment newInstance(List<StoredJobInfo> storedJobInfoList,
                                                 String currentStoredJobId) {
        ListDialogFragment f = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_SELECTED, currentStoredJobId);
        args.putParcelableArrayList(STORED_JOB_INFO_LIST, new ArrayList<>(storedJobInfoList));
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_radio_list, null);
        storedJobInfoList = getArguments().getParcelableArrayList(STORED_JOB_INFO_LIST);
        currentStoredJobId = getArguments().getString(CURRENT_SELECTED);
        findViewElements(view);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.stored_job)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        mEmptyTextView = view.findViewById(R.id.emptyTextView);
        mRadioGroup = view.findViewById(R.id.radioGroup);

        if (storedJobInfoList.size() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < storedJobInfoList.size(); i++) {
                final AppCompatRadioButton radio = new AppCompatRadioButton(getActivity());
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 5);
                radio.setLayoutParams(params);

                StoredJobInfo storedJobInfo = storedJobInfoList.get(i);
                StringBuilder infoStringBuilder = new StringBuilder();
                infoStringBuilder.append("(").append(storedJobInfo.getStoredJobId()).append(")\n")
                        .append("FolderName: ").append(storedJobInfo.getStoredJobFolderName()).append(", ")
                        .append("JobName: ").append(storedJobInfo.getStoredJobName());
                radio.setText(infoStringBuilder.toString());
                radio.setTag(storedJobInfo.getStoredJobId());
                if (!TextUtils.isEmpty(currentStoredJobId)
                        && currentStoredJobId.equals(storedJobInfo.getStoredJobId())) {
                    radio.setChecked(true);
                }
                radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Intent intent = new Intent();
                        intent.putExtra(CURRENT_SELECTED, (String) radio.getTag());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        getDialog().dismiss();
                    }
                });
                mRadioGroup.addView(radio);
            }
        }
    }

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}
