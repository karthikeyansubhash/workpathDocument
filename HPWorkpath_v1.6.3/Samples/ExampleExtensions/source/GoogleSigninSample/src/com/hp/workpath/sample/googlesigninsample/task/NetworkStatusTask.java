// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import static com.hp.workpath.sample.googlesigninsample.MainActivity.TAG;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkStatusTask {

    private static final int TIMEOUT = 3000;
    private final WeakReference<Context> mContextRef;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private NetworkStatusInterface networkStatusInterface;

    public NetworkStatusTask(Context context, NetworkStatusInterface networkStatusInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.networkStatusInterface = networkStatusInterface;
    }

    public interface NetworkStatusInterface {
        void isAvailable(boolean isAvailable);
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isNetworkAvailable(mContextRef.get()))
                            networkStatusInterface.isAvailable(false);
                        else if (!checkInternetConnection())
                            networkStatusInterface.isAvailable(false);
                        else
                            networkStatusInterface.isAvailable(true);
                    } catch (Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                        executor.shutdown();
                    }
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());
            executor.shutdown();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkInternetConnection() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }
}
