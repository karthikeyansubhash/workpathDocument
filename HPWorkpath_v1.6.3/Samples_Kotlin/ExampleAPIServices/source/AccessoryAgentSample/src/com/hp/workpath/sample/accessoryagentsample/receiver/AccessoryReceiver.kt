package com.hp.workpath.sample.accessoryagentsample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.access.Principal
import com.hp.workpath.api.accessory.RegistrationType
import com.hp.workpath.api.accessory.hid.AccessoryService
import com.hp.workpath.api.accessory.hid.EventCode
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo
import com.hp.workpath.api.accessory.hid.HIDInfo
import com.hp.workpath.api.accessory.hid.HIDReport
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo
import com.hp.workpath.sample.accessoryagentsample.Logger
import com.hp.workpath.sample.accessoryagentsample.MainActivity
import java.lang.ref.WeakReference

class AccessoryReceiver : BroadcastReceiver() {
    private var mContextRef: WeakReference<Context?>? = null
    private var appContext: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        appContext = context.applicationContext
        mContextRef = WeakReference(appContext)

        initService(Handler(), appContext)

        if (ACCESSORY_CONTEXT_CHANGE_ACTION == intent.action || ACCESSORY_CHANGE_ACTION == intent.action) {
            val action = intent.action

            when (action) {
                ACCESSORY_CONTEXT_CHANGE_ACTION -> onContextChange(intent)
                ACCESSORY_CHANGE_ACTION -> onChange(intent)
            }
        }
    }

    private fun initService(handler: Handler, context: Context?) {
        try {
            Log.i(TAG, "initService Workpath.getInstance().initialize")
            if (!isInitializedSDK) {
                context?.let { Workpath.getInstance().initialize(it) }
                isInitializedSDK = true
            }
        } catch (e: SsdkUnsupportedException) {
            Logger.showResult(null, e.message)
        }
    }

    private fun openAccessory(accessoryContextId: String) {
        Log.d(TAG, "Accessory opened: $accessoryContextId")
        val result = Result()
        try {
            appContext?.let { AccessoryService.open(it, accessoryContextId, result) }
            Logger.showResult(null, "AccessoryService.open", result)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

        try {
            val hidInfo = appContext?.let { AccessoryService.getInfo(it, accessoryContextId, result) }
            if (result.code == Result.RESULT_OK && hidInfo != null) {
                Log.d(TAG, "HID feature report length : ${hidInfo.featureReportLength}")
                Log.d(TAG, "HID input report length : ${hidInfo.inputReportLength}")
                Log.d(TAG, "HID output report length : ${hidInfo.outputReportLength}")
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

        try {
            appContext?.let { AccessoryService.startReading(it, accessoryContextId, result) }
            Logger.showResult(null, "AccessoryService.startReading", result)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun initiateSignIn() {
        val result = Result()
        Log.d(TAG, "initiateSignIn")
        appContext?.let { AccessService.initiateSignIn(it, result) }
        Logger.showResult(null, "AccessService.initiateSignIn", result)
    }

    private fun signOut() {
        val result = Result()
        Log.d(TAG, "signOut")
        appContext?.let { AccessService.signOut(it, result) }
        Logger.showResult(null, "AccessService.signOut", result)
    }

    private fun getCurrentPrincipal(): Principal? {
        val result = Result()
        Log.d(TAG, "getCurrentPrincipal")
        val principal = appContext?.let { AccessService.getCurrentPrincipal(it, result) }
        Logger.showResult(null, "AccessService.getCurrentPrincipal", result)
        return if (result.code == Result.RESULT_OK) {
            principal
        } else {
            null
        }
    }

    private fun onContextChange(intent: Intent) {
        val hidAccessoryInfo = intent.getParcelableExtra<HIDAccessoryInfo>("accessoryInfo")
        val vendorId = hidAccessoryInfo?.vendorId ?: -1
        val productId = hidAccessoryInfo?.productId ?: -1
        val serialNumber = hidAccessoryInfo?.serialNumber
        val registrationType = hidAccessoryInfo?.registrationType
        val timeStamp = intent.getStringExtra("timestamp")
        val eventCode = intent.getSerializableExtra("accessoryContextEventCode") as? EventCode
        val accessoryContextId = intent.getStringExtra("accessoryContextId")

        Log.i(TAG, "vendorId : $vendorId")
        Log.i(TAG, "productId : $productId")
        Log.i(TAG, "serialNumber : $serialNumber")
        Log.i(TAG, "RegistrationType : $registrationType")
        Log.i(TAG, "eventCode : $eventCode")
        Log.i(TAG, "timeStamp : $timeStamp")
        Log.i(TAG, "accessoryContextId : $accessoryContextId")

        if (eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT) {
            if (!isInitializedSDK) {
                Log.d(TAG, "The SDK has not been initialized yet.")
                return
            }
            if (!TextUtils.isEmpty(accessoryContextId)) {
                accessoryContextId?.let { openAccessory(it) }
            }
        } else if (eventCode == EventCode.CONTEXT_REVOKED) {
            Log.i(TAG, "Accessory context is revoked.")
        }
    }

    private fun onChange(intent: Intent) {
        Log.i(TAG, "ACCESSORY_CHANGE_ACTION")

        val hidAccessoryInfo = intent.getParcelableExtra<HIDAccessoryInfo>("accessoryInfo")
        val vendorId = hidAccessoryInfo?.vendorId ?: -1
        val productId = hidAccessoryInfo?.productId ?: -1
        val serialNumber = hidAccessoryInfo?.serialNumber

        val hidReportEventInfo = intent.getParcelableExtra<HIDReportEventInfo>("hidReportEventInfo")
        val reportOrdinal = hidReportEventInfo?.ordinal ?: -1
        val timeStamp = hidReportEventInfo?.timestamp
        val reports = hidReportEventInfo?.reports ?: emptyList()

        Log.i(TAG, "vendorId : $vendorId")
        Log.i(TAG, "productId : $productId")
        Log.i(TAG, "serialNumber : $serialNumber")
        Log.i(TAG, "ordinal : $reportOrdinal")
        Log.i(TAG, "timeStamp : $timeStamp")
        Log.i(TAG, "reportCount : ${reports.size}")

        for (i in reports.indices) {
            val hidReport = reports[i]
            val data = hidReport.data
            Log.i(TAG, "report_${i + 1} : ${String(Base64.encode(data, Base64.DEFAULT))}")
            if (isHPCardReader(vendorId, productId) && String(data).contains("\r")) {
                val principal = getCurrentPrincipal()
                if (principal != null && !principal.isAuthenticated) {
                    initiateSignIn()
                } else {
                    signOut()
                }
            }
        }
    }

    private fun isHPCardReader(vendorId: Int, productId: Int): Boolean {
        return vendorId == 1008 && productId == 69
    }

    companion object {
        const val TAG = MainActivity.TAG + "Receiver"
        private const val ACCESSORY_CONTEXT_CHANGE_ACTION = "com.hp.workpath.api.accessory.ACCESSORY_CONTEXT_CHANGE_ACTION"
        private const val ACCESSORY_CHANGE_ACTION = "com.hp.workpath.api.accessory.ACCESSORY_CHANGE_ACTION"
        private var isInitializedSDK = false
    }
}