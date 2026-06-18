package com.hp.workpath.sample.deviceusagesample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.fragment.CopyByMediaSizeFragment;
import com.hp.workpath.sample.deviceusagesample.fragment.PrintByMediaSizeFragment;

public class CopyByMediaSizeActivity extends AppCompatActivity {

    public static final String DATA = "data";

    private CopyByMediaSizeFragment mCopyByMediaSizeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_by_media_size);

        Bundle bundle = new Bundle();
        bundle.putString(DATA, getIntent().getStringExtra(DATA));
        mCopyByMediaSizeFragment = new CopyByMediaSizeFragment();
        mCopyByMediaSizeFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mCopyByMediaSizeFragment);
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