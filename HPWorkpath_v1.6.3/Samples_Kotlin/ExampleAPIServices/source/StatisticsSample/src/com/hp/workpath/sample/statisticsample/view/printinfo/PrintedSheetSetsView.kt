// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.print.PrintedSheetInfo.PrintedSheetSets
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class PrintedSheetSetsView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutBackImpressionClassification: ViewGroup
    private lateinit var layoutCount: ViewGroup
    private lateinit var layoutFrontImpressionClassification: ViewGroup
    private lateinit var layoutLogicalMediaOutputId: ViewGroup
    private lateinit var layoutMediaInputId: ViewGroup
    private lateinit var layoutMediaSizeId: ViewGroup
    private lateinit var layoutMediaTypeId: ViewGroup
    private lateinit var layoutPhysicalMediaOutputId: ViewGroup
    private lateinit var layoutPlex: ViewGroup

    fun setPrintedSheetSets(printedSheetSets: Array<PrintedSheetSets?>?) {
        rootView.removeAllViews()
        if (printedSheetSets != null) {
            for (index in printedSheetSets.indices) {
                rootView.addView(setPrintedSheetSetsInternal(index, printedSheetSets[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPrintedSheetSetsInternal(index: Int, printedSheetSets: PrintedSheetSets?): View {
        val view = inflater.inflate(R.layout.layout_printed_sheet_sets, rootView, false)
        initViewPrintedSheetSets(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (printedSheetSets != null) {
            Utils.setSummary(
                layoutBackImpressionClassification,
                printedSheetSets.backImpressionClassification
            )
            Utils.setSummary(layoutCount, printedSheetSets.count)
            Utils.setSummary(
                layoutFrontImpressionClassification,
                printedSheetSets.frontImpressionClassification
            )
            Utils.setSummary(layoutLogicalMediaOutputId, printedSheetSets.logicalMediaOutputId)
            Utils.setSummary(layoutMediaInputId, printedSheetSets.mediaInputId)
            Utils.setSummary(layoutMediaSizeId, printedSheetSets.mediaSizeId)
            Utils.setSummary(layoutMediaTypeId, printedSheetSets.mediaTypeId)
            Utils.setSummary(layoutPhysicalMediaOutputId, printedSheetSets.physicalMediaOutputId)
            Utils.setSummary(layoutPlex, printedSheetSets.plex)
        }
        return view
    }

    private fun initViewPrintedSheetSets(view: View) {
        layoutBackImpressionClassification = Utils.setTitle(
            view.findViewById(R.id.layoutBackImpressionClassification),
            R.string.backImpressionClassification
        )
        layoutCount = Utils.setTitle(view.findViewById(R.id.layoutCount), R.string.count)
        layoutFrontImpressionClassification = Utils.setTitle(
            view.findViewById(R.id.layoutFrontImpressionClassification),
            R.string.frontImpressionClassification
        )
        layoutLogicalMediaOutputId = Utils.setTitle(
            view.findViewById(R.id.layoutMediaInputId),
            R.string.logicalMediaOutputId
        )
        layoutMediaInputId =
            Utils.setTitle(view.findViewById(R.id.layoutMediaInputId), R.string.mediaInputId)
        layoutMediaSizeId =
            Utils.setTitle(view.findViewById(R.id.layoutMediaSizeId), R.string.mediaSizeId)
        layoutMediaTypeId =
            Utils.setTitle(view.findViewById(R.id.layoutMediaTypeId), R.string.mediaTypeId)
        layoutPhysicalMediaOutputId = Utils.setTitle(
            view.findViewById(R.id.layoutPhysicalMediaOutputId),
            R.string.physicalMediaOutputId
        )
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex)
    }
}