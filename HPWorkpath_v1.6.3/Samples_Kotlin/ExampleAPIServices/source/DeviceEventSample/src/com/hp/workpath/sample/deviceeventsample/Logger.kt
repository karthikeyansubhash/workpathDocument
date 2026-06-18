// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

import com.hp.workpath.api.Result
import com.hp.workpath.api.device.events.DeviceEvent

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _C = ","
    private const val _EQ = "="
    val _NF = "\n"

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

    @JvmStatic
    fun build(deviceEvent: DeviceEvent): String {
        val builder = StringBuilder()
        builder.append(_START)
        builder.append(_NF).append("Category:").append(deviceEvent.category).append(_C)
        var details: String? = null
        if (deviceEvent.details != null && deviceEvent.details.isNotEmpty()) {
            details = deviceEvent.details[0].toString()
        }
        builder.append(_NF).append("Detail:").append(details).append(_C)
        builder.append(_NF).append("EventCode:").append(deviceEvent.eventCode).append(_C)
        builder.append(_NF).append("InstanceId:").append(deviceEvent.instanceId).append(_C)
        builder.append(_NF).append("Serverity:").append(deviceEvent.severity).append(_C)
        builder.append(_NF).append("StateChangeType:").append(deviceEvent.stateChangeType).append(_C)
        if (deviceEvent.timestamp != null) {
            builder.append(_NF).append("Timestamp offset:").append(deviceEvent.timestamp.offset).append(_C)
            builder.append(_NF).append("Timestamp time:").append(deviceEvent.timestamp.time).append(_C)
        }
        builder.append(_NF).append("getTitle:").append(deviceEvent.title)
                .append(_NF).append(_END)
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