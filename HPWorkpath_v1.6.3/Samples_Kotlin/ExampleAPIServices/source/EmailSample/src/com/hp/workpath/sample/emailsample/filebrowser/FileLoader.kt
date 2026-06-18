// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.filebrowser

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import java.io.File
import java.util.Arrays
import java.util.Collections

class FileLoader(context: Context, private val mPath: String) : AsyncTaskLoader<List<File>>(context) {
    private var mData: List<File>? = null
    override fun loadInBackground(): List<File> {
        val list = ArrayList<File>()
        val pathDir = File(mPath)
        val dirs = pathDir.listFiles(FileUtils.DIR_FILTER)
        if (dirs != null) {
            Arrays.sort(dirs, FileUtils.COMPARATOR)
            Collections.addAll(list, *dirs)
        }
        val files = pathDir.listFiles(FileUtils.FILE_FILTER)
        if (files != null) {
            Arrays.sort(files, FileUtils.COMPARATOR)
            Collections.addAll(list, *files)
        }
        return list
    }

    override fun deliverResult(data: List<File>?) {
        if (isReset) {
            return
        }
        mData = data
        if (isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        if (mData != null) deliverResult(mData)
        if (takeContentChanged() || mData == null) forceLoad()
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        onStopLoading()
        mData = null
    }

    override fun onCanceled(data: List<File>?) {
        super.onCanceled(data)
    }

}