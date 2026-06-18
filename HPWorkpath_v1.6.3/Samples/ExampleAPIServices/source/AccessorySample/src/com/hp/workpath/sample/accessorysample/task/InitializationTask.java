// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.sample.accessorysample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializationTask {

    private final WeakReference<Context> mContextRef;
    private InitializeInterface initializeInterface;

    private Throwable mThrowable = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public void cancel() {
        executor.shutdown();
    }

    public interface InitializeInterface {
        void handleComplete();

        void handleException(Throwable t);
    }

    public InitializationTask(Context context, InitializeInterface initializeInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.initializeInterface = initializeInterface;
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

                        // Check if AccessoryService is supported
                        if (!AccessoryService.isSupported(mContextRef.get())) {
                            // AccessoryService is not supported on this device
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
                if (initializeInterface != null) {
                    if (status == InitStatus.NO_ERROR) {
                        initializeInterface.handleComplete();
                        return;
                    }

                    switch (status) {
                        case INIT_EXCEPTION:
                            initializeInterface.handleException(mThrowable);
                            break;
                        case NOT_SUPPORTED:
                            initializeInterface.handleException(new Throwable(mContextRef.get().getString(R.string.service_not_supported)));
                            break;
                        default:
                            initializeInterface.handleException(new Throwable(mContextRef.get().getString(R.string.unknown_error)));
                    }
                }
            }
        });
    }

    public enum InitStatus {
        INIT_EXCEPTION,
        NOT_SUPPORTED,
        NO_ERROR
    }
}