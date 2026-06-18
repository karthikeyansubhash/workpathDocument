// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hp.workpath.sample.suppliessample.fragment.SupplyFragment

class SupplyActivity : AppCompatActivity() {
    private lateinit var mSupplyFragment: SupplyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply)
        mSupplyFragment = SupplyFragment()
        val bundle = Bundle()
        bundle.putInt(INDEX, intent.getIntExtra(INDEX, 0))
        mSupplyFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mSupplyFragment)
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