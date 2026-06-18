package com.hp.workpath.sample.accessoryagentsample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.api.access.Principal;
import com.hp.workpath.api.accessory.RegistrationType;
import com.hp.workpath.api.accessory.hid.AccessoryService;
import com.hp.workpath.api.accessory.hid.EventCode;
import com.hp.workpath.api.accessory.hid.HIDAccessoryInfo;
import com.hp.workpath.api.accessory.hid.HIDInfo;
import com.hp.workpath.api.accessory.hid.HIDReport;
import com.hp.workpath.api.accessory.hid.HIDReportEventInfo;
import com.hp.workpath.sample.accessoryagentsample.Logger;
import com.hp.workpath.sample.accessoryagentsample.MainActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class AccessoryReceiver extends BroadcastReceiver {
    private static final String TAG = MainActivity.TAG + "Receiver";
    private static final String ACCESSORY_CONTEXT_CHANGE_ACTION = "com.hp.workpath.api.accessory.ACCESSORY_CONTEXT_CHANGE_ACTION";
    private static final String ACCESSORY_CHANGE_ACTION = "com.hp.workpath.api.accessory.ACCESSORY_CHANGE_ACTION";
    private WeakReference<Context> mContextRef;
    private Context appContext;
    private static boolean isInitializedSDK = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        appContext = context.getApplicationContext();
        this.mContextRef = new WeakReference<>(appContext);

        initService(new Handler(), appContext);

        if(ACCESSORY_CONTEXT_CHANGE_ACTION.equals(intent.getAction())
                || ACCESSORY_CHANGE_ACTION.equals(intent.getAction())){
            final String action = intent.getAction();

            switch (action) {
                case ACCESSORY_CONTEXT_CHANGE_ACTION:
                    onContextChange(intent);
                    break;
                case ACCESSORY_CHANGE_ACTION:
                    onChange(intent);
                    break;
            }
        }
    }

    private void initService(Handler handler, Context context) {
        try {
            Log.i(TAG,"initService Workpath.getInstance().initialize");
            if(!isInitializedSDK) {
                Workpath.getInstance().initialize(mContextRef.get());
                isInitializedSDK = true;
            }
        } catch (SsdkUnsupportedException e) {
            Logger.showResult(null,e.getMessage());
        }
    }

    private void openAccessory(String accessoryContextId) {
        Log.d(TAG, "Accessory opened: " + accessoryContextId);
        Result result = new Result();
        try{
            AccessoryService.open(appContext, accessoryContextId, result);
            Logger.showResult(null, "AccessoryService.open", result);
        } catch(Exception e) {
            Log.e(TAG,e.getMessage(),e);
        }

        try{
            HIDInfo hidInfo = AccessoryService.getInfo(appContext,accessoryContextId, result);
            if (result.getCode() == Result.RESULT_OK) {
                Log.d(TAG, "HID feature report length : " + hidInfo.getFeatureReportLength());
                Log.d(TAG, "HID input report length : " + hidInfo.getInputReportLength());
                Log.d(TAG, "HID output report length : " + hidInfo.getOutputReportLength());
            }
        } catch(Exception e) {
            Log.e(TAG,e.getMessage(),e);
        }

        try{
            AccessoryService.startReading(appContext, accessoryContextId, result);
            Logger.showResult(null, "AccessoryService.startReading", result);
        } catch(Exception e) {
            Log.e(TAG,e.getMessage(),e);
        }
    }


    private void initiateSignIn() {
        Result result = new Result();
        Log.d(TAG, "initiateSignIn");
        AccessService.initiateSignIn(appContext, result);
        Logger.showResult(null, "AccessService.initiateSignIn", result);
    }

    private void signOut() {
        Result result = new Result();
        Log.d(TAG, "signOut");
        AccessService.signOut(appContext, result);
        Logger.showResult(null, "AccessService.signOut", result);
    }

    private Principal getCurrentPrincipal() {
        Result result = new Result();
        Log.d(TAG, "getCurrentPrincipal");
        Principal principal = AccessService.getCurrentPrincipal(appContext, result);
        Logger.showResult(null, "AccessService.getCurrentPrincipal", result);
        if (result.getCode() == Result.RESULT_OK) {
            return principal;
        } else {
            return null;
        }
    }

    public void onContextChange(Intent intent) {
        HIDAccessoryInfo hidAccessoryInfo = intent.getParcelableExtra("accessoryInfo");
        int vendorId = hidAccessoryInfo.getVendorId();
        int productId = hidAccessoryInfo.getProductId();
        String serialNumber = hidAccessoryInfo.getSerialNumber();
        RegistrationType registrationType = hidAccessoryInfo.getRegistrationType();
        String timeStamp = intent.getStringExtra("timestamp");
        EventCode eventCode = (EventCode) intent.getSerializableExtra("accessoryContextEventCode");
        String accessoryContextId = intent.getStringExtra("accessoryContextId");

        Log.i(TAG, "vendorId : "+vendorId);
        Log.i(TAG, "productId : "+productId);
        Log.i(TAG, "serialNumber : "+serialNumber);
        Log.i(TAG, "RegistrationType : "+registrationType);
        Log.i(TAG, "eventCode : "+eventCode);
        Log.i(TAG, "timeStamp : "+timeStamp);
        Log.i(TAG, "accessoryContextId : "+accessoryContextId);


        if(eventCode == EventCode.CONTEXT_CREATED || eventCode == EventCode.CONTEXT_RESENT){
            if (isInitializedSDK == false) {
                Log.d(TAG, "The SDK has not been initialized yet.");
                return;
            }
            if (!TextUtils.isEmpty(accessoryContextId)) {
                openAccessory(accessoryContextId);
            }
        } else if(eventCode == EventCode.CONTEXT_REVOKED){
            Log.i(TAG, "Accessory context is revoked.");
        }
    }

    public void onChange(Intent intent) {
        Log.i(TAG, "ACCESSORY_CHANGE_ACTION");


        HIDAccessoryInfo hidAccessoryInfo = intent.getParcelableExtra("accessoryInfo");
        int vendorId = hidAccessoryInfo.getVendorId();
        int productId = hidAccessoryInfo.getProductId();
        String serialNumber = hidAccessoryInfo.getSerialNumber();


        HIDReportEventInfo hidReportEventInfo = intent.getParcelableExtra("hidReportEventInfo");
        long reportOrdinal = hidReportEventInfo.getOrdinal();
        String timeStamp = hidReportEventInfo.getTimestamp();
        List<HIDReport> reports = hidReportEventInfo.getReports();

        Log.i(TAG, "vendorId : "+vendorId);
        Log.i(TAG, "productId : "+productId);
        Log.i(TAG, "serialNumber : "+serialNumber);
        Log.i(TAG, "ordinal : "+reportOrdinal);
        Log.i(TAG, "timeStamp : "+timeStamp);
        Log.i(TAG, "reportCount : "+reports.size());

        for(int i=0; i<reports.size();i++){
            HIDReport hidReport = reports.get(i);
            byte[] data = hidReport.getData();
            Log.i(TAG, "report_"+(i+1)+" : "+ new String(Base64.encode(data,Base64.DEFAULT)));
            if(isHPCardReader(vendorId, productId) && new String(data).contains("\r")){
                Principal principal = getCurrentPrincipal();
                if (principal != null && !principal.isAuthenticated()) {
                    initiateSignIn();
                } else {
                    signOut();
                }
            }
        }
    }

    private boolean isHPCardReader(int vendorId, int productId) {
        return vendorId == 1008 && productId == 69;
    }
}
