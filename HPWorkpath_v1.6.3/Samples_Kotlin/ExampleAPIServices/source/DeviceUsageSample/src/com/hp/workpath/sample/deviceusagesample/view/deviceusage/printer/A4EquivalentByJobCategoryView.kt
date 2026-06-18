// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.deviceusage.printer.PrinterInfo
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.Utils

class A4EquivalentByJobCategoryView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var layoutJobCategory: ViewGroup
    private lateinit var layoutColorDeciImpressions: ViewGroup
    private lateinit var layoutMonoDeciImpressions: ViewGroup
    private lateinit var layoutTotalDeciImpressions: ViewGroup

    fun setA4EquivalentByJobCategory(byColorModes: Array<PrinterInfo.A4EquivalentByJobCategory?>?) {
        rootView.removeAllViews()
        if (byColorModes != null) {
            for (index in byColorModes.indices) {
                rootView.addView(setA4EquivalentByJobCategoryInternal(index, byColorModes[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setA4EquivalentByJobCategoryInternal(index: Int, a4EquivalentByJobCategory: PrinterInfo.A4EquivalentByJobCategory?): View {
        val view = inflater.inflate(R.layout.layout_a4_equivalent_by_job_category, rootView, false)
        initViewByJobCategory(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.option_background_color))
        }
        if (a4EquivalentByJobCategory != null) {
            Utils.setSummary(layoutJobCategory, a4EquivalentByJobCategory.jobCategory)
            Utils.setSummary(layoutColorDeciImpressions, a4EquivalentByJobCategory.colorDeciImpressions)
            Utils.setSummary(layoutMonoDeciImpressions, a4EquivalentByJobCategory.monoDeciImpressions)
            Utils.setSummary(layoutTotalDeciImpressions, a4EquivalentByJobCategory.totalDeciImpressions)
        }
        return view
    }

    private fun initViewByJobCategory(view: View) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory)
        layoutColorDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutColorDeciImpressions), R.string.colorDeciImpressions)
        layoutMonoDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutMonoDeciImpressions), R.string.monoDeciImpressions)
        layoutTotalDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutTotalDeciImpressions), R.string.totalDeciImpressions)
    }
}