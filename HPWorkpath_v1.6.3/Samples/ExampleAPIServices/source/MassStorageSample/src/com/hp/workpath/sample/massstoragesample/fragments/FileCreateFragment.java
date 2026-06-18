// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.sample.massstoragesample.Logger;
import com.hp.workpath.sample.massstoragesample.MainActivity;
import com.hp.workpath.sample.massstoragesample.R;
import com.hp.workpath.sample.massstoragesample.task.CreateFileTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileCreateFragment extends DialogFragment {

    public static final String CUSTOMER_DATA_FILE_KEY = "customerDataFile";
    public static final String MASS_STORAGE_DATA_KEY = "massStorageDataKey";

    private View view;
    private RadioGroup mFileTypeRadioGroup;
    private EditText mFileNameEditText;
    private EditText mContentEditText;
    private Button mRemoveButton;

    private CustomerDataFile mCustomerDataFile;
    private MassStorageInfo mMassStorageInfo;

    public static FileCreateFragment newInstance(MassStorageInfo massStorageInfo, CustomerDataFile customerDataFile) {
        FileCreateFragment f = new FileCreateFragment();
        Bundle args = new Bundle();
        args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile);
        args.putParcelable(MASS_STORAGE_DATA_KEY, massStorageInfo);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_file_create, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_file)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setPositiveButton(R.string.create, mCreateListener)
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
        mMassStorageInfo = getArguments().getParcelable(MASS_STORAGE_DATA_KEY);

        ViewGroup getNameItem = view.findViewById(R.id.createNameItem);
        ((TextView) getNameItem.findViewById(R.id.titleTextView)).setText(getString(R.string.name));

        mFileTypeRadioGroup = view.findViewById(R.id.fileTypeRadioGroup);
        mFileTypeRadioGroup.setOnCheckedChangeListener(mRadioListener);
        mContentEditText = view.findViewById(R.id.contentEditText);
        mFileNameEditText = getNameItem.findViewById(R.id.summaryTextView);
        mFileNameEditText.setEnabled(true);
        mFileNameEditText.setClickable(true);
        mFileNameEditText.setCursorVisible(true);
        mRemoveButton = getNameItem.findViewById(R.id.removeButton);
        mRemoveButton.setVisibility(View.VISIBLE);
        mRemoveButton.setOnClickListener(mOnClickListener);
        setTempFilename();
    }

    private void setTempFilename() {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".txt";
        mFileNameEditText.setText(fileName);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mFileNameEditText.setText("");
        }
    };

    private RadioGroup.OnCheckedChangeListener mRadioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.radioFileButton:
                    setTempFilename();
                    mContentEditText.setVisibility(View.VISIBLE);
                    break;
                case R.id.radioDirectoryButton:
                    mFileNameEditText.setText("");
                    mContentEditText.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

    private DialogInterface.OnClickListener mCreateListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String path = mCustomerDataFile.getPath();
            if (!"/".equals(path.substring(path.length() - 1))) {
                path += "/";
            }
            String filePath = path + mFileNameEditText.getText().toString();
            Log.i(MainActivity.TAG, "CreateFile Path: " + filePath);

            CustomerDataFile customerDataFile = new CustomerDataFile(getActivity(), mMassStorageInfo, filePath);
            String content = mContentEditText.getText().toString();

            ((MainActivity) getActivity()).enableButton(false);
            if (mFileTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioFileButton) {
                new CreateFileTask((MainActivity) getActivity(), customerDataFile, content).taskExecute();
            } else {
                boolean result = customerDataFile.mkdir();
                Logger.showResult(getActivity(), "mkdir: " + result);
                if (result) {
                    ((MainActivity) getActivity()).displayFileList(customerDataFile.getParentFile());
                }
            }
        }
    };
}