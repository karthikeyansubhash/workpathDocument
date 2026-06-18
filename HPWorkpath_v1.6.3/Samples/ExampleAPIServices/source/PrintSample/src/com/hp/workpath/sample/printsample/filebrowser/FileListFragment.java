// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.filebrowser;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.hp.workpath.sample.printsample.R;

import java.io.File;
import java.util.List;

public class FileListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<File>> {

    private static final int LOADER_ID_FILES = 1;

    public interface Callbacks {
        void onFileSelected(File file);
    }

    private FileListAdapter mAdapter;
    private String mPath;

    private Callbacks mListener;

    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(FileUtils.PATH, path);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (Callbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FileListFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new FileListAdapter(getActivity());
        mPath = getArguments() != null ? getArguments().getString(FileUtils.PATH) :
                Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setEmptyText(getString(R.string.empty_directory));
        setListAdapter(mAdapter);
        setListShown(false);

        LoaderManager.getInstance(this).initLoader(LOADER_ID_FILES, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileListAdapter adapter = (FileListAdapter) l.getAdapter();

        if (adapter != null) {
            File file = adapter.getItem(position);
            mPath = file.getAbsolutePath();
            mListener.onFileSelected(file);
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new FileLoader(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        mAdapter.setListItems(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mAdapter.clear();
    }
}
