// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.RegistrationType;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.sample.accessorysample.Action;
import com.hp.workpath.sample.accessorysample.Logger;
import com.hp.workpath.sample.accessorysample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListDialogFragment extends DialogFragment {

    public static final String ACTION = "action";
    public static final String CURRENT_SELECTED = "currentSelected";
    public static final String RESERVED_ACCESSORY = "reservedAccessory";
    public static final String ACCESSORY_INFO_LIST = "accessoryInfoList";

    private TextView mEmptyTextView;
    private RadioGroup mRadioGroup;

    private ArrayList<AccessoryInfo> accessoryInfoList;
    private AccessoryInfo reservedAccessory;
    private Action action;
    private int currentSelected;

    public static ListDialogFragment newInstance(Action action, List<AccessoryInfo> accessoryInfoList,
                                                 int currentSelected, AccessoryInfo reservedAccessory) {
        ListDialogFragment f = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ACTION, action);
        args.putInt(CURRENT_SELECTED, currentSelected);
        args.putParcelable(RESERVED_ACCESSORY, reservedAccessory);
        args.putParcelableArrayList(ACCESSORY_INFO_LIST, new ArrayList<>(accessoryInfoList));
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_radio_list, null);
        currentSelected = getArguments().getInt(CURRENT_SELECTED);
        accessoryInfoList = getArguments().getParcelableArrayList(ACCESSORY_INFO_LIST);
        reservedAccessory = getArguments().getParcelable(RESERVED_ACCESSORY);
        action = (Action) getArguments().getSerializable(ACTION);
        String title = "";
        if (Action.GET_OWNED.equals(action)) {
            title = getString(R.string.get_owned_accessories);
        } else if (Action.ENUMERATE.equals(action)) {
            title = getString(R.string.attached_accessories);
        }
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        mEmptyTextView = view.findViewById(R.id.emptyTextView);
        mRadioGroup = view.findViewById(R.id.radioGroup);

        if (accessoryInfoList.size() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < accessoryInfoList.size(); i++) {
                final AppCompatRadioButton radio = new AppCompatRadioButton(getActivity());
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 5, 0, 5);
                radio.setLayoutParams(params);

                HIDAccessoryInfo hidAccessoryInfo = accessoryInfoList.get(i).getDetails();
                String info = Logger.build(hidAccessoryInfo);
                radio.setText(info);
                radio.setTag(i);
                if (i == currentSelected) {
                    radio.setChecked(true);
                }

                if (reservedAccessory != null && reservedAccessory instanceof HIDAccessoryInfo) {
                    HIDAccessoryInfo reservedAccessoryInfo = reservedAccessory.getDetails();
                    if (hidAccessoryInfo.getProductId() == reservedAccessoryInfo.getProductId()
                            && hidAccessoryInfo.getVendorId() == reservedAccessoryInfo.getVendorId()
                            && Objects.equals(hidAccessoryInfo.getSerialNumber(), reservedAccessoryInfo.getSerialNumber())) {
                        radio.setBackgroundColor(getActivity().getResources().getColor(R.color.reserved));
                    }
                }

                radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Intent intent = new Intent();
                        intent.putExtra(ACTION, action);
                        intent.putExtra(CURRENT_SELECTED, (int) radio.getTag());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        getDialog().dismiss();
                    }
                });

                if (Action.ENUMERATE.equals(action)) {
                    if (!RegistrationType.SHARED.equals(hidAccessoryInfo.getRegistrationType())) {
                        radio.setEnabled(false);
                    }
                }
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
