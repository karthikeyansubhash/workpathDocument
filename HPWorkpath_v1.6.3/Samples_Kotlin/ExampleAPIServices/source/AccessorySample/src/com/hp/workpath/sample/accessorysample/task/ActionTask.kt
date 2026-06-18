// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.task

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hp.workpath.api.Result
import com.hp.workpath.api.accessory.AccessoryInfo
import com.hp.workpath.api.accessory.hid.AccessoryService
import com.hp.workpath.api.accessory.hid.HIDInfo
import com.hp.workpath.api.accessory.hid.HIDReport
import com.hp.workpath.api.accessory.hid.HIDReportType
import com.hp.workpath.sample.accessorysample.Action
import com.hp.workpath.sample.accessorysample.Logger
import com.hp.workpath.sample.accessorysample.MainActivity
import com.hp.workpath.sample.accessorysample.fragment.AccessoryReportsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class ActionTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val result: Result = Result()
    private var mThrowable: Throwable? = null
    private var mErrorMsg: String? = null

    private lateinit var action: Action
    private var accessoryContextId: String? = null
    private var accessoryInfo: AccessoryInfo? = null
    private var accessories: List<AccessoryInfo>? = null
    private lateinit var hidInfo: HIDInfo
    private lateinit var hidReport: HIDReport

    suspend fun execute(vararg voids: Any?) {
        var message = ""
        try {
            mContextRef.get()?.run {
                action = voids[0] as Action
                when (action) {
                    Action.GET_OWNED -> {
                        accessories = AccessoryService.getOwnedAccessories(this, result)
                        message = "owned accessories"
                    }
                    Action.ENUMERATE -> {
                        accessories = AccessoryService.enumerateAccessories(this, result)
                        message = "accessories enumerated"
                    }
                    Action.RESEND_OWNED -> {
                        accessoryInfo = voids[1] as AccessoryInfo?
                        accessoryInfo?.let {
                            AccessoryService.resendOwnedAccessoryContext(this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.resendOwnedAccessoryContext: accessoryContextId is null"
                        }
                        message = "resent"
                    }
                    Action.RESERVE_SHARED -> {
                        accessoryInfo = voids[1] as AccessoryInfo?
                        accessoryInfo?.let {
                            accessoryContextId = AccessoryService.reserveSharedAccessory(
                                    this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.reserveSharedAccessory: accessoryContextId is null"
                        }
                        message = "reserved: $accessoryContextId"
                    }
                    Action.RELEASE_SHARED -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        accessoryContextId?.let {
                            AccessoryService.releaseSharedAccessory(this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.releaseSharedAccessory: accessoryContextId is null"
                        }
                        message = "released"
                    }
                    Action.OPEN -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        accessoryContextId?.let {
                            AccessoryService.open(this, it, result)
                            if (result.code == Result.RESULT_OK) {
                                hidInfo = AccessoryService.getInfo(this, it, result)
                            }
                        } ?: run {
                            mErrorMsg = "AccessoryService.open: accessoryContextId is null"
                        }
                        message = "opened"
                    }
                    Action.CLOSE -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        accessoryContextId?.let {
                            AccessoryService.close(this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.close: accessoryContextId is null"
                        }
                        message = "closed"
                    }
                    Action.START_READ -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        accessoryContextId?.let {
                            AccessoryService.startReading(this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.startReading: accessoryContextId is null"
                        }
                        message = "started reading"
                    }
                    Action.STOP_READ -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        accessoryContextId?.let {
                            AccessoryService.stopReading(this, it, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.stopReading: accessoryContextId is null"
                        }
                        message = "stopped reading"
                    }
                    Action.READ_REPORT -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        val reportType = HIDReportType
                                .valueOf(mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_TYPE, HIDReportType.INPUT.name)
                                        ?: HIDReportType.INPUT.name)
                        accessoryContextId?.let {
                            hidReport = AccessoryService.readReport(this, it, reportType, result)
                            message = "report read: $hidReport"
                        } ?: run {
                            mErrorMsg = "AccessoryService.readReport: accessoryContextId is null"
                        }
                    }
                    Action.WRITE_REPORT -> {
                        accessoryContextId = if (voids.size > 1) voids[1] as String? else null
                        val reportType = HIDReportType
                                .valueOf(mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_TYPE, HIDReportType.INPUT.name)!!)
                        val reportDataHex = mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_DATA, "")
                        val data = fromHexString(reportDataHex ?: "")
                        if (data.isNotEmpty()) {
                            hidReport = HIDReport(reportType, data)
                        } else {
                            mErrorMsg = "Invalid report data"
                        }
                        accessoryContextId?.let {
                            AccessoryService.writeReport(this, it, hidReport, result)
                        } ?: run {
                            mErrorMsg = "AccessoryService.writeReport: accessoryContextId is null"
                        }
                        message = "report written"
                    }
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(message)
    }

    private suspend fun onPostExecute(response: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                Logger.showResult(this, response, result)
                if (result.code == Result.RESULT_OK) {
                    // Fill device description with received info
                    if (action === Action.RESERVE_SHARED || action === Action.RELEASE_SHARED) {
                        setAccessoryContextId(accessoryInfo, accessoryContextId)
                    } else if (action === Action.GET_OWNED || action === Action.ENUMERATE) {
                        loadAccessories(action, accessories)
                    } else if (action === Action.OPEN) {
                        setInfo(hidInfo)
                    } else if (action === Action.WRITE_REPORT || action === Action.READ_REPORT) {
                        setAccessoryData(Logger.build(hidReport))
                    }
                } else if (mThrowable != null) {
                    Logger.showResult(this, "$response ${mThrowable?.message}")
                } else if (mErrorMsg != null) {
                    Logger.showResult(this, "$response $mErrorMsg")
                }
            }
        }
    }

    companion object {
        private fun fromHexString(encoded: String): ByteArray {
            require(encoded.length % 2 == 0) { "Input string must contain an even number of characters" }
            val result = ByteArray(encoded.length / 2)
            val enc = encoded.toCharArray()
            var i = 0
            while (i < enc.size) {
                val curr = StringBuilder(2)
                curr.append(enc[i]).append(enc[i + 1])
                result[i / 2] = curr.toString().toInt(16).toByte()
                i += 2
            }
            return result
        }
    }
}