// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.job.JobInfo;
import com.hp.workpath.api.job.JobService;
import com.hp.workpath.api.job.JobletAttributes;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrinterService;
import com.hp.workpath.sample.printsample.fragments.CheckboxListDialogFragment;
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment;
import com.hp.workpath.sample.printsample.fragments.RadioListDialogFragment;
import com.hp.workpath.sample.printsample.task.InitializationTask;
import com.hp.workpath.sample.printsample.task.LoadCapabilitiesTask;
import com.hp.workpath.sample.printsample.task.LoadDefaultsTask;
import com.hp.workpath.sample.printsample.task.RequestPrintTask;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Main activity for Print Sample.
 */
public final class MainActivity extends AppCompatActivity implements CheckboxListDialogFragment.BatchJobListInterface {

    public static final String TAG = "[SAMPLE]" + "Print";
    public static final String REQUEST_KEY = "result-listener-request-key";


    public static final String ACTION_PRINT_COMPLETED = "com.hp.workpath.sample.printsample.ACTION_PRINT_COMPLETED";

    private static final String STATE_JOB_ID = "jobId";
    private static final String STATE_RID = "rid";

    public static final int PRINT_CLICKED = 1;
    public static final int BATCH_CLICKED = 2;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    /**
     * Fragment to display attributes configuration UI
     */
    private PrintConfigureFragment mFragment = null;
    private View mContainer;
    private SharedPreferences mPrefs = null;
    private JobObserver mJobObserver = null;

    private String mJobId = null;
    private String mRid = null;
    private PrintAttributesCaps mCapabilities;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;
    private LinearLayout mFooter;

    private boolean mResumedFromFileBrowser = false;
    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    public static ArrayList<PrintAttributes> batchJobs = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.layout).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }
        // find the text and button
        findViewElements();

        // add click listener
        addListener();

        mJobObserver = new JobObserver(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register JobObserver to receive job state callbacks
        mJobObserver.register(getApplicationContext());

        mContainer.setEnabled(false);
        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();

        if (!mResumedFromFileBrowser) {
            mFragment = new PrintConfigureFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dataContainer, mFragment)
                    .commit();
        }
        mResumedFromFileBrowser = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        // Unregister JobObserver
        mJobObserver.unregister(getApplicationContext());

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

    private void showListDialog(RadioListDialogFragment dialog) {
        if (dialog != null) {
            dialog.show(getSupportFragmentManager(), "dialog");
            getSupportFragmentManager().setFragmentResultListener(REQUEST_KEY, this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    boolean backgroundjob = result.getBoolean(RadioListDialogFragment.BACKGROUND_JOB, false);
                    Log.d(TAG, "showListDialog: " + backgroundjob);
                    executePrint(PRINT_CLICKED, backgroundjob);
                }
            });
        }
    }


    private void findViewElements() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mContainer = findViewById(R.id.container);
        mFooter = findViewById(R.id.footer);
    }

    private void addListener() {
        // Set listener for Print execution
        findViewById(R.id.printButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showListDialog(
                        RadioListDialogFragment.newInstance(batchJobs, false)
                );
            }
        });

        // Set listener for Add to Batch Job
        findViewById(R.id.addToBatchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                executePrint(BATCH_CLICKED, false);
            }
        });

        // Set listener for Load Capabilities
        findViewById(R.id.loadCapsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loadCapabilities();
            }
        });

        // Set listener for Load Defaults
        findViewById(R.id.loadDefaultsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loadDefaults();
            }
        });

        // Set listener for List Batch Jobs
        findViewById(R.id.listBatchJobButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                listBatchJobs();
            }
        });

        // Set listener for get Job Info
        findViewById(R.id.getJobInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getJobInfo();
            }
        });

        // Set listener for job cancel
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cancelJob();
            }
        });

        // Set listener for footer
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (mFooter.getVisibility() == View.VISIBLE) {
                        mFooter.setVisibility(View.GONE);
                    } else {
                        mFooter.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_JOB_ID, mJobId);
        outState.putString(STATE_RID, mRid);
    }

    public void disableEnableBatchButton(Boolean status) {
        findViewById(R.id.addToBatchButton).setEnabled(status);
        findViewById(R.id.listBatchJobButton).setEnabled(status);

        if (!batchJobs.isEmpty()) {
            if (!status) {
                mAlertDialog = new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Discard List Batch Jobs")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            batchJobs.clear();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            findViewById(R.id.addToBatchButton).setEnabled(true);
                            findViewById(R.id.listBatchJobButton).setEnabled(true);
                            loadDefaults();
                            dialog.dismiss();
                        })
                        .show();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mJobId = savedInstanceState.getString(STATE_JOB_ID);
        mRid = savedInstanceState.getString(STATE_RID);
    }

    public void setResumedFromFileBrowser(boolean value) {
        mResumedFromFileBrowser = value;
    }

    /**
     * Launches capabilities loading async task
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
     * Method to list batch jobs
     */
    private void listBatchJobs() {
        if (batchJobs.isEmpty()) {
            showSnackBar(getString(R.string.no_jobs_added));
            return;
        }
        CheckboxListDialogFragment
                .newInstance(batchJobs)
                .show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Launches Print job
     */
    private void executePrint(int buttonClicked, Boolean backgroundJob) {
        mJobId = null;
        mRid = null;
        new RequestPrintTask(MainActivity.this, buttonClicked, backgroundJob).taskExecute();
    }

    /**
     * Cancel Print job
     */
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

    /**
     * Requests printer Print capabilities
     *
     * @param context {@link Context} to obtain data
     * @return {@link com.hp.workpath.api.printer.PrintAttributesCaps}
     */
    public PrintAttributesCaps requestCaps(final Context context, Result result) {
        if (result == null) {
            result = new Result();
        }

        // cache capabilities for building PrintAttributes
        mCapabilities = PrinterService.getCapabilities(context, result);

        if (result.getCode() == Result.RESULT_OK && mCapabilities != null) {
            Log.i(TAG, "Caps=" + Logger.build(mCapabilities));
        } else {
            Logger.showResult(MainActivity.this, "PrinterService.getCapabilities", result);
        }

        return mCapabilities;
    }

    public PrintAttributesCaps getCapabilities() {
        return mCapabilities;
    }

    public void setRid(String rid) {
        this.mRid = rid;
    }

    /**
     * Called when list of jobs are selected to be removed from batch
     */
    @Override
    public void onRemovePrintAttributeFromList(Map<Integer, Boolean> mPrintObjectdMap) {
        ArrayList<Integer> indices = new ArrayList();

        for (Map.Entry<Integer, Boolean> entry : mPrintObjectdMap.entrySet()) {
            Boolean value = entry.getValue();
            if (value) indices.add(entry.getKey());
        }

        Collections.sort(indices, Collections.reverseOrder());
        indices.forEach(num -> {
            batchJobs.remove(num.intValue());
        });

        showSnackBar(getString(R.string.files_removed));
    }

    @Override
    public void showJobSelectionError() {
        Toast.makeText(MainActivity.this, (R.string.select_atleast_one_job), Toast.LENGTH_SHORT).show();
    }

    /**
     * Observer for submitted job
     */
    private class JobObserver extends JobService.AbstractJobletObserver {

        public JobObserver(final Handler handler) {
            super(handler);
        }

        public void onProgress(final String rid, final JobInfo jobInfo) {
            Log.i(TAG, "onProgress: Received rid=" + rid);
            Log.i(TAG, "JobInfo=" + Logger.build(jobInfo));
            if (rid.equals(mRid)) {
                if (mJobId == null) {
                    if (jobInfo.getJobId() != null) {
                        mJobId = jobInfo.getJobId();

                        Log.i(TAG, "Received jobId=" + mJobId);
                        showSnackBar(getString(R.string.job_id, mJobId));

                        if (mPrefs.getBoolean(PrintConfigureFragment.PREF_MONITORING_JOB, false)) {
                            // Store Job Id in order to verify it in the Broadcast Receiver
                            mPrefs.edit().putString(PrintConfigureFragment.CURRENT_JOB_ID, mJobId).apply();

                            final Intent intent = new Intent(getApplicationContext(), JobCompleteReceiver.class);
                            intent.setAction(ACTION_PRINT_COMPLETED);
                            intent.putExtra(JobCompleteReceiver.RID_EXTRA, rid);
                            intent.putExtra(JobCompleteReceiver.JOB_ID_EXTRA, mJobId);

                            final boolean showProgress =
                                    mPrefs.getBoolean(PrintConfigureFragment.PREF_SHOW_JOB_PROGRESS, true);

                            // Monitor the job completion
                            final JobletAttributes taskAttributes =
                                    new JobletAttributes.Builder().setShowUi(showProgress).build();

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
            if (jobInfo.getJobType() == JobInfo.JobType.PRINT) {
                showSnackBar(getString(R.string.job_completed, jobInfo.getJobName()));
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
