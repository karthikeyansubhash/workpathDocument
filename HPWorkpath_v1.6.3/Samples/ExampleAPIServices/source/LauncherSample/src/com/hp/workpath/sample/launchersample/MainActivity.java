// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.launchersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.ReportEventInfo;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.api.accessory.hid.EventCode;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo;
import com.hp.workpath.api.launcher.LaunchAction;
import com.hp.workpath.api.launcher.LauncherService;
import com.hp.workpath.sample.launchersample.task.DeviceInformationTask;
import com.hp.workpath.sample.launchersample.task.InitializationTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Launcher";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private AlertDialog mAlertDialog;
    private EditText mLinkAppUuidEditText;
    private Button mGoHomeButton;
    private Button mLinkAppLaunchButton;
    private Button mOpenMenuViewButton;

    private Spinner mAccessorySpinner;
    private Button mGetOwnedButton;
    private Button mResendButton;
    private Button mOpenButton;
    private TextView mAccessoryDataTextView;
    private TextView mHostnameTextView;

    List<AccessoryInfo> accessories;
    String accessoryContextId;

    IntentFilter filter = new IntentFilter();

    private SdkAccessoryReadyReceiver mSdkAccessoryReadyReceiver;
    private AccessoryObserver mAccessoryObserver;
    private AccessoryStartObserver mAccessoryStartObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the text and button
        findViewElements();

        // add click listener to call the MFP
        addListener();

        filter.addAction("com.hp.jetadvantage.link.action.WORKPATH_READY");
        filter.addAction("com.hp.jetadvantage.link.action.ACCESSORY_READY");
        mSdkAccessoryReadyReceiver = new SdkAccessoryReadyReceiver();

        Handler handler = new Handler();
        mAccessoryObserver = new AccessoryObserver(handler);
        mAccessoryStartObserver = new AccessoryStartObserver(handler);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();

        registerReceiver(mSdkAccessoryReadyReceiver, filter);
        mAccessoryObserver.register(getApplicationContext());
        mAccessoryStartObserver.register(getApplicationContext());
        new DeviceInformationTask(this).taskExecute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        unregisterReceiver(mSdkAccessoryReadyReceiver);
        mAccessoryObserver.unregister(getApplicationContext());
        mAccessoryStartObserver.unregister(getApplicationContext());

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
        mGoHomeButton = findViewById(R.id.goHomeButton);
        mLinkAppUuidEditText = findViewById(R.id.linkAppUuidEditText);
        mLinkAppLaunchButton = findViewById(R.id.linkAppLaunchButton);
        mOpenMenuViewButton = findViewById(R.id.menuViewButton);
        mGetOwnedButton = findViewById(R.id.getOwnedButton);
        mResendButton = findViewById(R.id.resendButton);
        mAccessorySpinner = findViewById(R.id.accessorySpinner);
        mOpenButton = findViewById(R.id.openButton);
        mAccessoryDataTextView = findViewById(R.id.accessoryDataTextview);
        mHostnameTextView = findViewById(R.id.hostnameTextView);
    }

    private void addListener() {
        mGoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
        mLinkAppLaunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mLinkAppUuidEditText.getText().toString())) {
                    Logger.showResult(MainActivity.this, getString(R.string.uuid_missing));
                } else {
                    String uuid = mLinkAppUuidEditText.getText().toString();
                    launchApplication(uuid);
                }
            }
        });
        mLinkAppUuidEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before < count) {
                    String text = s.toString();
                    if (text.length() == 8
                            || text.length() == 13
                            || text.length() == 18
                            || text.length() == 23) {
                        mLinkAppUuidEditText.setText(text + "-");
                        mLinkAppUuidEditText.setSelection(text.length() + 1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mOpenMenuViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        mGetOwnedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOwnedAccessory();
            }
        });
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAccessorySpinner.getSelectedItemPosition() > -1) {
                    AccessoryInfo accessoryInfo = accessories.get(mAccessorySpinner.getSelectedItemPosition());
                    resendAccessory(accessoryInfo);
                } else {
                    Logger.showResult(MainActivity.this, "Registered OwnedAccessories size: " + mAccessorySpinner.getAdapter().getCount());
                }
            }
        });
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(accessoryContextId)) {
                    openAndStartReadingAccessory(accessoryContextId);
                } else {
                    Logger.showResult(MainActivity.this, "Please resend accessory first");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void launchHomeScreen() {
        Result result = new Result();
        LauncherService.launch(MainActivity.this, LaunchAction.HOME, result);

        if (result.getCode() != Result.RESULT_OK) {
            Logger.showResult(MainActivity.this, "LauncherService.launch", result);
        }
    }

    private void launchApplication(String uuid) {
        Result result = new Result();
        LauncherService.launch(MainActivity.this, uuid, result);

        if (result.getCode() != Result.RESULT_OK) {
            Logger.showResult(MainActivity.this, "LauncherService.launch", result);
        }
    }

    private void getOwnedAccessory() {
        Result result = new Result();
        accessories = AccessoryService.getOwnedAccessories(getApplicationContext(), result);
        if (result.getCode() == Result.RESULT_OK) {
            ArrayList<String> accessoryList = new ArrayList<>();
            for (AccessoryInfo accessoryInfo : accessories) {
                accessoryList.add(Logger.build(accessoryInfo));
            }
            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, accessoryList);
            mAccessorySpinner.setAdapter(adapter);
            Logger.showResult(MainActivity.this, "Registered OwnedAccessories size: " + accessoryList.size());

        } else {
            Logger.showResult(MainActivity.this, "AccessoryService.getOwnedAccessories", result);
        }
    }

    private void resendAccessory(AccessoryInfo accessoryInfo) {
        Result result = new Result();
        AccessoryService.resendOwnedAccessoryContext(
                getApplicationContext(),
                accessoryInfo,
                result);
        if (result.getCode() != Result.RESULT_OK) {
            Logger.showResult(MainActivity.this, "AccessoryService.resendOwnedAccessoryContext", result);
        }
    }

    private void openAndStartReadingAccessory(String accessoryContextId) {
        Result result = new Result();
        AccessoryService.open(getApplicationContext(), accessoryContextId, result);
        if (result.getCode() == Result.RESULT_OK) {
            AccessoryService.startReading(getApplicationContext(), accessoryContextId, result);
            if (result.getCode() == Result.RESULT_OK) {
                Logger.showResult(MainActivity.this, "Ready");
            } else {
                Logger.showResult(MainActivity.this, "AccessoryService.startReading", result);
            }
        } else {
            Logger.showResult(MainActivity.this, "AccessoryService.open", result);
        }
    }

    public void handleComplete() {
        if (mGoHomeButton != null) {
            mGoHomeButton.setEnabled(true);
        }
        if (mLinkAppLaunchButton != null) {
            mLinkAppLaunchButton.setEnabled(true);
        }
    }

    public void handleDeviceInfo(String hostname) {
        mHostnameTextView.setText(hostname);
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

    private class SdkAccessoryReadyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("com.hp.jetadvantage.link.action.WORKPATH_READY")) {
                Log.i(TAG, "SDK initialized");
                Toast.makeText(context, "SDK initialized", Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals("com.hp.jetadvantage.link.action.ACCESSORY_READY")) {
                Log.i(TAG, "Accessory initialized");
                Toast.makeText(context, "Accessory initialized", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AccessoryObserver extends AccessoryService.AbstractAccessoryObserver {
        public AccessoryObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onContextChange(AccessoryInfo accessoryInfo, EventCode eventCode, String timeStamp, String accessoryContextId) {
            String log = new StringBuilder()
                    .append("accessoryContextId=").append(accessoryContextId).append("\n")
                    .append(", eventCode=").append(eventCode).append("\n")
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString();
            Logger.showResult(MainActivity.this, "AccessoryObserver onContextChange " + log);

            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                MainActivity.this.accessoryContextId = accessoryContextId;
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                MainActivity.this.accessoryContextId = null;
            }
        }

        @Override
        public void onReceive(AccessoryInfo accessoryInfo, ReportEventInfo reportEventInfo) {
            if (accessoryInfo != null &&
                    accessoryInfo.getDetails() != null &&
                    accessoryInfo.getDetails() instanceof HIDAccessoryInfo) {
                HIDAccessoryInfo hidAccessoryInfo = accessoryInfo.getDetails();
                HIDReportEventInfo hidReportEventInfo = reportEventInfo.getDetails();

                StringBuilder accessoryData = new StringBuilder(Logger.build(hidAccessoryInfo)).append("\n")
                        .append("data=");
                if (hidReportEventInfo != null) {
                    for (HIDReport hidReport : hidReportEventInfo.getReports()) {
                        accessoryData.append(Arrays.toString(hidReport.getData()));
                    }
                }
                mAccessoryDataTextView.setText(accessoryData.toString());
                Log.d(TAG, "AccessoryObserver onReceive(): " + accessoryData.toString());
            }
        }
    }

    private class AccessoryStartObserver extends AccessoryService.AbstractAccessoryStartObserver {

        public AccessoryStartObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReady(AccessoryInfo accessoryInfo, EventCode eventCode, String timeStamp, String accessoryContextId) {
            String log = new StringBuilder()
                    .append("accessoryContextId=").append(accessoryContextId).append("\n")
                    .append(", eventCode=").append(eventCode).append("\n")
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString();
            Log.i(TAG, "AccessoryObserver onReady(): " + log);
        }
    }
}
