// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample.service;

import android.util.Log;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.AbstractAuthenticationService;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.AuthenticationAttributes;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.api.access.SignInAction;
import com.hp.workpath.api.access.UserOverridesAttributes;
import com.hp.workpath.api.access.UserPreferencesAttributes;
import com.hp.workpath.sample.accessoryagentsample.ActionUtil;
import com.hp.workpath.sample.accessoryagentsample.Logger;
import com.hp.workpath.sample.accessoryagentsample.MainActivity;
import com.hp.workpath.sample.accessoryagentsample.R;
import com.hp.workpath.sample.accessoryagentsample.task.InitializationTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AccessoryAgentService extends AbstractAuthenticationService {

    private static final String TAG = MainActivity.TAG + "A";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    BlockingQueue<Boolean> initializedSDKQueue = new ArrayBlockingQueue<>(1);

    @Override
    public void onCreate() {
        super.onCreate();
        initializedSDK();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mInitializationTask != null) {
            mInitializationTask.cancel();
            mInitializationTask = null;
        }
    }

    @Override
    protected void onSignIn(Principal principal) {
        Log.i(TAG, "Received sign in event: " + principal.getUsername());
    }

    @Override
    protected void onSignOut() {
        Log.i(TAG, "Received sign out event");
    }

    @Override
    protected void onPrePrompt() {
        Log.i(TAG, "onPrePrompt()");
        try {
            if (initializedSDKQueue.take()) {
                Result result = new Result();
                SignInAction signInAction = new SignInAction(ActionUtil.getAction(this), null);
                AccessService.signIn(AccessoryAgentService.this, signInAction, getWindowsData(), result);
                showLogResult("AccessService.signIn", result);
                if (result.getCode() != Result.RESULT_OK) {
                    SignInAction failAction = new SignInAction(SignInAction.Action.FAIL, result.getCause());
                    AccessService.signIn(AccessoryAgentService.this, failAction, null, null);
                }
            } else {
                Log.i(TAG, "isInitializedSDK is false");
            }
        } catch (Throwable t) {
            showLogResult("AccessService.signIn " + t.getMessage());
        }
    }

    private AuthenticationAttributes getWindowsData() throws Throwable {
        String id = "Tester";
        String password = "password";

        UserOverridesAttributes userOverridesAttributes = new UserOverridesAttributes.Builder()
                .addBccAddress(getString(R.string.bcc_address_email_01), getString(R.string.bcc_address_name_01))
                .addBccAddress(getString(R.string.bcc_address_email_02), getString(R.string.bcc_address_name_02))
                .addCcAddress(getString(R.string.cc_address_email_01), getString(R.string.cc_address_name_01))
                .addCcAddress(getString(R.string.cc_address_email_02), getString(R.string.cc_address_name_02))
                .setFrom(getString(R.string.from_address_email), getString(R.string.from_address_name))
                .addToAddress(getString(R.string.to_address_email_01), getString(R.string.to_address_name_01))
                .addToAddress(getString(R.string.to_address_email_02), getString(R.string.to_address_name_02))
                .setMessage(getString(R.string.email_message))
                .setSubject(getString(R.string.email_subject))
                .setFaxBillingCode(getString(R.string.fax_billing_code))
                .setFaxCompanyName(getString(R.string.fax_company_name))
                .build();

        UserPreferencesAttributes userPreferencesAttributes = new UserPreferencesAttributes.Builder()
                .setAutoLaunchAppAccessPointId(getString(R.string.app_access_point_id))
                .setLanguageCode(getString(R.string.language_code))
                .build();

        return new AuthenticationAttributes.WindowsBuilder()
                .setFullyQualifiedName(getString(R.string.value_fully_qualified_name, id))
                .setDisplayName(id)
                .setPassword(password)
                .setUserDomain(getString(R.string.value_domain))
                .setUserEmail(getString(R.string.value_user_email, id))
                .setUserName(id)
                .setUserPrincipalName(id)
                .setHomeFolderPath(getString(R.string.value_home_folder_path))
                .addUserProperty(getString(R.string.value_user_property_key_01), getString(R.string.value_user_property_value_01))
                .addUserProperty(getString(R.string.value_user_property_key_02), getString(R.string.value_user_property_value_02))
                .setUserOverridesAttributes(userOverridesAttributes)
                .setUserPreferencesAttributes(userPreferencesAttributes)
                .build();
    }

    private void initializedSDK() {
        if (initializedSDKQueue.isEmpty()) {
            mInitializationTask = new InitializationTask(this, initializeInterface);
            mInitializationTask.taskExecute();
            mInitializationTask.setBlockingQueue(initializedSDKQueue);
        }
    }

    InitializationTask.InitializeInterface initializeInterface = new InitializationTask.InitializeInterface() {
        @Override
        public void handleComplete(InitializationTask.InitStatus initStatus) {
            if (initStatus != InitializationTask.InitStatus.NO_ERROR) {
                Log.e(TAG, getString(R.string.sdk_support_missing));
            }
        }

        @Override
        public void handleException(Throwable t) {
            Log.e(TAG, "Workpath.initialize exception " + t.getMessage());
        }
    };

    private void showLogResult(String msg) {
        showLogResult(msg, null);
    }

    private void showLogResult(String msg, Result result) {
        if (result != null) {
            msg = msg + Logger._NF + Logger.build(result);
            if (result.getCode() == Result.RESULT_FAIL) {
                Log.e(MainActivity.TAG, msg);
            } else {
                Log.d(MainActivity.TAG, msg);
            }
        } else {
            Log.d(MainActivity.TAG, msg);
        }
    }
}
