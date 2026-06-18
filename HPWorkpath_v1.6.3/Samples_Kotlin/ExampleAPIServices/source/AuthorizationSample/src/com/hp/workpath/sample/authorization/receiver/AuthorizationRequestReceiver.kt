// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.authorization.Logger
import com.hp.workpath.sample.authorization.MainActivity
import com.hp.workpath.sample.authorization.task.SetConfigurationUsingDefaultConfigTask
import java.util.concurrent.Executors

class AuthorizationRequestReceiver : BroadcastReceiver() {

    companion object {
        private const val ACTION_AUTHORIZATION_REQUEST =
            "com.hp.workpath.action.AUTHORIZATION_REQUEST"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.i(MainActivity.TAG, "action: $action")
        if (action == ACTION_AUTHORIZATION_REQUEST) {
            try {
                context?.let {
                    Workpath.getInstance().initialize(it)
                    val executorService = Executors.newSingleThreadExecutor()
                    val future = executorService.submit(SetConfigurationUsingDefaultConfigTask(it))
                    val result = future.get()
                    Log.i(MainActivity.TAG, "AuthorizationRequestReceiver: ${Logger.build(result)}")
                }
            } catch (e: Exception) {
                Log.e(MainActivity.TAG, "AuthorizationRequestReceiver: ${e.message}")
            }
        }
    }
}