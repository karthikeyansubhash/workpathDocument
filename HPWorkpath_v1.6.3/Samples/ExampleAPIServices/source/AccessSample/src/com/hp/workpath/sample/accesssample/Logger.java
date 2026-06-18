// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.Principal;

public class Logger {

    public static final boolean _DEBUG = BuildConfig.DEBUG;

    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(Principal principal) {
        if (principal != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("domain").append(_EQ).append(principal.getDomain()).append(_C)
                    .append(_NF).append("fullyQualifiedName").append(_EQ).append(principal.getFullyQualifiedName()).append(_C)
                    .append(_NF).append("principalID").append(_EQ).append(principal.getPrincipalId()).append(_C)
                    .append(_NF).append("provider").append(_EQ).append(principal.getProvider()).append(_C)
                    .append(_NF).append("providerUUID").append(_EQ).append(principal.getProviderUUID()).append(_C)
                    .append(_NF).append("email").append(_EQ).append(principal.getUserEmail()).append(_C)
                    .append(_NF).append("isAdmin").append(_EQ).append(principal.isAdmin()).append(_C)
                    .append(_NF).append("isAuthenticated").append(_EQ).append(principal.isAuthenticated()).append(_C)
                    .append(_NF).append("isAuthNAgentTrusted").append(_EQ).append(principal.isAuthNAgentTrusted()).append(_C)
                    .append(_NF).append("isDeviceUser").append(_EQ).append(principal.isDeviceUser()).append(_C)
                    .append(_NF).append("isGuestUser").append(_EQ).append(principal.isGuestUser()).append(_C)
                    .append(_NF).append("isHPCloudUser").append(_EQ).append(principal.isHPCloudUser()).append(_C)
                    .append(_NF).append("isServiceUser").append(_EQ).append(principal.isServiceUser()).append(_C)
                    .append(_NF).append("isSmartCardUser").append(_EQ).append(principal.isSmartCardUser()).append(_C)
                    .append(_NF).append("SimpleAuthorities").append(_EQ).append(principal.getSimpleAuthorities())
                    .append(_NF).append(_END);
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
