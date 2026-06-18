// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task

import android.content.SharedPreferences
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.preference.PreferenceManager
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.scanner.EmailAttributes
import com.hp.workpath.api.scanner.FileOptionsAttributes
import com.hp.workpath.api.scanner.Margins
import com.hp.workpath.api.scanner.NetworkCredentialsAttributes
import com.hp.workpath.api.scanner.ScanAttributes
import com.hp.workpath.api.scanner.ScanAttributesCaps
import com.hp.workpath.api.scanner.ScanletAttributes
import com.hp.workpath.api.scanner.ScannerService
import com.hp.workpath.api.scanner.SmtpAttributes
import com.hp.workpath.sample.scansample.Logger
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R
import com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.Companion.DEFAULT_PORT
import com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.Companion.DEFAULT_TIMEOUT
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class ScanToDestinationTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private lateinit var mErrorMsg: String
    private var mThrowable: Throwable? = null
    private var rid: String? = null

    suspend fun execute() {
        mContextRef.get()?.run {
            try {
                val settingsUi = mPrefs.getBoolean(ScanConfigureFragment.PREF_SETTINGS_UI, false)
                val allowMultipleScan = mPrefs.getBoolean(ScanConfigureFragment.PREF_ALLOW_MULTIPLE_SCAN, false)
                Log.d(TAG, "Settings UI:$settingsUi")
                Log.d(TAG, "Allow Multiple Scan:$allowMultipleScan")

                var mAttributes: ScanAttributes? = null

                if (!settingsUi) {
                    // Obtain Caps to build Scan Attributes
                    val caps = capabilities
                    if (caps == null) {
                        mErrorMsg = getString(R.string.capabilities_not_loaded)
                        onPostExecute(rid)
                        return
                    }

                    val dest = mPrefs.getString(ScanConfigureFragment.PREF_DESTINATION,
                            ScanAttributes.Destination.ME.name)?.let { ScanAttributes.Destination.valueOf(it) }
                            ?: run { ScanAttributes.Destination.ME }
                    mAttributes = buildScanAttributes(dest, caps)
                }

                val taskAttribs = ScanletAttributes.Builder()
                        .setShowSettingsUi(settingsUi)
                        .setAllowMultipleScan(allowMultipleScan)
                        .build()

                // Submit the job
                rid = ScannerService.submit(this, mAttributes, taskAttribs)
                Log.i(TAG, "Job submitted with rid = $rid")
            } catch (cee: CapabilitiesExceededException) {
                mErrorMsg = "CapabilitiesExceededException"
                mThrowable = cee
            } catch (iae: IllegalArgumentException) {
                mErrorMsg = "IllegalArgumentException"
                mThrowable = iae
            } catch (t: Throwable) {
                mErrorMsg = "Unknown Throwable"
                mThrowable = t
            }
            onPostExecute(rid)
        }
    }

    private suspend fun onPostExecute(rid: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (rid != null && !TextUtils.isEmpty(rid)) {
                    setRid(rid)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "$mErrorMsg ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, mErrorMsg)
                }
            }
        }
    }

    @Throws(CapabilitiesExceededException::class)
    private fun buildScanAttributes(destination: ScanAttributes.Destination, capabilities: ScanAttributesCaps): ScanAttributes {
        val df = ScanAttributes.DocumentFormat.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DOC_FORMAT,
                        ScanAttributes.DocumentFormat.DEFAULT.name)
                        ?: ScanAttributes.DocumentFormat.DEFAULT.name)
        Log.d(TAG, "Selected Doc Format:$df")

        val cm = ScanAttributes.ColorMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_COLOR_MODE,
                        ScanAttributes.ColorMode.DEFAULT.name)
                        ?: ScanAttributes.ColorMode.DEFAULT.name)
        Log.d(TAG, "Selected Color Mode:${cm.name}")

        val du = ScanAttributes.Duplex.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DUPLEX_MODE,
                        ScanAttributes.Duplex.DEFAULT.name) ?: ScanAttributes.Duplex.DEFAULT.name)
        Log.d(TAG, "Selected Duplex Mode:${du.name}")

        val orientation = ScanAttributes.Orientation.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ORIENTATION,
                        ScanAttributes.Orientation.DEFAULT.name)
                        ?: ScanAttributes.Orientation.DEFAULT.name)
        Log.d(TAG, "Selected Orientation:${orientation.name}")

        val ss = ScanAttributes.ScanSize.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ORG_SIZE,
                        ScanAttributes.ScanSize.DEFAULT.name)
                        ?: ScanAttributes.ScanSize.DEFAULT.name)
        Log.d(TAG, "Selected Scan Size:${ss.name}")

        val customLength = java.lang.Float.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_CUSTOM_LENGTH, "0")
                ?: "0")
        Log.d(TAG, "Selected Custom Length:$customLength")

        val customWidth = java.lang.Float.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_CUSTOM_WIDTH, "0")
                ?: "0")
        Log.d(TAG, "Selected Custom Width:$customWidth")

        val resolution = ScanAttributes.Resolution.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_RESOLUTION_TYPE,
                        ScanAttributes.Resolution.DEFAULT.name)
                        ?: ScanAttributes.Resolution.DEFAULT.name)
        Log.d(TAG, "Selected Resolution:${resolution.name}")

        val backgroundCleanup = ScanAttributes.BackgroundCleanup.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_BACKGROUND_CLEANUP,
                        ScanAttributes.BackgroundCleanup.DEFAULT.name)
                        ?: ScanAttributes.BackgroundCleanup.DEFAULT.name)
        Log.d(TAG, "Selected BackgroundCleanup:${backgroundCleanup.name}")

        val contrastAdjustment = ScanAttributes.ContrastAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CONTRAST_ADJUSTMENT,
                        ScanAttributes.ContrastAdjustment.DEFAULT.name)
                        ?: ScanAttributes.ContrastAdjustment.DEFAULT.name)
        Log.d(TAG, "Selected ContrastAdjustment:${contrastAdjustment.name}")

        val darknessAdjustment = ScanAttributes.DarknessAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DARKNESS_ADJUSTMENT,
                        ScanAttributes.DarknessAdjustment.DEFAULT.name)
                        ?: ScanAttributes.DarknessAdjustment.DEFAULT.name)
        Log.d(TAG, "Selected DarknessAdjustment:${darknessAdjustment.name}")

        val blankImageRemovalMode = ScanAttributes.BlankImageRemovalMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_BLANK_IMAGE_REMOVAL_MODE,
                        ScanAttributes.BlankImageRemovalMode.DEFAULT.name)
                        ?: ScanAttributes.BlankImageRemovalMode.DEFAULT.name)
        Log.d(TAG, "Selected BlankImageRemovalMode:${blankImageRemovalMode.name}")

        val colorDropoutMode = ScanAttributes.ColorDropoutMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_COLOR_DROPOUT_MODE,
                        ScanAttributes.ColorDropoutMode.DEFAULT.name)
                        ?: ScanAttributes.ColorDropoutMode.DEFAULT.name)
        Log.d(TAG, "Selected ColorDropoutMode:${colorDropoutMode.name}")

        val cropMode = ScanAttributes.CropMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CROP_MODE,
                        ScanAttributes.CropMode.DEFAULT.name)
                        ?: ScanAttributes.CropMode.DEFAULT.name)
        Log.d(TAG, "Selected CropMode:${cropMode.name}")

        val progressDialogMode = ScanAttributes.ProgressDialogMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_PROGRESS_DIALOG_MODE,
                        ScanAttributes.ProgressDialogMode.DEFAULT.name)
                        ?: ScanAttributes.ProgressDialogMode.DEFAULT.name)
        Log.d(TAG, "Selected ProgressDialogMode:${progressDialogMode.name}")

        val outputQuality = ScanAttributes.OutputQuality.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_OUTPUT_QUALITY,
                        ScanAttributes.OutputQuality.DEFAULT.name)
                        ?: ScanAttributes.OutputQuality.DEFAULT.name)
        Log.d(TAG, "Selected OutputQuality:${outputQuality.name}")

        val transmissionMode = ScanAttributes.TransmissionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TRANSMISSION_MODE,
                        ScanAttributes.TransmissionMode.DEFAULT.name)
                        ?: ScanAttributes.TransmissionMode.DEFAULT.name)
        Log.d(TAG, "Selected TransmissionMode:${transmissionMode.name}")

        val scanPreview = ScanAttributes.ScanPreview.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SCAN_PREVIEW,
                        ScanAttributes.ScanPreview.DEFAULT.name)
                        ?: ScanAttributes.ScanPreview.DEFAULT.name)
        Log.d(TAG, "Selected ScanPreview:${scanPreview.name}")

        val jobAssemblyMode = ScanAttributes.JobAssemblyMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_JOB_ASSEMBLY_MODE,
                        ScanAttributes.JobAssemblyMode.DEFAULT.name)
                        ?: ScanAttributes.JobAssemblyMode.DEFAULT.name)
        Log.d(TAG, "Selected JobAssemblyMode:${jobAssemblyMode.name}")

        val sharpnessAdjustment = ScanAttributes.SharpnessAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SHARPNESS_ADJUSTMENT,
                        ScanAttributes.SharpnessAdjustment.DEFAULT.name)
                        ?: ScanAttributes.SharpnessAdjustment.DEFAULT.name)
        Log.d(TAG, "Selected SharpnessAdjustment:${sharpnessAdjustment.name}")

        val mediaWeightAdjustment = ScanAttributes.MediaWeightAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MEDIA_WEIGHT_ADJUSTMENT,
                        ScanAttributes.MediaWeightAdjustment.DEFAULT.name)
                        ?: ScanAttributes.MediaWeightAdjustment.DEFAULT.name)
        Log.d(TAG, "Selected MediaWeightAdjustment:${mediaWeightAdjustment.name}")

        val textPhotoOptimization = ScanAttributes.TextPhotoOptimization.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TEXT_PHOTO_OPTIMIZATION,
                        ScanAttributes.TextPhotoOptimization.DEFAULT.name)
                        ?: ScanAttributes.TextPhotoOptimization.DEFAULT.name)
        Log.d(TAG, "Selected TextPhotoOptimization:${textPhotoOptimization.name}")

        val mediaSource = ScanAttributes.MediaSource.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MEDIA_SOURCE,
                        ScanAttributes.MediaSource.DEFAULT.name)
                        ?: ScanAttributes.MediaSource.DEFAULT.name)
        Log.d(TAG, "Selected MediaSource:${mediaSource.name}")

        val misfeedDetectionMode = ScanAttributes.MisfeedDetectionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MISFEED_DETECTION_MODE,
                        ScanAttributes.MisfeedDetectionMode.DEFAULT.name)
                        ?: ScanAttributes.MisfeedDetectionMode.DEFAULT.name)
        Log.d(TAG, "Selected MisfeedDetectionMode:${misfeedDetectionMode.name}")

        val splitAttachmentByPage = ScanAttributes.SplitAttachmentByPage.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SPLIT_ATTACHMENT_BY_PAGE,
                        ScanAttributes.SplitAttachmentByPage.DEFAULT.name)
                        ?: ScanAttributes.SplitAttachmentByPage.DEFAULT.name)
        Log.d(TAG, "Selected splitAttachmentByPage:" + splitAttachmentByPage.name)

        val maxPagesPerAttachment = Integer.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_MAX_PAGES_PER_ATTACHMENT, "0")
                ?: "0")

        val eraseMarginUnit = ScanAttributes.EraseMarginUnit.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ERASE_MARGIN_UNIT,
                        ScanAttributes.EraseMarginUnit.DEFAULT.name)
                        ?: ScanAttributes.EraseMarginUnit.DEFAULT.name)
        Log.d(TAG, "Selected eraseMarginUnit:" + eraseMarginUnit.name)

        val backMargin = Margins(mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_BOTTOM_MARGIN, 0.0f))

        val frontMargin = Margins(mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_BOTTOM_MARGIN, 0.0f))

        val captureMode = ScanAttributes.CaptureMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CAPTURE_MODE,
                        ScanAttributes.CaptureMode.DEFAULT.name)
                        ?: ScanAttributes.CaptureMode.DEFAULT.name)
        Log.d(TAG, "Selected captureMode:" + captureMode.name)

        val automaticToneMode = ScanAttributes.AutomaticToneMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_AUTOMATIC_TONE_MODE,
                        ScanAttributes.AutomaticToneMode.DEFAULT.name)
                        ?: ScanAttributes.AutomaticToneMode.DEFAULT.name)
        Log.d(TAG, "Selected automaticToneMode:" + automaticToneMode.name)

        val automaticStraightenMode = ScanAttributes.AutomaticStraightenMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_AUTOMATIC_STRAIGHTEN_MODE,
                        ScanAttributes.AutomaticStraightenMode.DEFAULT.name)
                        ?: ScanAttributes.AutomaticStraightenMode.DEFAULT.name)
        Log.d(TAG, "Selected automaticStraightenMode:" + automaticStraightenMode.name)


        val pdfEncryption = mPrefs.getString(ScanConfigureFragment.PREF_PDF_PASSWORD, null)

        val ocrLanguage = FileOptionsAttributes.OcrLanguage.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_OCR_LANGUAGE,
                        FileOptionsAttributes.OcrLanguage.DEFAULT.name)
                        ?: FileOptionsAttributes.OcrLanguage.DEFAULT.name)

        val compressionMode = FileOptionsAttributes.PdfCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_PDF_COMPRESSION,
                        FileOptionsAttributes.PdfCompressionMode.DEFAULT.name)
                        ?: FileOptionsAttributes.PdfCompressionMode.DEFAULT.name)

        val tiffCompressionMode = FileOptionsAttributes.TiffCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TIFF_COMPRESSION,
                        FileOptionsAttributes.TiffCompressionMode.DEFAULT.name)
                        ?: FileOptionsAttributes.TiffCompressionMode.DEFAULT.name)

        val xpsCompressionMode = FileOptionsAttributes.XpsCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_XPS_COMPRESSION,
                        FileOptionsAttributes.XpsCompressionMode.DEFAULT.name)
                        ?: FileOptionsAttributes.XpsCompressionMode.DEFAULT.name)

        val filename = mPrefs.getString(ScanConfigureFragment.PREF_FILE_NAME, null)

        val fileOptionsAttrCaps = mContextRef.get()?.fileOptionsAttributesCaps
        Log.d(TAG, "FileOptionsAttributesCaps=" + Logger.build(fileOptionsAttrCaps, cm, df))

        val fileOptionsAttributes = fileOptionsAttrCaps?.run {
            FileOptionsAttributes.Builder().apply {
                setPdfEncryptionPassword(pdfEncryption)
                setOcrLanguage(ocrLanguage)
                setPdfCompressionMode(compressionMode)
                setTiffCompressionMode(tiffCompressionMode)
                setXpsCompressionMode(xpsCompressionMode)
            }.build(this)
        }

        when (destination) {
            ScanAttributes.Destination.ME
            -> return ScanAttributes.MeBuilder().apply {
                setColorMode(cm)
                setDuplex(du)
                setDocumentFormat(df)
                setScanSize(ss)
                setCustomLength(customLength)
                setCustomWidth(customWidth)
                setResolution(resolution)
                setOrientation(orientation)
                setScanPreview(scanPreview)
                setBackgroundCleanup(backgroundCleanup)
                setContrastAdjustment(contrastAdjustment)
                setDarknessAdjustment(darknessAdjustment)
                setBlankImageRemovalMode(blankImageRemovalMode)
                setColorDropoutMode(colorDropoutMode)
                setCropMode(cropMode)
                setProgressDialogMode(progressDialogMode)
                setOutputQuality(outputQuality)
                setTransmissionMode(transmissionMode)
                setJobAssemblyMode(jobAssemblyMode)
                setSharpnessAdjustment(sharpnessAdjustment)
                setMediaWeightAdjustment(mediaWeightAdjustment)
                setTextPhotoOptimization(textPhotoOptimization)
                setMediaSource(mediaSource)
                setMisfeedDetectionMode(misfeedDetectionMode)
                setSplitAttachmentByPage(splitAttachmentByPage)
                setMaxPagesPerAttachment(maxPagesPerAttachment)
                setEraseMarginUnit(eraseMarginUnit)
                setEraseBackMargin(backMargin)
                setEraseFrontMargin(frontMargin)
                setCaptureMode(captureMode)
                setAutomaticToneMode(automaticToneMode)
                setAutomaticStraightenMode(automaticStraightenMode)
                if (fileOptionsAttributes != null) {
                    setFileOptionsAttributes(fileOptionsAttributes)
                }
                setFileName(filename)
            }.build(capabilities)

            ScanAttributes.Destination.HTTP -> {
                val httpUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP, ""))
                val mUriHttpUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP_USERNAME, "")
                val mUriHttpPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP_PASSWORD, "")

                var httpCredentialsAttributes: NetworkCredentialsAttributes? = null
                if (!TextUtils.isEmpty(mUriHttpUsername) && !TextUtils.isEmpty(mUriHttpPassword)) {
                    httpCredentialsAttributes = NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriHttpUsername)
                            .setPassword(mUriHttpPassword)
                            .build()
                }
                return ScanAttributes.HttpBuilder(httpUri).apply {
                    setColorMode(cm)
                    setDuplex(du)
                    setDocumentFormat(df)
                    setScanSize(ss)
                    setCustomLength(customLength)
                    setCustomWidth(customWidth)
                    setResolution(resolution)
                    setOrientation(orientation)
                    setScanPreview(scanPreview)
                    setBackgroundCleanup(backgroundCleanup)
                    setContrastAdjustment(contrastAdjustment)
                    setDarknessAdjustment(darknessAdjustment)
                    setBlankImageRemovalMode(blankImageRemovalMode)
                    setColorDropoutMode(colorDropoutMode)
                    setCropMode(cropMode)
                    setProgressDialogMode(progressDialogMode)
                    setOutputQuality(outputQuality)
                    setTransmissionMode(transmissionMode)
                    setJobAssemblyMode(jobAssemblyMode)
                    setSharpnessAdjustment(sharpnessAdjustment)
                    setMediaWeightAdjustment(mediaWeightAdjustment)
                    setTextPhotoOptimization(textPhotoOptimization)
                    setMediaSource(mediaSource)
                    setMisfeedDetectionMode(misfeedDetectionMode)
                    setSplitAttachmentByPage(splitAttachmentByPage)
                    setMaxPagesPerAttachment(maxPagesPerAttachment)
                    setEraseMarginUnit(eraseMarginUnit)
                    setEraseBackMargin(backMargin)
                    setEraseFrontMargin(frontMargin)
                    setCaptureMode(captureMode)
                    setAutomaticToneMode(automaticToneMode)
                    setAutomaticStraightenMode(automaticStraightenMode)
                    if (fileOptionsAttributes != null) {
                        setFileOptionsAttributes(fileOptionsAttributes)
                    }
                    setFileName(filename)
                    setNetworkCredentials(httpCredentialsAttributes)
                }.build(capabilities)
            }

            ScanAttributes.Destination.FTP -> {
                val ftpUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP, ""))
                val mUriFtpUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP_USERNAME, "")
                val mUriFtpPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP_PASSWORD, "")

                var ftpCredentialsAttributes: NetworkCredentialsAttributes? = null
                if (!TextUtils.isEmpty(mUriFtpUsername) && !TextUtils.isEmpty(mUriFtpPassword)) {
                    ftpCredentialsAttributes = NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriFtpUsername)
                            .setPassword(mUriFtpPassword)
                            .build()
                }
                return ScanAttributes.FtpBuilder(ftpUri).apply {
                    setColorMode(cm)
                    setDuplex(du)
                    setDocumentFormat(df)
                    setScanSize(ss)
                    setCustomLength(customLength)
                    setCustomWidth(customWidth)
                    setResolution(resolution)
                    setOrientation(orientation)
                    setScanPreview(scanPreview)
                    setBackgroundCleanup(backgroundCleanup)
                    setContrastAdjustment(contrastAdjustment)
                    setDarknessAdjustment(darknessAdjustment)
                    setBlankImageRemovalMode(blankImageRemovalMode)
                    setColorDropoutMode(colorDropoutMode)
                    setCropMode(cropMode)
                    setProgressDialogMode(progressDialogMode)
                    setOutputQuality(outputQuality)
                    setTransmissionMode(transmissionMode)
                    setJobAssemblyMode(jobAssemblyMode)
                    setSharpnessAdjustment(sharpnessAdjustment)
                    setMediaWeightAdjustment(mediaWeightAdjustment)
                    setTextPhotoOptimization(textPhotoOptimization)
                    setMediaSource(mediaSource)
                    setMisfeedDetectionMode(misfeedDetectionMode)
                    setSplitAttachmentByPage(splitAttachmentByPage)
                    setMaxPagesPerAttachment(maxPagesPerAttachment)
                    setEraseMarginUnit(eraseMarginUnit)
                    setEraseBackMargin(backMargin)
                    setEraseFrontMargin(frontMargin)
                    setCaptureMode(captureMode)
                    setAutomaticToneMode(automaticToneMode)
                    setAutomaticStraightenMode(automaticStraightenMode)
                    if (fileOptionsAttributes != null) {
                        setFileOptionsAttributes(fileOptionsAttributes)
                    }
                    setFileName(filename)
                    setNetworkCredentials(ftpCredentialsAttributes)
                }.build(capabilities)
            }

            ScanAttributes.Destination.NETWORK_FOLDER -> {
                val networkFolderUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER, ""))
                val mUriNetworkFolderUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_USERNAME, "")
                val mUriNetworkFolderPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_PASSWORD, "")
                val mUriNetworkFolderDomain = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_DOMAIN, "")

                var networkCredentialsAttributes: NetworkCredentialsAttributes? = null
                if (!TextUtils.isEmpty(mUriNetworkFolderUsername) && !TextUtils.isEmpty(mUriNetworkFolderPassword)) {
                    networkCredentialsAttributes = NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriNetworkFolderUsername)
                            .setPassword(mUriNetworkFolderPassword)
                            .setDomain(mUriNetworkFolderDomain)
                            .build()
                }

                return ScanAttributes.NetworkFolderBuilder(networkFolderUri).apply {
                    setColorMode(cm)
                    setDuplex(du)
                    setDocumentFormat(df)
                    setScanSize(ss)
                    setCustomLength(customLength)
                    setCustomWidth(customWidth)
                    setResolution(resolution)
                    setOrientation(orientation)
                    setScanPreview(scanPreview)
                    setBackgroundCleanup(backgroundCleanup)
                    setContrastAdjustment(contrastAdjustment)
                    setDarknessAdjustment(darknessAdjustment)
                    setBlankImageRemovalMode(blankImageRemovalMode)
                    setColorDropoutMode(colorDropoutMode)
                    setCropMode(cropMode)
                    setProgressDialogMode(progressDialogMode)
                    setOutputQuality(outputQuality)
                    setTransmissionMode(transmissionMode)
                    setJobAssemblyMode(jobAssemblyMode)
                    setSharpnessAdjustment(sharpnessAdjustment)
                    setMediaWeightAdjustment(mediaWeightAdjustment)
                    setTextPhotoOptimization(textPhotoOptimization)
                    setMediaSource(mediaSource)
                    setMisfeedDetectionMode(misfeedDetectionMode)
                    setSplitAttachmentByPage(splitAttachmentByPage)
                    setMaxPagesPerAttachment(maxPagesPerAttachment)
                    setEraseMarginUnit(eraseMarginUnit)
                    setEraseBackMargin(backMargin)
                    setEraseFrontMargin(frontMargin)
                    setCaptureMode(captureMode)
                    setAutomaticToneMode(automaticToneMode)
                    setAutomaticStraightenMode(automaticStraightenMode)
                    if (fileOptionsAttributes != null) {
                        setFileOptionsAttributes(fileOptionsAttributes)
                    }
                    setFileName(filename)
                    setNetworkCredentials(networkCredentialsAttributes)
                }.build(capabilities)
            }

            ScanAttributes.Destination.EMAIL -> {
                val emailTo = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_TO, "") ?: ""
                val emailCc = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_CC, "") ?: ""
                val emailBcc = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_BCC, "") ?: ""
                val emailFrom = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_FROM, "") ?: ""
                val emailSubject = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_SUBJECT, "")
                        ?: ""
                val emailMessage = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_MESSAGE, "")
                        ?: ""
                val isEnabledSmtp = mPrefs.getBoolean(ScanConfigureFragment.PREF_EMAIL_SMTP, false)

                val emailAttributesBuilder = EmailAttributes.Builder()
                val comma = ","

                if (!TextUtils.isEmpty(emailTo)) {
                    emailAttributesBuilder.addToAddresses(*emailTo.split(comma.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                }
                if (!TextUtils.isEmpty(emailCc)) {
                    emailAttributesBuilder.addCcAddresses(*emailCc.split(comma.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                }
                if (!TextUtils.isEmpty(emailBcc)) {
                    emailAttributesBuilder.addBccAddresses(*emailBcc.split(comma.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                }

                if (!TextUtils.isEmpty(emailFrom)) {
                    val value = emailFrom.trim { it <= ' ' }
                    if (!TextUtils.isEmpty(value)) {
                        emailAttributesBuilder.setFrom(emailFrom, null)
                    }
                }
                if (!TextUtils.isEmpty(emailSubject)) {
                    emailAttributesBuilder.setSubject(emailSubject)
                }
                if (!TextUtils.isEmpty(emailMessage)) {
                    emailAttributesBuilder.setMessage(emailMessage)
                }

                var smtpAttributes: SmtpAttributes? = null
                if (isEnabledSmtp) {
                    val hostname = mPrefs.getString(mContextRef.get()?.getString(R.string.pref_email_hostname), null)
                    val port = mPrefs.getInt(mContextRef.get()?.getString(R.string.pref_email_port), DEFAULT_PORT)
                    val connectionTimeout = mPrefs.getInt(mContextRef.get()?.getString(R.string.pref_email_connection_timeout), DEFAULT_TIMEOUT)
                    val readTimeout = mPrefs.getInt(mContextRef.get()?.getString(R.string.pref_email_read_timeout), DEFAULT_TIMEOUT)
                    val username = mPrefs.getString(mContextRef.get()?.getString(R.string.pref_email_username), null)
                    val password = mPrefs.getString(mContextRef.get()?.getString(R.string.pref_email_password), null)
                    val domain = mPrefs.getString(mContextRef.get()?.getString(R.string.pref_email_domain), null)
                    val transportModeStr = mPrefs.getString(mContextRef.get()?.getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name)
                            ?: SmtpAttributes.TransportMode.PLAIN.name

                    var authentication: NetworkCredentialsAttributes? = null
                    if (!TextUtils.isEmpty(username)) {
                        authentication = NetworkCredentialsAttributes.Builder().apply {
                            setUserName(username)
                            setPassword(password)
                            setDomain(domain)
                        }.build()
                    }

                    val transportMode = SmtpAttributes.TransportMode.valueOf(transportModeStr)

                    smtpAttributes = SmtpAttributes.Builder(hostname).apply {
                        setPort(port)
                        setConnectTimeout(connectionTimeout)
                        setReadTimeout(readTimeout)
                        setServerCredentials(authentication)
                        setTransportMode(transportMode)
                    }.build()
                }

                return ScanAttributes.EmailBuilder(emailAttributesBuilder.build()).apply {
                    setSmtpAttributes(smtpAttributes)
                    setColorMode(cm)
                    setDuplex(du)
                    setDocumentFormat(df)
                    setScanSize(ss)
                    setCustomLength(customLength)
                    setCustomWidth(customWidth)
                    setResolution(resolution)
                    setOrientation(orientation)
                    setScanPreview(scanPreview)
                    setBackgroundCleanup(backgroundCleanup)
                    setContrastAdjustment(contrastAdjustment)
                    setDarknessAdjustment(darknessAdjustment)
                    setBlankImageRemovalMode(blankImageRemovalMode)
                    setColorDropoutMode(colorDropoutMode)
                    setCropMode(cropMode)
                    setProgressDialogMode(progressDialogMode)
                    setOutputQuality(outputQuality)
                    setTransmissionMode(transmissionMode)
                    setJobAssemblyMode(jobAssemblyMode)
                    setSharpnessAdjustment(sharpnessAdjustment)
                    setMediaWeightAdjustment(mediaWeightAdjustment)
                    setTextPhotoOptimization(textPhotoOptimization)
                    setMediaSource(mediaSource)
                    setMisfeedDetectionMode(misfeedDetectionMode)
                    setSplitAttachmentByPage(splitAttachmentByPage)
                    setMaxPagesPerAttachment(maxPagesPerAttachment)
                    setEraseMarginUnit(eraseMarginUnit)
                    setEraseBackMargin(backMargin)
                    setEraseFrontMargin(frontMargin)
                    setCaptureMode(captureMode)
                    setAutomaticToneMode(automaticToneMode)
                    setAutomaticStraightenMode(automaticStraightenMode)
                    if (fileOptionsAttributes != null) {
                        setFileOptionsAttributes(fileOptionsAttributes)
                    }
                    setFileName(filename)
                }.build(capabilities)
            }

            ScanAttributes.Destination.USB -> {
                val usbLocation = mPrefs.getString(ScanConfigureFragment.PREF_USB_LOCATION, "")

                return ScanAttributes.UsbBuilder(usbLocation).apply {
                    setColorMode(cm)
                    setDuplex(du)
                    setDocumentFormat(df)
                    setScanSize(ss)
                    setResolution(resolution)
                    setOrientation(orientation)
                    setScanPreview(scanPreview)
                    setBackgroundCleanup(backgroundCleanup)
                    setContrastAdjustment(contrastAdjustment)
                    setDarknessAdjustment(darknessAdjustment)
                    setBlankImageRemovalMode(blankImageRemovalMode)
                    setColorDropoutMode(colorDropoutMode)
                    setCropMode(cropMode)
                    setProgressDialogMode(progressDialogMode)
                    setOutputQuality(outputQuality)
                    setTransmissionMode(transmissionMode)
                    setJobAssemblyMode(jobAssemblyMode)
                    setSharpnessAdjustment(sharpnessAdjustment)
                    setMediaWeightAdjustment(mediaWeightAdjustment)
                    setTextPhotoOptimization(textPhotoOptimization)
                    setMediaSource(mediaSource)
                    setMisfeedDetectionMode(misfeedDetectionMode)
                    setSplitAttachmentByPage(splitAttachmentByPage)
                    setMaxPagesPerAttachment(maxPagesPerAttachment)
                    setEraseMarginUnit(eraseMarginUnit)
                    setEraseBackMargin(backMargin)
                    setEraseFrontMargin(frontMargin)
                    setCaptureMode(captureMode)
                    setAutomaticToneMode(automaticToneMode)
                    setAutomaticStraightenMode(automaticStraightenMode)
                    if (fileOptionsAttributes != null) {
                        setFileOptionsAttributes(fileOptionsAttributes)
                    }
                    setFileName(filename)
                }.build(capabilities)
            }

            else -> throw IllegalArgumentException("Unsupported destination")
        }
    }

    companion object {
        /* log */
        private const val TAG = MainActivity.TAG
    }
}
