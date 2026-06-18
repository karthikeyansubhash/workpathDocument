// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.EditTextPreference
import com.hp.workpath.sample.scansample.R

class EditTextIntegerPreference : EditTextPreference {
    private var mValue: Int = 0
    private var mMin = 0
    private var mMax = 100000
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
        mMin = min
        mMax = max
        if (mValue in mMin..mMax) {
            text = mMin.toString()
        }
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format_integer), mMin, mMax)
    }

    private fun init() {
        mTitle = dialogTitle as String
        dialogTitle = mTitle + String.format(context.getString(R.string.title_format_integer), mMin, mMax)
    }

    override fun getText(): String {
        return mValue.toString()
    }

    override fun setText(text: String) {
        val wasBlocking = shouldDisableDependents()
        if (!TextUtils.isEmpty(text)) {
            val value = text.toInt()
            if (value in mMin..mMax) {
                mValue = value
                persistString(mValue.toString())
                val isBlocking = shouldDisableDependents()
                if (isBlocking != wasBlocking) {
                    notifyDependencyChange(isBlocking)
                }
            } else {
                Toast.makeText(context, R.string.capabilities_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }
}