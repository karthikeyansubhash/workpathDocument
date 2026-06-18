// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryservicesample

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.accessoryservicesample.ActionUtil.getAction
import com.hp.workpath.sample.accessoryservicesample.ActionUtil.setAction
import com.hp.workpath.sample.accessoryservicesample.databinding.ActivityMainBinding
import com.hp.workpath.sample.accessoryservicesample.model.UserDetails
import com.hp.workpath.sample.accessoryservicesample.task.InitializationTask
import com.hp.workpath.sample.accessoryservicesample.task.InitializationTask.InitializeInterface
import com.hp.workpath.sample.accessoryservicesample.task.UserDetailsReaderTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.EnumMap

/**
 * Main activity for AccessoryService Sample.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mAlertDialog: AlertDialog
    private var SCREEN_4_3_INCH = "Screen_4.3_Inch"

    /**
     * Map [UserDetails]
     * Store references to summary TextViews to provide information
     */
    private val mSummaries = EnumMap<UserDetails, TextView>(
        UserDetails::class.java
    )
    private lateinit var mBindingActivityMain: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }

        initView()
    }

    override fun onResume() {
        super.onResume()
        // call init task
        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity, initializeInterface).execute()
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

    private fun initView() {
        (findViewById<View>(R.id.headerUserInfo) as TextView).setText(R.string.header_user_info)
        (findViewById<View>(R.id.headerSignInAction) as TextView).setText(R.string.header_sign_in_action)

        mBindingActivityMain.signInActionButton.setOnClickListener(View.OnClickListener {
            try {
                setAction(this@MainActivity, mBindingActivityMain.signInActionSpinner.selectedItem as String)
                Logger.showResult(
                    this@MainActivity,
                    "Sign-in action is saved: " + mBindingActivityMain.signInActionSpinner.selectedItem
                )
            } catch (t: Throwable) {
                handleException(t)
            }
        })
        val signInActions = listOf(*resources.getStringArray(R.array.sign_in_action_arrays))
        val action = getAction(this@MainActivity)
        mBindingActivityMain.signInActionSpinner.setSelection(signInActions.indexOf(action.name))
        for (item in UserDetails.values()) {
            val itemLayout = findViewById<ViewGroup>(item.itemId)
            (itemLayout.findViewById<View>(R.id.titleTextView) as TextView).setText(item.titleId)
            mSummaries[item] = itemLayout.findViewById<View>(R.id.summaryTextView) as TextView
        }
    }

    fun handleUpdate(result: Map<UserDetails, String>) {
        // Fill device description with received info
        for (item in UserDetails.values()) {
            if (result.containsKey(item)) {
                mSummaries[item]?.text = result[item]
            }
        }
    }

    fun showProgress(visibility: Int) {
        mBindingActivityMain.progressBar.visibility = visibility
    }

    fun handleException(t: Throwable?) {
        showProgress(View.GONE)
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

    private var initializeInterface: InitializeInterface = object : InitializeInterface {
        override fun handleComplete() {
            showProgress(View.VISIBLE)
            lifecycleScope.launch(Dispatchers.Default) {
                UserDetailsReaderTask(this@MainActivity).execute()
            }
        }

        override fun handleException(t: Throwable?) {
            this@MainActivity.handleException(t)
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "AccessoryService"
    }
}