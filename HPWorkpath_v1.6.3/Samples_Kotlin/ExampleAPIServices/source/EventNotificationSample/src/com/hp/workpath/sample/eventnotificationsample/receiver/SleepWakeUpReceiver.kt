package com.hp.workpath.sample.eventnotificationsample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager

class SleepWakeUpReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "[SAMPLE]SleepWakeupER"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Sleep/Wakeup event received")
        PreferenceManager.saveEvent(context, intent?.action, null)
    }
}