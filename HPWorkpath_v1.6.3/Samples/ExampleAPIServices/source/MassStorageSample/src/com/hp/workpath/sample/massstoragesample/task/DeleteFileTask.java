// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.sample.massstoragesample.Logger;
import com.hp.workpath.sample.massstoragesample.MainActivity;
import com.hp.workpath.sample.massstoragesample.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteFileTask {

    private final WeakReference<MainActivity> mContextRef;
    private final List<CustomerDataFile> mFilesToBeDeleted;
    private final CustomerDataFile mBaseCustomerDataFile;
    private int failCount = 0;
    private int successCount = 0;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeleteFileTask(MainActivity context, List<CustomerDataFile> fileList, CustomerDataFile baseCustomerDataFile) {
        this.mContextRef = new WeakReference<>(context);
        this.mFilesToBeDeleted = fileList;
        this.mBaseCustomerDataFile = baseCustomerDataFile;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (CustomerDataFile customerDataFile : mFilesToBeDeleted) {
                            boolean isDeleted = customerDataFile.delete();
                            if (isDeleted) {
                                ++successCount;
                            } else {
                                ++failCount;
                            }
                        }
                        onPostExecute(true);
                    } catch (Throwable t) {
                        Logger.showResult(null, "CustomerDataFile.delete " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(false);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CustomerDataFile.delete " + e.getMessage());
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
                Logger.showResult(mContextRef.get(), mContextRef.get().getString(R.string.sucess_and_fail, successCount, failCount));
                if (result) {
                    mContextRef.get().displayFileList(mBaseCustomerDataFile);
                }
            }
        });
    }
}
