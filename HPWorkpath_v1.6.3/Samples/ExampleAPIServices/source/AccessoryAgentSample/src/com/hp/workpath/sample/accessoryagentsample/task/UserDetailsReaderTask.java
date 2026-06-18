// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample.task;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.sample.accessoryagentsample.Logger;
import com.hp.workpath.sample.accessoryagentsample.MainActivity;
import com.hp.workpath.sample.accessoryagentsample.R;
import com.hp.workpath.sample.accessoryagentsample.model.UserDetails;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDetailsReaderTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result mResult;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public UserDetailsReaderTask(MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.mResult = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Resources res = mContextRef.get().getResources();
                        Map<UserDetails, String> values = new HashMap<>();

                        values.put(UserDetails.FULLY_QUALIFIED_NAME, res.getString(R.string.na));
                        values.put(UserDetails.PRINCIPAL_ID, res.getString(R.string.na));
                        values.put(UserDetails.DOMAIN, res.getString(R.string.na));
                        values.put(UserDetails.PROVIDER, res.getString(R.string.na));
                        values.put(UserDetails.USER_NAME, res.getString(R.string.na));
                        values.put(UserDetails.IS_AUTHENTICATED, res.getString(R.string.na));

                        // get user principal information using AccessService API
                        Principal currentUser = AccessService.getCurrentPrincipal(mContextRef.get().getApplicationContext(), mResult);

                        // first check whether Result is fine or not
                        if (mResult.getCode() == Result.RESULT_OK && currentUser != null) {
                            values.put(UserDetails.FULLY_QUALIFIED_NAME, currentUser.getFullyQualifiedName());
                            values.put(UserDetails.PRINCIPAL_ID, currentUser.getPrincipalId());
                            values.put(UserDetails.DOMAIN, currentUser.getDomain());
                            values.put(UserDetails.PROVIDER, currentUser.getProvider());
                            values.put(UserDetails.USER_NAME, currentUser.getUsername());
                            values.put(UserDetails.IS_AUTHENTICATED, String.valueOf(currentUser.isAuthenticated()));
                            onPostExecute(values);
                            return;
                        }
                    } catch (Throwable t) {
                        Logger.showResult(null, "AccessService.getCurrentPrincipal is failed:" + t.getMessage());
                        onPostExecute(null);
                        executor.shutdown();
                    }
                    onPostExecute(null);
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "AccessService.getCurrentPrincipal is failed:" + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(Map<UserDetails, String> userResult) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                if (userResult != null && mResult.getCode() == Result.RESULT_OK) {
                    mContextRef.get().handleUpdate(userResult);
                } else {
                    Logger.showResult(mContextRef.get(), "AccessService.getCurrentPrincipal", mResult);
                }
            }
        });
    }
}
