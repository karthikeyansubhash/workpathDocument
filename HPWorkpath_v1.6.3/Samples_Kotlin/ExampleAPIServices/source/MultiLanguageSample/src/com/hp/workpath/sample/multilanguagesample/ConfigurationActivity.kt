// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.multilanguagesample

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.multilanguagesample.databinding.ActivityConfigurationBinding

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBindingActivityConfiguration: ActivityConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityConfiguration = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(mBindingActivityConfiguration.root)
        // find the button
        findViewElements()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo: PackageInfo = getPackageManager().getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    private fun findViewElements() {
        mBindingActivityConfiguration.messageButton.setOnClickListener { Toast.makeText(this@ConfigurationActivity, getString(R.string.message, "[Office]"), Toast.LENGTH_LONG).show() }
    }

    private fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(R.string.sdk_support_missing)
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
        mAlertDialog = AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
    }
}