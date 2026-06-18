// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;

import com.hp.workpath.sample.scansample.R;

public final class EditTextIntegerPreference extends EditTextPreference {

    private Integer mValue = 0;

    private int mMin = 0;
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

    public void setLimits(final int min, final int max) {
        mMin = min;
        mMax = max;
        if (mMin > mValue || mMax < mValue) {
            setText(Integer.toString(mMin));
        }
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format_integer), mMin, mMax));
    }

    private void init() {
        mTitle = (String) getDialogTitle();
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format_integer), mMin, mMax));
    }

    @Override
    public String getText() {
        return mValue != null ? mValue.toString() : null;
    }

    @Override
    public void setText(final String text) {
        final boolean wasBlocking = shouldDisableDependents();
        if (!TextUtils.isEmpty(text)) {
            int value = Integer.parseInt(text);
            if (isInRange(mMin, mMax, value)) {
                mValue = value;

                persistString(mValue != null ? mValue.toString() : null);

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