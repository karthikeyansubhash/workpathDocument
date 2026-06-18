// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.CopyAttributesCaps;
import com.hp.workpath.api.copier.CopyAttributesReader;
import com.hp.workpath.api.copier.StoredJobInfo;
import com.hp.workpath.api.job.CopyJobData;
import com.hp.workpath.api.job.CopyJobState;
import com.hp.workpath.api.job.JobInfo;

import androidx.multidex.BuildConfig;

public class Logger {

    public static final boolean _DEBUG = BuildConfig.DEBUG;

    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _START_SUB = "{";
    private static final String _END_SUB = "}";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(CopyAttributesCaps caps) {
        if (caps != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("backgroundCleanup").append(_EQ).append(caps.getBackgroundCleanupList()).append(_C)
                    .append(_NF).append("collateModeList").append(_EQ).append(caps.getCollateModeList()).append(_C)
                    .append(_NF).append("colorModeList").append(_EQ).append(caps.getColorModeList()).append(_C)
                    .append(_NF).append("contrastAdjustmentList").append(_EQ).append(caps.getContrastAdjustmentList()).append(_C)
                    .append(_NF).append("copiesRange").append(_EQ).append(caps.getCopiesRange().toString()).append(_C)
                    .append(_NF).append("copyPreviewList").append(_EQ).append(caps.getCopyPreviewList()).append(_C)
                    .append(_NF).append("darknessAdjustmentList").append(_EQ).append(caps.getDarknessAdjustmentList()).append(_C)
                    .append(_NF).append("jobAssemblyModeList").append(_EQ).append(caps.getJobAssemblyModeList()).append(_C)
                    .append(_NF).append("jobExecutionModeList").append(_EQ).append(caps.getJobExecutionModeList()).append(_C)
                    .append(_NF).append("numberUpDirectionByNumberUpCount").append(_EQ).append(caps.getNumberUpDirectionByNumberUpCount()).append(_C)
                    .append(_NF).append("numberUpModeList").append(_EQ).append(caps.getNumberUpModeList()).append(_C)
                    .append(_NF).append("orientationList").append(_EQ).append(caps.getOrientationList()).append(_C)
                    .append(_NF).append("paperSourceList").append(_EQ).append(caps.getPaperSourceList()).append(_C)
                    .append(_NF).append("paperTypeList").append(_EQ).append(caps.getPaperTypeList()).append(_C)
                    .append(_NF).append("passwordTypeList").append(_EQ).append(caps.getPasswordTypeList()).append(_C)
                    .append(_NF).append("printCustomLengthRange").append(_EQ).append(caps.getPrintCustomLengthRange().toString()).append(_C)
                    .append(_NF).append("printCustomWidthRange").append(_EQ).append(caps.getPrintCustomWidthRange().toString()).append(_C)
                    .append(_NF).append("printDuplexList").append(_EQ).append(caps.getPrintDuplexList()).append(_C)
                    .append(_NF).append("printSizeList").append(_EQ).append(caps.getPrintSizeList()).append(_C)
                    .append(_NF).append("scaleModeList").append(_EQ).append(caps.getScaleModeList()).append(_C)
                    .append(_NF).append("scalePercentRangeByScanSource").append(_EQ).append(caps.getScalePercentRangeByScanSource().toString()).append(_C)
                    .append(_NF).append("scanCustomLengthRange").append(_EQ).append(caps.getScanCustomLengthRange().toString()).append(_C)
                    .append(_NF).append("scanCustomWidthRange").append(_EQ).append(caps.getScanCustomWidthRange().toString()).append(_C)
                    .append(_NF).append("scanDuplexList").append(_EQ).append(caps.getScanDuplexList()).append(_C)
                    .append(_NF).append("scanSizeList").append(_EQ).append(caps.getScanSizeList()).append(_C)
                    .append(_NF).append("scanSourceList").append(_EQ).append(caps.getScanSourceList()).append(_C)
                    .append(_NF).append("sharpnessAdjustmentList").append(_EQ).append(caps.getSharpnessAdjustmentList()).append(_C)
                    .append(_NF).append("textGraphicsOptimizationList").append(_EQ).append(caps.getTextGraphicsOptimizationList()).append(_C)
                    .append(_NF).append("outputBin").append(_EQ).append(caps.getOutputBinList()).append(_C)
                    .append(_NF).append("progressDialogMode").append(_EQ).append(caps.getProgressDialogModeList()).append(_C)
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
                    .append(_NF).append("imageShiftReduceToFit").append(_EQ).append(caps.getImageShiftReduceToFitList()).append(_C)
                    .append(_NF).append("imageShiftUnits").append(_EQ).append(caps.getImageShiftUnitsList()).append(_C)
                    .append(_NF).append("imageShiftXFront").append(_EQ).append(caps.getImageShiftXFrontRange()).append(_C)
                    .append(_NF).append("imageShiftYFront").append(_EQ).append(caps.getImageShiftYFrontRange()).append(_C)
                    .append(_NF).append("imageShiftXBack").append(_EQ).append(caps.getImageShiftXBackRange()).append(_C)
                    .append(_NF).append("imageShiftYBack").append(_EQ).append(caps.getImageShiftYBackRange()).append(_C)
                    .append(_NF).append("bookletBordersEachPage").append(_EQ).append(caps.getBookletBordersEachPageList()).append(_C)
                    .append(_NF).append("bookletFinishingOption").append(_EQ).append(caps.getBookletFinishingOptionList()).append(_C)
                    .append(_NF).append("bookletFormat").append(_EQ).append(caps.getBookletFormatList()).append(_C)
                    .append(_NF).append("watermarkRotate45List").append(_EQ).append(caps.getWatermarkRotate45List()).append(_C)
                    .append(_NF).append("watermarkDarknessList").append(_EQ).append(caps.getWatermarkDarknessRange().toString()).append(_C)
                    .append(_NF).append("WatermarkTransparencyRange").append(_EQ).append(caps.getWatermarkTransparencyRange().toString()).append(_C)
                    .append(_NF).append("WatermarkOnlyFirstPage").append(_EQ).append(caps.getWatermarkOnlyFirstPageList())
                    .append(_NF).append("WatermarkBackgroundPattern").append(_EQ).append(caps.getWatermarkBackgroundPatternList()).append(_C)
                    .append(_NF).append("WatermarkMessageType").append(_EQ).append(caps.getWatermarkMessageTypeList()).append(_C)
                    .append(_NF).append("WatermarkBackgroundColor").append(_EQ).append(caps.getWatermarkBackgroundColorList()).append(_C)
                    .append(_NF).append("WatermarkFont").append(_EQ).append(caps.getWatermarkFontList()).append(_C)
                    .append(_NF).append("WatermarkTextColor").append(_EQ).append(caps.getWatermarkTextColorList()).append(_C)
                    .append(_NF).append("WatermarkTextSize").append(_EQ).append(caps.getWatermarkTextSizeList()).append(_C)
                    .append(_NF).append("WatermarkType").append(_EQ).append(caps.getWatermarkTypeList()).append(_C)
                    .append(_NF).append("stapleOption").append(_EQ).append(caps.getStapleOptionList()).append(_C)
                    .append(_NF).append("punchMode").append(_EQ).append(caps.getPunchModeList()).append(_C)
                    .append(_NF).append("foldMode").append(_EQ).append(caps.getFoldModeList())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(CopyAttributes attributes) {
        if (attributes != null) {
            CopyAttributesReader reader = new CopyAttributesReader(attributes);
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("backgroundCleanup").append(_EQ).append(reader.getBackgroundCleanup()).append(_C)
                    .append(_NF).append("collateMode").append(_EQ).append(reader.getCollateMode()).append(_C)
                    .append(_NF).append("colorMode").append(_EQ).append(reader.getColorMode()).append(_C)
                    .append(_NF).append("contrastAdjustment").append(_EQ).append(reader.getContrastAdjustment()).append(_C)
                    .append(_NF).append("copies").append(_EQ).append(reader.getCopies()).append(_C)
                    .append(_NF).append("copyPreview").append(_EQ).append(reader.getCopyPreview()).append(_C)
                    .append(_NF).append("darknessAdjustment").append(_EQ).append(reader.getDarknessAdjustment()).append(_C)
                    .append(_NF).append("jobAssemblyMode").append(_EQ).append(reader.getJobAssemblyMode()).append(_C)
                    .append(_NF).append("jobExecutionMode").append(_EQ).append(reader.getJobExecutionMode()).append(_C)
                    .append(_NF).append("numberUpDirection").append(_EQ).append(reader.getNumberUpDirection()).append(_C)
                    .append(_NF).append("numberUpMode").append(_EQ).append(reader.getNumberUpMode()).append(_C)
                    .append(_NF).append("orientation").append(_EQ).append(reader.getOrientation()).append(_C)
                    .append(_NF).append("paperSource").append(_EQ).append(reader.getPaperSource()).append(_C)
                    .append(_NF).append("paperType").append(_EQ).append(reader.getPaperType()).append(_C)
                    .append(_NF).append("printCustomLength").append(_EQ).append(reader.getPrintCustomLength()).append(_C)
                    .append(_NF).append("printCustomWidth").append(_EQ).append(reader.getPrintCustomWidth()).append(_C)
                    .append(_NF).append("printDuplex").append(_EQ).append(reader.getPrintDuplex()).append(_C)
                    .append(_NF).append("printSize").append(_EQ).append(reader.getPrintSize()).append(_C)
                    .append(_NF).append("scaleMode").append(_EQ).append(reader.getScaleMode()).append(_C)
                    .append(_NF).append("scalePercent").append(_EQ).append(reader.getScalePercent()).append(_C)
                    .append(_NF).append("scanCustomLength").append(_EQ).append(reader.getScanCustomLength()).append(_C)
                    .append(_NF).append("scanCustomWidth").append(_EQ).append(reader.getScanCustomWidth()).append(_C)
                    .append(_NF).append("scanDuplex").append(_EQ).append(reader.getScanDuplex()).append(_C)
                    .append(_NF).append("scanSize").append(_EQ).append(reader.getScanSize()).append(_C)
                    .append(_NF).append("scanSource").append(_EQ).append(reader.getScanSource()).append(_C)
                    .append(_NF).append("sharpnessAdjustment").append(_EQ).append(reader.getSharpnessAdjustment()).append(_C)
                    .append(_NF).append("storedJobRetentionModeOnPowerCycle").append(_EQ).append(reader.getStoredJobRetentionModeOnPowerCycle()).append(_C)
                    .append(_NF).append("storedJobRetentionModeOnRelease").append(_EQ).append(reader.getStoredJobRetentionModeOnRelease()).append(_C)
                    .append(_NF).append("storeJobFolderName").append(_EQ).append(reader.getStoreJobFolderName()).append(_C)
                    .append(_NF).append("storeJobName").append(_EQ).append(reader.getStoreJobName()).append(_C)
                    .append(_NF).append("textGraphicsOptimization").append(_EQ).append(reader.getTextGraphicsOptimization()).append(_C)
                    .append(_NF).append("outputBin").append(_EQ).append(reader.getOutputBin()).append(_C)
                    .append(_NF).append("progressDialogMode").append(_EQ).append(reader.getProgressDialogMode()).append(_C)
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
                    .append(_NF).append("imageShiftReduceToFit").append(_EQ).append(reader.getImageShiftReduceToFit()).append(_C)
                    .append(_NF).append("imageShiftUnits").append(_EQ).append(reader.getImageShiftUnits()).append(_C)
                    .append(_NF).append("imageShiftXFront").append(_EQ).append(reader.getImageShiftXFront()).append(_C)
                    .append(_NF).append("imageShiftYFront").append(_EQ).append(reader.getImageShiftYFront()).append(_C)
                    .append(_NF).append("imageShiftXBack").append(_EQ).append(reader.getImageShiftXBack()).append(_C)
                    .append(_NF).append("imageShiftYBack").append(_EQ).append(reader.getImageShiftYBack()).append(_C)
                    .append(_NF).append("bookletBordersEachPage").append(_EQ).append(reader.getBookletBordersEachPage()).append(_C)
                    .append(_NF).append("bookletFinishingOption").append(_EQ).append(reader.getBookletFinishingOption()).append(_C)
                    .append(_NF).append("bookletFormat").append(_EQ).append(reader.getBookletFormat()).append(_C)
                    .append(_NF).append("watermarkRotate45").append(_EQ).append(reader.getWatermarkRotation45()).append(_C)
                    .append(_NF).append("watermarkDarkness").append(_EQ).append(reader.getWatermarkDarkness()).append(_C)
                    .append(_NF).append("WatermarkTransparency").append(_EQ).append(reader.getWatermarkTransparency()).append(_C)
                    .append(_NF).append("WatermarkOnlyFirstPage").append(_EQ).append(reader.getWatermarkOnlyFirstPage())
                    .append(_NF).append("WatermarkBackgroundPattern").append(_EQ).append(reader.getWatermarkBackgroundPattern()).append(_C)
                    .append(_NF).append("WatermarkMessageType").append(_EQ).append(reader.getWatermarkMessageType()).append(_C)
                    .append(_NF).append("WatermarkBackgroundColor").append(_EQ).append(reader.getWatermarkBackgroundColor()).append(_C)
                    .append(_NF).append("WatermarkFont").append(_EQ).append(reader.getWatermarkFont()).append(_C)
                    .append(_NF).append("WatermarkText").append(_EQ).append(reader.getWatermarkText()).append(_C)
                    .append(_NF).append("WatermarkTextColor").append(_EQ).append(reader.getWatermarkTextColor()).append(_C)
                    .append(_NF).append("WatermarkTextSize").append(_EQ).append(reader.getWatermarkTextSize()).append(_C)
                    .append(_NF).append("WatermarkType").append(_EQ).append(reader.getWatermarkType()).append(_C)
                    .append(_NF).append("stapleOption").append(_EQ).append(reader.getStapleOption()).append(_C)
                    .append(_NF).append("punchMode").append(_EQ).append(reader.getPunchMode()).append(_C)
                    .append(_NF).append("foldMode").append(_EQ).append(reader.getFoldMode())
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(com.hp.workpath.api.scanner.StatusInfo statusInfo) {
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

    public static String build(com.hp.workpath.api.printer.StatusInfo statusInfo) {
        if (statusInfo != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("status").append(_EQ).append(statusInfo.getStatus()).append(_C)
                    .append(_NF).append("statusReasons").append(_EQ).append(statusInfo.getStatusReasons())
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
            if (jobInfo.getJobType() == JobInfo.JobType.COPY) {
                CopyJobData copyJobData = jobInfo.getJobData();
                if (copyJobData != null) {
                    logBuilder.append(_NF).append("jobData").append(_EQ).append(_START_SUB);
                    CopyJobState copyJobState = copyJobData.getJobState();
                    if (copyJobState != null) {
                        logBuilder.append("jobState").append(_EQ).append(_START_SUB)
                                .append("state").append(_EQ).append(copyJobState.getState()).append(_C)
                                .append("scanningState").append(_EQ).append(copyJobState.getScanningState()).append(_C)
                                .append("processingState").append(_EQ).append(copyJobState.getProcessingState()).append(_C)
                                .append("printingState").append(_EQ).append(copyJobState.getPrintingState()).append(_C)
                                .append("cancelingState").append(_EQ).append(copyJobState.getCancelingState()).append(_END_SUB);
                    }
                    logBuilder.append(_C)
                            .append("imagesScanned").append(_EQ).append(copyJobData.getImagesScanned()).append(_C)
                            .append("sheetsPrinted").append(_EQ).append(copyJobData.getSheetsPrinted()).append(_C)
                            .append("duplex").append(_EQ).append(copyJobData.getDuplex()).append(_C)
                            .append("scanSize").append(_EQ).append(copyJobData.getScanSize()).append(_C)
                            .append("jobExecutionMode").append(_EQ).append(copyJobData.getJobExecutionMode()).append(_END_SUB);
                }
            }
            logBuilder.append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(StoredJobInfo storedJobInfo) {
        if (storedJobInfo != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("colorMode").append(_EQ).append(storedJobInfo.getColorMode()).append(_C)
                    .append(_NF).append("copies").append(_EQ).append(storedJobInfo.getCopies()).append(_C)
                    .append(_NF).append("originalMediaSize").append(_EQ).append(storedJobInfo.getOriginalMediaSize()).append(_C)
                    .append(_NF).append("outputSides").append(_EQ).append(storedJobInfo.getOutputSides()).append(_C)
                    .append(_NF).append("storedJobFolderName").append(_EQ).append(storedJobInfo.getStoredJobFolderName()).append(_C)
                    .append(_NF).append("storedJobId").append(_EQ).append(storedJobInfo.getStoredJobId()).append(_C)
                    .append(_NF).append("storedJobName").append(_EQ).append(storedJobInfo.getStoredJobName()).append(_C)
                    .append(_NF).append("storedJobPasswordType").append(_EQ).append(storedJobInfo.getStoredJobPasswordType()).append(_C)
                    .append(_NF).append("storedJobUserName").append(_EQ).append(storedJobInfo.getStoredJobUserName()).append(_C)
                    .append(_NF).append("storeJobTimestamp").append(_EQ).append(storedJobInfo.getStoreJobTimestamp()).append(_C)
                    .append(_NF).append("totalPages").append(_EQ).append(storedJobInfo.getTotalPages())
                    .append(_NF).append(_END);
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
