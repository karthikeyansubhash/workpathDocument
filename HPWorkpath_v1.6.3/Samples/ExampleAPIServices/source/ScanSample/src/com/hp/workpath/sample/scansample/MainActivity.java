// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.JobService;
import com.hp.workpath.api.job.JobletAttributes;
import com.hp.workpath.api.job.ScanJobData;
import com.hp.workpath.api.scanner.FileOptionsAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributes;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.api.scanner.ScannerService;
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment;
import com.hp.workpath.sample.scansample.task.InitializationTask;
import com.hp.workpath.sample.scansample.task.LoadCapabilitiesTask;
import com.hp.workpath.sample.scansample.task.LoadDefaultsTask;
import com.hp.workpath.sample.scansample.task.ScanToDestinationTask;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * Main activity for Scan Sample.
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Scan";

    public static final String ACTION_SCAN_COMPLETED = "com.hp.workpath.sample.scansample.ACTION_SCAN_COMPLETED";

    private static final String STATE_JOB_ID = "jobId";
    private static final String STATE_RID = "rid";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private View mContainer;
    private Button mScanButton;
    private Button mCancelButton;
    private Button mLoadCapsButton;
    private Button mLoadDefaultsButton;
    private Button mGetJobInfoButton;
    private String mJobId = null;
    private String mRid = null;
    private JobObserver mJobObserver = null;

    /**
     * Fragment to display attributes configuration UI
     */
    private ScanConfigureFragment mFragment = null;
    private ScanAttributesCaps mCapabilities;
    private FileOptionsAttributesCaps mFileOptionsAttributesCaps;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SCREEN_4_3_INCH.equals((String) findViewById(R.id.container).getTag())) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            findViewById(R.id.fabMenu).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findViewById(R.id.buttonBarLayout).getVisibility() == View.VISIBLE) {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) findViewById(R.id.dataContainer).getLayoutParams();
                        params.setMargins(0, 0, 0, 0);
                        findViewById(R.id.dataContainer).setLayoutParams(params);
                        findViewById(R.id.buttonBarLayout).setVisibility(View.GONE);
                    } else {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) findViewById(R.id.dataContainer).getLayoutParams();
                        params.setMargins(0, 0, 0, 40);
                        findViewById(R.id.dataContainer).setLayoutParams(params);
                        findViewById(R.id.buttonBarLayout).setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        // find the text and button
        findViewElements();

        // add click listener to call the MFP
        addListener();

        mJobObserver = new JobObserver(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register JobObserver to receive job state callbacks
        mJobObserver.register(this);

        mContainer.setEnabled(false);
        mFragment = new ScanConfigureFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dataContainer, mFragment)
                .commit();

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        // Unregister JobObserver
        mJobObserver.unregister(this);

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
        } catch (Exception e) {
            handleException(e);
        }
        versionMenu.setTitle(version);
        return true;
    }

    private void findViewElements() {
        mContainer = findViewById(R.id.container);
        mScanButton = findViewById(R.id.scanButton);
        mCancelButton = findViewById(R.id.cancelButton);
        mLoadCapsButton = findViewById(R.id.loadCapsButton);
        mLoadDefaultsButton = findViewById(R.id.loadDefaultsButton);
        mGetJobInfoButton = findViewById(R.id.getJobInfoButton);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_JOB_ID, mJobId);
        outState.putString(STATE_RID, mRid);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mJobId = savedInstanceState.getString(STATE_JOB_ID);
        mRid = savedInstanceState.getString(STATE_RID);
    }

    /**
     * Launches Capabilities loading async task
     */
    private void loadCapabilities() {
        new LoadCapabilitiesTask(this, mFragment).taskExecute();
    }

    /**
     * Launches defaults loading async task
     */
    private void loadDefaults() {
        if (mCapabilities == null) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.capabilities_not_loaded));
            }
        } else {
            new LoadDefaultsTask(this, mFragment).taskExecute();
        }
    }

    /**
     * Executes request for capabilities from ScannerService.
     *
     * @param context {@link Context}
     * @return {@link ScanAttributesCaps}
     */
    public ScanAttributesCaps requestCaps(final Context context, Result result) {
        if (result == null) {
            result = new Result();
        }

        // cache capabilities for building ScanAttributes
        mCapabilities = ScannerService.getCapabilities(context, result);

        if (result.getCode() == Result.RESULT_OK && mCapabilities != null) {
            Log.i(TAG, "Caps=" + Logger.build(mCapabilities));
            Log.i(TAG, "DocumentFormatsByColorMode=" + Logger.build(mCapabilities.getDocumentFormatsByColorMode()));

            // get file options for defaults
            mFileOptionsAttributesCaps = requestFileOptionsCapabilities(
                    ScanAttributes.ColorMode.DEFAULT, ScanAttributes.DocumentFormat.DEFAULT);
        } else {
            Logger.showResult(MainActivity.this, "ScannerService.getCapabilities", result);
        }

        return mCapabilities;
    }

    public ScanAttributesCaps getCapabilities() {
        return mCapabilities;
    }

    public FileOptionsAttributesCaps requestFileOptionsCapabilities(ScanAttributes.ColorMode colorMode, ScanAttributes.DocumentFormat docFormat) {
        // cache file options capabilities for building FileOptionsAttributes later
        Result result = new Result();
        mFileOptionsAttributesCaps = ScannerService.getFileOptionsCapabilities(this, colorMode, docFormat, result);
        if (result.getCode() == Result.RESULT_OK) {
            Log.i(TAG, "FileOptionsAttributesCaps=" + Logger.build(mFileOptionsAttributesCaps, colorMode, docFormat));
            return mFileOptionsAttributesCaps;
        } else {
            Logger.showResult(MainActivity.this, "ScannerService.getFileOptionsCapabilities", result);
        }
        return null;
    }

    public FileOptionsAttributesCaps getFileOptionsAttributesCaps() {
        return mFileOptionsAttributesCaps;
    }

    /**
     * Sets listeners for all buttons
     */
    private void addListener() {
        mScanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scanToDestination();
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelJob();
            }
        });

        mLoadCapsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCapabilities();
            }
        });

        mLoadDefaultsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDefaults();
            }
        });

        mGetJobInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getJobInfo();
            }
        });
    }

    /**
     * Prepares {@link com.hp.workpath.api.scanner.ScanAttributes} and submits scan job.
     */
    private void scanToDestination() {
        mJobId = null;
        mRid = null;
        new ScanToDestinationTask(MainActivity.this).taskExecute();
    }

    private void cancelJob() {
        if (mJobId == null) {
            Logger.showResult(MainActivity.this, "There is no JobId");
        } else {
            Result result = JobService.cancelJob(MainActivity.this, mJobId);
            Logger.showResult(MainActivity.this, "Cancel: ", result);
        }
    }

    /**
     * Obtain current job info
     */
    private void getJobInfo() {
        final Result result = new Result();

        if (mJobId == null) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.no_job_info));
            }
        } else {
            JobInfo jobInfo = JobService.getJobInfo(getApplicationContext(), mJobId, result);
            if (result.getCode() != Result.RESULT_OK) {
                Logger.showResult(MainActivity.this, "JobService.getJobInfo", result);
            } else {
                Logger.showResult(MainActivity.this, "JobInfo=" + Logger.build(jobInfo));
            }
        }
    }

    public void handleComplete() {
        mContainer.setEnabled(true);
        mFragment.setSDKInitialized(true);
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

    public void setRid(String rid) {
        this.mRid = rid;
    }

    /**
     * Observer for submitted job
     */
    private class JobObserver extends JobService.AbstractJobletObserver {
        public JobObserver(final Handler handler) {
            super(handler);
        }

        @Override
        public void onProgress(final String rid, final JobInfo jobInfo) {
            Log.i(TAG, "onProgress: Received rid=" + rid);
            Log.i(TAG, "JobInfo=" + Logger.build(jobInfo));
            if (rid.equals(mRid)) {
                if (mJobId == null) {
                    if (jobInfo.getJobId() != null) {
                        mJobId = jobInfo.getJobId();

                        Log.i(TAG, "Received jobId=" + mJobId);
                        showSnackBar(getString(R.string.job_id, mJobId));

                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        final boolean monitorJob = prefs.getBoolean(ScanConfigureFragment.PREF_MONITOR_JOB, true);

                        if (monitorJob) {
                            prefs.edit().putString(ScanConfigureFragment.CURRENT_JOB_ID, mJobId).apply();

                            final boolean showProgress =
                                    prefs.getBoolean(ScanConfigureFragment.PREF_SHOW_JOB_PROGRESS, true);

                            // Monitor the job completion
                            final JobletAttributes taskAttributes =
                                    new JobletAttributes.Builder().setShowUi(showProgress).build();

                            final Intent intent = new Intent(getApplicationContext(), JobCompleteReceiver.class);
                            intent.setAction(ACTION_SCAN_COMPLETED);
                            intent.putExtra(JobCompleteReceiver.RID_EXTRA, rid);
                            intent.putExtra(JobCompleteReceiver.JOB_ID_EXTRA, mJobId);

                            final String jrid = JobService.monitorJobInForeground(MainActivity.this, mJobId,
                                    taskAttributes, intent);

                            Log.i(TAG, "MonitorJob request: " + jrid);
                        }
                    }
                }
            }
        }

        @Override
        public void onComplete(final String rid, final JobInfo jobInfo) {
            Log.i(TAG, "onComplete: Received rid=" + rid);
            Log.i(TAG, "JobInfo=" + Logger.build(jobInfo));
            if (jobInfo.getJobType() == JobInfo.JobType.SCAN) {
                ScanJobData scanJobData = jobInfo.getJobData();
                final List<String> images = scanJobData.getFileNames();


                if (images != null && images.size() > 0) {
                    try {
                        showSnackBar("Scan completed! Image path is " + UrlDecoder_replacer(Arrays.toString(images.toArray())));
                    } catch (Exception e) {
                        showSnackBar("Scan completed! Image path is " + Arrays.toString(images.toArray()));
                    }
                } else {
                    showSnackBar(getString(R.string.job_completed));
                }
            }
        }

        public String UrlDecoder_replacer(String data) {
            try {
                data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                data = data.replaceAll("\\+", "%2B");
                data = URLDecoder.decode(data, "utf-8");
            } catch (Exception e) {
                Logger.showResult(MainActivity.this,e.getMessage());
            }
            return data;
        }

        @Override
        public void onFail(final String rid, final Result result) {
            Log.i(TAG, "onFail: Received rid=" + rid + ", " + Logger.build(result));
            showSnackBar("onFail: Received rid=" + rid + ", " + getString(R.string.job_failed));
        }

        @Override
        public void onCancel(final String rid) {
            Log.i(TAG, "onCancel: Received rid=" + rid);
            showSnackBar(getString(R.string.job_cancelled));
        }
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
                mSnackBar.setAction(getString(R.string.ok), new OnClickListener() {
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
