// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.api.authorization.ProxyConfiguration
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

class SetConfigurationTask(context: Context, private val proxyConfiguration: ProxyConfiguration?) :
    Callable<Result> {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    @Throws(Exception::class)
    override fun call(): Result {
        val result = Result()
        contextRef.get()?.let { ctx ->
            AuthorizationService.setConfiguration(ctx, result, proxyConfiguration)
        }
        return result
    }
}