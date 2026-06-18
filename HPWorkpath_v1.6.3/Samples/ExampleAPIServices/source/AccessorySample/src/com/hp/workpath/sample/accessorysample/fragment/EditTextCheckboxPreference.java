// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.hp.workpath.sample.accessorysample.R;

public class EditTextCheckboxPreference extends DialogFragment {

    private EditText mValueText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.preference_text_checkbox, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_report_data)
                .setView(view)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        mValueText = view.findViewById(R.id.value);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mValueText.setText(sharedPref.getString(AccessoryReportsFragment.PREF_REPORT_DATA, ""));
    }

    private DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(AccessoryReportsFragment.PREF_REPORT_DATA, mValueText.getText().toString()).apply();
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}
