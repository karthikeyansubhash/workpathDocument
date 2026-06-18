// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceinfosample.task;

import static android.app.Activity.RESULT_OK;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.DeviceAttribute;
import com.hp.workpath.api.device.DeviceService;
import com.hp.workpath.sample.deviceinfosample.Logger;
import com.hp.workpath.sample.deviceinfosample.MainActivity;
import com.hp.workpath.sample.deviceinfosample.R;
import com.hp.workpath.sample.deviceinfosample.model.DeviceInfo;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceAttrReaderTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public DeviceAttrReaderTask(final MainActivity context) {
        mContextRef = new WeakReference<>(context);
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Resources res = mContextRef.get().getResources();
                        Map<DeviceInfo, String> values = new HashMap<>();

                        String VALUE_NA = res.getString(R.string.na);

                        if (sItemsToRefresh.contains(DeviceInfo.HOSTNAME)) {
                            Result result = new Result();
                            String hostName = DeviceService.getString(mContextRef.get(), DeviceInfo.HOSTNAME.getAttribute(), result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_NETWORK_HOSTNAME", result);
                                hostName = VALUE_NA;
                            }

                            values.put(DeviceInfo.HOSTNAME, hostName);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.MODEL)) {
                            Result vendorResult = new Result();
                            String vendor = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_DEVICE_VENDOR, vendorResult);

                            if (vendorResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_DEVICE_VENDOR", vendorResult);
                                vendor = VALUE_NA;
                            }

                            Result modelNameResult = new Result();
                            String modelName = DeviceService.getString(mContextRef.get(), DeviceInfo.MODEL.getAttribute(), modelNameResult);

                            if (modelNameResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_MODELNAME", modelNameResult);
                                modelName = VALUE_NA;
                            }

                            String value = res.getString(R.string.model_value, modelName, vendor);

                            values.put(DeviceInfo.MODEL, value);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.FIRMWARE_VERSION)) {
                            Result result = new Result();
                            String firmwareVersion = DeviceService.getString(mContextRef.get(), DeviceInfo.FIRMWARE_VERSION.getAttribute(), result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_FIRMWARE_VERSION", result);
                                firmwareVersion = VALUE_NA;
                            }

                            values.put(DeviceInfo.FIRMWARE_VERSION, firmwareVersion);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.DEVICE_ID)) {
                            Result deviceIdResult = new Result();
                            String deviceId = DeviceService.getString(mContextRef.get(), DeviceInfo.DEVICE_ID.getAttribute(), deviceIdResult);

                            if (deviceIdResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_DEVICE_ID", deviceIdResult);
                                deviceId = VALUE_NA;
                            }

                            Result productNumberResult = new Result();
                            String productNumber = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_SYSTEM_PRODUCT_NUMBER, productNumberResult);

                            if (productNumberResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_PRODUCT_NUMBER", productNumberResult);
                                productNumber = VALUE_NA;
                            }

                            String value = res.getString(R.string.device_id_value, deviceId, productNumber);
                            values.put(DeviceInfo.DEVICE_ID, value);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.SERIAL_NUMBER)) {
                            Result serialNumberResult = new Result();
                            String serialNumber = DeviceService.getString(mContextRef.get(), DeviceInfo.SERIAL_NUMBER.getAttribute(), serialNumberResult);

                            if (serialNumberResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_SERIALNUMBER", serialNumberResult);
                                serialNumber = VALUE_NA;
                            }

                            Result formatterSNResult = new Result();
                            String formatterSerialNumber = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_SYSTEM_FORMATTER_SERIAL_NUMBER, formatterSNResult);

                            if (formatterSNResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_FORMATTER_SERIAL_NUMBER", formatterSNResult);
                                formatterSerialNumber = VALUE_NA;
                            }

                            String value = res.getString(R.string.serial_number_value, serialNumber, formatterSerialNumber);
                            values.put(DeviceInfo.SERIAL_NUMBER, value);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.SYSTEM_LANGUAGE)) {
                            Result result = new Result();
                            String systemLanguage = DeviceService.getString(mContextRef.get(), DeviceInfo.SYSTEM_LANGUAGE.getAttribute(), result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_LANGUAGE", result);
                                systemLanguage = VALUE_NA;
                            }

                            values.put(DeviceInfo.SYSTEM_LANGUAGE, systemLanguage);
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.SYSTEM_LANGUAGE_CAPABILITY)) {
                            Result result = new Result();
                            String[] systemLanguageCapability = DeviceService.getStringArray(mContextRef.get(), DeviceAttribute.DA_SYSTEM_LANGUAGE_CAPABILITY, result);
                            StringBuilder systemLanguages = new StringBuilder(VALUE_NA);
                            if (result.getCode() != RESULT_OK || systemLanguageCapability == null) {
                                publishProgress("DeviceService.getStringArray DA_FEATURE_SYSTEM_LANGUAGE_CAPABILITY", result);
                            } else {
                                systemLanguages = new StringBuilder();
                                for (String language : systemLanguageCapability) {
                                    systemLanguages.append(language).append(",");
                                }
                            }
                            values.put(DeviceInfo.SYSTEM_LANGUAGE_CAPABILITY, systemLanguages.toString());
                        }

                        if (sItemsToRefresh.contains(DeviceInfo.NETWORK_ADDRESS)) {
                            Result macAddressResult = new Result();
                            String macAddress = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_NETWORK_MACADDRESS, macAddressResult);

                            if (macAddressResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_NETWORK_MACADDRESS", macAddressResult);
                                macAddress = VALUE_NA;
                            }

                            Result ipAddressResult = new Result();
                            String ipAddress = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_NETWORK_IPADDRESS, ipAddressResult);

                            if (ipAddressResult.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_NETWORK_IPADDRESS", ipAddressResult);
                                ipAddress = VALUE_NA;
                            }

                            String value = res.getString(R.string.address_value, ipAddress, macAddress);

                            values.put(DeviceInfo.NETWORK_ADDRESS, value);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.ASSET_NUMBER)) {
                            Result result = new Result();
                            String assetNumber = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_ASSET_NUMBER, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_ASSET_NUMBER", result);
                                assetNumber = VALUE_NA;
                            }

                            values.put(DeviceInfo.ASSET_NUMBER, assetNumber);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.COMPANY_CONTACT)) {
                            Result result = new Result();
                            String companyContact = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_COMPANY_CONTACT, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_COMPANY_CONTACT", result);
                                companyContact = VALUE_NA;
                            }

                            values.put(DeviceInfo.COMPANY_CONTACT, companyContact);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.COMPANY_NAME)) {
                            Result result = new Result();
                            String companyName = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_COMPANY_NAME, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_COMPANY_NAME", result);
                                companyName = VALUE_NA;
                            }

                            values.put(DeviceInfo.COMPANY_NAME, companyName);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.DEVICE_LOCATION)) {
                            Result result = new Result();
                            String deviceLocation = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_DEVICE_LOCATION, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_DEVICE_LOCATION", result);
                                deviceLocation = VALUE_NA;
                            }

                            values.put(DeviceInfo.DEVICE_LOCATION, deviceLocation);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.MACHINE_NAME)) {
                            Result result = new Result();
                            String machineName = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_MACHINE_NAME, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_MACHINE_NAME", result);
                                machineName = VALUE_NA;
                            }

                            values.put(DeviceInfo.MACHINE_NAME, machineName);
                        }

                        if(sItemsToRefresh.contains(DeviceInfo.HP_FUTURE_SMART_LEVEL)) {
                            Result result = new Result();
                            String futureSmartLevel = DeviceService.getString(mContextRef.get(), DeviceAttribute.DA_SYSTEM_HP_FUTURE_SMART_LEVEL, result);

                            if (result.getCode() != RESULT_OK) {
                                publishProgress("DeviceService.getString DA_SYSTEM_HP_FUTURE_SMART_LEVEL", result);
                                futureSmartLevel = VALUE_NA;
                            }

                            values.put(DeviceInfo.HP_FUTURE_SMART_LEVEL, futureSmartLevel);
                        }

                        onPostExecute(values);
                    } catch (Throwable t) {
                        Logger.showResult(null, "DeviceService.getString " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }

                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "DeviceService.getString " + e.getMessage());
            executor.shutdown();
            onPostExecute(null);
        }
    }

    /**
     * Enum to have only items we need to refresh, for faster display
     */
    private static final EnumSet<DeviceInfo> sItemsToRefresh = EnumSet.of(
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
    );

    private void publishProgress(Object... values) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // in case of error case
                String msg = (String) values[0];
                Result result = (Result) values[1];
                Log.i(TAG, msg + " " + Logger.build(result));
            }
        });
    }

    private void onPostExecute(Map<DeviceInfo, String> result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContextRef.get().showProgress(View.GONE);
                if (result != null) {
                    Logger.showResult(mContextRef.get(), "DeviceService.getString" + result.toString());
                    mContextRef.get().handleUpdate(result);
                } else {
                    mContextRef.get().handleException(new Throwable(mContextRef.get().getString(R.string.service_failed)));
                }
            }
        });
    }
}

