// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.sample.accessorysample.Logger
import com.hp.workpath.sample.accessorysample.R

/**
 * Simple [PreferenceFragmentCompat] to set Accessory Attributes and save into preferences.
 */
class AccessoryFragment : PreferenceFragmentCompat() {
    private var mAccessoryInfoPref: Preference? = null
    private var mAccessoryContextIdPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.accessory_preferences)
        mAccessoryInfoPref = findPreference(PREF_ACCESSORY_INFO)
        mAccessoryContextIdPref = findPreference(PREF_ACCESSORY_CONTEXT_ID)
    }

    /**
     * Updates accessory context id on screen
     *
     * @param accessoryContextId new accessory context id
     */
    fun updateAccessoryContextId(accessoryInfo: AccessoryInfo?, accessoryContextId: String?) {
        if (accessoryContextId != null) {
            mAccessoryContextIdPref?.summary = accessoryContextId
        } else {
            mAccessoryContextIdPref?.summary = requireActivity().getString(R.string.na)
        }
        if (accessoryInfo != null) {
            val hidAccessoryInfo = accessoryInfo.getDetails<HIDAccessoryInfo>()
            mAccessoryInfoPref?.summary = Logger.build(hidAccessoryInfo)
        } else {
            mAccessoryContextIdPref?.summary = requireActivity().getString(R.string.na)
            mAccessoryInfoPref?.summary = requireActivity().getString(R.string.na)
        }
    }

    companion object {
        // Preferences keys for accessories
        const val PREF_ACCESSORY_INFO = "pref_accessory_info"
        const val PREF_ACCESSORY_CONTEXT_ID = "pref_accessory_context_id"
        const val REQUEST_KEY = "result-listener-request-key"
        const val KEY_NUMBER = "key-number"
    }
}