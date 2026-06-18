package com.hp.workpath.sample.eventnotificationsample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

public class JobCompletedReceiver extends BroadcastReceiver {

    public static final String TAG = "[SAMPLE]" + "JobCompletedER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Job completed event received");
        PreferenceManager.saveEvent(context, intent.getAction(), null);
    }
}
