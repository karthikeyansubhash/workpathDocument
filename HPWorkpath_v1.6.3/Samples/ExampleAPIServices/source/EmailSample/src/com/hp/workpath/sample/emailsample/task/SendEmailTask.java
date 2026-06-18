// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.task;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.helper.email.Email;
import com.hp.workpath.api.helper.email.EmailAttributes;
import com.hp.workpath.api.helper.email.NetworkCredentialsAttributes;
import com.hp.workpath.api.helper.email.ProxyAttributes;
import com.hp.workpath.api.helper.email.SmtpAttributes;
import com.hp.workpath.sample.emailsample.Logger;
import com.hp.workpath.sample.emailsample.MainActivity;
import com.hp.workpath.sample.emailsample.R;
import com.hp.workpath.sample.emailsample.fragments.ProxySettingFragment;
import com.hp.workpath.sample.emailsample.fragments.SmtpSettingFragment;
import com.hp.workpath.sample.emailsample.model.EmailAddress;
import com.hp.workpath.sample.emailsample.model.EmailInfo;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendEmailTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;
    private final SharedPreferences mPrefs;

    private Result mResult;

    public enum SendType {
        SEND, SEND_WITH_SMTP, SEND_WITH_SMTP_PROXY
    }

    private SendType sendType;

    private EmailInfo emailInfo;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public SendEmailTask(final MainActivity context, SendType sendType, EmailInfo emailInfo) {
        this.mContextRef = new WeakReference<>(context);
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        this.mResult = new Result();
        this.sendType = sendType;
        this.emailInfo = emailInfo;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final boolean isSmtpEnabled = mPrefs.getBoolean(mContextRef.get().getString(R.string.smtp), false);
                        final boolean isProxyEnabled = mPrefs.getBoolean(mContextRef.get().getString(R.string.proxy), false);

                        SmtpAttributes smtpAttributes = null;
                        ProxyAttributes proxyAttributes = null;

                        if (isSmtpEnabled && sendType != SendType.SEND) {
                            String hostname = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_hostname), null);
                            int port = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_port), SmtpSettingFragment.DEFAULT_PORT);
                            int connectionTimeout = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_connection_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT);
                            int readTimeout = mPrefs.getInt(mContextRef.get().getString(R.string.pref_email_read_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT);
                            String username = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_username), null);
                            String password = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_password), null);
                            String domain = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_domain), null);
                            String transportModeString = mPrefs.getString(mContextRef.get().getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name());

                            NetworkCredentialsAttributes credentialsAttributes = null;
                            if (!TextUtils.isEmpty(username)) {
                                credentialsAttributes = new NetworkCredentialsAttributes.Builder()
                                        .setUserName(username)
                                        .setPassword(password)
                                        .setDomain(domain)
                                        .build();
                            }

                            SmtpAttributes.TransportMode transportMode = SmtpAttributes
                                    .TransportMode.valueOf(transportModeString);

                            smtpAttributes = new SmtpAttributes.Builder(hostname)
                                    .setPort(port)
                                    .setConnectTimeout(connectionTimeout)
                                    .setReadTimeout(readTimeout)
                                    .setServerCredentials(credentialsAttributes)
                                    .setTransportMode(transportMode)
                                    .build();
                        }

                        if (isProxyEnabled && sendType == SendType.SEND_WITH_SMTP_PROXY) {
                            String hostname = mPrefs.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, null);
                            int port = mPrefs.getInt(ProxySettingFragment.PREF_EMAIL_PROXY_PORT, ProxySettingFragment.DEFAULT_PORT);
                            String configurationModeString = mPrefs.getString(ProxySettingFragment.PREF_EMAIL_PROXY_CONFIG_MODE, ProxyAttributes.ProxyConfigurationMode.NONE.name());

                            ProxyAttributes.ProxyConfigurationMode configurationMode = ProxyAttributes
                                    .ProxyConfigurationMode.valueOf(configurationModeString);

                            proxyAttributes = new ProxyAttributes.Builder()
                                    .setHost(hostname)
                                    .setPort(port)
                                    .setProxyConfigurationMode(configurationMode)
                                    .build();
                        }

                        EmailAttributes.Builder emailAttrBuilder = new EmailAttributes.Builder()
                                .setFrom(emailInfo.getFrom().getAddress(), emailInfo.getFrom().getName())
                                .addAttachments(emailInfo.getAttachments())
                                .setSubject(emailInfo.getSubject())
                                .setMessage(emailInfo.getMessage());

                        if (emailInfo.getTo() != null && emailInfo.getTo().size() > 0) {
                            for (EmailAddress mail : emailInfo.getTo()) {
                                if (TextUtils.isEmpty(mail.getName())) {
                                    emailAttrBuilder.addToAddresses(mail.getAddress());
                                } else {
                                    emailAttrBuilder.addToAddress(mail.getAddress(), mail.getName());
                                }
                            }
                        }

                        if (emailInfo.getCc() != null && emailInfo.getCc().size() > 0) {
                            for (EmailAddress mail : emailInfo.getCc()) {
                                if (TextUtils.isEmpty(mail.getName())) {
                                    emailAttrBuilder.addCcAddresses(mail.getAddress());
                                } else {
                                    emailAttrBuilder.addCcAddress(mail.getAddress(), mail.getName());
                                }
                            }
                        }

                        if (emailInfo.getBcc() != null && emailInfo.getBcc().size() > 0) {
                            for (EmailAddress mail : emailInfo.getBcc()) {
                                if (TextUtils.isEmpty(mail.getName())) {
                                    emailAttrBuilder.addBccAddresses(mail.getAddress());
                                } else {
                                    emailAttrBuilder.addBccAddress(mail.getAddress(), mail.getName());
                                }
                            }
                        }

                        EmailAttributes emailAttributes = emailAttrBuilder.build();
                        Log.i(TAG, "EmailAttributes=" + Logger.build(emailAttributes));

                        switch (sendType) {
                            case SEND:
                                Email.send(mContextRef.get(), emailAttributes, mResult);
                                break;
                            case SEND_WITH_SMTP:
                                Email.send(mContextRef.get(), emailAttributes, smtpAttributes, mResult);
                                break;
                            case SEND_WITH_SMTP_PROXY:
                                Email.send(mContextRef.get(), emailAttributes, smtpAttributes, proxyAttributes, mResult);
                                break;
                        }
                    } catch (CapabilitiesExceededException cee) {
                        Logger.showResult(null, "Email.send " + cee.getMessage());
                    } catch (IllegalArgumentException iae) {
                        Logger.showResult(null, "Email.send " + iae.getMessage());
                    } catch (Throwable t) {
                        Logger.showResult(null, "Email.send " + t.getMessage());
                    }

                    onPostExecute(mResult);
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "Email.send " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final Result result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgressBar(View.GONE);
                if (result != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.succeed));
                } else {
                    Logger.showResult(mContextRef.get(), "Email.send", result);
                }
            }
        });
    }
}