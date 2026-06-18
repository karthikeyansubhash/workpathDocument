// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.*
import com.hp.workpath.api.Result
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.api.massstorage.MassStorageService
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrintAttributesReader
import com.hp.workpath.sample.printsample.FileBrowserActivity
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.R
import com.hp.workpath.sample.printsample.filebrowser.FileUtils
import com.hp.workpath.sample.printsample.filebrowser.FileUtils.copyAssets

/**
 * Simple [PreferenceFragmentCompat] to set Print Attributes and
 * save into preferences.
 */
class PrintConfigureFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    private var mFilenamePref: Preference? = null
    private var mFileUriPref: EditTextPreference? = null
    private var mFileUriUsernamePref: EditTextPreference? = null
    private var mFileUriPasswordPref: EditTextPreference? = null
    private var mUsbFilenamePref: EditTextPreference? = null
    private var mStreamFilenamePref: Preference? = null

    private var mDuplexPref: ListPreference? = null
    private var mCMPref: ListPreference? = null
    private var mAFPref: ListPreference? = null
    private var mSMPref: ListPreference? = null
    private var mCollatePref: ListPreference? = null
    private var mPaperSrcPref: ListPreference? = null
    private var mPaperSzPref: ListPreference? = null
    private var mPaperTypePref: ListPreference? = null
    private var mDocFmtPref: ListPreference? = null
    private var mSourcePref: ListPreference? = null
    private var mOrientationPref: ListPreference? = null
    private var mPrintQualityPref: ListPreference? = null
    private var mOutputBinPref: ListPreference? = null
    private var mCopiesPref: EditTextIntegerPreference? = null
    private var mJobNamePref: EditTextPreference? = null
    private var mStartPageRangesPref: EditTextPreference? = null
    private var mEndPageRangesPref: EditTextPreference? = null
    private var mFinishingsPref: MultiSelectListPreference? = null
    private var mCopyTestPage: Preference? = null
    private var mStreamCopyTestPage: Preference? = null

    private var mSourceCategory: PreferenceCategory? = null
    private var mStorageCategory: PreferenceCategory? = null
    private var mHttpCategory: PreferenceCategory? = null
    private var mUsbCategory: PreferenceCategory? = null
    private var mStreamCategory: PreferenceCategory? = null

    private var mMonitoringJobPref: CheckBoxPreference? = null
    private var mShowJobProgressPref: CheckBoxPreference? = null
    private var mShowSettingsPref: CheckBoxPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.print_preferences)

        mDuplexPref = findPreference(PREF_DUPLEX_MODE)
        mCMPref = findPreference(PREF_COLOR_MODE)
        mAFPref = findPreference(PREF_AUTOFIT)
        mSMPref = findPreference(PREF_STAPLE_MODE)
        mCollatePref = findPreference(PREF_COLLATE_MODE)
        mPaperSrcPref = findPreference(PREF_PAPER_SOURCE)
        mPaperSzPref = findPreference(PREF_PAPER_SIZE)
        mPaperTypePref = findPreference(PREF_PAPER_TYPE)
        mDocFmtPref = findPreference(PREF_DOC_FORMAT)
        mSourcePref = findPreference(PREF_SOURCE)
        mFilenamePref = findPreference(PREF_FILENAME)
        mFileUriPref = findPreference(PREF_URI)
        mFileUriPref?.text = null
        mFileUriUsernamePref = findPreference(PREF_URI_USERNAME)
        mFileUriUsernamePref?.text = null
        mFileUriPasswordPref = findPreference(PREF_URI_PASSWORD)
        mFileUriPasswordPref?.text = null
        mUsbFilenamePref = findPreference(PREF_USB_FILENAME)
        mUsbFilenamePref?.text = null
        mStreamFilenamePref = findPreference(PREF_STREAM_FILENAME)

        mOrientationPref = findPreference(PREF_ORIENTATION)
        mPrintQualityPref = findPreference(PREF_PRINT_QUALITY)
        mOutputBinPref = findPreference(PREF_OUTPUT_BIN)

        // Set default limits to single, default, value
        mCopiesPref = findPreference(PREF_COPIES)
        mCopiesPref?.setLimits(1, 1)

        mJobNamePref = findPreference(PREF_JOB_NAME)
        mJobNamePref?.text = null

        mStartPageRangesPref = findPreference(PREF_START_PAGE_RANGES)
        mStartPageRangesPref?.text = null
        mEndPageRangesPref = findPreference(PREF_END_PAGE_RANGES)
        mEndPageRangesPref?.text = null

        mFinishingsPref = findPreference(PREF_FINISHINGS)

        mStorageCategory = findPreference(PREF_SOURCE_STORAGE_CATEGORY)
        mHttpCategory = findPreference(PREF_SOURCE_HTTP_CATEGORY)
        mUsbCategory = findPreference(PREF_SOURCE_USB_CATEGORY)
        mStreamCategory = findPreference(PREF_SOURCE_STREAM_CATEGORY)

        mMonitoringJobPref = findPreference(PREF_MONITORING_JOB)
        mShowJobProgressPref = findPreference(PREF_SHOW_JOB_PROGRESS)
        mShowSettingsPref = findPreference(PREF_SHOW_SETTINGS)

        mMonitoringJobPref?.isChecked = true
        mShowJobProgressPref?.isChecked = true
        mShowSettingsPref?.isChecked = false

        setDefaultFilePreference()

        mCopyTestPage = findPreference(PREF_COPY_TEST_PAGE)
        mStreamCopyTestPage = findPreference(PREF_STREAM_COPY_TEST_PAGE)
        mCopyTestPage?.onPreferenceClickListener = copyTestPageListener
        mStreamCopyTestPage?.onPreferenceClickListener = copyTestPageListener
        mFilenamePref?.onPreferenceClickListener = startFileBrowserListener
        mStreamFilenamePref?.onPreferenceClickListener = startFileBrowserListener
    }

    override fun onResume() {
        super.onResume()
        val prefs = preferenceScreen.sharedPreferences
        prefs.registerOnSharedPreferenceChangeListener(this)
        refreshAllPrefs(prefs)
    }

    private fun refreshAllPrefs(prefs: SharedPreferences) {
        onSharedPreferenceChanged(prefs, PREF_COLOR_MODE)
        onSharedPreferenceChanged(prefs, PREF_DUPLEX_MODE)
        onSharedPreferenceChanged(prefs, PREF_COPIES)
        onSharedPreferenceChanged(prefs, PREF_FILENAME)
        onSharedPreferenceChanged(prefs, PREF_AUTOFIT)
        onSharedPreferenceChanged(prefs, PREF_STAPLE_MODE)
        onSharedPreferenceChanged(prefs, PREF_COLLATE_MODE)
        onSharedPreferenceChanged(prefs, PREF_PAPER_SOURCE)
        onSharedPreferenceChanged(prefs, PREF_PAPER_SIZE)
        onSharedPreferenceChanged(prefs, PREF_PAPER_TYPE)
        onSharedPreferenceChanged(prefs, PREF_DOC_FORMAT)
        onSharedPreferenceChanged(prefs, PREF_SHOW_SETTINGS)
        onSharedPreferenceChanged(prefs, PREF_MONITORING_JOB)
        onSharedPreferenceChanged(prefs, PREF_SHOW_JOB_PROGRESS)
        onSharedPreferenceChanged(prefs, PREF_SOURCE)
        onSharedPreferenceChanged(prefs, PREF_URI)
        onSharedPreferenceChanged(prefs, PREF_URI_USERNAME)
        onSharedPreferenceChanged(prefs, PREF_URI_PASSWORD)
        onSharedPreferenceChanged(prefs, PREF_USB_FILENAME)
        onSharedPreferenceChanged(prefs, PREF_STREAM_FILENAME)
        onSharedPreferenceChanged(prefs, PREF_JOB_NAME)

        onSharedPreferenceChanged(prefs, PREF_ORIENTATION)
        onSharedPreferenceChanged(prefs, PREF_PRINT_QUALITY)
        onSharedPreferenceChanged(prefs, PREF_OUTPUT_BIN)

        onSharedPreferenceChanged(prefs, PREF_START_PAGE_RANGES)
        onSharedPreferenceChanged(prefs, PREF_END_PAGE_RANGES)
        onSharedPreferenceChanged(prefs, PREF_FINISHINGS)
    }

    override fun onPause() {
        super.onPause()
        val prefs = preferenceScreen.sharedPreferences
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    private var startFileBrowserListener = Preference.OnPreferenceClickListener {
        val intent = Intent(context, FileBrowserActivity::class.java)
        mFileBrowserLauncher.launch(intent)
        false
    }

    private var mFileBrowserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(FILE_BROWSER_REQUEST_CODE, result)
    }


    private var copyTestPageListener = Preference.OnPreferenceClickListener {
        if (copyAssets(requireContext())) {
            Toast.makeText(activity, requireActivity().getString(R.string.test_page_copied), Toast.LENGTH_SHORT).show()
        }
        false
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if(result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            when (requestCode) {
                FILE_BROWSER_REQUEST_CODE -> {
                    (requireActivity() as MainActivity).setResumedFromFileBrowser(true)
                        val filePath = data?.getStringExtra(FileUtils.PATH)
                        if (!TextUtils.isEmpty(filePath)) {
                            val prefs = preferenceScreen.sharedPreferences
                            val source = PrintAttributes.Source.valueOf(prefs.getString(PREF_SOURCE, PrintAttributes.Source.STORAGE.name)
                                ?: PrintAttributes.Source.STORAGE.name)
                            if (source == PrintAttributes.Source.STORAGE) {
                                prefs.edit().putString(PREF_FILENAME, filePath).apply()
                            } else if (source == PrintAttributes.Source.STREAM) {
                                prefs.edit().putString(PREF_STREAM_FILENAME, filePath).apply()
                            }
                        }
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)
        preference?.run {
            if (PREF_SOURCE == key) {
                val entryStr = (preference as ListPreference).entry as String?
                when (PrintAttributes.Source.valueOf(entryStr
                        ?: PrintAttributes.Source.STORAGE.name)) {
                    PrintAttributes.Source.STORAGE -> {
                        (requireActivity() as MainActivity).disableEnableBatchButton(true)
                        mSourceCategory = mStorageCategory
                        if (preferenceScreen.findPreference<Preference?>(PREF_SOURCE_STORAGE_CATEGORY) == null) {
                            mSourceCategory?.isEnabled = true
                            preferenceScreen.addPreference(mSourceCategory)
                            mSourceCategory?.isEnabled = true
                        }
                        preferenceScreen.removePreference(mHttpCategory)
                        preferenceScreen.removePreference(mUsbCategory)
                        preferenceScreen.removePreference(mStreamCategory)

                    }
                    PrintAttributes.Source.HTTP -> {
                        (requireActivity() as MainActivity).disableEnableBatchButton(true)
                        mSourceCategory = mHttpCategory
                        if (preferenceScreen.findPreference<Preference?>(PREF_SOURCE_HTTP_CATEGORY) == null) {
                            mSourceCategory?.isEnabled = true
                            preferenceScreen.addPreference(mSourceCategory)
                            mSourceCategory?.isEnabled = true
                        }
                        preferenceScreen.removePreference(mStorageCategory)
                        preferenceScreen.removePreference(mUsbCategory)
                        preferenceScreen.removePreference(mStreamCategory)

                    }
                    PrintAttributes.Source.USB -> {
                        (requireActivity() as MainActivity).disableEnableBatchButton(true)
                        mSourceCategory = mUsbCategory
                        if (preferenceScreen.findPreference<Preference?>(PREF_SOURCE_USB_CATEGORY) == null) {
                            mSourceCategory?.isEnabled = true
                            preferenceScreen.addPreference(mSourceCategory)
                            mSourceCategory?.isEnabled = true
                        }
                        preferenceScreen.removePreference(mStorageCategory)
                        preferenceScreen.removePreference(mHttpCategory)
                        preferenceScreen.removePreference(mStreamCategory)
                        fillUSBStorages()
                        onSharedPreferenceChanged(sharedPreferences, PREF_USB_STORAGE)

                    }
                    PrintAttributes.Source.STREAM -> {
                        (requireActivity() as MainActivity).disableEnableBatchButton(false)
                        mSourceCategory = mStreamCategory
                        if (preferenceScreen.findPreference<Preference?>(PREF_SOURCE_STREAM_CATEGORY) == null) {
                            mSourceCategory?.isEnabled = true
                            preferenceScreen.addPreference(mSourceCategory)
                            mSourceCategory?.isEnabled = true
                        }
                        preferenceScreen.removePreference(mStorageCategory)
                        preferenceScreen.removePreference(mHttpCategory)
                        preferenceScreen.removePreference(mUsbCategory)
                        onSharedPreferenceChanged(sharedPreferences, PREF_STREAM_FILENAME)
                    }
                }
            } else if (PREF_USB_STORAGE == key) {
                val usbLocation = (preference as ListPreference).value + "/"
                mUsbFilenamePref?.text = usbLocation
                onSharedPreferenceChanged(sharedPreferences, PREF_USB_FILENAME)
            }
            if (PREF_FILENAME == key) {
                mFilenamePref?.summary = sharedPreferences.getString(key, requireContext().filesDir.path)
            } else if (PREF_STREAM_FILENAME == key) {
                mStreamFilenamePref?.summary = sharedPreferences.getString(key, requireContext().filesDir.path)
            }
            if (preference is ListPreference) {
                val entry = preference.entry as String?
                if (entry == null || entry.isEmpty()) {
                    preference.setValueIndex(0)
                    preference.setSummary("%s")
                } else {
                    preference.setSummary(entry)
                }
            } else if (preference is EditTextIntegerPreference) {
                val value = preference.text
                val max = preference.maxVal
                val min = preference.minVal
                var intValue = min
                if (!TextUtils.isEmpty(value)) {
                    intValue = value.toInt()
                }
                // Validate stored value and correct if needed
                if (intValue > max || intValue < min) {
                    preference.text = min.toString()
                } else {
                    preference.setSummary(preference.text)
                }
            } else if (preference is EditTextPreference) {
                var text = preference.text
                if (PREF_URI_PASSWORD == key) {
                    text = getTransformationString(text)
                } else if (PREF_START_PAGE_RANGES == key
                        || PREF_END_PAGE_RANGES == key) {
                    if (TextUtils.isEmpty(text).not()) {
                        var page = Integer.parseInt(text)
                        text = page.toString()
                        preference.text = text
                    }
                }
                preference.setSummary(text)
            } else if (preference is CheckBoxPreference) {
                if (PREF_MONITORING_JOB == key) {
                    findPreference<Preference>(PREF_SHOW_JOB_PROGRESS)?.isEnabled = preference.isChecked
                }
            } else if (preference is MultiSelectListPreference) {
                val prefSet = preference.values
                var summary = ""
                for (value in prefSet) {
                    summary += "$value "
                }
                preference.setSummary(summary)
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
     * Fills preferences with received capabilities.
     *
     * @param caps [com.hp.workpath.api.printer.PrintAttributesCaps]
     */
    fun loadCapabilities(caps: PrintAttributesCaps) {
        val duplexEntries = ArrayList<CharSequence>()
        val duplexEntryValues = ArrayList<CharSequence>()

        // Load duplex
        for (duplex in caps.duplexList) {
            duplexEntries.add(duplex.name)
            duplexEntryValues.add(duplex.name)
        }
        mDuplexPref?.let {
            it.entries = duplexEntries.toTypedArray()
            it.entryValues = duplexEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.Duplex.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load color mode
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()
        for (cm in caps.colorModeList) {
            cmEntries.add(cm.name)
            cmEntryValues.add(cm.name)
        }
        mCMPref?.let {
            it.entries = cmEntries.toTypedArray()
            it.entryValues = cmEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.ColorMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load AutoFit
        val afEntries = ArrayList<CharSequence>()
        val afEntryValues = ArrayList<CharSequence>()
        for (af in caps.autoFitList) {
            afEntries.add(af.name)
            afEntryValues.add(af.name)
        }
        mAFPref?.let {
            it.entries = afEntries.toTypedArray()
            it.entryValues = afEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.AutoFit.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load staple mode
        val smEntries = ArrayList<CharSequence>()
        val smEntryValues = ArrayList<CharSequence>()
        for (sm in caps.stapleModeList) {
            smEntries.add(sm.name)
            smEntryValues.add(sm.name)
        }
        mSMPref?.let {
            it.entries = smEntries.toTypedArray()
            it.entryValues = smEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.StapleMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load collate mode
        val collateEntries = ArrayList<CharSequence>()
        val collateEntryValues = ArrayList<CharSequence>()
        for (collateMode in caps.collateModeList) {
            collateEntries.add(collateMode.name)
            collateEntryValues.add(collateMode.name)
        }
        mCollatePref?.let {
            it.entries = collateEntries.toTypedArray()
            it.entryValues = collateEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.CollateMode.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Paper Source
        val psrcEntries = ArrayList<CharSequence>()
        val psrcEntryValues = ArrayList<CharSequence>()
        for (psrc in caps.paperSourceList) {
            psrcEntries.add(psrc.name)
            psrcEntryValues.add(psrc.name)
        }
        mPaperSrcPref?.let {
            it.entries = psrcEntries.toTypedArray()
            it.entryValues = psrcEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.PaperSource.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Paper Size
        val pszEntries = ArrayList<CharSequence>()
        val pszEntryValues = ArrayList<CharSequence>()
        for (psz in caps.paperSizeList) {
             val entry = if(psz.width==0.0 && psz.height==0.0 && psz.unit==null){
                psz.name
            }else {
                "${psz.name} (${psz.width} X ${psz.height} ${psz.unit})"
            }
            pszEntries.add(entry)
            pszEntryValues.add(psz.name)
        }
        mPaperSzPref?.let {
            it.entries = pszEntries.toTypedArray()
            it.entryValues = pszEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.PaperSize.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Paper Type
        val pTypeEntries = ArrayList<CharSequence>()
        val pTypeEntryValues = ArrayList<CharSequence>()
        for (paperType in caps.paperTypeList) {
            pTypeEntries.add(paperType.name)
            pTypeEntryValues.add(paperType.name)
        }
        mPaperTypePref?.let {
            it.entries = pTypeEntries.toTypedArray()
            it.entryValues = pTypeEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.PaperType.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Document Format
        val dfmtEntries = ArrayList<CharSequence>()
        val dfmtEntryValues = ArrayList<CharSequence>()
        for (dfmt in caps.documentFormatList) {
            dfmtEntries.add(dfmt.name)
            dfmtEntryValues.add(dfmt.name)
        }
        mDocFmtPref?.let {
            it.entries = dfmtEntries.toTypedArray()
            it.entryValues = dfmtEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.DocumentFormat.AUTO.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load source
        val srcEntries = ArrayList<CharSequence>()
        val srcEntryValues = ArrayList<CharSequence>()
        for (src in caps.sourceList) {
            srcEntries.add(src.name)
            srcEntryValues.add(src.name)
        }
        mSourcePref?.let {
            it.entries = srcEntries.toTypedArray()
            it.entryValues = srcEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.Source.STORAGE.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Apply Copies limits
        if (caps.maxCopies > 0) {
            mCopiesPref?.setLimits(1, caps.maxCopies)
        }

        // Load Orientation
        val otEntries = ArrayList<CharSequence>()
        val otEntryValues = ArrayList<CharSequence>()

        for (orientation in caps.orientationList) {
            otEntries.add(orientation.name)
            otEntryValues.add(orientation.name)
        }
        mOrientationPref?.let {
            it.entries = otEntries.toTypedArray()
            it.entryValues = otEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.Orientation.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Print-Quality
        val pqEntries = ArrayList<CharSequence>()
        val pqEntryValues = ArrayList<CharSequence>()

        for (printQuality in caps.printQualityList) {
            pqEntries.add(printQuality.name)
            pqEntryValues.add(printQuality.name)
        }

        mPrintQualityPref?.let {
            it.entries = pqEntries.toTypedArray()
            it.entryValues = pqEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.PrintQuality.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Output-Bin
        val obEntries = ArrayList<CharSequence>()
        val obEntryValues = ArrayList<CharSequence>()

        for (outputbin in caps.outputBinList) {
            obEntries.add(outputbin.name)
            obEntryValues.add(outputbin.name)
        }

        mOutputBinPref?.let {
            it.entries = obEntries.toTypedArray()
            it.entryValues = obEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.OutputBin.DEFAULT.name)
            it.setValueIndex(0)
            it.summary = "%s"
        }

        // Load Finishings options
        val foEntries = ArrayList<CharSequence>()
        val foEntryValues = ArrayList<CharSequence>()

        for (fo in caps.finishingsList) {
            foEntries.add(fo.name)
            foEntryValues.add(fo.name)
        }

        val finishings: MutableSet<String> = HashSet()
        mFinishingsPref?.let {
            it.entries = foEntries.toTypedArray()
            it.entryValues = foEntryValues.toTypedArray()
            it.setDefaultValue(PrintAttributes.Finishings.DEFAULT.name)
            finishings.add(PrintAttributes.Finishings.DEFAULT.name)
            it.values = finishings
            it.summary = PrintAttributes.Finishings.DEFAULT.name
        }
    }

    fun setDefaultPrintAttributes(printAttributes: PrintAttributes?) {
        if (printAttributes != null) {
            val printAttributesReader = PrintAttributesReader(printAttributes)
            setPreferenceEntryValue(PREF_SOURCE, printAttributesReader.source.name)
            setPreferenceEntryValue(PREF_AUTOFIT, printAttributesReader.autoFit.name)
            setPreferenceEntryValue(PREF_COLOR_MODE, printAttributesReader.colorMode.name)
            setPreferenceEntryValue(PREF_COLLATE_MODE, printAttributesReader.collateMode.name)
            setPreferenceEntryValue(PREF_STAPLE_MODE, printAttributesReader.stapleMode.name)
            setPreferenceEntryValue(PREF_PAPER_SOURCE, printAttributesReader.paperSource.name)
            setPreferenceEntryValue(PREF_PAPER_SIZE, printAttributesReader.paperSize.name)
            setPreferenceEntryValue(PREF_PAPER_TYPE, printAttributesReader.paperType.name)
            setPreferenceEntryValue(PREF_DUPLEX_MODE, printAttributesReader.plex.name)
            setPreferenceEntryValue(PREF_DOC_FORMAT, printAttributesReader.documentFormat.name)
            setPreferenceEntryValue(PREF_ORIENTATION, printAttributesReader.orientation.name)
            setPreferenceEntryValue(PREF_PRINT_QUALITY, printAttributesReader.printQuality.name)
            setPreferenceEntryValue(PREF_OUTPUT_BIN, printAttributesReader.outputBin.name)
            setMultiSelectPreferenceEntryValue<PrintAttributes.Finishings>(PREF_FINISHINGS, printAttributesReader.finishingsList)
            setPreferenceValue(PREF_COPIES, printAttributesReader.copies.toString())
            setDefaultFilePreference()
        }
    }

    private fun setDefaultFilePreference() {
        val internalDefaultPath = requireContext().filesDir.path
        val prefs = preferenceScreen.sharedPreferences
        prefs.edit().putString(PREF_FILENAME, internalDefaultPath).apply()
        prefs.edit().putString(PREF_STREAM_FILENAME, internalDefaultPath).apply()
    }

    private fun setPreferenceValue(pref: String, value: String) {
        val editTextPreference = findPreference<Preference>(pref) as EditTextPreference?
        editTextPreference?.text = value
        editTextPreference?.summary = value
    }

    private fun setPreferenceEntryValue(pref: String, attribute: String) {
        val listPreference = findPreference<Preference>(pref) as ListPreference
        var index = 0
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

    private fun <T : Enum<T>> setMultiSelectPreferenceEntryValue(pref: String, attributeList: List<T>) {
        val preference = findPreference<Preference>(pref) as MultiSelectListPreference?
        if (preference != null) {
            val prefSet: MutableSet<String> = HashSet()
            var summary = ""
            for (attribute in attributeList) {
                prefSet.add(attribute.name)
                summary += attribute.name + " "
            }
            preference.values = prefSet
            preference.summary = summary
        }
    }

    private fun fillUSBStorages() {
        val pref = findPreference<Preference>(PREF_USB_STORAGE) as ListPreference?
        if (pref != null) {
            val result = Result()
            if (!MassStorageService.isSupported(requireContext())) {
                Toast.makeText(activity, requireActivity().getString(R.string.mass_storage_not_supported), Toast.LENGTH_SHORT).show()
                return
            }
            var storageList = MassStorageService.getStorageList(requireContext(), result)
            if (result.code != Result.RESULT_OK || storageList == null) {
                storageList = emptyList()
            }
            val entries: MutableList<CharSequence> = ArrayList()
            val entriesValues: MutableList<CharSequence> = ArrayList()
            for (storage in storageList) {
                if (storage.type == MassStorageInfo.StorageType.USB && storage.isMounted) {
                    entries.add(storage.name)
                    entriesValues.add(storage.externalFileDirectory)
                }
            }
            pref.entries = entries.toTypedArray()
            pref.entryValues = entriesValues.toTypedArray()
            if (entries.isNotEmpty()) {
                val defaultValue = entriesValues[0] as String
                pref.setDefaultValue(defaultValue)
                pref.setValueIndex(0)
                pref.summary = "%s"
            }
        }
    }

    companion object {
        // Preferences keys for PrintAttributes
        const val PREF_COPIES = "pref_copies"
        const val PREF_COLOR_MODE = "pref_colorMode"
        const val PREF_DUPLEX_MODE = "pref_duplexMode"
        const val PREF_FILENAME = "pref_filename"
        const val PREF_USB_STORAGE = "pref_usb_storage"
        const val PREF_USB_FILENAME = "pref_usb_filename"
        const val PREF_STREAM_FILENAME = "pref_stream_filename"
        const val PREF_AUTOFIT = "pref_autoFit"
        const val PREF_STAPLE_MODE = "pref_stapleMode"
        const val PREF_COLLATE_MODE = "pref_collateMode"
        const val PREF_PAPER_SOURCE = "pref_paperSource"
        const val PREF_PAPER_SIZE = "pref_paperSize"
        const val PREF_PAPER_TYPE = "pref_paperType"
        const val PREF_DOC_FORMAT = "pref_documentFormat"
        const val PREF_SOURCE = "pref_source"
        const val PREF_URI = "pref_uri"
        const val PREF_URI_USERNAME = "pref_uri_username"
        const val PREF_URI_PASSWORD = "pref_uri_password"
        const val PREF_ORIENTATION = "pref_orientation"
        const val PREF_PRINT_QUALITY = "pref_print_quality"
        const val PREF_OUTPUT_BIN = "pref_output_bin"
        const val PREF_START_PAGE_RANGES = "pref_start_pageRanges"
        const val PREF_END_PAGE_RANGES = "pref_end_pageRanges"
        const val PREF_FINISHINGS = "pref_finishings"
        const val PREF_JOB_NAME = "pref_jobName"
        const val PREF_COPY_TEST_PAGE = "pref_copy_test_page"
        const val PREF_STREAM_COPY_TEST_PAGE = "pref_stream_copy_test_page"
        const val PREF_SOURCE_STORAGE_CATEGORY = "source_storage_category"
        const val PREF_SOURCE_HTTP_CATEGORY = "source_http_category"
        const val PREF_SOURCE_USB_CATEGORY = "source_usb_category"
        const val PREF_SOURCE_STREAM_CATEGORY = "source_stream_category"

        // Feedback settings
        const val PREF_MONITORING_JOB = "pref_monitoringJob"
        const val PREF_SHOW_JOB_PROGRESS = "pref_showJobProgress"
        const val PREF_SHOW_SETTINGS = "pref_showSettings"

        // Preference for current job id
        const val CURRENT_JOB_ID = "pref_currentJobId"
        const val FILE_BROWSER_REQUEST_CODE = 1
    }
}