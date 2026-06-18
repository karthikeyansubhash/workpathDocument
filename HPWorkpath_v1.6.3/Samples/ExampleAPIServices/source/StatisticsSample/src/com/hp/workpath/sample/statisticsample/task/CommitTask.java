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

public class CommitTask {

    private WeakReference<Context> mContextRef;
    private int mCommit;
    private Result mResult;
    private Boolean mValue;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private CommitTaskListener mCommitTaskListener;

    public CommitTask(Context context, int commit, CommitTaskListener commitTaskListener) {
        this.mContextRef = new WeakReference<>(context);
        this.mCommit = commit;
        this.mResult = new Result();
        this.mCommitTaskListener = commitTaskListener;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(StatisticsService.commit(mContextRef.get(), mCommit, mResult));
                    } catch (Throwable t) {
                        Logger.showResult(null, "StatisticsService.commit " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(false);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "StatisticsService.commit " + e.getMessage());
            onPostExecute(false);
            executor.shutdown();
        }
    }

    private void onPostExecute(Boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mCommitTaskListener.onCommitTask(result);
                Logger.showResult(null, "StatisticsService.commit", mResult);
                mValue = result;
            }
        });
    }

    public Boolean getValue() {
        return mValue;
    }

    public interface CommitTaskListener {
        void onCommitTask(Boolean committed);
    }
}
