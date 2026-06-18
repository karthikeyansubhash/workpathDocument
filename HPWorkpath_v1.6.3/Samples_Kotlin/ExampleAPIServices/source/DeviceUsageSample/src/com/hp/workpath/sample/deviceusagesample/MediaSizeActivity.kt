// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hp.workpath.sample.deviceusagesample.fragment.MediaSizeFragment

class MediaSizeActivity : AppCompatActivity() {
    private lateinit var mMediaSizeFragment: MediaSizeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_size)

        val bundle = Bundle()
        bundle.putString(DATA, intent.getStringExtra(DATA))
        mMediaSizeFragment = MediaSizeFragment()
        mMediaSizeFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mMediaSizeFragment)
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
        const val DATA = "data"
    }
}