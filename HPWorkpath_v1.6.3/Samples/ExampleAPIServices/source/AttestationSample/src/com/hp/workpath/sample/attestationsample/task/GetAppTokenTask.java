// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.attestationsample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.attestation.AppToken;
import com.hp.workpath.api.attestation.AttestationService;
import com.hp.workpath.sample.attestationsample.Logger;
import com.hp.workpath.sample.attestationsample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetAppTokenTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public GetAppTokenTask(final MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AppToken appToken = AttestationService.getAppToken(mContextRef.get(), result);
                        onPostExecute(appToken);
                    } catch (Throwable t) {
                        Logger.showResult(null, "AttestationService.getAppToken is failed:" + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "AttestationService.getAppToken is failed:" + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(AppToken appToken) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgressBar(View.GONE);

                Logger.showResult(mContextRef.get(), "AttestationService.getAppToken", result);
                mContextRef.get().getAppTokenComplete(appToken, result);
            }
        });
    }
}

