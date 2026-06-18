// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import static com.hp.workpath.sample.copysample.MainActivity.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.hp.workpath.sample.copysample.R;

public class ShiftPreferenceFragment extends PreferenceDialogFragmentCompat {

    EditText mXShiftEditText;
    EditText mYShiftEditText;

    public static ShiftPreferenceFragment newInstance(String key) {
        final ShiftPreferenceFragment fragment = new ShiftPreferenceFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        mXShiftEditText = view.findViewById(R.id.xShiftEditText);
        mYShiftEditText = view.findViewById(R.id.yShiftEditText);

        mXShiftEditText.setText(Float.toString(getShiftPreference().getxShift()));
        mYShiftEditText.setText(Float.toString(getShiftPreference().getyShift()));
    }

    // get the NumberPickerPreference instance
    private ShiftPreference getShiftPreference() { return (ShiftPreference) getPreference();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            try {
                getShiftPreference().setShifts(getFloatValue(mXShiftEditText),
                        getFloatValue(mYShiftEditText));
                getShiftPreference().applyShifts();
            } catch (Throwable t) {
                Log.e(TAG, "getShiftPreference " + t.getMessage());
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