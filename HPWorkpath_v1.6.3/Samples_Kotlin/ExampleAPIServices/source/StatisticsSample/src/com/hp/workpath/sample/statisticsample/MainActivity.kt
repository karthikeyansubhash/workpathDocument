// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.scanner.ScanAttributesCaps
import com.hp.workpath.sample.statisticsample.StatisticObserver.ObserverInterface
import com.hp.workpath.sample.statisticsample.databinding.ActivityCommonBinding
import com.hp.workpath.sample.statisticsample.fragment.StatisticListFragment
import com.hp.workpath.sample.statisticsample.fragment.TestJobFragment
import com.hp.workpath.sample.statisticsample.task.InitializationTask
import com.hp.workpath.sample.statisticsample.task.LoadCapabilitiesTask
import com.hp.workpath.sample.statisticsample.task.TotalCountTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mStatisticListFragment: StatisticListFragment
    private lateinit var mTestJobFragment: TestJobFragment

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar
    private lateinit var statisticObserver: StatisticObserver
    private lateinit var mBindingActivityMain: ActivityCommonBinding
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"

    private var mScanCaps: ScanAttributesCaps? = null
    private var mPrintCaps: PrintAttributesCaps? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        findViewElements()
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }
        mStatisticListFragment = StatisticListFragment()
        mTestJobFragment = TestJobFragment()
        replaceFragment(mStatisticListFragment)
        mBindingActivityMain.tabLayout.getTabAt(TAB_STATISTICS)?.select()

        statisticObserver = StatisticObserver(Handler(Looper.getMainLooper()), observerInterface)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }
        statisticObserver.register(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        statisticObserver.unregister(applicationContext)

        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
        if (this::mSnackBar.isInitialized) {
            mSnackBar.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        var version = ""
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            version = getString(
                R.string.version,
                pInfo.versionName,
                pInfo.longVersionCode.toInt(),
                sdkInfo.versionName,
                sdkInfo.versionCode
            )
        } catch (t: Throwable) {
            handleException(t)
        }
        versionMenu.title = version
        return true
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }

    private fun findViewElements() {
        mBindingActivityMain.refreshButton.setOnClickListener(this)
        mBindingActivityMain.tabLayout.addTab(
            mBindingActivityMain.tabLayout.newTab().setText(getString(R.string.statistics))
        )
        mBindingActivityMain.tabLayout.addTab(
            mBindingActivityMain.tabLayout.newTab().setText(getString(R.string.test_job))
        )
        mBindingActivityMain.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == TAB_STATISTICS) {
                    mBindingActivityMain.refreshButton.visibility = View.VISIBLE
                    mBindingActivityMain.totalTitleTextView.visibility = View.VISIBLE
                    replaceFragment(mStatisticListFragment)
                } else if (tab.position == TAB_TEST_JOB) {
                    mBindingActivityMain.refreshButton.visibility = View.GONE
                    mBindingActivityMain.totalTitleTextView.visibility = View.GONE
                    replaceFragment(mTestJobFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private var observerInterface = object : ObserverInterface {
        override fun onComplete(jobSequence: Int) {
            showSnackBar(getString(R.string.statistic_complete_observer, jobSequence))
        }
    }

    fun handleComplete() {
        getTotalCount()
        if (mScanCaps == null || mPrintCaps == null) {
            lifecycleScope.launch(Dispatchers.Default) {
                LoadCapabilitiesTask(this@MainActivity).execute()
            }
        }
    }

    fun loadCapabilities(scanCaps: ScanAttributesCaps?, printCaps: PrintAttributesCaps?) {
        if (applicationContext != null) {
            Logger.showResult(this@MainActivity, getString(R.string.loaded_caps))
        }
        mScanCaps = scanCaps
        mPrintCaps = printCaps
    }

    fun getScanCaps(): ScanAttributesCaps? {
        return mScanCaps
    }

    fun getPrintCaps(): PrintAttributesCaps? {
        return mPrintCaps
    }

    private fun getTotalCount() = runBlocking {
        try {
            val totalCount = lifecycleScope.async(Dispatchers.Default) {
                TotalCountTask(this@MainActivity).execute()
            }
            mBindingActivityMain.totalTitleTextView.text =
                getString(R.string.total, totalCount.await())
        } catch (t: Throwable) {
            Logger.showResult(this@MainActivity, "StatisticsService.getTotalCount ${t.message}")
        }
    }

    override fun onClick(v: View) {
        if (v === mBindingActivityMain.refreshButton) {
            if (mBindingActivityMain.tabLayout.selectedTabPosition == TAB_STATISTICS) {
                refreshJobInfoList()
            }
        }
    }

    fun refreshJobInfoList() {
        getTotalCount()
        mStatisticListFragment.getJobInfoList()
    }


    /**
     * Exception in could be because of following reasons
     *
     *  1. Library is not installed
     *  2. Library update is needed
     *  3. Version issue, unsupported
     *
     */
    fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(
                    R.string.sdk_support_missing
                )
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
        Log.e(TAG, errorMsg)
        mAlertDialog = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(errorMsg)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    fun showSnackBar(text: String) {
        Log.i(TAG, text)
        runOnUiThread {
            if (!::mSnackBar.isInitialized) {
                mSnackBar =
                    Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
                val snackBarView = mSnackBar.view
                val tv = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv.maxLines = 3
            }
            mSnackBar.run {
                setText(text)
                setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_button_color))
                setAction(getString(android.R.string.ok)) { mSnackBar.dismiss() }
                show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Statistic"
        const val TAB_STATISTICS = 0
        const val TAB_TEST_JOB = 1
    }
}