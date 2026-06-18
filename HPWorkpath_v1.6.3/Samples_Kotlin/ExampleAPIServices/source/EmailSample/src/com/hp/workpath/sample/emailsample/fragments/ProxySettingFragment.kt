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
import com.hp.workpath.api.helper.email.ProxyAttributes
import com.hp.workpath.sample.emailsample.EmailDialog
import com.hp.workpath.sample.emailsample.Logger
import com.hp.workpath.sample.emailsample.MainActivity
import com.hp.workpath.sample.emailsample.R
import com.hp.workpath.sample.emailsample.databinding.DialogProxySettingBinding
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener
import java.util.EnumSet
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProxySettingFragment : AppCompatDialogFragment() {
    private lateinit var mListener: IDialogFragmentListener
    private lateinit var customView: View
    private var mBindingFragment: DialogProxySettingBinding? = null
    private val mBindingDialogProxySetting get() = mBindingFragment!!

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
        mBindingFragment = DialogProxySettingBinding.inflate(inflater)
        customView = mBindingDialogProxySetting.root
        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
                .setTitle(R.string.proxy)
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
        loadProxySettings()
    }

    private fun setTransportMode() {
        val configurationModeList = ArrayList(EnumSet.allOf(ProxyAttributes.ProxyConfigurationMode::class.java))
        for (mode in configurationModeList) {
            val rButton = AppCompatRadioButton(activity)
            rButton.text = mode.name
            mBindingDialogProxySetting.configurationModeRadioGroup.addView(rButton)
        }
    }

    private fun saveProxySettings(hostname: String, port: Int, configurationMode: String) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPref.edit()
        editor.putString(PREF_EMAIL_PROXY_HOST, hostname)
        editor.putInt(PREF_EMAIL_PROXY_PORT, port)
        editor.putString(PREF_EMAIL_PROXY_CONFIG_MODE, configurationMode)
        editor.putBoolean(requireActivity().getString(R.string.proxy), true)
        editor.apply()
    }

    private fun loadProxySettings() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val hostname = sharedPref.getString(PREF_EMAIL_PROXY_HOST, "")
        val port = sharedPref.getInt(PREF_EMAIL_PROXY_PORT, DEFAULT_PORT)
        val configurationMode = sharedPref.getString(PREF_EMAIL_PROXY_CONFIG_MODE,
                ProxyAttributes.ProxyConfigurationMode.NONE.name)
        mBindingDialogProxySetting.proxyHostnameEditText.setText(hostname)
        mBindingDialogProxySetting.proxyPortEditText.setText(port.toString())
        for (idx in 0 until mBindingDialogProxySetting.configurationModeRadioGroup.childCount) {
            val radioButton = mBindingDialogProxySetting.configurationModeRadioGroup.getChildAt(idx) as AppCompatRadioButton
            if (radioButton.text == configurationMode) {
                radioButton.isChecked = true
                break
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun isValidProxySettings(hostname: String, port: Int): Boolean {
        val MINIMUM_PORT = 1
        val MAXIMUM_PORT = 65535
        return if (TextUtils.isEmpty(hostname)) {
            throw IllegalArgumentException(requireActivity().getString(R.string.hostname_empty))
        } else if (port < MINIMUM_PORT || port > MAXIMUM_PORT) {
            throw IllegalArgumentException(requireActivity().getString(R.string.proxy_port_range_error,
                    MINIMUM_PORT, MAXIMUM_PORT))
        } else {
            true
        }
    }

    private val mOKListener = DialogInterface.OnClickListener { _, _ ->
        try {
            val hostname = mBindingDialogProxySetting.proxyHostnameEditText.text.toString()
            val port = mBindingDialogProxySetting.proxyPortEditText.text.toString().toInt()
            val radioButtonID = mBindingDialogProxySetting.configurationModeRadioGroup.checkedRadioButtonId
            val radioButton: AppCompatRadioButton = mBindingDialogProxySetting.configurationModeRadioGroup.findViewById(radioButtonID)
            val configurationMode = radioButton.text.toString()
            if (isValidProxySettings(hostname, port)) {
                saveProxySettings(hostname, port, configurationMode)
                val result = HashMap<String, Any>()
                result[EmailDialog.DIALOG_TYPE] = EmailDialog.Type.PROXY
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
        const val PREF_EMAIL_PROXY_HOST = "pref_email_proxy_host"
        const val PREF_EMAIL_PROXY_PORT = "pref_email_proxy_port"
        const val PREF_EMAIL_PROXY_CONFIG_MODE = "pref_email_proxy_config_mode"
        const val DEFAULT_PORT = 80
    }
}