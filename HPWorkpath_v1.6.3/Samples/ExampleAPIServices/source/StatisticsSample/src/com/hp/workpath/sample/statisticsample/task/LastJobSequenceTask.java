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

public class LastJobSequenceTask {

    private WeakReference<Context> mContextRef;
    private Result mResult;
    private Integer seqValue;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private LastJobSequenceTaskCompletionListener mLastJobSequenceTaskCompletionListener;

    public LastJobSequenceTask(Context context, LastJobSequenceTaskCompletionListener lastJobSequenceTaskCompletionListener) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
        this.mLastJobSequenceTaskCompletionListener = lastJobSequenceTaskCompletionListener;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(StatisticsService.getLastJobSequence(mContextRef.get(), mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "StatisticsService.getLastCommittedJobSequence " + t.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "StatisticsService.getLastCommittedJobSequence " + e.getMessage());
            executor.shutdown();
        }
    }

    private void onPostExecute(Integer seq) {
        seqValue = seq;
        handler.post(new Runnable() {
            @Override
            public void run() {
                mLastJobSequenceTaskCompletionListener.onLastJobSequenceCompleted(seq);
                Logger.showResult(null, "StatisticsService.getLastCommittedJobSequence", mResult);
            }
        });
    }

    public Integer getSeqValue() {
        return seqValue;
    }

    public interface LastJobSequenceTaskCompletionListener {
        void onLastJobSequenceCompleted(int count);
    }
}
