// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryservicesample.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.api.accessory.AbstractAccessoryService;
import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.ReportEventInfo;
import com.hp.workpath.api.accessory.hid.EventCode;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo;
import com.hp.workpath.sample.accessoryservicesample.Logger;
import com.hp.workpath.sample.accessoryservicesample.MainActivity;
import com.hp.workpath.sample.accessoryservicesample.R;
import com.hp.workpath.sample.accessoryservicesample.task.InitializationTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AccessoryService extends AbstractAccessoryService {

    private static final String TAG = MainActivity.TAG + "S";
    private int ACCESSORY_NOTIFICATION_ID = 2019;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    BlockingQueue<Boolean> initializedSDKQueue = new ArrayBlockingQueue<>(1);

    private AccessoryObserver mAccessoryObserver;
    private AccessoryStartObserver mAccessoryStartObserver;
    private String mAccessoryContextId;
    private long mOrdinal = 0;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mInitializationTask != null) {
            mInitializationTask.cancel();
            mInitializationTask = null;
        }

        unregisterObserver();
    }

    private void unregisterObserver() {
        if (mAccessoryObserver != null) {
            try {
                mAccessoryObserver.unregister(getApplicationContext());
                mAccessoryObserver = null;
            } catch (Throwable t) {
                Logger.showResult(null, "Unregister AccessoryObserver " + t.getMessage());
            }
            Log.d(TAG, "AccessoryObserver is unregistered");
        }

        if (mAccessoryStartObserver != null) {
            try {
                mAccessoryStartObserver.unregister(getApplicationContext());
                mAccessoryStartObserver = null;
            } catch (Throwable t) {
                Logger.showResult(null, "Unregister StartObserver " + t.getMessage());
            }
            Log.d(TAG, "AccessoryStartObserver is unregistered");
        }
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

        startForeground(ACCESSORY_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        unregisterObserver();
        initService(new Handler(), getApplicationContext());
        Log.i(TAG, "AccessoryObserver is registered");
        return START_STICKY;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");

        Intent intent = new Intent(getApplicationContext(), getClass());
        PendingIntent pi = PendingIntent.getForegroundService(getApplicationContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pi);
    }

    private void initService(Handler handler, Context context) {
        Log.d(TAG, "init");

        initializedSDK();
        mAccessoryObserver = new AccessoryObserver(handler);
        mAccessoryObserver.register(context);
        mAccessoryStartObserver = new AccessoryStartObserver(handler);
        mAccessoryStartObserver.register(context);
    }

    public void resendOwnedAccessory() {
        boolean isReady = com.hp.workpath.api.accessory.hid.AccessoryService.isReady(getApplicationContext());
        Log.i(TAG, "isReady: " + isReady);
        List<AccessoryInfo> accessoryInfoList = getOwnedAccessoryInfo();
        Log.i(TAG, "resendOwnedAccessory: requesting resend");

        if (isReady && accessoryInfoList != null) {
            Log.i(TAG, "OwnedAccessoryList size: " + accessoryInfoList.size());
            for (AccessoryInfo accessoryInfo : accessoryInfoList) {
                Result result = new Result();
                com.hp.workpath.api.accessory.hid.AccessoryService.resendOwnedAccessoryContext(
                        getApplicationContext(),
                        accessoryInfo,
                        result);
                Logger.showResult(null, "AccessoryService.resendOwnedAccessoryContext", result);
            }
        } else {
            Log.i(TAG, "resendOwnedAccessory: retry");
            resendOwnedAccessoryAfterDelay();
        }
    }

    private void resendOwnedAccessoryAfterDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resendOwnedAccessory();
            }
        }, 1000 * 60);
    }

    private void initializedSDK() {
        if (initializedSDKQueue.isEmpty()) {
            Log.i(TAG, "initializedSDK: ");
            mInitializationTask = new InitializationTask(this, initializeInterface);
            mInitializationTask.taskExecute();
            mInitializationTask.setBlockingQueue(initializedSDKQueue);
        }
    }

    InitializationTask.InitializeInterface initializeInterface = new InitializationTask.InitializeInterface() {
        @Override
        public void handleComplete(InitializationTask.InitStatus initStatus) {
            Log.i(TAG, "initializedSDK: handleComplete");
            if (initStatus != InitializationTask.InitStatus.NO_ERROR) {
                Log.e(TAG, getString(R.string.sdk_support_missing));
            }
            resendOwnedAccessory();
        }

        @Override
        public void handleException(Throwable t) {
            Log.e(TAG, "Workpath.initialize exception " + t.getMessage());
            stopSelf();
        }
    };

    private void openAccessory(String accessoryContextId) {
        Result result = new Result();
        Log.d(TAG, "Accessory opened: " + accessoryContextId);
        com.hp.workpath.api.accessory.hid.AccessoryService.open(AccessoryService.this, accessoryContextId, result);
        Logger.showResult(null, "AccessoryService.open", result);
        if (result.getCode() == Result.RESULT_OK) {
            startReadingAccessory(accessoryContextId);
        }
    }

    private void startReadingAccessory(String accessoryContextId) {
        Result result = new Result();
        Log.d(TAG, "Accessory startReading: " + accessoryContextId);
        com.hp.workpath.api.accessory.hid.AccessoryService.startReading(AccessoryService.this, accessoryContextId, result);
        Logger.showResult(null, "AccessoryService.startReading", result);
    }

    private void initiateSignIn() {
        Result result = new Result();
        Log.d(TAG, "initiateSignIn");
        AccessService.initiateSignIn(getApplicationContext(), result);
        Logger.showResult(null, "AccessService.initiateSignIn", result);
    }

    private void signOut() {
        Result result = new Result();
        Log.d(TAG, "signOut");
        AccessService.signOut(getApplicationContext(), result);
        Logger.showResult(null, "AccessService.signOut", result);
    }

    private Principal getCurrentPrincipal() {
        Result result = new Result();
        Log.d(TAG, "getCurrentPrincipal");
        Principal principal = AccessService.getCurrentPrincipal(getApplicationContext(), result);
        Logger.showResult(null, "AccessService.getCurrentPrincipal", result);
        if (result.getCode() == Result.RESULT_OK) {
            return principal;
        } else {
            return null;
        }
    }

    /**
     * Observer for accessory event.
     */
    private class AccessoryObserver extends com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryObserver {

        public AccessoryObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onContextChange(AccessoryInfo accessoryInfo, EventCode eventCode, String timeStamp, String accessoryContextId) {
            if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
                Log.i(TAG, "AccessoryObserver onContextChange() accessoryContextId: " + accessoryContextId);
                Log.i(TAG, new StringBuilder().append("eventCode=").append(eventCode)
                        .append(", timestamp=").append(timeStamp).append("\n")
                        .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString());

                if (!TextUtils.isEmpty(accessoryContextId)) {
                    if (!accessoryContextId.equals(mAccessoryContextId)) {
                        mAccessoryContextId = accessoryContextId;
                        openAccessory(accessoryContextId);
                    }
                }
            } else if (eventCode == EventCode.CONTEXT_REVOKED) {
                Log.i(TAG, "Accessory context is expired");
                mAccessoryContextId = null;
            }
        }

        @Override
        public void onReceive(AccessoryInfo accessoryInfo, ReportEventInfo reportEventInfo) {
            if (accessoryInfo != null &&
                    accessoryInfo.getDetails() != null &&
                    accessoryInfo.getDetails() instanceof HIDAccessoryInfo) {
                HIDAccessoryInfo hidAccessoryInfo = accessoryInfo.getDetails();
                HIDReportEventInfo hidReportEventInfo = reportEventInfo.getDetails();

                Log.i(TAG, "AccessoryObserver onReceive()");
                Log.i(TAG, "AccessoryInfo=" + Logger.build(hidAccessoryInfo));

                if (mOrdinal != hidReportEventInfo.getOrdinal()) {
                    mOrdinal = hidReportEventInfo.getOrdinal();

                    /** if reading accessory data is finished,
                     *  start sign in or sign out.
                     */
                    if (hidReportEventInfo.getReports() != null) {
                        boolean endOfAccessoryData = false;
                        List<HIDReport> hidReports = hidReportEventInfo.getReports();
                        for (HIDReport report : hidReports) {
                            byte[] data = report.getData();
                            Log.i(TAG, "AccessoryService: " + Arrays.toString(data));
                            if (data != null && data.length > 0) {
                                for (byte a : data) {
                                    if (a == 0x0d) { //CR
                                        endOfAccessoryData = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (endOfAccessoryData) {
                            Principal principal = getCurrentPrincipal();
                            if (principal != null && !principal.isAuthenticated()) {
                                initiateSignIn();
                            } else {
                                signOut();
                            }
                        }
                    }
                }
            }
        }
    }

    private class AccessoryStartObserver extends com.hp.workpath.api.accessory.hid.AccessoryService.AbstractAccessoryStartObserver {

        public AccessoryStartObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReady(AccessoryInfo accessoryInfo, EventCode eventCode, String timeStamp, String accessoryContextId) {
            String log = new StringBuilder()
                    .append("accessoryContextId=").append(accessoryContextId).append("\n")
                    .append(", eventCode=").append(eventCode).append("\n")
                    .append(", timestamp=").append(timeStamp).append("\n")
                    .append("AccessoryInfo=").append(Logger.build(accessoryInfo)).toString();
            Log.i(TAG, "AccessoryObserver onReady(): " + log);
        }
    }

    ;

    private static final String HID_ACCESSORY_VENDOR_ID = "hid_accessory_vendor_id";
    private static final String HID_ACCESSORY_PRODUCT_ID = "hid_accessory_product_id";
    private static final String HID_ACCESSORY_SERIAL_NUMBER = "hid_accessory_serial_number";
    private static final String[] PROPERTY_FILES = {"accessory1.properties", "accessory2.properties"};

    private List<AccessoryInfo> getOwnedAccessoryInfo() {
        Result result = new Result();
        List<AccessoryInfo> accessoryInfoList = new ArrayList<>();
        List<AccessoryInfo> ownedAccessoryInfoList = com.hp.workpath.api.accessory.hid.AccessoryService.getOwnedAccessories(getApplicationContext(), result);

        if (result == null || result.getCode() != Result.RESULT_OK) {
            return null;
        }

        InputStream is = null;
        for (String propertyFile : PROPERTY_FILES) {
            try {
                is = getAssets().open(propertyFile);
                Properties properties = new Properties();
                properties.load(is);
                String vendorId = properties.getProperty(HID_ACCESSORY_VENDOR_ID);
                String productId = properties.getProperty(HID_ACCESSORY_PRODUCT_ID);
                String serialNumber = properties.getProperty(HID_ACCESSORY_SERIAL_NUMBER, null);

                for (AccessoryInfo accessoryInfo : ownedAccessoryInfoList) {
                    if (accessoryInfo instanceof HIDAccessoryInfo) {
                        if (((HIDAccessoryInfo) accessoryInfo).getVendorId() == Integer.parseInt(vendorId)
                                && ((HIDAccessoryInfo) accessoryInfo).getProductId() == Integer.parseInt(productId)
                                && Objects.equals(((HIDAccessoryInfo) accessoryInfo).getSerialNumber(), serialNumber)) {
                            accessoryInfoList.add(accessoryInfo);
                        }
                    }
                }
            } catch (Throwable t) {
                // no file - no accessory information
                Log.e(TAG, "Failed to load property file " + propertyFile);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close asset file " + propertyFile);
                    }
                }
            }
        }
        return accessoryInfoList;
    }
}
