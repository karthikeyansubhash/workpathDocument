package com.hp.workpath.sample.eventnotificationsample.handler

import android.content.Context
import android.util.Log
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask
import com.hp.workpath.sample.eventnotificationsample.task.ResultHandler
import com.hp.workpath.sample.eventnotificationsample.task.UserDetailsReaderTask
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A standalone implementation of ResultHandler that is not tied to the BroadcastReceiver lifecycle.
 * This handler will continue to work even after the BroadcastReceiver has been destroyed.
 */
class StandaloneResultHandler(context: Context, private val eventAction: String) : ResultHandler {
    companion object {
        const val TAG = "[SAMPLE]StandaloneHandler"
    }
    
    // Use application context to avoid memory leaks
    private val appContext: Context = context.applicationContext
    private var initTask: InitializationTask? = null
    
    fun setInitTask(task: InitializationTask) {
        this.initTask = task
    }
    
    override fun handleComplete() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val task = UserDetailsReaderTask(appContext, this@StandaloneResultHandler)
                task.taskExecute()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in handleComplete: ${e.message}")
            cleanup()
        }
    }
    
    override fun handleException(t: Throwable?) {
        Log.e(TAG, "handleException: ${t?.message}")
        cleanup()
    }
    
    override fun handleUpdate(currentPrincipal: String?) {
        try {
            Log.i(TAG, "handleUpdate: $currentPrincipal")
            PreferenceManager.saveEvent(appContext, eventAction, currentPrincipal)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in handleUpdate: ${e.message}")
        } finally {
            cleanup()
        }
    }
      /**
     * Clean up resources when all tasks are finished
     */
    private fun cleanup() {
        // Release the reference to the task
        initTask = null
    }
}
