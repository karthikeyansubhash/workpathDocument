// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.scansample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.sample.scansample.fragments.ScanConfigureFragment;

import androidx.preference.PreferenceManager;

/**
 * Receiver for PendingIntent from Job Manager about scan job completion
 */
public final class JobCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = MainActivity.TAG + "/BR";

    public static final String RID_EXTRA = "rid";
    public static final String JOB_ID_EXTRA = "jobid";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Received intent is null");
            return;
        }

        final String action = intent.getAction();
        final ComponentName component = intent.getComponent();
        // Verify that received Job Id is same as expected one
        final String jobId = intent.getStringExtra(JOB_ID_EXTRA);
        final String expectedJobId = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ScanConfigureFragment.CURRENT_JOB_ID, null);

        if (MainActivity.ACTION_SCAN_COMPLETED.equals(action) &&
                component != null && context.getPackageName().equals(component.getPackageName()) &&
                jobId.equals(expectedJobId)) {
            Log.d(TAG, context.getString(R.string.received_complete_intent));
        }
    }
}
