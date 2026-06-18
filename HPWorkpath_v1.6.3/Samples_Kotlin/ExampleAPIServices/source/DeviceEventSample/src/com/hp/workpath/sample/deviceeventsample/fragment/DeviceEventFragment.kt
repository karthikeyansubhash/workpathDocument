// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.hp.workpath.api.Result
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.sample.deviceeventsample.DeviceEventActivity
import com.hp.workpath.sample.deviceeventsample.Logger
import com.hp.workpath.sample.deviceeventsample.R
import com.hp.workpath.sample.deviceeventsample.databinding.FragmentDeviceEventBinding
import com.hp.workpath.sample.deviceeventsample.task.DeviceEventTask
import com.hp.workpath.sample.deviceeventsample.view.DeviceEventView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceEventFragment : Fragment() {

    private lateinit var mDeviceEvents: List<DeviceEvent>
    private lateinit var mDeviceEventView: DeviceEventView
    private var mBindingFragment: FragmentDeviceEventBinding? = null
    private val mBindingFragmentDeviceEvent get() = mBindingFragment!!
    var mIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentDeviceEventBinding.inflate(inflater, container, false)
        val view = mBindingFragmentDeviceEvent.root
        mDeviceEventView = DeviceEventView(inflater, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingFragmentDeviceEvent.deviceEventRawDataTextView.movementMethod =
            ScrollingMovementMethod()
        mIndex = arguments?.getInt(DeviceEventActivity.INDEX) ?: 0
        getDeviceEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private fun setRawData(deviceEvent: DeviceEvent) {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        mBindingFragmentDeviceEvent.deviceEventRawDataTextView.text = gson.toJson(deviceEvent)
    }

    private fun getDeviceEvent() {
        mBindingFragmentDeviceEvent.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            DeviceEventTask(requireContext(), deviceEventInterface).execute()
        }
    }

    private fun setDeviceEvent(deviceEvent: DeviceEvent) {
        mBindingFragmentDeviceEvent.titleTextView.text = deviceEvent.title
        setRawData(deviceEvent)
        mDeviceEventView.setDeviceEventData(deviceEvent)
    }

    private var deviceEventInterface: ResponseInterface = object : ResponseInterface {
        override fun success(deviceEvents: List<DeviceEvent>?) {
            mBindingFragmentDeviceEvent.progressBar.visibility = View.GONE
            mDeviceEvents = deviceEvents as List<DeviceEvent>
            if (mDeviceEvents.isNotEmpty()) {
                mBindingFragmentDeviceEvent.layoutDeviceEvent.layoutSummary.visibility =
                    View.VISIBLE
                setDeviceEvent(mDeviceEvents[mIndex - 1])
            } else {
                mBindingFragmentDeviceEvent.deviceEventRawDataTextView.text =
                    getString(R.string.no_event)
                mBindingFragmentDeviceEvent.layoutDeviceEvent.layoutSummary.visibility = View.GONE
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingFragmentDeviceEvent.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }
}