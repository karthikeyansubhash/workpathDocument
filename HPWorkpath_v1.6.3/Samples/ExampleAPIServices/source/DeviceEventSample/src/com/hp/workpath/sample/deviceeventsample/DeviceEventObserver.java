// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample;

import android.os.Handler;
import android.util.Log;

import com.hp.workpath.api.device.events.DeviceEvent;
import com.hp.workpath.api.device.events.DeviceEventsService;

public class DeviceEventObserver extends DeviceEventsService.AbstractDeviceEventsChangeObserver {

    ObserverInterface observerInterface;

    public DeviceEventObserver(Handler handler, ObserverInterface observerInterface) {
        super(handler);
        this.observerInterface = observerInterface;
    }

    @Override
    public void onChange(DeviceEvent deviceEvent) {
        Log.i(MainActivity.TAG, "onChange():" + Logger.build(deviceEvent));
        observerInterface.onChange(deviceEvent);
    }

    public interface ObserverInterface {
        void onChange(DeviceEvent deviceEvent);
    }
}
