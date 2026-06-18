// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task

import android.content.SharedPreferences

import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.copier.CopierService
import com.hp.workpath.api.copier.JobCredentialsAttributes
import com.hp.workpath.api.copier.StoredJobAttributes.StoredJobBuilder
import com.hp.workpath.sample.copysample.Logger
import com.hp.workpath.sample.copysample.MainActivity
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class ReleaseStoredTask(context: MainActivity, jobCredentials: JobCredentialsAttributes) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val jobCredentials: JobCredentialsAttributes = jobCredentials
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private var mThrowable: Throwable? = null
    private lateinit var mErrorMsg: String
    private var rid: String? = null

    suspend fun execute(vararg params: String) {
        try {
            mContextRef.get()?.run {
                val storedJobId = params[0]
                // Obtain Caps to build copy Attributes
                val caps = capabilities

                if (caps == null) {
                    mErrorMsg = getString(R.string.capabilities_not_loaded)
                    onPostExecute(rid)
                    return
                }

                val copies = Integer.valueOf(
                        mPrefs.getString(CopyConfigureFragment.PREF_COPIES, "1")!!)
                val storedJobAttributes = StoredJobBuilder(storedJobId)
                        .setCopies(copies)
                        .setJobCredentials(jobCredentials)
                        .build(caps)
                // Release the job
                rid = CopierService.releaseStoredJob(this, storedJobAttributes)
            }
        } catch (cee: CapabilitiesExceededException) {
            mErrorMsg = "CapabilitiesExceededException"
            mThrowable = cee
        } catch (iae: IllegalArgumentException) {
            mErrorMsg = "IllegalArgumentException"
            mThrowable = iae
        } catch (t: Throwable) {
            mErrorMsg = "Unknown Throwable"
            mThrowable = t
        }
        onPostExecute(rid)
    }

    private suspend fun onPostExecute(rid: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (!TextUtils.isEmpty(rid)) {
                    this.setRid(rid)
                    Logger.showResult(this, "Release StoredJob submitted with rid = $rid")
                } else {
                    if (::mErrorMsg.isInitialized) {
                        if (mThrowable != null) {
                            Logger.showResult(this, "$mErrorMsg ${mThrowable?.message}")
                        } else {
                            Logger.showResult(this, mErrorMsg)
                        }
                    }
                }
            }
        }
    }
}