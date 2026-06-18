// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrinterService;
import com.hp.workpath.sample.configsample.Logger;
import com.hp.workpath.sample.configsample.MainActivity;
import com.hp.workpath.sample.configsample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadPrintCapabilitiesTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadPrintCapabilitiesTask(final MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!PrinterService.isSupported(mContextRef.get())) {
                            Log.d(TAG, mContextRef.get().getString(R.string.print_service_not_supported));
                            onPostExecute(null);
                            return;
                        }
                        onPostExecute(PrinterService.getCapabilities(mContextRef.get(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "PrinterService.getCapabilities is failed: " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "PrinterService.getCapabilities is failed: " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final PrintAttributesCaps caps) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                if (caps != null && result.getCode() == Result.RESULT_OK) {
                    mContextRef.get().setPrintCapabilities(caps);
                    Logger.showResult(mContextRef.get(), "Success");
                } else {
                    Logger.showResult(mContextRef.get(), "PrinterService.getCapabilities", result);
                }
            }
        });
    }
}
