// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.hp.workpath.sample.scansample.R;

import static com.hp.workpath.sample.scansample.MainActivity.TAG;

public class MarginsPreferenceFragment extends PreferenceDialogFragmentCompat {

    EditText mLeftMarginEditText;
    EditText mTopMarginEditText;
    EditText mRightMarginEditText;
    EditText mBottomMarginEditText;

    public static MarginsPreferenceFragment newInstance(String key) {
        final MarginsPreferenceFragment fragment = new MarginsPreferenceFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        mLeftMarginEditText = view.findViewById(R.id.leftEditText);
        mTopMarginEditText = view.findViewById(R.id.topEditText);
        mRightMarginEditText = view.findViewById(R.id.rightEditText);
        mBottomMarginEditText = view.findViewById(R.id.bottomEditText);
        mLeftMarginEditText.setText(Float.toString(getMarginsPreference().getLeft()));
        mTopMarginEditText.setText(Float.toString(getMarginsPreference().getTop()));
        mRightMarginEditText.setText(Float.toString(getMarginsPreference().getRight()));
        mBottomMarginEditText.setText(Float.toString(getMarginsPreference().getBottom()));
    }

    // get the NumberPickerPreference instance
    private MarginsPreference getMarginsPreference() {
        return (MarginsPreference) getPreference();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            try {
                getMarginsPreference().setMargins(getFloatValue(mLeftMarginEditText),
                        getFloatValue(mTopMarginEditText),
                        getFloatValue(mRightMarginEditText),
                        getFloatValue(mBottomMarginEditText));
                getMarginsPreference().applyMargins();
            } catch (Throwable t) {
                Log.e(TAG, "getMarginsPreference " + t.getMessage());
            }
        }
    }

    private float getFloatValue(EditText editText) {
        float value = 0.0f;
        try {
            if (editText != null && !TextUtils.isEmpty(editText.getText().toString())) {
                value = Float.parseFloat(editText.getText().toString());
            }
        } catch (Throwable t) {}
        return value;
    }
}