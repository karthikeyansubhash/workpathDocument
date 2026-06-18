// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample.task;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.sample.accesssample.Logger;
import com.hp.workpath.sample.accesssample.MainActivity;
import com.hp.workpath.sample.accesssample.R;
import com.hp.workpath.sample.accesssample.model.UserDetails;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDetailsReaderTask {

    private final WeakReference<MainActivity> mContextRef;
    private Result result;

    private Principal currentUser;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public UserDetailsReaderTask(MainActivity context) {
        this.mContextRef = new WeakReference<>(context);
        this.result = new Result();
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Resources res = mContextRef.get().getResources();
                        Map<UserDetails, String> values = new HashMap<>();

                        values.put(UserDetails.VERSION, res.getString(R.string.na));
                        values.put(UserDetails.FULLY_QUALIFIED_NAME, res.getString(R.string.na));
                        values.put(UserDetails.PRINCIPAL_ID, res.getString(R.string.na));
                        values.put(UserDetails.DOMAIN, res.getString(R.string.na));
                        values.put(UserDetails.PROVIDER, res.getString(R.string.na));
                        values.put(UserDetails.USER_NAME, res.getString(R.string.na));
                        values.put(UserDetails.PASSWORD, res.getString(R.string.na));
                        values.put(UserDetails.USER_EMAIL, res.getString(R.string.na));
                        values.put(UserDetails.IS_AUTHENTICATED, res.getString(R.string.na));
                        values.put(UserDetails.SIMPLE_AUTHORITIES, res.getString(R.string.na));
                        values.put(UserDetails.IS_HP_CLOUD_USER, res.getString(R.string.na));

                        values.put(UserDetails.VERSION, res.getString(R.string.version,
                                Workpath.getInstance().getVersionName(),
                                Workpath.getInstance().getVersionCode()));

                        // get user principal information using AccessService API
                        currentUser = AccessService.getCurrentPrincipal(mContextRef.get().getApplicationContext(), result);

                        // first check whether Result is fine or not
                        if (result.getCode() == Result.RESULT_OK && currentUser != null) {
                            values.put(UserDetails.FULLY_QUALIFIED_NAME, currentUser.getFullyQualifiedName());
                            values.put(UserDetails.PRINCIPAL_ID, currentUser.getPrincipalId());
                            values.put(UserDetails.DOMAIN, currentUser.getDomain());
                            values.put(UserDetails.PROVIDER, currentUser.getProvider());
                            values.put(UserDetails.USER_NAME, currentUser.getUsername());
                            values.put(UserDetails.PASSWORD, currentUser.getPassword());
                            values.put(UserDetails.USER_EMAIL, currentUser.getUserEmail());
                            values.put(UserDetails.IS_ADMIN, String.valueOf(currentUser.isAdmin()));
                            values.put(UserDetails.IS_AUTHENTICATED, String.valueOf(currentUser.isAuthenticated()));
                            values.put(UserDetails.IS_HP_CLOUD_USER, String.valueOf(currentUser.isHPCloudUser()));
                            values.put(UserDetails.IS_GUEST_USER, String.valueOf(currentUser.isGuestUser()));
                            values.put(UserDetails.IS_DEVICE_USER, String.valueOf(currentUser.isDeviceUser()));
                            values.put(UserDetails.IS_SERVICE_USER, String.valueOf(currentUser.isServiceUser()));
                            values.put(UserDetails.IS_SMART_CARD_USER, String.valueOf(currentUser.isSmartCardUser()));
                            values.put(UserDetails.PROVIDER_UUID, String.valueOf(currentUser.getProviderUUID()));
                            values.put(UserDetails.SIMPLE_AUTHORITIES, currentUser.getSimpleAuthorities().toString());
                        } else {
                            onPostExecute(null);
                            return;
                        }
                        onPostExecute(values);
                    } catch (Throwable t) {
                        Logger.showResult(null, "AccessService.getCurrentPrincipal is failed:" + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
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
                if (userResult != null && result.getCode() == Result.RESULT_OK) {
                    Logger.showResult(mContextRef.get(), "AccessService.getCurrentPrincipal(): " + Logger.build(currentUser));
                    mContextRef.get().handleUpdate(userResult, currentUser);
                } else {
                    Logger.showResult(mContextRef.get(), Logger.build(result));
                }
            }
        });
    }
}
