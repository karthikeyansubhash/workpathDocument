// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.statistics.StatisticsJobData;
import com.hp.workpath.api.statistics.StatisticsService;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.fragment.ResponseInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobInfoTask {

    private WeakReference<Context> mContextRef;
    private ResponseInterface mResponseInterface;
    private Result mResult;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public JobInfoTask(Context context, ResponseInterface responseInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.mResponseInterface = responseInterface;
        this.mResult = new Result();
    }

    public void taskExecute(Integer... args) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Integer jobSequence = null;
                    if (args.length == 1) {
                        jobSequence = args[0];
                    }

                    try {
                        if (jobSequence == null) {
                            onPostExecute(StatisticsService.getAllJobsList(mContextRef.get(), mResult));
                        } else {
                            List<StatisticsJobData> jobDataList = new ArrayList<>();
                            jobDataList.add(StatisticsService.getJobInfoByJobSequence(mContextRef.get(), jobSequence, mResult));
                            onPostExecute(jobDataList);
                        }
                    } catch (Throwable t) {
                        Logger.showResult(null, "StatisticsService.getJobInfoByJobSequence " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }

                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "StatisticsService.getJobInfoByJobSequence " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(List<StatisticsJobData> jobDataList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResult.getCode() == Result.RESULT_OK && jobDataList != null) {
                    mResponseInterface.success(jobDataList);
                } else {
                    mResponseInterface.failure("StatisticsService.getJobInfoByJobSequence", mResult);
                }
            }
        });
    }
}
