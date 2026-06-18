// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.api.authorization.ProxyConfiguration
import com.hp.workpath.sample.authorization.exception.ResultException
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

class GetConfigurationTask(context: Context) : Callable<ProxyConfiguration?> {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    @Throws(Exception::class)
    override fun call(): ProxyConfiguration? {
        val result = Result()
        val proxyConfiguration = contextRef.get()?.let { ctx ->
            AuthorizationService.getConfiguration(ctx, result)
        }
        if (result.code != Result.RESULT_OK) {
            throw ResultException(result)
        }
        return proxyConfiguration
    }
}