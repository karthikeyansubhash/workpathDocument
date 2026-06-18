// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.statistics.StatisticsJobData;

import java.util.List;

public interface ResponseInterface {
    void success(List<StatisticsJobData> info);
    void failure(String msg, Result result);
}
