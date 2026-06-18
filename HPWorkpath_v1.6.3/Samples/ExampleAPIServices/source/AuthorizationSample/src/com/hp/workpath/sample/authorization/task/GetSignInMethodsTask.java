// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task;

import android.content.Context;
import android.text.TextUtils;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.authorization.AuthorizationService;
import com.hp.workpath.api.authorization.SignInMethod;
import com.hp.workpath.sample.authorization.R;
import com.hp.workpath.sample.authorization.exception.ResultException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class GetSignInMethodsTask implements Callable<ArrayList<SignInMethod>> {

    WeakReference<Context> context;
    String languageCode;

    public GetSignInMethodsTask(Context context, String languageCode) {
        this.context = new WeakReference<>(context);
        this.languageCode = languageCode;
    }

    @Override
    public ArrayList<SignInMethod> call() throws Exception {
        Result result = new Result();
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = context.get().getString(R.string.en_us);
        }
        ArrayList<SignInMethod> signInMethods = AuthorizationService.getSignInMethod(context.get(), result, languageCode);
        if (result.getCode() != Result.RESULT_OK) {
            throw new ResultException(result);
        }
        return signInMethods;
    }
}

