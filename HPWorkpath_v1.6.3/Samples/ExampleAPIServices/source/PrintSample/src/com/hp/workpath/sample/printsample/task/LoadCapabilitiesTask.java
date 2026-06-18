// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.sample.printsample.Logger;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.R;
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request device capabilities for print.
 */
public class LoadCapabilitiesTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result result;

    private final WeakReference<PrintConfigureFragment> mFragment;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadCapabilitiesTask(final MainActivity context, final PrintConfigureFragment fragment) {
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
                        Logger.showResult(null, "PrinterService.getCapabilities " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "PrinterService.getCapabilities " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final PrintAttributesCaps caps) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (caps != null && result.getCode() == Result.RESULT_OK) {
                    mFragment.get().loadCapabilities(caps);
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.loaded_caps));
                } else {
                    Logger.showResult(mContextRef.get(), "PrinterService.getCapabilities", result);
                }
            }
        });
    }
}
