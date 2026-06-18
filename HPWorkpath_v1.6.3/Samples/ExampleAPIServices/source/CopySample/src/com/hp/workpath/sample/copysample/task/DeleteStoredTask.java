// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.JobCredentialsAttributes;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteStoredTask {

    private final WeakReference<MainActivity> mContextRef;
    private JobCredentialsAttributes jobCredentials;

    private String mErrorMsg;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeleteStoredTask(final MainActivity context, JobCredentialsAttributes jobCredentials) {
        this.mContextRef = new WeakReference<>(context);
        this.jobCredentials = jobCredentials;
        this.result = new Result();
    }

    public void taskExecute(final String... params) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = mContextRef.get();
                    try {
                        String storedJobId = params[0];
                        CopierService.deleteStoredJob(activity, storedJobId, jobCredentials, result);
                    } catch (IllegalArgumentException iae) {
                        mErrorMsg = "IllegalArgumentException";
                        Logger.showResult(null, mErrorMsg + " " + iae.getMessage());
                        executor.shutdown();
                    } catch (Throwable t) {
                        mErrorMsg = "Unknown exception";
                        Logger.showResult(null, mErrorMsg + " " + t.getMessage());
                        executor.shutdown();
                    }
                    onPostExecute();
                }
            });
        } catch (Exception e) {
            mErrorMsg = "Unknown exception";
            Logger.showResult(null, mErrorMsg + " " + e.getMessage());
            executor.shutdown();
        }
    }

    private void onPostExecute() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (result.getCode() != Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), "CopierService.deleteStoredJob", result);
                } else {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.job_deleted));
                }
            }
        });
    }
} 