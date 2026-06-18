package com.hp.workpath.sample.eventnotificationsample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager

class JobCompletedReceiver : BroadcastReceiver() {
    companion object {
        val TAG = "[SAMPLE]JobCompletedER"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Job completed event received")
        PreferenceManager.saveEvent(context, intent?.action, null)
    }
}