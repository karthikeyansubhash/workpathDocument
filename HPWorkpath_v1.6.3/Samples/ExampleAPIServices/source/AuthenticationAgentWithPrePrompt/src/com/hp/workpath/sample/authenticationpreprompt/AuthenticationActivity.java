// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authenticationpreprompt;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.hp.workpath.sample.authenticationpreprompt.task.InitializationTask;

public class AuthenticationActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "AuthAgentPre";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private TextView mVersionTextView;

    private Spinner mAuthTypeSpinner;
    private Button mSignInButton;
    private Button mFailButton;
    private Button mHomeButton;
    private Button mBackButton;

    private Button mGenerateButton;
    private Button mRunButton;

    private EditText mIdEditText;
    private EditText mPasswordEditText;
    private EditText mResultEditText;

    boolean isInitializedSDK = false;

    private AlertDialog mAlertDialog;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

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
        mFailButton = findViewById(R.id.failButton);
        mHomeButton = findViewById(R.id.homeButton);
        mBackButton = findViewById(R.id.backButton);
        mGenerateButton = findViewById(R.id.generateButton);
        mRunButton = findViewById(R.id.runButton);
        mSignInButton.setOnClickListener(buttonClickListener);
        mFailButton.setOnClickListener(buttonClickListener);
        mRunButton.setOnClickListener(manualClickListener);
        mHomeButton.setOnClickListener(buttonClickListener);
        mBackButton.setOnClickListener(buttonClickListener);
        mGenerateButton.setOnClickListener(manualClickListener);
        mIdEditText = findViewById(R.id.idEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mResultEditText = findViewById(R.id.resultEditText);
        mAuthTypeSpinner = findViewById(R.id.authTypeSpinner);

        setVersion();
    }

    private void setVersion() {
        if (!SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
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
    }

    View.OnClickListener manualClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(mIdEditText.getText().toString())) {
                mIdEditText.setText("Tester");
            }
            try {
                if (v == mRunButton) {
                    if (TextUtils.isEmpty(mResultEditText.getText().toString())) {
                        Toast.makeText(AuthenticationActivity.this, "Please fill user information for sign in.", Toast.LENGTH_SHORT).show();
                    } else {
                        String data = mResultEditText.getText().toString();
                        Gson gson = new Gson();
                        AuthenticationAttributes generatedData = gson.fromJson(data, AuthenticationAttributes.class);

                        Result result = new Result();
                        SignInAction signInAction = new SignInAction(SignInAction.Action.SUCCESS, null);
                        AccessService.signIn(AuthenticationActivity.this, signInAction, generatedData, result);

                        if (result.getCode() != Result.RESULT_OK) {
                            Logger.showResult(AuthenticationActivity.this, "AccessService.signIn", result);
                        }
                    }
                } else if (v == mGenerateButton) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                    mResultEditText.setText(gson.toJson(getWindowsData()));
                }
            } catch (Throwable t) {
                Logger.showResult(AuthenticationActivity.this, "AccessService.signIn " + t.getMessage());
            }
        }
    };

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(mIdEditText.getText().toString())) {
                mIdEditText.setText("Tester");
            }
            try {
                Result result = new Result();
                if (v == mSignInButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.SUCCESS, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, getWindowsData(), result);
                } else if (v == mFailButton) {
                    String failureMessage = "This is failure message from AuthenticationAgentPrePrompt Sample.";
                    SignInAction signInAction = new SignInAction(SignInAction.Action.FAIL, failureMessage);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                } else if (v == mHomeButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.HOME, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                } else if (v == mBackButton) {
                    SignInAction signInAction = new SignInAction(SignInAction.Action.BACK, null);
                    AccessService.signIn(AuthenticationActivity.this, signInAction, null, result);
                }

                if (result.getCode() != Result.RESULT_OK) {
                    Logger.showResult(AuthenticationActivity.this, "AccessService.signIn", result);
                }

            } catch (Throwable t) {
                Logger.showResult(AuthenticationActivity.this, "AccessService.signIn " + t.getMessage());
            }
        }
    };

    private AuthenticationAttributes getWindowsData() throws Exception {
        String id = mIdEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

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

    public void handleException(Throwable t) {
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
        Log.e(TAG, errorMsg);
        mAlertDialog = new AlertDialog.Builder(AuthenticationActivity.this)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: method");
        if (SCREEN_4_3_INCH.equals((String) findViewById(R.id.container).getTag())) {
            getMenuInflater().inflate(R.menu.version, menu);
            Log.d(TAG, "onCreateOptionsMenu: true");
            MenuItem versionMenu = menu.findItem(R.id.menuVersion);
            try {
                Workpath sdkInfo = Workpath.getInstance();
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionMenu.setTitle(getString(
                        R.string.version,
                        pInfo.versionName,
                        (int) pInfo.getLongVersionCode(),
                        sdkInfo.getVersionName(),
                        sdkInfo.getVersionCode()
                ));
            } catch (PackageManager.NameNotFoundException e) {
                Logger.showResult(this,e.getMessage());
            }
        } else {
            Log.d(TAG, "onCreateOptionsMenu: else");
        }
        return true;
    }
}
