// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.ScanJobData;
import com.hp.workpath.api.job.ScanJobState;
import com.hp.workpath.api.scanner.FileOptionsAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributesReader;
import com.hp.workpath.api.scanner.StatusInfo;

import java.util.List;
import java.util.Map;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _START_SUB = "{";
    private static final String _END_SUB = "}";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(ScanAttributesCaps caps) {
        if (caps != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("backgroundCleanup").append(_EQ).append(caps.getBackgroundCleanupList()).append(_C)
                    .append(_NF).append("blankImageRemovalMode").append(_EQ).append(caps.getBlankImageRemovalModeList()).append(_C)
                    .append(_NF).append("colorDropoutMode").append(_EQ).append(caps.getColorDropoutModeList()).append(_C)
                    .append(_NF).append("colorMode").append(_EQ).append(caps.getColorModeList().toString()).append(_C)
                    .append(_NF).append("contrastAdjustment").append(_EQ).append(caps.getContrastAdjustmentList()).append(_C)
                    .append(_NF).append("cropMode").append(_EQ).append(caps.getCropModeList()).append(_C)
                    .append(_NF).append("customLength").append(_EQ).append(caps.getCustomLengthRange()).append(_C)
                    .append(_NF).append("customWidth").append(_EQ).append(caps.getCustomWidthRange()).append(_C)
                    .append(_NF).append("darknessAdjustment").append(_EQ).append(caps.getDarknessAdjustmentList()).append(_C)
                    .append(_NF).append("destination").append(_EQ).append(caps.getDestinationList()).append(_C)
                    .append(_NF).append("docFormat(Me)").append(_EQ).append(caps.getDocumentFormatList(ScanAttributes.Destination.ME).toString()).append(_C)
                    .append(_NF).append("duplex").append(_EQ).append(caps.getDuplexList().toString()).append(_C)
                    .append(_NF).append("jobAssemblyMode").append(_EQ).append(caps.getJobAssemblyModeList()).append(_C)
                    .append(_NF).append("mediaWeightAdjustment").append(_EQ).append(caps.getMediaWeightAdjustmentList()).append(_C)
                    .append(_NF).append("mediaSource").append(_EQ).append(caps.getMediaSourceList()).append(_C)
                    .append(_NF).append("misfeedDetectionMode").append(_EQ).append(caps.getMisfeedDetectionModeList()).append(_C)
                    .append(_NF).append("orientation").append(_EQ).append(caps.getOrientationList()).append(_C)
                    .append(_NF).append("outputQuality").append(_EQ).append(caps.getOutputQualityList()).append(_C)
                    .append(_NF).append("progressDialogMode").append(_EQ).append(caps.getProgressDialogModeList()).append(_C)
                    .append(_NF).append("resolution").append(_EQ).append(caps.getResolutionList()).append(_C)
                    .append(_NF).append("scanPreview").append(_EQ).append(caps.getScanPreviewList()).append(_C)
                    .append(_NF).append("scanSize").append(_EQ).append(caps.getScanSizeList()).append(_C)
                    .append(_NF).append("sharpnessAdjustment").append(_EQ).append(caps.getSharpnessAdjustmentList()).append(_C)
                    .append(_NF).append("textPhotoOptimization").append(_EQ).append(caps.getTextPhotoOptimizationList()).append(_C)
                    .append(_NF).append("transmissionMode").append(_EQ).append(caps.getTransmissionModeList()).append(_C)
                    // ScanTicket3
                    .append(_NF).append("splitAttachmentByPage").append(_EQ).append(caps.getSplitAttachmentByPageList()).append(_C)
                    .append(_NF).append("maxPagesPerAttachment").append(_EQ).append(caps.getMaxPagesPerAttachmentRange()).append(_C)
                    .append(_NF).append("eraseMarginUnit").append(_EQ).append(caps.getEraseMarginUnitList()).append(_C)
                    .append(_NF).append("eraseBackLeft").append(_EQ).append(caps.getEraseBackLeftRange()).append(_C)
                    .append(_NF).append("eraseBackTop").append(_EQ).append(caps.getEraseBackTopRange()).append(_C)
                    .append(_NF).append("eraseBackRight").append(_EQ).append(caps.getEraseBackRightRange()).append(_C)
                    .append(_NF).append("eraseBackBottom").append(_EQ).append(caps.getEraseBackBottomRange()).append(_C)
                    .append(_NF).append("eraseFrontLeft").append(_EQ).append(caps.getEraseFrontLeftRange()).append(_C)
                    .append(_NF).append("eraseFrontTop").append(_EQ).append(caps.getEraseFrontTopRange()).append(_C)
                    .append(_NF).append("eraseFrontRight").append(_EQ).append(caps.getEraseFrontRightRange()).append(_C)
                    .append(_NF).append("eraseFrontBottom").append(_EQ).append(caps.getEraseFrontBottomRange()).append(_C)
                    .append(_NF).append("captureMode").append(_EQ).append(caps.getCaptureModeList()).append(_C)
                    .append(_NF).append("automaticToneMode").append(_EQ).append(caps.getAutomaticToneModeList()).append(_C)
                    .append(_NF).append("automaticStraightenMode").append(_EQ).append(caps.getAutomaticStraightenModeList())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(Map<ScanAttributes.ColorMode, List<ScanAttributes.DocumentFormat>> entries) {
        if (entries != null) {
            StringBuilder logBuilder = new StringBuilder();
            String delim = "";
            for (Map.Entry<ScanAttributes.ColorMode, List<ScanAttributes.DocumentFormat>> entry : entries.entrySet()) {
                logBuilder.append(delim).append(_START).append(entry.getKey()).append(_END).append(_EQ).append(entry.getValue());
                delim = _NF;
            }
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(FileOptionsAttributesCaps caps, ScanAttributes.ColorMode colorMode, ScanAttributes.DocumentFormat docFormat) {
        if (caps != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("colorMode").append(_EQ).append(colorMode.name()).append(_C)
                    .append(_NF).append("docFormat").append(_EQ).append(docFormat.name()).append(_C)
                    .append(_NF).append("ocrLanguageList").append(_EQ).append(caps.getOcrLanguageList().toString()).append(_C)
                    .append(_NF).append("pdfCompressionModeList").append(_EQ).append(caps.getPdfCompressionModeList().toString()).append(_C)
                    .append(_NF).append("tiffCompressionModeList").append(_EQ).append(caps.getTiffCompressionModeList().toString()).append(_C)
                    .append(_NF).append("xpsCompressionModeList").append(_EQ).append(caps.getXpsCompressionModeList().toString()).append(_C)
                    .append(_NF).append("isPdfEncryptionPasswordSupported").append(_EQ).append(caps.isPdfEncryptionPasswordSupported())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(ScanAttributes attributes) {
        if (attributes != null) {
            ScanAttributesReader reader = new ScanAttributesReader(attributes);
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("backgroundCleanup").append(_EQ).append(reader.getBackgroundCleanup()).append(_C)
                    .append(_NF).append("blankImageRemovalMode").append(_EQ).append(reader.getBlankImageRemovalMode()).append(_C)
                    .append(_NF).append("colorDropoutMode").append(_EQ).append(reader.getColorDropoutMode()).append(_C)
                    .append(_NF).append("colorMode").append(_EQ).append(reader.getColorMode()).append(_C)
                    .append(_NF).append("contrastAdjustment").append(_EQ).append(reader.getContrastAdjustment()).append(_C)
                    .append(_NF).append("cropMode").append(_EQ).append(reader.getCropMode()).append(_C)
                    .append(_NF).append("customLength").append(_EQ).append(reader.getCustomLength()).append(_C)
                    .append(_NF).append("customWidth").append(_EQ).append(reader.getCustomWidth()).append(_C)
                    .append(_NF).append("darknessAdjustment").append(_EQ).append(reader.getDarknessAdjustment()).append(_C)
                    .append(_NF).append("destination").append(_EQ).append(reader.getDestination()).append(_C)
                    .append(_NF).append("documentFormat").append(_EQ).append(reader.getDocumentFormat()).append(_C)
                    .append(_NF).append("duplex").append(_EQ).append(reader.getPlex()).append(_C)
                    .append(_NF).append("jobAssemblyMode").append(_EQ).append(reader.getJobAssemblyMode()).append(_C)
                    .append(_NF).append("mediaSource").append(_EQ).append(reader.getMediaSource()).append(_C)
                    .append(_NF).append("mediaWeightAdjustment").append(_EQ).append(reader.getMediaWeightAdjustment()).append(_C)
                    .append(_NF).append("misfeedDetectionMode").append(_EQ).append(reader.getMisfeedDetectionMode()).append(_C)
                    .append(_NF).append("orientation").append(_EQ).append(reader.getOrientation()).append(_C)
                    .append(_NF).append("outputQuality").append(_EQ).append(reader.getOutputQuality()).append(_C)
                    .append(_NF).append("progressDialogMode").append(_EQ).append(reader.getProgressDialogMode()).append(_C)
                    .append(_NF).append("resolution").append(_EQ).append(reader.getResolution()).append(_C)
                    .append(_NF).append("scanPreview").append(_EQ).append(reader.getScanPreview()).append(_C)
                    .append(_NF).append("scanSize").append(_EQ).append(reader.getScanSize()).append(_C)
                    .append(_NF).append("sharpnessAdjustment").append(_EQ).append(reader.getSharpnessAdjustment()).append(_C)
                    .append(_NF).append("textPhotoOptimization").append(_EQ).append(reader.getTextPhotoOptimization()).append(_C)
                    .append(_NF).append("transmissionMode").append(_EQ).append(reader.getTransmissionMode()).append(_C)
                    .append(_NF).append("scanSize").append(_EQ).append(reader.getScanSize()).append(_C)
                    .append(_NF).append("splitAttachmentByPage").append(_EQ).append(reader.getSplitAttachmentByPage()).append(_C)
                    .append(_NF).append("maxPagesPerAttachment").append(_EQ).append(reader.getMaxPagesPerAttachment()).append(_C)
                    .append(_NF).append("eraseMarginUnit").append(_EQ).append(reader.getEraseMarginUnit()).append(_C)
                    .append(_NF).append("eraseBackLeft").append(_EQ).append(reader.getEraseBackLeftMargin()).append(_C)
                    .append(_NF).append("eraseBackTop").append(_EQ).append(reader.getEraseBackTopMargin()).append(_C)
                    .append(_NF).append("eraseBackRight").append(_EQ).append(reader.getEraseBackRightMargin()).append(_C)
                    .append(_NF).append("eraseBackBottom").append(_EQ).append(reader.getEraseBackBottomMargin()).append(_C)
                    .append(_NF).append("eraseFrontLeft").append(_EQ).append(reader.getEraseFrontLeftMargin()).append(_C)
                    .append(_NF).append("eraseFrontTop").append(_EQ).append(reader.getEraseFrontTopMargin()).append(_C)
                    .append(_NF).append("eraseFrontRight").append(_EQ).append(reader.getEraseFrontRightMargin()).append(_C)
                    .append(_NF).append("eraseFrontBottom").append(_EQ).append(reader.getEraseFrontBottomMargin()).append(_C)
                    .append(_NF).append("captureMode").append(_EQ).append(reader.getCaptureMode()).append(_C)
                    .append(_NF).append("automaticToneMode").append(_EQ).append(reader.getAutomaticToneMode()).append(_C)
                    .append(_NF).append("automaticStraightenMode").append(_EQ).append(reader.getAutomaticStraightenMode())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(StatusInfo statusInfo) {
        if (statusInfo != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("isBusy").append(_EQ).append(statusInfo.isBusy()).append(_C)
                    .append(_NF).append("isOnline").append(_EQ).append(statusInfo.isOnline()).append(_C)
                    .append(_NF).append("isPaperInFlatbed").append(_EQ).append(statusInfo.isPaperInFlatbed()).append(_C)
                    .append(_NF).append("isPaperInAdf").append(_EQ).append(statusInfo.isPaperInAdf()).append(_C)
                    .append(_NF).append("isAdfOutputBinFull").append(_EQ).append(statusInfo.isAdfOutputBinFull())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(JobInfo jobInfo) {
        if (jobInfo != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("jobId").append(_EQ).append(jobInfo.getJobId()).append(_C)
                    .append(_NF).append("jobName").append(_EQ).append(jobInfo.getJobName()).append(_C)
                    .append(_NF).append("jobType").append(_EQ).append(jobInfo.getJobType()).append(_C)
                    .append(_NF).append("owner").append(_EQ).append(jobInfo.getOwner()).append(_C)
                    .append(_NF).append("startTime").append(_EQ).append(jobInfo.getStartTime()).append(_C)
                    .append(_NF).append("completeTime").append(_EQ).append(jobInfo.getCompleteTime()).append(_C);
            if (jobInfo.getJobType() == JobInfo.JobType.SCAN) {
                ScanJobData scanJobData = jobInfo.getJobData();
                if (scanJobData != null) {
                    logBuilder.append(_NF).append("jobData").append(_EQ).append(_START_SUB);
                    ScanJobState scanJobState = scanJobData.getJobState();
                    if (scanJobState != null) {
                        logBuilder.append("jobState").append(_EQ).append(_START_SUB)
                                .append("state").append(_EQ).append(scanJobState.getState()).append(_C)
                                .append("scanningState").append(_EQ).append(scanJobState.getScanningState()).append(_C)
                                .append("processingState").append(_EQ).append(scanJobState.getProcessingState()).append(_C)
                                .append("transmittingState").append(_EQ).append(scanJobState.getTransmittingState()).append(_C)
                                .append("cancelingState").append(_EQ).append(scanJobState.getCancelingState()).append(_END_SUB);
                    }
                    logBuilder.append(_C)
                            .append("imagesScanned").append(_EQ).append(scanJobData.getImagesScanned()).append(_C)
                            .append("imagesProcessed").append(_EQ).append(scanJobData.getImagesProcessed()).append(_C)
                            .append("imagesTransmitted").append(_EQ).append(scanJobData.getImagesTransmitted()).append(_C)
                            .append("duplex").append(_EQ).append(scanJobData.getDuplex()).append(_C)
                            .append("fileNames").append(_EQ).append(scanJobData.getFileNames().toString()).append(_C)
                            .append("scanSize").append(_EQ).append(scanJobData.getScanSize()).append(_C)
                            .append("destination").append(_EQ).append(scanJobData.getDestination()).append(_END_SUB);
                }
            }
            logBuilder.append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(Result result) {
        String code = (Result.RESULT_OK == result.getCode())? "RESULT_OK" : "RESULT_FAIL";
        StringBuilder builder = new StringBuilder()
                .append(_START)
                .append(_NF).append("Code:").append(code);
        if (Result.RESULT_OK != result.getCode() && result.getErrorCode() != null) {
            builder.append(_C).append(_NF).append("ErrorCode:").append(result.getErrorCode());
        }
        if (!TextUtils.isEmpty(result.getCause())) {
            builder.append(_C).append(_NF).append("Cause:").append(result.getCause());
        }
        builder.append(_NF).append(_END);
        return builder.toString();
    }

    public static void showResult(Activity activity, String msg) {
        showResult(activity, msg, null);
    }

    public static void showResult(Activity activity, String msg, Result result) {
        if (result != null) {
            msg = msg + Logger._NF + Logger.build(result);
            if (result.getCode() == Result.RESULT_FAIL) {
                Log.e(MainActivity.TAG, msg);
            } else {
                Log.d(MainActivity.TAG, msg);
            }
        } else {
            Log.d(MainActivity.TAG, msg);
        }
        if (activity != null && !activity.isFinishing()) {
            showToastMessage(activity, msg);
        }
    }

    private static void showToastMessage(Activity activity, String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}