// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.AuthenticationAttributes;
import com.hp.workpath.api.access.SignInAction;
import com.hp.workpath.api.access.UserOverridesAttributes;
import com.hp.workpath.api.access.UserPreferencesAttributes;
import com.hp.workpath.sample.accessoryagentsample.task.InitializationTask;

public class AuthenticationActivity extends Activity {

    public static final String TAG = MainActivity.TAG;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private TextView mVersionTextView;

    private Button mSignInButton;
    private Button mFailedButton;
    private Button mHomeButton;
    private Button mBackButton;

    private Button mGenerateButton;
    private Button mRunButton;

    private EditText mIdEdt;
    private EditText mPasswordEdt;
    private EditText mResultEdt;

    boolean isInitializedSDK = false;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mInitializationTask = new InitializationTask(this, initializeInterface);
        mInitializationTask.taskExecute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    private void initView() {
        mVersionTextView = findViewById(R.id.versionTextView);
        mSignInButton = findViewById(R.id.signInButton);
        mFailedButton = findViewById(R.id.failButton);
        mHomeButton = findViewById(R.id.homeButton);
        mBackButton = findViewById(R.id.backButton);
        mGenerateButton = findViewById(R.id.generateButton);
        mRunButton = findViewById(R.id.runButton);
        mSignInButton.setOnClickListener(buttonClickListener);
        mFailedButton.setOnClickListener(buttonClickListener);
        mHomeButton.setOnClickListener(buttonClickListener);
        mBackButton.setOnClickListener(buttonClickListener);
        mGenerateButton.setOnClickListener(manualClickListener);
        mRunButton.setOnClickListener(manualClickListener);
        mIdEdt = findViewById(R.id.idEditText);
        mPasswordEdt = findViewById(R.id.passwordEditText);
        mResultEdt = findViewById(R.id.resultEditText);

        setVersion();
    }

    private void setVersion() {
        String version = "";
        try {
            Workpath sdkInfo = Workpath.getInstance();
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = getString(R.string.version, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            handleException(t);
        }
        mVersionTextView.setText(version);
    }

    View.OnClickListener manualClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(mIdEdt.getText().toString())) {
                mIdEdt.setText("Tester");
            }
            if (v == mRunButton) {
                if (TextUtils.isEmpty(mResultEdt.getText().toString())) {
                    Toast.makeText(AuthenticationActivity.this, "Please fill user information for sign in.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String data = mResultEdt.getText().toString();
                        Gson gson = new Gson();
                        AuthenticationAttributes generatedData = gson.fromJson(data, AuthenticationAttributes.class);

                        Result result = new Result();
                        SignInAction signInAction = new SignInAction(SignInAction.Action.SUCCESS, null);
                        AccessService.signIn(AuthenticationActivity.this, signInAction, generatedData, result);
                        Logger.showResult(AuthenticationActivity.this, "AccessService.signIn", result);
                    } catch (Throwable t) {
                        Logger.showResult(AuthenticationActivity.this, "AccessService.signIn " + t.getMessage());
                    }
                }
            } else if (v == mGenerateButton) {
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                    mResultEdt.setText(gson.toJson(getWindowsData()));
                } catch (Throwable t) {
                    Logger.showResult(AuthenticationActivity.this, "AuthenticationAttributes.WindowsBuilder " + t.getMessage());
                }
            }
        }
    };

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(mIdEdt.getText().toString())) {
                mIdEdt.setText("Tester");
            }
            try {
                Result result = new Result();
                if (v == mSignInButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.SUCCESS, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, getWindowsData(), result);
                } else if (v == mFailedButton) {
                    String failureMessage = "This is failure message from AccessoryAgent Sample.";
                    SignInAction signInAction = new SignInAction(SignInAction.Action.FAIL, failureMessage);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                } else if (v == mHomeButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.HOME, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                }  else if (v == mBackButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.BACK, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                }
                Logger.showResult(AuthenticationActivity.this, "AccessService.signIn", result);
            } catch (Throwable t) {
                Logger.showResult(AuthenticationActivity.this, "AccessService.signIn " + t.getMessage());
            }
        }
    };

    private AuthenticationAttributes getWindowsData() throws Throwable {
        String id = mIdEdt.getText().toString();
        String password = mPasswordEdt.getText().toString();
        UserOverridesAttributes userOverridesAttributes = null;
        UserPreferencesAttributes userPreferencesAttributes = null;
        try {
            userOverridesAttributes = new UserOverridesAttributes.Builder()
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
        } catch (Throwable t) {
            Logger.showResult(AuthenticationActivity.this, "UserOverridesAttributes.Builder " + t.getMessage());
        }

        try {
            userPreferencesAttributes = new UserPreferencesAttributes.Builder()
                    .setAutoLaunchAppAccessPointId(getString(R.string.app_access_point_id))
                    .setLanguageCode(getString(R.string.language_code))
                    .build();
        } catch (Throwable t) {
            Logger.showResult(AuthenticationActivity.this, "UserOverridesAttributes.Builder " + t.getMessage());
        }

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

    InitializationTask.InitializeInterface initializeInterface = new InitializationTask.InitializeInterface() {
        @Override
        public void handleComplete(InitializationTask.InitStatus initStatus) {
            isInitializedSDK = true;
        }

        @Override
        public void handleException(Throwable t) {
            AuthenticationActivity.this.handleException(t);
        }
    };

    private void handleException(Throwable t) {
        String errorMsg;
        if (t instanceof SsdkUnsupportedException) {
            switch (((SsdkUnsupportedException) t).getType()) {
                case SsdkUnsupportedException.LIBRARY_NOT_INSTALLED:
                case SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED:
                    errorMsg = getString(R.string.sdk_support_missing);
                    break;
                default:
                    errorMsg = getString(R.string.unknown_error);
            }
        } else {
            errorMsg = t.getMessage();
        }

        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}
