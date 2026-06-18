// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request device defaults for Copy
 */
public class LoadDefaultsTask {
    private final WeakReference<MainActivity> mContextRef;
    private final WeakReference<CopyConfigureFragment> mFragment;
    private Result result;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LoadDefaultsTask(final MainActivity context, final CopyConfigureFragment fragment) {
        this.mContextRef = new WeakReference<>(context);
        this.mFragment = new WeakReference<>(fragment);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(CopierService.getDefaults(mContextRef.get(), result));
                    } catch (Throwable t) {
                        Logger.showResult(null, "CopierService.getDefaults " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CopierService.getDefaults " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final CopyAttributes defaults) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (defaults != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.succeed) + Logger.build(defaults));
                    try {
                        mFragment.get().setDefaultCopyAttributes(defaults);
                    } catch (Exception e) {
                        Logger.showResult(mContextRef.get(), e.getMessage());
                    }
                } else {
                    Logger.showResult(mContextRef.get(), "CopierService.getDefaults", result);
                }
            }
        });

    }

}
