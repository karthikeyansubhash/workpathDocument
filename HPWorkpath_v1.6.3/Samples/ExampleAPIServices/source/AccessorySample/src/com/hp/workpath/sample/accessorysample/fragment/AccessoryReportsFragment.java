// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.accessory.hid.HIDInfo;
import com.hp.workpath.api.accessory.hid.HIDReportType;
import com.hp.workpath.sample.accessorysample.R;

import java.util.ArrayList;

/**
 * Simple {@link PreferenceFragmentCompat} to set Accessory Attributes and save into preferences.
 */
public final class AccessoryReportsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_REPORT_TYPE = "pref_report_type";
    public static final String PREF_REPORT_DATA = "pref_report_data";

    private Preference mReportDataPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.accessory_reports_preferences);
        cleanPreference();
        mReportDataPref = findPreference(PREF_REPORT_DATA);
        mReportDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new EditTextCheckboxPreference().show(getParentFragmentManager(), getString(R.string.pref_report_data));
                return false;
            }
        });

        fillListPreferenceValues(PREF_REPORT_TYPE, HIDReportType.values(), HIDReportType.INPUT.name());
    }

    private void fillListPreferenceValues(final String prefName, final Object[] values, final String defaultValue) {
        ListPreference pref = (ListPreference) findPreference(prefName);
        ArrayList<CharSequence> cmEntries = new ArrayList<>();
        ArrayList<CharSequence> cmEntryValues = new ArrayList<>();

        for (Object val : values) {
            cmEntries.add(val.toString());
            cmEntryValues.add(val.toString());
        }

        pref.setEntries(cmEntries.toArray(new CharSequence[0]));
        pref.setEntryValues(cmEntryValues.toArray(new CharSequence[0]));
        pref.setDefaultValue(defaultValue);
        pref.setValueIndex(0);
        pref.setSummary("%s");
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        refreshAllPrefs(prefs);
    }

    private void refreshAllPrefs(final SharedPreferences prefs) {
        onSharedPreferenceChanged(prefs, PREF_REPORT_TYPE);
        onSharedPreferenceChanged(prefs, PREF_REPORT_DATA);
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
        if (preference instanceof ListPreference) {
            String entry = (String) ((ListPreference) preference).getEntry();
            if (entry == null || entry.length() == 0) {
                ((ListPreference) preference).setValueIndex(0);
                preference.setSummary("%s");
            } else {
                preference.setSummary(entry);
            }
        }

        if (PREF_REPORT_DATA.equals(key)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String path = prefs.getString(PREF_REPORT_DATA, null);
            preference.setSummary(path);
        }
    }

    /**
     * Set report data string of correct length for output report
     *
     * @param hidInfo HID info
     */
    public void setInfo(HIDInfo hidInfo) {
        StringBuilder outputData = new StringBuilder();
        for (int i = 0; i < hidInfo.getOutputReportLength(); i++) {
            outputData.append("00");
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putString(PREF_REPORT_DATA, outputData.toString()).apply();

        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), PREF_REPORT_DATA);
    }

    public void cleanPreference() {
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.edit().putString(PREF_REPORT_DATA, "").apply();
    }
}
