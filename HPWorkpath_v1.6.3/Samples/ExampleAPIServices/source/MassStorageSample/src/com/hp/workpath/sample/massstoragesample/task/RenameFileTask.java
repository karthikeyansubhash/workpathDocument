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

public class RenameFileTask {

    private final WeakReference<MainActivity> mContextRef;
    private final CustomerDataFile mFile;
    private final CustomerDataFile mDest;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RenameFileTask(MainActivity context, CustomerDataFile file, CustomerDataFile dest) {
        this.mContextRef = new WeakReference<>(context);
        this.mFile = file;
        this.mDest = dest;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onPostExecute(mFile.renameTo(mDest));
                    } catch (Throwable t) {
                        Logger.showResult(null, "CustomerDataFile.renameTo " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(false);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CustomerDataFile.renameTo " + e.getMessage());
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
                Logger.showResult(mContextRef.get(), "CustomerDataFile.renameTo " + result);
                if (result) {
                    mContextRef.get().displayFileList(mFile.getParentFile());
                }
            }
        });
    }
}
