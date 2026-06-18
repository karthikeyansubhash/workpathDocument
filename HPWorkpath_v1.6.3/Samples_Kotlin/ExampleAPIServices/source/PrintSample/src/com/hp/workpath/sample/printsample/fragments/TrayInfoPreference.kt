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
import com.hp.workpath.api.printer.TrayInfo
import com.hp.workpath.sample.printsample.Logger
import com.hp.workpath.sample.printsample.Logger.build
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.R

class TrayInfoPreference : Preference {
    private lateinit var mGetTrayInfoButton: Button
    private lateinit var mTrayInfoTextView1: TextView
    private lateinit var mTrayInfoTextView2: TextView
    private var mTrayInfoStatus1: String = context.getString(R.string.na)
    private var mTrayInfoStatus2: String = ""

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)
        mGetTrayInfoButton = view.findViewById(R.id.getTrayInfoButton) as Button
        mGetTrayInfoButton.setOnClickListener { trayInfoStatus }
        mTrayInfoTextView1 = view.findViewById(R.id.trayTextView1) as TextView
        mTrayInfoTextView2 = view.findViewById(R.id.trayTextView2) as TextView
        mTrayInfoTextView1.text = mTrayInfoStatus1
    }

    private val trayInfoStatus: Unit
        get() {
            if (PrinterStatus.isSupported(context)) {
                val result = Result()
                val trayInfoList = PrinterStatus.getTrays(context, result)
                if (result.code == Result.RESULT_OK) {
                    mTrayInfoStatus1 = ""
                    mTrayInfoStatus2 = ""
                    var index = 0
                    for (trayInfo in trayInfoList) {
                        Log.i(MainActivity.TAG, "trayInfo=" + Logger.build(trayInfo))
                        val status: String = if (trayInfo.status == TrayInfo.Status.AVAILABLE) {
                            "${trayInfo.paperSource} (${trayInfo.status}): ${trayInfo.level}% cap:${trayInfo.capacity}, ${trayInfo.paperSize}, ${trayInfo.paperType}"
                        } else {
                            "${trayInfo.paperSource} (${trayInfo.status})"
                        }
                        if (index < 3) {
                            mTrayInfoStatus1 += status
                        } else {
                            mTrayInfoStatus2 += status
                        }
                        index++
                    }
                    mTrayInfoTextView1.text = mTrayInfoStatus1
                    mTrayInfoTextView2.text = mTrayInfoStatus2
                } else {
                    Log.i(MainActivity.TAG, "PrinterStatus.getTrays " + build(result))
                }
            } else {
                Log.e(MainActivity.TAG, "PrinterStatus is not supported")
            }
        }
}