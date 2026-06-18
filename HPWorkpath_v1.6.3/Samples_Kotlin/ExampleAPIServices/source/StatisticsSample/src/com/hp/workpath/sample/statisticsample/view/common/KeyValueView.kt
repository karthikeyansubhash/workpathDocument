// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.userinfo.ExtendedUserInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class KeyValueView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutKey: ViewGroup
    private lateinit var layoutValueString: ViewGroup

    fun setKeyValue(keyValues: Array<ExtendedUserInfo.KeyValue?>?) {
        rootView.removeAllViews()
        if (keyValues != null) {
            for (index in keyValues.indices) {
                rootView.addView(setKeyValueInternal(index, keyValues[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setKeyValueInternal(index: Int, keyValue: ExtendedUserInfo.KeyValue?): View {
        val view = inflater.inflate(R.layout.layout_key_value, rootView, false)
        initViewKeyValue(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (keyValue != null) {
            Utils.setSummary(layoutKey, keyValue.key)
            Utils.setSummary(layoutValueString, keyValue.valueString)
        }
        return view
    }

    private fun initViewKeyValue(view: View) {
        layoutKey = Utils.setTitle(view.findViewById(R.id.layoutKey), R.string.key)
        layoutValueString =
            Utils.setTitle(view.findViewById(R.id.layoutValueString), R.string.valueString)
    }
}