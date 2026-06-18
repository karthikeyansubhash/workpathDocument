// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.EditTextPreference
import com.hp.workpath.sample.copysample.R

class EditTextIntegerPreference : EditTextPreference {
    private var mInteger: Int = 0
    private var minVal = 1
    private var maxVal = 100000
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

    fun setLimits(min: Int, max: Int) {
        minVal = min
        maxVal = max
        if (minVal > mInteger || maxVal < mInteger) {
            text = minVal.toString()
        }
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format), minVal, maxVal)
    }

    private fun init() {
        mTitle = dialogTitle as String
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format), minVal, maxVal)
    }

    override fun getText(): String {
        return mInteger.toString()
    }

    override fun setText(text: String) {
        val wasBlocking = shouldDisableDependents()
        if (!TextUtils.isEmpty(text)) {
            val value = text.toInt()
            if (isInRange(minVal, maxVal, value)) {
                mInteger = value
                persistString(mInteger.toString())
                val isBlocking = shouldDisableDependents()
                if (isBlocking != wasBlocking) {
                    notifyDependencyChange(isBlocking)
                }
            } else {
                Toast.makeText(context, R.string.capabilities_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return if (max > min) value in min..max else value in max..min
    }
}