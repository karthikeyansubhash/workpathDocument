// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.api.deviceusage.scanner.ScannerInfo.ByMediaSize
import com.hp.workpath.sample.deviceusagesample.R

class MediaSizeAdapter : RecyclerView.Adapter<MediaSizeAdapter.ViewHolder>() {
    private var mByMediaSizeList: List<ByMediaSize> = ArrayList()

    private fun getItem(position: Int): ByMediaSize {
        return mByMediaSizeList[position]
    }

    fun setItem(byMediaSizeList: List<ByMediaSize>) {
        mByMediaSizeList = byMediaSizeList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mByMediaSizeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_by_media_size, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.indexTextView.text = (position + 1).toString()
        holder.mediaSizeTextView.text = data.mediaSize
        if (data.mediaSizeType != null) {
            holder.mediaSizeTypeTextView.text = data.mediaSizeType.name
        }
        holder.imagesTextView.text = data.images.toString()
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var indexTextView: TextView = view.findViewById(R.id.indexTextView)
        var mediaSizeTextView: TextView = view.findViewById(R.id.mediaSizeTextView)
        var mediaSizeTypeTextView: TextView = view.findViewById(R.id.mediaSizeTypeTextView)
        var imagesTextView: TextView = view.findViewById(R.id.imagesTextView)
    }
}