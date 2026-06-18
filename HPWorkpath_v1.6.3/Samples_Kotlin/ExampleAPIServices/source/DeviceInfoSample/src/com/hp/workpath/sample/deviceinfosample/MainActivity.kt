// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.device.settings.DeviceSettingsService
import com.hp.workpath.sample.deviceinfosample.databinding.ActivityMainBinding
import com.hp.workpath.sample.deviceinfosample.model.DeviceInfo
import com.hp.workpath.sample.deviceinfosample.task.DeviceAttrReaderTask
import com.hp.workpath.sample.deviceinfosample.task.InitializationTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.EnumMap

/**
 * Main activity for DeviceInfo Sample.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Map [DeviceInfo]
     * Store references to summary TextViews to provide information
     */
    private val mSummaries = EnumMap<DeviceInfo, TextView>(DeviceInfo::class.java)

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBindingActivityMain: ActivityMainBinding
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)

        if (SCREEN_4_3_INCH == mBindingActivityMain.container.tag) {
            setSupportActionBar(mBindingActivityMain.toolbar)
            supportActionBar?.title = resources.getString(R.string.app_name)
            mBindingActivityMain.fabMenu?.setOnClickListener {
                if (mBindingActivityMain.buttonBarLayout.visibility == View.VISIBLE) {
                    val param = mBindingActivityMain.dataContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0, 0, 0, 0)
                    mBindingActivityMain.dataContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.GONE

                } else {
                    val param = mBindingActivityMain.dataContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0, 0, 0, 40)
                    mBindingActivityMain.dataContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.VISIBLE
                }
            }
        }

        // find the text and button
        findViewElements()
        // add click listener to call the MFP
        addListener()
    }

    override fun onResume() {
        super.onResume()
        mBindingActivityMain.container.isEnabled = false
        // call init task
        lifecycleScope.launch {
            InitializationTask(this@MainActivity).execute()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        var version = ""
        try {
            val workpathInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            version = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), workpathInfo.versionName, workpathInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }

        versionMenu.title = version
        return true
    }

    private fun findViewElements() {
        // Store views summaries and ids
        for (item in DeviceInfo.values()) {
            val itemLayout = findViewById<ViewGroup>(item.itemId)
            (itemLayout.findViewById<View>(R.id.titleTextView) as TextView).setText(item.titleId)
            mSummaries[item] = itemLayout.findViewById<View>(R.id.summaryTextView) as TextView
        }
    }

    private fun addListener() {
        mBindingActivityMain.getInformationButton.setOnClickListener {
            // execute the async task to read data from MFP
            lifecycleScope.launch(Dispatchers.IO) {
                DeviceAttrReaderTask(this@MainActivity).execute()
            }
            showProgress(View.VISIBLE)
        }

        mBindingActivityMain.enablePortButton.setOnClickListener {
            if (!DeviceSettingsService.isSupported(this)) {
                Logger.showResult(this, "DeviceSettingsService is not supported")
            } else {
                val result = Result()
                DeviceSettingsService.enableExternalPrinting(this, result)
                if (result.code == Result.RESULT_OK) {
                    Logger.showResult(this, getString(R.string.enable_ports_deprecated))
                } else {
                    Logger.showResult(this, "DeviceSettingsService.enableExternalPrinting", result)
                }
            }
        }

        mBindingActivityMain.disablePortButton.setOnClickListener {
            if (!DeviceSettingsService.isSupported(this)) {
                Logger.showResult(this, "DeviceSettingsService is not supported")
            } else {
                val result = Result()
                DeviceSettingsService.disableExternalPrinting(this, result)
                if (result.code == Result.RESULT_OK) {
                    Logger.showResult(this, "Ports disabled", result)
                } else {
                    Logger.showResult(this,"DeviceSettingsService.disableExternalPrinting", result)
                }
            }
        }
    }

    fun handleComplete() {
        showProgress(View.VISIBLE)
        mBindingActivityMain.container.isEnabled = true
        lifecycleScope.launch {
            DeviceAttrReaderTask(this@MainActivity).execute()
        }
    }

    fun showProgress(visibility: Int) {
        mBindingActivityMain.progressBar.visibility = visibility
    }

    fun handleUpdate(result: Map<DeviceInfo, String>) {
        // Fill device description with received info
        for (item in DeviceInfo.values()) {
            if (result.containsKey(item)) {
                if (applicationContext != null) {
                    mSummaries[item]?.text = result[item]
                }
            }
        }
    }

    /**
     * Exception in could be because of following reasons
     *
     *  1. Library is not installed
     *  2. Library update is needed
     *  3. Version issue, unsupported
     *
     */
    fun handleException(t: Throwable?) {
        var errorMsg = ""
        mBindingActivityMain.progressBar.visibility = View.GONE
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

    companion object {
        var TAG: String = "[SDK]" + "[DEVINFOS]"
    }
}