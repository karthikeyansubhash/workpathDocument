// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.scanner.ScanAttributesCaps;
import com.hp.workpath.sample.statisticsample.fragment.StatisticListFragment;
import com.hp.workpath.sample.statisticsample.fragment.TestJobFragment;
import com.hp.workpath.sample.statisticsample.task.InitializationTask;
import com.hp.workpath.sample.statisticsample.task.LoadCapabilitiesTask;
import com.hp.workpath.sample.statisticsample.task.TotalCountTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TotalCountTask.TotalCountTaskCompletionListener {

    public static final String TAG = "[SAMPLE]" + "Statistic";
    private static final int TAB_STATISTICS = 0;
    private static final int TAB_TEST_JOB = 1;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    private StatisticListFragment mStatisticListFragment;
    private TestJobFragment mTestJobFragment;

    private TabLayout mTabLayout;
    private TextView mTotalTitleTextView;
    private ImageButton mRefreshButton;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;
    private StatisticObserver statisticObserver;

    private View mContainer;
    private ScanAttributesCaps mScanCaps;
    private PrintAttributesCaps mPrintCaps;
    private final String SCREEN_4_3_INCH = "Screen_4.3_Inch";
    private Toolbar mToolbar;
    private int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        findViewElements();
        if (SCREEN_4_3_INCH.equals(mContainer.getTag())) {
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
        mStatisticListFragment = new StatisticListFragment();
        mTestJobFragment = new TestJobFragment();
        replaceFragment(mStatisticListFragment);
        mTabLayout.getTabAt(TAB_STATISTICS).select();

        statisticObserver = new StatisticObserver(new Handler(), observerInterface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();

        statisticObserver.register(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        statisticObserver.unregister(getApplicationContext());

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

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        }
    }

    private void findViewElements() {
        mTabLayout = findViewById(R.id.tabLayout);
        mContainer = findViewById(R.id.container);
        mTotalTitleTextView = findViewById(R.id.totalTitleTextView);
        mRefreshButton = findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(this);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.statistics)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.test_job)));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == TAB_STATISTICS) {
                    mRefreshButton.setVisibility(View.VISIBLE);
                    mTotalTitleTextView.setVisibility(View.VISIBLE);
                    replaceFragment(mStatisticListFragment);
                } else if (tab.getPosition() == TAB_TEST_JOB) {
                    mRefreshButton.setVisibility(View.GONE);
                    mTotalTitleTextView.setVisibility(View.GONE);
                    replaceFragment(mTestJobFragment);
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

    StatisticObserver.ObserverInterface observerInterface = new StatisticObserver.ObserverInterface() {
        @Override
        public void onComplete(int jobSequence) {
            showSnackBar(getString(R.string.statistic_complete_observer, jobSequence));
        }
    };

    public void handleComplete() {
        new TotalCountTask(this, this).taskExecute();
        if (mScanCaps == null || mPrintCaps == null) {
            new LoadCapabilitiesTask(this).taskExecute();
        }
    }

    public void loadCapabilities(ScanAttributesCaps scanCaps, PrintAttributesCaps printCaps) {
        if (getApplicationContext() != null) {
            Logger.showResult(MainActivity.this, getString(R.string.loaded_caps));
        }
        mScanCaps = scanCaps;
        mPrintCaps = printCaps;
    }

    public ScanAttributesCaps getScanCaps() {
        return mScanCaps;
    }

    public PrintAttributesCaps getPrintCaps() {
        return mPrintCaps;
    }

    public int getTotalTaskCount() {
        return totalCount;
    }

    @Override
    public void onTotalCountTaskCompleted(int count) {
        totalCount = count;
        mTotalTitleTextView.setText(getString(R.string.total, totalCount));
    }

    @Override
    public void onClick(View v) {
        if (v == mRefreshButton) {
            if (mTabLayout.getSelectedTabPosition() == TAB_STATISTICS) {
                refreshJobInfoList();
            }
        }
    }

    public void refreshJobInfoList() {
        new TotalCountTask(this, this).taskExecute();
        mStatisticListFragment.getJobInfoList();
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
        finish();
        super.onBackPressed();
    }

    public void showSnackBar(String text) {
        Log.i(TAG, text);
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
                mSnackBar.setAction(getString(android.R.string.ok), new View.OnClickListener() {
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
