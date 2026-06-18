// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryservicesample.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.access.Principal
import com.hp.workpath.api.accessory.AbstractAccessoryService
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.ReportEventInfo
import com.hp.workpath.api.accessory.hid.AccessoryService
import com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryObserver
import com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryStartObserver
import com.hp.workpath.api.accessory.hid.EventCode
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo
import com.hp.workpath.sample.accessoryservicesample.Logger.build
import com.hp.workpath.sample.accessoryservicesample.Logger.showResult
import com.hp.workpath.sample.accessoryservicesample.MainActivity
import com.hp.workpath.sample.accessoryservicesample.R
import com.hp.workpath.sample.accessoryservicesample.task.InitializationTask
import com.hp.workpath.sample.accessoryservicesample.task.InitializationTask.InitializeInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import java.util.Properties

class AccessoryService : AbstractAccessoryService() {
    private val ACCESSORY_NOTIFICATION_ID = 2019

    /* Background task for Workpath SDK API initialization */
    private lateinit var mInitializationTask: Job
    private var isInitializedSDK = false

    private lateinit var mAccessoryObserver: AccessoryObserver
    private lateinit var mAccessoryStartObserver: AccessoryStartObserver
    private var mAccessoryContextId: String? = null
    private var mOrdinal: Long = 0

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        createNotificationChannel()
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        if (this::mInitializationTask.isInitialized) {
            mInitializationTask.cancel()
        }
        unregisterObserver()
    }

    private fun unregisterObserver() {
        if (this::mAccessoryObserver.isInitialized) {
            try {
                mAccessoryObserver.unregister(applicationContext)
            } catch (t: Throwable) {
                showResult(null, "Unregister AccessoryObserver " + t.message)
            }
            Log.d(TAG, "AccessoryObserver is unregistered")
        }

        if (this::mAccessoryStartObserver.isInitialized) {
            try {
                mAccessoryStartObserver.unregister(applicationContext)
            } catch (t: Throwable) {
                showResult(null, "Unregister StartObserver ${t.message}")
            }
            Log.d(TAG, "AccessoryStartObserver is unregistered")
        }
    }

    private fun createNotificationChannel() {
        val notificationChannelId = getString(R.string.app_name)
        val channelName = getString(R.string.channel_name)
        val nc = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        nc.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(nc)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        notificationBuilder.foregroundServiceBehavior =
            NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_transparent)
            .setContentTitle(getText(R.string.channel_description))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(
            ACCESSORY_NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        unregisterObserver()
        initService(Handler(Looper.getMainLooper()), applicationContext)
        Log.i(TAG, "AccessoryObserver is registered")
        return START_STICKY
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        val intent = Intent(applicationContext, javaClass)
        val pi = PendingIntent.getForegroundService(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val am = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pi)
    }

    private fun initService(handler: Handler, context: Context) {
        Log.d(TAG, "init")
        initializedSDK()
        mAccessoryObserver = AccessoryObserver(handler)
        mAccessoryObserver.register(context)
        mAccessoryStartObserver = AccessoryStartObserver(handler)
        mAccessoryStartObserver.register(context)
    }

    fun resendOwnedAccessory() {
        val isReady = AccessoryService.isReady(applicationContext)
        Log.i(TAG, "isReady: $isReady")
        val accessoryInfoList = ownedAccessoryInfo
        Log.i(TAG, "resendOwnedAccessory: requesting resend")

        if (isReady && accessoryInfoList != null) {
            Log.i(TAG, "OwnedAccessoryList size: " + accessoryInfoList.size)
            for (accessoryInfo in accessoryInfoList) {
                val result = Result()
                AccessoryService.resendOwnedAccessoryContext(
                    applicationContext,
                    accessoryInfo,
                    result
                )
                showResult(null, "AccessoryService.resendOwnedAccessoryContext", result)
            }
        } else {
            Log.i(TAG, "resendOwnedAccessory: retry")
            resendOwnedAccessoryAfterDelay()
        }
    }

    private fun resendOwnedAccessoryAfterDelay() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ resendOwnedAccessory() }, (1000 * 60).toLong())
    }

    private fun initializedSDK() {
        Log.i(TAG, "initializedSDK: ")
        try {
            val initializeInterface: InitializeInterface = object : InitializeInterface {
                override fun handleComplete() {
                    Log.d(TAG, "initializedSDK handleComplete")
                    isInitializedSDK = true
                    resendOwnedAccessory()
                }

                override fun handleException(t: Throwable?) {
                    Log.e(TAG, "Workpath.initialize ${t?.message}")
                    stopSelf()
                }
            }

            if (!isInitializedSDK) {
                mInitializationTask = GlobalScope.launch {
                    Log.d(TAG, "initializedSDK GlobalScope.launch")
                    InitializationTask(applicationContext, initializeInterface).execute()
                }
            }
        } catch (t: Throwable) {
            showResult(null, "Workpath.initialize ${t.message}")
        }
    }

    private fun openAccessory(accessoryContextId: String) {
        val result = Result()
        Log.d(TAG, "Accessory opened: $accessoryContextId")
        AccessoryService.open(this@AccessoryService, accessoryContextId, result)
        showResult(null, "AccessoryService.open", result)
        if (result.code == Result.RESULT_OK) {
            startReadingAccessory(accessoryContextId)
        }
    }

    private fun startReadingAccessory(accessoryContextId: String) {
        val result = Result()
        Log.d(TAG, "Accessory startReading: $accessoryContextId")
        AccessoryService.startReading(this@AccessoryService, accessoryContextId, result)
        showResult(null, "AccessoryService.startReading", result)
    }

    private fun initiateSignIn() {
        val result = Result()
        Log.d(TAG, "initiateSignIn")
        AccessService.initiateSignIn(applicationContext, result)
        showResult(null, "AccessService.initiateSignIn", result)
    }

    private fun signOut() {
        val result = Result()
        Log.d(TAG, "signOut")
        AccessService.signOut(applicationContext, result)
        showResult(null, "AccessService.signOut", result)
    }

    private val currentPrincipal: Principal?
        get() {
            val result = Result()
            Log.d(TAG, "getCurrentPrincipal")
            val principal = AccessService.getCurrentPrincipal(applicationContext, result)
            showResult(null, "AccessService.getCurrentPrincipal", result)
            return if (result.code == Result.RESULT_OK) {
                principal
            } else {
                null
            }
        }

    /**
     * Observer for accessory event.
     */
    private inner class AccessoryObserver(handler: Handler?) : AbstractAccessoryObserver(handler) {
        override fun onContextChange(
            accessoryInfo: AccessoryInfo,
            eventCode: EventCode,
            timeStamp: String,
            accessoryContextId: String
        ) {
            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                Log.i(TAG, "AccessoryObserver onContextChange() accessoryContextId: $accessoryContextId")
                Log.i(TAG, StringBuilder().append("eventCode=").append(eventCode)
                        .append(", timestamp=").append(timeStamp).append("\n")
                        .append("AccessoryInfo=").append(build(accessoryInfo)).toString())
                if (!TextUtils.isEmpty(accessoryContextId)) {
                    if (accessoryContextId != mAccessoryContextId) {
                        mAccessoryContextId = accessoryContextId
                        openAccessory(accessoryContextId)
                    }
                }
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                Log.i(TAG, "Accessory context is expired")
                mAccessoryContextId = null
            }
        }

        override fun onReceive(accessoryInfo: AccessoryInfo, reportEventInfo: ReportEventInfo) {
            if (accessoryInfo.getDetails<AccessoryInfo?>() != null &&
                    accessoryInfo.getDetails<AccessoryInfo>() is HIDAccessoryInfo) {
                val hidAccessoryInfo = accessoryInfo.getDetails<HIDAccessoryInfo>()
                val hidReportEventInfo = reportEventInfo.getDetails<HIDReportEventInfo>()

                Log.i(TAG, "AccessoryObserver onReceive()")
                Log.i(TAG, "AccessoryInfo=" + build(hidAccessoryInfo))

                if (mOrdinal != hidReportEventInfo.ordinal) {
                    mOrdinal = hidReportEventInfo.ordinal

                    /** if reading accessory data is finished,
                     * start sign in or sign out.
                     */
                    if (hidReportEventInfo.reports != null) {
                        var endOfAccessoryData = false
                        val hidReports = hidReportEventInfo.reports
                        for (report in hidReports) {
                            val data = report.data
                            Log.i(TAG, "AccessoryService: " + Arrays.toString(data))
                            if (data != null && data.isNotEmpty()) {
                                for (a in data) {
                                    if (a.toInt() == 0x0d) { //CR
                                        endOfAccessoryData = true
                                        break
                                    }
                                }
                            }
                        }
                        if (endOfAccessoryData) {
                            val principal = currentPrincipal
                            if (principal != null && !principal.isAuthenticated) {
                                initiateSignIn()
                            } else {
                                signOut()
                            }
                        }
                    }
                }
            }
        }
    }

    private inner class AccessoryStartObserver(handler: Handler?) : AbstractAccessoryStartObserver(handler) {
        override fun onReady(
            accessoryInfo: AccessoryInfo,
            eventCode: EventCode,
            timeStamp: String,
            accessoryContextId: String
        ) {
            val log = StringBuilder()
                .append("accessoryContextId=").append(accessoryContextId).append("\n")
                .append(", eventCode=").append(eventCode).append("\n")
                .append(", timestamp=").append(timeStamp).append("\n")
                .append("AccessoryInfo=").append(build(accessoryInfo)).toString()
            Log.i(TAG, "AccessoryObserver onReady(): $log")
        }
    }

    // no file - no accessory information
    private val ownedAccessoryInfo: List<AccessoryInfo>?
        get() {
            val result = Result()
            val accessoryInfoList: MutableList<AccessoryInfo> = ArrayList()
            val ownedAccessoryInfoList = AccessoryService.getOwnedAccessories(applicationContext, result)

            if (result.code != Result.RESULT_OK) {
                return null
            }

            var inputStream: InputStream? = null
            for (propertyFile in PROPERTY_FILES) {
                try {
                    inputStream = assets.open(propertyFile)
                    val properties = Properties()
                    properties.load(inputStream)
                    val vendorId = properties.getProperty(HID_ACCESSORY_VENDOR_ID)
                    val productId = properties.getProperty(HID_ACCESSORY_PRODUCT_ID)
                    val serialNumber = properties.getProperty(HID_ACCESSORY_SERIAL_NUMBER, null)
                    for (accessoryInfo in ownedAccessoryInfoList) {
                        if (accessoryInfo is HIDAccessoryInfo) {
                            if (accessoryInfo.vendorId == vendorId.toInt() && accessoryInfo.productId == productId.toInt() && accessoryInfo.serialNumber == serialNumber) {
                                accessoryInfoList.add(accessoryInfo)
                            }
                        }
                    }
                } catch (t: Throwable) {
                    // no file - no accessory information
                    Log.e(TAG, "Failed to load property file $propertyFile")
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            Log.e(TAG, "Failed to close asset file $propertyFile")
                        }
                    }
                }
            }
            return accessoryInfoList
        }

    companion object {
        private const val TAG = MainActivity.TAG + "S"
        private const val HID_ACCESSORY_VENDOR_ID = "hid_accessory_vendor_id"
        private const val HID_ACCESSORY_PRODUCT_ID = "hid_accessory_product_id"
        private const val HID_ACCESSORY_SERIAL_NUMBER = "hid_accessory_serial_number"
        private val PROPERTY_FILES = arrayOf("accessory1.properties", "accessory2.properties")
    }
}