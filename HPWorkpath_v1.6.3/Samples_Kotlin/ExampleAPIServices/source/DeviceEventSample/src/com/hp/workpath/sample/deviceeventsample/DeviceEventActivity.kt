// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hp.workpath.sample.deviceeventsample.fragment.DeviceEventFragment

class DeviceEventActivity : AppCompatActivity() {

    private lateinit var mDeviceEventFragment: DeviceEventFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_event)
        val bundle = Bundle()
        bundle.putInt(INDEX, intent.getIntExtra(INDEX, 0))
        mDeviceEventFragment = DeviceEventFragment()
        mDeviceEventFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mDeviceEventFragment)
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }

    companion object {
        const val INDEX = "index"
    }
}