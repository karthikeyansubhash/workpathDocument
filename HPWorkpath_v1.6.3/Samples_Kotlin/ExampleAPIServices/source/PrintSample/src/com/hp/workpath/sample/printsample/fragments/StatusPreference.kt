// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrinterStatus
import com.hp.workpath.sample.printsample.Logger
import com.hp.workpath.sample.printsample.Logger.build
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.R

class StatusPreference : Preference {
    private lateinit var mGetStatusButton: Button
    private lateinit var mStatusTextView: TextView
    private lateinit var mPrinterStatus: String

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        mGetStatusButton = view.findViewById(R.id.getStatusButton) as Button
        mGetStatusButton.setOnClickListener { printerStatus }
        mStatusTextView = view.findViewById(R.id.statusTextView) as TextView
        mPrinterStatus = context.getString(R.string.na)
        mStatusTextView.text = mPrinterStatus
    }

    private val printerStatus: Unit
        get() {
            if (PrinterStatus.isSupported(context)) {
                val result = Result()
                val statusInfo = PrinterStatus.getStatus(context, result)
                if (result.code == Result.RESULT_OK) {
                    Log.i(MainActivity.TAG, "StatusInfo=" + Logger.build(statusInfo))
                    Logger.build(statusInfo)?.let {
                        mPrinterStatus = it
                    } ?: run {
                        mPrinterStatus = context.getString(R.string.na)
                    }
                    mStatusTextView.text = mPrinterStatus
                } else {
                    Log.i(MainActivity.TAG, "PrinterStatus.getStatus" + build(result))
                }
            } else {
                Log.e(MainActivity.TAG, "PrinterStatus is not supported")
            }
        }
}