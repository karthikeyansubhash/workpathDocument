// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.view

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.supplies.supplyinfo.Supply
import com.hp.workpath.sample.suppliessample.R
import java.util.ArrayList

class SupplyListAdapter(var mListOnClickListener: View.OnClickListener) : RecyclerView.Adapter<SupplyListAdapter.ViewHolder>() {
    var mSupplyList: MutableList<Supply> = ArrayList()

    private fun getItem(position: Int): Supply {
        return mSupplyList[position]
    }

    fun setItem(supplyList: List<Supply>) {
        mSupplyList = supplyList.toMutableList()
        notifyDataSetChanged()
    }

    fun clear() {
        val size = mSupplyList.size
        if (size > 0) {
            mSupplyList.clear()
            notifyItemRangeRemoved(0, size)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mSupplyList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_supply_list, parent, false)
        view.setOnClickListener(mListOnClickListener)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val supplyData = getItem(position)
        holder.sequenceTextView.text = (position + 1).toString()
        holder.consumableTypeTextView.text = supplyData.consumableTypeEnum
        holder.approxPercentTextView.text = supplyData.approxPercentRemaining
        holder.productNumberTextView.text = supplyData.productNumber
        holder.descriptionTextView.text = supplyData.description
        setDescriptionBackground(holder.descriptionTextView, supplyData.description)
    }

    private fun setDescriptionBackground(textView: TextView, description: String) {
        if (!TextUtils.isEmpty(description)) {
            if (description.contains("Yellow")) {
                textView.setBackgroundResource(R.color.yellow)
            } else if (description.contains("Magenta")) {
                textView.setBackgroundResource(R.color.magenta)
            } else if (description.contains("Cyan")) {
                textView.setBackgroundResource(R.color.cyan)
            } else if (description.contains("Black")) {
                textView.setBackgroundResource(R.color.black)
            }
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var sequenceTextView: TextView = view.findViewById(R.id.sequenceTextView)
        var consumableTypeTextView: TextView = view.findViewById(R.id.consumableTypeTextView)
        var approxPercentTextView: TextView = view.findViewById(R.id.approxPercentTextView)
        var productNumberTextView: TextView = view.findViewById(R.id.productNumberTextView)
        var descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }
}