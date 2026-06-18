// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.suppliessample.fragment.SupplyFragment;

public class SupplyActivity extends AppCompatActivity {

    public static final String INDEX = "index";

    private SupplyFragment mSupplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);
        mSupplyFragment = new SupplyFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, getIntent().getIntExtra(INDEX, 0));
        mSupplyFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mSupplyFragment);
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
