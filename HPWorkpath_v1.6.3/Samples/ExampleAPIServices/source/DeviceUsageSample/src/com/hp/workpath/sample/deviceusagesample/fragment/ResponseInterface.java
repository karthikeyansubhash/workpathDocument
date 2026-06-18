// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.fragment;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.deviceusage.DeviceUsageInfo;

public interface ResponseInterface {
    void success(DeviceUsageInfo info);
    void failure(String msg, Result result);
}
