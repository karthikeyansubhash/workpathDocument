// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.hp.workpath.api.copier.CopyAttributes
import com.hp.workpath.api.copier.CopyAttributesCaps
import com.hp.workpath.api.copier.CopyAttributesReader
import com.hp.workpath.api.copier.JobCredentialsAttributes
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.copier.*
import com.hp.workpath.api.scanner.Margins
import com.hp.workpath.sample.copysample.R
import java.util.HashMap

/**
 * Simple [PreferenceFragmentCompat] to set Copy Attributes and save into preferences.
 */
class CopyConfigureFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    private var mNumberUpDirectionPref: ListPreference? = null
    private var mJobNamePref: EditTextPreference? = null
    private var mJobFolderPref: EditTextPreference? = null
    private var mJobPasswordPref: EditTextPreference? = null
    private var mCopiesPref: EditTextIntegerPreference? = null
    private var mScalePercentPref: EditTextIntegerPreference? = null
    private var mScanCustomLengthPref: EditTextFloatPreference? = null
    private var mScanCustomWidthPref: EditTextFloatPreference? = null
    private var mPrintCustomLengthPref: EditTextFloatPreference? = null
    private var mPrintCustomWidthPref: EditTextFloatPreference? = null
    private var mBaseAttributesCategory: PreferenceCategory? = null
    private var mStoreCategory: PreferenceCategory? = null
    private lateinit var mCaps: CopyAttributesCaps
    private var mEraseBackMarginPref: MarginsPreference? = null
    private var mEraseFrontMarginPref: MarginsPreference? = null
    private var mImageShiftFrontPref: ShiftPreference? = null
    private var mImageShiftBackPref: ShiftPreference? = null
    private var mWatermarkDarknessPref: EditTextIntegerPreference? = null
    private var mWatermarkTextPref: EditTextPreference? = null

    private var mWatermarkTextSizePref: ListPreference? = null
    private var mWatermarkTransparencyPref: EditTextIntegerPreference? = null
    private var mWatermarkBackgroundColorPref: ListPreference? = null
    private var mWatermarkFontPref: ListPreference? = null
    private var mWatermarkTextColorPref: ListPreference? = null
    private var mStampPref: StampOptionDialogPreference? = null
    private lateinit var mWatermarktype: String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.copy_preferences)
        // Set default limits to single, default, value
        mCopiesPref = findPreference(PREF_COPIES)
        mCopiesPref?.setLimits(1, 1)
        // Set default limits to single, default, value
        mScalePercentPref = findPreference(PREF_SCALE_PERCENT)
        mScalePercentPref?.setLimits(100, 100)
        mScanCustomLengthPref = findPreference(PREF_SCAN_CUSTOM_LENGTH)
        mScanCustomLengthPref?.setLimits(0f, 0f)
        mScanCustomWidthPref = findPreference(PREF_SCAN_CUSTOM_WIDTH)
        mScanCustomWidthPref?.setLimits(0f, 0f)
        mPrintCustomLengthPref = findPreference(PREF_PRINT_CUSTOM_LENGTH)
        mPrintCustomLengthPref?.setLimits(0f, 0f)
        mPrintCustomWidthPref = findPreference(PREF_PRINT_CUSTOM_WIDTH)
        mPrintCustomWidthPref?.setLimits(0f, 0f)
        mEraseBackMarginPref = findPreference(PREF_ERASE_BACK_MARGIN)
        mEraseFrontMarginPref = findPreference(PREF_ERASE_FRONT_MARGIN)
        mImageShiftFrontPref = findPreference(PREF_IMAGE_SHIFT_FRONT)
        mImageShiftBackPref = findPreference(PREF_IMAGE_SHIFT_BACK)
        mJobNamePref = findPreference(PREF_STORE_JOB_NAME)
        mJobNamePref?.setOnBindEditTextListener { editText: EditText ->
            editText.selectAll()
            val maxLength = 256
            editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        }
        mJobNamePref?.text = null
        mJobFolderPref = findPreference(PREF_STORE_JOB_FOLDER_NAME)
        mJobFolderPref?.setOnBindEditTextListener { editText: EditText ->
            editText.selectAll()
            val maxLength = 256
            editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        }
        mJobFolderPref?.text = null
        mJobPasswordPref = findPreference(PREF_STORE_JOB_PASSWORD)
        mJobPasswordPref?.text = null
        mNumberUpDirectionPref = findPreference(PREF_NUMBER_UP_DIRECTION)
        mBaseAttributesCategory = findPreference(PREF_BASE_ATTRIBUTES_CATEGORY)
        mStoreCategory = findPreference(PREF_DESTINATION_STORE_CATEGORY)
        mWatermarkDarknessPref = findPreference(PREF_WATERMARK_DARKNESS)
        mWatermarkDarknessPref?.setLimits(0, 2)

        mWatermarkTextSizePref = findPreference(PREF_WATERMARK_TEXT_SIZE)

        mWatermarkTransparencyPref = findPreference(PREF_WATERMARK_TRANSPARENCY)
        mWatermarkTransparencyPref?.setLimits(0, 0)

        mWatermarkBackgroundColorPref = findPreference(PREF_WATERMARK_BACKGROUND_COLOR)

        mWatermarkFontPref = findPreference(PREF_WATERMARK_FONT)

        mWatermarkTextColorPref = findPreference(PREF_WATERMARK_TEXT_COLOR)

        mWatermarkTextPref = findPreference(PREF_WATERMARK_TEXT)
        mWatermarkTextPref?.text = null

        mStampPref = findPreference<Preference>(PREF_STAMP) as StampOptionDialogPreference?
        if (this::mCaps.isInitialized) {
            loadCapabilities(mCaps)
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = preferenceScreen.sharedPreferences
        prefs.registerOnSharedPreferenceChangeListener(this)
        refreshAllPrefs(prefs)
    }

    private fun refreshAllPrefs(prefs: SharedPreferences) {
        onSharedPreferenceChanged(prefs, PREF_COPIES)
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE)
        onSharedPreferenceChanged(prefs, PREF_ORIENTATION)
        onSharedPreferenceChanged(prefs, PREF_SCAN_DUPLEX_MODE)
        onSharedPreferenceChanged(prefs, PREF_PRINT_DUPLEX_MODE)
        onSharedPreferenceChanged(prefs, PREF_SCAN_SIZE)
        onSharedPreferenceChanged(prefs, PREF_SCAN_CUSTOM_LENGTH)
        onSharedPreferenceChanged(prefs, PREF_SCAN_CUSTOM_WIDTH)
        onSharedPreferenceChanged(prefs, PREF_PRINT_SIZE)
        onSharedPreferenceChanged(prefs, PREF_PRINT_CUSTOM_LENGTH)
        onSharedPreferenceChanged(prefs, PREF_PRINT_CUSTOM_WIDTH)
        onSharedPreferenceChanged(prefs, PREF_COPY_PREVIEW)
        onSharedPreferenceChanged(prefs, PREF_SCAN_SOURCE)
        onSharedPreferenceChanged(prefs, PREF_BACKGROUND_CLEANUP)
        onSharedPreferenceChanged(prefs, PREF_CONTRAST_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_DARKNESS_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_SHARPNESS_ADJUSTMENT)
        onSharedPreferenceChanged(prefs, PREF_COLLATE_MODE)
        onSharedPreferenceChanged(prefs, PREF_PAPER_SOURCE)
        onSharedPreferenceChanged(prefs, PREF_PAPER_TYPE)
        onSharedPreferenceChanged(prefs, PREF_SCALE_MODE)
        onSharedPreferenceChanged(prefs, PREF_SCALE_PERCENT)
        onSharedPreferenceChanged(prefs, PREF_TEXT_GRAPHICS_OPTIMIZATION)
        onSharedPreferenceChanged(prefs, PREF_NUMBER_UP_MODE)
        onSharedPreferenceChanged(prefs, PREF_NUMBER_UP_DIRECTION)
        onSharedPreferenceChanged(prefs, PREF_JOB_ASSEMBLY_MODE)
        onSharedPreferenceChanged(prefs, PREF_JOB_EXECUTION_MODE)
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_BIN)
        onSharedPreferenceChanged(prefs, PREF_PROGRESS_DIALOG_MODE)
        onSharedPreferenceChanged(prefs, PREF_ERASE_MARGIN_UNIT)
        onSharedPreferenceChanged(prefs, PREF_ERASE_BACK_MARGIN)
        onSharedPreferenceChanged(prefs, PREF_ERASE_FRONT_MARGIN)
        onSharedPreferenceChanged(prefs, PREF_CAPTURE_MODE)
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_REDUCE_TO_FIT)
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_UNITS)
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_FRONT)
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_BACK)
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_BORDERS_EACH_PAGE)
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_FINISHING_OPTION)
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_FORMAT)
        onSharedPreferenceChanged(prefs, PREF_STAPLE_MODE)
        onSharedPreferenceChanged(prefs, PREF_PUNCH_MODE)
        onSharedPreferenceChanged(prefs, PREF_FOLD_MODE)

        for (stampPosition in CopyAttributes.StampPosition.values()) {
            onSharedPreferenceChanged(prefs, PREF_STAMP + stampPosition.name)
        }
        onSharedPreferenceChanged(prefs, PREF_MONITOR_JOB)
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS)
        onSharedPreferenceChanged(prefs, PREF_SETTINGS_UI)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_DARKNESS)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_ROTATION)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TYPE)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_MESSAGE_TYPE)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_BACKGROUND_PATTERN)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT_SIZE)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TRANSPARENCY)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_BACKGROUND_COLOR)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_FONT)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT_COLOR)
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_ONLY_FIRST_PAGE)
        onSharedPreferenceChanged(prefs, PREF_MONITOR_JOB)
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS)
        onSharedPreferenceChanged(prefs, PREF_SETTINGS_UI)

    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is MarginsPreference -> {
                val dialogFragment = MarginsPreferenceFragment.newInstance(preference.getKey())
                dialogFragment.setTargetFragment(this, 1)
                dialogFragment.show(parentFragmentManager, "Margin")
            }

            is ShiftPreference -> {
                val dialogFragment = ShiftPreferenceFragment.newInstance(preference.getKey())
                dialogFragment.setTargetFragment(this, 2)
                dialogFragment.show(parentFragmentManager, "Shift")
            }

            is StampOptionDialogPreference -> {
                val dialogFragment: DialogFragment = StampOptionPreferenceFragment.newInstance(preference.getKey())
                dialogFragment.setTargetFragment(this, 3)
                dialogFragment.show(parentFragmentManager, "Stamp")
            }

            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = preferenceScreen.sharedPreferences
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)
        if (preference != null) {
            when (key) {
                PREF_NUMBER_UP_MODE -> {
                    fillNumberUpDirectionAttrCaps(sharedPreferences)
                }

                PREF_SCAN_SOURCE -> {
                    fillScalePercentRange(sharedPreferences)
                }

                PREF_SCAN_SIZE -> {
                    showOriginalCustomSizePreference(sharedPreferences, key)
                }

                PREF_PRINT_SIZE -> {
                    showOutputCustomSizePreference(sharedPreferences, key)
                }

                PREF_SCALE_MODE -> {
                    showScalePercentPreference(sharedPreferences, key)
                    onSharedPreferenceChanged(sharedPreferences, PREF_SCALE_PERCENT)
                }

                PREF_STORE_JOB_PASSWORD_TYPE -> {
                    initiateStorePassword(sharedPreferences, key)
                    onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_PASSWORD)
                }

                PREF_JOB_EXECUTION_MODE -> {
                    val entryStr = (preference as ListPreference).entry as String?
                    when (if (entryStr == null) CopyAttributes.JobExecutionMode.NORMAL else CopyAttributes.JobExecutionMode.valueOf(
                        entryStr
                    )) {
                        CopyAttributes.JobExecutionMode.STORE -> {
                            if (preferenceScreen.findPreference<Preference?>(
                                    PREF_DESTINATION_STORE_CATEGORY
                                ) == null
                            ) {
                                preferenceScreen.addPreference(mStoreCategory)
                            }
                            mJobNamePref =
                                findPreference<Preference>(PREF_STORE_JOB_NAME) as EditTextPreference?
                            mJobNamePref?.text = null
                            mJobFolderPref =
                                findPreference<Preference>(PREF_STORE_JOB_FOLDER_NAME) as EditTextPreference?
                            mJobFolderPref?.text = null
                            mJobPasswordPref =
                                findPreference<Preference>(PREF_STORE_JOB_PASSWORD) as EditTextPreference?
                            mJobPasswordPref?.text = null
                            loadPrefFromCaps(
                                PREF_STORE_DELETE_ON_POWER,
                                listOf(*CopyAttributes.RetentionMode.values()),
                                CopyAttributes.RetentionMode.DEFAULT.name
                            )
                            loadPrefFromCaps(
                                PREF_STORE_DELETE_ON_RELEASE,
                                listOf(*CopyAttributes.RetentionMode.values()),
                                CopyAttributes.RetentionMode.DEFAULT.name
                            )
                            loadPrefFromCaps(
                                PREF_STORE_JOB_PASSWORD_TYPE,
                                mCaps.passwordTypeList,
                                JobCredentialsAttributes.PasswordType.NONE.name
                            )
                            onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_NAME)
                            onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_FOLDER_NAME)
                            onSharedPreferenceChanged(sharedPreferences, PREF_STORE_DELETE_ON_POWER)
                            onSharedPreferenceChanged(
                                sharedPreferences,
                                PREF_STORE_DELETE_ON_RELEASE
                            )
                            onSharedPreferenceChanged(
                                sharedPreferences,
                                PREF_STORE_JOB_PASSWORD_TYPE
                            )
                            onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_PASSWORD)
                        }

                        CopyAttributes.JobExecutionMode.NORMAL -> preferenceScreen.removePreference(
                            mStoreCategory
                        )

                        else -> preferenceScreen.removePreference(mStoreCategory)
                    }
                }
            }
            if (preference is ListPreference) {
                val entry = preference.entry
                if (entry == null || entry.isEmpty()) {
                    preference.setValueIndex(0)
                    preference.setSummary("%s")
                } else {
                    if (preference.key.equals(PREF_BOOKLET_FORMAT)) {
                        when (preference.value) {
                            OPTION_DEFAULT -> {
                                showHideBookletSubView(false)
                            }

                            OPTION_OFF -> {
                                showHideBookletSubView(false)
                            }

                            OPTION_LEFTEDGE -> {
                                showHideBookletSubView(true)
                            }
                        }
                    }


                    if (preference.key.equals(PREF_WATERMARK_TYPE)) {
                        mWatermarktype = preference.value
                        watermarkSubView(preference.value)
                    }
                    if (preference.key.equals(PREF_WATERMARK_MESSAGE_TYPE)) {
                        if (preference.value.equals("CUSTOM") && isWatermarkTypeValid(mWatermarktype)) {
                            waterMarkShowText(true)
                        } else {
                            waterMarkShowText(false)
                        }
                    }
                    preference.setSummary(entry)
                }
            } else if (preference is EditTextPreference) {
                var text = preference.text
                if (PREF_STORE_JOB_PASSWORD == key) {
                    text = getTransformationString(text)
                }
                preference.setSummary(text)
            } else if (preference is CheckBoxPreference) {
                if (PREF_MONITOR_JOB == key) {
                    findPreference<Preference>(PREF_SHOW_JOB_PROGRESS)?.isEnabled =
                        preference.isChecked
                }
            }
        }
    }

    private fun isWatermarkTypeValid(watermarkType: String): Boolean {
        return when (watermarkType) {
            "DEFAULT", "NONE" -> {
                false
            }

            else -> {
                true
            }
        }
    }

    private fun showHideBookletSubView(isVisible: Boolean) {
        val mBookletBorderEachPageView =
            findPreference(PREF_BOOKLET_BORDERS_EACH_PAGE) as ListPreference?
        val mBookletFinishingOptionView =
            findPreference(PREF_BOOKLET_FINISHING_OPTION) as ListPreference?

        if (isVisible) {
            mBookletBorderEachPageView?.isVisible = true
            mBookletFinishingOptionView?.isVisible = true
        } else {
            mBookletBorderEachPageView?.isVisible = false
            mBookletFinishingOptionView?.isVisible = false
        }
    }

    private fun watermarkSubView(watermarkType: String) {
        val mWatermarkTextColorView = findPreference(PREF_WATERMARK_TEXT_COLOR) as ListPreference?
        val mWatermarkMessageTypeView =
            findPreference(PREF_WATERMARK_MESSAGE_TYPE) as ListPreference?
        val mWatermarkTextView = findPreference(PREF_WATERMARK_TEXT) as EditTextPreference?
        val mWatermarkFontView = findPreference(PREF_WATERMARK_FONT) as ListPreference?
        val mWatermarkTextSizeView = findPreference(PREF_WATERMARK_TEXT_SIZE) as ListPreference?
        val mWatermarkOnlyFirstPageView =
            findPreference(PREF_WATERMARK_ONLY_FIRST_PAGE) as ListPreference?
        val mWatermarkTransparencyView =
            findPreference(PREF_WATERMARK_TRANSPARENCY) as EditTextPreference?
        val mWatermarkDarknessView = findPreference(PREF_WATERMARK_DARKNESS) as EditTextPreference?
        val mWatermarkBackGroundColorView =
            findPreference(PREF_WATERMARK_BACKGROUND_COLOR) as ListPreference?
        val mWatermarkBackGroundPatternView =
            findPreference(PREF_WATERMARK_BACKGROUND_PATTERN) as ListPreference?
        val mWatermarkRotationView = findPreference(PREF_WATERMARK_ROTATION) as ListPreference?
        when (watermarkType) {
            "DEFAULT", "NONE" -> {
                mWatermarkTextColorView?.isVisible = false
                mWatermarkMessageTypeView?.isVisible = false
                mWatermarkTextView?.isVisible = false
                mWatermarkFontView?.isVisible = false
                mWatermarkTextSizeView?.isVisible = false
                mWatermarkOnlyFirstPageView?.isVisible = false
                mWatermarkTransparencyView?.isVisible = false
                mWatermarkDarknessView?.isVisible = false
                mWatermarkBackGroundColorView?.isVisible = false
                mWatermarkBackGroundPatternView?.isVisible = false
                mWatermarkRotationView?.isVisible = false
            }

            "TEXT" -> {
                mWatermarkTextColorView?.isVisible = true
                mWatermarkMessageTypeView?.isVisible = true
                mWatermarkFontView?.isVisible = true
                mWatermarkTextSizeView?.isVisible = true
                mWatermarkOnlyFirstPageView?.isVisible = true
                mWatermarkTransparencyView?.isVisible = true
                mWatermarkDarknessView?.isVisible = true
                mWatermarkBackGroundColorView?.isVisible = false
                mWatermarkBackGroundPatternView?.isVisible = false
                mWatermarkRotationView?.isVisible = false
                mWatermarkTextView?.isVisible = mWatermarkMessageTypeView?.value.equals(CopyAttributes.WatermarkMessageType.CUSTOM.name) && isWatermarkTypeValid(watermarkType)
            }

            "SECURE" -> {
                mWatermarkTextColorView?.isVisible = false
                mWatermarkMessageTypeView?.isVisible = true
                mWatermarkFontView?.isVisible = true
                mWatermarkTextSizeView?.isVisible = true
                mWatermarkOnlyFirstPageView?.isVisible = true
                mWatermarkTransparencyView?.isVisible = true
                mWatermarkDarknessView?.isVisible = true
                mWatermarkBackGroundColorView?.isVisible = true
                mWatermarkBackGroundPatternView?.isVisible = true
                mWatermarkRotationView?.isVisible = true
                mWatermarkTextView?.isVisible = mWatermarkMessageTypeView?.value.equals(CopyAttributes.WatermarkMessageType.CUSTOM.name) && isWatermarkTypeValid(watermarkType)
            }
        }
    }

    private fun waterMarkShowText(messageType: Boolean) {
        val mWatermarkTextView = findPreference(PREF_WATERMARK_TEXT) as EditTextPreference?
        mWatermarkTextView?.isVisible = messageType
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
     * @param caps [com.hp.workpath.api.copier.CopyAttributesCaps]
     */
    fun loadCapabilities(caps: CopyAttributesCaps?) {
        caps?.let {
            mCaps = it
            loadPrefFromCaps(
                PREF_COLOR_MODE,
                it.colorModeList,
                CopyAttributes.ColorMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_ORIENTATION,
                it.orientationList,
                CopyAttributes.Orientation.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_SCAN_DUPLEX_MODE,
                it.scanDuplexList,
                CopyAttributes.Duplex.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_PRINT_DUPLEX_MODE,
                it.printDuplexList,
                CopyAttributes.Duplex.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_COPY_PREVIEW,
                it.copyPreviewList,
                CopyAttributes.CopyPreview.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_SCAN_SOURCE,
                it.scanSourceList,
                CopyAttributes.ScanSource.AUTO.name
            )
            loadPrefFromCaps(
                PREF_BACKGROUND_CLEANUP,
                it.backgroundCleanupList,
                CopyAttributes.BackgroundCleanup.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_CONTRAST_ADJUSTMENT,
                it.contrastAdjustmentList,
                CopyAttributes.ContrastAdjustment.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_DARKNESS_ADJUSTMENT,
                it.darknessAdjustmentList,
                CopyAttributes.DarknessAdjustment.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_SHARPNESS_ADJUSTMENT,
                it.sharpnessAdjustmentList,
                CopyAttributes.SharpnessAdjustment.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_COLLATE_MODE,
                it.collateModeList,
                CopyAttributes.CollateMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_PAPER_SOURCE,
                it.paperSourceList,
                CopyAttributes.PaperSource.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_PAPER_TYPE,
                it.paperTypeList,
                CopyAttributes.PaperType.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_SCALE_MODE,
                it.scaleModeList,
                CopyAttributes.ScaleMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_TEXT_GRAPHICS_OPTIMIZATION,
                it.textGraphicsOptimizationList,
                CopyAttributes.PaperSource.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_NUMBER_UP_MODE,
                it.numberUpModeList,
                CopyAttributes.NumberUpMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_JOB_ASSEMBLY_MODE,
                it.jobAssemblyModeList,
                CopyAttributes.JobAssemblyMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_JOB_EXECUTION_MODE,
                it.jobExecutionModeList,
                CopyAttributes.JobExecutionMode.NORMAL.name
            )
            loadPrefFromCaps(
                PREF_OUTPUT_BIN,
                it.outputBinList,
                CopyAttributes.OutputBin.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_PROGRESS_DIALOG_MODE,
                it.progressDialogModeList,
                CopyAttributes.ProgressDialogMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_ERASE_MARGIN_UNIT,
                it.eraseMarginUnitList,
                CopyAttributes.EraseMarginUnit.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_CAPTURE_MODE,
                it.captureModeList,
                CopyAttributes.CaptureMode.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_IMAGE_SHIFT_REDUCE_TO_FIT,
                it.imageShiftReduceToFitList,
                CopyAttributes.ImageShiftReduceToFit.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_IMAGE_SHIFT_UNITS,
                it.imageShiftUnitsList,
                CopyAttributes.ImageShiftUnits.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_BOOKLET_FORMAT,
                it.bookletFormatList,
                CopyAttributes.BookletFormat.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_BOOKLET_BORDERS_EACH_PAGE,
                it.bookletBordersEachPageList,
                CopyAttributes.BookletBordersEachPage.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_BOOKLET_FINISHING_OPTION,
                it.bookletFinishingOptionList,
                CopyAttributes.BookletFinishingOption.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_STAPLE_MODE,
                it.stapleOptionList,
                CopyAttributes.StapleOption.NONE.name
            )
            loadPrefFromCaps(PREF_PUNCH_MODE, it.punchModeList, CopyAttributes.PunchMode.NONE.name)
            loadPrefFromCaps(PREF_FOLD_MODE, it.foldModeList, CopyAttributes.FoldMode.NONE.name)

            loadPrefFromCaps(
                PREF_WATERMARK_ROTATION,
                it.watermarkRotate45List,
                CopyAttributes.WatermarkRotate45.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_WATERMARK_TYPE,
                it.watermarkTypeList,
                CopyAttributes.WatermarkType.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_WATERMARK_MESSAGE_TYPE,
                it.watermarkMessageTypeList,
                CopyAttributes.WatermarkMessageType.CONFIDENTIAL.name
            )
            loadPrefFromCaps(
                PREF_WATERMARK_BACKGROUND_PATTERN,
                it.watermarkBackgroundPatternList,
                CopyAttributes.WatermarkBackgroundPattern.DEFAULT.name
            )
            loadPrefFromCaps(
                PREF_WATERMARK_ONLY_FIRST_PAGE,
                it.watermarkOnlyFirstPageList,
                CopyAttributes.WatermarkOnlyFirstPage.DEFAULT.name
            )
            loadPrefFromCaps(PREF_WATERMARK_FONT, it.watermarkFontList, WATERMARK_FONT_DEFAULT)
            loadPrefFromCaps(
                PREF_WATERMARK_BACKGROUND_COLOR,
                it.watermarkBackgroundColorList,
                WATERMARK_BACKGROUND_COLOR_DEFAULT
            )
            loadPrefFromCaps(
                PREF_WATERMARK_TEXT_COLOR,
                it.watermarkTextColorList,
                WATERMARK_TEXT_COLOR_DEFAULT
            )
            loadPrefFromCaps(
                PREF_WATERMARK_TEXT_SIZE,
                it.watermarkTextSizeList,
                WATERMARK_TEXT_SIZE
            )

            mCopiesPref?.setLimits(it.copiesRange.lowerBound, it.copiesRange.upperBound)
            mCopiesPref?.text = "1"
            mWatermarkDarknessPref?.setLimits(
                it.watermarkDarknessRange.lowerBound,
                it.watermarkDarknessRange.upperBound
            )
            mWatermarkDarknessPref?.text = "1"

            mWatermarkTransparencyPref?.setLimits(
                it.watermarkTransparencyRange.lowerBound,
                it.watermarkTransparencyRange.upperBound
            )
            mWatermarkTransparencyPref?.text = "1"
            val scalePercentRange =
                it.scalePercentRangeByScanSource[CopyAttributes.ScanSource.DEFAULT]
            if (scalePercentRange != null) {
                mScalePercentPref?.setLimits(
                    scalePercentRange.lowerBound,
                    scalePercentRange.upperBound
                )
            }
            mScanCustomLengthPref?.setLimits(
                it.scanCustomLengthRange.lowerBound,
                it.scanCustomLengthRange.upperBound
            )
            mScanCustomWidthPref?.setLimits(
                it.scanCustomWidthRange.lowerBound,
                it.scanCustomWidthRange.upperBound
            )
            mPrintCustomLengthPref?.setLimits(
                it.printCustomLengthRange.lowerBound,
                it.printCustomLengthRange.upperBound
            )
            mPrintCustomWidthPref?.setLimits(
                it.printCustomWidthRange.lowerBound,
                it.printCustomWidthRange.upperBound
            )

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
            mImageShiftFrontPref?.setXShiftLimits(
                it.imageShiftXFrontRange.lowerBound,
                it.imageShiftXFrontRange.upperBound
            )
            mImageShiftFrontPref?.setYShiftLimits(
                it.imageShiftYFrontRange.lowerBound,
                it.imageShiftYFrontRange.upperBound
            )
            mImageShiftBackPref?.setXShiftLimits(
                it.imageShiftXBackRange.lowerBound,
                it.imageShiftXBackRange.upperBound
            )
            mImageShiftBackPref?.setYShiftLimits(
                it.imageShiftYBackRange.lowerBound,
                it.imageShiftYBackRange.upperBound
            )
        }
        loadStampOptionPrefFromCaps(caps!!)
        loadPrintSizePrefFromCaps(caps, PREF_PRINT_SIZE, CopyAttributes.PaperSize.DEFAULT.name)
        loadScanSizePrefFromCaps(caps, PREF_SCAN_SIZE, CopyAttributes.ScanSize.DEFAULT.name)
    }

    private fun loadStampOptionPrefFromCaps(caps: CopyAttributesCaps) {
        mStampPref!!.setmStampOptionMap(HashMap())
        val mStampTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampType>> = HashMap()
        val mStampPolicyTypeListMap: MutableMap<CopyAttributes.StampPosition, List<StampPolicyType>> =
            HashMap()
        val mStampFontListMap: MutableMap<CopyAttributes.StampPosition, List<String>> = HashMap()
        val mStampTextSizeListMap: MutableMap<CopyAttributes.StampPosition, List<Int>> = HashMap()
        val mStampTextColorListMap: MutableMap<CopyAttributes.StampPosition, List<String>> =
            HashMap()
        val mStampWhiteBackGroundListMap: MutableMap<CopyAttributes.StampPosition, List<Boolean>> =
            HashMap()
        mStampPref!!.setmStampPositionList(caps.stampPositionList)
        for (stampPosition in caps.stampPositionList) {
            mStampTypeListMap[stampPosition] = caps.getStampTypeList(stampPosition)
            mStampPolicyTypeListMap[stampPosition] = caps.getStampPolicyTypeList(stampPosition)
            mStampFontListMap[stampPosition] = caps.getStampFormatFontList(stampPosition)
            mStampTextSizeListMap[stampPosition] = caps.getStampFormatTextSizeList(stampPosition)
            mStampTextColorListMap[stampPosition] = caps.getStampFormatTextColorList(stampPosition)
            mStampWhiteBackGroundListMap[stampPosition] =
                caps.getStampFormatWhiteBackgroundList(stampPosition)
        }
        mStampPref!!.setmStampTypeListMap(mStampTypeListMap)
        mStampPref!!.setmStampPolicyTypeListMap(mStampPolicyTypeListMap)
        mStampPref!!.setmStampFontListMap(mStampFontListMap)
        mStampPref!!.setmStampTextSizeListMap(mStampTextSizeListMap)
        mStampPref!!.setmStampTextColorListMap(mStampTextColorListMap)
        mStampPref!!.setmStampWhiteBackGroundListMap(mStampWhiteBackGroundListMap)
    }

    private fun loadPrintSizePrefFromCaps(
        caps: CopyAttributesCaps,
        prefName: String,
        defValue: String
    ) {
        val pref = findPreference<Preference>(prefName) as ListPreference?
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()
        for (os in caps.printSizeList) {
            val entrie = if (os.width == 0.0 && os.height == 0.0 && os.unit == null) {
                os.name
            } else {
                "${os.name} (${os.width} X ${os.height} ${os.unit})"
            }
            cmEntries.add(entrie)
            cmEntryValues.add(os.name)
        }
        if (cmEntries.size > 0) {
            pref?.let {
                it.entries = cmEntries.toTypedArray()
                it.entryValues = cmEntryValues.toTypedArray()
                it.setDefaultValue(defValue)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }
    }

    private fun loadScanSizePrefFromCaps(
        caps: CopyAttributesCaps,
        prefName: String,
        defValue: String
    ) {
        val pref = findPreference<Preference>(prefName) as ListPreference?
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()
        for (os in caps.scanSizeList) {
            val entrie = if (os.width == 0.0 && os.height == 0.0 && os.unit == null) {
                os.name
            } else {
                "${os.name} (${os.width} X ${os.height} ${os.unit})"
            }
            cmEntries.add(entrie)
            cmEntryValues.add(os.name)
        }
        if (cmEntries.size > 0) {
            pref?.let {
                it.entries = cmEntries.toTypedArray()
                it.entryValues = cmEntryValues.toTypedArray()
                it.setDefaultValue(defValue)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }
    }

    private fun loadPrefFromCaps(prefName: String, caps: List<*>?, defValue: String) {
        val pref = findPreference<Preference>(prefName) as ListPreference?
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()
        if (caps != null) {
            for (cm in caps) {
                if (cm != null) {
                    if (cm is Enum<*>) {
                        cmEntries.add(cm.name)
                        cmEntryValues.add(cm.name)
                    } else {
                        cmEntries.add(cm.toString())
                        cmEntryValues.add(cm.toString())
                    }
                }
            }
        }
        if (cmEntries.size > 0) {
            pref?.let {
                it.entries = cmEntries.toTypedArray()
                it.entryValues = cmEntryValues.toTypedArray()
                it.setDefaultValue(defValue)
                it.setValueIndex(0)
                it.summary = "%s"
            }
        }
    }

    private fun fillNumberUpDirectionAttrCaps(sharedPreferences: SharedPreferences) {
        val numberUpMode = sharedPreferences.getString(
            PREF_NUMBER_UP_MODE,
            CopyAttributes.NumberUpMode.DEFAULT.name
        )?.let { CopyAttributes.NumberUpMode.valueOf(it) }
        if (numberUpMode != CopyAttributes.NumberUpMode.DEFAULT) {
            mBaseAttributesCategory?.addPreference(mNumberUpDirectionPref)
            if (this::mCaps.isInitialized) {
                loadPrefFromCaps(
                    PREF_NUMBER_UP_DIRECTION,
                    mCaps.numberUpDirectionByNumberUpCount[numberUpMode],
                    CopyAttributes.NumberUpDirection.DEFAULT.name
                )
            }
        } else {
            mBaseAttributesCategory?.removePreference(mNumberUpDirectionPref)
        }
    }

    private fun fillScalePercentRange(sharedPreferences: SharedPreferences) {
        val scanSource =
            sharedPreferences.getString(PREF_SCAN_SOURCE, CopyAttributes.ScanSource.DEFAULT.name)
                ?.let { CopyAttributes.ScanSource.valueOf(it) }
        if (this::mCaps.isInitialized) {
            val scalePercentRange = mCaps.scalePercentRangeByScanSource[scanSource]
            scalePercentRange?.run {
                mScalePercentPref?.setLimits(lowerBound, upperBound)
            }
        } else {
            mScalePercentPref?.setLimits(100, 100)
        }
    }

    private fun showOriginalCustomSizePreference(preferences: SharedPreferences, key: String) {
        val scanSize = preferences.getString(key, CopyAttributes.ScanSize.DEFAULT.name)
            ?.let { CopyAttributes.ScanSize.valueOf(it) }
        if (scanSize == CopyAttributes.ScanSize.CUSTOM) {
            mScanCustomLengthPref?.summary = mScanCustomLengthPref?.text
            mScanCustomWidthPref?.summary = mScanCustomWidthPref?.text
            mBaseAttributesCategory?.addPreference(mScanCustomLengthPref)
            mBaseAttributesCategory?.addPreference(mScanCustomWidthPref)
        } else {
            mBaseAttributesCategory?.removePreference(mScanCustomLengthPref)
            mBaseAttributesCategory?.removePreference(mScanCustomWidthPref)
        }
    }

    private fun showOutputCustomSizePreference(preferences: SharedPreferences, key: String) {
        val printSize = preferences.getString(key, CopyAttributes.PaperSize.DEFAULT.name)
            ?.let { CopyAttributes.PaperSize.valueOf(it) }
        if (printSize == CopyAttributes.PaperSize.CUSTOM) {
            mPrintCustomLengthPref?.summary = mPrintCustomLengthPref?.text
            mPrintCustomWidthPref?.summary = mPrintCustomWidthPref?.text
            mBaseAttributesCategory?.addPreference(mPrintCustomLengthPref)
            mBaseAttributesCategory?.addPreference(mPrintCustomWidthPref)
        } else {
            mBaseAttributesCategory?.removePreference(mPrintCustomLengthPref)
            mBaseAttributesCategory?.removePreference(mPrintCustomWidthPref)
        }
    }

    private fun showScalePercentPreference(preferences: SharedPreferences, key: String) {
        val scaleMode = preferences.getString(key, CopyAttributes.ScaleMode.DEFAULT.name)
            ?.let { CopyAttributes.ScaleMode.valueOf(it) }
        if (scaleMode == CopyAttributes.ScaleMode.MANUAL) {
            mBaseAttributesCategory?.addPreference(mScalePercentPref)
        } else {
            mBaseAttributesCategory?.removePreference(mScalePercentPref)
        }
    }

    private fun initiateStorePassword(preferences: SharedPreferences, key: String) {
        val type = preferences.getString(key, JobCredentialsAttributes.PasswordType.NONE.name)
            ?.let { JobCredentialsAttributes.PasswordType.valueOf(it) }
        if (JobCredentialsAttributes.PasswordType.NONE.name == type?.name) {
            clearJobPassword()
        }
    }

    fun clearJobPassword() {
        if (mJobPasswordPref != null) {
            mJobPasswordPref?.text = null
            mJobPasswordPref?.summary = null
        }
    }

    fun setDefaultCopyAttributes(copyAttributes: CopyAttributes?) {
        if (copyAttributes != null) {
            val copyAttributesReader = CopyAttributesReader(copyAttributes)
            setPreferenceEntryValue(
                PREF_BACKGROUND_CLEANUP,
                copyAttributesReader.backgroundCleanup.name
            )
            setPreferenceEntryValue(PREF_COLLATE_MODE, copyAttributesReader.collateMode.name)
            setPreferenceEntryValue(PREF_COLOR_MODE, copyAttributesReader.colorMode.name)
            setPreferenceEntryValue(
                PREF_CONTRAST_ADJUSTMENT,
                copyAttributesReader.contrastAdjustment.name
            )
            setPreferenceEntryValue(
                PREF_TEXT_GRAPHICS_OPTIMIZATION,
                copyAttributesReader.textGraphicsOptimization.name
            )
            setPreferenceEntryValue(PREF_COPY_PREVIEW, copyAttributesReader.copyPreview.name)
            setPreferenceEntryValue(
                PREF_DARKNESS_ADJUSTMENT,
                copyAttributesReader.darknessAdjustment.name
            )
            setPreferenceEntryValue(
                PREF_JOB_ASSEMBLY_MODE,
                copyAttributesReader.jobAssemblyMode.name
            )
            setPreferenceEntryValue(
                PREF_JOB_EXECUTION_MODE,
                copyAttributesReader.jobExecutionMode.name
            )
            setPreferenceEntryValue(PREF_NUMBER_UP_MODE, copyAttributesReader.numberUpMode.name)
            setPreferenceEntryValue(PREF_NUMBER_UP_DIRECTION, copyAttributesReader.numberUpDirection.name)
            setPreferenceEntryValue(PREF_ORIENTATION, copyAttributesReader.orientation.name)
            setPreferenceEntryValue(PREF_PAPER_SOURCE, copyAttributesReader.paperSource.name)
            setPreferenceEntryValue(PREF_PAPER_TYPE, copyAttributesReader.paperType.name)
            setPreferenceEntryValue(PREF_PRINT_DUPLEX_MODE, copyAttributesReader.printDuplex.name)
            setPreferenceEntryValue(PREF_PRINT_SIZE, copyAttributesReader.printSize.name)
            setPreferenceEntryValue(PREF_SCALE_MODE, copyAttributesReader.scaleMode.name)
            setPreferenceEntryValue(PREF_SCAN_DUPLEX_MODE, copyAttributesReader.scanDuplex.name)
            setPreferenceEntryValue(PREF_SCAN_SIZE, copyAttributesReader.scanSize.name)
            setPreferenceEntryValue(PREF_SCAN_SOURCE, copyAttributesReader.scanSource.name)
            setPreferenceEntryValue(
                PREF_SHARPNESS_ADJUSTMENT,
                copyAttributesReader.sharpnessAdjustment.name
            )
            setPreferenceEntryValue(PREF_OUTPUT_BIN, copyAttributesReader.outputBin.name)
            setPreferenceEntryValue(
                PREF_PROGRESS_DIALOG_MODE,
                copyAttributesReader.progressDialogMode.name
            )
            if (copyAttributesReader.eraseMarginUnit != null) {
                setPreferenceEntryValue(
                    PREF_ERASE_MARGIN_UNIT,
                    copyAttributesReader.eraseMarginUnit.name
                )
            }

            val backMargin = Margins(
                copyAttributesReader.eraseBackLeftMargin,
                copyAttributesReader.eraseBackTopMargin,
                copyAttributesReader.eraseBackRightMargin,
                copyAttributesReader.eraseBackBottomMargin
            )

            val frontMargin = Margins(
                copyAttributesReader.eraseFrontLeftMargin,
                copyAttributesReader.eraseFrontTopMargin,
                copyAttributesReader.eraseFrontRightMargin,
                copyAttributesReader.eraseFrontBottomMargin
            )

            setMarginPreferenceValue(PREF_ERASE_BACK_MARGIN, backMargin)
            setMarginPreferenceValue(PREF_ERASE_FRONT_MARGIN, frontMargin)

            val imageShiftFront = Shifts(
                copyAttributesReader.imageShiftXFront,
                copyAttributesReader.imageShiftYFront
            )

            val imageShiftBack = Shifts(
                copyAttributesReader.imageShiftXBack,
                copyAttributesReader.imageShiftYBack
            )

            setImageShitPreferenceValue(PREF_IMAGE_SHIFT_FRONT, imageShiftFront)
            setImageShitPreferenceValue(PREF_IMAGE_SHIFT_BACK, imageShiftBack)

            setPreferenceEntryValue(PREF_CAPTURE_MODE, copyAttributesReader.captureMode.name)
            setPreferenceEntryValue(
                PREF_IMAGE_SHIFT_REDUCE_TO_FIT,
                copyAttributesReader.imageShiftReduceToFit.name
            )
            setPreferenceEntryValue(
                PREF_IMAGE_SHIFT_UNITS,
                copyAttributesReader.imageShiftUnits.name
            )
            setPreferenceEntryValue(
                PREF_BOOKLET_BORDERS_EACH_PAGE,
                copyAttributesReader.bookletBordersEachPage.name
            )
            setPreferenceEntryValue(
                PREF_BOOKLET_FINISHING_OPTION,
                copyAttributesReader.bookletFinishingOption.name
            )
            setPreferenceEntryValue(PREF_BOOKLET_FORMAT, copyAttributesReader.bookletFormat.name)
            setPreferenceEntryValue(PREF_FOLD_MODE, copyAttributesReader.foldMode.name)
            setPreferenceEntryValue(PREF_STAPLE_MODE, copyAttributesReader.stapleOption.name)
            setPreferenceEntryValue(PREF_PUNCH_MODE, copyAttributesReader.punchMode.name)

            setPreferenceValue(PREF_COPIES, copyAttributesReader.copies.toString())
            setPreferenceValue(PREF_SCALE_PERCENT, copyAttributesReader.scalePercent.toString())
            setPreferenceEntryValue(
                PREF_WATERMARK_ROTATION,
                copyAttributesReader.watermarkRotation45.name
            )
            setPreferenceEntryValue(
                PREF_WATERMARK_ONLY_FIRST_PAGE,
                copyAttributesReader.watermarkOnlyFirstPage.name
            )
            setPreferenceEntryValue(PREF_WATERMARK_TYPE, copyAttributesReader.watermarkType.name)
            setPreferenceEntryValue(
                PREF_WATERMARK_BACKGROUND_PATTERN,
                copyAttributesReader.watermarkBackgroundPattern.name
            )
            setPreferenceEntryValue(
                PREF_WATERMARK_MESSAGE_TYPE,
                copyAttributesReader.watermarkMessageType.name
            )
            setPreferenceValue(
                PREF_WATERMARK_DARKNESS,
                copyAttributesReader.watermarkDarkness.toString()
            )
            setPreferenceEntryValue(
                PREF_WATERMARK_TEXT_SIZE,
                copyAttributesReader.watermarkTextSize.toString()
            )
            setPreferenceValue(
                PREF_WATERMARK_TRANSPARENCY,
                copyAttributesReader.watermarkTransparency.toString()
            )
            if (copyAttributesReader.watermarkText != null) {
                setPreferenceValue(PREF_WATERMARK_TEXT, copyAttributesReader.watermarkText)
            } else {
                setPreferenceValue(PREF_WATERMARK_TEXT, "")
            }
            if (copyAttributesReader.watermarkFont != null) {
                setPreferenceEntryValue(PREF_WATERMARK_FONT, copyAttributesReader.watermarkFont)
            } else {
                setPreferenceEntryValue(PREF_WATERMARK_FONT, WATERMARK_FONT_DEFAULT)
            }
            if (copyAttributesReader.watermarkBackgroundColor != null) {
                setPreferenceEntryValue(
                        PREF_WATERMARK_BACKGROUND_COLOR,
                        copyAttributesReader.watermarkBackgroundColor
                )
            } else {
                setPreferenceEntryValue(
                        PREF_WATERMARK_BACKGROUND_COLOR,
                        WATERMARK_BACKGROUND_COLOR_DEFAULT
                )
            }
            if (copyAttributesReader.watermarkTextColor != null) {
                setPreferenceEntryValue(PREF_WATERMARK_TEXT_COLOR, copyAttributesReader.watermarkTextColor)
            } else {
                setPreferenceEntryValue(PREF_WATERMARK_TEXT_COLOR, PREF_WATERMARK_TEXT_COLOR)
            }

            val stampOptionMap = copyAttributesReader.stampOption
            val stampOptionDialogPreference =
                findPreference<Preference>(PREF_STAMP) as StampOptionDialogPreference?
            stampOptionDialogPreference!!.setmStampOptionMap(stampOptionMap)

            setPreferenceValue(PREF_CUSTOM_LENGTH,copyAttributesReader.printCustomLength.toString())
            setPreferenceValue(PREF_CUSTOM_WIDTH,copyAttributesReader.printCustomWidth.toString())
        }
    }

    private fun setImageShitPreferenceValue(prefImageShiftFront: String, imageShift: Shifts) {
        val imageShiftPreference =
            findPreference<Preference>(prefImageShiftFront) as ShiftPreference?
        if (imageShiftPreference != null) {
            imageShiftPreference.setShifts(
                imageShift.xShift,
                imageShift.yShift
            )
            imageShiftPreference.applyShifts()
            imageShiftPreference.summary =
                context?.getString(R.string.summary_shift, imageShift.xShift, imageShift.yShift)
        }
    }

    private fun setMarginPreferenceValue(prefCopies: String, backMargin: Margins) {
        val eraseMarginPreference = findPreference<Preference>(prefCopies) as MarginsPreference?
        if (eraseMarginPreference != null) {
            eraseMarginPreference.setMargins(
                backMargin.leftMargin,
                backMargin.topMargin,
                backMargin.rightMargin,
                backMargin.bottomMargin
            )
            eraseMarginPreference.applyMargins()
            eraseMarginPreference.summary = context?.getString(
                R.string.summary_margin,
                backMargin.leftMargin,
                backMargin.topMargin,
                backMargin.rightMargin,
                backMargin.bottomMargin
            )
        }

    }

    private fun setPreferenceValue(pref: String, value: String) {
        val editTextPreference = findPreference<Preference>(pref) as EditTextPreference?
        if (editTextPreference != null) {
            editTextPreference.text = value
            editTextPreference.summary = value
        }
    }

    private fun setPreferenceEntryValue(pref: String, attribute: String) {
        val listPreference = findPreference<Preference>(pref) as ListPreference?
        var index = 0
        listPreference?.run {
            for (value in entryValues) {
                if (value != null && value.toString() == attribute) {
                    break
                }
                index++
            }
            if (index < entryValues.size) {
                setValueIndex(index)
            }
        }
    }

    companion object {
        // Preferences keys for CopyAttributes
        const val PREF_COPIES = "pref_copies"
        const val PREF_COLOR_MODE = "pref_colorMode"
        const val PREF_ORIENTATION = "pref_orientation"
        const val PREF_SCAN_DUPLEX_MODE = "pref_scanDuplexMode"
        const val PREF_PRINT_DUPLEX_MODE = "pref_printDuplexMode"
        const val PREF_SCAN_SIZE = "pref_scanSize"
        const val PREF_SCAN_CUSTOM_LENGTH = "pref_scan_customLength"
        const val PREF_SCAN_CUSTOM_WIDTH = "pref_scan_customWidth"
        const val PREF_PRINT_SIZE = "pref_printSize"
        const val PREF_PRINT_CUSTOM_LENGTH = "pref_print_customLength"
        const val PREF_PRINT_CUSTOM_WIDTH = "pref_print_customWidth"
        const val PREF_COPY_PREVIEW = "pref_copyPreview"
        const val PREF_SCAN_SOURCE = "pref_scanSource"
        const val PREF_BACKGROUND_CLEANUP = "pref_backgroundCleanup"
        const val PREF_CONTRAST_ADJUSTMENT = "pref_contrastAdjustment"
        const val PREF_DARKNESS_ADJUSTMENT = "pref_darknessAdjustment"
        const val PREF_SHARPNESS_ADJUSTMENT = "pref_sharpnessAdjustment"
        const val PREF_TEXT_GRAPHICS_OPTIMIZATION = "pref_textGraphicsOptimization"
        const val PREF_COLLATE_MODE = "pref_collateMode"
        const val PREF_PAPER_SOURCE = "pref_paperSource"
        const val PREF_PAPER_TYPE = "pref_paperType"
        const val PREF_SCALE_MODE = "pref_scaleMode"
        const val PREF_SCALE_PERCENT = "pref_scalePercent"
        const val PREF_NUMBER_UP_MODE = "pref_numberUpMode"
        const val PREF_NUMBER_UP_DIRECTION = "pref_numberUpDirection"
        const val PREF_JOB_ASSEMBLY_MODE = "pref_jobAssemblyMode"
        const val PREF_JOB_EXECUTION_MODE = "pref_jobExecutionMode"
        const val PREF_OUTPUT_BIN = "pref_outputBin"
        const val PREF_PROGRESS_DIALOG_MODE = "pref_progressDialogMode"
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
        const val PREF_IMAGE_SHIFT_REDUCE_TO_FIT = "pref_imageShiftReduceToFit"
        const val PREF_IMAGE_SHIFT_UNITS = "pref_imageShiftUnits"
        const val PREF_IMAGE_SHIFT_FRONT = "pref_imageShiftFront"
        const val PREF_IMAGE_SHIFT_BACK = "pref_imageShiftBack"
        const val PREF_IMAGE_SHIFT_X_FRONT = "pref_imageShiftFrontxShift"
        const val PREF_IMAGE_SHIFT_Y_FRONT = "pref_imageShiftFrontyShift"
        const val PREF_IMAGE_SHIFT_X_BACK = "pref_imageShiftBackxShift"
        const val PREF_IMAGE_SHIFT_Y_BACK = "pref_imageShiftBackyShift"
        const val PREF_BOOKLET_BORDERS_EACH_PAGE = "pref_bookletBordersEachPage"
        const val PREF_BOOKLET_FINISHING_OPTION = "pref_bookletFinishingOption"
        const val PREF_BOOKLET_FORMAT = "pref_bookletFormat"
        const val PREF_FOLD_MODE = "pref_foldMode"
        const val PREF_STAPLE_MODE = "pref_stapleMode"
        const val PREF_PUNCH_MODE = "pref_punchMode"
        const val PREF_WATERMARK_ROTATION = "pref_watermarkRotate"
        const val PREF_WATERMARK_TEXT = "pref_watermarkText"
        const val PREF_WATERMARK_DARKNESS = "pref_watermarkDarkness"
        const val PREF_WATERMARK_TEXT_SIZE = "pref_watermarkTextSize"
        const val PREF_WATERMARK_TYPE = "pref_watermarkType"
        const val PREF_WATERMARK_MESSAGE_TYPE = "pref_watermarkMessageType"
        const val PREF_WATERMARK_BACKGROUND_PATTERN = "pref_watermarkBackgroundPattern"
        const val PREF_WATERMARK_TRANSPARENCY = "pref_watermarkTransparency"
        const val PREF_WATERMARK_BACKGROUND_COLOR = "pref_watermarkBackgroundColor"
        const val PREF_WATERMARK_FONT = "pref_watermarkFont"
        const val PREF_WATERMARK_TEXT_COLOR = "pref_watermarkTextColor"
        const val PREF_WATERMARK_ONLY_FIRST_PAGE = "pref_watermarkOnlyFirstPage"

        const val PREF_STAMP = "pref_stamp"
        const val PREF_STORE_JOB_NAME = "pref_storedJobName"
        const val PREF_STORE_JOB_FOLDER_NAME = "pref_storedJobFolderName"
        const val PREF_STORE_DELETE_ON_POWER = "pref_storedDeleteOnPower"
        const val PREF_STORE_DELETE_ON_RELEASE = "pref_storedDeleteOnRelease"
        const val PREF_STORE_JOB_PASSWORD_TYPE = "pref_storedJobPasswordType"
        const val PREF_STORE_JOB_PASSWORD = "pref_storedJobPassword"
        const val PREF_BASE_ATTRIBUTES_CATEGORY = "base_attributes_category"
        const val PREF_DESTINATION_STORE_CATEGORY = "destination_store_category"
        const val PREF_CUSTOM_LENGTH = "pref_print_customLength"
        const val PREF_CUSTOM_WIDTH = "pref_print_customWidth"



        // Feedback / UI preferences
        const val PREF_MONITOR_JOB = "pref_monitorJob"
        const val PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress"
        const val PREF_SETTINGS_UI = "pref_settingsUi"

        // Preference for current job id
        const val CURRENT_JOB_ID = "pref_currentJobId"

        const val OPTION_DEFAULT = "DEFAULT"
        const val OPTION_OFF = "OFF"
        const val OPTION_LEFTEDGE = "LEFTEDGE"
        const val WATERMARK_FONT_DEFAULT = "LetterGothic"
        const val WATERMARK_BACKGROUND_COLOR_DEFAULT = "Gray"
        const val WATERMARK_TEXT_COLOR_DEFAULT = "Black"
        const val WATERMARK_TEXT_SIZE = "40"


    }
}