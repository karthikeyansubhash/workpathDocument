// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.filebrowser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hp.workpath.sample.emailsample.R
import java.io.File

class FileListAdapter(context: Context?) : BaseAdapter() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mData: MutableList<File> = ArrayList()

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): File {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }

    fun setListItems(data: List<File>) {
        mData = data as MutableList<File>
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_file, parent, false)
        }

        val fileNameTextView = view?.findViewById<TextView>(R.id.fileNameTextView)
        val file = getItem(position)
        fileNameTextView?.text = file.name
        val icon = if (file.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
        fileNameTextView?.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        return view
    }

}