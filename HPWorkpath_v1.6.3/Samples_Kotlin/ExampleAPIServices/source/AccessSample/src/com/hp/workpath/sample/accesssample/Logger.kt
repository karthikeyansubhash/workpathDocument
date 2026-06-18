// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.Principal

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _C = ","
    private const val _EQ = "="
    private val _NF = if (BuildConfig.DEBUG) "\n" else ""
    fun build(principal: Principal?): String? {
        if (principal != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("domain").append(_EQ).append(principal.domain).append(_C)
            logBuilder.append(_NF).append("fullyQualifiedName").append(_EQ).append(principal.fullyQualifiedName).append(_C)
            logBuilder.append(_NF).append("principalID").append(_EQ).append(principal.principalId).append(_C)
            logBuilder.append(_NF).append("provider").append(_EQ).append(principal.provider).append(_C)
            logBuilder.append(_NF).append("providerUUID").append(_EQ).append(principal.providerUUID).append(_C)
            logBuilder.append(_NF).append("email").append(_EQ).append(principal.userEmail).append(_C)
            logBuilder.append(_NF).append("isAdmin").append(_EQ).append(principal.isAdmin).append(_C)
            logBuilder.append(_NF).append("isAuthenticated").append(_EQ).append(principal.isAuthenticated).append(_C)
            logBuilder.append(_NF).append("isAuthNAgentTrusted").append(_EQ).append(principal.isAuthNAgentTrusted).append(_C)
            logBuilder.append(_NF).append("isDeviceUser").append(_EQ).append(principal.isDeviceUser).append(_C)
            logBuilder.append(_NF).append("isGuestUser").append(_EQ).append(principal.isGuestUser).append(_C)
            logBuilder.append(_NF).append("isHPCloudUser").append(_EQ).append(principal.isHPCloudUser).append(_C)
            logBuilder.append(_NF).append("isServiceUser").append(_EQ).append(principal.isServiceUser).append(_C)
            logBuilder.append(_NF).append("isSmartCardUser").append(_EQ).append(principal.isSmartCardUser).append(_C)
            logBuilder.append(_NF).append("SimpleAuthorities").append(_EQ).append(principal.simpleAuthorities)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
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

    private fun showResult(activity: Activity?, msg: String?, result: Result?) {
        var message = msg
        if (result != null) {
            message = msg + Logger._NF + build(result)
            if (result.code == Result.RESULT_FAIL) {
                Log.e(MainActivity.TAG, message)
            } else {
                Log.d(MainActivity.TAG, message)
            }
        } else {
            if (message != null) {
                Log.d(MainActivity.TAG, message)
            }
        }
        if (activity != null && !activity.isFinishing) {
            showToastMessage(activity, message)
        }
    }

    private fun showToastMessage(activity: Activity, msg: String?) {
        activity.runOnUiThread { Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show() }
    }
}