// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hp.workpath.sample.massstoragesample.model.FileInfo

class FileListAdapter(private val mFileListOnClickListener: FileListOnClickListener) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private var mData: MutableList<FileInfo> = ArrayList()
    private var checkedItemArray: BooleanArray? = null

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_file, parent, false)) {
        var fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        var fileCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            itemView.setOnClickListener(clickListener)
            fileCheckBox.setOnClickListener(clickListener);
        }

        fun initViewHolder(position: Int) {
            fileCheckBox.tag = position
            fileCheckBox.visibility = View.VISIBLE
            fileNameTextView.text = ""
        }
    }

    interface FileListOnClickListener {
        fun onDeleteButton(enable: Boolean)
        fun onRenameButton(enable: Boolean)
        fun onItemClick(view: View)
    }

    fun clear() {
        Log.i(MainActivity.TAG, "FileListAdapter clear")
        checkedItemArray = null
        mData.clear()
        notifyItemRangeRemoved(0, mData.size)
    }

    fun getItem(position: Int): FileInfo {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = getItem(position)
        holder.initViewHolder(position)
        holder.fileCheckBox.tag = position
        holder.fileNameTextView.text = file.fileName
        if (BACK == file.fileName) {
            holder.fileCheckBox.visibility = View.INVISIBLE
        }
        checkedItemArray?.run {
            holder.fileCheckBox.isChecked = this[position]
        }
    }

    val clickListener: View.OnClickListener = View.OnClickListener { v ->
        if (v.id == R.id.checkBox) {
            checkedItemArray?.let {
                it[v.tag as Int] = (v as CheckBox).isChecked
                var checkedCount = 0
                for (x in it.indices) {
                    if (it[x]) {
                        ++checkedCount
                    }
                }
                mFileListOnClickListener.onDeleteButton(checkedCount >= 1)
                mFileListOnClickListener.onRenameButton(checkedCount == 1)
            }
        } else {
            mFileListOnClickListener.onItemClick(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getCheckedItemArray(): BooleanArray? {
        return checkedItemArray
    }

    fun setListItems(data: MutableList<FileInfo>) {
        mData = data
        checkedItemArray = BooleanArray(data.size)
        notifyDataSetChanged()
    }

    companion object {
        const val BACK = "<< (UP)"
    }
}