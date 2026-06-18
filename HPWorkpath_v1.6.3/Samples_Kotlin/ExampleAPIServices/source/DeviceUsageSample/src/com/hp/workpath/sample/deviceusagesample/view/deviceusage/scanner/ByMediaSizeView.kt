// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.gson.Gson
import com.hp.workpath.api.deviceusage.scanner.ScannerInfo.ByMediaSize
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.MediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R

class ByMediaSizeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var byMediaSizeButton: Button

    fun setByMediaSize(byMediaSizes: Array<ByMediaSize?>?) {
        rootView.removeAllViews()
        if (byMediaSizes != null) {
            rootView.addView(setByMediaSizeInternal(byMediaSizes))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setByMediaSizeInternal(byMediaSizes: Array<ByMediaSize?>?): View {
        val view = inflater.inflate(R.layout.layout_button, rootView, false)
        initViewByMediaSize(view)
        if (byMediaSizes != null) {
            byMediaSizeButton.setOnClickListener { v ->
                val gson = Gson()
                val intent = Intent(v.context, MediaSizeActivity::class.java)
                intent.putExtra(JobCategoryAndMediaSizeActivity.DATA, gson.toJson(byMediaSizes))
                v.context.startActivity(intent)
            }
        }
        return view
    }

    private fun initViewByMediaSize(view: View) {
        byMediaSizeButton = view.findViewById(R.id.detailButton)
    }
}