// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.task;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScannerService;
import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.MainActivity;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.fragment.TestJobFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestScanTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<TestJobFragment> mContextRef;
    private ScanAttributes mScanAttributes;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestScanTask(TestJobFragment context, ScanAttributes mScanAttributes) {
        this.mContextRef = new WeakReference<>(context);
        this.mScanAttributes = mScanAttributes;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String rid = ScannerService.submit(mContextRef.get().getContext(), mScanAttributes, null);
                        Log.i(TAG, "Job submitted with rid = " + rid);
                        onPostExecute(rid);
                    } catch (Throwable t) {
                        Logger.showResult(null, "ScannerService.submit " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "ScannerService.submit " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(String rid) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(rid)) {
                    mContextRef.get().setRid(rid);
                } else {
                    if (mContextRef.get() != null) {
                        Logger.showResult(mContextRef.get().getActivity(), mContextRef.get().getString(R.string.job_request_failed));
                    }
                }
            }
        });

    }
}
