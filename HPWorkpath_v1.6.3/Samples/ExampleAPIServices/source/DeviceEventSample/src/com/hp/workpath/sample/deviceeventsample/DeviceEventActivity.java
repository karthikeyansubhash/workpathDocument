// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceeventsample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.deviceeventsample.fragment.DeviceEventFragment;

public class DeviceEventActivity extends AppCompatActivity {

    public static final String INDEX = "index";

    private DeviceEventFragment mDeviceEventFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_event);

        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, getIntent().getIntExtra(INDEX, 0));
        mDeviceEventFragment = new DeviceEventFragment();
        mDeviceEventFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mDeviceEventFragment);
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
