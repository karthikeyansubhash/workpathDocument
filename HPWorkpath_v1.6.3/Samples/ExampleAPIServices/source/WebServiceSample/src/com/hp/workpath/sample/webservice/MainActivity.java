// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.webservice;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.JobService;
import com.hp.workpath.api.job.JobletAttributes;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrinterService;

import org.w3c.dom.Text;

/**
 * Main activity for Print Sample.
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Linkbus";

    private AlertDialog mAlertDialog;

    private TextView mMethod;

    private TextView mRequestHeader;

    private TextView mRequestBody;

    private EditText mResponseBody;

    private static TextViewHandler mHandler;

    private static String mResponse;

    public static final String KEY_METHOD = "method";
    public static final String KEY_HEADER = "header";
    public static final String KEY_BODY = "body";

    class TextViewHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle == null)
                return;

            if (mMethod != null) {
                mMethod.setText(bundle.getString(KEY_METHOD));
            }
            if (mRequestHeader != null) {
                mRequestHeader.setText(bundle.getString(KEY_HEADER));
            }
            if (mRequestBody != null) {
                mRequestBody.setText(bundle.getString(KEY_BODY));
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new TextViewHandler();
        mMethod = (TextView) findViewById(R.id.method_name);
        mRequestHeader = (TextView) findViewById(R.id.request_header);
        mRequestBody = (TextView) findViewById(R.id.request_body);
        mResponseBody = (EditText) findViewById(R.id.response_body);
        mResponseBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mResponse = s.toString();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        mResponse = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestHeader = null;
        mRequestBody = null;
        mResponseBody = null;
        mHandler = null;
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

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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

    public static void setRequest(String method, String header, String body) {
        if (mHandler == null) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_METHOD, method);
        bundle.putString(KEY_HEADER, header);
        if (body != null) {
            Log.e(TAG, "body = " + body);
        }
        bundle.putString(KEY_BODY, body);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public static String getResponse() {
        return mResponse;
    }
}
