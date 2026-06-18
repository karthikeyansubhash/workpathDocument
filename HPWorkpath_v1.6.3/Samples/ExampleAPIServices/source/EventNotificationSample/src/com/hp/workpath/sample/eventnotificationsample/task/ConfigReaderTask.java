// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.eventnotificationsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.sample.eventnotificationsample.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Worker task for configuration request
 */
public class ConfigReaderTask {

    private final WeakReference<Context> mContextRef;
    private final WeakReference<ResultHandler> mConfigResultHandlerRef;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ConfigReaderTask(final Context context, final ResultHandler resultHandler) {
        this.mContextRef = new WeakReference<>(context);
        this.mConfigResultHandlerRef = new WeakReference<>(resultHandler);
        this.mResult = new Result();
    }

    public void taskExecute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // call ConfigService API to retrieve configuration for the application
                    onPostExecute(ConfigService.getDefaultConfig(mContextRef.get(), mResult));
                } catch (Throwable t) {
                    Logger.showResult(null, "ConfigService.getDefaultConfig is failed:" + t.getMessage());
                    executor.shutdown();
                }
            }
        });
    }

    private void onPostExecute(JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (jsonObject != null && mResult.getCode() == Result.RESULT_OK) {
                    try {
                        mConfigResultHandlerRef.get().handleUpdate(jsonObject.toString(4));
                    } catch (JSONException e) {
                        mConfigResultHandlerRef.get().handleException(e);
                    }
                }
            }
        });
    }
}

