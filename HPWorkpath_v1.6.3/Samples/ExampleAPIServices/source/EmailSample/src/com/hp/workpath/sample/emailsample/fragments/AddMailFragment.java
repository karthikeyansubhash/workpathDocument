// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.sample.emailsample.EmailDialog;
import com.hp.workpath.sample.emailsample.MainActivity;
import com.hp.workpath.sample.emailsample.R;
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener;
import com.hp.workpath.sample.emailsample.model.EmailAddress;

import java.util.HashMap;

public class AddMailFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;
    private EditText mMailNameEditText;
    private EditText mMailAddressEditText;

    private EmailDialog.Type mAddMailType;

    private IDialogFragmentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            Toast.makeText(activity, activity.getClass().getSimpleName()
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show();
            Log.e(TAG, activity.toString() + " must implement IDialogFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mAddMailType = (EmailDialog.Type) args.getSerializable(EmailDialog.DIALOG_TYPE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_mail, null);
        findViewElements(view);
        String title = getResources().getString(R.string.add);
        switch (mAddMailType) {
            case ADD_TO:
                title += " (" + getResources().getString(R.string.to) + ")";
                break;
            case ADD_CC:
                title += " (" + getResources().getString(R.string.cc) + ")";
                break;
            case ADD_BCC:
                title += " (" + getResources().getString(R.string.bcc) + ")";
                break;
        }
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle(title)
                        .setView(view)
                        .setPositiveButton(android.R.string.ok, mOKListener)
                        .setNegativeButton(android.R.string.cancel, mCancelListener)
                        .setCancelable(false);
        return dialogBuilder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener == null) {
            dismiss();
        }
    }

    private void findViewElements(View view) {
        mMailNameEditText = view.findViewById(R.id.mailNameEditText);
        mMailAddressEditText = view.findViewById(R.id.mailAddrEditText);
    }

    private DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String mailName = mMailNameEditText.getText().toString();
            String mailAddress = mMailAddressEditText.getText().toString();
            if (!TextUtils.isEmpty(mailAddress)) {
                HashMap<String, Object> result = new HashMap<>();
                EmailAddress emailAddress = new EmailAddress(mailAddress, mailName);
                result.put(EmailDialog.DIALOG_TYPE, mAddMailType);
                result.put(getString(R.string.email), emailAddress);
                mListener.onReturnValue(result);
                getDialog().dismiss();
            } else {
                Toast.makeText(getActivity(), getString(R.string.email_address_empty),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
}