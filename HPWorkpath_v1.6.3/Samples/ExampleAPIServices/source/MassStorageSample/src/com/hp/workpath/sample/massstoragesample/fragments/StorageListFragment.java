// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.sample.massstoragesample.MainActivity;
import com.hp.workpath.sample.massstoragesample.R;

import java.util.ArrayList;
import java.util.List;

public class StorageListFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;
    public static final String STORAGE_INFO_LIST = "massStorageInfoList";

    private RadioGroup mStorageRadioGroup;

    private ArrayList<MassStorageInfo> mMassStorageInfoList;
    private ISelectedStorageListener mListener;

    public static StorageListFragment newInstance(List<MassStorageInfo> massStorageInfoList) {
        StorageListFragment f = new StorageListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(STORAGE_INFO_LIST, new ArrayList<>(massStorageInfoList));
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ISelectedStorageListener) activity;
        } catch (ClassCastException e) {
            Toast.makeText(activity, activity.getClass().getSimpleName() + " must implement ISelectedStorageListener", Toast.LENGTH_SHORT).show();
            Log.e(TAG, activity.toString() + " must implement ISelectedStorageListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_storage_list, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.storage_list)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener == null) {
            dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void findViewElements(View view) {
        mStorageRadioGroup = view.findViewById(R.id.storageRadioGroup);

        mMassStorageInfoList = getArguments().getParcelableArrayList(STORAGE_INFO_LIST);

        for (int i = 0; i < mMassStorageInfoList.size(); i++) {
            final AppCompatRadioButton radio = new AppCompatRadioButton(getActivity());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            radio.setLayoutParams(params);

            radio.setText(mMassStorageInfoList.get(i).getName());
            radio.setTag(i);
            radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mListener.selectedStorage((int) radio.getTag());
                    getDialog().dismiss();
                }
            });
            mStorageRadioGroup.addView(radio);
        }
    }

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}