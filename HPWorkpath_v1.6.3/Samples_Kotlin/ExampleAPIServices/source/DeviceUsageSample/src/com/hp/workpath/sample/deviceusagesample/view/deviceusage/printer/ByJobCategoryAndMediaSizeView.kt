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
import com.hp.workpath.api.deviceusage.printer.PrinterInfo.ByJobCategoryAndMediaSize
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R

class ByJobCategoryAndMediaSizeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var byJobCategoryAndMediaSizeButton: Button

    fun setByJobCategoryAndMediaSize(byJobCategoryAndMediaSizes: Array<ByJobCategoryAndMediaSize?>?) {
        rootView.removeAllViews()
        if (byJobCategoryAndMediaSizes != null) {
            rootView.addView(setByJobCategoryAndMediaSizeInternal(byJobCategoryAndMediaSizes))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setByJobCategoryAndMediaSizeInternal(categories: Array<ByJobCategoryAndMediaSize?>?): View {
        val view = inflater.inflate(R.layout.layout_button, rootView, false)
        initViewByJobCategory(view)
        if (categories != null) {
            byJobCategoryAndMediaSizeButton.setOnClickListener { v ->
                val gson = Gson()
                val intent = Intent(v.context, JobCategoryAndMediaSizeActivity::class.java)
                intent.putExtra(JobCategoryAndMediaSizeActivity.DATA, gson.toJson(categories))
                v.context.startActivity(intent)
            }
        }
        return view
    }

    private fun initViewByJobCategory(view: View) {
        byJobCategoryAndMediaSizeButton = view.findViewById(R.id.detailButton)
    }
}