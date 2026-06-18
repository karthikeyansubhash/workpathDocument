// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.hp.workpath.api.Result
import com.hp.workpath.api.printer.PrinterStatus
import com.hp.workpath.api.scanner.ScannerStatus
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.R

class StatusPreference : Preference {
    private lateinit var mGetStatusButton: Button
    private lateinit var mPrinterStatusTextView: TextView
    private lateinit var mScannerStatusTextView: TextView
    var mPrinterStatus: String = context.getString(R.string.na)
    var mScannerStatus: String = context.getString(R.string.na)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        mGetStatusButton = view.findViewById(R.id.getStatusButton) as Button
        mGetStatusButton.setOnClickListener {
            printerStatus
            scannerStatus
        }
        mPrinterStatusTextView = view.findViewById(R.id.printerStatusTextView) as TextView
        mPrinterStatusTextView.text = mPrinterStatus
        mScannerStatusTextView = view.findViewById(R.id.scannerStatusTextView) as TextView
        mScannerStatusTextView.text = mScannerStatus
    }

    private val printerStatus: Unit
        get() {
            if (PrinterStatus.isSupported(context)) {
                val result = Result()
                val statusInfo = PrinterStatus.getStatus(context, result)
                if (result.code == Result.RESULT_OK) {
                    mPrinterStatus = "Printer: " + Logger.build(statusInfo)
                    mPrinterStatusTextView.text = mPrinterStatus
                    showResult(mPrinterStatus, null)
                } else {
                    showResult("PrinterStatus.getStatus(): ", result)
                }
            } else {
                showResult("PrinterStatus is not supported", null)
            }
        }

    private val scannerStatus: Unit
        get() {
            if (ScannerStatus.isSupported(context)) {
                val result = Result()
                val statusInfo = ScannerStatus.getStatus(context, result)
                if (result.code == Result.RESULT_OK) {
                    mScannerStatus = "Scanner: " + Logger.build(statusInfo)
                    mScannerStatusTextView.text = mScannerStatus
                    showResult(mScannerStatus, null)
                } else {
                    showResult("ScannerStatus.getStatus(): ", result)
                }
            } else {
                showResult("ScannerStatus is not supported", null)
            }
        }

    private fun showResult(msg: String, result: Result?) {
        Toast.makeText(context, if (result != null) msg + Logger.build(result) else msg, Toast.LENGTH_SHORT).show()
    }
}