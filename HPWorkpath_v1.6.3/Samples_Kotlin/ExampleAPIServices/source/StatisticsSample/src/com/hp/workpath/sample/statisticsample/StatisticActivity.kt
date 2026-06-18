// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.hp.workpath.sample.statisticsample.fragment.StatisticFragment

class StatisticActivity : AppCompatActivity() {
    private lateinit var mStatisticFragment: StatisticFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        mStatisticFragment = StatisticFragment()
        val bundle = Bundle()
        bundle.putInt(INDEX, intent.getIntExtra(INDEX, 0))
        bundle.putBoolean(LAST_JOB, intent.getBooleanExtra(LAST_JOB, false))
        mStatisticFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mStatisticFragment)
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
        const val LAST_JOB = "last_job"
        const val REQUEST_KEY = "result-listener-request-key"
    }
}