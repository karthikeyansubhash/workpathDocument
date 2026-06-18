// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.hp.workpath.api.accessory.hid.HIDInfo
import com.hp.workpath.api.accessory.hid.HIDReportType
import com.hp.workpath.sample.accessorysample.R

/**
 * Simple [PreferenceFragmentCompat] to set Accessory Attributes and save into preferences.
 */
class AccessoryReportsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    private var mReportDataPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.accessory_reports_preferences)
        cleanPreference()
        mReportDataPref = findPreference(PREF_REPORT_DATA)
        mReportDataPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            EditTextCheckboxPreference().show(parentFragmentManager, requireActivity().getString(R.string.pref_report_data))
            false
        }
        fillListPreferenceValues(PREF_REPORT_TYPE, HIDReportType.values(), HIDReportType.INPUT.name)
    }

    private fun fillListPreferenceValues(prefName: String, values: Array<HIDReportType>, defaultValue: String) {
        val pref = findPreference<Preference>(prefName) as ListPreference
        val cmEntries = ArrayList<CharSequence>()
        val cmEntryValues = ArrayList<CharSequence>()

        for (value in values) {
            cmEntries.add(value.toString())
            cmEntryValues.add(value.toString())
        }

        pref.entries = cmEntries.toTypedArray()
        pref.entryValues = cmEntryValues.toTypedArray()
        pref.setDefaultValue(defaultValue)
        pref.setValueIndex(0)
        pref.summary = "%s"
    }

    override fun onResume() {
        super.onResume()
        val prefs = preferenceScreen.sharedPreferences
        prefs.registerOnSharedPreferenceChangeListener(this)
        refreshAllPrefs(prefs)
    }

    private fun refreshAllPrefs(prefs: SharedPreferences) {
        onSharedPreferenceChanged(prefs, PREF_REPORT_TYPE)
        onSharedPreferenceChanged(prefs, PREF_REPORT_DATA)
    }

    override fun onPause() {
        super.onPause()
        val prefs = preferenceScreen.sharedPreferences
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)
        if (preference is ListPreference) {
            val entry = preference.entry
            if (entry == null || entry.isEmpty()) {
                preference.setValueIndex(0)
                preference.setSummary("%s")
            } else {
                preference.setSummary(entry)
            }
        }
        if (PREF_REPORT_DATA == key) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val path = prefs.getString(PREF_REPORT_DATA, null)
            preference?.summary = path
        }
    }

    /**
     * Set report data string of correct length for output report
     *
     * @param hidInfo HID info
     */
    fun setInfo(hidInfo: HIDInfo) {
        val outputData = StringBuilder()
        for (i in 0 until hidInfo.outputReportLength) {
            outputData.append("00")
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        prefs.edit().putString(PREF_REPORT_DATA, outputData.toString()).apply()
        onSharedPreferenceChanged(preferenceScreen.sharedPreferences, PREF_REPORT_DATA)
    }

    private fun cleanPreference() {
        val prefs = preferenceScreen.sharedPreferences
        prefs.edit().putString(PREF_REPORT_DATA, "").apply()
    }

    companion object {
        const val PREF_REPORT_TYPE = "pref_report_type"
        const val PREF_REPORT_DATA = "pref_report_data"
    }
}