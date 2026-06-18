// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.ReportEventInfo
import com.hp.workpath.api.accessory.hid.AccessoryService
import com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryObserver
import com.hp.workpath.api.accessory.hid.EventCode
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo
import com.hp.workpath.api.launcher.LaunchAction
import com.hp.workpath.api.launcher.LauncherService
import com.hp.workpath.sample.launchersample.databinding.ActivityMainBinding
import com.hp.workpath.sample.launchersample.task.DeviceInformationTask
import com.hp.workpath.sample.launchersample.task.InitializationTask
import java.util.Arrays
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var mAccessoryObserver: AccessoryObserver
    private lateinit var mAccessoryStartObserver: AccessoryStartObserver
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBindingActivityMain: ActivityMainBinding

    private var accessories: List<AccessoryInfo>? = null
    var accessoryContextId: String? = null

    val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        // add click listener to call the MFP
        addListener()

        filter.addAction("com.hp.jetadvantage.link.action.WORKPATH_READY")
        filter.addAction("com.hp.jetadvantage.link.action.ACCESSORY_READY")
        mAccessoryObserver = AccessoryObserver(Handler(Looper.getMainLooper()))
        mAccessoryStartObserver = AccessoryStartObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()
        /* Background task for Workpath SDK API initialization */
        lifecycleScope.launch (Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
            DeviceInformationTask(this@MainActivity).execute()
        }
        registerReceiver(sdkAccessoryReadyReceiver, filter)
        mAccessoryObserver.register(applicationContext)
        mAccessoryStartObserver.register(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(sdkAccessoryReadyReceiver)
        mAccessoryObserver.unregister(applicationContext)
        mAccessoryStartObserver.unregister(applicationContext)

        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    private fun addListener() {
        mBindingActivityMain.goHomeButton.setOnClickListener { launchHomeScreen() }
        mBindingActivityMain.linkAppLaunchButton.setOnClickListener {
            if (TextUtils.isEmpty(mBindingActivityMain.linkAppUuidEditText.text.toString())) {
                Logger.showResult(this, getString(R.string.uuid_missing))
            } else {
                val uuid = mBindingActivityMain.linkAppUuidEditText.text.toString()
                launchApplication(uuid)
            }
        }
        mBindingActivityMain.linkAppUuidEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (before < count) {
                    val text = s.toString()
                    if (text.length == 8 || text.length == 13 || text.length == 18 || text.length == 23) {
                        mBindingActivityMain.linkAppUuidEditText.setText("$text-")
                        mBindingActivityMain.linkAppUuidEditText.setSelection(text.length + 1)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mBindingActivityMain.menuViewButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }
        mBindingActivityMain.getOwnedButton.setOnClickListener { ownedAccessory }
        mBindingActivityMain.resendButton.setOnClickListener {
            if(mBindingActivityMain.accessorySpinner.selectedItemPosition>-1) {
                val accessoryInfo =
                    accessories?.get(mBindingActivityMain.accessorySpinner.selectedItemPosition)
                accessoryInfo?.let { resendAccessory(it) }
            }else{
                Logger.showResult(this, "Registered OwnedAccessories size: " + mBindingActivityMain.accessorySpinner.adapter.count)
            }
        }

        mBindingActivityMain.openButton.setOnClickListener {
            if (!TextUtils.isEmpty(accessoryContextId)) {
                openAndStartReadingAccessory(accessoryContextId)
            } else {
                Logger.showResult(this, "Please resend accessory first")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            false
        } else super.onKeyDown(keyCode, event)
    }

    private fun launchHomeScreen() {
        val result = Result()
        LauncherService.launch(this@MainActivity, LaunchAction.HOME, result)
        if (result.code != Result.RESULT_OK) {
            Logger.showResult(this, "LauncherService.launch", result)
        }
    }

    private fun launchApplication(uuid: String) {
        val result = Result()
        LauncherService.launch(this@MainActivity, uuid, result)
        if (result.code != Result.RESULT_OK) {
            Logger.showResult(this, "LauncherService.launch", result)
        }
    }

    private val ownedAccessory: Unit
        get() {
            val result = Result()
            accessories = AccessoryService.getOwnedAccessories(applicationContext, result)
            if (result.code == Result.RESULT_OK) {
                val accessoryList = ArrayList<String?>()
                for (accessoryInfo in accessories as MutableList<AccessoryInfo>) {
                    accessoryList.add(Logger.build(accessoryInfo))
                }
                val adapter: ArrayAdapter<*> = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, accessoryList as List<Any?>)
                mBindingActivityMain.accessorySpinner.adapter = adapter
                Logger.showResult(this, "Registered OwnedAccessories size: " + accessoryList.size)
            } else {
                Logger.showResult(this, "AccessoryService.getOwnedAccessories", result)
            }
        }

    private fun resendAccessory(accessoryInfo: AccessoryInfo) {
        val result = Result()
        AccessoryService.resendOwnedAccessoryContext(
                applicationContext,
                accessoryInfo,
                result)
        if (result.code != Result.RESULT_OK) {
            Logger.showResult(this, "AccessoryService.resendOwnedAccessoryContext", result)
        }
    }

    private fun openAndStartReadingAccessory(accessoryContextId: String?) {
        val result = Result()
        accessoryContextId?.run {
            AccessoryService.open(applicationContext, this, result)
            if (result.code == Result.RESULT_OK) {
                AccessoryService.startReading(applicationContext, this, result)
                if (result.code == Result.RESULT_OK) {
                    Logger.showResult(this@MainActivity, "Ready")
                } else {
                    Logger.showResult(this@MainActivity, "AccessoryService.startReading", result)
                }
            } else {
                Logger.showResult(this@MainActivity, "AccessoryService.open", result)
            }
        }
    }

    fun handleComplete() {
        mBindingActivityMain.goHomeButton.isEnabled = true
        mBindingActivityMain.linkAppLaunchButton.isEnabled = true
    }

    fun handleDeviceInfo(hostname: String) {
        mBindingActivityMain.hostnameTextView.text = hostname
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
        Log.e(TAG, errorMsg)
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

    val sdkAccessoryReadyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.hp.jetadvantage.link.action.WORKPATH_READY" -> {
                    Log.i(TAG, "SDK initialized")
                    Toast.makeText(context, "SDK initialized", Toast.LENGTH_LONG).show()
                }
                "com.hp.jetadvantage.link.action.ACCESSORY_READY" -> {
                    Log.i(TAG, "Accessory initialized")
                    Toast.makeText(context, "Accessory initialized", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private inner class AccessoryObserver(handler: Handler?) : AbstractAccessoryObserver(handler) {
        override fun onContextChange(accessoryInfo: AccessoryInfo, eventCode: EventCode, timeStamp: String, accessoryContextId: String) {
            val log = StringBuilder()
                    .append("accessoryContextId=").append(accessoryContextId).append("\n")
                    .append(", eventCode=").append(eventCode).append("\n")
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString()
            Logger.showResult(this@MainActivity, "AccessoryObserver onContextChange(): $log")
            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                this@MainActivity.accessoryContextId = accessoryContextId
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                this@MainActivity.accessoryContextId = null
            }
        }

        override fun onReceive(accessoryInfo: AccessoryInfo, reportEventInfo: ReportEventInfo) {
            if (accessoryInfo.getDetails<AccessoryInfo?>() != null &&
                    accessoryInfo.getDetails<AccessoryInfo>() is HIDAccessoryInfo) {
                val hidAccessoryInfo = accessoryInfo.getDetails<HIDAccessoryInfo>()
                val hidReportEventInfo = reportEventInfo.getDetails<HIDReportEventInfo>()
                val accessoryData = Logger.build(hidAccessoryInfo)?.let {
                    StringBuilder(it).append("\n")
                            .append("data=")
                }
                if (hidReportEventInfo != null) {
                    for (hidReport in hidReportEventInfo.reports) {
                        accessoryData?.append(Arrays.toString(hidReport.data))
                    }
                }
                mBindingActivityMain.accessoryDataTextview.text = accessoryData.toString()
                Log.d(TAG, "AccessoryObserver onReceive(): $accessoryData")
            }
        }
    }

    private inner class AccessoryStartObserver(handler: Handler?) : AccessoryService.AbstractAccessoryStartObserver(handler) {

        override fun onReady(accessoryInfo: AccessoryInfo?, eventCode: EventCode?, timeStamp: String?, accessoryContextId: String?) {
            val log = java.lang.StringBuilder()
                    .append("accessoryContextId=").append(accessoryContextId).append("\n")
                    .append(", eventCode=").append(eventCode).append("\n")
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString()
            Log.i(TAG, "AccessoryObserver onReady $log")
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Launcher"
    }
}