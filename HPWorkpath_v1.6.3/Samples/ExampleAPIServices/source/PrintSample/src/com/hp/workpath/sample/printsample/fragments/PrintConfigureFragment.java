// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.api.massstorage.MassStorageService;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributes.AutoFit;
import com.hp.workpath.api.printer.PrintAttributes.ColorMode;
import com.hp.workpath.api.printer.PrintAttributes.DocumentFormat;
import com.hp.workpath.api.printer.PrintAttributes.Duplex;
import com.hp.workpath.api.printer.PrintAttributes.Finishings;
import com.hp.workpath.api.printer.PrintAttributes.PaperSize;
import com.hp.workpath.api.printer.PrintAttributes.PaperSource;
import com.hp.workpath.api.printer.PrintAttributes.PaperType;
import com.hp.workpath.api.printer.PrintAttributes.Source;
import com.hp.workpath.api.printer.PrintAttributes.StapleMode;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrintAttributesReader;
import com.hp.workpath.sample.printsample.FileBrowserActivity;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.R;
import com.hp.workpath.sample.printsample.filebrowser.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple {@link PreferenceFragmentCompat} to set Print Attributes and
 * save into preferences.
 */
public final class PrintConfigureFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    // Preferences keys for PrintAttributes
    public static final String PREF_COPIES = "pref_copies";
    public static final String PREF_COLOR_MODE = "pref_colorMode";
    public static final String PREF_DUPLEX_MODE = "pref_duplexMode";
    public static final String PREF_FILENAME = "pref_filename";
    public static final String PREF_USB_STORAGE = "pref_usb_storage";
    public static final String PREF_USB_FILENAME = "pref_usb_filename";
    public static final String PREF_STREAM_FILENAME = "pref_stream_filename";
    public static final String PREF_AUTOFIT = "pref_autoFit";
    public static final String PREF_STAPLE_MODE = "pref_stapleMode";
    public static final String PREF_COLLATE_MODE = "pref_collateMode";
    public static final String PREF_PAPER_SOURCE = "pref_paperSource";
    public static final String PREF_PAPER_SIZE = "pref_paperSize";
    public static final String PREF_PAPER_TYPE = "pref_paperType";
    public static final String PREF_DOC_FORMAT = "pref_documentFormat";
    public static final String PREF_SOURCE = "pref_source";
    public static final String PREF_URI = "pref_uri";
    public static final String PREF_URI_USERNAME = "pref_uri_username";
    public static final String PREF_URI_PASSWORD = "pref_uri_password";

    public static final String PREF_ORIENTATION = "pref_orientation";
    public static final String PREF_PRINT_QUALITY = "pref_print_quality";
    public static final String PREF_OUTPUT_BIN = "pref_output_bin";
    public static final String PREF_START_PAGE_RANGES = "pref_start_pageRanges";
    public static final String PREF_END_PAGE_RANGES = "pref_end_pageRanges";
    public static final String PREF_FINISHINGS = "pref_finishings";

    public static final String PREF_JOB_NAME = "pref_jobName";
    public static final String PREF_COPY_TEST_PAGE = "pref_copy_test_page";
    public static final String PREF_STREAM_COPY_TEST_PAGE = "pref_stream_copy_test_page";

    public static final String PREF_SOURCE_STORAGE_CATEGORY = "source_storage_category";
    public static final String PREF_SOURCE_HTTP_CATEGORY = "source_http_category";
    public static final String PREF_SOURCE_USB_CATEGORY = "source_usb_category";
    public static final String PREF_SOURCE_STREAM_CATEGORY = "source_stream_category";

    // Feedback settings
    public static final String PREF_MONITORING_JOB = "pref_monitoringJob";
    public static final String PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress";
    public static final String PREF_SHOW_SETTINGS = "pref_showSettings";

    // Preference for current job id
    public static final String CURRENT_JOB_ID = "pref_currentJobId";

    public static final int FILE_BROWSER_REQUEST_CODE = 1;

    private Preference mFilenamePref;
    private EditTextPreference mFileUriPref;
    private EditTextPreference mFileUriUsernamePref;
    private EditTextPreference mFileUriPasswordPref;
    private EditTextPreference mUsbFilenamePref;
    private Preference mStreamFilenamePref;
    private ListPreference mDuplexPref;
    private ListPreference mCMPref;
    private ListPreference mAFPref;
    private ListPreference mSMPref;
    private ListPreference mCollatePref;
    private ListPreference mPaperSrcPref;
    private ListPreference mPaperSzPref;
    private ListPreference mPaperTypePref;
    private ListPreference mDocFmtPref;
    private ListPreference mSourcePref;
    private ListPreference mOrientationPref;
    private ListPreference mPrintQualityPref;
    private ListPreference mOutputBinPref;
    private EditTextIntegerPreference mCopiesPref;
    private EditTextPreference mJobNamePref;
    private EditTextPreference mStartPageRangesPref;
    private EditTextPreference mEndPageRangesPref;
    private MultiSelectListPreference mFinishingsPref;
    private Preference mCopyTestPage;
    private Preference mStreamCopyTestPage;

    private PreferenceCategory mSourceCategory;
    private PreferenceCategory mStorageCategory;
    private PreferenceCategory mHttpCategory;
    private PreferenceCategory mUsbCategory;
    private PreferenceCategory mStreamCategory;

    private CheckBoxPreference mMonitoringJobPref;
    private CheckBoxPreference mShowJobProgressPref;
    private CheckBoxPreference mShowSettingsPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.print_preferences);

        mDuplexPref = (ListPreference) findPreference(PREF_DUPLEX_MODE);
        mCMPref = (ListPreference) findPreference(PREF_COLOR_MODE);
        mAFPref = (ListPreference) findPreference(PREF_AUTOFIT);
        mSMPref = (ListPreference) findPreference(PREF_STAPLE_MODE);
        mCollatePref = (ListPreference) findPreference(PREF_COLLATE_MODE);
        mPaperSrcPref = (ListPreference) findPreference(PREF_PAPER_SOURCE);
        mPaperSzPref = (ListPreference) findPreference(PREF_PAPER_SIZE);
        mPaperTypePref = (ListPreference) findPreference(PREF_PAPER_TYPE);
        mDocFmtPref = (ListPreference) findPreference(PREF_DOC_FORMAT);
        mSourcePref = (ListPreference) findPreference(PREF_SOURCE);
        mFilenamePref = findPreference(PREF_FILENAME);
        mFileUriPref = (EditTextPreference) findPreference(PREF_URI);
        mFileUriPref.setText(null);
        mFileUriUsernamePref = (EditTextPreference) findPreference(PREF_URI_USERNAME);
        mFileUriUsernamePref.setText(null);
        mFileUriPasswordPref = (EditTextPreference) findPreference(PREF_URI_PASSWORD);
        mFileUriPasswordPref.setText(null);
        mUsbFilenamePref = (EditTextPreference) findPreference(PREF_USB_FILENAME);
        mUsbFilenamePref.setText(null);
        mStreamFilenamePref = findPreference(PREF_STREAM_FILENAME);

        mOrientationPref = (ListPreference) findPreference(PREF_ORIENTATION);
        mPrintQualityPref = (ListPreference) findPreference(PREF_PRINT_QUALITY);
        mOutputBinPref = (ListPreference) findPreference(PREF_OUTPUT_BIN);

        // Set default limits to single, default, value
        mCopiesPref = ((EditTextIntegerPreference) findPreference(PREF_COPIES));
        mCopiesPref.setLimits(1, 1);

        mJobNamePref = (EditTextPreference) findPreference(PREF_JOB_NAME);
        mJobNamePref.setText(null);

        mStartPageRangesPref = (EditTextPreference)findPreference(PREF_START_PAGE_RANGES);
        mStartPageRangesPref.setText(null);
        mEndPageRangesPref = (EditTextPreference) findPreference(PREF_END_PAGE_RANGES);
        mEndPageRangesPref.setText(null);

        mFinishingsPref = (MultiSelectListPreference) findPreference(PREF_FINISHINGS);

        mStorageCategory = (PreferenceCategory) findPreference(PREF_SOURCE_STORAGE_CATEGORY);
        mHttpCategory = (PreferenceCategory) findPreference(PREF_SOURCE_HTTP_CATEGORY);
        mUsbCategory = (PreferenceCategory) findPreference(PREF_SOURCE_USB_CATEGORY);
        mStreamCategory = (PreferenceCategory) findPreference(PREF_SOURCE_STREAM_CATEGORY);

        mMonitoringJobPref = (CheckBoxPreference) findPreference(PREF_MONITORING_JOB);
        mShowJobProgressPref = (CheckBoxPreference) findPreference(PREF_SHOW_JOB_PROGRESS);
        mShowSettingsPref = (CheckBoxPreference) findPreference(PREF_SHOW_SETTINGS);

        mMonitoringJobPref.setChecked(true);
        mShowJobProgressPref.setChecked(true);
        mShowSettingsPref.setChecked(false);

        setDefaultFilePreference();

        mCopyTestPage = findPreference(PREF_COPY_TEST_PAGE);
        mStreamCopyTestPage = findPreference(PREF_STREAM_COPY_TEST_PAGE);
        mCopyTestPage.setOnPreferenceClickListener(copyTestPageListener);
        mStreamCopyTestPage.setOnPreferenceClickListener(copyTestPageListener);
        mFilenamePref.setOnPreferenceClickListener(startFileBrowserListener);
        mStreamFilenamePref.setOnPreferenceClickListener(startFileBrowserListener);
    }

    private void setDefaultFilePreference() {
        String internalDefaultPath = getContext().getFilesDir().getPath();
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.edit().putString(PREF_FILENAME, internalDefaultPath).apply();
        prefs.edit().putString(PREF_STREAM_FILENAME, internalDefaultPath).apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        refreshAllPrefs(prefs);
    }

    private void refreshAllPrefs(final SharedPreferences prefs) {
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE);
        onSharedPreferenceChanged(prefs, PREF_DUPLEX_MODE);
        onSharedPreferenceChanged(prefs, PREF_COPIES);
        onSharedPreferenceChanged(prefs, PREF_FILENAME);
        onSharedPreferenceChanged(prefs, PREF_AUTOFIT);
        onSharedPreferenceChanged(prefs, PREF_STAPLE_MODE);
        onSharedPreferenceChanged(prefs, PREF_COLLATE_MODE);
        onSharedPreferenceChanged(prefs, PREF_PAPER_SOURCE);
        onSharedPreferenceChanged(prefs, PREF_PAPER_SIZE);
        onSharedPreferenceChanged(prefs, PREF_PAPER_TYPE);
        onSharedPreferenceChanged(prefs, PREF_DOC_FORMAT);
        onSharedPreferenceChanged(prefs, PREF_SHOW_SETTINGS);
        onSharedPreferenceChanged(prefs, PREF_MONITORING_JOB);
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS);
        onSharedPreferenceChanged(prefs, PREF_SOURCE);
        onSharedPreferenceChanged(prefs, PREF_URI);
        onSharedPreferenceChanged(prefs, PREF_URI_USERNAME);
        onSharedPreferenceChanged(prefs, PREF_URI_PASSWORD);
        onSharedPreferenceChanged(prefs, PREF_USB_FILENAME);
        onSharedPreferenceChanged(prefs, PREF_STREAM_FILENAME);
        onSharedPreferenceChanged(prefs, PREF_JOB_NAME);

        onSharedPreferenceChanged(prefs, PREF_ORIENTATION);
        onSharedPreferenceChanged(prefs, PREF_PRINT_QUALITY);
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_BIN);

        onSharedPreferenceChanged(prefs, PREF_START_PAGE_RANGES);
        onSharedPreferenceChanged(prefs, PREF_END_PAGE_RANGES);
        onSharedPreferenceChanged(prefs, PREF_FINISHINGS);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    Preference.OnPreferenceClickListener startFileBrowserListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(getContext(), FileBrowserActivity.class);
            startActivityForResult(intent, FILE_BROWSER_REQUEST_CODE);
            return false;
        }
    };

    Preference.OnPreferenceClickListener copyTestPageListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (FileUtils.copyAssets(getContext())) {
                Toast.makeText(getActivity(), getString(R.string.test_page_copied), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_BROWSER_REQUEST_CODE) {
            ((MainActivity) getActivity()).setResumedFromFileBrowser(true);
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getStringExtra(FileUtils.PATH);
                if (!TextUtils.isEmpty(filePath)) {
                    SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
                    Source source = Source.valueOf(prefs.getString(PREF_SOURCE, Source.STORAGE.name()));
                    if (source == Source.STORAGE) {
                        prefs.edit().putString(PREF_FILENAME, filePath).apply();
                    } else if (source == Source.STREAM) {
                        prefs.edit().putString(PREF_STREAM_FILENAME, filePath).apply();
                    }
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Preference preference = findPreference(key);
        if (preference != null) {
            if (PREF_SOURCE.equals(key)) {
                final String entryStr = (String) ((ListPreference) preference).getEntry();
                final PrintAttributes.Source entry =
                        entryStr == null ? Source.STORAGE : PrintAttributes.Source.valueOf(entryStr);

                if (entry == Source.STORAGE) {
                    mSourceCategory = mStorageCategory;
                    ((MainActivity) getActivity()).disableEnableBatchButton(true);
                    if (getPreferenceScreen().findPreference(PREF_SOURCE_STORAGE_CATEGORY) == null) {
                        mSourceCategory.setEnabled(true);
                        getPreferenceScreen().addPreference(mSourceCategory);
                        mSourceCategory.setEnabled(true);
                    }
                    getPreferenceScreen().removePreference(mHttpCategory);
                    getPreferenceScreen().removePreference(mUsbCategory);
                    getPreferenceScreen().removePreference(mStreamCategory);
                } else if (entry == Source.HTTP) {
                    mSourceCategory = mHttpCategory;
                    ((MainActivity) getActivity()).disableEnableBatchButton(true);
                    if (getPreferenceScreen().findPreference(PREF_SOURCE_HTTP_CATEGORY) == null) {
                        mSourceCategory.setEnabled(true);
                        getPreferenceScreen().addPreference(mSourceCategory);
                        mSourceCategory.setEnabled(true);
                    }
                    getPreferenceScreen().removePreference(mStorageCategory);
                    getPreferenceScreen().removePreference(mUsbCategory);
                    getPreferenceScreen().removePreference(mStreamCategory);
                } else if (entry == Source.USB) {
                    mSourceCategory = mUsbCategory;
                    ((MainActivity) getActivity()).disableEnableBatchButton(true);
                    if (getPreferenceScreen().findPreference(PREF_SOURCE_USB_CATEGORY) == null) {
                        mSourceCategory.setEnabled(true);
                        getPreferenceScreen().addPreference(mSourceCategory);
                        mSourceCategory.setEnabled(true);
                    }
                    getPreferenceScreen().removePreference(mStorageCategory);
                    getPreferenceScreen().removePreference(mHttpCategory);
                    getPreferenceScreen().removePreference(mStreamCategory);

                    fillUSBStorages();

                    onSharedPreferenceChanged(sharedPreferences, PREF_USB_STORAGE);
                } else if (entry == Source.STREAM) {
                    mSourceCategory = mStreamCategory;
                    ((MainActivity) getActivity()).disableEnableBatchButton(false);
                    if (getPreferenceScreen().findPreference(PREF_SOURCE_STREAM_CATEGORY) == null) {
                        mSourceCategory.setEnabled(true);
                        getPreferenceScreen().addPreference(mSourceCategory);
                        mSourceCategory.setEnabled(true);
                    }
                    getPreferenceScreen().removePreference(mStorageCategory);
                    getPreferenceScreen().removePreference(mHttpCategory);
                    getPreferenceScreen().removePreference(mUsbCategory);

                    onSharedPreferenceChanged(sharedPreferences, PREF_STREAM_FILENAME);
                }
            } else if (PREF_USB_STORAGE.equals(key)) {
                final String usbLocation = ((ListPreference) preference).getValue() + "/";

                mUsbFilenamePref.setText(usbLocation);

                onSharedPreferenceChanged(sharedPreferences, PREF_USB_FILENAME);
            }

            if (PREF_FILENAME.equals(key)) {
                mFilenamePref.setSummary(sharedPreferences.getString(key, getContext().getFilesDir().getPath()));
            } else if (PREF_STREAM_FILENAME.equals(key)) {
                mStreamFilenamePref.setSummary(sharedPreferences.getString(key, getContext().getFilesDir().getPath()));
            }

            if (preference instanceof ListPreference) {
                final String entry = (String) ((ListPreference) preference).getEntry();

                if (entry == null || entry.length() == 0) {
                    ((ListPreference) preference).setValueIndex(0);
                    preference.setSummary("%s");
                } else {
                    preference.setSummary(entry);
                }
            } else if (preference instanceof EditTextIntegerPreference) {
                String value = ((EditTextIntegerPreference) preference).getText();
                int max = ((EditTextIntegerPreference) preference).getMaxVal();
                int min = ((EditTextIntegerPreference) preference).getMinVal();

                int intValue = min;
                if (!TextUtils.isEmpty(value)) {
                    intValue = Integer.parseInt(value);
                }
                // Validate stored value and correct if needed
                if (intValue > max || intValue < min) {
                    ((EditTextIntegerPreference) preference).setText(String.valueOf(min));
                } else {
                    preference.setSummary(((EditTextIntegerPreference) preference).getText());
                }
            } else if (preference instanceof EditTextPreference) {
                String text = ((EditTextPreference) preference).getText();
                if (PREF_URI_PASSWORD.equals(key)) {
                    text = getTransformationString(text);
                } else if (PREF_START_PAGE_RANGES.equals(key)
                    || PREF_END_PAGE_RANGES.equals(key)) {
                    if (!TextUtils.isEmpty(text)) {
                        int page = Integer.parseInt(text);
                        text = Integer.toString(page);
                        ((EditTextPreference) preference).setText(text);
                    }
                }
                preference.setSummary(text);
            } else if (preference instanceof CheckBoxPreference) {
                if (PREF_MONITORING_JOB.equals(key)) {
                    findPreference(PREF_SHOW_JOB_PROGRESS)
                            .setEnabled(((CheckBoxPreference) preference).isChecked());
                }
            } else if (preference instanceof MultiSelectListPreference ) {
                Set<String> prefSet = ((MultiSelectListPreference) preference).getValues();
                String summary = "";
                for (String value: prefSet) {
                    summary += value + " ";
                }
                preference.setSummary(summary);
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
     * Fills preferences with received capabilities.
     *
     * @param caps {@link com.hp.workpath.api.printer.PrintAttributesCaps}
     */
    public void loadCapabilities(final PrintAttributesCaps caps) {
        final ArrayList<CharSequence> duplexEntries = new ArrayList<>();
        final ArrayList<CharSequence> duplexEntryValues = new ArrayList<>();

        // Load duplex
        for (Duplex duplex : caps.getDuplexList()) {
            duplexEntries.add(duplex.name());
            duplexEntryValues.add(duplex.name());
        }

        mDuplexPref.setEntries(duplexEntries.toArray(new CharSequence[duplexEntries.size()]));
        mDuplexPref.setEntryValues(duplexEntryValues.toArray(new CharSequence[duplexEntryValues.size()]));
        mDuplexPref.setDefaultValue(Duplex.DEFAULT.name());
        mDuplexPref.setValueIndex(0);
        mDuplexPref.setSummary("%s");

        // Load color mode
        final ArrayList<CharSequence> cmEntries = new ArrayList<>();
        final ArrayList<CharSequence> cmEntryValues = new ArrayList<>();

        for (final ColorMode cm : caps.getColorModeList()) {
            cmEntries.add(cm.name());
            cmEntryValues.add(cm.name());
        }

        mCMPref.setEntries(cmEntries.toArray(new CharSequence[cmEntries.size()]));
        mCMPref.setEntryValues(cmEntryValues.toArray(new CharSequence[cmEntryValues.size()]));
        mCMPref.setDefaultValue(ColorMode.DEFAULT.name());
        mCMPref.setValueIndex(0);
        mCMPref.setSummary("%s");

        // Load AutoFit
        final ArrayList<CharSequence> afEntries = new ArrayList<>();
        final ArrayList<CharSequence> afEntryValues = new ArrayList<>();

        for (final AutoFit af : caps.getAutoFitList()) {
            afEntries.add(af.name());
            afEntryValues.add(af.name());
        }

        mAFPref.setEntries(afEntries.toArray(new CharSequence[afEntries.size()]));
        mAFPref.setEntryValues(afEntryValues.toArray(new CharSequence[afEntryValues.size()]));
        mAFPref.setDefaultValue(AutoFit.DEFAULT.name());
        mAFPref.setValueIndex(0);
        mAFPref.setSummary("%s");

        // Load staple mode
        final ArrayList<CharSequence> smEntries = new ArrayList<>();
        final ArrayList<CharSequence> smEntryValues = new ArrayList<>();

        for (final StapleMode sm : caps.getStapleModeList()) {
            smEntries.add(sm.name());
            smEntryValues.add(sm.name());
        }

        mSMPref.setEntries(smEntries.toArray(new CharSequence[smEntries.size()]));
        mSMPref.setEntryValues(smEntryValues.toArray(new CharSequence[smEntryValues.size()]));
        mSMPref.setDefaultValue(StapleMode.DEFAULT.name());
        mSMPref.setValueIndex(0);
        mSMPref.setSummary("%s");

        // Load collate mode
        final ArrayList<CharSequence> collateEntries = new ArrayList<>();
        final ArrayList<CharSequence> collateEntryValues = new ArrayList<>();

        for (final PrintAttributes.CollateMode collateMode : caps.getCollateModeList()) {
            collateEntries.add(collateMode.name());
            collateEntryValues.add(collateMode.name());
        }

        mCollatePref.setEntries(collateEntries.toArray(new CharSequence[collateEntries.size()]));
        mCollatePref.setEntryValues(collateEntryValues.toArray(new CharSequence[collateEntryValues.size()]));
        mCollatePref.setDefaultValue(PrintAttributes.CollateMode.DEFAULT.name());
        mCollatePref.setValueIndex(0);
        mCollatePref.setSummary("%s");

        // Load Paper Source
        final ArrayList<CharSequence> psrcEntries = new ArrayList<>();
        final ArrayList<CharSequence> psrcEntryValues = new ArrayList<>();

        for (final PaperSource psrc : caps.getPaperSourceList()) {
            psrcEntries.add(psrc.name());
            psrcEntryValues.add(psrc.name());
        }

        mPaperSrcPref.setEntries(psrcEntries.toArray(new CharSequence[psrcEntries.size()]));
        mPaperSrcPref.setEntryValues(psrcEntryValues.toArray(new CharSequence[psrcEntryValues.size()]));
        mPaperSrcPref.setDefaultValue(PaperSource.DEFAULT.name());
        mPaperSrcPref.setValueIndex(0);
        mPaperSrcPref.setSummary("%s");

        // Load Paper Size
        final ArrayList<CharSequence> pszEntries = new ArrayList<>();
        final ArrayList<CharSequence> pszEntryValues = new ArrayList<>();

        for (PrintAttributes.PaperSize os : caps.getPaperSizeList()) {
            CharSequence entrie = "";
            if (os.getWidth() == 0.0 && os.getHeight() == 0.0 && os.getUnit() == null) {
                entrie = os.name();
            } else {
                entrie = os.name()+" ("+os.getWidth()+" X "+os.getHeight()+" "+os.getUnit()+")";
            }
            pszEntries.add(entrie);
            pszEntryValues.add(os.name());
        }

        mPaperSzPref.setEntries(pszEntries.toArray(new CharSequence[pszEntries.size()]));
        mPaperSzPref.setEntryValues(pszEntryValues.toArray(new CharSequence[pszEntryValues.size()]));
        mPaperSzPref.setDefaultValue(PrintAttributes.PaperSize.DEFAULT.name());
        mPaperSzPref.setValueIndex(0);
        mPaperSzPref.setSummary("%s");

        // Load Paper Type
        final ArrayList<CharSequence> pTypeEntries = new ArrayList<>();
        final ArrayList<CharSequence> pTypeEntryValues = new ArrayList<>();

        for (final PaperType paperType : caps.getPaperTypeList()) {
            pTypeEntries.add(paperType.name());
            pTypeEntryValues.add(paperType.name());
        }

        mPaperTypePref.setEntries(pTypeEntries.toArray(new CharSequence[pTypeEntries.size()]));
        mPaperTypePref.setEntryValues(pTypeEntryValues.toArray(new CharSequence[pTypeEntryValues.size()]));
        mPaperTypePref.setDefaultValue(PaperType.DEFAULT.name());
        mPaperTypePref.setValueIndex(0);
        mPaperTypePref.setSummary("%s");

        // Load Document Format
        final ArrayList<CharSequence> dfmtEntries = new ArrayList<>();
        final ArrayList<CharSequence> dfmtEntryValues = new ArrayList<>();

        for (final DocumentFormat dfmt : caps.getDocumentFormatList()) {
            dfmtEntries.add(dfmt.name());
            dfmtEntryValues.add(dfmt.name());
        }

        mDocFmtPref.setEntries(dfmtEntries.toArray(new CharSequence[dfmtEntries.size()]));
        mDocFmtPref.setEntryValues(dfmtEntryValues.toArray(new CharSequence[dfmtEntryValues.size()]));
        mDocFmtPref.setDefaultValue(DocumentFormat.AUTO.name());
        mDocFmtPref.setValueIndex(0);
        mDocFmtPref.setSummary("%s");

        // Load source
        final ArrayList<CharSequence> srcEntries = new ArrayList<>();
        final ArrayList<CharSequence> srcEntryValues = new ArrayList<>();

        for (final Source src : caps.getSourceList()) {
            srcEntries.add(src.name());
            srcEntryValues.add(src.name());
        }

        mSourcePref.setEntries(srcEntries.toArray(new CharSequence[srcEntries.size()]));
        mSourcePref.setEntryValues(srcEntryValues.toArray(new CharSequence[srcEntryValues.size()]));
        mSourcePref.setDefaultValue(Source.STORAGE.name());
        mSourcePref.setValueIndex(0);
        mSourcePref.setSummary("%s");

        // Apply Copies limits
        if (caps.getMaxCopies() > 0) {
            mCopiesPref.setLimits(1, caps.getMaxCopies());
        }

        // Load Orientation
        final ArrayList<CharSequence> otEntries = new ArrayList<>();
        final ArrayList<CharSequence> otEntryValues = new ArrayList<>();

        for (final PrintAttributes.Orientation orientation : caps.getOrientationList()) {
            otEntries.add(orientation.name());
            otEntryValues.add(orientation.name());
        }

        mOrientationPref.setEntries(otEntries.toArray(new CharSequence[otEntries.size()]));
        mOrientationPref.setEntryValues(otEntryValues.toArray(new CharSequence[otEntryValues.size()]));
        mOrientationPref.setDefaultValue(PrintAttributes.Orientation.DEFAULT.name());
        mOrientationPref.setValueIndex(0);
        mOrientationPref.setSummary("%s");

        // Load Print-Quality
        final ArrayList<CharSequence> pqEntries = new ArrayList<>();
        final ArrayList<CharSequence> pqEntryValues = new ArrayList<>();

        for (final PrintAttributes.PrintQuality printQuality : caps.getPrintQualityList()) {
            pqEntries.add(printQuality.name());
            pqEntryValues.add(printQuality.name());
        }

        mPrintQualityPref.setEntries(pqEntries.toArray(new CharSequence[pqEntries.size()]));
        mPrintQualityPref.setEntryValues(pqEntryValues.toArray(new CharSequence[pqEntryValues.size()]));
        mPrintQualityPref.setDefaultValue(PrintAttributes.PrintQuality.DEFAULT.name());
        mPrintQualityPref.setValueIndex(0);
        mPrintQualityPref.setSummary("%s");

        // Load Output-Bin
        final ArrayList<CharSequence> obEntries = new ArrayList<>();
        final ArrayList<CharSequence> obEntryValues = new ArrayList<>();

        for (final PrintAttributes.OutputBin outputbin : caps.getOutputBinList()) {
            obEntries.add(outputbin.name());
            obEntryValues.add(outputbin.name());
        }

        mOutputBinPref.setEntries(obEntries.toArray(new CharSequence[obEntries.size()]));
        mOutputBinPref.setEntryValues(obEntryValues.toArray(new CharSequence[obEntryValues.size()]));
        mOutputBinPref.setDefaultValue(PrintAttributes.OutputBin.DEFAULT.name());
        mOutputBinPref.setValueIndex(0);
        mOutputBinPref.setSummary("%s");

        // Load Finishings options
        final ArrayList<CharSequence> foEntries = new ArrayList<>();
        final ArrayList<CharSequence> foEntryValues = new ArrayList<>();

        for (final Finishings fo : caps.getFinishingsList()) {
            foEntries.add(fo.name());
            foEntryValues.add(fo.name());
        }
        Set<String> finishings = new HashSet<>();

        mFinishingsPref.setEntries(foEntries.toArray(new CharSequence[foEntries.size()]));
        mFinishingsPref.setEntryValues(foEntryValues.toArray(new CharSequence[foEntryValues.size()]));
        mFinishingsPref.setDefaultValue(PrintAttributes.Finishings.DEFAULT.name());
        finishings.add(PrintAttributes.Finishings.DEFAULT.name());
        mFinishingsPref.setValues(finishings);
        mFinishingsPref.setSummary(Finishings.DEFAULT.name());
    }

    public void setDefaultPrintAttributes(PrintAttributes printAttributes) {
        if (printAttributes != null) {
            PrintAttributesReader printAttributesReader = new PrintAttributesReader(printAttributes);
            setPreferenceEntryValue(PREF_SOURCE, printAttributesReader.getSource().name());
            setPreferenceEntryValue(PREF_AUTOFIT, printAttributesReader.getAutoFit().name());
            setPreferenceEntryValue(PREF_COLOR_MODE, printAttributesReader.getColorMode().name());
            setPreferenceEntryValue(PREF_COLLATE_MODE, printAttributesReader.getCollateMode().name());
            setPreferenceEntryValue(PREF_STAPLE_MODE, printAttributesReader.getStapleMode().name());
            setPreferenceEntryValue(PREF_PAPER_SOURCE, printAttributesReader.getPaperSource().name());
            setPreferenceEntryValue(PREF_PAPER_SIZE, printAttributesReader.getPaperSize().name());
            setPreferenceEntryValue(PREF_PAPER_TYPE, printAttributesReader.getPaperType().name());
            setPreferenceEntryValue(PREF_DUPLEX_MODE, printAttributesReader.getPlex().name());
            setPreferenceEntryValue(PREF_DOC_FORMAT, printAttributesReader.getDocumentFormat().name());
            setPreferenceEntryValue(PREF_ORIENTATION, printAttributesReader.getOrientation().name());
            setPreferenceEntryValue(PREF_PRINT_QUALITY, printAttributesReader.getPrintQuality().name());
            setPreferenceEntryValue(PREF_OUTPUT_BIN, printAttributesReader.getOutputBin().name());
            setMultiSelectPreferenceEntryValue(PREF_FINISHINGS, printAttributesReader.getFinishingsList());
            setPreferenceValue(PREF_COPIES, Integer.toString(printAttributesReader.getCopies()));
            setDefaultFilePreference();
        }
    }

    private void setPreferenceValue(String pref, String value) {
        EditTextPreference editTextPreference = (EditTextPreference) findPreference(pref);
        editTextPreference.setText(value);
        editTextPreference.setSummary(value);
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

    private <T extends Enum<T>> void setMultiSelectPreferenceEntryValue(String pref, List<T> attributeList) {
        MultiSelectListPreference preference = (MultiSelectListPreference) findPreference(pref);
        Set<String> prefSet = new HashSet<>();
        String summary = "";
        for (T attribute : attributeList) {
            prefSet.add(attribute.name());
            summary += attribute.name() + " ";
        }
        preference.setValues(prefSet);
        preference.setSummary(summary);
    }

    private void fillUSBStorages() {
        ListPreference pref = (ListPreference) findPreference(PREF_USB_STORAGE);

        if (pref != null) {
            Result result = new Result();
            if (!MassStorageService.isSupported(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.mass_storage_not_supported), Toast.LENGTH_SHORT).show();
                return;
            }

            List<MassStorageInfo> storageList = MassStorageService.getStorageList(getActivity(), result);
            if (result.getCode() != Result.RESULT_OK || storageList == null) {
                storageList = Collections.emptyList();
            }

            final List<CharSequence> entries = new ArrayList<>();
            final List<CharSequence> entriesValues = new ArrayList<>();

            for (MassStorageInfo storage : storageList) {
                if (storage.getType() == MassStorageInfo.StorageType.USB && storage.isMounted()) {
                    entries.add(storage.getName());
                    entriesValues.add(storage.getExternalFileDirectory());
                }
            }

            pref.setEntries(entries.toArray(new CharSequence[entries.size()]));
            pref.setEntryValues(entriesValues.toArray(new CharSequence[entriesValues.size()]));

            if (!entries.isEmpty()) {
                String defaultValue = (String) entriesValues.get(0);

                pref.setDefaultValue(defaultValue);
                pref.setValueIndex(0);
                pref.setSummary("%s");
            }
        }
    }
}
