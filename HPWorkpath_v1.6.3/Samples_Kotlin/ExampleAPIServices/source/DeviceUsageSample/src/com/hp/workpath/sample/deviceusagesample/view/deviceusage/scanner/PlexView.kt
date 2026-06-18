// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.scanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.deviceusage.Plex
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.Utils

class PlexView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutPlex: ViewGroup
    private lateinit var layoutSheets: ViewGroup

    fun setPlex(plexs: Array<Plex?>?) {
        rootView.removeAllViews()
        if (plexs != null) {
            for (index in plexs.indices) {
                rootView.addView(setPlexInternal(index, plexs[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPlexInternal(index: Int, plex: Plex?): View {
        val view = inflater.inflate(R.layout.layout_plex, rootView, false)
        initViewPlex(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.option_background_color))
        }
        if (plex != null) {
            Utils.setSummary(layoutPlex, plex.plex)
            Utils.setSummary(layoutSheets, plex.sheets)
        }
        return view
    }

    private fun initViewPlex(view: View) {
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex)
        layoutSheets = Utils.setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets)
    }
}