package com.hp.workpath.sample.deviceusagesample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.deviceusagesample.fragment.PlexByMediaSizeFragment;
import com.hp.workpath.sample.deviceusagesample.fragment.PrintByMediaSizeFragment;

public class PlexByMediaSizeActivity extends AppCompatActivity {

    public static final String DATA = "data";

    private PlexByMediaSizeFragment mPlexByMediaSizeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plex_by_media_size);

        Bundle bundle = new Bundle();
        bundle.putString(DATA, getIntent().getStringExtra(DATA));
        mPlexByMediaSizeFragment = new PlexByMediaSizeFragment();
        mPlexByMediaSizeFragment.setArguments(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(mPlexByMediaSizeFragment);
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