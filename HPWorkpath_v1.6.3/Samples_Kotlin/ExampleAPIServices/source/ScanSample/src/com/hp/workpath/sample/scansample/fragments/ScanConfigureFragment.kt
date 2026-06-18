// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.hp.workpath.api.Result
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.api.massstorage.MassStorageService
import com.hp.workpath.api.scanner.*
import com.hp.workpath.api.scanner.ScanAttributes.AutomaticToneMode
import com.hp.workpath.api.scanner.ScanAttributes.AutomaticStraightenMode
import com.hp.workpath.api.scanner.ScanAttributes.BackgroundCleanup
import com.hp.workpath.api.scanner.ScanAttributes.BlankImageRemovalMode
import com.hp.workpath.api.scanner.ScanAttributes.CaptureMode
import com.hp.workpath.api.scanner.ScanAttributes.ColorDropoutMode
import com.hp.workpath.api.scanner.ScanAttributes.ColorMode
import com.hp.workpath.api.scanner.ScanAttributes.ContrastAdjustment
import com.hp.workpath.api.scanner.ScanAttributes.CropMode
import com.hp.workpath.api.scanner.ScanAttributes.DarknessAdjustment
import com.hp.workpath.api.scanner.ScanAttributes.Destination
import com.hp.workpath.api.scanner.ScanAttributes.DocumentFormat
import com.hp.workpath.api.scanner.ScanAttributes.Duplex
import com.hp.workpath.api.scanner.ScanAttributes.EraseMarginUnit
import com.hp.workpath.api.scanner.ScanAttributes.JobAssemblyMode
import com.hp.workpath.api.scanner.ScanAttributes.MediaSource
import com.hp.workpath.api.scanner.ScanAttributes.MediaWeightAdjustment
import com.hp.workpath.api.scanner.ScanAttributes.MisfeedDetectionMode
import com.hp.workpath.api.scanner.ScanAttributes.OutputQuality
import com.hp.workpath.api.scanner.ScanAttributes.Orientation
import com.hp.workpath.api.scanner.ScanAttributes.ProgressDialogMode
import com.hp.workpath.api.scanner.ScanAttributes.Resolution
import com.hp.workpath.api.scanner.ScanAttributes.ScanSize
import com.hp.workpath.api.scanner.ScanAttributes.SharpnessAdjustment
import com.hp.workpath.api.scanner.ScanAttributes.TextPhotoOptimization
import com.hp.workpath.api.scanner.ScanAttributes.ScanPreview
import com.hp.workpath.api.scanner.ScanAttributes.SplitAttachmentByPage
import com.hp.workpath.api.scanner.ScanAttributes.TransmissionMode
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R
import com.hp.workpath.sample.scansample.fragments.EmailSmtpSettingFragment.Companion.DEFAULT_PORT

/**
 * Simple [PreferenceFragmentCompat] to set Scan Attributes and save into preferences.
 */
class ScanConfigureFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var mCaps: ScanAttributesCaps? = null
    private var mHttpCategory: PreferenceCategory? = null
    private var mFtpCategory: PreferenceCategory? = null
    private var mNetworkFolderCategory: PreferenceCategory? = null
    private var mEmailCategory: PreferenceCategory? = null
    private var mUsbCategory: PreferenceCategory? = null
    private var mBaseAttributesCategory: PreferenceCategory? = null
    private var mFeedbackCategory: PreferenceCategory? = null

    private var mFilenamePref: EditTextPreference? = null
    private var mFileUriHttpPref: EditTextPreference? = null
    private var mFileUriFtpPref: EditTextPreference? = null
    private var mFileUriNetworkFolderPref: EditTextPreference? = null
    private var mFileUriHttpUsernamePref: EditTextPreference? = null
    private var mFileUriHttpPasswordPref: EditTextPreference? = null
    private var mFileUriFtpUsernamePref: EditTextPreference? = null
    private var mFileUriFtpPasswordPref: EditTextPreference? = null
    private var mFileUriNetworkFolderUsernamePref: EditTextPreference? = null
    private var mFileUriNetworkFolderPasswordPref: EditTextPreference? = null
    private var mFileUriNetworkFolderDomainPref: EditTextPreference? = null
    private var mEmailToPref: EditTextPreference? = null
    private var mEmailCcPref: EditTextPreference? = null
    private var mEmailBccPref: EditTextPreference? = null
    private var mUsbFolderPref: EditTextPreference? = null
    private var mEmailSmtpPref: CheckBoxPreference? = null

    private var mTransmissionPref: ListPreference? = null
    private var mPDFCompressionPref: ListPreference? = null
    private var mOCRLanguagePref: ListPreference? = null
    private var mPDFPasswordPref: EditTextPreference? = null
    private var mTIFFCompressionPref: ListPreference? = null
    private var mXPSCompressionPref: ListPreference? = null
    private var mCustomLengthPref: EditTextFloatPreference? = null
    private var mCustomWidthPref: EditTextFloatPreference? = null
    private var mMaxPagesPerAttachmentPref: EditTextIntegerPreference? = null
    private var mEraseBackMarginPref: MarginsPreference? = null
    private var mEraseFrontMarginPref: MarginsPreference? = null

    private var mMonitorJobPref: CheckBoxPreference? = null
    private var mShowJobProgressPref: CheckBoxPreference? = null
    private var mSettingsUIPref: CheckBoxPreference? = null
    private var mAllowMultipleScanPref: CheckBoxPreference? = null

    var isSDKInitialized: Boolean = false

    var defaultTransmissionMode:String = TransmissionMode.JOB.name;

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.scan_preferences)

        mFilenamePref = findPreference(PREF_FILE_NAME)
        mFilenamePref?.text = null
        mFileUriHttpPref = findPreference(PREF_URI_HTTP)
        mFileUriHttpPref?.text = null
        mFileUriFtpPref = findPreference(PREF_URI_FTP)
        mFileUriFtpPref?.text = null
        mFileUriNetworkFolderPref = findPreference(PREF_URI_NETWORK_FOLDER)
        mFileUriNetworkFolderPref?.text = null
        mFileUriHttpUsernamePref = findPreference(PREF_URI_HTTP_USERNAME)
        mFileUriHttpUsernamePref?.text = null
        mFileUriHttpPasswordPref = findPreference(PREF_URI_NETWORK_FOLDER)
        mFileUriHttpPasswordPref?.text = null

        mFileUriFtpUsernamePref = findPreference(PREF_URI_FTP_USERNAME)
        mFileUriFtpUsernamePref?.text = null
        mFileUriFtpPasswordPref = findPreference(PREF_URI_FTP_PASSWORD)
        mFileUriFtpPasswordPref?.text = null

        mFileUriNetworkFolderUsernamePref = findPreference(PREF_URI_NETWORK_FOLDER_USERNAME)
        mFileUriNetworkFolderUsernamePref?.text = null
        mFileUriNetworkFolderPasswordPref = findPreference(PREF_URI_NETWORK_FOLDER_PASSWORD)
        mFileUriNetworkFolderPasswordPref?.text = null
        mFileUriNetworkFolderDomainPref = findPreference(PREF_URI_NETWORK_FOLDER_DOMAIN)
        mFileUriNetworkFolderDomainPref?.text = null

        mUsbFolderPref = findPreference(PREF_USB_LOCATION)
        mUsbFolderPref?.text = null

        mEmailToPref = findPreference(PREF_EMAIL_TO)
        mEmailCcPref = findPreference(PREF_EMAIL_CC)
        mEmailBccPref = findPreference(PREF_EMAIL_BCC)
        mEmailSmtpPref = findPreference(PREF_EMAIL_SMTP)
        mEmailSmtpPref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    EmailSmtpSettingFragment().show(
                        parentFragmentManager,
                        requireActivity().getString(R.string.pref_email_smtp_title)
                    )
                }
                true
            }

        mTransmissionPref = findPreference(PREF_TRANSMISSION_MODE)
        mPDFCompressionPref = findPreference(PREF_PDF_COMPRESSION)
        mOCRLanguagePref = findPreference(PREF_OCR_LANGUAGE)
        mPDFPasswordPref = findPreference(PREF_PDF_PASSWORD)
        mTIFFCompressionPref = findPreference(PREF_TIFF_COMPRESSION)
        mXPSCompressionPref = findPreference(PREF_XPS_COMPRESSION)
        mCustomLengthPref = findPreference(PREF_CUSTOM_LENGTH)
        mCustomLengthPref?.setLimits(0f, 0f)
        mCustomLengthPref?.text = ""
        mCustomWidthPref = findPreference(PREF_CUSTOM_WIDTH)
        mCustomWidthPref?.setLimits(0f, 0f)
        mCustomWidthPref?.text = ""
        mMaxPagesPerAttachmentPref = findPreference(PREF_MAX_PAGES_PER_ATTACHMENT)
        mMaxPagesPerAttachmentPref?.setLimits(0, 0)
        mMaxPagesPerAttachmentPref?.text = ""
        mEraseBackMarginPref = findPreference(PREF_ERASE_BACK_MARGIN)
        mEraseFrontMarginPref = findPreference(PREF_ERASE_FRONT_MARGIN)

        mHttpCategory = findPreference(PREF_DESTINATION_HTTP_CATEGORY)!!
        mFtpCategory = findPreference(PREF_DESTINATION_FTP_CATEGORY)!!
        mNetworkFolderCategory = findPreference(PREF_DESTINATION_NETWORK_FOLDER_CATEGORY)!!
        mEmailCategory = findPreference(PREF_DESTINATION_EMAIL_CATEGORY)!!
        mUsbCategory = findPreference(PREF_DESTINATION_USB_CATEGORY)!!
        mBaseAttributesCategory = findPreference(PREF_BASE_ATTRIBUTES_CATEGORY)!!
        mFeedbackCategory = findPreference(PREF_DESTINATION_FEEDBACK_CATEGORY)!!

        mMonitorJobPref = findPreference(PREF_MONITOR_JOB)!!
        mShowJobProgressPref = findPreference(PREF_SHOW_JOB_PROGRESS)!!
        mSettingsUIPref = findPreference(PREF_SETTINGS_UI)!!
        mAllowMultipleScanPref = findPreference(PREF_ALLOW_MULTIPLE_SCAN)!!

        mMonitorJobPref?.isChecked = true
        mShowJobProgressPref?.isChecked = true
        mSettingsUIPref?.isChecked = false
        mAllowMultipleScanPref?.isChecked = false
    }

    override fun onResume() {
        super.onResume()
        val prefs = preferenceScreen.sharedPreferences
        prefs.registerOnSharedPreferenceChangeListener(this)
        refreshAllPrefs(prefs)
    }

    override fun onPause() {
        super.onPause()
        val prefs = preferenceScreen.sharedPreferences
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Refreshes all values in the fragment
     *
     * @param prefs [SharedPreferences]
     */
    private fun refreshAllPrefs(prefs: SharedPreferences) {
        onSharedPreferenceChanged(prefs, PREF_DESTINATION)
        onSharedPreferenceChanged(prefs, PREF_FILE_NAME)
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE)
        onSharedPreferenceChanged(prefs, PREF_DUPLEX_MODE)
        onSharedPreferenceChanged(prefs, PREF_ORIENTATION)
        onSharedPreferenceChanged(prefs, PREF_RESOLUTION_TYPE)
        onSharedPreferenceChanged(prefs, PREF_ORG_SIZE)
        onSharedPreferenceChanged(prefs, PREF_CUSTOM_LENGTH)
        onSharedPreferenceChanged(prefs, PREF_CUSTOM_WIDTH)
        onSharedPreferenceChanged(prefs, PREF_DOC_FORMAT)
        onSharedPreferenceChanged(prefs, PREF_SCAN_PREVIEW)
        onSharedPreferenceChanged(prefs, PREF_BACKGROUND_CLEANUP)
        onSharedPreferenceChanged(prefs, PREF_CONTRAST_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_DARKNESS_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_BLANK_IMAGE_REMOVAL_MODE)
        onSharedPreferenceChanged(prefs, PREF_COLOR_DROPOUT_MODE)
        onSharedPreferenceChanged(prefs, PREF_CROP_MODE)
        onSharedPreferenceChanged(prefs, PREF_PROGRESS_DIALOG_MODE)
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_QUALITY)
        onSharedPreferenceChanged(prefs, PREF_TRANSMISSION_MODE)
        onSharedPreferenceChanged(prefs, PREF_JOB_ASSEMBLY_MODE)
        onSharedPreferenceChanged(prefs, PREF_SHARPNESS_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_MEDIA_WEIGHT_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_TEXT_PHOTO_OPTIMIZATION)
        onSharedPreferenceChanged(prefs, PREF_MEDIA_SOURCE)
        onSharedPreferenceChanged(prefs, PREF_MISFEED_DETECTION_MODE)
        onSharedPreferenceChanged(prefs, PREF_SPLIT_ATTACHMENT_BY_PAGE)
        onSharedPreferenceChanged(prefs, PREF_MAX_PAGES_PER_ATTACHMENT)
        onSharedPreferenceChanged(prefs, PREF_ERASE_MARGIN_UNIT)
        onSharedPreferenceChanged(prefs, PREF_ERASE_BACK_MARGIN)
        onSharedPreferenceChanged(prefs, PREF_ERASE_FRONT_MARGIN)
        onSharedPreferenceChanged(prefs, PREF_CAPTURE_MODE)
        onSharedPreferenceChanged(prefs, PREF_AUTOMATIC_TONE_MODE)
        onSharedPreferenceChanged(prefs, PREF_AUTOMATIC_STRAIGHTEN_MODE)

        onSharedPreferenceChanged(prefs, PREF_MONITOR_JOB)
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS)
        onSharedPreferenceChanged(prefs, PREF_SETTINGS_UI)

        onSharedPreferenceChanged(prefs, PREF_EMAIL_TO)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_CC)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_BCC)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_FROM)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_SUBJECT)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_MESSAGE)
        onSharedPreferenceChanged(prefs, PREF_EMAIL_SMTP)

        onSharedPreferenceChanged(prefs, PREF_PDF_COMPRESSION)
        onSharedPreferenceChanged(prefs, PREF_OCR_LANGUAGE)
        onSharedPreferenceChanged(prefs, PREF_PDF_PASSWORD)
        onSharedPreferenceChanged(prefs, PREF_TIFF_COMPRESSION)
        onSharedPreferenceChanged(prefs, PREF_XPS_COMPRESSION)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is MarginsPreference) {
            val dialogFragment: DialogFragment =
                MarginsPreferenceFragment.newInstance(preference.getKey())
            dialogFragment.setTargetFragment(this, 1)
            dialogFragment.show(parentFragmentManager, "DIALOG")
        } else super.onDisplayPreferenceDialog(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)
        preference?.run {
            when (key) {
                PREF_DOC_FORMAT -> {
                    setColorMode(sharedPreferences, key)
                    showOptionalPreference(sharedPreferences, key)
                    fillFileOptionAttrCaps()
                }

                PREF_ORG_SIZE -> {
                    showCustomSizePreference(sharedPreferences, key)
                }

                PREF_COLOR_MODE -> {
                    fillFileOptionAttrCaps()
                }

                PREF_DESTINATION -> {
                    val entryStr = (preference as ListPreference).entry as String?
                    val destination =
                        if (entryStr == null) Destination.ME else Destination.valueOf(entryStr)

                    fillDocFormatPreferences(mCaps, PREF_DOC_FORMAT, destination)

                    when (destination) {
                        Destination.HTTP -> {
                            with(preferenceScreen) {
                                if (findPreference<Preference>(PREF_DESTINATION_HTTP_CATEGORY) == null) {
                                    addPreference(mHttpCategory)
                                }
                                removePreference(mFtpCategory)
                                removePreference(mNetworkFolderCategory)
                                removePreference(mEmailCategory)
                                removePreference(mUsbCategory)
                            }

                            mFileUriHttpUsernamePref = findPreference(PREF_URI_HTTP_USERNAME)
                            mFileUriHttpUsernamePref?.text = null
                            mFileUriHttpPasswordPref = findPreference(PREF_URI_HTTP_PASSWORD)
                            mFileUriHttpPasswordPref?.text = null

                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP)
                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP_USERNAME)
                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_HTTP_PASSWORD)
                            supportTransmissionModeCaps(true, Destination.HTTP.name)
                        }

                        Destination.FTP -> {
                            with(preferenceScreen) {
                                if (findPreference<Preference>(PREF_DESTINATION_FTP_CATEGORY) == null) {
                                    addPreference(mFtpCategory)
                                }
                                removePreference(mHttpCategory)
                                removePreference(mNetworkFolderCategory)
                                removePreference(mEmailCategory)
                                removePreference(mUsbCategory)
                            }

                            mFileUriFtpUsernamePref = findPreference(PREF_URI_FTP_USERNAME)
                            mFileUriFtpUsernamePref?.text = null
                            mFileUriFtpPasswordPref = findPreference(PREF_URI_FTP_PASSWORD)
                            mFileUriFtpPasswordPref?.text = null

                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP)
                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP_USERNAME)
                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_FTP_PASSWORD)
                            supportTransmissionModeCaps(true, Destination.FTP.name)
                        }

                        Destination.NETWORK_FOLDER -> {
                            with(preferenceScreen) {
                                if (findPreference<Preference>(
                                        PREF_DESTINATION_NETWORK_FOLDER_CATEGORY
                                    ) == null
                                ) {
                                    addPreference(mNetworkFolderCategory)
                                }
                                removePreference(mHttpCategory)
                                removePreference(mFtpCategory)
                                removePreference(mEmailCategory)
                                removePreference(mUsbCategory)
                            }

                            mFileUriNetworkFolderUsernamePref =
                                findPreference(PREF_URI_NETWORK_FOLDER_USERNAME)
                            mFileUriNetworkFolderUsernamePref?.text = null
                            mFileUriNetworkFolderPasswordPref =
                                findPreference(PREF_URI_NETWORK_FOLDER_PASSWORD)
                            mFileUriNetworkFolderPasswordPref?.text = null
                            mFileUriNetworkFolderDomainPref =
                                findPreference(PREF_URI_NETWORK_FOLDER_DOMAIN)
                            mFileUriNetworkFolderDomainPref?.text = null

                            onSharedPreferenceChanged(sharedPreferences, PREF_URI_NETWORK_FOLDER)
                            onSharedPreferenceChanged(
                                sharedPreferences,
                                PREF_URI_NETWORK_FOLDER_USERNAME
                            )
                            onSharedPreferenceChanged(
                                sharedPreferences,
                                PREF_URI_NETWORK_FOLDER_PASSWORD
                            )
                            onSharedPreferenceChanged(
                                sharedPreferences,
                                PREF_URI_NETWORK_FOLDER_DOMAIN
                            )

                            supportTransmissionModeCaps(true, Destination.NETWORK_FOLDER.name)
                        }

                        Destination.EMAIL -> {
                            with(preferenceScreen) {
                                if (findPreference<Preference>(PREF_DESTINATION_EMAIL_CATEGORY) == null) {
                                    addPreference(mEmailCategory)
                                }
                                removePreference(mHttpCategory)
                                removePreference(mFtpCategory)
                                removePreference(mNetworkFolderCategory)
                                removePreference(mUsbCategory)
                            }

                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_TO)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_CC)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_BCC)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_FROM)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_SUBJECT)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_MESSAGE)
                            onSharedPreferenceChanged(sharedPreferences, PREF_EMAIL_SMTP)
                            supportTransmissionModeCaps(true, Destination.EMAIL.name)
                        }

                        Destination.USB -> {
                            with(preferenceScreen) {
                                if (findPreference<Preference>(PREF_DESTINATION_USB_CATEGORY) == null) {
                                    addPreference(mUsbCategory)
                                }
                                removePreference(mHttpCategory)
                                removePreference(mFtpCategory)
                                removePreference(mNetworkFolderCategory)
                                removePreference(mEmailCategory)
                            }

                            fillUSBStorages()
                            onSharedPreferenceChanged(sharedPreferences, PREF_USB_STORAGE)
                            supportTransmissionModeCaps(false, Destination.USB.name)
                        }

                        else -> {
                            with(preferenceScreen) {
                                removePreference(mHttpCategory)
                                removePreference(mFtpCategory)
                                removePreference(mNetworkFolderCategory)
                                removePreference(mEmailCategory)
                                removePreference(mUsbCategory)
                            }
                            supportTransmissionModeCaps(false, Destination.ME.name)
                        }
                    }
                }

                PREF_USB_STORAGE -> {
                    val usbLocation = (preference as ListPreference).value
                    mUsbFolderPref?.text = usbLocation
                    onSharedPreferenceChanged(sharedPreferences, PREF_USB_LOCATION)
                }
            }

            if (preference is ListPreference) {
                val entry = preference.entry as String?

                if (TextUtils.isEmpty(entry)) {
                    preference.setValueIndex(0)
                    preference.summary = "%s"
                } else {
                    preference.summary = entry
                }
            } else if (preference is EditTextPreference) {
                var text: String? = preference.text
                if (PREF_URI_HTTP_PASSWORD == key
                    || PREF_URI_FTP_PASSWORD == key
                    || PREF_URI_NETWORK_FOLDER_PASSWORD == key
                    || PREF_PDF_PASSWORD == key
                ) {
                    text = getTransformationString(text)
                }
                preference.summary = text
            } else if (preference is CheckBoxPreference) {
                if (PREF_MONITOR_JOB == key) {
                    findPreference<CheckBoxPreference>(PREF_SHOW_JOB_PROGRESS)?.isEnabled =
                        preference.isChecked
                } else if (PREF_EMAIL_SMTP == key) {
                    val sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
                    val value = sharedPref.getBoolean(key, false)
                    preference.isChecked = value
                    if (value) {
                        val hostname = sharedPref.getString(
                            requireActivity().getString(R.string.pref_email_hostname),
                            ""
                        )
                        val port = sharedPref.getInt(
                            requireActivity().getString(R.string.pref_email_port),
                            DEFAULT_PORT
                        )
                        preference.summary = requireActivity().getString(
                            R.string.hint_email_smtp_enable,
                            hostname,
                            port
                        )
                    } else {
                        preference.summary = requireActivity().getString(R.string.hint_email_smtp)
                    }
                }
            }
        }
    }

    private fun getTransformationString(text: String?): String? {
        if (!TextUtils.isEmpty(text)) {
            val editText = EditText(activity)
            editText.setText(text)
            editText.transformationMethod = PasswordTransformationMethod()
            return editText.transformationMethod.getTransformation(text, editText).toString()
        }
        return null
    }

    /**
     * Applies capabilities to configuration screen
     *
     * @param caps [com.hp.workpath.api.scanner.ScanAttributesCaps]
     */
    fun loadCapabilities(caps: ScanAttributesCaps) {
        mCaps = caps

        // Load Resolutions
        var pref: ListPreference? = findPreference(PREF_RESOLUTION_TYPE)
        val resEntries = ArrayList<CharSequence>()
        val resEntryValues = ArrayList<CharSequence>()

        for (res in caps.resolutionList) {
            resEntries.add(res.name)
            resEntryValues.add(res.name)
        }

        pref?.let {
            it.entries = resEntries.toTypedArray()
            it.entryValues = resEntryValues.toTypedArray()
            it.setDefaultValue(Resolution.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load color mode
        pref = findPreference(PREF_COLOR_MODE)
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()

        for (cm in caps.colorModeList) {
            cmEntries.add(cm.name)
            cmEntryValues.add(cm.name)
        }

        pref?.let {
            it.entries = cmEntries.toTypedArray()
            it.entryValues = cmEntryValues.toTypedArray()
            it.setDefaultValue(ColorMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load duplex mode
        pref = findPreference(PREF_DUPLEX_MODE)
        val duEntries = ArrayList<CharSequence>()
        val duEntryValues = ArrayList<CharSequence>()

        for (du in caps.duplexList) {
            duEntries.add(du.name)
            duEntryValues.add(du.name)
        }

        pref?.let {
            it.entries = duEntries.toTypedArray()
            it.entryValues = duEntryValues.toTypedArray()
            it.setDefaultValue(Duplex.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Original Orientation
        pref = findPreference(PREF_ORIENTATION)
        val orientationEntries = ArrayList<CharSequence>()
        val orientationEntryValues = ArrayList<CharSequence>()

        for (ori in caps.orientationList) {
            orientationEntries.add(ori.name) //name
            orientationEntryValues.add(ori.name) //value
        }

        pref?.let {
            it.entries = orientationEntries.toTypedArray()
            it.entryValues = orientationEntryValues.toTypedArray()
            it.setDefaultValue(Orientation.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Background Clean up
        pref = findPreference(PREF_BACKGROUND_CLEANUP)
        val backgroundCleanupEntries = ArrayList<CharSequence>()
        val backgroundCleanupValues = ArrayList<CharSequence>()

        for (backgroundCleanup in caps.backgroundCleanupList) {
            backgroundCleanupEntries.add(backgroundCleanup.name) //name
            backgroundCleanupValues.add(backgroundCleanup.name) //value
        }

        pref?.let {
            it.entries = backgroundCleanupEntries.toTypedArray()
            it.entryValues = backgroundCleanupValues.toTypedArray()
            it.setDefaultValue(BackgroundCleanup.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Contrast Adjustment
        pref = findPreference(PREF_CONTRAST_ADJUSTMENT)
        val contrastAdjustmentEntries = ArrayList<CharSequence>()
        val contrastAdjustmentValues = ArrayList<CharSequence>()

        for (contrastAdjustment in caps.contrastAdjustmentList) {
            contrastAdjustmentEntries.add(contrastAdjustment.name) //name
            contrastAdjustmentValues.add(contrastAdjustment.name) //value
        }

        pref?.let {
            it.entries = contrastAdjustmentEntries.toTypedArray()
            it.entryValues = contrastAdjustmentValues.toTypedArray()
            it.setDefaultValue(ContrastAdjustment.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Darkness Adjustment
        pref = findPreference(PREF_DARKNESS_ADJUSTMENT)
        val darknessAdjustmentEntries = ArrayList<CharSequence>()
        val darknessAdjustmentValues = ArrayList<CharSequence>()

        for (darknessAdjustment in caps.darknessAdjustmentList) {
            darknessAdjustmentEntries.add(darknessAdjustment.name) //name
            darknessAdjustmentValues.add(darknessAdjustment.name) //value
        }

        pref?.let {
            it.entries = darknessAdjustmentEntries.toTypedArray()
            it.entryValues = darknessAdjustmentValues.toTypedArray()
            it.setDefaultValue(DarknessAdjustment.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Black Image Removal mode
        pref = findPreference(PREF_BLANK_IMAGE_REMOVAL_MODE)
        val blankImageRemovalEntries = ArrayList<CharSequence>()
        val blankImageRemovalValues = ArrayList<CharSequence>()

        for (blankImageRemovalMode in caps.blankImageRemovalModeList) {
            blankImageRemovalEntries.add(blankImageRemovalMode.name) //name
            blankImageRemovalValues.add(blankImageRemovalMode.name) //value
        }

        pref?.let {
            it.entries = blankImageRemovalEntries.toTypedArray()
            it.entryValues = blankImageRemovalValues.toTypedArray()
            it.setDefaultValue(BlankImageRemovalMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Color Dropout mode
        pref = findPreference(PREF_COLOR_DROPOUT_MODE)
        val colorDropoutModeEntries = ArrayList<CharSequence>()
        val colorDropoutModeValues = ArrayList<CharSequence>()

        for (colorDropoutMode in caps.colorDropoutModeList) {
            colorDropoutModeEntries.add(colorDropoutMode.name) //name
            colorDropoutModeValues.add(colorDropoutMode.name) //value
        }

        pref?.let {
            it.entries = colorDropoutModeEntries.toTypedArray()
            it.entryValues = colorDropoutModeValues.toTypedArray()
            it.setDefaultValue(ColorDropoutMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Crop mode
        pref = findPreference(PREF_CROP_MODE)
        val cropModeEntries = ArrayList<CharSequence>()
        val cropModeValues = ArrayList<CharSequence>()

        for (cropMode in caps.cropModeList) {
            cropModeEntries.add(cropMode.name) //name
            cropModeValues.add(cropMode.name) //value
        }

        pref?.let {
            it.entries = cropModeEntries.toTypedArray()
            it.entryValues = cropModeValues.toTypedArray()
            it.setDefaultValue(CropMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Original Size
        pref = findPreference(PREF_ORG_SIZE)
        val osEntries = ArrayList<CharSequence>()
        val osEntryValues = ArrayList<CharSequence>()

        for (os in caps.scanSizeList) {
            Log.d(
                "TAG",
                "loadCapabilities: ${os.name} (${os.width} X ${os.height} ${os.unit})"
            )
            val entrie = if (os.width == 0.0 && os.height == 0.0 && os.unit == null) {
                os.name
            } else {
                "${os.name} (${os.width} X ${os.height} ${os.unit})"
            }
            osEntries.add(entrie)
            osEntryValues.add(os.name)
        }

        pref?.let {
            it.entries = osEntries.toTypedArray()
            it.entryValues = osEntryValues.toTypedArray()
            it.setDefaultValue(ScanSize.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Destination
        pref = findPreference(PREF_DESTINATION)
        val destEntries = ArrayList<CharSequence>()
        val destEntryValues = ArrayList<CharSequence>()

        for (dest in caps.destinationList) {
            destEntries.add(dest.name)
            destEntryValues.add(dest.name)
        }

        pref?.let {
            it.entries = destEntries.toTypedArray()
            it.entryValues = destEntryValues.toTypedArray()
            // ME is always presented
            it.setDefaultValue(caps.destinationList[0])
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Scan Preview
        pref = findPreference(PREF_SCAN_PREVIEW)
        val scanPreviewEntries = ArrayList<CharSequence>()
        val scanPreviewEntryValues = ArrayList<CharSequence>()

        for (scanPreview in caps.scanPreviewList) {
            scanPreviewEntries.add(scanPreview.name)
            scanPreviewEntryValues.add(scanPreview.name)
        }

        pref?.let {
            it.entries = scanPreviewEntries.toTypedArray()
            it.entryValues = scanPreviewEntryValues.toTypedArray()
            it.setDefaultValue(ScanPreview.DEFAULT)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Progress Dialog mode
        pref = findPreference(PREF_PROGRESS_DIALOG_MODE)
        val progressDialogModeEntries = ArrayList<CharSequence>()
        val progressDialogModeValues = ArrayList<CharSequence>()

        for (progressDialogMode in caps.progressDialogModeList) {
            progressDialogModeEntries.add(progressDialogMode.name) //name
            progressDialogModeValues.add(progressDialogMode.name) //value
        }

        pref?.let {
            it.entries = progressDialogModeEntries.toTypedArray()
            it.entryValues = progressDialogModeValues.toTypedArray()
            it.setDefaultValue(ProgressDialogMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Output Quality
        pref = findPreference(PREF_OUTPUT_QUALITY)
        val outputQualityEntries = ArrayList<CharSequence>()
        val outputQualityValues = ArrayList<CharSequence>()

        for (outputQuality in caps.outputQualityList) {
            outputQualityEntries.add(outputQuality.name) //name
            outputQualityValues.add(outputQuality.name) //value
        }

        pref?.let {
            it.entries = outputQualityEntries.toTypedArray()
            it.entryValues = outputQualityValues.toTypedArray()
            it.setDefaultValue(OutputQuality.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Job Assembly mode
        pref = findPreference(PREF_JOB_ASSEMBLY_MODE)
        val jobAssemblyModeEntries = ArrayList<CharSequence>()
        val jobAssemblyModeValues = ArrayList<CharSequence>()

        for (jobAssemblyMode in caps.jobAssemblyModeList) {
            jobAssemblyModeEntries.add(jobAssemblyMode.name) //name
            jobAssemblyModeValues.add(jobAssemblyMode.name) //value
        }

        pref?.let {
            it.entries = jobAssemblyModeEntries.toTypedArray()
            it.entryValues = jobAssemblyModeValues.toTypedArray()
            it.setDefaultValue(JobAssemblyMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Sharpness Adjustment
        pref = findPreference(PREF_SHARPNESS_ADJUSTMENT)
        val sharpnessAdjustmentEntries = ArrayList<CharSequence>()
        val sharpnessAdjustmentValues = ArrayList<CharSequence>()

        for (sharpnessAdjustment in caps.sharpnessAdjustmentList) {
            sharpnessAdjustmentEntries.add(sharpnessAdjustment.name) //name
            sharpnessAdjustmentValues.add(sharpnessAdjustment.name) //value
        }

        pref?.let {
            it.entries = sharpnessAdjustmentEntries.toTypedArray()
            it.entryValues = sharpnessAdjustmentValues.toTypedArray()
            it.setDefaultValue(SharpnessAdjustment.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Media Weight Adjustment
        pref = findPreference(PREF_MEDIA_WEIGHT_ADJUSTMENT)
        val mediaWeightAdjustmentEntries = ArrayList<CharSequence>()
        val mediaWeightAdjustmentValues = ArrayList<CharSequence>()

        for (mediaWeightAdjustment in caps.mediaWeightAdjustmentList) {
            mediaWeightAdjustmentEntries.add(mediaWeightAdjustment.name) //name
            mediaWeightAdjustmentValues.add(mediaWeightAdjustment.name) //value
        }

        pref?.let {
            it.entries = mediaWeightAdjustmentEntries.toTypedArray()
            it.entryValues = mediaWeightAdjustmentValues.toTypedArray()
            it.setDefaultValue(MediaWeightAdjustment.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Text Photo Optimization
        pref = findPreference(PREF_TEXT_PHOTO_OPTIMIZATION)
        val textPhotoOptimizationEntries = ArrayList<CharSequence>()
        val textPhotoOptimizationValues = ArrayList<CharSequence>()

        for (textPhotoOptimization in caps.textPhotoOptimizationList) {
            textPhotoOptimizationEntries.add(textPhotoOptimization.name) //name
            textPhotoOptimizationValues.add(textPhotoOptimization.name) //value
        }

        pref?.let {
            it.entries = textPhotoOptimizationEntries.toTypedArray()
            it.entryValues = textPhotoOptimizationValues.toTypedArray()
            it.setDefaultValue(TextPhotoOptimization.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Media Source
        pref = findPreference(PREF_MEDIA_SOURCE)
        val mediaSourceEntries = ArrayList<CharSequence>()
        val mediaSourceValues = ArrayList<CharSequence>()

        for (mediaSource in caps.mediaSourceList) {
            mediaSourceEntries.add(mediaSource.name) //name
            mediaSourceValues.add(mediaSource.name) //value
        }

        pref?.let {
            it.entries = mediaSourceEntries.toTypedArray()
            it.entryValues = mediaSourceValues.toTypedArray()
            it.setDefaultValue(MediaSource.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Misfeed Detection mode
        pref = findPreference(PREF_MISFEED_DETECTION_MODE)
        val misfeedDetectionModeEntries = ArrayList<CharSequence>()
        val misfeedDetectionModeValues = ArrayList<CharSequence>()

        for (misfeedDetectionMode in caps.misfeedDetectionModeList) {
            misfeedDetectionModeEntries.add(misfeedDetectionMode.name) //name
            misfeedDetectionModeValues.add(misfeedDetectionMode.name) //value
        }

        pref?.let {
            it.entries = misfeedDetectionModeEntries.toTypedArray()
            it.entryValues = misfeedDetectionModeValues.toTypedArray()
            it.setDefaultValue(MisfeedDetectionMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        //Load Custom length, height
        mCustomLengthPref?.setLimits(
            caps.customLengthRange.lowerBound,
            caps.customLengthRange.upperBound
        )
        mCustomWidthPref?.setLimits(
            caps.customWidthRange.lowerBound,
            caps.customWidthRange.upperBound
        )

        //Load Split Attachment by page
        if (caps.splitAttachmentByPageList.size > 0) {
            pref = findPreference(PREF_SPLIT_ATTACHMENT_BY_PAGE)
            val splitAttachmentByPageEntries = ArrayList<CharSequence>()
            val splitAttachmentByPageValues = ArrayList<CharSequence>()
            for (splitAttachmentByPage in caps.splitAttachmentByPageList) {
                splitAttachmentByPageEntries.add(splitAttachmentByPage.name) //name
                splitAttachmentByPageValues.add(splitAttachmentByPage.name) //value
            }
            pref?.let {
                it.entries = splitAttachmentByPageEntries.toTypedArray()
                it.entryValues = splitAttachmentByPageValues.toTypedArray()
                it.setDefaultValue(SplitAttachmentByPage.DEFAULT.name)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }

        if (caps.maxPagesPerAttachmentRange != null) {
            mMaxPagesPerAttachmentPref?.setLimits(
                caps.maxPagesPerAttachmentRange.lowerBound.toInt(),
                caps.maxPagesPerAttachmentRange.upperBound.toInt()
            )
        }

        //Load erase margin unit
        if (caps.eraseMarginUnitList.size > 0) {
            pref = findPreference(PREF_ERASE_MARGIN_UNIT)
            val eraseMarginUnitEntries = ArrayList<CharSequence>()
            val eraseMarginUnitValues = ArrayList<CharSequence>()
            for (eraseMarginUnit in caps.eraseMarginUnitList) {
                eraseMarginUnitEntries.add(eraseMarginUnit.name) //name
                eraseMarginUnitValues.add(eraseMarginUnit.name) //value
            }
            pref?.let {
                it.entries = eraseMarginUnitEntries.toTypedArray()
                it.entryValues = eraseMarginUnitValues.toTypedArray()
                it.setDefaultValue(EraseMarginUnit.DEFAULT.name)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }

        if (caps.eraseBackBottomRange != null) {
            mEraseBackMarginPref?.let {
                it.setBottomLimits(
                    caps.eraseBackBottomRange.lowerBound,
                    caps.eraseBackBottomRange.upperBound
                )
                it.setLeftLimits(
                    caps.eraseBackLeftRange.lowerBound,
                    caps.eraseBackLeftRange.upperBound
                )
                it.setRightLimits(
                    caps.eraseBackRightRange.lowerBound,
                    caps.eraseBackRightRange.upperBound
                )
                it.setTopLimits(
                    caps.eraseBackTopRange.lowerBound,
                    caps.eraseBackTopRange.upperBound
                )
            }
        }

        if (caps.eraseFrontBottomRange != null) {
            mEraseFrontMarginPref?.let {
                it.setBottomLimits(
                    caps.eraseFrontBottomRange.lowerBound,
                    caps.eraseFrontBottomRange.upperBound
                )
                it.setLeftLimits(
                    caps.eraseFrontLeftRange.lowerBound,
                    caps.eraseFrontLeftRange.upperBound
                )
                it.setRightLimits(
                    caps.eraseFrontRightRange.lowerBound,
                    caps.eraseFrontRightRange.upperBound
                )
                it.setTopLimits(
                    caps.eraseFrontTopRange.lowerBound,
                    caps.eraseFrontTopRange.upperBound
                )
            }
        }

        //Load Capture mode
        if (caps.captureModeList.size > 0) {
            pref = findPreference(PREF_CAPTURE_MODE)
            val captureModeEntries = ArrayList<CharSequence>()
            val captureModeValues = ArrayList<CharSequence>()
            for (captureMode in caps.captureModeList) {
                captureModeEntries.add(captureMode.name) //name
                captureModeValues.add(captureMode.name) //value
            }
            pref?.let {
                it.entries = captureModeEntries.toTypedArray()
                it.entryValues = captureModeValues.toTypedArray()
                it.setDefaultValue(CaptureMode.DEFAULT.name)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }

        //Load automatic Tone Mode
        if (caps.automaticToneModeList.size > 0) {
            pref = findPreference(PREF_AUTOMATIC_TONE_MODE)
            val automaticToneModeEntries = ArrayList<CharSequence>()
            val automaticToneModeValues = ArrayList<CharSequence>()
            for (automaticToneMode in caps.automaticToneModeList) {
                automaticToneModeEntries.add(automaticToneMode.name) //name
                automaticToneModeValues.add(automaticToneMode.name) //value
            }
            pref?.let {
                it.entries = automaticToneModeEntries.toTypedArray()
                it.entryValues = automaticToneModeValues.toTypedArray()
                it.setDefaultValue(AutomaticToneMode.DEFAULT.name)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }

        //Load automatic Straighten Mode
        if (caps.automaticStraightenModeList.size > 0) {
            pref = findPreference(PREF_AUTOMATIC_STRAIGHTEN_MODE)
            val automaticStraightenModeEntries = ArrayList<CharSequence>()
            val automaticStraightenModeValues = ArrayList<CharSequence>()
            for (automaticStraightenMode in caps.automaticStraightenModeList) {
                automaticStraightenModeEntries.add(automaticStraightenMode.name) //name
                automaticStraightenModeValues.add(automaticStraightenMode.name) //value
            }
            pref?.let {
                it.entries = automaticStraightenModeEntries.toTypedArray()
                it.entryValues = automaticStraightenModeValues.toTypedArray()
                it.setDefaultValue(AutomaticStraightenMode.DEFAULT.name)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }

        // Doc Formats for different destinations
        fillDocFormatPreferences(caps, PREF_DOC_FORMAT, Destination.ME)
        fillFileOptionAttrCaps()
    }

    /**
     * Fills Doc format preferences values
     *
     * @param caps          [ScanAttributesCaps] to take data from
     * @param prefDocFormat String shared preferences key
     * @param dest          [ScanAttributes.Destination] to
     */
    private fun fillDocFormatPreferences(
        caps: ScanAttributesCaps?,
        prefDocFormat: String,
        dest: Destination
    ) {
        if (caps == null) {
            return
        }

        val pref = findPreference<ListPreference>(prefDocFormat)

        val dfEntries = ArrayList<CharSequence>()
        val dfEntryValues = ArrayList<CharSequence>()

        // Load Doc format
        for (df in caps.getDocumentFormatList(dest)) {
            dfEntries.add(df.name)
            dfEntryValues.add(df.name)
        }

        pref?.run {
            entries = dfEntries.toTypedArray()
            entryValues = dfEntryValues.toTypedArray()
            setValueIndex(0)
            summary = "%s"
        }
    }

    private fun setColorMode(sharedPreferences: SharedPreferences, key: String) {
        val docFormat = sharedPreferences.getString(key, DocumentFormat.DEFAULT.name)
            ?.let { DocumentFormat.valueOf(it) }
        val colorModeList = findPreference<ListPreference>(PREF_COLOR_MODE)
        val colorEntry = colorModeList?.entry as String?
        val colorMode =
                if (colorEntry == null) ColorMode.DEFAULT else ColorMode.valueOf(colorEntry)

        if (mCaps != null) {
            val entries = ArrayList<CharSequence>()
            entries.add(colorMode.name)

            for ((key1, value) in mCaps!!.documentFormatsByColorMode) {
                if (value.contains(docFormat) && key1 != ColorMode.DEFAULT) {
                    entries.add(key1.name)
                }
            }

            val entriesArray = entries.toTypedArray()
            colorModeList?.entries = entriesArray
            colorModeList?.entryValues = entriesArray
            colorModeList?.setValueIndex(0)
        } else {
            colorModeList?.setEntries(R.array.pref_default_entries)
            colorModeList?.setEntryValues(R.array.pref_default_entries)
        }
    }

    private fun showOptionalPreference(preferences: SharedPreferences, key: String) {
        mBaseAttributesCategory?.run {
            removePreference(mPDFCompressionPref)
            removePreference(mOCRLanguagePref)
            removePreference(mPDFPasswordPref)
            removePreference(mTIFFCompressionPref)
            removePreference(mXPSCompressionPref)

            when (preferences.getString(key, DocumentFormat.DEFAULT.name)
                ?.let { DocumentFormat.valueOf(it) }) {
                DocumentFormat.PDF -> {
                    addPreference(mPDFCompressionPref)
                    addPreference(mPDFPasswordPref)
                }

                DocumentFormat.OCR_PDF_TEXT_UNDER_IMAGE -> {
                    addPreference(mPDFCompressionPref)
                    addPreference(mOCRLanguagePref)
                    addPreference(mPDFPasswordPref)
                }

                DocumentFormat.MTIFF,
                DocumentFormat.TIFF ->
                    addPreference(mTIFFCompressionPref)

                DocumentFormat.OCR_PDF_A_TEXT_UNDER_IMAGE -> {
                    addPreference(mPDFCompressionPref)
                    addPreference(mOCRLanguagePref)
                }

                DocumentFormat.OCR_CSV,
                DocumentFormat.OCR_HTML,
                DocumentFormat.OCR_RTF,
                DocumentFormat.OCR_TEXT,
                DocumentFormat.OCR_UNICODE_TEXT -> addPreference(mOCRLanguagePref)

                DocumentFormat.PDF_A -> addPreference(mPDFCompressionPref)
                DocumentFormat.XPS -> addPreference(mXPSCompressionPref)
                else -> {
                }
            }
        }
    }

    private fun showCustomSizePreference(preferences: SharedPreferences, key: String) {
        val scanSize =
            preferences.getString(key, ScanSize.DEFAULT.name)?.let { ScanSize.valueOf(it) }

        if (scanSize == ScanSize.CUSTOM) {
            mCustomLengthPref?.summary = mCustomLengthPref?.text
            mCustomWidthPref?.summary = mCustomWidthPref?.text
            mBaseAttributesCategory?.addPreference(mCustomLengthPref)
            mBaseAttributesCategory?.addPreference(mCustomWidthPref)
        } else {
            mBaseAttributesCategory?.removePreference(mCustomLengthPref)
            mBaseAttributesCategory?.removePreference(mCustomWidthPref)
        }
    }

    private fun supportTransmissionModeCaps(isSupported: Boolean, name: String) {
        if (isSupported) {
            mBaseAttributesCategory?.addPreference(mTransmissionPref)
            mTransmissionPref?.let {
                val transmissionModeEntries = ArrayList<CharSequence>()
                val transmissionModeValues = ArrayList<CharSequence>()
                mCaps?.run {
                    for (transmissionMode in transmissionModeList) {
                        if (name == Destination.HTTP.name) {
                            transmissionModeEntries.add(transmissionMode.name) //name
                            transmissionModeValues.add(transmissionMode.name) //value
                        } else {
                            if (transmissionMode.name != TransmissionMode.IMAGE.name) {
                                transmissionModeEntries.add(transmissionMode.name) //name
                                transmissionModeValues.add(transmissionMode.name) //value
                            }
                        }
                    }
                    it.entries = transmissionModeEntries.toTypedArray()
                    it.entryValues = transmissionModeValues.toTypedArray()
                    for (transmissionMode in this.transmissionModeList) {
                        if(defaultTransmissionMode == transmissionMode.name){
                            it.setDefaultValue(transmissionMode.name)
                            it.value = transmissionMode.name
                        }
                    }
                }
                it.summary = "%s"
            }
        } else {
            mBaseAttributesCategory?.removePreference(mTransmissionPref)
            val mPrefs =
                PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            val transmissionPref = findPreference<ListPreference>(PREF_TRANSMISSION_MODE)
            val transmissionEntry = transmissionPref?.entry as String?
            val transmission =
                    if (transmissionEntry == null) TransmissionMode.DEFAULT else TransmissionMode.valueOf(transmissionEntry)

            val editor = mPrefs.edit()
            editor.putString(PREF_TRANSMISSION_MODE, transmission.name)
            editor.apply()
        }
    }

    fun setDefaultScanAttributes(scanAttributes: ScanAttributes?) {
        if (scanAttributes != null) {
            val scanAttributesReader = ScanAttributesReader(scanAttributes)
            defaultTransmissionMode = scanAttributesReader.transmissionMode.name
            setPreferenceEntryValue(
                PREF_BACKGROUND_CLEANUP,
                scanAttributesReader.backgroundCleanup.name
            )
            setPreferenceEntryValue(
                PREF_BLANK_IMAGE_REMOVAL_MODE,
                scanAttributesReader.blankImageRemovalMode.name
            )
            setPreferenceEntryValue(
                PREF_COLOR_DROPOUT_MODE,
                scanAttributesReader.colorDropoutMode.name
            )
            setPreferenceEntryValue(PREF_COLOR_MODE, scanAttributesReader.colorMode.name)
            setPreferenceEntryValue(
                PREF_CONTRAST_ADJUSTMENT,
                scanAttributesReader.contrastAdjustment.name
            )
            setPreferenceEntryValue(PREF_CROP_MODE, scanAttributesReader.cropMode.name)
            setPreferenceEntryValue(
                PREF_DARKNESS_ADJUSTMENT,
                scanAttributesReader.darknessAdjustment.name
            )
            setPreferenceEntryValue(PREF_DOC_FORMAT, scanAttributesReader.documentFormat.name)
            setPreferenceEntryValue(
                PREF_JOB_ASSEMBLY_MODE,
                scanAttributesReader.jobAssemblyMode.name
            )
            setPreferenceEntryValue(PREF_MEDIA_SOURCE, scanAttributesReader.mediaSource.name)
            setPreferenceEntryValue(
                PREF_MEDIA_WEIGHT_ADJUSTMENT,
                scanAttributesReader.mediaWeightAdjustment.name
            )
            setPreferenceEntryValue(
                PREF_MISFEED_DETECTION_MODE,
                scanAttributesReader.misfeedDetectionMode.name
            )
            setPreferenceEntryValue(PREF_ORIENTATION, scanAttributesReader.orientation.name)
            setPreferenceEntryValue(PREF_OUTPUT_QUALITY, scanAttributesReader.outputQuality.name)
            setPreferenceEntryValue(PREF_DUPLEX_MODE, scanAttributesReader.plex.name)
            setPreferenceEntryValue(PREF_TRANSMISSION_MODE, scanAttributesReader.transmissionMode.name)
            setPreferenceEntryValue(
                PREF_PROGRESS_DIALOG_MODE,
                scanAttributesReader.progressDialogMode.name
            )
            setPreferenceEntryValue(PREF_RESOLUTION_TYPE, scanAttributesReader.resolution.name)
            setPreferenceEntryValue(
                PREF_TEXT_PHOTO_OPTIMIZATION,
                scanAttributesReader.textPhotoOptimization.name
            )
            setPreferenceEntryValue(PREF_ORG_SIZE, scanAttributesReader.scanSize.name)
            setEditTextPreferenceValue(PREF_MAX_PAGES_PER_ATTACHMENT, scanAttributesReader.maxPagesPerAttachment.toString())
            setPreferenceEntryValue(
                PREF_SHARPNESS_ADJUSTMENT,
                scanAttributesReader.sharpnessAdjustment.name
            )
            setEditTextPreferenceValue(PREF_FILE_NAME, "")

            if (scanAttributesReader.splitAttachmentByPage != null) {
                setPreferenceEntryValue(
                    PREF_SPLIT_ATTACHMENT_BY_PAGE,
                    scanAttributesReader.splitAttachmentByPage.name
                )
            }

            if (scanAttributesReader.eraseMarginUnit != null) {
                setPreferenceEntryValue(
                    PREF_ERASE_MARGIN_UNIT,
                    scanAttributesReader.eraseMarginUnit.name
                )
            }
            val backMargin = Margins(
                scanAttributesReader.eraseBackLeftMargin,
                scanAttributesReader.eraseBackTopMargin,
                scanAttributesReader.eraseBackRightMargin,
                scanAttributesReader.eraseBackBottomMargin
            )

            val frontMargin = Margins(
                scanAttributesReader.eraseFrontLeftMargin,
                scanAttributesReader.eraseFrontTopMargin,
                scanAttributesReader.eraseFrontRightMargin,
                scanAttributesReader.eraseFrontBottomMargin
            )

            setMarginPreferenceValue(PREF_ERASE_BACK_MARGIN, backMargin)
            setMarginPreferenceValue(PREF_ERASE_FRONT_MARGIN, frontMargin)

            if (scanAttributesReader.captureMode != null) {
                setPreferenceEntryValue(PREF_CAPTURE_MODE, scanAttributesReader.captureMode.name)
            }
            if (scanAttributesReader.automaticToneMode != null) {
                setPreferenceEntryValue(
                    PREF_AUTOMATIC_TONE_MODE,
                    scanAttributesReader.automaticToneMode.name
                )
            }
            if (scanAttributesReader.automaticStraightenMode != null) {
                setPreferenceEntryValue(
                    PREF_AUTOMATIC_STRAIGHTEN_MODE,
                    scanAttributesReader.automaticStraightenMode.name
                )
            }
            if (scanAttributesReader.scanPreview != null) {
                setPreferenceEntryValue(
                    PREF_SCAN_PREVIEW,
                    scanAttributesReader.scanPreview.name
                )
            }
        }
    }

    private fun setMarginPreferenceValue(prefEraseFrontMargin: String, margin: Margins) {
        val eraseMarginPreference =
            findPreference<Preference>(prefEraseFrontMargin) as MarginsPreference?
        if (eraseMarginPreference != null) {
            eraseMarginPreference.setMargins(
                margin.leftMargin,
                margin.topMargin,
                margin.rightMargin,
                margin.bottomMargin
            )
            eraseMarginPreference.applyMargins()
            eraseMarginPreference.summary = context?.getString(
                R.string.summary_margin,
                margin.leftMargin,
                margin.topMargin,
                margin.rightMargin,
                margin.bottomMargin
            )
        }
    }

    private fun setEditTextPreferenceValue(pref: String, attribute: String) {
        val editTextPreference = findPreference<EditTextPreference>(pref)
        editTextPreference.let {
            it?.setDefaultValue(attribute)
            it?.text = attribute
        }
    }

    private fun setPreferenceEntryValue(pref: String, attribute: String) {
        val listPreference = findPreference<ListPreference>(pref)
        var index = 0
        listPreference?.let {
            for (value in listPreference.entryValues) {
                if (value != null && value.toString() == attribute) {
                    break
                }
                index++
            }

            if (index < listPreference.entryValues.size) {
                listPreference.setValueIndex(index)
            }
        }

    }

    private fun fillFileOptionAttrCaps() {
        if (isSDKInitialized) {
            val docPref = findPreference<ListPreference>(PREF_DOC_FORMAT)
            val docEntry = docPref?.entry as String?
            val docFormat =
                if (docEntry == null) DocumentFormat.DEFAULT else DocumentFormat.valueOf(docEntry)

            val colorPref = findPreference<ListPreference>(PREF_COLOR_MODE)
            val colorEntry = colorPref?.entry as String?
            val colorMode =
                if (colorEntry == null) ColorMode.DEFAULT else ColorMode.valueOf(colorEntry)

            val mPrefs =
                PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)

            val fileOptionsAttrCaps =
                (activity as MainActivity).requestFileOptionsCapabilities(colorMode, docFormat)
            if (fileOptionsAttrCaps != null) {
                val editPref = findPreference(PREF_PDF_PASSWORD) as EditTextPreference?
                val isPdfEncryptionSupport = fileOptionsAttrCaps.isPdfEncryptionPasswordSupported
                if (editPref != null && isPdfEncryptionSupport) {
                    val password = mPrefs.getString(PREF_PDF_PASSWORD, null)
                    editPref.text = password
                } else {
                    val editor = mPrefs.edit()
                    editor.putString(PREF_PDF_PASSWORD, null)
                    editor.apply()
                }

                var pref = findPreference(PREF_OCR_LANGUAGE) as ListPreference?

                val ocrEntries = ArrayList<CharSequence>()
                val ocrEntryValues = ArrayList<CharSequence>()

                for (language in fileOptionsAttrCaps.ocrLanguageList) {
                    if (language?.name != null) {
                        ocrEntries.add(language.name)
                        ocrEntryValues.add(language.name)
                    }
                }

                if (pref != null) {
                    pref.entries = ocrEntries.toTypedArray()
                    pref.entryValues = ocrEntryValues.toTypedArray()
                    pref.setDefaultValue(FileOptionsAttributes.OcrLanguage.DEFAULT.name)
                    pref.setValueIndex(0)
                    pref.summary = "%s"
                } else {
                    val editor = mPrefs.edit()
                    editor.putString(
                        PREF_OCR_LANGUAGE,
                        FileOptionsAttributes.OcrLanguage.DEFAULT.name
                    )
                    editor.apply()
                }

                pref = findPreference(PREF_PDF_COMPRESSION) as ListPreference?

                val pdfCompEntries = ArrayList<CharSequence>()
                val pdfCompEntryValues = ArrayList<CharSequence>()

                for (compressionMode in fileOptionsAttrCaps.pdfCompressionModeList) {
                    pdfCompEntries.add(compressionMode.name)
                    pdfCompEntryValues.add(compressionMode.name)
                }

                if (pref != null) {
                    pref.entries = pdfCompEntries.toTypedArray()
                    pref.entryValues = pdfCompEntryValues.toTypedArray()
                    pref.setDefaultValue(FileOptionsAttributes.PdfCompressionMode.DEFAULT.name)
                    pref.setValueIndex(0)
                    pref.summary = "%s"
                } else {
                    val editor = mPrefs.edit()
                    editor.putString(
                        PREF_PDF_COMPRESSION,
                        FileOptionsAttributes.PdfCompressionMode.DEFAULT.name
                    )
                    editor.apply()
                }

                pref = findPreference(PREF_TIFF_COMPRESSION) as ListPreference?

                val tiffCompEntries = ArrayList<CharSequence>()
                val tiffCompEntryValues = ArrayList<CharSequence>()

                for (tiffComp in fileOptionsAttrCaps.tiffCompressionModeList) {
                    tiffCompEntries.add(tiffComp.name)
                    tiffCompEntryValues.add(tiffComp.name)
                }

                if (pref != null) {
                    pref.entries = tiffCompEntries.toTypedArray()
                    pref.entryValues = tiffCompEntryValues.toTypedArray()
                    pref.setDefaultValue(FileOptionsAttributes.TiffCompressionMode.DEFAULT.name)
                    pref.setValueIndex(0)
                    pref.summary = "%s"
                } else {
                    val editor = mPrefs.edit()
                    editor.putString(
                        PREF_TIFF_COMPRESSION,
                        FileOptionsAttributes.TiffCompressionMode.DEFAULT.name
                    )
                    editor.apply()
                }

                pref = findPreference(PREF_XPS_COMPRESSION) as ListPreference?

                val xpsCompEntries = ArrayList<CharSequence>()
                val xpsCompEntryValues = ArrayList<CharSequence>()

                for (xpsComp in fileOptionsAttrCaps.xpsCompressionModeList) {
                    xpsCompEntries.add(xpsComp.name)
                    xpsCompEntryValues.add(xpsComp.name)
                }

                if (pref != null) {
                    pref.entries = xpsCompEntries.toTypedArray()
                    pref.entryValues = xpsCompEntryValues.toTypedArray()
                    pref.setDefaultValue(FileOptionsAttributes.XpsCompressionMode.DEFAULT.name)
                    pref.setValueIndex(0)
                    pref.summary = "%s"
                } else {
                    val editor = mPrefs.edit()
                    editor.putString(
                        PREF_XPS_COMPRESSION,
                        FileOptionsAttributes.XpsCompressionMode.DEFAULT.name
                    )
                    editor.apply()
                }
            }
        }
    }

    private fun fillUSBStorages() {
        val pref = findPreference(PREF_USB_STORAGE) as ListPreference?

        if (pref != null) {
            val result = Result()
            if (!MassStorageService.isSupported(requireContext())) {
                Toast.makeText(
                    activity,
                    requireActivity().getString(R.string.mass_storage_not_supported),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            var usbStorageList: List<MassStorageInfo>? =
                MassStorageService.getStorageList(requireActivity(), result)
            if (result.code != Result.RESULT_OK || usbStorageList == null) {
                usbStorageList = emptyList()
            }

            val entries = ArrayList<CharSequence>()
            val entriesValues = ArrayList<CharSequence>()

            for (storage in usbStorageList) {
                if (storage.type == MassStorageInfo.StorageType.USB && storage.isMounted) {
                    entries.add(storage.name)
                    entriesValues.add(storage.externalFileDirectory)
                }
            }

            pref.entries = entries.toTypedArray()
            pref.entryValues = entriesValues.toTypedArray()

            if (entries.isNotEmpty()) {
                pref.setDefaultValue(entriesValues[0])
                pref.setValueIndex(0)
                pref.summary = "%s"
            }
        }
    }

    companion object {
        // Preferences keys for ScanAttributes
        const val PREF_DESTINATION = "pref_destination"
        const val PREF_FILE_NAME = "pref_filename"
        const val PREF_COLOR_MODE = "pref_colorMode"
        const val PREF_DUPLEX_MODE = "pref_duplexMode"
        const val PREF_RESOLUTION_TYPE = "pref_resolutionType"
        const val PREF_DOC_FORMAT = "pref_docFormat"
        const val PREF_ORG_SIZE = "pref_originalSize"
        const val PREF_CUSTOM_LENGTH = "pref_customLength"
        const val PREF_CUSTOM_WIDTH = "pref_customWidth"
        const val PREF_ORIENTATION = "pref_orientation"
        const val PREF_SCAN_PREVIEW = "pref_scanPreview"
        const val PREF_BACKGROUND_CLEANUP = "pref_backgroundCleanup"
        const val PREF_CONTRAST_ADJUSTMENT = "pref_contrastAdjustment"
        const val PREF_DARKNESS_ADJUSTMENT = "pref_darknessAdjustment"
        const val PREF_BLANK_IMAGE_REMOVAL_MODE = "pref_blankImageRemovalMode"
        const val PREF_COLOR_DROPOUT_MODE = "pref_colorDropoutMode"
        const val PREF_CROP_MODE = "pref_cropMode"
        const val PREF_PROGRESS_DIALOG_MODE = "pref_progressDialogMode"
        const val PREF_OUTPUT_QUALITY = "pref_outputQuality"
        const val PREF_TRANSMISSION_MODE = "pref_transmissionMode"
        const val PREF_JOB_ASSEMBLY_MODE = "pref_jobAssemblyMode"
        const val PREF_SHARPNESS_ADJUSTMENT = "pref_sharpnessAdjustment"
        const val PREF_MEDIA_WEIGHT_ADJUSTMENT = "pref_mediaWeightAdjustment"
        const val PREF_TEXT_PHOTO_OPTIMIZATION = "pref_textPhotoOptimization"

        const val PREF_SPLIT_ATTACHMENT_BY_PAGE = "pref_splitAttachmentByPage"
        const val PREF_MAX_PAGES_PER_ATTACHMENT = "pref_maxPagesPerAttachment"
        const val PREF_ERASE_MARGIN_UNIT = "pref_eraseMarginUnit"
        const val PREF_ERASE_BACK_MARGIN = "pref_eraseBackMargin"
        const val PREF_ERASE_BACK_LEFT_MARGIN = "pref_eraseBackMarginLeft"
        const val PREF_ERASE_BACK_TOP_MARGIN = "pref_eraseBackMarginTop"
        const val PREF_ERASE_BACK_RIGHT_MARGIN = "pref_eraseBackMarginRight"
        const val PREF_ERASE_BACK_BOTTOM_MARGIN = "pref_eraseBackMarginBottom"
        const val PREF_ERASE_FRONT_MARGIN = "pref_eraseFrontMargin"
        const val PREF_ERASE_FRONT_LEFT_MARGIN = "pref_eraseFrontMarginLeft"
        const val PREF_ERASE_FRONT_TOP_MARGIN = "pref_eraseFrontMarginTop"
        const val PREF_ERASE_FRONT_RIGHT_MARGIN = "pref_eraseFrontMarginRight"
        const val PREF_ERASE_FRONT_BOTTOM_MARGIN = "pref_eraseFrontMarginBottom"
        const val PREF_CAPTURE_MODE = "pref_captureMode"
        const val PREF_AUTOMATIC_TONE_MODE = "pref_automaticToneMode"
        const val PREF_AUTOMATIC_STRAIGHTEN_MODE = "pref_automaticStraightenMode"

        const val PREF_MEDIA_SOURCE = "pref_mediaSource"
        const val PREF_MISFEED_DETECTION_MODE = "pref_misfeedDetectionMode"
        const val PREF_PDF_COMPRESSION = "pref_pdf_compression"
        const val PREF_OCR_LANGUAGE = "pref_ocr_language"
        const val PREF_PDF_PASSWORD = "pref_pdf_password"
        const val PREF_TIFF_COMPRESSION = "pref_tiff_compression"
        const val PREF_XPS_COMPRESSION = "pref_xps_compression"

        const val PREF_URI_HTTP = "pref_uri_http"
        const val PREF_URI_FTP = "pref_uri_ftp"
        const val PREF_URI_NETWORK_FOLDER = "pref_uri_network_folder"
        const val PREF_URI_HTTP_USERNAME = "pref_uri_http_username"
        const val PREF_URI_HTTP_PASSWORD = "pref_uri_http_password"
        const val PREF_URI_FTP_USERNAME = "pref_uri_ftp_username"
        const val PREF_URI_FTP_PASSWORD = "pref_uri_ftp_password"
        const val PREF_URI_NETWORK_FOLDER_USERNAME = "pref_uri_network_folder_username"
        const val PREF_URI_NETWORK_FOLDER_PASSWORD = "pref_uri_network_folder_password"
        const val PREF_URI_NETWORK_FOLDER_DOMAIN = "pref_uri_network_folder_domain"

        const val PREF_EMAIL_TO = "pref_email_to"
        const val PREF_EMAIL_CC = "pref_email_cc"
        const val PREF_EMAIL_BCC = "pref_email_bcc"
        const val PREF_EMAIL_FROM = "pref_email_from"
        const val PREF_EMAIL_SUBJECT = "pref_email_subject"
        const val PREF_EMAIL_MESSAGE = "pref_email_message"
        const val PREF_EMAIL_SMTP = "pref_email_smtp"

        const val PREF_USB_STORAGE = "pref_usb_storage"
        const val PREF_USB_LOCATION = "pref_usb_location"

        const val PREF_DESTINATION_HTTP_CATEGORY = "destination_http_category"
        const val PREF_DESTINATION_FTP_CATEGORY = "destination_ftp_category"
        const val PREF_DESTINATION_NETWORK_FOLDER_CATEGORY = "destination_network_folder_category"
        const val PREF_DESTINATION_EMAIL_CATEGORY = "destination_email_category"
        const val PREF_DESTINATION_USB_CATEGORY = "destination_usb_category"
        const val PREF_BASE_ATTRIBUTES_CATEGORY = "base_attributes_category"
        const val PREF_DESTINATION_FEEDBACK_CATEGORY = "destination_feedback_category"

        // Feedback / UI preferences
        const val PREF_MONITOR_JOB = "pref_monitorJob"
        const val PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress"
        const val PREF_SETTINGS_UI = "pref_settingsUi"
        const val PREF_ALLOW_MULTIPLE_SCAN = "pref_allow_multiple_scan"

        // Preference for current job id
        const val CURRENT_JOB_ID = "pref_currentJobId"

        const val PREF_SCANNER_STATUS = "pref_scanner_status"
    }
}
