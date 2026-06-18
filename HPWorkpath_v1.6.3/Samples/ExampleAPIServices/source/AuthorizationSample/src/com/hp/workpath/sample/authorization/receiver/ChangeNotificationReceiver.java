// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hp.workpath.api.authorization.ChangeNotificationEventCode;
import com.hp.workpath.api.authorization.ChangeNotificationEventData;
import com.hp.workpath.sample.authorization.MainActivity;

public class ChangeNotificationReceiver extends BroadcastReceiver {

    private static final String ACTION_CHANGE_NOTIFICATION = "com.hp.workpath.action.AUTHORIZATION_CHANGE_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(ACTION_CHANGE_NOTIFICATION)) {
            ChangeNotificationEventData data = intent.getParcelableExtra("changeNotificationEventData");
            Log.i(MainActivity.TAG, "ChangeNotificationReceiver: eventCode=" + data.getEventCode() + ", timestamp=" + data.getTimestamp());

            if (data.getEventCode().equals(ChangeNotificationEventCode.PERMISSION_ADDED)) {
                Log.i(MainActivity.TAG, "Permissions have changed");
            } else if (data.getEventCode().equals(ChangeNotificationEventCode.PROXY_CONFIGURATION_CHANGED)) {
                Log.i(MainActivity.TAG, "Configuration has changed");
            }
        }
    }
}
