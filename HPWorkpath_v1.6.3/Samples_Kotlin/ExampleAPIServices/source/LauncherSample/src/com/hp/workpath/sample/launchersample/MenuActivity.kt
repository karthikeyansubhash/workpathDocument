// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample

import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.hp.workpath.sample.launchersample.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var mBindingActivityMenu: ActivityMenuBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMenu = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMenu.root)
        findViewElements()
    }

    private fun findViewElements() {
        mBindingActivityMenu.copyButton.setOnClickListener(buttonClickListener)
        mBindingActivityMenu.printButton.setOnClickListener(buttonClickListener)
        mBindingActivityMenu.scanButton.setOnClickListener(buttonClickListener)
    }

    private var buttonClickListener = View.OnClickListener { view ->
        if (view is Button) {
            val text = view.text.toString()
            Toast.makeText(this@MenuActivity, "$text Button", Toast.LENGTH_SHORT).show()
        }
    }
}