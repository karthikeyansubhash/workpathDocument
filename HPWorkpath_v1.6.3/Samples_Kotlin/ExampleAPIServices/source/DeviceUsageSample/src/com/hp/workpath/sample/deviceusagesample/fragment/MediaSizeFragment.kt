// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hp.workpath.api.deviceusage.scanner.ScannerInfo.ByMediaSize
import com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.view.MediaSizeAdapter

class MediaSizeFragment : Fragment() {
    private lateinit var mListView: RecyclerView
    private lateinit var mListAdapter: MediaSizeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_media_size, container, false)
        mListView = view.findViewById(R.id.mediaSizeView)
        mListAdapter = MediaSizeAdapter()
        val layoutManager = LinearLayoutManager(context)
        mListView.layoutManager = layoutManager
        mListView.adapter = mListAdapter
        val dividerItemDecoration = DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)
        mListView.addItemDecoration(dividerItemDecoration)

        val data = arguments?.getString(JobCategoryAndMediaSizeActivity.DATA)
        showJobCategoryAndMediaSize(data)
        return view
    }

    private fun showJobCategoryAndMediaSize(data: String?) {
        val gson = Gson()
        val listType = object : TypeToken<List<ByMediaSize?>?>() {}.type
        val list = gson.fromJson<List<ByMediaSize>>(data, listType)
        mListAdapter.setItem(list)
    }
}