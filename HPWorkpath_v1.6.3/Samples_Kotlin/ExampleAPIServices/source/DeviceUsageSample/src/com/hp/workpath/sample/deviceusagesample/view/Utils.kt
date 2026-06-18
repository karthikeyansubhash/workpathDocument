// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.hp.workpath.sample.deviceusagesample.R

object Utils {

    fun setTitle(viewGroup: ViewGroup, id: Int): ViewGroup {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup
    }

    fun <T> setSummary(viewGroup: ViewGroup, value: T?) {
        try {
            if (value != null) {
                val valueString: String = when (value) {
                    is Enum<*> -> {
                        (value as Enum<*>).name
                    }
                    is Int -> {
                        value.toString()
                    }
                    else -> {
                        value as String
                    }
                }
                (viewGroup.findViewById<View>(R.id.summaryTextView) as TextView).text = valueString
                return
            }
        } catch (ignore: Throwable) { }
        viewGroup.visibility = View.GONE
    }

    fun getLayout(viewGroup: ViewGroup, id: Int): LinearLayout {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup.findViewById<View>(R.id.layoutChild) as LinearLayout
    }
}