// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceDialogFragmentCompat
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R

class MarginsPreferenceFragment : PreferenceDialogFragmentCompat() {

    lateinit var mLeftMarginEditText: EditText
    lateinit var mTopMarginEditText: EditText
    lateinit var mRightMarginEditText: EditText
    lateinit var mBottomMarginEditText: EditText

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        view?.run {
            mLeftMarginEditText = findViewById(R.id.leftEditText)
            mTopMarginEditText = findViewById(R.id.topEditText)
            mRightMarginEditText = findViewById(R.id.rightEditText)
            mBottomMarginEditText = findViewById(R.id.bottomEditText)

            mLeftMarginEditText.setText(marginsPreference.left.toString())
            mTopMarginEditText.setText(marginsPreference.top.toString())
            mRightMarginEditText.setText(marginsPreference.right.toString())
            mBottomMarginEditText.setText(marginsPreference.bottom.toString())
        }
    }

    // get the NumberPickerPreference instance
    private val marginsPreference: MarginsPreference
        get() = preference as MarginsPreference

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            try {
                marginsPreference.setMargins(getFloatValue(mLeftMarginEditText),
                        getFloatValue(mTopMarginEditText),
                        getFloatValue(mRightMarginEditText),
                        getFloatValue(mBottomMarginEditText))
                marginsPreference.applyMargins()
            } catch (t: Throwable) {
                Log.e(MainActivity.TAG, "getMarginsPreference ${t.message}")
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
        fun newInstance(key: String?): MarginsPreferenceFragment {
            val fragment = MarginsPreferenceFragment()
            val bundle = Bundle()
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}