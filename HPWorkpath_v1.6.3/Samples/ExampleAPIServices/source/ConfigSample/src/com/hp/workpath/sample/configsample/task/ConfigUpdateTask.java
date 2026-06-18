// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.sample.configsample.Logger;
import com.hp.workpath.sample.configsample.MainActivity;
import com.hp.workpath.sample.configsample.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigUpdateTask {

    private final WeakReference<MainActivity> mContextRef;
    private final String value;
    private Throwable mThrowable = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ConfigUpdateTask(final MainActivity context, final String value) {
        this.mContextRef = new WeakReference<>(context);
        this.value = value;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result result = new Result();
                        if (mContextRef.get() != null) {
                            // convert string value to json object
                            JSONObject json = new JSONObject(value);

                            // call ConfigService API to set new configuration value for current application
                            result = ConfigService.setDefaultConfig(mContextRef.get(), json);
                        }
                        onPostExecute(result);
                    } catch (Throwable t) {
                        mThrowable = t;
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            mThrowable = e;
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final Result result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                mContextRef.get().setConfigComplete();
                if (result != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.success));
                } else if (mThrowable != null) {
                    Logger.showResult(mContextRef.get(), "ConfigService.setDefaultConfig " + mThrowable.getMessage());
                } else {
                    Logger.showResult(mContextRef.get(), "ConfigService.setDefaultConfig", result);
                }
            }
        });
    }

    public void cancel() {
        executor.shutdown();
    }
}

