// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.helper.email.EmailAddressInfo;
import com.hp.workpath.api.helper.email.EmailAttributes;

import java.util.List;

import androidx.multidex.BuildConfig;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(EmailAttributes attributes) {
        if (attributes != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("attachmentList").append(_EQ).append(attributes.getAttachments()).append(_C)
                    .append(_NF).append("ccList").append(_EQ).append(build(attributes.getCc())).append(_C)
                    .append(_NF).append("bccList").append(_EQ).append(build(attributes.getBcc())).append(_C)
                    .append(_NF).append("from").append(_EQ).append((attributes.getFrom() != null) ? attributes.getFrom().getName() : "").append(_C)
                    .append(_NF).append("message").append(_EQ).append(attributes.getMessage()).append(_C)
                    .append(_NF).append("subject").append(_EQ).append(attributes.getSubject()).append(_C)
                    .append(_NF).append("toList").append(_EQ).append(build(attributes.getTo()))
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    private static String build(List<EmailAddressInfo> infos) {
        if (infos != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START);

            String delim = "";
            for (EmailAddressInfo info : infos) {
                logBuilder.append(delim).append(info.getName());
                delim = _C;
            }
            logBuilder.append(_END);
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(Result result) {
        String code = (Result.RESULT_OK == result.getCode())? "RESULT_OK" : "RESULT_FAIL";
        StringBuilder builder = new StringBuilder()
                .append(_START)
                .append(_NF).append("Code:").append(code);
        if (Result.RESULT_OK != result.getCode() && result.getErrorCode() != null) {
            builder.append(_C).append(_NF).append("ErrorCode:").append(result.getErrorCode());
        }
        if (!TextUtils.isEmpty(result.getCause())) {
            builder.append(_C).append(_NF).append("Cause:").append(result.getCause());
        }
        builder.append(_NF).append(_END);
        return builder.toString();
    }

    public static void showResult(Activity activity, String msg) {
        showResult(activity, msg, null);
    }

    public static void showResult(Activity activity, String msg, Result result) {
        if (result != null) {
            msg = msg + Logger._NF + Logger.build(result);
            if (result.getCode() == Result.RESULT_FAIL) {
                Log.e(MainActivity.TAG, msg);
            } else {
                Log.d(MainActivity.TAG, msg);
            }
        } else {
            Log.d(MainActivity.TAG, msg);
        }
        if (activity != null && !activity.isFinishing()) {
            showToastMessage(activity, msg);
        }
    }

    private static void showToastMessage(Activity activity, String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
