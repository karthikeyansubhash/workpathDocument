// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScannerService;
import com.hp.workpath.sample.scansample.Logger;
import com.hp.workpath.sample.scansample.MainActivity;
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request device defaults for Scan
 */
public class LoadDefaultsTask {

    private final WeakReference<MainActivity> mContextRef;
    private final WeakReference<ScanConfigureFragment> mFragment;
    private Result result;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public LoadDefaultsTask(final MainActivity context, final ScanConfigureFragment fragment) {
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
                        onPostExecute(ScannerService.getDefaults(mContextRef.get(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "ScannerService.getDefaults " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "ScannerService.getDefaults " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final ScanAttributes defaults) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (defaults != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), "Defaults=" + Logger.build(defaults));
                    try {
                        mFragment.get().setDefaultScanAttributes(defaults);
                    } catch (Exception e) {
                        Logger.showResult(mContextRef.get(), e.getMessage());
                    }
                } else {
                    Logger.showResult(mContextRef.get(), "ScannerService.getDefaults", result);
                }
            }
        });
    }
}
