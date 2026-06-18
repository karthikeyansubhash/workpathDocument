// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.eventnotificationsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.eventnotificationsample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class InitializationTask {

    private Throwable mThrowable = null;

    private final WeakReference<Context> mContextRef;

    private final WeakReference<ResultHandler> mResultHandlerRef;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public InitializationTask(Context context, ResultHandler handler) {
        this.mContextRef = new WeakReference<>(context);
        this.mResultHandlerRef = new WeakReference<>(handler);
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

                        // Check if AccessService is supported
                        if (!isSupported(mContextRef.get())) {
                            // AccessService is not supported on this device
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
                if (status == InitStatus.NO_ERROR) {
                    mResultHandlerRef.get().handleComplete();
                    return;
                }

                switch (status) {
                    case INIT_EXCEPTION:
                        mResultHandlerRef.get().handleException(mThrowable);
                        break;
                    case NOT_SUPPORTED:
                        mResultHandlerRef.get().handleException(new Throwable(mContextRef.get().getString(getExceptionMessage())));
                        break;
                    default:
                        mResultHandlerRef.get().handleException(new Throwable(mContextRef.get().getString(R.string.unknown_error)));
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

    public abstract boolean isSupported(Context context);

    public abstract int getExceptionMessage();
}