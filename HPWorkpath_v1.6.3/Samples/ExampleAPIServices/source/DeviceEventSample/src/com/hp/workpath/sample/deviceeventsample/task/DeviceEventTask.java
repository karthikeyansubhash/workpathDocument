// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.api.device.events.DeviceEventsService;
import com.hp.workpath.sample.deviceeventsample.Logger;
import com.hp.workpath.sample.deviceeventsample.fragment.ResponseInterface;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceEventTask {

    private WeakReference<Context> mContextRef;
    private ResponseInterface mResponseInterface;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeviceEventTask(Context context, ResponseInterface responseInterface) {
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
                        onPostExecute(DeviceEventsService.getDeviceEvents(mContextRef.get(), mResult));
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

    private void onPostExecute(List<DeviceEvent> deviceEventList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResult.getCode() == Result.RESULT_OK) {
                    mResponseInterface.success(deviceEventList);
                } else {
                    mResponseInterface.failure("DeviceEventsService.getDeviceEvents", mResult);
                }
            }
        });
    }
}
