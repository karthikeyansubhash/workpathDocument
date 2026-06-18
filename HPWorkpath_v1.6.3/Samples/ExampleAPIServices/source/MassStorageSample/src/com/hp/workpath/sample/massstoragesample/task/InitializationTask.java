// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.massstorage.MassStorageService;
import com.hp.workpath.sample.massstoragesample.MainActivity;
import com.hp.workpath.sample.massstoragesample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializationTask {

    private final WeakReference<MainActivity> mContextRef;
    private Throwable mThrowable = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public InitializationTask(MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    InitStatus status = InitStatus.NO_ERROR;

                    try {
                        // initialize Workpath SDK
                        Workpath.getInstance().initialize(mContextRef.get());

                        // Check if MassStorageService is supported
                        if (!MassStorageService.isSupported(mContextRef.get())) {
                            // MassStorageService is not supported on this device
                            status = InitStatus.NOT_SUPPORTED;
                        }
                    } catch (SsdkUnsupportedException sue) {
                        mThrowable = sue;
                        status = InitStatus.INIT_EXCEPTION;
                    } catch (SecurityException se) {
                        mThrowable = se;
                        status = InitStatus.INIT_EXCEPTION;
                    } catch (Throwable t) {
                        mThrowable = t;
                        status = InitStatus.INIT_EXCEPTION;
                    }

                    onPostExecute(status);
                }
            });
        } catch (Exception e) {
            mThrowable = e;
            onPostExecute(InitStatus.INIT_EXCEPTION);
            executor.shutdown();
        }
    }

    private void onPostExecute(final InitializationTask.InitStatus status) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (status == InitStatus.NO_ERROR || status == null) {
                    mContextRef.get().handleComplete();
                    return;
                }
                switch (status) {
                    case INIT_EXCEPTION:
                        mContextRef.get().handleException(mThrowable);
                        break;
                    case NOT_SUPPORTED:
                        mContextRef.get().handleException(new Throwable(mContextRef.get().getString(R.string.service_not_supported)));
                        break;
                    default:
                        mContextRef.get().handleException(new Throwable(mContextRef.get().getString(R.string.unknown_error)));
                }
            }
        });
    }

    public void cancel() {
        executor.shutdown();
    }

    public enum InitStatus {
        INIT_EXCEPTION,
        NOT_SUPPORTED,
        NO_ERROR
    }
}