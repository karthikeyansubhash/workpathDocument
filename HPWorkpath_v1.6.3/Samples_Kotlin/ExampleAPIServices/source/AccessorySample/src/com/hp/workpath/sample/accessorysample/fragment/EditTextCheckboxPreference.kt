// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.hp.workpath.sample.accessorysample.R

class EditTextCheckboxPreference : DialogFragment() {

    private lateinit var value: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.preference_text_checkbox, null)
        findViewElements(view)

        val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle(R.string.pref_report_data)
                .setView(view)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    private fun findViewElements(view: View) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        value = view.findViewById(R.id.value)
        value.setText(sharedPref.getString(AccessoryReportsFragment.PREF_REPORT_DATA, ""))
    }

    private val mOKListener = DialogInterface.OnClickListener { _, _ ->
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPref.edit()
        editor.putString(AccessoryReportsFragment.PREF_REPORT_DATA, value.text.toString()).apply()
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }
}