// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;

import java.util.Arrays;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(AccessoryInfo accessoryInfo) {
        if (accessoryInfo != null) {
            if (accessoryInfo instanceof HIDAccessoryInfo) {
                HIDAccessoryInfo hidAccessoryInfo = (HIDAccessoryInfo) accessoryInfo;
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append(_START)
                        .append("registrationType").append(_EQ).append(hidAccessoryInfo.getRegistrationType()).append(_C)
                        .append("PID").append(_EQ).append(hidAccessoryInfo.getProductId()).append(_C)
                        .append("VID").append(_EQ).append(hidAccessoryInfo.getVendorId()).append(_C)
                        .append("S/N").append(_EQ).append(hidAccessoryInfo.getSerialNumber())
                        .append(_END);
                return logBuilder.toString();
            }
        }
        return null;
    }

    public static String build(HIDAccessoryInfo hidAccessoryInfo) {
        if (hidAccessoryInfo != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START).append(hidAccessoryInfo.getRegistrationType()).append(_END)
                    .append("PID").append(_EQ).append(hidAccessoryInfo.getProductId()).append(_C)
                    .append("VID").append(_EQ).append(hidAccessoryInfo.getVendorId()).append(_C)
                    .append("S/N").append(_EQ).append(hidAccessoryInfo.getSerialNumber());
            return logBuilder.toString();
        }
        return null;
    }

    public static String build(HIDReport hidReport) {
        if (hidReport != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append("Type").append(_EQ).append(hidReport.getType()).append(_C)
                    .append("Data").append(_EQ).append(Arrays.toString(hidReport.getData()))
                    .append(_END);
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
