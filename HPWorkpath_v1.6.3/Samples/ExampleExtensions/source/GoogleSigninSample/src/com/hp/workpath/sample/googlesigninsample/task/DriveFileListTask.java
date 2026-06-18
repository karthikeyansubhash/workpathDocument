// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveFileListTask {

    private final WeakReference<Context> mContextRef;
    private String mCredential;
    private Throwable mThrowable;
    private DriveTaskInterface mTaskInterface;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DriveFileListTask(Context context, String clientSecret, DriveTaskInterface taskInterface) {
        this.mContextRef = new WeakReference<>(context);
        this.mCredential = clientSecret;
        this.mTaskInterface = taskInterface;
    }

    public interface DriveTaskInterface {
        void onFailure(Throwable t);

        void onResponse(List<File> files);
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Drive service = getDriveService(mContextRef.get(), mCredential);
                        FileList result = service.files().list().execute();
                        onPostExecute(result.getFiles());
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


    protected void onPostExecute(List<File> files) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mThrowable != null) {
                    mTaskInterface.onFailure(mThrowable);
                } else {
                    mTaskInterface.onResponse(files);
                }
            }
        });
    }

    /**
     * Build and return an authorized Drive client service.
     *
     * @return an authorized Drive client service
     * @throws IOException
     */
    private Drive getDriveService(Context context, String credentialJson) throws Throwable {
        Credential credential = authorize(credentialJson);

        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, null)
                .setHttpRequestInitializer(credential)
                .setApplicationName(context.getApplicationInfo().name)
                .build();
    }

    private GoogleCredential authorize(String credentialJson) throws IOException {
        Gson gson = new Gson();
        AccountInfo accountInfo = gson.fromJson(credentialJson, AccountInfo.class);

        GoogleCredential credential = new GoogleCredential();
        credential.setAccessToken(accountInfo.getAccessToken());
        return credential;
    }
}
