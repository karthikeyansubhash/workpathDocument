// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessorysample.task;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.preference.PreferenceManager;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.Result.ErrorCode;
import com.hp.workpath.api.accessory.AccessoryInfo;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.api.accessory.hid.HIDInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;
import com.hp.workpath.api.accessory.hid.HIDReportType;
import com.hp.workpath.sample.accessorysample.Action;
import com.hp.workpath.sample.accessorysample.Logger;
import com.hp.workpath.sample.accessorysample.MainActivity;
import com.hp.workpath.sample.accessorysample.fragment.AccessoryReportsFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActionTask {

    private final WeakReference<MainActivity> mContextRef;
    private final SharedPreferences mPrefs;
    private Result result;
    private Action action;
    private String accessoryContextId;
    private AccessoryInfo accessoryInfo;
    private List<AccessoryInfo> accessories;
    private HIDInfo hidInfo;
    private HIDReport hidReport;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ActionTask(final MainActivity activity) {
        mContextRef = new WeakReference<>(activity);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        result = new Result();
    }

    public void taskExecute(Object... voids) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    try {
                        action = (Action) voids[0];
                        switch (action) {
                            case GET_OWNED:
                                accessories = AccessoryService.getOwnedAccessories(mContextRef.get(), result);
                                message = "AccessoryService.getOwnedAccessories";
                                break;

                            case ENUMERATE:
                                accessories = AccessoryService.enumerateAccessories(mContextRef.get(), result);
                                message = "AccessoryService.enumerateAccessories";
                                break;

                            case RESEND_OWNED: {
                                accessoryInfo = (AccessoryInfo) voids[1];
                                AccessoryService.resendOwnedAccessoryContext(
                                        mContextRef.get(),
                                        accessoryInfo,
                                        result);

                                message = "AccessoryService.resendOwnedAccessoryContext";
                                break;
                            }

                            case RESERVE_SHARED: {
                                accessoryInfo = (AccessoryInfo) voids[1];
                                accessoryContextId = AccessoryService.reserveSharedAccessory(
                                        mContextRef.get(),
                                        accessoryInfo,
                                        result);

                                message = "AccessoryService.reserveSharedAccessory: " + accessoryContextId;
                                break;
                            }

                            case RELEASE_SHARED:
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                AccessoryService.releaseSharedAccessory(mContextRef.get(), accessoryContextId, result);
                                message = "AccessoryService.releaseSharedAccessory";
                                break;

                            case OPEN:
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                AccessoryService.open(mContextRef.get(), accessoryContextId, result);
                                message = "AccessoryService.open";

                                if (result.getCode() == Result.RESULT_OK) {
                                    hidInfo = AccessoryService.getInfo(mContextRef.get(), accessoryContextId, result);
                                }
                                break;

                            case CLOSE:
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                AccessoryService.close(mContextRef.get(), accessoryContextId, result);
                                message = "AccessoryService.close";
                                break;

                            case START_READ:
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                AccessoryService.startReading(mContextRef.get(), accessoryContextId, result);
                                message = "AccessoryService.startReading";
                                break;

                            case STOP_READ:
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                AccessoryService.stopReading(mContextRef.get(), accessoryContextId, result);
                                message = "AccessoryService.stopReading";
                                break;

                            case READ_REPORT: {
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                HIDReportType reportType = HIDReportType
                                        .valueOf(mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_TYPE, HIDReportType.INPUT.name()));

                                hidReport = AccessoryService
                                        .readReport(mContextRef.get(), accessoryContextId, reportType, result);
                                message = "AccessoryService.readReport" + hidReport;
                                break;
                            }

                            case WRITE_REPORT: {
                                accessoryContextId = voids.length > 1 ? (String) voids[1] : null;

                                HIDReportType reportType = HIDReportType
                                        .valueOf(mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_TYPE, HIDReportType.INPUT.name()));

                                String reportDataHex = mPrefs.getString(AccessoryReportsFragment.PREF_REPORT_DATA, "");

                                byte[] data = fromHexString(reportDataHex);
                                if (data != null) {
                                    hidReport = new HIDReport(reportType, data);
                                } else {
                                    Result.pack(result, Result.RESULT_FAIL, ErrorCode.INVALID_PARAM, "Invalid report data");
                                    onPostExecute(null);
                                    return;
                                }
                                AccessoryService.writeReport(mContextRef.get(), accessoryContextId, hidReport, result);
                                message = "AccessoryService.writeReport";
                                break;
                            }
                        }
                    } catch (Throwable t) {
                        Logger.showResult(null, "AccessoryService method is failed:" + t.getMessage());
                    }

                    onPostExecute(message);
                }
            });
        } catch (Exception e) {
            Logger.showResult(null, "AccessoryService method is failed:" + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(String response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Logger.showResult(mContextRef.get(), response, result);
                if (result.getCode() == Result.RESULT_OK) {
                    // Fill device description with received info
                    if (action == Action.RESERVE_SHARED || action == Action.RELEASE_SHARED) {
                        mContextRef.get().setAccessoryContextId(accessoryInfo, accessoryContextId);
                    } else if (action == Action.GET_OWNED || action == Action.ENUMERATE) {
                        mContextRef.get().loadAccessories(action, accessories);
                    } else if (action == Action.OPEN) {
                        mContextRef.get().setInfo(hidInfo);
                    } else if (action == Action.WRITE_REPORT || action == Action.READ_REPORT) {
                        mContextRef.get().setAccessoryData(Logger.build(hidReport));
                    }
                }
            }
        });
    }

    private static byte[] fromHexString(final String encoded) {
        if ((encoded.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte result[] = new byte[encoded.length() / 2];
        final char enc[] = encoded.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }
}
