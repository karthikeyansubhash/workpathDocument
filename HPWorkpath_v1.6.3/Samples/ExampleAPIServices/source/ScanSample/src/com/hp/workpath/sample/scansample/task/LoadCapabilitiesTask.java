// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.sample.scansample.Logger;
import com.hp.workpath.sample.scansample.MainActivity;
import com.hp.workpath.sample.scansample.R;
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request device capabilities for scan
 */
public class LoadCapabilitiesTask {

    private final WeakReference<MainActivity> mContextRef;
    private final WeakReference<ScanConfigureFragment> mFragment;
    private Result result;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public LoadCapabilitiesTask(final MainActivity context, final ScanConfigureFragment fragment) {
        this.mContextRef = new WeakReference<>(context);
        this.mFragment = new WeakReference<>(fragment);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(mContextRef.get().requestCaps(mContextRef.get(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "ScannerService.getCapabilities " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "ScannerService.getCapabilities " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }


    private void onPostExecute(final ScanAttributesCaps caps) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (caps != null && result.getCode() == Result.RESULT_OK) {
                    mFragment.get().loadCapabilities(caps);
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.loaded_caps));
                } else {
                    Logger.showResult(mContextRef.get(), "ScannerService.getCapabilities", result);
                }
            }
        });
    }
}
