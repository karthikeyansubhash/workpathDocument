// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.services.drive.model.File;
import com.hp.workpath.sample.googlesigninsample.adapter.FileListViewAdapter;
import com.hp.workpath.sample.googlesigninsample.task.DriveFileListTask;

import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.TAG;

    public static final String INTENT_CREDENTIAL = "intent_credential";

    private ListView mFileLisView;
    private TextView mNoFileTextView;
    private FileListViewAdapter mAdapter;
    private String mCredential;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initView();
        mCredential = getIntent().getStringExtra(INTENT_CREDENTIAL);
    }

    private void initView() {
        mFileLisView = findViewById(R.id.file_list_view);
        mAdapter = new FileListViewAdapter(this);
        mFileLisView.setAdapter(mAdapter);
        mNoFileTextView = findViewById(R.id.no_file_text_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mCredential)) {
            new DriveFileListTask(this, mCredential, taskInterface).taskExecute();
        }
    }

    DriveFileListTask.DriveTaskInterface taskInterface = new DriveFileListTask.DriveTaskInterface() {
        @Override
        public void onFailure(Throwable t) {
            if (t != null && TextUtils.isEmpty(t.getMessage())) {
                Log.e(TAG, "Load failure: " + t.getMessage());
                Toast.makeText(FileListActivity.this, "Load failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onResponse(List<File> files) {
            if (files == null || files.size() == 0) {
                mNoFileTextView.setVisibility(View.VISIBLE);
            } else {
                mAdapter.setItems(files);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
}
