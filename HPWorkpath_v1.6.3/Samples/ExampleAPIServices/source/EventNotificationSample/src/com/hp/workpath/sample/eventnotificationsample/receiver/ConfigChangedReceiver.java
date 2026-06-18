package com.hp.workpath.sample.eventnotificationsample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.sample.eventnotificationsample.task.ConfigInitializationTask;
import com.hp.workpath.sample.eventnotificationsample.handler.ConfigResultHandler;
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask;

public class ConfigChangedReceiver extends BroadcastReceiver {

    public static final String TAG = "[SAMPLE]" + "ConfigER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Config changed event received");
        
        // Create a standalone handler that isn't tied to the BroadcastReceiver lifecycle
        ConfigResultHandler handler = new ConfigResultHandler(context);
        
        // Start the initialization task with the standalone handler
        InitializationTask initTask = new ConfigInitializationTask(context, handler);
        handler.setInitTask(initTask);
        initTask.taskExecute();
        
        // BroadcastReceiver can now safely terminate
    }
}
