// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.api.authorization.SignInMethod
import com.hp.workpath.sample.authorization.R
import com.hp.workpath.sample.authorization.exception.ResultException
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

class GetSignInMethodsTask(context: Context, private var languageCode: String?) :
    Callable<ArrayList<SignInMethod>?> {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    @Throws(Exception::class)
    override fun call(): ArrayList<SignInMethod>? {
        val result = Result()
        val context = contextRef.get()
        if (languageCode.isNullOrEmpty()) {
            languageCode = context?.getString(R.string.en_us) ?: ""
        }
        val signInMethods = context?.let { ctx ->
            AuthorizationService.getSignInMethod(ctx, result, languageCode ?: "")
        }
        if (result.code != Result.RESULT_OK) {
            throw ResultException(result)
        }
        return signInMethods
    }
}