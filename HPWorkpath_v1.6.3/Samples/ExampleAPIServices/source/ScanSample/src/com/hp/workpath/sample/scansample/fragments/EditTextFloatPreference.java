// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;

import com.hp.workpath.sample.scansample.R;

public final class EditTextFloatPreference extends EditTextPreference {

    private Float mFloat = 0.0f;

    private float mMin = 0.0f;
    private float mMax = 100000.0f;

    private String mTitle = "";

    public EditTextFloatPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditTextFloatPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextFloatPreference(final Context context) {
        super(context);
        init();
    }

    public void setLimits(final float min, final float max) {
        mMin = min;
        mMax = max;
        if (mMin > mFloat || mMax < mFloat) {
            setText(Float.toString(mMin));
        }
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format), mMin, mMax));
    }

    private void init() {
        mTitle = (String) getDialogTitle();
        setDialogTitle(mTitle + String.format(getContext().getString(R.string.title_format), mMin, mMax));
    }

    @Override
    public String getText() {
        return mFloat != null ? mFloat.toString() : null;
    }

    @Override
    public void setText(final String text) {
        final boolean wasBlocking = shouldDisableDependents();
        if (!TextUtils.isEmpty(text)) {
            float value = Float.parseFloat(text);
            if (isInRange(mMin, mMax, value)) {
                mFloat = value;

                persistString(mFloat != null ? mFloat.toString() : null);

                final boolean isBlocking = shouldDisableDependents();

                if (isBlocking != wasBlocking) {
                    notifyDependencyChange(isBlocking);
                }
            } else {
                Toast.makeText(getContext(), R.string.capabilities_not_available, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isInRange(final float min, final float max, final float value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}