// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.SignInAction;
import com.hp.workpath.sample.accessoryagentsample.model.UserDetails;
import com.hp.workpath.sample.accessoryagentsample.task.InitializationTask;
import com.hp.workpath.sample.accessoryagentsample.task.UserDetailsReaderTask;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity for AccessoryAgent Sample.
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "AccessoryAgent";

    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    boolean isInitializedSDK = false;

    private AlertDialog mAlertDialog;
    private ProgressBar mProgress;
    private Spinner mSignInActionSpinner;
    private Button mSignInActionButton;


    /**
     * Map {@link UserDetails}
     * Store references to summary TextViews to provide information
     */
    private final EnumMap<UserDetails, TextView> mSummaries = new EnumMap<>(
            UserDetails.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call init task
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

    private void initView() {
        ((TextView) findViewById(R.id.headerUserInfo)).setText(R.string.header_user_info);
        ((TextView) findViewById(R.id.headerSignInAction)).setText(R.string.header_sign_in_action);

        mSignInActionSpinner = findViewById(R.id.signInActionSpinner);
        mSignInActionButton = findViewById(R.id.signInActionButton);
        mSignInActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActionUtil.setAction(MainActivity.this, (String) mSignInActionSpinner.getSelectedItem());
                    Logger.showResult(MainActivity.this, "Sign-in action is saved: " + mSignInActionSpinner.getSelectedItem());
                } catch (Throwable t) {
                    MainActivity.this.handleException(t);
                }
            }
        });

        List<String> signInActions = Arrays.asList(getResources().getStringArray(R.array.sign_in_action_arrays));
        SignInAction.Action action = ActionUtil.getAction(MainActivity.this);
        mSignInActionSpinner.setSelection(signInActions.indexOf(action.name()));

        for (UserDetails item : UserDetails.values()) {
            ViewGroup itemLayout = findViewById(item.getItemId());
            ((TextView) itemLayout.findViewById(R.id.titleTextView)).setText(item.getTitleId());
            mSummaries.put(item, (TextView) itemLayout.findViewById(R.id.summaryTextView));
        }
        mProgress = findViewById(R.id.progressBar);
    }

    public void showProgress(int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    public void handleUpdate(Map<UserDetails, String> result) {
        // Fill device description with received info
        for (UserDetails item : UserDetails.values()) {
            if (result.containsKey(item)) {
                mSummaries.get(item).setText(result.get(item));
            }
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
        public void handleComplete(InitializationTask.InitStatus initStatus) {
            isInitializedSDK = true;
            showProgress(View.VISIBLE);
            new UserDetailsReaderTask(MainActivity.this).taskExecute();
        }

        @Override
        public void handleException(Throwable t) {
            MainActivity.this.handleException(t);
        }
    };
}
