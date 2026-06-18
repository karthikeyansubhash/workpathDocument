// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.attestationsample

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.attestation.AppToken
import com.hp.workpath.sample.attestationsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.attestationsample.task.GetAppTokenTask
import com.hp.workpath.sample.attestationsample.task.InitializationTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBindingActivityMain: ActivityMainBinding

    private var SCREEN_4_3_INCH = "Screen_4.3_Inch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }

        // add click listener to call the MFP
        addListener()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch (Dispatchers.Default) {
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
        mBindingActivityMain.getAppTokenButton.setOnClickListener { appToken }
    }

    private val appToken: Unit
        get() {
            showProgressBar(View.VISIBLE)
            lifecycleScope.launch (Dispatchers.Default) {
                GetAppTokenTask(this@MainActivity).execute()
            }
        }

    fun showProgressBar(visibility: Int) {
        mBindingActivityMain.progressBar.visibility = visibility
    }

    fun getAppTokenComplete(appToken: AppToken?, result: Result?) {
        if (appToken != null) {
            mBindingActivityMain.tokenTextView.text = appToken.appToken
            mBindingActivityMain.expiresInTextView.text = appToken.expiresIn.toString()
        } else {
            mBindingActivityMain.tokenTextView.text = getString(R.string.na)
            mBindingActivityMain.expiresInTextView.text = getString(R.string.na)
        }
        if (result != null) {
            if (result.code == Result.RESULT_OK) {
                mBindingActivityMain.resultTextView.text = getString(R.string.result_success, "Result.RESULT_OK")
            } else if (result.code == Result.RESULT_FAIL) {
                mBindingActivityMain.resultTextView.text = getString(R.string.result_failed, "Result.RESULT_FAIL", result.errorCode.name, result.cause)
            }
        }
    }

    fun handleComplete() {
        mBindingActivityMain.getAppTokenButton.isEnabled = true
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
        const val TAG = "[SAMPLE]" + "Attestation"
    }
}