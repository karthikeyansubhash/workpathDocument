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
import com.hp.workpath.sample.deviceusagesample.PlexByMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R

class PlexByMediaSizeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var plexByMediaSizeButton: Button

    fun setPlexByMediaSize(plexByMediaSizeButton: Array<PrinterInfo.PlexByMediaSize?>?) {
        rootView.removeAllViews()
        if (plexByMediaSizeButton != null) {
            rootView.addView(setPlexByMediaSizeInternal(plexByMediaSizeButton))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPlexByMediaSizeInternal(categories: Array<PrinterInfo.PlexByMediaSize?>?): View {
        val view = inflater.inflate(R.layout.layout_button, rootView, false)
        initViewPlexByMediaSize(view)
        if (categories != null) {
            plexByMediaSizeButton.setOnClickListener { v ->
                val gson = Gson()
                val intent = Intent(v.context, PlexByMediaSizeActivity::class.java)
                intent.putExtra(PlexByMediaSizeActivity.DATA, gson.toJson(categories))
                v.context.startActivity(intent)
            }
        }
        return view
    }

    private fun initViewPlexByMediaSize(view: View) {
        plexByMediaSizeButton = view.findViewById(R.id.detailButton)
    }
}