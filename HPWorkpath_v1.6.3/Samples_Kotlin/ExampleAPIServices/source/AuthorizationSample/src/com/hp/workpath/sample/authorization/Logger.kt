// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization

import android.text.TextUtils
import androidx.multidex.BuildConfig
import com.hp.workpath.api.Result

object Logger {
    private const val START = "["
    private const val END = "]"
    private const val COMMA = ","
    private const val EQUALS = "="
    private val NEW_LINE: String = if (BuildConfig.DEBUG) "\n" else ""

    fun build(result: Result): String {
        val builder = StringBuilder()
        builder.append(START)
            .append(NEW_LINE)
            .append("Code:")
            .append(if (Result.RESULT_OK == result.code) "RESULT_OK" else "RESULT_FAIL")

        if (Result.RESULT_OK != result.code && result.errorCode != null) {
            builder.append(COMMA)
                .append(NEW_LINE)
                .append("ErrorCode:")
                .append(result.errorCode)
        }

        if (!TextUtils.isEmpty(result.cause)) {
            builder.append(COMMA)
                .append(NEW_LINE)
                .append("Cause:")
                .append(result.cause)
        }

        builder.append(NEW_LINE).append(END)
        return builder.toString()
    }
}