// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.RegistrationType;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.sample.accessorysample.Action;
import com.hp.workpath.sample.accessorysample.Logger;
import com.hp.workpath.sample.accessorysample.MainActivity;
import com.hp.workpath.sample.accessorysample.R;
import com.hp.workpath.sample.accessorysample.task.ActionTask;

import java.util.ArrayList;
import java.util.List;

public class AccessoryListFragment extends Fragment implements View.OnClickListener {

    public static final int DIALOG_FRAGMENT = 1;

    RelativeLayout mOwnedLayout;
    RelativeLayout mEnumerateLayout;

    TextView mOwnedTextView;
    TextView mEnumerateTextView;
    ImageView mOwnedCheckbox;
    ImageView mEnumerateCheckbox;
    Button mGetOwnedButton;
    Button mResendButton;
    Button mEnumerateButton;
    Button mReserveButton;
    Button mReleaseButton;

    List<AccessoryInfo> ownedAccessories = new ArrayList<>();
    List<AccessoryInfo> enumeratedAccessories = new ArrayList<>();
    AccessoryInfo mReservedAccessory;
    String mAccessoryContextId;

    int ownedValueIndex = 0;
    int enumerateValueIndex = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accessory_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        mOwnedLayout = view.findViewById(R.id.ownedListLayout);
        mEnumerateLayout = view.findViewById(R.id.enumerateListLayout);
        mOwnedLayout.setOnClickListener(this);
        mEnumerateLayout.setOnClickListener(this);
        mOwnedTextView = view.findViewById(R.id.ownedTextView);
        mEnumerateTextView = view.findViewById(R.id.enumerateTextView);
        mOwnedCheckbox = view.findViewById(R.id.getOwnedCheckBox);
        mEnumerateCheckbox = view.findViewById(R.id.enumerateCheckBox);
        mGetOwnedButton = view.findViewById(R.id.getOwnedButton);
        mResendButton = view.findViewById(R.id.resendButton);
        mEnumerateButton = view.findViewById(R.id.enumerateButton);
        mReserveButton = view.findViewById(R.id.reserveButton);
        mReleaseButton = view.findViewById(R.id.releaseButton);
        mGetOwnedButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mEnumerateButton.setOnClickListener(this);
        mReserveButton.setOnClickListener(this);
        mReleaseButton.setOnClickListener(this);
    }

    public void loadAccessories(Action action, List<AccessoryInfo> accessoryInfos) {
        switch (action) {
            case GET_OWNED:
                ownedAccessories = accessoryInfos;
                ownedValueIndex = 0;
                showSelectedOwnedAccessory();
                break;
            case ENUMERATE:
                enumeratedAccessories = accessoryInfos;
                enumerateValueIndex = 0;
                showSelectedEnumerateAccessory();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        ListDialogFragment dialogFragment = null;
        Action action;
        if (v == mOwnedLayout) {
            action = Action.GET_OWNED;
            dialogFragment = ListDialogFragment.newInstance(action, ownedAccessories, ownedValueIndex, mReservedAccessory);
            showListDialog(dialogFragment);
        } else if (v == mEnumerateLayout) {
            action = Action.ENUMERATE;
            dialogFragment = ListDialogFragment.newInstance(action, enumeratedAccessories, enumerateValueIndex,mReservedAccessory);
            showListDialog(dialogFragment);
        } else if (v == mGetOwnedButton) {
            new ActionTask((MainActivity) getActivity()).taskExecute(Action.GET_OWNED);
        } else if (v == mResendButton) {
            new ActionTask((MainActivity) getActivity()).taskExecute(Action.RESEND_OWNED, getSelectedOwnedAccessory());
        } else if (v == mEnumerateButton) {
            new ActionTask((MainActivity) getActivity()).taskExecute(Action.ENUMERATE);
        } else if (v == mReserveButton) {
            AccessoryInfo accessoryInfo = getSelectedEnumerateAccessory();
            if (accessoryInfo != null && !accessoryInfo.getRegistrationType().equals(RegistrationType.SHARED)) {
                if(getContext() != null) {
                    Logger.showResult(getActivity(), getString(R.string.shared_only));
                }
                return;
            }
            new ActionTask((MainActivity) getActivity()).taskExecute(Action.RESERVE_SHARED, getSelectedEnumerateAccessory());
        } else if (v == mReleaseButton) {
            new ActionTask((MainActivity) getActivity()).taskExecute(Action.RELEASE_SHARED, mAccessoryContextId);
        }
    }

    private void showListDialog(ListDialogFragment dialog) {
        if (dialog != null) {
            dialog.setTargetFragment(this, DIALOG_FRAGMENT);
            dialog.show(getParentFragmentManager(), "dialog");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_FRAGMENT:
                    if (data != null && data.hasExtra(ListDialogFragment.CURRENT_SELECTED)) {
                        if (Action.GET_OWNED.equals(data.getSerializableExtra(ListDialogFragment.ACTION))) {
                            ownedValueIndex = data.getIntExtra(ListDialogFragment.CURRENT_SELECTED, 0);
                            showSelectedOwnedAccessory();
                        } else if (Action.ENUMERATE.equals(data.getSerializableExtra(ListDialogFragment.ACTION))) {
                            enumerateValueIndex = data.getIntExtra(ListDialogFragment.CURRENT_SELECTED, 0);
                            showSelectedEnumerateAccessory();
                        }
                    }
                    break;
            }
        }
    }

    public void updateReservedAccessory(AccessoryInfo accessoryInfo, String accessoryContextId) {
        this.mReservedAccessory = accessoryInfo;
        this.mAccessoryContextId = accessoryContextId;

        if (accessoryInfo == null) {
            mOwnedCheckbox.setVisibility(View.GONE);
            mEnumerateCheckbox.setVisibility(View.GONE);
        } else {
            switch (accessoryInfo.getRegistrationType()) {
                case OWNED:
                    mOwnedCheckbox.setVisibility(View.VISIBLE);
                    mEnumerateCheckbox.setVisibility(View.GONE);
                    break;
                case SHARED:
                    mOwnedCheckbox.setVisibility(View.GONE);
                    mEnumerateCheckbox.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public AccessoryInfo getSelectedOwnedAccessory() {
        if (ownedAccessories.size() > ownedValueIndex) {
            return ownedAccessories.get(ownedValueIndex);
        } else {
            return null;
        }
    }

    public AccessoryInfo getSelectedEnumerateAccessory() {
        if (enumeratedAccessories.size() > enumerateValueIndex) {
            return enumeratedAccessories.get(enumerateValueIndex);
        } else {
            return null;
        }
    }

    private void showSelectedOwnedAccessory() {
        if (mOwnedTextView != null
                && ownedAccessories.size() > 0
                && ownedAccessories.size() > ownedValueIndex) {
            AccessoryInfo ownedAccessory = ownedAccessories.get(ownedValueIndex);
            if (ownedAccessory instanceof HIDAccessoryInfo) {
                mOwnedTextView.setText(Logger.build((HIDAccessoryInfo) ownedAccessory));
            }
        }
    }

    private void showSelectedEnumerateAccessory() {
        if (mEnumerateTextView != null
                && enumeratedAccessories.size() > 0
                && enumeratedAccessories.size() > enumerateValueIndex) {
            AccessoryInfo enumeratedAccessory = enumeratedAccessories.get(enumerateValueIndex);
            if (enumeratedAccessory instanceof HIDAccessoryInfo) {
                mEnumerateTextView.setText(Logger.build((HIDAccessoryInfo) enumeratedAccessory));
            }
        }
    }
}
