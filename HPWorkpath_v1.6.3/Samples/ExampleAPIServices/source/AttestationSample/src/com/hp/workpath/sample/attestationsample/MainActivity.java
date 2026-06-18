// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.attestationsample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.attestation.AppToken;
import com.hp.workpath.sample.attestationsample.task.GetAppTokenTask;
import com.hp.workpath.sample.attestationsample.task.InitializationTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Attestation";

    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";


    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private AlertDialog mAlertDialog;
    private TextView mTokenTextView;
    private TextView mExpiresInTextView;
    private TextView mResultTextView;
    private Button mGetAppTokenButton;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }


        // find the text and button
        findViewElements();

        // add click listener to call the MFP
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mInitializationTask = new InitializationTask(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.version, menu);
        MenuItem versionMenu = menu.findItem(R.id.menuVersion);
        String version = "";
        try {
            Workpath sdkInfo = Workpath.getInstance();
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = getString(R.string.version, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            handleException(t);
        }
        versionMenu.setTitle(version);
        return true;
    }

    private void findViewElements() {
        mTokenTextView = findViewById(R.id.tokenTextView);
        mExpiresInTextView = findViewById(R.id.expiresInTextView);
        mResultTextView = findViewById(R.id.resultTextView);
        mGetAppTokenButton = findViewById(R.id.getAppTokenButton);
        mProgressBar = findViewById(R.id.progressBar);
    }

    private void addListener() {
        mGetAppTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppToken();
            }
        });
    }

    private void getAppToken() {
        showProgressBar(View.VISIBLE);
        new GetAppTokenTask(MainActivity.this).taskExecute();
    }

    public void showProgressBar(int visibility) {
        mProgressBar.setVisibility(visibility);
    }

    public void getAppTokenComplete(AppToken appToken, Result result) {
        if (appToken != null) {
            mTokenTextView.setText(appToken.getAppToken());
            mExpiresInTextView.setText(Long.toString(appToken.getExpiresIn()));
        } else {
            mTokenTextView.setText(getString(R.string.na));
            mExpiresInTextView.setText(getString(R.string.na));
        }

        if (result != null) {
            if (result.getCode() == Result.RESULT_OK) {
                mResultTextView.setText(getString(R.string.result_success, "Result.RESULT_OK"));
            } else if (result.getCode() == Result.RESULT_FAIL) {
                mResultTextView.setText(getString(R.string.result_failed, "Result.RESULT_FAIL", result.getErrorCode().name(), result.getCause()));
            }
        }
    }

    public void handleComplete() {
        mGetAppTokenButton.setEnabled(true);
    }

    /**
     * Exception in could be because of following reasons
     * <ol>
     * <li>Library is not installed</li>
     * <li>Library update is needed</li>
     * <li>Version issue, unsupported</li>
     * </ol>
     */
    public void handleException(final Throwable t) {
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
