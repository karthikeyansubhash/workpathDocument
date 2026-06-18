// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result
import com.hp.workpath.api.copier.CopyAttributes
import com.hp.workpath.api.copier.CopyAttributesCaps
import com.hp.workpath.api.copier.CopyAttributesReader
import com.hp.workpath.api.copier.StoredJobInfo
import com.hp.workpath.api.job.CopyJobData
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.scanner.StatusInfo

object Logger {
    private const val _START = "["
    private const val _END = "]"
    private const val _START_SUB = "{"
    private const val _END_SUB = "}"
    private const val _C = ","
    private const val _EQ = "="
    private val _NF = if (BuildConfig.DEBUG) "\n" else ""
    fun build(caps: CopyAttributesCaps?): String? {
        if (caps != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("backgroundCleanup").append(_EQ).append(caps.backgroundCleanupList).append(_C)
            logBuilder.append(_NF).append("collateModeList").append(_EQ).append(caps.collateModeList).append(_C)
            logBuilder.append(_NF).append("colorModeList").append(_EQ).append(caps.colorModeList).append(_C)
            logBuilder.append(_NF).append("contrastAdjustmentList").append(_EQ).append(caps.contrastAdjustmentList).append(_C)
            logBuilder.append(_NF).append("copiesRange").append(_EQ).append(caps.copiesRange.toString()).append(_C)
            logBuilder.append(_NF).append("copyPreviewList").append(_EQ).append(caps.copyPreviewList).append(_C)
            logBuilder.append(_NF).append("darknessAdjustmentList").append(_EQ).append(caps.darknessAdjustmentList).append(_C)
            logBuilder.append(_NF).append("jobAssemblyModeList").append(_EQ).append(caps.jobAssemblyModeList).append(_C)
            logBuilder.append(_NF).append("jobExecutionModeList").append(_EQ).append(caps.jobExecutionModeList).append(_C)
            logBuilder.append(_NF).append("numberUpDirectionByNumberUpCount").append(_EQ).append(caps.numberUpDirectionByNumberUpCount).append(_C)
            logBuilder.append(_NF).append("numberUpModeList").append(_EQ).append(caps.numberUpModeList).append(_C)
            logBuilder.append(_NF).append("orientationList").append(_EQ).append(caps.orientationList).append(_C)
            logBuilder.append(_NF).append("paperSourceList").append(_EQ).append(caps.paperSourceList).append(_C)
            logBuilder.append(_NF).append("paperTypeList").append(_EQ).append(caps.paperTypeList).append(_C)
            logBuilder.append(_NF).append("passwordTypeList").append(_EQ).append(caps.passwordTypeList).append(_C)
            logBuilder.append(_NF).append("printCustomLengthRange").append(_EQ).append(caps.printCustomLengthRange.toString()).append(_C)
            logBuilder.append(_NF).append("printCustomWidthRange").append(_EQ).append(caps.printCustomWidthRange.toString()).append(_C)
            logBuilder.append(_NF).append("printDuplexList").append(_EQ).append(caps.printDuplexList).append(_C)
            logBuilder.append(_NF).append("printSizeList").append(_EQ).append(caps.printSizeList).append(_C)
            logBuilder.append(_NF).append("scaleModeList").append(_EQ).append(caps.scaleModeList).append(_C)
            logBuilder.append(_NF).append("scalePercentRangeByScanSource").append(_EQ).append(caps.scalePercentRangeByScanSource.toString()).append(_C)
            logBuilder.append(_NF).append("scanCustomLengthRange").append(_EQ).append(caps.scanCustomLengthRange.toString()).append(_C)
            logBuilder.append(_NF).append("scanCustomWidthRange").append(_EQ).append(caps.scanCustomWidthRange.toString()).append(_C)
            logBuilder.append(_NF).append("scanDuplexList").append(_EQ).append(caps.scanDuplexList).append(_C)
            logBuilder.append(_NF).append("scanSizeList").append(_EQ).append(caps.scanSizeList).append(_C)
            logBuilder.append(_NF).append("scanSourceList").append(_EQ).append(caps.scanSourceList).append(_C)
            logBuilder.append(_NF).append("sharpnessAdjustmentList").append(_EQ).append(caps.sharpnessAdjustmentList).append(_C)
            logBuilder.append(_NF).append("textGraphicsOptimizationList").append(_EQ).append(caps.textGraphicsOptimizationList).append(_C)
            logBuilder.append(_NF).append("outputBin").append(_EQ).append(caps.outputBinList).append(_C)
            logBuilder.append(_NF).append("progressDialogMode").append(_EQ).append(caps.progressDialogModeList).append(_C)
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
            logBuilder.append(_NF).append("imageShiftReduceToFit").append(_EQ).append(caps.imageShiftReduceToFitList).append(_C)
            logBuilder.append(_NF).append("imageShiftUnits").append(_EQ).append(caps.imageShiftUnitsList).append(_C)
            logBuilder.append(_NF).append("imageShiftXFront").append(_EQ).append(caps.imageShiftXFrontRange).append(_C)
            logBuilder.append(_NF).append("imageShiftYFront").append(_EQ).append(caps.imageShiftYFrontRange).append(_C)
            logBuilder.append(_NF).append("imageShiftXBack").append(_EQ).append(caps.imageShiftXBackRange).append(_C)
            logBuilder.append(_NF).append("imageShiftYBack").append(_EQ).append(caps.imageShiftYBackRange).append(_C)
            logBuilder.append(_NF).append("bookletBordersEachPage").append(_EQ).append(caps.bookletBordersEachPageList).append(_C)
            logBuilder.append(_NF).append("bookletFinishingOption").append(_EQ).append(caps.bookletFinishingOptionList).append(_C)
            logBuilder.append(_NF).append("bookletFormat").append(_EQ).append(caps.bookletFormatList).append(_C)
            logBuilder.append(_NF).append("watermarkRotate45List").append(_EQ).append(caps.watermarkRotate45List).append(_C)
            logBuilder.append(_NF).append("watermarkDarknessList").append(_EQ).append(caps.watermarkDarknessRange).append(_C)
            logBuilder.append(_NF).append("WatermarkTransparencyRange").append(_EQ).append(caps.watermarkTransparencyRange).append(_C)
            logBuilder.append(_NF).append("WatermarkOnlyFirstPage").append(_EQ).append(caps.watermarkOnlyFirstPageList)
            logBuilder.append(_NF).append("WatermarkBackgroundPattern").append(_EQ).append(caps.watermarkBackgroundPatternList).append(_C)
            logBuilder.append(_NF).append("WatermarkMessageType").append(_EQ).append(caps.watermarkMessageTypeList).append(_C)
            logBuilder.append(_NF).append("WatermarkBackgroundColor").append(_EQ).append(caps.watermarkBackgroundColorList).append(_C)
            logBuilder.append(_NF).append("WatermarkFont").append(_EQ).append(caps.watermarkFontList).append(_C)
            logBuilder.append(_NF).append("WatermarkTextColor").append(_EQ).append(caps.watermarkTextColorList).append(_C)
            logBuilder.append(_NF).append("WatermarkTextSize").append(_EQ).append(caps.watermarkTextSizeList).append(_C)
            logBuilder.append(_NF).append("WatermarkType").append(_EQ).append(caps.watermarkTypeList).append(_C)
            logBuilder.append(_NF).append("stapleOption").append(_EQ).append(caps.stapleOptionList).append(_C)
            logBuilder.append(_NF).append("punchMode").append(_EQ).append(caps.punchModeList).append(_C)
            logBuilder.append(_NF).append("foldMode").append(_EQ).append(caps.foldModeList)

            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(attributes: CopyAttributes?): String? {
        if (attributes != null) {
            val reader = CopyAttributesReader(attributes)
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("backgroundCleanup").append(_EQ).append(reader.backgroundCleanup).append(_C)
            logBuilder.append(_NF).append("collateMode").append(_EQ).append(reader.collateMode).append(_C)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(reader.colorMode).append(_C)
            logBuilder.append(_NF).append("contrastAdjustment").append(_EQ).append(reader.contrastAdjustment).append(_C)
            logBuilder.append(_NF).append("copies").append(_EQ).append(reader.copies).append(_C)
            logBuilder.append(_NF).append("copyPreview").append(_EQ).append(reader.copyPreview).append(_C)
            logBuilder.append(_NF).append("darknessAdjustment").append(_EQ).append(reader.darknessAdjustment).append(_C)
            logBuilder.append(_NF).append("jobAssemblyMode").append(_EQ).append(reader.jobAssemblyMode).append(_C)
            logBuilder.append(_NF).append("jobExecutionMode").append(_EQ).append(reader.jobExecutionMode).append(_C)
            logBuilder.append(_NF).append("numberUpDirection").append(_EQ).append(reader.numberUpDirection).append(_C)
            logBuilder.append(_NF).append("numberUpMode").append(_EQ).append(reader.numberUpMode).append(_C)
            logBuilder.append(_NF).append("orientation").append(_EQ).append(reader.orientation).append(_C)
            logBuilder.append(_NF).append("paperSource").append(_EQ).append(reader.paperSource).append(_C)
            logBuilder.append(_NF).append("paperType").append(_EQ).append(reader.paperType).append(_C)
            logBuilder.append(_NF).append("printCustomLength").append(_EQ).append(reader.printCustomLength).append(_C)
            logBuilder.append(_NF).append("printCustomWidth").append(_EQ).append(reader.printCustomWidth).append(_C)
            logBuilder.append(_NF).append("printDuplex").append(_EQ).append(reader.printDuplex).append(_C)
            logBuilder.append(_NF).append("printSize").append(_EQ).append(reader.printSize).append(_C)
            logBuilder.append(_NF).append("scaleMode").append(_EQ).append(reader.scaleMode).append(_C)
            logBuilder.append(_NF).append("scalePercent").append(_EQ).append(reader.scalePercent).append(_C)
            logBuilder.append(_NF).append("scanCustomLength").append(_EQ).append(reader.scanCustomLength).append(_C)
            logBuilder.append(_NF).append("scanCustomWidth").append(_EQ).append(reader.scanCustomWidth).append(_C)
            logBuilder.append(_NF).append("scanDuplex").append(_EQ).append(reader.scanDuplex).append(_C)
            logBuilder.append(_NF).append("scanSize").append(_EQ).append(reader.scanSize).append(_C)
            logBuilder.append(_NF).append("scanSource").append(_EQ).append(reader.scanSource).append(_C)
            logBuilder.append(_NF).append("sharpnessAdjustment").append(_EQ).append(reader.sharpnessAdjustment).append(_C)
            logBuilder.append(_NF).append("storedJobRetentionModeOnPowerCycle").append(_EQ).append(reader.storedJobRetentionModeOnPowerCycle).append(_C)
            logBuilder.append(_NF).append("storedJobRetentionModeOnRelease").append(_EQ).append(reader.storedJobRetentionModeOnRelease).append(_C)
            logBuilder.append(_NF).append("storeJobFolderName").append(_EQ).append(reader.storeJobFolderName).append(_C)
            logBuilder.append(_NF).append("storeJobName").append(_EQ).append(reader.storeJobName).append(_C)
            logBuilder.append(_NF).append("textGraphicsOptimization").append(_EQ).append(reader.textGraphicsOptimization)
            logBuilder.append(_NF).append("outputBin").append(_EQ).append(reader.outputBin).append(_C)
            logBuilder.append(_NF).append("progressDialogMode").append(_EQ).append(reader.progressDialogMode).append(_C)
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
            logBuilder.append(_NF).append("imageShiftReduceToFit").append(_EQ).append(reader.imageShiftReduceToFit).append(_C)
            logBuilder.append(_NF).append("imageShiftUnits").append(_EQ).append(reader.imageShiftUnits).append(_C)
            logBuilder.append(_NF).append("imageShiftXFront").append(_EQ).append(reader.imageShiftXFront).append(_C)
            logBuilder.append(_NF).append("imageShiftYFront").append(_EQ).append(reader.imageShiftYFront).append(_C)
            logBuilder.append(_NF).append("imageShiftXBack").append(_EQ).append(reader.imageShiftXBack).append(_C)
            logBuilder.append(_NF).append("imageShiftYBack").append(_EQ).append(reader.imageShiftYBack).append(_C)
            logBuilder.append(_NF).append("bookletBordersEachPage").append(_EQ).append(reader.bookletBordersEachPage).append(_C)
            logBuilder.append(_NF).append("bookletFinishingOption").append(_EQ).append(reader.bookletFinishingOption).append(_C)
            logBuilder.append(_NF).append("bookletFormat").append(_EQ).append(reader.bookletFormat).append(_C)
            logBuilder.append(_NF).append("watermarkRotate45List").append(_EQ).append(reader.watermarkRotation45).append(_C)
            logBuilder.append(_NF).append("watermarkDarknessList").append(_EQ).append(reader.watermarkDarkness).append(_C)
            logBuilder.append(_NF).append("WatermarkTransparencyRange").append(_EQ).append(reader.watermarkTransparency.toString()).append(_C)
            logBuilder.append(_NF).append("WatermarkOnlyFirstPage").append(_EQ).append(reader.watermarkOnlyFirstPage).append(_C)
            logBuilder.append(_NF).append("WatermarkBackgroundPattern").append(_EQ).append(reader.watermarkBackgroundPattern).append(_C)
            logBuilder.append(_NF).append("WatermarkMessageType").append(_EQ).append(reader.watermarkMessageType).append(_C)
            logBuilder.append(_NF).append("WatermarkBackgroundColor").append(_EQ).append(reader.watermarkBackgroundColor).append(_C)
            logBuilder.append(_NF).append("WatermarkFont").append(_EQ).append(reader.watermarkFont).append(_C)
            logBuilder.append(_NF).append("WatermarkText").append(_EQ).append(reader.watermarkText).append(_C)
            logBuilder.append(_NF).append("WatermarkTextColor").append(_EQ).append(reader.watermarkTextColor).append(_C)
            logBuilder.append(_NF).append("WatermarkTextSize").append(_EQ).append(reader.watermarkTextSize).append(_C)
            logBuilder.append(_NF).append("WatermarkType").append(_EQ).append(reader.watermarkType).append(_C)
            logBuilder.append(_NF).append("stapleOption").append(_EQ).append(reader.stapleOption).append(_C)
            logBuilder.append(_NF).append("punchMode").append(_EQ).append(reader.punchMode).append(_C)
            logBuilder.append(_NF).append("foldMode").append(_EQ).append(reader.foldMode)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(statusInfo: StatusInfo?): String? {
        if (statusInfo != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("isBusy").append(_EQ).append(statusInfo.isBusy).append(_C)
            logBuilder.append(_NF).append("isOnline").append(_EQ).append(statusInfo.isOnline).append(_C)
            logBuilder.append(_NF).append("isPaperInFlatbed").append(_EQ).append(statusInfo.isPaperInFlatbed).append(_C)
            logBuilder.append(_NF).append("isPaperInAdf").append(_EQ).append(statusInfo.isPaperInAdf).append(_C)
            logBuilder.append(_NF).append("isAdfOutputBinFull").append(_EQ).append(statusInfo.isAdfOutputBinFull)
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(statusInfo: com.hp.workpath.api.printer.StatusInfo?): String? {
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
            if (jobInfo.jobType == JobInfo.JobType.COPY) {
                val copyJobData = jobInfo.getJobData<CopyJobData>()
                if (copyJobData != null) {
                    logBuilder.append(_NF).append("jobData").append(_EQ).append(_START_SUB)
                    val copyJobState = copyJobData.jobState
                    if (copyJobState != null) {
                        logBuilder.append("jobState").append(_EQ).append(_START_SUB)
                        logBuilder.append("state").append(_EQ).append(copyJobState.state).append(_C)
                        logBuilder.append("scanningState").append(_EQ).append(copyJobState.scanningState).append(_C)
                        logBuilder.append("processingState").append(_EQ).append(copyJobState.processingState).append(_C)
                        logBuilder.append("printingState").append(_EQ).append(copyJobState.printingState).append(_C)
                        logBuilder.append("cancelingState").append(_EQ).append(copyJobState.cancelingState).append(_END_SUB)
                    }
                    logBuilder.append(_C)
                    logBuilder.append("imagesScanned").append(_EQ).append(copyJobData.imagesScanned).append(_C)
                    logBuilder.append("sheetsPrinted").append(_EQ).append(copyJobData.sheetsPrinted).append(_C)
                    logBuilder.append("duplex").append(_EQ).append(copyJobData.duplex).append(_C)
                    logBuilder.append("scanSize").append(_EQ).append(copyJobData.scanSize).append(_C)
                    logBuilder.append("jobExecutionMode").append(_EQ).append(copyJobData.jobExecutionMode).append(_END_SUB)
                }
            }
            logBuilder.append(_NF).append(_END)
            return logBuilder.toString()
        }
        return null
    }

    fun build(storedJobInfo: StoredJobInfo?): String? {
        if (storedJobInfo != null) {
            val logBuilder = StringBuilder()
            logBuilder.append(_START)
            logBuilder.append(_NF).append("colorMode").append(_EQ).append(storedJobInfo.colorMode).append(_C)
            logBuilder.append(_NF).append("copies").append(_EQ).append(storedJobInfo.copies).append(_C)
            logBuilder.append(_NF).append("originalMediaSize").append(_EQ).append(storedJobInfo.originalMediaSize).append(_C)
            logBuilder.append(_NF).append("outputSides").append(_EQ).append(storedJobInfo.outputSides).append(_C)
            logBuilder.append(_NF).append("storedJobFolderName").append(_EQ).append(storedJobInfo.storedJobFolderName).append(_C)
            logBuilder.append(_NF).append("storedJobId").append(_EQ).append(storedJobInfo.storedJobId).append(_C)
            logBuilder.append(_NF).append("storedJobName").append(_EQ).append(storedJobInfo.storedJobName).append(_C)
            logBuilder.append(_NF).append("storedJobPasswordType").append(_EQ).append(storedJobInfo.storedJobPasswordType).append(_C)
            logBuilder.append(_NF).append("storedJobUserName").append(_EQ).append(storedJobInfo.storedJobUserName).append(_C)
            logBuilder.append(_NF).append("storeJobTimestamp").append(_EQ).append(storedJobInfo.storeJobTimestamp).append(_C)
            logBuilder.append(_NF).append("totalPages").append(_EQ).append(storedJobInfo.totalPages)
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