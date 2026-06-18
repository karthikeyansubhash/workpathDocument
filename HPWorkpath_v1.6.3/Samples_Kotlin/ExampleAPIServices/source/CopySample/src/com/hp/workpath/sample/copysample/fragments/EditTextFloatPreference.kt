// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.EditTextPreference
import com.hp.workpath.sample.copysample.R

class EditTextFloatPreference : EditTextPreference {
    private var mFloat: Float = 0.0f
    private var mMin = 0.0f
    private var mMax = 100000.0f
    private var mTitle = ""

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    fun setLimits(min: Float, max: Float) {
        mMin = min
        mMax = max
        if (mMin > mFloat || mMax < mFloat) {
            text = mMin.toString()
        }
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format_float), mMin, mMax)
    }

    private fun init() {
        mTitle = dialogTitle as String
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format_float), mMin, mMax)
    }

    override fun getText(): String {
        return mFloat.toString()
    }

    override fun setText(text: String) {
        val wasBlocking = shouldDisableDependents()
        if (!TextUtils.isEmpty(text)) {
            val value = text.toFloat()
            if (isInRange(mMin, mMax, value)) {
                mFloat = value
                persistString(mFloat.toString())
                val isBlocking = shouldDisableDependents()
                if (isBlocking != wasBlocking) {
                    notifyDependencyChange(isBlocking)
                }
            } else {
                Toast.makeText(context, R.string.capabilities_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isInRange(min: Float, max: Float, value: Float): Boolean {
        return if (max > min) value in min..max else value in max..min
    }
}