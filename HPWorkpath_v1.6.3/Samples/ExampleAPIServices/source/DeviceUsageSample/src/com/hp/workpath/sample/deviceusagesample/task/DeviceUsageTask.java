// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.deviceusage.DeviceUsageInfo;
import com.hp.workpath.api.deviceusage.DeviceUsageService;
import com.hp.workpath.sample.deviceusagesample.Logger;
import com.hp.workpath.sample.deviceusagesample.fragment.ResponseInterface;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceUsageTask {

    private WeakReference<Context> mContextRef;
    private ResponseInterface mResponseInterface;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeviceUsageTask(Context context, ResponseInterface responseInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.mResponseInterface = responseInterface;
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(DeviceUsageService.getDeviceUsageInfo(mContextRef.get(), mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "DeviceEventsService.getDeviceEvents " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);

                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "DeviceEventsService.getDeviceEvents " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(DeviceUsageInfo deviceUsageInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResult.getCode() == Result.RESULT_OK && deviceUsageInfo != null) {
                    mResponseInterface.success(deviceUsageInfo);
                } else {
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents", mResult);
                }
            }
        });
    }
}
