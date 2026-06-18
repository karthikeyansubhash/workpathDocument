// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.sample.massstoragesample.Logger;
import com.hp.workpath.sample.massstoragesample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNewFileTask {

    private final WeakReference<MainActivity> mContextRef;
    private final CustomerDataFile mFile;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public CreateNewFileTask(MainActivity context, CustomerDataFile file) {
        this.mContextRef = new WeakReference<>(context);
        this.mFile = file;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(mFile.createNewFile());
                    } catch (Throwable t) {
                        Logger.showResult(null, "CustomerDataFile.createNewFile " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(false);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CustomerDataFile.createNewFile " + e.getMessage());
            onPostExecute(false);
            executor.shutdown();
        }
    }

    private void onPostExecute(Boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().enableButton(true);
                mContextRef.get().showProgress(View.GONE);
                Logger.showResult(mContextRef.get(), "CustomerDataFile.createNewFile " + result);
                if (result) {
                    mContextRef.get().displayFileList(mFile.getParentFile());
                }
            }
        });
    }
}
