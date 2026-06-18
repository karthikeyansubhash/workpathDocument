// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.hp.workpath.api.scanner.SmtpAttributes
import com.hp.workpath.sample.scansample.MainActivity
import com.hp.workpath.sample.scansample.R
import com.hp.workpath.sample.scansample.databinding.FragmentEmailSmtpSettingBinding

class EmailSmtpSettingFragment : DialogFragment() {
    //    private lateinit var mBindingEmailSmtpSetting: FragmentEmailSmtpSettingBinding
    private var mBindingFragment: FragmentEmailSmtpSettingBinding? = null

    private val mBindingEmailSmtpSetting get() = mBindingFragment!!

    private val containerView: View by lazy {
        mBindingFragment = FragmentEmailSmtpSettingBinding.inflate(layoutInflater)
        mBindingEmailSmtpSetting.root
    }

    private val mOKListener = DialogInterface.OnClickListener { dialog, _ ->
        try {
            saveSmtpSettings()
            setEnableSmtpPreference(true)
            dialog.dismiss()
        } catch (t: Throwable) {
            Log.e(TAG, "SMTP Settings ${t.message}")
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ ->
        setEnableSmtpPreference(false)
        dialog.cancel()
    }

    private val isValidSmtpSettings: Boolean
        @Throws(Exception::class)
        get() {
            val MINIMUM_PORT = 1
            val MAXIMUM_PORT = 65535
            val MINIMUM_TIMEOUT = 1
            val MAXIMUM_TIMEOUT = 300

            val hostname = mBindingEmailSmtpSetting.hostnameEditText.text.toString()
            val port = mBindingEmailSmtpSetting.portEditText.text.toString()
            val connectionTimeout = mBindingEmailSmtpSetting.connectionTimeoutEditText.text.toString()
            val readTimeout = mBindingEmailSmtpSetting.readTimeoutEditText.text.toString()

            if (TextUtils.isEmpty(hostname)) {
                throw Throwable("hostname is empty")
            }

            if (TextUtils.isEmpty(port)) {
                throw Throwable("port is empty")
            } else {
                val portNum = Integer.parseInt(port)
                if (portNum !in MINIMUM_PORT..MAXIMUM_PORT) {
                    throw Throwable(getString(R.string.range_smtp_port, MINIMUM_PORT, MAXIMUM_PORT))
                }
            }

            if (TextUtils.isEmpty(connectionTimeout)) {
                throw Throwable("connectionTimeout is empty")
            } else {
                val connectionTimeoutNum = Integer.parseInt(connectionTimeout)
                if (connectionTimeoutNum !in MINIMUM_TIMEOUT..MAXIMUM_TIMEOUT) {
                    throw Throwable(getString(R.string.range_connection_timeout_port, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT))
                }
            }

            if (TextUtils.isEmpty(readTimeout)) {
                throw Throwable("readTimeout is empty")
            } else {
                val readTimeoutNum = Integer.parseInt(readTimeout)
                if (readTimeoutNum !in MINIMUM_TIMEOUT..MAXIMUM_TIMEOUT) {
                    throw Throwable(getString(R.string.range_read_timeout_port, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT))
                }
            }
            return true
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setEnableSmtpPreference(false)
        return AlertDialog.Builder(requireActivity(), R.style.DialogTheme).apply {
            setTitle(R.string.pref_email_smtp_title)
            setView(containerView)
            setPositiveButton(android.R.string.ok, mOKListener)
            setNegativeButton(android.R.string.cancel, mCancelListener)
            setCancelable(false)
        }.create()
    }

    override fun getView() = containerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        findViewElements()
        loadSmtpSettings()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun findViewElements() {
        val transportModeList = SmtpAttributes.TransportMode.values()

        for (transportMode in transportModeList) {
            val radioButton = AppCompatRadioButton(activity)
            radioButton.text = transportMode.name
            mBindingEmailSmtpSetting.transportModeRadioGroup.addView(radioButton)
        }
    }

    @Throws(Throwable::class)
    private fun saveSmtpSettings() {
        if (isValidSmtpSettings) {
            val hostname = mBindingEmailSmtpSetting.hostnameEditText.text.toString()
            val port = Integer.parseInt(mBindingEmailSmtpSetting.portEditText.text.toString())
            val connectionTimeout = Integer.parseInt(mBindingEmailSmtpSetting.connectionTimeoutEditText.text.toString())
            val readTimeout = Integer.parseInt(mBindingEmailSmtpSetting.readTimeoutEditText.text.toString())
            val username = mBindingEmailSmtpSetting.usernameEditText.text.toString()
            val password = mBindingEmailSmtpSetting.passwordEditText.text.toString()
            val domain = mBindingEmailSmtpSetting.domainEditText.text.toString()

            val radioButtonID = mBindingEmailSmtpSetting.transportModeRadioGroup.checkedRadioButtonId
            val radioButton = mBindingEmailSmtpSetting.transportModeRadioGroup.findViewById<RadioButton>(radioButtonID)
            val transportMode = radioButton.text.toString()

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
            sharedPref.edit().apply {
                putString(requireActivity().getString(R.string.pref_email_hostname), hostname)
                putInt(requireActivity().getString(R.string.pref_email_port), port)
                putInt(requireActivity().getString(R.string.pref_email_connection_timeout), connectionTimeout)
                putInt(requireActivity().getString(R.string.pref_email_read_timeout), readTimeout)
                putString(requireActivity().getString(R.string.pref_email_username), username)
                putString(requireActivity().getString(R.string.pref_email_password), password)
                putString(requireActivity().getString(R.string.pref_email_domain), domain)
                putString(requireActivity().getString(R.string.pref_email_transport_mode), transportMode)
            }.apply()
        }
    }

    private fun loadSmtpSettings() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        val hostname = sharedPref.getString(requireActivity().getString(R.string.pref_email_hostname), "")
        val port = sharedPref.getInt(requireActivity().getString(R.string.pref_email_port), DEFAULT_PORT)
        val connectionTimeout = sharedPref.getInt(requireActivity().getString(R.string.pref_email_connection_timeout), DEFAULT_TIMEOUT)
        val readTimeout = sharedPref.getInt(requireActivity().getString(R.string.pref_email_read_timeout), DEFAULT_TIMEOUT)
        val username = sharedPref.getString(requireActivity().getString(R.string.pref_email_username), "")
        val password = sharedPref.getString(requireActivity().getString(R.string.pref_email_password), "")
        val domain = sharedPref.getString(requireActivity().getString(R.string.pref_email_domain), "")
        val transportMode = sharedPref.getString(requireActivity().getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name)

        mBindingEmailSmtpSetting.hostnameEditText.setText(hostname)
        mBindingEmailSmtpSetting.portEditText.setText(Integer.toString(port))
        mBindingEmailSmtpSetting.connectionTimeoutEditText.setText(Integer.toString(connectionTimeout))
        mBindingEmailSmtpSetting.readTimeoutEditText.setText(Integer.toString(readTimeout))
        mBindingEmailSmtpSetting.usernameEditText.setText(username)
        mBindingEmailSmtpSetting.passwordEditText.setText(password)
        mBindingEmailSmtpSetting.domainEditText.setText(domain)

        for (i in 0 until mBindingEmailSmtpSetting.transportModeRadioGroup.childCount) {
            val radio = mBindingEmailSmtpSetting.transportModeRadioGroup.getChildAt(i) as RadioButton
            if (radio.text == transportMode) {
                radio.isChecked = true
            }
        }
    }

    private fun setEnableSmtpPreference(value: Boolean) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
        val editor = sharedPref.edit()
        editor.putBoolean(ScanConfigureFragment.PREF_EMAIL_SMTP, value)
        editor.apply()
    }

    companion object {
        private const val TAG = MainActivity.TAG

        const val DEFAULT_PORT = 25
        const val DEFAULT_TIMEOUT = 60
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }
}