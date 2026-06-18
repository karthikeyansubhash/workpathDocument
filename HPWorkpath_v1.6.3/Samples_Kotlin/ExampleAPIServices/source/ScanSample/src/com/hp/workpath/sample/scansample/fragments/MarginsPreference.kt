// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.hp.workpath.sample.scansample.R

class MarginsPreference : DialogPreference {
    var left = 0.0f
    var top = 0.0f
    var right = 0.0f
    var bottom = 0.0f
    private var mLeftMin = 0.0f
    private var mTopMin = 0.0f
    private var mRightMin = 0.0f
    private var mBottomMin = 0.0f
    private var mLeftMax = 0.0f
    private var mTopMax = 0.0f
    private var mRightMax = 0.0f
    private var mBottomMax = 0.0f

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        dialogLayoutResource = R.layout.layout_margins
        loadMargins()
    }

    @Throws(Throwable::class)
    fun applyMargins() {
        if (left !in mLeftMin..mLeftMax) {
            throw Throwable(context.getString(R.string.range_margin, mLeftMin, mLeftMax))
        }
        if (top !in mTopMin..mTopMax) {
            throw Throwable(context.getString(R.string.range_margin, mTopMin, mTopMax))
        }
        if (right !in mRightMin..mRightMax) {
            throw Throwable(context.getString(R.string.range_margin, mRightMin, mRightMax))
        }
        if (bottom !in mBottomMin..mBottomMax) {
            throw Throwable(context.getString(R.string.range_margin, mBottomMin, mBottomMax))
        }
        saveMargins()
    }

    private fun loadMargins() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        left = sharedPref.getFloat(key + "Left", 0.0f)
        top = sharedPref.getFloat(key + "Top", 0.0f)
        right = sharedPref.getFloat(key + "Right", 0.0f)
        bottom = sharedPref.getFloat(key + "Bottom", 0.0f)
        setMarginSummary()
    }

    private fun setMarginSummary() {
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable { summary = context.getString(R.string.summary_margin, left, top, right, bottom) }
        handler.post(r)
    }

    private fun saveMargins() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putFloat(key + "Left", left)
        editor.putFloat(key + "Top", top)
        editor.putFloat(key + "Right", right)
        editor.putFloat(key + "Bottom", bottom)
        editor.apply()
        setMarginSummary()
    }

    fun setLeftLimits(min: Float, max: Float) {
        mLeftMin = min
        mLeftMax = max
    }

    fun setTopLimits(min: Float, max: Float) {
        mTopMin = min
        mTopMax = max
    }

    fun setRightLimits(min: Float, max: Float) {
        mRightMin = min
        mRightMax = max
    }

    fun setBottomLimits(min: Float, max: Float) {
        mBottomMin = min
        mBottomMax = max
    }

    fun setMargins(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }
}