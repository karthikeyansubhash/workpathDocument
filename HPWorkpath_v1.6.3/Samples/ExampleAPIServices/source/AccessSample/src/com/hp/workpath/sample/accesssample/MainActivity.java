// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.sample.accesssample.model.UserDetails;
import com.hp.workpath.sample.accesssample.task.InitializationTask;
import com.hp.workpath.sample.accesssample.task.UserDetailsReaderTask;

import java.util.EnumMap;
import java.util.Map;

/**
 * Main activity for Access Sample.
 */
public final class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "[SAMPLE]" + "Access";

    /**
     * Map {@link UserDetails}
     * Store references to summary TextViews to provide information
     */
    private final EnumMap<UserDetails, TextView> mSummaries = new EnumMap<>(
            UserDetails.class);

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private EditText mKeyEditText;
    private TextView mValueTextView;
    private Button mGetValueButton;

    private ProgressBar mProgress;

    private Principal mCurrentPrincipal;
    private AlertDialog mAlertDialog;
    private String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.layout);
        if (SCREEN_4_3_INCH.equals(view.getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }

        // find the text and button
        findViewElements();
    }

    private void findViewElements() {
        // setting headers
        ((TextView) findViewById(R.id.headerVersion)).setText(R.string.header_version);
        ((TextView) findViewById(R.id.headerUser)).setText(R.string.header_user_details);
        ((TextView) findViewById(R.id.headerProperty)).setText(R.string.header_user_property);

        // store views summaries and ids
        for (UserDetails item : UserDetails.values()) {
            ViewGroup itemLayout = (ViewGroup) findViewById(item.getItemId());
            ((TextView) itemLayout.findViewById(R.id.titleTextView)).setText(item.getTitleId());
            mSummaries.put(item, (TextView) itemLayout.findViewById(R.id.summaryTextView));
        }

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mKeyEditText = findViewById(R.id.keyEditText);
        mValueTextView = findViewById(R.id.valueTextView);
        mGetValueButton = findViewById(R.id.getValueButton);
        mGetValueButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            version = getString(R.string.version_code, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            handleException(t);
        }
        versionMenu.setTitle(version);
        return true;
    }

    public void handleComplete() {
        showProgress(View.VISIBLE);
        new UserDetailsReaderTask(MainActivity.this).taskExecute();
    }

    public void showProgress(int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    public void handleUpdate(Map<UserDetails, String> result, Principal currentPrincipal) {
        // Fill device description with received info
        for (UserDetails item : UserDetails.values()) {
            if (result.containsKey(item)) {
                mSummaries.get(item).setText(result.get(item));
            }
        }
        mCurrentPrincipal = currentPrincipal;
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
        showProgress(View.GONE);

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
    public void onClick(View v) {
        if (v == mGetValueButton) {
            String key = mKeyEditText.getText().toString();
            if (mCurrentPrincipal != null && !TextUtils.isEmpty(key)) {
                String value = mCurrentPrincipal.getUserProperty(key);
                Logger.showResult(MainActivity.this, "UserProperty key: " + key + ", value: " + value);

                if (!TextUtils.isEmpty(value)) {
                    mValueTextView.setText(value);
                }
            }
        }
    }
}
