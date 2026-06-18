package com.hp.workpath.sample.eventnotificationsample.handler;

import android.content.Context;
import android.util.Log;

import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.eventnotificationsample.task.ConfigReaderTask;
import com.hp.workpath.sample.eventnotificationsample.task.InitializationTask;
import com.hp.workpath.sample.eventnotificationsample.task.ResultHandler;
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

/**
 * A standalone implementation of ResultHandler for config changes that is not tied to the BroadcastReceiver lifecycle.
 * This handler will continue to work even after the BroadcastReceiver has been destroyed.
 */
public class ConfigResultHandler implements ResultHandler {
    public static final String TAG = "[SAMPLE]" + "ConfigHandler";
    
    private final Context mAppContext;  // Use application context to avoid memory leaks
    private InitializationTask mInitTask;
    
    public ConfigResultHandler(Context context) {
        // Store application context to avoid memory leaks
        this.mAppContext = context.getApplicationContext();
    }
    
    public void setInitTask(InitializationTask task) {
        this.mInitTask = task;
    }
    
    @Override
    public void handleComplete() {
        try {
            new ConfigReaderTask(mAppContext, this).taskExecute();
        } catch (Exception e) {
            Log.e(TAG, "Exception in handleComplete: " + e.getMessage());
            cleanup();
        }
    }
    
    @Override
    public void handleException(Throwable t) {
        Log.e(TAG, "Config initialization failed: " + t.getMessage());
        cleanup();
    }
    
    @Override
    public void handleUpdate(String updateData) {
        try {
            PreferenceManager.saveEvent(mAppContext, Workpath.actions.CONFIG_CHANGED, updateData);
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
