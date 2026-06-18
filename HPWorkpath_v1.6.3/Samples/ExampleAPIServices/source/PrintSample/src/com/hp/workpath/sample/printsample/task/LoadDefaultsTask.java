// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrinterService;
import com.hp.workpath.sample.printsample.Logger;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request device defaults for Print.
 */
public class LoadDefaultsTask {

    private final WeakReference<MainActivity> mContextRef;
    private final WeakReference<PrintConfigureFragment> mFragment;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadDefaultsTask(final MainActivity context, final PrintConfigureFragment fragment) {
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
                        onPostExecute(PrinterService.getDefaults(mContextRef.get(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "PrinterService.getDefaults " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "PrinterService.getDefaults " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final PrintAttributes defaults) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (defaults != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), "Defaults=" + Logger.build(defaults));
                    mFragment.get().setDefaultPrintAttributes(defaults);
                } else {
                    Logger.showResult(mContextRef.get(), "PrinterService.getDefaults", result);
                }
            }
        });
    }
}
