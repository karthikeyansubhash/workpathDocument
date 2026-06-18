// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;

import com.hp.workpath.sample.googlesigninsample.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RevokeTokenTask {

    private static final String TAG = MainActivity.TAG;

    private String mAccessToken;
    private TaskInterface mTaskInterface;
    private Throwable mThrowable;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RevokeTokenTask(@NonNull String accessToken, @NonNull TaskInterface taskInterface) {
        this.mAccessToken = accessToken;
        this.mTaskInterface = taskInterface;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .readTimeout(60, TimeUnit.SECONDS)
                                .writeTimeout(60, TimeUnit.SECONDS)
                                .build();

                        Log.i(TAG, "Revoke accessToken: " + mAccessToken);
                        Request request = new Request.Builder()
                                .url("https://accounts.google.com/o/oauth2/revoke?token=" + mAccessToken)
                                .method("POST", RequestBody.create(null, new byte[0]))
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                onPostExecute(true);
                            } else {
                                mThrowable = new Exception(response.body().string());
                                onPostExecute(false);
                            }
                        } else {
                            mThrowable = new Exception("Revoke token response is null");
                            onPostExecute(false);
                        }
                    } catch (Throwable t) {
                        mThrowable = t;
                        onPostExecute(false);
                        executor.shutdown();
                    }
                }
            });
        } catch (Throwable t) {
            mThrowable = t;
            onPostExecute(false);
            executor.shutdown();
        }
    }

    protected void onPostExecute(boolean result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!result) {
                    mTaskInterface.onFailure(mThrowable);
                } else {
                    mTaskInterface.revokedToken();
                }
                CookieManager.getInstance().removeAllCookie();
            }
        });
    }
}
