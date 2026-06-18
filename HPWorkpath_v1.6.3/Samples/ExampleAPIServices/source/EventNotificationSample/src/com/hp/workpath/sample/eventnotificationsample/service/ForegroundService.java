package com.hp.workpath.sample.eventnotificationsample.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hp.workpath.sample.eventnotificationsample.MainActivity;
import com.hp.workpath.sample.eventnotificationsample.R;

public class ForegroundService extends Service {
    private static final String TAG = MainActivity.TAG + "S";
    private int ACCESS_NOTIFICATION_ID = 2024;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.i(TAG, "onCreate");
    }

    private void createNotificationChannel() {
        String NOTIFICATION_CHANNEL_ID = getString(R.string.app_name);
        String channelName = getString(R.string.channel_name);
        NotificationChannel nc = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);
        nc.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(nc);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_transparent)
                .setContentTitle(getText(R.string.channel_description))
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(ACCESS_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
