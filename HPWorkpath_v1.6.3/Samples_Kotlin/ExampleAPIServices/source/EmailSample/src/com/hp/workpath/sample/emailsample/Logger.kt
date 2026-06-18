// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.helper.email.EmailAddressInfo
import com.hp.workpath.api.helper.email.EmailAttributes

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _C = ","
    private const val _EQ = "="
    private val _NF = if (BuildConfig.DEBUG) "\n" else ""
    fun build(attributes: EmailAttributes?): String? {
        if (attributes != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("attachmentList").append(_EQ).append(attributes.attachments).append(_C)
            logBuilder.append(_NF).append("ccList").append(_EQ).append(build(attributes.cc)).append(_C)
            logBuilder.append(_NF).append("bccList").append(_EQ).append(build(attributes.bcc)).append(_C)
            logBuilder.append(_NF).append("from").append(_EQ).append(if (attributes.from != null) attributes.from.name else "").append(_C)
            logBuilder.append(_NF).append("message").append(_EQ).append(attributes.message).append(_C)
            logBuilder.append(_NF).append("subject").append(_EQ).append(attributes.subject).append(_C)
            logBuilder.append(_NF).append("toList").append(_EQ).append(build(attributes.to))
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    private fun build(infos: List<EmailAddressInfo>?): String? {
        if (infos != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            var delim = ""
            for (info in infos) {
                logBuilder.append(delim).append(info.name)
                delim = _C
            }
            logBuilder.append(_END)
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