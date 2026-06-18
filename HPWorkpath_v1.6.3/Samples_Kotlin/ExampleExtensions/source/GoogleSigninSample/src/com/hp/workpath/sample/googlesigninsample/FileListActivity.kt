// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.services.drive.model.File
import com.hp.workpath.sample.googlesigninsample.adapter.FileListViewAdapter
import com.hp.workpath.sample.googlesigninsample.databinding.ActivityFileListBinding
import com.hp.workpath.sample.googlesigninsample.task.DriveFileListTask
import com.hp.workpath.sample.googlesigninsample.task.DriveFileListTask.DriveTaskInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FileListActivity : AppCompatActivity() {
    private var mCredential: String? = null
    private lateinit var mBindingFileListActivity: ActivityFileListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingFileListActivity = ActivityFileListBinding.inflate(layoutInflater)
        setContentView(mBindingFileListActivity.root)
        mCredential = intent.getStringExtra(INTENT_CREDENTIAL)
    }

    override fun onResume() {
        super.onResume()
        mCredential?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                DriveFileListTask(this@FileListActivity, it, taskInterface).execute()
            }
        }
    }

    private var taskInterface: DriveTaskInterface = object : DriveTaskInterface {
        override fun onFailure(t: Throwable?) {
            if (t != null && TextUtils.isEmpty(t.message)) {
                Log.e(TAG, "Load failure: ${t.message}")
                Toast.makeText(
                    this@FileListActivity,
                    "Load failure: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onResponse(files: List<File>?) {
            if (files.isNullOrEmpty()) {
                mBindingFileListActivity.noFileTextview.visibility = View.VISIBLE
            } else {
                mBindingFileListActivity.fileListView.adapter = FileListViewAdapter(files)
                mBindingFileListActivity.fileListView.layoutManager =
                    LinearLayoutManager(applicationContext)
            }
        }
    }

    companion object {
        private const val TAG: String = MainActivity.TAG
        const val INTENT_CREDENTIAL = "intent_credential"
    }
}