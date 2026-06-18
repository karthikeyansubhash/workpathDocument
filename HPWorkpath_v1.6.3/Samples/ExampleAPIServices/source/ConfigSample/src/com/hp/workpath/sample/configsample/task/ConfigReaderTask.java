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

/**
 * Worker task for configuration request
 */
public class ConfigReaderTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;
    private boolean isUpdate;
    private Throwable mThrowable = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ConfigReaderTask(final MainActivity context, boolean isUpdate) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
        this.isUpdate = isUpdate;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // call ConfigService API to retrieve configuration for the application
                        onPostExecute(ConfigService.getDefaultConfig(mContextRef.get().getApplicationContext(), mResult));
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

    private void onPostExecute(JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                if (jsonObject != null && mResult.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.success));
                    mContextRef.get().getConfigComplete(jsonObject);
                    if (isUpdate) {
                        mContextRef.get().updatePrintOption(jsonObject);
                    }
                } else if (mThrowable != null) {
                    Logger.showResult(mContextRef.get(), "ConfigService.getDefaultConfig " + mThrowable.getMessage());
                } else {
                    Logger.showResult(mContextRef.get(), "ConfigService.getDefaultConfig", mResult);
                }
            }
        });
    }
}

