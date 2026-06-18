// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.deviceusagesample.fragment.JobCategoryAndMediaSizeFragment;

public class JobCategoryAndMediaSizeActivity extends AppCompatActivity {

    public static final String DATA = "data";

    private JobCategoryAndMediaSizeFragment mJobCategoryAndMediaSizeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_category_and_media_size);

        Bundle bundle = new Bundle();
        bundle.putString(DATA, getIntent().getStringExtra(DATA));
        mJobCategoryAndMediaSizeFragment = new JobCategoryAndMediaSizeFragment();
        mJobCategoryAndMediaSizeFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mJobCategoryAndMediaSizeFragment);
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
