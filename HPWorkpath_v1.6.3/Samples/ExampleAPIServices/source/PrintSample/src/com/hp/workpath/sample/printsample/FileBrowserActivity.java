// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hp.workpath.sample.printsample.filebrowser.FileListFragment;

import java.io.File;

import static com.hp.workpath.sample.printsample.filebrowser.FileUtils.PATH;

public class FileBrowserActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener, FileListFragment.Callbacks {

    private FragmentManager mFragmentManager;

    private String mPath;
    private TextView mPathTextView;
    private HorizontalScrollView mHorizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        // find the text and button
        findViewElements();

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);

        mPath = getFilesDir().getPath();
        addFragment(mPath);
        setNavigationPath(mPath);
    }

    private void findViewElements() {
        mPathTextView = findViewById(R.id.pathTextView);
        mHorizontalScrollView = findViewById(R.id.horizontalScrollView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackStackChanged() {
        int count = mFragmentManager.getBackStackEntryCount();

        if (count > 0) {
            FragmentManager.BackStackEntry fragment = mFragmentManager.getBackStackEntryAt(count - 1);
            mPath = fragment.getName();
        } else {
            mPath = getFilesDir().getPath();
        }
        setNavigationPath(mPath);
        invalidateOptionsMenu();
    }

    private void addFragment(String path) {
        FileListFragment fragment = FileListFragment.newInstance(path);
        mFragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, fragment).commit();
    }

    private void replaceFragment(File file) {
        mPath = file.getAbsolutePath();

        FileListFragment fragment = FileListFragment.newInstance(mPath);
        mFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(mPath).commit();
    }

    private void finishWithResult(File file) {
        if (file != null) {
            setResult(RESULT_OK, new Intent().putExtra(PATH, file.getAbsolutePath()));
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onFileSelected(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                setNavigationPath(file.getAbsolutePath());
                replaceFragment(file);
            } else {
                finishWithResult(file);
            }
        } else {
            Toast.makeText(FileBrowserActivity.this, R.string.error_select_file,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setNavigationPath(String absolutePath) {
        if (mPathTextView != null) {
            mPathTextView.setText(absolutePath);
        }
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
