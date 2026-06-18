// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.job.JobInfo
import com.hp.workpath.api.job.JobService
import com.hp.workpath.api.job.JobletAttributes
import com.hp.workpath.api.job.ScanJobData
import com.hp.workpath.api.scanner.FileOptionsAttributesCaps
import com.hp.workpath.api.scanner.ScanAttributes
import com.hp.workpath.api.scanner.ScanAttributesCaps
import com.hp.workpath.api.scanner.ScannerService
import com.hp.workpath.sample.scansample.databinding.ActivityMainBinding
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment
import com.hp.workpath.sample.scansample.task.InitializationTask
import com.hp.workpath.sample.scansample.task.LoadCapabilitiesTask
import com.hp.workpath.sample.scansample.task.LoadDefaultsTask
import com.hp.workpath.sample.scansample.task.ScanToDestinationTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.util.*

/**
 * Main activity for Scan Sample.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var mFragment: ScanConfigureFragment
    private lateinit var mJobObserver: JobObserver

    var capabilities: ScanAttributesCaps? = null
        private set
    var fileOptionsAttributesCaps: FileOptionsAttributesCaps? = null
        private set


    private var mJobId: String? = null
    private var mRid: String? = null

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar
    private lateinit var mBindingActivityMain: ActivityMainBinding
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)

        if (SCREEN_4_3_INCH == mBindingActivityMain.container.tag) {
            setSupportActionBar(mBindingActivityMain.toolbar)
            mBindingActivityMain.fabMenu?.setOnClickListener {
                if (mBindingActivityMain.buttonBarLayout.visibility == View.VISIBLE) {
                    val param = mBindingActivityMain.dataContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0, 0, 0, 0)
                    mBindingActivityMain.dataContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.GONE
                } else {
                    val param = mBindingActivityMain.dataContainer.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0, 0, 0, 40)
                    mBindingActivityMain.dataContainer.layoutParams = param
                    mBindingActivityMain.buttonBarLayout.visibility = View.VISIBLE
                }
            }
        }
        // add click listener to call the MFP
        addListener()

        mJobObserver = JobObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()

        // Register JobObserver to receive job state callbacks
        mJobObserver.register(this)

        mBindingActivityMain.container.isEnabled = false
        mFragment = ScanConfigureFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.dataContainer, mFragment)
            .commit()

        /* Background task for Workpath SDK API initialization */
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val linkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), linkInfo.versionName, linkInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
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
     * Launches Capabilities loading async task
     */
    private fun loadCapabilities() {
        lifecycleScope.launch(Dispatchers.Default) {
            LoadCapabilitiesTask(this@MainActivity, mFragment).execute()
        }
    }

    /**
     * Launches defaults loading async task
     */
    private fun loadDefaults() {
        if (capabilities == null) {
            Logger.showResult(this, getString(R.string.capabilities_not_loaded))
        } else {
            lifecycleScope.launch(Dispatchers.Default) {
                LoadDefaultsTask(this@MainActivity, mFragment).execute()
            }
        }
    }

    /**
     * Executes request for capabilities from ScannerService.
     *
     * @param context [Context]
     * @return [com.hp.workpath.api.scanner.ScanAttributesCaps]
     */
    fun requestCaps(context: Context, result: Result): ScanAttributesCaps? {
        // cache capabilities for building ScanAttributes
        capabilities = ScannerService.getCapabilities(context, result)

        if (result.code == Result.RESULT_OK && capabilities != null) {
            Log.i(TAG, "Caps=" + Logger.build(capabilities))
            Log.i(TAG, "DocumentFormatsByColorMode=" + Logger.build(capabilities?.documentFormatsByColorMode))

            // get file options for defaults
            fileOptionsAttributesCaps = requestFileOptionsCapabilities(
                ScanAttributes.ColorMode.DEFAULT, ScanAttributes.DocumentFormat.DEFAULT)
        } else {
            Logger.showResult(this, "ScannerService.getCapabilities()", result)
        }

        return capabilities
    }

    fun requestFileOptionsCapabilities(colorMode: ScanAttributes.ColorMode, docFormat: ScanAttributes.DocumentFormat): FileOptionsAttributesCaps? {
        // cache file options capabilities for building FileOptionsAttributes later
        val result = Result()
        fileOptionsAttributesCaps = ScannerService.getFileOptionsCapabilities(this, colorMode, docFormat, result)
        if (result.code == Result.RESULT_OK && fileOptionsAttributesCaps != null) {
            Log.i(TAG, "FileOptionsAttributesCaps=" + Logger.build(fileOptionsAttributesCaps, colorMode, docFormat))
            return fileOptionsAttributesCaps
        } else {
            Logger.showResult(this, "ScannerService.getFileOptionsCapabilities", result)
        }
        return null
    }

    /**
     * Sets listeners for all buttons
     */
    private fun addListener() {
        mBindingActivityMain.scanButton.setOnClickListener { scanToDestination() }

        mBindingActivityMain.loadCapsButton.setOnClickListener { loadCapabilities() }

        mBindingActivityMain.loadDefaultsButton.setOnClickListener { loadDefaults() }

        mBindingActivityMain.getJobInfoButton.setOnClickListener { getJobInfo() }

        mBindingActivityMain.cancelButton.setOnClickListener { cancelJob() }
    }

    /**
     * Prepares [com.hp.workpath.api.scanner.ScanAttributes] and submits scan job.
     */
    private fun scanToDestination() {
        mJobId = null
        mRid = null
        lifecycleScope.launch(Dispatchers.Default) {
            ScanToDestinationTask(this@MainActivity).execute()
        }
    }

    private fun cancelJob() {
        mJobId?.run {
            val result = JobService.cancelJob(this@MainActivity, this)
            Logger.showResult(this@MainActivity, "Cancel: ", result)
        } ?: run {
            Logger.showResult(this@MainActivity, "There is no JobId")
        }
    }

    /**
     * Obtain current job info
     */
    private fun getJobInfo() {
        val result = Result()

        mJobId?.let {
            val jobInfo = JobService.getJobInfo(applicationContext, it, result)
            if (result.code != Result.RESULT_OK) {
                Logger.showResult(this, "JobService.getJobInfo", result)
            } else {
                Logger.showResult(this, "JobInfo=" + Logger.build(jobInfo))
            }
        } ?: run {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.no_job_info))
            }
        }
    }

    fun handleComplete() {
        mBindingActivityMain.container.isEnabled = true
        mFragment.isSDKInitialized = true
    }

    fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(R.string.sdk_support_missing)
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

    fun setRid(rid: String) {
        this.mRid = rid
    }

    /**
     * Observer for submitted job
     */
    private inner class JobObserver(handler: Handler) : JobService.AbstractJobletObserver(handler) {

        override fun onProgress(rid: String, jobInfo: JobInfo) {
            Log.i(TAG, "onProgress: Received rid=$rid")
            Log.i(TAG, "JobInfo=" + Logger.build(jobInfo))
            if (rid == mRid) {
                if (mJobId == null) {
                    jobInfo.jobId?.let {
                        mJobId = it

                        Log.i(TAG, "Received jobId=$mJobId")
                        showSnackBar(getString(R.string.job_id, mJobId))

                        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val monitorJob = prefs.getBoolean(ScanConfigureFragment.PREF_MONITOR_JOB, true)

                        if (monitorJob) {
                            prefs.edit().putString(ScanConfigureFragment.CURRENT_JOB_ID, mJobId).apply()

                            val showProgress = prefs.getBoolean(ScanConfigureFragment.PREF_SHOW_JOB_PROGRESS, true)

                            // Monitor the job completion
                            val taskAttributes = JobletAttributes.Builder().setShowUi(showProgress).build()

                            val intent = Intent(applicationContext, JobCompleteReceiver::class.java)
                            intent.action = ACTION_SCAN_COMPLETED
                            intent.putExtra(JobCompleteReceiver.RID_EXTRA, rid)
                            intent.putExtra(JobCompleteReceiver.JOB_ID_EXTRA, mJobId)

                            val jrid = JobService.monitorJobInForeground(this@MainActivity, it, taskAttributes, intent)

                            Log.i(TAG, "MonitorJob request: $jrid")
                        }
                    }
                }
            }
        }

        override fun onComplete(rid: String, jobInfo: JobInfo) {
            Log.i(TAG, "onComplete: Received rid=$rid")
            Log.i(TAG, "JobInfo=" + Logger.build(jobInfo))
            if (jobInfo.jobType == JobInfo.JobType.SCAN) {
                val scanJobData = jobInfo.getJobData<ScanJobData>()
                val images = scanJobData.fileNames

                if (images != null && images.size > 0) {
                    showSnackBar("Scan completed! Image path is " + UrlDecoder_replacer(Arrays.toString(images.toTypedArray())))
                } else {
                    showSnackBar(getString(R.string.job_completed))
                }
            }
        }

        fun UrlDecoder_replacer(input_data: String): String? {
            var data:String = input_data
            try {
                data = data.replace("%(?![0-9a-fA-F]{2})", "%25");
                data = data.replace("\\+", "%2B");
                data = URLDecoder.decode(data, "utf-8");
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return data
        }

        override fun onFail(rid: String?, result: Result) {
            Log.e(TAG, "onFail: Received rid=$rid")
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
                mSnackBar = Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
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
        const val TAG = "[SDK]" + "[SCANS]"
        const val ACTION_SCAN_COMPLETED = "com.hp.workpath.sample.scansample.ACTION_SCAN_COMPLETED"
        const val STATE_JOB_ID = "jobId"
        const val STATE_RID = "rid"
    }
}
