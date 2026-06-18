// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File
import com.hp.workpath.sample.googlesigninsample.R

class FileListViewAdapter(var files: List<File>) :
    RecyclerView.Adapter<FileListViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val id: TextView
        val mimeType: TextView

        init {
            name = view.findViewById(R.id.name_text_view)
            id = view.findViewById(R.id.id_text_view)
            mimeType = view.findViewById(R.id.mime_type_text_view)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_file_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id.text = files[position].id
        holder.mimeType.text = files[position].mimeType
        holder.name.text = files[position].name
    }
}