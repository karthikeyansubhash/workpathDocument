// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.api.massstorage.CustomerDataFileUtils;
import com.hp.workpath.sample.massstoragesample.Logger;
import com.hp.workpath.sample.massstoragesample.MainActivity;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateFileTask {

    private final WeakReference<MainActivity> mContextRef;
    private final CustomerDataFile mCustomerDataFile;
    private final String mContent;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public CreateFileTask(MainActivity context, CustomerDataFile customerDataFile, String content) {
        this.mContextRef = new WeakReference<>(context);
        this.mCustomerDataFile = customerDataFile;
        this.mContent = content;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCustomerDataFile.createNewFile();

                        OutputStream fos = null;
                        BufferedOutputStream bos = null;
                        try {
                            fos = CustomerDataFileUtils.openOutputStream(mContextRef.get(), mCustomerDataFile);
                            bos = new BufferedOutputStream(fos);
                            bos.write(mContent.getBytes());
                            bos.flush();
                            onPostExecute(true);
                        } catch (Throwable t) {
                            Logger.showResult(mContextRef.get(), "File could not be created. " + t.getMessage());
                        } finally {
                            if (bos != null) {
                                try {
                                    bos.close();
                                } catch (IOException e) { // ignore
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) { // ignore
                                }
                            }
                        }

                    } catch (Throwable t) {
                        Logger.showResult(mContextRef.get(), "File could not be created. " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(false);
                    }
                }
            });
        } catch (Exception e) {
            Logger.showResult(mContextRef.get(), "File could not be created. " + e.getMessage());
            onPostExecute(false);
            executor.shutdown();
        }
    }

    private void onPostExecute(Boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().enableButton(true);
                if (result) {
                    Logger.showResult(mContextRef.get(), "File created");
                    mContextRef.get().displayFileList(mCustomerDataFile.getParentFile());
                }
            }
        });
    }
}
