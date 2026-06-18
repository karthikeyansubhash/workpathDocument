// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hp.workpath.api.Result
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.sample.deviceeventsample.DeviceEventActivity
import com.hp.workpath.sample.deviceeventsample.Logger
import com.hp.workpath.sample.deviceeventsample.Logger.build
import com.hp.workpath.sample.deviceeventsample.R
import com.hp.workpath.sample.deviceeventsample.databinding.FragmentDeviceEventListBinding
import com.hp.workpath.sample.deviceeventsample.task.DeviceEventTask
import com.hp.workpath.sample.deviceeventsample.view.DeviceEventListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceEventListFragment : Fragment(), View.OnClickListener {

    private lateinit var mListAdapter: DeviceEventListAdapter
    private lateinit var mDeviceEvents: List<DeviceEvent>
    private var mBindingFragment: FragmentDeviceEventListBinding? = null
    private val mBindingFragmentDeviceEventList get() = mBindingFragment!!
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentDeviceEventListBinding.inflate(inflater, container, false)
        val tag = mBindingFragment?.layout?.tag
        if (SCREEN_4_3_INCH.equals(tag)) {
            (activity as AppCompatActivity).setSupportActionBar(mBindingFragment?.toolbar)
        }
        return mBindingFragmentDeviceEventList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingFragmentDeviceEventList.eventTextView.movementMethod = ScrollingMovementMethod()
        mBindingFragmentDeviceEventList.getDeviceEventListButton.setOnClickListener(this)
        mListAdapter = DeviceEventListAdapter(listOnClickListener)
        val layoutManager = LinearLayoutManager(context)
        mBindingFragmentDeviceEventList.deviceEventListView.layoutManager = layoutManager
        mBindingFragmentDeviceEventList.deviceEventListView.adapter = mListAdapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        mBindingFragmentDeviceEventList.deviceEventListView.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    fun getDeviceEventList() {
        mBindingFragmentDeviceEventList.noEventTextView.visibility = View.GONE
        mBindingFragmentDeviceEventList.progressBar.visibility = View.VISIBLE
        mBindingFragmentDeviceEventList.totalTextView.text = "0"
        mListAdapter.clear()
        lifecycleScope.launch(Dispatchers.Default) {
            DeviceEventTask(requireContext(), deviceEventInterface).execute()
        }
    }

    fun setEvent(deviceEvent: DeviceEvent?) {
        mBindingFragmentDeviceEventList.eventTextView.text = deviceEvent?.let { build(it) }
    }

    private var listOnClickListener = View.OnClickListener { v ->
        val itemPosition =
            mBindingFragmentDeviceEventList.deviceEventListView.getChildLayoutPosition(v)
        startDeviceEventActivity(itemPosition + 1)
    }

    private fun startDeviceEventActivity(index: Int) {
        if (index > 0 && index <= mDeviceEvents.size) {
            val intent = Intent(context, DeviceEventActivity::class.java)
            intent.putExtra(DeviceEventActivity.INDEX, index)
            startActivity(intent)
        } else {
            Logger.showResult(requireActivity(), getString(R.string.range_over))
        }
    }

    override fun onClick(v: View) {
        when (v) {
            mBindingFragmentDeviceEventList.getDeviceEventListButton -> {
                getDeviceEventList()
            }
        }
    }

    private var deviceEventInterface: ResponseInterface = object : ResponseInterface {
        override fun success(deviceEvents: List<DeviceEvent>?) {
            mBindingFragmentDeviceEventList.progressBar.visibility = View.GONE
            mDeviceEvents = deviceEvents as List<DeviceEvent>
            if (mDeviceEvents.isNotEmpty()) {
                mBindingFragmentDeviceEventList.totalTextView.text = mDeviceEvents.size.toString()
                mListAdapter.setItem(mDeviceEvents as MutableList<DeviceEvent>)
            } else {
                mBindingFragmentDeviceEventList.noEventTextView.visibility = View.VISIBLE
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingFragmentDeviceEventList.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }

}
