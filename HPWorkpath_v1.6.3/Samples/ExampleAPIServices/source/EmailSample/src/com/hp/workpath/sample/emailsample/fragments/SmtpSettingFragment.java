// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.helper.email.SmtpAttributes;
import com.hp.workpath.sample.emailsample.EmailDialog;
import com.hp.workpath.sample.emailsample.Logger;
import com.hp.workpath.sample.emailsample.MainActivity;
import com.hp.workpath.sample.emailsample.R;
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class SmtpSettingFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;

    public static final int DEFAULT_PORT = 25;
    public static final int DEFAULT_TIMEOUT = 60;

    private IDialogFragmentListener mListener;

    private EditText mHostnameEditText;
    private EditText mPortEditText;
    private EditText mConnectionTimeoutEditText;
    private EditText mReadTimeoutEditText;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mDomainEditText;
    private RadioGroup mTransportModeRadioGroup;

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_smtp_setting, null);
        findViewElements(view);
        loadSmtpSettings();
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle(R.string.smtp)
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
        mHostnameEditText = view.findViewById(R.id.hostnameEditText);
        mPortEditText = view.findViewById(R.id.portEditText);
        mConnectionTimeoutEditText = view.findViewById(R.id.connectionTimeoutEditText);
        mReadTimeoutEditText = view.findViewById(R.id.readTimeoutEditText);
        mUsernameEditText = view.findViewById(R.id.usernameEditText);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        mDomainEditText = view.findViewById(R.id.domainEditText);
        mTransportModeRadioGroup = view.findViewById(R.id.transportModeRadioGroup);

        ArrayList<SmtpAttributes.TransportMode> transportModeList =
                new ArrayList<>(EnumSet.allOf(SmtpAttributes.TransportMode.class));

        for (SmtpAttributes.TransportMode mode : transportModeList) {
            AppCompatRadioButton rButton = new AppCompatRadioButton(getActivity());
            rButton.setText(mode.name());
            mTransportModeRadioGroup.addView(rButton);
        }
    }

    private void saveSmtpSettings(String hostname, int port, int connectionTimeout, int readTimeout,
                                  String username, String password, String domain, String transportMode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.pref_email_hostname), hostname);
        editor.putInt(getString(R.string.pref_email_port), port);
        editor.putInt(getString(R.string.pref_email_connection_timeout), connectionTimeout);
        editor.putInt(getString(R.string.pref_email_read_timeout), readTimeout);
        editor.putString(getString(R.string.pref_email_username), username);
        editor.putString(getString(R.string.pref_email_password), password);
        editor.putString(getString(R.string.pref_email_domain), domain);
        editor.putString(getString(R.string.pref_email_transport_mode), transportMode);
        editor.putBoolean(getString(R.string.smtp), true);
        editor.apply();
    }

    private void loadSmtpSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String hostname = sharedPref.getString(getString(R.string.pref_email_hostname), "");
        int port = sharedPref.getInt(getString(R.string.pref_email_port), DEFAULT_PORT);
        int connectionTimeout = sharedPref.getInt(getString(R.string.pref_email_connection_timeout), DEFAULT_TIMEOUT);
        int readTimeout = sharedPref.getInt(getString(R.string.pref_email_read_timeout), DEFAULT_TIMEOUT);
        String username = sharedPref.getString(getString(R.string.pref_email_username), "");
        String password = sharedPref.getString(getString(R.string.pref_email_password), "");
        String domain = sharedPref.getString(getString(R.string.pref_email_domain), "");
        String transportMode = sharedPref.getString(getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name());

        mHostnameEditText.setText(hostname);
        mPortEditText.setText(Integer.toString(port));
        mConnectionTimeoutEditText.setText(Integer.toString(connectionTimeout));
        mReadTimeoutEditText.setText(Integer.toString(readTimeout));
        mUsernameEditText.setText(username);
        mPasswordEditText.setText(password);
        mDomainEditText.setText(domain);

        for (int idx = 0; idx < mTransportModeRadioGroup.getChildCount(); idx++) {
            AppCompatRadioButton radioButton = (AppCompatRadioButton) mTransportModeRadioGroup.getChildAt(idx);
            if (radioButton.getText().equals(transportMode)) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    private boolean isValidSmtpSettings(String hostname, int port, int connectionTimeout,
                                        int readTimeout) throws IllegalArgumentException {
        final int MINIMUM_PORT = 1;
        final int MAXIMUM_PORT = 65535;
        final int MINIMUM_TIMEOUT = 1;
        final int MAXIMUM_TIMEOUT = 300;

        if (TextUtils.isEmpty(hostname)) {
            throw new IllegalArgumentException(getString(R.string.hostname_empty));
        } else if (port < MINIMUM_PORT || port > MAXIMUM_PORT) {
            throw new IllegalArgumentException(getString(R.string.smtp_port_range_error, MINIMUM_PORT, MAXIMUM_PORT));
        } else if (connectionTimeout < MINIMUM_TIMEOUT || connectionTimeout > MAXIMUM_TIMEOUT) {
            throw new IllegalArgumentException(getString(R.string.connection_timeout_range_error, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT));
        } else if (readTimeout < MINIMUM_TIMEOUT || readTimeout > MAXIMUM_TIMEOUT) {
            throw new IllegalArgumentException(getString(R.string.read_timeout_range_error, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT));
        } else {
            return true;
        }
    }

    private DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                String hostname = mHostnameEditText.getText().toString();
                int port = Integer.parseInt(mPortEditText.getText().toString());
                int connectionTimeout = Integer.parseInt(mConnectionTimeoutEditText.getText().toString());
                int readTimeout = Integer.parseInt(mReadTimeoutEditText.getText().toString());
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String domain = mDomainEditText.getText().toString();

                int radioButtonID = mTransportModeRadioGroup.getCheckedRadioButtonId();
                AppCompatRadioButton radioButton = mTransportModeRadioGroup.findViewById(radioButtonID);
                String transportMode = radioButton.getText().toString();

                if (isValidSmtpSettings(hostname, port, connectionTimeout, readTimeout)) {
                    saveSmtpSettings(hostname, port, connectionTimeout, readTimeout,
                            username, password, domain, transportMode);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(EmailDialog.DIALOG_TYPE, EmailDialog.Type.SMTP);
                    mListener.onReturnValue(result);
                    getDialog().dismiss();
                }
            } catch (NumberFormatException nfe) {
                Logger.showResult(getActivity(), "NumberFormatException " + nfe);
            } catch (IllegalArgumentException iae) {
                Logger.showResult(getActivity(), "IllegalArgumentException " + iae);
            } catch (Throwable t) {
                Logger.showResult(getActivity(), "Unknown Throwable " + t);
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