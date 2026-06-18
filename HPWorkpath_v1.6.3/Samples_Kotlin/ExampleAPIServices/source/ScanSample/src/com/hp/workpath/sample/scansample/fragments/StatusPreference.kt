// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

import com.hp.workpath.api.Result
import com.hp.workpath.api.scanner.ScannerStatus
import com.hp.workpath.sample.scansample.Logger
import com.hp.workpath.sample.scansample.R

import com.hp.workpath.sample.scansample.MainActivity.Companion.TAG

class StatusPreference : Preference {

    private lateinit var mGetStatusButton: Button
    private lateinit var mStatusTextView: TextView

    private var mScannerStatus: String? = null

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        mGetStatusButton = view.findViewById(R.id.getStatusButton) as Button
        mGetStatusButton.setOnClickListener { getScannerStatus() }
        mStatusTextView = view.findViewById(R.id.statusTextView) as TextView
        if (TextUtils.isEmpty(mScannerStatus)) {
            mScannerStatus = context.getString(R.string.na)
        }
        mStatusTextView.text = mScannerStatus
    }

    private fun getScannerStatus() {
        if (ScannerStatus.isSupported(context)) {
            val result = Result()
            val statusInfo = ScannerStatus.getStatus(context, result)
            if (result.code == Result.RESULT_OK) {
                Log.i(TAG, "StatusInfo=" + Logger.build(statusInfo, true))
                mScannerStatus = Logger.build(statusInfo, false)
                mStatusTextView.text = mScannerStatus
            } else {
                Log.i(TAG, "ScannerStatus.getStatus ${Logger.build(result)}")
            }
        } else {
            Log.e(TAG, "ScannerStatus is not supported")
        }
    }
}
