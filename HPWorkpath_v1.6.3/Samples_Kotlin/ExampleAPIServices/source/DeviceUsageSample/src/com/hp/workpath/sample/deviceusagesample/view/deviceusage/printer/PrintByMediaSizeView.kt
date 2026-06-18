// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.Gson
import com.hp.workpath.api.deviceusage.printer.PrinterInfo
import com.hp.workpath.api.deviceusage.printer.PrinterInfo.ByJobCategoryAndMediaSize
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.PrintByMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R

class PrintByMediaSizeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var printByMediaSizeButton: Button

    fun setPrintByMediaSize(printByMediaSizes: Array<PrinterInfo.PrintByMediaSize?>?) {
        rootView.removeAllViews()
        if (printByMediaSizes != null) {
            rootView.addView(setPrintByMediaSizeInternal(printByMediaSizes))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPrintByMediaSizeInternal(categories: Array<PrinterInfo.PrintByMediaSize?>?): View {
        val view = inflater.inflate(R.layout.layout_button, rootView, false)
        initViewPrintByMediaSize(view)
        if (categories != null) {
            printByMediaSizeButton.setOnClickListener { v ->
                val gson = Gson()
                val intent = Intent(v.context, PrintByMediaSizeActivity::class.java)
                intent.putExtra(PrintByMediaSizeActivity.DATA, gson.toJson(categories))
                v.context.startActivity(intent)
            }
        }
        return view
    }

    private fun initViewPrintByMediaSize(view: View) {
        printByMediaSizeButton = view.findViewById(R.id.detailButton)
    }
}