// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization;

import android.text.TextUtils;

import androidx.multidex.BuildConfig;

import com.hp.workpath.api.Result;

public class Logger {
    private static final String _START = "[";
    private static final String _END = "]";
    private static final String _C = ",";
    private static final String _EQ = "=";
    public static final String _NF = (BuildConfig.DEBUG) ? "\n" : "";

    public static String build(Result result) {
        String code = (Result.RESULT_OK == result.getCode()) ? "RESULT_OK" : "RESULT_FAIL";
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
}
