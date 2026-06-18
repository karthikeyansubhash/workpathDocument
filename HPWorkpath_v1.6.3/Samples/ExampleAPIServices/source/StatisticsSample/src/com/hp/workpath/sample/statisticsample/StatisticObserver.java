// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample;

import android.os.Handler;

import com.hp.workpath.api.statistics.StatisticsService;

public class StatisticObserver extends StatisticsService.AbstractStatisticsNotificationObserver {

    private ObserverInterface observerInterface;

    public StatisticObserver(Handler handler, ObserverInterface observerInterface) {
        super(handler);
        this.observerInterface = observerInterface;
    }

    public interface ObserverInterface {
        void onComplete(int jobSequence);
    }

    @Override
    public void onComplete(int jobSequence) {
        observerInterface.onComplete(jobSequence);
    }
}
