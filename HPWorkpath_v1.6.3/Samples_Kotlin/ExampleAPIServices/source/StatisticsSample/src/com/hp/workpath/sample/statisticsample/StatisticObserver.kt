// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample

import android.os.Handler
import com.hp.workpath.api.statistics.StatisticsService.AbstractStatisticsNotificationObserver

class StatisticObserver(handler: Handler?, private val observerInterface: ObserverInterface) :
    AbstractStatisticsNotificationObserver(handler) {
    interface ObserverInterface {
        fun onComplete(jobSequence: Int)
    }

    override fun onComplete(jobSequence: Int) {
        observerInterface.onComplete(jobSequence)
    }
}