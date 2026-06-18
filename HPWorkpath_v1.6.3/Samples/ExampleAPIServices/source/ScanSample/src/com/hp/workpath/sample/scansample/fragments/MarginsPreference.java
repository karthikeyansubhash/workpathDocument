// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.hp.workpath.sample.scansample.R;

public class MarginsPreference extends DialogPreference {


    float mLeft = 0.0f;
    float mTop = 0.0f;
    float mRight = 0.0f;
    float mBottom = 0.0f;

    float mLeftMin = 0.0f;
    float mTopMin = 0.0f;
    float mRightMin = 0.0f;
    float mBottomMin = 0.0f;

    float mLeftMax = 0.0f;
    float mTopMax = 0.0f;
    float mRightMax = 0.0f;
    float mBottomMax = 0.0f;

    public MarginsPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MarginsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarginsPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        setDialogLayoutResource(R.layout.layout_margins);
        loadMargins();
    }

    public void applyMargins() throws Exception {
        if (!isInRange(mLeftMin, mLeftMax, mLeft)) {
            throw new Exception(getContext().getString(R.string.range_margin, mLeftMin, mLeftMax));
        }
        if (!isInRange(mTopMin, mTopMax, mTop)) {
            throw new Exception(getContext().getString(R.string.range_margin, mTopMin, mTopMax));
        }
        if (!isInRange(mRightMin, mRightMax, mRight)) {
            throw new Exception(getContext().getString(R.string.range_margin, mRightMin, mRightMax));
        }
        if (!isInRange(mBottomMin, mBottomMax, mBottom)) {
            throw new Exception(getContext().getString(R.string.range_margin, mBottomMin, mBottomMax));
        }
        saveMargins();
    }

    private void loadMargins() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mLeft = sharedPref.getFloat(getKey() + "Left", 0.0f);
        mTop = sharedPref.getFloat(getKey() + "Top", 0.0f);
        mRight = sharedPref.getFloat(getKey() + "Right", 0.0f);
        mBottom = sharedPref.getFloat(getKey() + "Bottom", 0.0f);
        setMarginSummary();
    }

    private void setMarginSummary() {
        Handler handler = new Handler();
        final Runnable r = () -> setSummary(getContext().getString(R.string.summary_margin, mLeft, mTop, mRight, mBottom));
        handler.post(r);
    }

    private void saveMargins() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getKey() + "Left", mLeft);
        editor.putFloat(getKey() + "Top", mTop);
        editor.putFloat(getKey() + "Right", mRight);
        editor.putFloat(getKey() + "Bottom", mBottom);
        editor.apply();
        setMarginSummary();
    }

    public void setLeftLimits(final float min, final float max) {
        mLeftMin = min;
        mLeftMax = max;
    }

    public void setTopLimits(final float min, final float max) {
        mTopMin = min;
        mTopMax = max;
    }

    public void setRightLimits(final float min, final float max) {
        mRightMin = min;
        mRightMax = max;
    }

    public void setBottomLimits(final float min, final float max) {
        mBottomMin = min;
        mBottomMax = max;
    }

    public void setMargins(float left, float top, float right, float bottom) {
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
    }

    public float getLeft() {
        return mLeft;
    }

    public float getTop() {
        return mTop;
    }

    public float getRight() {
        return mRight;
    }

    public float getBottom() {
        return mBottom;
    }

    private boolean isInRange(final float min, final float max, final float value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}