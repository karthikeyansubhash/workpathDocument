// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.sample.accessorysample.Logger;
import com.hp.workpath.sample.accessorysample.R;

/**
 * Simple {@link PreferenceFragmentCompat} to set Accessory Attributes and save into preferences.
 */
public final class AccessoryFragment extends PreferenceFragmentCompat {

    // Preferences keys for accessories
    public static final String PREF_ACCESSORY_INFO = "pref_accessory_info";
    public static final String PREF_ACCESSORY_CONTEXT_ID = "pref_accessory_context_id";

    private Preference mAccessoryInfoPref;
    private Preference mAccessoryContextIdPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.accessory_preferences);

        mAccessoryInfoPref = findPreference(PREF_ACCESSORY_INFO);
        mAccessoryContextIdPref = findPreference(PREF_ACCESSORY_CONTEXT_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Updates accessory context id on screen
     *
     * @param accessoryContextId new accessory context id
     */
    public void updateAccessoryContextId(AccessoryInfo accessoryInfo, String accessoryContextId) {
        if (accessoryContextId != null) {
            mAccessoryContextIdPref.setSummary(accessoryContextId);
        } else {
            mAccessoryContextIdPref.setSummary(getString(R.string.na));
        }

        if (accessoryInfo != null) {
            HIDAccessoryInfo hidAccessoryInfo = accessoryInfo.getDetails();
            mAccessoryInfoPref.setSummary(Logger.build(hidAccessoryInfo));
        } else {
            mAccessoryContextIdPref.setSummary(getString(R.string.na));
            mAccessoryInfoPref.setSummary(getString(R.string.na));
        }
    }
}
