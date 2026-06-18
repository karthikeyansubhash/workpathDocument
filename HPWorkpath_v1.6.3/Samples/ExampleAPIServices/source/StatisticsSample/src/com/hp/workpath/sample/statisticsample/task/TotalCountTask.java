// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.statistics.StatisticsService;
import com.hp.workpath.sample.statisticsample.Logger;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TotalCountTask {

    private WeakReference<Context> mContextRef;
    private Result mResult;
    private TotalCountTaskCompletionListener mTotalCountTaskCompletionListener;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Integer countValue;

    public TotalCountTask(Context context, TotalCountTaskCompletionListener totalCountTaskCompletionListener) {
        this.mContextRef = new WeakReference<>(context);
        this.mTotalCountTaskCompletionListener = totalCountTaskCompletionListener;
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        countValue = StatisticsService.getTotalCount(mContextRef.get(), mResult);
                        onPostExecute();
                    } catch (Throwable t) {
                        Logger.showResult(null, "StatisticsService.getTotalCount " + t.getMessage());
                        executor.shutdown();
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "StatisticsService.getTotalCount " + e.getMessage());
            executor.shutdown();
        }
    }

    private void onPostExecute() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTotalCountTaskCompletionListener.onTotalCountTaskCompleted(countValue);
                Logger.showResult(null, "StatisticsService.getTotalCount", mResult);
            }
        });
    }

    public Integer getValue() {
        return countValue;
    }

    public interface TotalCountTaskCompletionListener {
        void onTotalCountTaskCompleted(int count);
    }
}
