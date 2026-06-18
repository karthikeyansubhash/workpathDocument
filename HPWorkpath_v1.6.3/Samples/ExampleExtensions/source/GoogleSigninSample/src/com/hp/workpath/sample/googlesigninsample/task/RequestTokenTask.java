// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hp.workpath.sample.googlesigninsample.MainActivity;
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo;
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestTokenTask {

    private static final String TAG = MainActivity.TAG;

    private ClientInfo mClientInfo;
    private String mCode;
    private TaskInterface mTaskInterface;
    private Throwable mThrowable;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestTokenTask(@NonNull ClientInfo clientInfo, @NonNull String code, @NonNull TaskInterface taskInterface) {
        this.mClientInfo = clientInfo;
        this.mCode = code;
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

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("code", mCode)
                                .addFormDataPart("client_id", mClientInfo.getClientId())
                                .addFormDataPart("redirect_uri", mClientInfo.getRedirectUri())
                                .addFormDataPart("grant_type", mClientInfo.getGrantType())
                                .addFormDataPart("code_verifier", mClientInfo.getCodeVerifier())
                                .addFormDataPart("client_secret", mClientInfo.getClientSecret())
                                .build();

                        Request request = new Request.Builder()
                                .url(mClientInfo.getTokenUri())
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .method("POST", RequestBody.create(null, new byte[0]))
                                .post(requestBody)
                                .build();


                        Response response = client.newCall(request).execute();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                Gson gson = new Gson();
                                onPostExecute(gson.fromJson(responseBody, AccountInfo.class));
                            } else {
                                mThrowable = new Exception(response.body().string());
                                onPostExecute(null);
                            }
                        } else {
                            mThrowable = new Exception("Request token response is null");
                            onPostExecute(null);
                        }
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

    protected void onPostExecute(AccountInfo accountInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (accountInfo == null) {
                    mTaskInterface.onFailure(mThrowable);
                } else {
                    mTaskInterface.receivedToken(accountInfo);
                }
            }
        });
    }
}
