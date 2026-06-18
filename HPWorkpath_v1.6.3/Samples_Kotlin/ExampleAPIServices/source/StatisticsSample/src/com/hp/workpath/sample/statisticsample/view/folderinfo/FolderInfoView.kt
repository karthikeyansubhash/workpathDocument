// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.folderinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.folderinfo.FolderInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils
import com.hp.workpath.sample.statisticsample.view.common.DigitalFaxCallView
import com.hp.workpath.sample.statisticsample.view.common.DigitalSendInfoView

class FolderInfoView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var digitalFaxCallView: DigitalFaxCallView
    private lateinit var digitalSendInfoView: DigitalSendInfoView
    private lateinit var layoutDigitalFaxCalls: LinearLayout
    private lateinit var layoutDigitalSendInfo: LinearLayout
    private lateinit var layoutUncPath: ViewGroup
    private lateinit var layoutUserName: ViewGroup

    fun setFolderInfo(folderInfo: Array<FolderInfo?>?) {
        rootView.removeAllViews()
        if (folderInfo != null) {
            for (index in folderInfo.indices) {
                rootView.addView(setFolderInfoInternal(index, folderInfo[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setFolderInfoInternal(index: Int, folderInfo: FolderInfo?): View {
        val view = inflater.inflate(R.layout.layout_folder_info, rootView, false)
        initViewFolderInfo(view)
        initViewClass(inflater)
        if (index % 2 == 0) {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.option_background_color
                )
            )
        }
        if (folderInfo != null) {
            digitalFaxCallView.setDigitalFaxCall(folderInfo.digitalFaxCalls)
            digitalSendInfoView.setDigitalSendInfo(folderInfo.digitalSendInfo)
            Utils.setSummary(layoutUncPath, folderInfo.uncPath)
            Utils.setSummary(layoutUserName, folderInfo.userName)
        }
        return view
    }

    private fun initViewFolderInfo(view: View) {
        layoutDigitalFaxCalls =
            Utils.getLayout(view.findViewById(R.id.layoutDigitalFaxCalls), R.string.digitalFaxCalls)
        layoutDigitalSendInfo =
            Utils.getLayout(view.findViewById(R.id.layoutDigitalSendInfo), R.string.digitalSendInfo)
        layoutUncPath = Utils.setTitle(view.findViewById(R.id.layoutUncPath), R.string.uncPath)
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName)
    }

    private fun initViewClass(inflater: LayoutInflater) {
        digitalFaxCallView = DigitalFaxCallView(inflater, layoutDigitalFaxCalls)
        digitalSendInfoView = DigitalSendInfoView(inflater, layoutDigitalSendInfo)
    }
}