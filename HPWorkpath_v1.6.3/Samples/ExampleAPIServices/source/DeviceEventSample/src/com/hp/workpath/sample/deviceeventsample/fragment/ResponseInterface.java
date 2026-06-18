// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample.fragment;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.device.events.DeviceEvent;

import java.util.List;

public interface ResponseInterface {
    void success(List<DeviceEvent> deviceEvents);
    void failure(String msg, Result result);
}
