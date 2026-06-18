// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task;

import android.content.Context;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.authorization.AuthorizationService;
import com.hp.workpath.api.authorization.ProxyConfiguration;
import com.hp.workpath.sample.authorization.Logger;
import com.hp.workpath.sample.authorization.exception.ResultException;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public class GetConfigurationTask implements Callable<ProxyConfiguration> {

    WeakReference<Context> context;

    public GetConfigurationTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public ProxyConfiguration call() throws Exception {
        Result result = new Result();
        ProxyConfiguration proxyConfiguration = AuthorizationService.getConfiguration(context.get(), result);
        if (result.getCode() != Result.RESULT_OK) {
            throw new ResultException(result);
        }
        return proxyConfiguration;
    }
}

