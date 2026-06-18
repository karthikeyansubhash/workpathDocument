// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.CopyAttributesCaps;
import com.hp.workpath.api.job.CopyJobData;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.JobService;
import com.hp.workpath.api.job.JobletAttributes;
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment;
import com.hp.workpath.sample.copysample.fragments.StoreJobFragment;
import com.hp.workpath.sample.copysample.task.CopyTask;
import com.hp.workpath.sample.copysample.task.DeleteStoredTask;
import com.hp.workpath.sample.copysample.task.InitializationTask;
import com.hp.workpath.sample.copysample.task.LoadCapabilitiesTask;
import com.hp.workpath.sample.copysample.task.LoadDefaultsTask;
import com.hp.workpath.sample.copysample.task.ReleaseStoredTask;

/**
 * CopySample Main activity
 * The activity shows the following interactions:<br>
 * <ol>
 * <li>How to initialize Workpath SDK</li>
 * <li>How to get the copier service to get attribute capability details</li>
 * <li>How to launch copy job on a printer</li>
 * <li>How to cancel progressing job</li>
 * <li>How to obtain existing job info</li>
 * </ol>
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Copy";

    private View mContainer;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    public static final String ACTION_COPY_COMPLETED = "com.hp.workpath.sample.copysample.ACTION_COPY_COMPLETED";

    private static final String STATE_JOB_ID = "jobId";
    private static final String STATE_RID = "rid";
    private static final String COPY_TAB = "Copy";
    private static final String STORED_JOB_TAB = "Stored Job";
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    private static final int TAB_COPY = 0;
    private static final int TAB_STORE = 1;

    private TabLayout mTabLayout;
    private LinearLayout mButtonBar;
    private FrameLayout mFragmentContainer;
    private Button mCopyButton;
    private Button mLoadCapsButton;
    private Button mLoadDefaultsButton;
    private Button mGetJobInfoButton;
    private Button mReleaseJobButton;
    private Button mDeleteJobButton;
    private Button mCancelJobButton;
    private FloatingActionButton mFloatSettings;
    private String mRid = null;
    private String mJobId = null;
    private String mStoredJobId = null;
    private JobObserver mJobObserver = null;
    private String TAB_SELECTED = COPY_TAB;

    /**
     * Fragment to display attributes configuration UI
     */
    private CopyConfigureFragment mCopyConfigureFragment = null;
    private StoreJobFragment mStoreJobFragment = null;
    private CopyAttributesCaps mCapabilities;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;

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

        mJobObserver = new JobObserver(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCopyConfigureFragment = new CopyConfigureFragment();
        mStoreJobFragment = new StoreJobFragment();

        replaceFragment(mCopyConfigureFragment);
        mTabLayout.getTabAt(TAB_COPY).select();
        // Register JobObserver to receive job state callbacks
        mJobObserver.register(this);

        mContainer.setEnabled(false);

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
        } catch (Throwable t) {
            handleException(t);
        }
        versionMenu.setTitle(version);
        return true;
    }

    private void findViewElements() {
        mTabLayout = findViewById(R.id.tabLayout);
        mButtonBar = findViewById(R.id.buttonBarLayout);
        mFragmentContainer = findViewById(R.id.fragmentContainer);
        mContainer = findViewById(R.id.container);
        mCopyButton = findViewById(R.id.copyButton);
        mLoadCapsButton = findViewById(R.id.loadCapsButton);
        mLoadDefaultsButton = findViewById(R.id.loadDefaultsButton);
        mGetJobInfoButton = findViewById(R.id.getJobInfoButton);
        mReleaseJobButton = findViewById(R.id.releaseButton);
        mDeleteJobButton = findViewById(R.id.deleteButton);
        mCancelJobButton = findViewById(R.id.cancelButton);
        mFloatSettings = findViewById(R.id.floatSettings);

        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.copy_button)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.stored_job)));
    }

    private void setVisibility() {
        if (TAB_SELECTED.equals(COPY_TAB)) {
            mCopyButton.setVisibility(View.VISIBLE);
            mLoadCapsButton.setVisibility(View.VISIBLE);
            mLoadDefaultsButton.setVisibility(View.VISIBLE);
            mGetJobInfoButton.setVisibility(View.VISIBLE);
            mCancelJobButton.setVisibility(View.VISIBLE);
            mReleaseJobButton.setVisibility(View.GONE);
            mDeleteJobButton.setVisibility(View.GONE);
        } else {
            mLoadCapsButton.setVisibility(View.VISIBLE);
            mGetJobInfoButton.setVisibility(View.VISIBLE);
            mReleaseJobButton.setVisibility(View.VISIBLE);
            mDeleteJobButton.setVisibility(View.VISIBLE);
            mCancelJobButton.setVisibility(View.VISIBLE);
            mCopyButton.setVisibility(View.GONE);
            mLoadDefaultsButton.setVisibility(View.GONE);
        }
    }

    /**
     * Sets listeners for all buttons
     */
    private void addListener() {
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            mFloatSettings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mButtonBar.getVisibility() == View.VISIBLE) {
                        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mFragmentContainer.getLayoutParams();
                        param.bottomMargin = 0;
                        mFragmentContainer.setLayoutParams(param);
                        mButtonBar.setVisibility(View.GONE);
                    } else {
                        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mFragmentContainer.getLayoutParams();
                        param.bottomMargin = (int) (40 * Resources.getSystem().getDisplayMetrics().density);
                        mFragmentContainer.setLayoutParams(param);
                        mButtonBar.setVisibility(View.VISIBLE);
                        setVisibility();
                    }
                }
            });
        }

        mCopyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startCopy();
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

        mReleaseJobButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseStoredJob();
            }
        });

        mDeleteJobButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStoredJob();
            }
        });

        mCancelJobButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mJobId == null) {
                    Logger.showResult(MainActivity.this, "There is no JobId");
                } else {
                    Result result = JobService.cancelJob(MainActivity.this, mJobId);
                    Logger.showResult(MainActivity.this, "JobService.cancelJob", result);
                }
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mButtonBar.getVisibility() == View.VISIBLE && SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
                    ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mFragmentContainer.getLayoutParams();
                    param.bottomMargin = 0;
                    mFragmentContainer.setLayoutParams(param);
                    mButtonBar.setVisibility(View.GONE);
                }
                if (tab.getPosition() == TAB_COPY) {
                    replaceFragment(mCopyConfigureFragment);
                } else if (tab.getPosition() == TAB_STORE) {
                    replaceFragment(mStoreJobFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();

            if (fragment == mCopyConfigureFragment) {
                if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
                    TAB_SELECTED = COPY_TAB;
                } else {
                    mCopyButton.setVisibility(View.VISIBLE);
                    mLoadDefaultsButton.setVisibility(View.VISIBLE);
                    mReleaseJobButton.setVisibility(View.GONE);
                    mDeleteJobButton.setVisibility(View.GONE);
                }
            } else if (fragment == mStoreJobFragment) {
                if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
                    TAB_SELECTED = STORED_JOB_TAB;
                } else {
                    mCopyButton.setVisibility(View.GONE);
                    mLoadDefaultsButton.setVisibility(View.GONE);
                    mReleaseJobButton.setVisibility(View.VISIBLE);
                    mDeleteJobButton.setVisibility(View.VISIBLE);
                }
            }
        }
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
     * Launches Capabilities loading
     */
    private void loadCapabilities() {
        new LoadCapabilitiesTask(this, mCopyConfigureFragment).taskExecute();
    }

    private void loadDefaults() {
        if (mCapabilities == null) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.capabilities_not_loaded));
            }
        } else {
            new LoadDefaultsTask(this, mCopyConfigureFragment).taskExecute();
        }
    }

    /**
     * Executes request for capabilities from CopierService.
     *
     * @param context {@link Context}
     * @return {@link com.hp.workpath.api.copier.CopyAttributesCaps}
     */
    public CopyAttributesCaps requestCaps(final Context context, Result result) {
        if (result == null) {
            result = new Result();
        }

        // cache capabilities for building CopyAttributes
        mCapabilities = CopierService.getCapabilities(context, result);

        if (result.getCode() == Result.RESULT_OK && mCapabilities != null) {
            Logger.showResult(MainActivity.this, "Caps=" + Logger.build(mCapabilities));
        } else {
            Logger.showResult(MainActivity.this, "CopierService.getCapabilities", result);
        }

        return mCapabilities;
    }

    public CopyAttributesCaps getCapabilities() {
        return mCapabilities;
    }

    /**
     * Prepares {@link com.hp.workpath.api.copier.CopyAttributes} and submits Copy job.
     */
    private void startCopy() {
        mJobId = null;
        mRid = null;
        new CopyTask(MainActivity.this).taskExecute();
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

    private void releaseStoredJob() {
        if (mStoredJobId != null) {
            try {
                mJobId = null;
                new ReleaseStoredTask(this, mStoreJobFragment.getJobCredentials()).taskExecute(mStoredJobId);
            } catch (CapabilitiesExceededException e) {
                Logger.showResult(MainActivity.this, "CapabilitiesExceededException " + e.getMessage());
            }
        } else {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.no_job_info));
            }
        }
    }

    private void deleteStoredJob() {
        if (mStoredJobId != null) {
            try {
                mJobId = null;
                new DeleteStoredTask(this, mStoreJobFragment.getJobCredentials()).taskExecute(mStoredJobId);
            } catch (CapabilitiesExceededException e) {
                Logger.showResult(MainActivity.this, "CapabilitiesExceededException " + e.getMessage());
            }
        } else {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.no_job_info));
            }
        }
    }

    public String getStoredJobId() {
        return mStoredJobId;
    }

    public void setStoredJobId(String storedJobId) {
        mStoredJobId = storedJobId;
    }

    public void handleComplete() {
        mContainer.setEnabled(true);
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
        this.mCopyConfigureFragment.clearJobPassword();
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
                        final boolean monitorJob = prefs.getBoolean(CopyConfigureFragment.PREF_MONITOR_JOB, true);

                        if (monitorJob) {
                            prefs.edit().putString(CopyConfigureFragment.CURRENT_JOB_ID, mJobId).apply();

                            final boolean showProgress =
                                    prefs.getBoolean(CopyConfigureFragment.PREF_SHOW_JOB_PROGRESS, true);

                            // Monitor the job completion
                            final JobletAttributes taskAttributes =
                                    new JobletAttributes.Builder().setShowUi(showProgress).build();

                            final Intent intent = new Intent(getApplicationContext(), JobCompleteReceiver.class);
                            intent.setAction(ACTION_COPY_COMPLETED);
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
            if (jobInfo.getJobType() == JobInfo.JobType.COPY) {
                CopyJobData copyJobData = jobInfo.getJobData();
                if (copyJobData.getJobExecutionMode() == CopyAttributes.JobExecutionMode.STORE
                        && mTabLayout.getSelectedTabPosition() == TAB_COPY) {
                    mStoredJobId = mJobId;
                    showSnackBar(getString(R.string.job_stored));
                } else {
                    showSnackBar(getString(R.string.job_completed));
                }
            }
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