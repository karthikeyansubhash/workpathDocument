// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.hp.workpath.api.job.JobService.AbstractJobletObserver
import com.hp.workpath.api.job.JobletAttributes
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.sample.printsample.Logger.build
import com.hp.workpath.sample.printsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.printsample.fragments.CheckboxListDialogFragment
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment
import com.hp.workpath.sample.printsample.fragments.RadioListDialogFragment
import com.hp.workpath.sample.printsample.task.InitializationTask
import com.hp.workpath.sample.printsample.task.LoadCapabilitiesTask
import com.hp.workpath.sample.printsample.task.LoadDefaultsTask
import com.hp.workpath.sample.printsample.task.RequestPrintTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.collections.ArrayList

/**
 * Main activity for Print Sample.
 */
class MainActivity : AppCompatActivity(), CheckboxListDialogFragment.BatchJobListInterface {
    private lateinit var mFragment: PrintConfigureFragment
    private lateinit var mJobObserver: JobObserver

    private var mJobId: String? = null
    private var mRid: String? = null
    var capabilities: PrintAttributesCaps? = null
        private set
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar
    private lateinit var mBindingActivityMain: ActivityMainBinding
    private var mResumedFromFileBrowser = false
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    var batchJobs: ArrayList<PrintAttributes> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        val tag = mBindingActivityMain.layout.tag
        if (SCREEN_4_3_INCH == tag) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }
        setContentView(mBindingActivityMain.root)

        // add click listener
        addListener()
        mJobObserver = JobObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()

        // Register JobObserver to receive job state callbacks
        mJobObserver.register(applicationContext)
        mBindingActivityMain.container.isEnabled = false

        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }

        if (!mResumedFromFileBrowser) {
            mFragment = PrintConfigureFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.dataContainer, mFragment)
                    .commit()
        }
        mResumedFromFileBrowser = false
    }

    override fun onPause() {
        super.onPause()

        // Unregister JobObserver
        mJobObserver.unregister(applicationContext)

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

    private fun addListener() {
        // Set listener for Print execution
        mBindingActivityMain.printButton.setOnClickListener {
            showListDialog(
                RadioListDialogFragment.newInstance(batchJobs, false)
            )
        }

        mBindingActivityMain.addToBatchButton?.setOnClickListener {
            executePrint(
                BATCH_CLICKED,
                false
            )
        }

        // Set listener for Load Capabilities
        mBindingActivityMain.loadCapsButton.setOnClickListener { loadCapabilities() }

        // Set listener for Load Defaults
        mBindingActivityMain.loadDefaultsButton.setOnClickListener { loadDefaults() }

        // Set listener for list batch job
        mBindingActivityMain.listBatchJobButton?.setOnClickListener { listBatchJobs() }

        // Set listener for get Job Info
        mBindingActivityMain.getJobInfoButton.setOnClickListener { jobInfo }

        mBindingActivityMain.cancelButton.setOnClickListener { cancelJob() }

        mBindingActivityMain.fab?.setOnClickListener {
            if (mBindingActivityMain.footer?.getVisibility() == View.VISIBLE) {
                mBindingActivityMain.footer?.setVisibility(View.GONE)
            } else {
                mBindingActivityMain.footer?.setVisibility(View.VISIBLE)
            }
        }
    }

    private fun showListDialog(dialog: RadioListDialogFragment) {
        dialog.show(supportFragmentManager, "dialog")
        supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            this
        ) { _, result ->
            val backgroundJob = result.getBoolean(RadioListDialogFragment.BACKGROUND_JOB, false)
            Log.d(TAG, "showListDialog: $backgroundJob")
            executePrint(PRINT_CLICKED, backgroundJob)
        }
    }

    /**
     * Method to list batch jobs
     */
    private fun listBatchJobs() {
        if (batchJobs.isEmpty()) {
            showSnackBar(getString(R.string.no_jobs_added))
            return
        }
        CheckboxListDialogFragment
            .newInstance(batchJobs)
            .show(supportFragmentManager, "dialog")
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

    fun setResumedFromFileBrowser(value: Boolean) {
        mResumedFromFileBrowser = value
    }

    fun disableEnableBatchButton(boolean: Boolean) {
        mBindingActivityMain.addToBatchButton?.isEnabled = boolean
        mBindingActivityMain.listBatchJobButton?.isEnabled = boolean
        if (batchJobs.isNotEmpty()) {
            if (!boolean) {
                mAlertDialog = AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Discard List Batch Jobs")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, _ ->
                        batchJobs.clear()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        mBindingActivityMain.addToBatchButton?.isEnabled = true
                        mBindingActivityMain.listBatchJobButton?.isEnabled = true
                        loadDefaults()
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    /**
     * Launches capabilities loading async task
     */
    private fun loadCapabilities() {
        if (::mFragment.isInitialized) {
            lifecycleScope.launch(Dispatchers.Default) {
                LoadCapabilitiesTask(this@MainActivity, mFragment).execute()
            }
        }
    }

    /**
     * Launches defaults loading async task
     */
    private fun loadDefaults() {
        if (capabilities == null) {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.capabilities_not_loaded))
            }
        } else {
            if (::mFragment.isInitialized) {
                lifecycleScope.launch(Dispatchers.Default) {
                    LoadDefaultsTask(this@MainActivity, mFragment).execute()
                }
            }
        }
    }

    /**
     * Launches Print job
     */
    private fun executePrint(i: Int, backgroundJob: Boolean) {
        mJobId = null
        mRid = null
        lifecycleScope.launch(Dispatchers.Default) {
            RequestPrintTask(this@MainActivity).execute(i, backgroundJob)
        }
    }

    /**
     * Cancel Print job
     */
    private fun cancelJob() {
        mJobId?.let {
            val result = JobService.cancelJob(this@MainActivity, it)
            Logger.showResult(this, "Cancel: ", result)
        } ?: run {
            Logger.showResult(this, "There is no JobId");
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
                    Logger.showResult(this, "JobService.getJobInfo(): ", result)
                } else {
                    Logger.showResult(this, "JobInfo=" + build(jobInfo))
                }
            } ?: run {
                if (applicationContext != null) {
                    Logger.showResult(this, getString(R.string.no_job_info))
                }
            }
        }

    /**
     * Requests printer Print capabilities
     *
     * @param context [Context] to obtain data
     * @return [com.hp.workpath.api.printer.PrintAttributesCaps]
     */
    fun requestCaps(context: Context, result: Result): PrintAttributesCaps? {
        // cache capabilities for building PrintAttributes
        capabilities = PrinterService.getCapabilities(context, result)
        if (result.code == Result.RESULT_OK && capabilities != null) {
            Log.i(TAG, "Caps=" + build(capabilities))
        } else {
            Logger.showResult(this, "PrinterService.getCapabilities", result)
        }
        return capabilities
    }

    fun setRid(rid: String?) {
        mRid = rid
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
                    jobInfo.jobId?.let {
                        mJobId = it

                        Log.i(TAG, "Received jobId=$it")
                        showSnackBar(getString(R.string.job_id, it))

                        val prefs =
                                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        if (prefs.getBoolean(PrintConfigureFragment.PREF_MONITORING_JOB, false)) {
                            // Store Job Id in order to verify it in the Broadcast Receiver
                            prefs.edit().putString(PrintConfigureFragment.CURRENT_JOB_ID, it)
                                    .apply()

                            val intent = Intent(applicationContext, JobCompleteReceiver::class.java)
                            intent.action = ACTION_PRINT_COMPLETED
                            intent.putExtra(JobCompleteReceiver.RID_EXTRA, rid)
                            intent.putExtra(JobCompleteReceiver.JOB_ID_EXTRA, it)

                            val showProgress = prefs.getBoolean(
                                    PrintConfigureFragment.PREF_SHOW_JOB_PROGRESS,
                                    true
                            )
                            // Monitor the job completion
                            val taskAttributes =
                                    JobletAttributes.Builder().setShowUi(showProgress).build()
                            val jrid = JobService.monitorJobInForeground(
                                    this@MainActivity,
                                    it,
                                    taskAttributes,
                                    intent
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
            if (jobInfo.jobType == JobInfo.JobType.PRINT) {
                showSnackBar(getString(R.string.job_completed, jobInfo.jobName))
                Log.i("TAG_Print","Print job completed")
            }
        }

        override fun onFail(rid: String?, result: Result) {
            Log.i(TAG, "onFail: Received rid=" + rid + ", " + build(result))
            showSnackBar("onFail: Received rid=" + rid + ", " + getString(R.string.job_failed))
        }

        override fun onCancel(rid: String?) {
            Log.i(TAG, "onCancel: Received rid=$rid")
            showSnackBar(getString(R.string.job_cancelled))
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
        const val TAG = "[SAMPLE]" + "Print"
        const val ACTION_PRINT_COMPLETED =
                "com.hp.workpath.sample.printsample.ACTION_PRINT_COMPLETED"
        private const val STATE_JOB_ID = "jobId"
        private const val STATE_RID = "rid"
        const val PRINT_CLICKED = 1
        const val BATCH_CLICKED = 2
        const val REQUEST_KEY = "result-listener-request-key"
    }

    override fun showJobSelectionError() {
        Toast.makeText(this@MainActivity, (R.string.select_atleast_one_job), Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when list of jobs are selected to be removed from batch
     */
    override fun onRemovePrintAttributeFromList(mPrintObjectdMap: MutableMap<Int, Boolean>) {
        var indices: ArrayList<Int> = ArrayList()

        for (mPrintObject in mPrintObjectdMap) {
            if (mPrintObject.value) indices.add(mPrintObject.key)
        }

        Collections.sort(indices, Collections.reverseOrder());
        indices.stream().mapToInt { i -> i }.forEach { batchJobs.removeAt(it) }

        showSnackBar(getString(R.string.files_removed))
    }
}