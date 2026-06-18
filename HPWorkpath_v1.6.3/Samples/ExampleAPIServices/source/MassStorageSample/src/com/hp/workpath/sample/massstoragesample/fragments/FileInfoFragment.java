// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.sample.massstoragesample.R;
import com.hp.workpath.sample.massstoragesample.task.ReadFileTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfoFragment extends DialogFragment {
    public static final String CUSTOMER_DATA_FILE_KEY = "customerDataFile";

    private CustomerDataFile mCustomerDataFile;
    private EditText mContentEditText;
    private EditText mNameEditText;
    private ProgressBar mProgressBar;

    public static FileInfoFragment newInstance(CustomerDataFile customerDataFile) {
        FileInfoFragment f = new FileInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_file_info, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.file_info)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void findViewElements(View view) {
        mCustomerDataFile = getArguments().getParcelable(CUSTOMER_DATA_FILE_KEY);

        ViewGroup getNameItem = view.findViewById(R.id.getNameItem);
        ((TextView) getNameItem.findViewById(R.id.titleTextView)).setText(getString(R.string.name));
        mProgressBar = view.findViewById(R.id.progressBar);
        mContentEditText = view.findViewById(R.id.contentTextView);
        mContentEditText.setEnabled(false);
        mNameEditText = getNameItem.findViewById(R.id.summaryTextView);
        mNameEditText.setText(mCustomerDataFile.getName());
        mNameEditText.setEnabled(false);

        ViewGroup getPathItem = view.findViewById(R.id.getPathItem);
        ((TextView) getPathItem.findViewById(R.id.titleTextView)).setText(getString(R.string.path_name));
        ((TextView) getPathItem.findViewById(R.id.summaryTextView)).setText(mCustomerDataFile.getPath());
        getPathItem.findViewById(R.id.summaryTextView).setEnabled(false);

        long fileLength = mCustomerDataFile.length();
        ViewGroup lengthItem = view.findViewById(R.id.lengthItem);
        ((TextView) lengthItem.findViewById(R.id.titleTextView)).setText(getString(R.string.length));
        ((TextView) lengthItem.findViewById(R.id.summaryTextView)).setText(Long.toString(fileLength));
        lengthItem.findViewById(R.id.summaryTextView).setEnabled(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastModified = mCustomerDataFile.lastModified() + " (" + sdf.format(new Date(mCustomerDataFile.lastModified())) + ")";
        ViewGroup lastModifiedItem = view.findViewById(R.id.lastModifiedItem);
        ((TextView) lastModifiedItem.findViewById(R.id.titleTextView)).setText(getString(R.string.last_modified));
        ((TextView) lastModifiedItem.findViewById(R.id.summaryTextView)).setText(lastModified);
        lastModifiedItem.findViewById(R.id.summaryTextView).setEnabled(false);

        if (fileLength > 0) {
            new ReadFileTask(getActivity(), mCustomerDataFile, mReadFileTaskInterface).taskExecute();
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    ReadFileTask.ReadFileTaskInterface mReadFileTaskInterface = new ReadFileTask.ReadFileTaskInterface() {
        @Override
        public void fileContent(String content) {
            mProgressBar.setVisibility(View.GONE);
            mContentEditText.setText(content);
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}