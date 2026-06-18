// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrinterService;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.scanner.ScannerService;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.MainActivity;
import com.hp.workpath.sample.statisticsample.view.Utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadCapabilitiesTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadCapabilitiesTask(MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    PrintAttributesCaps printCaps = null;
                    ScanAttributesCaps scanCaps = null;

                    try {
                        Utils.copyAssets(mContextRef.get());
                        if (ScannerService.isSupported(mContextRef.get())) {
                            Logger.showResult(null, "ScannerService is supported");
                            scanCaps = requestScanCaps(mContextRef.get(), mResult);
                        }
                        if (PrinterService.isSupported(mContextRef.get())) {
                            Logger.showResult(null, "PrinterService is supported");
                            printCaps = requestPrintCaps(mContextRef.get(), mResult);
                        }
                        onPostExecute(printCaps, scanCaps);
                    } catch (Throwable t) {
                        Logger.showResult(null, "getCapabilities " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(printCaps, scanCaps);
                    }

                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "getCapabilities " + e.getMessage());
            onPostExecute(null, null);
            executor.shutdown();
        }
    }

    private void onPostExecute(PrintAttributesCaps printCaps, ScanAttributesCaps scanCaps) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Logger.showResult(mContextRef.get(), "getCapabilities", mResult);
                mContextRef.get().loadCapabilities(scanCaps, printCaps);
            }
        });
    }


    private PrintAttributesCaps requestPrintCaps(Context context, Result result) {
        return PrinterService.getCapabilities(context, result);
    }

    private ScanAttributesCaps requestScanCaps(Context context, Result result) {
        return ScannerService.getCapabilities(context, result);
    }
}
