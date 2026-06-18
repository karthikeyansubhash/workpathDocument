// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authenticationpreprompt.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.AccessService;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializationTask {

    private Throwable mThrowable = null;

    private final WeakReference<Context> mContextRef;
    private InitializeInterface initializeInterface;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private BlockingQueue<Boolean> blockingQueue;

    public void cancel() {
        executor.shutdown();
    }

    public interface InitializeInterface {
        void handleComplete(InitializationTask.InitStatus initStatus);

        void handleException(Throwable t);
    }

    public InitializationTask(Context context, InitializeInterface initializeInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.initializeInterface = initializeInterface;
    }

    public void setBlockingQueue(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    InitStatus status = InitStatus.NO_ERROR;

                    try {
                        // initialize the Workpath SDK
                        Workpath.getInstance().initialize(mContextRef.get());

                        // Check if AccessService is supported
                        if (!AccessService.isSupported(mContextRef.get())) {
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
        if (blockingQueue != null) {
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
                    initializeInterface.handleException(mThrowable);
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