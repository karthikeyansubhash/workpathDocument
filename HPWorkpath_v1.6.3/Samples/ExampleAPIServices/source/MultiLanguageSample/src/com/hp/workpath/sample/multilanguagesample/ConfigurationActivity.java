// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.multilanguagesample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;

public class ConfigurationActivity extends AppCompatActivity {

    private Button mMessageButton;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // find the button
        findViewElements();
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
        mMessageButton = findViewById(R.id.messageButton);
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfigurationActivity.this, getString(R.string.message, "[Office]"), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

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

        mAlertDialog = new AlertDialog.Builder(ConfigurationActivity.this)
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