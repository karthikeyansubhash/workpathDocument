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

class ByJobCategoryView(var inflater: LayoutInflater, private var rootView: LinearLayout) {
    private lateinit var layoutJobCategory: ViewGroup
    private lateinit var layoutJobCategoryType: ViewGroup
    private lateinit var layoutA4EquivalentDeciImpressions: ViewGroup

    fun setByJobCategory(byJobCategories: Array<PrinterInfo.ByJobCategory?>?) {
        rootView.removeAllViews()
        if (byJobCategories != null) {
            for (index in byJobCategories.indices) {
                rootView.addView(setByJobCategoryInternal(index, byJobCategories[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setByJobCategoryInternal(index: Int, category: PrinterInfo.ByJobCategory?): View {
        val view = inflater.inflate(R.layout.layout_by_job_category, rootView, false)
        initViewByJobCategory(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.option_background_color))
        }
        if (category != null) {
            Utils.setSummary(layoutJobCategory, category.jobCategory)
            Utils.setSummary(layoutJobCategoryType, category.jobCategoryType)
            Utils.setSummary(layoutA4EquivalentDeciImpressions, category.a4EquivalentDeciImpressions)
        }
        return view
    }

    private fun initViewByJobCategory(view: View) {
        layoutJobCategory = Utils.setTitle(view.findViewById(R.id.layoutJobCategory), R.string.jobCategory)
        layoutJobCategoryType = Utils.setTitle(view.findViewById(R.id.layoutJobCategoryType), R.string.jobCategoryType)
        layoutA4EquivalentDeciImpressions = Utils.setTitle(view.findViewById(R.id.layoutA4EquivalentDeciImpressions), R.string.a4EquivalentDeciImpressions)
    }
}