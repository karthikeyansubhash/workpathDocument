// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.StoredJobInfo;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.fragments.StoreJobFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnumerateStoredJobTask {

    private final WeakReference<StoreJobFragment> mContextRef;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public EnumerateStoredJobTask(final StoreJobFragment context) {
        this.mContextRef = new WeakReference<>(context);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(CopierService.enumerateStoredJob(mContextRef.get().getActivity(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "CopierService.enumerateStoredJob " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CopierService.enumerateStoredJob " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final List<StoredJobInfo> storedJobInfoList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgressBar(View.GONE);
                if (result.getCode() == Result.RESULT_OK && storedJobInfoList != null) {
                    for (StoredJobInfo storedJobInfo : storedJobInfoList) {
                        Logger.showResult(mContextRef.get().getActivity(), "StoredJobList=" + Logger.build(storedJobInfo));
                    }
                    mContextRef.get().enumerateStoredJob(storedJobInfoList);
                } else {
                    Logger.showResult(mContextRef.get().getActivity(), "CopierService.enumerateStoredJob", result);
                }
            }
        });
    }
} 