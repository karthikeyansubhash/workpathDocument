// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.fragment

import com.hp.workpath.api.Result
import com.hp.workpath.api.statistics.StatisticsJobData

interface ResponseInterface {
    fun success(info: List<StatisticsJobData?>)
    fun failure(msg: String?, result: Result?)
}