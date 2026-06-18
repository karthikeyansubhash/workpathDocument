package com.hp.workpath.sample.eventnotificationsample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.eventnotificationsample.task.AccessInitializationTask;
import com.hp.workpath.sample.eventnotificationsample.handler.StandaloneResultHandler;
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask;

public class SignInOutReceiver extends BroadcastReceiver {

    public static final String TAG = "[SAMPLE]" + "SiSoER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Workpath.actions.SIGN_IN.equals(intent.getAction())) {
            Log.i(TAG, "Sign in event received");
        } else if (Workpath.actions.SIGN_OUT.equals(intent.getAction())) {
            Log.i(TAG, "Sign out event received");
        }
        
        // Create a standalone handler that isn't tied to the BroadcastReceiver lifecycle
        StandaloneResultHandler handler = new StandaloneResultHandler(context, intent.getAction());
        
        // Start the initialization task with the standalone handler
        InitializationTask initTask = new AccessInitializationTask(context, handler);
        handler.setInitTask(initTask);
        initTask.taskExecute();
        
        // BroadcastReceiver can now safely terminate
    }
}
