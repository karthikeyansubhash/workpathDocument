// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.deviceusage.printer.PrinterInfo.ByColorMode
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.Utils

class ByColorModeView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var layoutJobCategory: ViewGroup
    private lateinit var layoutColorMode: ViewGroup
    private lateinit var layoutColorModeType: ViewGroup
    private lateinit var layoutImpressions: ViewGroup

    fun setByColorMode(byColorModes: Array<ByColorMode?>?) {
        rootView.removeAllViews()
        if (byColorModes != null) {
            for (index in byColorModes.indices) {
                rootView.addView(setByColorModeInternal(index, byColorModes[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setByColorModeInternal(index: Int, colorMode: ByColorMode?): View {
        val view = inflater.inflate(R.layout.layout_by_color_mode, rootView, false)
        initViewByJobCategory(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.option_background_color))
        }
        if (colorMode != null) {
            Utils.setSummary(layoutJobCategory, colorMode.jobCategory)
            Utils.setSummary(layoutColorMode, colorMode.colorMode)
            Utils.setSummary(layoutColorModeType, colorMode.colorModeType)
            Utils.setSummary(layoutImpressions, colorMode.impressions)
        }
        return view
    }

    private fun initViewByJobCategory(view: View) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory)
        layoutColorMode = Utils.setTitle(view.findViewById(R.id.layoutColorMode), R.string.colorMode)
        layoutColorModeType = Utils.setTitle(view.findViewById(R.id.layoutColorModeType), R.string.colorModeType)
        layoutImpressions = Utils.setTitle(view.findViewById(R.id.layoutImpressions), R.string.impressions)
    }
}