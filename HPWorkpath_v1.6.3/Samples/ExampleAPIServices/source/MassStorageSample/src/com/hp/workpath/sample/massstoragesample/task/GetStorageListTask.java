// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.api.massstorage.MassStorageService;
import com.hp.workpath.sample.massstoragesample.Logger;
import com.hp.workpath.sample.massstoragesample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetStorageListTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public GetStorageListTask(MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(MassStorageService.getStorageList(mContextRef.get(), mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "MassStorageService.getStorageList " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "MassStorageService.getStorageList " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(List<MassStorageInfo> massStorageInfoList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                Logger.showResult(mContextRef.get(), "MassStorageService.getStorageList", mResult);
                if (massStorageInfoList != null && mResult.getCode() == Result.RESULT_OK) {
                    mContextRef.get().loadStorageList(massStorageInfoList);
                }
            }
        });
    }
}
