package com.hp.workpath.sample.eventnotificationsample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.sample.eventnotificationsample.task.ConfigInitializationTask
import com.hp.workpath.sample.eventnotificationsample.handler.ConfigResultHandler
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigChangedReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "[SAMPLE]ConfigER"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Config changed event received")
        
        context?.let { ctx ->
            // Create a standalone handler that isn't tied to the BroadcastReceiver lifecycle
            val handler = ConfigResultHandler(ctx)
            
            // Start the initialization task with the standalone handler
            val initTask: InitializationTask = ConfigInitializationTask(ctx, handler)
            handler.setInitTask(initTask)
            
            // Use coroutine to execute task in background
            CoroutineScope(Dispatchers.IO).launch {
                initTask.taskExecute()
            }
            
            // BroadcastReceiver can now safely terminate
        }
    }
}