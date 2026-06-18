// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task;

import android.content.Context;

import com.google.gson.Gson;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.authorization.AuthorizationService;
import com.hp.workpath.api.authorization.ProxyConfiguration;
import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.sample.authorization.exception.ResultException;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public class SetConfigurationUsingDefaultConfigTask implements Callable<Result> {

    WeakReference<Context> context;

    public SetConfigurationUsingDefaultConfigTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public Result call() throws Exception {
        Result result = new Result();
        JSONObject jsonObject = ConfigService.getDefaultConfig(context.get(), result);
        if (result.getCode() != Result.RESULT_OK) {
            throw new ResultException(result);
        }
        ProxyConfiguration proxyConfiguration = null;
        if (jsonObject != null) {
            proxyConfiguration = new Gson().fromJson(jsonObject.toString(), ProxyConfiguration.class);
        }
        AuthorizationService.setConfiguration(context.get(), result, proxyConfiguration);
        return result;
    }
}

