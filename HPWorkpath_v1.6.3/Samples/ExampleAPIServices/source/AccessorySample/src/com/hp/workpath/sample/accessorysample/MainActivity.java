// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.ReportEventInfo;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.api.accessory.hid.EventCode;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo;
import com.hp.workpath.sample.accessorysample.fragment.AccessoryFragment;
import com.hp.workpath.sample.accessorysample.fragment.AccessoryListFragment;
import com.hp.workpath.sample.accessorysample.fragment.AccessoryReportsFragment;
import com.hp.workpath.sample.accessorysample.task.ActionTask;
import com.hp.workpath.sample.accessorysample.task.InitializationTask;

import java.util.Arrays;
import java.util.List;

/**
 * Main activity for Accessory Sample.
 */
public final class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG = "[SAMPLE]" + "Accessory";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private Button mOpenBtn;
    private Button mCloseBtn;
    private Button mStartReadingBtn;
    private Button mStopReadingBtn;
    private Button mReadReportBtn;
    private Button mWriteReportBtn;
    private TextView mAccessoryDataTextView;
    private FloatingActionButton mFloatSettings;

    private View mContainer;

    private AccessoryObserver mAccessoryObserver;
    private AccessoryStartObserver mAccessoryStartObserver;

    private AccessoryFragment mAccessoryFragment = null;
    private AccessoryListFragment mAccessoryListFragment = null;
    private AccessoryReportsFragment mAccessoryReportFragment = null;

    private String mAccessoryContextId;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;
    private final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.layout).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }
        mContainer = findViewById(R.id.container);

        // find the text and button
        findViewElements();

        Handler handler = new Handler();
        mAccessoryObserver = new AccessoryObserver(handler);
        mAccessoryStartObserver = new AccessoryStartObserver(handler);
    }

    private void findViewElements() {
        mOpenBtn = findViewById(R.id.openButton);
        mCloseBtn = findViewById(R.id.closeButton);
        mStartReadingBtn = findViewById(R.id.startReadingButton);
        mStopReadingBtn = findViewById(R.id.stopReadingButton);
        mReadReportBtn = findViewById(R.id.readReportButton);
        mWriteReportBtn = findViewById(R.id.writeReportButton);
        mAccessoryDataTextView = findViewById(R.id.accessoryDataTextview);
        mFloatSettings = findViewById(R.id.floatSettings);

        mOpenBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        mStartReadingBtn.setOnClickListener(this);
        mStopReadingBtn.setOnClickListener(this);
        mReadReportBtn.setOnClickListener(this);
        mWriteReportBtn.setOnClickListener(this);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.layout).getTag())) {
            mFloatSettings.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mOpenBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.OPEN, mAccessoryContextId);
        } else if (v == mCloseBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.CLOSE, mAccessoryContextId);
        } else if (v == mStartReadingBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.START_READ, mAccessoryContextId);
        } else if (v == mStopReadingBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.STOP_READ, mAccessoryContextId);
        } else if (v == mReadReportBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.READ_REPORT, mAccessoryContextId);
        } else if (v == mWriteReportBtn) {
            new ActionTask(MainActivity.this).taskExecute(Action.WRITE_REPORT, mAccessoryContextId);
        } else if (v == mFloatSettings) {
            if (findViewById(R.id.buttonBarRow).getVisibility() == View.VISIBLE) {
                findViewById(R.id.buttonBarRow).setVisibility(View.GONE);
            } else {
                findViewById(R.id.buttonBarRow).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAccessoryObserver.register(getApplicationContext());
        mAccessoryStartObserver.register(getApplicationContext());

        mContainer.setEnabled(false);
        // call init task
        mInitializationTask = new InitializationTask(this, initializeInterface);
        mInitializationTask.taskExecute();

        mAccessoryFragment = new AccessoryFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.accessoryContainer, mAccessoryFragment)
                .commit();

        mAccessoryListFragment = new AccessoryListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.accessoryListContainer, mAccessoryListFragment)
                .commit();

        mAccessoryReportFragment = new AccessoryReportsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.accessoryReportContainer, mAccessoryReportFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        mAccessoryObserver.unregister(getApplicationContext());
        mAccessoryStartObserver.unregister(getApplicationContext());

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (mSnackBar != null) {
            mSnackBar.dismiss();
            mSnackBar = null;
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

    public void setAccessoryContextId(AccessoryInfo accessoryInfo, String accessoryContextId) {
        mAccessoryContextId = accessoryContextId;
        mAccessoryFragment.updateAccessoryContextId(accessoryInfo, mAccessoryContextId);
        mAccessoryListFragment.updateReservedAccessory(accessoryInfo, mAccessoryContextId);
    }

    public void loadAccessories(Action action, List<AccessoryInfo> enumeratedAccessories) {
        for (AccessoryInfo accessoryInfo : enumeratedAccessories) {
            Log.i(TAG, "AccessoryInfo=" + Logger.build(accessoryInfo));
        }
        mAccessoryListFragment.loadAccessories(action, enumeratedAccessories);
    }

    public void setInfo(HIDInfo hidInfo) {
        mAccessoryReportFragment.setInfo(hidInfo);
    }

    /**
     * Observer for accessory event.
     */
    private class AccessoryObserver extends AccessoryService.AbstractAccessoryObserver {
        public AccessoryObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onContextChange(AccessoryInfo accessoryInfo, EventCode eventCode, String timeStamp, String accessoryContextId) {
            Log.i(TAG, "AccessoryObserver onContextChange() accessoryContextId: " + accessoryContextId);
            String log = new StringBuilder().append("eventCode=").append(eventCode)
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString();
            Log.i(TAG, log);
            showSnackBar(log);
            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                setAccessoryContextId(accessoryInfo, accessoryContextId);
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                setAccessoryContextId(accessoryInfo, null);
            }
        }

        @Override
        public void onReceive(AccessoryInfo accessoryInfo, ReportEventInfo reportEventInfo) {
            if (accessoryInfo != null &&
                    accessoryInfo.getDetails() != null &&
                    accessoryInfo.getDetails() instanceof HIDAccessoryInfo) {
                HIDAccessoryInfo hidAccessoryInfo = accessoryInfo.getDetails();
                HIDReportEventInfo hidReportEventInfo = reportEventInfo.getDetails();

                Log.i(TAG, "AccessoryObserver onReceive()");
                Log.i(TAG, "AccessoryInfo=" + Logger.build(hidAccessoryInfo));
                StringBuilder accessoryData = new StringBuilder(Logger.build(hidAccessoryInfo));
                accessoryData.append("\ndata=");
                String comma = "";
                if (hidReportEventInfo != null) {
                    for (HIDReport hidReport : hidReportEventInfo.getReports()) {
                        accessoryData.append(comma).append(Arrays.toString(hidReport.getData()));
                        comma = ", ";
                    }
                }
                setAccessoryData(accessoryData.toString());
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
        mAlertDialog = new AlertDialog.Builder(MainActivity.this)
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

    InitializationTask.InitializeInterface initializeInterface = new InitializationTask.InitializeInterface() {
        @Override
        public void handleComplete() {
            mContainer.setEnabled(true);
        }

        @Override
        public void handleException(Throwable t) {
            MainActivity.this.handleException(t);
        }
    };

    public void setAccessoryData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAccessoryDataTextView != null) {
                    mAccessoryDataTextView.setText(data);
                }
            }
        });
    }

    public void showSnackBar(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSnackBar == null) {
                    mSnackBar = Snackbar.make(mContainer, "", Snackbar.LENGTH_INDEFINITE);
                    View snackBarView = mSnackBar.getView();
                    TextView tv = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setMaxLines(3);
                }
                mSnackBar.setText(text);
                mSnackBar.setActionTextColor(getResources().getColor(R.color.snackbar_button_color));
                mSnackBar.setAction(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSnackBar != null) {
                            mSnackBar.dismiss();
                            mSnackBar = null;
                        }
                    }
                }).show();
            }
        });
    }
}