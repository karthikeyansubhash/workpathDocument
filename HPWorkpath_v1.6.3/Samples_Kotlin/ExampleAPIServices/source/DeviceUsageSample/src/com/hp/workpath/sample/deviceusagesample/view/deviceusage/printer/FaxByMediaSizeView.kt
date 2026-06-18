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
import com.hp.workpath.sample.deviceusagesample.FaxByMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R

class FaxByMediaSizeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var faxByMediaSizeButton: Button

    fun setFaxByMediaSize(faxByMediaSizes: Array<PrinterInfo.FaxByMediaSize?>?) {
        rootView.removeAllViews()
        if (faxByMediaSizes != null) {
            rootView.addView(setFaxByMediaSizeInternal(faxByMediaSizes))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setFaxByMediaSizeInternal(categories: Array<PrinterInfo.FaxByMediaSize?>?): View {
        val view = inflater.inflate(R.layout.layout_button, rootView, false)
        initViewFaxByMediaSize(view)
        if (categories != null) {
            faxByMediaSizeButton.setOnClickListener { v ->
                val gson = Gson()
                val intent = Intent(v.context, FaxByMediaSizeActivity::class.java)
                intent.putExtra(FaxByMediaSizeActivity.DATA, gson.toJson(categories))
                v.context.startActivity(intent)
            }
        }
        return view
    }

    private fun initViewFaxByMediaSize(view: View) {
        faxByMediaSizeButton = view.findViewById(R.id.detailButton)
    }
}