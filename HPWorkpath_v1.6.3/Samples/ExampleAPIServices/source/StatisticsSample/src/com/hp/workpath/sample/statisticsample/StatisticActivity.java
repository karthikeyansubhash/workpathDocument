// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.statisticsample.fragment.StatisticFragment;

public class StatisticActivity extends AppCompatActivity {

    public static final String INDEX = "index";
    public static final String LAST_JOB = "last_job";

    private StatisticFragment mStatisticFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        mStatisticFragment = new StatisticFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, getIntent().getIntExtra(INDEX, 0));
        bundle.putBoolean(LAST_JOB, getIntent().getBooleanExtra(LAST_JOB, false));
        mStatisticFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mStatisticFragment);
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.commit();
        }
    }
}
