// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.events.DeviceEvent;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = "\n";

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

    public static String build(DeviceEvent deviceEvent) {
        StringBuilder builder = new StringBuilder()
                .append(_START)
                .append(_NF).append("Category:").append(deviceEvent.getCategory()).append(_C);
        String details = null;
        if (deviceEvent.getDetails() != null && deviceEvent.getDetails().length > 0) {
            details = deviceEvent.getDetails()[0];
        }
        builder.append(_NF).append("Detail:").append(details).append(_C)
                .append(_NF).append("EventCode:").append(deviceEvent.getEventCode()).append(_C)
                .append(_NF).append("InstanceId:").append(deviceEvent.getInstanceId()).append(_C)
                .append(_NF).append("Serverity:").append(deviceEvent.getSeverity()).append(_C)
                .append(_NF).append("StateChangeType:").append(deviceEvent.getStateChangeType()).append(_C);
        if (deviceEvent.getTimestamp() != null) {
            builder.append(_NF).append("Timestamp offset:").append(deviceEvent.getTimestamp().getOffset()).append(_C);
            builder.append(_NF).append("Timestamp time:").append(deviceEvent.getTimestamp().getTime()).append(_C);
        }
        builder.append(_NF).append("getTitle:").append(deviceEvent.getTitle())
                .append(_NF).append(_END);
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
