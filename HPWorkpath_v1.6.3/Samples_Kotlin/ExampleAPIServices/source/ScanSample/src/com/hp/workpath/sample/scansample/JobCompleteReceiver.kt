// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment

/**
 * Receiver for PendingIntent from Job Manager about scan job completion
 */
class JobCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            Log.d(TAG, "Received intent is null")
            return
        }

        val action = intent.action
        val component = intent.component
        // Verify that received Job Id is same as expected one
        val jobId = intent.getStringExtra(JOB_ID_EXTRA)
        val expectedJobId = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ScanConfigureFragment.CURRENT_JOB_ID, null)

        if (MainActivity.ACTION_SCAN_COMPLETED == action &&
                component != null && context.packageName == component.packageName &&
                jobId == expectedJobId) {
            Log.d(TAG, context.getString(R.string.received_complete_intent))
        }
    }

    companion object {
        private const val TAG = MainActivity.TAG

        const val RID_EXTRA = "rid"
        const val JOB_ID_EXTRA = "jobid"
    }
}
