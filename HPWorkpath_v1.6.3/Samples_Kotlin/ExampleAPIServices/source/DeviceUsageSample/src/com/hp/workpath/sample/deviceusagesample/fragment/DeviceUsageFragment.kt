// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.hp.workpath.api.Result
import com.hp.workpath.api.deviceusage.DeviceUsageInfo
import com.hp.workpath.sample.deviceusagesample.Logger
import com.hp.workpath.sample.deviceusagesample.R
import com.hp.workpath.sample.deviceusagesample.databinding.FragmentDeviceUsageBinding
import com.hp.workpath.sample.deviceusagesample.task.DeviceUsageTask
import com.hp.workpath.sample.deviceusagesample.view.DeviceUsageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceUsageFragment : Fragment() {
    private lateinit var deviceUsageView: DeviceUsageView
    private var mBindingFragment: FragmentDeviceUsageBinding? = null
    private val mBindingFragmentDeviceUsage get() = mBindingFragment!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBindingFragment = FragmentDeviceUsageBinding.inflate(inflater, container, false)
        val view = mBindingFragmentDeviceUsage.root
        deviceUsageView = DeviceUsageView(inflater, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingFragmentDeviceUsage.deviceUsageRawDataTextView.movementMethod = ScrollingMovementMethod()
        mBindingFragmentDeviceUsage.getDeviceUsageButton.setOnClickListener { getDeviceUsage() }
        getDeviceUsage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    fun getDeviceUsage() {
        mBindingFragmentDeviceUsage.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch (Dispatchers.Default) {
            DeviceUsageTask(requireContext(), deviceUsageInterface).execute()
        }
    }

    private fun setDeviceUsage(deviceUsageInfo: DeviceUsageInfo) {
        deviceUsageView.setDeviceUsageInfo(deviceUsageInfo)
    }

    private var deviceUsageInterface: ResponseInterface = object : ResponseInterface {
        override fun success(info: DeviceUsageInfo?) {
            mBindingFragmentDeviceUsage.progressBar.visibility = View.GONE
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            if (info != null) {
                mBindingFragmentDeviceUsage.deviceUsageRawDataTextView.text = gson.toJson(info)
                setDeviceUsage(info)
            }
        }

        override fun failure(msg: String?, result: Result?) {
            mBindingFragmentDeviceUsage.progressBar.visibility = View.GONE
            Logger.showResult(requireActivity(), getString(R.string.failed), result)
        }

    }
}