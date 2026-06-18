// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.authorization.EmailAddressInfo;
import com.hp.workpath.sample.authorization.DialogType;
import com.hp.workpath.sample.authorization.MainActivity;
import com.hp.workpath.sample.authorization.R;
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MailFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;
    private EditText mMailNameEditText;
    private EditText mMailAddressEditText;

    private DialogType.Email mMailType;

    private IDialogFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (IDialogFragmentListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context, context.getClass().getSimpleName()
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show();
            Log.e(TAG, context + " must implement IDialogFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mMailType = (DialogType.Email) args.getSerializable(DialogType.DIALOG_TYPE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_mail, null);
        findViewElements(view);
        String title = getResources().getString(R.string.add);
        switch (mMailType) {
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
                new AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
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

    private final DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String mailName = mMailNameEditText.getText().toString();
            String mailAddress = mMailAddressEditText.getText().toString();
            if (!TextUtils.isEmpty(mailAddress)) {
                HashMap<String, Object> result = new HashMap<>();
                EmailAddressInfo emailAddress = new EmailAddressInfo(mailAddress, mailName);
                result.put(DialogType.DIALOG_TYPE, mMailType);
                result.put(getString(R.string.email), emailAddress);
                mListener.onDialogResult(result);
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), getString(R.string.email_address_empty), Toast.LENGTH_LONG).show();
            }
        }
    };

    private final DialogInterface.OnClickListener mCancelListener = (dialog, which) -> dialog.cancel();

    public static void addMailView(ViewGroup viewGroup, final LinearLayout parent, final EmailAddressInfo mail, final ArrayList<EmailAddressInfo> mailList) {
        TextView textView = viewGroup.findViewById(R.id.textView);
        String mailAddress = mail.getAddress();
        String mailName = mail.getName();
        String mailViewText;
        if (TextUtils.isEmpty(mailName)) {
            mailViewText = mailAddress;
        } else {
            mailViewText = mailName + " <" + mailAddress + ">";
        }
        textView.setText(mailViewText);

        Button deleteButton = viewGroup.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            mailList.remove(mail);
            parent.removeView(viewGroup);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 10, 0);
        viewGroup.setLayoutParams(params);
        parent.addView(viewGroup);
    }
}