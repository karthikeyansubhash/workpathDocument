package com.hp.workpath.sample.deviceusagesample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.deviceusagesample.fragment.FaxByMediaSizeFragment;

public class FaxByMediaSizeActivity extends AppCompatActivity {

    public static final String DATA = "data";

    private FaxByMediaSizeFragment mFaxByMediaSizeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fax_by_media_size);

        Bundle bundle = new Bundle();
        bundle.putString(DATA, getIntent().getStringExtra(DATA));
        mFaxByMediaSizeFragment = new FaxByMediaSizeFragment();
        mFaxByMediaSizeFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mFaxByMediaSizeFragment);
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