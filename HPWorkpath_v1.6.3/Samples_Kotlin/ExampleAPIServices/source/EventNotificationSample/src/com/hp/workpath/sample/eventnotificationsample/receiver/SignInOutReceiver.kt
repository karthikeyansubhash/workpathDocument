package com.hp.workpath.sample.eventnotificationsample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.eventnotificationsample.task.AccessInitializationTask
import com.hp.workpath.sample.eventnotificationsample.handler.StandaloneResultHandler
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInOutReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "[SAMPLE]SiSoER"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Workpath.actions.SIGN_IN.equals(intent?.action)) {
            Log.i(TAG, "Sign in event received")
        } else if (Workpath.actions.SIGN_OUT.equals(intent?.action)) {
            Log.i(TAG, "Sign out event received")
        }
        
        context?.let { ctx ->
            intent?.action?.let { action ->
                // Create a standalone handler that isn't tied to the BroadcastReceiver lifecycle
                val handler = StandaloneResultHandler(ctx, action)
                
                // Start the initialization task with the standalone handler
                val initTask: InitializationTask = AccessInitializationTask(ctx, handler)
                handler.setInitTask(initTask)
                
                // Use coroutine to execute task in background
                CoroutineScope(Dispatchers.IO).launch {
                    initTask.taskExecute()
                }
                
                // BroadcastReceiver can now safely terminate
            }
        }
    }
}