// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.device.settings.DeviceSettingsService;
import com.hp.workpath.sample.deviceinfosample.model.DeviceInfo;
import com.hp.workpath.sample.deviceinfosample.task.DeviceAttrReaderTask;
import com.hp.workpath.sample.deviceinfosample.task.InitializationTask;

import java.util.EnumMap;
import java.util.Map;

/**
 * Main activity for DeviceInfo Sample.
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "DevInfo";

    /**
     * Map {@link DeviceInfo}
     * Store references to summary TextViews to provide information
     */
    private final EnumMap<DeviceInfo, TextView> mSummaries = new EnumMap<>(DeviceInfo.class);

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private Button mGetInformationButton;

    private Button mEnablePortsButton;
    private Button mDisablePortsButton;

    private View mContainer;
    private ProgressBar mProgress;

    private AlertDialog mAlertDialog;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            findViewById(R.id.fab_menu).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.buttonBarLayout).getVisibility() == View.VISIBLE) {
                        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) findViewById(R.id.dataContainer).getLayoutParams();
                        param.setMargins(0, 0, 0, 0);
                        findViewById(R.id.dataContainer).setLayoutParams(param);
                        findViewById(R.id.buttonBarLayout).setVisibility(View.GONE);
                    } else {
                        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) findViewById(R.id.dataContainer).getLayoutParams();
                        param.setMargins(0, 0, 0, 40);
                        findViewById(R.id.dataContainer).setLayoutParams(param);
                        findViewById(R.id.buttonBarLayout).setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // find the text and button
        findViewElements();

        // add click listener to call the MFP
        addListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mContainer.setEnabled(false);

        // call init task
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
        mProgress = findViewById(R.id.progressBar);
        mContainer = findViewById(R.id.container);
        mGetInformationButton = findViewById(R.id.getInformationButton);
        mEnablePortsButton = findViewById(R.id.enablePortButton);
        mDisablePortsButton = findViewById(R.id.disablePortButton);

        // Store views summaries and ids
        for (DeviceInfo item : DeviceInfo.values()) {
            ViewGroup itemLayout = findViewById(item.getItemId());
            ((TextView) itemLayout.findViewById(R.id.titleTextView)).setText(item.getTitleId());
            mSummaries.put(item, (TextView) itemLayout.findViewById(R.id.summaryTextView));
        }
    }

    private void addListener() {
        mGetInformationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                new DeviceAttrReaderTask(MainActivity.this).taskExecute();
                showProgress(View.VISIBLE);
            }
        });

        mEnablePortsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DeviceSettingsService.isSupported(MainActivity.this)) {
                    Logger.showResult(MainActivity.this, "DeviceSettingsService is not supported");
                } else {
                    Result result = new Result();
                    DeviceSettingsService.enableExternalPrinting(MainActivity.this, result);

                    if (result.getCode() == Result.RESULT_OK) {
                        Logger.showResult(MainActivity.this, getString(R.string.enable_ports_deprecated));
                    } else {
                        Logger.showResult(MainActivity.this, "DeviceSettingsService.enableExternalPrinting", result);
                    }
                }
            }
        });

        mDisablePortsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DeviceSettingsService.isSupported(MainActivity.this)) {
                    Logger.showResult(MainActivity.this, "DeviceSettingsService is not supported");
                } else {
                    Result result = new Result();
                    DeviceSettingsService.disableExternalPrinting(MainActivity.this, result);

                    if (result.getCode() == Result.RESULT_OK) {
                        Logger.showResult(MainActivity.this, "Ports disabled");
                    } else {
                        Logger.showResult(MainActivity.this, "DeviceSettingsService.disableExternalPrinting", result);
                    }
                }
            }
        });
    }

    public void handleComplete() {
        showProgress(View.VISIBLE);
        mContainer.setEnabled(true);
        new DeviceAttrReaderTask(MainActivity.this).taskExecute();
    }

    public void showProgress(int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    public void handleUpdate(Map<DeviceInfo, String> result) {
        // Fill device description with received info
        for (DeviceInfo item : DeviceInfo.values()) {
            if (result.containsKey(item)) {
                if (getApplicationContext() != null) {
                    mSummaries.get(item).setText(result.get(item));
                }
            }
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
}