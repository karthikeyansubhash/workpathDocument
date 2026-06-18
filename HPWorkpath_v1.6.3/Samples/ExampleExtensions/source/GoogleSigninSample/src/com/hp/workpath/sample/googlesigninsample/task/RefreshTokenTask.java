// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.hp.workpath.sample.googlesigninsample.MainActivity;
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RefreshTokenTask {

    private static final String TAG = MainActivity.TAG;

    private ClientInfo mClientInfo;
    private String mRefreshToken;
    private TaskInterface mTaskInterface;
    private Throwable mThrowable;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RefreshTokenTask(@NonNull ClientInfo clientInfo, @NonNull String refreshToken, @NonNull TaskInterface taskInterface) {
        this.mClientInfo = clientInfo;
        this.mRefreshToken = refreshToken;
        this.mTaskInterface = taskInterface;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TokenResponse response = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                                mRefreshToken, mClientInfo.getClientId(), mClientInfo.getClientSecret()).execute();
                        Log.i(TAG, "Access token: " + response.getAccessToken());
                        onPostExecute(response.getAccessToken());
                    } catch (Throwable t) {
                        mThrowable = t;
                        onPostExecute(null);
                        executor.shutdown();
                    }
                }
            });
        } catch (Throwable t) {
            mThrowable = t;
            onPostExecute(null);
            executor.shutdown();
        }
    }

    protected void onPostExecute(String accessToken) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(accessToken)) {
                    mTaskInterface.onFailure(mThrowable);
                } else {
                    mTaskInterface.refreshedToken(accessToken);
                }
            }
        });
    }
}
