// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _C = ","
    private const val _EQ = "="
    public val _NF = if (BuildConfig.DEBUG) "\n" else ""
    fun build(accessoryInfo: AccessoryInfo?): String? {
        if (accessoryInfo != null) {
            if (accessoryInfo is HIDAccessoryInfo) {
                val logBuilder = StringBuilder()
                logBuilder.append(_START)
                logBuilder.append(_NF).append("registrationType").append(_EQ).append(accessoryInfo.registrationType).append(_C)
                logBuilder.append(_NF).append("PID").append(_EQ).append(accessoryInfo.productId).append(_C)
                logBuilder.append(_NF).append("VID").append(_EQ).append(accessoryInfo.vendorId).append(_C)
                logBuilder.append(_NF).append("S/N").append(_EQ).append(accessoryInfo.serialNumber)
                logBuilder.append(_NF).append(_END)
                return logBuilder.toString()
            }
        }
        return null
    }

    fun build(result: Result): String {
        val code = if (Result.RESULT_OK == result.code) "RESULT_OK" else "RESULT_FAIL"
        val builder = java.lang.StringBuilder()
        builder.append(_START)
        builder.append(_NF).append("Code:").append(code)
        if (Result.RESULT_OK != result.code && result.errorCode != null) {
            builder.append(_C).append(_NF).append("ErrorCode:").append(result.errorCode)
        }
        if (!TextUtils.isEmpty(result.cause)) {
            builder.append(_C).append(_NF).append("Cause:").append(result.cause)
        }
        builder.append(_NF).append(_END)
        return builder.toString()
    }

    fun showResult(activity: Activity?, msg: String?) {
        showResult(activity, msg, null)
    }

    fun showResult(activity: Activity?, msg: String?, result: Result?) {
        var message = msg
        if (result != null) {
            message = msg + Logger._NF + build(result)
            if (result.code == Result.RESULT_FAIL) {
                Log.e(MainActivity.TAG, message)
            } else {
                Log.d(MainActivity.TAG, message)
            }
        } else {
            message?.let { Log.d(MainActivity.TAG, it) }
        }
        if (activity != null && !activity.isFinishing) {
            showToastMessage(activity, message)
        }
    }

    private fun showToastMessage(activity: Activity, msg: String?) {
        activity.runOnUiThread { Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show() }
    }
}