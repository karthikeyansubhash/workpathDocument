// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.deviceusagesample.fragment.DeviceUsageFragment;
import com.hp.workpath.sample.deviceusagesample.task.InitializationTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "DeviceUsage";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private DeviceUsageFragment mDeviceUsageFragment;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceUsageFragment = new DeviceUsageFragment();
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

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        }
    }

    public void handleComplete() {
        if (getApplicationContext() != null) {
            replaceFragment(mDeviceUsageFragment);
            Logger.showResult(MainActivity.this, getString(R.string.initialized));
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
