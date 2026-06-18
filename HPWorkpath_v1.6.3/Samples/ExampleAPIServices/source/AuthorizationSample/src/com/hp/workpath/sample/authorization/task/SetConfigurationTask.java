// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task;

import android.content.Context;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.authorization.AuthorizationService;
import com.hp.workpath.api.authorization.ProxyConfiguration;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public class SetConfigurationTask implements Callable<Result> {

    WeakReference<Context> context;
    ProxyConfiguration proxyConfiguration;

    public SetConfigurationTask(Context context, ProxyConfiguration proxyConfiguration) {
        this.context = new WeakReference<>(context);
        this.proxyConfiguration = proxyConfiguration;
    }

    @Override
    public Result call() throws Exception {
        Result result = new Result();
        AuthorizationService.setConfiguration(context.get(), result, this.proxyConfiguration);
        return result;
    }
}

