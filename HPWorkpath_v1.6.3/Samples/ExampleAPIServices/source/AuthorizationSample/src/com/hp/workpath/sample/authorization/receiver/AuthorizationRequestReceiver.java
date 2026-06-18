// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.authorization.Logger;
import com.hp.workpath.sample.authorization.MainActivity;
import com.hp.workpath.sample.authorization.task.SetConfigurationUsingDefaultConfigTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AuthorizationRequestReceiver extends BroadcastReceiver {

    private static final String ACTION_AUTHORIZATION_REQUEST = "com.hp.workpath.action.AUTHORIZATION_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(MainActivity.TAG, "action: " + action);
        if (action != null && action.equals(ACTION_AUTHORIZATION_REQUEST)) {
            try {
                Workpath.getInstance().initialize(context);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<Result> future = executorService.submit(new SetConfigurationUsingDefaultConfigTask(context));
                Result result = future.get();
                Log.i(MainActivity.TAG, "AuthorizationRequestReceiver: " + Logger.build(result));
            } catch (Exception e) {
                Log.e(MainActivity.TAG, "AuthorizationRequestReceiver: " + e.getMessage());
            }
        }
    }
}
