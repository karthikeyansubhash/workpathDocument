// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.filebrowser;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileLoader extends AsyncTaskLoader<List<File>> {

    private List<File> mData;
    private String mPath;

    public FileLoader(Context context, String path) {
        super(context);
        this.mPath = path;
    }

    @Override
    public List<File> loadInBackground() {
        ArrayList<File> list = new ArrayList<File>();

        final File pathDir = new File(mPath);
        final File[] dirs = pathDir.listFiles(FileUtils.DIR_FILTER);
        if (dirs != null) {
            Arrays.sort(dirs, FileUtils.COMPARATOR);
            Collections.addAll(list, dirs);
        }

        final File[] files = pathDir.listFiles(FileUtils.FILE_FILTER);
        if (files != null) {
            Arrays.sort(files, FileUtils.COMPARATOR);
            Collections.addAll(list, files);
        }
        return list;
    }

    @Override
    public void deliverResult(List<File> data) {
        if (isReset()) {
            return;
        }

        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null)
            deliverResult(mData);

        if (takeContentChanged() || mData == null)
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mData = null;
    }

    @Override
    public void onCanceled(List<File> data) {
        super.onCanceled(data);
    }
}