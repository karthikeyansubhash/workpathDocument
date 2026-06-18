// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.hp.workpath.sample.copysample.R;

public class ShiftPreference extends DialogPreference {

    float xShift = 0.0f;
    float yShift = 0.0f;

    float mXShiftMin = 0.0f;
    float mYShiftMin = 0.0f;
    float mXShiftMax = 0.0f;
    float mYShiftMax = 0.0f;

    public ShiftPreference (Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public ShiftPreference (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShiftPreference (Context context) { super(context); }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        setDialogLayoutResource(R.layout.layout_shifts);
        loadShifts();
    }

    public void applyShifts() throws Exception {
        if (!isInRange(mXShiftMin, mXShiftMax, xShift)) {
            throw new Exception(getContext().getString(R.string.range_shift, mXShiftMin, mXShiftMax));
        }
        if (!isInRange(mYShiftMin, mYShiftMax, yShift)) {
            throw new Exception(getContext().getString(R.string.range_shift, mYShiftMin, mYShiftMax));
        }
        saveShifts();
    }

    private void loadShifts() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        xShift = sharedPref.getFloat(getKey() + "xShift", 0.0f);
        yShift = sharedPref.getFloat(getKey() + "yShift", 0.0f);
        setShiftSummary();
    }

    private void setShiftSummary() {
        Handler handler = new Handler();
        final Runnable r = () -> setSummary(getContext().getString(R.string.summary_shift, xShift, yShift));
        handler.post(r);
    }

    private void saveShifts() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getKey() + "xShift", xShift);
        editor.putFloat(getKey() + "yShift", yShift);
        editor.apply();
        setShiftSummary();
    }

    public void setXShiftLimits(final float min, final float max) {
        mXShiftMin = min;
        mXShiftMax = max;
    }

    public void setYShiftLimits(final float min, final float max) {
        mYShiftMin = min;
        mYShiftMax = max;
    }

    public void setShifts(float xShift, float yShift) {
        this.xShift = xShift;
        this.yShift = yShift;
    }

    public float getxShift() {
        return xShift;
    }

    public float getyShift() {
        return yShift;
    }

    private boolean isInRange(final float min, final float max, final float value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}