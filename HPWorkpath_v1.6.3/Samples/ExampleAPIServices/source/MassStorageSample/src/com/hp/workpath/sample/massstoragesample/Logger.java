// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.api.massstorage.MassStorageInfo;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(MassStorageInfo info) {
        if (info != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("externalFileDirectory").append(_EQ).append(info.getExternalFileDirectory()).append(_C)
                    .append(_NF).append("freeSpace").append(_EQ).append(info.getFreeSpace()).append(_C)
                    .append(_NF).append("name").append(_EQ).append(info.getName()).append(_C)
                    .append(_NF).append("protocol").append(_EQ).append(info.getProtocol()).append(_C)
                    .append(_NF).append("totalSpace").append(_EQ).append(info.getTotalSpace()).append(_C)
                    .append(_NF).append("type").append(_EQ).append(info.getType()).append(_C)
                    .append(_NF).append("volumeName").append(_EQ).append(info.getVolumeName()).append(_C)
                    .append(_NF).append("isMounted").append(_EQ).append(info.isMounted())
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

    public static String build(CustomerDataFile file) {
        if (file != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(_START)
                    .append(_NF).append("path").append(_EQ).append(file.getPath()).append(_C)
                    .append(_NF).append("packageName").append(_EQ).append(file.getPackageName()).append(_C)
                    .append(_NF).append("name").append(_EQ).append(file.getName()).append(_C)
                    .append(_NF).append("exists").append(_EQ).append(file.exists()).append(_C)
                    .append(_NF).append("isFile").append(_EQ).append(file.isFile()).append(_C)
                    .append(_NF).append("isDirectory").append(_EQ).append(file.isDirectory()).append(_C)
                    .append(_NF).append("lastModified").append(_EQ).append(file.lastModified()).append(_C)
                    .append(_NF).append("length").append(_EQ).append(file.length()).append(_C)
                    .append(_NF).append("storageInfo").append(_EQ).append(build(file.getStorageInfo()))
                    .append(_NF).append(_END);
            return logBuilder.toString();
        }
        return null;
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
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
