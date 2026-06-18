// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.hp.workpath.sample.configsample.model.SimplePrintOption

class OptionListAdapter(private var mContext: Context) : ArrayAdapter<Any?>(mContext, 0) {
    private var mOptions: ArrayList<OptionData> = ArrayList()

    fun setItem(option: SimplePrintOption?) {
        mOptions = ArrayList()
        if (option != null) {
            if (!TextUtils.isEmpty(option.colorMode)) {
                val data = OptionData(context.getString(R.string.print_color_mode), option.colorMode)
                mOptions.add(data)
            }
            if (!TextUtils.isEmpty(option.paperSize)) {
                val data = OptionData(context.getString(R.string.print_paper_size), option.paperSize)
                mOptions.add(data)
            }
            val data = OptionData(context.getString(R.string.print_copies), option.copies.toString())
            mOptions.add(data)
        }
    }

    override fun getCount(): Int {
        return mOptions.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.layout_option, parent, false)
        val nameTextView = rowView.findViewById<TextView>(R.id.optionNameTextView)
        val valueTextView = rowView.findViewById<TextView>(R.id.optionValueTextView)
        nameTextView.text = mOptions[position].optionName
        valueTextView.text = mOptions[position].optionValue
        return rowView
    }

    private inner class OptionData(var optionName: String, var optionValue: String?)
}