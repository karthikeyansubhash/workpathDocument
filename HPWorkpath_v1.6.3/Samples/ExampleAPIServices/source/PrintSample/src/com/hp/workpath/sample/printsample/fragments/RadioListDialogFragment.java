// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments;

import static com.hp.workpath.sample.printsample.MainActivity.REQUEST_KEY;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.sample.printsample.R;

import java.util.ArrayList;
import java.util.List;

public class RadioListDialogFragment extends DialogFragment {

    private static String BATCH_JOB_LIST = "batchjoblist";
    public static String BACKGROUND_JOB = "backgroundjob";

    private List<PrintAttributes> batchjobsList;
    private int currentSelected = 0;
    private boolean backgroundjob=false;
    private String[] batch_jobslist;
    private String[] stream_joblist;

    private TextView mEmptyTextView;
    private RadioGroup mRadioGroup;
    private SharedPreferences mPrefs;

    public static RadioListDialogFragment newInstance(ArrayList<PrintAttributes> batchJobs,
                                                      Boolean backgroundJob) {
        RadioListDialogFragment f = new RadioListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BATCH_JOB_LIST,new ArrayList<>(batchJobs));
        args.putBoolean(BACKGROUND_JOB,backgroundJob);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_radio_list, null);
        batch_jobslist = getResources().getStringArray(R.array.batch_backgroundjob);
        stream_joblist = getResources().getStringArray(R.array.batch_stream);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        batchjobsList = requireArguments().getParcelableArrayList(BATCH_JOB_LIST);
        backgroundjob = requireArguments().getBoolean(BACKGROUND_JOB);



        String title = "Select print mode";
        if(!batchjobsList.isEmpty()){
            if(batchjobsList.size()>1){
                title+=" ("+batchjobsList.size()+" Jobs are listed)";
            }else{
                title+=" ("+batchjobsList.size()+" Job listed)";
            }

        }
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setPositiveButton("Print",mPrintListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        mEmptyTextView = view.findViewById(R.id.emptyTextView);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        final PrintAttributes.Source source = PrintAttributes.Source.valueOf(
                mPrefs.getString(PrintConfigureFragment.PREF_SOURCE, PrintAttributes.Source.STORAGE.name()));

        if(PrintAttributes.Source.STREAM.equals(source)){
            radioList(stream_joblist);
        }else{
            radioList(batch_jobslist);
        }

    }

    private void radioList(String[] batch_jobslist) {
        for (int i = 0; i < batch_jobslist.length; i++) {
            final AppCompatRadioButton radio = new AppCompatRadioButton(getActivity());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            radio.setLayoutParams(params);
            radio.setText(batch_jobslist[i]);
            radio.setTag(i);
            radio.setId(i);
            if(i == currentSelected){
                radio.setChecked(true);
                backgroundjob = false;
            }
            mRadioGroup.addView(radio);
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                backgroundjob = currentSelected != checkedId;
                Log.d("TAG", "findViewElements: "+backgroundjob);

            }
        });
    }

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

    private DialogInterface.OnClickListener mPrintListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Bundle bundle=new Bundle();
            bundle.putBoolean(BACKGROUND_JOB,backgroundjob);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY,bundle);
        }
    };
}
