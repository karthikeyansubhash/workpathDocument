// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.ReportEventInfo
import com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryObserver
import com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryStartObserver
import com.hp.workpath.api.accessory.hid.EventCode
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDInfo
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo
import com.hp.workpath.sample.accessorysample.Logger.build
import com.hp.workpath.sample.accessorysample.databinding.ActivityMainBinding
import com.hp.workpath.sample.accessorysample.databinding.LayoutAccessoryDataBinding
import com.hp.workpath.sample.accessorysample.fragment.AccessoryFragment
import com.hp.workpath.sample.accessorysample.fragment.AccessoryListFragment
import com.hp.workpath.sample.accessorysample.fragment.AccessoryReportsFragment
import com.hp.workpath.sample.accessorysample.task.ActionTask
import com.hp.workpath.sample.accessorysample.task.InitializationTask
import com.hp.workpath.sample.accessorysample.task.InitializationTask.InitializeInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Main activity for Accessory Sample.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAccessoryObserver: AccessoryObserver
    private lateinit var mAccessoryStartObserver: AccessoryStartObserver
    private lateinit var mAccessoryFragment: AccessoryFragment
    private lateinit var mAccessoryListFragment: AccessoryListFragment
    private lateinit var mAccessoryReportFragment: AccessoryReportsFragment

    private var mAccessoryContextId: String? = null
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar
    private lateinit var mBindingActivityMain: ActivityMainBinding
    private lateinit var mBindingLayoutAccessoryData: LayoutAccessoryDataBinding
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        val tag = mBindingActivityMain.layout?.tag
        if (SCREEN_4_3_INCH.equals(tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }
        mBindingLayoutAccessoryData = LayoutAccessoryDataBinding.bind(mBindingActivityMain.root)
        setContentView(mBindingActivityMain.root)
        setOnClickListener()
        mAccessoryObserver = AccessoryObserver(Handler(Looper.getMainLooper()))
        mAccessoryStartObserver = AccessoryStartObserver(Handler(Looper.getMainLooper()))
    }

    private fun setOnClickListener() {
        mBindingActivityMain.openButton.setOnClickListener(this)
        mBindingActivityMain.closeButton.setOnClickListener(this)
        mBindingActivityMain.startReadingButton.setOnClickListener(this)
        mBindingActivityMain.stopReadingButton.setOnClickListener(this)
        mBindingActivityMain.readReportButton.setOnClickListener(this)
        mBindingActivityMain.writeReportButton.setOnClickListener(this)
        mBindingActivityMain.fab?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            mBindingActivityMain.openButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.OPEN, mAccessoryContextId)
                }
            }
            mBindingActivityMain.closeButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.CLOSE, mAccessoryContextId)
                }
            }
            mBindingActivityMain.startReadingButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.START_READ, mAccessoryContextId)
                }
            }
            mBindingActivityMain.stopReadingButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.STOP_READ, mAccessoryContextId)
                }
            }
            mBindingActivityMain.readReportButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.READ_REPORT, mAccessoryContextId)
                }
            }
            mBindingActivityMain.writeReportButton -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    ActionTask(this@MainActivity).execute(Action.WRITE_REPORT, mAccessoryContextId)
                }
            }
            mBindingActivityMain.fab -> {
                if (mBindingActivityMain.buttonBarRow?.getVisibility() == View.VISIBLE) {

                    mBindingActivityMain.buttonBarRow?.setVisibility(View.GONE)
                } else {

                    mBindingActivityMain.buttonBarRow?.setVisibility(View.VISIBLE)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mAccessoryObserver.register(applicationContext)
        mAccessoryStartObserver.register(applicationContext)
        mBindingActivityMain.container.isEnabled = false
        // call init task
        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity, initializeInterface).execute()
        }

        mAccessoryFragment = AccessoryFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.accessoryContainer, mAccessoryFragment)
            .commit()

        mAccessoryListFragment = AccessoryListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.accessoryListContainer, mAccessoryListFragment)
            .commit()

        mAccessoryReportFragment = AccessoryReportsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.accessoryReportContainer, mAccessoryReportFragment)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        mAccessoryObserver.unregister(applicationContext)
        mAccessoryStartObserver.unregister(applicationContext)

        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
        if (this::mSnackBar.isInitialized) {
            mSnackBar.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(
                R.string.version,
                pInfo.versionName,
                pInfo.longVersionCode.toInt(),
                sdkInfo.versionName,
                sdkInfo.versionCode
            )
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    fun setAccessoryContextId(accessoryInfo: AccessoryInfo?, accessoryContextId: String?) {
        mAccessoryContextId = accessoryContextId
        mAccessoryContextId?.run {
            mAccessoryFragment.updateAccessoryContextId(accessoryInfo, this)
            mAccessoryListFragment.updateReservedAccessory(accessoryInfo, this)
        }
    }

    fun loadAccessories(action: Action?, enumeratedAccessories: List<AccessoryInfo>?) {
        if (enumeratedAccessories != null) {
            for (accessoryInfo in enumeratedAccessories) {
                Log.i(TAG, "AccessoryInfo=" + build(accessoryInfo))
            }
            mAccessoryListFragment.loadAccessories(action, enumeratedAccessories)
        }
    }

    fun setInfo(hidInfo: HIDInfo?) {
        if (hidInfo != null) {
            mAccessoryReportFragment.setInfo(hidInfo)
        }
    }

    /**
     * Observer for accessory event.
     */
    private inner class AccessoryObserver(handler: Handler?) : AbstractAccessoryObserver(handler) {
        override fun onContextChange(
            accessoryInfo: AccessoryInfo,
            eventCode: EventCode,
            timeStamp: String,
            accessoryContextId: String
        ) {
            Log.i(
                TAG,
                "AccessoryObserver onContextChange() accessoryContextId: $accessoryContextId"
            )
            val log = StringBuilder().append("eventCode=").append(eventCode)
                .append(", timestamp=").append(timeStamp).append("\n")
                .append("AccessoryInfo=").append(build(accessoryInfo)).toString()
            Log.i(TAG, log)
            showSnackBar(log)
            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                setAccessoryContextId(accessoryInfo, accessoryContextId)
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                setAccessoryContextId(accessoryInfo, null)
            }
        }

        override fun onReceive(accessoryInfo: AccessoryInfo, reportEventInfo: ReportEventInfo) {
            if (accessoryInfo.getDetails<AccessoryInfo?>() != null &&
                accessoryInfo.getDetails<AccessoryInfo>() is HIDAccessoryInfo
            ) {
                val hidAccessoryInfo = accessoryInfo.getDetails<HIDAccessoryInfo>()
                val hidReportEventInfo = reportEventInfo.getDetails<HIDReportEventInfo>()

                Log.i(TAG, "AccessoryObserver onReceive()")
                Log.i(TAG, "AccessoryInfo=" + build(hidAccessoryInfo))
                val accessoryData = StringBuilder(build(hidAccessoryInfo)!!)
                accessoryData.append("\ndata=")
                var comma = ""
                if (hidReportEventInfo != null) {
                    for (hidReport in hidReportEventInfo.reports) {
                        accessoryData.append(comma).append(Arrays.toString(hidReport.data))
                        comma = ", "
                    }
                }
                setAccessoryData(accessoryData.toString())
            }
        }
    }

    private class AccessoryStartObserver(handler: Handler?) :
        AbstractAccessoryStartObserver(handler) {
        override fun onReady(
            accessoryInfo: AccessoryInfo,
            eventCode: EventCode,
            timeStamp: String,
            accessoryContextId: String
        ) {
            val log = java.lang.StringBuilder()
                .append("accessoryContextId=").append(accessoryContextId).append("\n")
                .append(", eventCode=").append(eventCode).append("\n")
                .append(", timestamp=").append(timeStamp).append("\n")
                .append("AccessoryInfo=").append(build(accessoryInfo)).toString()
            Log.i(TAG, "AccessoryObserver onReady(): $log")
        }
    }

    private var initializeInterface: InitializeInterface = object : InitializeInterface {
        override fun handleComplete() {
            mBindingActivityMain.container.isEnabled = true
        }

        override fun handleException(t: Throwable?) {
            this@MainActivity.handleException(t)
        }
    }

    fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(
                    R.string.sdk_support_missing
                )
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
        Log.e(TAG, errorMsg)
        mAlertDialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Error")
            .setMessage(errorMsg)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    fun setAccessoryData(data: String?) {
        runOnUiThread {
            mBindingLayoutAccessoryData.accessoryDataTextview.text = data
        }
    }

    fun showSnackBar(text: String) {
        runOnUiThread {
            if (!::mSnackBar.isInitialized) {
                mSnackBar = Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
                val snackBarView = mSnackBar.view
                val tv = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv?.maxLines = 3
            }
            mSnackBar.run {
                setText(text)
                setActionTextColor(ContextCompat.getColor(view.context, R.color.snackbar_button_color))
                setAction(getString(R.string.ok)) { mSnackBar.dismiss() }
                show()
            }
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Accessory"
    }
}