// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.hp.workpath.sample.copysample.R

class ShiftPreference : DialogPreference {
    var xShift = 0.0f
    var yShift = 0.0f
    private var mXShiftMin = 0.0f
    private var mYShiftMin = 0.0f
    private var mXShiftMax = 0.0f
    private var mYShiftMax = 0.0f

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        dialogLayoutResource = R.layout.layout_shifts
        loadShifts()
    }

    @Throws(Throwable::class)
    fun applyShifts() {
        if (xShift !in mXShiftMin..mXShiftMax) {
            throw Throwable(context.getString(R.string.range_shift, mXShiftMin, mXShiftMax))
        }
        if (yShift !in mYShiftMin..mYShiftMax) {
            throw Throwable(context.getString(R.string.range_shift, mYShiftMin, mYShiftMax))
        }
        saveShifts()
    }

    private fun loadShifts() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        xShift = sharedPref.getFloat(key + "xShift", 0.0f)
        yShift = sharedPref.getFloat(key + "yShift", 0.0f)
        setShiftSummary()
    }

    private fun setShiftSummary() {
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable { summary = context.getString(R.string.summary_shift, xShift, yShift) }
        handler.post(r)
    }

    private fun saveShifts() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putFloat(key + "xShift", xShift)
        editor.putFloat(key + "yShift", yShift)
        editor.apply()
        setShiftSummary()
    }

    fun setXShiftLimits(min: Float, max: Float) {
        mXShiftMin = min
        mXShiftMax = max
    }

    fun setYShiftLimits(min: Float, max: Float) {
        mYShiftMin = min
        mYShiftMax = max
    }

    fun setShifts(xShift: Float, yShift: Float) {
        this.xShift = xShift
        this.yShift = yShift
    }
}