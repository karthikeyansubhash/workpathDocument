// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.job.ScanJobData
import com.hp.workpath.api.scanner.*

object Logger {
    private val _START = "["
    private val _END = "]"
    private val _START_SUB = "{"
    private val _END_SUB = "}"
    private val _C = ","
    private val _EQ = "="
    private val _NF = when (BuildConfig.DEBUG) {
        true -> "\n"
        false -> ""
    }

    fun build(caps: ScanAttributesCaps?): String? {
        if (caps != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("backgroundCleanup").append(_EQ).append(caps.backgroundCleanupList).append(_C)
            logBuilder.append(_NF).append("blankImageRemovalMode").append(_EQ).append(caps.blankImageRemovalModeList).append(_C)
            logBuilder.append(_NF).append("colorDropoutMode").append(_EQ).append(caps.colorDropoutModeList).append(_C)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(caps.colorModeList.toString()).append(_C)
            logBuilder.append(_NF).append("contrastAdjustment").append(_EQ).append(caps.contrastAdjustmentList).append(_C)
            logBuilder.append(_NF).append("cropMode").append(_EQ).append(caps.cropModeList).append(_C)
            logBuilder.append(_NF).append("customLength").append(_EQ).append(caps.customLengthRange).append(_C)
            logBuilder.append(_NF).append("customWidth").append(_EQ).append(caps.customWidthRange).append(_C)
            logBuilder.append(_NF).append("darknessAdjustment").append(_EQ).append(caps.darknessAdjustmentList).append(_C)
            logBuilder.append(_NF).append("destination").append(_EQ).append(caps.destinationList).append(_C)
            logBuilder.append(_NF).append("docFormat(Me)").append(_EQ).append(caps.getDocumentFormatList(ScanAttributes.Destination.ME).toString()).append(_C)
            logBuilder.append(_NF).append("duplex").append(_EQ).append(caps.duplexList.toString()).append(_C)
            logBuilder.append(_NF).append("jobAssemblyMode").append(_EQ).append(caps.jobAssemblyModeList).append(_C)
            logBuilder.append(_NF).append("mediaWeightAdjustment").append(_EQ).append(caps.mediaWeightAdjustmentList).append(_C)
            logBuilder.append(_NF).append("mediaSource").append(_EQ).append(caps.mediaSourceList).append(_C)
            logBuilder.append(_NF).append("misfeedDetectionMode").append(_EQ).append(caps.misfeedDetectionModeList).append(_C)
            logBuilder.append(_NF).append("orientation").append(_EQ).append(caps.orientationList).append(_C)
            logBuilder.append(_NF).append("outputQuality").append(_EQ).append(caps.outputQualityList).append(_C)
            logBuilder.append(_NF).append("progressDialogMode").append(_EQ).append(caps.progressDialogModeList).append(_C)
            logBuilder.append(_NF).append("resolution").append(_EQ).append(caps.resolutionList).append(_C)
            logBuilder.append(_NF).append("scanPreview").append(_EQ).append(caps.scanPreviewList).append(_C)
            logBuilder.append(_NF).append("scanSize").append(_EQ).append(caps.scanSizeList).append(_C)
            logBuilder.append(_NF).append("sharpnessAdjustment").append(_EQ).append(caps.sharpnessAdjustmentList).append(_C)
            logBuilder.append(_NF).append("textPhotoOptimization").append(_EQ).append(caps.textPhotoOptimizationList).append(_C)
            logBuilder.append(_NF).append("transmissionMode").append(_EQ).append(caps.transmissionModeList)
                    // ScanTicket3
            logBuilder.append(_NF).append("splitAttachmentByPage").append(_EQ).append(caps.splitAttachmentByPageList).append(_C)
            logBuilder.append(_NF).append("maxPagesPerAttachment").append(_EQ).append(caps.maxPagesPerAttachmentRange).append(_C)
            logBuilder.append(_NF).append("eraseMarginUnit").append(_EQ).append(caps.eraseMarginUnitList).append(_C)
            logBuilder.append(_NF).append("eraseBackLeft").append(_EQ).append(caps.eraseBackLeftRange).append(_C)
            logBuilder.append(_NF).append("eraseBackTop").append(_EQ).append(caps.eraseBackTopRange).append(_C)
            logBuilder.append(_NF).append("eraseBackRight").append(_EQ).append(caps.eraseBackRightRange).append(_C)
            logBuilder.append(_NF).append("eraseBackBottom").append(_EQ).append(caps.eraseBackBottomRange).append(_C)
            logBuilder.append(_NF).append("eraseFrontLeft").append(_EQ).append(caps.eraseFrontLeftRange).append(_C)
            logBuilder.append(_NF).append("eraseFrontTop").append(_EQ).append(caps.eraseFrontTopRange).append(_C)
            logBuilder.append(_NF).append("eraseFrontRight").append(_EQ).append(caps.eraseFrontRightRange).append(_C)
            logBuilder.append(_NF).append("eraseFrontBottom").append(_EQ).append(caps.eraseFrontBottomRange).append(_C)
            logBuilder.append(_NF).append("captureMode").append(_EQ).append(caps.captureModeList).append(_C)
            logBuilder.append(_NF).append("automaticToneMode").append(_EQ).append(caps.automaticToneModeList).append(_C)
            logBuilder.append(_NF).append("automaticStraightenMode").append(_EQ).append(caps.automaticStraightenModeList)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(entries: Map<ScanAttributes.ColorMode, List<ScanAttributes.DocumentFormat>>?): String? {
        if (entries != null) {
            val logBuilder = StringBuilder()
            var delim = ""
            entries.forEach { (key, value) ->
                logBuilder.append(delim).append(_START).append(key).append(_END).append(_EQ).append(value)
                delim = _NF
            }
            return logBuilder.toString()
        }
        return null
    }

    fun build(caps: FileOptionsAttributesCaps?, colorMode: ScanAttributes.ColorMode, docFormat: ScanAttributes.DocumentFormat): String? {
        if (caps != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(colorMode.name).append(_C)
            logBuilder.append(_NF).append("docFormat").append(_EQ).append(docFormat.name).append(_C)
            logBuilder.append(_NF).append("ocrLanguageList").append(_EQ).append(caps.ocrLanguageList.toString()).append(_C)
            logBuilder.append(_NF).append("pdfCompressionModeList").append(_EQ).append(caps.pdfCompressionModeList.toString()).append(_C)
            logBuilder.append(_NF).append("tiffCompressionModeList").append(_EQ).append(caps.tiffCompressionModeList.toString()).append(_C)
            logBuilder.append(_NF).append("xpsCompressionModeList").append(_EQ).append(caps.xpsCompressionModeList.toString()).append(_C)
            logBuilder.append(_NF).append("isPdfEncryptionPasswordSupported").append(_EQ).append(caps.isPdfEncryptionPasswordSupported)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(attributes: ScanAttributes?): String? {
        if (attributes != null) {
            val reader = ScanAttributesReader(attributes)
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("backgroundCleanup").append(_EQ).append(reader.backgroundCleanup).append(_C)
            logBuilder.append(_NF).append("blankImageRemovalMode").append(_EQ).append(reader.blankImageRemovalMode).append(_C)
            logBuilder.append(_NF).append("colorDropoutMode").append(_EQ).append(reader.colorDropoutMode).append(_C)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(reader.colorMode).append(_C)
            logBuilder.append(_NF).append("contrastAdjustment").append(_EQ).append(reader.contrastAdjustment).append(_C)
            logBuilder.append(_NF).append("cropMode").append(_EQ).append(reader.cropMode).append(_C)
            logBuilder.append(_NF).append("customLength").append(_EQ).append(reader.customLength).append(_C)
            logBuilder.append(_NF).append("customWidth").append(_EQ).append(reader.customWidth).append(_C)
            logBuilder.append(_NF).append("darknessAdjustment").append(_EQ).append(reader.darknessAdjustment).append(_C)
            logBuilder.append(_NF).append("destination").append(_EQ).append(reader.destination).append(_C)
            logBuilder.append(_NF).append("documentFormat").append(_EQ).append(reader.documentFormat).append(_C)
            logBuilder.append(_NF).append("duplex").append(_EQ).append(reader.plex).append(_C)
            logBuilder.append(_NF).append("jobAssemblyMode").append(_EQ).append(reader.jobAssemblyMode).append(_C)
            logBuilder.append(_NF).append("mediaSource").append(_EQ).append(reader.mediaSource).append(_C)
            logBuilder.append(_NF).append("mediaWeightAdjustment").append(_EQ).append(reader.mediaWeightAdjustment).append(_C)
            logBuilder.append(_NF).append("misfeedDetectionMode").append(_EQ).append(reader.misfeedDetectionMode).append(_C)
            logBuilder.append(_NF).append("orientation").append(_EQ).append(reader.orientation).append(_C)
            logBuilder.append(_NF).append("outputQuality").append(_EQ).append(reader.outputQuality).append(_C)
            logBuilder.append(_NF).append("progressDialogMode").append(_EQ).append(reader.progressDialogMode).append(_C)
            logBuilder.append(_NF).append("resolution").append(_EQ).append(reader.resolution).append(_C)
            logBuilder.append(_NF).append("scanPreview").append(_EQ).append(reader.scanPreview).append(_C)
            logBuilder.append(_NF).append("scanSize").append(_EQ).append(reader.scanSize).append(_C)
            logBuilder.append(_NF).append("sharpnessAdjustment").append(_EQ).append(reader.sharpnessAdjustment).append(_C)
            logBuilder.append(_NF).append("textPhotoOptimization").append(_EQ).append(reader.textPhotoOptimization).append(_C)
            logBuilder.append(_NF).append("transmissionMode").append(_EQ).append(reader.transmissionMode).append(_C)
            logBuilder.append(_NF).append("scanSize").append(_EQ).append(reader.scanSize)
            logBuilder.append(_NF).append("splitAttachmentByPage").append(_EQ).append(reader.splitAttachmentByPage).append(_C)
            logBuilder.append(_NF).append("maxPagesPerAttachment").append(_EQ).append(reader.maxPagesPerAttachment).append(_C)
            logBuilder.append(_NF).append("eraseMarginUnit").append(_EQ).append(reader.eraseMarginUnit).append(_C)
            logBuilder.append(_NF).append("eraseBackLeft").append(_EQ).append(reader.eraseBackLeftMargin).append(_C)
            logBuilder.append(_NF).append("eraseBackTop").append(_EQ).append(reader.eraseBackTopMargin).append(_C)
            logBuilder.append(_NF).append("eraseBackRight").append(_EQ).append(reader.eraseBackRightMargin).append(_C)
            logBuilder.append(_NF).append("eraseBackBottom").append(_EQ).append(reader.eraseBackBottomMargin).append(_C)
            logBuilder.append(_NF).append("eraseFrontLeft").append(_EQ).append(reader.eraseFrontLeftMargin).append(_C)
            logBuilder.append(_NF).append("eraseFrontTop").append(_EQ).append(reader.eraseFrontTopMargin).append(_C)
            logBuilder.append(_NF).append("eraseFrontRight").append(_EQ).append(reader.eraseFrontRightMargin).append(_C)
            logBuilder.append(_NF).append("eraseFrontBottom").append(_EQ).append(reader.eraseFrontBottomMargin).append(_C)
            logBuilder.append(_NF).append("captureMode").append(_EQ).append(reader.captureMode).append(_C)
            logBuilder.append(_NF).append("automaticToneMode").append(_EQ).append(reader.automaticToneMode).append(_C)
            logBuilder.append(_NF).append("automaticStraightenMode").append(_EQ).append(reader.automaticStraightenMode)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(statusInfo: StatusInfo?, isNewLine: Boolean): String? {
        if (statusInfo != null) {
            val newLine = when (isNewLine) {
                true -> _NF
                false -> ""
            }
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(newLine).append("isBusy").append(_EQ).append(statusInfo.isBusy).append(_C)
            logBuilder.append(newLine).append("isOnline").append(_EQ).append(statusInfo.isOnline).append(_C)
            logBuilder.append(newLine).append("isPaperInFlatbed").append(_EQ).append(statusInfo.isPaperInFlatbed).append(_C)
            logBuilder.append(newLine).append("isPaperInAdf").append(_EQ).append(statusInfo.isPaperInAdf).append(_C)
            logBuilder.append(newLine).append("isAdfOutputBinFull").append(_EQ).append(statusInfo.isAdfOutputBinFull)
            logBuilder.append(newLine).append(_END)
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
            when (jobInfo.jobType) {
                JobInfo.JobType.SCAN -> {
                    jobInfo.getJobData<ScanJobData>()?.let {
                        logBuilder.append(_NF).append("jobData").append(_EQ).append(_START_SUB)
                        it.jobState?.let {
                            logBuilder.append("jobState").append(_EQ).append(_START_SUB)
                            logBuilder.append("state").append(_EQ).append(it.state).append(_C)
                            logBuilder.append("scanningState").append(_EQ).append(it.scanningState).append(_C)
                            logBuilder.append("processingState").append(_EQ).append(it.processingState).append(_C)
                            logBuilder.append("transmittingState").append(_EQ).append(it.transmittingState).append(_C)
                            logBuilder.append("cancelingState").append(_EQ).append(it.cancelingState).append(_END_SUB)
                        }
                        logBuilder.append(_C)
                        logBuilder.append("imagesScanned").append(_EQ).append(it.imagesScanned).append(_C)
                        logBuilder.append("imagesProcessed").append(_EQ).append(it.imagesProcessed).append(_C)
                        logBuilder.append("imagesTransmitted").append(_EQ).append(it.imagesTransmitted).append(_C)
                        logBuilder.append("duplex").append(_EQ).append(it.duplex).append(_C)
                        logBuilder.append("fileNames").append(_EQ).append(it.fileNames.toString()).append(_C)
                        logBuilder.append("scanSize").append(_EQ).append(it.scanSize).append(_C)
                        logBuilder.append("destination").append(_EQ).append(it.destination).append(_END_SUB)
                    }

                }
                else -> Unit
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
