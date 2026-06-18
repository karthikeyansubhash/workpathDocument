// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.copier.*
import com.hp.workpath.api.copier.CopyAttributes.CopyBuilder
import com.hp.workpath.api.copier.CopyAttributes.StoreCopyBuilder
import com.hp.workpath.api.scanner.Margins
import com.hp.workpath.api.copier.Shifts
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class CopyTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private lateinit var mErrorMsg: String
    private var mThrowable: Throwable? = null
    private var rid: String? = null

    suspend fun execute() {
        mContextRef.get()?.run {
            try {
                val settingsUi = mPrefs.getBoolean(CopyConfigureFragment.PREF_SETTINGS_UI, false)
                Log.i(TAG, "Settings UI:$settingsUi")

                var attributes: CopyAttributes? = null

                if (!settingsUi) { // Obtain Caps to build Copy Attributes
                    val caps = capabilities
                    if (caps == null) {
                        mErrorMsg = getString(R.string.capabilities_not_loaded)
                        onPostExecute(rid)
                        return
                    } else {
                        attributes = buildCopyAttributes(caps)
                    }
                }

                val taskAttribs = CopyletAttributes.Builder()
                        .setShowSettingsUi(settingsUi)
                        .build()
                // Submit the job
                rid = CopierService.submit(this, attributes, taskAttribs)
                Log.i(TAG, "Job submitted with rid = $rid")
            } catch (cee: CapabilitiesExceededException) {
                mErrorMsg = "CapabilitiesExceededException"
                mThrowable = cee
            } catch (iae: IllegalArgumentException) {
                Log.e("[TEST]","",iae);
                mErrorMsg = "IllegalArgumentException"
                mThrowable = iae
            } catch (t: Throwable) {
                Log.e("[TEST]","",t);
                mErrorMsg = "Unknown exception"
                mThrowable = t
            }
            onPostExecute(rid)
        }
    }

    private suspend fun onPostExecute(rid: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (!TextUtils.isEmpty(rid)) {
                    this.setRid(rid)
                    Logger.showResult(this, "Job submitted with rid = $rid")
                } else {
                    if (::mErrorMsg.isInitialized) {
                        if (mThrowable != null) {
                            Logger.showResult(this, "$mErrorMsg ${mThrowable?.message}")
                        } else {
                            Logger.showResult(this, mErrorMsg)
                        }
                    }
                }
            }
        }
    }

    @Throws(CapabilitiesExceededException::class)
    private fun buildCopyAttributes(capabilities: CopyAttributesCaps): CopyAttributes {
        val colorMode = CopyAttributes.ColorMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COLOR_MODE,
                        CopyAttributes.ColorMode.DEFAULT.name)
                        ?: CopyAttributes.ColorMode.DEFAULT.name)
        Log.i(TAG, "Selected Color Mode:" + colorMode.name)

        val scanDuplex = CopyAttributes.Duplex.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_DUPLEX_MODE,
                        CopyAttributes.Duplex.DEFAULT.name) ?: CopyAttributes.Duplex.DEFAULT.name)
        Log.i(TAG, "Selected Duplex Mode:" + scanDuplex.name)

        val orientation = CopyAttributes.Orientation.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_ORIENTATION,
                        CopyAttributes.Orientation.DEFAULT.name)
                        ?: CopyAttributes.Orientation.DEFAULT.name)
        Log.i(TAG, "Selected Orientation:" + orientation.name)

        val scanSize = CopyAttributes.ScanSize.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_SIZE,
                        CopyAttributes.ScanSize.DEFAULT.name)
                        ?: CopyAttributes.ScanSize.DEFAULT.name)
        Log.i(TAG, "Selected Scan Size:" + scanSize.name)

        val scanSource = CopyAttributes.ScanSource.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_SOURCE,
                        CopyAttributes.ScanSource.DEFAULT.name)
                        ?: CopyAttributes.ScanSource.DEFAULT.name)
        Log.i(TAG, "Selected Scan Source:" + scanSource.name)

        val copyPreview = CopyAttributes.CopyPreview.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COPY_PREVIEW,
                        CopyAttributes.CopyPreview.DEFAULT.name)
                        ?: CopyAttributes.CopyPreview.DEFAULT.name)
        Log.i(TAG, "Selected Copy Preview:" + copyPreview.name)

        val scanCustomLength = java.lang.Float.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_CUSTOM_LENGTH, "0") ?: "0")
        Log.i(TAG, "Selected Scan Custom Length:$scanCustomLength")

        val scanCustomWidth = java.lang.Float.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCAN_CUSTOM_WIDTH, "0") ?: "0")
        Log.i(TAG, "Selected Scan Custom Width:$scanCustomWidth")

        val backgroundCleanup = CopyAttributes.BackgroundCleanup.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_BACKGROUND_CLEANUP,
                        CopyAttributes.BackgroundCleanup.DEFAULT.name)
                        ?: CopyAttributes.BackgroundCleanup.DEFAULT.name)
        Log.i(TAG, "Selected BackgroundCleanup:" + backgroundCleanup.name)

        val contrastAdjustment = CopyAttributes.ContrastAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_CONTRAST_ADJUSTMENT,
                        CopyAttributes.ContrastAdjustment.DEFAULT.name)
                        ?: CopyAttributes.ContrastAdjustment.DEFAULT.name)
        Log.i(TAG, "Selected ContrastAdjustment:" + contrastAdjustment.name)

        val darknessAdjustment = CopyAttributes.DarknessAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_DARKNESS_ADJUSTMENT,
                        CopyAttributes.DarknessAdjustment.DEFAULT.name)
                        ?: CopyAttributes.DarknessAdjustment.DEFAULT.name)
        Log.i(TAG, "Selected DarknessAdjustment:" + darknessAdjustment.name)

        val sharpnessAdjustment = CopyAttributes.SharpnessAdjustment.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SHARPNESS_ADJUSTMENT,
                        CopyAttributes.SharpnessAdjustment.DEFAULT.name)
                        ?: CopyAttributes.SharpnessAdjustment.DEFAULT.name)
        Log.i(TAG, "Selected SharpnessAdjustment:" + sharpnessAdjustment.name)

        val printDuplex = CopyAttributes.Duplex.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_DUPLEX_MODE,
                        CopyAttributes.Duplex.DEFAULT.name) ?: CopyAttributes.Duplex.DEFAULT.name)
        Log.i(TAG, "Selected Print Duplex Mode:" + printDuplex.name)

        val printSize = CopyAttributes.PaperSize.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_SIZE,
                        CopyAttributes.PaperSize.DEFAULT.name)
                        ?: CopyAttributes.PaperSize.DEFAULT.name)
        Log.i(TAG, "Selected Print Size:" + printSize.name)

        val printCustomLength = java.lang.Float.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_CUSTOM_LENGTH, "0") ?: "0")
        Log.i(TAG, "Selected Print Custom Length:$printCustomLength")

        val printCustomWidth = java.lang.Float.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PRINT_CUSTOM_WIDTH, "0") ?: "0")
        Log.i(TAG, "Selected Print Custom Width:$printCustomWidth")

        val copies = Integer.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COPIES, "1") ?: "1")
        Log.i(TAG, "Selected Copies:$copies")

        val collateMode = CopyAttributes.CollateMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_COLLATE_MODE,
                        CopyAttributes.CollateMode.DEFAULT.name)
                        ?: CopyAttributes.CollateMode.DEFAULT.name)
        Log.i(TAG, "Selected Collate Mode:" + collateMode.name)

        val paperSource = CopyAttributes.PaperSource.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PAPER_SOURCE,
                        CopyAttributes.PaperSource.DEFAULT.name)
                        ?: CopyAttributes.PaperSource.DEFAULT.name)
        Log.i(TAG, "Selected Paper Source:" + paperSource.name)

        val paperType = CopyAttributes.PaperType.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PAPER_TYPE,
                        CopyAttributes.PaperType.DEFAULT.name)
                        ?: CopyAttributes.PaperType.DEFAULT.name)
        Log.i(TAG, "Selected Paper Type:" + paperType.name)

        val scaleMode = CopyAttributes.ScaleMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCALE_MODE,
                        CopyAttributes.ScaleMode.DEFAULT.name)
                        ?: CopyAttributes.ScaleMode.DEFAULT.name)
        Log.i(TAG, "Selected Scale Mode:" + scaleMode.name)

        val textGraphicsOptimization = CopyAttributes.TextGraphicsOptimization.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_TEXT_GRAPHICS_OPTIMIZATION,
                        CopyAttributes.TextGraphicsOptimization.DEFAULT.name)
                        ?: CopyAttributes.TextGraphicsOptimization.DEFAULT.name)
        Log.i(TAG, "Selected Text/Graphics Optimization:" + textGraphicsOptimization.name)

        val scalePercent = Integer.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_SCALE_PERCENT, "100") ?: "100")
        Log.i(TAG, "Selected Scale Percent:$scalePercent")

        val jobAssemblyMode = CopyAttributes.JobAssemblyMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_JOB_ASSEMBLY_MODE,
                        CopyAttributes.JobAssemblyMode.DEFAULT.name)
                        ?: CopyAttributes.JobAssemblyMode.DEFAULT.name)
        Log.i(TAG, "Selected Job Assembly Mode:" + jobAssemblyMode.name)

        val jobExecutionMode = CopyAttributes.JobExecutionMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_JOB_EXECUTION_MODE,
                        CopyAttributes.JobExecutionMode.NORMAL.name)
                        ?: CopyAttributes.JobExecutionMode.NORMAL.name)
        Log.i(TAG, "Selected Job Execution Mode:" + jobExecutionMode.name)

        val numberUpMode = CopyAttributes.NumberUpMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_NUMBER_UP_MODE,
                        CopyAttributes.NumberUpMode.DEFAULT.name)
                        ?: CopyAttributes.NumberUpMode.DEFAULT.name)
        Log.i(TAG, "Selected Number Up Mode:" + numberUpMode.name)

        val numberUpDirection = CopyAttributes.NumberUpDirection.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_NUMBER_UP_DIRECTION,
                        CopyAttributes.NumberUpDirection.DEFAULT.name)
                        ?: CopyAttributes.NumberUpDirection.DEFAULT.name)
        Log.i(TAG, "Selected Number Up Direction:" + numberUpDirection.name)

        val outputBin = CopyAttributes.OutputBin.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_OUTPUT_BIN,
                        CopyAttributes.OutputBin.DEFAULT.name)
                        ?: CopyAttributes.OutputBin.DEFAULT.name)
        Log.i(TAG, "Selected Output Bin:" + outputBin.name)

        val progressDialogMode = CopyAttributes.ProgressDialogMode.valueOf(
                mPrefs.getString(CopyConfigureFragment.PREF_PROGRESS_DIALOG_MODE,
                        CopyAttributes.ProgressDialogMode.DEFAULT.name)
                        ?: CopyAttributes.ProgressDialogMode.DEFAULT.name)
        Log.i(TAG, "Selected Progress Dialog Mode:" + progressDialogMode.name)

        val eraseMarginUnit = CopyAttributes.EraseMarginUnit.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_ERASE_MARGIN_UNIT,
                CopyAttributes.EraseMarginUnit.DEFAULT.name)
                ?: CopyAttributes.EraseMarginUnit.DEFAULT.name)
        Log.d(TAG, "Selected eraseMarginUnit:" + eraseMarginUnit.name)

        val backMargin = Margins(mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_LEFT_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_TOP_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_RIGHT_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_BACK_BOTTOM_MARGIN, 0.0f))
        Log.d(TAG, "Selected backeraseMargin:" + backMargin.topMargin +","+backMargin.bottomMargin+","+backMargin.leftMargin+","+backMargin.rightMargin)

        val frontMargin = Margins(mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_LEFT_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_TOP_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_RIGHT_MARGIN, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_ERASE_FRONT_BOTTOM_MARGIN, 0.0f))

        val captureMode = CopyAttributes.CaptureMode.valueOf(
                     mPrefs.getString(CopyConfigureFragment.PREF_CAPTURE_MODE,
                         CopyAttributes.CaptureMode.DEFAULT.name)
                         ?: CopyAttributes.CaptureMode.DEFAULT.name)
        Log.i(TAG, "Selected Capture Mode:" + captureMode.name)

        val imageShiftReduceToFit = CopyAttributes.ImageShiftReduceToFit.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_IMAGE_SHIFT_REDUCE_TO_FIT,
                CopyAttributes.ImageShiftReduceToFit.DEFAULT.name)
                ?: CopyAttributes.ImageShiftReduceToFit.DEFAULT.name)
        Log.i(TAG, "Selected image shift reduce to fit:" + imageShiftReduceToFit.name)

        val imageShiftUnits = CopyAttributes.ImageShiftUnits.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_IMAGE_SHIFT_UNITS,
                CopyAttributes.ImageShiftUnits.DEFAULT.name)
                ?: CopyAttributes.ImageShiftUnits.DEFAULT.name)
        Log.i(TAG, "Selected image shift units:" + imageShiftUnits.name)

        val imageShiftFront = Shifts(mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_X_FRONT, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_Y_FRONT, 0.0f))
        Log.i(TAG, "Selected imageShiftFront x shift :${imageShiftFront.xShift}"
                + " imageShiftFront y shift :${imageShiftFront.yShift}")

        val imageShiftBack = Shifts(mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_X_BACK, 0.0f),
            mPrefs.getFloat(CopyConfigureFragment.PREF_IMAGE_SHIFT_Y_BACK, 0.0f))
        Log.i(TAG, "Selected imageShiftBack x shift :${imageShiftBack.xShift}"
                + " imageShiftBack y shift :${imageShiftBack.yShift}")

        val bookletBordersEachPage = CopyAttributes.BookletBordersEachPage.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_BORDERS_EACH_PAGE,
                CopyAttributes.BookletBordersEachPage.DEFAULT.name)
                ?: CopyAttributes.BookletBordersEachPage.DEFAULT.name)
        Log.i(TAG, "Selected bookletBordersEachPage:" + bookletBordersEachPage.name)

        val bookletFinishingOption = CopyAttributes.BookletFinishingOption.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_FINISHING_OPTION,
                CopyAttributes.BookletFinishingOption.DEFAULT.name)
                ?: CopyAttributes.BookletFinishingOption.DEFAULT.name)
        Log.i(TAG, "Selected bookletFinishingOption:" + bookletFinishingOption.name)

        val bookletFormat = CopyAttributes.BookletFormat.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_BOOKLET_FORMAT,
                CopyAttributes.BookletFormat.DEFAULT.name)
                ?: CopyAttributes.BookletFormat.DEFAULT.name)
        Log.i(TAG, "Selected bookletFormat:" + bookletFormat.name)

        val foldMode = CopyAttributes.FoldMode.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_FOLD_MODE,
                CopyAttributes.FoldMode.NONE.name)
                ?: CopyAttributes.FoldMode.NONE.name)
        Log.i(TAG, "Selected Fold Mode:" + foldMode.name)

        val stapleOption = CopyAttributes.StapleOption.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_STAPLE_MODE,
                CopyAttributes.StapleOption.NONE.name)
                ?: CopyAttributes.StapleOption.NONE.name)
        Log.i(TAG, "Selected staple Mode:" + stapleOption.name)

        val punchMode = CopyAttributes.PunchMode.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_PUNCH_MODE,
                CopyAttributes.PunchMode.NONE.name)
                ?: CopyAttributes.PunchMode.NONE.name)
        Log.i(TAG, "Selected punch Mode:" + punchMode.name)

        val watermarkTextSize = Integer.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT_SIZE, "0") ?: "0")
        Log.i(TAG, "Selected watermarktextsize:$watermarkTextSize")

        val watermarkTransparency = Integer.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TRANSPARENCY, "0") ?: "0")
        Log.i(TAG, "Selected watermarktransparency:$watermarkTransparency")

        val watermarkTextColor = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT_COLOR, null)
        Log.i(TAG, "watermarkTextColor is:$watermarkTextColor")

        val watermarkFont = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_FONT, null)
        Log.i(TAG, "watermarkFont is:$watermarkFont")

        val watermarkBackgroundColor = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_BACKGROUND_COLOR, null)
        Log.i(TAG, "watermarkBackgroundColor is:$watermarkBackgroundColor")

        val watermarkOnlyFirstPage = CopyAttributes.WatermarkOnlyFirstPage.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_ONLY_FIRST_PAGE,
                CopyAttributes.WatermarkOnlyFirstPage.DEFAULT.name)
                ?: CopyAttributes.WatermarkOnlyFirstPage.DEFAULT.name)
        Log.i(TAG, "Selected OnlyFirst:" + watermarkOnlyFirstPage.name)


        val watermarkDarkness = Integer.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_DARKNESS, "0") ?: "0")
        Log.i(TAG, "Selected watermarkDarkness:$watermarkDarkness")

        val watermarkText = mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TEXT, null)
        Log.i(TAG, "watermarkText is:$watermarkText")

        val watermarkRotate45 = CopyAttributes.WatermarkRotate45.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_ROTATION,
                CopyAttributes.WatermarkRotate45.DEFAULT.name)
                ?: CopyAttributes.WatermarkRotate45.DEFAULT.name)
        Log.i(TAG, "Selected rotation:" + watermarkRotate45.name)

        val watermarkType = CopyAttributes.WatermarkType.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_TYPE,
                CopyAttributes.WatermarkType.DEFAULT.name)
                ?: CopyAttributes.WatermarkType.DEFAULT.name)
        Log.i(TAG, "Selected Type:" + watermarkType.name)

        val watermarkBackgroundPattern = CopyAttributes.WatermarkBackgroundPattern.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_BACKGROUND_PATTERN,
                CopyAttributes.WatermarkBackgroundPattern.DEFAULT.name)
                ?: CopyAttributes.WatermarkBackgroundPattern.DEFAULT.name)
        Log.i(TAG, "Selected WatermarkBackgroundPattern:" + watermarkBackgroundPattern.name)

        val watermarkMessageType = CopyAttributes.WatermarkMessageType.valueOf(
            mPrefs.getString(CopyConfigureFragment.PREF_WATERMARK_MESSAGE_TYPE,
                CopyAttributes.WatermarkMessageType.NONE.name)
                ?: CopyAttributes.WatermarkMessageType.NONE.name)
        Log.i(TAG, "Selected watermarkMessageType:" + watermarkMessageType.name)

        val gson = Gson()

        return when (jobExecutionMode) {

            CopyAttributes.JobExecutionMode.NORMAL -> {
                val copyBuilder = CopyBuilder()
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
                for (stampPosition in CopyAttributes.StampPosition.values()) {
                    val jsonStampOption = mPrefs.getString(CopyConfigureFragment.PREF_STAMP + stampPosition.name, "")
                    if ("" == jsonStampOption) {
                        continue
                    }
                    val stampOption = gson.fromJson(jsonStampOption, StampOption::class.java)
                    copyBuilder.setStampOption(stampPosition, stampOption)
                }

                return copyBuilder.build(capabilities);
            }

            CopyAttributes.JobExecutionMode.STORE -> {
                val storeJobName = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_NAME, "")
                        ?: ""
                Log.i(TAG, "Selected Store Job Name:$storeJobName")
                val storeJobFolderName = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_FOLDER_NAME, "")
                        ?: ""
                Log.i(TAG, "Selected Store Job Folder Mode:$storeJobFolderName")
                val deleteOnPower = CopyAttributes.RetentionMode.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_DELETE_ON_POWER,
                                CopyAttributes.RetentionMode.DEFAULT.name)
                                ?: CopyAttributes.RetentionMode.DEFAULT.name)
                Log.i(TAG, "Selected Store Job Delete On Power:" + deleteOnPower.name)
                val deleteOnRelease = CopyAttributes.RetentionMode.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_DELETE_ON_RELEASE,
                                CopyAttributes.RetentionMode.DEFAULT.name)
                                ?: CopyAttributes.RetentionMode.DEFAULT.name)
                Log.i(TAG, "Selected Store Job Delete On Release:" + deleteOnRelease.name)
                val storeJobPasswordType = JobCredentialsAttributes.PasswordType.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_PASSWORD_TYPE,
                                JobCredentialsAttributes.PasswordType.NONE.name)
                                ?: JobCredentialsAttributes.PasswordType.NONE.name)
                val storeJobPassword = mPrefs.getString(CopyConfigureFragment.PREF_STORE_JOB_PASSWORD, "")
                        ?: ""
                val storeCopyBuilder = StoreCopyBuilder()
                if (storeJobPasswordType != JobCredentialsAttributes.PasswordType.NONE) {
                    val jobCredentialsAttributes = JobCredentialsAttributes.Builder()
                            .setPasswordType(storeJobPasswordType)
                            .setPassword(storeJobPassword)
                            .build()
                    storeCopyBuilder.setStoreJobCredentials(jobCredentialsAttributes)
                }

                for (stampPosition in CopyAttributes.StampPosition.values()) {
                    val jsonStampOption =
                        mPrefs.getString(CopyConfigureFragment.PREF_STAMP + stampPosition.name, "")
                    if ("" == jsonStampOption) {
                        continue
                    }
                    val stampOption = gson.fromJson(jsonStampOption, StampOption::class.java)
                    storeCopyBuilder.setStampOption(stampPosition, stampOption)
                }

                storeCopyBuilder
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
                        .build(capabilities)
            }
            else -> throw IllegalStateException("Unsupported job execution mode")
        }
    }

    companion object {
        private const val TAG = MainActivity.TAG
    }

}