package com.hp.workpath.sample.eventnotificationsample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hp.workpath.sample.eventnotificationsample.MainActivity
import com.hp.workpath.sample.eventnotificationsample.R

class ForegroundService : Service() {
    companion object {
        val TAG = MainActivity.TAG + "S"
        val ACCESS_NOTIFICATION_ID = 2024
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.i(TAG, "onCreate")
    }

    private fun createNotificationChannel() {
        val NOTIFICATION_CHANNEL_ID = getString(R.string.app_name)
        val channelName = getString(R.string.channel_name)
        val nc = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN)
        nc.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(nc)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        notificationBuilder.priority = NotificationCompat.PRIORITY_MAX
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_transparent)
            .setContentTitle(getText(R.string.channel_description))
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(ACCESS_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}