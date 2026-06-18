// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample.model

import com.hp.workpath.api.device.DeviceAttribute
import com.hp.workpath.api.device.DeviceAttributeBase
import com.hp.workpath.sample.deviceinfosample.R

/**
 * Simple enum to have all display items information
 *
 *
 * DisplayItem.MODEL,
 * DisplayItem.HOSTNAME,
 * DisplayItem.NETWORK_ADDRESS,
 * DisplayItem.FIRMWARE_VERSION,
 * DisplayItem.DEVICE_ID,
 * DisplayItem.SERIAL_NUMBER,
 * DisplayItem.SYSTEM_LANGUAGE,
 * DisplayItem.SYSTEM_LANGUAGE_CAPABILITY
 */
enum class DeviceInfo
/**
 * @param itemId    in layout
 * @param titleId   in child layout
 * @param attribute to be used with this item, may be null if multiple attributes are composed or
 * items is not about attributes
 */
constructor(val itemId: Int, val titleId: Int, val attribute: DeviceAttributeBase) {
    // Device information
    MODEL(R.id.childModelName, R.string.model_name, DeviceAttribute.DA_SYSTEM_MODELNAME),
    HOSTNAME(R.id.childHostName, R.string.host_name, DeviceAttribute.DA_NETWORK_HOSTNAME),
    NETWORK_ADDRESS(R.id.childNetworkAddress, R.string.network_address, DeviceAttribute.DA_NETWORK_HOSTNAME),
    FIRMWARE_VERSION(R.id.childFirmwareVersion, R.string.firmware_version, DeviceAttribute.DA_SYSTEM_FIRMWARE_VERSION),
    DEVICE_ID(R.id.childDeviceId, R.string.device_id, DeviceAttribute.DA_SYSTEM_DEVICE_ID),
    SERIAL_NUMBER(R.id.childSerialNumber, R.string.serial_number, DeviceAttribute.DA_SYSTEM_SERIALNUMBER),
    SYSTEM_LANGUAGE(R.id.childLanguageSet, R.string.system_language, DeviceAttribute.DA_SYSTEM_LANGUAGE),
    SYSTEM_LANGUAGE_CAPABILITY(R.id.childLanguageCapability, R.string.system_language_capability, DeviceAttribute.DA_SYSTEM_LANGUAGE_CAPABILITY),
    ASSET_NUMBER(R.id.childAssetNumber, R.string.asset_number, DeviceAttribute.DA_ASSET_NUMBER),
    COMPANY_CONTACT(R.id.childCompanyContact, R.string.company_contact, DeviceAttribute.DA_COMPANY_CONTACT),
    COMPANY_NAME(R.id.childCompanyName, R.string.company_name, DeviceAttribute.DA_COMPANY_NAME),
    DEVICE_LOCATION(R.id.childDeviceLocation, R.string.device_location, DeviceAttribute.DA_DEVICE_LOCATION),
    MACHINE_NAME(R.id.childMachineName, R.string.machine_name, DeviceAttribute.DA_MACHINE_NAME),
    HP_FUTURE_SMART_LEVEL(R.id.childFutureSmartLevel, R.string.future_smart_level, DeviceAttribute.DA_SYSTEM_HP_FUTURE_SMART_LEVEL)
}

