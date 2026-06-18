// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment

import com.hp.workpath.api.Result
import com.hp.workpath.api.device.events.DeviceEvent

interface ResponseInterface {
    fun success(deviceEvents: List<DeviceEvent>?)
    fun failure(msg: String?, result: Result?)
}