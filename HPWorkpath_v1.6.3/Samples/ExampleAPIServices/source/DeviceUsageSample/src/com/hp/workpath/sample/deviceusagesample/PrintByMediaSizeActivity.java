package com.hp.workpath.sample.deviceusagesample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.hp.workpath.sample.deviceusagesample.fragment.JobCategoryAndMediaSizeFragment;
import com.hp.workpath.sample.deviceusagesample.fragment.PrintByMediaSizeFragment;

public class PrintByMediaSizeActivity extends AppCompatActivity {

    public static final String DATA = "data";

    private PrintByMediaSizeFragment mPrintByMediaSizeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_by_media_size);

        Bundle bundle = new Bundle();
        bundle.putString(DATA, getIntent().getStringExtra(DATA));
        mPrintByMediaSizeFragment = new PrintByMediaSizeFragment();
        mPrintByMediaSizeFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mPrintByMediaSizeFragment);
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