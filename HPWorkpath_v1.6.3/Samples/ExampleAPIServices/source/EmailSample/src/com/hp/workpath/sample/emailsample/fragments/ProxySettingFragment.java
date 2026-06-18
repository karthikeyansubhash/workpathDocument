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

import com.hp.workpath.api.helper.email.ProxyAttributes;
import com.hp.workpath.sample.emailsample.EmailDialog;
import com.hp.workpath.sample.emailsample.Logger;
import com.hp.workpath.sample.emailsample.MainActivity;
import com.hp.workpath.sample.emailsample.R;
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class ProxySettingFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;
    public static final String PREF_EMAIL_PROXY_HOST = "pref_email_proxy_host";
    public static final String PREF_EMAIL_PROXY_PORT = "pref_email_proxy_port";
    public static final String PREF_EMAIL_PROXY_CONFIG_MODE = "pref_email_proxy_config_mode";

    public static final int DEFAULT_PORT = 80;

    private IDialogFragmentListener mListener;

    private EditText mHostnameEditText;
    private EditText mPortEditText;
    private RadioGroup mConfigurationModeRadioGroup;

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
        View view = inflater.inflate(R.layout.dialog_proxy_setting, null);
        findViewElements(view);
        loadProxySettings();
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                        .setTitle(R.string.proxy)
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

        mHostnameEditText = view.findViewById(R.id.proxyHostnameEditText);
        mPortEditText = view.findViewById(R.id.proxyPortEditText);
        mConfigurationModeRadioGroup = view.findViewById(R.id.configurationModeRadioGroup);

        ArrayList<ProxyAttributes.ProxyConfigurationMode> configurationModeList =
                new ArrayList<>(EnumSet.allOf(ProxyAttributes.ProxyConfigurationMode.class));


        for (ProxyAttributes.ProxyConfigurationMode mode : configurationModeList) {
            AppCompatRadioButton rButton = new AppCompatRadioButton(getActivity());
            rButton.setText(mode.name());
            mConfigurationModeRadioGroup.addView(rButton);
        }
    }

    private void saveProxySettings(String hostname, int port, String configurationMode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(PREF_EMAIL_PROXY_HOST, hostname);
        editor.putInt(PREF_EMAIL_PROXY_PORT, port);
        editor.putString(PREF_EMAIL_PROXY_CONFIG_MODE, configurationMode);
        editor.putBoolean(getString(R.string.proxy), true);
        editor.apply();

    }

    private void loadProxySettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String hostname = sharedPref.getString(PREF_EMAIL_PROXY_HOST, "");
        int port = sharedPref.getInt(PREF_EMAIL_PROXY_PORT, DEFAULT_PORT);
        String configurationMode = sharedPref.getString(PREF_EMAIL_PROXY_CONFIG_MODE,
                ProxyAttributes.ProxyConfigurationMode.NONE.name());

        mHostnameEditText.setText(hostname);
        mPortEditText.setText(Integer.toString(port));

        for (int idx = 0; idx < mConfigurationModeRadioGroup.getChildCount(); idx++) {
            AppCompatRadioButton radioButton = (AppCompatRadioButton) mConfigurationModeRadioGroup.getChildAt(idx);
            if (radioButton.getText().equals(configurationMode)) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    private boolean isValidProxySettings(String hostname, int port) throws IllegalArgumentException {
        final int MINIMUM_PORT = 1;
        final int MAXIMUM_PORT = 65535;

        if (TextUtils.isEmpty(hostname)) {
            throw new IllegalArgumentException(getString(R.string.hostname_empty));
        } else if (port < MINIMUM_PORT || port > MAXIMUM_PORT) {
            throw new IllegalArgumentException(getString(R.string.proxy_port_range_error,
                    MINIMUM_PORT, MAXIMUM_PORT));
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
                int radioButtonID = mConfigurationModeRadioGroup.getCheckedRadioButtonId();
                AppCompatRadioButton radioButton = mConfigurationModeRadioGroup.findViewById(radioButtonID);
                String configurationMode = radioButton.getText().toString();

                if (isValidProxySettings(hostname, port)) {
                    saveProxySettings(hostname, port, configurationMode);

                    HashMap<String, Object> result = new HashMap<>();
                    result.put(EmailDialog.DIALOG_TYPE, EmailDialog.Type.PROXY);
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