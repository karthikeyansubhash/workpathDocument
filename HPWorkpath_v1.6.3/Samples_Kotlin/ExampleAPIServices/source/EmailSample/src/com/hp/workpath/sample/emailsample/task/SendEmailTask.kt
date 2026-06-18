// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.task

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.Result
import com.hp.workpath.api.helper.email.Email
import com.hp.workpath.api.helper.email.EmailAttributes
import com.hp.workpath.api.helper.email.SmtpAttributes
import com.hp.workpath.api.helper.email.ProxyAttributes
import com.hp.workpath.api.helper.email.NetworkCredentialsAttributes
import com.hp.workpath.sample.emailsample.Logger
import com.hp.workpath.sample.emailsample.Logger.build
import com.hp.workpath.sample.emailsample.MainActivity
import com.hp.workpath.sample.emailsample.R
import com.hp.workpath.sample.emailsample.fragments.ProxySettingFragment
import com.hp.workpath.sample.emailsample.fragments.SmtpSettingFragment
import com.hp.workpath.sample.emailsample.model.EmailInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class SendEmailTask(context: MainActivity, private val sendType: SendType, private val emailInfo: EmailInfo) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val mResult: Result = Result()
    private lateinit var mErrorMsg: String
    private var mThrowable: Throwable? = null

    enum class SendType {
        SEND, SEND_WITH_SMTP, SEND_WITH_SMTP_PROXY
    }

    suspend fun execute() {
        try {
            mContextRef.get()?.run {
                val isSmtpEnabled = mPrefs.getBoolean(getString(R.string.smtp), false)
                val isProxyEnabled = mPrefs.getBoolean(getString(R.string.proxy), false)
                var smtpAttributes: SmtpAttributes? = null
                var proxyAttributes: ProxyAttributes? = null
                if (isSmtpEnabled && sendType != SendType.SEND) {
                    val hostname = mPrefs.getString(getString(R.string.pref_email_hostname), null)
                    val port = mPrefs.getInt(getString(R.string.pref_email_port), SmtpSettingFragment.DEFAULT_PORT)
                    val connectionTimeout = mPrefs.getInt(getString(R.string.pref_email_connection_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT)
                    val readTimeout = mPrefs.getInt(getString(R.string.pref_email_read_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT)
                    val username = mPrefs.getString(getString(R.string.pref_email_username), null)
                    val password = mPrefs.getString(getString(R.string.pref_email_password), null)
                    val domain = mPrefs.getString(getString(R.string.pref_email_domain), null)
                    val transportModeString = mPrefs.getString(getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name)
                    var credentialsAttributes: NetworkCredentialsAttributes? = null
                    if (!TextUtils.isEmpty(username)) {
                        credentialsAttributes = NetworkCredentialsAttributes.Builder()
                                .setUserName(username)
                                .setPassword(password)
                                .setDomain(domain)
                                .build()
                    }
                    val transportMode = transportModeString?.let { SmtpAttributes.TransportMode.valueOf(it) }
                    smtpAttributes = transportMode?.let {
                        SmtpAttributes.Builder(hostname)
                                .setPort(port)
                                .setConnectTimeout(connectionTimeout)
                                .setReadTimeout(readTimeout)
                                .setServerCredentials(credentialsAttributes)
                                .setTransportMode(it)
                                .build()
                    }
                }
                if (isProxyEnabled && sendType == SendType.SEND_WITH_SMTP_PROXY) {
                    val hostname = mPrefs.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, null)
                    val port = mPrefs.getInt(ProxySettingFragment.PREF_EMAIL_PROXY_PORT, ProxySettingFragment.DEFAULT_PORT)
                    val configurationModeString = mPrefs.getString(ProxySettingFragment.PREF_EMAIL_PROXY_CONFIG_MODE, ProxyAttributes.ProxyConfigurationMode.NONE.name)
                    val configurationMode = configurationModeString?.let { ProxyAttributes.ProxyConfigurationMode.valueOf(it) }
                    proxyAttributes = hostname?.let {
                        ProxyAttributes.Builder()
                                .setHost(it)
                                .setPort(port)
                                .setProxyConfigurationMode(configurationMode)
                                .build()
                    }
                }

                val emailAttrBuilder = EmailAttributes.Builder()
                emailInfo.from?.run {
                    emailAttrBuilder.setFrom(address, name)
                            .setSubject(emailInfo.subject)
                            .setMessage(emailInfo.message)
                    emailInfo.attachments?.run {
                        emailAttrBuilder.addAttachments(*this)
                    }
                } ?: run {
                    throw IllegalArgumentException("emailInfo.from is empty")
                }

                emailInfo.to?.run {
                    for (mail in this) {
                        if (TextUtils.isEmpty(mail.name)) {
                            emailAttrBuilder.addToAddresses(mail.address)
                        } else {
                            emailAttrBuilder.addToAddress(mail.address, mail.name)
                        }
                    }
                }

                emailInfo.cc?.run {
                    for (mail in this) {
                        if (TextUtils.isEmpty(mail.name)) {
                            emailAttrBuilder.addCcAddresses(mail.address)
                        } else {
                            emailAttrBuilder.addCcAddress(mail.address, mail.name)
                        }
                    }
                }

                emailInfo.bcc?.run {
                    for (mail in this) {
                        if (TextUtils.isEmpty(mail.name)) {
                            emailAttrBuilder.addBccAddresses(mail.address)
                        } else {
                            emailAttrBuilder.addBccAddress(mail.address, mail.name)
                        }
                    }
                }

                val emailAttributes = emailAttrBuilder.build()
                Log.i(TAG, "EmailAttributes=" + build(emailAttributes))
                when (sendType) {
                    SendType.SEND -> Email.send(this, emailAttributes, mResult)
                    SendType.SEND_WITH_SMTP -> Email.send(this, emailAttributes, smtpAttributes, mResult)
                    SendType.SEND_WITH_SMTP_PROXY -> Email.send(this, emailAttributes, smtpAttributes, proxyAttributes, mResult)
                }
            }
        } catch (cee: CapabilitiesExceededException) {
            mErrorMsg = "CapabilitiesExceededException"
            mThrowable = cee
        } catch (iae: IllegalArgumentException) {
            mErrorMsg = "IllegalArgumentException"
            mThrowable = iae
        } catch (t: Throwable) {
            mErrorMsg = "Unknown Throwable"
            mThrowable = t
        }
        onPostExecute(mResult)
    }

    private suspend fun onPostExecute(result: Result?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                if (result != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, getString(R.string.succeed))
                } else if (mThrowable != null) {
                    Logger.showResult(this, "$mErrorMsg ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "Email.send", result)
                }
            }
        }
    }

    companion object {
        private const val TAG = MainActivity.TAG
    }
}