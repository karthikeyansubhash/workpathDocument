// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceDialogFragmentCompat
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R

class ShiftPreferenceFragment : PreferenceDialogFragmentCompat() {

    lateinit var mXShiftEditText: EditText
    lateinit var mYShiftEditText: EditText

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        view?.run {
            mXShiftEditText = findViewById(R.id.xShiftEditText)
            mYShiftEditText = findViewById(R.id.yShiftEditText)

            mXShiftEditText.setText(shiftPreference.xShift.toString())
            mYShiftEditText.setText(shiftPreference.yShift.toString())
        }
    }

    // get the NumberPickerPreference instance
    private val shiftPreference: ShiftPreference
        get() = preference as ShiftPreference

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            try {
                shiftPreference.setShifts(getFloatValue(mXShiftEditText),
                    getFloatValue(mYShiftEditText))
                shiftPreference.applyShifts()
            } catch (t: Throwable) {
                Log.e(MainActivity.TAG, "getShiftPreference ${t.message}")
            }
        }
    }

    private fun getFloatValue(editText: EditText?): Float {
        var value = 0.0f
        try {
            if (editText != null && !TextUtils.isEmpty(editText.text.toString())) {
                value = editText.text.toString().toFloat()
            }
        } catch (e: Exception) {
        }
        return value
    }

    companion object {
        fun newInstance(key: String?): ShiftPreferenceFragment {
            val fragment = ShiftPreferenceFragment()
            val bundle = Bundle()
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}