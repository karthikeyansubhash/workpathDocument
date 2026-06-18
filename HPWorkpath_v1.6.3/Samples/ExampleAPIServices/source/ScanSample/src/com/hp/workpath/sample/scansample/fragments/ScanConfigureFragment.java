// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.api.massstorage.MassStorageService;
import com.hp.workpath.api.scanner.FileOptionsAttributes;
import com.hp.workpath.api.scanner.FileOptionsAttributesCaps;
import com.hp.workpath.api.scanner.Margins;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScanAttributes.ColorMode;
import com.hp.workpath.api.scanner.ScanAttributes.DocumentFormat;
import com.hp.workpath.api.scanner.ScanAttributes.Duplex;
import com.hp.workpath.api.scanner.ScanAttributes.Orientation;
import com.hp.workpath.api.scanner.ScanAttributes.Resolution;
import com.hp.workpath.api.scanner.ScanAttributes.ScanPreview;
import com.hp.workpath.api.scanner.ScanAttributes.ScanSize;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributesReader;
import com.hp.workpath.sample.scansample.MainActivity;
import com.hp.workpath.sample.scansample.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.DEFAULT_PORT;

/**
 * Simple {@link PreferenceFragmentCompat} to set Scan Attributes and save into preferences.
 */
public final class ScanConfigureFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivity.TAG;

    // Preferences keys for ScanAttributes
    public static final String PREF_DESTINATION = "pref_destination";
    public static final String PREF_FILE_NAME = "pref_filename";
    public static final String PREF_COLOR_MODE = "pref_colorMode";
    public static final String PREF_DUPLEX_MODE = "pref_duplexMode";
    public static final String PREF_RESOLUTION_TYPE = "pref_resolutionType";
    public static final String PREF_DOC_FORMAT = "pref_docFormat";
    public static final String PREF_ORG_SIZE = "pref_originalSize";
    public static final String PREF_CUSTOM_LENGTH = "pref_customLength";
    public static final String PREF_CUSTOM_WIDTH = "pref_customWidth";
    public static final String PREF_ORIENTATION = "pref_orientation";
    public static final String PREF_SCAN_PREVIEW = "pref_scanPreview";
    public static final String PREF_BACKGROUND_CLEANUP = "pref_backgroundCleanup";
    public static final String PREF_CONTRAST_ADJUSTMENT = "pref_contrastAdjustment";
    public static final String PREF_DARKNESS_ADJUSTMENT = "pref_darknessAdjustment";
    public static final String PREF_BLANK_IMAGE_REMOVAL_MODE = "pref_blankImageRemovalMode";
    public static final String PREF_COLOR_DROPOUT_MODE = "pref_colorDropoutMode";
    public static final String PREF_CROP_MODE = "pref_cropMode";
    public static final String PREF_PROGRESS_DIALOG_MODE = "pref_progressDialogMode";
    public static final String PREF_OUTPUT_QUALITY = "pref_outputQuality";
    public static final String PREF_TRANSMISSION_MODE = "pref_transmissionMode";
    public static final String PREF_JOB_ASSEMBLY_MODE = "pref_jobAssemblyMode";
    public static final String PREF_SHARPNESS_ADJUSTMENT = "pref_sharpnessAdjustment";
    public static final String PREF_MEDIA_WEIGHT_ADJUSTMENT = "pref_mediaWeightAdjustment";
    public static final String PREF_TEXT_PHOTO_OPTIMIZATION = "pref_textPhotoOptimization";

    public static final String PREF_SPLIT_ATTACHMENT_BY_PAGE = "pref_splitAttachmentByPage";
    public static final String PREF_MAX_PAGES_PER_ATTACHMENT = "pref_maxPagesPerAttachment";
    public static final String PREF_ERASE_MARGIN_UNIT = "pref_eraseMarginUnit";
    public static final String PREF_ERASE_BACK_MARGIN = "pref_eraseBackMargin";
    public static final String PREF_ERASE_BACK_LEFT_MARGIN = "pref_eraseBackMarginLeft";
    public static final String PREF_ERASE_BACK_TOP_MARGIN = "pref_eraseBackMarginTop";
    public static final String PREF_ERASE_BACK_RIGHT_MARGIN = "pref_eraseBackMarginRight";
    public static final String PREF_ERASE_BACK_BOTTOM_MARGIN = "pref_eraseBackMarginBottom";
    public static final String PREF_ERASE_FRONT_MARGIN = "pref_eraseFrontMargin";
    public static final String PREF_ERASE_FRONT_LEFT_MARGIN = "pref_eraseFrontMarginLeft";
    public static final String PREF_ERASE_FRONT_TOP_MARGIN = "pref_eraseFrontMarginTop";
    public static final String PREF_ERASE_FRONT_RIGHT_MARGIN = "pref_eraseFrontMarginRight";
    public static final String PREF_ERASE_FRONT_BOTTOM_MARGIN = "pref_eraseFrontMarginBottom";
    public static final String PREF_CAPTURE_MODE = "pref_captureMode";
    public static final String PREF_AUTOMATIC_TONE_MODE = "pref_automaticToneMode";
    public static final String PREF_AUTOMATIC_STRAIGHTEN_MODE = "pref_automaticStraightenMode";

    public static final String PREF_MEDIA_SOURCE = "pref_mediaSource";
    public static final String PREF_MISFEED_DETECTION_MODE = "pref_misfeedDetectionMode";
    public static final String PREF_PDF_COMPRESSION = "pref_pdf_compression";
    public static final String PREF_OCR_LANGUAGE = "pref_ocr_language";
    public static final String PREF_PDF_PASSWORD = "pref_pdf_password";
    public static final String PREF_TIFF_COMPRESSION = "pref_tiff_compression";
    public static final String PREF_XPS_COMPRESSION = "pref_xps_compression";

    public static final String PREF_URI_HTTP = "pref_uri_http";
    public static final String PREF_URI_FTP = "pref_uri_ftp";
    public static final String PREF_URI_NETWORK_FOLDER = "pref_uri_network_folder";
    public static final String PREF_URI_HTTP_USERNAME = "pref_uri_http_username";
    public static final String PREF_URI_HTTP_PASSWORD = "pref_uri_http_password";
    public static final String PREF_URI_FTP_USERNAME = "pref_uri_ftp_username";
    public static final String PREF_URI_FTP_PASSWORD = "pref_uri_ftp_password";
    public static final String PREF_URI_NETWORK_FOLDER_USERNAME = "pref_uri_network_folder_username";
    public static final String PREF_URI_NETWORK_FOLDER_PASSWORD = "pref_uri_network_folder_password";
    public static final String PREF_URI_NETWORK_FOLDER_DOMAIN = "pref_uri_network_folder_domain";

    public static final String PREF_EMAIL_TO = "pref_email_to";
    public static final String PREF_EMAIL_CC = "pref_email_cc";
    public static final String PREF_EMAIL_BCC = "pref_email_bcc";
    public static final String PREF_EMAIL_FROM = "pref_email_from";
    public static final String PREF_EMAIL_SUBJECT = "pref_email_subject";
    public static final String PREF_EMAIL_MESSAGE = "pref_email_message";
    public static final String PREF_EMAIL_SMTP = "pref_email_smtp";

    public static final String PREF_USB_STORAGE = "pref_usb_storage";
    public static final String PREF_USB_LOCATION = "pref_usb_location";

    public static final String PREF_DESTINATION_HTTP_CATEGORY = "destination_http_category";
    public static final String PREF_DESTINATION_FTP_CATEGORY = "destination_ftp_category";
    public static final String PREF_DESTINATION_NETWORK_FOLDER_CATEGORY = "destination_network_folder_category";
    public static final String PREF_DESTINATION_EMAIL_CATEGORY = "destination_email_category";
    public static final String PREF_DESTINATION_USB_CATEGORY = "destination_usb_category";
    public static final String PREF_BASE_ATTRIBUTES_CATEGORY = "base_attributes_category";
    public static final String PREF_DESTINATION_FEEDBACK_CATEGORY = "destination_feedback_category";

    // Feedback / UI preferences
    public static final String PREF_MONITOR_JOB = "pref_monitorJob";
    public static final String PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress";
    public static final String PREF_SETTINGS_UI = "pref_settingsUi";
    public static final String PREF_ALLOW_MULTIPLE_SCAN = "pref_allow_multiple_scan";

    // Preference for current job id
    public static final String CURRENT_JOB_ID = "pref_currentJobId";

    public static final String PREF_SCANNER_STATUS = "pref_scanner_status";

    private ScanAttributesCaps mCaps;
    private PreferenceCategory mHttpCategory;
    private PreferenceCategory mFtpCategory;
    private PreferenceCategory mNetworkFolderCategory;
    private PreferenceCategory mEmailCategory;
    private PreferenceCategory mUsbCategory;
    private PreferenceCategory mBaseAttributesCategory;
    private PreferenceCategory mFeedbackCategory;

    private EditTextPreference mFilenamePref;
    private EditTextPreference mFileUriHttpPref, mFileUriFtpPref, mFileUriNetworkFolderPref;
    private EditTextPreference mFileUriHttpUsernamePref;
    private EditTextPreference mFileUriHttpPasswordPref;
    private EditTextPreference mFileUriFtpUsernamePref;
    private EditTextPreference mFileUriFtpPasswordPref;
    private EditTextPreference mFileUriNetworkFolderUsernamePref;
    private EditTextPreference mFileUriNetworkFolderPasswordPref;
    private EditTextPreference mFileUriNetworkFolderDomainPref;
    private EditTextPreference mEmailToPref;
    private EditTextPreference mEmailCcPref;
    private EditTextPreference mEmailBccPref;
    private EditTextPreference mUsbFolderPref;
    private CheckBoxPreference mEmailSmtpPref;

    private ListPreference mTransmissionPref;
    private ListPreference mPDFCompressionPref;
    private ListPreference mOCRLanguagePref;
    private EditTextPreference mPDFPasswordPref;
    private ListPreference mTIFFCompressionPref;
    private ListPreference mXPSCompressionPref;
    private EditTextFloatPreference mCustomLengthPref;
    private EditTextFloatPreference mCustomWidthPref;
    private EditTextIntegerPreference mMaxPagesPerAttachmentPref;
    private MarginsPreference mEraseBackMarginPref;
    private MarginsPreference mEraseFrontMarginPref;

    private CheckBoxPreference mMonitorJobPref;
    private CheckBoxPreference mShowJobProgressPref;
    private CheckBoxPreference mSettingsUIPref;
    private CheckBoxPreference mAllowMultipleScanPref;

    private boolean isSDKInitialized;
    private String defaultTransmissionMode = ScanAttributes.TransmissionMode.JOB.name();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.scan_preferences);

        mFilenamePref = (EditTextPreference) findPreference(PREF_FILE_NAME);
        mFilenamePref.setText(null);
        mFileUriHttpPref = (EditTextPreference) findPreference(PREF_URI_HTTP);
        mFileUriHttpPref.setText(null);
        mFileUriFtpPref = (EditTextPreference) findPreference(PREF_URI_FTP);
        mFileUriFtpPref.setText(null);
        mFileUriNetworkFolderPref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER);
        mFileUriNetworkFolderPref.setText(null);
        mFileUriHttpUsernamePref = (EditTextPreference) findPreference(PREF_URI_HTTP_USERNAME);
        mFileUriHttpUsernamePref.setText(null);
        mFileUriHttpPasswordPref = (EditTextPreference) findPreference(PREF_URI_HTTP_PASSWORD);
        mFileUriHttpPasswordPref.setText(null);

        mFileUriFtpUsernamePref = (EditTextPreference) findPreference(PREF_URI_FTP_USERNAME);
        mFileUriFtpUsernamePref.setText(null);
        mFileUriFtpPasswordPref = (EditTextPreference) findPreference(PREF_URI_FTP_PASSWORD);
        mFileUriFtpPasswordPref.setText(null);

        mFileUriNetworkFolderUsernamePref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_USERNAME);
        mFileUriNetworkFolderUsernamePref.setText(null);
        mFileUriNetworkFolderPasswordPref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_PASSWORD);
        mFileUriNetworkFolderPasswordPref.setText(null);
        mFileUriNetworkFolderDomainPref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_DOMAIN);
        mFileUriNetworkFolderDomainPref.setText(null);

        mUsbFolderPref = (EditTextPreference) findPreference(PREF_USB_LOCATION);
        mUsbFolderPref.setText(null);

        mEmailToPref = (EditTextPreference) findPreference(PREF_EMAIL_TO);
        mEmailCcPref = (EditTextPreference) findPreference(PREF_EMAIL_CC);
        mEmailBccPref = (EditTextPreference) findPreference(PREF_EMAIL_BCC);
        mEmailSmtpPref = (CheckBoxPreference) findPreference(PREF_EMAIL_SMTP);
        mEmailSmtpPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean) newValue) {
                    new EmailSmtpSettingFragment().show(getParentFragmentManager(), getString(R.string.pref_email_smtp_title));
                }
                return true;
            }
        });

        mTransmissionPref = (ListPreference) findPreference(PREF_TRANSMISSION_MODE);
        mPDFCompressionPref = (ListPreference) findPreference(PREF_PDF_COMPRESSION);
        mOCRLanguagePref = (ListPreference) findPreference(PREF_OCR_LANGUAGE);
        mPDFPasswordPref = (EditTextPreference) findPreference(PREF_PDF_PASSWORD);
        mTIFFCompressionPref = (ListPreference) findPreference(PREF_TIFF_COMPRESSION);
        mXPSCompressionPref = (ListPreference) findPreference(PREF_XPS_COMPRESSION);
        mCustomLengthPref = (EditTextFloatPreference) findPreference(PREF_CUSTOM_LENGTH);
        mCustomLengthPref.setLimits(0, 0);
        mCustomLengthPref.setText(null);
        mCustomWidthPref = (EditTextFloatPreference) findPreference(PREF_CUSTOM_WIDTH);
        mCustomWidthPref.setLimits(0, 0);
        mCustomWidthPref.setText(null);
        mMaxPagesPerAttachmentPref = (EditTextIntegerPreference) findPreference(PREF_MAX_PAGES_PER_ATTACHMENT);
        mMaxPagesPerAttachmentPref.setLimits(0, 0);
        mMaxPagesPerAttachmentPref.setText(null);
        mEraseBackMarginPref = (MarginsPreference) findPreference(PREF_ERASE_BACK_MARGIN);
        mEraseFrontMarginPref = (MarginsPreference) findPreference(PREF_ERASE_FRONT_MARGIN);

        mHttpCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_HTTP_CATEGORY);
        mFtpCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_FTP_CATEGORY);
        mNetworkFolderCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_NETWORK_FOLDER_CATEGORY);
        mEmailCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_EMAIL_CATEGORY);
        mUsbCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_USB_CATEGORY);
        mBaseAttributesCategory = (PreferenceCategory) findPreference(PREF_BASE_ATTRIBUTES_CATEGORY);
        mFeedbackCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_FEEDBACK_CATEGORY);

        mMonitorJobPref = (CheckBoxPreference) findPreference(PREF_MONITOR_JOB);
        mShowJobProgressPref = (CheckBoxPreference) findPreference(PREF_SHOW_JOB_PROGRESS);
        mSettingsUIPref = (CheckBoxPreference) findPreference(PREF_SETTINGS_UI);
        mAllowMultipleScanPref = (CheckBoxPreference) findPreference(PREF_ALLOW_MULTIPLE_SCAN);

        mMonitorJobPref.setChecked(true);
        mShowJobProgressPref.setChecked(true);
        mSettingsUIPref.setChecked(false);
        mAllowMultipleScanPref.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        refreshAllPrefs(prefs);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Refreshes all values in the fragment
     *
     * @param prefs {@link SharedPreferences}
     */
    private void refreshAllPrefs(final SharedPreferences prefs) {
        onSharedPreferenceChanged(prefs, PREF_DESTINATION);
        onSharedPreferenceChanged(prefs, PREF_FILE_NAME);
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE);
        onSharedPreferenceChanged(prefs, PREF_DUPLEX_MODE);
        onSharedPreferenceChanged(prefs, PREF_ORIENTATION);
        onSharedPreferenceChanged(prefs, PREF_RESOLUTION_TYPE);
        onSharedPreferenceChanged(prefs, PREF_ORG_SIZE);
        onSharedPreferenceChanged(prefs, PREF_CUSTOM_LENGTH);
        onSharedPreferenceChanged(prefs, PREF_CUSTOM_WIDTH);
        onSharedPreferenceChanged(prefs, PREF_DOC_FORMAT);
        onSharedPreferenceChanged(prefs, PREF_SCAN_PREVIEW);
        onSharedPreferenceChanged(prefs, PREF_BACKGROUND_CLEANUP);
        onSharedPreferenceChanged(prefs, PREF_CONTRAST_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_DARKNESS_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_BLANK_IMAGE_REMOVAL_MODE);
        onSharedPreferenceChanged(prefs, PREF_COLOR_DROPOUT_MODE);
        onSharedPreferenceChanged(prefs, PREF_CROP_MODE);
        onSharedPreferenceChanged(prefs, PREF_PROGRESS_DIALOG_MODE);
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_QUALITY);
        onSharedPreferenceChanged(prefs, PREF_TRANSMISSION_MODE);
        onSharedPreferenceChanged(prefs, PREF_JOB_ASSEMBLY_MODE);
        onSharedPreferenceChanged(prefs, PREF_SHARPNESS_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_MEDIA_WEIGHT_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_TEXT_PHOTO_OPTIMIZATION);
        onSharedPreferenceChanged(prefs, PREF_MEDIA_SOURCE);
        onSharedPreferenceChanged(prefs, PREF_MISFEED_DETECTION_MODE);
        onSharedPreferenceChanged(prefs, PREF_SPLIT_ATTACHMENT_BY_PAGE);
        onSharedPreferenceChanged(prefs, PREF_MAX_PAGES_PER_ATTACHMENT);
        onSharedPreferenceChanged(prefs, PREF_ERASE_MARGIN_UNIT);
        onSharedPreferenceChanged(prefs, PREF_ERASE_BACK_MARGIN);
        onSharedPreferenceChanged(prefs, PREF_ERASE_FRONT_MARGIN);
        onSharedPreferenceChanged(prefs, PREF_CAPTURE_MODE);
        onSharedPreferenceChanged(prefs, PREF_AUTOMATIC_TONE_MODE);
        onSharedPreferenceChanged(prefs, PREF_AUTOMATIC_STRAIGHTEN_MODE);

        onSharedPreferenceChanged(prefs, PREF_MONITOR_JOB);
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS);
        onSharedPreferenceChanged(prefs, PREF_SETTINGS_UI);
        onSharedPreferenceChanged(prefs, PREF_ALLOW_MULTIPLE_SCAN);

        onSharedPreferenceChanged(prefs, PREF_EMAIL_TO);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_CC);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_BCC);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_FROM);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_SUBJECT);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_MESSAGE);
        onSharedPreferenceChanged(prefs, PREF_EMAIL_SMTP);

        onSharedPreferenceChanged(prefs, PREF_PDF_COMPRESSION);
        onSharedPreferenceChanged(prefs, PREF_OCR_LANGUAGE);
        onSharedPreferenceChanged(prefs, PREF_PDF_PASSWORD);
        onSharedPreferenceChanged(prefs, PREF_TIFF_COMPRESSION);
        onSharedPreferenceChanged(prefs, PREF_XPS_COMPRESSION);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof MarginsPreference) {
            DialogFragment dialogFragment = MarginsPreferenceFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), null);
        } else super.onDisplayPreferenceDialog(preference);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Preference preference = findPreference(key);
        if (preference != null) {
            if (PREF_DOC_FORMAT.equals(key)) {
                setColorMode(sharedPreferences, key);
                showOptionalPreference(sharedPreferences, key);
                fillFileOptionAttrCaps();
            } else if (PREF_ORG_SIZE.equals(key)) {
                showCustomSizePreference(sharedPreferences, key);
            } else if (PREF_COLOR_MODE.equals(key)) {
                fillFileOptionAttrCaps();
            } else if (PREF_DESTINATION.equals(key)) {
                final String entryStr = (String) ((ListPreference) preference).getEntry();
                final ScanAttributes.Destination destination =
                        entryStr == null ? ScanAttributes.Destination.ME : ScanAttributes.Destination.valueOf(entryStr);

                fillDocFormatPreferences(mCaps, PREF_DOC_FORMAT, destination);

                switch (destination) {
                    case HTTP:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_HTTP_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mHttpCategory);
                        }

                        getPreferenceScreen().removePreference(mFtpCategory);
                        getPreferenceScreen().removePreference(mNetworkFolderCategory);
                        getPreferenceScreen().removePreference(mEmailCategory);
                        getPreferenceScreen().removePreference(mUsbCategory);

                        mFileUriHttpUsernamePref = (EditTextPreference) findPreference(PREF_URI_HTTP_USERNAME);
                        mFileUriHttpUsernamePref.setText(null);
                        mFileUriHttpPasswordPref = (EditTextPreference) findPreference(PREF_URI_HTTP_PASSWORD);
                        mFileUriHttpPasswordPref.setText(null);

                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP_USERNAME);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP_PASSWORD);
                        supportTransmissionModeCaps(true, ScanAttributes.Destination.HTTP.name());

                        break;
                    case FTP:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_FTP_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mFtpCategory);
                        }

                        getPreferenceScreen().removePreference(mHttpCategory);
                        getPreferenceScreen().removePreference(mNetworkFolderCategory);
                        getPreferenceScreen().removePreference(mEmailCategory);
                        getPreferenceScreen().removePreference(mUsbCategory);

                        mFileUriFtpUsernamePref = (EditTextPreference) findPreference(PREF_URI_FTP_USERNAME);
                        mFileUriFtpUsernamePref.setText(null);
                        mFileUriFtpPasswordPref = (EditTextPreference) findPreference(PREF_URI_FTP_PASSWORD);
                        mFileUriFtpPasswordPref.setText(null);

                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP_USERNAME);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP_PASSWORD);
                        supportTransmissionModeCaps(true, ScanAttributes.Destination.FTP.name());

                        break;
                    case NETWORK_FOLDER:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_NETWORK_FOLDER_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mNetworkFolderCategory);
                        }

                        getPreferenceScreen().removePreference(mHttpCategory);
                        getPreferenceScreen().removePreference(mFtpCategory);
                        getPreferenceScreen().removePreference(mEmailCategory);
                        getPreferenceScreen().removePreference(mUsbCategory);

                        mFileUriNetworkFolderUsernamePref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_USERNAME);
                        mFileUriNetworkFolderUsernamePref.setText(null);
                        mFileUriNetworkFolderPasswordPref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_PASSWORD);
                        mFileUriNetworkFolderPasswordPref.setText(null);
                        mFileUriNetworkFolderDomainPref = (EditTextPreference) findPreference(PREF_URI_NETWORK_FOLDER_DOMAIN);
                        mFileUriNetworkFolderDomainPref.setText(null);

                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_NETWORK_FOLDER);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_NETWORK_FOLDER_USERNAME);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_NETWORK_FOLDER_PASSWORD);
                        onSharedPreferenceChanged(sharedPreferences, PREF_URI_NETWORK_FOLDER_DOMAIN);

                        supportTransmissionModeCaps(true, ScanAttributes.Destination.NETWORK_FOLDER.name());

                        break;

                    case EMAIL:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_EMAIL_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mEmailCategory);
                        }

                        getPreferenceScreen().removePreference(mHttpCategory);
                        getPreferenceScreen().removePreference(mFtpCategory);
                        getPreferenceScreen().removePreference(mNetworkFolderCategory);
                        getPreferenceScreen().removePreference(mUsbCategory);

                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_TO);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_CC);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_BCC);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_FROM);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_SUBJECT);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_MESSAGE);
                        onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_SMTP);
                        supportTransmissionModeCaps(true, ScanAttributes.Destination.EMAIL.name());

                        break;

                    case USB:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_USB_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mUsbCategory);
                        }

                        getPreferenceScreen().removePreference(mHttpCategory);
                        getPreferenceScreen().removePreference(mFtpCategory);
                        getPreferenceScreen().removePreference(mNetworkFolderCategory);
                        getPreferenceScreen().removePreference(mEmailCategory);

                        fillUSBStorages();

                        onSharedPreferenceChanged(sharedPreferences, PREF_USB_STORAGE);
                        supportTransmissionModeCaps(false, ScanAttributes.Destination.USB.name());
                        break;

                    default:
                        getPreferenceScreen().removePreference(mHttpCategory);
                        getPreferenceScreen().removePreference(mFtpCategory);
                        getPreferenceScreen().removePreference(mNetworkFolderCategory);
                        getPreferenceScreen().removePreference(mEmailCategory);
                        getPreferenceScreen().removePreference(mUsbCategory);
                        supportTransmissionModeCaps(false, ScanAttributes.Destination.ME.name());
                        break;
                }
            } else if (PREF_USB_STORAGE.equals(key)) {
                final String usbLocation = ((ListPreference) preference).getValue();
                mUsbFolderPref.setText(usbLocation);
                onSharedPreferenceChanged(sharedPreferences, PREF_USB_LOCATION);
            }

            if (preference instanceof ListPreference) {
                final String entry = (String) ((ListPreference) preference).getEntry();

                if (entry == null || entry.length() == 0) {
                    ((ListPreference) preference).setValueIndex(0);
                    preference.setSummary("%s");
                } else {
                    preference.setSummary(entry);
                }
            } else if (preference instanceof EditTextPreference) {
                String text = ((EditTextPreference) preference).getText();
                if (PREF_URI_HTTP_PASSWORD.equals(key)
                        || PREF_URI_FTP_PASSWORD.equals(key)
                        || PREF_URI_NETWORK_FOLDER_PASSWORD.equals(key)
                        || PREF_PDF_PASSWORD.equals(key)) {
                    text = getTransformationString(text);
                }
                preference.setSummary(text);
            } else if (preference instanceof CheckBoxPreference) {
                if (PREF_MONITOR_JOB.equals(key)) {
                    findPreference(PREF_SHOW_JOB_PROGRESS)
                            .setEnabled(((CheckBoxPreference) preference).isChecked());
                } else if (PREF_EMAIL_SMTP.equals(key)) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    boolean value = sharedPref.getBoolean(key, false);
                    ((CheckBoxPreference) preference).setChecked(value);
                    if (value) {
                        String hostname = sharedPref.getString(getString(R.string.pref_email_hostname), "");
                        int port = sharedPref.getInt(getString(R.string.pref_email_port), DEFAULT_PORT);
                        preference.setSummary(getString(R.string.hint_email_smtp_enable, hostname, port));
                    } else {
                        preference.setSummary(getString(R.string.hint_email_smtp));
                    }
                }
            }
        }
    }

    private String getTransformationString(String text) {
        if (!TextUtils.isEmpty(text)) {
            EditText editText = new EditText(getActivity());
            editText.setText(text);
            editText.setTransformationMethod(new PasswordTransformationMethod());
            return editText.getTransformationMethod().getTransformation(text, editText).toString();
        }
        return null;
    }

    /**
     * Applies capabilities to configuration screen
     *
     * @param caps {@link com.hp.workpath.api.scanner.ScanAttributesCaps}
     */
    public void loadCapabilities(final ScanAttributesCaps caps) {
        mCaps = caps;

        ListPreference pref;

        // Load Resolutions
        pref = (ListPreference) findPreference(PREF_RESOLUTION_TYPE);
        ArrayList<CharSequence> resEntries = new ArrayList<>();
        ArrayList<CharSequence> resEntryValues = new ArrayList<>();

        for (Resolution res : caps.getResolutionList()) {
            resEntries.add(res.name());
            resEntryValues.add(res.name());
        }

        pref.setEntries(resEntries.toArray(new CharSequence[resEntries.size()]));
        pref.setEntryValues(resEntryValues.toArray(new CharSequence[resEntryValues.size()]));
        pref.setDefaultValue(Resolution.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        // Load color mode
        pref = (ListPreference) findPreference(PREF_COLOR_MODE);
        ArrayList<CharSequence> cmEntries = new ArrayList<>();
        ArrayList<CharSequence> cmEntryValues = new ArrayList<>();

        for (ColorMode cm : caps.getColorModeList()) {
            cmEntries.add(cm.name());
            cmEntryValues.add(cm.name());
        }

        pref.setEntries(cmEntries.toArray(new CharSequence[cmEntries.size()]));
        pref.setEntryValues(cmEntryValues.toArray(new CharSequence[cmEntryValues.size()]));
        pref.setDefaultValue(ColorMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        // Load duplex mode
        pref = (ListPreference) findPreference(PREF_DUPLEX_MODE);
        ArrayList<CharSequence> duEntries = new ArrayList<>();
        ArrayList<CharSequence> duEntryValues = new ArrayList<>();

        for (Duplex du : caps.getDuplexList()) {
            duEntries.add(du.name());
            duEntryValues.add(du.name());
        }

        pref.setEntries(duEntries.toArray(new CharSequence[duEntries.size()]));
        pref.setEntryValues(duEntryValues.toArray(new CharSequence[duEntryValues.size()]));
        pref.setDefaultValue(Duplex.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Original Orientation
        pref = (ListPreference) findPreference(PREF_ORIENTATION);
        ArrayList<CharSequence> orientationEntries = new ArrayList<>();
        ArrayList<CharSequence> orientationEntryValues = new ArrayList<>();

        for (Orientation ori : caps.getOrientationList()) {
            orientationEntries.add(ori.name()); //name
            orientationEntryValues.add(ori.name()); //value
        }
        pref.setEntries(orientationEntries.toArray(new CharSequence[orientationEntries.size()]));
        pref.setEntryValues(orientationEntryValues.toArray(new CharSequence[orientationEntryValues.size()]));
        pref.setDefaultValue(Orientation.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Background Clean up
        pref = (ListPreference) findPreference(PREF_BACKGROUND_CLEANUP);
        ArrayList<CharSequence> backgroundCleanupEntries = new ArrayList<>();
        ArrayList<CharSequence> backgroundCleanupValues = new ArrayList<>();

        for (ScanAttributes.BackgroundCleanup backgroundCleanup : caps.getBackgroundCleanupList()) {
            backgroundCleanupEntries.add(backgroundCleanup.name()); //name
            backgroundCleanupValues.add(backgroundCleanup.name()); //value
        }
        pref.setEntries(backgroundCleanupEntries.toArray(new CharSequence[backgroundCleanupEntries.size()]));
        pref.setEntryValues(backgroundCleanupValues.toArray(new CharSequence[backgroundCleanupValues.size()]));
        pref.setDefaultValue(ScanAttributes.BackgroundCleanup.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Contrast Adjustment
        pref = (ListPreference) findPreference(PREF_CONTRAST_ADJUSTMENT);
        ArrayList<CharSequence> contrastAdjustmentEntries = new ArrayList<>();
        ArrayList<CharSequence> contrastAdjustmentValues = new ArrayList<>();

        for (ScanAttributes.ContrastAdjustment contrastAdjustment : caps.getContrastAdjustmentList()) {
            contrastAdjustmentEntries.add(contrastAdjustment.name()); //name
            contrastAdjustmentValues.add(contrastAdjustment.name()); //value
        }
        pref.setEntries(contrastAdjustmentEntries.toArray(new CharSequence[contrastAdjustmentEntries.size()]));
        pref.setEntryValues(contrastAdjustmentValues.toArray(new CharSequence[contrastAdjustmentValues.size()]));
        pref.setDefaultValue(ScanAttributes.ContrastAdjustment.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Darkness Adjustment
        pref = (ListPreference) findPreference(PREF_DARKNESS_ADJUSTMENT);
        ArrayList<CharSequence> darknessAdjustmentEntries = new ArrayList<>();
        ArrayList<CharSequence> darknessAdjustmentValues = new ArrayList<>();

        for (ScanAttributes.DarknessAdjustment darknessAdjustment : caps.getDarknessAdjustmentList()) {
            darknessAdjustmentEntries.add(darknessAdjustment.name()); //name
            darknessAdjustmentValues.add(darknessAdjustment.name()); //value
        }
        pref.setEntries(darknessAdjustmentEntries.toArray(new CharSequence[darknessAdjustmentEntries.size()]));
        pref.setEntryValues(darknessAdjustmentValues.toArray(new CharSequence[darknessAdjustmentValues.size()]));
        pref.setDefaultValue(ScanAttributes.DarknessAdjustment.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Black Image Removal mode
        pref = (ListPreference) findPreference(PREF_BLANK_IMAGE_REMOVAL_MODE);
        ArrayList<CharSequence> blankImageRemovalEntries = new ArrayList<>();
        ArrayList<CharSequence> blankImageRemovalValues = new ArrayList<>();

        for (ScanAttributes.BlankImageRemovalMode blankImageRemovalMode : caps.getBlankImageRemovalModeList()) {
            blankImageRemovalEntries.add(blankImageRemovalMode.name()); //name
            blankImageRemovalValues.add(blankImageRemovalMode.name()); //value
        }
        pref.setEntries(blankImageRemovalEntries.toArray(new CharSequence[blankImageRemovalEntries.size()]));
        pref.setEntryValues(blankImageRemovalValues.toArray(new CharSequence[blankImageRemovalValues.size()]));
        pref.setDefaultValue(ScanAttributes.BlankImageRemovalMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Color Dropout mode
        pref = (ListPreference) findPreference(PREF_COLOR_DROPOUT_MODE);
        ArrayList<CharSequence> colorDropoutModeEntries = new ArrayList<>();
        ArrayList<CharSequence> colorDropoutModeValues = new ArrayList<>();

        for (ScanAttributes.ColorDropoutMode colorDropoutMode : caps.getColorDropoutModeList()) {
            colorDropoutModeEntries.add(colorDropoutMode.name()); //name
            colorDropoutModeValues.add(colorDropoutMode.name()); //value
        }
        pref.setEntries(colorDropoutModeEntries.toArray(new CharSequence[colorDropoutModeEntries.size()]));
        pref.setEntryValues(colorDropoutModeValues.toArray(new CharSequence[colorDropoutModeValues.size()]));
        pref.setDefaultValue(ScanAttributes.ColorDropoutMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Crop mode
        pref = (ListPreference) findPreference(PREF_CROP_MODE);
        ArrayList<CharSequence> cropModeEntries = new ArrayList<>();
        ArrayList<CharSequence> cropModeValues = new ArrayList<>();

        for (ScanAttributes.CropMode cropMode : caps.getCropModeList()) {
            cropModeEntries.add(cropMode.name()); //name
            cropModeValues.add(cropMode.name()); //value
        }
        pref.setEntries(cropModeEntries.toArray(new CharSequence[cropModeEntries.size()]));
        pref.setEntryValues(cropModeValues.toArray(new CharSequence[cropModeValues.size()]));
        pref.setDefaultValue(ScanAttributes.CropMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        // Load Original Size
        pref = (ListPreference) findPreference(PREF_ORG_SIZE);
        ArrayList<CharSequence> osEntries = new ArrayList<>();
        ArrayList<CharSequence> osEntryValues = new ArrayList<>();

        for (ScanAttributes.ScanSize os : caps.getScanSizeList()) {
            CharSequence entrie = "";
            if (os.getWidth() == 0.0 && os.getHeight() == 0.0 && os.getUnit() == null) {
                entrie = os.name();
            } else {
                entrie = os.name()+" ("+os.getWidth()+" X "+os.getHeight()+" "+os.getUnit()+")";
            }
            osEntries.add(entrie);
            osEntryValues.add(os.name());
        }

        pref.setEntries(osEntries.toArray(new CharSequence[osEntries.size()]));
        pref.setEntryValues(osEntryValues.toArray(new CharSequence[osEntryValues.size()]));
        pref.setDefaultValue(ScanAttributes.ScanSize.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Destination
        pref = (ListPreference) findPreference(PREF_DESTINATION);
        ArrayList<CharSequence> destEntries = new ArrayList<>();
        ArrayList<CharSequence> destEntryValues = new ArrayList<>();

        for (ScanAttributes.Destination dest : caps.getDestinationList()) {
            destEntries.add(dest.name());
            destEntryValues.add(dest.name());
        }

        pref.setEntries(destEntries.toArray(new CharSequence[destEntries.size()]));
        pref.setEntryValues(destEntryValues.toArray(new CharSequence[destEntryValues.size()]));
        // ME is always presented
        pref.setDefaultValue(caps.getDestinationList().get(0));
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Scan Preview
        pref = (ListPreference) findPreference(PREF_SCAN_PREVIEW);
        ArrayList<CharSequence> scanPreviewEntries = new ArrayList<>();
        ArrayList<CharSequence> scanPreviewEntryValues = new ArrayList<>();

        for (ScanPreview scanPreview : caps.getScanPreviewList()) {
            scanPreviewEntries.add(scanPreview.name());
            scanPreviewEntryValues.add(scanPreview.name());
        }
        pref.setEntries(scanPreviewEntries.toArray(new CharSequence[scanPreviewEntries.size()]));
        pref.setEntryValues(scanPreviewEntryValues.toArray(new CharSequence[scanPreviewEntryValues.size()]));
        pref.setDefaultValue(ScanAttributes.ScanPreview.DEFAULT);
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Progress Dialog mode
        pref = (ListPreference) findPreference(PREF_PROGRESS_DIALOG_MODE);
        ArrayList<CharSequence> progressDialogModeEntries = new ArrayList<>();
        ArrayList<CharSequence> progressDialogModeValues = new ArrayList<>();

        for (ScanAttributes.ProgressDialogMode progressDialogMode : caps.getProgressDialogModeList()) {
            progressDialogModeEntries.add(progressDialogMode.name()); //name
            progressDialogModeValues.add(progressDialogMode.name()); //value
        }
        pref.setEntries(progressDialogModeEntries.toArray(new CharSequence[progressDialogModeEntries.size()]));
        pref.setEntryValues(progressDialogModeValues.toArray(new CharSequence[progressDialogModeValues.size()]));
        pref.setDefaultValue(ScanAttributes.ProgressDialogMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Output Quality
        pref = (ListPreference) findPreference(PREF_OUTPUT_QUALITY);
        ArrayList<CharSequence> outputQualityEntries = new ArrayList<>();
        ArrayList<CharSequence> outputQualityValues = new ArrayList<>();

        for (ScanAttributes.OutputQuality outputQuality : caps.getOutputQualityList()) {
            outputQualityEntries.add(outputQuality.name()); //name
            outputQualityValues.add(outputQuality.name()); //value
        }
        pref.setEntries(outputQualityEntries.toArray(new CharSequence[outputQualityEntries.size()]));
        pref.setEntryValues(outputQualityValues.toArray(new CharSequence[outputQualityValues.size()]));
        pref.setDefaultValue(ScanAttributes.OutputQuality.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Job Assembly mode
        pref = (ListPreference) findPreference(PREF_JOB_ASSEMBLY_MODE);
        ArrayList<CharSequence> jobAssemblyModeEntries = new ArrayList<>();
        ArrayList<CharSequence> jobAssemblyModeValues = new ArrayList<>();

        for (ScanAttributes.JobAssemblyMode jobAssemblyMode : caps.getJobAssemblyModeList()) {
            jobAssemblyModeEntries.add(jobAssemblyMode.name()); //name
            jobAssemblyModeValues.add(jobAssemblyMode.name()); //value
        }
        pref.setEntries(jobAssemblyModeEntries.toArray(new CharSequence[jobAssemblyModeEntries.size()]));
        pref.setEntryValues(jobAssemblyModeValues.toArray(new CharSequence[jobAssemblyModeValues.size()]));
        pref.setDefaultValue(ScanAttributes.JobAssemblyMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Sharpness Adjustment
        pref = (ListPreference) findPreference(PREF_SHARPNESS_ADJUSTMENT);
        ArrayList<CharSequence> sharpnessAdjustmentEntries = new ArrayList<>();
        ArrayList<CharSequence> sharpnessAdjustmentValues = new ArrayList<>();

        for (ScanAttributes.SharpnessAdjustment sharpnessAdjustment : caps.getSharpnessAdjustmentList()) {
            sharpnessAdjustmentEntries.add(sharpnessAdjustment.name()); //name
            sharpnessAdjustmentValues.add(sharpnessAdjustment.name()); //value
        }
        pref.setEntries(sharpnessAdjustmentEntries.toArray(new CharSequence[sharpnessAdjustmentEntries.size()]));
        pref.setEntryValues(sharpnessAdjustmentValues.toArray(new CharSequence[sharpnessAdjustmentValues.size()]));
        pref.setDefaultValue(ScanAttributes.SharpnessAdjustment.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Media Weight Adjustment
        pref = (ListPreference) findPreference(PREF_MEDIA_WEIGHT_ADJUSTMENT);
        ArrayList<CharSequence> mediaWeightAdjustmentEntries = new ArrayList<>();
        ArrayList<CharSequence> mediaWeightAdjustmentValues = new ArrayList<>();

        for (ScanAttributes.MediaWeightAdjustment mediaWeightAdjustment : caps.getMediaWeightAdjustmentList()) {
            mediaWeightAdjustmentEntries.add(mediaWeightAdjustment.name()); //name
            mediaWeightAdjustmentValues.add(mediaWeightAdjustment.name()); //value
        }
        pref.setEntries(mediaWeightAdjustmentEntries.toArray(new CharSequence[mediaWeightAdjustmentEntries.size()]));
        pref.setEntryValues(mediaWeightAdjustmentValues.toArray(new CharSequence[mediaWeightAdjustmentValues.size()]));
        pref.setDefaultValue(ScanAttributes.MediaWeightAdjustment.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Text Photo Optimization
        pref = (ListPreference) findPreference(PREF_TEXT_PHOTO_OPTIMIZATION);
        ArrayList<CharSequence> textPhotoOptimizationEntries = new ArrayList<>();
        ArrayList<CharSequence> textPhotoOptimizationValues = new ArrayList<>();

        for (ScanAttributes.TextPhotoOptimization textPhotoOptimization : caps.getTextPhotoOptimizationList()) {
            textPhotoOptimizationEntries.add(textPhotoOptimization.name()); //name
            textPhotoOptimizationValues.add(textPhotoOptimization.name()); //value
        }
        pref.setEntries(textPhotoOptimizationEntries.toArray(new CharSequence[textPhotoOptimizationEntries.size()]));
        pref.setEntryValues(textPhotoOptimizationValues.toArray(new CharSequence[textPhotoOptimizationValues.size()]));
        pref.setDefaultValue(ScanAttributes.TextPhotoOptimization.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Media Source
        pref = (ListPreference) findPreference(PREF_MEDIA_SOURCE);
        ArrayList<CharSequence> mediaSourceEntries = new ArrayList<>();
        ArrayList<CharSequence> mediaSourceValues = new ArrayList<>();

        for (ScanAttributes.MediaSource mediaSource : caps.getMediaSourceList()) {
            mediaSourceEntries.add(mediaSource.name()); //name
            mediaSourceValues.add(mediaSource.name()); //value
        }
        pref.setEntries(mediaSourceEntries.toArray(new CharSequence[mediaSourceEntries.size()]));
        pref.setEntryValues(mediaSourceValues.toArray(new CharSequence[mediaSourceValues.size()]));
        pref.setDefaultValue(ScanAttributes.MediaSource.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Misfeed Detection mode
        pref = (ListPreference) findPreference(PREF_MISFEED_DETECTION_MODE);
        ArrayList<CharSequence> misfeedDetectionModeEntries = new ArrayList<>();
        ArrayList<CharSequence> misfeedDetectionModeValues = new ArrayList<>();

        for (ScanAttributes.MisfeedDetectionMode misfeedDetectionMode : caps.getMisfeedDetectionModeList()) {
            misfeedDetectionModeEntries.add(misfeedDetectionMode.name()); //name
            misfeedDetectionModeValues.add(misfeedDetectionMode.name()); //value
        }
        pref.setEntries(misfeedDetectionModeEntries.toArray(new CharSequence[misfeedDetectionModeEntries.size()]));
        pref.setEntryValues(misfeedDetectionModeValues.toArray(new CharSequence[misfeedDetectionModeValues.size()]));
        pref.setDefaultValue(ScanAttributes.MisfeedDetectionMode.DEFAULT.name());
        pref.setValueIndex(0);
        pref.setSummary("%s");

        //Load Custom length, height
        mCustomLengthPref.setLimits(caps.getCustomLengthRange().getLowerBound(), caps.getCustomLengthRange().getUpperBound());
        mCustomWidthPref.setLimits(caps.getCustomWidthRange().getLowerBound(), caps.getCustomWidthRange().getUpperBound());

        //Load Split Attachment by page
        if (caps.getSplitAttachmentByPageList().size() > 0) {
            pref = (ListPreference) findPreference(PREF_SPLIT_ATTACHMENT_BY_PAGE);
            ArrayList<CharSequence> splitAttachmentByPageEntries = new ArrayList<>();
            ArrayList<CharSequence> splitAttachmentByPageValues = new ArrayList<>();

            for (ScanAttributes.SplitAttachmentByPage splitAttachmentByPage : caps.getSplitAttachmentByPageList()) {
                splitAttachmentByPageEntries.add(splitAttachmentByPage.name()); //name
                splitAttachmentByPageValues.add(splitAttachmentByPage.name()); //value
            }
            pref.setEntries(splitAttachmentByPageEntries.toArray(new CharSequence[splitAttachmentByPageEntries.size()]));
            pref.setEntryValues(splitAttachmentByPageValues.toArray(new CharSequence[splitAttachmentByPageValues.size()]));
            pref.setDefaultValue(ScanAttributes.SplitAttachmentByPage.DEFAULT.name());
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }

        if (caps.getMaxPagesPerAttachmentRange() != null) {
            mMaxPagesPerAttachmentPref.setLimits((int) caps.getMaxPagesPerAttachmentRange().getLowerBound(),
                    (int) caps.getMaxPagesPerAttachmentRange().getUpperBound());
        }

        //Load erase margin unit
        if (caps.getEraseMarginUnitList().size() > 0) {
            pref = (ListPreference) findPreference(PREF_ERASE_MARGIN_UNIT);
            ArrayList<CharSequence> eraseMarginUnitEntries = new ArrayList<>();
            ArrayList<CharSequence> eraseMarginUnitValues = new ArrayList<>();

            for (ScanAttributes.EraseMarginUnit eraseMarginUnit : caps.getEraseMarginUnitList()) {
                eraseMarginUnitEntries.add(eraseMarginUnit.name()); //name
                eraseMarginUnitValues.add(eraseMarginUnit.name()); //value
            }
            pref.setEntries(eraseMarginUnitEntries.toArray(new CharSequence[eraseMarginUnitEntries.size()]));
            pref.setEntryValues(eraseMarginUnitValues.toArray(new CharSequence[eraseMarginUnitValues.size()]));
            pref.setDefaultValue(ScanAttributes.EraseMarginUnit.DEFAULT.name());
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }

        if (caps.getEraseBackBottomRange() != null) {
            mEraseBackMarginPref.setBottomLimits(caps.getEraseBackBottomRange().getLowerBound(), caps.getEraseBackBottomRange().getUpperBound());
            mEraseBackMarginPref.setLeftLimits(caps.getEraseBackLeftRange().getLowerBound(), caps.getEraseBackLeftRange().getUpperBound());
            mEraseBackMarginPref.setRightLimits(caps.getEraseBackRightRange().getLowerBound(), caps.getEraseBackRightRange().getUpperBound());
            mEraseBackMarginPref.setTopLimits(caps.getEraseBackTopRange().getLowerBound(), caps.getEraseBackTopRange().getUpperBound());
        }

        if (caps.getEraseFrontBottomRange() != null) {
            mEraseFrontMarginPref.setBottomLimits(caps.getEraseFrontBottomRange().getLowerBound(), caps.getEraseFrontBottomRange().getUpperBound());
            mEraseFrontMarginPref.setLeftLimits(caps.getEraseFrontLeftRange().getLowerBound(), caps.getEraseFrontLeftRange().getUpperBound());
            mEraseFrontMarginPref.setRightLimits(caps.getEraseFrontRightRange().getLowerBound(), caps.getEraseFrontRightRange().getUpperBound());
            mEraseFrontMarginPref.setTopLimits(caps.getEraseFrontTopRange().getLowerBound(), caps.getEraseFrontTopRange().getUpperBound());
        }

        //Load Capture mode
        if (caps.getCaptureModeList().size() > 0) {
            pref = (ListPreference) findPreference(PREF_CAPTURE_MODE);
            ArrayList<CharSequence> captureModeEntries = new ArrayList<>();
            ArrayList<CharSequence> captureModeValues = new ArrayList<>();

            for (ScanAttributes.CaptureMode captureMode : caps.getCaptureModeList()) {
                captureModeEntries.add(captureMode.name()); //name
                captureModeValues.add(captureMode.name()); //value
            }
            pref.setEntries(captureModeEntries.toArray(new CharSequence[captureModeEntries.size()]));
            pref.setEntryValues(captureModeValues.toArray(new CharSequence[captureModeValues.size()]));
            pref.setDefaultValue(ScanAttributes.CaptureMode.DEFAULT.name());
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }

        //Load automatic Tone Mode
        if (caps.getAutomaticToneModeList().size() > 0) {
            pref = (ListPreference) findPreference(PREF_AUTOMATIC_TONE_MODE);
            ArrayList<CharSequence> automaticToneModeEntries = new ArrayList<>();
            ArrayList<CharSequence> automaticToneModeValues = new ArrayList<>();

            for (ScanAttributes.AutomaticToneMode automaticToneMode : caps.getAutomaticToneModeList()) {
                automaticToneModeEntries.add(automaticToneMode.name()); //name
                automaticToneModeValues.add(automaticToneMode.name()); //value
            }
            pref.setEntries(automaticToneModeEntries.toArray(new CharSequence[automaticToneModeEntries.size()]));
            pref.setEntryValues(automaticToneModeValues.toArray(new CharSequence[automaticToneModeValues.size()]));
            pref.setDefaultValue(ScanAttributes.AutomaticToneMode.DEFAULT.name());
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }

        //Load automatic Straighten Mode
        if (caps.getAutomaticStraightenModeList().size() > 0) {
            pref = (ListPreference) findPreference(PREF_AUTOMATIC_STRAIGHTEN_MODE);
            ArrayList<CharSequence> automaticStraightenModeEntries = new ArrayList<>();
            ArrayList<CharSequence> automaticStraightenModeValues = new ArrayList<>();

            for (ScanAttributes.AutomaticStraightenMode automaticStraightenMode : caps.getAutomaticStraightenModeList()) {
                automaticStraightenModeEntries.add(automaticStraightenMode.name()); //name
                automaticStraightenModeValues.add(automaticStraightenMode.name()); //value
            }
            pref.setEntries(automaticStraightenModeEntries.toArray(new CharSequence[automaticStraightenModeEntries.size()]));
            pref.setEntryValues(automaticStraightenModeValues.toArray(new CharSequence[automaticStraightenModeValues.size()]));
            pref.setDefaultValue(ScanAttributes.AutomaticStraightenMode.DEFAULT.name());
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }

        // Doc Formats for different destinations
        fillDocFormatPreferences(caps, PREF_DOC_FORMAT, ScanAttributes.Destination.ME);
        fillFileOptionAttrCaps();
    }

    /**
     * Fills Doc format preferences values
     *
     * @param caps          {@link ScanAttributesCaps} to take data from
     * @param prefDocFormat String shared preferences key
     * @param dest          {@link ScanAttributes.Destination} to
     */
    private void fillDocFormatPreferences(ScanAttributesCaps caps, String prefDocFormat, ScanAttributes.Destination dest) {
        if (caps == null) {
            return;
        }

        ListPreference pref = (ListPreference) findPreference(prefDocFormat);

        ArrayList<CharSequence> dfEntries = new ArrayList<>();
        ArrayList<CharSequence> dfEntryValues = new ArrayList<>();

        // Load Doc format
        for (ScanAttributes.DocumentFormat df : caps.getDocumentFormatList(dest)) {
            dfEntries.add(df.name());
            dfEntryValues.add(df.name());
        }

        if (pref != null) {
            pref.setEntries(dfEntries.toArray(new CharSequence[dfEntries.size()]));
            pref.setEntryValues(dfEntryValues.toArray(new CharSequence[dfEntryValues.size()]));
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }
    }

    private void setColorMode(SharedPreferences sharedPreferences, final String key) {
        final DocumentFormat docFormat = DocumentFormat.valueOf(sharedPreferences.getString(key, DocumentFormat.DEFAULT.name()));
        final ListPreference colorModeList =
                (ListPreference) findPreference(ScanConfigureFragment.PREF_COLOR_MODE);

        String colorEntry = (String) colorModeList.getEntry();
        ColorMode colorMode = colorEntry == null ? ColorMode.DEFAULT : ScanAttributes.ColorMode.valueOf(colorEntry);

        if (mCaps != null) {
            List<CharSequence> entries = new ArrayList<>();
            entries.add(colorMode.name());

            for (Map.Entry<ColorMode, List<DocumentFormat>> entry : mCaps.getDocumentFormatsByColorMode().entrySet()) {
                if (entry.getValue().contains(docFormat)
                        && entry.getKey() != ColorMode.DEFAULT) {
                    entries.add(entry.getKey().name());
                }
            }

            CharSequence[] entriesArray = entries.toArray(new CharSequence[entries.size()]);
            colorModeList.setEntries(entriesArray);
            colorModeList.setEntryValues(entriesArray);
            colorModeList.setValueIndex(0);
        } else {
            colorModeList.setEntries(R.array.pref_default_entries);
            colorModeList.setEntryValues(R.array.pref_default_entries);
        }
    }

    private void showOptionalPreference(SharedPreferences preferences, String key) {
        mBaseAttributesCategory.removePreference(mPDFCompressionPref);
        mBaseAttributesCategory.removePreference(mOCRLanguagePref);
        mBaseAttributesCategory.removePreference(mPDFPasswordPref);
        mBaseAttributesCategory.removePreference(mTIFFCompressionPref);
        mBaseAttributesCategory.removePreference(mXPSCompressionPref);

        DocumentFormat docFormat = DocumentFormat.valueOf(preferences.getString(key, DocumentFormat.DEFAULT.name()));
        switch (docFormat) {
            case PDF:
                mBaseAttributesCategory.addPreference(mPDFCompressionPref);
                mBaseAttributesCategory.addPreference(mPDFPasswordPref);
                break;
            case OCR_PDF_TEXT_UNDER_IMAGE:
                mBaseAttributesCategory.addPreference(mPDFCompressionPref);
                mBaseAttributesCategory.addPreference(mOCRLanguagePref);
                mBaseAttributesCategory.addPreference(mPDFPasswordPref);
                break;
            case MTIFF:
            case TIFF:
                mBaseAttributesCategory.addPreference(mTIFFCompressionPref);
                break;
            case OCR_PDF_A_TEXT_UNDER_IMAGE:
                mBaseAttributesCategory.addPreference(mPDFCompressionPref);
                mBaseAttributesCategory.addPreference(mOCRLanguagePref);
                break;
            case OCR_CSV:
            case OCR_HTML:
            case OCR_RTF:
            case OCR_TEXT:
            case OCR_UNICODE_TEXT:
                mBaseAttributesCategory.addPreference(mOCRLanguagePref);
                break;
            case PDF_A:
                mBaseAttributesCategory.addPreference(mPDFCompressionPref);
                break;
            case XPS:
                mBaseAttributesCategory.addPreference(mXPSCompressionPref);
                break;
        }
    }

    private void showCustomSizePreference(SharedPreferences preferences, String key) {
        ScanSize scanSize = ScanSize.valueOf(preferences.getString(key, ScanSize.DEFAULT.name()));

        if (scanSize == ScanSize.CUSTOM) {
            mCustomLengthPref.setSummary(mCustomLengthPref.getText());
            mCustomWidthPref.setSummary(mCustomWidthPref.getText());
            mBaseAttributesCategory.addPreference(mCustomLengthPref);
            mBaseAttributesCategory.addPreference(mCustomWidthPref);
        } else {
            mBaseAttributesCategory.removePreference(mCustomLengthPref);
            mBaseAttributesCategory.removePreference(mCustomWidthPref);
        }
    }

    private void supportTransmissionModeCaps(boolean isSupported, String name) {
        if (isSupported) {
            mBaseAttributesCategory.addPreference(mTransmissionPref);
            if (mTransmissionPref != null) {
                ArrayList<CharSequence> transmissionModeEntries = new ArrayList<>();
                ArrayList<CharSequence> transmissionModeValues = new ArrayList<>();

                for (ScanAttributes.TransmissionMode transmissionMode : mCaps.getTransmissionModeList()) {
                    if (name == ScanAttributes.Destination.HTTP.name()) {
                        transmissionModeEntries.add(transmissionMode.name()); //name
                        transmissionModeValues.add(transmissionMode.name()); //value
                    } else {
                        if (transmissionMode.name() != ScanAttributes.TransmissionMode.IMAGE.name()) {
                            transmissionModeEntries.add(transmissionMode.name()); //name
                            transmissionModeValues.add(transmissionMode.name()); //value
                        }
                    }
                }
                mTransmissionPref.setEntries(transmissionModeEntries.toArray(new CharSequence[transmissionModeEntries.size()]));
                mTransmissionPref.setEntryValues(transmissionModeValues.toArray(new CharSequence[transmissionModeValues.size()]));
                for (ScanAttributes.TransmissionMode transmissionMode : mCaps.getTransmissionModeList()) {
                    if(defaultTransmissionMode == transmissionMode.name()){
                        mTransmissionPref.setDefaultValue(transmissionMode.name());
                        mTransmissionPref.setValue(transmissionMode.name());
                    }
                }
                mTransmissionPref.setSummary("%s");
            }
        } else {
            mBaseAttributesCategory.removePreference(mTransmissionPref);
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_TRANSMISSION_MODE, ScanAttributes.TransmissionMode.DEFAULT.name());
            editor.apply();
        }
    }

    public void setDefaultScanAttributes(ScanAttributes scanAttributes) throws Exception {
        if (scanAttributes != null) {
            ScanAttributesReader scanAttributesReader = new ScanAttributesReader(scanAttributes);
            defaultTransmissionMode = scanAttributesReader.getTransmissionMode().name();
            setPreferenceEntryValue(PREF_BACKGROUND_CLEANUP, scanAttributesReader.getBackgroundCleanup().name());
            setPreferenceEntryValue(PREF_BLANK_IMAGE_REMOVAL_MODE, scanAttributesReader.getBlankImageRemovalMode().name());
            setPreferenceEntryValue(PREF_COLOR_DROPOUT_MODE, scanAttributesReader.getColorDropoutMode().name());
            setPreferenceEntryValue(PREF_COLOR_MODE, scanAttributesReader.getColorMode().name());
            setPreferenceEntryValue(PREF_CONTRAST_ADJUSTMENT, scanAttributesReader.getContrastAdjustment().name());
            setPreferenceEntryValue(PREF_CROP_MODE, scanAttributesReader.getCropMode().name());
            setPreferenceEntryValue(PREF_DARKNESS_ADJUSTMENT, scanAttributesReader.getDarknessAdjustment().name());
            setPreferenceEntryValue(PREF_DOC_FORMAT, scanAttributesReader.getDocumentFormat().name());
            setPreferenceEntryValue(PREF_JOB_ASSEMBLY_MODE, scanAttributesReader.getJobAssemblyMode().name());
            setPreferenceEntryValue(PREF_MEDIA_SOURCE, scanAttributesReader.getMediaSource().name());
            setPreferenceEntryValue(PREF_MEDIA_WEIGHT_ADJUSTMENT, scanAttributesReader.getMediaWeightAdjustment().name());
            setPreferenceEntryValue(PREF_MISFEED_DETECTION_MODE, scanAttributesReader.getMisfeedDetectionMode().name());
            setPreferenceEntryValue(PREF_ORIENTATION, scanAttributesReader.getOrientation().name());
            setPreferenceEntryValue(PREF_OUTPUT_QUALITY, scanAttributesReader.getOutputQuality().name());
            setPreferenceEntryValue(PREF_DUPLEX_MODE, scanAttributesReader.getPlex().name());
            setPreferenceEntryValue(PREF_PROGRESS_DIALOG_MODE, scanAttributesReader.getProgressDialogMode().name());
            setPreferenceEntryValue(PREF_RESOLUTION_TYPE, scanAttributesReader.getResolution().name());
            setPreferenceEntryValue(PREF_TEXT_PHOTO_OPTIMIZATION, scanAttributesReader.getTextPhotoOptimization().name());
            setPreferenceEntryValue(PREF_ORG_SIZE, scanAttributesReader.getScanSize().name());
            setPreferenceEntryValue(PREF_SHARPNESS_ADJUSTMENT, scanAttributesReader.getSharpnessAdjustment().name());
            setEditTextPreferenceValue(PREF_FILE_NAME, "");
            setEditTextPreferenceValue(PREF_MAX_PAGES_PER_ATTACHMENT, String.valueOf(scanAttributesReader.getMaxPagesPerAttachment()));

            if (scanAttributesReader.getSplitAttachmentByPage() != null) {
                setPreferenceEntryValue(PREF_SPLIT_ATTACHMENT_BY_PAGE, scanAttributesReader.getSplitAttachmentByPage().name());
            }
            if (scanAttributesReader.getEraseMarginUnit() != null) {
                setPreferenceEntryValue(PREF_ERASE_MARGIN_UNIT, scanAttributesReader.getEraseMarginUnit().name());
            }

            Margins backMargin = new Margins(scanAttributesReader.getEraseBackLeftMargin(),
                    scanAttributesReader.getEraseBackTopMargin(),
                    scanAttributesReader.getEraseBackRightMargin(),
                    scanAttributesReader.getEraseBackBottomMargin());

            Margins frontMargin = new Margins(scanAttributesReader.getEraseFrontLeftMargin(),
                    scanAttributesReader.getEraseFrontTopMargin(),
                    scanAttributesReader.getEraseFrontRightMargin(),
                    scanAttributesReader.getEraseFrontBottomMargin());

            setMarginPreferenceValue(PREF_ERASE_BACK_MARGIN, backMargin);
            setMarginPreferenceValue(PREF_ERASE_FRONT_MARGIN, frontMargin);

            if (scanAttributesReader.getCaptureMode() != null) {
                setPreferenceEntryValue(PREF_CAPTURE_MODE, scanAttributesReader.getCaptureMode().name());
            }
            if (scanAttributesReader.getAutomaticToneMode() != null) {
                setPreferenceEntryValue(PREF_AUTOMATIC_TONE_MODE, scanAttributesReader.getAutomaticToneMode().name());
            }
            if (scanAttributesReader.getAutomaticStraightenMode() != null) {
                setPreferenceEntryValue(PREF_AUTOMATIC_STRAIGHTEN_MODE, scanAttributesReader.getAutomaticStraightenMode().name());
            }
            if (scanAttributesReader.getScanPreview() != null) {
                setPreferenceEntryValue(PREF_SCAN_PREVIEW, scanAttributesReader.getScanPreview().name());
            }
        }
    }

    private void setMarginPreferenceValue(String prefEraseFrontMargin, Margins margin) throws Exception {
        MarginsPreference eraseMarginPreference = (MarginsPreference)findPreference(prefEraseFrontMargin);
        if (eraseMarginPreference != null) {
            eraseMarginPreference.setMargins(margin.getLeftMargin(),
                    margin.getTopMargin(),
                    margin.getRightMargin(),
                    margin.getBottomMargin());
            eraseMarginPreference.applyMargins();
            eraseMarginPreference.setSummary(requireContext().getString(R.string.summary_margin, margin.getLeftMargin(), margin.getTopMargin(), margin.getRightMargin(), margin.getBottomMargin()));
        }
    }

    private void setEditTextPreferenceValue(String pref, String attribute) {
        EditTextPreference editTextPreference = (EditTextPreference)findPreference(pref);
          if (editTextPreference != null) {
             editTextPreference.setDefaultValue(attribute);
             editTextPreference.setText(attribute);
          }
    }

    private void setPreferenceEntryValue(String pref, String attribute) {
        ListPreference listPreference = (ListPreference) findPreference(pref);
        int index = 0;
        for (CharSequence value : listPreference.getEntryValues()) {
            if (value != null && value.toString().equals(attribute)) {
                break;
            }
            index++;
        }

        if (index < listPreference.getEntryValues().length) {
            listPreference.setValueIndex(index);
        }
    }

    private void fillFileOptionAttrCaps() {
        if (isSDKInitialized()) {
            ListPreference docPref = (ListPreference) findPreference(PREF_DOC_FORMAT);
            String docEntry = (String) docPref.getEntry();
            DocumentFormat docFormat = docEntry == null ? DocumentFormat.DEFAULT : ScanAttributes.DocumentFormat.valueOf(docEntry);

            ListPreference colorPref = (ListPreference) findPreference(PREF_COLOR_MODE);
            String colorEntry = (String) colorPref.getEntry();
            ColorMode colorMode = colorEntry == null ? ColorMode.DEFAULT : ScanAttributes.ColorMode.valueOf(colorEntry);

            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            FileOptionsAttributesCaps fileOptionsAttrCaps = ((MainActivity) getActivity()).requestFileOptionsCapabilities(colorMode, docFormat);
            if (fileOptionsAttrCaps != null) {
                EditTextPreference editPref = (EditTextPreference) findPreference(PREF_PDF_PASSWORD);
                boolean isPdfEncryptionSupport = fileOptionsAttrCaps.isPdfEncryptionPasswordSupported();
                if (editPref != null && isPdfEncryptionSupport) {
                    String password = mPrefs.getString(PREF_PDF_PASSWORD, null);
                    editPref.setText(password);
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(PREF_PDF_PASSWORD, null);
                    editor.apply();
                }

                ListPreference pref = (ListPreference) findPreference(PREF_OCR_LANGUAGE);

                ArrayList<CharSequence> ocrEntries = new ArrayList<>();
                ArrayList<CharSequence> ocrEntryValues = new ArrayList<>();

                for (FileOptionsAttributes.OcrLanguage language : fileOptionsAttrCaps.getOcrLanguageList()) {
                    if (language != null && language.name() != null) {
                        ocrEntries.add(language.name());
                        ocrEntryValues.add(language.name());
                    }
                }

                if (pref != null) {
                    pref.setEntries(ocrEntries.toArray(new CharSequence[ocrEntries.size()]));
                    pref.setEntryValues(ocrEntryValues.toArray(new CharSequence[ocrEntryValues.size()]));
                    pref.setDefaultValue(FileOptionsAttributes.OcrLanguage.DEFAULT.name());
                    pref.setValueIndex(0);
                    pref.setSummary("%s");
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(PREF_OCR_LANGUAGE, FileOptionsAttributes.OcrLanguage.DEFAULT.name());
                    editor.apply();
                }

                pref = (ListPreference) findPreference(PREF_PDF_COMPRESSION);

                ArrayList<CharSequence> pdfCompEntries = new ArrayList<>();
                ArrayList<CharSequence> pdfCompEntryValues = new ArrayList<>();

                for (FileOptionsAttributes.PdfCompressionMode compressionMode : fileOptionsAttrCaps.getPdfCompressionModeList()) {
                    pdfCompEntries.add(compressionMode.name());
                    pdfCompEntryValues.add(compressionMode.name());
                }

                if (pref != null) {
                    pref.setEntries(pdfCompEntries.toArray(new CharSequence[pdfCompEntries.size()]));
                    pref.setEntryValues(pdfCompEntryValues.toArray(new CharSequence[pdfCompEntryValues.size()]));
                    pref.setDefaultValue(FileOptionsAttributes.PdfCompressionMode.DEFAULT.name());
                    pref.setValueIndex(0);
                    pref.setSummary("%s");
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(PREF_PDF_COMPRESSION, FileOptionsAttributes.PdfCompressionMode.DEFAULT.name());
                    editor.apply();
                }

                pref = (ListPreference) findPreference(PREF_TIFF_COMPRESSION);

                ArrayList<CharSequence> tiffCompEntries = new ArrayList<>();
                ArrayList<CharSequence> tiffCompEntryValues = new ArrayList<>();

                for (FileOptionsAttributes.TiffCompressionMode tiffComp : fileOptionsAttrCaps.getTiffCompressionModeList()) {
                    tiffCompEntries.add(tiffComp.name());
                    tiffCompEntryValues.add(tiffComp.name());
                }

                if (pref != null) {
                    pref.setEntries(tiffCompEntries.toArray(new CharSequence[tiffCompEntries.size()]));
                    pref.setEntryValues(tiffCompEntryValues.toArray(new CharSequence[tiffCompEntryValues.size()]));
                    pref.setDefaultValue(FileOptionsAttributes.TiffCompressionMode.DEFAULT.name());
                    pref.setValueIndex(0);
                    pref.setSummary("%s");
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(PREF_TIFF_COMPRESSION, FileOptionsAttributes.TiffCompressionMode.DEFAULT.name());
                    editor.apply();
                }

                pref = (ListPreference) findPreference(PREF_XPS_COMPRESSION);

                ArrayList<CharSequence> xpsCompEntries = new ArrayList<>();
                ArrayList<CharSequence> xpsCompEntryValues = new ArrayList<>();

                for (FileOptionsAttributes.XpsCompressionMode xpsComp : fileOptionsAttrCaps.getXpsCompressionModeList()) {
                    xpsCompEntries.add(xpsComp.name());
                    xpsCompEntryValues.add(xpsComp.name());
                }

                if (pref != null) {
                    pref.setEntries(xpsCompEntries.toArray(new CharSequence[xpsCompEntries.size()]));
                    pref.setEntryValues(xpsCompEntryValues.toArray(new CharSequence[xpsCompEntryValues.size()]));
                    pref.setDefaultValue(FileOptionsAttributes.XpsCompressionMode.DEFAULT.name());
                    pref.setValueIndex(0);
                    pref.setSummary("%s");
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(PREF_XPS_COMPRESSION, FileOptionsAttributes.XpsCompressionMode.DEFAULT.name());
                    editor.apply();
                }
            }
        }
    }

    private void fillUSBStorages() {
        ListPreference pref = (ListPreference) findPreference(PREF_USB_STORAGE);

        if (pref != null) {
            Result result = new Result();
            if (!MassStorageService.isSupported(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.mass_storage_not_supported), Toast.LENGTH_SHORT).show();
                return;
            }
            List<MassStorageInfo> usbStorageList = MassStorageService.getStorageList(getActivity(), result);
            if (result.getCode() != Result.RESULT_OK || usbStorageList == null) {
                usbStorageList = Collections.emptyList();
            }

            final List<CharSequence> entries = new ArrayList<>();
            final List<CharSequence> entriesValues = new ArrayList<>();

            for (MassStorageInfo storage : usbStorageList) {
                if (storage.getType() == MassStorageInfo.StorageType.USB && storage.isMounted()) {
                    entries.add(storage.getName());
                    entriesValues.add(storage.getExternalFileDirectory());
                }
            }

            pref.setEntries(entries.toArray(new CharSequence[entries.size()]));
            pref.setEntryValues(entriesValues.toArray(new CharSequence[entriesValues.size()]));

            if (!entries.isEmpty()) {
                pref.setDefaultValue(entriesValues.get(0));
                pref.setValueIndex(0);
                pref.setSummary("%s");
            }
        }
    }

    public boolean isSDKInitialized() {
        return isSDKInitialized;
    }

    public void setSDKInitialized(boolean SDKInitialized) {
        isSDKInitialized = SDKInitialized;
    }
}