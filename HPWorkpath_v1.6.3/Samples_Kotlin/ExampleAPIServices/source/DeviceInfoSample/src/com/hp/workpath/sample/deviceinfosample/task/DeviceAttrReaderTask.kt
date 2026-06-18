// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample.task

import android.app.Activity
import android.util.Log
import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.device.DeviceAttribute
import com.hp.workpath.api.device.DeviceService
import com.hp.workpath.sample.deviceinfosample.Logger
import com.hp.workpath.sample.deviceinfosample.Logger.build
import com.hp.workpath.sample.deviceinfosample.MainActivity
import com.hp.workpath.sample.deviceinfosample.R
import com.hp.workpath.sample.deviceinfosample.model.DeviceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.EnumSet
import kotlin.collections.HashMap
import kotlin.collections.set

class DeviceAttrReaderTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null

    suspend fun execute() {
        val values: MutableMap<DeviceInfo, String> = HashMap()
        try {
            mContextRef.get()?.run {
                val res = this.resources
                val VALUE_NA = res.getString(R.string.na)

                if (sItemsToRefresh.contains(DeviceInfo.HOSTNAME)) {
                    val result = Result()
                    var hostName = DeviceService.getString(this, DeviceInfo.HOSTNAME.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_NETWORK_HOSTNAME", result)
                        hostName = VALUE_NA
                    }
                    values[DeviceInfo.HOSTNAME] = hostName
                }
                if (sItemsToRefresh.contains(DeviceInfo.MODEL)) {
                    val vendorResult = Result()
                    var vendor = DeviceService.getString(this, DeviceAttribute.DA_DEVICE_VENDOR, vendorResult)
                    if (vendorResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_DEVICE_VENDOR", vendorResult)
                        vendor = VALUE_NA
                    }
                    val modelNameResult = Result()
                    var modelName = DeviceService.getString(this, DeviceInfo.MODEL.attribute, modelNameResult)
                    if (modelNameResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_MODELNAME", modelNameResult)
                        modelName = VALUE_NA
                    }
                    val value = res.getString(R.string.model_value, modelName, vendor)
                    values[DeviceInfo.MODEL] = value
                }
                if (sItemsToRefresh.contains(DeviceInfo.FIRMWARE_VERSION)) {
                    val result = Result()
                    var firmwareVersion = DeviceService.getString(this, DeviceInfo.FIRMWARE_VERSION.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_FIRMWARE_VERSION", result)
                        firmwareVersion = VALUE_NA
                    }
                    values[DeviceInfo.FIRMWARE_VERSION] = firmwareVersion
                }
                if (sItemsToRefresh.contains(DeviceInfo.DEVICE_ID)) {
                    val deviceIdResult = Result()
                    var deviceId = DeviceService.getString(this, DeviceInfo.DEVICE_ID.attribute, deviceIdResult)
                    if (deviceIdResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_DEVICE_ID", deviceIdResult)
                        deviceId = VALUE_NA
                    }
                    val productNumberResult = Result()
                    var productNumber = DeviceService.getString(this, DeviceAttribute.DA_SYSTEM_PRODUCT_NUMBER, productNumberResult)
                    if (productNumberResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_PRODUCT_NUMBER", productNumberResult)
                        productNumber = VALUE_NA
                    }
                    val value = res.getString(R.string.device_id_value, deviceId, productNumber)
                    values[DeviceInfo.DEVICE_ID] = value
                }
                if (sItemsToRefresh.contains(DeviceInfo.SERIAL_NUMBER)) {
                    val serialNumberResult = Result()
                    var serialNumber = DeviceService.getString(this, DeviceInfo.SERIAL_NUMBER.attribute, serialNumberResult)
                    if (serialNumberResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_SERIALNUMBER", serialNumberResult)
                        serialNumber = VALUE_NA
                    }
                    val formatterSNResult = Result()
                    var formatterSerialNumber = DeviceService.getString(this, DeviceAttribute.DA_SYSTEM_FORMATTER_SERIAL_NUMBER, formatterSNResult)
                    if (formatterSNResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_FORMATTER_SERIAL_NUMBER", formatterSNResult)
                        formatterSerialNumber = VALUE_NA
                    }
                    val value = res.getString(R.string.serial_number_value, serialNumber, formatterSerialNumber)
                    values[DeviceInfo.SERIAL_NUMBER] = value
                }
                if (sItemsToRefresh.contains(DeviceInfo.SYSTEM_LANGUAGE)) {
                    val result = Result()
                    var systemLanguage = DeviceService.getString(this, DeviceInfo.SYSTEM_LANGUAGE.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_LANGUAGE", result)
                        systemLanguage = VALUE_NA
                    }
                    values[DeviceInfo.SYSTEM_LANGUAGE] = systemLanguage
                }
                if (sItemsToRefresh.contains(DeviceInfo.SYSTEM_LANGUAGE_CAPABILITY)) {
                    val result = Result()
                    val systemLanguageCapability = DeviceService.getStringArray(this, DeviceAttribute.DA_SYSTEM_LANGUAGE_CAPABILITY, result)
                    var systemLanguages = StringBuilder(VALUE_NA)
                    if (result.code != Activity.RESULT_OK || systemLanguageCapability == null) {
                        onProgressUpdate("DeviceService.getStringArray DA_FEATURE_SYSTEM_LANGUAGE_CAPABILITY", result)
                    } else {
                        systemLanguages = StringBuilder()
                        for (language in systemLanguageCapability) {
                            systemLanguages.append(language).append(",")
                        }
                    }
                    values[DeviceInfo.SYSTEM_LANGUAGE_CAPABILITY] = systemLanguages.toString()
                }
                if (sItemsToRefresh.contains(DeviceInfo.NETWORK_ADDRESS)) {
                    val macAddressResult = Result()
                    var macAddress = DeviceService.getString(this, DeviceAttribute.DA_NETWORK_MACADDRESS, macAddressResult)
                    if (macAddressResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_NETWORK_MACADDRESS", macAddressResult)
                        macAddress = VALUE_NA
                    }
                    val ipAddressResult = Result()
                    var ipAddress = DeviceService.getString(this, DeviceAttribute.DA_NETWORK_IPADDRESS, ipAddressResult)
                    if (ipAddressResult.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_NETWORK_IPADDRESS", ipAddressResult)
                        ipAddress = VALUE_NA
                    }

                    val value = res.getString(R.string.address_value, ipAddress, macAddress)
                    values[DeviceInfo.NETWORK_ADDRESS] = value
                }
                if (sItemsToRefresh.contains(DeviceInfo.ASSET_NUMBER)) {
                    val result = Result()
                    var assetNumber = DeviceService.getString(this, DeviceInfo.ASSET_NUMBER.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_ASSET_NUMBER", result)
                        assetNumber = VALUE_NA
                    }
                    values[DeviceInfo.ASSET_NUMBER] = assetNumber
                }
                if (sItemsToRefresh.contains(DeviceInfo.COMPANY_CONTACT)) {
                    val result = Result()
                    var companyContact = DeviceService.getString(this, DeviceInfo.COMPANY_CONTACT.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_COMPANY_CONTACT", result)
                        companyContact = VALUE_NA
                    }
                    values[DeviceInfo.COMPANY_CONTACT] = companyContact
                }
                if (sItemsToRefresh.contains(DeviceInfo.COMPANY_NAME)) {
                    val result = Result()
                    var companyName = DeviceService.getString(this, DeviceInfo.COMPANY_NAME.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_COMPANY_NAME", result)
                        companyName = VALUE_NA
                    }
                    values[DeviceInfo.COMPANY_NAME] = companyName

                }
                if (sItemsToRefresh.contains(DeviceInfo.DEVICE_LOCATION)) {
                    val result = Result()
                    var deviceLocation = DeviceService.getString(this, DeviceInfo.DEVICE_LOCATION.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_DEVICE_LOCATION", result)
                        deviceLocation = VALUE_NA
                    }
                    values[DeviceInfo.DEVICE_LOCATION] = deviceLocation
                }
                if (sItemsToRefresh.contains(DeviceInfo.MACHINE_NAME)) {
                    val result = Result()
                    var machineName = DeviceService.getString(this, DeviceInfo.MACHINE_NAME.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_MACHINE_NAME", result)
                        machineName = VALUE_NA
                    }
                    values[DeviceInfo.MACHINE_NAME] = machineName
                }
                if (sItemsToRefresh.contains(DeviceInfo.HP_FUTURE_SMART_LEVEL)) {
                    val result = Result()
                    var futureSmartLevel = DeviceService.getString(this, DeviceInfo.HP_FUTURE_SMART_LEVEL.attribute, result)
                    if (result.code != Activity.RESULT_OK) {
                        onProgressUpdate("DeviceService.getString DA_SYSTEM_HP_FUTURE_SMART_LEVEL", result)
                        futureSmartLevel = VALUE_NA
                    }
                    values[DeviceInfo.HP_FUTURE_SMART_LEVEL] = futureSmartLevel
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(values);
    }

    private suspend fun onProgressUpdate(vararg values: Any) {
        // in case of error case
        val msg = values[0] as String
        val result = values[1] as Result
        Log.i(MainActivity.TAG, msg + " " + build(result))
    }

    private suspend fun onPostExecute(result: Map<DeviceInfo, String>?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgress(View.GONE)
                if (result != null) {
                    Logger.showResult(this, "DeviceService.getString $result")
                    handleUpdate(result)
                } else if (mThrowable != null) {
                    Logger.showResult(this, getString(R.string.service_failed) + " " + mThrowable?.message)
                } else {
                    handleException(Throwable(getString(R.string.service_failed)))
                }
            }
        }
    }


    companion object {
        /**
         * Enum to have only items we need to refresh, for faster display
         */
        private val sItemsToRefresh = EnumSet.of(
            DeviceInfo.MODEL,
            DeviceInfo.HOSTNAME,
            DeviceInfo.NETWORK_ADDRESS,
            DeviceInfo.FIRMWARE_VERSION,
            DeviceInfo.DEVICE_ID,
            DeviceInfo.SERIAL_NUMBER,
            DeviceInfo.SYSTEM_LANGUAGE,
            DeviceInfo.SYSTEM_LANGUAGE_CAPABILITY,
            DeviceInfo.ASSET_NUMBER,
            DeviceInfo.COMPANY_CONTACT,
            DeviceInfo.COMPANY_NAME,
            DeviceInfo.DEVICE_LOCATION,
            DeviceInfo.MACHINE_NAME,
            DeviceInfo.HP_FUTURE_SMART_LEVEL
        )
    }
}