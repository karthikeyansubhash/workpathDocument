// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.view

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.hp.workpath.sample.deviceeventsample.R

object Utils {

    fun setTitle(viewGroup: ViewGroup, id: Int): ViewGroup {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup
    }

    fun <T> setSummary(viewGroup: ViewGroup, value: T?) {
        try {
            if (value != null) {
                var valueString: String? = ""
                if (value is Enum<*>) {
                    valueString = (value as Enum<*>).name
                } else if (value is Int) {
                    valueString = value.toString()
                } else if (value is Array<*>) {
                    for (str in value) {
                        valueString = valueString + str + "\n"
                    }
                } else {
                    valueString = value as String?
                }
                (viewGroup.findViewById<View>(R.id.summaryTextView) as TextView).text = valueString
                return
            }
        } catch (ignore: Throwable) {
        }
        viewGroup.visibility = View.GONE
    }

    fun getLayout(viewGroup: ViewGroup, id: Int): LinearLayout {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup.findViewById<View>(R.id.layoutChild) as LinearLayout
    }
}