// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryservicesample.task;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.sample.accessoryservicesample.Logger;
import com.hp.workpath.sample.accessoryservicesample.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializationTask {
    private final WeakReference<Context> mContextRef;
    private InitializeInterface initializeInterface;

    private Throwable mThrowable = null;
    private final int DEFAULT_RETRIES = 5;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private BlockingQueue<Boolean> blockingQueue;

    public void cancel() {
        executor.shutdown();
    }

    public void setBlockingQueue(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public interface InitializeInterface {
        void handleComplete(InitializationTask.InitStatus initStatus);

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
                    InitStatus status = InitStatus.INIT_EXCEPTION;

                    int numberOfRetries = 0;
                    while (status == InitStatus.INIT_EXCEPTION && numberOfRetries < DEFAULT_RETRIES) {
                        numberOfRetries++;
                        try {
                            // initialize Workpath SDK
                            Workpath.getInstance().initialize(mContextRef.get());
                            status = InitStatus.NO_ERROR;
                        } catch (SsdkUnsupportedException sue) {
                            mThrowable = sue;
                            status = InitStatus.INIT_EXCEPTION;
                        } catch (SecurityException se) {
                            mThrowable = se;
                            status = InitStatus.INIT_EXCEPTION;
                        } catch (Exception e) {
                            mThrowable = e;
                            status = InitStatus.INIT_EXCEPTION;
                        }

                        // Check if AccessoryService or AccessService is supported
                        if (status == InitStatus.NO_ERROR
                                && !AccessoryService.isSupported(mContextRef.get())
                                && !AccessService.isSupported(mContextRef.get())) {
                            // AccessoryService or AccessService is not supported on this device
                            onPostExecute(InitStatus.NOT_SUPPORTED);
                            return;
                        }

                        try {
                            sleep(1000 * 3);
                        } catch (InterruptedException e) {
                            Logger.showResult(null, "initialize is interrupted:" + e.getMessage());
                        }
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
        if (blockingQueue != null && blockingQueue.size() == 0) {
            if (status == InitStatus.NO_ERROR) blockingQueue.add(true);
            else blockingQueue.add(false);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (initializeInterface != null) {
                    if (status == InitStatus.NO_ERROR) {
                        initializeInterface.handleComplete(status);
                        return;
                    }

                    switch (status) {
                        case INIT_EXCEPTION:
                            initializeInterface.handleException(mThrowable);
                            break;
                        case NOT_SUPPORTED:
                            initializeInterface.handleException(new Exception(mContextRef.get().getString(R.string.service_not_supported)));
                            break;
                        default:
                            initializeInterface.handleException(new Exception(mContextRef.get().getString(R.string.unknown_error)));
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