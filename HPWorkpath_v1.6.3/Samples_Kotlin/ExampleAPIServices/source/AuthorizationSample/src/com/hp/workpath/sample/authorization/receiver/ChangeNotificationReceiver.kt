// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hp.workpath.api.authorization.ChangeNotificationEventCode
import com.hp.workpath.api.authorization.ChangeNotificationEventData
import com.hp.workpath.sample.authorization.MainActivity

class ChangeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == ACTION_CHANGE_NOTIFICATION) {
            val data =
                intent.getParcelableExtra<ChangeNotificationEventData>("changeNotificationEventData")
            Log.i(
                MainActivity.TAG,
                "ChangeNotificationReceiver: eventCode=${data?.eventCode}, timestamp=${data?.timestamp}"
            )
            when (data?.eventCode) {
                ChangeNotificationEventCode.PERMISSION_ADDED.value() -> {
                    // Handle permission added event
                    Log.i(MainActivity.TAG, "Permissions have changed")
                }

                ChangeNotificationEventCode.PROXY_CONFIGURATION_CHANGED.value() -> {
                    // Handle proxy configuration changed event
                    Log.i(MainActivity.TAG, "Configuration has changed")
                }

                else -> {
                    // Handle unknown event code
                }
            }
        }
    }

    companion object {
        private const val ACTION_CHANGE_NOTIFICATION =
            "com.hp.workpath.action.AUTHORIZATION_CHANGE_NOTIFICATION"
    }
}