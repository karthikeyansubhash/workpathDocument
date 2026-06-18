// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.CopyAttributesCaps;
import com.hp.workpath.api.copier.CopyletAttributes;
import com.hp.workpath.api.copier.JobCredentialsAttributes;
import com.hp.workpath.api.copier.Shifts;
import com.hp.workpath.api.copier.StampOption;
import com.hp.workpath.api.scanner.Margins;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CopyTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;

    private final SharedPreferences mPrefs;
    private String mErrorMsg = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public CopyTask(final MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = mContextRef.get();

                    try {
                        boolean settingsUi = mPrefs.getBoolean(CopyConfigureFragment.PREF_SETTINGS_UI, false);
                        Log.i(TAG, "Settings UI:" + settingsUi);

                        CopyAttributes attributes = null;
                        if (!settingsUi) {
                            // Obtain Caps to build Copy Attributes
                            CopyAttributesCaps caps = activity.getCapabilities();

                            if (caps == null) {
                                mErrorMsg = activity.getString(R.string.capabilities_not_loaded);
                                onPostExecute(null);
                                return;
                            }

                            attributes = buildCopyAttributes(caps);
                        }

                        final CopyletAttributes taskAttribs = new CopyletAttributes.Builder()
                                .setShowSettingsUi(settingsUi)
                                .build();

                        // Submit the job
                        final String rid = CopierService.submit(activity, attributes, taskAttribs);
                        onPostExecute(rid);
                    } catch (CapabilitiesExceededException cee) {
                        mErrorMsg = "CapabilitiesExceededException";
                        Logger.showResult(null, mErrorMsg + " " + cee.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (IllegalArgumentException iae) {
                        mErrorMsg = "IllegalArgumentException";
                        Logger.showResult(null, mErrorMsg + " " + iae.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (Throwable t) {
                        mErrorMsg = "Unknown exception";
                        Logger.showResult(null, mErrorMsg + " " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }

                }
            });
        } catch (Exception e) {
            mErrorMsg = e.getMessage();
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final String rid) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(rid)) {
                    mContextRef.get().setRid(rid);
                    Logger.showResult(mContextRef.get(), "Job submitted with rid = " + rid);
                } else if (mErrorMsg != null) {
                    Logger.showResult(mContextRef.get(), mErrorMsg);
                }
            }
        });

    }

    private CopyAttributes buildCopyAttributes(CopyAttributesCaps capabilities)
            throws CapabilitiesExceededException {
        final CopyAttributes.ColorMode colorMode = CopyAttributes.ColorMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COLOR_MODE,
                        CopyAttributes.ColorMode.DEFAULT.name()));
        Log.i(TAG, "Selected Color Mode:" + colorMode.name());

        final CopyAttributes.Duplex scanDuplex = CopyAttributes.Duplex.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_DUPLEX_MODE,
                        CopyAttributes.Duplex.DEFAULT.name()));
        Log.i(TAG, "Selected Duplex Mode:" + scanDuplex.name());

        final CopyAttributes.Orientation orientation = CopyAttributes.Orientation.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_ORIENTATION,
                        CopyAttributes.Orientation.DEFAULT.name()));
        Log.i(TAG, "Selected Orientation:" + orientation.name());

        final CopyAttributes.ScanSize scanSize = CopyAttributes.ScanSize.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_SIZE,
                        CopyAttributes.ScanSize.DEFAULT.name()));
        Log.i(TAG, "Selected Scan Size:" + scanSize.name());

        final CopyAttributes.ScanSource scanSource = CopyAttributes.ScanSource.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_SOURCE,
                        CopyAttributes.ScanSource.DEFAULT.name()));
        Log.i(TAG, "Selected Scan Source:" + scanSource.name());

        final CopyAttributes.CopyPreview copyPreview = CopyAttributes.CopyPreview.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COPY_PREVIEW,
                        CopyAttributes.CopyPreview.DEFAULT.name()));
        Log.i(TAG, "Selected Copy Preview:" + copyPreview.name());

        final float scanCustomLength = Float.parseFloat(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_CUSTOM_LENGTH, "0"));
        Log.i(TAG, "Selected Scan Custom Length:" + scanCustomLength);

        final float scanCustomWidth = Float.parseFloat(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_CUSTOM_WIDTH, "0"));
        Log.i(TAG, "Selected Scan Custom Width:" + scanCustomWidth);

        final CopyAttributes.BackgroundCleanup backgroundCleanup = CopyAttributes.BackgroundCleanup.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_BACKGROUND_CLEANUP,
                        CopyAttributes.BackgroundCleanup.DEFAULT.name()));
        Log.i(TAG, "Selected BackgroundCleanup:" + backgroundCleanup.name());

        final CopyAttributes.ContrastAdjustment contrastAdjustment = CopyAttributes.ContrastAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_CONTRAST_ADJUSTMENT,
                        CopyAttributes.ContrastAdjustment.DEFAULT.name()));
        Log.i(TAG, "Selected ContrastAdjustment:" + contrastAdjustment.name());

        final CopyAttributes.DarknessAdjustment darknessAdjustment = CopyAttributes.DarknessAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_DARKNESS_ADJUSTMENT,
                        CopyAttributes.DarknessAdjustment.DEFAULT.name()));
        Log.i(TAG, "Selected DarknessAdjustment:" + darknessAdjustment.name());

        final CopyAttributes.SharpnessAdjustment sharpnessAdjustment = CopyAttributes.SharpnessAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SHARPNESS_ADJUSTMENT,
                        CopyAttributes.SharpnessAdjustment.DEFAULT.name()));
        Log.i(TAG, "Selected SharpnessAdjustment:" + sharpnessAdjustment.name());

        final CopyAttributes.Duplex printDuplex = CopyAttributes.Duplex.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_DUPLEX_MODE,
                        CopyAttributes.Duplex.DEFAULT.name()));
        Log.i(TAG, "Selected Print Duplex Mode:" + printDuplex.name());

        final CopyAttributes.PaperSize printSize = CopyAttributes.PaperSize.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_SIZE,
                        CopyAttributes.PaperSize.DEFAULT.name()));
        Log.i(TAG, "Selected Print Size:" + printSize.name());

        final float printCustomLength = Float.parseFloat(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_CUSTOM_LENGTH, "0"));
        Log.i(TAG, "Selected Print Custom Length:" + printCustomLength);

        final float printCustomWidth = Float.parseFloat(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_CUSTOM_WIDTH, "0"));
        Log.i(TAG, "Selected Print Custom Width:" + printCustomWidth);

        final int copies = Integer.parseInt(
                mPrefs.getString(CopyConfigureFragment.PREF_COPIES, "1"));
        Log.i(TAG, "Selected Copies:" + copies);

        final CopyAttributes.CollateMode collateMode = CopyAttributes.CollateMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COLLATE_MODE,
                        CopyAttributes.CollateMode.DEFAULT.name()));
        Log.i(TAG, "Selected Collate Mode:" + collateMode.name());

        final CopyAttributes.PaperSource paperSource = CopyAttributes.PaperSource.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PAPER_SOURCE,
                        CopyAttributes.PaperSource.DEFAULT.name()));
        Log.i(TAG, "Selected Paper Source:" + paperSource.name());

        final CopyAttributes.PaperType paperType = CopyAttributes.PaperType.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PAPER_TYPE,
                        CopyAttributes.PaperType.DEFAULT.name()));
        Log.i(TAG, "Selected Paper Type:" + paperType.name());

        final CopyAttributes.ScaleMode scaleMode = CopyAttributes.ScaleMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCALE_MODE,
                        CopyAttributes.ScaleMode.DEFAULT.name()));
        Log.i(TAG, "Selected Scale Mode:" + scaleMode.name());

        final CopyAttributes.TextGraphicsOptimization textGraphicsOptimization = CopyAttributes.TextGraphicsOptimization.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_TEXT_GRAPHICS_OPTIMIZATION,
                        CopyAttributes.TextGraphicsOptimization.DEFAULT.name()));
        Log.i(TAG, "Selected Text/Graphics Optimization:" + textGraphicsOptimization.name());

        final int scalePercent = Integer.parseInt(
                mPrefs.getString(CopyConfigureFragment.PREF_SCALE_PERCENT, "100"));
        Log.i(TAG, "Selected Scale Percent:" + scalePercent);

        final CopyAttributes.JobAssemblyMode jobAssemblyMode = CopyAttributes.JobAssemblyMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_JOB_ASSEMBLY_MODE,
                        CopyAttributes.JobAssemblyMode.DEFAULT.name()));
        Log.i(TAG, "Selected Job Assembly Mode:" + jobAssemblyMode.name());

        final CopyAttributes.JobExecutionMode jobExecutionMode = CopyAttributes.JobExecutionMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_JOB_EXECUTION_MODE,
                        CopyAttributes.JobExecutionMode.NORMAL.name()));
        Log.i(TAG, "Selected Job Execution Mode:" + jobExecutionMode.name());

        final CopyAttributes.NumberUpMode numberUpMode = CopyAttributes.NumberUpMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_NUMBER_UP_MODE,
                        CopyAttributes.NumberUpMode.DEFAULT.name()));
        Log.i(TAG, "Selected Number Up Mode:" + numberUpMode.name());

        final CopyAttributes.NumberUpDirection numberUpDirection = CopyAttributes.NumberUpDirection.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_NUMBER_UP_DIRECTION,
                        CopyAttributes.NumberUpDirection.DEFAULT.name()));
        Log.i(TAG, "Selected Number Up Direction:" + numberUpDirection.name());

        final CopyAttributes.OutputBin outputBin = CopyAttributes.OutputBin.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_OUTPUT_BIN,
                        CopyAttributes.OutputBin.DEFAULT.name()));
        Log.i(TAG, "Selected Output Bin:" + outputBin.name());

        final CopyAttributes.ProgressDialogMode progressDialogMode = CopyAttributes.ProgressDialogMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PROGRESS_DIALOG_MODE,
                        CopyAttributes.ProgressDialogMode.DEFAULT.name()));
        Log.i(TAG, "Selected Progress Dialog Mode:" + progressDialogMode.name());

        final CopyAttributes.EraseMarginUnit eraseMarginUnit = CopyAttributes.EraseMarginUnit.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_ERASE_MARGIN_UNIT,
                        CopyAttributes.EraseMarginUnit.DEFAULT.name()));
        Log.d(TAG, "Selected eraseMarginUnit:" + eraseMarginUnit.name());

        final Margins backMargin = new Margins(mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_BOTTOM_MARGIN, 0.0f));
        Log.d(TAG, "Selected back eraseMargin:" + backMargin.getTopMargin() + "," + backMargin.getBottomMargin() + "," + backMargin.getLeftMargin() + "," + backMargin.getRightMargin());

        final Margins frontMargin = new Margins(mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_BOTTOM_MARGIN, 0.0f));

        final CopyAttributes.CaptureMode captureMode = CopyAttributes.CaptureMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_CAPTURE_MODE,
                        CopyAttributes.CaptureMode.DEFAULT.name()));
        Log.i(TAG, "Selected Capture Mode:" + captureMode.name());

        final CopyAttributes.ImageShiftReduceToFit imageShiftReduceToFit = CopyAttributes.ImageShiftReduceToFit.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_IMAGE_SHIFT_REDUCE_TO_FIT,
                        CopyAttributes.ImageShiftReduceToFit.DEFAULT.name()));
        Log.i(TAG, "Selected image shift reduce to fit:" + imageShiftReduceToFit.name());

        final CopyAttributes.ImageShiftUnits imageShiftUnits = CopyAttributes.ImageShiftUnits.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_IMAGE_SHIFT_UNITS,
                        CopyAttributes.ImageShiftUnits.DEFAULT.name()));
        Log.i(TAG, "Selected image shift units:" + imageShiftUnits.name());

        final Shifts imageShiftFront = new Shifts(mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_X_FRONT, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_Y_FRONT, 0.0f));
        Log.i(TAG, "Selected imageShiftFront x shift :" + imageShiftFront.getXShift()
                + " imageShiftFront y shift :" + imageShiftFront.getYShift());

        final Shifts imageShiftBack = new Shifts(mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_X_BACK, 0.0f),
                mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_Y_BACK, 0.0f));
        Log.i(TAG, "Selected imageShiftBack x shift :" + imageShiftBack.getXShift() +
                " imageShiftBack y shift :" + imageShiftBack.getYShift());

        final CopyAttributes.BookletBordersEachPage bookletBordersEachPage = CopyAttributes.BookletBordersEachPage.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_BORDERS_EACH_PAGE,
                        CopyAttributes.BookletBordersEachPage.DEFAULT.name()));
        Log.i(TAG, "Selected bookletBordersEachPage:" + bookletBordersEachPage.name());

        final CopyAttributes.BookletFinishingOption bookletFinishingOption = CopyAttributes.BookletFinishingOption.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_FINISHING_OPTION,
                        CopyAttributes.BookletFinishingOption.DEFAULT.name()));
        Log.i(TAG, "Selected bookletFinishingOption:" + bookletFinishingOption.name());

        final CopyAttributes.BookletFormat bookletFormat = CopyAttributes.BookletFormat.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_FORMAT,
                        CopyAttributes.BookletFormat.DEFAULT.name()));
        Log.i(TAG, "Selected bookletFormat:" + bookletFormat.name());

        final CopyAttributes.FoldMode foldMode = CopyAttributes.FoldMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_FOLD_MODE,
                        CopyAttributes.FoldMode.NONE.name()));
        Log.i(TAG, "Selected Fold Mode:" + foldMode.name());

        final CopyAttributes.StapleOption stapleOption = CopyAttributes.StapleOption.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_STAPLE_MODE,
                        CopyAttributes.StapleOption.NONE.name()));
        Log.i(TAG, "Selected staple Mode:" + stapleOption.name());

        final CopyAttributes.PunchMode punchMode = CopyAttributes.PunchMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PUNCH_MODE,
                        CopyAttributes.PunchMode.NONE.name()));
        Log.i(TAG, "Selected punch Mode:" + punchMode.name());

        final int watermarkTextSize = Integer.parseInt(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT_SIZE, "0"));
        Log.i(TAG, "Selected watermark text size:" + watermarkTextSize);

        final int watermarkTransparency = Integer.parseInt(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TRANSPARENCY, "0"));
        Log.i(TAG, "Selected watermark transparency:" + watermarkTransparency);

        final String watermarkTextColor = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT_COLOR, null);
        Log.i(TAG, "watermarkTextColor is:" + watermarkTextColor);

        final String watermarkFont = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_FONT, null);
        Log.i(TAG, "watermarkFont is:+" + watermarkFont);

        final String watermarkBackgroundColor = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_BACKGROUND_COLOR, null);
        Log.i(TAG, "watermarkBackgroundColor is:" + watermarkBackgroundColor);

        final CopyAttributes.WatermarkOnlyFirstPage watermarkOnlyFirstPage = CopyAttributes.WatermarkOnlyFirstPage.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_ONLY_FIRST_PAGE,
                        CopyAttributes.WatermarkOnlyFirstPage.DEFAULT.name()));
        Log.i(TAG, "Selected OnlyFirst:" + watermarkOnlyFirstPage.name());


        final int watermarkDarkness = Integer.parseInt(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_DARKNESS, "0"));
        Log.i(TAG, "Selected watermarkDarkness:" + watermarkDarkness);

        final String watermarkText = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT, null);
        Log.i(TAG, "watermarkText is:+" + watermarkText);

        final CopyAttributes.WatermarkRotate45 watermarkRotate45 = CopyAttributes.WatermarkRotate45.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_ROTATION,
                        CopyAttributes.WatermarkRotate45.DEFAULT.name()));
        Log.i(TAG, "Selected rotation:" + watermarkRotate45.name());

        final CopyAttributes.WatermarkType watermarkType = CopyAttributes.WatermarkType.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TYPE,
                        CopyAttributes.WatermarkType.DEFAULT.name()));
        Log.i(TAG, "Selected Type:" + watermarkType.name());

        final CopyAttributes.WatermarkBackgroundPattern watermarkBackgroundPattern = CopyAttributes.WatermarkBackgroundPattern.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_BACKGROUND_PATTERN,
                        CopyAttributes.WatermarkBackgroundPattern.DEFAULT.name()));
        Log.i(TAG, "Selected WatermarkBackgroundPattern:" + watermarkBackgroundPattern.name());

        final CopyAttributes.WatermarkMessageType watermarkMessageType = CopyAttributes.WatermarkMessageType.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_MESSAGE_TYPE,
                        CopyAttributes.WatermarkMessageType.NONE.name()));
        Log.i(TAG, "Selected watermarkMessageType:" + watermarkMessageType.name());

        Gson gson = new Gson();

        switch (jobExecutionMode) {
            case NORMAL:
                CopyAttributes.CopyBuilder copyBuilder =
                        new CopyAttributes.CopyBuilder()
                                .setColorMode(colorMode)
                                .setOrientation(orientation)
                                .setScanDuplex(scanDuplex)
                                .setScanSize(scanSize)
                                .setScanCustomLength(scanCustomLength)
                                .setScanCustomWidth(scanCustomWidth)
                                .setScanSource(scanSource)
                                .setCopyPreview(copyPreview)
                                .setBackgroundCleanup(backgroundCleanup)
                                .setContrastAdjustment(contrastAdjustment)
                                .setDarknessAdjustment(darknessAdjustment)
                                .setSharpnessAdjustment(sharpnessAdjustment)
                                .setPrintDuplex(printDuplex)
                                .setPrintSize(printSize)
                                .setPrintCustomLength(printCustomLength)
                                .setPrintCustomWidth(printCustomWidth)
                                .setCopies(copies)
                                .setCollateMode(collateMode)
                                .setPaperSource(paperSource)
                                .setPaperType(paperType)
                                .setScaleMode(scaleMode)
                                .setScalePercent(scalePercent)
                                .setTextGraphicsOptimization(textGraphicsOptimization)
                                .setJobAssemblyMode(jobAssemblyMode)
                                .setNumberUpMode(numberUpMode)
                                .setNumberUpDirection(numberUpDirection)
                                .setOutputBin(outputBin)
                                .setProgressDialogMode(progressDialogMode)
                                .setEraseMarginUnit(eraseMarginUnit)
                                .setEraseBackMargin(backMargin)
                                .setEraseFrontMargin(frontMargin)
                                .setCaptureMode(captureMode)
                                .setImageShiftReduceToFit(imageShiftReduceToFit)
                                .setImageShiftUnits(imageShiftUnits)
                                .setImageShiftFront(imageShiftFront)
                                .setImageShiftBack(imageShiftBack)
                                .setBookletBordersEachPage(bookletBordersEachPage)
                                .setBookletFinishingOption(bookletFinishingOption)
                                .setBookletFormat(bookletFormat)
                                .setFoldMode(foldMode)
                                .setStapleOption(stapleOption)
                                .setPunchMode(punchMode)
                                .setWatermarkDarkness(watermarkDarkness)
                                .setWatermarktextSize(watermarkTextSize)
                                .setWatermarkTransparency(watermarkTransparency)
                                .setWatermarkFont(watermarkFont)
                                .setWatermarkBackgroundColor(watermarkBackgroundColor)
                                .setWatermarkTextColor(watermarkTextColor)
                                .setWatermarkOnlyFirstPage(watermarkOnlyFirstPage)
                                .setWatermarkText(watermarkText)
                                .setWatermarkRotate45(watermarkRotate45)
                                .setWatermarkType(watermarkType)
                                .setWatermarkBackgroundPattern(watermarkBackgroundPattern)
                                .setWatermarkMessageType(watermarkMessageType);
                
                for (CopyAttributes.StampPosition stampPosition : CopyAttributes.StampPosition.values()) {
                    String jsonStampOption = mPrefs.getString(CopyConfigureFragment.PREF_STAMP + stampPosition.name(), "");
                    if ("".equals(jsonStampOption)) {
                        continue;
                    }
                    StampOption stampOption = gson.fromJson(jsonStampOption, StampOption.class);
                    copyBuilder.setStampOption(stampPosition, stampOption);
                }

                return copyBuilder.build(capabilities);

            case STORE:
                final String storeJobName = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_NAME, "");
                Log.i(TAG, "Selected Store Job Name:" + storeJobName);

                final String storeJobFolderName = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_FOLDER_NAME, "");
                Log.i(TAG, "Selected Store Job Folder Mode:" + storeJobFolderName);

                final CopyAttributes.RetentionMode deleteOnPower = CopyAttributes.RetentionMode.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_DELETE_ON_POWER,
                                CopyAttributes.RetentionMode.DEFAULT.name()));
                Log.i(TAG, "Selected Store Job Delete On Power:" + deleteOnPower.name());

                final CopyAttributes.RetentionMode deleteOnRelease = CopyAttributes.RetentionMode.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_DELETE_ON_RELEASE,
                                CopyAttributes.RetentionMode.DEFAULT.name()));
                Log.i(TAG, "Selected Store Job Delete On Release:" + deleteOnRelease.name());

                final JobCredentialsAttributes.PasswordType storeJobPasswordType = JobCredentialsAttributes.PasswordType.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_PASSWORD_TYPE,
                                JobCredentialsAttributes.PasswordType.NONE.name()));

                final String storeJobPassword = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_PASSWORD, "");

                CopyAttributes.StoreCopyBuilder storeCopyBuilder = new CopyAttributes.StoreCopyBuilder();

                if (storeJobPasswordType != JobCredentialsAttributes.PasswordType.NONE) {
                    JobCredentialsAttributes jobCredentialsAttributes = new JobCredentialsAttributes.Builder()
                            .setPasswordType(storeJobPasswordType)
                            .setPassword(storeJobPassword)
                            .build();

                    storeCopyBuilder.setStoreJobCredentials(jobCredentialsAttributes);
                }

                return storeCopyBuilder
                        .setStoreJobName(storeJobName)
                        .setStoreJobFolderName(storeJobFolderName)
                        .setRetentionModeOnPowerCycle(deleteOnPower)
                        .setRetentionModeOnRelease(deleteOnRelease)
                        .setColorMode(colorMode)
                        .setOrientation(orientation)
                        .setScanDuplex(scanDuplex)
                        .setScanSize(scanSize)
                        .setScanCustomLength(scanCustomLength)
                        .setScanCustomWidth(scanCustomWidth)
                        .setScanSource(scanSource)
                        .setCopyPreview(copyPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setPrintDuplex(printDuplex)
                        .setPrintSize(printSize)
                        .setPrintCustomLength(printCustomLength)
                        .setPrintCustomWidth(printCustomWidth)
                        .setCopies(copies)
                        .setCollateMode(collateMode)
                        .setPaperSource(paperSource)
                        .setPaperType(paperType)
                        .setScaleMode(scaleMode)
                        .setScalePercent(scalePercent)
                        .setTextGraphicsOptimization(textGraphicsOptimization)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setNumberUpMode(numberUpMode)
                        .setNumberUpDirection(numberUpDirection)
                        .setOutputBin(outputBin)
                        .setProgressDialogMode(progressDialogMode)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setImageShiftReduceToFit(imageShiftReduceToFit)
                        .setImageShiftUnits(imageShiftUnits)
                        .setImageShiftFront(imageShiftFront)
                        .setImageShiftBack(imageShiftBack)
                        .setBookletBordersEachPage(bookletBordersEachPage)
                        .setBookletFinishingOption(bookletFinishingOption)
                        .setBookletFormat(bookletFormat)
                        .setFoldMode(foldMode)
                        .setStapleOption(stapleOption)
                        .setPunchMode(punchMode)
                        .setWatermarkDarkness(watermarkDarkness)
                        .setWatermarktextSize(watermarkTextSize)
                        .setWatermarkTransparency(watermarkTransparency)
                        .setWatermarkFont(watermarkFont)
                        .setWatermarkBackgroundColor(watermarkBackgroundColor)
                        .setWatermarkTextColor(watermarkTextColor)
                        .setWatermarkOnlyFirstPage(watermarkOnlyFirstPage)
                        .setWatermarkText(watermarkText)
                        .setWatermarkRotate45(watermarkRotate45)
                        .setWatermarkType(watermarkType)
                        .setWatermarkBackgroundPattern(watermarkBackgroundPattern)
                        .setWatermarkMessageType(watermarkMessageType)
                        .build(capabilities);
            default:
                throw new IllegalStateException("Unsupported job execution mode");
        }
    }
}