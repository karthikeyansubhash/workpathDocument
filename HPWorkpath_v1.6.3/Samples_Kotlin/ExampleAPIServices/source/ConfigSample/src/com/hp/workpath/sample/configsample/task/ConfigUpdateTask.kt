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


class ConfigUpdateTask(context: MainActivity, value: String) {

    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val value: String = value

    suspend fun execute() {
        var result: Result? = null
        try {
            mContextRef.get()?.run {
                // convert string value to json object
                val json = JSONObject(value)
                // call ConfigService API to set new configuration value for current application
                result = ConfigService.setDefaultConfig(this, json)
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(result)
    }

    private suspend fun onPostExecute(result: Result?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgressBar(View.GONE)
                setConfigComplete()
                if (result != null && result.code == Result.RESULT_OK) {
                    Logger.showResult(this, getString(R.string.success))
                } else if (mThrowable != null) {
                    Logger.showResult(this, "ConfigService.setDefaultConfig ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "ConfigService.setDefaultConfig", result)
                }
            }
        }
    }
}