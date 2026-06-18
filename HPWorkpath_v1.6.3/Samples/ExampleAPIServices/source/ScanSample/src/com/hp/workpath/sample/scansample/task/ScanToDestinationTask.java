// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task;

import static com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.DEFAULT_PORT;
import static com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.DEFAULT_TIMEOUT;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.scanner.EmailAttributes;
import com.hp.workpath.api.scanner.FileOptionsAttributes;
import com.hp.workpath.api.scanner.FileOptionsAttributesCaps;
import com.hp.workpath.api.scanner.Margins;
import com.hp.workpath.api.scanner.NetworkCredentialsAttributes;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.scanner.ScanletAttributes;
import com.hp.workpath.api.scanner.ScannerService;
import com.hp.workpath.api.scanner.SmtpAttributes;
import com.hp.workpath.sample.scansample.Logger;
import com.hp.workpath.sample.scansample.MainActivity;
import com.hp.workpath.sample.scansample.R;
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Builds attributes and executes Scan To Me launch
 */
public class ScanToDestinationTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;

    private final SharedPreferences mPrefs;
    private String mErrorMsg = null;
    private Throwable mThrowable = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public ScanToDestinationTask(final MainActivity context) {
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
                        final boolean settingsUi = mPrefs.getBoolean(ScanConfigureFragment.PREF_SETTINGS_UI, false);
                        final boolean allowMultipleScan = mPrefs.getBoolean(ScanConfigureFragment.PREF_ALLOW_MULTIPLE_SCAN, false);
                        Log.d(TAG, "Settings UI:" + settingsUi);
                        Log.d(TAG, "Allow Multiple Scan:" + allowMultipleScan);

                        ScanAttributes attributes = null;

                        if (!settingsUi) {
                            // Obtain Caps to build Scan Attributes
                            final ScanAttributesCaps caps = activity.getCapabilities();

                            if (caps == null) {
                                mErrorMsg = activity.getString(R.string.capabilities_not_loaded);
                                onPostExecute(null);
                                return;
                            }

                            final ScanAttributes.Destination dest =
                                    ScanAttributes.Destination.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_DESTINATION,
                                            ScanAttributes.Destination.ME.name()));

                            attributes = buildScanAttributes(dest, caps);
                        }

                        final ScanletAttributes taskAttribs = new ScanletAttributes.Builder()
                                .setShowSettingsUi(settingsUi)
                                .setAllowMultipleScan(allowMultipleScan)
                                .build();

                        // Submit the job
                        final String rid = ScannerService.submit(activity, attributes, taskAttribs);
                        onPostExecute(rid);
                    } catch (CapabilitiesExceededException cee) {
                        mErrorMsg = "CapabilitiesExceededException";
                        mThrowable = cee;
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (IllegalArgumentException iae) {
                        mErrorMsg = "IllegalArgumentException";
                        mThrowable = iae;
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (Throwable t) {
                        mErrorMsg = "Unknown exception";
                        mThrowable = t;
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            mErrorMsg = "Unknown exception";
            mThrowable = e;
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
                } else if (mThrowable != null) {
                    Logger.showResult(mContextRef.get(), mErrorMsg + " " + mThrowable.getMessage());
                } else {
                    Logger.showResult(mContextRef.get(), mErrorMsg);
                }
            }
        });
    }

    private ScanAttributes buildScanAttributes(ScanAttributes.Destination destination, ScanAttributesCaps capabilities)
            throws CapabilitiesExceededException {
        final ScanAttributes.DocumentFormat df = ScanAttributes.DocumentFormat.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DOC_FORMAT,
                        ScanAttributes.DocumentFormat.DEFAULT.name()));
        Log.d(TAG, "Selected Doc Format:" + df);

        final ScanAttributes.ColorMode cm = ScanAttributes.ColorMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_COLOR_MODE,
                        ScanAttributes.ColorMode.DEFAULT.name()));
        Log.d(TAG, "Selected Color Mode:" + cm.name());

        final ScanAttributes.Duplex du = ScanAttributes.Duplex.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DUPLEX_MODE,
                        ScanAttributes.Duplex.DEFAULT.name()));
        Log.d(TAG, "Selected Duplex Mode:" + du.name());

        final ScanAttributes.Orientation orientation = ScanAttributes.Orientation.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ORIENTATION,
                        ScanAttributes.Orientation.DEFAULT.name()));
        Log.d(TAG, "Selected Orientation:" + orientation.name());

        final ScanAttributes.ScanSize ss = ScanAttributes.ScanSize.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ORG_SIZE,
                        ScanAttributes.ScanSize.DEFAULT.name()));
        Log.d(TAG, "Selected Scan Size:" + ss.name());

        final float customLength = Float.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_CUSTOM_LENGTH, "0"));
        Log.d(TAG, "Selected Custom Length:" + customLength);

        final float customWidth = Float.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_CUSTOM_WIDTH, "0"));
        Log.d(TAG, "Selected Custom Width:" + customWidth);

        final ScanAttributes.Resolution resolution = ScanAttributes.Resolution.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_RESOLUTION_TYPE,
                        ScanAttributes.Resolution.DEFAULT.name()));
        Log.d(TAG, "Selected Resolution:" + resolution.name());

        final ScanAttributes.BackgroundCleanup backgroundCleanup = ScanAttributes.BackgroundCleanup.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_BACKGROUND_CLEANUP,
                        ScanAttributes.BackgroundCleanup.DEFAULT.name()));
        Log.d(TAG, "Selected BackgroundCleanup:" + backgroundCleanup.name());

        final ScanAttributes.ContrastAdjustment contrastAdjustment = ScanAttributes.ContrastAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CONTRAST_ADJUSTMENT,
                        ScanAttributes.ContrastAdjustment.DEFAULT.name()));
        Log.d(TAG, "Selected ContrastAdjustment:" + contrastAdjustment.name());

        final ScanAttributes.DarknessAdjustment darknessAdjustment = ScanAttributes.DarknessAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_DARKNESS_ADJUSTMENT,
                        ScanAttributes.DarknessAdjustment.DEFAULT.name()));
        Log.d(TAG, "Selected DarknessAdjustment:" + darknessAdjustment.name());

        final ScanAttributes.BlankImageRemovalMode blankImageRemovalMode = ScanAttributes.BlankImageRemovalMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_BLANK_IMAGE_REMOVAL_MODE,
                        ScanAttributes.BlankImageRemovalMode.DEFAULT.name()));
        Log.d(TAG, "Selected BlankImageRemovalMode:" + blankImageRemovalMode.name());

        final ScanAttributes.ColorDropoutMode colorDropoutMode = ScanAttributes.ColorDropoutMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_COLOR_DROPOUT_MODE,
                        ScanAttributes.ColorDropoutMode.DEFAULT.name()));
        Log.d(TAG, "Selected ColorDropoutMode:" + colorDropoutMode.name());

        final ScanAttributes.CropMode cropMode = ScanAttributes.CropMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CROP_MODE,
                        ScanAttributes.CropMode.DEFAULT.name()));
        Log.d(TAG, "Selected CropMode:" + cropMode.name());

        final ScanAttributes.ProgressDialogMode progressDialogMode = ScanAttributes.ProgressDialogMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_PROGRESS_DIALOG_MODE,
                        ScanAttributes.ProgressDialogMode.DEFAULT.name()));
        Log.d(TAG, "Selected ProgressDialogMode:" + progressDialogMode.name());

        final ScanAttributes.OutputQuality outputQuality = ScanAttributes.OutputQuality.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_OUTPUT_QUALITY,
                        ScanAttributes.OutputQuality.DEFAULT.name()));
        Log.d(TAG, "Selected OutputQuality:" + outputQuality.name());

        final ScanAttributes.TransmissionMode transmissionMode = ScanAttributes.TransmissionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TRANSMISSION_MODE,
                        ScanAttributes.TransmissionMode.DEFAULT.name()));
        Log.d(TAG, "Selected TransmissionMode:" + transmissionMode.name());

        final ScanAttributes.ScanPreview scanPreview = ScanAttributes.ScanPreview.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SCAN_PREVIEW,
                        ScanAttributes.ScanPreview.DEFAULT.name()));
        Log.d(TAG, "Selected ScanPreview:" + scanPreview.name());

        final ScanAttributes.JobAssemblyMode jobAssemblyMode = ScanAttributes.JobAssemblyMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_JOB_ASSEMBLY_MODE,
                        ScanAttributes.JobAssemblyMode.DEFAULT.name()));
        Log.d(TAG, "Selected JobAssemblyMode:" + jobAssemblyMode.name());

        final ScanAttributes.SharpnessAdjustment sharpnessAdjustment = ScanAttributes.SharpnessAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SHARPNESS_ADJUSTMENT,
                        ScanAttributes.SharpnessAdjustment.DEFAULT.name()));
        Log.d(TAG, "Selected SharpnessAdjustment:" + sharpnessAdjustment.name());

        final ScanAttributes.MediaWeightAdjustment mediaWeightAdjustment = ScanAttributes.MediaWeightAdjustment.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MEDIA_WEIGHT_ADJUSTMENT,
                        ScanAttributes.MediaWeightAdjustment.DEFAULT.name()));
        Log.d(TAG, "Selected MediaWeightAdjustment:" + mediaWeightAdjustment.name());

        final ScanAttributes.TextPhotoOptimization textPhotoOptimization = ScanAttributes.TextPhotoOptimization.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TEXT_PHOTO_OPTIMIZATION,
                        ScanAttributes.TextPhotoOptimization.DEFAULT.name()));
        Log.d(TAG, "Selected TextPhotoOptimization:" + textPhotoOptimization.name());

        final ScanAttributes.MediaSource mediaSource = ScanAttributes.MediaSource.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MEDIA_SOURCE,
                        ScanAttributes.MediaSource.DEFAULT.name()));
        Log.d(TAG, "Selected MediaSource:" + mediaSource.name());

        final ScanAttributes.MisfeedDetectionMode misfeedDetectionMode = ScanAttributes.MisfeedDetectionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_MISFEED_DETECTION_MODE,
                        ScanAttributes.MisfeedDetectionMode.DEFAULT.name()));
        Log.d(TAG, "Selected MisfeedDetectionMode:" + misfeedDetectionMode.name());

        final ScanAttributes.SplitAttachmentByPage splitAttachmentByPage = ScanAttributes.SplitAttachmentByPage.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_SPLIT_ATTACHMENT_BY_PAGE,
                        ScanAttributes.SplitAttachmentByPage.DEFAULT.name()));
        Log.d(TAG, "Selected splitAttachmentByPage:" + splitAttachmentByPage.name());

        final int maxPagesPerAttachment = Integer.valueOf(mPrefs.getString(ScanConfigureFragment.PREF_MAX_PAGES_PER_ATTACHMENT, "0"));

        final ScanAttributes.EraseMarginUnit eraseMarginUnit = ScanAttributes.EraseMarginUnit.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_ERASE_MARGIN_UNIT,
                        ScanAttributes.EraseMarginUnit.DEFAULT.name()));
        Log.d(TAG, "Selected eraseMarginUnit:" + eraseMarginUnit.name());

        final Margins backMargin = new Margins(mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_BACK_BOTTOM_MARGIN, 0.0f));

        final Margins frontMargin = new Margins(mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_LEFT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_TOP_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_RIGHT_MARGIN, 0.0f),
                mPrefs.getFloat(ScanConfigureFragment.PREF_ERASE_FRONT_BOTTOM_MARGIN, 0.0f));

        final ScanAttributes.CaptureMode captureMode = ScanAttributes.CaptureMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_CAPTURE_MODE,
                        ScanAttributes.CaptureMode.DEFAULT.name()));
        Log.d(TAG, "Selected captureMode:" + captureMode.name());

        final ScanAttributes.AutomaticToneMode automaticToneMode = ScanAttributes.AutomaticToneMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_AUTOMATIC_TONE_MODE,
                        ScanAttributes.AutomaticToneMode.DEFAULT.name()));
        Log.d(TAG, "Selected automaticToneMode:" + automaticToneMode.name());

        final ScanAttributes.AutomaticStraightenMode automaticStraightenMode = ScanAttributes.AutomaticStraightenMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_AUTOMATIC_STRAIGHTEN_MODE,
                        ScanAttributes.AutomaticStraightenMode.DEFAULT.name()));
        Log.d(TAG, "Selected automaticStraightenMode:" + automaticStraightenMode.name());

        final String pdfEncryption = mPrefs.getString(ScanConfigureFragment.PREF_PDF_PASSWORD, null);

        final FileOptionsAttributes.OcrLanguage ocrLanguage = FileOptionsAttributes.OcrLanguage.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_OCR_LANGUAGE,
                        FileOptionsAttributes.OcrLanguage.DEFAULT.name()));

        final FileOptionsAttributes.PdfCompressionMode compressionMode = FileOptionsAttributes.PdfCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_PDF_COMPRESSION,
                        FileOptionsAttributes.PdfCompressionMode.DEFAULT.name()));

        final FileOptionsAttributes.TiffCompressionMode tiffCompressionMode = FileOptionsAttributes.TiffCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_TIFF_COMPRESSION,
                        FileOptionsAttributes.TiffCompressionMode.DEFAULT.name()));

        final FileOptionsAttributes.XpsCompressionMode xpsCompressionMode = FileOptionsAttributes.XpsCompressionMode.valueOf(
                mPrefs.getString(ScanConfigureFragment.PREF_XPS_COMPRESSION,
                        FileOptionsAttributes.XpsCompressionMode.DEFAULT.name()));

        final String filename = mPrefs.getString(ScanConfigureFragment.PREF_FILE_NAME, null);

        FileOptionsAttributesCaps fileOptionsAttrCaps = mContextRef.get().getFileOptionsAttributesCaps();
        Log.d(TAG, "FileOptionsAttributesCaps=" + Logger.build(fileOptionsAttrCaps, cm, df));

        FileOptionsAttributes fileOptionsAttributes = new FileOptionsAttributes.Builder()
                .setPdfEncryptionPassword(pdfEncryption)
                .setOcrLanguage(ocrLanguage)
                .setPdfCompressionMode(compressionMode)
                .setTiffCompressionMode(tiffCompressionMode)
                .setXpsCompressionMode(xpsCompressionMode)
                .build(fileOptionsAttrCaps);

        switch (destination) {
            case ME:
                return new ScanAttributes.MeBuilder()
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setCustomLength(customLength)
                        .setCustomWidth(customWidth)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .build(capabilities);
            case HTTP:
                final Uri httpUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP, ""));
                final String mUriHttpUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP_USERNAME, "");
                final String mUriHttpPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_HTTP_PASSWORD, "");

                NetworkCredentialsAttributes httpCredentialsAttributes = null;
                if (!TextUtils.isEmpty(mUriHttpUsername) && !TextUtils.isEmpty(mUriHttpPassword)) {
                    httpCredentialsAttributes = new NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriHttpUsername)
                            .setPassword(mUriHttpPassword)
                            .build();
                }

                return new ScanAttributes.HttpBuilder(httpUri)
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setCustomLength(customLength)
                        .setCustomWidth(customWidth)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .setNetworkCredentials(httpCredentialsAttributes)
                        .build(capabilities);

            case FTP:
                final Uri ftpUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP, ""));
                final String mUriFtpUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP_USERNAME, "");
                final String mUriFtpPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_FTP_PASSWORD, "");

                NetworkCredentialsAttributes ftpCredentialsAttributes = null;
                if (!TextUtils.isEmpty(mUriFtpUsername) && !TextUtils.isEmpty(mUriFtpPassword)) {
                    ftpCredentialsAttributes = new NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriFtpUsername)
                            .setPassword(mUriFtpPassword)
                            .build();
                }

                return new ScanAttributes.FtpBuilder(ftpUri)
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setCustomLength(customLength)
                        .setCustomWidth(customWidth)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .setNetworkCredentials(ftpCredentialsAttributes)
                        .build(capabilities);

            case NETWORK_FOLDER:
                final Uri networkFolderUri = Uri.parse(mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER, ""));
                final String mUriNetworkFolderUsername = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_USERNAME, "");
                final String mUriNetworkFolderPassword = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_PASSWORD, "");
                final String mUriNetworkFolderDomain = mPrefs.getString(ScanConfigureFragment.PREF_URI_NETWORK_FOLDER_DOMAIN, "");

                NetworkCredentialsAttributes networkCredentialsAttributes = null;
                if (!TextUtils.isEmpty(mUriNetworkFolderUsername) && !TextUtils.isEmpty(mUriNetworkFolderPassword)) {
                    networkCredentialsAttributes = new NetworkCredentialsAttributes.Builder()
                            .setUserName(mUriNetworkFolderUsername)
                            .setPassword(mUriNetworkFolderPassword)
                            .setDomain(mUriNetworkFolderDomain)
                            .build();
                }

                return new ScanAttributes.NetworkFolderBuilder(networkFolderUri)
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setCustomLength(customLength)
                        .setCustomWidth(customWidth)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .setNetworkCredentials(networkCredentialsAttributes)
                        .build(capabilities);

            case EMAIL:
                String emailTo = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_TO, "");
                String emailCc = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_CC, "");
                String emailBcc = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_BCC, "");
                String emailFrom = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_FROM, "");
                String emailSubject = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_SUBJECT, "");
                String emailMessage = mPrefs.getString(ScanConfigureFragment.PREF_EMAIL_MESSAGE, "");
                boolean isEnabledSmtp = mPrefs.getBoolean(ScanConfigureFragment.PREF_EMAIL_SMTP, false);

                EmailAttributes.Builder emailAttributesBuilder = new EmailAttributes.Builder();

                if (!TextUtils.isEmpty(emailTo)) {
                    emailAttributesBuilder.addToAddresses(emailTo.split(","));
                }
                if (!TextUtils.isEmpty(emailCc)) {
                    emailAttributesBuilder.addCcAddresses(emailCc.split(","));
                }
                if (!TextUtils.isEmpty(emailBcc)) {
                    emailAttributesBuilder.addBccAddresses(emailBcc.split(","));
                }

                if (!TextUtils.isEmpty(emailFrom)) {
                    String value = emailFrom.trim();
                    if (!TextUtils.isEmpty(value)) {
                        emailAttributesBuilder.setFrom(emailFrom, null);
                    }
                }
                if (!TextUtils.isEmpty(emailSubject)) {
                    emailAttributesBuilder.setSubject(emailSubject);
                }
                if (!TextUtils.isEmpty(emailMessage)) {
                    emailAttributesBuilder.setMessage(emailMessage);
                }

                SmtpAttributes smtpAttributes = null;
                if (isEnabledSmtp) {
                    String hostname = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_hostname), null);
                    int port = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_port), DEFAULT_PORT);
                    int connectionTimeout = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_connection_timeout), DEFAULT_TIMEOUT);
                    int readTimeout = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_read_timeout), DEFAULT_TIMEOUT);
                    String username = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_username), null);
                    String password = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_password), null);
                    String domain = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_domain), null);
                    String transportModeStr = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name());

                    NetworkCredentialsAttributes authentication = null;
                    if (!TextUtils.isEmpty(username)) {
                        authentication = new NetworkCredentialsAttributes.Builder()
                                .setUserName(username)
                                .setPassword(password)
                                .setDomain(domain)
                                .build();
                    }

                    SmtpAttributes.TransportMode transportMode = SmtpAttributes.TransportMode.valueOf(transportModeStr);

                    smtpAttributes = new SmtpAttributes.Builder(hostname)
                            .setPort(port)
                            .setConnectTimeout(connectionTimeout)
                            .setReadTimeout(readTimeout)
                            .setServerCredentials(authentication)
                            .setTransportMode(transportMode)
                            .build();
                }

                return new ScanAttributes.EmailBuilder(emailAttributesBuilder.build())
                        .setSmtpAttributes(smtpAttributes)
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setCustomLength(customLength)
                        .setCustomWidth(customWidth)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .build(capabilities);

            case USB:
                final String usbLocation = mPrefs.getString(ScanConfigureFragment.PREF_USB_LOCATION, "");

                return new ScanAttributes.UsbBuilder(usbLocation)
                        .setColorMode(cm)
                        .setDuplex(du)
                        .setDocumentFormat(df)
                        .setScanSize(ss)
                        .setResolution(resolution)
                        .setOrientation(orientation)
                        .setScanPreview(scanPreview)
                        .setBackgroundCleanup(backgroundCleanup)
                        .setContrastAdjustment(contrastAdjustment)
                        .setDarknessAdjustment(darknessAdjustment)
                        .setBlankImageRemovalMode(blankImageRemovalMode)
                        .setColorDropoutMode(colorDropoutMode)
                        .setCropMode(cropMode)
                        .setProgressDialogMode(progressDialogMode)
                        .setOutputQuality(outputQuality)
                        .setTransmissionMode(transmissionMode)
                        .setJobAssemblyMode(jobAssemblyMode)
                        .setSharpnessAdjustment(sharpnessAdjustment)
                        .setMediaWeightAdjustment(mediaWeightAdjustment)
                        .setTextPhotoOptimization(textPhotoOptimization)
                        .setMediaSource(mediaSource)
                        .setMisfeedDetectionMode(misfeedDetectionMode)
                        .setSplitAttachmentByPage(splitAttachmentByPage)
                        .setMaxPagesPerAttachment(maxPagesPerAttachment)
                        .setEraseMarginUnit(eraseMarginUnit)
                        .setEraseBackMargin(backMargin)
                        .setEraseFrontMargin(frontMargin)
                        .setCaptureMode(captureMode)
                        .setAutomaticToneMode(automaticToneMode)
                        .setAutomaticStraightenMode(automaticStraightenMode)
                        .setFileOptionsAttributes(fileOptionsAttributes)
                        .setFileName(filename)
                        .build(capabilities);

            default:
                throw new IllegalArgumentException("Unsupported destination");
        }
    }
}
