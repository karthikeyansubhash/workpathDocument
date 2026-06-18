// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.hp.workpath.api.scanner.SmtpAttributes;
import com.hp.workpath.sample.scansample.MainActivity;
import com.hp.workpath.sample.scansample.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment.PREF_EMAIL_SMTP;

public class EmailSmtpSettingFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;

    public static final int DEFAULT_PORT = 25;
    public static final int DEFAULT_TIMEOUT = 60;

    private EditText mHostnameEditText;
    private EditText mPortEditText;
    private EditText mConnectionTimeoutEditText;
    private EditText mReadTimeoutEditText;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mDomainEditText;
    private RadioGroup mTransportModeRadioGroup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_email_smtp_setting, null);
        findViewElements(view);
        loadSmtpSettings();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setTitle(R.string.pref_email_smtp_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        setEnableSmtpPreference(false);
        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewElements(view);
        loadSmtpSettings();
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

        List<SmtpAttributes.TransportMode> transportModeList =
                new ArrayList<>(EnumSet.allOf(SmtpAttributes.TransportMode.class));

        for (SmtpAttributes.TransportMode transportMode : transportModeList) {
            AppCompatRadioButton radioButton = new AppCompatRadioButton(getActivity());
            radioButton.setText(transportMode.name());
            mTransportModeRadioGroup.addView(radioButton);
        }
    }

    private DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                saveSmtpSettings();
                setEnableSmtpPreference(true);
                dialog.dismiss();
            } catch (Throwable t) {
                Log.e(TAG, "SMTP Settings " + t.getMessage());
            }
        }
    };

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setEnableSmtpPreference(false);
            dialog.cancel();
        }
    };

    private void saveSmtpSettings() throws Exception {
        if (isValidSmtpSettings()) {
            String hostname = mHostnameEditText.getText().toString();
            int port = Integer.parseInt(mPortEditText.getText().toString());
            int connectionTimeout = Integer.parseInt(mConnectionTimeoutEditText.getText().toString());
            int readTimeout = Integer.parseInt(mReadTimeoutEditText.getText().toString());
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            String domain = mDomainEditText.getText().toString();

            int radioButtonID = mTransportModeRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = mTransportModeRadioGroup.findViewById(radioButtonID);
            String transportMode = radioButton.getText().toString();

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
            editor.apply();
        }
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

        for (int i = 0; i < mTransportModeRadioGroup.getChildCount(); i++) {
            RadioButton radio = (RadioButton) mTransportModeRadioGroup.getChildAt(i);
            if (radio.getText().equals(transportMode)) {
                radio.setChecked(true);
            }
        }
    }

    private void setEnableSmtpPreference(boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(PREF_EMAIL_SMTP, value);
        editor.apply();
    }

    private boolean isValidSmtpSettings() throws Exception {
        final int MINIMUM_PORT = 1;
        final int MAXIMUM_PORT = 65535;
        final int MINIMUM_TIMEOUT = 1;
        final int MAXIMUM_TIMEOUT = 300;

        String hostname = mHostnameEditText.getText().toString();
        String port = mPortEditText.getText().toString();
        String connectionTimeout = mConnectionTimeoutEditText.getText().toString();
        String readTimeout = mReadTimeoutEditText.getText().toString();

        if (TextUtils.isEmpty(hostname)) {
            throw new Exception("hostname is empty");
        }

        if (TextUtils.isEmpty(port)) {
            throw new Exception("port is empty");
        } else {
            int portNum = Integer.parseInt(port);
            if (portNum < MINIMUM_PORT || portNum > MAXIMUM_PORT) {
                throw new Exception(getString(R.string.range_smtp_port, MINIMUM_PORT, MAXIMUM_PORT));
            }
        }

        if (TextUtils.isEmpty(connectionTimeout)) {
            throw new Exception("connectionTimeout is empty");
        } else {
            int connectionTimeoutNum = Integer.parseInt(connectionTimeout);
            if (connectionTimeoutNum < MINIMUM_TIMEOUT || connectionTimeoutNum > MAXIMUM_TIMEOUT) {
                throw new Exception(getString(R.string.range_connection_timeout_port, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT));
            }
        }

        if (TextUtils.isEmpty(readTimeout)) {
            throw new Exception("readTimeout is empty");
        } else {
            int readTimeoutNum = Integer.parseInt(readTimeout);
            if (readTimeoutNum < MINIMUM_TIMEOUT || readTimeoutNum > MAXIMUM_TIMEOUT) {
                throw new Exception(getString(R.string.range_read_timeout_port, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT));
            }
        }
        return true;
    }
}