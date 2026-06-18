// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.webservice

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath

/**
 * Main activity for Print Sample.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mAlertDialog : AlertDialog

    private lateinit var mMethod : TextView

    private lateinit var  mRequestHeader : TextView

    private lateinit var mRequestBody : TextView

    private lateinit var mResponseBody : EditText


    companion object {
        const val TAG = "[SAMPLE] Linkbus"

        private lateinit var mHandler : TextViewHandler

        private var mResponse : String = ""

        const val KEY_METHOD : String = "method"

        const val KEY_HEADER : String = "header"

        const val KEY_BODY : String = "body"

        fun setText(method : String, header : String, body : String) {
            mHandler.let {
                val msg: Message = mHandler.obtainMessage()
                val bundle : Bundle = Bundle()
                bundle.putString(KEY_METHOD, method)
                bundle.putString(KEY_HEADER, header)
                bundle.putString(KEY_BODY, body)
                msg.data = bundle
                mHandler.sendMessage(msg)
            }
        }

        fun getResponse() : String {
            return mResponse
        }
    }

    inner class TextViewHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle : Bundle = msg.data
            mMethod.text = bundle.getString(KEY_METHOD)
            mRequestHeader.text = bundle.getString(KEY_HEADER)
            mRequestBody.text = bundle.getString(KEY_BODY)?:""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler = TextViewHandler()
        mMethod = findViewById(R.id.method_name)
        mRequestHeader = findViewById(R.id.request_header)
        mRequestBody = findViewById(R.id.request_body)
        mResponseBody = findViewById(R.id.response_body)
        mResponseBody.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mResponse = s.toString()
            }

        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
        mResponse = ""
    }

    override fun onDestroy() {
        super.onDestroy()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Exception in could be because of following reasons
     *
     *  1. Library is not installed
     *  2. Library update is needed
     *  3. Version issue, unsupported
     *
     */
    private fun handleException(t: Throwable?) {
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