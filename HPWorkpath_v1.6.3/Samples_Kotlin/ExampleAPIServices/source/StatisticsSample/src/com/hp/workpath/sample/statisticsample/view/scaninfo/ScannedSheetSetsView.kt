// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.scaninfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.scan.ScannedSheetInfo.ScannedSheetSets
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class ScannedSheetSetsView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutCount: ViewGroup
    private lateinit var layoutMediaInputId: ViewGroup
    private lateinit var layoutMediaSizeId: ViewGroup
    private lateinit var layoutPlex: ViewGroup

    fun setScannedSheetSets(scannedSheetSet: Array<ScannedSheetSets?>?) {
        rootView.removeAllViews()
        if (scannedSheetSet != null) {
            for (index in scannedSheetSet.indices) {
                rootView.addView(setScannedSheetSets(index, scannedSheetSet[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setScannedSheetSets(index: Int, scannedSheetSet: ScannedSheetSets?): View {
        val view = inflater.inflate(R.layout.layout_scanned_sheet_sets, rootView, false)
        initViewScannedSheetSets(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (scannedSheetSet != null) {
            Utils.setSummary(layoutCount, scannedSheetSet.count)
            Utils.setSummary(layoutMediaInputId, scannedSheetSet.mediaInputId)
            Utils.setSummary(layoutMediaSizeId, scannedSheetSet.mediaSizeId)
            Utils.setSummary(layoutPlex, scannedSheetSet.plex)
        }
        return view
    }

    private fun initViewScannedSheetSets(view: View) {
        layoutCount = Utils.setTitle(view.findViewById(R.id.layoutCount), R.string.count)
        layoutMediaInputId =
            Utils.setTitle(view.findViewById(R.id.layoutMediaInputId), R.string.mediaInputId)
        layoutMediaSizeId =
            Utils.setTitle(view.findViewById(R.id.layoutMediaSizeId), R.string.mediaSizeId)
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex)
    }
}