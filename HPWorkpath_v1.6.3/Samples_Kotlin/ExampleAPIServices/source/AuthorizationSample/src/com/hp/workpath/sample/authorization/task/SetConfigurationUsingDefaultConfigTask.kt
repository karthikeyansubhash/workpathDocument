// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hp.workpath.api.Result
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.api.authorization.ProxyConfiguration
import com.hp.workpath.api.config.ConfigService
import com.hp.workpath.sample.authorization.exception.ResultException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

class SetConfigurationUsingDefaultConfigTask(context: Context) : Callable<Result> {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    @Throws(Exception::class)
    override fun call(): Result {
        val result = Result()
        val context = contextRef.get()
        val jsonObject: JSONObject? = context?.let { ctx ->
            ConfigService.getDefaultConfig(ctx, result)
        }
        if (result.code != Result.RESULT_OK) {
            throw ResultException(result)
        }
        Log.i("[SAMPLE]authzC", "jsonObject: ${jsonObject.toString()}")
        try {
            val proxyConfiguration: ProxyConfiguration? = jsonObject?.let {
                Gson().fromJson(it.toString(), ProxyConfiguration::class.java)
            }
            context?.let { ctx ->
                AuthorizationService.setConfiguration(ctx, result, proxyConfiguration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}