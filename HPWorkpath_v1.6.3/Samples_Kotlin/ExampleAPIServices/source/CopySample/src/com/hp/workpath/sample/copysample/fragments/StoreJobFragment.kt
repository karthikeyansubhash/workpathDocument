// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.copier.JobCredentialsAttributes
import com.hp.workpath.api.copier.StoredJobInfo
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.databinding.FragmentStoreJobBinding
import com.hp.workpath.sample.copysample.fragments.ListDialogFragment.Companion.newInstance
import com.hp.workpath.sample.copysample.task.EnumerateStoredJobTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreJobFragment : Fragment() {
    private var mStoredJobInfoList: List<StoredJobInfo> = ArrayList()
    private var mCurrentStoredJob: StoredJobInfo? = null
    private lateinit var mPrefs: SharedPreferences
    private var mBindingFragment: FragmentStoreJobBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingFragmentStoreJob get() = mBindingFragment!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = getDefaultSharedPreferences(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentStoreJobBinding.inflate(inflater, container, false)
        return mBindingFragmentStoreJob.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBindingFragmentStoreJob.enumerateLayout.setOnClickListener { showEnumerateListDialog() }
        mBindingFragmentStoreJob.enumerateButton.setOnClickListener {
            showProgressBar(View.VISIBLE)
            lifecycleScope.launch(Dispatchers.Default) {
                EnumerateStoredJobTask(this@StoreJobFragment).execute()
            }
        }
        mBindingFragmentStoreJob.detailButton.setOnClickListener {
            mCurrentStoredJob?.run {
                val dialogFragment = DetailDialogFragment.newInstance(this)
                dialogFragment.show(parentFragmentManager, "dialog")
            } ?: run {
                if (context != null) {
                    Logger.showResult(activity, getString(R.string.stored_job_not_loaded))
                }
            }
        }
        initStoredJobList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private fun initStoredJobList() {
        if (mStoredJobInfoList.isNotEmpty()) {
            val jobId = currentStoredJobId
            if (jobId != null) {
                selectedStoredJob(jobId)
            } else {
                selectedStoredJob(mStoredJobInfoList[0])
            }
        } else {
            currentStoredJobId = null
        }
    }

    fun showProgressBar(visibility: Int) {
        mBindingFragmentStoreJob.progressBar.visibility = visibility
    }

    fun enumerateStoredJob(storedJobInfoList: List<StoredJobInfo>) {
        mStoredJobInfoList = storedJobInfoList
        if (storedJobInfoList.isNotEmpty()) {
            val jobId = currentStoredJobId
            if (jobId != null) {
                selectedStoredJob(jobId)
            } else {
                selectedStoredJob(storedJobInfoList[0])
            }
            if (context != null) {
                Logger.showResult(activity, getString(R.string.succeed))
            }
        } else {
            if (context != null) {
                Logger.showResult(activity, getString(R.string.stored_job_empty))
            }
            mBindingFragmentStoreJob.enumerateTextView.text = ""
            mBindingFragmentStoreJob.passwordEditText.setText("")
            mBindingFragmentStoreJob.passwordEditText.visibility = View.GONE
        }
    }

    private fun selectedStoredJob(storedJobId: String) {
        var found = false
        for (storedJobInfo in mStoredJobInfoList) {
            if (storedJobId == storedJobInfo.storedJobId) {
                selectedStoredJob(storedJobInfo)
                found = true
                break
            }
        }
        if (!found) {
            selectedStoredJob(mStoredJobInfoList[0])
        }
    }

    private fun selectedStoredJob(storedJobInfo: StoredJobInfo?) {
        if (storedJobInfo != null) {
            mCurrentStoredJob = storedJobInfo
            currentStoredJobId = storedJobInfo.storedJobId
            setCopiesInPreference(storedJobInfo.copies)
            val emulateStringBuilder = StringBuilder()
            emulateStringBuilder.append("(").append(storedJobInfo.storedJobId).append(")\n")
                .append("UserName: ").append(storedJobInfo.storedJobUserName).append(" / ")
                .append("JobName: ").append(storedJobInfo.storedJobName)
            mBindingFragmentStoreJob.enumerateTextView.text = emulateStringBuilder.toString()
            mBindingFragmentStoreJob.passwordEditText.setText("")
            if (JobCredentialsAttributes.PasswordType.NUMERIC == storedJobInfo.storedJobPasswordType) {
                mBindingFragmentStoreJob.passwordEditText.visibility = View.VISIBLE
            } else {
                mBindingFragmentStoreJob.passwordEditText.visibility = View.GONE
            }
        }
    }

    private fun setCopiesInPreference(copies: Int) {
        mPrefs.edit().putString(CopyConfigureFragment.PREF_COPIES, copies.toString()).apply()
    }

    private fun showEnumerateListDialog() {
        val jobId = currentStoredJobId
        if (jobId != null) {
            val dialogFragment = newInstance(mStoredJobInfoList, jobId)
            dialogFragment.show(parentFragmentManager, "dialog")
            parentFragmentManager.setFragmentResultListener(
                REQUEST_KEY,
                this
            ) { requestKey, result ->
                if (REQUEST_KEY == requestKey) {
                    val storedJobId = result.getString(ListDialogFragment.CURRENT_SELECTED)
                    selectedStoredJob(storedJobId.toString())
                }
            }
        } else {
            if (context != null) {
                Logger.showResult(activity, getString(R.string.stored_job_not_loaded))
            }
        }
    }

    @get:Throws(CapabilitiesExceededException::class)
    val jobCredentials: JobCredentialsAttributes
        get() {
            val builder = JobCredentialsAttributes.Builder()
            mCurrentStoredJob?.run {
                builder.setPasswordType(storedJobPasswordType)
                builder.setPassword(mBindingFragmentStoreJob.passwordEditText.text.toString())
            }
            return builder.build()
        }

    private var currentStoredJobId: String?
        get() = if (activity != null) {
            (requireActivity() as MainActivity).storedJobId
        } else null
        private set(storedJobId) {
            (requireActivity() as MainActivity).storedJobId = storedJobId
        }

    companion object {
        const val REQUEST_KEY = "1"
    }
}