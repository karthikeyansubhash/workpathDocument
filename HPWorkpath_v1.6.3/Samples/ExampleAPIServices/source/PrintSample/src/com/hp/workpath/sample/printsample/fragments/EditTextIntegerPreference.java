// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;

import com.hp.workpath.sample.printsample.R;

public final class EditTextIntegerPreference extends EditTextPreference {

    private Integer mInteger;

    private int mMin = 1;
    private int mMax = 100000;

    private String mTitle = "";

    public EditTextIntegerPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditTextIntegerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextIntegerPreference(final Context context) {
        super(context);
        init();
    }

    public int getMaxVal() {
        return mMax;
    }

    public int getMinVal() {
        return mMin;
    }

    public void setLimits(final int min, final int max) {
        mMin = min;
        mMax = max;
        if (mMin > mInteger || mMax < mInteger) {
            setText(Integer.toString(mMin));
        }
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format), mMin, mMax));
    }

    private void init() {
        mTitle = (String) getDialogTitle();
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format), mMin, mMax));
    }

    @Override
    public String getText() {
        return mInteger != null ? mInteger.toString() : null;
    }

    @Override
    public void setText(final String text) {
        final boolean wasBlocking = shouldDisableDependents();
        if (!TextUtils.isEmpty(text)) {
            int value = Integer.parseInt(text);
            if (isInRange(mMin, mMax, value)) {
                mInteger = value;
                persistString(mInteger != null ? mInteger.toString() : null);

                final boolean isBlocking = shouldDisableDependents();

                if (isBlocking != wasBlocking) {
                    notifyDependencyChange(isBlocking);
                }
            } else {
                Toast.makeText(getContext(), R.string.capabilities_not_available, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isInRange(final int min, final int max, final int value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}