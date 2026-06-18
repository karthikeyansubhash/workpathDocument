// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.eventnotificationsample.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.sample.eventnotificationsample.Logger;
import com.hp.workpath.sample.eventnotificationsample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDetailsReaderTask {

    private final WeakReference<Context> mContextRef;

    private final WeakReference<ResultHandler> mResultHandlerRef;

    private Result result;

    private Principal currentUser;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public UserDetailsReaderTask(Context context, ResultHandler handler) {
        this.mContextRef = new WeakReference<>(context);
        this.mResultHandlerRef = new WeakReference<>(handler);
        this.result = new Result();
    }

    public void taskExecute() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // get user principal information using AccessService API
                    currentUser = AccessService.getCurrentPrincipal(mContextRef.get(), result);

                    // first check whether Result is fine or not
                    if (result.getCode() == Result.RESULT_OK && currentUser != null) {
                        onPostExecute();
                    }
                } catch (Throwable t) {
                    Logger.showResult(null, "AccessService.getCurrentPrincipal is failed:" + t.getMessage());
                    executor.shutdown();
                    onPostExecute();
                }
            }
        });
    }

    private void onPostExecute() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity activity = null;
                if (mContextRef.get() instanceof MainActivity)
                    activity = (MainActivity) mContextRef.get();

                if (currentUser != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(activity, "AccessService.getCurrentPrincipal(): " + Logger.build(currentUser));
                    mResultHandlerRef.get().handleUpdate(currentUser.toString());
                } else {
                    Logger.showResult(activity, Logger.build(result));
                }
            }
        });
    }
}
