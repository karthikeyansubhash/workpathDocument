// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample

import android.os.Handler
import android.util.Log
import com.hp.workpath.api.device.events.DeviceEvent
import com.hp.workpath.api.device.events.DeviceEventsService.AbstractDeviceEventsChangeObserver

class DeviceEventObserver(handler: Handler?, private var observerInterface: ObserverInterface) : AbstractDeviceEventsChangeObserver(handler) {
    override fun onChange(deviceEvent: DeviceEvent) {
        Log.i(MainActivity.TAG, Logger.build(deviceEvent))
        observerInterface.onChange(deviceEvent)
    }

    interface ObserverInterface {
        fun onChange(deviceEvent: DeviceEvent)
    }
}