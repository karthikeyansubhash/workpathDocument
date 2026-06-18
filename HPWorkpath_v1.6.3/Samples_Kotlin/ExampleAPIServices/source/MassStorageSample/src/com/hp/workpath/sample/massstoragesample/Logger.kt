// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.MassStorageInfo

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _C = ","
    private const val _EQ = "="
    private val _NF = if (BuildConfig.DEBUG) "\n" else ""
    fun build(info: MassStorageInfo?): String? {
        if (info != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("externalFileDirectory").append(_EQ).append(info.externalFileDirectory).append(_C)
            logBuilder.append(_NF).append("freeSpace").append(_EQ).append(info.freeSpace).append(_C)
            logBuilder.append(_NF).append("name").append(_EQ).append(info.name).append(_C)
            logBuilder.append(_NF).append("protocol").append(_EQ).append(info.protocol).append(_C)
            logBuilder.append(_NF).append("totalSpace").append(_EQ).append(info.totalSpace).append(_C)
            logBuilder.append(_NF).append("type").append(_EQ).append(info.type).append(_C)
            logBuilder.append(_NF).append("volumeName").append(_EQ).append(info.volumeName).append(_C)
            logBuilder.append(_NF).append("isMounted").append(_EQ).append(info.isMounted)
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

    @JvmStatic
    fun build(file: CustomerDataFile?): String? {
        if (file != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
                    .append(_NF).append("path").append(_EQ).append(file.path).append(_C)
                    .append(_NF).append("packageName").append(_EQ).append(file.packageName).append(_C)
                    .append(_NF).append("name").append(_EQ).append(file.name).append(_C)
                    .append(_NF).append("exists").append(_EQ).append(file.exists()).append(_C)
                    .append(_NF).append("isFile").append(_EQ).append(file.isFile).append(_C)
                    .append(_NF).append("isDirectory").append(_EQ).append(file.isDirectory).append(_C)
                    .append(_NF).append("lastModified").append(_EQ).append(file.lastModified()).append(_C)
                    .append(_NF).append("length").append(_EQ).append(file.length()).append(_C)
                    .append(_NF).append("storageInfo").append(_EQ).append(build(file.storageInfo))
                    .append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
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
        activity.runOnUiThread { Toast.makeText(activity, msg, Toast.LENGTH_LONG).show() }
    }
}