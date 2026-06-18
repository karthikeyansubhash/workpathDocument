// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.preference.PreferenceManager
import com.hp.workpath.api.helper.email.SmtpAttributes
import com.hp.workpath.sample.emailsample.EmailDialog
import com.hp.workpath.sample.emailsample.Logger
import com.hp.workpath.sample.emailsample.MainActivity
import com.hp.workpath.sample.emailsample.R
import com.hp.workpath.sample.emailsample.databinding.DialogSmtpSettingBinding
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener
import java.lang.IllegalArgumentException
import java.util.EnumSet
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SmtpSettingFragment : AppCompatDialogFragment() {
    private lateinit var mListener: IDialogFragmentListener
    private lateinit var customView: View
    private var mBindingFragment: DialogSmtpSettingBinding? = null
    private val mBindingDialogSmtpSetting get() = mBindingFragment!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var activity: Activity? = null
        if (context is Activity) {
            activity = context
        }
        try {
            mListener = activity as IDialogFragmentListener
        } catch (e: ClassCastException) {
            Toast.makeText(activity, activity?.javaClass?.simpleName
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$activity must implement IDialogFragmentListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogSmtpSettingBinding.inflate(inflater)
        customView = mBindingDialogSmtpSetting.root
        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                .setTitle(R.string.smtp)
                .setView(customView)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return customView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTransportMode()
        loadSmtpSettings()
    }

    private fun setTransportMode() {
        val transportModeList = ArrayList(EnumSet.allOf(SmtpAttributes.TransportMode::class.java))
        for (mode in transportModeList) {
            val rButton = AppCompatRadioButton(activity)
            rButton.text = mode.name
            mBindingDialogSmtpSetting.transportModeRadioGroup.addView(rButton)
        }
    }

    private fun saveSmtpSettings(hostname: String, port: Int, connectionTimeout: Int, readTimeout: Int,
                                 username: String, password: String, domain: String, transportMode: String) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.pref_email_hostname), hostname)
        editor.putInt(getString(R.string.pref_email_port), port)
        editor.putInt(getString(R.string.pref_email_connection_timeout), connectionTimeout)
        editor.putInt(getString(R.string.pref_email_read_timeout), readTimeout)
        editor.putString(getString(R.string.pref_email_username), username)
        editor.putString(getString(R.string.pref_email_password), password)
        editor.putString(getString(R.string.pref_email_domain), domain)
        editor.putString(getString(R.string.pref_email_transport_mode), transportMode)
        editor.putBoolean(getString(R.string.smtp), true)
        editor.apply()
    }

    private fun loadSmtpSettings() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val hostname = sharedPref.getString(getString(R.string.pref_email_hostname), "")
        val port = sharedPref.getInt(getString(R.string.pref_email_port), DEFAULT_PORT)
        val connectionTimeout = sharedPref.getInt(getString(R.string.pref_email_connection_timeout), DEFAULT_TIMEOUT)
        val readTimeout = sharedPref.getInt(getString(R.string.pref_email_read_timeout), DEFAULT_TIMEOUT)
        val username = sharedPref.getString(getString(R.string.pref_email_username), "")
        val password = sharedPref.getString(getString(R.string.pref_email_password), "")
        val domain = sharedPref.getString(getString(R.string.pref_email_domain), "")
        val transportMode = sharedPref.getString(getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name)
        mBindingDialogSmtpSetting.hostnameEditText.setText(hostname)
        mBindingDialogSmtpSetting.portEditText.setText(port.toString())
        mBindingDialogSmtpSetting.connectionTimeoutEditText.setText(connectionTimeout.toString())
        mBindingDialogSmtpSetting.readTimeoutEditText.setText(readTimeout.toString())
        mBindingDialogSmtpSetting.usernameEditText.setText(username)
        mBindingDialogSmtpSetting.passwordEditText.setText(password)
        mBindingDialogSmtpSetting.domainEditText.setText(domain)
        for (idx in 0 until mBindingDialogSmtpSetting.transportModeRadioGroup.childCount) {
            val radioButton = mBindingDialogSmtpSetting.transportModeRadioGroup.getChildAt(idx) as AppCompatRadioButton
            if (radioButton.text == transportMode) {
                radioButton.isChecked = true
                break
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun isValidSmtpSettings(hostname: String, port: Int, connectionTimeout: Int,
                                    readTimeout: Int): Boolean {
        val MINIMUM_PORT = 1
        val MAXIMUM_PORT = 65535
        val MINIMUM_TIMEOUT = 1
        val MAXIMUM_TIMEOUT = 300
        return if (TextUtils.isEmpty(hostname)) {
            throw IllegalArgumentException(getString(R.string.hostname_empty))
        } else if (port < MINIMUM_PORT || port > MAXIMUM_PORT) {
            throw IllegalArgumentException(getString(R.string.smtp_port_range_error, MINIMUM_PORT, MAXIMUM_PORT))
        } else if (connectionTimeout < MINIMUM_TIMEOUT || connectionTimeout > MAXIMUM_TIMEOUT) {
            throw IllegalArgumentException(getString(R.string.connection_timeout_range_error, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT))
        } else if (readTimeout < MINIMUM_TIMEOUT || readTimeout > MAXIMUM_TIMEOUT) {
            throw IllegalArgumentException(getString(R.string.read_timeout_range_error, MINIMUM_TIMEOUT, MAXIMUM_TIMEOUT))
        } else {
            true
        }
    }

    private val mOKListener = DialogInterface.OnClickListener { _, _ ->
        try {
            val hostname = mBindingDialogSmtpSetting.hostnameEditText.text.toString()
            val port = mBindingDialogSmtpSetting.portEditText.text.toString().toInt()
            val connectionTimeout = mBindingDialogSmtpSetting.connectionTimeoutEditText.text.toString().toInt()
            val readTimeout = mBindingDialogSmtpSetting.readTimeoutEditText.text.toString().toInt()
            val username = mBindingDialogSmtpSetting.usernameEditText.text.toString()
            val password = mBindingDialogSmtpSetting.passwordEditText.text.toString()
            val domain = mBindingDialogSmtpSetting.domainEditText.text.toString()
            val radioButtonID = mBindingDialogSmtpSetting.transportModeRadioGroup.checkedRadioButtonId
            val radioButton: AppCompatRadioButton = mBindingDialogSmtpSetting.transportModeRadioGroup.findViewById(radioButtonID)
            val transportMode = radioButton.text.toString()
            if (isValidSmtpSettings(hostname, port, connectionTimeout, readTimeout)) {
                saveSmtpSettings(hostname, port, connectionTimeout, readTimeout,
                        username, password, domain, transportMode)
                val result = HashMap<String, Any>()
                result[EmailDialog.DIALOG_TYPE] = EmailDialog.Type.SMTP
                mListener.onReturnValue(result)
                dialog?.dismiss()
            }
        } catch (nfe: NumberFormatException) {
            Logger.showResult(activity, "NumberFormatException ${nfe.message}")
        } catch (iae: IllegalArgumentException) {
            Logger.showResult(activity, "IllegalArgumentException ${iae.message}")
        } catch (t: Throwable) {
            Logger.showResult(activity, "Unknown Exception ${t.message}")
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        private const val TAG = MainActivity.TAG
        const val DEFAULT_PORT = 25
        const val DEFAULT_TIMEOUT = 60
    }
}