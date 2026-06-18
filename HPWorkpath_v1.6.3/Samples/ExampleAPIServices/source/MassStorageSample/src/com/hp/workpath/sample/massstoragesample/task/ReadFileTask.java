// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.api.massstorage.CustomerDataFileUtils;
import com.hp.workpath.sample.massstoragesample.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadFileTask {
    private final WeakReference<Context> mContextRef;
    private CustomerDataFile mCustomerDataFile;
    private ReadFileTaskInterface readFileTaskInterface;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ReadFileTask(Context context, CustomerDataFile customerDataFile, ReadFileTaskInterface readFileTaskInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.mCustomerDataFile = customerDataFile;
        this.readFileTaskInterface = readFileTaskInterface;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String content = "";
                    InputStream fis = null;
                    BufferedInputStream bis = null;
                    long fileLength = mCustomerDataFile.length();

                    try {
                        int bufferSize = 64;
                        if (fileLength < bufferSize) {
                            bufferSize = (int) fileLength;
                        }

                        byte[] buffer = new byte[bufferSize];
                        fis = CustomerDataFileUtils.openInputStream(mContextRef.get(), mCustomerDataFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(buffer);
                        content = new String(buffer);
                    } catch (Throwable t) {
                        Logger.showResult(null, "CustomerDataFileUtils.openInputStream " + t.getMessage());
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                            }
                        }
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                    onPostExecute(content);
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "CustomerDataFileUtils.openInputStream " + e.getMessage());
            executor.shutdown();
        }
    }

    public interface ReadFileTaskInterface {
        void fileContent(String content);
    }

    private void onPostExecute(String content) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                readFileTaskInterface.fileContent(content);
            }
        });
    }
}
