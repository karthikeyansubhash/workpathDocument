// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.FileInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils

class FileInfoView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutDataSize: ViewGroup
    private lateinit var layoutFileName: ViewGroup

    fun setFileInfo(fileInfo: FileInfo?) {
        rootView.removeAllViews()
        if (fileInfo != null) {
            rootView.addView(setFileInfoInternal(1, fileInfo))
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    fun setFileInfo(fileInfos: Array<FileInfo?>?) {
        rootView.removeAllViews()
        if (fileInfos != null) {
            for (index in fileInfos.indices) {
                rootView.addView(setFileInfoInternal(index, fileInfos[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setFileInfoInternal(index: Int, fileInfo: FileInfo?): View {
        val view = inflater.inflate(R.layout.layout_file_info, rootView, false)
        initViewFileInfo(view)
        if (index % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.option_background_color))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.context,android.R.color.white))
        }
        if (fileInfo != null) {
            Utils.setSummary(layoutDataSize, fileInfo.dataSize)
            Utils.setSummary(layoutFileName, fileInfo.fileName)
        }
        return view
    }

    private fun initViewFileInfo(view: View) {
        layoutDataSize = Utils.setTitle(view.findViewById(R.id.layoutDataSize), R.string.dataSize)
        layoutFileName = Utils.setTitle(view.findViewById(R.id.layoutFileName), R.string.fileName)
    }
}