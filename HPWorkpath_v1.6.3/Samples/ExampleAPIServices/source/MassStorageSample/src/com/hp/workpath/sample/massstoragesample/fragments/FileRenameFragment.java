// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.hp.workpath.sample.massstoragesample.task.RenameFileTask;

public class FileRenameFragment extends DialogFragment {
    public static final String CUSTOMER_DATA_FILE_KEY = "customerDataFile";
    public static final String MASS_STORAGE_DATA_KEY = "massStorageDataKey";

    private CustomerDataFile mCustomerDataFile;
    private MassStorageInfo mMassStorageInfo;
    private View view;
    private EditText mNameTextView;
    private Button mRemoveButton;


    public static FileRenameFragment newInstance(MassStorageInfo massStorageInfo, CustomerDataFile customerDataFile) {
        FileRenameFragment f = new FileRenameFragment();
        Bundle args = new Bundle();
        args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile);
        args.putParcelable(MASS_STORAGE_DATA_KEY, massStorageInfo);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_file_rename, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.rename_file)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setPositiveButton(R.string.rename, mCreateListener)
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

        ViewGroup getNameItem = view.findViewById(R.id.renameNameItem);
        ((TextView) getNameItem.findViewById(R.id.titleTextView)).setText(getString(R.string.name));

        mNameTextView = getNameItem.findViewById(R.id.summaryTextView);
        mNameTextView.setText(mCustomerDataFile.getName());
        mNameTextView.setEnabled(true);
        mNameTextView.setClickable(true);
        mNameTextView.setCursorVisible(true);
        mRemoveButton = getNameItem.findViewById(R.id.removeButton);
        mRemoveButton.setVisibility(View.VISIBLE);
        mRemoveButton.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mNameTextView.setText("");
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
            String path = "";
            if (mCustomerDataFile.getParentFile() != null) {
                path += mCustomerDataFile.getParentFile().getPath();
            }
            if (!"/".equals(path.substring(path.length() - 1))) {
                path += "/";
            }
            if (!TextUtils.isEmpty(mNameTextView.getText().toString())) {
                String filePath = path + mNameTextView.getText().toString();
                Log.i(MainActivity.TAG, "renameTo Path: " + filePath);

                ((MainActivity) getActivity()).enableButton(false);
                ((MainActivity) getActivity()).showProgress(View.VISIBLE);
                CustomerDataFile dest = new CustomerDataFile(getActivity(), mMassStorageInfo, filePath);
                new RenameFileTask((MainActivity) getActivity(), mCustomerDataFile, dest).taskExecute();

            } else {
                if (getContext() != null) {
                    Logger.showResult(getActivity(), getString(R.string.input_value));
                }
            }
        }
    };
}