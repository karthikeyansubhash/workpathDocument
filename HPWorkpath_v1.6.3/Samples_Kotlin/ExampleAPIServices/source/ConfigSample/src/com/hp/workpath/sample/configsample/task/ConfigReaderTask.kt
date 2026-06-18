// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.config.ConfigService
import com.hp.workpath.sample.configsample.Logger
import com.hp.workpath.sample.configsample.MainActivity
import com.hp.workpath.sample.configsample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * Worker task for configuration request
 */
class ConfigReaderTask(context: MainActivity, isUpdate: Boolean) {

    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()
    private val isUpdate: Boolean = isUpdate

    suspend fun execute() {
        var configuration: JSONObject? = null
        // call ConfigService API to retrieve configuration for the application
        try {
            mContextRef.get()?.run {
                configuration = ConfigService.getDefaultConfig(this, result)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(configuration)
    }

    private suspend fun onPostExecute(jsonObject: JSONObject?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                if (jsonObject != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, getString(R.string.success))
                    getConfigComplete(jsonObject)
                    if (isUpdate) {
                        updatePrintOption(jsonObject)
                    }
                } else if (mThrowable != null) {
                    Logger.showResult(this, "ConfigService.getDefaultConfig ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "ConfigService.getDefaultConfig", result)
                }
            }
        }
    }
}