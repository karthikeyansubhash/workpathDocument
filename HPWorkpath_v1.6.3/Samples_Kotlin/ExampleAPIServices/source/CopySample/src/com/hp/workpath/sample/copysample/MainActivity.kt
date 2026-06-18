// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.copier.CopierService
import com.hp.workpath.api.copier.CopyAttributes
import com.hp.workpath.api.copier.CopyAttributesCaps
import com.hp.workpath.api.job.CopyJobData
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.job.JobService
import com.hp.workpath.api.job.JobService.AbstractJobletObserver
import com.hp.workpath.api.job.JobletAttributes
import com.hp.workpath.sample.copysample.Logger.build
import com.hp.workpath.sample.copysample.databinding.ActivityMainBinding
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment
import com.hp.workpath.sample.copysample.fragments.StoreJobFragment
import com.hp.workpath.sample.copysample.task.InitializationTask
import com.hp.workpath.sample.copysample.task.CopyTask
import com.hp.workpath.sample.copysample.task.DeleteStoredTask
import com.hp.workpath.sample.copysample.task.LoadCapabilitiesTask
import com.hp.workpath.sample.copysample.task.LoadDefaultsTask
import com.hp.workpath.sample.copysample.task.ReleaseStoredTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * CopySample Main activity
 * The activity shows the following interactions:<br></br>
 *
 *  1. How to initialize Workpath SDK
 *  2. How to get the copier service to get attribute capability details
 *  3. How to launch copy job on a printer
 *  4. How to cancel progressing job
 *  5. How to obtain existing job info
 *
 */
class MainActivity : AppCompatActivity() {
    /* Background task for Workpath SDK API initialization */
    private var mRid: String? = null
    private var mJobId: String? = null
    var storedJobId: String? = null
    private lateinit var mJobObserver: JobObserver

    private lateinit var mBindingActivityMain: ActivityMainBinding

    /**
     * Fragment to display attributes configuration UI
     */
    private lateinit var mCopyConfigureFragment: CopyConfigureFragment
    private lateinit var mStoreJobFragment: StoreJobFragment
    var capabilities: CopyAttributesCaps? = null
        private set
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar

    private val COPY_TAB = "Copy"
    private val STORED_JOB_TAB = "Stored Job"
    private var TAB_SELCETED = COPY_TAB
    private var SCREEN_4_3_INCH = "Screen_4.3_Inch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }

        // find the text and button
        findViewElements()

        // add click listener to call the MFP
        addListener()

        mJobObserver = JobObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()

        mCopyConfigureFragment = CopyConfigureFragment()
        mStoreJobFragment = StoreJobFragment()

        replaceFragment(mCopyConfigureFragment)
        mBindingActivityMain.tabLayout.getTabAt(TAB_COPY)?.select()
        // Register JobObserver to receive job state callbacks
        mJobObserver.register(this)

        mBindingActivityMain.container.isEnabled = false

        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister JobObserver
        mJobObserver.unregister(this)
        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
        if (this::mSnackBar.isInitialized) {
            mSnackBar.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(
                R.string.version,
                pInfo.versionName,
                pInfo.longVersionCode.toInt(),
                sdkInfo.versionName,
                sdkInfo.versionCode
            )
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    private fun findViewElements() {
        mBindingActivityMain.tabLayout.addTab(
            mBindingActivityMain.tabLayout.newTab().setText(getString(R.string.copy_button))
        )
        mBindingActivityMain.tabLayout.addTab(
            mBindingActivityMain.tabLayout.newTab().setText(getString(R.string.stored_job))
        )
    }

    private fun setVisibility() {
        if (TAB_SELCETED == COPY_TAB) {
            mBindingActivityMain.copyButton.visibility = View.VISIBLE
            mBindingActivityMain.loadCapsButton.visibility = View.VISIBLE
            mBindingActivityMain.loadDefaultsButton.visibility = View.VISIBLE
            mBindingActivityMain.getJobInfoButton.visibility = View.VISIBLE
            mBindingActivityMain.cancelButton.visibility = View.VISIBLE
            mBindingActivityMain.releaseButton.visibility = View.GONE
            mBindingActivityMain.deleteButton.visibility = View.GONE
        } else {
            mBindingActivityMain.loadCapsButton.visibility = View.VISIBLE
            mBindingActivityMain.getJobInfoButton.visibility = View.VISIBLE
            mBindingActivityMain.releaseButton.visibility = View.VISIBLE
            mBindingActivityMain.deleteButton.visibility = View.VISIBLE
            mBindingActivityMain.cancelButton.visibility = View.VISIBLE
            mBindingActivityMain.copyButton.visibility = View.GONE
            mBindingActivityMain.loadDefaultsButton.visibility = View.GONE
        }
    }

    /**
     * Sets listeners for all buttons
     */
    private fun addListener() {
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            mBindingActivityMain.floatSettings?.setOnClickListener {
                if (mBindingActivityMain.buttonBarLayout.visibility == View.VISIBLE) {
                    val param = mBindingActivityMain.fragmentContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 0
                    mBindingActivityMain.fragmentContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.GONE
                } else {
                    val param = mBindingActivityMain.fragmentContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = (40 * Resources.getSystem().getDisplayMetrics().density).toInt();
                    mBindingActivityMain.fragmentContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.VISIBLE
                    setVisibility()
                }
            }
        }
        mBindingActivityMain.copyButton.setOnClickListener { startCopy() }
        mBindingActivityMain.loadCapsButton.setOnClickListener { loadCapabilities() }
        mBindingActivityMain.loadDefaultsButton.setOnClickListener { loadDefaults() }
        mBindingActivityMain.getJobInfoButton.setOnClickListener { jobInfo }
        mBindingActivityMain.releaseButton.setOnClickListener { releaseStoredJob() }
        mBindingActivityMain.deleteButton.setOnClickListener { deleteStoredJob() }
        mBindingActivityMain.cancelButton.setOnClickListener {
            mJobId?.let {
                val result = JobService.cancelJob(this, it)
                Logger.showResult(this, "JobService.cancelJob", result)
            } ?: run {
                Logger.showResult(this, "There is no JobId")
            }
        }
        mBindingActivityMain.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (mBindingActivityMain.buttonBarLayout.visibility == View.VISIBLE && SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
                    val param = mBindingActivityMain.fragmentContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.bottomMargin = 0
                    mBindingActivityMain.fragmentContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.GONE
                }
                if (tab.position == TAB_COPY) {
                    replaceFragment(mCopyConfigureFragment)
                } else if (tab.position == TAB_STORE) {
                    replaceFragment(mStoreJobFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
            if (fragment === mCopyConfigureFragment) {
                if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
                    TAB_SELCETED = COPY_TAB
                } else {
                    mBindingActivityMain.copyButton.visibility = View.VISIBLE
                    mBindingActivityMain.loadDefaultsButton.visibility = View.VISIBLE
                    mBindingActivityMain.releaseButton.visibility = View.GONE
                    mBindingActivityMain.deleteButton.visibility = View.GONE
                }
            } else if (fragment === mStoreJobFragment) {
                if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
                    TAB_SELCETED = STORED_JOB_TAB
                } else {
                    mBindingActivityMain.copyButton.visibility = View.GONE
                    mBindingActivityMain.loadDefaultsButton.visibility = View.GONE
                    mBindingActivityMain.releaseButton.visibility = View.VISIBLE
                    mBindingActivityMain.deleteButton.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_JOB_ID, mJobId)
        outState.putString(STATE_RID, mRid)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mJobId = savedInstanceState.getString(STATE_JOB_ID)
        mRid = savedInstanceState.getString(STATE_RID)
    }

    /**
     * Launches Capabilities loading
     */
    private fun loadCapabilities() {
        lifecycleScope.launch(Dispatchers.Default) {
            LoadCapabilitiesTask(this@MainActivity, mCopyConfigureFragment).execute()
        }
    }

    private fun loadDefaults() {
        if (capabilities != null) {
            lifecycleScope.launch(Dispatchers.Default) {
                LoadDefaultsTask(this@MainActivity, mCopyConfigureFragment).execute()
            }
        } else {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.capabilities_not_loaded))
            }
        }
    }

    /**
     * Executes request for capabilities from CopierService.
     *
     * @param context [Context]
     * @return [com.hp.workpath.api.copier.CopyAttributesCaps]
     */
    fun requestCaps(context: Context, result: Result): CopyAttributesCaps? {
        // cache capabilities for building CopyAttributes
        capabilities = CopierService.getCapabilities(context, result)
        return if (result.code == Result.RESULT_OK) {
            Logger.showResult(this, "Caps=" + build(capabilities))
            capabilities
        } else {
            Logger.showResult(this, "CopierService.getCapabilities", result)
            null
        }
    }

    /**
     * Prepares [com.hp.workpath.api.copier.CopyAttributes] and submits Copy job.
     */
    private fun startCopy() {
        mJobId = null
        mRid = null
        lifecycleScope.launch(Dispatchers.Default) {
            CopyTask(this@MainActivity).execute()
        }
    }

    /**
     * Obtain current job info
     */
    private val jobInfo: Unit
        get() {
            val result = Result()
            mJobId?.let {
                val jobInfo = JobService.getJobInfo(applicationContext, it, result)
                if (result.code != Result.RESULT_OK) {
                    Logger.showResult(this, "JobService.getJobInfo", result)
                } else {
                    Logger.showResult(this, "JobInfo=" + build(jobInfo))
                }
            } ?: run {
                if (applicationContext != null) {
                    Logger.showResult(this, getString(R.string.no_job_info))
                }
            }
        }

    private fun releaseStoredJob() {
        storedJobId?.let {
            mJobId = null
            lifecycleScope.launch(Dispatchers.Default) {
                try {
                    ReleaseStoredTask(this@MainActivity, mStoreJobFragment.jobCredentials).execute(
                        it
                    )
                } catch (e: CapabilitiesExceededException) {
                    if (applicationContext != null) {
                        Logger.showResult(
                            this@MainActivity,
                            "CapabilitiesExceededException: $e.message"
                        )
                    }
                }
            }
        } ?: run {
            if (applicationContext != null) {
                Logger.showResult(this, resources.getString(R.string.no_job_info))
            }
        }
    }

    private fun deleteStoredJob() {
        storedJobId?.let {
            mJobId = null
            lifecycleScope.launch(Dispatchers.Default) {
                try {
                    DeleteStoredTask(
                        this@MainActivity,
                        mStoreJobFragment.jobCredentials
                    ).execute(it)
                } catch (e: CapabilitiesExceededException) {
                    Logger.showResult(
                        this@MainActivity,
                        "CapabilitiesExceededException: ${e.message}"
                    )
                }
            }
        } ?: run {
            if (applicationContext != null) {
                Logger.showResult(this, resources.getString(R.string.no_job_info))
            }
        }
    }

    fun handleComplete() {
        mBindingActivityMain.container.isEnabled = true
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

    fun setRid(rid: String?) {
        mRid = rid
        mCopyConfigureFragment.clearJobPassword()
    }

    /**
     * Observer for submitted job
     */
    private inner class JobObserver(handler: Handler?) : AbstractJobletObserver(handler) {
        override fun onProgress(rid: String, jobInfo: JobInfo) {
            Log.i(TAG, "onProgress: Received rid=$rid")
            Log.i(TAG, "JobInfo=" + build(jobInfo))
            if (rid == mRid) {
                if (mJobId == null) {
                    if (jobInfo.jobId != null) {
                        mJobId = jobInfo.jobId

                        Log.i(TAG, "Received jobId=$mJobId")
                        showSnackBar(getString(R.string.job_id, mJobId))

                        val prefs =
                            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val monitorJob =
                            prefs.getBoolean(CopyConfigureFragment.PREF_MONITOR_JOB, true)

                        if (monitorJob) {
                            prefs.edit().putString(CopyConfigureFragment.CURRENT_JOB_ID, mJobId)
                                .apply()

                            val showProgress =
                                prefs.getBoolean(CopyConfigureFragment.PREF_SHOW_JOB_PROGRESS, true)
                            // Monitor the job completion

                            val taskAttributes =
                                JobletAttributes.Builder().setShowUi(showProgress).build()

                            val intent = Intent(applicationContext, JobCompleteReceiver::class.java)
                            intent.action = ACTION_COPY_COMPLETED
                            intent.putExtra(JobCompleteReceiver.RID_EXTRA, rid)
                            intent.putExtra(JobCompleteReceiver.JOB_ID_EXTRA, mJobId)

                            val jrid = JobService.monitorJobInForeground(
                                this@MainActivity, jobInfo.jobId,
                                taskAttributes, intent
                            )
                            Log.i(TAG, "MonitorJob request: $jrid")
                        }
                    }
                }
            }
        }

        override fun onComplete(rid: String, jobInfo: JobInfo) {
            Log.i(TAG, "onComplete: Received rid=$rid")
            Log.i(TAG, "JobInfo=" + build(jobInfo))
            if (jobInfo.jobType == JobInfo.JobType.COPY) {
                val copyJobData = jobInfo.getJobData<CopyJobData>()
                if (copyJobData.jobExecutionMode == CopyAttributes.JobExecutionMode.STORE
                    && mBindingActivityMain.tabLayout.selectedTabPosition == TAB_COPY
                ) {
                    storedJobId = mJobId
                    showSnackBar(getString(R.string.job_stored))
                } else {
                    showSnackBar(getString(R.string.job_completed))
                }
            }
        }

        override fun onFail(rid: String?, result: Result) {
            Log.e(TAG, "onFail: Received rid=" + rid + ", " + build(result))
            showSnackBar("onFail: Received rid=" + rid + ", " + getString(R.string.job_failed))
        }

        override fun onCancel(rid: String?) {
            Log.i(TAG, "onCancel: Received rid=$rid")
            showSnackBar(getString(R.string.job_cancelled))
        }
    }

    fun showSnackBar(text: String) {
        runOnUiThread {
            if (!::mSnackBar.isInitialized) {
                mSnackBar =
                    Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
                val snackBarView = mSnackBar.view
                val tv = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv?.maxLines = 3
            }
            mSnackBar.run {
                setText(text)
                setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_button_color))
                setAction(getString(R.string.ok)) { mSnackBar.dismiss() }
                show()
            }
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Copy"
        const val ACTION_COPY_COMPLETED = "com.hp.workpath.sample.copysample.ACTION_COPY_COMPLETED"

        private const val STATE_JOB_ID = "jobId"
        private const val STATE_RID = "rid"
        private const val TAB_COPY = 0
        private const val TAB_STORE = 1
    }
}