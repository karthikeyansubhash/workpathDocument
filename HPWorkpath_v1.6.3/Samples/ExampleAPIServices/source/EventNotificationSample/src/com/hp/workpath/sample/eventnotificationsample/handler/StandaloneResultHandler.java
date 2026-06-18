package com.hp.workpath.sample.eventnotificationsample.handler;

import android.content.Context;
import android.util.Log;

import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask;
import com.hp.workpath.sample.eventnotificationsample.task.ResultHandler;
import com.hp.workpath.sample.eventnotificationsample.task.UserDetailsReaderTask;
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

/**
 * A standalone implementation of ResultHandler that is not tied to the BroadcastReceiver lifecycle.
 * This handler will continue to work even after the BroadcastReceiver has been destroyed.
 */
public class StandaloneResultHandler implements ResultHandler {
    public static final String TAG = "[SAMPLE]" + "StandaloneHandler";
    
    private final Context mAppContext;  // Use application context to avoid memory leaks
    private final String mEventAction;
    private InitializationTask mInitTask;
    
    public StandaloneResultHandler(Context context, String eventAction) {
        // Store application context to avoid memory leaks
        this.mAppContext = context.getApplicationContext();
        this.mEventAction = eventAction;
    }
    
    public void setInitTask(InitializationTask task) {
        this.mInitTask = task;
    }
    
    @Override
    public void handleComplete() {
        try {
            new UserDetailsReaderTask(mAppContext, this).taskExecute();
        } catch (Exception e) {
            Log.e(TAG, "Exception in handleComplete: " + e.getMessage());
            cleanup();
        }
    }
    
    @Override
    public void handleException(Throwable t) {
        Log.e(TAG, "handleException: " + t.getMessage());
        cleanup();
    }
    
    @Override
    public void handleUpdate(String currentPrincipal) {
        try {
            Log.i(TAG, "handleUpdate: " + currentPrincipal);
            PreferenceManager.saveEvent(mAppContext, mEventAction, currentPrincipal);
        } catch (Exception e) {
            Log.e(TAG, "Exception in handleUpdate: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Clean up resources when all tasks are finished
     */
    private void cleanup() {
        if (mInitTask != null) {
            mInitTask.cancel();
            mInitTask = null;
        }
    }
}
