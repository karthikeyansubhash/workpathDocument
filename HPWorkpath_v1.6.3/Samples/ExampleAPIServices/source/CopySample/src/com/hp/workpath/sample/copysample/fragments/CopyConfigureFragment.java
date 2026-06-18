// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.CopyAttributes.BackgroundCleanup;
import com.hp.workpath.api.copier.CopyAttributes.CollateMode;
import com.hp.workpath.api.copier.CopyAttributes.ColorMode;
import com.hp.workpath.api.copier.CopyAttributes.ContrastAdjustment;
import com.hp.workpath.api.copier.CopyAttributes.CopyPreview;
import com.hp.workpath.api.copier.CopyAttributes.DarknessAdjustment;
import com.hp.workpath.api.copier.CopyAttributes.Duplex;
import com.hp.workpath.api.copier.CopyAttributes.JobAssemblyMode;
import com.hp.workpath.api.copier.CopyAttributes.JobExecutionMode;
import com.hp.workpath.api.copier.CopyAttributes.NumberUpDirection;
import com.hp.workpath.api.copier.CopyAttributes.NumberUpMode;
import com.hp.workpath.api.copier.CopyAttributes.Orientation;
import com.hp.workpath.api.copier.CopyAttributes.PaperSize;
import com.hp.workpath.api.copier.CopyAttributes.PaperSource;
import com.hp.workpath.api.copier.CopyAttributes.PaperType;
import com.hp.workpath.api.copier.CopyAttributes.RetentionMode;
import com.hp.workpath.api.copier.CopyAttributes.ScaleMode;
import com.hp.workpath.api.copier.CopyAttributes.ScanSize;
import com.hp.workpath.api.copier.CopyAttributes.ScanSource;
import com.hp.workpath.api.copier.CopyAttributes.SharpnessAdjustment;
import com.hp.workpath.api.copier.CopyAttributesCaps;
import com.hp.workpath.api.copier.CopyAttributesReader;
import com.hp.workpath.api.copier.JobCredentialsAttributes;
import com.hp.workpath.api.copier.Range;
import com.hp.workpath.api.copier.Shifts;
import com.hp.workpath.api.copier.StampOption;
import com.hp.workpath.api.copier.StampPolicyType;
import com.hp.workpath.api.copier.StampType;
import com.hp.workpath.api.scanner.Margins;
import com.hp.workpath.sample.copysample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple {@link PreferenceFragmentCompat} to set Copy Attributes and save into preferences.
 */
public final class CopyConfigureFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Preferences keys for CopyAttributes
    public static final String PREF_COPIES = "pref_copies";
    public static final String PREF_COLOR_MODE = "pref_colorMode";
    public static final String PREF_ORIENTATION = "pref_orientation";
    public static final String PREF_SCAN_DUPLEX_MODE = "pref_scanDuplexMode";
    public static final String PREF_PRINT_DUPLEX_MODE = "pref_printDuplexMode";
    public static final String PREF_SCAN_SIZE = "pref_scanSize";
    public static final String PREF_SCAN_CUSTOM_LENGTH = "pref_scan_customLength";
    public static final String PREF_SCAN_CUSTOM_WIDTH = "pref_scan_customWidth";
    public static final String PREF_PRINT_SIZE = "pref_printSize";
    public static final String PREF_PRINT_CUSTOM_LENGTH = "pref_print_customLength";
    public static final String PREF_PRINT_CUSTOM_WIDTH = "pref_print_customWidth";
    public static final String PREF_COPY_PREVIEW = "pref_copyPreview";
    public static final String PREF_SCAN_SOURCE = "pref_scanSource";
    public static final String PREF_BACKGROUND_CLEANUP = "pref_backgroundCleanup";
    public static final String PREF_CONTRAST_ADJUSTMENT = "pref_contrastAdjustment";
    public static final String PREF_DARKNESS_ADJUSTMENT = "pref_darknessAdjustment";
    public static final String PREF_SHARPNESS_ADJUSTMENT = "pref_sharpnessAdjustment";
    public static final String PREF_TEXT_GRAPHICS_OPTIMIZATION = "pref_textGraphicsOptimization";
    public static final String PREF_COLLATE_MODE = "pref_collateMode";
    public static final String PREF_PAPER_SOURCE = "pref_paperSource";
    public static final String PREF_PAPER_TYPE = "pref_paperType";
    public static final String PREF_SCALE_MODE = "pref_scaleMode";
    public static final String PREF_SCALE_PERCENT = "pref_scalePercent";
    public static final String PREF_NUMBER_UP_MODE = "pref_numberUpMode";
    public static final String PREF_NUMBER_UP_DIRECTION = "pref_numberUpDirection";
    public static final String PREF_JOB_ASSEMBLY_MODE = "pref_jobAssemblyMode";
    public static final String PREF_JOB_EXECUTION_MODE = "pref_jobExecutionMode";
    public static final String PREF_STORE_JOB_NAME = "pref_storedJobName";
    public static final String PREF_STORE_JOB_FOLDER_NAME = "pref_storedJobFolderName";
    public static final String PREF_STORE_DELETE_ON_POWER = "pref_storedDeleteOnPower";
    public static final String PREF_STORE_DELETE_ON_RELEASE = "pref_storedDeleteOnRelease";
    public static final String PREF_STORE_JOB_PASSWORD_TYPE = "pref_storedJobPasswordType";
    public static final String PREF_STORE_JOB_PASSWORD = "pref_storedJobPassword";
    public static final String PREF_OUTPUT_BIN = "pref_outputBin";
    public static final String PREF_PROGRESS_DIALOG_MODE = "pref_progressDialogMode";
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
    public static final String PREF_IMAGE_SHIFT_REDUCE_TO_FIT = "pref_imageShiftReduceToFit";
    public static final String PREF_IMAGE_SHIFT_UNITS = "pref_imageShiftUnits";
    public static final String PREF_IMAGE_SHIFT_FRONT = "pref_imageShiftFront";
    public static final String PREF_IMAGE_SHIFT_BACK = "pref_imageShiftBack";
    public static final String PREF_IMAGE_SHIFT_X_FRONT = "pref_imageShiftFrontxShift";
    public static final String PREF_IMAGE_SHIFT_Y_FRONT = "pref_imageShiftFrontyShift";
    public static final String PREF_IMAGE_SHIFT_X_BACK = "pref_imageShiftBackxShift";
    public static final String PREF_IMAGE_SHIFT_Y_BACK = "pref_imageShiftBackyShift";
    public static final String PREF_BOOKLET_BORDERS_EACH_PAGE = "pref_bookletBordersEachPage";
    public static final String PREF_BOOKLET_FINISHING_OPTION = "pref_bookletFinishingOption";
    public static final String PREF_BOOKLET_FORMAT = "pref_bookletFormat";
    public static final String PREF_FOLD_MODE = "pref_foldMode";
    public static final String PREF_STAPLE_MODE = "pref_stapleMode";
    public static final String PREF_PUNCH_MODE = "pref_punchMode";
    public static final String PREF_WATERMARK_ROTATION = "pref_watermarkRotate";
    public static final String PREF_WATERMARK_TEXT = "pref_watermarkText";
    public static final String PREF_WATERMARK_DARKNESS = "pref_watermarkDarkness";
    public static final String PREF_WATERMARK_TEXT_SIZE = "pref_watermarkTextSize";
    public static final String PREF_WATERMARK_TYPE = "pref_watermarkType";
    public static final String PREF_WATERMARK_MESSAGE_TYPE = "pref_watermarkMessageType";
    public static final String PREF_WATERMARK_BACKGROUND_PATTERN = "pref_watermarkBackgroundPattern";
    public static final String PREF_WATERMARK_TRANSPARENCY = "pref_watermarkTransparency";
    public static final String PREF_WATERMARK_BACKGROUND_COLOR = "pref_watermarkBackgroundColor";
    public static final String PREF_WATERMARK_FONT = "pref_watermarkFont";
    public static final String PREF_WATERMARK_TEXT_COLOR = "pref_watermarkTextColor";
    public static final String PREF_WATERMARK_ONLY_FIRST_PAGE = "pref_watermarkOnlyFirstPage";

    public static final String PREF_CUSTOM_LENGTH = "pref_print_customLength";

    public static final String PREF_CUSTOM_WIDTH = "pref_print_customWidth";

    public static final String PREF_STAMP = "pref_stamp";

    public static final String WATERMARK_FONT_DEFAULT = "LetterGothic";
    public static final String WATERMARK_BACKGROUND_COLOR_DEFAULT = "Gray";
    public static final String WATERMARK_TEXT_COLOR_DEFAULT = "Black";
    public static final String WATERMARK_TEXT_SIZE = "40";

    public static final String OPTION_DEFAULT = "DEFAULT";
    public static final String OPTION_OFF = "OFF";
    public static final String OPTION_LEFTEDGE = "LEFTEDGE";

    public static final String PREF_BASE_ATTRIBUTES_CATEGORY = "base_attributes_category";
    public static final String PREF_DESTINATION_STORE_CATEGORY = "destination_store_category";

    // Feedback / UI preferences
    public static final String PREF_MONITOR_JOB = "pref_monitorJob";
    public static final String PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress";
    public static final String PREF_SETTINGS_UI = "pref_settingsUi";

    // Preference for current job id
    public static final String CURRENT_JOB_ID = "pref_currentJobId";

    private ListPreference mNumberUpDirectionPref;
    private EditTextPreference mJobNamePref;
    private EditTextPreference mJobFolderPref;
    private EditTextPreference mJobPasswordPref;
    private EditTextIntegerPreference mCopiesPref;
    private EditTextIntegerPreference mScalePercentPref;
    private EditTextFloatPreference mScanCustomLengthPref;
    private EditTextFloatPreference mScanCustomWidthPref;
    private EditTextFloatPreference mPrintCustomLengthPref;
    private EditTextFloatPreference mPrintCustomWidthPref;

    private PreferenceCategory mBaseAttributesCategory;
    private PreferenceCategory mStoreCategory;

    private CopyAttributesCaps mCaps;

    private MarginsPreference mEraseBackMarginPref;
    private MarginsPreference mEraseFrontMarginPref;
    private ShiftPreference mImageShiftFrontPref;
    private ShiftPreference mImageShiftBackPref;
    private EditTextIntegerPreference mWatermarkDarknessPref;
    private EditTextPreference mWatermarkTextPref;

    private ListPreference mWatermarkTextSizePref;
    private EditTextIntegerPreference mWatermarkTransparencyPref;
    private ListPreference mWatermarkBackgroundColorPref;
    private ListPreference mWatermarkFontPref;
    private ListPreference mWatermarkTextColorPref;
    private StampOptionDialogPreference mStampPref;
    private  String mWatermarktype;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.copy_preferences);

        // Set default limits to single, default, value
        mCopiesPref = ((EditTextIntegerPreference) findPreference(PREF_COPIES));
        mCopiesPref.setLimits(1, 1);

        // Set default limits to single, default, value
        mScalePercentPref = ((EditTextIntegerPreference) findPreference(PREF_SCALE_PERCENT));
        mScalePercentPref.setLimits(100, 100);

        mScanCustomLengthPref = (EditTextFloatPreference) findPreference(PREF_SCAN_CUSTOM_LENGTH);
        mScanCustomLengthPref.setLimits(0, 0);
        mScanCustomLengthPref.setText(null);
        mScanCustomWidthPref = (EditTextFloatPreference) findPreference(PREF_SCAN_CUSTOM_WIDTH);
        mScanCustomWidthPref.setLimits(0, 0);
        mScanCustomWidthPref.setText(null);

        mPrintCustomLengthPref = (EditTextFloatPreference) findPreference(PREF_PRINT_CUSTOM_LENGTH);
        mPrintCustomLengthPref.setLimits(0, 0);
        mPrintCustomLengthPref.setText(null);
        mPrintCustomWidthPref = (EditTextFloatPreference) findPreference(PREF_PRINT_CUSTOM_WIDTH);
        mPrintCustomWidthPref.setLimits(0, 0);
        mPrintCustomWidthPref.setText(null);

        mJobNamePref = (EditTextPreference) findPreference(PREF_STORE_JOB_NAME);
        if (mJobNamePref != null) {
            mJobNamePref.setOnBindEditTextListener(editText -> {
                editText.selectAll();
                int maxLength = 256;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            });
        }
        mJobNamePref.setText(null);
        mJobFolderPref = (EditTextPreference) findPreference(PREF_STORE_JOB_FOLDER_NAME);
        if (mJobFolderPref != null) {
            mJobFolderPref.setOnBindEditTextListener(editText -> {
                editText.selectAll();
                int maxLength = 256;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            });
        }
        mJobFolderPref.setText(null);
        mJobPasswordPref = (EditTextPreference) findPreference(PREF_STORE_JOB_PASSWORD);
        mJobPasswordPref.setText(null);

        mNumberUpDirectionPref = (ListPreference) findPreference(PREF_NUMBER_UP_DIRECTION);

        mBaseAttributesCategory = (PreferenceCategory) findPreference(PREF_BASE_ATTRIBUTES_CATEGORY);
        mStoreCategory = (PreferenceCategory) findPreference(PREF_DESTINATION_STORE_CATEGORY);

        mEraseBackMarginPref = findPreference(PREF_ERASE_BACK_MARGIN);
        mEraseFrontMarginPref = findPreference(PREF_ERASE_FRONT_MARGIN);
        mImageShiftFrontPref = findPreference(PREF_IMAGE_SHIFT_FRONT);
        mImageShiftBackPref = findPreference(PREF_IMAGE_SHIFT_BACK);
        mWatermarkDarknessPref = findPreference(PREF_WATERMARK_DARKNESS);
        mWatermarkDarknessPref.setText("1");
        mWatermarkDarknessPref.setLimits(0, 2);

        mWatermarkTextSizePref = findPreference(PREF_WATERMARK_TEXT_SIZE);

        mWatermarkTransparencyPref = findPreference(PREF_WATERMARK_TRANSPARENCY);
        mWatermarkTransparencyPref.setText("1");
        mWatermarkTransparencyPref.setLimits(0, 0);

        mWatermarkBackgroundColorPref = findPreference(PREF_WATERMARK_BACKGROUND_COLOR);

        mWatermarkFontPref = findPreference(PREF_WATERMARK_FONT);

        mWatermarkTextColorPref = findPreference(PREF_WATERMARK_TEXT_COLOR);

        mWatermarkTextPref = findPreference(PREF_WATERMARK_TEXT);
        mWatermarkTextPref.setText(null);

        mStampPref = (StampOptionDialogPreference)findPreference(PREF_STAMP);

        if (mCaps != null) {
            loadCapabilities(mCaps);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

        prefs.registerOnSharedPreferenceChangeListener(this);

        refreshAllPrefs(prefs);
    }

    private void refreshAllPrefs(final SharedPreferences prefs) {
        onSharedPreferenceChanged(prefs, PREF_COPIES);
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE);
        onSharedPreferenceChanged(prefs, PREF_ORIENTATION);
        onSharedPreferenceChanged(prefs, PREF_SCAN_DUPLEX_MODE);
        onSharedPreferenceChanged(prefs, PREF_PRINT_DUPLEX_MODE);
        onSharedPreferenceChanged(prefs, PREF_SCAN_SIZE);
        onSharedPreferenceChanged(prefs, PREF_SCAN_CUSTOM_LENGTH);
        onSharedPreferenceChanged(prefs, PREF_SCAN_CUSTOM_WIDTH);
        onSharedPreferenceChanged(prefs, PREF_PRINT_SIZE);
        onSharedPreferenceChanged(prefs, PREF_PRINT_CUSTOM_LENGTH);
        onSharedPreferenceChanged(prefs, PREF_PRINT_CUSTOM_WIDTH);
        onSharedPreferenceChanged(prefs, PREF_COPY_PREVIEW);
        onSharedPreferenceChanged(prefs, PREF_SCAN_SOURCE);
        onSharedPreferenceChanged(prefs, PREF_BACKGROUND_CLEANUP);
        onSharedPreferenceChanged(prefs, PREF_CONTRAST_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_DARKNESS_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_SHARPNESS_ADJUSTMENT);
        onSharedPreferenceChanged(prefs, PREF_COLLATE_MODE);
        onSharedPreferenceChanged(prefs, PREF_PAPER_SOURCE);
        onSharedPreferenceChanged(prefs, PREF_PAPER_TYPE);
        onSharedPreferenceChanged(prefs, PREF_SCALE_MODE);
        onSharedPreferenceChanged(prefs, PREF_SCALE_PERCENT);
        onSharedPreferenceChanged(prefs, PREF_TEXT_GRAPHICS_OPTIMIZATION);
        onSharedPreferenceChanged(prefs, PREF_NUMBER_UP_MODE);
        onSharedPreferenceChanged(prefs, PREF_NUMBER_UP_DIRECTION);
        onSharedPreferenceChanged(prefs, PREF_JOB_ASSEMBLY_MODE);
        onSharedPreferenceChanged(prefs, PREF_JOB_EXECUTION_MODE);
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_BIN);
        onSharedPreferenceChanged(prefs, PREF_PROGRESS_DIALOG_MODE);
        onSharedPreferenceChanged(prefs, PREF_ERASE_MARGIN_UNIT);
        onSharedPreferenceChanged(prefs, PREF_ERASE_BACK_MARGIN);
        onSharedPreferenceChanged(prefs, PREF_ERASE_FRONT_MARGIN);
        onSharedPreferenceChanged(prefs, PREF_CAPTURE_MODE);
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_REDUCE_TO_FIT);
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_UNITS);
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_FRONT);
        onSharedPreferenceChanged(prefs, PREF_IMAGE_SHIFT_BACK);
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_BORDERS_EACH_PAGE);
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_FINISHING_OPTION);
        onSharedPreferenceChanged(prefs, PREF_BOOKLET_FORMAT);
        onSharedPreferenceChanged(prefs, PREF_STAPLE_MODE);
        onSharedPreferenceChanged(prefs, PREF_PUNCH_MODE);
        onSharedPreferenceChanged(prefs, PREF_FOLD_MODE);
        CopyAttributes.StampPosition[] stampPositionsList = CopyAttributes.StampPosition.values();

        for(int i = 0; i <  stampPositionsList.length; ++i) {
            CopyAttributes.StampPosition stampPosition = stampPositionsList[i];
            this.onSharedPreferenceChanged(prefs, "pref_stamp" + stampPosition.name());
        }

        onSharedPreferenceChanged(prefs, PREF_WATERMARK_DARKNESS);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_ROTATION);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TYPE);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_MESSAGE_TYPE);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_BACKGROUND_PATTERN);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT_SIZE);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TRANSPARENCY);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_BACKGROUND_COLOR);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_FONT);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_TEXT_COLOR);
        onSharedPreferenceChanged(prefs, PREF_WATERMARK_ONLY_FIRST_PAGE);

        onSharedPreferenceChanged(prefs, PREF_MONITOR_JOB);
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS);
        onSharedPreferenceChanged(prefs, PREF_SETTINGS_UI);
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof MarginsPreference) {
            DialogFragment dialogFragment = MarginsPreferenceFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 1);
            dialogFragment.show(getParentFragmentManager(), "DIALOG");
        } else if (preference instanceof ShiftPreference) {
            DialogFragment dialogFragment = ShiftPreferenceFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
        } else if (preference instanceof StampOptionDialogPreference) {
            DialogFragment dialogFragment = StampOptionPreferenceFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Preference preference = findPreference(key);
        if (preference != null) {
            if (PREF_NUMBER_UP_MODE.equals(key)) {
                fillNumberUpDirectionAttrCaps(sharedPreferences);
            } else if (PREF_SCAN_SOURCE.equals(key)) {
                fillScalePercentRange(sharedPreferences);
            } else if (PREF_SCAN_SIZE.equals(key)) {
                showOriginalCustomSizePreference(sharedPreferences, key);
            } else if (PREF_PRINT_SIZE.equals(key)) {
                showOutputCustomSizePreference(sharedPreferences, key);
            } else if (PREF_SCALE_MODE.equals(key)) {
                showScalePercentPreference(sharedPreferences, key);
                onSharedPreferenceChanged(sharedPreferences, PREF_SCALE_PERCENT);
            } else if (PREF_STORE_JOB_PASSWORD_TYPE.equals(key)) {
                initiateStorePassword(sharedPreferences, key);
                onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_PASSWORD);
            } else if (PREF_JOB_EXECUTION_MODE.equals(key)) {
                final String entryStr = (String) ((ListPreference) preference).getEntry();
                final CopyAttributes.JobExecutionMode destination =
                        entryStr == null ? JobExecutionMode.NORMAL : CopyAttributes.JobExecutionMode.valueOf(entryStr);

                switch (destination) {
                    case STORE:
                        if (getPreferenceScreen().findPreference(PREF_DESTINATION_STORE_CATEGORY) == null) {
                            getPreferenceScreen().addPreference(mStoreCategory);
                        }

                        mJobNamePref = (EditTextPreference) findPreference(PREF_STORE_JOB_NAME);
                        mJobNamePref.setText(null);
                        mJobFolderPref = (EditTextPreference) findPreference(PREF_STORE_JOB_FOLDER_NAME);
                        mJobFolderPref.setText(null);
                        mJobPasswordPref = (EditTextPreference) findPreference(PREF_STORE_JOB_PASSWORD);
                        mJobPasswordPref.setText(null);

                        loadPrefFromCaps(PREF_STORE_DELETE_ON_POWER, Arrays.asList(RetentionMode.values()), RetentionMode.DEFAULT.name());
                        loadPrefFromCaps(PREF_STORE_DELETE_ON_RELEASE, Arrays.asList(RetentionMode.values()), RetentionMode.DEFAULT.name());
                        loadPrefFromCaps(PREF_STORE_JOB_PASSWORD_TYPE, mCaps.getPasswordTypeList(), JobCredentialsAttributes.PasswordType.NONE.name());

                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_NAME);
                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_FOLDER_NAME);
                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_DELETE_ON_POWER);
                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_DELETE_ON_RELEASE);
                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_PASSWORD_TYPE);
                        onSharedPreferenceChanged(sharedPreferences, PREF_STORE_JOB_PASSWORD);
                        break;

                    case NORMAL:
                    default:
                        getPreferenceScreen().removePreference(mStoreCategory);
                        break;
                }
            }

            if (preference instanceof ListPreference) {
                final String entry = (String) ((ListPreference) preference).getEntry();

                if (entry == null || entry.length() == 0) {
                    ((ListPreference) preference).setValueIndex(0);
                    preference.setSummary("%s");
                } else {
                    if (preference.getKey().equals(PREF_BOOKLET_FORMAT)){
                        switch (((ListPreference) preference).getValue()) {
                           case OPTION_DEFAULT:
                            case OPTION_OFF: {
                                ShowhideBookletSubView(false);
                                break;
                            }
                            case OPTION_LEFTEDGE:{
                                ShowhideBookletSubView(true);
                                break;
                            }
                        }
                    }
                    if (preference.getKey().equals(PREF_WATERMARK_TYPE)){
                        mWatermarktype=((ListPreference) preference).getValue();
                        WatermarkSubView(((ListPreference) preference).getValue());
                    }
                    if (preference.getKey().equals(PREF_WATERMARK_MESSAGE_TYPE)){
                        if(((ListPreference) preference).getValue().equals("CUSTOM") && isWatermarkTypeValid(mWatermarktype)){
                            waterMarkTextshow(true);
                        }else{
                            waterMarkTextshow(false);
                        }
                    }
                    preference.setSummary(entry);
                }
            } else if (preference instanceof EditTextPreference) {
                String text = ((EditTextPreference) preference).getText();
                if (PREF_STORE_JOB_PASSWORD.equals(key)) {
                    text = getTransformationString(text);
                }
                preference.setSummary(text);
            } else if (preference instanceof CheckBoxPreference) {
                if (PREF_MONITOR_JOB.equals(key)) {
                    findPreference(PREF_SHOW_JOB_PROGRESS)
                            .setEnabled(((CheckBoxPreference) preference).isChecked());
                }
            }
        }
    }

    private Boolean isWatermarkTypeValid(String mWatermarktype) {
         switch (mWatermarktype){
             case "DEFAULT":
             case "NONE": {
                 return false;
             }
             default: {
                 return true;
             }
        }
    }

    private void ShowhideBookletSubView(Boolean isVisible) {
        ListPreference mBookletBorderEachPageView =
                findPreference(PREF_BOOKLET_BORDERS_EACH_PAGE);
        ListPreference mBookletFinishingOptionView =
                findPreference(PREF_BOOKLET_FINISHING_OPTION);

        if (isVisible) {
            mBookletBorderEachPageView.setVisible(true);
            mBookletFinishingOptionView.setVisible(true);
        } else {
            mBookletBorderEachPageView.setVisible(false);
            mBookletFinishingOptionView.setVisible(false);
        }
    }

    private void WatermarkSubView(String watermarkType){
        ListPreference mWatermarkTextColorView = findPreference(PREF_WATERMARK_TEXT_COLOR);
        ListPreference mWatermarkMessageTypeView = findPreference(PREF_WATERMARK_MESSAGE_TYPE);
        EditTextPreference mWatermarkTextView = findPreference(PREF_WATERMARK_TEXT);
        ListPreference mWatermarkFontView = findPreference(PREF_WATERMARK_FONT);
        ListPreference mWatermarkTextSizeView = findPreference(PREF_WATERMARK_TEXT_SIZE);
        ListPreference mWatermarkOnlyFirstPageView = findPreference(PREF_WATERMARK_ONLY_FIRST_PAGE);
        EditTextPreference mWatermarkTransparencyView = findPreference(PREF_WATERMARK_TRANSPARENCY);
        EditTextPreference mWatermarkDarknessView = findPreference(PREF_WATERMARK_DARKNESS);
        ListPreference mWatermarkBackGroundColorView = findPreference(PREF_WATERMARK_BACKGROUND_COLOR);
        ListPreference mWatermarkBackGroundPatternView = findPreference(PREF_WATERMARK_BACKGROUND_PATTERN);
        ListPreference mWatermarkRotationView = findPreference(PREF_WATERMARK_ROTATION);
        switch (watermarkType){
            case "DEFAULT":
            case "NONE":{
                mWatermarkTextColorView.setVisible(false);
                mWatermarkMessageTypeView.setVisible(false);
                mWatermarkTextView.setVisible(false);
                mWatermarkFontView.setVisible(false);
                mWatermarkTextSizeView.setVisible(false);
                mWatermarkOnlyFirstPageView.setVisible(false);
                mWatermarkTransparencyView.setVisible(false);
                mWatermarkDarknessView.setVisible(false);
                mWatermarkBackGroundColorView.setVisible(false);
                mWatermarkBackGroundPatternView.setVisible(false);
                mWatermarkRotationView.setVisible(false);
                break;
            }
            case "TEXT": {
                mWatermarkTextColorView.setVisible(true);
                mWatermarkMessageTypeView.setVisible(true);
                mWatermarkFontView.setVisible(true);
                mWatermarkTextSizeView.setVisible(true);
                mWatermarkOnlyFirstPageView.setVisible(true);
                mWatermarkTransparencyView.setVisible(true);
                mWatermarkDarknessView.setVisible(true);
                mWatermarkBackGroundColorView.setVisible(false);
                mWatermarkBackGroundPatternView.setVisible(false);
                mWatermarkRotationView.setVisible(false);
                mWatermarkTextView.setVisible(mWatermarkMessageTypeView.getValue().equals(CopyAttributes.WatermarkMessageType.CUSTOM.name()) && isWatermarkTypeValid(watermarkType));
                break;
            }
            case "SECURE": {
                mWatermarkTextColorView.setVisible(false);
                mWatermarkMessageTypeView.setVisible(true);
                mWatermarkFontView.setVisible(true);
                mWatermarkTextSizeView.setVisible(true);
                mWatermarkOnlyFirstPageView.setVisible(true);
                mWatermarkTransparencyView.setVisible(true);
                mWatermarkDarknessView.setVisible(true);
                mWatermarkBackGroundColorView.setVisible(true);
                mWatermarkBackGroundPatternView.setVisible(true);
                mWatermarkRotationView.setVisible(true);
                mWatermarkTextView.setVisible(mWatermarkMessageTypeView.getValue().equals(CopyAttributes.WatermarkMessageType.CUSTOM.name()) && isWatermarkTypeValid(watermarkType));
                break;
            }
        }
    }

    private void waterMarkTextshow(Boolean messageType){
        EditTextPreference mWatermarkTextView = findPreference(PREF_WATERMARK_TEXT);
        mWatermarkTextView.setVisible(messageType);
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
     * @param caps {@link com.hp.workpath.api.copier.CopyAttributesCaps}
     */

    public void loadCapabilities(final CopyAttributesCaps caps) {
        mCaps = caps;

        loadPrefFromCaps(PREF_COLOR_MODE, caps.getColorModeList(), ColorMode.DEFAULT.name());
        loadPrefFromCaps(PREF_ORIENTATION, caps.getOrientationList(), Orientation.DEFAULT.name());
        loadPrefFromCaps(PREF_SCAN_DUPLEX_MODE, caps.getScanDuplexList(), Duplex.DEFAULT.name());
        loadPrefFromCaps(PREF_PRINT_DUPLEX_MODE, caps.getPrintDuplexList(), Duplex.DEFAULT.name());
        loadPrefFromCaps(PREF_COPY_PREVIEW, caps.getCopyPreviewList(), CopyPreview.DEFAULT.name());
        loadPrefFromCaps(PREF_SCAN_SOURCE, caps.getScanSourceList(), ScanSource.AUTO.name());
        loadPrefFromCaps(PREF_BACKGROUND_CLEANUP, caps.getBackgroundCleanupList(), BackgroundCleanup.DEFAULT.name());
        loadPrefFromCaps(PREF_CONTRAST_ADJUSTMENT, caps.getContrastAdjustmentList(), ContrastAdjustment.DEFAULT.name());
        loadPrefFromCaps(PREF_DARKNESS_ADJUSTMENT, caps.getDarknessAdjustmentList(), DarknessAdjustment.DEFAULT.name());
        loadPrefFromCaps(PREF_SHARPNESS_ADJUSTMENT, caps.getSharpnessAdjustmentList(), SharpnessAdjustment.DEFAULT.name());
        loadPrefFromCaps(PREF_COLLATE_MODE, caps.getCollateModeList(), CollateMode.DEFAULT.name());
        loadPrefFromCaps(PREF_PAPER_SOURCE, caps.getPaperSourceList(), PaperSource.DEFAULT.name());
        loadPrefFromCaps(PREF_PAPER_TYPE, caps.getPaperTypeList(), PaperType.DEFAULT.name());
        loadPrefFromCaps(PREF_SCALE_MODE, caps.getScaleModeList(), ScaleMode.DEFAULT.name());
        loadPrefFromCaps(PREF_TEXT_GRAPHICS_OPTIMIZATION, caps.getTextGraphicsOptimizationList(), PaperSource.DEFAULT.name());
        loadPrefFromCaps(PREF_NUMBER_UP_MODE, caps.getNumberUpModeList(), NumberUpMode.DEFAULT.name());
        loadPrefFromCaps(PREF_JOB_ASSEMBLY_MODE, caps.getJobAssemblyModeList(), JobAssemblyMode.DEFAULT.name());
        loadPrefFromCaps(PREF_JOB_EXECUTION_MODE, caps.getJobExecutionModeList(), JobExecutionMode.NORMAL.name());
        loadPrefFromCaps(PREF_OUTPUT_BIN, caps.getOutputBinList(), CopyAttributes.OutputBin.DEFAULT.name());
        loadPrefFromCaps(PREF_PROGRESS_DIALOG_MODE, caps.getProgressDialogModeList(), CopyAttributes.ProgressDialogMode.DEFAULT.name());
        loadPrefFromCaps(PREF_ERASE_MARGIN_UNIT,caps.getEraseMarginUnitList(), CopyAttributes.EraseMarginUnit.DEFAULT.name());
        loadPrefFromCaps(PREF_CAPTURE_MODE,caps.getCaptureModeList(),CopyAttributes.CaptureMode.DEFAULT.name());
        loadPrefFromCaps(PREF_IMAGE_SHIFT_REDUCE_TO_FIT,caps.getImageShiftReduceToFitList(), CopyAttributes.ImageShiftReduceToFit.DEFAULT.name());
        loadPrefFromCaps(PREF_IMAGE_SHIFT_UNITS,caps.getImageShiftUnitsList(), CopyAttributes.ImageShiftUnits.DEFAULT.name());
        loadPrefFromCaps(PREF_BOOKLET_FORMAT,caps.getBookletFormatList(), CopyAttributes.BookletFormat.DEFAULT.name());
        loadPrefFromCaps(PREF_BOOKLET_BORDERS_EACH_PAGE,caps.getBookletBordersEachPageList(), CopyAttributes.BookletBordersEachPage.DEFAULT.name());
        loadPrefFromCaps(PREF_BOOKLET_FINISHING_OPTION,caps.getBookletFinishingOptionList(), CopyAttributes.BookletFinishingOption.DEFAULT.name());
        loadPrefFromCaps(PREF_STAPLE_MODE,caps.getStapleOptionList(), CopyAttributes.StapleOption.NONE.name());
        loadPrefFromCaps(PREF_PUNCH_MODE,caps.getPunchModeList(), CopyAttributes.PunchMode.NONE.name());
        loadPrefFromCaps(PREF_FOLD_MODE,caps.getFoldModeList(), CopyAttributes.FoldMode.NONE.name());

        loadPrefFromCaps(PREF_WATERMARK_ROTATION,caps.getWatermarkRotate45List(), CopyAttributes.WatermarkRotate45.DEFAULT.name());
        loadPrefFromCaps(PREF_WATERMARK_TYPE,caps.getWatermarkTypeList(), CopyAttributes.WatermarkType.DEFAULT.name());
        loadPrefFromCaps(PREF_WATERMARK_MESSAGE_TYPE,caps.getWatermarkMessageTypeList(), CopyAttributes.WatermarkMessageType.CONFIDENTIAL.name());
        loadPrefFromCaps(PREF_WATERMARK_BACKGROUND_PATTERN,caps.getWatermarkBackgroundPatternList(), CopyAttributes.WatermarkBackgroundPattern.DEFAULT.name());
        loadPrefFromCaps(PREF_WATERMARK_ONLY_FIRST_PAGE,caps.getWatermarkOnlyFirstPageList(), CopyAttributes.WatermarkOnlyFirstPage.DEFAULT.name());
        loadPrefFromCaps(PREF_WATERMARK_FONT,caps.getWatermarkFontList(), WATERMARK_FONT_DEFAULT);
        loadPrefFromCaps(PREF_WATERMARK_BACKGROUND_COLOR,caps.getWatermarkBackgroundColorList(), WATERMARK_BACKGROUND_COLOR_DEFAULT);
        loadPrefFromCaps(PREF_WATERMARK_TEXT_COLOR,caps.getWatermarkTextColorList(), WATERMARK_TEXT_COLOR_DEFAULT);
        loadPrefFromCaps(PREF_WATERMARK_TEXT_SIZE,caps.getWatermarkTextSizeList(), WATERMARK_TEXT_SIZE);

        mCopiesPref.setLimits(caps.getCopiesRange().getLowerBound(), caps.getCopiesRange().getUpperBound());
        mCopiesPref.setText("1");
        mWatermarkDarknessPref.setLimits(caps.getWatermarkDarknessRange().getLowerBound(), caps.getWatermarkDarknessRange().getUpperBound());
        mWatermarkDarknessPref.setText("1");
        mWatermarkTransparencyPref.setLimits(caps.getWatermarkTransparencyRange().getLowerBound(), caps.getWatermarkTransparencyRange().getUpperBound());
        mWatermarkTransparencyPref.setText("1");
        Range scalePercentRange = caps.getScalePercentRangeByScanSource().get(ScanSource.DEFAULT);
        mScalePercentPref.setLimits(scalePercentRange.getLowerBound(), scalePercentRange.getUpperBound());

        mScanCustomLengthPref.setLimits(caps.getScanCustomLengthRange().getLowerBound(), caps.getScanCustomLengthRange().getUpperBound());
        mScanCustomWidthPref.setLimits(caps.getScanCustomWidthRange().getLowerBound(), caps.getScanCustomWidthRange().getUpperBound());

        mPrintCustomLengthPref.setLimits(caps.getPrintCustomLengthRange().getLowerBound(), caps.getPrintCustomLengthRange().getUpperBound());
        mPrintCustomWidthPref.setLimits(caps.getPrintCustomWidthRange().getLowerBound(), caps.getPrintCustomWidthRange().getUpperBound());

        if (caps.getEraseBackBottomRange() != null) {
            if(mEraseBackMarginPref!=null){
                mEraseBackMarginPref.setBottomLimits(caps.getEraseBackBottomRange().getLowerBound(), caps.getEraseBackBottomRange().getUpperBound());
                mEraseBackMarginPref.setLeftLimits(caps.getEraseBackLeftRange().getLowerBound(), caps.getEraseBackLeftRange().getUpperBound());
                mEraseBackMarginPref.setRightLimits(caps.getEraseBackRightRange().getLowerBound(), caps.getEraseBackRightRange().getUpperBound());
                mEraseBackMarginPref.setTopLimits(caps.getEraseBackTopRange().getLowerBound(), caps.getEraseBackTopRange().getUpperBound());
            }
        }
        if (caps.getEraseFrontBottomRange() != null) {
            if(mEraseFrontMarginPref!=null) {
                mEraseFrontMarginPref.setBottomLimits(caps.getEraseFrontBottomRange().getLowerBound(), caps.getEraseFrontBottomRange().getUpperBound());
                mEraseFrontMarginPref.setLeftLimits(caps.getEraseFrontLeftRange().getLowerBound(), caps.getEraseFrontLeftRange().getUpperBound());
                mEraseFrontMarginPref.setRightLimits(caps.getEraseFrontRightRange().getLowerBound(), caps.getEraseFrontRightRange().getUpperBound());
                mEraseFrontMarginPref.setTopLimits(caps.getEraseFrontTopRange().getLowerBound(), caps.getEraseFrontTopRange().getUpperBound());
            }
        }
        mImageShiftFrontPref.setXShiftLimits(caps.getImageShiftXFrontRange().getLowerBound(), caps.getImageShiftXFrontRange().getUpperBound());
        mImageShiftFrontPref.setYShiftLimits(caps.getImageShiftYFrontRange().getLowerBound(), caps.getImageShiftYFrontRange().getUpperBound());
        mImageShiftBackPref.setXShiftLimits(caps.getImageShiftXBackRange().getLowerBound(), caps.getImageShiftXBackRange().getUpperBound());
        mImageShiftBackPref.setYShiftLimits(caps.getImageShiftYBackRange().getLowerBound(), caps.getImageShiftYBackRange().getUpperBound());

        loadStampOptionPrefFromCaps(caps);
        loadPrintSizePrefFromCaps(caps, PREF_PRINT_SIZE, CopyAttributes.PaperSize.DEFAULT.name());
        loadScanSizePrefFromCaps(caps, PREF_SCAN_SIZE, CopyAttributes.ScanSize.DEFAULT.name());
    }

    private void loadStampOptionPrefFromCaps(final CopyAttributesCaps caps){

        mStampPref.setmStampOptionMap(new HashMap<CopyAttributes.StampPosition, StampOption>());

        Map<CopyAttributes.StampPosition, List<StampType>> mStampTypeListMap = new HashMap<>();
        Map<CopyAttributes.StampPosition, List<StampPolicyType>> mStampPolicyTypeListMap = new HashMap<>();
        Map<CopyAttributes.StampPosition, List<String>> mStampFontListMap = new HashMap<>();
        Map<CopyAttributes.StampPosition, List<Integer>> mStampTextSizeListMap = new HashMap<>();
        Map<CopyAttributes.StampPosition, List<String>> mStampTextColorListMap = new HashMap<>();
        Map<CopyAttributes.StampPosition, List<Boolean>> mStampWhiteBackGroundListMap = new HashMap<>();

        mStampPref.setmStampPositionList(caps.getStampPositionList());
        for(CopyAttributes.StampPosition stampPosition : caps.getStampPositionList()){

            mStampTypeListMap.put(stampPosition, caps.getStampTypeList(stampPosition));
            mStampPolicyTypeListMap.put(stampPosition, caps.getStampPolicyTypeList(stampPosition));
            mStampFontListMap.put(stampPosition, caps.getStampFormatFontList(stampPosition));
            mStampTextSizeListMap.put(stampPosition, caps.getStampFormatTextSizeList(stampPosition));
            mStampTextColorListMap.put(stampPosition, caps.getStampFormatTextColorList(stampPosition));
            mStampWhiteBackGroundListMap.put(stampPosition, caps.getStampFormatWhiteBackgroundList(stampPosition));
        }
        mStampPref.setmStampTypeListMap(mStampTypeListMap);
        mStampPref.setmStampPolicyTypeListMap(mStampPolicyTypeListMap);
        mStampPref.setmStampFontListMap(mStampFontListMap);
        mStampPref.setmStampTextSizeListMap(mStampTextSizeListMap);
        mStampPref.setmStampTextColorListMap(mStampTextColorListMap);
        mStampPref.setmStampWhiteBackGroundListMap(mStampWhiteBackGroundListMap);
    }

    private void loadPrintSizePrefFromCaps(CopyAttributesCaps caps, String prefName, String defValue) {
        ListPreference pref = (ListPreference) findPreference(prefName);
        ArrayList<CharSequence> cmEntries = new ArrayList<>();
        ArrayList<CharSequence> cmEntryValues = new ArrayList<>();
        for (CopyAttributes.PaperSize os : caps.getPrintSizeList()) {
            CharSequence entrie = "";
            if (os.getWidth() == 0.0 && os.getHeight() == 0.0 && os.getUnit() == null) {
                entrie = os.name();
            } else {
                entrie = os.name()+" ("+os.getWidth()+" X "+os.getHeight()+" "+os.getUnit()+")";
            }
            cmEntries.add(entrie);
            cmEntryValues.add(os.name());
        }
        if (cmEntries.size() > 0) {
            pref.setEntries(cmEntries.toArray(new CharSequence[cmEntries.size()]));
            pref.setEntryValues(cmEntryValues.toArray(new CharSequence[cmEntryValues.size()]));
            pref.setDefaultValue(defValue);
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }
    }

    private void loadScanSizePrefFromCaps(CopyAttributesCaps caps, String prefName, String defValue) {
        ListPreference pref = (ListPreference) findPreference(prefName);
        ArrayList<CharSequence> cmEntries = new ArrayList<>();
        ArrayList<CharSequence> cmEntryValues = new ArrayList<>();
        for (CopyAttributes.ScanSize os : caps.getScanSizeList()) {
            CharSequence entrie = "";
            if (os.getWidth() == 0.0 && os.getHeight() == 0.0 && os.getUnit() == null) {
                entrie = os.name();
            } else {
                entrie = os.name()+" ("+os.getWidth()+" X "+os.getHeight()+" "+os.getUnit()+")";
            }
            cmEntries.add(entrie);
            cmEntryValues.add(os.name());
        }
        if (cmEntries.size() > 0) {
            pref.setEntries(cmEntries.toArray(new CharSequence[cmEntries.size()]));
            pref.setEntryValues(cmEntryValues.toArray(new CharSequence[cmEntryValues.size()]));
            pref.setDefaultValue(defValue);
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }
    }


    private void loadPrefFromCaps(String prefName, List caps, String defValue) {
        ListPreference pref = (ListPreference) findPreference(prefName);
        ArrayList<CharSequence> cmEntries = new ArrayList<>();
        ArrayList<CharSequence> cmEntryValues = new ArrayList<>();

        for (Object cm : caps) {
            if (cm instanceof Enum<?>) {
                Enum en = (Enum) cm;
                cmEntries.add(en.name());
                cmEntryValues.add(en.name());
            } else {
                cmEntries.add(cm.toString());
                cmEntryValues.add(cm.toString());
            }
        }

        if (cmEntries.size() > 0) {
            pref.setEntries(cmEntries.toArray(new CharSequence[cmEntries.size()]));
            pref.setEntryValues(cmEntryValues.toArray(new CharSequence[cmEntryValues.size()]));
            pref.setDefaultValue(defValue);
            pref.setValueIndex(0);
            pref.setSummary("%s");
        }
    }

    private void fillNumberUpDirectionAttrCaps(SharedPreferences sharedPreferences) {
        final NumberUpMode numberUpMode = NumberUpMode.valueOf(sharedPreferences.getString(PREF_NUMBER_UP_MODE, NumberUpMode.DEFAULT.name()));

        if (numberUpMode != NumberUpMode.DEFAULT) {
            mBaseAttributesCategory.addPreference(mNumberUpDirectionPref);

            if (mCaps != null) {
                loadPrefFromCaps(PREF_NUMBER_UP_DIRECTION, mCaps.getNumberUpDirectionByNumberUpCount().get(numberUpMode), NumberUpDirection.DEFAULT.name());
            }
        } else {
            mBaseAttributesCategory.removePreference(mNumberUpDirectionPref);
        }
    }

    private void fillScalePercentRange(SharedPreferences sharedPreferences) {
        final ScanSource scanSource = ScanSource.valueOf(sharedPreferences.getString(PREF_SCAN_SOURCE, ScanSource.DEFAULT.name()));

        if (mCaps != null) {
            Range scalePercentRange = mCaps.getScalePercentRangeByScanSource().get(scanSource);
            mScalePercentPref.setLimits(scalePercentRange.getLowerBound(), scalePercentRange.getUpperBound());
        } else {
            mScalePercentPref.setLimits(100, 100);
        }
    }

    private void showOriginalCustomSizePreference(SharedPreferences preferences, String key) {
        ScanSize scanSize = ScanSize.valueOf(preferences.getString(key, ScanSize.DEFAULT.name()));

        if (scanSize == ScanSize.CUSTOM) {
            mScanCustomLengthPref.setSummary(mScanCustomLengthPref.getText());
            mScanCustomWidthPref.setSummary(mScanCustomWidthPref.getText());
            mBaseAttributesCategory.addPreference(mScanCustomLengthPref);
            mBaseAttributesCategory.addPreference(mScanCustomWidthPref);
        } else {
            mBaseAttributesCategory.removePreference(mScanCustomLengthPref);
            mBaseAttributesCategory.removePreference(mScanCustomWidthPref);
        }
    }

    private void showOutputCustomSizePreference(SharedPreferences preferences, String key) {
        PaperSize printSize = PaperSize.valueOf(preferences.getString(key, PaperSize.DEFAULT.name()));

        if (printSize == PaperSize.CUSTOM) {
            mPrintCustomLengthPref.setSummary(mPrintCustomLengthPref.getText());
            mPrintCustomWidthPref.setSummary(mPrintCustomWidthPref.getText());
            mBaseAttributesCategory.addPreference(mPrintCustomLengthPref);
            mBaseAttributesCategory.addPreference(mPrintCustomWidthPref);
        } else {
            mBaseAttributesCategory.removePreference(mPrintCustomLengthPref);
            mBaseAttributesCategory.removePreference(mPrintCustomWidthPref);
        }
    }

    private void showScalePercentPreference(SharedPreferences preferences, String key) {
        ScaleMode scaleMode = ScaleMode.valueOf(preferences.getString(key, ScaleMode.DEFAULT.name()));

        if (scaleMode == ScaleMode.MANUAL) {
            mBaseAttributesCategory.addPreference(mScalePercentPref);
        } else {
            mBaseAttributesCategory.removePreference(mScalePercentPref);
        }
    }

    private void initiateStorePassword(SharedPreferences preferences, String key) {
        JobCredentialsAttributes.PasswordType type
                = JobCredentialsAttributes.PasswordType.valueOf(preferences.getString(key, JobCredentialsAttributes.PasswordType.NONE.name()));
        if (JobCredentialsAttributes.PasswordType.NONE.name().equals(type.name())) {
            clearJobPassword();
        }
    }

    public void clearJobPassword() {
        if (mJobPasswordPref != null) {
            mJobPasswordPref.setText(null);
            mJobPasswordPref.setSummary(null);
        }
    }

    public void setDefaultCopyAttributes(CopyAttributes copyAttributes) throws Exception {
        if (copyAttributes != null) {
            CopyAttributesReader copyAttributesReader = new CopyAttributesReader(copyAttributes);
            setPreferenceEntryValue(PREF_BACKGROUND_CLEANUP, copyAttributesReader.getBackgroundCleanup().name());
            setPreferenceEntryValue(PREF_COLLATE_MODE, copyAttributesReader.getCollateMode().name());
            setPreferenceEntryValue(PREF_COLOR_MODE, copyAttributesReader.getColorMode().name());
            setPreferenceEntryValue(PREF_CONTRAST_ADJUSTMENT, copyAttributesReader.getContrastAdjustment().name());
            setPreferenceEntryValue(PREF_TEXT_GRAPHICS_OPTIMIZATION, copyAttributesReader.getTextGraphicsOptimization().name());
            setPreferenceEntryValue(PREF_COPY_PREVIEW, copyAttributesReader.getCopyPreview().name());
            setPreferenceEntryValue(PREF_DARKNESS_ADJUSTMENT, copyAttributesReader.getDarknessAdjustment().name());
            setPreferenceEntryValue(PREF_JOB_ASSEMBLY_MODE, copyAttributesReader.getJobAssemblyMode().name());
            setPreferenceEntryValue(PREF_JOB_EXECUTION_MODE, copyAttributesReader.getJobExecutionMode().name());
            setPreferenceEntryValue(PREF_NUMBER_UP_MODE, copyAttributesReader.getNumberUpMode().name());
            setPreferenceEntryValue(PREF_NUMBER_UP_DIRECTION, copyAttributesReader.getNumberUpDirection().name());
            setPreferenceEntryValue(PREF_ORIENTATION, copyAttributesReader.getOrientation().name());
            setPreferenceEntryValue(PREF_PAPER_SOURCE, copyAttributesReader.getPaperSource().name());
            setPreferenceEntryValue(PREF_PAPER_TYPE, copyAttributesReader.getPaperType().name());
            setPreferenceEntryValue(PREF_PRINT_DUPLEX_MODE, copyAttributesReader.getPrintDuplex().name());
            setPreferenceEntryValue(PREF_PRINT_SIZE, copyAttributesReader.getPrintSize().name());
            setPreferenceEntryValue(PREF_SCALE_MODE, copyAttributesReader.getScaleMode().name());
            setPreferenceEntryValue(PREF_SCAN_DUPLEX_MODE, copyAttributesReader.getScanDuplex().name());
            setPreferenceEntryValue(PREF_SCAN_SIZE, copyAttributesReader.getScanSize().name());
            setPreferenceEntryValue(PREF_SCAN_SOURCE, copyAttributesReader.getScanSource().name());
            setPreferenceEntryValue(PREF_SHARPNESS_ADJUSTMENT, copyAttributesReader.getSharpnessAdjustment().name());
            setPreferenceEntryValue(PREF_OUTPUT_BIN, copyAttributesReader.getOutputBin().name());
            setPreferenceEntryValue(PREF_PROGRESS_DIALOG_MODE, copyAttributesReader.getProgressDialogMode().name());
            if (copyAttributesReader.getEraseMarginUnit() != null) {
                setPreferenceEntryValue(PREF_ERASE_MARGIN_UNIT, copyAttributesReader.getEraseMarginUnit().name());
            }
            Margins backMargin = new Margins(copyAttributesReader.getEraseBackLeftMargin(),
                    copyAttributesReader.getEraseBackTopMargin(),
                    copyAttributesReader.getEraseBackRightMargin(),
                    copyAttributesReader.getEraseBackBottomMargin());

            Margins frontMargin = new Margins(copyAttributesReader.getEraseFrontLeftMargin(),
                    copyAttributesReader.getEraseFrontTopMargin(),
                    copyAttributesReader.getEraseFrontRightMargin(),
                    copyAttributesReader.getEraseFrontBottomMargin());

            setMarginPreferenceValue(PREF_ERASE_BACK_MARGIN, backMargin);
            setMarginPreferenceValue(PREF_ERASE_FRONT_MARGIN, frontMargin);

            Shifts imageShiftFront = new Shifts(copyAttributesReader.getImageShiftXFront(),
                    copyAttributesReader.getImageShiftYFront());

            Shifts imageShiftBack = new Shifts(copyAttributesReader.getImageShiftXBack(),
                    copyAttributesReader.getImageShiftYBack());

            setImageShitPreferenceValue(PREF_IMAGE_SHIFT_FRONT,imageShiftFront);
            setImageShitPreferenceValue(PREF_IMAGE_SHIFT_BACK,imageShiftBack);



            setPreferenceEntryValue(PREF_CAPTURE_MODE,copyAttributesReader.getCaptureMode().name());
            setPreferenceEntryValue(PREF_IMAGE_SHIFT_REDUCE_TO_FIT, copyAttributesReader.getImageShiftReduceToFit().name());
            setPreferenceEntryValue(PREF_IMAGE_SHIFT_UNITS, copyAttributesReader.getImageShiftUnits().name());
            setPreferenceEntryValue(PREF_BOOKLET_BORDERS_EACH_PAGE, copyAttributesReader.getBookletBordersEachPage().name());
            setPreferenceEntryValue(PREF_BOOKLET_FINISHING_OPTION, copyAttributesReader.getBookletFinishingOption().name());
            setPreferenceEntryValue(PREF_BOOKLET_FORMAT, copyAttributesReader.getBookletFormat().name());
            setPreferenceEntryValue(PREF_FOLD_MODE, copyAttributesReader.getFoldMode().name());
            setPreferenceEntryValue(PREF_STAPLE_MODE, copyAttributesReader.getStapleOption().name());
            setPreferenceEntryValue(PREF_PUNCH_MODE, copyAttributesReader.getPunchMode().name());
            setPreferenceValue(PREF_COPIES, Integer.toString(copyAttributesReader.getCopies()));
            setPreferenceValue(PREF_SCALE_PERCENT, Integer.toString(copyAttributesReader.getScalePercent()));
            setPreferenceEntryValue(PREF_WATERMARK_ROTATION, copyAttributesReader.getWatermarkRotation45().name());
            setPreferenceEntryValue(PREF_WATERMARK_ONLY_FIRST_PAGE, copyAttributesReader.getWatermarkOnlyFirstPage().name());
            setPreferenceEntryValue(PREF_WATERMARK_TYPE, copyAttributesReader.getWatermarkType().name());
            setPreferenceEntryValue(PREF_WATERMARK_BACKGROUND_PATTERN, copyAttributesReader.getWatermarkBackgroundPattern().name());
            setPreferenceEntryValue(PREF_WATERMARK_MESSAGE_TYPE, copyAttributesReader.getWatermarkMessageType().name());
            setPreferenceValue(PREF_WATERMARK_DARKNESS, String.valueOf(copyAttributesReader.getWatermarkDarkness()));
            setPreferenceEntryValue(PREF_WATERMARK_TEXT_SIZE, String.valueOf(copyAttributesReader.getWatermarkTextSize()));
            setPreferenceValue(PREF_WATERMARK_TRANSPARENCY, String.valueOf(copyAttributesReader.getWatermarkTransparency()));

            setPreferenceEntryValue(PREF_WATERMARK_FONT, WATERMARK_FONT_DEFAULT);
            setPreferenceEntryValue(PREF_WATERMARK_BACKGROUND_COLOR, WATERMARK_BACKGROUND_COLOR_DEFAULT);
            setPreferenceEntryValue(PREF_WATERMARK_TEXT_COLOR, PREF_WATERMARK_TEXT_COLOR);
            setPreferenceValue(PREF_WATERMARK_TEXT, copyAttributesReader.getWatermarkText());
            if (copyAttributesReader.getWatermarkFont() != null) {
                setPreferenceEntryValue(PREF_WATERMARK_FONT, copyAttributesReader.getWatermarkFont());
            } else {
                setPreferenceEntryValue(PREF_WATERMARK_FONT, WATERMARK_FONT_DEFAULT);
            }
            if (copyAttributesReader.getWatermarkBackgroundColor() != null) {
                setPreferenceEntryValue(PREF_WATERMARK_BACKGROUND_COLOR, copyAttributesReader.getWatermarkBackgroundColor());
            } else {
                setPreferenceEntryValue(PREF_WATERMARK_BACKGROUND_COLOR, WATERMARK_BACKGROUND_COLOR_DEFAULT);
            }
            if (copyAttributesReader.getWatermarkTextColor() != null) {
                setPreferenceEntryValue(PREF_WATERMARK_TEXT_COLOR, copyAttributesReader.getWatermarkTextColor());
            } else {
                setPreferenceEntryValue(PREF_WATERMARK_TEXT_COLOR, PREF_WATERMARK_TEXT_COLOR);
            }

            Map<CopyAttributes.StampPosition,StampOption> stampOptionMap = copyAttributesReader.getStampOption();
            StampOptionDialogPreference stampOptionDialogPreference = findPreference(PREF_STAMP);
            stampOptionDialogPreference.setmStampOptionMap(stampOptionMap);

            setPreferenceValue(PREF_CUSTOM_LENGTH,String.valueOf(copyAttributesReader.getPrintCustomLength()));
            setPreferenceValue(PREF_CUSTOM_WIDTH,String.valueOf(copyAttributesReader.getPrintCustomWidth()));
        }
    }

    private void setImageShitPreferenceValue(String prefImageShiftFront, Shifts imageShift) throws Exception {
        ShiftPreference imageShiftPreference = (ShiftPreference)findPreference(prefImageShiftFront);
        if (imageShiftPreference != null) {
            imageShiftPreference.setShifts(imageShift.getXShift(),
                    imageShift.getYShift());
            imageShiftPreference.applyShifts();
            imageShiftPreference.setSummary(requireContext().getString(R.string.summary_shift, imageShift.getXShift(), imageShift.getYShift()));
        }
    }

    private void setMarginPreferenceValue(String prefCopies, Margins backMargin ) throws Exception {
        MarginsPreference eraseMarginPreference = (MarginsPreference)findPreference(prefCopies);
        if (eraseMarginPreference != null) {
            eraseMarginPreference.setMargins(backMargin.getLeftMargin(),
                    backMargin.getTopMargin(),
                    backMargin.getRightMargin(),
                    backMargin.getBottomMargin());
            eraseMarginPreference.applyMargins();
            eraseMarginPreference.setSummary(requireContext().getString(R.string.summary_margin, backMargin.getLeftMargin(), backMargin.getTopMargin(), backMargin.getRightMargin(), backMargin.getBottomMargin()));
        }

    }

    private void setPreferenceValue(String pref, String value) {
        EditTextPreference editTextPreference = (EditTextPreference) findPreference(pref);
        if (editTextPreference != null) {
            editTextPreference.setText(value);
            editTextPreference.setSummary(value);
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
}