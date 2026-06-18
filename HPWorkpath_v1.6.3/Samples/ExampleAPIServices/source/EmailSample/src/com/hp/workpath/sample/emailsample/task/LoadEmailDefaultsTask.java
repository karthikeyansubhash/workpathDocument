// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.helper.email.Email;
import com.hp.workpath.api.helper.email.EmailAttributes;
import com.hp.workpath.sample.emailsample.Logger;
import com.hp.workpath.sample.emailsample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadEmailDefaultsTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadEmailDefaultsTask(final MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(Email.getDefaults(mContextRef.get(), mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "Email.getDefaults " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "Email.getDefaults " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final EmailAttributes defaults) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgressBar(View.GONE);
                if (defaults != null && mResult.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), "EmailAttributes=" + Logger.build(defaults));
                    mContextRef.get().setDefaultEmailAttributes(defaults);
                } else {
                    Logger.showResult(mContextRef.get(), "Email.getDefaults", mResult);
                }
            }
        });
    }
}