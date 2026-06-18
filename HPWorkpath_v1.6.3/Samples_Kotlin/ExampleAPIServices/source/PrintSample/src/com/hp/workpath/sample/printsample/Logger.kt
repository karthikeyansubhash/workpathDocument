// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.job.PrintJobData
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesReader
import com.hp.workpath.api.printer.StatusInfo
import com.hp.workpath.api.printer.TrayInfo

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _START_SUB = "{"
    private const val _END_SUB = "}"
    private const val _C = ","
    private const val _EQ = "="
    private val _NF = if (BuildConfig.DEBUG) "\n" else ""

    fun build(caps: PrintAttributesCaps?): String? {
        if (caps != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("autoFit").append(_EQ).append(caps.autoFitList).append(_C)
            logBuilder.append(_NF).append("collateMode").append(_EQ).append(caps.collateModeList).append(_C)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(caps.colorModeList).append(_C)
            logBuilder.append(_NF).append("documentFormat").append(_EQ).append(caps.documentFormatList).append(_C)
            logBuilder.append(_NF).append("duplex").append(_EQ).append(caps.duplexList).append(_C)
            logBuilder.append(_NF).append("maxCopies").append(_EQ).append(caps.maxCopies).append(_C)
            logBuilder.append(_NF).append("paperSize").append(_EQ).append(caps.paperSizeList).append(_C)
            logBuilder.append(_NF).append("paperSource").append(_EQ).append(caps.paperSourceList).append(_C)
            logBuilder.append(_NF).append("paperType").append(_EQ).append(caps.paperTypeList).append(_C)
            logBuilder.append(_NF).append("stapleMode").append(_EQ).append(caps.stapleModeList).append(_C)
            logBuilder.append(_NF).append("orientation").append(_EQ).append(caps.orientationList).append(_C)
            logBuilder.append(_NF).append("printQuality").append(_EQ).append(caps.printQualityList).append(_C)
            logBuilder.append(_NF).append("outputBin").append(_EQ).append(caps.outputBinList).append(_C)
            logBuilder.append(_NF).append("finishings").append(_EQ).append(caps.finishingsList)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(attributes: PrintAttributes?): String? {
        if (attributes != null) {
            val reader = PrintAttributesReader(attributes)
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("autoFit").append(_EQ).append(reader.autoFit).append(_C)
            logBuilder.append(_NF).append("collateMode").append(_EQ).append(reader.collateMode).append(_C)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(reader.colorMode).append(_C)
            logBuilder.append(_NF).append("copies").append(_EQ).append(reader.copies).append(_C)
            logBuilder.append(_NF).append("documentFormat").append(_EQ).append(reader.documentFormat).append(_C)
            logBuilder.append(_NF).append("duplex").append(_EQ).append(reader.plex).append(_C)
            logBuilder.append(_NF).append("jobName").append(_EQ).append(reader.jobName).append(_C)
            logBuilder.append(_NF).append("paperSize").append(_EQ).append(reader.paperSize).append(_C)
            logBuilder.append(_NF).append("paperSource").append(_EQ).append(reader.paperSource).append(_C)
            logBuilder.append(_NF).append("paperType").append(_EQ).append(reader.paperType).append(_C)
            logBuilder.append(_NF).append("stapleMode").append(_EQ).append(reader.stapleMode).append(_C)
            logBuilder.append(_NF).append("orientation").append(_EQ).append(reader.orientation).append(_C)
            logBuilder.append(_NF).append("printQuality").append(_EQ).append(reader.printQuality).append(_C)
            logBuilder.append(_NF).append("outputBin").append(_EQ).append(reader.outputBin).append(_C)
            logBuilder.append(_NF).append("finishings").append(_EQ).append(reader.finishingsList)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(statusInfo: StatusInfo?): String? {
        if (statusInfo != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("status").append(_EQ).append(statusInfo.status).append(_C)
            logBuilder.append(_NF).append("statusReasons").append(_EQ).append(statusInfo.statusReasons)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(trayInfo: TrayInfo?): String? {
        if (trayInfo != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            if (trayInfo.status == TrayInfo.Status.AVAILABLE) {
                logBuilder.append("paperSource").append(_EQ).append(trayInfo.paperSource).append(_C)
                logBuilder.append(_NF).append("status").append(_EQ).append(trayInfo.status).append(_C)
                logBuilder.append(_NF).append("level").append(_EQ).append(trayInfo.level).append(_C)
                logBuilder.append(_NF).append("cap").append(_EQ).append(trayInfo.capacity).append(_C)
                logBuilder.append(_NF).append("paperSize").append(_EQ).append(trayInfo.paperSize).append(_C)
                logBuilder.append(_NF).append("paperType").append(_EQ).append(trayInfo.paperType).append(_END)
            } else {
                logBuilder.append("paperSource").append(_EQ).append(trayInfo.paperSource).append(_C)
                        .append("status").append(_EQ).append(trayInfo.status).append(_END)
            }
            return logBuilder.toString()
        }
        return null
    }

    fun build(jobInfo: JobInfo?): String? {
        if (jobInfo != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("jobId").append(_EQ).append(jobInfo.jobId).append(_C)
            logBuilder.append(_NF).append("jobName").append(_EQ).append(jobInfo.jobName).append(_C)
            logBuilder.append(_NF).append("jobType").append(_EQ).append(jobInfo.jobType).append(_C)
            logBuilder.append(_NF).append("owner").append(_EQ).append(jobInfo.owner).append(_C)
            logBuilder.append(_NF).append("startTime").append(_EQ).append(jobInfo.startTime).append(_C)
            logBuilder.append(_NF).append("completeTime").append(_EQ).append(jobInfo.completeTime).append(_C)
            if (jobInfo.jobType == JobInfo.JobType.PRINT) {
                val printJobData = jobInfo.getJobData<PrintJobData>()
                if (printJobData != null) {
                    logBuilder.append(_NF).append("jobData").append(_EQ).append(_START_SUB)
                    val printJobState = printJobData.jobState
                    if (printJobState != null) {
                        logBuilder.append("jobState").append(_EQ).append(_START_SUB)
                        logBuilder.append("state").append(_EQ).append(printJobState.state).append(_END_SUB)
                    }
                    logBuilder.append(_C)
                    logBuilder.append("sheetsPrinted").append(_EQ).append(printJobData.sheetsPrinted).append(_C)
                    logBuilder.append("impressionsPrinted").append(_EQ).append(printJobData.impressionsPrinted).append(_C)
                    logBuilder.append("copies").append(_EQ).append(printJobData.copies).append(_C)
                    logBuilder.append("duplex").append(_EQ).append(printJobData.duplex).append(_C)
                    logBuilder.append("source").append(_EQ).append(printJobData.source).append(_END_SUB)
                }
            }
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