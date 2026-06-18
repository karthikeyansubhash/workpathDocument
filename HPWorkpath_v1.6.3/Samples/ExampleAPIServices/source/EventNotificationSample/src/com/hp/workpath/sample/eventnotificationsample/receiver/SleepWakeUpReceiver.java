package com.hp.workpath.sample.eventnotificationsample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

public class SleepWakeUpReceiver extends BroadcastReceiver {

    public static final String TAG = "[SAMPLE]" + "SleepWakeupER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Sleep/Wakeup event received");
        PreferenceManager.saveEvent(context, intent.getAction(), null);
    }
}
