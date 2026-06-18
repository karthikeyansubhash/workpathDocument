// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.DeviceAttribute;
import com.hp.workpath.api.device.DeviceService;
import com.hp.workpath.sample.launchersample.Logger;
import com.hp.workpath.sample.launchersample.MainActivity;
import com.hp.workpath.sample.launchersample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceInformationTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeviceInformationTask(final MainActivity context) {
        mContextRef = new WeakReference<>(context);
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mResult = new Result();
                        onPostExecute(DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_NETWORK_HOSTNAME, mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "DeviceService.getString " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "DeviceService.getString " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(String hostname) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResult.getCode() == Result.RESULT_OK && hostname != null) {
                    mContextRef.get().handleDeviceInfo(hostname);
                } else {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.error), mResult);
                }
            }
        });
    }
}

