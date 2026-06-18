// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.supplies.SuppliesService;
import com.hp.workpath.api.supplies.supplyinfo.Supply;
import com.hp.workpath.sample.suppliessample.Logger;
import com.hp.workpath.sample.suppliessample.fragment.ResponseInterface;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuppliesTask {

    private WeakReference<Context> mContextRef;
    private ResponseInterface mResponseInterface;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public SuppliesTask(Context context, ResponseInterface responseInterface) {
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
                        onPostExecute(SuppliesService.getSuppliesInfo(mContextRef.get(), mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "SuppliesService.getSuppliesInfo" + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }

                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "SuppliesService.getSuppliesInfo" + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(List<Supply> supplyList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResult.getCode() == Result.RESULT_OK && supplyList != null) {
                    mResponseInterface.success(supplyList);
                } else {
                    mResponseInterface.failure("SuppliesService.getSuppliesInfo", mResult);
                }
            }
        });
    }
}
